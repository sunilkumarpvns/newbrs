package com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.netvertexsm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.netvertexsm.util.constants.ActionMessageConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface.form.LDAPSPInterfaceForm;

public class InitCreateLDAPSPInterfaceAction extends BaseWebAction {
	private static final String CREATE_FORWARD = "createLDAPSPInterface";
	private static final String ACTION_ALIAS =  ConfigConstant.CREATE_SP_INTERFACE;
	private static final long QUERY_MAX_EXEC_TIME = 1000l;
	
	private static String [] logicalNames = {SPRFields.USERNAME.displayName,
		SPRFields.PASSWORD.displayName,
		SPRFields.CUSTOMER_TYPE.displayName,
		SPRFields.DATA_PACKAGE.displayName,
		SPRFields.IMS_PACKAGE.displayName,
		SPRFields.EXPIRY_DATE.displayName,
		SPRFields.CUI.displayName,
		SPRFields.IMSI.displayName,
		SPRFields.SUBSCRIBER_IDENTITY.displayName,
		SPRFields.PARENT_ID.displayName};
	
	private static String [] logicalValues = {SPRFields.USERNAME.name(),
		SPRFields.PASSWORD.name(),
		SPRFields.CUSTOMER_TYPE.name(),
		SPRFields.DATA_PACKAGE.name(),
		SPRFields.IMS_PACKAGE.name(),
		SPRFields.EXPIRY_DATE.name(),
		SPRFields.CUI.name(),
		SPRFields.IMSI.name(),
		SPRFields.SUBSCRIBER_IDENTITY.name(),
		SPRFields.PARENT_ID.name()};
	
	private static String [] ldapAttributes = {SPRFields.USERNAME.columnName,
		SPRFields.PASSWORD.columnName,
		SPRFields.CUSTOMER_TYPE.columnName,
		SPRFields.DATA_PACKAGE.columnName,
		SPRFields.IMS_PACKAGE.columnName,
		SPRFields.EXPIRY_DATE.columnName,
		SPRFields.CUI.columnName,
		SPRFields.IMSI.columnName,
		SPRFields.SUBSCRIBER_IDENTITY.columnName,
		SPRFields.PARENT_ID.columnName};
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			try{
				LDAPSPInterfaceForm ldapSPInterfaceForm = (LDAPSPInterfaceForm) form;
				DriverBLManager driverBLManager = new DriverBLManager();
				SPInterfaceBLManager spInterfaceDriverBLManager = new SPInterfaceBLManager();
				LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
				
				List<LDAPDatasourceData> ldapDatasourceList = ldapDatasourceBLManager.getLDAPDSList();
				List<LogicalNameValuePoolData> logicalNamePoolList = driverBLManager.getLogicalNamePoolList();
				
				ldapSPInterfaceForm.setLdapDatasourceList(ldapDatasourceList);
				ldapSPInterfaceForm.setLogicalNamePoolList(logicalNamePoolList);
				ldapSPInterfaceForm.setQueryMaxExecTime(QUERY_MAX_EXEC_TIME); 
				ldapSPInterfaceForm.setExpiryDatePattern(ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT));
				
						
				request.setAttribute("logicalNames", logicalNames);
				request.setAttribute("ldapAttributes",ldapAttributes);
				request.setAttribute("logicalValues",logicalValues);
				
				
				return mapping.findForward(CREATE_FORWARD);
				
			}catch(DataManagerException managerExp){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,managerExp);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute(ActionMessageConstant.ERROR_DETAILS, errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add(ActionMessageConstant.INFORMATION , new ActionMessage("spinterface.create.failure"));
	            saveErrors(request, messages);
	            return mapping.findForward(FAILURE);
	            
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute(ActionMessageConstant.ERROR_DETAILS, errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add(ActionMessageConstant.INFORMATION , new ActionMessage("spinterface.create.failure"));
	            saveErrors(request, messages);
	            return mapping.findForward(FAILURE);
			}
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add(ActionMessageConstant.INFORMATION , message);
	        saveErrors(request, messages);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}