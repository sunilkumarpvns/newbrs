package com.elitecore.netvertexsm.web.servermgr.server;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.servermgr.dictionary.diameter.DiameterDictionaryBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.dictionary.radius.RadiusDictionaryBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data.DiameterDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.server.form.UpdateNetDictionarySynchronizeForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;

public class UpdateNetDictionarySynchronizeAction extends BaseWebAction {

	private static final String VIEW_FORWARD = "updateNetDictionarySynchronizeAction";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "SYNCHRONIZE DICTIONARY ACTION";
	private static final String ACTION_ALIAS = "SYNCHRONIZE DICTIONARY ACTION";
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());

		try {
			request.getSession().removeAttribute("radiusDictionaryList");
			request.getSession().removeAttribute("diameterDictionaryList");

			UpdateNetDictionarySynchronizeForm updateNetDictionarySynchronizelForm =(UpdateNetDictionarySynchronizeForm)form;
			NetServerBLManager netServerBLManager = new NetServerBLManager();

			String strNetServerId = request.getParameter("netServerId");
			Long netServerId=null;
			if(strNetServerId == null){
				netServerId = updateNetDictionarySynchronizelForm.getNetServerId();
			}else{
				netServerId = Long.parseLong(strNetServerId);
			}

			if(netServerId != null){
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String  actionAlias=ACTION_ALIAS;
				/*get Radius and Diameter  Dictionary List */
				RadiusDictionaryBLManager radiusDictionaryBLManager  = new RadiusDictionaryBLManager();
				DiameterDictionaryBLManager diameterDictionaryBLManager = new DiameterDictionaryBLManager();

				List<RadiusDictionaryData> radiusDictionaryList=radiusDictionaryBLManager.getOnlyDictionaryDataList();
				List<DiameterDictionaryData> diameterDictionaryList=diameterDictionaryBLManager.getOnlyDiameterDictionaryDataList();

				request.getSession().setAttribute("radiusDictionaryList",radiusDictionaryList);
				request.getSession().setAttribute("diameterDictionaryList",diameterDictionaryList);


				INetServerInstanceData netServerInstanceData = new NetServerInstanceData();
				netServerInstanceData.setNetServerId(netServerId);
				netServerInstanceData = netServerBLManager.getNetServerInstance(netServerInstanceData.getNetServerId());
				List netServerTypeList = netServerBLManager.getNetServerTypeList();
				request.setAttribute("netServerInstanceData",netServerInstanceData);
				request.setAttribute("netServerTypeList",netServerTypeList);
			}

			return mapping.findForward(VIEW_FORWARD);
		} catch (Exception e) {
			Logger.logError(MODULE,e.getMessage());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
		}
		ActionMessage message = new ActionMessage("servermgr.update.failure");
		ActionMessages messages = new ActionMessages();
		messages.add("information",message);
		saveErrors(request,messages);
		return mapping.findForward(FAILURE_FORWARD);
	}

}
