package com.elitecore.netvertex.service.offlinernc.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;

public class RnCResponse implements ServiceResponse {
	
	private Map<String, String> attributes = new HashMap<>();
	private Map<String, Object> parameters = new HashMap<>();
	@Nullable
	private OfflineRnCErrorCodes errorCode;
	private String errorMessage;
	private String statusMessage;
	private String sourceUnitName;
	private String serialNumber;
	
	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getSourceUnitName() {
		return sourceUnitName;
	}

	public void setSourceUnitName(String sourceUnitName) {
		this.sourceUnitName = sourceUnitName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public void markForDropRequest() {
		// no-op
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public boolean isMarkedForDropRequest() {
		return false;
	}

	@Override
	public void setParameter(String key, Object parameterValue) {
		parameters.put(key, parameterValue);
	}

	@Override
	public Object getParameter(String str) {
		return parameters.get(str);
	}

	@Override
	public boolean isProcessingCompleted() {
		return false;
	}

	@Override
	public void setProcessingCompleted(boolean value) {
		// no-op
	}

	@Override
	public boolean isFurtherProcessingRequired() {
		return false;
	}

	@Override
	public void setFurtherProcessingRequired(boolean value) {
		// no-op
		
	}

	public String getAttribute(String key) {
		return attributes.get(key);
	}
	
	public String getAttribute(OfflineRnCKeyConstants key) {
		return attributes.get(key.getName());
	}
	
	public void setAttribute(OfflineRnCKeyConstants key, String value) {
		attributes.put(key.getName(), value);
	}
	
	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}
	
	public static RnCResponse of(RnCRequest request) {
		RnCResponse response = new RnCResponse();
		response.attributes.putAll(request.getAttributes());
		response.setStatusMessage(request.getStatusMessage());
		response.setSourceUnitName(request.getSourceUnitName());
		response.setSerialNumber(request.getSerialNumber());
		return response;
	}

	public @Nullable OfflineRnCErrorCodes getErrorCode() {
		return errorCode;
	}
	
	public @Nullable String getErrorMessage() {
		return errorMessage;
	}

	public void setError(OfflineRnCException ex) {
		this.errorCode = ex.getCode();
		this.errorMessage = ex.getMessage();
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
			.append("\t").append("SerialNumber = " + serialNumber)
			.append("\t").append("SourceUnitName = " + sourceUnitName);
		
		if (errorCode != null) {
			builder.append("\t").append("ErrorCode = " + errorCode);
		}
	}

}
