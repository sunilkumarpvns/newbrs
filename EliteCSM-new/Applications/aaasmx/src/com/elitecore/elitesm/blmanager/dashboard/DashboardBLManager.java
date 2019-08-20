package com.elitecore.elitesm.blmanager.dashboard;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.dashboard.DashboardDataManager;
import com.elitecore.elitesm.datamanager.dashboard.category.data.CategoryData;
import com.elitecore.elitesm.datamanager.dashboard.data.DashboardData;
import com.elitecore.elitesm.datamanager.dashboard.data.ManageDashboardData;
import com.elitecore.elitesm.datamanager.dashboard.userrelation.data.DashboardUserRelData;
import com.elitecore.elitesm.datamanager.datasource.database.DatabaseDSDataManager;
import com.elitecore.elitesm.web.core.system.dashboardconfiguration.DashboardConfigData;
import com.elitecore.elitesm.web.dashboard.json.TemplateGlobalConfData;
import com.elitecore.elitesm.web.dashboard.json.WidgetData;
import com.elitecore.elitesm.web.dashboard.json.WidgetOrderData;
import com.elitecore.elitesm.web.dashboard.json.WidgetTemplateData;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigData;

public class DashboardBLManager extends BaseBLManager{

	public PageList search(DashboardData dashboardData, Map infoMap) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			PageList lstServerCertificateList;

			if(dashboardDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			lstServerCertificateList=dashboardDataManager.search(dashboardData, infoMap);
			session.commit();
			return lstServerCertificateList;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public PageList manageSearch(DashboardData dashboardData, Map infoMap) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			PageList lstServerCertificateList;

			if(dashboardDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			lstServerCertificateList=dashboardDataManager.manageSearch(dashboardData, infoMap);
			session.commit();
			return lstServerCertificateList;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}
	
	public void makeActiveInActive(String isActive,String dashboardId,String staffId) throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data manager implementation not found for "+getClass().getName());				
			}
			
			session.beginTransaction();
			dashboardDataManager.updateIsActive(isActive,dashboardId,staffId);
			session.commit();
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("ACtion failed : "+exp.getMessage(),exp);			
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed  : "+e.getMessage());			
		} finally {
			closeSession(session);
		}
	}
	
	public void create(DashboardData dashboardData, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.create(dashboardData,staffData.getStaffId());
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public void delete(String dashboardId)throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Managerimplementation not found for "+getClass().getName());			
			}
			
		
			session.beginTransaction();
			if(dashboardId!=null){
				dashboardDataManager.delete(dashboardId);
			}else{
				throw new DataManagerException("Data manager implementation not found for");
			}
			session.commit();
		}catch(ConstraintViolationException cve){
			session.rollback();
			throw cve;
		}catch(Exception exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage());
		}finally{
			closeSession(session);
		}
	}
	
	public PageList searchDataFromDashboardTable(String dashboardId, Map infoMap) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			PageList lstServerCertificateList;

			if(dashboardDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			lstServerCertificateList=dashboardDataManager.searchDataFromDashboardTable(dashboardId, infoMap);
			return lstServerCertificateList;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			closeSession(session);
		}
	}

	
	public DashboardDataManager getDashboardDataManager(IDataManagerSession session)throws Exception{
		DashboardDataManager dashboardDatamanager=(DashboardDataManager)DataManagerFactory.getInstance().getDataManager(DashboardDataManager.class, session);
		return dashboardDatamanager;
	}

	public PageList getWidgteData(String dashboardId) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			PageList lstServerCertificateList;

			if(dashboardDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			lstServerCertificateList=dashboardDataManager.getWidgetData(dashboardId);
			session.commit();
			return lstServerCertificateList;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			closeSession(session);
		}
	}

	public void updateWidgetConfig(WidgetConfigData widgetConfigData) throws DataManagerException, Exception {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);

			if(dashboardDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			
			 if (widgetConfigData != null && widgetConfigData != null) {
				    dashboardDataManager.updatewidgetConfig(widgetConfigData);
	                session.commit();
	         } else {
	                session.rollback();
	                throw new DataManagerException("Data Manager List of Ids are null" + getClass().getName());
	         }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeSession(session);
		}
		
	}

	public PageList getAllConfigurationList(String widgetId) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			PageList lstwidgetConfigurationList=null;

			if(dashboardDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			lstwidgetConfigurationList=dashboardDataManager.getWidgetConfigurationData(widgetId);
			return lstwidgetConfigurationList;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			closeSession(session);
		}
	}

	public PageList getAllCategories(CategoryData categoryData, Map infoMap) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			PageList categoriesList;

			if(dashboardDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			categoriesList=dashboardDataManager.getAllCategoriesList(categoryData, infoMap);
			return categoriesList;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			closeSession(session);
		}
	}

	public Long countElementOfCategory(String categoryId) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			Long countCategory;

			if(dashboardDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			countCategory=dashboardDataManager.countElementOfCategory(categoryId);
			return countCategory;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public Long getWidgetOrder(String templateId, String dashboardId) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			Long orderId;

			if(dashboardDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			orderId=dashboardDataManager.getWidgetOrder(templateId,dashboardId);
			return orderId;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public Long addOrderData(WidgetOrderData widgetOrderData) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		Long nextOrderId;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			session.beginTransaction();
			nextOrderId=dashboardDataManager.addOrderData(widgetOrderData);
			session.commit();
			return nextOrderId;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public String addNewWidget(WidgetData widgetData) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		String nextWidgetId = null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			nextWidgetId=dashboardDataManager.addNewWidget(widgetData);
			session.commit();
			return nextWidgetId;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public void addWidgetConfig(WidgetConfigData widgetConfigData) throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		Long nextWidgetId;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.addWidgetConfig(widgetConfigData);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public String checkWidgetConfigurations(String widgetId) throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		String isExist;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			isExist=dashboardDataManager.checkWidgetConfigurations(widgetId);
			session.commit();
			return isExist;
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			closeSession(session);
		}
	}

	public void saveWidgetConfig(String key, String value,String widgetId) throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.addWidgetConfiguration(key,value,widgetId);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			closeSession(session);
		}
	}
	
	public void deleteWidgetConfiguration(String widgetId) throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.deleteWidgetConfiguration(widgetId);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			closeSession(session);
		}
	}
	

	public void deleteWidget(String widgetId) throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.deleteWidget(widgetId);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public void changeWidgetOrder(String widgetId, Long columnId,Long orderNumber) throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.changeWidgetOrder(widgetId,columnId,orderNumber);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public void saveDashboardConfiguration(DashboardConfigData dashboardConfigData, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.saveDashboardConfiguration(dashboardConfigData);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}
	
	public DashboardConfigData getDashboardConfiguration() throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		DashboardConfigData dashboardConfigData=null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardConfigData=dashboardDataManager.getDashboardConfiguration();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
		return dashboardConfigData;
	}
	
	public List<WidgetTemplateData> getwidgetTemplateDetails() throws DataManagerException {
		 IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		 List<WidgetTemplateData> widgetTemplateList=null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			widgetTemplateList=dashboardDataManager.getwidgetTemplateDetails();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
		return widgetTemplateList;
	}

	public void saveTemplateGlobalConf(TemplateGlobalConfData templateGlobalConfData) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.saveTemplateGlobalConf(templateGlobalConfData);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public void saveCustomWidget(WidgetTemplateData widgetTemplateData,IStaffData staffData)throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.saveCustomWidget(widgetTemplateData);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public WidgetTemplateData getTemplateDetails(String templateId) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		WidgetTemplateData widgetTemplateData=null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			widgetTemplateData=dashboardDataManager.getTemplateDetails(templateId);
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
		return widgetTemplateData;
	}

	public void updateCustomWidgetConf(WidgetTemplateData widgetTemplateData) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.updateCustomWidgetConf(widgetTemplateData);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
	}

	public List<TemplateGlobalConfData> fetchTemplateGlobalConfiguration() throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<TemplateGlobalConfData>  templateGlobalConfDataList=null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			session.beginTransaction();
			templateGlobalConfDataList=dashboardDataManager.fetchTemplateGlobalConfiguration();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
		return templateGlobalConfDataList;
	}

	public List<DashboardUserRelData> getUserDashboardList(String staffId) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		List<DashboardUserRelData>  dashboardUserRelData=null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			session.beginTransaction();
			dashboardUserRelData=dashboardDataManager.getUserDashboardList(staffId);
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
		return dashboardUserRelData;
	}
	
	public DashboardData getDashboardData(String dashboardId) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		DashboardData dashboardData=null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardData=dashboardDataManager.getDashboardData(dashboardId);
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally {
			closeSession(session);
		}
		return dashboardData;
	}

	public DashboardUserRelData getUserRelList(String dashboardId,String staffId)  throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		DashboardUserRelData dashboardUserRelData=null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardUserRelData=dashboardDataManager.getUserRelList(dashboardId,staffId);
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
		return dashboardUserRelData;
	}

	public String saveUserRelData(ManageDashboardData manageDashboardData)  throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		String userRelId=null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			userRelId=dashboardDataManager.saveUserRelData(manageDashboardData);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		} finally {
			closeSession(session);
		}
		return userRelId;
	}

	public void deleteDashboard(String dashboardId) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			dashboardDataManager.deleteDashboard(dashboardId);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			closeSession(session);
		}
	}

	public void updateDashboard(DashboardData dashboardData,IStaffData staffData) throws Exception {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
		if(dashboardDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for DatabaseDSDataManager / SystemAuditDataManager");
		}
		try{
			session.beginTransaction();

			dashboardDataManager.updateDashboardDetail(dashboardData,staffData);
		
			session.commit();
		}catch(DuplicateInstanceNameFoundException e){
			session.rollback();
			throw new DuplicateInstanceNameFoundException("Action failed :"+e.getMessage());	
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}

	public Long countMaxDashboard(String staffId) throws Exception {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
		Long countDashboard = null;
		if(dashboardDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for DatabaseDSDataManager / SystemAuditDataManager");
		}
		try{
			session.beginTransaction();

			countDashboard = dashboardDataManager.countMaxDashboard(staffId);
		
			session.commit();
		}catch(DuplicateInstanceNameFoundException e){
			session.rollback();
			throw new DuplicateInstanceNameFoundException("Action failed :"+e.getMessage());	
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return countDashboard;
		
	}

	public String getCustomWidgetId(String customWidgetName) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		String customWidgetId=null;
		try{
			DashboardDataManager dashboardDataManager=getDashboardDataManager(session);
			if(dashboardDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
		
			session.beginTransaction();
			customWidgetId=dashboardDataManager.getCustomWidgetId(customWidgetName);
			session.commit();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			closeSession(session);
		}
		return customWidgetId;
	}
	
}
