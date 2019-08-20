package com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
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
import com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface.form.LDAPSPInterfaceForm;

public class EditLDAPSPInterfaceAction extends BaseWebAction {
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			String expiryDatePattern = null;
			try{
				
				LDAPSPInterfaceForm ldapSPInterfaceForm = (LDAPSPInterfaceForm) form;
				DriverInstanceData sessionData =(DriverInstanceData)request.getSession().getAttribute("driverInstanceData");
				SPInterfaceBLManager driverBLManager = new SPInterfaceBLManager();
				
				expiryDatePattern = ldapSPInterfaceForm.getExpiryDatePattern();
				SimpleDateFormat sdf = new SimpleDateFormat(expiryDatePattern);
				LDAPSPInterfaceData ldapSPInterfaceDriverData = convertFormToBean(ldapSPInterfaceForm);
				Set<LDAPFieldMapData> ldapFieldSet = new LinkedHashSet<LDAPFieldMapData>();
				
				String[] logicalNames = request.getParameterValues("logicalNames");
				String[] ldapAttribute = request.getParameterValues("ldapAttributes");
				
				if(logicalNames != null) {
					for(int i=0; i<logicalNames.length; i++) {
						LDAPFieldMapData ldapFieldMapData = new LDAPFieldMapData();
						ldapFieldMapData.setLdapAttribute(ldapAttribute[i]);
						ldapFieldMapData.setLogicalName(logicalNames[i]);
						ldapFieldSet.add(ldapFieldMapData);						
					}
				}
				
				ldapSPInterfaceDriverData.setFieldMapSet(ldapFieldSet);
				
				Date currentDate = new Date();
				DriverInstanceData driverInstanceData = new DriverInstanceData();
				driverInstanceData.setDriverInstanceId(ldapSPInterfaceForm.getDriverInstanceId());
				driverInstanceData.setName(ldapSPInterfaceForm.getName());
				driverInstanceData.setDescription(ldapSPInterfaceForm.getDescription());
				driverInstanceData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
				IStaffData staff =getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				driverInstanceData.setLastModifiedByStaffId(staff.getStaffId());
				
				Set<LDAPSPInterfaceData> ldapDriverDataSet = new LinkedHashSet<LDAPSPInterfaceData>();
				ldapDriverDataSet.add(ldapSPInterfaceDriverData);
				driverInstanceData.setLdapspInterfaceDriverSet(ldapDriverDataSet);
				driverBLManager.updateLDAPDriver(driverInstanceData,staff,ACTION_ALIAS);
				
				ActionMessage message = new ActionMessage("spinterface.update.success", driverInstanceData.getName());
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
				messages.add(ActionMessageConstant.INFORMATION , new ActionMessage("spinterface.update.failure"));
				messages.add(ActionMessageConstant.INFORMATION , new ActionMessage("spinterface.invalid.dateformat"));
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);

			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute(ActionMessageConstant.ERROR_DETAILS, errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add(ActionMessageConstant.INFORMATION , new ActionMessage("spinterface.update.failure"));
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
	private LDAPSPInterfaceData convertFormToBean(LDAPSPInterfaceForm form){
		LDAPSPInterfaceData data = new LDAPSPInterfaceData();
		data.setDriverInstanceId(form.getDriverInstanceId());
		data.setExpiryDatePattern(form.getExpiryDatePattern());
		data.setLdapSPInterfaceId(form.getLdapSPInterfaceId());
		data.setLdapDsId(form.getLdapDSId());
		data.setPasswordDecryptType(form.getPasswordDecryptType());
		data.setQueryMaxExecTime(form.getQueryMaxExecTime());
		return data;
	}
}
