package com.elitecore.elitesm.hibernate.dashboard;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.dashboard.DashboardDataManager;
import com.elitecore.elitesm.datamanager.dashboard.category.data.CategoryData;
import com.elitecore.elitesm.datamanager.dashboard.data.DashboardData;
import com.elitecore.elitesm.datamanager.dashboard.data.ManageDashboardData;
import com.elitecore.elitesm.datamanager.dashboard.userrelation.data.DashboardUserRelData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.dashboardconfiguration.DashboardConfigData;
import com.elitecore.elitesm.web.dashboard.json.TemplateGlobalConfData;
import com.elitecore.elitesm.web.dashboard.json.WidgetData;
import com.elitecore.elitesm.web.dashboard.json.WidgetOrderData;
import com.elitecore.elitesm.web.dashboard.json.WidgetTemplateData;
import com.elitecore.elitesm.web.dashboard.widget.configuration.WidgetConfigData;

public class HDashboardDataManager extends HBaseDataManager implements DashboardDataManager{

	private static final String MODULE = "SERVER-CERTI-DM";

	@Override
	public void create(DashboardData dashboardData,String staffId)	throws DataManagerException {
		try{			
			Session session=getSession();
			
			Criteria criteria = session.createCriteria(DashboardData.class).setProjection(Projections.max("orderNumber")); 
			
			List  maxOrderNumber = criteria.list();

			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
			long orderNumber = (Long) maxOrderNumber.get(0);
				dashboardData.setOrderNumber(++orderNumber);
			} else {
				dashboardData.setOrderNumber(1L);
			}
			
			session.save(dashboardData);
			session.flush();
			
			String dashboardId=null;
			criteria = session.createCriteria(DashboardData.class).setProjection(Projections.max("dashboardId")); 
			
			maxOrderNumber = criteria.list();
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
				long orderNumber = (Long) maxOrderNumber.get(0);
				dashboardId=String.valueOf(orderNumber);
			} else {
					dashboardId="1";
			}
			session.flush();
			
			DashboardUserRelData dashboardUserRelData=new DashboardUserRelData();
			
			criteria = session.createCriteria(DashboardUserRelData.class).setProjection(Projections.max("orderNumber")); 
			
			 maxOrderNumber = criteria.list();
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
				long orderNumber = (Long) maxOrderNumber.get(0);
					dashboardUserRelData.setOrderNumber(++orderNumber);
				} else {
					dashboardUserRelData.setOrderNumber(1L);
				}
			
			dashboardUserRelData.setDashboardId(dashboardId);
			dashboardUserRelData.setIsActive("A");
			dashboardUserRelData.setStaffId(staffId);
			
			session.save(dashboardUserRelData);
			session.flush();
			
			Logger.logDebug(MODULE, "Record Created Successfully.");
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public PageList search(DashboardData dashboardData,Map infoMap) throws DataManagerException {
		PageList pageList=null;
		try{			
			int pageNo=(Integer)infoMap.get("pageNo");
			int pageSize=(Integer)infoMap.get("pageSize");
			Session session=getSession();
			Criteria criteria=session.createCriteria(DashboardData.class);

			if(dashboardData.getDashboardName()!=null & dashboardData.getDashboardName().trim().length()>0){
				criteria.add(Restrictions.ilike("dashboardName", dashboardData.getDashboardName().trim(),MatchMode.ANYWHERE));
			}		
			
			criteria.addOrder(Order.asc("orderNumber"));
		
			List list=criteria.list();
			int totalItems=list.size();	

			criteria.setFirstResult(((pageNo-1) * pageSize));
			criteria.setMaxResults(pageSize);

			List<DashboardData> dashboardDataList=criteria.list();
			
			long totalPages = (long) Math.ceil(totalItems/pageSize);

			if(totalItems%pageSize ==0){
				totalPages-=1;
			}
			Logger.logDebug(MODULE, "Search Successfully....");
			pageList=new PageList(dashboardDataList,pageNo,totalPages,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}
	
	@Override
	public PageList manageSearch(DashboardData dashboardData,Map infoMap) throws DataManagerException {
		PageList pageList=null;
		try{			
			int pageNo=(Integer)infoMap.get("pageNo");
			int pageSize=(Integer)infoMap.get("pageSize");
			Session session=getSession();
			Criteria criteria=session.createCriteria(DashboardData.class);

			if(dashboardData.getDashboardName()!=null & dashboardData.getDashboardName().trim().length()>0){
				criteria.add(Restrictions.ilike("dashboardName", dashboardData.getDashboardName().trim(),MatchMode.ANYWHERE));
			}		
			
			criteria.addOrder(Order.asc("orderNumber"));
			List list=criteria.list();
			int totalItems=list.size();	

			criteria.setFirstResult(((pageNo-1) * pageSize));
			criteria.setMaxResults(pageSize);

			List<DashboardData> dashboardDataList=criteria.list();
			long totalPages = (long) Math.ceil(totalItems/pageSize);

			if(totalItems%pageSize ==0){
				totalPages-=1;
			}
			Logger.logDebug(MODULE, "Search Successfully....");
			pageList=new PageList(dashboardDataList,pageNo,totalPages,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public void delete(String dashboardId) throws DataManagerException {

		try{			
			Session session=getSession();
			
			DashboardData dashboardData = (DashboardData ) session.createCriteria(DashboardData.class).add(Restrictions.eq("dashboardId", dashboardId)).uniqueResult();
			session.delete(dashboardData);
			Logger.logDebug(MODULE, "Record Deleted Successfully.");
	
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}		
	}

	@Override
	public void updateIsActive(String isActive, String dashboardId,String staffId)throws DataManagerException {
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(DashboardUserRelData.class); 

			DashboardUserRelData data=(DashboardUserRelData)criteria.add(Restrictions.eq("dashboardId", dashboardId)).add(Restrictions.eq("staffId",staffId)).uniqueResult();
		
			if(data != null){
				
				data.setIsActive(isActive);
				
				session.update(data);
				session.flush();
			}else{
				criteria=session.createCriteria(DashboardUserRelData.class); 

				criteria.add(Restrictions.eq("staffId",staffId)).setProjection(Projections.max("orderNumber")).uniqueResult();
				DashboardUserRelData dashboardUserRelData=new DashboardUserRelData();
				List  maxOrderNumber = criteria.list();
				if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
				long orderNumber = (Long) maxOrderNumber.get(0);
					dashboardUserRelData.setOrderNumber(++orderNumber);
				} else {
					dashboardUserRelData.setOrderNumber(1L);
				}
				
				dashboardUserRelData.setDashboardId(dashboardId);
				dashboardUserRelData.setIsActive(isActive);
				dashboardUserRelData.setStaffId(staffId);
				
				session.save(dashboardUserRelData);
				session.flush();
			}
			
			Logger.logDebug(MODULE, "Record Updated Successfully.");
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
		
	}	
	
	@Override
	public PageList searchDataFromDashboardTable(String dashboardid,Map infoMap) throws DataManagerException {
		PageList pageList = null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DashboardData.class);
			if(dashboardid != null)
			{
				criteria.add(Restrictions.eq("dashboardId",dashboardid));
			}	
			
			criteria.addOrder(Order.asc("orderNumber"));
            List list = criteria.list();
			int totalItems = list.size();
			List<DashboardData> dashboardDataList = criteria.list();
			pageList = new PageList(dashboardDataList,0,0,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}

	@Override
	public PageList getWidgetData(String dashboardId)  throws DataManagerException {
		PageList pageList = null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DashboardData.class);
			if(dashboardId != null)
			{
				criteria.add(Restrictions.eq("dashboardId",dashboardId));
			}	
			
			criteria.add(Restrictions.eq("isActive", "A"));
			criteria.addOrder(Order.asc("orderNumber"));
            List list = criteria.list();
			int totalItems = list.size();
			List<DashboardData> dashboardDataList = criteria.list();
			pageList = new PageList(dashboardDataList,0,0,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}

	@Override
	public void updatewidgetConfig(WidgetConfigData widgetConfigData) throws DataManagerException {
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(WidgetConfigData.class);
			WidgetConfigData data=(WidgetConfigData)criteria.add(Restrictions.eq("parameterId", widgetConfigData.getParameterId())).uniqueResult();
			
			data.setParameterId(widgetConfigData.getParameterId());
			data.setParameterKey(widgetConfigData.getParameterKey());
			data.setParameterValue(widgetConfigData.getParameterValue());
			data.setWidgetId(widgetConfigData.getWidgetId());
			
			session.update(data);
			session.flush();
			
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
	}

	@Override
	public PageList getWidgetConfigurationData(String widgetId)throws DataManagerException {
		PageList pageList = null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(WidgetConfigData.class);
			if(widgetId != null)
			{
				criteria.add(Restrictions.eq("widgetId",widgetId));
			}	
			
            List list = criteria.list();
			int totalItems = list.size();
			List<WidgetConfigData> widgetConfigList = criteria.list();
			pageList = new PageList(widgetConfigList,0,0,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}

	@Override
	public PageList getAllCategoriesList(CategoryData categoryData, Map infoMap)throws DataManagerException {
		PageList pageList=null;
		try{			
			int pageNo=(Integer)infoMap.get("pageNo");
			int pageSize=(Integer)infoMap.get("pageSize");
			Session session=getSession();
			Criteria criteria=session.createCriteria(CategoryData.class);

			if(categoryData.getCategoryName()!=null & categoryData.getCategoryName().trim().length()>0){
				criteria.add(Restrictions.ilike("categoryName", categoryData.getCategoryName().trim(),MatchMode.ANYWHERE));
			}		
		
			List list=criteria.list();
			int totalItems=list.size();	

			criteria.setFirstResult(((pageNo-1) * pageSize));
			criteria.setMaxResults(pageSize);

			List<CategoryData> categoryDataList=criteria.list();
			
			long totalPages = (long) Math.ceil(totalItems/pageSize);

			if(totalItems%pageSize ==0){
				totalPages-=1;
			}
			Logger.logDebug(MODULE, "Search Successfully....");
			pageList=new PageList(categoryDataList,pageNo,totalPages,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	@Override
	public Long countElementOfCategory(String categoryId) throws DataManagerException {
		Long categoryCount = null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(WidgetTemplateData.class);
			if(categoryId != null)
			{
				criteria.add(Restrictions.eq("categoryId",categoryId));
			}	
			
            List list = criteria.list();
            categoryCount = (long) list.size();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return categoryCount;
	}

	@Override
	public Long getWidgetOrder(String templateId, String dashboardId)throws DataManagerException {
		Long newOrderNumber = null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(WidgetData.class);
			criteria.add(Restrictions.eq("dashboardId", dashboardId));
			List list=criteria.list();
			
			List<WidgetData> widgetData=criteria.list();
			
			if(widgetData != null){
					session.flush();
					criteria = session.createCriteria(WidgetOrderData.class).add(Restrictions.eq("dashboardId", dashboardId)).add(Restrictions.eq("columnNumber", 1L)).add(Restrictions.eq("layout", 2L)).setProjection(Projections.max("orderNumber")); 
					List  maxOrderNumber = criteria.list();
					
					if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
					long orderNumber = (Long) maxOrderNumber.get(0);
						newOrderNumber=++orderNumber;
					} else {
						newOrderNumber=1L;
					}
					session.flush();
					return newOrderNumber;
			}else{
				
				criteria = session.createCriteria(WidgetOrderData.class).add(Restrictions.eq("dashboardId", dashboardId)).setProjection(Projections.max("orderNumber")); 
				List  maxOrderNumber = criteria.list();
				
				if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
				long orderNumber = (Long) maxOrderNumber.get(0);
					newOrderNumber=++orderNumber;
				} else {
					newOrderNumber=1L;
				}
				session.flush();
				
				return newOrderNumber;
				
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public Long addOrderData(WidgetOrderData widgetOrderData)throws DataManagerException {
		try{			
			Session session=getSession();
		    session.save(widgetOrderData);
			session.flush();
			
			Criteria criteria = session.createCriteria(WidgetOrderData.class);
			criteria = session.createCriteria(WidgetOrderData.class).setProjection(Projections.max("orderId")); 
			List  maxOrderNumber = criteria.list();
			
			long newOrderNumber;
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
			long orderNumber = (Long) maxOrderNumber.get(0);
				newOrderNumber=orderNumber;
			} else {
				newOrderNumber=1L;
			}
			
			Logger.logDebug(MODULE, "Record Created Successfully.");
			System.out.println("Max Order Id : " +newOrderNumber);
			return newOrderNumber;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}

	@Override
	public String addNewWidget(WidgetData widgetData) throws DataManagerException {
		try{			
			Session session=getSession();
			
		    session.save(widgetData);
			session.flush();
			
			
			Criteria criteria = session.createCriteria(WidgetData.class);
			criteria = session.createCriteria(WidgetData.class).setProjection(Projections.max("widgetId")); 
			List  maxOrderNumber = criteria.list();
			
			String newWidgetId;
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
				long orderNumber = (Long) maxOrderNumber.get(0);
				newWidgetId=String.valueOf(orderNumber++);
			} else {
				newWidgetId="1";
			}
			
			Logger.logDebug(MODULE, "Record Created Successfully.");
			return newWidgetId;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void addWidgetConfig(WidgetConfigData widgetConfigData)throws DataManagerException {
		try{			
			Session session=getSession();
			
		    session.save(widgetConfigData);
			session.flush();
			
			Logger.logDebug(MODULE, "Record Created Successfully.");
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public String checkWidgetConfigurations(String widgetId)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(WidgetConfigData.class);
			if(widgetId != null)
			{
				criteria.add(Restrictions.eq("widgetId",widgetId));
			}	
			
	        List list = criteria.list();
	        long categoryCount = (long) list.size();
	        if(categoryCount >=1){
	        	return "true";
	        }else{
	        	return "false";
	        }
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void addWidgetConfiguration(String key, String value,String widgetId)throws DataManagerException {
		try{			
			Session session=getSession();
			
			
			List<WidgetConfigData> widgetConfigDataList =null;
						
			Criteria criteria=session.createCriteria(WidgetConfigData.class).add(Restrictions.eq("widgetId", widgetId)).add(Restrictions.eq("parameterKey", key));
			widgetConfigDataList=criteria.list();
			if(widgetConfigDataList!=null && widgetConfigDataList.size()>0){
				for(int i=0 ; i<widgetConfigDataList.size() ;i++){
					WidgetConfigData widgetConfigData=(WidgetConfigData)widgetConfigDataList.get(i);
					session.delete(widgetConfigData);
					session.flush();				
				}
			}			
			
			WidgetConfigData widgetConfigData=new WidgetConfigData();
			
			widgetConfigData.setParameterKey(key);
			widgetConfigData.setParameterValue(value);
			widgetConfigData.setWidgetId(widgetId);
			
		    session.saveOrUpdate(widgetConfigData);
			session.flush();
			
			Logger.logDebug(MODULE, "Record Created Successfully.");
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}

	
	@Override
	public void deleteWidgetConfiguration(String widgetId)throws DataManagerException {
		try{			
			Session session=getSession();
			
			List<WidgetConfigData> widgetConfigDataList =null;
						
			Criteria criteria=session.createCriteria(WidgetConfigData.class).add(Restrictions.eq("widgetId", widgetId));
			widgetConfigDataList=criteria.list();
				if(widgetConfigDataList!=null && widgetConfigDataList.size()>0){
					for(int i=0 ; i<widgetConfigDataList.size() ;i++){
						WidgetConfigData widgetConfigData=(WidgetConfigData)widgetConfigDataList.get(i);
						session.delete(widgetConfigData);
						session.flush();				
					}
				}			
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	@Override
	public void deleteWidget(String widgetId) throws DataManagerException {
		try{			
			Session session=getSession();
			
			List<WidgetConfigData> widgetConfigDataList =null;
			
			Criteria criteria=session.createCriteria(WidgetConfigData.class).add(Restrictions.eq("widgetId", widgetId));
			widgetConfigDataList=criteria.list();
			if(widgetConfigDataList != null && widgetConfigDataList.size() > 0){
				for(int i=0 ; i<widgetConfigDataList.size() ;i++){
					WidgetConfigData widgetConfigData=(WidgetConfigData)widgetConfigDataList.get(i);
					session.delete(widgetConfigData);
					session.flush();				
				}
			}	
			
			WidgetData widgetData = (WidgetData) session.createCriteria(WidgetData.class).add(Restrictions.eq("widgetId", widgetId)).uniqueResult();
			String orderId=widgetData.getOrderId();
			
			if(widgetData != null){
				session.delete(widgetData);
				session.flush();
			}
			
			WidgetOrderData widgetOrderData = (WidgetOrderData) session.createCriteria(WidgetOrderData.class).add(Restrictions.eq("orderId", orderId)).uniqueResult();
			
			if(widgetOrderData != null){
				session.delete(widgetOrderData);
				session.flush();
			}
			
			Logger.logDebug(MODULE, "Record Deleted Successfully.");
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}

	@Override
	public void changeWidgetOrder(String widgetId, Long columnId,Long orderNumber)throws DataManagerException {
		try{			
			Session session=getSession();
			
			WidgetData widgetData = (WidgetData) session.createCriteria(WidgetData.class).add(Restrictions.eq("widgetId", widgetId)).uniqueResult();
			String orderId=widgetData.getOrderId();
			
			session.flush();
			
			if(orderId !=null){
				Criteria criteria=session.createCriteria(WidgetOrderData.class);

				WidgetOrderData widgetOrderData=(WidgetOrderData)criteria.add(Restrictions.eq("orderId", orderId)).uniqueResult();
			
				widgetOrderData.setOrderNumber(orderNumber);
				widgetOrderData.setColumnNumber(columnId);
			
				session.update(widgetOrderData);
				session.flush();
			}
			
			Logger.logDebug(MODULE, "Order Changed Successfully.");
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void saveDashboardConfiguration(DashboardConfigData dashboardConfigData) throws DataManagerException {
		try{			
			Session session=getSession();
			
			DashboardConfigData dashboardConfData =(DashboardConfigData) session.createCriteria(DashboardConfigData.class).add(Restrictions.eq("dashboardConfigId", dashboardConfigData.getDashboardConfigId())).uniqueResult();
			session.flush();
			
			if(dashboardConfData !=null){
				Criteria criteria=session.createCriteria(DashboardConfigData.class);

				DashboardConfigData dashboardConData=(DashboardConfigData)criteria.add(Restrictions.eq("dashboardConfigId",dashboardConfigData.getDashboardConfigId())).uniqueResult();
			
				dashboardConData.setDatabaseId(dashboardConfigData.getDatabaseId());
				dashboardConData.setMaxConcurrentAccess(dashboardConfigData.getMaxConcurrentAccess());
				dashboardConData.setMaxTabs(dashboardConfigData.getMaxTabs());
				dashboardConData.setMaxWebSockets(dashboardConfigData.getMaxWebSockets());
				dashboardConData.setMaxWidgets(dashboardConfigData.getMaxWidgets());
			
				session.update(dashboardConData);
				session.flush();
			}else{
				DashboardConfigData dashboardConfiData=new DashboardConfigData();
				
				dashboardConfiData.setDashboardConfigId(dashboardConfigData.getDashboardConfigId());
				dashboardConfiData.setDatabaseId(dashboardConfigData.getDatabaseId());
				dashboardConfiData.setMaxConcurrentAccess(dashboardConfigData.getMaxConcurrentAccess());
				dashboardConfiData.setMaxTabs(dashboardConfigData.getMaxTabs());
				dashboardConfiData.setMaxWebSockets(dashboardConfigData.getMaxWebSockets());
				dashboardConfiData.setMaxWidgets(dashboardConfigData.getMaxWidgets());
				
			    session.saveOrUpdate(dashboardConfiData);
				session.flush();
				
			}
			
			Logger.logDebug(MODULE, "Record Created Successfully.");
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	@Override
	public DashboardConfigData getDashboardConfiguration() throws DataManagerException {
	{   
			List parameterList = null;	
			DashboardConfigData dashboardConfigData=null;
			try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(DashboardConfigData.class);
				parameterList = criteria.list();
				if(parameterList != null && parameterList.size() > 0){
					dashboardConfigData=(DashboardConfigData) parameterList.get(0);
				}

			}catch (HibernateException hExp) {
				throw new DataManagerException(hExp.getMessage(), hExp);
			}

			return dashboardConfigData;
		}
	}

	
	@Override
	public  List<WidgetTemplateData> getwidgetTemplateDetails() throws DataManagerException {
		PageList pageList=null;
		try{			
			Session session=getSession();
			Criteria criteria=session.createCriteria(WidgetTemplateData.class);

			List<WidgetTemplateData> serverCertificateList=criteria.list();
			
			return serverCertificateList;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void saveTemplateGlobalConf(TemplateGlobalConfData templateGlobalConfData) throws DataManagerException {
		try{			
			Session session=getSession();
			
			TemplateGlobalConfData templateGlobalData=(TemplateGlobalConfData)session.createCriteria(TemplateGlobalConfData.class).add(Restrictions.eq("templateId", templateGlobalConfData.getTemplateId())).uniqueResult();
			
			if(templateGlobalData != null){
				session.delete(templateGlobalData);
				session.flush();
			}
			
			session.save(templateGlobalConfData);
			session.flush();
			Logger.logDebug(MODULE, "Record Created Successfully.");
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void saveCustomWidget(WidgetTemplateData widgetTemplateData)throws DataManagerException {
		try{			
			Session session=getSession();
			
			session.save(widgetTemplateData);
			session.flush();
			Logger.logDebug(MODULE, "Record Created Successfully.");
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public WidgetTemplateData getTemplateDetails(String templateId)throws DataManagerException {
		try{			
			Session session=getSession();
			WidgetTemplateData  widgetTemplateData= (WidgetTemplateData) session.createCriteria(WidgetTemplateData.class).add(Restrictions.eq("widgteTemplateId", templateId)).uniqueResult();
			
			return widgetTemplateData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void updateCustomWidgetConf(WidgetTemplateData widgetTemplateData)throws DataManagerException {
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(WidgetTemplateData.class);

			WidgetTemplateData data=(WidgetTemplateData)criteria.add(Restrictions.eq("widgteTemplateId", widgetTemplateData.getWidgteTemplateId())).uniqueResult();
			data.setConfigJspUrl(widgetTemplateData.getConfigJspUrl());
			data.setDescription(widgetTemplateData.getDescription());
			data.setJspUrl(widgetTemplateData.getJspUrl());
			data.setThumbnail(widgetTemplateData.getThumbnail());
			data.setTitle(widgetTemplateData.getTitle());
			data.setWidgetGroovy(widgetTemplateData.getWidgetGroovy());
			data.setOrderNumber(widgetTemplateData.getOrderNumber());
			
			session.update(data);
			session.flush();
			Logger.logDebug(MODULE, "Record Updated Successfully.");
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}
	}

	@Override
	public List<TemplateGlobalConfData> fetchTemplateGlobalConfiguration()throws DataManagerException {
		try{			
			Session session=getSession();
			Criteria criteria=session.createCriteria(TemplateGlobalConfData.class);

			List<TemplateGlobalConfData> globalConfigList=criteria.list();
			
			return globalConfigList;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public List<DashboardUserRelData> getUserDashboardList(String staffId)throws DataManagerException {
		try{			
			Session session=getSession();
			List<DashboardUserRelData>  dashboardUserRelDataList = null;
			
			Criteria criteria=session.createCriteria(DashboardUserRelData.class).add(Restrictions.eq("staffId", staffId));
			dashboardUserRelDataList=criteria.list();
			
			return dashboardUserRelDataList;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public DashboardData getDashboardData(String dashboardId)throws DataManagerException {
		try{			
			Session session=getSession();
			DashboardData  dashboardData= (DashboardData) session.createCriteria(DashboardData.class).add(Restrictions.eq("dashboardId", dashboardId)).uniqueResult();
			
			return dashboardData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public DashboardUserRelData getUserRelList(String dashboardId,String staffId)throws DataManagerException {
		try{			
			Session session=getSession();
			DashboardUserRelData  dashboardUserRelData= (DashboardUserRelData) session.createCriteria(DashboardUserRelData.class).add(Restrictions.eq("dashboardId", dashboardId)).add(Restrictions.eq("staffId", staffId)).uniqueResult();

			return dashboardUserRelData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public String saveUserRelData(ManageDashboardData manageDashboardData)throws DataManagerException {
		try{			
			Session session=getSession();
			
			DashboardUserRelData dashboardUserRelData=new DashboardUserRelData();
			
			dashboardUserRelData.setDashboardId(manageDashboardData.getDashboardId());
			dashboardUserRelData.setIsActive(manageDashboardData.getIsActive());
			dashboardUserRelData.setOrderNumber(manageDashboardData.getOrderNumber());
			dashboardUserRelData.setStaffId(manageDashboardData.getStaffId());
			
			session.save(dashboardUserRelData);
			session.flush();
			
			Criteria criteria = session.createCriteria(DashboardUserRelData.class);
			criteria = session.createCriteria(DashboardUserRelData.class).setProjection(Projections.max("dashboardRelId")); 
			List  maxOrderNumber = criteria.list();
			
			String userRelId;
			if(!maxOrderNumber.isEmpty() && maxOrderNumber.get(0) != null){
			long orderNumber = (Long) maxOrderNumber.get(0);
				userRelId=String.valueOf(orderNumber);
			} else {
				userRelId="1";
			}
			Logger.logDebug(MODULE, "Record Created Successfully.");	
			return userRelId;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}

	@Override
	public void deleteDashboard(String dashboardId) throws DataManagerException {
		Session session=getSession();
		
		Criteria criteria = session.createCriteria(WidgetData.class);
		
		//remove all widgets as well as its configuration 
		List<WidgetData> widgetDataList=criteria.add(Restrictions.eq("dashboardId", dashboardId)).list();
		if(widgetDataList != null && widgetDataList.isEmpty() == false){
			for (Iterator<WidgetData> iterator = widgetDataList.iterator(); iterator.hasNext();) {
				WidgetData widgetData = iterator.next();
				criteria = session.createCriteria(WidgetConfigData.class);
				List<WidgetConfigData> widgetConfigData=criteria.add(Restrictions.eq("widgetId", widgetData.getWidgetId())).list();
				for(Iterator<WidgetConfigData> configDataObj = widgetConfigData.iterator();configDataObj.hasNext();){
						WidgetConfigData configData = configDataObj.next();
						session.delete(configData);
						session.flush();
					}
			}
		}	
		
		for (Iterator<WidgetData> iteratorInstance = widgetDataList.iterator(); iteratorInstance.hasNext();) {
			WidgetData widgetData = iteratorInstance.next();
			session.delete(widgetData);
			session.flush();
		}
		
		//delete data from widget Order data table
		criteria = session.createCriteria(WidgetOrderData.class);
		List<WidgetOrderData> widgetOrderList=criteria.add(Restrictions.eq("dashboardId", dashboardId)).list();
			
		for (Iterator<WidgetOrderData> iteratorInstance = widgetOrderList.iterator(); iteratorInstance.hasNext();) {
			WidgetOrderData widgetOrderData = iteratorInstance.next();
			session.delete(widgetOrderData);
			session.flush();
		}
		
		//delete data from DashboardUserRelData table
		criteria = session.createCriteria(DashboardUserRelData.class);
		List<DashboardUserRelData> dashboardUserRelDataList=criteria.add(Restrictions.eq("dashboardId", dashboardId)).list();
		
		for (Iterator<DashboardUserRelData> iteratorInstance = dashboardUserRelDataList.iterator(); iteratorInstance.hasNext();) {
			DashboardUserRelData dashboardUserRelData = iteratorInstance.next();
			session.delete(dashboardUserRelData);
			session.flush();
		}
		
		criteria = session.createCriteria(DashboardData.class);
		List<DashboardData> dashboardDataList=criteria.add(Restrictions.eq("dashboardId", dashboardId)).list();
		
		for (Iterator<DashboardData> iteratorInstance = dashboardDataList.iterator(); iteratorInstance.hasNext();) {
			DashboardData dashboardData = iteratorInstance.next();
			session.delete(dashboardData);
			session.flush();
		}
		
		session.clear();
	}

	@Override
	public void updateDashboardDetail(DashboardData dashboardData,IStaffData staffData) throws DataManagerException {
		Session session = getSession();
		DashboardData dashboardDataObj = null;

		if(dashboardData != null){
			EliteAssert.notNull(dashboardData,"Dashboard name must not be null.");
			try{
				verifyDashboardNameForUpdate(dashboardData);
				Criteria criteria = session.createCriteria(DashboardData.class);
				dashboardDataObj = (DashboardData)criteria.add(Restrictions.eq("dashboardId",dashboardData.getDashboardId())).uniqueResult();
				
				dashboardDataObj.setDashboardId(dashboardData.getDashboardId());
				dashboardDataObj.setDashboardName(dashboardData.getDashboardName());
				dashboardDataObj.setDashboardDesc(dashboardData.getDashboardDesc());
				dashboardDataObj.setStartFrom(dashboardData.getStartFrom());
				dashboardDataObj.setAddShares(dashboardData.getAddShares());
				dashboardDataObj.setAuthor(dashboardData.getAuthor());
				
				session.update(dashboardDataObj);
				session.flush();
				
			} catch (DuplicateInstanceNameFoundException dExp) {
				throw new DuplicateInstanceNameFoundException(dExp.getMessage(), dExp);
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(),hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException("UpdateDatabaseDatasource Failed");
		}
	}

	private void verifyDashboardNameForUpdate(DashboardData dashboardData) throws DuplicateInstanceNameFoundException {
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(DashboardData.class);
		List list = criteria.add(Restrictions.eq("dashboardName",dashboardData.getDashboardName())).add(Restrictions.ne("dashboardId",dashboardData.getDashboardId())).list();
		if(list==null || list.isEmpty()){
			return;
		}else{
			throw new DuplicateInstanceNameFoundException("Database Datasource Name Is Duplicated.");
		}
		
	}

	@Override
	public Long countMaxDashboard(String staffId) throws DataManagerException {
		Long dashboardCount = null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DashboardUserRelData.class);
			criteria.add(Restrictions.eq("staffId", staffId));
			
            List list = criteria.list();
            dashboardCount = (long) list.size();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return dashboardCount;
		
	}

	@Override
	public String getCustomWidgetId(String customWidgetName) throws DataManagerException {
		String customWidgetId = null;
        try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(CategoryData.class);
			
			CategoryData categoryData = new CategoryData();
			categoryData = (CategoryData)criteria.add(Restrictions.eq("categoryName",customWidgetName)).uniqueResult();
			
            customWidgetId = categoryData.getCategoryId();
            System.out.println("Cutsom Widget Id = "+customWidgetId);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return customWidgetId;
	}
}