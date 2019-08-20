package com.elitecore.elitesm.hibernate.servermgr.alert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.alert.AlertListenerDataManager;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertFileListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTrapListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.BaseAlertListener;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogAlertListenerData;
import com.elitecore.elitesm.datamanager.servermgr.alert.data.SYSLogNameValuePoolData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.AlertListenerConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HAlertListenerDataManager extends HBaseDataManager implements AlertListenerDataManager{
	private static final String ALERT_LISTENER_CONFIGURATION = "Alert listener configuration ";
	private static final String LISTENER_NAME = "name";
	private static final String LISTENER_ID = "listenerId";
	private final static String MODULE="HALERTLISTENERDATAMANAGER";

	@Override
	public String create(Object obj) throws DataManagerException {
		AlertListenerData alertListenerData = (AlertListenerData) obj;
		EliteAssert.notNull(alertListenerData,"alertListenerData must not be null.");
		try{
			Logger.logDebug(MODULE, "Listener Type is : "+alertListenerData.getTypeId());
			Logger.logDebug(MODULE, "Listener Id is : "+alertListenerData.getListenerId());
			Session session = getSession();
			session.clear();
			
			String auditId= UUIDGenerator.generate();
			
			alertListenerData.setAuditUId(auditId);
			
			session.save(alertListenerData);			
			session.flush();
			session.clear();
			List<AlertListenerRelData> alertListenerRelDataSet = alertListenerData.getAlertListenerRelDataList();
			alertListenerData.setAlertListenerRelDataList(null);
			
			if(alertListenerRelDataSet != null){
				Iterator<AlertListenerRelData> alertListenerRelDataIterator = alertListenerRelDataSet.iterator();
				while(alertListenerRelDataIterator.hasNext()){
					AlertListenerRelData tempAlertListenerRelData = alertListenerRelDataIterator.next();
					tempAlertListenerRelData.setInstanceId(alertListenerData.getListenerId());
					session.save(tempAlertListenerRelData);
					session.flush();
					session.clear();
				}
			}
			
			Logger.logDebug(MODULE, "Listener Id is : "+alertListenerData.getListenerId());
			alertListenerData.getAlertListener().setListenerId(alertListenerData.getListenerId());
			
			session.save(alertListenerData.getAlertListener());
			session.flush();
			session.clear();
		} catch(ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+ALERT_LISTENER_CONFIGURATION + alertListenerData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+ALERT_LISTENER_CONFIGURATION + alertListenerData.getName() + REASON + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+ALERT_LISTENER_CONFIGURATION + alertListenerData.getName() + REASON + exp.getMessage(), exp);
		}
		return alertListenerData.getName();
	}

	public List<AlertListenerTypeData> getAvailableListenerType() throws DataManagerException {
		List<AlertListenerTypeData> alertListenerTypeList;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertListenerTypeData.class);
			alertListenerTypeList = criteria.list();

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Listener Type of Alert Listener configuration, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Listener Type of Alert Listener configuration, Reason: " + exp.getMessage(), exp);
		}
		return alertListenerTypeList;
	}
	
	public List<AlertTypeData> getAlertTypeData() throws DataManagerException {
		List<AlertTypeData> alertTypeDataList;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertTypeData.class);
			alertTypeDataList = criteria.add(Restrictions.isNull("parentId")).list();

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Alert Type Data of Alert Listener configuration, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Alert Type Data of Alert Listener configuration, Reason: " + exp.getMessage(), exp);
		}
		return alertTypeDataList;
	}
	
	private AlertListenerData getAlertListenerData(Object propertyValue, String propertyName) throws DataManagerException {
		String alertListenerName = (LISTENER_NAME.equals(propertyName)) ? (String) propertyValue : "Alert Listener configuration";
		AlertListenerData alertListenerData = null;
		try {
			Session session = getSession();			 
			Criteria criteria = session.createCriteria(AlertListenerData.class);
			alertListenerData = (AlertListenerData)criteria.add(Restrictions.eq(propertyName, propertyValue)).uniqueResult();

			if (alertListenerData == null) {
				throw new InvalidValueException("Alert Listener configuration not found");
			}

			alertListenerName = alertListenerData.getName();

			if(alertListenerData.getTypeId().equals(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID)){
				criteria = session.createCriteria(AlertFileListenerData.class);
				AlertFileListenerData alertFileListenerData = (AlertFileListenerData)criteria.add(Restrictions.eq(LISTENER_ID,alertListenerData.getListenerId())).uniqueResult();
				BaseAlertListener baseAlertListener = alertFileListenerData;
				alertListenerData.setAlertListener(baseAlertListener);

			} else if(alertListenerData.getTypeId().equals(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID)){
				criteria = session.createCriteria(AlertTrapListenerData.class);
				AlertTrapListenerData alertTrapListenerData = (AlertTrapListenerData)criteria.add(Restrictions.eq(LISTENER_ID,alertListenerData.getListenerId())).uniqueResult();
				BaseAlertListener baseAlertListener = alertTrapListenerData;
				alertListenerData.setAlertListener(baseAlertListener);

			} else if(alertListenerData.getTypeId().equals(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID)){
				criteria = session.createCriteria(SYSLogAlertListenerData.class);
				SYSLogAlertListenerData alertSysListenerData = (SYSLogAlertListenerData)criteria.add(Restrictions.eq(LISTENER_ID,alertListenerData.getListenerId())).uniqueResult();
				BaseAlertListener baseAlertListener = alertSysListenerData;
				alertListenerData.setAlertListener(baseAlertListener);
			}

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive "+ alertListenerName + ", Reason: "+ hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive "+ alertListenerName + ", Reason: "+ exp.getMessage(), exp);
		}
		return alertListenerData;
	}
	
	public List<AlertListenerRelData> getAlertListenerRelDataList(String listenerId)throws DataManagerException {
		List<AlertListenerRelData> alertListenerRelDataList = null;
		try{
			Session session = getSession();			 
			Criteria criteria = session.createCriteria(AlertListenerRelData.class);
			criteria.add(Restrictions.eq("instanceId",listenerId)).addOrder(Order.asc("typeId"));
			alertListenerRelDataList = (List<AlertListenerRelData>)criteria.add(Restrictions.eq("instanceId",listenerId)).list();
			session.flush();					
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Alert list of Alert Listener configuration, Reason: "+ hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Alert list of Alert Listener configuration, Reason: "+ exp.getMessage(), exp);
		}
		return alertListenerRelDataList;
	}

	public PageList search(AlertListenerData alertListenerData,int requiredPageNo, Integer pageSize) throws DataManagerException{

		PageList pageList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertListenerData.class);
            
			
			//add search criteria
			
			if(alertListenerData.getName() != null && alertListenerData.getName().length()>0){
				criteria.add(Restrictions.ilike(LISTENER_NAME,alertListenerData.getName()));
			}
			if(!("0".equals(alertListenerData.getTypeId()))){
				criteria.add(Restrictions.ilike("typeId",alertListenerData.getTypeId()));
			}

			//paging
			
			int totalItems =criteria.list().size();
			criteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List alertListnerList = criteria.list();


			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(alertListnerList, requiredPageNo, totalPages ,totalItems);
			Logger.logDebug(MODULE,"LIST SIZE IS:"+alertListnerList.size());

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to search Alert Listener configuration, Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to search Alert Listener configuration, Reason: " + exp.getMessage(), exp);
		}

		return pageList;

	}

	@Override
	public List<String> delete(List<String> alertListnerIdList) throws DataManagerException {
		String listenerName = "Alert Listener configuration";
		
		try{
			
			List<String> deletedAlertConfLst = new ArrayList<String>();
			Session session = getSession();
			
			for(String listenerId : alertListnerIdList){
				if (Strings.isNullOrBlank(listenerId) == false) {
					Criteria alertListenerRelcriteria = session.createCriteria(AlertListenerRelData.class);
					List<AlertListenerRelData> alertListenerRelList = alertListenerRelcriteria.add(Restrictions.eq("instanceId", listenerId)).list();
					deleteObjectList(alertListenerRelList, session);
				}
			}
			session.flush();
			
			MultiIdentifierLoadAccess<AlertListenerData> multiLoadAccess = session.byMultipleIds(AlertListenerData.class);
			List<AlertListenerData> driverInstanceDataLst = multiLoadAccess.multiLoad(alertListnerIdList);
			
			if(Collectionz.isNullOrEmpty(driverInstanceDataLst) == false){
				for(AlertListenerData alertListenerInst : driverInstanceDataLst){
					
					if(alertListenerInst != null){
						
						String typeId= alertListenerInst.getTypeId();
						listenerName = alertListenerInst.getName();
					
						if(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID.equals(typeId)){
							//file listener
							Criteria fileCriteria=session.createCriteria(AlertFileListenerData.class);
							fileCriteria.add(Restrictions.eq(LISTENER_ID, alertListenerInst.getListenerId()));
							AlertFileListenerData  alertFileListenerData =(AlertFileListenerData)fileCriteria.uniqueResult();
							session.delete(alertFileListenerData);

						}else if(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID.equals(typeId)){
							//trap listener
							Criteria trapCriteria=session.createCriteria(AlertTrapListenerData.class);
							trapCriteria.add(Restrictions.eq(LISTENER_ID, alertListenerInst.getListenerId()));
							AlertTrapListenerData alertTrapListenerData=(AlertTrapListenerData)trapCriteria.uniqueResult();
							session.delete(alertTrapListenerData);
						}else if(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID.equals(typeId)){
							//SysLog listener
							Criteria sysCriteria=session.createCriteria(SYSLogAlertListenerData.class);
							sysCriteria.add(Restrictions.eq(LISTENER_ID, alertListenerInst.getListenerId()));
							SYSLogAlertListenerData alertSysListenerData=(SYSLogAlertListenerData)sysCriteria.uniqueResult();
							session.delete(alertSysListenerData);
						}
						
						session.delete(alertListenerInst);
						session.flush();
						deletedAlertConfLst.add(listenerName);
					}
				}
			}
		
			return deletedAlertConfLst;
		}catch(ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete "+ listenerName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to delete "+ listenerName +", Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to delete "+ listenerName +", Reason: "+e.getMessage(),e);
		}
	}
	
	private void updateAlertListener(AlertListenerData updatealertListenerData,IStaffData staffData,String propertyName, Object propertyValue)throws DataManagerException {
		String alertListenerName = (LISTENER_NAME.equals(propertyName)) ? (String) propertyValue : "Alert Listener configuration";
		EliteAssert.notNull(updatealertListenerData,"alertListenerData must not be null.");
		Session session = getSession();
		AlertListenerData alertListenerData = null;
		String listenerInstanceId = null;
		try {
			
			Logger.logDebug(MODULE, "Listener Type is : "+updatealertListenerData.getTypeId());
			Logger.logDebug(MODULE, "Listener Id is : "+updatealertListenerData.getListenerId());
			
			Criteria  criteria =session.createCriteria(AlertListenerData.class);
			criteria.add(Restrictions.eq(propertyName, propertyValue));
			alertListenerData = (AlertListenerData)criteria.uniqueResult();
			
			if (alertListenerData == null) {
				throw new InvalidValueException("Alert Listener configuration not found");
			}
			
			listenerInstanceId = alertListenerData.getListenerId();
			alertListenerName = alertListenerData.getName();

			if (updatealertListenerData.getAlertListener() != null) {
			updatealertListenerData.getAlertListener().setListenerId(listenerInstanceId);
			}
			
			session.flush();
			if(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(alertListenerData.getTypeId())){
				Criteria fileAlertListenerCriteria = session.createCriteria(AlertFileListenerData.class);
			    fileAlertListenerCriteria.add(Restrictions.eq(LISTENER_ID, listenerInstanceId));
			    AlertFileListenerData alertFileListenerData =(AlertFileListenerData)fileAlertListenerCriteria.uniqueResult();
			    alertListenerData.setAlertListener(alertFileListenerData);
			    session.flush();
			}else if(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(alertListenerData.getTypeId())){
				Criteria trapAlertListenerCriteria = session.createCriteria(AlertTrapListenerData.class);
			    trapAlertListenerCriteria.add(Restrictions.eq(LISTENER_ID, listenerInstanceId));
			    AlertTrapListenerData alertTrapListenerData =(AlertTrapListenerData)trapAlertListenerCriteria.uniqueResult();
			    alertListenerData.setAlertListener(alertTrapListenerData);
			    session.flush();
			}else if(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(alertListenerData.getTypeId())){
				Criteria sysAlertListenerCriteria = session.createCriteria(SYSLogAlertListenerData.class);
				sysAlertListenerCriteria.add(Restrictions.eq(LISTENER_ID, listenerInstanceId));
			    SYSLogAlertListenerData alertSysListenerData =(SYSLogAlertListenerData)sysAlertListenerCriteria.uniqueResult();
			    alertListenerData.setAlertListener(alertSysListenerData);
			    session.flush();
			}
			
			Criteria alertListenerRelcriteria = session.createCriteria(AlertListenerRelData.class);
			List<AlertListenerRelData> alertListenerRelList = alertListenerRelcriteria.add(Restrictions.eq("instanceId", listenerInstanceId)).list();
			alertListenerData.setAlertListenerRelDataList(new ArrayList<AlertListenerRelData>(alertListenerRelList));
			
			JSONArray jsonArray=ObjectDiffer.diff(alertListenerData, updatealertListenerData);
			
			alertListenerData.setName(updatealertListenerData.getName());
			alertListenerData.setTypeId(updatealertListenerData.getTypeId());
			
			List<AlertListenerRelData> alertListenerRelDataSet = updatealertListenerData.getAlertListenerRelDataList();
				
			deleteObjectList(alertListenerRelList, session);
			
			session.flush();
			
			if(alertListenerRelDataSet != null){
				Iterator<AlertListenerRelData> alertListenerRelDataIterator = alertListenerRelDataSet.iterator();
				while(alertListenerRelDataIterator.hasNext()){
					AlertListenerRelData tempAlertListenerRelData = alertListenerRelDataIterator.next();
					tempAlertListenerRelData.setInstanceId(alertListenerData.getListenerId());
					session.save(tempAlertListenerRelData);
					session.flush();
				}
			}
			if (alertListenerData.getAuditUId() == null) {
				String auditId= UUIDGenerator.generate();
				alertListenerData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
		//	alertListenerData.setEnabledAlerts(updatealertListenerData.getEnabledAlerts());	
			session.update(alertListenerData);
			session.flush();
			if(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(updatealertListenerData.getTypeId())){
				     AlertFileListenerData updateAlertFileListenerData =(AlertFileListenerData)updatealertListenerData.getAlertListener();
				     Criteria fileAlertListenerCriteria = session.createCriteria(AlertFileListenerData.class);
				     fileAlertListenerCriteria.add(Restrictions.eq(LISTENER_ID, listenerInstanceId));
				     AlertFileListenerData alertFileListenerData =(AlertFileListenerData)fileAlertListenerCriteria.uniqueResult();
				     alertFileListenerData.setFileName(updateAlertFileListenerData.getFileName());
				     alertFileListenerData.setCompRollingUnit(updateAlertFileListenerData.getCompRollingUnit());
					 alertFileListenerData.setRollingType(updateAlertFileListenerData.getRollingType());
					 alertFileListenerData.setRollingUnit(updateAlertFileListenerData.getRollingUnit());
					 alertFileListenerData.setMaxRollingUnit(updateAlertFileListenerData.getMaxRollingUnit());
				     alertFileListenerData.setListenerId(updateAlertFileListenerData.getListenerId());
				     alertFileListenerData.setRepeatedMessageReduction(updateAlertFileListenerData.getRepeatedMessageReduction());
				     session.update(alertFileListenerData);
				     session.flush();
				     
			}else if(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(updatealertListenerData.getTypeId())){
				 
				 AlertTrapListenerData updateAlertTrapListenerData = (AlertTrapListenerData)updatealertListenerData.getAlertListener();
				 Criteria trapAlertListenerCriteria = session.createCriteria(AlertTrapListenerData.class);
			     trapAlertListenerCriteria.add(Restrictions.eq(LISTENER_ID, listenerInstanceId));
			     AlertTrapListenerData alertTrapListenerData =(AlertTrapListenerData)trapAlertListenerCriteria.uniqueResult();
			     alertTrapListenerData.setTrapServer(updateAlertTrapListenerData.getTrapServer());
			     alertTrapListenerData.setTrapVersion(updateAlertTrapListenerData.getTrapVersion());
			     alertTrapListenerData.setCommunity(updateAlertTrapListenerData.getCommunity());
			     alertTrapListenerData.setListenerId(updateAlertTrapListenerData.getListenerId());
			     alertTrapListenerData.setAdvanceTrap(updateAlertTrapListenerData.getAdvanceTrap());
			     alertTrapListenerData.setRepeatedMessageReduction(updateAlertTrapListenerData.getRepeatedMessageReduction());
			     session.update(alertTrapListenerData); 
			     session.flush();
				
			}else if(AlertListenerConstant.SYS_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(updatealertListenerData.getTypeId())){
				 
				 SYSLogAlertListenerData updateAlertSysListenerData = (SYSLogAlertListenerData)updatealertListenerData.getAlertListener();
				 Criteria sysAlertListenerCriteria = session.createCriteria(SYSLogAlertListenerData.class);
				 sysAlertListenerCriteria.add(Restrictions.eq(LISTENER_ID, listenerInstanceId));
			     SYSLogAlertListenerData alertSysListenerData =(SYSLogAlertListenerData)sysAlertListenerCriteria.uniqueResult();
			     alertSysListenerData.setAddress(updateAlertSysListenerData.getAddress());
			     alertSysListenerData.setFacility(updateAlertSysListenerData.getFacility());
			     alertSysListenerData.setListenerId(updateAlertSysListenerData.getListenerId());
			     alertSysListenerData.setRepeatedMessageReduction(updateAlertSysListenerData.getRepeatedMessageReduction());
			     session.update(alertSysListenerData); 
			     session.flush();
			}
			
			//Auditing Parameters
			staffData.setAuditName(alertListenerData.getName());
			staffData.setAuditId(alertListenerData.getAuditUId());
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_ALERT_LISTENER);
		} catch(ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Alert Listener configuration " + alertListenerName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Alert Listener configuration " + alertListenerName + ", Reason: " + hExp.getMessage(), hExp);
		} catch(Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Alert Listener configuration " + alertListenerName + ", Reason: " + exp.getMessage(), exp);
		}	
		
	}

	public List<AlertListenerData> getAlertListernerDataList() throws DataManagerException {
		List<AlertListenerData> alertListenerList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertListenerData.class);
			alertListenerList = criteria.list();

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive list of Alert Listener configuration, Reason: "+ hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive list of Alert Listener configuration, Reason: "+ exp.getMessage(), exp);
		}
		return alertListenerList;
	}
	
	public List<SYSLogNameValuePoolData> getSysLogNameValuePoolList() throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(SYSLogNameValuePoolData.class);
			criteria.addOrder(Order.asc(LISTENER_NAME));
			List<SYSLogNameValuePoolData> nameValuePool = criteria.list();
			return nameValuePool;
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive syslog name value data pool of Alert Listener configuration, Reason: "+ hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive syslog name value data pool of Alert Listener configuration, Reason: "+ exp.getMessage(), exp);
		}
	}

	@Override
	public AlertTypeData getAlertTypeData(String typeId) throws DataManagerException {
		AlertTypeData alertTypeData = null;
		try{
			Session session = getSession();			 
			Criteria criteria = session.createCriteria(AlertTypeData.class);
			alertTypeData = (AlertTypeData)criteria.add(Restrictions.eq("alertTypeId",typeId)).uniqueResult();
			session.flush();			
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Alert Type Data of Alert Listener configuration, Reason: " + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Alert Type Data of Alert Listener configuration, Reason: " + exp.getMessage(), exp);
		}
		return alertTypeData;
	}

	@Override
	public List<AlertTypeData> getAlertTypeDataList() throws DataManagerException {
		List<AlertTypeData> alertTypeDataList;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertTypeData.class);
			alertTypeDataList = criteria.list();

		} catch(HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Alert parameter list of Alert Listener configuration, Reason: " + hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Alert parameter list of Alert Listener configuration, Reason: " + exp.getMessage(), exp);
		}
		return alertTypeDataList;
	}

	@Override
	public void updateById(AlertListenerData alertListenerData, IStaffData staffData, String listenerId) throws DataManagerException {
		updateAlertListener(alertListenerData, staffData, LISTENER_ID, listenerId);
		
	}

	@Override
	public void updateByName(AlertListenerData alertListenerData, IStaffData staffData, String alertListenerName) throws DataManagerException {
		updateAlertListener(alertListenerData, staffData, LISTENER_NAME, alertListenerName);
		
	}

	/*@Override
	public String deleteById(long listenerId) throws DataManagerException {
		return delete(LISTENER_ID, listenerId);
	}

	@Override
	public String deleteByName(String listenerName) throws DataManagerException {
		return delete(LISTENER_NAME, listenerName);
	}*/

	@Override
	public AlertListenerData getAlertListenerDataById(String searchVal) throws DataManagerException {
		return getAlertListenerData(searchVal, LISTENER_ID);
	}

	@Override
	public AlertListenerData getAlertListenerDataByName(String searchVal) throws DataManagerException {
		return getAlertListenerData(searchVal, LISTENER_NAME);
	}

}
