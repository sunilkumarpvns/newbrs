package com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory;

public class JVMDetailMemory {
	private String serverName;
	private String createTime;
	
	private Long edenSpaceMin;
	private Long edenSpaceMax;
	private float edenSpanceAvg;
	
	private Long survivorSpaceMin;
	private Long survivorSpaceMax;
	private float survivorSpaceAvg;
	
	private Long oldGenSpaceMin;
	private Long oldGenSpaceMax;
	private float oldGenSpaceAvg;
	
	private Long permGenSpaceMin;
	private Long permGenSpaceMax;
	private float permGenSpaceAvg;
	
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Long getEdenSpaceMin() {
		return edenSpaceMin;
	}
	public void setEdenSpaceMin(Long edenSpaceMin) {
		this.edenSpaceMin = edenSpaceMin;
	}
	public Long getEdenSpaceMax() {
		return edenSpaceMax;
	}
	public void setEdenSpaceMax(Long edenSpaceMax) {
		this.edenSpaceMax = edenSpaceMax;
	}
	public float getEdenSpanceAvg() {
		return edenSpanceAvg;
	}
	public void setEdenSpanceAvg(float edenSpanceAvg) {
		this.edenSpanceAvg = edenSpanceAvg;
	}
	public Long getSurvivorSpaceMin() {
		return survivorSpaceMin;
	}
	public void setSurvivorSpaceMin(Long survivorSpaceMin) {
		this.survivorSpaceMin = survivorSpaceMin;
	}
	public Long getSurvivorSpaceMax() {
		return survivorSpaceMax;
	}
	public void setSurvivorSpaceMax(Long survivorSpaceMax) {
		this.survivorSpaceMax = survivorSpaceMax;
	}
	public float getSurvivorSpaceAvg() {
		return survivorSpaceAvg;
	}
	public void setSurvivorSpaceAvg(float survivorSpaceAvg) {
		this.survivorSpaceAvg = survivorSpaceAvg;
	}
	public Long getOldGenSpaceMin() {
		return oldGenSpaceMin;
	}
	public void setOldGenSpaceMin(Long oldGenSpaceMin) {
		this.oldGenSpaceMin = oldGenSpaceMin;
	}
	public Long getOldGenSpaceMax() {
		return oldGenSpaceMax;
	}
	public void setOldGenSpaceMax(Long oldGenSpaceMax) {
		this.oldGenSpaceMax = oldGenSpaceMax;
	}
	public float getOldGenSpaceAvg() {
		return oldGenSpaceAvg;
	}
	public void setOldGenSpaceAvg(float oldGenSpaceAvg) {
		this.oldGenSpaceAvg = oldGenSpaceAvg;
	}
	public Long getPermGenSpaceMin() {
		return permGenSpaceMin;
	}
	public void setPermGenSpaceMin(Long permGenSpaceMin) {
		this.permGenSpaceMin = permGenSpaceMin;
	}
	public Long getPermGenSpaceMax() {
		return permGenSpaceMax;
	}
	public void setPermGenSpaceMax(Long permGenSpaceMax) {
		this.permGenSpaceMax = permGenSpaceMax;
	}
	public float getPermGenSpaceAvg() {
		return permGenSpaceAvg;
	}
	public void setPermGenSpaceAvg(float permGenSpaceAvg) {
		this.permGenSpaceAvg = permGenSpaceAvg;
	}
	
	@Override
	public String toString() {
		return "JVMDetailMemory [serverName=" + serverName + ", createTime="
				+ createTime + ", edenSpaceMin=" + edenSpaceMin
				+ ", edenSpaceMax=" + edenSpaceMax + ", edenSpanceAvg="
				+ edenSpanceAvg + ", survivorSpaceMin=" + survivorSpaceMin
				+ ", survivorSpaceMax=" + survivorSpaceMax
				+ ", survivorSpaceAvg=" + survivorSpaceAvg
				+ ", oldGenSpaceMin=" + oldGenSpaceMin + ", oldGenSpaceMax="
				+ oldGenSpaceMax + ", oldGenSpaceAvg=" + oldGenSpaceAvg
				+ ", permGenSpaceMin=" + permGenSpaceMin + ", permGenSpaceMax="
				+ permGenSpaceMax + ", permGenSpaceAvg=" + permGenSpaceAvg
				+ "]";
	}
	
}
