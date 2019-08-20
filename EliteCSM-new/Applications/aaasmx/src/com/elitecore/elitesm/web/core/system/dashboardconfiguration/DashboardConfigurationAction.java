package com.elitecore.elitesm.web.core.system.dashboardconfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.JVMDetailUsageData;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.LiveCPUUsageData;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.LiveMemoryUsageData;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.NASESIReqChartData;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.RadAcctReqChartData;
import com.elitecore.elitesm.datamanager.dashboard.data.chartdata.TotalReqChartData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.dashboardconfiguration.forms.DashboardConfigurationForm;
import com.elitecore.elitesm.web.core.system.db.datasource.DBDataSourceImpl;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.dashboard.json.TemplateGlobalConfData;
import com.elitecore.elitesm.web.dashboard.json.WidgetTemplateData;
import com.elitecore.elitesm.web.dashboard.widget.dao.cpuusage.CpuUsageDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.cpuusage.CpuUsageDAOImpl;
import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthclient.DefaultRadiusDynaAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthclient.RadiusDynaAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthserver.DefaultRadiusDynaAuthServerDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthserver.RadiusDynaAuthServerDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.jvmdetailmemoryusage.DefaultJVMDetailMemoryUsageDao;
import com.elitecore.elitesm.web.dashboard.widget.dao.jvmdetailmemoryusage.DetailMemoryUsageDao;
import com.elitecore.elitesm.web.dashboard.widget.dao.memoryusage.DefaultMemoryUsageDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.memoryusage.MemoryUsageDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.nasesistatistics.DefaultNASESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.nasesistatistics.NASESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctclient.DefaultRadiusAcctClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctclient.RadiusAcctClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctesistatistics.DefaultRadAcctESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctesistatistics.RadiusAcctESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctserv.DefaultRadiusAcctServDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radacctserv.RadiusAcctServDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthclient.DefaultRadiusAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthclient.RadiusAuthClientDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthesistatistics.DefaultRadAuthESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthesistatistics.RadiusAuthESIStatisticsDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthserv.DefaultRadiusAuthServDAO;
import com.elitecore.elitesm.web.dashboard.widget.dao.radauthserv.RadiusAuthServDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.cpuusage.CpuUsageData;
import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthclient.RadiusDynaAuthClientData;
import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthserver.RadiusDynaAuthServerData;
import com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory.JVMDetailMemory;
import com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory.JVMDetailMemoryUsageData;
import com.elitecore.elitesm.web.dashboard.widget.model.memoryusage.MemoryUsageData;
import com.elitecore.elitesm.web.dashboard.widget.model.nasesistatistics.NasESIStatData;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctclient.RadiusAcctClientData;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctesistatistics.RadiusAcctESIStatData;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctserv.RadiusAcctServData;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthclient.RadiusAuthClientData;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthesistatistics.RadiusAuthESIStatData;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthserv.RadiusAuthServData;
import com.google.gson.Gson;

public class DashboardConfigurationAction extends BaseDispatchAction {

	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = DashboardConfigurationAction.class.getSimpleName();
	private static final String VIEWDASHBOARD = "dashboardConfiguration";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";

	public ActionForward viewDashboardConfiguration(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered viewDashboardConfiguration method of "+ getClass().getName());
		try {
			    DashboardConfigurationForm dashboardConfigurationForm = (DashboardConfigurationForm)form;
			    DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				DashboardBLManager blManager = new DashboardBLManager();
				
				List<IDatabaseDSData> databaseList = databaseDSBLManager.getDatabaseDSList();
				
				DashboardConfigData dashboardConfigData=blManager.getDashboardConfiguration();
				
				String customWidgteId = null;
				customWidgteId = blManager.getCustomWidgetId(ConfigConstant.CUSTOM);
				
				List<WidgetTemplateData> widgetTemplateDataList=blManager.getwidgetTemplateDetails();
				List<WidgetTemplateData> systemWidgetList=new ArrayList<WidgetTemplateData>();
				List<WidgetTemplateData> customWidgetList=new ArrayList<WidgetTemplateData>();
				
				for(WidgetTemplateData widgetTemplateData:widgetTemplateDataList){
					if(widgetTemplateData.getCategoryId().equals(customWidgteId)){
						customWidgetList.add(widgetTemplateData);
					}else{
						systemWidgetList.add(widgetTemplateData);
					}
				}
				
				
				if(widgetTemplateDataList !=null &&  widgetTemplateDataList.size() > 0){
					dashboardConfigurationForm.setWidgetTemplateDataList(systemWidgetList);
				}
				
				if(customWidgetList != null && customWidgetList.size() > 0) {
					dashboardConfigurationForm.setCustomWidgetList(customWidgetList);
				}
				if(dashboardConfigData !=null){
					convertbeantoform(dashboardConfigData,dashboardConfigurationForm);
				}
				
				dashboardConfigurationForm.setDatabaseDSList(databaseList);
				request.setAttribute("dashboardConfigurationForm", dashboardConfigurationForm);
				return mapping.findForward(VIEWDASHBOARD); 
		
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			// Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			// Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	private void convertbeantoform(DashboardConfigData dashboardConfigData,DashboardConfigurationForm dashboardConfigurationForm) {
		dashboardConfigurationForm.setDashboardConfigId(dashboardConfigData.getDashboardConfigId());
		dashboardConfigurationForm.setDatabaseId(dashboardConfigData.getDatabaseId());
		dashboardConfigurationForm.setMaxConcurrentAccess(dashboardConfigData.getMaxConcurrentAccess());
		dashboardConfigurationForm.setMaxTabs(dashboardConfigData.getMaxTabs());
		dashboardConfigurationForm.setMaxWebSockets(dashboardConfigData.getMaxWebSockets());
		dashboardConfigurationForm.setMaxWidgets(dashboardConfigData.getMaxWidgets());
	}
	public ActionForward saveDashboardConfiguration(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered viewDashboardConfiguration method of "+ getClass().getName());
		try {
			    DashboardConfigurationForm dashboardConfigurationForm = (DashboardConfigurationForm)form;
				DashboardConfigData dashboardConfigData = new DashboardConfigData();

				convertFormToBean(dashboardConfigurationForm, dashboardConfigData);

				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				DashboardBLManager dashboardBLManager = new DashboardBLManager();
				dashboardBLManager.saveDashboardConfiguration(dashboardConfigData, staffData);

				//START : Get Database DataSource
				
				DatabaseDSBLManager databaseDSBLManager = new DatabaseDSBLManager();
				IDatabaseDSData databaseDsData=databaseDSBLManager.getDatabaseDSDataById(dashboardConfigData.getDatabaseId());
				DBConnectionManager dbConnectionManager = DBConnectionManager.getInstance(databaseDsData.getName());

				if(dbConnectionManager.isInitilized() == false){
					DBDataSourceImpl dbDataSourceImpl = new  DBDataSourceImpl();
					dbDataSourceImpl.setDatasourceID(databaseDsData.getName());
					dbDataSourceImpl.setDataSourceName(databaseDsData.getName());
					dbDataSourceImpl.setConnectionURL(databaseDsData.getConnectionUrl());
					dbDataSourceImpl.setPassword(databaseDsData.getPassword());
					dbDataSourceImpl.setMinimumPoolSize(Long.valueOf(databaseDsData.getMinimumPool()).intValue());
					dbDataSourceImpl.setMaximumPoolSize(Long.valueOf(databaseDsData.getMaximumPool()).intValue());
					dbDataSourceImpl.setUsername(databaseDsData.getUserName());
					
					dbConnectionManager.init(dbDataSourceImpl, null);
				}
				
				request.setAttribute("responseUrl","/dashboardConfiguration.do?method=viewDashboardConfiguration");
				ActionMessage message = new ActionMessage("dashboard.configuration.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
		
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			// Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			// Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	private void convertFormToBean(DashboardConfigurationForm dashboardConfigurationForm,
			DashboardConfigData dashboardConfigData) throws IOException {
			if( Strings.isNullOrBlank(dashboardConfigurationForm.getDashboardConfigId()) ){
				dashboardConfigData.setDashboardConfigId(null);
			}else{
				dashboardConfigData.setDashboardConfigId(dashboardConfigurationForm.getDashboardConfigId());
			}
	
			
			dashboardConfigData.setDatabaseId(dashboardConfigurationForm.getDatabaseId());
			dashboardConfigData.setMaxConcurrentAccess(dashboardConfigurationForm.getMaxConcurrentAccess());
			dashboardConfigData.setMaxTabs(dashboardConfigurationForm.getMaxTabs());
			dashboardConfigData.setMaxWebSockets(dashboardConfigurationForm.getMaxWebSockets());
			dashboardConfigData.setMaxWidgets(dashboardConfigurationForm.getMaxWidgets());
	}
	public ActionForward saveTemplateGlobalConfiguration(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered viewDashboardConfiguration method of "+ getClass().getName());
		try {
			String strTemplateId = (request.getParameter("templateId")).trim();
			String strRefreshIntervals = (request.getParameter("refreshIntervals")).trim();
			String active=(request.getParameter("active")).trim();
			
			Long refreshIntervals=Long.parseLong(strRefreshIntervals);
			DashboardBLManager blManager = new DashboardBLManager();
			if( Strings.isNullOrEmpty(strTemplateId) == false && refreshIntervals != null && active != null){
				
				TemplateGlobalConfData templateGlobalConfData=new TemplateGlobalConfData();
				
				templateGlobalConfData.setActive(active);
				templateGlobalConfData.setRefreshInterval(refreshIntervals);
				templateGlobalConfData.setTemplateId(strTemplateId);
				
				blManager.saveTemplateGlobalConf(templateGlobalConfData);
			}
	    } catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			// Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	public ActionForward createCustomWidget(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered viewDashboardConfiguration method of "+ getClass().getName());
		try {
			    DashboardConfigurationForm dashboardConfigurationForm = (DashboardConfigurationForm)form;
				WidgetTemplateData widgetTemplateData =  new WidgetTemplateData();
				DashboardBLManager dashboardBLManager = new DashboardBLManager();
				
				String customWidgteId = dashboardBLManager.getCustomWidgetId(ConfigConstant.CUSTOM);
				convertFormToBeanCustomWidget(dashboardConfigurationForm, widgetTemplateData,customWidgteId);

				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				
				dashboardBLManager.saveCustomWidget(widgetTemplateData, staffData);

				request.setAttribute("responseUrl","/dashboardConfiguration.do?method=viewDashboardConfiguration");
				ActionMessage message = new ActionMessage("create.custom.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				return mapping.findForward(SUCCESS_FORWARD);
		
		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error during Data Manager operation ");
			// Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			// Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	private void convertFormToBeanCustomWidget(DashboardConfigurationForm dashboardConfigurationForm,WidgetTemplateData widgetTemplateData,String customWidgetId) {
		widgetTemplateData.setCategoryId(customWidgetId);
		widgetTemplateData.setConfigJspUrl(dashboardConfigurationForm.getWidgetConfJsp());
		widgetTemplateData.setDescription(dashboardConfigurationForm.getWidgetDesc());
		widgetTemplateData.setJspUrl(dashboardConfigurationForm.getWidgetJsp());
		widgetTemplateData.setThumbnail(dashboardConfigurationForm.getWidgetThumbnail());
		widgetTemplateData.setTitle(dashboardConfigurationForm.getWidgetName());
		widgetTemplateData.setWidgetGroovy(dashboardConfigurationForm.getWidgetGroovy());
	}
	
	public ActionForward getCustomWidgetConf(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered viewDashboardConfiguration method of "+ getClass().getName());
		try {
			
				String strTemplateId = (request.getParameter("templateId")).trim();
				response.setContentType("application/json");

				DashboardBLManager blManager = new DashboardBLManager();
				
				if(Strings.isNullOrEmpty(strTemplateId) == false){
					WidgetTemplateData widgetTemplateData=blManager.getTemplateDetails(strTemplateId);
					if(widgetTemplateData != null){
						String json = new Gson().toJson(widgetTemplateData);
						response.getWriter().write(json.toString());
					}
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	public ActionForward updateCustomWidgetConf(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered updateCustomWidgetConf method of "+ getClass().getName());
		try {
			
				String strTemplateId = (request.getParameter("templateId")).trim();
				String strtitle = (request.getParameter("title")).trim();
				String strjspUrl = (request.getParameter("jspUrl")).trim();
				String strconfigUrl = (request.getParameter("configUrl")).trim();
				String strthumbnail = (request.getParameter("thumbnail")).trim();
				String strdesription = (request.getParameter("desription")).trim();
				String strwidgetGroovy = (request.getParameter("widgetGroovy")).trim();
				
				response.setContentType("text/html");
			
				DashboardBLManager blManager = new DashboardBLManager();
				
				if(Strings.isNullOrEmpty(strTemplateId) == false){
					WidgetTemplateData widgetTemplateData = new WidgetTemplateData();
					widgetTemplateData.setWidgteTemplateId(strTemplateId);
					widgetTemplateData.setTitle(strtitle);
					widgetTemplateData.setJspUrl(strjspUrl);
					widgetTemplateData.setConfigJspUrl(strconfigUrl);
					widgetTemplateData.setThumbnail(strthumbnail);
					widgetTemplateData.setDescription(strdesription);
					widgetTemplateData.setWidgetGroovy(strwidgetGroovy);
					blManager.updateCustomWidgetConf(widgetTemplateData);
				
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward fetchTemplateGlobalConfiguration(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered viewDashboardConfiguration method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
			
				DashboardBLManager blManager = new DashboardBLManager();
				
				List<TemplateGlobalConfData> templateGlobalConfData=blManager.fetchTemplateGlobalConfiguration();
				if(templateGlobalConfData != null){
					String json = new Gson().toJson(templateGlobalConfData);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getESIData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getESIData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String esiName = (request.getParameter("esiName"));
				if(esiName != null){
					esiName=esiName.trim();
				}
				//String strStartTime = (request.getParameter("startTimeValue"));
				//String strEndTime = (request.getParameter("endTimeValue"));
				
				Logger.logDebug(MODULE, "ESI Name   :"+esiName);
				//Logger.logDebug(MODULE, "Start Time :"+strStartTime);
				//Logger.logDebug(MODULE, "End Time   :"+strEndTime);
				
				/*String pattern="MM-dd-yyyy hh:mm:ss";
				
				Timestamp startTimestamp=null;
				Timestamp endTimestamp=null;
				
				if(strStartTime != null && !("".equalsIgnoreCase(strStartTime)))
				 startTimestamp = new Timestamp(stringToDate(strStartTime,pattern).getTime());
				
				if(strEndTime != null && !("".equalsIgnoreCase(strEndTime)))
				 endTimestamp = new Timestamp(stringToDate(strEndTime,pattern).getTime());*/
				
				// need to convert it into Timestatmp
				
				
				RadiusAuthESIStatisticsDAO radAuthESIStatDAO=new DefaultRadAuthESIStatisticsDAO();
				RadiusAuthESIStatData criteriaData=new RadiusAuthESIStatData();
				//criteriaData.setStartTime(strStartTime);
				//criteriaData.setEndTime(strEndTime);
				criteriaData.setRadAuthServerAddress(esiName);
				long startTime = System.currentTimeMillis();
				List<RadiusAuthESIStatData> radAuthESIStatisticsDetails = radAuthESIStatDAO.getRadAuthESIStatisticsDetails(criteriaData);
				long endTime = System.currentTimeMillis();
				Logger.logDebug(MODULE,"DB Fetch time-difference :["+(endTime-startTime)+"] ms");
				
				startTime=System.currentTimeMillis();
			    TotalReqChartData chartData = convertToChartData(radAuthESIStatisticsDetails);
			    
			    
				if(chartData != null){
					String json = new Gson().toJson(chartData);
					endTime=System.currentTimeMillis();
				    Logger.logDebug(MODULE,"Convert to chart data time-difference :["+(endTime-startTime)+"] ms");
					//Logger.logDebug(MODULE, "esi data :"+json);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getAcctESIData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getAcctESIData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String esiName = (request.getParameter("esiName")).trim();
				
				Logger.logDebug(MODULE, "ESI Name   :"+esiName);
				
				// need to convert it into Timestatmp
				
				RadiusAcctESIStatisticsDAO radAcctESIStatDAO = new DefaultRadAcctESIStatisticsDAO();
				RadiusAcctESIStatData criteriaData = new RadiusAcctESIStatData();
				
				criteriaData.setRadiusAccServerAddress(esiName);
				long startTime = System.currentTimeMillis();
				List<RadiusAcctESIStatData> radAuthESIStatisticsDetails = radAcctESIStatDAO.getRadAuthESIStatisticsDetails(criteriaData);
				long endTime = System.currentTimeMillis();
				Logger.logDebug(MODULE,"DB Fetch time-difference :["+(endTime-startTime)+"] ms");
				
				startTime=System.currentTimeMillis();
				RadAcctReqChartData chartData = convertToAcctChartData(radAuthESIStatisticsDetails);
			    
				if(chartData != null){
					String json = new Gson().toJson(chartData);
					endTime=System.currentTimeMillis();
				    Logger.logDebug(MODULE,"Convert to chart data time-difference :["+(endTime-startTime)+"] ms");
					//Logger.logDebug(MODULE, "esi data :"+json);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getNASESIData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getAcctESIData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String esiName = (request.getParameter("esiName")).trim();
				
				Logger.logDebug(MODULE, "ESI Name   :"+esiName);
				
				// need to convert it into Timestatmp
				NASESIStatisticsDAO nasesiStatisticsDAO = new DefaultNASESIStatisticsDAO();
				NasESIStatData criteriaData =new NasESIStatData();
				
				
				criteriaData.setRadiusDynAuthServerAddress(esiName);
				long startTime = System.currentTimeMillis();
				List<NasESIStatData> radAuthESIStatisticsDetails = nasesiStatisticsDAO.getNasESIStatisticsDetails(criteriaData);
				long endTime = System.currentTimeMillis();
				Logger.logDebug(MODULE,"DB Fetch time-difference :["+(endTime-startTime)+"] ms");
				
				startTime=System.currentTimeMillis();
				NASESIReqChartData chartData = convertToNasChartData(radAuthESIStatisticsDetails);
			    
				if(chartData != null){
					String json = new Gson().toJson(chartData);
					endTime=System.currentTimeMillis();
				    Logger.logDebug(MODULE,"Convert to chart data time-difference :["+(endTime-startTime)+"] ms");
					//Logger.logDebug(MODULE, "esi data :"+json);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	
	
	private NASESIReqChartData convertToNasChartData( List<NasESIStatData> nasEsiStatDataList) {
		NASESIReqChartData reqChartData =null;
		if(nasEsiStatDataList != null && !nasEsiStatDataList.isEmpty())
		{
		  reqChartData =new NASESIReqChartData();
		  int size = nasEsiStatDataList.size();
		  String[] esiArray= new String[size];
		  Integer[] disconnectReq=new Integer[size];
		  Integer[] disconnectNack=new Integer[size];
		  Integer[] disconnectAck=new Integer[size];
		  Integer[] disconnectTimeout=new Integer[size];
		  Integer[] coaReq=new Integer[size];
		  Integer[] coaNack=new Integer[size];
		  Integer[] coaAck=new Integer[size];
		  Integer[] coaTimeout=new Integer[size];
		  String[] serverAddress = new  String[size];
		  Integer[] serverPort = new Integer[size];
		  
		  Long[] epochTimeArray=new Long[size];
		  
		  NASESIReqChartData[] nasESIReqChartData=null;
		
		  
		  for(int i=0;i<size;i++)
		  {
			  NasESIStatData totalReqStatisticsData = nasEsiStatDataList.get(i);
			  
			  esiArray[i]=totalReqStatisticsData.getDynaAuthServerName();
			  serverAddress[i]=totalReqStatisticsData.getRadiusDynAuthServerAddress();
			  serverPort[i]=totalReqStatisticsData.getDynaAuthServerPort();
			  disconnectReq[i]=totalReqStatisticsData.getClientDisconRequests();
			  disconnectNack[i]=totalReqStatisticsData.getRadiusDynAuthClientDisconNaks();
			  disconnectAck[i]=totalReqStatisticsData.getRadiusDynAuthClientDisconAcks();
			  disconnectTimeout[i]=totalReqStatisticsData.getClientDisconTimeouts();
			  coaReq[i]=totalReqStatisticsData.getRadiusDynAuthClientCoARequests();
			  coaAck[i]=totalReqStatisticsData.getRadiusDynAuthClientCoAAcks();
			  coaNack[i]=totalReqStatisticsData.getRadiusDynAuthClientCoANaks();
			  coaTimeout[i]=totalReqStatisticsData.getRadiusDynAuthClientCoATimeouts();
			  epochTimeArray[i]=totalReqStatisticsData.getCreateTime().getTime();
		  }
		  
		  reqChartData.setEsi(esiArray);
		  reqChartData.setCoaAck(coaAck);
		  reqChartData.setCoaNack(coaNack);
		  reqChartData.setCoaReq(coaReq);
		  reqChartData.setCoaTimeout(coaTimeout);
		  reqChartData.setDisconnectAck(disconnectAck);
		  reqChartData.setDisconnectNack(disconnectNack);
		  reqChartData.setDisconnectReq(disconnectReq);
		  reqChartData.setDisconnectTimeout(disconnectTimeout);
		  reqChartData.setEpochTime(epochTimeArray);
		  reqChartData.setNasReqDataArray(nasESIReqChartData);
		  reqChartData.setServerAddress(serverAddress);
		  reqChartData.setServerPort(serverPort);
		}
		return reqChartData;
	}
	
	private TotalReqChartData convertToChartData(List<RadiusAuthESIStatData> totalReqStatistics) throws DataManagerException{
		
		TotalReqChartData reqChartData =null;
		if(totalReqStatistics != null && !totalReqStatistics.isEmpty())
		{
		  reqChartData =new TotalReqChartData();
		  int size = totalReqStatistics.size();
		  String[] esiArray= new String[size];
		  Integer[] accessChallengeArray=new Integer[size];
		  Integer[] accessAcceptArray=new Integer[size];
		  Integer[] accessRejectArray=new Integer[size];
		  Integer[] requestDropArray=new Integer[size];
		  Long[] epochTimeArray=new Long[size];
		  Integer[] serverPortArray=new Integer[size];
		  String[] serverAddressArray= new String[size];
		  
		  TotalReqChartData[] totalReqChartDataArray=null;
		 
		  for(int i=0;i<size;i++)
		  {
			  RadiusAuthESIStatData totalReqStatisticsData = totalReqStatistics.get(i);
			  
			  esiArray[i]=totalReqStatisticsData.getAuthServerName();
			  accessChallengeArray[i]=totalReqStatisticsData.getClientAccessChallenges();
			  accessAcceptArray[i]=totalReqStatisticsData.getRadAuthClientAccessAccepts();
			  accessRejectArray[i]=totalReqStatisticsData.getRadAuthClientAccessRejects();
			  requestDropArray[i]=totalReqStatisticsData.getRadAuthClientTimeouts();
			  epochTimeArray[i]=totalReqStatisticsData.getCreateTime().getTime();
			  serverPortArray[i]=totalReqStatisticsData.getClientServerPortNumber();
			  serverAddressArray[i]=totalReqStatisticsData.getRadAuthServerAddress();
		  }
		  reqChartData.setEsi(esiArray);
		  reqChartData.setAccessAccept(accessAcceptArray);
		  reqChartData.setAccessChallenge(accessChallengeArray);
		  reqChartData.setAccessReject(accessRejectArray);
		  reqChartData.setRequestDrop(requestDropArray);
		  reqChartData.setEpochTime(epochTimeArray);
		  reqChartData.setTotalReqDataArray(totalReqChartDataArray);
		  reqChartData.setServerAddress(serverAddressArray);
		  reqChartData.setServerPort(serverPortArray);
		  
		}
		return reqChartData;
	}

	private RadAcctReqChartData convertToAcctChartData(List<RadiusAcctESIStatData> radiusAcctESIStatDataList) throws DataManagerException{
		
		RadAcctReqChartData reqChartData =null;
		if(radiusAcctESIStatDataList != null && !radiusAcctESIStatDataList.isEmpty())
		{
		  reqChartData =new RadAcctReqChartData();
		  int size = radiusAcctESIStatDataList.size();
		  String[] esiArray= new String[size];
		  Integer[] clientRequestArray=new Integer[size];
		  Integer[] clientResponseArray=new Integer[size];
		  Integer[] retransmissionArray=new Integer[size];
		  Integer[] requestDropArray=new Integer[size];
		  Long[] epochTimeArray=new Long[size];
		  String[] serverAddressArray = new String[size];
		  Integer[] serverPortArray = new Integer[size];
		  
		  RadAcctReqChartData[] totalReqChartDataArray=null;
		 
		  for(int i=0;i<size;i++)
		  {
			  RadiusAcctESIStatData radiusAcctESIStatData = radiusAcctESIStatDataList.get(i);
			  
			  serverAddressArray[i]=radiusAcctESIStatData.getRadiusAccServerAddress();
			  serverPortArray[i]=radiusAcctESIStatData.getClientServerPortNumber();
			  esiArray[i]=radiusAcctESIStatData.getAcctServerName();
			  clientRequestArray[i]=radiusAcctESIStatData.getRadiusAccClientRequests();
			  clientResponseArray[i]=radiusAcctESIStatData.getRadiusAccClientResponses();
			  retransmissionArray[i]=radiusAcctESIStatData.getRadiusAccClientRetransmissions();
			  requestDropArray[i]=radiusAcctESIStatData.getRadiusAccClientTimeouts();
			  epochTimeArray[i]=radiusAcctESIStatData.getCreateTime().getTime();
			  
		  }
		  
		  reqChartData.setEsi(esiArray);
		  reqChartData.setAccountingRes(clientResponseArray);
		  reqChartData.setAccountingReq(clientRequestArray);
		  reqChartData.setRetransmission(retransmissionArray);
		  reqChartData.setRequestDrop(requestDropArray);
		  reqChartData.setEpochTime(epochTimeArray);
		  reqChartData.setTotalReqDataArray(totalReqChartDataArray);
		  reqChartData.setServerAddress(serverAddressArray);
		  reqChartData.setServerPort(serverPortArray);
		  
		}
		return reqChartData;
	}
	
	public ActionForward getRadAuthESIData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getRadAuthESIData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "ESI Server Name   :"+serverIds);
				
				
				RadiusAuthESIStatisticsDAO radAuthESIStatDAO=new DefaultRadAuthESIStatisticsDAO();
				
				List<RadiusAuthESIStatData> radAuthESIStatisticsDetails = radAuthESIStatDAO.getRadAuthESIStatisticsDetailsData(serverIds);
				long endTime = System.currentTimeMillis();
				
			    TotalReqChartData chartData = convertToChartData(radAuthESIStatisticsDetails);
			    
			    
				if(chartData != null){
					String json = new Gson().toJson(chartData);
					//Logger.logDebug(MODULE, "esi data :"+json);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getAuthenticationServiceData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getAuthenticationServiceData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "ESI Server Name   :"+serverIds);
				
				RadiusAuthServDAO authServDAO= new DefaultRadiusAuthServDAO();
				
				List<RadiusAuthServData> radAuthESIStatisticsDetails = authServDAO.getAuthServDataList(serverIds);
			    
				if(radAuthESIStatisticsDetails != null){
					String json = new Gson().toJson(radAuthESIStatisticsDetails);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getAccountingServiceData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getAccountingServiceData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "ESI Server Name   :"+serverIds);
				
				RadiusAcctServDAO acctServDAO = new DefaultRadiusAcctServDAO();
				List<RadiusAcctServData> radAcctServerStatisticsDetails = acctServDAO.getAuthServDataList(serverIds);
			    
				if(radAcctServerStatisticsDetails != null){
					String json = new Gson().toJson(radAcctServerStatisticsDetails);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getAuthClientData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getAuthClientData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "ESI Server Name   :"+serverIds);
				
				RadiusAuthClientDAO authClientDAO = new DefaultRadiusAuthClientDAO();
				List<RadiusAuthClientData> radAuthClientDataDetails = authClientDAO.getRadiusAuthClientDataList(serverIds);
			    
				if(radAuthClientDataDetails != null){
					String json = new Gson().toJson(radAuthClientDataDetails);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getAcctClientData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getAcctClientData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "ESI Server Name   :"+serverIds);
				
				RadiusAcctClientDAO acctClientDAO = new DefaultRadiusAcctClientDAO();
				List<RadiusAcctClientData> radAcctClientDataDetails = acctClientDAO.getRadiusAcctClientDataList(serverIds);
			    
				if(radAcctClientDataDetails != null){
					String json = new Gson().toJson(radAcctClientDataDetails);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getRadAcctESIData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getRadAcctESIData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "ESI Server Name   :"+serverIds);
				
				
				RadiusAcctESIStatisticsDAO radAcctEsiDAO=new DefaultRadAcctESIStatisticsDAO();
				
				List<RadiusAcctESIStatData> radAuthESIStatisticsDetails = radAcctEsiDAO.getRadAuthESIDataList(serverIds);
				long endTime = System.currentTimeMillis();
				
				RadAcctReqChartData chartData = convertToAcctChartData(radAuthESIStatisticsDetails);
			    
				if(chartData != null){
					String json = new Gson().toJson(chartData);
					//Logger.logDebug(MODULE, "esi data :"+json);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getNasESIData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getNasESIData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "NAS ESI Server Name   :"+serverIds);
				
				NASESIStatisticsDAO nasesiStatisticsDAO = new DefaultNASESIStatisticsDAO();
				
				List<NasESIStatData> nasESIStatisticsDetails = nasesiStatisticsDAO.getNasESIDataList(serverIds);
				
				NASESIReqChartData chartData = convertToNasChartData(nasESIStatisticsDetails);
			    
				if(chartData != null){
					String json = new Gson().toJson(chartData);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getDynaClientData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getAcctClientData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "ESI Server Name   :"+serverIds);
				
				RadiusDynaAuthClientDAO dynaAuthClientDAO = new DefaultRadiusDynaAuthClientDAO();
				List<RadiusDynaAuthClientData> dynaAuthClientDataDetails = dynaAuthClientDAO.getRadiusDynaAuthClientDataList(serverIds);
			    
				if(dynaAuthClientDataDetails != null){
					String json = new Gson().toJson(dynaAuthClientDataDetails);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	
	public ActionForward getDynaAuthServerData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getDynaAuthServerData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "ESI Server Name   :"+serverIds);
				
				RadiusDynaAuthServerDAO dynaAuthServerDAO = new DefaultRadiusDynaAuthServerDAO();
				List<RadiusDynaAuthServerData> dynaAuthServerDataDetails = dynaAuthServerDAO.getRadiusDynaAuthServerDataList(serverIds);
			    
				if(dynaAuthServerDataDetails != null){
					String json = new Gson().toJson(dynaAuthServerDataDetails);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getLiveCpuUsageData(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getLiveCpuUsageData method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "Server Name   :"+serverIds);
				
				CpuUsageDAO cpuUsageDAO = new CpuUsageDAOImpl();
				List<CpuUsageData> cpuUsageDataList = cpuUsageDAO.getCpuUsageLiveData(serverIds);
				
				long endTime = System.currentTimeMillis();
				
				LiveCPUUsageData chartData = convertToLiveCPUChartData(cpuUsageDataList);
				
				if(chartData != null){
					String json = new Gson().toJson(chartData);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	private LiveCPUUsageData convertToLiveCPUChartData(List<CpuUsageData> cpuUsageDataList) {
		LiveCPUUsageData liveCpuData =null;
		
		if(cpuUsageDataList != null && !cpuUsageDataList.isEmpty()){
		  
			liveCpuData = new LiveCPUUsageData();
			int size = cpuUsageDataList.size();
		 
		    String[] serverName= new String[size];
		    String[] jvmPoolType=new String[size];
		    String[] jvmPoolName=new String[size];
		    Long[] systemMinValue=new Long[size];
		    Long[] systemMaxValue=new Long[size];
		    Float[] systemAverage=new Float[size];
		    String[] createTime= new String[size];
		  
		    LiveCPUUsageData[] liveCPUDataArray=null;
		 
			for(int i=0;i<size;i++){
			  CpuUsageData cpuUsageData = cpuUsageDataList.get(i);
				  
			  serverName[i]=cpuUsageData.getInstanceID();
			  jvmPoolType[i]=cpuUsageData.getJvmMemoryPoolType();
			  jvmPoolName[i]=cpuUsageData.getJvmMemoryPoolName();
			  systemMinValue[i]=cpuUsageData.getCpuUsageMinVal();
			  systemMaxValue[i]=cpuUsageData.getCpuUsageMaxVal();
			  systemAverage[i]=cpuUsageData.getCpuAverageUsage();
			  createTime[i]=cpuUsageData.getStrCreateTime();
			}
		  
			liveCpuData.setServerName(serverName);
			liveCpuData.setJvmPoolName(jvmPoolName);
			liveCpuData.setJvmPoolType(jvmPoolType);
			liveCpuData.setSystemMinValue(systemMinValue);
			liveCpuData.setSystemMaxValue(systemMaxValue);
			liveCpuData.setSystemAverage(systemAverage);
			liveCpuData.setCreateTime(createTime);
		}
		return liveCpuData;
	}
	
	public ActionForward getLiveMemoryDetails(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getLiveMemoryDetails method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "Server Name   :"+serverIds);
				
				MemoryUsageDAO memoryUsageDAO = new DefaultMemoryUsageDAO();
				List<MemoryUsageData> memoryUsageDataList = memoryUsageDAO.getMemoryUsageLiveData(serverIds);
				
				long endTime = System.currentTimeMillis();
				
				LiveMemoryUsageData chartData = convertToLiveMemoryChartData(memoryUsageDataList);
				
				if(chartData != null){
					String json = new Gson().toJson(chartData);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	private LiveMemoryUsageData convertToLiveMemoryChartData(List<MemoryUsageData> memoryUsageDataList) {
		LiveMemoryUsageData liveMemoryUsageData =null;
		
		if(memoryUsageDataList != null && !memoryUsageDataList.isEmpty()){
		  
			liveMemoryUsageData = new LiveMemoryUsageData();
			int size = memoryUsageDataList.size();
		 
			String[] serverName= new String[size];
			Long[] heapUsed=new Long[size];
			Long[] heapUsedMinVal=new Long[size];
			Long[] heapUsedMaxVal=new Long[size];
			Long[] nonHeapUsed=new Long[size];
			Long[] nonHeapMinVal=new Long[size];
			Long[] nonHeapMaxVal=new Long[size];
			String[] createTime = new String[size];
		  
			for(int i=0;i<size;i++){
				MemoryUsageData memoryUsageData = memoryUsageDataList.get(i);
				  
			    serverName[i]=memoryUsageData.getInstanceId();
			    heapUsed[i]=memoryUsageData.getHeapUsed();
			    heapUsedMinVal[i]=memoryUsageData.getHeapUsedMinVal();
			    heapUsedMaxVal[i]=memoryUsageData.getHeapUsedMaxVal();
			    nonHeapUsed[i]=memoryUsageData.getNonHeapUsed();
			    nonHeapMinVal[i]=memoryUsageData.getNonHeapMinVal();
			    nonHeapMaxVal[i]=memoryUsageData.getNonHeapMaxVal();
			    createTime[i]=memoryUsageData.getStrCreateTime();
			}
		  
			liveMemoryUsageData.setServerName(serverName);
			liveMemoryUsageData.setHeapUsed(heapUsed);
			liveMemoryUsageData.setHeapUsedMinVal(heapUsedMinVal);
			liveMemoryUsageData.setHeapUsedMaxVal(heapUsedMaxVal);
			liveMemoryUsageData.setNonHeapUsed(nonHeapUsed);
			liveMemoryUsageData.setNonHeapMinVal(nonHeapMinVal);
			liveMemoryUsageData.setNonHeapMaxVal(nonHeapMaxVal);
			liveMemoryUsageData.setStrCreateTime(createTime);
		}
		return liveMemoryUsageData;
	}
	
	public ActionForward getLiveDetailMemoryDetails(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getLiveDetailMemoryDetails method of "+ getClass().getName());
		try {
				response.setContentType("application/json");
				String serverIds = (request.getParameter("serverIds"));
				if(serverIds != null){
					serverIds=serverIds.trim();
				}
				
				Logger.logDebug(MODULE, "Server Name   :"+serverIds);
				
				DetailMemoryUsageDao detailMemoryUsageDAO = new DefaultJVMDetailMemoryUsageDao();
				List<JVMDetailMemoryUsageData> detailMemoryDataList = detailMemoryUsageDAO.getDetailMemoryUsageDataList(serverIds);
				
				long endTime = System.currentTimeMillis();
				
				JVMDetailUsageData chartData = convertToDetailMemoryChartData(detailMemoryDataList);
				
				List<JVMDetailMemory> jvmDetailMemoryList = new ArrayList<JVMDetailMemory>();
				
				String createTime = "";
				String serverName = "";
				JVMDetailMemory jvmDetailMemory = new JVMDetailMemory();
				for(JVMDetailMemoryUsageData jvmDetailData : detailMemoryDataList){
					if(createTime.equals("")){
						createTime = jvmDetailData.getCreateTime();
						serverName = jvmDetailData.getServerInstance();
						jvmDetailMemory.setCreateTime(createTime);
						jvmDetailMemory.setServerName(serverName);
						
						if(jvmDetailData.getJvmPoolName().equals("PS Eden Space")){
							jvmDetailMemory.setEdenSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setEdenSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setEdenSpanceAvg(jvmDetailData.getSystemLoadAvg());
						}else if(jvmDetailData.getJvmPoolName().equals("PS Survivor Space")){
							jvmDetailMemory.setSurvivorSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setSurvivorSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setSurvivorSpaceAvg(jvmDetailData.getSystemLoadAvg());
						}else if(jvmDetailData.getJvmPoolName().equals("PS Old Gen")){
							jvmDetailMemory.setOldGenSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setOldGenSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setOldGenSpaceAvg(jvmDetailData.getSystemLoadAvg());
						}else if(jvmDetailData.getJvmPoolName().equals("PS Perm Gen")){
							jvmDetailMemory.setPermGenSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setPermGenSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setPermGenSpaceAvg(jvmDetailData.getSystemLoadAvg());
						}
						
					}else if(createTime.equals(jvmDetailData.getCreateTime())){
						
						if(jvmDetailData.getJvmPoolName().equals("PS Eden Space")){
							jvmDetailMemory.setEdenSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setEdenSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setEdenSpanceAvg(jvmDetailData.getSystemLoadAvg());
						}else if(jvmDetailData.getJvmPoolName().equals("PS Survivor Space")){
							jvmDetailMemory.setSurvivorSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setSurvivorSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setSurvivorSpaceAvg(jvmDetailData.getSystemLoadAvg());
						}else if(jvmDetailData.getJvmPoolName().equals("PS Old Gen")){
							jvmDetailMemory.setOldGenSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setOldGenSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setOldGenSpaceAvg(jvmDetailData.getSystemLoadAvg());
						}else if(jvmDetailData.getJvmPoolName().equals("PS Perm Gen")){
							jvmDetailMemory.setPermGenSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setPermGenSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setPermGenSpaceAvg(jvmDetailData.getSystemLoadAvg());
						}
						
					}else if( !createTime.equals(jvmDetailData.getCreateTime())){
						jvmDetailMemoryList.add(jvmDetailMemory);
						jvmDetailMemory = new  JVMDetailMemory();
						createTime = jvmDetailData.getCreateTime();
						jvmDetailMemory.setCreateTime(jvmDetailData.getCreateTime());
						jvmDetailMemory.setServerName(jvmDetailData.getServerInstance());
						
						if(jvmDetailData.getJvmPoolName().equals("PS Eden Space")){
							jvmDetailMemory.setEdenSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setEdenSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setEdenSpanceAvg(jvmDetailData.getSystemLoadAvg());
						}else if(jvmDetailData.getJvmPoolName().equals("PS Survivor Space")){
							jvmDetailMemory.setSurvivorSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setSurvivorSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setSurvivorSpaceAvg(jvmDetailData.getSystemLoadAvg());
						}else if(jvmDetailData.getJvmPoolName().equals("PS Old Gen")){
							jvmDetailMemory.setOldGenSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setOldGenSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setOldGenSpaceAvg(jvmDetailData.getSystemLoadAvg());
						}else if(jvmDetailData.getJvmPoolName().equals("PS Perm Gen")){
							jvmDetailMemory.setPermGenSpaceMax(jvmDetailData.getSystemLoadAvgMax());
							jvmDetailMemory.setPermGenSpaceMin(jvmDetailData.getSystemLoadAvgMin());
							jvmDetailMemory.setPermGenSpaceAvg(jvmDetailData.getSystemLoadAvg());
						}
					}
					
				}
				
				System.out.println("******************************************");
				System.out.println(jvmDetailMemoryList.toString());
				
				if(jvmDetailMemoryList != null){
					String json = new Gson().toJson(jvmDetailMemoryList);
					response.getWriter().write(json.toString());
				}
				return null;
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	private JVMDetailUsageData convertToDetailMemoryChartData(List<JVMDetailMemoryUsageData> detailMemoryDataList) {
		JVMDetailUsageData jvmDetailMemoryUsageData =null;
		
		if(detailMemoryDataList != null && !detailMemoryDataList.isEmpty()){
		  
			jvmDetailMemoryUsageData = new JVMDetailUsageData();
			int size = detailMemoryDataList.size();
			
			String[] serverInstance = new String[size];;
			String[] createTime=new String[size];;
			String[] jvmPoolName =new String[size];;
			Long[] systemLoadAvgMin = new Long[size];
			Long[] systemLoadAvgMax = new Long[size];
			Float[] systemLoadAvg = new Float[size];
		  
			for(int i=0;i<size;i++){
				JVMDetailMemoryUsageData memoryUsageData = detailMemoryDataList.get(i);
				  
				serverInstance[i]=memoryUsageData.getServerInstance();
				createTime[i]=memoryUsageData.getCreateTime();
				jvmPoolName[i]=memoryUsageData.getJvmPoolName();
				systemLoadAvgMin[i]=memoryUsageData.getSystemLoadAvgMin();
				systemLoadAvgMax[i]=memoryUsageData.getSystemLoadAvgMax();
				systemLoadAvg[i]=memoryUsageData.getSystemLoadAvg();
			}
		  
			jvmDetailMemoryUsageData.setServerInstance(serverInstance);
			jvmDetailMemoryUsageData.setCreateTime(createTime);
			jvmDetailMemoryUsageData.setJvmPoolName(jvmPoolName);
			jvmDetailMemoryUsageData.setSystemLoadAvgMin(systemLoadAvgMin);
			jvmDetailMemoryUsageData.setSystemLoadAvgMax(systemLoadAvgMax);
			jvmDetailMemoryUsageData.setSystemLoadAvg(systemLoadAvg);
		}
		return jvmDetailMemoryUsageData;
	}
	
}
