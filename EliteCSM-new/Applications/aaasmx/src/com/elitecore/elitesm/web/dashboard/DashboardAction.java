package com.elitecore.elitesm.web.dashboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.collections.map.HashedMap;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.dashboard.DashboardBLManager;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.dashboard.category.data.CategoryData;
import com.elitecore.elitesm.datamanager.dashboard.data.DashboardData;
import com.elitecore.elitesm.datamanager.dashboard.data.ManageDashboardData;
import com.elitecore.elitesm.datamanager.dashboard.userrelation.data.DashboardUserRelData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.dashboardconfiguration.DashboardConfigData;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.dashboard.categories.WidgetCategories;
import com.elitecore.elitesm.web.dashboard.categories.WidgetCategory;
import com.elitecore.elitesm.web.dashboard.categories.WidgetSubCategories;
import com.elitecore.elitesm.web.dashboard.categories.WidgetSubCategory;
import com.elitecore.elitesm.web.dashboard.form.DashboardForm;
import com.elitecore.elitesm.web.dashboard.json.WidgetData;
import com.elitecore.elitesm.web.dashboard.json.WidgetJSONData;
import com.elitecore.elitesm.web.dashboard.json.WidgetOrderData;
import com.elitecore.elitesm.web.dashboard.json.WidgetTemplateData;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigData;
import com.elitecore.elitesm.ws.logger.Logger;

public class DashboardAction extends BaseDispatchAction {
	private static final String MODULE = DashboardAction.class.getSimpleName();;
	private static final String VIEWDASHBOARDCONTAINER = "viewDashboardContainer";
	private static final String VIEWMANAGEDASHBOARD = "viewManageDashboard";
	private static final String CREATE_FORWARD = "createDashboard";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String INIT_UPDATE_FORWARD="initUpadateDashboard";
	private static final String MANAGE_DASHBOARD_ALIAS=ConfigConstant.MANAGE_DASHBOARD_ACTION;
	private static final String CREATE_DASHBOARD_ALIAS=ConfigConstant.CREATE_DASHBOARD_ACTION;
	private static final String UPDATE_DASHBOARD_ALIAS=ConfigConstant.UPDATE_RADIUS_POLICY_ACTION;
	
	private static final String ADMINISTRATIVE_PERMISSION_ALIAS=ConfigConstant.ADMINISTRATIVE_PERMISSION_ACTION;
	private static final long AUTH_ESI_TYPE=ConfigConstant.AUTH_ESI_TYPE;
	private static final long ACCT_ESI_TYPE=ConfigConstant.ACCT_ESI_TYPE;
	private static final long NAS_ESI_TYPE=ConfigConstant.NAS_ESI_TYPE;
	
	public ActionForward getDashboardData(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called initDashboard");
		try {
			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));

			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo = 1;
			Map infoMap = new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);
			infoMap.put("staffData", staffData);

			DashboardData dashboardData = new DashboardData();
			DashboardForm dashboardForm = (DashboardForm) form;
			dashboardData.setDashboardName("");

			DashboardBLManager blManager = new DashboardBLManager();
			PageList pageList = blManager.search(dashboardData, infoMap);

			getAAAServerList(request,dashboardForm);
			getEsiList(request);
			getDriverList(request,requiredPageNo,pageSize);
			
			List<DashboardData> listOfDashboard = pageList.getListData();
			createJSONFiles(listOfDashboard,request);
			createCategoryJSONFile(infoMap,request);
			
			boolean checkpermission=true;
			try{
				checkActionPermission(request,CREATE_DASHBOARD_ALIAS);
			}catch(ActionNotPermitedException e){
				checkpermission=false;
			}
			dashboardForm.setAccessGroupId(checkpermission);
			
			List<DashboardUserRelData> dashboardUserRelDataList=blManager.getUserDashboardList(staffData.getStaffId());
			List<DashboardData> userwiseDashboardList = new ArrayList<DashboardData>();
			
			Collections.sort(dashboardUserRelDataList,new DashboardUserRelData());
			
			if((listOfDashboard != null && listOfDashboard.size() > 0) && (dashboardUserRelDataList !=null && dashboardUserRelDataList.size() > 0)){
			for(DashboardUserRelData dashboardRelDataObj:dashboardUserRelDataList){
					for(DashboardData dashboardDataObj:listOfDashboard){
						if(dashboardRelDataObj.getDashboardId().equals(dashboardDataObj.getDashboardId()) && dashboardRelDataObj.getIsActive().equals("A")){
							userwiseDashboardList.add(dashboardDataObj);
						}
					}
				}
			}
			
			dashboardForm.setDashboardList(userwiseDashboardList);
			request.setAttribute("dashboardForm", dashboardForm);
			
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE,"Error while getting Database connection. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting user widgets information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return mapping.findForward(VIEWDASHBOARDCONTAINER);
	}

	private void createCategoryJSONFile(Map infoMap,HttpServletRequest request) throws DataManagerException {
		try{
			//get categories & create JSON File
			DashboardBLManager blManager = new DashboardBLManager();
			CategoryData categoryData=new CategoryData();
			categoryData.setCategoryName("");
			PageList categoryPageList=blManager.getAllCategories(categoryData,infoMap);
			WidgetCategories widgetCategories = new WidgetCategories();
			List<WidgetCategory> lstWidgetCategories = new ArrayList<WidgetCategory>();
			List<CategoryData> listOfCategories = categoryPageList.getListData();
			boolean found = false;
			
			//category json file path
			String jsonConfigFilePath = EliteUtility.getSMHome()+ ConfigConstant.JSONCONFIGFILEPATH;
			
			for (CategoryData categoryDataDetails : listOfCategories) {
					WidgetCategory widgetCategory=new WidgetCategory();
	
					Long countCategoryElement=blManager.countElementOfCategory(categoryDataDetails.getCategoryId());
					
					String categoryId = categoryDataDetails.getCategoryId();
					widgetCategory.setId(categoryId);
					widgetCategory.setAmount(countCategoryElement);
					widgetCategory.setTitle(categoryDataDetails.getCategoryName());
					widgetCategory.setUrl(categoryDataDetails.getJsonURL());
					
					lstWidgetCategories.add(widgetCategory);
					found=true;
			}
		
			//create category JSON file dynamically
			if (found == true) {
				widgetCategories.setCategory(lstWidgetCategories);
				JSONObject jsonObject = new JSONObject();
				jsonObject = JSONObject.fromObject(widgetCategories);
	
				JSONObject responseDetailsJson = new JSONObject();
	
				responseDetailsJson.put("categories", jsonObject.toString());
	
				try {
					FileWriter jsonFileWriter = new FileWriter(jsonConfigFilePath+"widgetcategories.json");
					
					jsonFileWriter.write(responseDetailsJson.toString());
					jsonFileWriter.flush();
					jsonFileWriter.close();
	
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
		
			WidgetSubCategories widgetSubCategories = new WidgetSubCategories();
			
			for (CategoryData categoryDataDetails : listOfCategories) {
				boolean subCatFound=false;
				Set<WidgetTemplateData> widgteTemplateDataSet = categoryDataDetails.getWidgetTemplateData();
				List<WidgetSubCategory> lstWidgetSubCategory = new ArrayList<WidgetSubCategory>();
				
				for(WidgetTemplateData widgetTemplateData:widgteTemplateDataSet){
					WidgetSubCategory widgetSubCategory=new WidgetSubCategory();
					
					widgetSubCategory.setId(widgetTemplateData.getWidgteTemplateId());
					widgetSubCategory.setTitle(widgetTemplateData.getTitle());
					widgetSubCategory.setDescription(widgetTemplateData.getDescription());
					widgetSubCategory.setCreator("EliteCSM");
					widgetSubCategory.setUrl(widgetTemplateData.getJspUrl());
					widgetSubCategory.setImage(widgetTemplateData.getThumbnail());
					widgetSubCategory.setEditurl(widgetTemplateData.getConfigJspUrl());
					
					lstWidgetSubCategory.add(widgetSubCategory);
					subCatFound=true;
				}
				
				//create sub category json dynamically
				if(subCatFound == true){
					widgetSubCategories.setData(lstWidgetSubCategory);
	
					JSONObject jsonObject = new JSONObject();
					jsonObject = JSONObject.fromObject(widgetSubCategories);
	
					JSONObject responseDetailsJson = new JSONObject();
	
					responseDetailsJson.put("result", jsonObject.toString());
	
					try {
						FileWriter jsonFileWriter = new FileWriter(jsonConfigFilePath+ categoryDataDetails.getCategoryName()+ ".json");
						jsonFileWriter.write(responseDetailsJson.toString());
						jsonFileWriter.flush();
						jsonFileWriter.close();
	
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			}catch(Exception e){
				Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessage message = new ActionMessage("general.error");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
			}
	}

	private void createJSONFiles(List<DashboardData> listOfDashboard,HttpServletRequest request) {
		try{
			
			String jsonFilePath = EliteUtility.getSMHome()+ ConfigConstant.JSONFILEPATH;

			for (DashboardData dashboardDataDetails : listOfDashboard) {

				Set<WidgetData> widgteDataSet = dashboardDataDetails.getWidgetSet();
				WidgetClientDashboard widgetDashboard = new WidgetClientDashboard();
				WidgetTemplateData widgetTemplatedata = new WidgetTemplateData();
				WidgetOrderData widgetOrderData = new WidgetOrderData();
				List<WidgetJSONData> lstWidgetJsonData = new ArrayList<WidgetJSONData>();
				List<WidgetData> widgetDataList=new ArrayList<WidgetData>();
				boolean found = false;
				String widgetId = null;
				
				for(WidgetData widgetData:widgteDataSet){
					widgetDataList.add(widgetData);
				}
				
				//sort data by order
				Collections.sort(widgetDataList,new WidgetData());
				
				for (WidgetData widgetData : widgetDataList) {

					WidgetJSONData widgetJsonData = new WidgetJSONData();
					widgetTemplatedata = widgetData.getWidgteTemplateData();
					widgetOrderData = widgetData.getWidgetOrderData();
					List<WidgetConfigData> widgetConfigDatas= widgetData.getWidgetConfigDataList();
					String widgetName="";
					if(widgetConfigDatas != null && widgetConfigDatas.size() > 0){
						for(WidgetConfigData widgetConfigData:widgetConfigDatas){
							if(widgetConfigData.getParameterKey().equals("NAME")){
								widgetName=widgetConfigData.getParameterValue();
							}
						}
					}
					
					
					widgetId = widgetData.getWidgetId();
					widgetJsonData.setId(widgetId);
					widgetJsonData.setEditurl(widgetTemplatedata.getConfigJspUrl());
					widgetJsonData.setUrl(widgetTemplatedata.getJspUrl());
					widgetJsonData.setOpen("true");
					if(widgetName != ""){
						widgetJsonData.setTitle(widgetName);
					}else{
						widgetJsonData.setTitle(widgetTemplatedata.getTitle());
					}
					
					if(widgetOrderData.getColumnNumber() != null){
						Long columnNumber=widgetOrderData.getColumnNumber();
						if(columnNumber == 1L){
							widgetJsonData.setColumn("first");
						}else{
							widgetJsonData.setColumn("second");
						}
					}

					lstWidgetJsonData.add(widgetJsonData);

					found = true;
				}

				//create dashboard JSON file dynamically
				if (found == true) {
					widgetDashboard.setData(lstWidgetJsonData);

					JSONObject jsonObject = new JSONObject();
					jsonObject = JSONObject.fromObject(widgetDashboard);
					
					JSONObject responseDetailsJson = new JSONObject();
					
					responseDetailsJson.put("result", jsonObject.toString());

					File isFileExist = new File(jsonFilePath+ dashboardDataDetails.getDashboardName()+ ".json");
					isFileExist.delete();
					
					try {
						FileWriter jsonFileWriter = new FileWriter(jsonFilePath+ dashboardDataDetails.getDashboardName()+ ".json");
						jsonFileWriter.write(responseDetailsJson.toString());
						jsonFileWriter.flush();
						jsonFileWriter.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					File isFileExist = new File(jsonFilePath+ dashboardDataDetails.getDashboardName()+ ".json");
					isFileExist.delete();
				}
			}
		}catch(Exception e){
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	}





	private void getDriverList(HttpServletRequest request, int requiredPageNo,Integer pageSize) throws DataManagerException {
		try {
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
			DriverBLManager driverBLManager = new DriverBLManager();
			DriverInstanceData driverInstanceData = new DriverInstanceData();
			ESITypeAndInstanceData esiInstanceData = new ESITypeAndInstanceData();
			driverInstanceData.setName("");
			PageList esiPageList = esiBLManager.searchESIInstance(esiInstanceData, requiredPageNo, pageSize,staffData);
			PageList pageList1 = driverBLManager.search(driverInstanceData, staffData, requiredPageNo, pageSize);
			List radiusESIPageList = esiBLManager.getESIInstanceAndTypeDetails("", AUTH_ESI_TYPE);
			List radiusAcctESIPageList = esiBLManager.getESIInstanceAndTypeDetails("", ACCT_ESI_TYPE);
			List radiusNASESIPageList = esiBLManager.getESIInstanceAndTypeDetails("", NAS_ESI_TYPE);
			
			request.getSession().setAttribute("driverList",pageList1.getListData());
			request.getSession().setAttribute("esiPageList",esiPageList.getListData());
			request.getSession().setAttribute("radESIList", radiusESIPageList);
			request.getSession().setAttribute("radiusAcctESIPageList", radiusAcctESIPageList);
			request.getSession().setAttribute("radiusNASESIPageList", radiusNASESIPageList);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	}





	private void getEsiList(HttpServletRequest request) throws DataManagerException {
		try {
			ExternalSystemInterfaceBLManager esiBLManager = new ExternalSystemInterfaceBLManager();
			ESITypeAndInstanceData esiInstanceData = new ESITypeAndInstanceData();
			esiInstanceData.setName("");
			List<ExternalSystemInterfaceTypeData> esiTypeList = esiBLManager.getListOfESIType();
			request.getSession().setAttribute("esiList", esiTypeList);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	}


	private void getAAAServerList(HttpServletRequest request,DashboardForm dashboardForm) throws DataManagerException {
		try {
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			List<NetServerInstanceData> allServerList = netServerBLManager.getNetServerInstanceList();
			List<NetServerInstanceData> aaaServerList = netServerBLManager.getAAAServerInstanceList();
	
			dashboardForm.setListServer(aaaServerList);
			
			request.getSession().setAttribute("serverAAAList",aaaServerList);
			request.getSession().setAttribute("allServerList",allServerList);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
	}


	public ActionForward initCreate(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered initCreate method of "+ getClass().getName());
		try {
			// checkActionPermission(request, CREATE_ACTION_ALIAS);
			DashboardData dashboardData = new DashboardData();
			DashboardForm dashboardForm = (DashboardForm) form;
			dashboardData.setDashboardName("");

			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));

			DashboardBLManager blManager = new DashboardBLManager();

			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo = 1;
			Map infoMap = new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);
			infoMap.put("staffData", staffData);

			PageList pageList = blManager.search(dashboardData, infoMap);

			dashboardForm.setDashboardList(pageList.getListData());

			AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();

			List accessGroupList = accessGroupBLManager.getAccessGroupDetailsList();
			dashboardForm.setListAccessGroup(accessGroupList);

			StaffBLManager staffBLmanager = new StaffBLManager();
			List staffList = staffBLmanager.getList();
			dashboardForm.setListSearchStaff(staffList);

			request.setAttribute("dashboardForm", dashboardForm);
			return mapping.findForward(CREATE_FORWARD);
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

	public ActionForward create(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered create method of "+ getClass().getName());
		if((checkAccess(request, CREATE_DASHBOARD_ALIAS)) || (checkAccess(request, ADMINISTRATIVE_PERMISSION_ALIAS))){
		try {
			DashboardForm dashboardForm = (DashboardForm) form;
			DashboardData dashboardData = new DashboardData();

			String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			dashboardForm.setAuthor(currentUser);

			convertFormToBean(dashboardForm, dashboardData);

			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			DashboardBLManager dashboardBLManager = new DashboardBLManager();
			dashboardBLManager.create(dashboardData, staffData);

			request.setAttribute("responseUrl","/dashboard.do?method=getDashboardData");
			ActionMessage message = new ActionMessage("dashboard.create.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveMessages(request, messages);
			return mapping.findForward(SUCCESS_FORWARD);

		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("dashboard.create.failure");
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

	private void convertFormToBean(DashboardForm dashboardForm,
		DashboardData dashboardData) throws IOException {
		dashboardData.setDashboardId(dashboardForm.getDashboardId());
		dashboardData.setDashboardName(dashboardForm.getDashboardName());
		dashboardData.setDashboardDesc(dashboardForm.getDashboardDesc());
		dashboardData.setStartFrom(dashboardForm.getStartFrom());
		dashboardData.setAddShares(dashboardForm.getAddShares());
		dashboardData.setAuthor(dashboardForm.getAuthor());
	}

	public ActionForward initManage(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logTrace(MODULE,"Enter the Init Manage method of :"+getClass().getName());
		if((checkAccess(request, MANAGE_DASHBOARD_ALIAS))){
		try {
			String userId = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));

			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo = 1;
			Map infoMap = new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);
			infoMap.put("staffData", staffData);

			DashboardData dashboardData = new DashboardData();
			DashboardForm dashboardForm = (DashboardForm) form;
			dashboardData.setDashboardName("");

			DashboardBLManager blManager = new DashboardBLManager();
			PageList pageList = blManager.manageSearch(dashboardData, infoMap);

			AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();

			List accessGroupList = accessGroupBLManager.getAccessGroupDetailsList();
			dashboardForm.setListAccessGroup(accessGroupList);

			StaffBLManager staffBLmanager = new StaffBLManager();
			List staffList = staffBLmanager.getList();
			dashboardForm.setListSearchStaff(staffList);
			
			boolean checkpermission=true;
			try{
				checkActionPermission(request,ADMINISTRATIVE_PERMISSION_ALIAS);
			}catch(ActionNotPermitedException e){
				checkpermission=false;
			}
			dashboardForm.setManageAccessGroupId(checkpermission);
			
			List<DashboardData> dashboardDataList=pageList.getListData();
			List<IGroupData> lstGroupData=new ArrayList<IGroupData>();
			
			for(DashboardData data:dashboardDataList){
				boolean checkGroupId=isInteger(data.getAddShares());
				if(checkGroupId){
					String groupName=accessGroupBLManager.getAccessGroupName(data.getAddShares());
					if(groupName != null){
						data.setAddShares(groupName);
					}
				}
			}
			
			
			List<DashboardUserRelData> dashboardUserRelList=blManager.getUserDashboardList(staffData.getStaffId());
			List<ManageDashboardData> manageDashboardList = new ArrayList<ManageDashboardData>();
			
			Collections.sort(dashboardUserRelList,new DashboardUserRelData());
			
			if(dashboardUserRelList != null && dashboardUserRelList.size() > 0){
				if(dashboardDataList != null && dashboardDataList.size() > 0){
					for(DashboardData data:dashboardDataList){
						ManageDashboardData manageDashboardData= new ManageDashboardData();
						
						DashboardUserRelData dashboardUserRelData=blManager.getUserRelList(data.getDashboardId(),staffData.getStaffId());
						if(dashboardUserRelData != null){
							if(dashboardUserRelData.getIsActive().equals("A")){
								manageDashboardData.setIsActive("A");
								
								manageDashboardData.setDashboardRelId(dashboardUserRelData.getDashboardRelId());
								manageDashboardData.setDashboardId(data.getDashboardId());
								manageDashboardData.setAddShares(data.getAddShares());
								manageDashboardData.setAuthor(data.getAuthor());
								manageDashboardData.setDashboardName(data.getDashboardName());
								manageDashboardData.setOrderNumber(dashboardUserRelData.getOrderNumber());
								
								manageDashboardList.add(manageDashboardData);
								
							}else{
								manageDashboardData.setIsActive("I");
								
								manageDashboardData.setDashboardRelId(dashboardUserRelData.getDashboardRelId());
								manageDashboardData.setDashboardId(data.getDashboardId());
								manageDashboardData.setAddShares(data.getAddShares());
								manageDashboardData.setAuthor(data.getAuthor());
								manageDashboardData.setDashboardName(data.getDashboardName());
								manageDashboardData.setOrderNumber(dashboardUserRelData.getOrderNumber());
								
								manageDashboardList.add(manageDashboardData);
							}
						}
					}
				}
			}else{
				for(DashboardData data:dashboardDataList){
					ManageDashboardData manageDashboardData= new ManageDashboardData();
				
					manageDashboardData.setIsActive("I");
					//FIX ME :: get manage id here...
					manageDashboardData.setDashboardId(data.getDashboardId());
					manageDashboardData.setAddShares(data.getAddShares());
					manageDashboardData.setAuthor(data.getAuthor());
					manageDashboardData.setDashboardName(data.getDashboardName());
					manageDashboardData.setOrderNumber(data.getOrderNumber());
					manageDashboardData.setStaffId(staffData.getStaffId());
					
					String userRelId=blManager.saveUserRelData(manageDashboardData);
					
					manageDashboardData.setDashboardRelId(userRelId);
					manageDashboardList.add(manageDashboardData);
				}
			}
			Collections.sort(manageDashboardList,new ManageDashboardData());
			
			dashboardForm.setDashboardList(dashboardDataList);
			dashboardForm.setDashboardDataList(manageDashboardList);
			request.setAttribute("dashboardForm", dashboardForm);
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE,"Error while getting Database connection. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting user widgets information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return mapping.findForward(VIEWMANAGEDASHBOARD);
	}else{
        Logger.logError(MODULE, "Error during Data Manager operation ");
        ActionMessage message = new ActionMessage("general.user.restricted");
        ActionMessages messages = new ActionMessages();
        messages.add("information", message);
        saveErrors(request, messages);
		
		return mapping.findForward(INVALID_ACCESS_FORWARD);
	}
	}
	
	public ActionForward tableWiseDashboard(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called initDashboard");
		try {
			String userId = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));


			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo = 1;
			Map infoMap = new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);
			infoMap.put("staffData", staffData);

			DashboardData dashboardData = new DashboardData();
			DashboardForm dashboardForm = (DashboardForm) form;
			dashboardData.setDashboardName("");

			DashboardBLManager blManager = new DashboardBLManager();
			PageList pageList = blManager.searchDataFromDashboardTable(dashboardData.getDashboardId(), infoMap);
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			List<NetServerInstanceData> serverList = netServerBLManager.getNetServerInstanceList();

			dashboardForm.setListServer(serverList);

			List listOfDashboard = pageList.getListData();
			
			boolean checkpermission=true;
			try{
				checkActionPermission(request,ADMINISTRATIVE_PERMISSION_ALIAS);
			}catch(ActionNotPermitedException e){
				checkpermission=false;
			}
			dashboardForm.setManageAccessGroupId(checkpermission);
			
			//check permission for User
			checkpermission=true;
			try{
				checkActionPermission(request,CREATE_DASHBOARD_ALIAS);
			}catch(ActionNotPermitedException e){
				checkpermission=false;
			}
			dashboardForm.setAccessGroupId(checkpermission);
			
			List<DashboardData> dashboardList = pageList.getListData();
			List<DashboardUserRelData> dashboardUserRelDataList=blManager.getUserDashboardList(staffData.getStaffId());
			List<DashboardData> userwiseDashboardList = new ArrayList<DashboardData>();
			
			Collections.sort(dashboardUserRelDataList,new DashboardUserRelData());
			
			if((listOfDashboard != null && listOfDashboard.size() > 0) && (dashboardUserRelDataList !=null && dashboardUserRelDataList.size() > 0)){
			for(DashboardUserRelData dashboardRelDataObj:dashboardUserRelDataList){
					for(DashboardData dashboardDataObj:dashboardList){
						if(dashboardRelDataObj.getDashboardId().equals(dashboardDataObj.getDashboardId()) && dashboardRelDataObj.getIsActive().equals("A")){
							userwiseDashboardList.add(dashboardDataObj);
						}
					}
				}
			}
			
			dashboardForm.setDashboardList(userwiseDashboardList);
			request.setAttribute("dashboardForm", dashboardForm);
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE,"Error while getting Database connection. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting user widgets information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return mapping.findForward(VIEWDASHBOARDCONTAINER);
	}
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	public ActionForward activeInActiveDashboard(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called initDashboard");
		try {
			String userId = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			String isActive = (request.getParameter("isActive")).trim();
			String dashboardId = (request.getParameter("dashboardId")).trim();
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			StaffBLManager staffBLManager = new StaffBLManager();
			DashboardBLManager blManager = new DashboardBLManager();
			
			if(isActive != null && dashboardId != null){
				blManager.makeActiveInActive(isActive, dashboardId, userId);
			}
		
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE,"Error while getting Database connection. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting user widgets information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	
	public ActionForward isDSConfigured(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called isDSConfigured");
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			DashboardBLManager blManager = new DashboardBLManager();
			
			DashboardConfigData dashboardConfigData = blManager.getDashboardConfiguration();
			
			if(dashboardConfigData == null){
				out.print(false);
			}else{
				if(Strings.isNullOrBlank(dashboardConfigData.getDatabaseId()) == false){
					out.print(true);
				}
			}
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE,"Error while getting Database connection. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting user widgets information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	
	public ActionForward closeDashboard(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called closeDashboard");
		try {
			String userId = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			String isActive = (request.getParameter("isActive")).trim();
			String dashboardId = (request.getParameter("dashboardId")).trim();
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			DashboardBLManager blManager = new DashboardBLManager();
			
			if(isActive != null && dashboardId != null){
				blManager.makeActiveInActive(isActive, dashboardId, userId);
			}
			out.print("deleted");
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE,"Error while getting Database connection. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting user widgets information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	
	public ActionForward deleteDashboard(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called deleteDashboard");
		try {
			String dashboardId = (request.getParameter("dashboardIds")).trim();
			response.setContentType("text/plain");
			DashboardBLManager blManager = new DashboardBLManager();
			
			String[] dashboardIdsArray=dashboardId.split(",");
			
			for(String strDashboardId : dashboardIdsArray){
				DashboardData dashboardData = blManager.getDashboardData(strDashboardId);
				
				if(dashboardId != null ){
					blManager.deleteDashboard(strDashboardId);
				}
				
				String jsonFilePath = EliteUtility.getSMHome()+ ConfigConstant.JSONFILEPATH;
				
				File isFileExist = new File(jsonFilePath+ dashboardData.getDashboardName()+ ".json");
				isFileExist.delete();
				
			}
			
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE,"Error while getting Database connection. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting user widgets information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	
	
	public ActionForward initUpdateDashboardData(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called deleteDashboard");
		try {
			String userId = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			String dashboardId = (request.getParameter("dashboardId")).trim();
			
			String dashboardIds = null;
			if( Strings.isNullOrEmpty(dashboardId) == false){
				dashboardIds = dashboardId;
			}
			
			DashboardForm dashboardForm = (DashboardForm) form;
			DashboardData dashboardData = new DashboardData();
			dashboardData.setDashboardName("");
			
			DashboardBLManager blManager = new DashboardBLManager();
			
			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			
			//fetching created dashboard list
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo = 1;
			Map infoMap = new HashedMap();
			infoMap.put("pageNo", requiredPageNo);
			infoMap.put("pageSize", pageSize);
			infoMap.put("staffData", staffData);
			
			PageList pageList = blManager.search(dashboardData, infoMap);
		
			dashboardData = blManager.getDashboardData(dashboardIds);
			convertBeanToFromForDashboard(dashboardData,dashboardForm);
			
			dashboardForm.setDashboardList(pageList.getListData());

			//feting access group data
			AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();

			List accessGroupList = accessGroupBLManager.getAccessGroupDetailsList();
			dashboardForm.setListAccessGroup(accessGroupList);

			StaffBLManager staffBLmanager = new StaffBLManager();
			List staffList = staffBLmanager.getList();
			dashboardForm.setListSearchStaff(staffList);

			request.setAttribute("dashboardForm", dashboardForm);
			return mapping.findForward(INIT_UPDATE_FORWARD);
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE,"Error while getting Database connection. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting user widgets information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}

	private void convertBeanToFromForDashboard(DashboardData dashboardData,DashboardForm dashboardForm) {
		dashboardForm.setDashboardId(dashboardData.getDashboardId());
		dashboardForm.setDashboardName(dashboardData.getDashboardName());
		dashboardForm.setDashboardDesc(dashboardData.getDashboardDesc());
		dashboardForm.setAuthor(dashboardData.getAuthor());
		dashboardForm.setAddShares(dashboardData.getAddShares());
		dashboardForm.setStartFrom(dashboardData.getStartFrom());
	}
	
	public ActionForward updateDashboard(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered create method of "+ getClass().getName());
		if((checkAccess(request, UPDATE_DASHBOARD_ALIAS)) || (checkAccess(request, ADMINISTRATIVE_PERMISSION_ALIAS))){
		try {
			DashboardForm dashboardForm = (DashboardForm) form;
			DashboardData dashboardData = new DashboardData();

			String currentUser = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
			dashboardForm.setAuthor(currentUser);

			convertFormToBean(dashboardForm, dashboardData);

			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			DashboardBLManager dashboardBLManager = new DashboardBLManager();
			dashboardBLManager.updateDashboard(dashboardData, staffData);

			request.setAttribute("responseUrl","/dashboard.do?method=initManage");
			ActionMessage message = new ActionMessage("dashboard.update.success");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveMessages(request, messages);
			return mapping.findForward(SUCCESS_FORWARD);

		} catch (ActionNotPermitedException e) {
			Logger.logError(MODULE, "Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
		} catch (Exception exp) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("dashboard.create.failure");
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
	
	public ActionForward checkDashboardLimit(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called checkDashboardLimit");
		try {
			String userId = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getUserId();
			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			
			Long countMaxDashboard = 0L;
			DashboardBLManager blManager = new DashboardBLManager();
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			
			countMaxDashboard = blManager.countMaxDashboard(staffData.getStaffId());
			
			DashboardConfigData dashboardConfigData=blManager.getDashboardConfiguration();
		
			if(dashboardConfigData.getMaxTabs() != null){
				if(countMaxDashboard >= dashboardConfigData.getMaxTabs()){
					out.print(true);
				}else{
					out.print(false);
				}
			}
		} catch (DatabaseConnectionException e) {
			Logger.logError(MODULE,"Error while getting Database connection. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting user widgets information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	
}
