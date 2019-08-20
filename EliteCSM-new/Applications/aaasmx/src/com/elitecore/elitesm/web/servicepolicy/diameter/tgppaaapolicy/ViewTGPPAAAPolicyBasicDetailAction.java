package com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.servicepolicy.diameter.TGPPAAAPolicyBLManager;
import com.elitecore.elitesm.blmanager.sessionmanager.SessionManagerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servicepolicy.diameter.tgppaaapolicy.data.TGPPAAAPolicyData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.form.TGPPAAAPolicyForm;
import com.elitecore.elitesm.web.servicepolicy.tgpp.data.TGPPServerPolicyData;

public class ViewTGPPAAAPolicyBasicDetailAction extends BaseWebAction {

	private static final String VIEW_FORWARD = "viewTGPPAAAPolicyBasicDetails";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "ViewTGPPAAAPolicyBasicDetailAction";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_TGPP_AAA_SERVICE_POLICY;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		if ((checkAccess(request, ACTION_ALIAS))) {
			Logger.logTrace(MODULE, "Enter execute method of" + getClass().getName());
			try {

				TGPPAAAPolicyForm tgppAAAPolicyForm = (TGPPAAAPolicyForm) form;
				TGPPAAAPolicyBLManager tgppAAAPolicyBLManager = new TGPPAAAPolicyBLManager();
				SessionManagerBLManager sessionManagerBLManager = new SessionManagerBLManager();

				String strTGPPAAAPolicyID = request.getParameter("tgppAAAPolicyId");
				String tgppAAAPolicyID;

				if (strTGPPAAAPolicyID != null) {
					tgppAAAPolicyID = strTGPPAAAPolicyID;
				} else {
					tgppAAAPolicyID = tgppAAAPolicyForm.getTgppAAAPolicyId();
				}

				if (Strings.isNullOrBlank(tgppAAAPolicyID) == false) {

					TGPPAAAPolicyData tgppAAAPolicyData = new TGPPAAAPolicyData();
					tgppAAAPolicyData.setTgppAAAPolicyId(tgppAAAPolicyID);

					IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;

					tgppAAAPolicyData = tgppAAAPolicyBLManager.getTGPPAAAPolicyData(tgppAAAPolicyID);
					
					// get Xml Data's
					TGPPServerPolicyData tgppServerPolicyData = ConfigUtil.deserialize(new String(tgppAAAPolicyData.getTgppAAAPolicyXml()), TGPPServerPolicyData.class);
					tgppAAAPolicyData.setCommandCodeResponseAttributesList(tgppServerPolicyData.getCommandCodeResponseAttributesList());
					
					String strAuditId = request.getParameter("auditId");
					if(Strings.isNullOrBlank(strAuditId) == false){
						request.setAttribute("viewHistory", true);
						
						HistoryBLManager historyBlManager= new HistoryBLManager();
						
						staffData.setAuditName(tgppAAAPolicyData.getName());
						staffData.setAuditId(tgppAAAPolicyData.getAuditUid());
						
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditId);
						
						String name=request.getParameter("name");
						request.setAttribute("name", name);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
						
					}else{
						request.setAttribute("viewHistory", false);
					}

					request.setAttribute("tgppAAAPolicyForm", tgppAAAPolicyForm);
					request.setAttribute("tgppAAAPolicyData", tgppAAAPolicyData);
				}

				return mapping.findForward(VIEW_FORWARD);
				
			} catch (Exception e) {
				e.printStackTrace();
				Logger.logTrace(MODULE, e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);

			}

			return mapping.findForward(FAILURE_FORWARD);
			
		} else {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
