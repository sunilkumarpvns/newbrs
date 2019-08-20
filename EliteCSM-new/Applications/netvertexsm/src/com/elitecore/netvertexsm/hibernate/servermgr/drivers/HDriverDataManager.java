package com.elitecore.netvertexsm.hibernate.servermgr.drivers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.DriverDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVDriverData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVStripFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.ServiceTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRDriverData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.dbcdrdriver.data.DBCDRFieldMappingData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DatabaseSPInterfaceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HDriverDataManager extends HBaseDataManager implements DriverDataManager {

	/**
	 * This method will create the LDAP SPR Driver.
	 * @author Manjil Purohit
	 * @param driverData
	 * @throws DataManagerException
	 */
	public void create(DriverInstanceData driverData) throws DataManagerException, DuplicateParameterFoundExcpetion {
		try{
			Session session = getSession();
			if(driverData.getDriverTypeId()==4) {
				Set<CSVDriverData> csvDriverDataSet = driverData.getCsvDriverDataSet();
				driverData.setCsvDriverDataSet(null);
				session.save(driverData);								
				session.flush();
				for (Iterator<CSVDriverData> iterator = csvDriverDataSet.iterator(); iterator.hasNext();) {
					CSVDriverData csvDriverData =  iterator.next();
					csvDriverData.setDriverInstanceId(driverData.getDriverInstanceId());
					Set<CSVFieldMapData> csvFieldMapSet  = csvDriverData.getCsvFieldMapSet();
					Set<CSVStripFieldMapData> csvStripFieldMapDataSet = csvDriverData.getCsvStripFieldMapDataSet();
					
					csvDriverData.setCsvFieldMapSet(null);
					csvDriverData.setCsvStripFieldMapDataSet(null);
					session.save(csvDriverData);
					session.flush();				
					
					if(csvFieldMapSet!=null){
						for (Iterator<CSVFieldMapData> itFieldMap = csvFieldMapSet.iterator(); itFieldMap.hasNext();) {
							CSVFieldMapData csvFieldMapData = itFieldMap.next();
							csvFieldMapData.setCsvDriverId(csvDriverData.getCsvDriverId());
							session.save(csvFieldMapData);	
						}
					}
					
					if(csvStripFieldMapDataSet!=null){
						for (Iterator<CSVStripFieldMapData> csvStripFieldMap = csvStripFieldMapDataSet.iterator(); csvStripFieldMap.hasNext();) {
							CSVStripFieldMapData csvStripFieldMapData = csvStripFieldMap.next();
							csvStripFieldMapData.setCsvDriverID(csvDriverData.getCsvDriverId());
							session.save(csvStripFieldMapData);	
						}
					}										
				}
			}else if (driverData.getDriverTypeId()==5) {
				Set<DBCDRDriverData> dbcdrDriverDataSet = driverData.getDbcdrDriverDataSet();
				driverData.setDbcdrDriverDataSet(null);
				session.save(driverData);								
				session.flush();
				for (Iterator<DBCDRDriverData> iterator = dbcdrDriverDataSet.iterator(); iterator.hasNext();) {
					DBCDRDriverData dbcdrDriverData =  iterator.next();
					dbcdrDriverData.setDriverInstanceId(driverData.getDriverInstanceId());
					Set<DBCDRFieldMappingData> dbcdrFieldMappingDataSet  = dbcdrDriverData.getDbcdrDriverFieldMappingDataSet();
					
					dbcdrDriverData.setDbcdrDriverFieldMappingDataSet(null);
					session.save(dbcdrDriverData);
					session.flush();				
					
					if(dbcdrFieldMappingDataSet!=null){
						for (Iterator<DBCDRFieldMappingData> itFieldMap = dbcdrFieldMappingDataSet.iterator(); itFieldMap.hasNext();) {
							DBCDRFieldMappingData dbcdrFieldMappingData = itFieldMap.next();
							dbcdrFieldMappingData.setDbCDRDriverID(dbcdrDriverData.getDbCDRDriverID());
							session.save(dbcdrFieldMappingData);	
						}
					}										
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
	 * This method will create the LDAP SPR Driver.
	 * @author Manjil Purohit
	 * @param databaseSPInterfaceData
	 * @throws DataManagerException
	 */
	public void create(DatabaseSPInterfaceData databaseSPInterfaceData) throws DataManagerException, DuplicateParameterFoundExcpetion {
		try{
			Session session = getSession();
			session.save(databaseSPInterfaceData);								
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

	/**
	 * This method will return the search result.
	 * @author Manjil Purohit
	 * @param ldapSpInterfaceData
	 * @return pageList
	 */
	public PageList search(LDAPSPInterfaceData ldapSpInterfaceData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(LDAPSPInterfaceData.class);			
			
			if(ldapSpInterfaceData.getLdapDsId()!=null) {
				criteria.add(Restrictions.eq("ldapDsId", ldapSpInterfaceData.getLdapDsId()));
			}if(ldapSpInterfaceData.getDriverInstanceId()!=null) {
				criteria.add(Restrictions.eq("driverInstanceId", ldapSpInterfaceData.getDriverInstanceId()));
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
	 * @param databaseSPInterfaceData
	 * @return pageList
	 */
	public PageList search(DatabaseSPInterfaceData databaseSPInterfaceData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseSPInterfaceData.class);			
			
			if(databaseSPInterfaceData.getDatabaseDsId()!=null) {
				criteria.add(Restrictions.eq("databaseDsId", databaseSPInterfaceData.getDatabaseDsId()));
			}if(databaseSPInterfaceData.getDriverInstanceId()!=null) {
				criteria.add(Restrictions.eq("driverInstanceId", databaseSPInterfaceData.getDriverInstanceId()));
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
	
	/**
	 * This method will return the search result.
	 * @author Manjil Purohit
	 * @param driverInstanceData
	 * @return pageList
	 */
	public PageList search(DriverInstanceData driverInstanceData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class);
			Criteria driverTypeCriteria = criteria.createCriteria("driverTypeData");
			driverTypeCriteria.add(Restrictions.isNotNull("serviceTypeId"));
			String name=driverInstanceData.getName();
			
			if((name != null && !"".equals(name))){
				name = "%"+name+"%";
				criteria.add(Restrictions.ilike("name",name));
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
	 * @param driverIdList
	 */
	public void delete(List<Long> driverIdList) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class).add(Restrictions.in("driverInstanceId", driverIdList));
			List list = criteria.list();
			deleteObjectList(list, session);
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	/**
     * This method returns all the Driver Instance.
     * All the not null fields will be compared for equality.
     * @author Manjil Purohit
     * @return driverInstanceList
	 * @throws DataManagerException 
     */
	public List<DriverInstanceData> getDriverInstanceList() throws DataManagerException {
		List<DriverInstanceData> driverInstanceList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class);
		    Criteria driverTypeCriteria = criteria.createCriteria("driverTypeData");
			driverTypeCriteria.add(Restrictions.isNotNull("serviceTypeId"));
						
			driverInstanceList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return driverInstanceList;
	}

	public DriverInstanceData getDriverInstanceData(DriverInstanceData driverInstanceData) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(DriverInstanceData.class);
		Criteria driverTypeCriteria = criteria.createCriteria("driverTypeData");
		driverTypeCriteria.add(Restrictions.isNotNull("serviceTypeId"));
		if(driverInstanceData.getDriverInstanceId()>0) {
			criteria.add(Restrictions.eq("driverInstanceId", driverInstanceData.getDriverInstanceId()));
		}	
		return (DriverInstanceData)criteria.uniqueResult();
	}
	
	
	/**
     * This method returns all the Service Type.
     * All the not null fields will be compared for equality.
     * @author Manjil Purohit
     * @return serviceTypeList
	 * @throws DataManagerException 
     */
	public List<ServiceTypeData> getServiceTypeList() throws DataManagerException {
		List<ServiceTypeData> serviceTypeList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(ServiceTypeData.class);
			serviceTypeList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return serviceTypeList;
	}
	
	/**
     * This method returns all the Driver Type.
     * All the not null fields will be compared for equality.
     * @author Manjil Purohit
     * @return driverTypeList
	 * @throws DataManagerException 
     */
	public List<DriverTypeData> getDriverTypeList() throws DataManagerException {
		List<DriverTypeData> driverTypeList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverTypeData.class).add(Restrictions.isNotNull("serviceTypeId"));
			driverTypeList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return driverTypeList;
	}
	
	public List<DriverInstanceData> getDriverInstanceList(long serviceTypeId) throws DataManagerException {
		List<DriverInstanceData> driverInstanceDataList = new ArrayList<DriverInstanceData>();
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverTypeData.class).add(Restrictions.eq("serviceTypeId", serviceTypeId));
			List<DriverTypeData> list = criteria.list();
			if(list!=null){
				Long driverTypeIds[] = new Long[list.size()];
				for(int i =0;i<list.size();i++){
					driverTypeIds[i] = list.get(i).getDriverTypeId();
				}
				if(driverTypeIds != null && driverTypeIds.length > 0){
					driverInstanceDataList = session.createCriteria(DriverInstanceData.class).add(Restrictions.in("driverTypeId",driverTypeIds)).list();
				}				
				return driverInstanceDataList;
			}
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return driverInstanceDataList;
	}
	
	
	/**
     * This method returns all the Logical Name Value Pool.
     * All the not null fields will be compared for equality.
     * @author Manjil Purohit
     * @return logicalNamePoolList
	 * @throws DataManagerException 
     */
	public List<LogicalNameValuePoolData> getLogicalNamePoolList() throws DataManagerException {
		List<LogicalNameValuePoolData> logicalNamePoolList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(LogicalNameValuePoolData.class);
			logicalNamePoolList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return logicalNamePoolList;
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

	@Override
	public void updateCSVDriver(DriverInstanceData driverInstanceData) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class).add(Restrictions.eq("driverInstanceId",driverInstanceData.getDriverInstanceId()));
			DriverInstanceData data  = (DriverInstanceData)criteria.uniqueResult();
			data.setLastModifiedByStaffId(driverInstanceData.getLastModifiedByStaffId());
			data.setLastModifiedDate(driverInstanceData.getLastModifiedDate());
			session.update(data);
			Set<CSVDriverData> csvDriverDataSet = driverInstanceData.getCsvDriverDataSet();
			CSVDriverData newCSVDriverData  = null;
			if(csvDriverDataSet!=null && !csvDriverDataSet.isEmpty()){
				for (Iterator<CSVDriverData> iterator = csvDriverDataSet.iterator(); iterator.hasNext();) {
					newCSVDriverData =  iterator.next();
				}
			}
			
			if(newCSVDriverData!=null){
				criteria = session.createCriteria(CSVDriverData.class).add(Restrictions.eq("driverInstanceId",driverInstanceData.getDriverInstanceId()));
				CSVDriverData csvData  = (CSVDriverData)criteria.uniqueResult();
				csvData.setAllocatingProtocol(newCSVDriverData.getAllocatingProtocol());
				csvData.setArchiveLocation(newCSVDriverData.getArchiveLocation());
				csvData.setDefaultDirName(newCSVDriverData.getDefaultDirName());
				csvData.setDelimiter(newCSVDriverData.getDelimiter());
				csvData.setCdrTimestampFormat(newCSVDriverData.getCdrTimestampFormat());
				csvData.setDefaultDirName(newCSVDriverData.getDefaultDirName());
				csvData.setFailOvertime(newCSVDriverData.getFailOvertime());
				csvData.setFileLocation(newCSVDriverData.getFileLocation());
				csvData.setFileName(newCSVDriverData.getFileName());
				csvData.setFileRollingType(newCSVDriverData.getFileRollingType());
				csvData.setFolderName(newCSVDriverData.getFolderName());
				csvData.setGlobalization(newCSVDriverData.getGlobalization());
				csvData.setHeader(newCSVDriverData.getHeader());
				csvData.setAddress(newCSVDriverData.getAddress());
				csvData.setPassword(newCSVDriverData.getPassword());
				
				csvData.setPostOperation(newCSVDriverData.getPostOperation());
				csvData.setPrefixFileName(newCSVDriverData.getPrefixFileName());
				csvData.setRange(newCSVDriverData.getRange());
				csvData.setSequencePosition(newCSVDriverData.getSequencePosition());
				csvData.setRemoteLocation(newCSVDriverData.getRemoteLocation());
				csvData.setRollingUnit(newCSVDriverData.getRollingUnit());
				csvData.setUserName(newCSVDriverData.getUserName());
				
				csvData.setReportingType(newCSVDriverData.getReportingType());
				csvData.setUsageKeyHeader(newCSVDriverData.getUsageKeyHeader());
				csvData.setInputOctetsHeader(newCSVDriverData.getInputOctetsHeader());
				csvData.setOutputOctetsHeader(newCSVDriverData.getOutputOctetsHeader());
				csvData.setTotalOctetsHeader(newCSVDriverData.getTotalOctetsHeader());
				csvData.setUsageTimeHeader(newCSVDriverData.getUsageTimeHeader());
				session.update(csvData);
				
				//delete old field map
				criteria = session.createCriteria(CSVFieldMapData.class).add(Restrictions.eq("csvDriverId",csvData.getCsvDriverId()));
				List csvFieldList = criteria.list();
				deleteObjectList(csvFieldList, session);
				
				//add new field map
				Set<CSVFieldMapData> csvFieldMapSet = newCSVDriverData.getCsvFieldMapSet();
				if(csvFieldMapSet!=null && !csvFieldMapSet.isEmpty()){
					for (Iterator<CSVFieldMapData> iterator = csvFieldMapSet.iterator(); iterator.hasNext();) {
						CSVFieldMapData csvFieldData =  iterator.next();
						csvFieldData.setCsvDriverId(csvData.getCsvDriverId());
						session.save(csvFieldData);
					}
				}
				
				//delete old strip field map
				criteria = session.createCriteria(CSVStripFieldMapData.class).add(Restrictions.eq("csvDriverID",csvData.getCsvDriverId()));
				List csvStripFieldList = criteria.list();
				deleteObjectList(csvStripFieldList, session);
				
				//add new strip field map
				Set<CSVStripFieldMapData> csvStripFieldMapSet = newCSVDriverData.getCsvStripFieldMapDataSet();
				if(csvStripFieldMapSet!=null && !csvStripFieldMapSet.isEmpty()){
					for (Iterator<CSVStripFieldMapData> iterator = csvStripFieldMapSet.iterator(); iterator.hasNext();) {
						CSVStripFieldMapData csvStripFieldMapData =  iterator.next();
						csvStripFieldMapData.setCsvDriverID(csvData.getCsvDriverId());						
						session.save(csvStripFieldMapData);
					}
				}
			}else{
				throw new DataManagerException("CSVDriverData object not found");
			}
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
	}
	
	@Override
	public void updateDBCDRDriver(DriverInstanceData driverInstanceData) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class).add(Restrictions.eq("driverInstanceId",driverInstanceData.getDriverInstanceId()));
			DriverInstanceData data  = (DriverInstanceData)criteria.uniqueResult();
			data.setLastModifiedByStaffId(driverInstanceData.getLastModifiedByStaffId());
			data.setLastModifiedDate(driverInstanceData.getLastModifiedDate());
			session.update(data);
			Set<DBCDRDriverData> dbcdrDriverDataSet = driverInstanceData.getDbcdrDriverDataSet();
			DBCDRDriverData newDBCDRDriverData  = null;
			if(dbcdrDriverDataSet!=null && !dbcdrDriverDataSet.isEmpty()){
				for (Iterator<DBCDRDriverData> iterator = dbcdrDriverDataSet.iterator(); iterator.hasNext();) {
					newDBCDRDriverData =  iterator.next();
				}
			}
			
			if(newDBCDRDriverData!=null){
				criteria = session.createCriteria(DBCDRDriverData.class).add(Restrictions.eq("driverInstanceId",driverInstanceData.getDriverInstanceId()));
				DBCDRDriverData dbcdrDriverData  = (DBCDRDriverData)criteria.uniqueResult();
				dbcdrDriverData.setDatabaseDSID(newDBCDRDriverData.getDatabaseDSID());
				dbcdrDriverData.setDbQueryTimeout(newDBCDRDriverData.getDbQueryTimeout());
				dbcdrDriverData.setQueryTimeout(newDBCDRDriverData.getQueryTimeout());
				dbcdrDriverData.setTableName(newDBCDRDriverData.getTableName());
				dbcdrDriverData.setIdentityField(newDBCDRDriverData.getIdentityField());
				dbcdrDriverData.setSequenceName(newDBCDRDriverData.getSequenceName());
				dbcdrDriverData.setStoreAllCDR(newDBCDRDriverData.getStoreAllCDR());
				dbcdrDriverData.setIsBatchUpdate(newDBCDRDriverData.getIsBatchUpdate());
				dbcdrDriverData.setBatchSize(newDBCDRDriverData.getBatchSize());
				dbcdrDriverData.setBatchUpdateInterval(newDBCDRDriverData.getBatchUpdateInterval());
				dbcdrDriverData.setQueryTimeout(newDBCDRDriverData.getQueryTimeout());
				dbcdrDriverData.setSessionIDFieldName(newDBCDRDriverData.getSessionIDFieldName());
				dbcdrDriverData.setCreateDateFieldName(newDBCDRDriverData.getCreateDateFieldName());
				dbcdrDriverData.setLastModifiedFieldName(newDBCDRDriverData.getLastModifiedFieldName());
				dbcdrDriverData.setTimeStampformat(newDBCDRDriverData.getTimeStampformat());
				dbcdrDriverData.setReportingType(newDBCDRDriverData.getReportingType());
				dbcdrDriverData.setUsageKeyFieldName(newDBCDRDriverData.getUsageKeyFieldName());
				dbcdrDriverData.setInputOctetsFieldName(newDBCDRDriverData.getInputOctetsFieldName());
				dbcdrDriverData.setOutputOctetsFieldName(newDBCDRDriverData.getOutputOctetsFieldName());
				dbcdrDriverData.setTotalOctetsFieldName(newDBCDRDriverData.getTotalOctetsFieldName());
				dbcdrDriverData.setUsageTimeFieldName(newDBCDRDriverData.getUsageTimeFieldName());
				dbcdrDriverData.setMaxQueryTimeoutCount(newDBCDRDriverData.getMaxQueryTimeoutCount());
				session.update(dbcdrDriverData);
				
				//delete old field map
				criteria = session.createCriteria(DBCDRFieldMappingData.class).add(Restrictions.eq("dbCDRDriverID",dbcdrDriverData.getDbCDRDriverID()));
				List dbCDRFieldMapList = criteria.list();
				deleteObjectList(dbCDRFieldMapList, session);
				
				//add new field map
				Set<DBCDRFieldMappingData> dbcdrFieldMappingDataSet = newDBCDRDriverData.getDbcdrDriverFieldMappingDataSet();
				if(dbcdrFieldMappingDataSet!=null && !dbcdrFieldMappingDataSet.isEmpty()){
					for (Iterator<DBCDRFieldMappingData> iterator = dbcdrFieldMappingDataSet.iterator(); iterator.hasNext();) {
						DBCDRFieldMappingData dbcdrFieldMappingData =  iterator.next();
						dbcdrFieldMappingData.setDbCDRDriverID(dbcdrDriverData.getDbCDRDriverID());
						session.save(dbcdrFieldMappingData);
					}
				}
			}else{
				throw new DataManagerException("CSVDriverData object not found");
			}
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
	}

	@Override
	public void update(DriverInstanceData driverInstanceData) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class).add(Restrictions.eq("driverInstanceId",driverInstanceData.getDriverInstanceId()));
			DriverInstanceData data  = (DriverInstanceData)criteria.uniqueResult();
			data.setName(driverInstanceData.getName());
			data.setDescription(driverInstanceData.getDescription());
			data.setLastModifiedByStaffId(driverInstanceData.getLastModifiedByStaffId());
			data.setLastModifiedDate(driverInstanceData.getLastModifiedDate());
			session.update(data);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		
	}

}
