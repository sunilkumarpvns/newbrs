package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;



public class RadAcctReqChartData {
	
	private Long[] epochTime;
	private String[] esi;
	private Integer[] accountingReq;
	private Integer[] accountingRes;
	private Integer[] retransmission;
	private Integer[] requestDrop;
	private RadAcctReqChartData[] totalReqDataArray; 
	private String[] serverAddress;
	private Integer[] serverPort;

	public String[] getEsi() {
		return esi;
	}
	public void setEsi(String[] esi) {
		this.esi = esi;
	}
	public Integer[] getRequestDrop() {
		return requestDrop;
	}
	public void setRequestDrop(Integer[] requestDrop) {
		this.requestDrop = requestDrop;
	}
	public Long[] getEpochTime() {
		return epochTime;
	}
	public void setEpochTime(Long[] epochTime) {
		this.epochTime = epochTime;
	}
	public RadAcctReqChartData[] getTotalReqDataArray() {
		return totalReqDataArray;
	}
	public void setTotalReqDataArray(RadAcctReqChartData[] totalReqDataArray) {
		this.totalReqDataArray = totalReqDataArray;
	}
	public Integer[] getAccountingReq() {
		return accountingReq;
	}
	public void setAccountingReq(Integer[] accountingReq) {
		this.accountingReq = accountingReq;
	}
	public Integer[] getAccountingRes() {
		return accountingRes;
	}
	public void setAccountingRes(Integer[] accountingRes) {
		this.accountingRes = accountingRes;
	}
	public Integer[] getRetransmission() {
		return retransmission;
	}
	public void setRetransmission(Integer[] retransmission) {
		this.retransmission = retransmission;
	}
	public Integer[] getServerPort() {
		return serverPort;
	}
	public void setServerPort(Integer[] serverPort) {
		this.serverPort = serverPort;
	}
	public String[] getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String[] serverAddress) {
		this.serverAddress = serverAddress;
	}
}
