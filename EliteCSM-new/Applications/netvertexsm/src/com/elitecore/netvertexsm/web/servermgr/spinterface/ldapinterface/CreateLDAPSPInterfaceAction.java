package com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData;
import com.elitecore.netvertexsm.util.constants.ActionMessageConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.spinterface.form.CreateSPInterfaceForm;
import com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface.form.LDAPSPInterfaceForm;

public class CreateLDAPSPInterfaceAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			String expiryDatePattern = null;
			try{
				LDAPSPInterfaceForm ldapSPInterfaceForm = (LDAPSPInterfaceForm) form;
				CreateSPInterfaceForm driverInstanceForm =	(CreateSPInterfaceForm)request.getSession().getAttribute("createSPInterfaceForm");
				
				SPInterfaceBLManager spInterfaceDriverBLManager = new SPInterfaceBLManager();
				LDAPSPInterfaceData ldapDriverData = new LDAPSPInterfaceData();
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				expiryDatePattern = ldapSPInterfaceForm.getExpiryDatePattern();
				SimpleDateFormat sdf = new SimpleDateFormat(expiryDatePattern);
				ldapDriverData.setExpiryDatePattern(sdf.toPattern());
				ldapDriverData.setPasswordDecryptType(ldapSPInterfaceForm.getPasswordDecryptType());
				ldapDriverData.setQueryMaxExecTime(ldapSPInterfaceForm.getQueryMaxExecTime());
				ldapDriverData.setLdapDsId(ldapSPInterfaceForm.getLdapDSId());
				
				String[] logicalNames = request.getParameterValues("logicalNames");
				String[] ldapAttribute = request.getParameterValues("ldapAttributes");
				
				ArrayList<LDAPFieldMapData> ldapFieldList = new ArrayList<LDAPFieldMapData>();
				if(logicalNames != null) {
					for(int i=0; i<logicalNames.length; i++) {
						LDAPFieldMapData ldapFieldMapData = new LDAPFieldMapData();
						ldapFieldMapData.setLdapAttribute(ldapAttribute[i]);
						ldapFieldMapData.setLogicalName(logicalNames[i]);
						
						ldapFieldList.add(ldapFieldMapData);						
					}
				}
				
				Date currentDate = new Date();
				driverInstanceData.setCreateDate(new Timestamp(currentDate.getTime()));
				IStaffData staff =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				driverInstanceData.setCreatedByStaffId( staff.getStaffId());
				
				driverInstanceData.setDescription(driverInstanceForm.getDescription());
				driverInstanceData.setDriverTypeId(driverInstanceForm.getDriverTypeId());
				driverInstanceData.setName(driverInstanceForm.getName());
				driverInstanceData.setStatus(driverInstanceForm.getStatus());
				driverInstanceData.setLdapDriverData(ldapDriverData);
				driverInstanceData.setLdapFieldMapList(ldapFieldList);
				               	
				spInterfaceDriverBLManager.create(driverInstanceData,staff,ACTION_ALIAS);
				
				ActionMessage message = new ActionMessage("spinterface.create.success", driverInstanceData.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add(ActionMessageConstant.INFORMATION , message);
	            saveMessages(request,messages);				
				request.setAttribute(ActionMessageConstant.RESPONSE_URL,"/initSearchSPInterface.do");
				return mapping.findForward(SUCCESS);
			}catch(IllegalArgumentException argEx){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,argEx);
				String errorElements[] = {"Invalid Expiry Date pattern configured [" + expiryDatePattern + "] "};
				request.setAttribute(ActionMessageConstant.ERROR_DETAILS, errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add(ActionMessageConstant.INFORMATION , new ActionMessage("spinterface.create.failure"));
				messages.add(ActionMessageConstant.INFORMATION , new ActionMessage("spinterface.invalid.dateformat"));
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);

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
