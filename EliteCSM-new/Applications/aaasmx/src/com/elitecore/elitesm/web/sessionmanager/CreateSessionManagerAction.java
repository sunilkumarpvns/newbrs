package com.elitecore.elitesm.web.sessionmanager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.sessionmanager.forms.CreateSessionManagerForm;

public class CreateSessionManagerAction extends BaseWebAction {
	private static final String FAILURE_FORWARD = "failure";
	private static final String NEXT_FORWARD = "createSessionManagerDetail";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_SESSION_MANAGER;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		if((checkAccess(request, ACTION_ALIAS))){
			Logger.logTrace(MODULE,"Enter the execute method of :"+getClass().getName());
			try{
				
				ExternalSystemInterfaceBLManager externalSystemBLmanager = new ExternalSystemInterfaceBLManager();
				List<ExternalSystemInterfaceInstanceData> nasInstanceList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.NAS);
				String nasInstanceIds[] = new String[nasInstanceList.size()];
				String nasInstanceNames[][] = new String[nasInstanceList.size()][4]; 
				for(int i=0;i<nasInstanceList.size();i++) {
					ExternalSystemInterfaceInstanceData	externalSystemData = nasInstanceList.get(i);
					nasInstanceNames[i][0] = String.valueOf(externalSystemData.getName());
					nasInstanceNames[i][1] = String.valueOf(externalSystemData.getAddress());
					nasInstanceNames[i][2] = String.valueOf(externalSystemData.getMinLocalPort());
					nasInstanceNames[i][3] = String.valueOf(externalSystemData.getExpiredRequestLimitCount());
					nasInstanceIds[i] = String.valueOf(externalSystemData.getEsiInstanceId());
				}
									
				request.setAttribute("nasInstanceIds", nasInstanceIds);
				request.setAttribute("nasInstanceNames", nasInstanceNames);
				request.setAttribute("nasInstanceList", nasInstanceList);
				
				List<ExternalSystemInterfaceInstanceData> externalSystemInstanceList = externalSystemBLmanager.getExternalSystemInstanceDataList(ExternalSystemConstants.ACCT_PROXY);
				String esiInstanceIds[] = new String[externalSystemInstanceList.size()];
				String esiInstanceNames[][] = new String[externalSystemInstanceList.size()][4]; 
				for(int i=0;i<externalSystemInstanceList.size();i++) {
					ExternalSystemInterfaceInstanceData	externalSystemData = externalSystemInstanceList.get(i);
					esiInstanceNames[i][0] = String.valueOf(externalSystemData.getName());
					esiInstanceNames[i][1] = String.valueOf(externalSystemData.getAddress());
					esiInstanceNames[i][2] = String.valueOf(externalSystemData.getMinLocalPort());
					esiInstanceNames[i][3] = String.valueOf(externalSystemData.getExpiredRequestLimitCount());
					esiInstanceIds[i] = String.valueOf(externalSystemData.getEsiInstanceId());
				}
				
				request.setAttribute("esiInstanceIds", esiInstanceIds);
				request.setAttribute("esiInstanceNames", esiInstanceNames);
				request.setAttribute("proxyServerRelList", externalSystemInstanceList);
				
				String currentUser = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserId();
				CreateSessionManagerForm createSessionManagerForm = (CreateSessionManagerForm)form;
				ISessionManagerInstanceData sessionManagerData = new SessionManagerInstanceData();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				sessionManagerData.setName(createSessionManagerForm.getName());
				sessionManagerData.setDescription(createSessionManagerForm.getDescription());
				sessionManagerData.setCreatedbystaffid(currentUser);
				sessionManagerData.setLastmodifiedbystaffid(currentUser);
				sessionManagerData.setCreatedate(getCurrentTimeStemp());
				sessionManagerData.setLastmodifieddate(getCurrentTimeStemp());
			
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				List<IDatabaseDSData> lstdatasource = databaseDSBLManager.getDatabaseDSList();
				request.getSession().setAttribute("sessionManagerData",sessionManagerData);
				request.setAttribute("lstDatasource",lstdatasource);
				return mapping.findForward(NEXT_FORWARD);
			}catch(DataManagerException e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("sessionmanager.create.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);

			}catch(Exception e){
				e.printStackTrace();
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("sessionmanager.create.failure");
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
}
