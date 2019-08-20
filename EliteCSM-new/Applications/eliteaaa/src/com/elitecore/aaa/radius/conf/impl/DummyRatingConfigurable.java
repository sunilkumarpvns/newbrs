package com.elitecore.aaa.radius.conf.impl;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.radius.conf.DummyRatingConfiguration;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "dummy-rating")
@ConfigurationProperties(moduleName ="DUMMY_RATING_CONFIGURABLE",readWith = XMLReader.class, synchronizeKey ="DUMMY_RATING")
@XMLProperties(name = "dummy-rating", schemaDirectories = {"system","schema"}, configDirectories = {"conf"})
public class DummyRatingConfigurable extends Configurable implements DummyRatingConfiguration{

	private String sessionTimeOut;
	private String chargingType;
	private String quotaId;
	private String quota;
	private String thresoldId;
	private String iteration;
	private String termnationAction;
	private String terminataionActionReq;
	private QosConfigDetail qosConfigDetail ;
	private PacketDescriptorDetail packetDescriptorDetail;
	private HashMap<Object, Object> dummyRatingConfiguration;

	
	public DummyRatingConfigurable(){
		qosConfigDetail = new QosConfigDetail();
		packetDescriptorDetail = new PacketDescriptorDetail();
		this.dummyRatingConfiguration = new HashMap<Object, Object>();
	}
	@XmlElement(name = "qos")
	public QosConfigDetail getQosConfigDetail() {
		return qosConfigDetail;
	}

	public void setQosConfigDetail(QosConfigDetail qosConfigDetail) {
		this.qosConfigDetail = qosConfigDetail;
	}

	@XmlElement(name = "packet-flow-descriptor")
	public PacketDescriptorDetail getPacketDescriptorDetail() {
		return packetDescriptorDetail;
	}

	public void setPacketDescriptorDetail(PacketDescriptorDetail packetDescriptorDetail) {
		this.packetDescriptorDetail = packetDescriptorDetail;
	}

	@XmlElement(name = "session-timeout", type = String.class)
	public String getSessionTimeOut() {
		return sessionTimeOut;
	}

	public void setSessionTimeOut(String sessionTimeOut) {
		this.sessionTimeOut = sessionTimeOut;
	}
	@XmlElement(name = "charging-type", type = String.class)
	public String getChargingType() {
		return chargingType;
	}

	public void setChargingType(String chargingType) {
		this.chargingType = chargingType;
	}
	@XmlElement(name = "quota-id", type = String.class)
	public String getQuotaId() {
		return quotaId;
	}

	public void setQuotaId(String quotaId) {
		this.quotaId = quotaId;
	}
	@XmlElement(name = "quota", type = String.class)
	public String getQuota() {
		return quota;
	}

	public void setQuota(String quota) {
		this.quota = quota;
	}
	@XmlElement(name = "threshold", type = String.class)
	public String getThresoldId() {
		return thresoldId;
	}

	public void setThresoldId(String thresoldId) {
		this.thresoldId = thresoldId;
	}
	@XmlElement(name = "iterations", type = String.class)
	public String getIteration() {
		return iteration;
	}

	public void setIteration(String iteration) {
		this.iteration = iteration;
	}
	@XmlElement(name = "termination-action", type = String.class)
	public String getTermnationAction() {
		return termnationAction;
	}

	public void setTermnationAction(String termnationAction) {
		this.termnationAction = termnationAction;
	}
	@XmlElement(name = "action-on-terminate-request", type = String.class)
	public String getTerminataionActionReq() {
		return terminataionActionReq;
	}

	public void setTerminataionActionReq(String terminataionActionReq) {
		this.terminataionActionReq = terminataionActionReq;
	}

	@PostRead
	public void postReadProcessing() {

		HashMap<Object, Object> localParameterMap = new HashMap<Object, Object>();
		HashMap<Object, Object> qosParametersMap = new HashMap<Object, Object>();
		HashMap<Object, Object> packetFlowParametersMap = new HashMap<Object, Object>();


		if(getSessionTimeOut()!=null)
			localParameterMap.put(SESSION_TIMEOUT, getSessionTimeOut());

		if(getChargingType()!=null)
			localParameterMap.put(CHARGING_TYPE, getChargingType());

		if(getQuotaId()!=null)
			localParameterMap.put(QUOTA_ID, getQuotaId());

		if(getQuota()!=null)
			localParameterMap.put(QUOTA, getQuota());

		if(getThresoldId()!=null)
			localParameterMap.put(THRESHOLD, getThresoldId());

		if(getIteration()!=null)
			localParameterMap.put(ITERATIONS, getIteration());

		if(getTermnationAction()!=null)
			localParameterMap.put(TERMINATION_ACTION,getTermnationAction());

		if(getTerminataionActionReq()!=null)
			localParameterMap.put(ACTION_ON_TERMINATE_REQUEST, getTerminataionActionReq());
		
		QosConfigDetail qosConfObj = getQosConfigDetail();
		
		if(qosConfObj!=null){
			if(qosConfObj.getQosId()!=null)
				qosParametersMap.put(QOSID, qosConfObj.getQosId());
			
			if(qosConfObj.getGlobalServiceClassName()!=null)
				qosParametersMap.put(GLOBAL_SERVICE_CLASS_NAME, qosConfObj.getGlobalServiceClassName());
	
			if(qosConfObj.getServiceClassName()!=null)
				qosParametersMap.put(SERVICE_CLASS_NAME, qosConfObj.getServiceClassName());
	
			if(qosConfObj.getScheduleType()!=null)
				qosParametersMap.put(SCHEDULE_TYPE, qosConfObj.getScheduleType());	
	
			if(qosConfObj.getTrafficPriority()!=null)
				qosParametersMap.put(TRAFFIC_PRIORITY, qosConfObj.getTrafficPriority());
	
			if(qosConfObj.getMaxTrafficRate()!=null)
				qosParametersMap.put(MAX_TRAFFIC_RATE, qosConfObj.getMaxTrafficRate());
	
			if(qosConfObj.getMinTraficRate()!=null)
				qosParametersMap.put(MIN_TRAFFIC_RATE, qosConfObj.getMinTraficRate());
	
			if(qosConfObj.getMaxTrafficBurst()!=null)
				qosParametersMap.put(MAX_TRAFFIC_BURST, qosConfObj.getMaxTrafficBurst());
	
			if(qosConfObj.getTolaredJitter()!=null)
				qosParametersMap.put(TOLERATED_JITTER, qosConfObj.getTolaredJitter());
	
			if( qosConfObj.getMaxLatency()!=null)
				qosParametersMap.put(MAX_LATENCY, qosConfObj.getMaxLatency());
	
			if(qosConfObj.getResourceCode()!=null)
				qosParametersMap.put(REDUCED_RESOURCE_CODE, qosConfObj.getResourceCode());
	
			if(qosConfObj.getMediaFlowType()!=null)
				qosParametersMap.put(MEDIA_FLOW_TYPE, qosConfObj.getMediaFlowType());
	
			if( qosConfObj.getGrantInterval()!=null)
				qosParametersMap.put(UNSOLICITED_GRANT_INTERVAL, qosConfObj.getGrantInterval());
	
			if(qosConfObj.getSduSize()!=null)
				qosParametersMap.put(SDU_SIZE, qosConfObj.getSduSize());
	
			if(qosConfObj.getPollingInterval()!=null)
				qosParametersMap.put(UNSOLICITED_POLLING_INTERVAL, qosConfObj.getPollingInterval());
	
			if( qosConfObj.getMediaFlowDescription()!=null)
				qosParametersMap.put(MEDIA_FLOW_DESCRIPTION, qosConfObj.getMediaFlowDescription());
			
			localParameterMap.put(QOS, qosParametersMap);
			
		}
		PacketDescriptorDetail packetDescriptorObj = getPacketDescriptorDetail();
		if(packetDescriptorObj != null){
			if(packetDescriptorObj.getPdfId()!=null)
				packetFlowParametersMap.put(PDFID, packetDescriptorObj.getPdfId());
			if( packetDescriptorObj.getSdfId()!=null)
				packetFlowParametersMap.put(SDFID, packetDescriptorObj.getSdfId());
			if(packetDescriptorObj.getServiceProfileId()!=null)
				packetFlowParametersMap.put(SERVICE_PROFILE_ID, packetDescriptorObj.getServiceProfileId());
			if( packetDescriptorObj.getDirection()!=null)
				packetFlowParametersMap.put(DIRECTION, packetDescriptorObj.getDirection());
			if( packetDescriptorObj.getActivationTrigger()!=null)
				packetFlowParametersMap.put(ACTIVATION_TRIGGER, packetDescriptorObj.getActivationTrigger());
			if(packetDescriptorObj.getTraspoetType()!=null)
				packetFlowParametersMap.put(TRANSPORT_TYPE, packetDescriptorObj.getTraspoetType());
			if(packetDescriptorObj.getUplinkQosId()!=null)
				packetFlowParametersMap.put(UPLINK_QOS_ID, packetDescriptorObj.getUplinkQosId());
			if( packetDescriptorObj.getDownlinkQosId()!=null)
				packetFlowParametersMap.put(DOWNLINK_QOS_ID, packetDescriptorObj.getDownlinkQosId());
			if( packetDescriptorObj.getUpLinkClassifier()!=null)
				packetFlowParametersMap.put(UPLINK_CLASSIFIER, packetDescriptorObj.getUpLinkClassifier());
			if( packetDescriptorObj.getDownLinkClassifier()!=null)
				packetFlowParametersMap.put(DOWNLINK_CLASSIFIER, packetDescriptorObj.getDownLinkClassifier());
			
			localParameterMap.put(PACKET_FLOW_DESCRIPTOR, packetFlowParametersMap);
		}
		this.dummyRatingConfiguration = localParameterMap;
		
	}
	
	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {

	}
	
	@Override
	@XmlTransient
	public HashMap<Object, Object> getDummyRatingConfiguration() {
		return dummyRatingConfiguration;
	}

}

@XmlType(propOrder = {})
class QosConfigDetail{
	
	private String qosId;
	private String globalServiceClassName;
	private String serviceClassName;
	private String scheduleType;
	private String trafficPriority;
	private String maxTrafficRate;
	private String minTraficRate;
	private String maxTrafficBurst;
	private String tolaredJitter;
	private String maxLatency;
	private String resourceCode;
	private String mediaFlowType;
	private String grantInterval;
	private String sduSize;
	private String pollingInterval;
	private String mediaFlowDescription;
	
	public QosConfigDetail(){
		//required by Jaxb.
	}
	
	@XmlElement(name = "qos-id",type = String.class)
	public String getQosId() {
		return qosId;
	}
	public void setQosId(String qosId) {
		this.qosId = qosId;
	}
	@XmlElement(name = "global-service-class-name",type = String.class)
	public String getGlobalServiceClassName() {
		return globalServiceClassName;
	}
	public void setGlobalServiceClassName(String globalServiceClassName) {
		this.globalServiceClassName = globalServiceClassName;
	}
	@XmlElement(name = "service-class-name",type = String.class)
	public String getServiceClassName() {
		return serviceClassName;
	}
	public void setServiceClassName(String serviceClassName) {
		this.serviceClassName = serviceClassName;
	}
	@XmlElement(name = "schedule-type",type = String.class)
	public String getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	@XmlElement(name = "traffic-priority",type = String.class)
	public String getTrafficPriority() {
		return trafficPriority;
	}
	public void setTrafficPriority(String trafficPriority) {
		this.trafficPriority = trafficPriority;
	}
	@XmlElement(name = "max-traffic-rate",type = String.class)
	public String getMaxTrafficRate() {
		return maxTrafficRate;
	}
	public void setMaxTrafficRate(String maxTrafficRate) {
		this.maxTrafficRate = maxTrafficRate;
	}
	@XmlElement(name = "min-traffic-rate",type = String.class)
	public String getMinTraficRate() {
		return minTraficRate;
	}
	public void setMinTraficRate(String minTraficRate) {
		this.minTraficRate = minTraficRate;
	}
	@XmlElement(name = "max-traffic-burst",type = String.class)
	public String getMaxTrafficBurst() {
		return maxTrafficBurst;
	}
	public void setMaxTrafficBurst(String maxTrafficBurst) {
		this.maxTrafficBurst = maxTrafficBurst;
	}
	@XmlElement(name = "tolerated-jitter",type = String.class)
	public String getTolaredJitter() {
		return tolaredJitter;
	}
	public void setTolaredJitter(String tolaredJitter) {
		this.tolaredJitter = tolaredJitter;
	}
	@XmlElement(name = "max-latency",type = String.class)
	public String getMaxLatency() {
		return maxLatency;
	}
	public void setMaxLatency(String maxLatency) {
		this.maxLatency = maxLatency;
	}
	@XmlElement(name = "reduced-resource-code",type = String.class)
	public String getResourceCode() {
		return resourceCode;
	}
	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}
	@XmlElement(name = "media-flow-type",type = String.class)
	public String getMediaFlowType() {
		return mediaFlowType;
	}
	public void setMediaFlowType(String mediaFlowType) {
		this.mediaFlowType = mediaFlowType;
	}
	@XmlElement(name = "unsolicited-grant-interval",type = String.class)
	public String getGrantInterval() {
		return grantInterval;
	}
	public void setGrantInterval(String grantInterval) {
		this.grantInterval = grantInterval;
	}
	@XmlElement(name = "sdu-size",type = String.class)
	public String getSduSize() {
		return sduSize;
	}
	public void setSduSize(String sduSize) {
		this.sduSize = sduSize;
	}
	@XmlElement(name = "unsolicited-polling-interval",type = String.class)
	public String getPollingInterval() {
		return pollingInterval;
	}
	public void setPollingInterval(String pollingInterval) {
		this.pollingInterval = pollingInterval;
	}
	@XmlElement(name = "media-flow-description",type = String.class)
	public String getMediaFlowDescription() {
		return mediaFlowDescription;
	}
	public void setMediaFlowDescription(String mediaFlowDescription) {
		this.mediaFlowDescription = mediaFlowDescription;
	}



}

@XmlType(propOrder = {})
class PacketDescriptorDetail{
	
	private String pdfId;
	private String sdfId;
	private String serviceProfileId;
	private String direction;
	private String activationTrigger;
	private String traspoetType;
	private String uplinkQosId;
	private String downlinkQosId;
	private String upLinkClassifier;
	private String downLinkClassifier;

	public PacketDescriptorDetail(){
		//required by Jaxb.
	}
	
	@XmlElement(name = "pdfid",type = String.class)
	public String getPdfId() {
		return pdfId;
	}
	public void setPdfId(String pdfId) {
		this.pdfId = pdfId;
	}
	@XmlElement(name = "sdfid",type = String.class)
	public String getSdfId() {
		return sdfId;
	}
	public void setSdfId(String sdfId) {
		this.sdfId = sdfId;
	}
	@XmlElement(name = "service-profile-id",type = String.class)
	public String getServiceProfileId() {
		return serviceProfileId;
	}
	public void setServiceProfileId(String serviceProfileId) {
		this.serviceProfileId = serviceProfileId;
	}
	@XmlElement(name = "direction",type = String.class)
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	@XmlElement(name = "activation-trigger",type = String.class)
	public String getActivationTrigger() {
		return activationTrigger;
	}
	public void setActivationTrigger(String activationTrigger) {
		this.activationTrigger = activationTrigger;
	}
	@XmlElement(name = "transport-type",type = String.class)
	public String getTraspoetType() {
		return traspoetType;
	}
	public void setTraspoetType(String traspoetType) {
		this.traspoetType = traspoetType;
	}
	@XmlElement(name = "uplink-qos-id",type = String.class)
	public String getUplinkQosId() {
		return uplinkQosId;
	}
	public void setUplinkQosId(String uplinkQosId) {
		this.uplinkQosId = uplinkQosId;
	}
	@XmlElement(name = "downlink-qos-id",type = String.class)
	public String getDownlinkQosId() {
		return downlinkQosId;
	}
	public void setDownlinkQosId(String downlinkQosId) {
		this.downlinkQosId = downlinkQosId;
	}
	@XmlElement(name = "uplink-classifier",type = String.class)
	public String getUpLinkClassifier() {
		return upLinkClassifier;
	}
	public void setUpLinkClassifier(String upLinkClassifier) {
		this.upLinkClassifier = upLinkClassifier;
	}
	@XmlElement(name = "downlink-classifier",type = String.class)
	public String getDownLinkClassifier() {
		return downLinkClassifier;
	}
	public void setDownLinkClassifier(String downLinkClassifier) {
		this.downLinkClassifier = downLinkClassifier;
	}

}
