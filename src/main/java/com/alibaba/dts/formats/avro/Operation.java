/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package com.alibaba.dts.formats.avro;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public enum Operation { 
  INSERT, UPDATE, DELETE, DDL, BEGIN, COMMIT, ROLLBACK, ABORT, HEARTBEAT, CHECKPOINT, COMMAND, FILL, FINISH  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"Operation\",\"namespace\":\"com.alibaba.dts.formats.avro\",\"symbols\":[\"INSERT\",\"UPDATE\",\"DELETE\",\"DDL\",\"BEGIN\",\"COMMIT\",\"ROLLBACK\",\"ABORT\",\"HEARTBEAT\",\"CHECKPOINT\",\"COMMAND\",\"FILL\",\"FINISH\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
}