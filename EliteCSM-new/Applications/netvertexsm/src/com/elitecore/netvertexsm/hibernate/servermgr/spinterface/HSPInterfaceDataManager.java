package com.elitecore.netvertexsm.hibernate.servermgr.spinterface;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.SPInterfaceDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HSPInterfaceDataManager extends HBaseDataManager implements SPInterfaceDataManager {

	/**
	 * This method will create the LDAP SP Interface Driver.
	 * @author Manjil Purohit
	 * @param driverData
	 * @throws DataManagerException
	 */
	public void create(DriverInstanceData driverData) throws DataManagerException, DuplicateParameterFoundExcpetion {
		try{
			Session session = getSession();
			session.save(driverData);								
			session.flush();
			if(driverData.getDriverTypeId()==1) {
				driverData.getDatabaseDriverData().setDriverInstanceId(driverData.getDriverInstanceId());				
				session.save(driverData.getDatabaseDriverData());
				session.flush();				
				
				for(int i=0; i<driverData.getDbFieldMapList().size(); i++) {
					driverData.getDbFieldMapList().get(i).setDbSpInterfaceId(driverData.getDatabaseDriverData().getDatabaseSpInterfaceId());
					session.save(driverData.getDbFieldMapList().get(i));
				}
			}else if(driverData.getDriverTypeId()==2) {
				driverData.getLdapDriverData().setDriverInstanceId(driverData.getDriverInstanceId());
				session.save(driverData.getLdapDriverData());
				session.flush();
				
				for(int i=0; i<driverData.getLdapFieldMapList().size(); i++) {
					driverData.getLdapFieldMapList().get(i).setLdapSPInterfaceId(driverData.getLdapDriverData().getLdapSPInterfaceId());
					session.save(driverData.getLdapFieldMapList().get(i));
				}
			}			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
	/**
	 * This method will create the LDAP SP Interface Driver.
	 * @author Manjil Purohit
	 * @param databaseSPInterfaceDriverData
	 * @throws DataManagerException
	 */
	public void create(DatabaseSPInterfaceData databaseSPInterfaceDriverData) throws DataManagerException, DuplicateParameterFoundExcpetion {
		try{
			Session session = getSession();
			session.save(databaseSPInterfaceDriverData);								
			session.flush();
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	public void create(DBFieldMapData databaseFieldMapData) throws DataManagerException, DuplicateParameterFoundExcpetion {
		try{
			Session session = getSession();
			session.save(databaseFieldMapData);								
			session.flush();
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
		public PageList search(DriverInstanceData driverInstanceData, int pageNo, int pageSize) throws DataManagerException {
			PageList pageList = null;
			try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(DriverInstanceData.class);
				Criteria driverTypeCriteria = criteria.createCriteria("driverTypeData");
				driverTypeCriteria.add(Restrictions.isNull("serviceTypeId"));
							
				if(driverInstanceData.getName()!=null && Strings.isNullOrBlank(driverInstanceData.getName()) == false) {
					criteria.add(Restrictions.ilike("name", driverInstanceData.getName(), MatchMode.ANYWHERE));
				}
				if(driverInstanceData.getDriverTypeId()!=null) {
					criteria.add(Restrictions.eq("driverTypeId", driverInstanceData.getDriverTypeId()));
				}
				int totalItems = criteria.list().size();		
				criteria.setFirstResult(((pageNo-1) * pageSize));
				
				if(pageSize > 0) {
					criteria.setMaxResults(pageSize);
				}
				List driverList = criteria.list();
		        long totalPages = (long)Math.ceil(totalItems/pageSize);
		        if(totalItems%pageSize == 0)
					totalPages-=1;
		        
		        pageList = new PageList(driverList, pageNo, totalPages ,totalItems);
			}catch (HibernateException hExe) {
				throw new DataManagerException(hExe.getMessage(), hExe);
			}catch (Exception exp) {
				throw new DataManagerException(exp.getMessage(), exp);
			}		
			return pageList;
		}
	/**
	 * This method will return the search result.
	 * @author Manjil Purohit
	 * @param ldapSpInterfaceDriverData
	 * @return pageList
	 */
	public PageList search(LDAPSPInterfaceData ldapSpInterfaceDriverData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(LDAPSPInterfaceData.class);			
			Criteria driverTypeCriteria = criteria.createCriteria("driverTypeData");
			driverTypeCriteria.add(Restrictions.isNull("serviceTypeId"));
						
			if(ldapSpInterfaceDriverData.getLdapDsId()!=null) {
				criteria.add(Restrictions.eq("ldapDsId", ldapSpInterfaceDriverData.getLdapDsId()));
			}if(ldapSpInterfaceDriverData.getDriverInstanceId()!=null) {
				criteria.add(Restrictions.eq("driverInstanceId", ldapSpInterfaceDriverData.getDriverInstanceId()));
			}
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			
			if(pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			List ldapDriverList = criteria.list();
	        
	        long totalPages = (long)Math.ceil(totalItems/10);
	        pageList = new PageList(ldapDriverList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
		return pageList;
	}
	
	/**
	 * This method will return the search result.
	 * @author Manjil Purohit
	 * @param databaseSPInterfaceDriverData
	 * @return pageList
	 */
	
	public List<DriverInstanceData> getDriverInstanceList() throws DataManagerException {
		List<DriverInstanceData> driverInstanceList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class);
		    Criteria driverTypeCriteria = criteria.createCriteria("driverTypeData");
			driverTypeCriteria.add(Restrictions.isNull("serviceTypeId"));
			driverInstanceList = criteria.list();
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return driverInstanceList;
	}

	
	public PageList search(DatabaseSPInterfaceData databaseSPInterfaceDriverData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseSPInterfaceData.class);			
			Criteria driverTypeCriteria = criteria.createCriteria("driverTypeData");
			driverTypeCriteria.add(Restrictions.isNull("serviceTypeId"));
						
			if(databaseSPInterfaceDriverData.getDatabaseDsId()!=null) {
				criteria.add(Restrictions.eq("databaseDsId", databaseSPInterfaceDriverData.getDatabaseDsId()));
			}if(databaseSPInterfaceDriverData.getDriverInstanceId()!=null) {
				criteria.add(Restrictions.eq("driverInstanceId", databaseSPInterfaceDriverData.getDriverInstanceId()));
			}
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			
			if(pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			List dbDriverList = criteria.list();
	        
	        long totalPages = (long)Math.ceil(totalItems/10);
	        pageList = new PageList(dbDriverList, pageNo, totalPages ,totalItems);
	        
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return pageList;
	}
	
	
	public void update(LDAPSPInterfaceData ldapSpInterfaceDriverData, long ldapSPInterfaceId) throws DataManagerException {
		
	}
	public void updateDriverInstanceData(DriverInstanceData driverData,Long oldDriverTypeId) throws DataManagerException{
		try{
			Session session = getSession();
			session.update(driverData);
			if(driverData.getDriverTypeId()==1){
				session.saveOrUpdate(driverData.getDatabaseDriverData());
			}else if(driverData.getDriverTypeId()==2){
					session.saveOrUpdate(driverData.getLdapDriverData());
			} 
			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	public LDAPSPInterfaceData getLDAPSPInterfaceDriverData(LDAPSPInterfaceData ldapspInterfaceDriver) throws DataManagerException {	
		Session session = getSession();
		Criteria criteria = session.createCriteria(LDAPSPInterfaceData.class);;
			
		if(ldapspInterfaceDriver.getDriverInstanceId()>0) {
			criteria.add(Restrictions.eq("driverInstanceId", ldapspInterfaceDriver.getDriverInstanceId()));
		}	
		return (LDAPSPInterfaceData)criteria.uniqueResult();
	}
	
	public DatabaseSPInterfaceData getDatabaseSPInterfaceData(DatabaseSPInterfaceData databasespInterfaceDriver) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(DatabaseSPInterfaceData.class);
		
		if(databasespInterfaceDriver.getDriverInstanceId() > 0) {
			criteria.add(Restrictions.eq("driverInstanceId", databasespInterfaceDriver.getDriverInstanceId()));
		}
		return (DatabaseSPInterfaceData)criteria.uniqueResult();
	}
	
	public List<DBFieldMapData> getDBFieldMapData(DBFieldMapData dbFieldMap) throws DataManagerException {
		List<DBFieldMapData> fieldMapList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DBFieldMapData.class);
			if(dbFieldMap.getDbSpInterfaceId()>0) {
				criteria.add(Restrictions.eq("dbSpInterfaceId", dbFieldMap.getDbSpInterfaceId()));
			}
			fieldMapList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return fieldMapList;
	}
	public List<LDAPFieldMapData> getLDAPFieldMapData(LDAPFieldMapData ldapFieldMap) throws DataManagerException {
		List<LDAPFieldMapData> ldapfieldMapList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(LDAPFieldMapData.class);
			if(ldapFieldMap.getLdapSPInterfaceId()>0) {
				criteria.add(Restrictions.eq("ldapSPInterfaceId", ldapFieldMap.getLdapSPInterfaceId()));
			}
			ldapfieldMapList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return ldapfieldMapList;
	}

	
	@Override
	public List<DriverTypeData> getSPInterfaceDriverTypeList()
			throws DataManagerException {
		List<DriverTypeData> driverTypeList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverTypeData.class).add(Restrictions.isNull("serviceTypeId")).add(Restrictions.eq("status","Y"));
			driverTypeList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return driverTypeList;
	}
	/**
     * This method returns all the DB Field Map.
     * All the not null fields will be compared for equality.
     * @author Manjil Purohit
     * @return dbFieldMapList
	 * @throws DataManagerException 
     */
	public List<DBFieldMapData> getDBFieldMapList() throws DataManagerException {
		List<DBFieldMapData> dbFieldMapList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DBFieldMapData.class);
			dbFieldMapList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return dbFieldMapList;
	}
	
	/**
     * This method returns all the LDAP Field Map.
     * All the not null fields will be compared for equality.
     * @author Manjil Purohit
     * @return ldapFieldMapList
	 * @throws DataManagerException 
     */
	public List<LDAPFieldMapData> getLDAPFieldMapList() throws DataManagerException {
		List<LDAPFieldMapData> ldapFieldMapList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(LDAPFieldMapData.class);
			ldapFieldMapList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return ldapFieldMapList;
	}
	
	
	public void deleteDBFieldMapData(Long dbspInterfaceId)throws DataManagerException {
		try{
			Session session = getSession();
		
			Criteria criteria=session.createCriteria(DBFieldMapData.class);
			criteria.add(Restrictions.eq("dbSpInterfaceId", dbspInterfaceId));
			List<DBFieldMapData> listDBSPInterfaceData=null;
			listDBSPInterfaceData=criteria.list();
			
			for(DBFieldMapData dbSPInterfaceData:listDBSPInterfaceData){
				session.delete(dbSPInterfaceData);
			}
			session.flush();
			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	public void deleteLDAPFieldMapData(Long ldapSPInterfaceId)throws DataManagerException {
		try{
			Session session = getSession();
		
			Criteria criteria=session.createCriteria(LDAPFieldMapData.class);
			criteria.add(Restrictions.eq("ldapSPInterfaceId",ldapSPInterfaceId));
			List<LDAPFieldMapData> listldapData=null;
			listldapData=criteria.list();
			
			for(LDAPFieldMapData ldapFieldData:listldapData){
				session.delete(ldapFieldData);
			}
			session.flush();
			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public void delete(List<Long> driverIdList) throws DataManagerException {
		try{
			Session session = getSession();			
			for(Long driverInstanceId : driverIdList){
				DriverInstanceData driverInstance = (DriverInstanceData) session.load(DriverInstanceData.class, driverInstanceId);
				session.delete(driverInstance);
			}			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
		
	}

	
	public DriverInstanceData getDriverInstanceData(DriverInstanceData driverInstanceData) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(DriverInstanceData.class);
		Criteria driverTypeCriteria = criteria.createCriteria("driverTypeData");
		driverTypeCriteria.add(Restrictions.isNull("serviceTypeId"));
		if(driverInstanceData.getDriverInstanceId()>0) {
			criteria.add(Restrictions.eq("driverInstanceId", driverInstanceData.getDriverInstanceId()));
		}	
		return (DriverInstanceData)criteria.uniqueResult();
	}
	
	public List<String> getResourceGroupRelationData(String sprId) throws DataManagerException {
		Session session = getSession();
		List<String> groupNames = Collectionz.newArrayList();
/*		Criteria criteria = session.createCriteria(ResourceGroupRelationData.class);
		ResourceData resourceData = new ResourceData();
		resourceData.setId(sprId);
		criteria.add(Restrictions.eq("resourceData", resourceData));
		List<ResourceGroupRelationData> resourceGroupRelationDatas = criteria.list();
		for(ResourceGroupRelationData resourceGroupRelationData : resourceGroupRelationDatas){
			groupNames.add(resourceGroupRelationData.getGroupData().getName());
		}
*/		return groupNames;
	}

	@Override
	public void updateLDAPDriver(DriverInstanceData driverInstanceData) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class).add(Restrictions.eq("driverInstanceId",driverInstanceData.getDriverInstanceId()));
			DriverInstanceData data  = (DriverInstanceData)criteria.uniqueResult();
			data.setLastModifiedByStaffId(driverInstanceData.getLastModifiedByStaffId());
			data.setName(driverInstanceData.getName());
			data.setDescription(driverInstanceData.getDescription());
			data.setLastModifiedDate(driverInstanceData.getLastModifiedDate());
			session.update(data);
			Set<LDAPSPInterfaceData> ldapSPInterfaceDriverSet = driverInstanceData.getLdapspInterfaceDriverSet();
			LDAPSPInterfaceData newLDAPDriverData  = null;
			if(ldapSPInterfaceDriverSet!=null && !ldapSPInterfaceDriverSet.isEmpty()){
				for (Iterator<LDAPSPInterfaceData> iterator = ldapSPInterfaceDriverSet.iterator(); iterator.hasNext();) {
					newLDAPDriverData =  iterator.next();
				}
			}
			
			if(newLDAPDriverData!=null){
				criteria = session.createCriteria(LDAPSPInterfaceData.class).add(Restrictions.eq("driverInstanceId",driverInstanceData.getDriverInstanceId()));
				LDAPSPInterfaceData ldapSPInterfaceDriverData  = (LDAPSPInterfaceData)criteria.uniqueResult();
				ldapSPInterfaceDriverData.setExpiryDatePattern(newLDAPDriverData.getExpiryDatePattern());
				ldapSPInterfaceDriverData.setLdapDsId(newLDAPDriverData.getLdapDsId());
				ldapSPInterfaceDriverData.setPasswordDecryptType(newLDAPDriverData.getPasswordDecryptType());
				ldapSPInterfaceDriverData.setQueryMaxExecTime(newLDAPDriverData.getQueryMaxExecTime());
				session.update(ldapSPInterfaceDriverData);
				
				//delete old field map
				criteria = session.createCriteria(LDAPFieldMapData.class).add(Restrictions.eq("ldapSPInterfaceId",ldapSPInterfaceDriverData.getLdapSPInterfaceId()));
				List dbFieldList = criteria.list();
				deleteObjectList(dbFieldList, session);

				
				//add new field map
				Set<LDAPFieldMapData> ldapFieldMapSet = newLDAPDriverData.getFieldMapSet();
				if(ldapFieldMapSet!=null && !ldapFieldMapSet.isEmpty()){
					for (Iterator<LDAPFieldMapData> iterator = ldapFieldMapSet.iterator(); iterator.hasNext();) {
						LDAPFieldMapData ldapFieldMapData =  iterator.next();
						ldapFieldMapData.setLdapSPInterfaceId(ldapSPInterfaceDriverData.getLdapSPInterfaceId());
						session.save(ldapFieldMapData);
					}
				}
				
				
			}else{
				throw new DataManagerException("LDAP SP InterfaceDriverData object not found");
			}
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}	
	}

	@Override
	public void updateDBDriver(DriverInstanceData driverInstanceData)
			throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class).add(Restrictions.eq("driverInstanceId",driverInstanceData.getDriverInstanceId()));
			DriverInstanceData data  = (DriverInstanceData)criteria.uniqueResult();
			data.setLastModifiedByStaffId(driverInstanceData.getLastModifiedByStaffId());
			data.setLastModifiedDate(driverInstanceData.getLastModifiedDate());
			data.setName(driverInstanceData.getName());
			data.setDescription(driverInstanceData.getDescription());
			session.update(data);
			Set<DatabaseSPInterfaceData> dbSPInterfaceDriverSet = driverInstanceData.getDatabaseSPInterfaceDriverSet();
			DatabaseSPInterfaceData newDBDriverData  = null;
			if(dbSPInterfaceDriverSet!=null && !dbSPInterfaceDriverSet.isEmpty()){
				for (Iterator<DatabaseSPInterfaceData> iterator = dbSPInterfaceDriverSet.iterator(); iterator.hasNext();) {
					newDBDriverData =  iterator.next();
				}
			}
			
			if(newDBDriverData!=null){
				criteria = session.createCriteria(DatabaseSPInterfaceData.class).add(Restrictions.eq("driverInstanceId",driverInstanceData.getDriverInstanceId()));
				DatabaseSPInterfaceData dbDriverData  = (DatabaseSPInterfaceData)criteria.uniqueResult();
				dbDriverData.setDatabaseDsId(newDBDriverData.getDatabaseDsId());
				dbDriverData.setDbQueryTimeout(newDBDriverData.getDbQueryTimeout());
				dbDriverData.setIdentityField(newDBDriverData.getIdentityField());
				dbDriverData.setMaxQueryTimeoutCnt(newDBDriverData.getMaxQueryTimeoutCnt());
				dbDriverData.setTableName(newDBDriverData.getTableName());
				session.update(dbDriverData);
				
				//delete old field map
				criteria = session.createCriteria(DBFieldMapData.class).add(Restrictions.eq("dbSpInterfaceId",dbDriverData.getDatabaseSpInterfaceId()));
				List dbFieldList = criteria.list();
				deleteObjectList(dbFieldList, session);

				
				//add new field map
				Set<DBFieldMapData> dbFieldMapSet = newDBDriverData.getDbFieldMapSet();
				if(dbFieldMapSet!=null && !dbFieldMapSet.isEmpty()){
					for (Iterator<DBFieldMapData> iterator = dbFieldMapSet.iterator(); iterator.hasNext();) {
						DBFieldMapData dbFieldMapData =  iterator.next();
						dbFieldMapData.setDbSpInterfaceId(newDBDriverData.getDatabaseSpInterfaceId());
						session.save(dbFieldMapData);
					}
				}
				
				
			}else{
				throw new DataManagerException("Database SP InterfaceDriverData object not found");
			}
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}	
		
	}

	

}
