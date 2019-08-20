package com.elitecore.elitesm.hibernate.core.base;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.GenericDataManager;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.tableorder.TableOrder;
import com.elitecore.elitesm.web.tableorder.TableOrderData;



public class HGenericDataManager extends HBaseDataManager implements GenericDataManager {
    
	public PageList getAllRecords(Class<?> instanceClass,String orderByProperty,boolean isAsc) throws DataManagerException {
		PageList pageList = null; 
       try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(instanceClass);
			if(orderByProperty !=null && (!orderByProperty.trim().equalsIgnoreCase("")))
			{
				if(isAsc)
				{
					criteria.addOrder(Order.asc(orderByProperty));
				}	
				else
				{
					criteria.addOrder(Order.desc(orderByProperty));
				}	
			}	
        	List list = criteria.list();
			int totalItems = list.size();
			List allRecordsList = criteria.list();
			pageList = new PageList(allRecordsList, 1, 1,totalItems);

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return pageList;
	}
	
	public List<?> getAllRecords(Class<?> instanceClass,String statusProperty,String orderByProperty,boolean isAsc) throws DataManagerException {
		List<?> list = null; 
       try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(instanceClass)
										.add(Restrictions.eq(statusProperty, "CST01"));
			if(orderByProperty !=null && (!orderByProperty.trim().equalsIgnoreCase("")))
			{
				if(isAsc)
				{
					criteria.addOrder(Order.asc(orderByProperty));
				}	
				else
				{
					criteria.addOrder(Order.desc(orderByProperty));
				}	
			}	
			list = criteria.list();
			

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return list;
	}
	

	@Override
	public void saveOrderOfData(String className, String[] ids) throws DataManagerException {
		try{
			Session session = getSession();
			long orderNum = 0L;
			for(String id : ids){
				if(id != null){
					TableOrder tableOrder =  (TableOrder) session.get(TableOrderData.getInstance().getClassFromName(className), id);
					tableOrder.setOrderNumber(orderNum++);
					session.update(tableOrder);	
				}
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	
	
}