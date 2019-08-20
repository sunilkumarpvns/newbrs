package com.elitecore.elitesm.web.diameter.diameterepeergroup;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.diameter.diameterpeergroup.DiameterPeerGroupBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerGroup;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.diameterepeergroup.form.DiameterPeerGroupForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewDiameterPeerGroupHistoryAction extends BaseWebAction {
	private static final String VIEW_FORWARD 	= "viewDiameterPeerGroupHistory";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS 	= ConfigConstant.VIEW_DIAMETER_PEER_GROUP;
	private static final String MODULE 			= "ViewDiameterPolicyGroupHistoryAction";

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				
				DiameterPeerGroupForm diameterPeerGroupForm = (DiameterPeerGroupForm)form;
				DiameterPeerGroupBLManager blManager = new DiameterPeerGroupBLManager();
				
				DiameterPeerGroup diameterPeerGroup = new DiameterPeerGroup();
				String strDiaPeerGroupId = request.getParameter("peerGroupId");
				String diaPeerGroupId = strDiaPeerGroupId;
				if(Strings.isNullOrBlank(diaPeerGroupId) == true){
					diaPeerGroupId = diameterPeerGroupForm.getPeerGroupId();
				}

				if(Strings.isNullOrBlank(diaPeerGroupId) == false){
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					diameterPeerGroup = blManager.getDiameterPeerGroupById(diaPeerGroupId);
					
					HistoryBLManager historyBlManager= new HistoryBLManager();
					
					String strAuditUid = request.getParameter("auditUid");
					String strSytemAuditId=request.getParameter("systemAuditId");
					String peerGroupName=request.getParameter("peerGroupName");
					
					if(strSytemAuditId != null){
						request.setAttribute("systemAuditId", strSytemAuditId);
					}
					
					if(diaPeerGroupId != null && Strings.isNullOrBlank(strAuditUid) == false){
						
						staffData.setAuditName(diameterPeerGroup.getPeerGroupName());
						staffData.setAuditId(diameterPeerGroup.getAuditUId());
						
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
						
						request.setAttribute("peerGroupName", peerGroupName);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
					}
					
					if(Strings.isNullOrBlank(diameterPeerGroup.getGeoRedunduntGroup())){
						diameterPeerGroupForm.setGeoRedunduntGroupName("NONE");
					}else{
						DiameterPeerGroup diameterPeerGroupData = blManager.getDiameterPeerGroupById(diameterPeerGroup.getGeoRedunduntGroup());
						diameterPeerGroupForm.setGeoRedunduntGroupName(diameterPeerGroupData.getPeerGroupName());
					}
					
					request.setAttribute("diameterPeerGroup",diameterPeerGroup);
					request.setAttribute("diameterPeerGroupForm", diameterPeerGroupForm);
				}

				return mapping.findForward(VIEW_FORWARD);

			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("diameterpeergroup.view.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE_FORWARD);
		
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}


}
