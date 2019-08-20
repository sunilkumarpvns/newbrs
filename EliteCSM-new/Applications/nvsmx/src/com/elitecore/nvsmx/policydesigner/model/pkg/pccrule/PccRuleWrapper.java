package com.elitecore.nvsmx.policydesigner.model.pkg.pccrule;

import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;

import java.io.Serializable;

/**
 * This will wrap the gbrul/dl[with its unit] and mbrul/dl[with its unit].
 * @author Dhyani.raval
 *
 */
public class PccRuleWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String serviceName;
	private String serviceId;
	private String monitoringKey;
	private String gbrul;
	private String gbrdl;
	private String mbrul;
	private String mbrdl;
	private String scope;
	private String groups;
	private String type;

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}


	public static class PccRuleWrapperBuilder{
		
		private PccRuleWrapper pccRuleWrapper;
		
		public PccRuleWrapperBuilder(){
			pccRuleWrapper =  new PccRuleWrapper();
		}
		
		public PccRuleWrapper build(){
			return pccRuleWrapper;
		}

		public PccRuleWrapperBuilder withPccRulesDetails(PCCRuleData pccRule){
			setData(pccRule);
			return this;
		}
		
		private void setData(PCCRuleData pccRule){

			String Unit_ARROW ;
			final String UPLOAD_STRING = "<span class='glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow'></span>";
			final String DOWNLOAD_STRING = "<span class='glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow'></span>";
			pccRuleWrapper.id = pccRule.getId();
			pccRuleWrapper.type = pccRule.getType();
			pccRuleWrapper.name = pccRule.getName();
			pccRuleWrapper.serviceName = pccRule.getDataServiceTypeData().getName();
			pccRuleWrapper.serviceId = pccRule.getDataServiceTypeData().getId();
			pccRuleWrapper.monitoringKey = pccRule.getMonitoringKey();
			pccRuleWrapper.scope=pccRule.getScope().name();
			pccRuleWrapper.groups = pccRule.getGroups();

			String SPAN_WITH_STYLE = "<span style='display: inline-block;width: 45px;'>";
			String OPENING_DIV = "<div>";
			String CLOSING_DIV = "</div>";
			String CLOSING_SPAN = "</span>";
			String EMPTY_STRING = "";
			String SINGLE_SPACE = " ";

			Unit_ARROW = SPAN_WITH_STYLE+pccRule.getGbrdlUnit() + SINGLE_SPACE + DOWNLOAD_STRING + CLOSING_SPAN;
			pccRuleWrapper.gbrdl = OPENING_DIV + (pccRule.getGbrdl() == null ? EMPTY_STRING : String.valueOf(pccRule.getGbrdl())+ SINGLE_SPACE + Unit_ARROW) + CLOSING_DIV;

			Unit_ARROW = SPAN_WITH_STYLE+pccRule.getGbrulUnit() + SINGLE_SPACE + UPLOAD_STRING + CLOSING_SPAN;
			pccRuleWrapper.gbrul = OPENING_DIV + (pccRule.getGbrul() == null ? EMPTY_STRING : String.valueOf(pccRule.getGbrul())+ SINGLE_SPACE + Unit_ARROW)+ CLOSING_DIV;

			Unit_ARROW = SPAN_WITH_STYLE+pccRule.getMbrdlUnit() + SINGLE_SPACE + DOWNLOAD_STRING + CLOSING_SPAN;
			pccRuleWrapper.mbrdl = OPENING_DIV + (pccRule.getMbrdl() == null ? EMPTY_STRING : String.valueOf(pccRule.getMbrdl())+ SINGLE_SPACE + Unit_ARROW)+ CLOSING_DIV;

			Unit_ARROW = SPAN_WITH_STYLE+pccRule.getMbrulUnit() + SINGLE_SPACE + UPLOAD_STRING + CLOSING_SPAN;
			pccRuleWrapper.mbrul = OPENING_DIV + (pccRule.getMbrul() == null ? EMPTY_STRING : String.valueOf(pccRule.getMbrul())+ SINGLE_SPACE + Unit_ARROW)+ CLOSING_DIV;
			
		}

		public PccRuleWrapperBuilder withPccRulesDetails(ChargingRuleBaseNameData chargingRuleBaseNameData){
			setData(chargingRuleBaseNameData);
			return this;
		}

		private void setData(ChargingRuleBaseNameData chargingRuleBaseNameData){

			pccRuleWrapper.id = chargingRuleBaseNameData.getId();
			pccRuleWrapper.type = "CRBN";
			pccRuleWrapper.name = chargingRuleBaseNameData.getName();

			StringBuilder serviceTypes = new StringBuilder();
			StringBuilder serviceTypeIds = new StringBuilder();
			StringBuilder monitoringKeys = new StringBuilder();
			String COMMA = ", ";

			for(ChargingRuleDataServiceTypeData chargingRuleDataServiceTypeData : chargingRuleBaseNameData.getChargingRuleDataServiceTypeDatas()) {


				if( serviceTypes.length()==0 ) {
					serviceTypes.append(chargingRuleDataServiceTypeData.getDataServiceTypeData().getName());
					serviceTypeIds.append(chargingRuleDataServiceTypeData.getDataServiceTypeData().getId());
				} else {
					serviceTypes.append(COMMA);
					serviceTypes.append(chargingRuleDataServiceTypeData.getDataServiceTypeData().getName());
					serviceTypeIds.append(COMMA);
					serviceTypeIds.append(chargingRuleDataServiceTypeData.getDataServiceTypeData().getId());
				}

				if( monitoringKeys.length()==0 ) {
					monitoringKeys.append(chargingRuleDataServiceTypeData.getMonitoringKey());
				} else {
					monitoringKeys.append(COMMA);
					monitoringKeys.append(chargingRuleDataServiceTypeData.getMonitoringKey());
				}
			}

			pccRuleWrapper.serviceName = serviceTypes.toString();
			pccRuleWrapper.serviceId = serviceTypeIds.toString();
			pccRuleWrapper.monitoringKey = monitoringKeys.toString();

			pccRuleWrapper.groups = chargingRuleBaseNameData.getGroups();

		}
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMonitoringKey() {
		return monitoringKey;
	}
	public void setMonitoringKey(String monitoringKey) {
		this.monitoringKey = monitoringKey;
	}

	public String getGbrul() {
		return gbrul;
	}
	public void setGbrul(String gbrul) {
		this.gbrul = gbrul;
	}


	public String getGbrdl() {
		return gbrdl;
	}
	public void setGbrdl(String gbrdl) {
		this.gbrdl = gbrdl;
	}


	public String getMbrul() {
		return mbrul;
	}
	public void setMbrul(String mbrul) {
		this.mbrul = mbrul;
	}


	public String getMbrdl() {
		return mbrdl;
	}
	public void setMbrdl(String mbrdl) {
		this.mbrdl = mbrdl;
	}

	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}

	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
}
