package com.elitecore.netvertex.gateway.file.parsing;

import java.util.HashMap;
import java.util.Map;

public class CommonResponsePacket {
	
	private long responseCode;
	private String responseMessage;
	private int totalSuccessfulEntities;
	private Map<Object,Object> map;
	
	public CommonResponsePacket() {
		map = new HashMap<>();
	}



	/**
	 * Gets the response message.
	 *
	 * @return the response message
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * Sets the response message.
	 *
	 * @param responseMessage the new response message
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	/**
	 * Gets the response code.
	 *
	 * @return the response code
	 */
	public long getResponseCode(){
		return responseCode;
	}

	/**
	 * Sets the response code.
	 *
	 * @param responceCode the new response code
	 */
	public void setResponseCode(long responceCode){
		responseCode = responceCode;
	}

	/**
	 * Gets the total successful entities.
	 *
	 * @return the total successful entities
	 */
	public int getTotalSuccessfulEntities() {
		return totalSuccessfulEntities;
	}

	/**
	 * Sets the total successful entities.
	 *
	 * @param totalSuccessfulEntities the new total successful entities
	 */
	public void setTotalSuccessfulEntities(int totalSuccessfulEntities) {
		this.totalSuccessfulEntities = totalSuccessfulEntities;
	}

	public Object getParameter(String key) {
		return map.get(key);
	}

	public void setParameter(String key, Object object) {
		map.put(key, object);
	}

}
