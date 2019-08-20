package com.elitecore.elitesm.web.wsconfig.subscriber;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;

public class ViewSubcriberWSHistoryAction extends BaseWebAction {

	private static final String VIEW_SUBCRIBER_WS_HISTORY = "viewSubcriberWSHistory";
	private static final String FAILURE_FORWARD = "failure";
	
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		try {
			HistoryBLManager historyBlManager = new HistoryBLManager();
			List<DatabaseHistoryData> lstDatabaseDSHistoryDatas = historyBlManager.getHistoryDataByModuleName(ConfigConstant.SUBSCRIBER_PROFILE_WEB_SERVICE_CONFIG);

			request.setAttribute("lstDatabaseDSHistoryDatas",lstDatabaseDSHistoryDatas);
			return mapping.findForward(VIEW_SUBCRIBER_WS_HISTORY);
		} catch (Exception e) {
			Logger.logError(MODULE, "Returning error forward from "+ getClass().getName());
			Logger.logTrace(MODULE, e);
			Object []errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			ActionMessage message1 = new ActionMessage("databaseds.viewdatasource.failure");
			messages.add("information", message1);
			saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		}
	}
}