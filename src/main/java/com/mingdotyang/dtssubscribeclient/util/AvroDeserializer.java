package com.mingdotyang.dtssubscribeclient.util;

import com.alibaba.dts.formats.avro.Record;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

@Slf4j
public class AvroDeserializer {

	private final SpecificDatumReader<Record> reader = new SpecificDatumReader<Record>(com.alibaba.dts.formats.avro.Record.class);

	public AvroDeserializer() {
	}

	public com.alibaba.dts.formats.avro.Record deserialize(byte[] data) {

		Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
		Record payload = null;
		try {
			payload = reader.read(null, decoder);
			return payload;
		} catch (Throwable ex) {
			log.error("AvroDeserializer: deserialize record failed cause " + ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}
}
