package com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.dbinterface.form.SearchDBSPInterfaceForm;

public class InitSearchDBSPInterfaceAction extends BaseWebAction {
	private static final String SEARCH_FORWARD = "initSearchDBSPInterface";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SP_INTERFACE;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of "+getClass().getName());

		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchDBSPInterfaceForm searchDBSPInterfaceForm = (SearchDBSPInterfaceForm) form;
				DriverBLManager driverBLManager = new DriverBLManager();
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();

				List<DatabaseDSData> databaseDSList = databaseDSBLManager.getDatabaseDSList();
				List<DriverInstanceData> driverInstanceList = driverBLManager.getDriverInstanceList();

				searchDBSPInterfaceForm.setDatabaseDSList(databaseDSList);
				searchDBSPInterfaceForm.setDriverInstanceList(driverInstanceList);

				return mapping.findForward(SEARCH_FORWARD);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("spinterface.search.failure"));
				saveErrors(request, messages);
				
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("driver.error.heading","searching");
	            errorHeadingMessage.add("errorHeading",message);
	            saveMessages(request,errorHeadingMessage);

				return mapping.findForward(FAILURE);
			}

		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("driver.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
