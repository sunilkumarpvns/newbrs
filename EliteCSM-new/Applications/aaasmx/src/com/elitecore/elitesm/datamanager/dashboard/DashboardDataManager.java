package com.elitecore.elitesm.datamanager.dashboard;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.dashboard.category.data.CategoryData;
import com.elitecore.elitesm.datamanager.dashboard.data.DashboardData;
import com.elitecore.elitesm.datamanager.dashboard.data.ManageDashboardData;
import com.elitecore.elitesm.datamanager.dashboard.userrelation.data.DashboardUserRelData;
import com.elitecore.elitesm.web.core.system.dashboardconfiguration.DashboardConfigData;
import com.elitecore.elitesm.web.dashboard.json.TemplateGlobalConfData;
import com.elitecore.elitesm.web.dashboard.json.WidgetData;
import com.elitecore.elitesm.web.dashboard.json.WidgetOrderData;
import com.elitecore.elitesm.web.dashboard.json.WidgetTemplateData;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigData;

public interface DashboardDataManager extends DataManager {

	public void create(DashboardData dashboardData,String staffId) throws DataManagerException;
	
	public PageList search(DashboardData dashboardData,Map infoMap) throws DataManagerException;
	
	public PageList manageSearch(DashboardData dashboardData,Map infoMap) throws DataManagerException;
	
	public void updateIsActive(String isActive,String dashboardId,String staffId) throws DataManagerException;
	
	public void delete(String dashboardId) throws DataManagerException;
	
	public PageList searchDataFromDashboardTable(String dashboardId, Map infoMap) throws DataManagerException;

	public PageList getWidgetData(String dashboardId)  throws DataManagerException;

	public void updatewidgetConfig(WidgetConfigData widgetConfigData)throws DataManagerException ;

	public PageList getWidgetConfigurationData(String widgetId)throws DataManagerException ;

	public PageList getAllCategoriesList(CategoryData categoryData, Map infoMap)throws DataManagerException;

	public Long countElementOfCategory(String categoryId)throws DataManagerException;

	public Long getWidgetOrder(String templateId, String dashboardId)throws DataManagerException;

	public Long addOrderData(WidgetOrderData widgetOrderData)throws DataManagerException;

	public String addNewWidget(WidgetData widgetData)throws DataManagerException;

	public void addWidgetConfig(WidgetConfigData widgetConfigData)throws DataManagerException;

	public String checkWidgetConfigurations(String widgetId)throws DataManagerException;

	public void addWidgetConfiguration(String key, String value,String widgetId)throws DataManagerException;

	public void deleteWidget(String widgetId)throws DataManagerException;

	public void changeWidgetOrder(String widgetId, Long columnId,Long orderNumber)throws DataManagerException;

	public void saveDashboardConfiguration(DashboardConfigData dashboardConfigData)throws DataManagerException ;

	public DashboardConfigData getDashboardConfiguration() throws DataManagerException;

	public List<WidgetTemplateData> getwidgetTemplateDetails() throws DataManagerException;

	public void saveTemplateGlobalConf(TemplateGlobalConfData templateGlobalConfData)throws DataManagerException;

	public void saveCustomWidget(WidgetTemplateData widgetTemplateData)throws DataManagerException;

	public WidgetTemplateData getTemplateDetails(String templateId)throws DataManagerException;

	public void updateCustomWidgetConf(WidgetTemplateData widgetTemplateData)throws DataManagerException;

	public List<TemplateGlobalConfData> fetchTemplateGlobalConfiguration()throws DataManagerException;

	public List<DashboardUserRelData> getUserDashboardList(String staffId)throws DataManagerException;

	public DashboardData getDashboardData(String dashboardId)throws DataManagerException;

	public DashboardUserRelData getUserRelList(String dashboardId, String staffId)throws DataManagerException;

	public String saveUserRelData(ManageDashboardData manageDashboardData)throws DataManagerException;

	public void deleteDashboard(String dashboardId)throws DataManagerException;

	public void updateDashboardDetail(DashboardData dashboardData,IStaffData staffData)throws DataManagerException;

	public Long countMaxDashboard(String staffId)throws DataManagerException;

	public String getCustomWidgetId(String customWidgetName)throws DataManagerException;
	
	public void deleteWidgetConfiguration(String widgetId)throws DataManagerException;

}
