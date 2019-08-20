package com.elitecore.elitesm.web.servermgr.service;

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
import com.elitecore.elitesm.blmanager.servermgr.service.NetServiceBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.servermgr.ChartTypeBean;
import com.elitecore.elitesm.web.servermgr.service.forms.ViewServiceGraphForm;

public class InitViewServiceGraphAction extends BaseWebAction{
	private static String VIEW_SERVICE_GRAPH ="viewServiceGraph";

	private static String ACTION_ALIAS =ConfigConstant.VIEW_SERVER_GRAPH;


	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ViewServiceGraphForm chartForm = (ViewServiceGraphForm)form;
		NetServerBLManager serverBLManager = new NetServerBLManager();
		String strNetServiceId = request.getParameter("netserviceid");
		List netServiceTypeList = null;
		List<NetServerInstanceData> netServerInstanceList = null;
		
		try {
			checkActionPermission(request, ACTION_ALIAS);
			String netServiceId=null;
			if(strNetServiceId != null){
				netServiceId = strNetServiceId;
			}
			
			NetServiceBLManager netServiceBLManager = new NetServiceBLManager();
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			netServerInstanceList = netServerBLManager.getNetServerInstanceList();	
			
			netServiceTypeList = netServiceBLManager.getNetServiceTypeList();

			INetServiceInstanceData netServiceInstanceData = netServiceBLManager.getNetServiceInstance(netServiceId);
			INetServerInstanceData netServerInstanceData =serverBLManager.getNetServerInstance(netServiceInstanceData.getNetServerId());
			INetServiceTypeData netServiceTypeData =netServiceBLManager.getNetServiceType(netServiceInstanceData.getNetServiceTypeId());


			List<ChartTypeBean> chartTypeList = new ArrayList<ChartTypeBean>();

			if(netServiceTypeData.getAlias().equals(ServermgrConstant.RAD_AUTH)){
				ChartTypeBean chartTypeBean = new ChartTypeBean();
				chartTypeBean.setServerId(netServerInstanceData.getNetServerId());
				chartTypeBean.setName("Authentication Summary");
				chartTypeBean.setDescription("Shows authentication service summary");
				chartTypeBean.setChartType("AUTHSUMMARY");
				chartTypeList.add(chartTypeBean);

				chartTypeBean = new ChartTypeBean();
				chartTypeBean.setServerId(netServerInstanceData.getNetServerId());
				chartTypeBean.setName("Authentication Response Time");
				chartTypeBean.setDescription("Shows average response time of authentication service");
				chartTypeBean.setChartType("AUTHRESPONSETIME");
				chartTypeList.add(chartTypeBean);

				chartTypeBean = new ChartTypeBean();
				chartTypeBean.setServerId(netServerInstanceData.getNetServerId());
				chartTypeBean.setName("Authentication Errors");
				chartTypeBean.setDescription("Shows Authentication Errors");
				chartTypeBean.setChartType("AUTHERRORS");
				chartTypeList.add(chartTypeBean);
				chartForm.setChartList(chartTypeList);
				
			}else if(netServiceTypeData.getAlias().equals(ServermgrConstant.RAD_ACCT)){
				
				ChartTypeBean chartTypeBean = new ChartTypeBean();
				chartTypeBean.setServerId(netServerInstanceData.getNetServerId());
				chartTypeBean.setName("Accounting Summary");
				chartTypeBean.setDescription("Shows accounting service summary");
				chartTypeBean.setChartType("ACCTSUMMARY");
				chartTypeList.add(chartTypeBean);

				chartTypeBean = new ChartTypeBean();
				chartTypeBean.setServerId(netServerInstanceData.getNetServerId());
				chartTypeBean.setName("Accounting Response Time");
				chartTypeBean.setDescription("Shows average response time of accounting service");
				chartTypeBean.setChartType("ACCTRESPONSETIME");
				chartTypeList.add(chartTypeBean);

				chartTypeBean = new ChartTypeBean();
				chartTypeBean.setServerId(netServerInstanceData.getNetServerId());
				chartTypeBean.setName("Accounting Errors");
				chartTypeBean.setDescription("Shows Accounting Errors");
				chartTypeBean.setChartType("ACCTERRORS");
				chartTypeList.add(chartTypeBean);
				chartForm.setChartList(chartTypeList);

			}
			request.setAttribute("netServiceInstanceData",netServiceInstanceData);
			request.setAttribute("netServerInstanceList",netServerInstanceList);
			request.setAttribute("netServiceTypeList",netServiceTypeList);
			request.setAttribute("viewServiceGraph",chartForm);

			return mapping.findForward(VIEW_SERVICE_GRAPH);
			
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
