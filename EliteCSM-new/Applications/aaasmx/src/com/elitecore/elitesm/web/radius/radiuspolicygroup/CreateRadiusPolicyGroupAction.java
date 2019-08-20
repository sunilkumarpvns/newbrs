package com.elitecore.elitesm.web.radius.radiuspolicygroup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.radiuspolicygroup.RadiusPolicyGroupBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterpolicygroup.forms.DiameterPolicyGroupForm;
import com.elitecore.elitesm.web.radius.radiuspolicygroup.forms.RadiusPolicyGroupForm;

public class CreateRadiusPolicyGroupAction extends BaseWebAction{
	protected static final String MODULE = "CreateRadiusPolicyGroupAction";
	private static final String CREATE_FORWARD = "initCreateRadiusPolicyGroup";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_RADIUS_POLICY_GROUP;

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		try{
			   if(checkAccess(request, ACTION_ALIAS)){  
				   Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
				   RadiusPolicyGroupForm radiusPolicyGroupForm = (RadiusPolicyGroupForm)form;
				   
				   if(radiusPolicyGroupForm.getAction() == null){
					   request.setAttribute("radiusPolicyGroupForm",radiusPolicyGroupForm);
					   return mapping.findForward(CREATE_FORWARD);	
				   }else{
						IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
						
						RadiusPolicyGroup radiusPolicyGroup = new  RadiusPolicyGroup();
						convertFormToBean(radiusPolicyGroup, radiusPolicyGroupForm);
						
						RadiusPolicyGroupBLManager blManager = new RadiusPolicyGroupBLManager();
						
						blManager.createRadiusPolicyGroup(radiusPolicyGroup, staffData);
						
						request.setAttribute("responseUrl", "/searchRadiusPolicyGroup");      
						ActionMessage message = new ActionMessage("radiuspolicygroup.create.success");
						ActionMessages messages = new ActionMessages();          
						messages.add("information", message);                    
						saveMessages(request,messages);         				   
						Logger.logInfo(MODULE, "Returning success forward from " + getClass().getName()); 
						return mapping.findForward(SUCCESS);   
					}
			} else{
				Logger.logError(MODULE, "Error during Data Manager operation ");
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
	
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch (DuplicateInstanceNameFoundException authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("diameter.ccpolicy.duplicate");                                                         
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);     
		}catch (Exception authExp) {                                                                                           
			Logger.logError(MODULE, "Error during Data Manager operation , reason :" + authExp.getMessage());                              
			Logger.logTrace(MODULE, authExp);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(authExp);
			request.setAttribute("errorDetails", errorElements);            
			ActionMessage message = new ActionMessage("radiuspolicygroup.create.failure");                                                         		    
			ActionMessages messages = new ActionMessages();                                                                                 
			messages.add("information", message);                                                                                           
			saveErrors(request, messages);                                                                                                  
		}
		return mapping.findForward(FAILURE);
	}

	private void convertFormToBean(RadiusPolicyGroup radiusPolicyGroup, RadiusPolicyGroupForm radiusPolicyGroupForm) {
		radiusPolicyGroup.setPolicyName(radiusPolicyGroupForm.getPolicyname());
		radiusPolicyGroup.setExpression(radiusPolicyGroupForm.getExpression());
	}
}
