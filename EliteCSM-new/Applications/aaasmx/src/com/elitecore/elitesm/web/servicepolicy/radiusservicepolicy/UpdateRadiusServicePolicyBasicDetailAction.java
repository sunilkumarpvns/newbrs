package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.io.StringReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPathExpressionException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AcctResponseBehaviors;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.AuthResponseBehaviors;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.ChargeableUserIdentityConfiguration;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadiusServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.SupportedMessages;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.UpdateRadiusServicePolicyForm;

public class UpdateRadiusServicePolicyBasicDetailAction extends BaseWebAction{
	private static final String UPDATE_FORWARD = "updateRadiusServicePolicyBasicDetails";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="UpdateAuthServicePolicyBasicDetailAction";
	private static final String ACTION_ALIAS=ConfigConstant.UPDATE_RADIUS_SERVICE_POLICY_BASIC_DETAILS;
	private static boolean isConcurrencyBounded = false;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){		
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm = (UpdateRadiusServicePolicyForm)form;
			try{
				ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
				SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
				String action = updateRadiusServicePolicyForm.getAction();
				
				String strRadiusPolicyID = request.getParameter("radiusPolicyId");
				String radiusPolicyID;
				
				IStaffData staffData = getStaffObject((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm"));
				
				if(strRadiusPolicyID != null){
					radiusPolicyID = strRadiusPolicyID;
				}else{
					radiusPolicyID=updateRadiusServicePolicyForm.getRadiusPolicyId();
				}
				if(action==null || action.equals("")){
					if( Strings.isNullOrBlank(radiusPolicyID) == false){
						RadServicePolicyData radServicePolicyData = new RadServicePolicyData();
						
						List<ISessionManagerInstanceData> sessionManagerInstanceDataList = sessionManagerBLManager.getSessionManagerInstanceList();
					      
						updateRadiusServicePolicyForm.setSessionManagerInstanceDataList(sessionManagerInstanceDataList);
						
						radServicePolicyData = servicePolicyBLManager.getRadiusServicePolicyInstData(radiusPolicyID,staffData,ACTION_ALIAS);
						
						/*Check for is Concurrency handler is bind or not if bounded then it will not allowed to user to delete in basic details*/
						if(radServicePolicyData != null){

	 						String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
	 						
	 						if( xmlDatas != null && !(xmlDatas.isEmpty()) ){
	 	 						
	 	 						boolean isConcurrencyBind = searchElementFromXml(xmlDatas.trim());
	 	 						
	 	 						if(isConcurrencyBind){
	 	 							updateRadiusServicePolicyForm.setConcurrencyHandlerBind(true);
	 	 						}else{
	 	 							updateRadiusServicePolicyForm.setConcurrencyHandlerBind(false);
	 	 						}
	 						}
	        			}
						
						if( isConcurrencyBounded ){
							isConcurrencyBounded=false;
						}
						
						//get Database Xml Data's
						String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
						StringReader stringReader =new StringReader(xmlDatas.trim());
						
						JAXBContext context = JAXBContext.newInstance(RadiusServicePolicyData.class);
						Unmarshaller unmarshaller = context.createUnmarshaller();
						RadiusServicePolicyData radiusServicePolicyData = (RadiusServicePolicyData) unmarshaller.unmarshal(stringReader);
					
						/* Setting Advanced CUI Expression */
						radServicePolicyData.setAdvancedCuiExpression(radiusServicePolicyData.getCuiConfiguration().getExpression());
						
						request.setAttribute("radServicePolicyData",radServicePolicyData);
						updateRadiusServicePolicyForm = convertBeanToForm(radServicePolicyData,updateRadiusServicePolicyForm);
						request.setAttribute("updateRadiusServicePolicyForm",updateRadiusServicePolicyForm);
					}
				}else if(action!=null && action.equals("update")){
					ServicePolicyBLManager servicePoilcyBLManager = new ServicePolicyBLManager();
					
					RadServicePolicyData radServicePolicyData = new RadServicePolicyData();

					radServicePolicyData = servicePolicyBLManager.getRadiusServicePolicyInstData(radiusPolicyID,staffData,ACTION_ALIAS);

					//get Database Xml Data's
					String xmlDatas = new String(radServicePolicyData.getRadiusPolicyXml());
					StringReader stringReader =new StringReader(xmlDatas.trim());
					
					JAXBContext context = JAXBContext.newInstance(RadiusServicePolicyData.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					RadiusServicePolicyData radiusServicePolicyData = (RadiusServicePolicyData) unmarshaller.unmarshal(stringReader);
				
					radiusServicePolicyData = doChangesXml(radiusServicePolicyData,updateRadiusServicePolicyForm);

					servicePoilcyBLManager.updateRadiusServicePolicyById(radiusServicePolicyData, staffData, ACTION_ALIAS);
					
					Logger.logDebug(MODULE, "RadServicePolicyData : "+radServicePolicyData);
	                request.setAttribute("responseUrl","/viewRadiusServicePolicy.do?radiusPolicyId="+radServicePolicyData.getRadiusPolicyId());
	                ActionMessage message = new ActionMessage("radiusservicepolicy.update.success");
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
				ActionMessage message = new ActionMessage("radiusservicepolicy.update.failure",updateRadiusServicePolicyForm.getName());
			    ActionMessages messages = new ActionMessages();
			    messages.add("information", message);
			    saveErrors(request, messages);
			    
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("radiusservicepolicy.update.failure");
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
	private RadiusServicePolicyData doChangesXml(RadiusServicePolicyData radiusServicePolicyData,UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm) {
		radiusServicePolicyData.setPolicyId(updateRadiusServicePolicyForm.getRadiusPolicyId());
		radiusServicePolicyData.setName(updateRadiusServicePolicyForm.getName());
		radiusServicePolicyData.setDescription(updateRadiusServicePolicyForm.getDescription());
		if(BaseConstant.ACTIVE_STATUS.equalsIgnoreCase(updateRadiusServicePolicyForm.getStatus())){
			radiusServicePolicyData.setStatus(updateRadiusServicePolicyForm.getStatus());
		} else {
			radiusServicePolicyData.setStatus(BaseConstant.INACTIVE_STATUS);
		}
		
		SupportedMessages supportedMessages = new SupportedMessages();
		supportedMessages.setAccountingMessageEnabled(String.valueOf(updateRadiusServicePolicyForm.isAccounting()));
		supportedMessages.setAuthenticationMessageEnabled(String.valueOf(updateRadiusServicePolicyForm.isAuthentication()));
		
		radiusServicePolicyData.setSupportedMessages(supportedMessages);
		radiusServicePolicyData.setAccountingRuleset(updateRadiusServicePolicyForm.getRuleSetAcct());
		radiusServicePolicyData.setAuthenticationRuleset(updateRadiusServicePolicyForm.getRuleSetAuth());
		
		radiusServicePolicyData.setValidatePacket(String.valueOf(updateRadiusServicePolicyForm.isValidatePacket()));
		radiusServicePolicyData.setAuthResponseAttributes(updateRadiusServicePolicyForm.getAuthResponseAttributes());
		radiusServicePolicyData.setAcctResponseAttributes(updateRadiusServicePolicyForm.getAcctResponseAttributes());
		
		if(Strings.isNullOrBlank(updateRadiusServicePolicyForm.getSessionManagerId()) == false && 
				"0".equalsIgnoreCase(updateRadiusServicePolicyForm.getSessionManagerId()) == false){
			radiusServicePolicyData.setSessionManagerId(updateRadiusServicePolicyForm.getSessionManagerId());
		}else{
			radiusServicePolicyData.setSessionManagerId(null);
		}
		
		radiusServicePolicyData.setHotlinePolicy(updateRadiusServicePolicyForm.getHotlinePolicy());
		radiusServicePolicyData.setUserIdentity(updateRadiusServicePolicyForm.getUserIdentity());
		
		if(AuthResponseBehaviors.REJECT.name().equals(updateRadiusServicePolicyForm.getAuthResponseBehavior())){
			radiusServicePolicyData.setDefaultAuthResponseBehavior(AuthResponseBehaviors.REJECT.name());
		}else if(AuthResponseBehaviors.DROP.name().equals(updateRadiusServicePolicyForm.getAuthResponseBehavior())){
			radiusServicePolicyData.setDefaultAuthResponseBehavior(AuthResponseBehaviors.DROP.name());
		}else if(AuthResponseBehaviors.HOTLINE.name().equals(updateRadiusServicePolicyForm.getAuthResponseBehavior())){
			radiusServicePolicyData.setDefaultAuthResponseBehavior(AuthResponseBehaviors.HOTLINE.name());
		}
		
		if(AcctResponseBehaviors.DROP.name().equals(updateRadiusServicePolicyForm.getAcctResponseBehavior())){
			radiusServicePolicyData.setDefaultAcctResponseBehavior(AcctResponseBehaviors.DROP.name());
		}else if(AcctResponseBehaviors.RESPONSE.name().equals(updateRadiusServicePolicyForm.getAcctResponseBehavior())){
			radiusServicePolicyData.setDefaultAcctResponseBehavior(AcctResponseBehaviors.RESPONSE.name());
		}
		
		radiusServicePolicyData.setAccountingRuleset(updateRadiusServicePolicyForm.getRuleSetAcct());
		radiusServicePolicyData.setAuthenticationRuleset(updateRadiusServicePolicyForm.getRuleSetAuth());
		
		radiusServicePolicyData.setSessionManagerId(updateRadiusServicePolicyForm.getSessionManagerId());
		ChargeableUserIdentityConfiguration cuiAttributes = new ChargeableUserIdentityConfiguration();
		cuiAttributes.setAccountingCuiAttribute(updateRadiusServicePolicyForm.getAcctAttributes());
		cuiAttributes.setAuthenticationCuiAttribute(updateRadiusServicePolicyForm.getAuthAttributes());
		cuiAttributes.setCui(updateRadiusServicePolicyForm.getCui());
		cuiAttributes.setExpression(updateRadiusServicePolicyForm.getAdvancedCuiExpression());
		
		radiusServicePolicyData.setCuiConfiguration(cuiAttributes);
		
		return radiusServicePolicyData;
		
	}

	private UpdateRadiusServicePolicyForm convertBeanToForm(RadServicePolicyData data,UpdateRadiusServicePolicyForm form){
		if(data!=null && form!=null){
			form.setName(data.getName());
			form.setDescription(data.getDescription());
			form.setStatus(data.getStatus());
			form.setAuthentication(Boolean.parseBoolean(data.getAuthMsg()));
			form.setAccounting(Boolean.parseBoolean(data.getAcctMsg()));
			form.setRuleSetAuth(data.getAuthRuleset());
			form.setRuleSetAcct(data.getAcctRuleset());
			form.setValidatePacket(Boolean.parseBoolean(data.getValidatepacket()));
			form.setAuthResponseBehavior(data.getDefaultAuthResBehavior());
			form.setHotlinePolicy(data.getHotlinePolicy()!=null?data.getHotlinePolicy():"");
			form.setAcctResponseBehavior(data.getDefaultAcctResBehavior());
			
			if(data.getSessionManagerId() == null){
				form.setSessionManagerId(null);
			}else{
				form.setSessionManagerId(data.getSessionManagerId());
			}
			
			form.setAuthResponseAttributes(data.getAuthResponseAttributes());
			form.setAcctResponseAttributes(data.getAcctResponseAttributes());
			form.setCui(data.getCui());
			form.setAdvancedCuiExpression(data.getAdvancedCuiExpression());
			form.setAcctAttributes(data.getAcctAttribute());
			form.setAuthAttributes(data.getAuthAttribute());
			form.setAuditUid(data.getAuditUid());
			form.setUserIdentity(data.getUserIdentity());
			form.setAdvancedCuiExpression(data.getAdvancedCuiExpression());
		}
		return form;
	}
	
	private boolean searchElementFromXml(String stringReader) throws XPathExpressionException {
		try{
			 InputSource myInputSource=new InputSource(new StringReader(stringReader));
			 
			 SAXParserFactory factory = SAXParserFactory.newInstance();
		     SAXParser saxParser = factory.newSAXParser();
		     
		     /* Define a handler for the parser. The handler would just dump the value of tags
		        as fetched from the oracle table */
		     DefaultHandler handler = new DefaultHandler() {
		         boolean bid = false;
		         public void startElement(String uri, String localName,String qName, Attributes attributes)
		         throws SAXException {
		                 if (qName.equalsIgnoreCase("concurrency-handler")) {
		                   bid = true;
		                  }
		            }
		            public void characters(char ch[], int start, int length)
		            throws SAXException {

		                if (bid) {
		                	isConcurrencyBounded = true;
		                    bid = false;
		                }
		            }
		        };
		        /* Call parse method to parse the input */
		        saxParser.parse(myInputSource, handler);
		        /* Close all DB related objects */
		}catch(Exception e){
			e.fillInStackTrace();
		}
		return isConcurrencyBounded;
	}

}

