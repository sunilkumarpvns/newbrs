package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.io.StringReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.ServicePolicyBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData;
import com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadiusServicePolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.ViewRadiusServicePolicyForm;

public class ViewRadiusServicePolicyAction extends BaseWebAction{
	private static final String VIEW_FORWARD = "viewRadiusServicePolicy";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE="ViewRadiusServicePolicyAction";
	private static final String ACTION_ALIAS=ConfigConstant.VIEW_RADIUS_SERVICE_POLICY;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){		
			Logger.logTrace(MODULE,"Enter execute method of"+getClass().getName());
			try{
				ViewRadiusServicePolicyForm viewRadiusServicePolicyForm =(ViewRadiusServicePolicyForm)form;
				ServicePolicyBLManager servicePolicyBLManager = new ServicePolicyBLManager();
				SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();
				String strRadiusPolicyID = request.getParameter("radiusPolicyId");
				String radiusPolicyID;
				
				if(strRadiusPolicyID != null){
					radiusPolicyID = strRadiusPolicyID;
				}else{
					radiusPolicyID = viewRadiusServicePolicyForm.getRadiusPolicyId();
				}
				
				if(Strings.isNullOrBlank(radiusPolicyID) == false){
					RadServicePolicyData radServicePolicyData =new RadServicePolicyData();
					
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					radServicePolicyData = servicePolicyBLManager.getRadiusServicePolicyInstData(radiusPolicyID,staffData,actionAlias);
					if(radServicePolicyData != null && Strings.isNullOrBlank(radServicePolicyData.getSessionManagerId()) == false){
						request.setAttribute("sessionManagerName", radServicePolicyData.getSessionManagerId());
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
					
					String strAuditId = request.getParameter("auditId");
					if(Strings.isNullOrBlank(strAuditId) == false){
						request.setAttribute("viewHistory", true);
						
						HistoryBLManager historyBlManager= new HistoryBLManager();
						
						staffData.setAuditName(radServicePolicyData.getName());
						staffData.setAuditId(radServicePolicyData.getAuditUid());
						
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas =null;
						if(!(strAuditId.toLowerCase().indexOf("null") >= 0)){
							lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditId);
						}
						
						String name=request.getParameter("name");
						request.setAttribute("name", name);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
					} else {
						request.setAttribute("viewHistory", false);
					}
					doAuditing(staffData, actionAlias);
				}
				
				return mapping.findForward(VIEW_FORWARD);
			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);

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
}
