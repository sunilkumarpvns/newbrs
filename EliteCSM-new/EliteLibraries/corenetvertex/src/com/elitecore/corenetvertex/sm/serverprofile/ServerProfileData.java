package com.elitecore.corenetvertex.sm.serverprofile;


import com.elitecore.corenetvertex.sm.ResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity( name="com.elitecore.corenetvertex.sm.serverprofile.ServerProfileData" )
@Table( name="TBLM_SERVER_PROFILE" )
public class ServerProfileData extends ResourceData {

    private Integer pcrfServiceQueueSize;
    private Integer pcrfServiceMinThreads;
    private Integer pcrfServiceMaxThreads;
    private Integer pcrfServiceWorkerThreadPriority;

    private Integer diameterQueueSize;
    private Integer diameterMinThreads;
    private Integer diameterMaxThreads;
    private Integer diameterDwInterval;
    private Boolean diameterDuplicateReqCheckEnabled = false;
    private Integer diameterDuplicateReqPurgeInterval;
    private Integer diameterSessionCleanupInterval;
    private Integer diameterSessionTimeout;

    private Integer radiusQueueSize;
    private Integer radiusMinThreads;
    private Integer radiusMaxThreads;
    private Boolean radiusDuplicateReqCheckEnabled = false;
    private Integer radiusDuplicateReqPurgeInterval;

    private String logLevel;
    private Integer rollingType;
    private Integer rollingUnits;
    private Integer maxRolledUnits;

    private Integer notificationServiceExecutionPeriod;
    private Integer maxParallelExecution;
    private Integer batchSize;
    

    public ServerProfileData(){
        super();
    }

    @Column(name="PCRF_SERVICE_QUEUE_SIZE")
    public Integer getPcrfServiceQueueSize() {
        return pcrfServiceQueueSize;
    }

    public void setPcrfServiceQueueSize(Integer pcrfServiceQueueSize) {
        this.pcrfServiceQueueSize = pcrfServiceQueueSize;
    }

    @Column(name="PCRF_SERVICE_MINIMUM_THREADS")
    public Integer getPcrfServiceMinThreads() {
        return pcrfServiceMinThreads;
    }

    public void setPcrfServiceMinThreads(Integer pcrfServiceMinThreads) {
        this.pcrfServiceMinThreads = pcrfServiceMinThreads;
    }

    @Column(name="PCRF_SERVICE_MAXIMUM_THREADS")
    public Integer getPcrfServiceMaxThreads() {
        return pcrfServiceMaxThreads;
    }

    public void setPcrfServiceMaxThreads(Integer pcrfServiceMaxThreads) {
        this.pcrfServiceMaxThreads = pcrfServiceMaxThreads;
    }

    @Column(name="PCRF_SRVC_WRKR_THREAD_PRIORITY")
    public Integer getPcrfServiceWorkerThreadPriority() {
        return pcrfServiceWorkerThreadPriority;
    }

    public void setPcrfServiceWorkerThreadPriority(Integer pcrfServiceWorkerThreadPriority) {
        this.pcrfServiceWorkerThreadPriority = pcrfServiceWorkerThreadPriority;
    }

    @Column(name="DIA_QUEUE_SIZE")
    public Integer getDiameterQueueSize() {
        return diameterQueueSize;
    }

    public void setDiameterQueueSize(Integer diameterQueueSize) {
        this.diameterQueueSize = diameterQueueSize;
    }

    @Column(name="DIA_MINIMUM_THREADS")
    public Integer getDiameterMinThreads() {
        return diameterMinThreads;
    }

    public void setDiameterMinThreads(Integer diameterMinThreads) {
        this.diameterMinThreads = diameterMinThreads;
    }

    @Column(name="DIA_MAXIMUM_THREADS")
    public Integer getDiameterMaxThreads() {
        return diameterMaxThreads;
    }

    public void setDiameterMaxThreads(Integer diameterMaxThreads) {
        this.diameterMaxThreads = diameterMaxThreads;
    }

    @Column(name="DIA_DW_INTERVAL")
    public Integer getDiameterDwInterval() {
        return diameterDwInterval;
    }

    public void setDiameterDwInterval(Integer diameterDwInterval) {
        this.diameterDwInterval = diameterDwInterval;
    }

    @Column(name="DIA_DUPLICATE_REQ_CHECK")
    public Boolean getDiameterDuplicateReqCheckEnabled() {
        return diameterDuplicateReqCheckEnabled;
    }

    public void setDiameterDuplicateReqCheckEnabled(Boolean diameterDuplicateReqCheckEnabled) {
        this.diameterDuplicateReqCheckEnabled = diameterDuplicateReqCheckEnabled;
    }

    @Column(name="DIA_DUPLICATE_REQ_PURGE_INTRVL")
    public Integer getDiameterDuplicateReqPurgeInterval() {
        return diameterDuplicateReqPurgeInterval;
    }

    public void setDiameterDuplicateReqPurgeInterval(Integer diameterDuplicateReqPurgeInterval) {
        this.diameterDuplicateReqPurgeInterval = diameterDuplicateReqPurgeInterval;
    }

    @Column(name="DIA_SESSION_CLEAN_UP_INTERVAL")
    public Integer getDiameterSessionCleanupInterval() {
        return diameterSessionCleanupInterval;
    }

    public void setDiameterSessionCleanupInterval(Integer diameterSessionCleanupInterval) {
        this.diameterSessionCleanupInterval = diameterSessionCleanupInterval;
    }

    @Column(name="DIA_SESSION_TIMEOUT")
    public Integer getDiameterSessionTimeout() {
        return diameterSessionTimeout;
    }

    public void setDiameterSessionTimeout(Integer diameterSessionTimeout) {
        this.diameterSessionTimeout = diameterSessionTimeout;
    }

    @Column(name="RAD_QUEUE_SIZE")
    public Integer getRadiusQueueSize() {
        return radiusQueueSize;
    }

    public void setRadiusQueueSize(Integer radiusQueueSize) {
        this.radiusQueueSize = radiusQueueSize;
    }

    @Column(name="RAD_MINIMUM_THREADS")
    public Integer getRadiusMinThreads() {
        return radiusMinThreads;
    }

    public void setRadiusMinThreads(Integer radiusMinThreads) {
        this.radiusMinThreads = radiusMinThreads;
    }

    @Column(name="RAD_MAXIMUM_THREADS")
    public Integer getRadiusMaxThreads() {
        return radiusMaxThreads;
    }

    public void setRadiusMaxThreads(Integer radiusMaxThreads) {
        this.radiusMaxThreads = radiusMaxThreads;
    }

    @Column(name="RAD_DUPLICATE_REQ_CHECK")
    public Boolean getRadiusDuplicateReqCheckEnabled() {
        return radiusDuplicateReqCheckEnabled;
    }

    public void setRadiusDuplicateReqCheckEnabled(Boolean radiusDuplicateReqCheckEnabled) {
        this.radiusDuplicateReqCheckEnabled = radiusDuplicateReqCheckEnabled;
    }

    @Column(name="RAD_DUPLICATE_REQ_PURGE_INTRVL")
    public Integer getRadiusDuplicateReqPurgeInterval() {
        return radiusDuplicateReqPurgeInterval;
    }

    public void setRadiusDuplicateReqPurgeInterval(Integer radiusDuplicateReqPurgeInterval) {
        this.radiusDuplicateReqPurgeInterval = radiusDuplicateReqPurgeInterval;
    }

    @Column(name="LOG_LEVEL")
    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Column(name="ROLLING_TYPE")
    public Integer getRollingType() {
        return rollingType;
    }

    public void setRollingType(Integer rollingType) {
        this.rollingType = rollingType;
    }

    @Column(name="ROLLING_UNITS")
    public Integer getRollingUnits() {
        return rollingUnits;
    }

    public void setRollingUnits(Integer rollingUnits) {
        this.rollingUnits = rollingUnits;
    }

    @Column(name="MAX_ROLLED_UNITS")
    public Integer getMaxRolledUnits() {
        return maxRolledUnits;
    }

    public void setMaxRolledUnits(Integer maxRolledUnits) {
        this.maxRolledUnits = maxRolledUnits;
    }

    @Column(name="NOTIF_SRVC_EXEC_PERIOD")
    public Integer getNotificationServiceExecutionPeriod() {
        return notificationServiceExecutionPeriod;
    }

    public void setNotificationServiceExecutionPeriod(Integer notificationServiceExecutionPeriod) {
        this.notificationServiceExecutionPeriod = notificationServiceExecutionPeriod;
    }

    @Column(name="MAX_PARALLEL_EXECUTION")
    public Integer getMaxParallelExecution() {
        return maxParallelExecution;
    }

    public void setMaxParallelExecution(Integer maxParallelExecution) {
        this.maxParallelExecution = maxParallelExecution;
    }

    @Column(name="BATCH_SIZE")
    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    @Transient
    @JsonIgnore
    public String getGroups() { return super.getGroups(); }

    @Override
    @Transient
    @JsonIgnore
    public String getGroupNames(){
        return super.getGroupNames();
    }

    @Transient
    @Override
    public String getResourceName() {
        return "ServerProfile";
    }

    @Override
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("PCRF Service Queue Size", pcrfServiceQueueSize);
        jsonObject.addProperty("PCRF Service Min Threads", pcrfServiceMinThreads);
        jsonObject.addProperty("PCRF Service Max Threads",pcrfServiceMaxThreads);
        jsonObject.addProperty("PCRF Service Worker Thread Priority",pcrfServiceWorkerThreadPriority);
        jsonObject.addProperty("Diameter Queue Size",diameterQueueSize);
        jsonObject.addProperty("Diameter Min Threads",diameterMinThreads);
        jsonObject.addProperty("Diameter Max Threads",diameterMaxThreads);
        jsonObject.addProperty("Diameter Dw Interval",diameterDwInterval);
        jsonObject.addProperty("Diameter Duplicate Request Check Enabled", diameterDuplicateReqCheckEnabled);
        jsonObject.addProperty("Diameter Duplicate Request Purge Interval",diameterDuplicateReqPurgeInterval);
        jsonObject.addProperty("Diameter Session Cleanup Interval",diameterSessionCleanupInterval);
        jsonObject.addProperty("Diameter Session Timeout",diameterSessionTimeout);
        jsonObject.addProperty("Radius Queue Size",radiusQueueSize);
        jsonObject.addProperty("Radius Min Threads",radiusMinThreads);
        jsonObject.addProperty("Radius Max Threads",radiusMaxThreads);
        jsonObject.addProperty("Radius Duplicate Request Check Enabled",radiusDuplicateReqCheckEnabled);
        jsonObject.addProperty("Radius Duplicate Request Purge Interval",radiusDuplicateReqPurgeInterval);
        jsonObject.addProperty("Log Level",logLevel);
        jsonObject.addProperty("Rolling Type", rollingType);
        jsonObject.addProperty("Rolling Units",rollingUnits);
        jsonObject.addProperty("Max Rolled Units",maxRolledUnits);
        jsonObject.addProperty("Notification Service Execution Period",notificationServiceExecutionPeriod);
        jsonObject.addProperty("Max Parallel Execution", maxParallelExecution);
        jsonObject.addProperty("Batch Size", batchSize);
        return jsonObject;
    }

    /**
	 * Returns an object with default values- meant to be disabled
	 * @return {@link ServerProfileData}
	 */
	public static ServerProfileData disabled() {
		return new ServerProfileData();
	}
}
