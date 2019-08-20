package com.elitecore.elitesm.web.diameter.sessionmanager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.diameter.sessionmanager.DiameterSessionManagerBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.diameter.sessionmanager.data.DiameterSessionManagerData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.diameter.sessionmanager.form.CreateDiameterSessionManagerForm;
import com.elitecore.elitesm.web.sessionmanager.forms.CreateSessionManagerForm;


public class GetDiameterSessionManager extends BaseWebAction {

	private static final String CREATE_FORWARD = "createDiameterSessionManager";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DIAMETER_SESSION_MANAGER;

	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			CreateDiameterSessionManagerForm createDiameterSessionManagerForm = (CreateDiameterSessionManagerForm) form;
			String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
			List<IDatabaseDSData> databaseDsDataList = databaseDSBLManager.getDatabaseDSList();
			createDiameterSessionManagerForm.setLstDatasource(databaseDsDataList);
			createDiameterSessionManagerForm.setDescription(getDefaultDescription(userName));
			
			DiameterSessionManagerBLManager diameterSessionManagerBLManager = new DiameterSessionManagerBLManager();
			List<DiameterSessionManagerData> diameterSessionManagerDatasList = diameterSessionManagerBLManager.getDiameterSessionManagerDatas();
			createDiameterSessionManagerForm.setDiameterSessionMappingDataList(diameterSessionManagerDatasList);
			
			request.setAttribute("createDiameterSessionManagerForm", createDiameterSessionManagerForm);
			request.setAttribute("diameterSessionMappingDataList", diameterSessionManagerDatasList);
			request.setAttribute("lstDatasource",databaseDsDataList);
			Logger.logTrace(MODULE,"Execute Method Of :"+getClass().getName());
			return mapping.findForward(CREATE_FORWARD);
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
