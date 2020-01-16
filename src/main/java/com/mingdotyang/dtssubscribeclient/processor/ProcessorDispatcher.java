package com.mingdotyang.dtssubscribeclient.processor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mingdotyang.dtssubscribeclient.convertor.RecordInfo;
import com.mingdotyang.dtssubscribeclient.processor.impl.DefaultProcessor;
import com.mingdotyang.dtssubscribeclient.repo.SubscribeInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

@Component
@Order(Integer.MIN_VALUE - 10)
@Slf4j
public class ProcessorDispatcher implements InitializingBean {

	private Map<String, Processor> processorMap = new HashMap<>();

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private SubscribeInstanceRepository subscribeInstanceRepository;

	private long lastCommitTimestamp = System.currentTimeMillis();

	/**
	 * 处理对象分派
	 *
	 * @param recordInfo
	 * @param autoCommitIntervalMs
	 */
	public void dispatcher(RecordInfo recordInfo, long autoCommitIntervalMs) {
		RecordInfo.OperateType operateType = recordInfo.getType();
		Processor processor = processorMap.get(recordInfo.getProcessorId());
		if (null == processor) {
			processor = processorMap.get(DefaultProcessor.DEFAULT_HANDLER);
		}

		Class<?> clz = null;
		Type[] actualTypes = processor.getClass().getGenericInterfaces();
		if (actualTypes != null && actualTypes.length > 0) {
			if (actualTypes[0] instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) actualTypes[0];
				clz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
			}
		}

		try {
			if (operateType == RecordInfo.OperateType.INSERT) {
				processor.insert(mapToBean(clz, recordInfo.getData()));
			} else if (operateType == RecordInfo.OperateType.UPDATE) {
				processor.update(mapToBean(clz, recordInfo.getOld()), mapToBean(clz, recordInfo.getData()));
			} else if (operateType == RecordInfo.OperateType.DELETE) {
				processor.delete(mapToBean(clz, recordInfo.getOld()));
			}
		} catch (Exception e) {
			processor.onError(e);
		}

		if (System.currentTimeMillis() - lastCommitTimestamp > autoCommitIntervalMs) {
			commit(recordInfo.getInstanceId(), recordInfo.getSourceTimestamp(), recordInfo.getOffset());
			this.lastCommitTimestamp = System.currentTimeMillis();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Processor> processors = applicationContext.getBeansOfType(Processor.class);
		List<Processor> list = processors.entrySet().stream().map(item -> item.getValue()).collect(Collectors.toList());
		list.forEach(item -> processorMap.put(item.processorId(), item));
	}


	private void commit(Long instanceId, Long sourceTimestamp, Long offset) {
		subscribeInstanceRepository.updateInstanceOffset(instanceId, sourceTimestamp, offset);
	}

	private Object mapToBean(Class<?> clz, Map<String, Object> map) {
		try {
			if (clz == null) {
				return map;
			}
			Object obj = clz.getDeclaredConstructor().newInstance();
			if (obj == null) {
				return map;
			}
			BeanUtils.populate(obj, map);

			return obj;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("map to bean convert is error: {}", ex.getMessage());
		}

		return null;
	}
}
