package com.mingdotyang.dtssubscribeclient.convertor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dts.formats.avro.BinaryGeometry;
import com.alibaba.dts.formats.avro.BinaryObject;
import com.alibaba.dts.formats.avro.Character;
import com.alibaba.dts.formats.avro.DateTime;
import com.alibaba.dts.formats.avro.Decimal;
import com.alibaba.dts.formats.avro.Field;
import com.alibaba.dts.formats.avro.Integer;
import com.alibaba.dts.formats.avro.Operation;
import com.alibaba.dts.formats.avro.Record;
import com.alibaba.dts.formats.avro.TextGeometry;
import com.alibaba.dts.formats.avro.TextObject;
import com.alibaba.dts.formats.avro.Timestamp;
import com.alibaba.dts.formats.avro.TimestampWithTimeZone;
import com.mingdotyang.dtssubscribeclient.util.AvroDeserializer;
import com.mingdotyang.dtssubscribeclient.util.CamelUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumerRecordConvertor {

	private ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

	private AvroDeserializer avroDeserializer;

	public ConsumerRecordConvertor(AvroDeserializer avroDeserializer) {
		this.avroDeserializer = avroDeserializer;
	}

	public RecordInfo to(ConsumerRecord<String, byte[]> byteRecord, Long instanceId, String instanceName) throws IOException {
		RecordInfo recordInfo = new RecordInfo();

		Record record = this.avroDeserializer.deserialize(byteRecord.value());
		Operation operation = record.getOperation();

		if (operation == Operation.INSERT || operation == Operation.UPDATE || operation == Operation.DELETE) {
			String[] objectName = record.getObjectName().toString().split("[.]");

			recordInfo
					.setInstanceId(instanceId)
					.setInstanceName(instanceName)
					.setDatabase(objectName[0])
					.setTable(objectName[1])
					.setType(RecordInfo.OperateType.valueOf(operation.name()))
					.setSourceTimestamp(byteRecord.timestamp())
					.setOffset(byteRecord.offset());

			List<Field> fields = (List<Field>) record.getFields();
			Map<String, Object> afterMap = new HashMap<>();
			Map<String, Object> beforeMap = new HashMap<>();
			List<String> keys = new ArrayList<>(50);
			for (Field field : fields) {
				String key = CamelUtil.underlineToCamel(String.valueOf(field.getName()));
				keys.add(key);
				afterMap.put(key, null);
				beforeMap.put(key, null);
			}

			if (operation == Operation.INSERT || operation == Operation.UPDATE) {
				List<Object> afterImages = (List<Object>) record.getAfterImages();

				for (int i = 0; i < afterImages.size(); i++) {
					String key = keys.get(i);
					Object value = afterImages.get(i);
					setValue(afterMap, key, value);
				}

			}

			if (operation == Operation.DELETE || operation == Operation.UPDATE) {
				List<Object> beforeImages = (List<Object>) record.getBeforeImages();

				for (int i = 0; i < beforeImages.size(); i++) {
					String key = keys.get(i);
					Object value = beforeImages.get(i);
					setValue(beforeMap, key, value);
				}
			}

			recordInfo.setData(afterMap);
			recordInfo.setOld(beforeMap);

			return recordInfo;
		}

		return null;
	}

	private void setValue(Map<String, Object> map, String key, Object o) {
		Object value = null;
		if (o == null) {
			map.put(key, null);
		}

		if (o instanceof Character) {
			Character character = (Character) o;
			value = new String(character.getValue().array(), StandardCharsets.UTF_8);
		}

		if (o instanceof Integer) {
			Integer integer = (Integer) o;
			int precision = integer.getPrecision();
			if (precision <= 4) {
				value = new java.lang.Integer(integer.getValue().toString());
			} else {
				value = new Long(integer.getValue().toString());
			}
		}

		if (o instanceof com.alibaba.dts.formats.avro.Float) {
			com.alibaba.dts.formats.avro.Float aFloat = (com.alibaba.dts.formats.avro.Float) o;
			value = aFloat.getValue();
		}

		if (o instanceof Decimal) {
			Decimal decimal = (Decimal) o;
			value = decimal.getValue();
		}

		if (o instanceof TextObject) {
			TextObject textObject = (TextObject) o;
			value = textObject.getValue().toString();
		}

		if (o instanceof DateTime) {
			DateTime dateTime = (DateTime) o;
			java.lang.Integer year = dateTime.getYear() != null ? dateTime.getYear() : 0;
			java.lang.Integer month = dateTime.getMonth() != null ? dateTime.getMonth() : 0;
			java.lang.Integer day = dateTime.getDay() != null ? dateTime.getDay() : 0;
			java.lang.Integer hour = dateTime.getHour() != null ? dateTime.getHour() : 0;
			java.lang.Integer minute = dateTime.getMinute() != null ? dateTime.getMinute() : 0;
			java.lang.Integer second = dateTime.getSecond() != null ? dateTime.getSecond() : 0;
			java.lang.Integer millis = dateTime.getMillis() != null ? dateTime.getMillis() : 0;
			LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second, millis);
			value = localDateTime.toInstant(zoneOffset).toEpochMilli();
		}

		if (o instanceof Timestamp) {
			Timestamp timestamp = (Timestamp) o;
			value = timestamp.getTimestamp();
		}

		if (o instanceof TimestampWithTimeZone) {
			TimestampWithTimeZone timestamp = (TimestampWithTimeZone) o;
			DateTime dateTime = timestamp.getValue();
			java.lang.Integer year = dateTime.getYear() != null ? dateTime.getYear() : 0;
			java.lang.Integer month = dateTime.getMonth() != null ? dateTime.getMonth() : 0;
			java.lang.Integer day = dateTime.getDay() != null ? dateTime.getDay() : 0;
			java.lang.Integer hour = dateTime.getHour() != null ? dateTime.getHour() : 0;
			java.lang.Integer minute = dateTime.getMinute() != null ? dateTime.getMinute() : 0;
			java.lang.Integer second = dateTime.getSecond() != null ? dateTime.getSecond() : 0;
			java.lang.Integer millis = dateTime.getMillis() != null ? dateTime.getMillis() : 0;
			LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second, millis);
			value = localDateTime.toInstant(zoneOffset).toEpochMilli();
		}

		if (o instanceof TextObject) {
			TextObject textObject = (TextObject) o;
			value = textObject.getValue();
		}

		if (o instanceof TextGeometry) {
			TextGeometry textGeometry = (TextGeometry) o;
			value = textGeometry.getValue();
		}

		if (o instanceof BinaryObject) {
			BinaryObject binaryObject = (BinaryObject) o;
			value = binaryObject.getValue().array();
		}


		if (o instanceof BinaryGeometry) {
			BinaryGeometry binaryGeometry = (BinaryGeometry) o;
			value = binaryGeometry.getValue().array();
		}

		map.put(key, value);
	}

}
