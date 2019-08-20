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

import com.elitecore.elitesm.blmanager.servicepolicy.rm.CGPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.rm.cgpolicy.data.CGPolicyDriverRelationData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.rm.cgpolicy.forms.CreateCGPolicyForm;

public class CreateCGPolicyAction extends BaseWebAction {
	private static final String ACTION_ALIAS =ConfigConstant.CREATE_CG_POLICY;
	private static final String SUCCESS_FORWARD = "success";               
	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="CGPOLICY";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ 
		try {
			checkActionPermission(request, ACTION_ALIAS);
			Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName()); 
			CreateCGPolicyForm createCGPolicyForm = (CreateCGPolicyForm)form;
			String[] driversList = (String[])request.getParameterValues("selecteddriverIds");

			List<CGPolicyDriverRelationData> mainDriverList = getMainDriverList(driversList);

			CGPolicyData cgPolicyData = convertFormToBean(createCGPolicyForm);
			
			String statusCheckBox = createCGPolicyForm.getStatus();
			if(statusCheckBox == null || statusCheckBox.equalsIgnoreCase("")) {
				createCGPolicyForm.setStatus("0");
			} else {
				createCGPolicyForm.setStatus("1");   
			}
			
			if(BaseConstant.HIDE_STATUS.equals(createCGPolicyForm.getStatus())){
				cgPolicyData.setStatus(BaseConstant.HIDE_STATUS_ID);
			}else{
				cgPolicyData.setStatus(BaseConstant.SHOW_STATUS_ID);
			}
			
			cgPolicyData.setDriverList(mainDriverList);

			CGPolicyBLManager blManager = new CGPolicyBLManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));

			blManager.create(cgPolicyData, staffData);

			request.setAttribute("responseUrl", "/initSearchCGPolicy");      
			ActionMessage message = new ActionMessage("rm.cgpolicy.create");
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

		}catch (DuplicateInstanceNameFoundException authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("rm.cgpolicy.duplicate");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);       
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("rm.cgpolicy.create.failure");                                                         		    
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}  
		return mapping.findForward(FAILURE_FORWARD);
	}      

	private CGPolicyData convertFormToBean(CreateCGPolicyForm createCGPolicyForm){
		CGPolicyData cgPolicyData = new CGPolicyData();
		cgPolicyData.setDescription(createCGPolicyForm.getDescription());
		cgPolicyData.setName(createCGPolicyForm.getName());
		cgPolicyData.setOrderNumber(createCGPolicyForm.getOrderNumber());			 
		cgPolicyData.setRuleSet(createCGPolicyForm.getRuleSet());
		cgPolicyData.setStatus("CST01");
		cgPolicyData.setScript(createCGPolicyForm.getScript());
		return cgPolicyData;
	}

	private List<CGPolicyDriverRelationData> getMainDriverList(String mainDrivers[]) throws ArrayIndexOutOfBoundsException,NumberFormatException{
		if(mainDrivers!=null){
			List<CGPolicyDriverRelationData> mainDriverRelList= new ArrayList<CGPolicyDriverRelationData>();
			for (int i = 0; i < mainDrivers.length; i++) {
				String[] driverInstanceIdWgtValues = mainDrivers[i].split("-");
				String driverInstanceId = driverInstanceIdWgtValues[0];				
				CGPolicyDriverRelationData mainDriverRelData = new CGPolicyDriverRelationData();
				mainDriverRelData.setDriverInstanceId(driverInstanceId);
				mainDriverRelData.setWeightage(Integer.parseInt(driverInstanceIdWgtValues[1].trim()));
				mainDriverRelList.add(mainDriverRelData);
			}
			return mainDriverRelList;
		}
		return null;
	}
}
