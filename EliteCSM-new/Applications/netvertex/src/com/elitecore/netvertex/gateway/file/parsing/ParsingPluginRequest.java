package com.elitecore.netvertex.gateway.file.parsing;

public class ParsingPluginRequest {

	private GeneralBatchPacket packet;
	private CommonResponsePacket responsePacket;
	private boolean processOver;
	private Throwable exception;
	private long responseCode;
	private String responseMessage;
	private boolean processCancled;

	public ParsingPluginRequest(GeneralBatchPacket packet){
		this.packet = packet;
		responsePacket = null;
		processOver = false;
		exception = null;
		responseCode = 0;
		responseMessage = null;
		processCancled = false;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#getPacket()
	 */
	
	public GeneralBatchPacket getPacket() {
		return packet;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#setPacket(com.elitecore.core.commons.packet.IPacket)
	 */
	
	public void setPacket(GeneralBatchPacket packet) {
		this.packet = packet;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#isProcessOver()
	 */
	
	public boolean isProcessOver() {
		return processOver;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#setProcessOver()
	 */
	
	public void setProcessOver() {
		processOver = true;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#getException()
	 */
	
	public Throwable getException() {
		return exception;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#setException(java.lang.Throwable)
	 */
	
	public void setException(Throwable exception) {
		this.exception = exception;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#getResponsePacket()
	 */
	
	public CommonResponsePacket getResponsePacket() {
		return responsePacket;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#setResponsePacket(com.elitecore.core.commons.packet.IPacket)
	 */
	
	public void setResponsePacket(CommonResponsePacket responsePacket) {
		this.responsePacket = responsePacket;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#setResponseCode(long)
	 */
	
	public void setResponseCode(long responseCode) {
		this.responseCode = responseCode;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#getResponseCode()
	 */
	
	public long getResponseCode() {
		return responseCode;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#setResponseMessage(java.lang.String)
	 */
	
	public void setResponseMessage(String message) {
		responseMessage = message;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#getResponseMessage()
	 */
	
	public String getResponseMessage() {
		return responseMessage;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#setProcessCancled()
	 */
	
	public void setProcessCancled() {
		processCancled = true;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.plugins.IPluginRequest#isProcessCancled()
	 */
	
	public boolean isProcessCancled() {
		return processCancled;
	}
}
