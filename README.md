# DTS-SUBSCRIBE-CLIENT

本项目主要从阿里云DTS订阅数据，解析avro数据格式后转换成统一中间数据格式，再根据各个订阅业务的需求，转换成订阅业务数据进行业务的处理。

## 使用技术
* kafka client
* spring boot
* avro

## sql
```
DROP TABLE IF EXISTS `subscribe_instance`;
CREATE TABLE `subscribe_instance` (
  `id` bigint(19) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `instance_name` varchar(32) NOT NULL COMMENT '订阅名称',
  `brokers` varchar(128) NOT NULL COMMENT '订阅地址',
  `topic` varchar(64) NOT NULL COMMENT '订阅主题',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `group_id` varchar(32) NOT NULL COMMENT '消费组ID号',
  `session_timeout` bigint(20) NOT NULL COMMENT '连接超时',
  `auto_commit_interval` int(11) NOT NULL COMMENT '自动提交间隔',
  `poll_timeout` int(11) NOT NULL COMMENT 'poll超时',
  `partition` int(11) DEFAULT NULL COMMENT '分区',
  `start_time` bigint(19) DEFAULT NULL COMMENT '开始时间',
  `offset` bigint(19) DEFAULT NULL COMMENT '偏移量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
```

