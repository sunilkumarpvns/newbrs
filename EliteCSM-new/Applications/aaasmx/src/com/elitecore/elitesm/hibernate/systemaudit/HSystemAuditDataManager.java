package com.elitecore.elitesm.hibernate.systemaudit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.MDC;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.datamanager.systemaudit.Data.ISystemAuditData;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HSystemAuditDataManager extends HBaseDataManager implements SystemAuditDataManager{
	
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias, String transactionId) throws DataManagerException{
		updateTbltSystemAudit(staffData, actionAlias, transactionId, "");
	}
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias) throws DataManagerException{
		updateTbltSystemAudit(staffData, actionAlias, null, "");
	}
	
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias, String transactionId, String remarks) throws DataManagerException{

		try{

			Session session = getSession();
			
			Criteria criteria = session.createCriteria(ActionData.class);
			criteria.add(Restrictions.eq("alias",actionAlias));
			List lstActionId = criteria.list();
			IActionData actionData = (ActionData)lstActionId.get(0);
			String actionId = actionData.getActionId();
            String staffId = staffData.getStaffId();
			ISystemAuditData systemAuditData = new SystemAuditData();

			systemAuditData.setActionId(actionId);
			systemAuditData.setSystemUserId(staffId);
			systemAuditData.setSystemUserName(staffData.getUsername());
			systemAuditData.setRemarks(remarks);
			systemAuditData.setTransactionId(transactionId);
			systemAuditData.setClientIP(getClientIP());
			Date auditDate = new Date();
			systemAuditData.setAuditDate(auditDate);
			systemAuditData.setAuditId(staffData.getAuditId());
			systemAuditData.setAuditName(staffData.getAuditName());
			Logger.logDebug("MODULE", systemAuditData);
			
			session.save(systemAuditData);
			session.flush();
			
		} catch (ConstraintViolationException cve) {
			throw new DataManagerException("Failed to create "+staffData.getAuditName()+", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp.getCause());
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp.getCause());
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
		String userId = systemAuditData.getSystemUserId();
		String auditName=systemAuditData.getAuditName().trim();
		System.out.println("the userid here in database file is " +userId);
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(SystemAuditData.class);
			if(!actionId.equalsIgnoreCase("")){
				criteria.add(Restrictions.eq("actionId",actionId));
			}
			if(!userId.equalsIgnoreCase("")){
				criteria.add(Restrictions.like("systemUserId",userId+"%"));
			}
			if(!auditName.equalsIgnoreCase("")){
				criteria.add((Restrictions.ilike("auditName","%"+auditName+"%",MatchMode.ANYWHERE)));
			}
			
			if(systemAuditData.getAuditDate() != null && systemAuditData.getAuditDateFrom() != null){
				Date systemAuditDate = systemAuditData.getAuditDate();
				Date systemAuditDateFrom =systemAuditData.getAuditDateFrom();
				
				criteria.add(Restrictions.between("auditDate", systemAuditDate, systemAuditDateFrom));
			}else if(systemAuditData.getAuditDate() != null && systemAuditData.getAuditDateFrom() == null){
				Date systemAuditDate = systemAuditData.getAuditDate();
				
				
				Calendar c = new GregorianCalendar();
				c.setTime(systemAuditDate);
				
				
				c.add(Calendar.DAY_OF_MONTH, 1);
				Date newDate = c.getTime();
				
				//criteria.add(Expression.between("auditDate",systemAuditDate,newDate));
				criteria.add(Restrictions.between("auditDate", systemAuditDate, newDate));
			}
			
			
			
			criteria.addOrder(Order.desc("auditDate"));
			int totalItems = criteria.list().size();
				criteria.setFirstResult((pageNo - 1) * pageSize);
			if (pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			
			List auditDetailList = criteria.list();
			
			long totalPages = (long)Math.ceil(totalItems/pageSize);
            if(totalItems%pageSize == 0)
           	totalPages-=1;
			
			auditDetails = new PageList(auditDetailList,pageNo, totalPages,totalItems);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return auditDetails;
	}
	
	private String getClientIP(){
		String clientAddress = (String)MDC.get("remoteaddress");

		if(clientAddress == null){
			clientAddress = (String)MDC.get("restremoteaddress");

			if(clientAddress == null){
				clientAddress  = "Unknown";
			}
		}
		return clientAddress;
	}
}
