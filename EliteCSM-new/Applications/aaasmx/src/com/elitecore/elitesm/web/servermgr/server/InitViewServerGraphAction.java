package com.elitecore.elitesm.web.servermgr.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.servermgr.ChartTypeBean;
import com.elitecore.elitesm.web.servermgr.server.forms.ViewServerGraphForm;

public class InitViewServerGraphAction extends BaseWebAction{
	private static String VIEW_SERVER_GRAPH ="viewServerGraph";
	private static String ACTION_ALIAS =ConfigConstant.VIEW_SERVER_GRAPH;


	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ViewServerGraphForm chartForm = (ViewServerGraphForm)form;
		String strNetServerId = request.getParameter("netserverid");
		
		try {
			checkAccess(request, ACTION_ALIAS);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}
			
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
			List netServerTypeList = netServerBLManager.getNetServerTypeList();
			List<ChartTypeBean> chartTypeList = new ArrayList<ChartTypeBean>();

			  /*
			   *  Memory Useage ChartTypeBean 
			   */
				ChartTypeBean memoryChartTypeBean = new ChartTypeBean();
				memoryChartTypeBean.setServerId(netServerInstanceData.getNetServerId());
				memoryChartTypeBean.setName("Memory Usage Summary");
				memoryChartTypeBean.setDescription("Shows memory usage summary");
				memoryChartTypeBean.setChartType("MEMORYUSAGE");
				chartTypeList.add(memoryChartTypeBean);
				
				
				/*
				 * Thread Statistic ChartTypeBean
				 */
				
				ChartTypeBean threadChartTypeBean = new ChartTypeBean();
				threadChartTypeBean.setServerId(netServerInstanceData.getNetServerId());
				threadChartTypeBean.setName("Thread Statistics ");
				threadChartTypeBean.setDescription("Shows Thread Statistics");
				threadChartTypeBean.setChartType("THREADSTATISTICS");
				chartTypeList.add(threadChartTypeBean);
				
				
				chartForm.setChartList(chartTypeList);
				
		    request.setAttribute("netServerInstanceData",netServerInstanceData);
		    request.setAttribute("netServerTypeList",netServerTypeList);
			request.setAttribute("viewServerGraph",chartForm);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(VIEW_SERVER_GRAPH);
			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException exp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
			return mapping.findForward(FAILURE);
		}
	}
}
