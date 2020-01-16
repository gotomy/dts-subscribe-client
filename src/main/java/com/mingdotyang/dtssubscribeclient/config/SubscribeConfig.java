package com.mingdotyang.dtssubscribeclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "datasync.dts")
public class SubscribeConfig {

	private Long subscribeInstanceId;

}
