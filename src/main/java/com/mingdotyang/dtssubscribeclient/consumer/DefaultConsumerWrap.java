package com.mingdotyang.dtssubscribeclient.consumer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import com.mingdotyang.dtssubscribeclient.model.SubscribeInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

@Slf4j
public class DefaultConsumerWrap extends ConsumerWrap {

	private KafkaConsumer<String, byte[]> consumer;
	private final long pollTimeout;

	public DefaultConsumerWrap(SubscribeInstance subscribeInstance) {
		consumer = new KafkaConsumer<>(initProps(subscribeInstance));
		pollTimeout = subscribeInstance.getPollTimeout();
	}

	private Properties initProps(SubscribeInstance subscribeInstance) {
		Properties props = new Properties();

		props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, subscribeInstance.getBrokers());
		props.put("security.protocol", "SASL_PLAINTEXT");
		props.setProperty(SaslConfigs.SASL_MECHANISM, "PLAIN");
		props.setProperty(SaslConfigs.SASL_JAAS_CONFIG,
				buildJaasConfig(subscribeInstance.getGroupId(), subscribeInstance.getUsername(), subscribeInstance.getPassword()));
		props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, subscribeInstance.getGroupId());
		props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
		props.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, String.valueOf(subscribeInstance.getSessionTimeout()));
		props.setProperty(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, ClusterSwitchListener.class.getName());

		return props;
	}

	private static String buildJaasConfig(String sid, String user, String password) {
		String jaasTemplate = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s-%s\" password=\"%s\";";
		return String.format(jaasTemplate, user, sid, password);
	}

	@Override
	public void setFetchOffsetByOffset(SubscribeInstance subscribeInstance) {
		consumer.seek(getTopicPartition(subscribeInstance), subscribeInstance.getOffset());
	}

	@Override
	public void setFetchOffsetByTimestamp(SubscribeInstance subscribeInstance) {
		long timeStamp = subscribeInstance.getStartTime() / 1000;
		TopicPartition topicPartition = getTopicPartition(subscribeInstance);
		Map<TopicPartition, OffsetAndTimestamp> remoteOffset = consumer.offsetsForTimes(Collections.singletonMap(topicPartition, timeStamp));
		OffsetAndTimestamp toSet = remoteOffset.get(topicPartition);
		if (null == toSet) {
			throw new RuntimeException(String.format("seek timestamp for topic [%s] with timestamp [%s] failed", topicPartition, timeStamp));
		}
		consumer.seek(topicPartition, toSet.offset());
	}

	@Override
	public void assignTopic(SubscribeInstance subscribeInstance) {
		TopicPartition topicPartition = getTopicPartition(subscribeInstance);
		consumer.assign(Arrays.asList(topicPartition));
		setFetchOffsetByTimestamp(subscribeInstance);
	}

	private TopicPartition getTopicPartition(SubscribeInstance subscribeInstance) {
		return new TopicPartition(subscribeInstance.getTopic(), subscribeInstance.getPartition());
	}

	@Override
	public ConsumerRecords<String, byte[]> poll() {
		return consumer.poll(pollTimeout);
	}

	@Override
	public KafkaConsumer<String, byte[]> getRawConsumer() {
		return consumer;
	}

	public synchronized void close() {
		if (null != consumer) {
			consumer.close();
		}
	}
}
