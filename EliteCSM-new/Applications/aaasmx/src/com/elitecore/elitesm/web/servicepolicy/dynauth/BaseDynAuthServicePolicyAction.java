package com.elitecore.elitesm.web.servicepolicy.dynauth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthNasClientsData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData;
import com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynaAuthPolicyESIRelData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servicepolicy.dynauth.forms.AddDynAuthServicePolicyForm;

public class BaseDynAuthServicePolicyAction extends BaseWebAction{

	protected DynAuthPolicyInstData convertFormToBean(AddDynAuthServicePolicyForm form,HashSet<DynAuthFieldMapData> tempFeildMapSet, List<DynaAuthPolicyESIRelData> nasClientList,LinkedHashSet<DynAuthNasClientsData> dynAuthNasClientsDataSet){

		DynAuthPolicyInstData dynAuthPolicyInstData = new DynAuthPolicyInstData();

		/* Basic Detail */
		dynAuthPolicyInstData.setName(form.getName());
		dynAuthPolicyInstData.setDescription(form.getDescription());
		dynAuthPolicyInstData.setRuleSet(form.getRuleSet());
		dynAuthPolicyInstData.setResponseAttributes(form.getResponseAttributes());
		dynAuthPolicyInstData.setEventTimestamp(form.getEventTimestamp());
		dynAuthPolicyInstData.setEligibleSession(form.getEligibleSessions());
		dynAuthPolicyInstData.setDatabaseDatasourceId(form.getDatabaseDatasourceId());
		dynAuthPolicyInstData.setTableName(form.getTableName());
		dynAuthPolicyInstData.setValidatePacket(form.getValidatePacket());
		dynAuthPolicyInstData.setOrderNumber(1L);
		dynAuthPolicyInstData.setDynAuthFeildMapSet(tempFeildMapSet);
		dynAuthPolicyInstData.setDynAuthNasClientDataSet(dynAuthNasClientsDataSet);
		if(BaseConstant.HIDE_STATUS.equals(form.getStatus())){
			dynAuthPolicyInstData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}else{
			dynAuthPolicyInstData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}
		dynAuthPolicyInstData.setDbFailureAction(form.getDbFailureAction());

		return dynAuthPolicyInstData;

	}

	protected List<DynaAuthPolicyESIRelData> getNASClientList(String[] nasClients) throws DataManagerException {

		List<DynaAuthPolicyESIRelData> dynaAuthPolicyESIRelList= new ArrayList<DynaAuthPolicyESIRelData>();
		if(nasClients!=null){

			for (int i = 0; i < nasClients.length; i++) {
				
				ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
				
				String nasESIInstanceId = nasClients[i];				
				DynaAuthPolicyESIRelData dynaAuthPolicyESIRelData = new DynaAuthPolicyESIRelData();
				dynaAuthPolicyESIRelData.setEsiInstanceId(nasESIInstanceId);
				ExternalSystemInterfaceInstanceData esiInstanceData=esiBLManager.getExternalSystemInterfaceInstanceDataById(nasESIInstanceId);
				dynaAuthPolicyESIRelData.setExternalSystemData(esiInstanceData);
				dynaAuthPolicyESIRelList.add(dynaAuthPolicyESIRelData);
			}

		}
		return dynaAuthPolicyESIRelList;


	}

}
