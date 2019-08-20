package com.elitecore.elitesm.web.dashboard.memoryusage;

public class GarbageCollectorData {

	private String name;
	private Long collectionCount;
	private Long collectionTime;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getCollectionCount() {
		return collectionCount;
	}
	public void setCollectionCount(Long collectionCount) {
		this.collectionCount = collectionCount;
	}
	public Long getCollectionTime() {
		return collectionTime;
	}
	public void setCollectionTime(Long collectionTime) {
		this.collectionTime = collectionTime;
	}
}
