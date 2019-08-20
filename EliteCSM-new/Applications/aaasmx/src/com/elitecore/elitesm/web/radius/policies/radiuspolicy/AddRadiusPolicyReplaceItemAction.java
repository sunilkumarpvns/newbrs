package com.elitecore.elitesm.web.radius.policies.radiuspolicy;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.radius.dictionary.RadiusDictionaryBLManager;
import com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.RadiusPolicyBLManager;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyForm;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.AddRadiusPolicyReplaceItemForm;


public class AddRadiusPolicyReplaceItemAction extends BaseDictionaryAction {
	protected static final String MODULE = "RADIUSPOLICY ACTION";
	private static final String FAILURE_FORWARD = "failure";
	private static final String NEW_RADIUSPOLICY_FORWARD = "newRadiusPolicy";    
	private static final String CREATE_REPLACEITEM_FORWARD = "createRadiusPolicyReplaceItem";    
	private static final String ACTION_ALIAS = "CREATE_RADIUS_POLICY_ACTION";

	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(checkAccess(request, ACTION_ALIAS)){
			Logger.logTrace(MODULE, "Entered execute method of " + getClass().getName());
			AddRadiusPolicyReplaceItemForm radiusPolicyReplaceItemForm = (AddRadiusPolicyReplaceItemForm)form;
			AddRadiusPolicyForm radiusPolicyForm = (AddRadiusPolicyForm)request.getSession().getAttribute("addRadiusPolicyForm");
			ActionErrors errors = new ActionErrors();
			//try{
			String action = radiusPolicyReplaceItemForm.getReplaceAction();
			RadiusDictionaryBLManager dictionaryBLManager = new RadiusDictionaryBLManager();
			RadiusPolicyBLManager radPolBLManager = new RadiusPolicyBLManager();


			List lstDictionaryDetailList = null;// dictionaryBLManager.getDictionaryParametersById(colDictionaryId);

			HashMap mapDictionaryParameters = new HashMap();


			for(int i=0;i<lstDictionaryDetailList.size();i++){
				IDictionaryParameterDetailData dicParamDetailData = (IDictionaryParameterDetailData)lstDictionaryDetailList.get(i);
				mapDictionaryParameters.put(new Long(dicParamDetailData.getDictionaryParameterDetailId()),
						dicParamDetailData);
			}
			request.setAttribute("dictionaryParameters",lstDictionaryDetailList);
			request.setAttribute("preDefinedValueMap",new HashMap());
			if(action != null){
				if(action.equalsIgnoreCase("previous")){
					String strPrevPath = NEW_RADIUSPOLICY_FORWARD;

				}else if(action.equalsIgnoreCase("Reload")){
					System.out.println(radiusPolicyReplaceItemForm.getParameterId()+"&"+radiusPolicyReplaceItemForm.getParameterName());
					IDictionaryParameterDetailData dicParamDetailData = (IDictionaryParameterDetailData)mapDictionaryParameters.get(new Long(radiusPolicyReplaceItemForm.getParameterId()));
					radiusPolicyReplaceItemForm.setParameterValue("");
					request.setAttribute("preDefinedValueMap",dicParamDetailData.getPreDefinedValueMap());
					return mapping.findForward(CREATE_REPLACEITEM_FORWARD);
				}


			}else{
				return mapping.findForward(CREATE_REPLACEITEM_FORWARD);        		
			}

			//TODO : Baiju - set error message and then forward to error page.
			Logger.logTrace(MODULE, "Returning error forward from " + getClass().getName());
			errors.add("fatal", new ActionError("general.error")); 
			saveErrors(request,errors);
			return mapping.findForward(FAILURE_FORWARD);
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
