package com.elitecore.elitesm.datamanager.dashboard.data.chartdata;



public class TotalReqChartData {
	
	private Long[] epochTime;
	private String[] esi;
	private Integer[] accessChallenge;
	private Integer[] accessAccept;
	private Integer[] accessReject;
	private Integer[] requestDrop;
	private TotalReqChartData[] totalReqDataArray; 
	private String[] serverAddress;
	private Integer[] serverPort;
	
	public String[] getEsi() {
		return esi;
	}

	public void setEsi(String[] esi) {
		this.esi = esi;
	}

	public Integer[] getAccessChallenge() {
		return accessChallenge;
	}

	public void setAccessChallenge(Integer[] accessChallenge) {
		this.accessChallenge = accessChallenge;
	}

	public Integer[] getAccessAccept() {
		return accessAccept;
	}

	public void setAccessAccept(Integer[] accessAccept) {
		this.accessAccept = accessAccept;
	}

	public Integer[] getAccessReject() {
		return accessReject;
	}

	public void setAccessReject(Integer[] accessReject) {
		this.accessReject = accessReject;
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

	public TotalReqChartData[] getTotalReqDataArray() {
		return totalReqDataArray;
	}

	public void setTotalReqDataArray(TotalReqChartData[] totalReqDataArray) {
		this.totalReqDataArray = totalReqDataArray;
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
