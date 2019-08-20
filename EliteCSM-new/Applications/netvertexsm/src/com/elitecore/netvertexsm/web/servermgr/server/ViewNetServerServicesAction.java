package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.server.form.ViewNetServerServicesForm;


public class ViewNetServerServicesAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "viewNetServerServices";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "VIEW SERVER SERVICE ACTION";
	private static final String SUB_MODULE_ACTIONALIAS = ConfigConstant.VIEW_CONFIGURED_SERVICES_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
		String strNetServerId = request.getParameter("netserverid");

		try {
			checkActionPermission(request,SUB_MODULE_ACTIONALIAS);
			ViewNetServerServicesForm viewNetServerServicesForm = (ViewNetServerServicesForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
			Long netServerId=null;
			if(strNetServerId != null){
				netServerId = Long.parseLong(strNetServerId);
			}else{
				netServerId = viewNetServerServicesForm.getNetServerId();
			}
			
			if(netServerId != null){
				INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				
				List netServerTypeList = netServerBLManager.getNetServerTypeList();
				List netServiceInstanceList = netServiceBLManager.getNetServiceInstanceList(netServerId);
				List lstNetServiceType = netServiceBLManager.getNetServiceTypeList();
/*				System.out.println("********** before ****************");
				List listName = new ArrayList();

				for(int i=0;i<lstNetServiceType.size();i++){
					System.out.println("Here the value of the netServiceTypeId  is BEfore:"+((INetServiceTypeData)lstNetServiceType.get(i)).getName());
					listName.add(((INetServiceTypeData)lstNetServiceType.get(i)).getNetServiceTypeId());
				}
//				listName.insertionSort();
*/
				viewNetServerServicesForm.setListServices(netServiceInstanceList);
				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("netServiceInstanceList",netServiceInstanceList);
				request.setAttribute("netServerTypeList",netServerTypeList);
				request.setAttribute("lstNetServiceType",lstNetServiceType);

				return mapping.findForward(SUCCESS_FORWARD);
			}else{
				Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
				ActionMessage message = new ActionMessage("servermgr.server.view.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information",message);
				saveErrors(request,messages);
				return mapping.findForward(FAILURE_FORWARD);
			}
		}catch(ActionNotPermitedException e){
            Logger.logError(MODULE,"Error :-" + e.getMessage());
            printPermitedActionAlias(request);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("general.user.restricted"));
            saveErrors(request, messages);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
        }catch (Exception exp) {
			exp.getMessage();
			Logger.logError(MODULE,"Error during data Manager Operation, reason :"+exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
		}
		Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
		ActionMessage message = new ActionMessage("servermgr.server.view.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}
	/*public void insertionSort(){
		int in,out;
		for(out=1;out<)
	}*/
}
