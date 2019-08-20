package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy;

import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.CommandCodeResponseAttribute;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.TGPPServerPolicyData;


/**
 * @author nayana.rathod
 *
 */

public class UpdateTGPPAAAPolicyBasicDetailAction extends BaseWebAction{

	private static final String UPDATE_FORWARD = "updateTGPPAAAPolicyBasicDetails";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "UpdateTGPPAAAPolicyBasicDetailAction";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_TGPP_AAA_SERVICE_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
	
		if((checkAccess(request, ACTION_ALIAS))){		
			
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			TGPPAAAPolicyForm tgppAAAPolicyForm = (TGPPAAAPolicyForm)form;
			
			try{
				
				TGPPAAAPolicyBLManager tgppAAAPolicyBLManager = new TGPPAAAPolicyBLManager();
				String action = tgppAAAPolicyForm.getAction();
				
				String strtgppAAAPolicyID = request.getParameter("tgppAAAPolicyId");
				String tgppAAAPolicyID;
				
				IStaffData staffData = getStaffObject((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm"));
				
				if (strtgppAAAPolicyID != null) {
					tgppAAAPolicyID = strtgppAAAPolicyID;
				} else {
					tgppAAAPolicyID = tgppAAAPolicyForm.getTgppAAAPolicyId();
				}

				if (action == null || action.equals("")) {
					if(Strings.isNullOrEmpty(tgppAAAPolicyID) == false){
						
						TGPPAAAPolicyData tgppAAAPolicyData = new TGPPAAAPolicyData();
						tgppAAAPolicyData.setTgppAAAPolicyId(tgppAAAPolicyID);
																	
						tgppAAAPolicyData = tgppAAAPolicyBLManager.getTGPPAAAPolicyData(tgppAAAPolicyID);
						
						tgppAAAPolicyForm = convertBeanToForm(tgppAAAPolicyData,tgppAAAPolicyForm,request);
					   							
						request.setAttribute("tgppAAAPolicyData",tgppAAAPolicyData);
						request.setAttribute("tgppAAAPolicyForm",tgppAAAPolicyForm);
					}
				}else if(action!=null && action.equals("update")){
					
					TGPPAAAPolicyData tgppAAAPolicyData = new TGPPAAAPolicyData();
					tgppAAAPolicyData.setTgppAAAPolicyId(tgppAAAPolicyData.getTgppAAAPolicyId());
					
					tgppAAAPolicyData = tgppAAAPolicyBLManager.getTGPPAAAPolicyData(tgppAAAPolicyID);
					tgppAAAPolicyData = convertFormToBean(tgppAAAPolicyForm,tgppAAAPolicyData);
					
					//get Database Xml Data's
					String xmlDatas = new String(tgppAAAPolicyData.getTgppAAAPolicyXml());
					StringReader stringReader =new StringReader(xmlDatas.trim());
					
					JAXBContext context = JAXBContext.newInstance(TGPPServerPolicyData.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					TGPPServerPolicyData tgppServerPolicyData = (TGPPServerPolicyData) unmarshaller.unmarshal(stringReader);
					
					tgppServerPolicyData = doChangesXml(tgppServerPolicyData,tgppAAAPolicyForm);
					
					JAXBContext jaxbContext = JAXBContext.newInstance(TGPPServerPolicyData.class);
				    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				    java.io.StringWriter xmlObj = new StringWriter();
				    jaxbMarshaller.marshal(tgppServerPolicyData,xmlObj);
					
				    String xmlDataString = xmlObj.toString().trim();
				    tgppAAAPolicyData.setTgppAAAPolicyXml(xmlDataString.getBytes());
				    
				    Logger.getLogger().info(MODULE, "******************************* XML Data*******************************");
				    Logger.getLogger().info(MODULE, xmlDataString);
				    Logger.getLogger().info(MODULE, "***********************************************************************");
				    Logger.getLogger().info(MODULE, "XML Length : "+ xmlDataString.length());
				    Logger.getLogger().info(MODULE, "***********************************************************************");
					
				    staffData.setAuditId(tgppAAAPolicyData.getAuditUid());
					staffData.setAuditName(tgppAAAPolicyData.getName());
					
					tgppAAAPolicyBLManager.updateTgppAAAPolicyByID(tgppAAAPolicyData, staffData, ACTION_ALIAS, ConfigConstant.IS_AUDIT_ENABLED);
					doAuditing(staffData, ACTION_ALIAS);
					
					Logger.logDebug(MODULE, "TGPPAAAPolicyData : "+tgppAAAPolicyData);
	                request.setAttribute("responseUrl","/viewTGPPAAAPolicy.do?tgppAAAPolicyId="+tgppAAAPolicyData.getTgppAAAPolicyId());
	                ActionMessage message = new ActionMessage("tgppaaaservicepolicy.update.success");
	                ActionMessages messages = new ActionMessages();
	                messages.add("information", message);
	                saveMessages(request, messages);
	                return mapping.findForward(SUCCESS);
				}
				return mapping.findForward(UPDATE_FORWARD);
			}catch(DuplicateInstanceNameFoundException e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("tgppaaaservicepolicy.update.failure",tgppAAAPolicyForm.getName());
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveErrors(request, messages);
			    
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("tgppaaaservicepolicy.update.failure");
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveErrors(request, messages);
			}
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

	private TGPPServerPolicyData doChangesXml( TGPPServerPolicyData tgppServerPolicyData, TGPPAAAPolicyForm tgppAAAPolicyForm) {
		tgppServerPolicyData.setName(tgppAAAPolicyForm.getName());
		tgppServerPolicyData.setDescription(tgppAAAPolicyForm.getDescription());
		tgppServerPolicyData.setUserIdentity(tgppAAAPolicyForm.getUserIdentity());
		tgppServerPolicyData.setRuleSet(tgppAAAPolicyForm.getRuleset());
		tgppServerPolicyData.setSessionManagementEnabled(tgppAAAPolicyForm.getsessionManagement());
		
		if(BaseConstant.SHOW_STATUS.equals(tgppAAAPolicyForm.getStatus())){
			tgppServerPolicyData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			tgppServerPolicyData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}
		
		tgppServerPolicyData.getCommandCodeResponseAttributesList().clear();
		JSONArray resAttrJsonArray = JSONArray.fromObject(tgppAAAPolicyForm.getCommandCodeWiseRespAttrib());
		int resAttrJsonArraySize = resAttrJsonArray.size();
		for (int i = 0; i < resAttrJsonArraySize ; i++) {
			JSONObject resAttrObj = resAttrJsonArray.getJSONObject(i); 
			CommandCodeResponseAttribute commandCodeResponseAttribute = new CommandCodeResponseAttribute();
			commandCodeResponseAttribute.setCommandCodes(resAttrObj.getString("commandCode"));
			commandCodeResponseAttribute.setResponseAttributes(resAttrObj.getString("responseAttr"));
			tgppServerPolicyData.getCommandCodeResponseAttributesList().add(commandCodeResponseAttribute);
		}
		tgppServerPolicyData.setDefaultResponseBehaviorParameter(tgppAAAPolicyForm.getDefaultResponseBehaviourArgument());
		tgppServerPolicyData.setDefaultResponseBehaviorType(DefaultResponseBehaviorType.valueOf(tgppAAAPolicyForm.getDefaultResponseBehaviour()));
		return tgppServerPolicyData;
	}

	private TGPPAAAPolicyData convertFormToBean( TGPPAAAPolicyForm tgppAAAPolicyForm, TGPPAAAPolicyData tgppAAAPolicyData) {
		tgppAAAPolicyData.setName(tgppAAAPolicyForm.getName());
		tgppAAAPolicyData.setDescription(tgppAAAPolicyForm.getDescription());
		
		if(BaseConstant.SHOW_STATUS.equals(tgppAAAPolicyForm.getStatus())){
			tgppAAAPolicyData.setStatus(BaseConstant.SHOW_STATUS_ID);
		}else{
			tgppAAAPolicyData.setStatus(BaseConstant.HIDE_STATUS_ID);
		}	
		tgppAAAPolicyData.setRuleset(tgppAAAPolicyForm.getRuleset());
		tgppAAAPolicyData.setUserIdentity(tgppAAAPolicyForm.getUserIdentity());
		
		tgppAAAPolicyData.setSessionManagement(tgppAAAPolicyForm.getsessionManagement());
		
		tgppAAAPolicyData.setCui(tgppAAAPolicyForm.getCui());
		tgppAAAPolicyData.setDefaultResponseBehaviorArgument(tgppAAAPolicyForm.getDefaultResponseBehaviourArgument());
		tgppAAAPolicyData.setDefaultResponseBehaviour(tgppAAAPolicyForm.getDefaultResponseBehaviour());
		return tgppAAAPolicyData;
	}

	private TGPPAAAPolicyForm convertBeanToForm( TGPPAAAPolicyData tgppAAAPolicyData, TGPPAAAPolicyForm tgppAAAPolicyForm,HttpServletRequest request) {
		
		tgppAAAPolicyForm.setTgppAAAPolicyId(tgppAAAPolicyData.getTgppAAAPolicyId());
		tgppAAAPolicyForm.setName(tgppAAAPolicyData.getName());
		tgppAAAPolicyForm.setDescription(tgppAAAPolicyData.getDescription());
		
		if(BaseConstant.HIDE_STATUS_ID.equals(tgppAAAPolicyData.getStatus())){
			tgppAAAPolicyForm.setStatus(BaseConstant.HIDE_STATUS);
		}else{
			tgppAAAPolicyForm.setStatus(BaseConstant.SHOW_STATUS);
		}
		tgppAAAPolicyForm.setRuleset(tgppAAAPolicyData.getRuleset());
		tgppAAAPolicyForm.setUserIdentity(tgppAAAPolicyData.getUserIdentity());
		
		tgppAAAPolicyForm.setsessionManagement(tgppAAAPolicyData.getSessionManagement());
		tgppAAAPolicyForm.setCui(tgppAAAPolicyData.getCui());
		
		tgppAAAPolicyForm.setDefaultResponseBehaviour(tgppAAAPolicyData.getDefaultResponseBehaviour());
		tgppAAAPolicyForm.setDefaultResponseBehaviourArgument(tgppAAAPolicyData.getDefaultResponseBehaviorArgument());
		
		JSONArray resAttrJsonArray = new JSONArray();
		TGPPServerPolicyData tgppServerPolicyData;
		try{
			tgppServerPolicyData = ConfigUtil.deserialize(new String(tgppAAAPolicyData.getTgppAAAPolicyXml()), TGPPServerPolicyData.class);
			
			for(CommandCodeResponseAttribute tgppResponseAttributes : tgppServerPolicyData.getCommandCodeResponseAttributesList()){
				JSONObject resAttrObj = new JSONObject();
				resAttrObj.put("commandCode", tgppResponseAttributes.getCommandCodes());
				resAttrObj.put("responseAttr", tgppResponseAttributes.getResponseAttributes());
				resAttrJsonArray.add(resAttrObj);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			Logger.logTrace(MODULE,e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("tgppaaaservicepolicy.update.failure");
		    ActionMessages messages = new ActionMessages();
		    messages.add("information", message);
		    saveErrors(request, messages);
		}
		
		tgppAAAPolicyForm.setCommandCodeWiseRespAttrib(resAttrJsonArray.toString());
		
		return tgppAAAPolicyForm;
	}
}
