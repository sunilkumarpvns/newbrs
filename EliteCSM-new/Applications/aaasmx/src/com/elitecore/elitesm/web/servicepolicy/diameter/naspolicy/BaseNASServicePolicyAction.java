package com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthMethodRelData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ServicePolicyConstants;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.AddNASServicePolicyForm;

public class BaseNASServicePolicyAction extends BaseWebAction{

	protected NASPolicyInstData convertFormToBean(AddNASServicePolicyForm addNASServicePolicyForm){

		NASPolicyInstData  nasPolicyInstData= new NASPolicyInstData();
		
		/* Basic Detail */
		nasPolicyInstData.setName(addNASServicePolicyForm.getName());
		nasPolicyInstData.setDescription(addNASServicePolicyForm.getDescription());
		nasPolicyInstData.setRuleSet(addNASServicePolicyForm.getRuleSet());
		nasPolicyInstData.setSessionManagement(addNASServicePolicyForm.getSessionManagement());
		if(BaseConstant.HIDE_STATUS.equals(addNASServicePolicyForm.getStatus())){
			nasPolicyInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}else{
			nasPolicyInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}
		nasPolicyInstData.setDefaultResponseBehaviourArgument(addNASServicePolicyForm.getDefaultResponseBehaviourArgument());
		nasPolicyInstData.setDefaultResponseBehaviour(addNASServicePolicyForm.getDefaultResponseBehaviour());
		
		/*Authenticate Parameters*/
		 
		if(addNASServicePolicyForm.isPap()){
			
			NASPolicyAuthMethodRelData data = new NASPolicyAuthMethodRelData();
			data.setAuthMethodTypeId(ServicePolicyConstants.PAP_AUTH_METHOD_TYPE_ID);
			nasPolicyInstData.getNasPolicyAuthMethodRelList().add(data);
		 }
		
		if(addNASServicePolicyForm.isChap()){
			NASPolicyAuthMethodRelData data = new NASPolicyAuthMethodRelData();
			data.setAuthMethodTypeId(ServicePolicyConstants.CHAP_AUTH_METHOD_TYPE_ID);
			nasPolicyInstData.getNasPolicyAuthMethodRelList().add(data);
		}
		
		nasPolicyInstData.setMultipleUserIdentity(addNASServicePolicyForm.getMultipleUserIdentity());
		nasPolicyInstData.setCaseSensitiveUserIdentity(addNASServicePolicyForm.getCaseSensitiveUserIdentity());
		nasPolicyInstData.setStripUserIdentity(addNASServicePolicyForm.getStripUserIdentity());
		nasPolicyInstData.setRealmSeparator(addNASServicePolicyForm.getRealmSeparator());
		nasPolicyInstData.setRealmPattern(addNASServicePolicyForm.getRealmPattern());
		nasPolicyInstData.setTrimUserIdentity(addNASServicePolicyForm.getTrimUserIdentity());
		nasPolicyInstData.setTrimPassword(addNASServicePolicyForm.getTrimPassword());
		nasPolicyInstData.setCui(addNASServicePolicyForm.getCui());
		nasPolicyInstData.setAdvancedCuiExpression(addNASServicePolicyForm.getAdvancedCuiExpression());
		nasPolicyInstData.setRequestType(addNASServicePolicyForm.getRequestType());
		nasPolicyInstData.setAnonymousProfileIdentity(addNASServicePolicyForm.getAnonymousProfileIdentity());
		
		/* Authorization Parameter */
		nasPolicyInstData.setRejectOnCheckItemNotFound(addNASServicePolicyForm.getRejectOnCheckItemNotFound());
		nasPolicyInstData.setRejectOnRejectItemNotFound(addNASServicePolicyForm.getRejectOnRejectItemNotFound());
		nasPolicyInstData.setActionOnPolicyNotFound(addNASServicePolicyForm.getActionOnPolicyNotFound());
		
		nasPolicyInstData.setWimax(addNASServicePolicyForm.getWimax());
		nasPolicyInstData.setGracePolicy(addNASServicePolicyForm.getGracePolicy());
		
		if(Strings.isNullOrBlank(addNASServicePolicyForm.getDiameterConcurrency()) || "0".equals(addNASServicePolicyForm.getDiameterConcurrency())){
			nasPolicyInstData.setDiameterConcurrency(null);
		}else{
			nasPolicyInstData.setDiameterConcurrency(addNASServicePolicyForm.getDiameterConcurrency());
		}
		
		if(Strings.isNullOrBlank(addNASServicePolicyForm.getAdditionalDiameterConcurrency()) || "0".equals(addNASServicePolicyForm.getAdditionalDiameterConcurrency())){
			nasPolicyInstData.setAdditionalDiameterConcurrency(null);
		}else{
			nasPolicyInstData.setAdditionalDiameterConcurrency(addNASServicePolicyForm.getDiameterConcurrency());
		}
		
		nasPolicyInstData.setDefaultSessionTimeout(addNASServicePolicyForm.getDefaultSessionTimeout());
		
		/*Accounting Parameter*/
		nasPolicyInstData.setNasResponseAttributesSet(addNASServicePolicyForm.getNasResponseAttributesSet());
				
		return nasPolicyInstData;

	}
	
}
