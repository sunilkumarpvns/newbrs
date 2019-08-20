package com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elitecore.corenetvertex.spr.data.SPRFields;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData;
import com.elitecore.netvertexsm.util.constants.ActionMessageConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface.form.LDAPSPInterfaceForm;

public class InitEditLDAPSPInterfaceAction extends BaseWebAction{
	private static final String FAILURE_FORWARD = "failure";
	private static final String EDIT_LDAPDRIVER_FORWARD = "editLDAPSPInterface";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				LDAPSPInterfaceForm ldapSPInterfaceForm = (LDAPSPInterfaceForm) form;
				DriverInstanceData driverInstanceData =(DriverInstanceData)request.getSession().getAttribute("driverInstanceData");
				Set<LDAPSPInterfaceData> ldapDriverDataSet =  driverInstanceData.getLdapspInterfaceDriverSet();
				LDAPSPInterfaceData ldapDriverData = null;
				if(ldapDriverDataSet!=null && !ldapDriverDataSet.isEmpty()){
					for (Iterator<LDAPSPInterfaceData> iterator = ldapDriverDataSet.iterator(); iterator.hasNext();) {
						 ldapDriverData =  iterator.next();
					}
				}
				LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
				DriverBLManager driverBLManager = new DriverBLManager();
				List<LDAPDatasourceData> ldapDatasourceList = ldapDatasourceBLManager.getLDAPDSList();
				List<LogicalNameValuePoolData> logicalNamePoolList = driverBLManager.getLogicalNamePoolList();
				ldapSPInterfaceForm.setName(driverInstanceData.getName());
				ldapSPInterfaceForm.setDescription(driverInstanceData.getDescription());
				ldapSPInterfaceForm.setLdapDatasourceList(ldapDatasourceList);
				ldapSPInterfaceForm.setLogicalNamePoolList(logicalNamePoolList);
				               			
				if(ldapDriverData!=null){
					Set<LDAPFieldMapData> ldapFieldSet = ldapDriverData.getFieldMapSet();
					convertBeanToForm(ldapDriverData,ldapSPInterfaceForm);
					
					if(ldapFieldSet!=null && !ldapFieldSet.isEmpty()){
						String ldapAttributeArray[] = new String[ldapFieldSet.size()];
						String logicalNameArray[] = new String[ldapFieldSet.size()];
						String logicalValueArray[] = new String[ldapFieldSet.size()];
						int i=0;
						for (Iterator<LDAPFieldMapData> iterator = ldapFieldSet.iterator(); iterator.hasNext();) {
							LDAPFieldMapData ldapFieldMapData =  iterator.next();
							ldapAttributeArray[i] = ldapFieldMapData.getLdapAttribute();
							logicalValueArray[i]= ldapFieldMapData.getLogicalName();
							logicalNameArray[i]= SPRFields.fromSPRFields(ldapFieldMapData.getLogicalName()).displayName;
							i++;
						}
						request.setAttribute("ldapAttributes", ldapAttributeArray);
						request.setAttribute("logicalNames", logicalNameArray);
						request.setAttribute("logicalValues", logicalValueArray);
					}
					
				}
				request.setAttribute("driverInstanceData",driverInstanceData);
				return mapping.findForward(EDIT_LDAPDRIVER_FORWARD);
				
			}catch(Exception managerExp){
				managerExp.printStackTrace();
				Logger.logTrace(MODULE,"Error during data Manager operation, reason :"+managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute(ActionMessageConstant.ERROR_DETAILS, errorElements);
				ActionMessages messages = new ActionMessages();
	            messages.add(ActionMessageConstant.INFORMATION , new ActionMessage("driver.update.failure"));
	            saveErrors(request, messages);
			}
			return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add(ActionMessageConstant.INFORMATION , message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	private void convertBeanToForm(LDAPSPInterfaceData data,LDAPSPInterfaceForm  form){
		
		if(data!=null){
			form.setLdapDSId(data.getLdapDsId());
			form.setLdapSPInterfaceId(data.getLdapSPInterfaceId());
			form.setExpiryDatePattern(data.getExpiryDatePattern());
			form.setPasswordDecryptType(data.getPasswordDecryptType());
			form.setQueryMaxExecTime(data.getQueryMaxExecTime());
		}
		
		
	}
}

