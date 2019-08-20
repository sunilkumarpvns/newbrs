package com.elitecore.netvertexsm.hibernate.servermgr.alert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.DuplicateEntityFoundException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.AlertListenerDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertFileListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerRelData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTrapListenerData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.alert.data.BaseAlertListener;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteAssert;
import com.elitecore.netvertexsm.util.constants.AlertListenerConstant;
import com.elitecore.netvertexsm.util.logger.Logger;

public class HAlertListenerDataManager extends HBaseDataManager implements AlertListenerDataManager{
	private final static String MODULE="HALERTLISTENERDATAMANAGER";

	public void create(AlertListenerData alertListenerData) throws DataManagerException {
		EliteAssert.notNull(alertListenerData,"alertListenerData must not be null.");
		try{
			verifyAlertListenerName(alertListenerData);
			Logger.logDebug(MODULE, "Listener Type is : "+alertListenerData.getTypeId());
			Logger.logDebug(MODULE, "Listener Id is : "+alertListenerData.getListenerId());
			Session session = getSession();
			session.save(alertListenerData);			
			session.flush();
			
			HashSet<AlertListenerRelData> alertListenerRelDataSet = alertListenerData.getAlertListenerRelDataSet();
			alertListenerData.setAlertListenerRelDataSet(null);
			
			if(alertListenerRelDataSet != null){
				Iterator<AlertListenerRelData> alertListenerRelDataIterator = alertListenerRelDataSet.iterator();
				while(alertListenerRelDataIterator.hasNext()){
					AlertListenerRelData tempAlertListenerRelData = alertListenerRelDataIterator.next();
					tempAlertListenerRelData.setInstanceId(alertListenerData.getListenerId());
					session.save(tempAlertListenerRelData);
					session.flush();
				}
			}
			
			Logger.logDebug(MODULE, "Listener Id is : "+alertListenerData.getListenerId());
			alertListenerData.getAlertListener().setListenerId(alertListenerData.getListenerId());
			session.save(alertListenerData.getAlertListener());
			session.flush();
		}catch(DuplicateEntityFoundException exp){
			throw new DuplicateEntityFoundException(exp.getMessage(), exp);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}		
	}

	public List<AlertListenerTypeData> getAvailableListenerType() throws DataManagerException {
		List<AlertListenerTypeData> alertListenerTypeList;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertListenerTypeData.class);
			alertListenerTypeList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return alertListenerTypeList;
	}
	
	public List<AlertTypeData> getAlertTypeData() throws DataManagerException {
		List<AlertTypeData> alertTypeDataList;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertTypeData.class);
			alertTypeDataList = criteria.add(Restrictions.isNull("parentId")).list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return alertTypeDataList;
	}
	
	public List<AlertTypeData> getEnabledAlertTypeData(String[] enabledAlertsArray) throws DataManagerException {
		List<AlertTypeData> enabledAlertTypeDataList;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertTypeData.class);
			enabledAlertTypeDataList = criteria.add(Restrictions.in("alertTypeId", enabledAlertsArray)).list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return enabledAlertTypeDataList;
	}

	public AlertListenerData getAlertListener(long listenerId)throws DataManagerException {
		AlertListenerData alertListenerData = null;
		try{
			Session session = getSession();			 
			Criteria criteria = session.createCriteria(AlertListenerData.class);
			alertListenerData = (AlertListenerData)criteria.add(Restrictions.eq("listenerId",listenerId)).uniqueResult();
			session.flush();
			if(alertListenerData.getTypeId().equals(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID)){
				criteria = session.createCriteria(AlertFileListenerData.class);
				AlertFileListenerData alertFileListenerData = (AlertFileListenerData)criteria.add(Restrictions.eq("listenerId",listenerId)).uniqueResult();
				BaseAlertListener baseAlertListener = alertFileListenerData;
				alertListenerData.setAlertListener(baseAlertListener);
				
			}else if(alertListenerData.getTypeId().equals(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID)){
				criteria = session.createCriteria(AlertTrapListenerData.class);
				AlertTrapListenerData alertTrapListenerData = (AlertTrapListenerData)criteria.add(Restrictions.eq("listenerId",listenerId)).uniqueResult();
				BaseAlertListener baseAlertListener = alertTrapListenerData;
				alertListenerData.setAlertListener(baseAlertListener);
				
			}
			session.flush();			

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
		return alertListenerData;
	}
	
	public List<AlertListenerRelData> getAlertListenerRelDataList(long listenerId)throws DataManagerException {
		List<AlertListenerRelData> alertListenerRelDataList = null;
		try{
			Session session = getSession();			 
			Criteria criteria = session.createCriteria(AlertListenerRelData.class);
			alertListenerRelDataList = (List<AlertListenerRelData>)criteria.add(Restrictions.eq("instanceId",listenerId)).list();
			session.flush();					
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
		return alertListenerRelDataList;
	}

	public void delete(List alertListenerIds) throws DataManagerException {

		try{
			Session session = getSession();
			long id= 0;
			for(int i =0;i<alertListenerIds.size();i++){
				id = Long.parseLong(alertListenerIds.get(i).toString().trim());
				AlertListenerData alertListenerData = getAlertListener(id);
				if(alertListenerData.getTypeId().equals(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID)){
					session.delete(alertListenerData.getAlertFileListenerData());
					session.flush();
				}else if(alertListenerData.getTypeId().equals(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID)){
					session.delete(alertListenerData.getAlertTrapListenerData());
					session.flush();
				}
				session.delete(alertListenerData);
				session.flush();
			}

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}

	}

	public List<AlertListenerData> getAlertListenerList(String name, String typeId) throws DataManagerException {
		List<AlertListenerData> alertListenerList = null;

		try{
			Session session = getSession();
			List<AlertListenerData> alertListenerIds = null;
			Criteria criteria = null;
			if((name == null || name.equals("")) && (typeId == null || typeId.equals(""))){

				criteria= session.createCriteria(AlertListenerData.class);
				alertListenerIds = criteria.list();

			}else if((name == null || name.equals("")) && (typeId!= null || !typeId.equals(""))){
				// retrieve on basis of type id. 
				criteria = session.createCriteria(AlertListenerData.class);
				alertListenerIds = criteria.add(Restrictions.eq("typeId",typeId.trim())).list();

			}else if((typeId == null || typeId.equals("")) && (name!= null || !name.equals(""))){
				//retrieve on basis of name 
				criteria = session.createCriteria(AlertListenerData.class);
				alertListenerIds = criteria.add(Restrictions.eq("name",name)).list();

			}else if((name!= null || name.equals("")) && (typeId!= null || !typeId.equals(""))){

				// retrieve on basis of both ..
				criteria = session.createCriteria(AlertListenerData.class);
				alertListenerIds = criteria.add(Restrictions.eq("name",name)).add(Restrictions.eq("typeId", typeId.trim())).list();

			}	        
			alertListenerIds = criteria.list();


			alertListenerList = new ArrayList<AlertListenerData>();
			for(int i =0;i<alertListenerIds.size();i++){
				alertListenerList.add(getAlertListener(alertListenerIds.get(i).getListenerId()));
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return alertListenerList;

	}


	public PageList search(AlertListenerData alertListenerData,int requiredPageNo, Integer pageSize) throws DataManagerException{

		PageList pageList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertListenerData.class);
            
			
			//add search criteria
			
			if(alertListenerData.getName() != null && alertListenerData.getName().length()>0){
				criteria.add(Restrictions.ilike("name","%"+alertListenerData.getName()+"%"));
			}
			if(alertListenerData.getTypeId() != null && alertListenerData.getTypeId().length() > 0 &&  !("0".equals(alertListenerData.getTypeId()))){
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

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}

		return pageList;

	}

	public void delete(long alertListenerId) throws DataManagerException
	{
	    
		 List aList=null;
	    
	        try{
	            Session session = getSession();
	            
	            Criteria criteria = session.createCriteria(AlertListenerData.class);
	        	criteria.add(Restrictions.eq("listenerId",alertListenerId));
	        	AlertListenerData alertListenerData=(AlertListenerData)criteria.list().get(0);
	        	
	        	String typeId= alertListenerData.getTypeId();
	        	Logger.logDebug(MODULE,"Type Id is:"+typeId);
	        	
	        	//deleting child records....
	        	

				Criteria alertListenerRelcriteria = session.createCriteria(AlertListenerRelData.class);
				List<AlertListenerRelData> alertListenerRelList = alertListenerRelcriteria.add(Restrictions.eq("instanceId", alertListenerId)).list();
				deleteObjectList(alertListenerRelList, session);
	        	
	        	 if(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID.equals(typeId)){
	        		 //file listener
	        		 Criteria fileCriteria=session.createCriteria(AlertFileListenerData.class);
	        		 fileCriteria.add(Restrictions.eq("listenerId",alertListenerId));
	        		 AlertFileListenerData  alertFileListenerData =(AlertFileListenerData)fileCriteria.list().get(0);
	        		 session.delete(alertFileListenerData);
	        		 
	        	 }else if(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID.equals(typeId)){
	        		 
	        		 //trap listener
	        		 Criteria trapCriteria=session.createCriteria(AlertTrapListenerData.class);
	        		 trapCriteria.add(Restrictions.eq("listenerId",alertListenerId));
	        		 AlertTrapListenerData alertTrapListenerData=(AlertTrapListenerData)trapCriteria.list().get(0);
	        		 session.delete(alertTrapListenerData);
	        		 
	        	 }
	        	 
	        	 session.delete(alertListenerData);
	        	 session.flush();
	        	

	        }catch(HibernateException hExp){
	            throw new DataManagerException(hExp.getMessage(), hExp);
	        }catch(Exception exp){
	        	throw new DataManagerException(exp.getMessage(), exp);
	        }

	
	
	}

	public void updateAlertListener(AlertListenerData updatealertListenerData)throws DataManagerException {
		EliteAssert.notNull(updatealertListenerData,"alertListenerData must not be null.");
		try{
			verifyAlertListenerNameForUpdate(updatealertListenerData);
			Logger.logDebug(MODULE, "Listener Type is : "+updatealertListenerData.getTypeId());
			Logger.logDebug(MODULE, "Listener Id is : "+updatealertListenerData.getListenerId());
			
			Session session = getSession();
			Criteria  criteria =session.createCriteria(AlertListenerData.class);
			criteria.add(Restrictions.eq("listenerId",updatealertListenerData.getListenerId()));
		    
			AlertListenerData alertListenerData = (AlertListenerData)criteria.uniqueResult();
			alertListenerData.setName(updatealertListenerData.getName());
			alertListenerData.setTypeId(updatealertListenerData.getTypeId());
			
			HashSet<AlertListenerRelData> alertListenerRelDataSet = updatealertListenerData.getAlertListenerRelDataSet();
				
			Criteria alertListenerRelcriteria = session.createCriteria(AlertListenerRelData.class);
			List<AlertListenerRelData> alertListenerRelList = alertListenerRelcriteria.add(Restrictions.eq("instanceId", updatealertListenerData.getListenerId())).list();
			deleteObjectList(alertListenerRelList, session);
			
			if(alertListenerRelDataSet != null){
				Iterator<AlertListenerRelData> alertListenerRelDataIterator = alertListenerRelDataSet.iterator();
				while(alertListenerRelDataIterator.hasNext()){
					AlertListenerRelData tempAlertListenerRelData = alertListenerRelDataIterator.next();
					tempAlertListenerRelData.setInstanceId(alertListenerData.getListenerId());
					session.save(tempAlertListenerRelData);
					session.flush();
				}
			}
			
		//	alertListenerData.setEnabledAlerts(updatealertListenerData.getEnabledAlerts());
			setUpdateAuditDetail(alertListenerData);
			session.update(alertListenerData);
			session.flush();
			if(AlertListenerConstant.FILE_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(updatealertListenerData.getTypeId())){
				     AlertFileListenerData updateAlertFileListenerData =(AlertFileListenerData)updatealertListenerData.getAlertListener();
				     Criteria fileAlertListenerCriteria = session.createCriteria(AlertFileListenerData.class);
				     fileAlertListenerCriteria.add(Restrictions.eq("listenerId",updatealertListenerData.getAlertListener().getListenerId()));
				     AlertFileListenerData alertFileListenerData =(AlertFileListenerData)fileAlertListenerCriteria.uniqueResult();
				     alertFileListenerData.setFileName(updateAlertFileListenerData.getFileName());
				     alertFileListenerData.setCompRollingUnit(updateAlertFileListenerData.getCompRollingUnit());
					 alertFileListenerData.setRollingType(updateAlertFileListenerData.getRollingType());
					 alertFileListenerData.setRollingUnit(updateAlertFileListenerData.getRollingUnit());
					 alertFileListenerData.setMaxRollingUnit(updateAlertFileListenerData.getMaxRollingUnit());
				     alertFileListenerData.setListenerId(updateAlertFileListenerData.getListenerId());
				     session.update(alertFileListenerData);
				     session.flush();
				     
			}else if(AlertListenerConstant.TRAP_ALERT_LISTENER_TYPE_ID.equalsIgnoreCase(updatealertListenerData.getTypeId())){
				 
				 AlertTrapListenerData updateAlertTrapListenerData = (AlertTrapListenerData)updatealertListenerData.getAlertListener();
				 Criteria trapAlertListenerCriteria = session.createCriteria(AlertTrapListenerData.class);
			     trapAlertListenerCriteria.add(Restrictions.eq("listenerId",updatealertListenerData.getAlertListener().getListenerId()));
			     AlertTrapListenerData alertTrapListenerData =(AlertTrapListenerData)trapAlertListenerCriteria.uniqueResult();
			     alertTrapListenerData.setTrapServer(updateAlertTrapListenerData.getTrapServer());
			     alertTrapListenerData.setTrapVersion(updateAlertTrapListenerData.getTrapVersion());
			     alertTrapListenerData.setCommunity(updateAlertTrapListenerData.getCommunity());
			     alertTrapListenerData.setListenerId(updateAlertTrapListenerData.getListenerId());
			     alertTrapListenerData.setAdvanceTrap(updateAlertTrapListenerData.getAdvanceTrap());
			     alertTrapListenerData.setSnmpRequestType(updateAlertTrapListenerData.getSnmpRequestType());
			     alertTrapListenerData.setTimeout(updateAlertTrapListenerData.getTimeout());
			     alertTrapListenerData.setRetryCount(updateAlertTrapListenerData.getRetryCount());				  

			     session.update(alertTrapListenerData); 
			     session.flush();
				
			}
			
			
		}catch(DuplicateEntityFoundException exp){
			throw new DuplicateEntityFoundException(exp.getMessage(), exp);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}		
		
	}
	
	private void verifyAlertListenerName(AlertListenerData alertListenerData) throws DuplicateEntityFoundException{
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(AlertListenerData.class);
		List list = criteria.add(Restrictions.eq("name",alertListenerData.getName())).list();
		if(list==null || list.isEmpty()){
			return;
		}else{
			throw new DuplicateEntityFoundException("Alert Listener Name Is Duplicated.");
		}
	}

	private void verifyAlertListenerNameForUpdate(AlertListenerData alertListenerData) throws DuplicateEntityFoundException{
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(AlertListenerData.class);
		List list = criteria.add(Restrictions.eq("name",alertListenerData.getName())).add(Restrictions.ne("listenerId", alertListenerData.getListenerId())).list();
		if(list==null || list.isEmpty()){
			return;
		}else{
			throw new DuplicateEntityFoundException("Alert Listener Name Is Duplicated.");
		}
	}

	public List<AlertListenerData> getAlertListernerDataList() throws DataManagerException {
		List<AlertListenerData> alertListenerList = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(AlertListenerData.class);
			alertListenerList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
		return alertListenerList;

	}
}
