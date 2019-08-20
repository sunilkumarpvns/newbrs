package com.elitecore.netvertexsm.hibernate.systemaudit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.datamanager.systemaudit.Data.ISystemAuditData;
import com.elitecore.netvertexsm.datamanager.systemaudit.Data.SystemAuditData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteUtility;

public class HSystemAuditDataManager extends HBaseDataManager implements SystemAuditDataManager{
	
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias, String transactionId) throws DataManagerException{
		 updateTbltSystemAudit(staffData,  actionAlias, transactionId,"-");
	}
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias) throws DataManagerException{
		 updateTbltSystemAudit(staffData,  actionAlias, null,"-");
	}
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias, String transactionId, String remarks) throws DataManagerException{

		try{

			Session session = getSession();

			Criteria criteria = session.createCriteria(ActionData.class);
			criteria.add(Restrictions.eq("alias",actionAlias));
			List lstActionId = criteria.list();
			IActionData actionData = (ActionData)lstActionId.get(0);
			String actionId = actionData.getActionId();
			Long staffId = staffData.getStaffId();
			ISystemAuditData systemAuditData = new SystemAuditData();

			systemAuditData.setActionId(actionId);
			systemAuditData.setSystemUserId(staffId);
			systemAuditData.setSystemUserName(staffData.getUserName());
			systemAuditData.setClientIP(EliteUtility.getClientIP());
			systemAuditData.setRemarks(remarks);
			systemAuditData.setTransactionId(transactionId);

			Date auditDate = new Date();
			systemAuditData.setAuditDate(auditDate);
			session.save(systemAuditData);

		}
		catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	public List getAllAction() throws DataManagerException{
		List actionListInCombo = new ArrayList();
		try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(ActionData.class);
    		//criteria.add(Restrictions.eq("commonStatusId","CST01"));            
            actionListInCombo = criteria.list();

         }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
		return actionListInCombo;	
	}
	public List getAllUsers() throws DataManagerException{
		List usersListInCombo = new ArrayList();
		try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(StaffData.class);
    		//criteria.add(Restrictions.eq("commonStatusId","CST01"));            
            usersListInCombo = criteria.list();

         }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
		return usersListInCombo;
	}
	public PageList getAuditDetails(ISystemAuditData systemAuditData,int pageNo, int pageSize) throws DataManagerException{
		PageList auditDetails = null;
		String actionId = systemAuditData.getActionId();
		Long userId = systemAuditData.getSystemUserId();
		System.out.println("the userid here in database file is " +userId);
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(SystemAuditData.class);
			if(!actionId.equalsIgnoreCase("")){
				criteria.add(Restrictions.eq("actionId",actionId));
			}
			
			if(userId!=null && userId>0){
				criteria.add(Restrictions.eq("systemUserId",userId));
			}
			
			if(systemAuditData.getAuditDate() !=null){
				Date systemAuditDate = systemAuditData.getAuditDate();
				Calendar c = new GregorianCalendar();
				c.setTime(systemAuditDate);
				c.add(Calendar.DAY_OF_MONTH, 1);
				Date newDate = c.getTime();
				criteria.add(Restrictions.between("auditDate",systemAuditDate,newDate)).addOrder(Order.asc("auditDate"));
				
			}
			
			int totalItems = criteria.list().size();
				criteria.setFirstResult((pageNo - 1) * pageSize);
			if (pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			
			List auditDetailList = criteria.list();
			long totalPages = (long) Math.ceil(totalItems / pageSize);
			auditDetails = new PageList(auditDetailList,pageNo, totalPages,totalItems);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return auditDetails;
	}
}