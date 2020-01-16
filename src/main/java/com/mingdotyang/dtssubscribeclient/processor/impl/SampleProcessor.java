package com.mingdotyang.dtssubscribeclient.processor.impl;

import com.mingdotyang.dtssubscribeclient.dto.SampleDTO;
import com.mingdotyang.dtssubscribeclient.processor.Processor;

/**
 * 具体对应每个表的业务处理器可以自己定义.
 *
 * @author ming yang
 */
public class SampleProcessor implements Processor<SampleDTO> {

	private static final String DB_NAME = "db_name";
	private static final String TABLE_NAME = "table_name";

	@Override
	public String processorId() {
		return String.format("%s-%s", DB_NAME, TABLE_NAME);
	}

	@Override
	public void insert(SampleDTO sampleDTO) {
		// do thing business logic
	}

	@Override
	public void update(SampleDTO oldRecord, SampleDTO newRecord) {
		// do thing business logic
	}

	@Override
	public void delete(SampleDTO sampleDTO) {
		// do thing business logic
	}

	@Override
	public void onError(Exception e) {
		// do thing business logic
	}
}
