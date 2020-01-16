package com.mingdotyang.dtssubscribeclient.consumer;


import com.mingdotyang.dtssubscribeclient.model.SubscribeInstance;

public interface ConsumerWrapFactory {

	ConsumerWrap getConsumerWrap(SubscribeInstance subscribeInstance);

	class KafkaConsumerWrapFactory implements ConsumerWrapFactory {

		@Override
		public ConsumerWrap getConsumerWrap(SubscribeInstance subscribeInstance) {
			return new DefaultConsumerWrap(subscribeInstance);
		}
	}
}
