package com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.rm.CGPolicyBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyDriverRelationData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.UpdateCGPolicyForm;

public class UpdateCGPolicyAction extends BaseWebAction { 
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_CG_POLICY;
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CGPOLICY";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			//checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			UpdateCGPolicyForm updateCGPolicyForm = (UpdateCGPolicyForm)form;
			List<CGPolicyDriverRelationData>  mainDriverList = getMainDriverList(updateCGPolicyForm.getSelecteddriverIds(),updateCGPolicyForm.getPolicyId());				
			CGPolicyData cgPolicyData = convertFormToBean(updateCGPolicyForm);
			cgPolicyData.setDriverList(mainDriverList);

			CGPolicyBLManager blManager = new CGPolicyBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			staffData.setAuditId(cgPolicyData.getAuditUId());
			staffData.setAuditName(cgPolicyData.getName());
			
			blManager.updateById(cgPolicyData,staffData);
			request.setAttribute("cgPolicyData",cgPolicyData);
			request.setAttribute("updateCGPolicyForm",updateCGPolicyForm);
			request.setAttribute("responseUrl", "/initViewCGPolicy?policyId="+cgPolicyData.getPolicyId()); 
			ActionMessage message = new ActionMessage("rm.cgpolicy.update");
			ActionMessages messages = new ActionMessages();          
			messages.add("information", message);                    
			saveMessages(request,messages);         				   
			Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
			return mapping.findForward(SUCCESS_FORWARD);

		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE, "Restricted to do action.");
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);	
		}catch (DuplicateInstanceNameFoundException dpfExp) {
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE,dpfExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(dpfExp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("diameter.eappolicy.duplicate");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);                                                                                               
			ActionMessage message = new ActionMessage("diameter.eappolicy.updateerror");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}                                                                                                                                   
		return mapping.findForward(FAILURE_FORWARD); 
	}

	private CGPolicyData convertFormToBean(UpdateCGPolicyForm updateCGPolicyForm) {
		CGPolicyData cgPolicyData = new CGPolicyData();
		cgPolicyData.setPolicyId(updateCGPolicyForm.getPolicyId());
		cgPolicyData.setDescription(updateCGPolicyForm.getDescription());
		cgPolicyData.setName(updateCGPolicyForm.getName());
		cgPolicyData.setStatus(updateCGPolicyForm.getStatus());
		if(BaseConstant.SHOW_STATUS.equals(updateCGPolicyForm.getStatus())){
			cgPolicyData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			cgPolicyData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
		cgPolicyData.setRuleSet(updateCGPolicyForm.getRuleSet());
		cgPolicyData.setScript(updateCGPolicyForm.getScript());
		cgPolicyData.setAuditUId(updateCGPolicyForm.getAuditUId());
		return cgPolicyData;
	}

	private List<CGPolicyDriverRelationData> getMainDriverList(String mainDrivers[],String policyId) throws ArrayIndexOutOfBoundsException,NumberFormatException, DataManagerException{
		if(mainDrivers!=null){
			List<CGPolicyDriverRelationData> mainDriverRelList= new ArrayList<CGPolicyDriverRelationData>();
			for(int i = 0; i < mainDrivers.length; i++) {
				DriverBLManager driverBLManager=new DriverBLManager();
				
				String[] driverInstanceIdWgtValues = mainDrivers[i].split("-");
				String driverInstanceId = driverInstanceIdWgtValues[0];	
				
				DriverInstanceData driverInstanceData=driverBLManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
				
				CGPolicyDriverRelationData mainDriverRelData = new CGPolicyDriverRelationData();
				mainDriverRelData.setDriverInstanceId(driverInstanceId);
				mainDriverRelData.setPolicyId(policyId);
				if(driverInstanceIdWgtValues.length > 1)
					mainDriverRelData.setWeightage(Integer.parseInt(driverInstanceIdWgtValues[1].trim()));
				else
					mainDriverRelData.setWeightage(new Integer(1));
				mainDriverRelData.setDriverData(driverInstanceData);
				mainDriverRelList.add(mainDriverRelData);
			}
			return mainDriverRelList;
		}
		return null;
	}
}
