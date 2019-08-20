package com.elitecore.elitesm.datamanager.diameter.routingconf.data;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;
import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableBLManager;
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.referencialdata.dao.EliteSMReferencialDAO;
import com.elitecore.elitesm.ws.rest.adapter.diameterpeerprofiles.FollowRedirectionAdapter;
import com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable.RoutingActionAdapter;
import com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable.RoutingTableNameToIdAdapter;
import com.elitecore.elitesm.ws.rest.adapter.diameterroutingtable.StatefulRoutingAdapter;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@XmlRootElement(name = "routing-configuration")
@ValidObject
@XmlType(propOrder = { "name", "description", "routingTableId", "realmName", "appIds", "originHost", "originRealm", "ruleset", "translationMappingName", "routingAction", "statefulRouting", "attachedRedirection", "transactionTimeout", "subscriberRoutingOneName", "subscriberRoutingTwoName", "diameterRoutingConfigFailureParamSet", "diameterPeerGroupDataSet" })
public class DiameterRoutingConfData extends BaseData implements Differentiable, Validator {
	
	private String routingConfigId;
	
	@Expose
	@SerializedName("Name")
	@NotEmpty(message = "Name must be specified.")
	@Pattern(regexp = RestValidationMessages.NAME_REGEX, message = RestValidationMessages.NAME_INVALID)
	@Length(max = 128, message = "Length of Routing Table Name must not greater than 128.")
	private String name;
	
	@Expose
	@SerializedName("Routing Table Name")
	@NotNull(message = "Routing Table Name must be specified and It must be valid value.")
	private String routingTableId;
	
	@Expose
	@SerializedName("Description")
	@Length(max = 512, message = "Length of Description must not more than 512.")
	private String description;
	
	@Expose
	@SerializedName("Destination Realm")
	@Length(max = 256, message = "Length of Destination Realm must not more than 256.")
	@NotEmpty(message = "Destination Realm must be specified.")
	private String realmName;
	
	@Expose
	@SerializedName("Application IDs")
	@Length(max = 128, message = "Length of Application IDs must not more than 128.")
	@NotEmpty(message = "Application IDs must be specified.")
	private String appIds;
	
	@Expose
	@SerializedName("Origin Host")
	@Length(max = 256, message = "Length of Origin Host must not more than 256.")
	private String originHost;
	
	@Expose
	@SerializedName("Origin Realm")
	@Length(max = 256, message = "Length of Origin Realm must not more than 256.")
	private String originRealm;
	
	@Expose
	@SerializedName("RuleSet")
	@Length(max = 2000, message = "Length of RuleSet must not more than 2000.")
	private String ruleset;
	 
	@Expose
	@SerializedName("Translation Mapping")
	private String transMapConfId;
	
	@Expose
	@SerializedName("Routing Action")
	@NotNull(message = "Routing Action must be specified. Value could be 'Local' or 'Relay' or 'Proxy' or 'Redirect' or 'Virtual'.")
	private Long routingAction;
	
	@Expose
	@SerializedName("Stateful Routing")
	@NotNull(message = "Stateful Routing must be specified. Value could be 'Enabled' or 'Disabled'.")
	private Long statefulRouting;
	
	@Expose
	@SerializedName("Transaction Time Out")
	@NotNull(message = "Transaction Time Out must be specified and It msut be Numeric.")
	private Long transactionTimeout;
	
	@Expose
	@SerializedName("Attached Redirection")
	@NotNull(message = "Attached Redirection must be specified. Value could be 'Enabled' or 'Disabled'.")
	@Pattern(regexp = "true|false", message = "Invalid value of Attached Redirection. Value could be 'Enabled' or 'Disabled'.")
	private String attachedRedirection;
	
	@Expose
	@SerializedName("Subscriber Routing 1")
	private String imsiBasedRoutingTableId;
	
	@Expose
	@SerializedName("Subscriber Routing 2")
	private String msisdnBasedRoutingTableId;
	
	private String subscriberRoutingOneName;
	private String subscriberRoutingTwoName;
	
	private TranslationMappingConfData translationMappingConfData;
	private Long orderNumber;
	private Timestamp createDate;
	private String createdByStaffId;
	private Timestamp lastModifiedDate;
	private String lastModifiedByStaffId;
	private Set<DiameterPeerGroupData> diameterPeerGroupDataSet;
	private DiameterRoutingTableData diameterRoutingTableData;
	
	private Long protocolFailureAction;
	private Long transientFailureAction;
	private Long permanentFailureAction;
	private String protocolFailureArguments;
	private String transientFailureArguments;
	private String permanentFailureArguments;
	private Long timeOutAction;
	private String timeOutArguments;
	
	@Valid
	private Set<DiameterRoutingConfigFailureParam> diameterRoutingConfigFailureParamSet;
	
	private String routingActionName;
	private String auditUId;
	private String copyPacketMapId;
	private CopyPacketTranslationConfData copyPacketMappingConfData;
	private IMSIBasedRoutingTableData imsiBasedRoutingTableData;
	private MSISDNBasedRoutingTableData msisdnBasedRoutingTableData;
	private String subsciberMode;
	
	private String translationMappingName;
	
	public DiameterRoutingConfData() {
		description = RestUtitlity.getDefaultDescription();
	}

	@XmlElementWrapper(name = "peer-group")
	@XmlElement(name = "peer-group-data")
	public Set<DiameterPeerGroupData> getDiameterPeerGroupDataSet() {
		return diameterPeerGroupDataSet;
	}

	public void setDiameterPeerGroupDataSet(Set<DiameterPeerGroupData> diameterPeerGroupDataSet) {
		this.diameterPeerGroupDataSet = diameterPeerGroupDataSet;
	}

	@XmlTransient
	public String getTransMapConfId() {
		return transMapConfId;
	}

	public void setTransMapConfId(String transMapConfId) {
		this.transMapConfId = transMapConfId;
	}

	@XmlTransient
	public String getRoutingConfigId() {
		return routingConfigId;
	}
	
	public void setRoutingConfigId(String routingConfigId) {
		this.routingConfigId = routingConfigId;
	}
	
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name = "destination-realm")
	public String getRealmName() {
		return realmName;
	}
	
	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}
	
	@XmlElement(name = "application-ids")
	public String getAppIds() {
		return appIds;
	}
	
	public void setAppIds(String appIds) {
		this.appIds = appIds;
	}
	
	@XmlElement(name = "origin-host")
	public String getOriginHost() {
		return originHost;
	}
	
	public void setOriginHost(String originHost) {
		this.originHost = originHost;
	}
	
	@XmlElement(name = "origin-realm")
	public String getOriginRealm() {
		return originRealm;
	}
	
	public void setOriginRealm(String originRealm) {
		this.originRealm = originRealm;
	}
	
	@XmlElement(name = "ruleset")
	public String getRuleset() {
		return ruleset;
	}
	
	public void setRuleset(String ruleset) {
		this.ruleset = ruleset;
	}
	
	@XmlTransient
	public TranslationMappingConfData getTranslationMappingConfData() {
		return translationMappingConfData;
	}
	
	public void setTranslationMappingConfData(TranslationMappingConfData translationMappingConfData) {
		this.translationMappingConfData = translationMappingConfData;
	}
	
	@XmlElement(name = "routing-action")
	@XmlJavaTypeAdapter(RoutingActionAdapter.class)
	public Long getRoutingAction() {
		return routingAction;
	}
	
	public void setRoutingAction(Long routingAction) {
		this.routingAction = routingAction;
	}
	
	@XmlTransient
	public Long getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	@XmlTransient
	public Timestamp getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	
	@XmlTransient
	public String getCreatedByStaffId() {
		return createdByStaffId;
	}
	
	public void setCreatedByStaffId(String createdByStaffId) {
		this.createdByStaffId = createdByStaffId;
	}
	
	@XmlTransient
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	@XmlTransient
	public String getLastModifiedByStaffId() {
		return lastModifiedByStaffId;
	}
	
	public void setLastModifiedByStaffId(String lastModifiedByStaffId) {
		this.lastModifiedByStaffId = lastModifiedByStaffId;
	}
	
	@XmlTransient
	public DiameterRoutingTableData getDiameterRoutingTableData() {
		return diameterRoutingTableData;
	}

	public void setDiameterRoutingTableData(DiameterRoutingTableData diameterRoutingTableData) {
		this.diameterRoutingTableData = diameterRoutingTableData;
	}

	@XmlElement(name = "routing-table-name")
	@XmlJavaTypeAdapter(RoutingTableNameToIdAdapter.class)
	public String getRoutingTableId() {
		return routingTableId;
	}

	public void setRoutingTableId(String routingTableId) {
		this.routingTableId = routingTableId;
	}

	@XmlTransient
	public Long getProtocolFailureAction() {
		return protocolFailureAction;
	}

	public void setProtocolFailureAction(Long protocolFailureAction) {
		this.protocolFailureAction = protocolFailureAction;
	}

	@XmlTransient
	public Long getTransientFailureAction() {
		return transientFailureAction;
	}

	public void setTransientFailureAction(Long transientFailureAction) {
		this.transientFailureAction = transientFailureAction;
	}

	@XmlTransient
	public Long getPermanentFailureAction() {
		return permanentFailureAction;
	}

	public void setPermanentFailureAction(Long permanentFailureAction) {
		this.permanentFailureAction = permanentFailureAction;
	}

	@XmlTransient
	public String getProtocolFailureArguments() {
		return protocolFailureArguments;
	}

	public void setProtocolFailureArguments(String protocolFailureArguments) {
		this.protocolFailureArguments = protocolFailureArguments;
	}

	@XmlTransient
	public String getTransientFailureArguments() {
		return transientFailureArguments;
	}

	public void setTransientFailureArguments(String transientFailureArguments) {
		this.transientFailureArguments = transientFailureArguments;
	}

	@XmlTransient
	public String getPermanentFailureArguments() {
		return permanentFailureArguments;
	}

	public void setPermanentFailureArguments(String permanentFailureArguments) {
		this.permanentFailureArguments = permanentFailureArguments;
	}

	@XmlTransient
	public Long getTimeOutAction() {
		return timeOutAction;
	}

	public void setTimeOutAction(Long timeOutAction) {
		this.timeOutAction = timeOutAction;
	}

	@XmlTransient
	public String getTimeOutArguments() {
		return timeOutArguments;
	}

	public void setTimeOutArguments(String timeOutArguments) {
		this.timeOutArguments = timeOutArguments;
	}

	@XmlElement(name = "transaction-timeout")
	public Long getTransactionTimeout() {
		return transactionTimeout;
	}

	public void setTransactionTimeout(Long transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}

	@XmlElement(name = "stateful-routing")
	@XmlJavaTypeAdapter(StatefulRoutingAdapter.class)
	public Long getStatefulRouting() {
		return statefulRouting;
	}

	public void setStatefulRouting(Long statefulRouting) {
		this.statefulRouting = statefulRouting;
	}

	@XmlElementWrapper(name = "failure-actions")
	@XmlElement(name = "failure-action-data")
	public Set<DiameterRoutingConfigFailureParam> getDiameterRoutingConfigFailureParamSet() {
		return diameterRoutingConfigFailureParamSet;
	}

	public void setDiameterRoutingConfigFailureParamSet(Set<DiameterRoutingConfigFailureParam> diameterRoutingConfigFailureParamSet) {
		this.diameterRoutingConfigFailureParamSet = diameterRoutingConfigFailureParamSet;
	}

	@XmlElement(name = "attached-redirection")
	@XmlJavaTypeAdapter(FollowRedirectionAdapter.class)
	public String getAttachedRedirection() {
		return attachedRedirection;
	}

	public void setAttachedRedirection(String attachedRouting) {
		this.attachedRedirection = attachedRouting;
	}
	
	@XmlTransient
	public String getRoutingActionName() {
		return RoutingActions.getActionString(getRoutingAction().intValue());
	}

	public void setRoutingActionName(String routingActionName) {
		this.routingActionName = routingActionName;
	}

	@XmlTransient
	public String getAuditUId() {
		return auditUId;
	}

	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
	
	@XmlTransient
	public CopyPacketTranslationConfData getCopyPacketMappingConfData() {
		return copyPacketMappingConfData;
	}
	
	public void setCopyPacketMappingConfData(CopyPacketTranslationConfData copyPacketMappingConfData) {
		this.copyPacketMappingConfData = copyPacketMappingConfData;
	}
	
	@XmlTransient
	public String getCopyPacketMapId() {
		return copyPacketMapId;
	}
	
	public void setCopyPacketMapId(String copyPacketMapId) {
		this.copyPacketMapId = copyPacketMapId;
	}

	@XmlTransient
	public String getImsiBasedRoutingTableId() {
		return imsiBasedRoutingTableId;
	}

	public void setImsiBasedRoutingTableId(String imsiBasedRoutingTableId) {
		this.imsiBasedRoutingTableId = imsiBasedRoutingTableId;
	}

	@XmlTransient
	public IMSIBasedRoutingTableData getImsiBasedRoutingTableData() {
		return imsiBasedRoutingTableData;
	}

	public void setImsiBasedRoutingTableData(IMSIBasedRoutingTableData imsiBasedRoutingTableData) {
		this.imsiBasedRoutingTableData = imsiBasedRoutingTableData;
	}

	@XmlTransient
	public String getMsisdnBasedRoutingTableId() {
		return msisdnBasedRoutingTableId;
	}

	public void setMsisdnBasedRoutingTableId(String msisdnBasedRoutingTableId) {
		this.msisdnBasedRoutingTableId = msisdnBasedRoutingTableId;
	}
	
	@XmlElement(name = "subscriber-routing-1")
	public String getSubscriberRoutingOneName() {
		return subscriberRoutingOneName;
	}
	
	public void setSubscriberRoutingOneName(String subscriberRoutingOneName) {
		this.subscriberRoutingOneName = subscriberRoutingOneName;
	}
	
	@XmlElement(name = "subscriber-routing-2")
	public String getSubscriberRoutingTwoName() {
		return subscriberRoutingTwoName;
	}
	
	public void setSubscriberRoutingTwoName(String subscriberRoutingTwoName) {
		this.subscriberRoutingTwoName = subscriberRoutingTwoName;
	}
	
	@XmlTransient
	public String getSubsciberMode() {
		return subsciberMode;
	}

	public void setSubsciberMode(String subsciberMode) {
		this.subsciberMode = subsciberMode;
	}

	@XmlTransient
	public MSISDNBasedRoutingTableData getMsisdnBasedRoutingTableData() {
		return msisdnBasedRoutingTableData;
	}

	public void setMsisdnBasedRoutingTableData(MSISDNBasedRoutingTableData msisdnBasedRoutingTableData) {
		this.msisdnBasedRoutingTableData = msisdnBasedRoutingTableData;
	}

	@XmlElement(name = "translation-mapping")
	public String getTranslationMappingName() {
		return translationMappingName;
	}

	public void setTranslationMappingName(String translationMappingName) {
		this.translationMappingName = translationMappingName;
	}
	
	@Override
	public String toString() {
		StringWriter out = new StringWriter();
		TabbedPrintWriter writer = new TabbedPrintWriter(out);
		writer.print(StringUtility.fillChar("", 30, '-'));
		writer.print(this.getClass().getName());
		writer.println(StringUtility.fillChar("", 30, '-'));
		writer.incrementIndentation();
		
		writer.println("Routing ConfigId :" + routingConfigId);
		writer.println("Name :" + name);
		writer.println("Description :" + description);
		writer.println("Realm Name :" + realmName);
		writer.println("AppIds :" + appIds);
		writer.println("Origin Host :" + originHost);
		writer.println("Origin Realm :" + originRealm);
		writer.println("Ruleset :" + ruleset);
		writer.println("Trans Map ConfId :" + transMapConfId);
		writer.println("Translation Mapping Conf Data :" + translationMappingConfData);
		writer.println("Copy Packet Mapping ConfId :" + copyPacketMapId);
		writer.println("Routing Action :" + routingAction);
		writer.println("Order Number :" + orderNumber);
		writer.println("Create Date :" + createDate);
		writer.println("CreatedBy StaffId :" + createdByStaffId);
		writer.println("Last Modified Date :" + lastModifiedDate);
		writer.println("Last ModifiedBy StaffId :" + lastModifiedByStaffId);
		writer.println("Diameter Peer Group DataSet :" + diameterPeerGroupDataSet);
		writer.println("Diameter Routing Table Data :" + diameterRoutingTableData);
		writer.println("Routing TableId :" + routingTableId);
		writer.println("Protocol Failure Action :" + protocolFailureAction);
		writer.println("Transient Failure Action :" + transientFailureAction);
		writer.println("Permanent Failure Action :" + permanentFailureAction);
		writer.println("Protocol Failure Arguments :" + protocolFailureArguments);
		writer.println("Transient Failure Arguments :" + transientFailureArguments);
		writer.println("Permanent Failure Arguments :" + permanentFailureArguments);
		writer.println("TimeOut Action :" + timeOutAction);
		writer.println("TimeOut Arguments :" + timeOutArguments);
		writer.println("Transaction Timeout :" + transactionTimeout);
		writer.println("Stateful Routing :" + statefulRouting);
		writer.println("Attached Redirection :" + attachedRedirection);
		writer.println("Diameter Routing Config Failure ParamSet :" + diameterRoutingConfigFailureParamSet);
		writer.println("Routing Action Name :" + routingActionName);
		
		writer.decrementIndentation();
		writer.println(StringUtility.fillChar("", 80, '-'));
		writer.close();
		return out.toString();
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject object = new JSONObject();
		object.put("Name", name);
		object.put("Description", description);
		object.put("Routing Table Name", EliteSMReferencialDAO.fetchDiameterRoutingTableData(routingTableId));
		object.put("Destination Realm", realmName);
		object.put("Application IDs", appIds);
		object.put("Origin Host", originHost);
		object.put("Origin Realm", originRealm);
		object.put("RuleSet", ruleset);
		object.put("Translation Mapping",  EliteSMReferencialDAO.fetchTranslationMappingData(transMapConfId));
		object.put("Copy Packet Mapping", EliteSMReferencialDAO.fetchCopypacketMappingData(copyPacketMapId));
		object.put("Routing Action", RoutingActions.getActionString(getRoutingAction().intValue()));
		object.put("Stateful Routing", (statefulRouting == 1) ? "Enabled" : "Disabled");
		object.put("Attached Redirection", (attachedRedirection.equals("true")) ? "Enabled" : "Disabled");
		object.put("Transaction Time Out", transactionTimeout);
		if(diameterRoutingConfigFailureParamSet!=null){
			JSONArray fields = new JSONArray();
			for (DiameterRoutingConfigFailureParam element : diameterRoutingConfigFailureParamSet) {
				fields.add(element.toJson());
			}
			object.put("Failure Actions", fields);
		}
		if(diameterPeerGroupDataSet!=null){
			JSONArray array = new JSONArray();
			for (DiameterPeerGroupData element : diameterPeerGroupDataSet) {
				array.add(element.toJson());
			}
			object.put("Peer Group", array);
		}
		
		if( subsciberMode != null ){
			if(subsciberMode.equals(CommonConstants.IMSI_MSISDN )){
				object.put("Subscriber Routing 1", EliteSMReferencialDAO.fetchIMSIBasedRoutingTableData(imsiBasedRoutingTableId));
				object.put("Subscriber Routing 2", EliteSMReferencialDAO.fetchMSISDNBasedRoutingTableData(msisdnBasedRoutingTableId));
			}else if(subsciberMode.equals(CommonConstants.MSISDN_IMSI)){
				object.put("Subscriber Routing 1", EliteSMReferencialDAO.fetchMSISDNBasedRoutingTableData(msisdnBasedRoutingTableId));
				object.put("Subscriber Routing 2", EliteSMReferencialDAO.fetchIMSIBasedRoutingTableData(imsiBasedRoutingTableId));
			}
		}
		
		return object;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		
		boolean isValid = true;
		
		if (Strings.isNullOrBlank(subscriberRoutingOneName) == false) {
			try {
				if (new IMSIBasedRoutingTableBLManager().getIMSIDataByName(subscriberRoutingOneName) != null) {
					imsiBasedRoutingTableId = new IMSIBasedRoutingTableBLManager().getIMSIDataByName(subscriberRoutingOneName).getRoutingTableId();
					if (Strings.isNullOrBlank(subscriberRoutingTwoName) == false) {
						try {
							if (new MSISDNBasedRoutingTableBLManager().getMSISDNDataByName(subscriberRoutingTwoName) != null) {
								msisdnBasedRoutingTableId = new MSISDNBasedRoutingTableBLManager().getMSISDNDataByName(subscriberRoutingTwoName).getRoutingTableId();
							} else {
								isValid = false;
								RestUtitlity.setValidationMessage(context, "Invalid MSISDN name in Subscriber Routing 2.");
							}
						} catch (Exception e) {
							isValid = false;
							RestUtitlity.setValidationMessage(context, "Invalid MSISDN name in Subscriber Routing 2.");
						}
					}
					subsciberMode = CommonConstants.IMSI_MSISDN;
				} else if (new MSISDNBasedRoutingTableBLManager().getMSISDNDataByName(subscriberRoutingOneName) != null) {
					msisdnBasedRoutingTableId = new MSISDNBasedRoutingTableBLManager().getMSISDNDataByName(subscriberRoutingOneName).getRoutingTableId();
					if (Strings.isNullOrBlank(subscriberRoutingTwoName) == false) {
						try {
							if (new IMSIBasedRoutingTableBLManager().getIMSIDataByName(subscriberRoutingTwoName) != null) {
								imsiBasedRoutingTableId = new IMSIBasedRoutingTableBLManager().getIMSIDataByName(subscriberRoutingTwoName).getRoutingTableId();
							} else {
								isValid = false;
								RestUtitlity.setValidationMessage(context, "Invalid IMSI name in Subscriber Routing 2.");
							}
						} catch (Exception e) {
							isValid = false;
							RestUtitlity.setValidationMessage(context, "Invalid IMSI name in Subscriber Routing 2.");
						}
					}
					subsciberMode = CommonConstants.MSISDN_IMSI;
				} else {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid IMSI or MSISDN name in Subscriber Routing 1.");
				}
			} catch (Exception e) {
				try {
					msisdnBasedRoutingTableId = new MSISDNBasedRoutingTableBLManager().getMSISDNDataByName(subscriberRoutingOneName).getRoutingTableId();
					if (Strings.isNullOrBlank(subscriberRoutingTwoName) == false) {
						try {
							if (new IMSIBasedRoutingTableBLManager().getIMSIDataByName(subscriberRoutingTwoName) != null) {
								imsiBasedRoutingTableId = new IMSIBasedRoutingTableBLManager().getIMSIDataByName(subscriberRoutingTwoName).getRoutingTableId();
							} else {
								isValid = false;
								RestUtitlity.setValidationMessage(context, "Invalid IMSI name in Subscriber Routing 2.");
							}
						} catch (Exception e1) {
							isValid = false;
							RestUtitlity.setValidationMessage(context, "Invalid IMSI name in Subscriber Routing 2.");
						}
					}
					subsciberMode = CommonConstants.MSISDN_IMSI;
				} catch (Exception e2) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid IMSI or MSISDN name in Subscriber Routing 1.");
				}
			}
			
		} else if (Strings.isNullOrBlank(subscriberRoutingTwoName) == false) {
			isValid = false;
			RestUtitlity.setValidationMessage(context, "Only Subscriber Routing 2 is not allowed, Subscriber Routing 1 must be specified.");
		}
		
		if (Strings.isNullOrBlank(translationMappingName) == false) {
			try {
				transMapConfId = new TranslationMappingConfBLManager().getTranslationMappingConfDataByName(translationMappingName).getTranslationMapConfigId();
			} catch (Exception e) {
				try {
					copyPacketMapId = new CopyPacketTransMapConfBLManager().getCopyPacketTransMapConfigDetailDataByName(translationMappingName).getCopyPacketTransConfId();
				} catch (Exception e2) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Invalid value of Translation Mapping.");
				}
			}
		}
		
		if (Collectionz.isNullOrEmpty(diameterPeerGroupDataSet) == false) {
			for (DiameterPeerGroupData diameterPeerGroupData : diameterPeerGroupDataSet) {
				if (Strings.isNullOrBlank(diameterPeerGroupData.getRuleset())) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "RuleSet value of Peer Group for "+name+" Routing Configuration must be specified.");
				}
				if (Collectionz.isNullOrEmpty(diameterPeerGroupData.getDiameterPeerGroupRelDataSet())) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "At least one Peer entry for Peer Group of "+name+" Routing Configuration must be specified.");
				} else {
					Set<DiameterPeerGroupRelData> diameterPeerGroupRelDatas = diameterPeerGroupData.getDiameterPeerGroupRelDataSet();
					List<String> peerIds = new ArrayList<String>();
					for (DiameterPeerGroupRelData diameterPeerGroupRelData : diameterPeerGroupRelDatas) {
						if (diameterPeerGroupRelData.getPeerUUID() == null) {
							isValid = false;
							RestUtitlity.setValidationMessage(context, "Peer for Peer Group of "+name+" Routing Configuration must be specified and It must be valid value.");
						} else {
							if (diameterPeerGroupRelDatas.size() > 0 && peerIds.contains(diameterPeerGroupRelData.getPeerUUID())) {
								isValid = false;
								RestUtitlity.setValidationMessage(context, "Duplicate Peer entry for Peer Group of "+name+" Routing Configuration is not valid.");
							} 
							peerIds.add(diameterPeerGroupRelData.getPeerUUID());
						}
					}
				}
			}
		}
		
		if (Collectionz.isNullOrEmpty(diameterRoutingConfigFailureParamSet) == false) {
			for (DiameterRoutingConfigFailureParam diameterRoutingConfigFailureParam : diameterRoutingConfigFailureParamSet) {
				if (diameterRoutingConfigFailureParam.getFailureAction() == null) {
					isValid = false;
					RestUtitlity.setValidationMessage(context, "Failure Action must be specified. Value could be 'Drop' or 'Failover' or 'Redirect' or 'Passthrough' or 'Translate' or 'Record'.");
				}
			}
		}
		
		return isValid;
	}
	
}