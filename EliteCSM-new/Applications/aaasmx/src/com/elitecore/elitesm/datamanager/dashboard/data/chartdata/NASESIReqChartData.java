package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;

public class NASESIReqChartData {
	
	private Long[] epochTime;
	private String[] esi;
	private Integer[] disconnectReq;
	private Integer[] disconnectNack;
	private Integer[] disconnectAck;
	private Integer[] disconnectTimeout;
	private Integer[] coaReq;
	private Integer[] coaNack;
	private Integer[] coaAck;
	private Integer[] coaTimeout;
	private NASESIReqChartData[] nasReqDataArray;
	private String[] serverAddress;
	private Integer[] serverPort;
	
	public Long[] getEpochTime() {
		return epochTime;
	}
	public void setEpochTime(Long[] epochTime) {
		this.epochTime = epochTime;
	}
	public String[] getEsi() {
		return esi;
	}
	public void setEsi(String[] esi) {
		this.esi = esi;
	}
	public Integer[] getDisconnectReq() {
		return disconnectReq;
	}
	public void setDisconnectReq(Integer[] disconnectReq) {
		this.disconnectReq = disconnectReq;
	}
	public Integer[] getDisconnectNack() {
		return disconnectNack;
	}
	public void setDisconnectNack(Integer[] disconnectNack) {
		this.disconnectNack = disconnectNack;
	}
	public Integer[] getDisconnectAck() {
		return disconnectAck;
	}
	public void setDisconnectAck(Integer[] disconnectAck) {
		this.disconnectAck = disconnectAck;
	}
	public Integer[] getDisconnectTimeout() {
		return disconnectTimeout;
	}
	public void setDisconnectTimeout(Integer[] disconnectTimeout) {
		this.disconnectTimeout = disconnectTimeout;
	}
	public Integer[] getCoaReq() {
		return coaReq;
	}
	public void setCoaReq(Integer[] coaReq) {
		this.coaReq = coaReq;
	}
	public Integer[] getCoaNack() {
		return coaNack;
	}
	public void setCoaNack(Integer[] coaNack) {
		this.coaNack = coaNack;
	}
	public Integer[] getCoaAck() {
		return coaAck;
	}
	public void setCoaAck(Integer[] coaAck) {
		this.coaAck = coaAck;
	}
	public Integer[] getCoaTimeout() {
		return coaTimeout;
	}
	public void setCoaTimeout(Integer[] coaTimeout) {
		this.coaTimeout = coaTimeout;
	}
	public NASESIReqChartData[] getNasReqDataArray() {
		return nasReqDataArray;
	}
	public void setNasReqDataArray(NASESIReqChartData[] nasReqDataArray) {
		this.nasReqDataArray = nasReqDataArray;
	}
	public String[] getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String[] serverAddress) {
		this.serverAddress = serverAddress;
	}
	public Integer[] getServerPort() {
		return serverPort;
	}
	public void setServerPort(Integer[] serverPort) {
		this.serverPort = serverPort;
	} 
}
