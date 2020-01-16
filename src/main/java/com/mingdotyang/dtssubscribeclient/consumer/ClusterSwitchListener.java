package com.mingdotyang.dtssubscribeclient.consumer;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.ClusterResource;
import org.apache.kafka.common.ClusterResourceListener;
import org.apache.kafka.common.KafkaException;

@Slf4j
public class ClusterSwitchListener implements ClusterResourceListener, ConsumerInterceptor {
	private ClusterResource originClusterResource = null;

	public ConsumerRecords onConsume(ConsumerRecords records) {
		return records;
	}


	public void close() {
	}

	public void onCommit(Map offsets) {
	}


	public void onUpdate(ClusterResource clusterResource) {
		synchronized (this) {
			if (null == originClusterResource) {
				log.info("Cluster updated to " + clusterResource.clusterId());
				originClusterResource = clusterResource;
			} else {
				if (clusterResource.clusterId().equals(originClusterResource.clusterId())) {
					log.info("Cluster not changed on update:" + clusterResource.clusterId());
				} else {
					throw new ClusterSwitchException("Cluster changed from " + originClusterResource.clusterId() + " to " + clusterResource.clusterId()
							+ ", consumer require restart");
				}
			}
		}
	}

	public void configure(Map<String, ?> configs) {
	}

	public static class ClusterSwitchException extends KafkaException {
		public ClusterSwitchException(String message, Throwable cause) {
			super(message, cause);
		}

		public ClusterSwitchException(String message) {
			super(message);
		}

		public ClusterSwitchException(Throwable cause) {
			super(cause);
		}

		public ClusterSwitchException() {
			super();
		}

	}
}

