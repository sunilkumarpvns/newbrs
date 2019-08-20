package com.elitecore.netvertexsm.hibernate.core.base;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.GenericDataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.ws.logger.Logger;
import com.elitecore.netvertexsm.web.tableorder.TableOrder;
import com.elitecore.netvertexsm.web.tableorder.TableOrderData;


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
	public void saveOrderOfData(String className, Long[] ids)
			throws DataManagerException {
		try{
			Session session = getSession();
			long orderNum = 0L;
			for(Long id : ids){
				TableOrder tableOrder =  (TableOrder) session.get(TableOrderData.getInstance().getClassFromName(className), id);
				tableOrder.setOrderNumber(orderNum++);
				session.update(tableOrder);
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	
	
}