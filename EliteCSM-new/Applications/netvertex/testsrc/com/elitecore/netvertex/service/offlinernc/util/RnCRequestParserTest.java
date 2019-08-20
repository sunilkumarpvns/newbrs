package com.elitecore.netvertex.service.offlinernc.util;

import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.text.ParseException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public class RnCRequestParserTest {

	private static final String DATE = "01-01-2018 01:01:01";
	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

	private static final String JSON = "{\"Attributes\""
			+ ":{"
			+ "\"MobileNumber\":\"8128961877\","
			+ "\"UserId\":\"1001\","
			+ "\"PROCESS-DATE\":\"" + DATE + "\","
			+ "\"CallerId\":\"123456789\","
			+ "\"FILE-NAME\":\"input.csv\","
			+ "\"Name\":\" Bond \""
			+ "},"
			+ "\"SerialNumber\":\"AB12345\","
			+ "\"SourceUnitName\":\"MW\","
			+ "\"StatusMessage\":\"Successfully Created::"
			+ "\"}";

	private static final String JSON_MAP_ATTRIBUTE_NULL = "{\"Attributes\""
			+ ":{"
			+ "\"MobileNumber\":\"8128961877\","
			+ "\"UserId\":\"1001\","
			+ "\"PROCESS-DATE\":\"" + DATE + "\","
			+ "\"CallerId\":\"\","
			+ "\"FILE-NAME\":\"input.csv\","
			+ "\"Name\":\" Bond \""
			+ "},"
			+ "\"SerialNumber\":\"AB12345\","
			+ "\"SourceUnitName\":\"MW\","
			+ "\"StatusMessage\":\"Successfully Created::\""
			+ "}";
	private static final String JSON_INSTANCE_VARIABLE_NULL = "{\"Attributes\""
			+ ":{"
			+ "\"MobileNumber\":\"8128961877\","
			+ "\"UserId\":\"1001\","
			+ "\"PROCESS-DATE\":\"" + DATE + "\","
			+ "\"CallerId\":\"123456789\","
			+ "\"FILE-NAME\":\"input.csv\","
			+ "\"Name\":\" Bond \""
			+ "},"
			+ "\"SerialNumber\":\"\","
			+ "\"SourceUnitName\":\"\","
			+ "\"StatusMessage\":\"\""
			+ "}";


	private TimeSource timesource;
	private RnCRequestParser parser;
	private RnCRequest rncRequest;
	private SimpleDateFormatThreadLocal format = SimpleDateFormatThreadLocal.create(TIME_FORMAT);

	@Before
	public void setUp() throws ParseException {
		timesource = new FixedTimeSource(format.get().parse(DATE).getTime());
		parser = new RnCRequestParser(timesource, format);
		rncRequest = new RnCRequest(timesource);
		Map<String,String>attributes = rncRequest.getAttributes();
		attributes.put("CallerId", "123456789");
		attributes.put("UserId", "1001");
		attributes.put("MobileNumber", "8128961877");
		attributes.put("Name"," Bond ");
		rncRequest.setLogicalFileName("input.csv");
		rncRequest.setProcessDate(DATE);
		rncRequest.setSerialNumber("AB12345");
		rncRequest.setSourceUnitName("MW");
		rncRequest.setStatusMessage("Successfully Created::");
	}

	@Test
	public void convertsRnCRequestToJSON() throws Exception {
		assertEquals(JSON, parser.serialize(RnCResponse.of(rncRequest)));
	}

	@Test
	public void convertsJSONToRnCRequest() throws ParseException {
		assertReflectionEquals(rncRequest, parser.deserialize(JSON), ReflectionComparatorMode.LENIENT_ORDER);
	}

	@Test
	public void nullAttributeValuesAreSerializedAsEmptyStrings() throws Exception {
		rncRequest.getAttributes().put("CallerId", null);

		assertEquals(JSON_MAP_ATTRIBUTE_NULL, parser.serialize(RnCResponse.of(rncRequest)));
	}

	@Test
	public void emptyStringAttributeValuesAreDeserializedAsNullValues() throws ParseException {
		rncRequest.getAttributes().put("CallerId", "");

		assertReflectionEquals(rncRequest, parser.deserialize(JSON_MAP_ATTRIBUTE_NULL), ReflectionComparatorMode.LENIENT_ORDER);
	}

	@Test
	public void nullInstanceValuesAreSerializedAsEmptyStrings() throws Exception {
		rncRequest.setSerialNumber(null);
		rncRequest.setStatusMessage(null);
		rncRequest.setSourceUnitName(null);

		assertEquals(JSON_INSTANCE_VARIABLE_NULL, parser.serialize(RnCResponse.of(rncRequest)));
	}

	@Test
	public void emptyStringInstanceValuesAreDeserializedAsNullValues() throws ParseException {
		rncRequest.setSerialNumber("");
		rncRequest.setStatusMessage("");
		rncRequest.setSourceUnitName("");

		assertReflectionEquals(rncRequest, parser.deserialize(JSON_INSTANCE_VARIABLE_NULL), ReflectionComparatorMode.LENIENT_ORDER);
	}
}
