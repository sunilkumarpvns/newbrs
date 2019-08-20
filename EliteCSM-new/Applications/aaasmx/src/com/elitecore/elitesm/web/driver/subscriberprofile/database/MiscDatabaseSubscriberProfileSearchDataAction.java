package com.elitecore.elitesm.web.driver.subscriberprofile.database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.MiscDatabaseSusbscriberProfileDataSearchForm;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.UpdateDataForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;

public class MiscDatabaseSubscriberProfileSearchDataAction extends BaseDictionaryAction{
	
	private static final String SUCCESS_FORWARD = "success";
	private static final String LIST_FORWARD = "miscDatabaseDataSourceDataSearch";
	private static final String FAILURE_FORWARD = "failure";
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		try{
//			MiscDatabaseSusbscriberProfileDataSearchForm miscDatabaseDatasourceDataSearchForm = (MiscDatabaseSusbscriberProfileDataSearchForm)actionForm;
//			IAccessPolicyData iAccessPolicyData = new AccessPolicyData();
//			IDatabaseSubscriberProfileData databaseSubscribeProfileData = new DatabaseSubscriberProfileData();
//		
//
//			DatabaseSubscriberProfileBLManager blManager = new DatabaseSubscriberProfileBLManager();
//			String datasourceId  = miscDatabaseDatasourceDataSearchForm.getDataSourceId();	
//						
//			UpdateDataForm updateDataForm = new UpdateDataForm();
//			updateDataForm.setFieldName(miscDatabaseDatasourceDataSearchForm.getFieldName());
//
//			
//			request.setAttribute("miscDatabaseDataSourceDataSearchForm",miscDatabaseDatasourceDataSearchForm);
			
			return mapping.findForward(LIST_FORWARD);

		}catch(Exception managerExp){
			Logger.logTrace(MODULE,"Error during Data Manager operation, reason :"+managerExp.getMessage());
		}
		return mapping.findForward(FAILURE_FORWARD);
	}

}
