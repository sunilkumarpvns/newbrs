package com.elitecore.elitesm.web.inmemorydatagrid;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.aaa.core.conf.impl.ImdgConfigData;
import com.elitecore.aaa.core.conf.impl.ImdgDiameterSessionConfigData;
import com.elitecore.aaa.core.conf.impl.ImdgRadiusSessionConfigData;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.inmemorydatagrid.InMemoryDataGridBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.inmemorydatagrid.data.InMemoryDataGridData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.inmemorydatagrid.form.InMemoryDataGridForm;
import com.elitecore.elitesm.ws.logger.Logger;
import com.google.gson.Gson;

public class InMemoryDataGridAction extends BaseDispatchAction {
	
	private static final String ERROR_DETAILS = "errorDetails";
	private static final String UPDATE_BASIC_DETAIL = "UPDATE_BASIC_DETAIL";
	private static final String UPDATE_RADIUS_SESSION_INDEX = "UPDATE_RADIUS_SESSION_INDEX";
	private static final String UPDATE_DIAMETER_SESSION_INDEX = "UPDATE_DIAMETER_SESSION_INDEX";
	private static final String INFORMATION = "information";
	private static final String IN_MEMORY_DATA_GRID = InMemoryDataGridAction.class.getSimpleName();
	private static final String SEARCH_FORWARD = "search";
	private static final String VIEW_HISTORY_FORWARD = "viewhistory";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	public static final String UPDATE_INMEMORYDATAGRID = ConfigConstant.UPDATE_INMEMORYDATAGRID;  
	public static final String VIEW_INMEMORYDATAGRID = ConfigConstant.VIEW_INMEMORYDATAGRID; 

	public ActionForward search(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logTrace(IN_MEMORY_DATA_GRID,"Enter the execute method of :" + getClass().getName());
		
		try {
			
			if(checkAccess(request, VIEW_INMEMORYDATAGRID)){  
			
				InMemoryDataGridForm inMemoryDataGridForm = (InMemoryDataGridForm) form;
				
				InMemoryDataGridBLManager inMemoryDataGridBLManager = new InMemoryDataGridBLManager();
				InMemoryDataGridData inMemoryDataGridData = inMemoryDataGridBLManager.search();
				
				if(inMemoryDataGridData != null){
					
					/* fetch XML Data */
					String xmlDatas = new String(inMemoryDataGridData.getImdgXml());
					StringReader stringReader =new StringReader(xmlDatas.trim());
					
					//Convert into relevant POJO 
					JAXBContext context = JAXBContext.newInstance(ImdgConfigData.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					ImdgConfigData imdgConfigData = (ImdgConfigData) unmarshaller.unmarshal(stringReader);
				
					Gson gson = new Gson();
				    String imdgJSON = gson.toJson(imdgConfigData);
				    
				    inMemoryDataGridForm.setImdgConfig(imdgJSON);
				}
				
				NetServerBLManager netServerBLManager = new NetServerBLManager();
				List<NetServerInstanceData> netServerInstanceDatas = netServerBLManager.getNetServerInstanceList();
				
				inMemoryDataGridForm.setInstanceDataList(netServerInstanceDatas);
				
				request.setAttribute("inMemoryDataGridForm", inMemoryDataGridForm);
				
			} else {
					Logger.logError(MODULE, "Error during Data Manager operation ");
					ActionMessage message = new ActionMessage("general.user.restricted");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveErrors(request, messages);
					return mapping.findForward(INVALID_ACCESS_FORWARD);
			}

		} catch (Exception e) {
			Logger.logTrace(IN_MEMORY_DATA_GRID, e);
			Logger.logTrace(IN_MEMORY_DATA_GRID, e.getMessage());
			Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("imdg.update.failure");
		    ActionMessages messages = new ActionMessages();
		    messages.add(INFORMATION, message);
		    saveErrors(request, messages);
		} 
		return mapping.findForward(SEARCH_FORWARD);
	}

	public ActionForward updateRadiusSessionIndex(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		Logger.logTrace(IN_MEMORY_DATA_GRID,"Enter the execute method of :" + getClass().getName());
		
		try {
			return updateIMDGConfiguration(form, request, mapping, UPDATE_RADIUS_SESSION_INDEX);
		} catch (Exception e) {
			Logger.logTrace(IN_MEMORY_DATA_GRID, e);
			Logger.logTrace(IN_MEMORY_DATA_GRID, e.getMessage());
			Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("imdg.update.radius.session.failure");
		    ActionMessages messages = new ActionMessages();
		    messages.add(INFORMATION, message);
		    saveErrors(request, messages);
		    return mapping.findForward(FAILURE_FORWARD);
		} 
	}
	
	public ActionForward updateDiameterSessionIndex(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		Logger.logTrace(IN_MEMORY_DATA_GRID,"Enter the execute method of :" + getClass().getName());

		try {
			return updateIMDGConfiguration(form, request, mapping, UPDATE_DIAMETER_SESSION_INDEX);
		} catch (Exception e) {
			Logger.logTrace(IN_MEMORY_DATA_GRID, e);
			Logger.logTrace(IN_MEMORY_DATA_GRID, e.getMessage());
			Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("imdg.update.diameter.session.failure");
		    ActionMessages messages = new ActionMessages();
		    messages.add(INFORMATION, message);
		    saveErrors(request, messages);
		    return mapping.findForward(FAILURE_FORWARD);
		} 
	}
	
	public ActionForward updateBasicDetails(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		Logger.logTrace(IN_MEMORY_DATA_GRID,"Enter the execute update method of :"+getClass().getName());
		try {
			return updateIMDGConfiguration(form,request,mapping,UPDATE_BASIC_DETAIL);
		} catch (Exception e) {
			Logger.logTrace(IN_MEMORY_DATA_GRID, e);
			Logger.logTrace(IN_MEMORY_DATA_GRID, e.getMessage());
			Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("imdg.update.basic.details.failure");
		    ActionMessages messages = new ActionMessages();
		    messages.add(INFORMATION, message);
		    saveErrors(request, messages);
		    return mapping.findForward(FAILURE_FORWARD);
		} 
	}
	
	private ActionForward updateIMDGConfiguration(ActionForm form, HttpServletRequest request, ActionMapping mapping, String alias) throws Exception{


		if(checkAccess(request, UPDATE_INMEMORYDATAGRID)){  
			IStaffData staffData = getStaffObject((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm"));

			InMemoryDataGridForm inMemoryDataGridForm = (InMemoryDataGridForm) form;
			InMemoryDataGridBLManager inMemoryDataGridBLManager = new InMemoryDataGridBLManager();
			InMemoryDataGridData inMemoryDataGridData = null;
			ImdgConfigData existingIMDGData = null;
			inMemoryDataGridData = inMemoryDataGridBLManager.search();

			if(inMemoryDataGridForm.getImdgConfig() != null ){

				if(inMemoryDataGridData == null){

					inMemoryDataGridData = new InMemoryDataGridData();
					existingIMDGData = new Gson().fromJson(inMemoryDataGridForm.getImdgConfig(), ImdgConfigData.class);
				} else {
					String existingxmlDatas = new String(inMemoryDataGridData.getImdgXml());
					StringReader stringReader =new StringReader(existingxmlDatas.trim());

					//Convert into relevant POJO 
					JAXBContext context = JAXBContext.newInstance(ImdgConfigData.class);
					Unmarshaller unmarshaller = context.createUnmarshaller();
					existingIMDGData = (ImdgConfigData) unmarshaller.unmarshal(stringReader);

					ImdgConfigData newIMDGConfigData= new Gson().fromJson(inMemoryDataGridForm.getImdgConfig(), ImdgConfigData.class);

					if(UPDATE_BASIC_DETAIL.equalsIgnoreCase(alias)){
						existingIMDGData.setActive(newIMDGConfigData.isActive());
						existingIMDGData.setClusterGroups(newIMDGConfigData.getClusterGroups());
						existingIMDGData.setInMemoryFormat(newIMDGConfigData.getInMemoryFormat());
						existingIMDGData.setMancenterUrl(newIMDGConfigData.getMancenterUrl());
						existingIMDGData.setOutboundPorts(newIMDGConfigData.getOutboundPorts());
						existingIMDGData.setPropertyList(newIMDGConfigData.getPropertyList());
						existingIMDGData.setStartPort(newIMDGConfigData.getStartPort());
						existingIMDGData.setStartPortCount(newIMDGConfigData.getStartPortCount());
					} else if(UPDATE_RADIUS_SESSION_INDEX.equalsIgnoreCase(alias)){
						existingIMDGData.setActive(newIMDGConfigData.isActive());
						ImdgRadiusSessionConfigData imdgConfigRadiusSessionData = new ImdgRadiusSessionConfigData();
						imdgConfigRadiusSessionData.setRadiusIMDGFieldMapping(newIMDGConfigData.getImdgRadiusConfig().getRadiusIMDGFieldMapping());
						imdgConfigRadiusSessionData.setRadiusSessionFieldMappingList(newIMDGConfigData.getImdgRadiusConfig().getRadiusSessionFieldMappingList());
						imdgConfigRadiusSessionData.setSessionClosureProperties(newIMDGConfigData.getImdgRadiusConfig().getSessionClosureProperties());
						existingIMDGData.setImdgRadiusConfig(imdgConfigRadiusSessionData);
					}else if(UPDATE_DIAMETER_SESSION_INDEX.equalsIgnoreCase(alias)){
						existingIMDGData.setActive(newIMDGConfigData.isActive());
						ImdgDiameterSessionConfigData imdgConfigDiameterSessionData = new ImdgDiameterSessionConfigData();
						imdgConfigDiameterSessionData.setDiameterIMDGFieldMapping(newIMDGConfigData.getImdgDiameterConfig().getDiameterIMDGFieldMapping());
						existingIMDGData.setImdgDiameterConfig(imdgConfigDiameterSessionData);
					}
				}
				/* Generate XML */
				JAXBContext jaxbContext = JAXBContext.newInstance(ImdgConfigData.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				StringWriter xmlObj = new StringWriter();
				jaxbMarshaller.marshal(existingIMDGData,xmlObj);

				String xmlDatas = xmlObj.toString().trim();
				byte[] byteArray = xmlDatas.getBytes();

				inMemoryDataGridData.setImdgXml(byteArray);
				inMemoryDataGridBLManager.update(inMemoryDataGridData, staffData);
				
				Logger.getLogger().info(IN_MEMORY_DATA_GRID, "******************************* XML Data *******************************");
				Logger.getLogger().info(IN_MEMORY_DATA_GRID, xmlDatas);
				Logger.getLogger().info(IN_MEMORY_DATA_GRID, "***********************************************************************");
				Logger.getLogger().info(IN_MEMORY_DATA_GRID, "XML Length : "+ xmlDatas.length());
				Logger.getLogger().info(IN_MEMORY_DATA_GRID, "***********************************************************************");
			}
			request.setAttribute("inMemoryDataGridForm", inMemoryDataGridForm);
			request.setAttribute("responseUrl","/inMemoryDataGrid.do?method=search");
			ActionMessage message = new ActionMessage("imdg.update.success");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveMessages(request, messages);
			return mapping.findForward(SUCCESS_FORWARD);
		}else {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add(INFORMATION, message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	public ActionForward viewHistory(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logTrace(IN_MEMORY_DATA_GRID,"Enter the execute method of :" + getClass().getName());
		
		try {
			
			if(checkAccess(request, VIEW_INMEMORYDATAGRID)){  
			
				InMemoryDataGridForm inMemoryDataGridForm = (InMemoryDataGridForm) form;
				
				InMemoryDataGridBLManager inMemoryDataGridBLManager = new InMemoryDataGridBLManager();
				InMemoryDataGridData inMemoryDataGridData = inMemoryDataGridBLManager.search();
				
				if(inMemoryDataGridData != null){
					
					HistoryBLManager historyBlManager= new HistoryBLManager();
	
					List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(inMemoryDataGridData.getAuditUId());
					
					if( Strings.isNullOrBlank(inMemoryDataGridData.getAuditUId()) == false){
						request.setAttribute("systemAuditId", inMemoryDataGridData.getAuditUId());
						request.setAttribute("name", "In-Memory Data Grid");
					}
	
					request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
				}
				
				request.setAttribute("inMemoryDataGridForm", inMemoryDataGridForm);
				request.setAttribute("inMemoryDataGridData", inMemoryDataGridData);
			} else {
				Logger.logError(MODULE, "Error during Data Manager operation ");
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add(INFORMATION, message);
				saveErrors(request, messages);

				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
		} catch (Exception e) {
			Logger.logTrace(IN_MEMORY_DATA_GRID, e);
			Logger.logTrace(IN_MEMORY_DATA_GRID, e.getMessage());
			Object[] errorElements = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute(ERROR_DETAILS, errorElements);
			ActionMessage message = new ActionMessage("imdg.view.history.failure");
		    ActionMessages messages = new ActionMessages();
		    messages.add(INFORMATION, message);
		    saveErrors(request, messages);
			return mapping.findForward(FAILURE_FORWARD);
		} 
		return mapping.findForward(VIEW_HISTORY_FORWARD);
	}
}
