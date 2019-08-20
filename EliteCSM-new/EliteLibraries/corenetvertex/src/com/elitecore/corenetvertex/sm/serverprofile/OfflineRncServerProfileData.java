package com.elitecore.corenetvertex.sm.serverprofile;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;

@Entity(name = "com.elitecore.corenetvertex.sm.serverprofile.OfflineRncServerProfileData")
@Table(name = "TBLM_OFFLINE_RNC")
public class OfflineRncServerProfileData extends ResourceData implements Serializable {

	private static final long serialVersionUID = -8203939917690337725L;

	private int minThread;
	private int maxThread;
	private int fileBatchSize;
	private int fileBatchQueue;
	private int scanInterval;
	private int threadPriority;

	private String logLevel;
	private Integer rollingType;
	private Integer rollingUnits;
	private Integer maxRolledUnits;

	@Column(name = "MIN_THREAD")
	public int getMinThread() {
		return minThread;
	}

	public void setMinThread(int minThread) {
		this.minThread = minThread;
	}

	@Column(name = "MAX_THREAD")
	public int getMaxThread() {
		return maxThread;
	}

	public void setMaxThread(int maxThread) {
		this.maxThread = maxThread;
	}

	@Column(name = "FILE_BATCH_SIZE")
	public int getFileBatchSize() {
		return fileBatchSize;
	}

	public void setFileBatchSize(int fileBatchSize) {
		this.fileBatchSize = fileBatchSize;
	}

	@Column(name = "FILE_BATCH_QUEUE")
	public int getFileBatchQueue() {
		return fileBatchQueue;
	}

	public void setFileBatchQueue(int fileBatchQueue) {
		this.fileBatchQueue = fileBatchQueue;
	}

	@Column(name = "SCAN_INTERVAL")
	public int getScanInterval() {
		return scanInterval;
	}

	public void setScanInterval(int scanInterval) {
		this.scanInterval = scanInterval;
	}

	@Column(name = "THREAD_PRIORITY")
	public int getThreadPriority() {
		return threadPriority;
	}

	public void setThreadPriority(int threadPriority) {
		this.threadPriority = threadPriority;
	}

	@Column(name = "LOG_LEVEL")
	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	@Column(name = "ROLLING_TYPE")
	public Integer getRollingType() {
		return rollingType;
	}

	public void setRollingType(Integer rollingType) {
		this.rollingType = rollingType;
	}

	@Column(name = "ROLLING_UNITS")
	public Integer getRollingUnits() {
		return rollingUnits;
	}

	public void setRollingUnits(Integer rollingUnits) {
		this.rollingUnits = rollingUnits;
	}

	@Column(name = "MAX_ROLLED_UNITS")
	public Integer getMaxRolledUnits() {
		return maxRolledUnits;
	}

	public void setMaxRolledUnits(Integer maxRolledUnits) {
		this.maxRolledUnits = maxRolledUnits;
	}

	@Transient
	@Override
	@JsonIgnore
	public String getResourceName() {
		return ("Partner RnC");
	}

	@Override
	public JsonObject toJson() {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(FieldValueConstants.MIN_THREAD, minThread);
		jsonObject.addProperty(FieldValueConstants.MAX_THREAD, maxThread);
		jsonObject.addProperty(FieldValueConstants.THREAD_PRIORITY, threadPriority);
		jsonObject.addProperty(FieldValueConstants.FILE_BATCH_SIZE, fileBatchSize);
		jsonObject.addProperty(FieldValueConstants.FILE_BATCH_QUEUE, fileBatchQueue);
		jsonObject.addProperty(FieldValueConstants.SCAN_INTERVAL, scanInterval);
		jsonObject.addProperty(FieldValueConstants.LOG_LEVEL, logLevel);
		jsonObject.addProperty(FieldValueConstants.ROLLING_TYPE, rollingType);
		jsonObject.addProperty(FieldValueConstants.ROLLING_UNITS, rollingUnits);
		jsonObject.addProperty(FieldValueConstants.MAX_ROLLED_UNITS, maxRolledUnits);
		return jsonObject;
	}

	/**
	 * Returns an object with default values- meant to be disabled
	 * @return {@link OfflineRncServerProfileData}
	 */
	public static OfflineRncServerProfileData disabled() {
		return new OfflineRncServerProfileData();
	}

}
