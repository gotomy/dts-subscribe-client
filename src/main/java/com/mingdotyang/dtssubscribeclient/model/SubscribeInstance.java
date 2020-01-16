package com.mingdotyang.dtssubscribeclient.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SubscribeInstance {
	// 订阅实例ID
	private Long id;
	// 实例实称
	private String instanceName;
	// dts brokers
	private String brokers;
	// 数据订阅topoc
	private String topic;
	// 订阅用户名
	private String username;
	// 订阅密码
	private String password;
	// 消费组
	private String groupId;
	// 超时时间
	private Long sessionTimeout;
	// 自动保证offset间隔
	private Long autoCommitInterval;
	// poll超时时间
	private Long pollTimeout;
	// 分区，dts订阅分区号为0
	private Integer partition;
	// 开始订阅起始时间
	private Long startTime;
	// 偏移量
	private Long offset;

}
