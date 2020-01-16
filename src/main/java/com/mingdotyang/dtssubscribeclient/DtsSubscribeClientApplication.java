package com.mingdotyang.dtssubscribeclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mingdotyang.dtssubscribeclient.config.SubscribeConfig;
import com.mingdotyang.dtssubscribeclient.consumer.ConsumerWrap;
import com.mingdotyang.dtssubscribeclient.consumer.ConsumerWrapFactory;
import com.mingdotyang.dtssubscribeclient.convertor.ConsumerRecordConvertor;
import com.mingdotyang.dtssubscribeclient.convertor.RecordInfo;
import com.mingdotyang.dtssubscribeclient.model.SubscribeInstance;
import com.mingdotyang.dtssubscribeclient.processor.ProcessorDispatcher;
import com.mingdotyang.dtssubscribeclient.repo.SubscribeInstanceRepository;
import com.mingdotyang.dtssubscribeclient.util.AvroDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

@SpringBootApplication
@Slf4j
public class DtsSubscribeClientApplication implements CommandLineRunner {

	@Autowired
	private SubscribeInstanceRepository repository;
	@Autowired
	private ProcessorDispatcher processorDispatcher;
	@Autowired
	private SubscribeConfig subscribeConfig;

	public static void main(String[] args) {
		SpringApplication.run(DtsSubscribeClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		SubscribeInstance subscribeInstance = repository.loadSubscribeInstance(this.subscribeConfig.getSubscribeInstanceId());
		ConsumerWrapFactory.KafkaConsumerWrapFactory kafkaFactory = new ConsumerWrapFactory.KafkaConsumerWrapFactory();
		ConsumerWrap consumerWrap = kafkaFactory.getConsumerWrap(subscribeInstance);
		consumerWrap.assignTopic(subscribeInstance);

		AvroDeserializer avroDeserializer = new AvroDeserializer();
		ConsumerRecordConvertor consumerRecordConvertor = new ConsumerRecordConvertor(avroDeserializer);
		while (true) {
			ConsumerRecords<String, byte[]> records = consumerWrap.poll();
			for (ConsumerRecord<String, byte[]> record : records) {
				RecordInfo recordInfo = consumerRecordConvertor.to(record, subscribeInstance.getId(), subscribeInstance.getInstanceName());
				if (recordInfo != null) {
					log.debug("received msg is {}", recordInfo);
					this.processorDispatcher.dispatcher(recordInfo, subscribeInstance.getAutoCommitInterval());
				}
			}
		}
	}
}
