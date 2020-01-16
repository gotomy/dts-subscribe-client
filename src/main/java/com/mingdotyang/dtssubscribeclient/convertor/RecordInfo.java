package com.mingdotyang.dtssubscribeclient.convertor;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RecordInfo implements Serializable {

	private final static long serialVersionUID = 12445643490394893L;

	private Long instanceId;

	private String instanceName;

	private String database;

	private String table;

	private OperateType type;

	private Long sourceTimestamp;

	private Long offset;

	// image after data
	private Map<String, Object> data;

	// image old data
	private Map<String, Object> old;


	public enum OperateType {
		INSERT, UPDATE, DELETE, DDL, UNKNOWN
	}

	public String getProcessorId() {
		return String.format("%s-%s", this.database, this.table);
	}

}
