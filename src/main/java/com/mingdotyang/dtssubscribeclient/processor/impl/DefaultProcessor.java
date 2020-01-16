package com.mingdotyang.dtssubscribeclient.processor.impl;


import org.springframework.stereotype.Component;

import com.mingdotyang.dtssubscribeclient.processor.Processor;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认处理器，只打印数据.
 */
@Slf4j
@Component
public class DefaultProcessor implements Processor {

	public static final String DEFAULT_HANDLER = "default";

	@Override
	public String processorId() {
		return DEFAULT_HANDLER;
	}

	@Override
	public void insert(Object o) {
		log.debug("default processor, insert event, the data is: {}", o);
	}

	@Override
	public void update(Object oldRecord, Object newRecord) {
		log.debug("default processor, update event, old data is: {}", oldRecord);
		log.debug("default processor, update event, new data is: {}", newRecord);
	}

	@Override
	public void delete(Object o) {
		log.debug("default processor, delete event, data is: {}", o);
	}

	@Override
	public void onError(Exception e) {
		log.error("default processor, exception is {}", e.getMessage());
	}

}
