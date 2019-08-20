package com.elitecore.elitesm.web.dashboard.widget.model.esi;

import java.sql.Timestamp;

public class ESIWidgetData {
	private Long ID;
	private String accessAccept;
	private String accessReject;
	private String accessChallange;
	private String requestDrop;
	private String esi;
	private Timestamp timestamp;
	
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	public String getAccessAccept() {
		return accessAccept;
	}
	public void setAccessAccept(String accessAccept) {
		this.accessAccept = accessAccept;
	}
	public String getAccessReject() {
		return accessReject;
	}
	public void setAccessReject(String accessReject) {
		this.accessReject = accessReject;
	}
	public String getAccessChallange() {
		return accessChallange;
	}
	public void setAccessChallange(String accessChallange) {
		this.accessChallange = accessChallange;
	}
	public String getRequestDrop() {
		return requestDrop;
	}
	public void setRequestDrop(String requestDrop) {
		this.requestDrop = requestDrop;
	}
	public String getEsi() {
		return esi;
	}
	public void setEsi(String esi) {
		this.esi = esi;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "ESIWidgetData [ID=" + ID + ", accessAccept=" + accessAccept
				+ ", accessReject=" + accessReject + ", accessChallange="
				+ accessChallange + ", requestDrop=" + requestDrop + ", esi="
				+ esi + ", timestamp=" + timestamp + "]";
	}
	
	
}
