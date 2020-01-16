package com.mingdotyang.dtssubscribeclient.processor;

/**
 * 业务数据处理接口.
 *
 * @param <T> 处理对象
 */
public interface Processor<T> {

	/**
	 * 处理器编号，对应一个数据库实例的库+表名
	 *
	 * @return 库+'-'+表
	 */
	String processorId();

	/**
	 * 新增事件
	 *
	 * @param t
	 */
	void insert(T t);

	/**
	 * 更新事件
	 *
	 * @param oldRecord
	 * @param newRecord
	 */
	void update(T oldRecord, T newRecord);

	/**
	 * 删除事件
	 *
	 * @param t
	 */
	void delete(T t);

	/**
	 * 异步逻辑处理
	 *
	 * @param e
	 */
	void onError(Exception e);

}
