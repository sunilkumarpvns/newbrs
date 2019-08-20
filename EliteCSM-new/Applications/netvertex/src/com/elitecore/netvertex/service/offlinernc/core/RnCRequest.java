package com.elitecore.netvertex.service.offlinernc.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.core.servicex.base.BaseServiceRequest;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.gateway.file.parsing.AsciiPacket;

public class RnCRequest extends BaseServiceRequest {
	private static final String ORIGINAL_PACKET = "ORIGINAL_PACKET";
	
	private Map<String, String> attributes = new HashMap<>();
	private Map<String, Object> parameters = new HashMap<>(2);
	private String statusMessage;
	private String sourceUnitName;
	private String serialNumber;
	private IndentingPrintWriter ratingHuntingTrace;
	private StringWriter stringWriter;
	private Date processDate;
	private OfflineRnCEvent eventType = OfflineRnCEvent.CDR;
	
	public RnCRequest() {
		super();
	}
	
	public RnCRequest(TimeSource timeSource) {
		super(timeSource);
	}
	
	@Override
	public void setParameter(String key, Object parameterValue) {
		parameters.put(key, parameterValue);
	}

	@Override
	public Object getParameter(String str) {
		return parameters.get(str);
	}
	
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	
	public String getAttribute(OfflineRnCKeyConstants key) {
		return attributes.get(key.getName());
	}
	
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
		
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public String getSourceUnitName() {
		return sourceUnitName;
	}

	public String getLogicalFileName() {
		return this.attributes.get(OfflineRnCKeyConstants.FILE_NAME.getName());
	}
	
	public String getAbsoluteFileName() {
		return this.attributes.get(OfflineRnCKeyConstants.ABSLOUTE_FILE_NAME.getName());
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSourceUnitName(String sourceUnitName) {
		this.sourceUnitName = sourceUnitName;
	}

	public void setLogicalFileName(String logicalFileName) {
		this.attributes.put(OfflineRnCKeyConstants.FILE_NAME.getName(), logicalFileName);
	}

	public OfflineRnCEvent getEventType() {
		return eventType;
	}

	public void setEventType(OfflineRnCEvent type) {
		this.eventType = type;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	public @Nullable AsciiPacket getOriginalPacket() {
		return (AsciiPacket) parameters.get(ORIGINAL_PACKET);
	}
	
	public void setOriginalPacket(AsciiPacket originalPacket) {
		parameters.put(ORIGINAL_PACKET, originalPacket);
	}

	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		appendInfoKeys(builder);

		Iterator<Entry<String, String>> iterator = attributes.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, String> next = iterator.next();
			builder.append("\t")
				.append(next.getKey()).append(" = ").append(next.getValue())
			.append("\n");
		}
		return builder.toString();
	}
	
	private void appendInfoKeys(StringBuilder builder) {
		builder
			.append("\t").append("SerialNumber = " + serialNumber).append("\n")
			.append("\t").append("SourceUnitName = " + sourceUnitName).append("\n");
	}

	public String toString(List<String> headerAttributes) {
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = attributes.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, String> next = iterator.next();
			if(headerAttributes.contains(next.getKey())) {
				builder.append(next.getValue());
				builder.append(",");
			}
		}
		
		return builder.toString();
	}
	
	public IndentingPrintWriter getTraceWriter() {
		if (ratingHuntingTrace == null) {
			stringWriter = new StringWriter();
			ratingHuntingTrace = new IndentingPrintWriter(new PrintWriter(stringWriter));
		}
		return ratingHuntingTrace;
	}

	public String getTrace() {
		if (ratingHuntingTrace == null) {
			return "";
		}
		return stringWriter.toString();
	}

	public void append(String trace) {
		if (ratingHuntingTrace == null) {
			stringWriter = new StringWriter();
			ratingHuntingTrace = new IndentingPrintWriter(new PrintWriter(stringWriter));
		}

		ratingHuntingTrace.println(trace);
	}

	public void setProcessDate(String date) {
		attributes.put(OfflineRnCKeyConstants.PROCESS_DATE.getName(), date);
	}
	
	public Date getProcessDate() {
		return processDate;
	}
	
	public static RnCRequest copy(RnCRequest rnCRequest) {

		RnCRequest request = new RnCRequest();
		for (String key : rnCRequest.attributes.keySet()) {
			request.setAttribute(key, rnCRequest.getAttribute(key));
		}
		request.parameters = rnCRequest.parameters;
		request.statusMessage = rnCRequest.statusMessage;
		request.sourceUnitName = rnCRequest.sourceUnitName;
		request.serialNumber = rnCRequest.serialNumber;
		request.processDate = rnCRequest.processDate;
		request.ratingHuntingTrace = rnCRequest.ratingHuntingTrace;
		request.stringWriter = rnCRequest.stringWriter;
		return request;
		
	}
	

}
