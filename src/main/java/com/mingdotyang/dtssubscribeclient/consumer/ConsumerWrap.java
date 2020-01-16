package com.mingdotyang.dtssubscribeclient.consumer;

import com.mingdotyang.dtssubscribeclient.model.SubscribeInstance;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

@Slf4j
public abstract class ConsumerWrap implements Cloneable {

	public abstract void setFetchOffsetByOffset(SubscribeInstance checkpoint);

	public abstract void setFetchOffsetByTimestamp(SubscribeInstance checkpoint);

	public abstract void assignTopic(SubscribeInstance checkpoint);

	public abstract ConsumerRecords<String, byte[]> poll();

	public abstract KafkaConsumer<String, byte[]> getRawConsumer();

}
