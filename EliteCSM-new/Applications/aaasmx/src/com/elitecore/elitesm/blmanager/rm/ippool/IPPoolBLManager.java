package com.elitecore.elitesm.blmanager.rm.ippool;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.rm.ippool.IPPoolDataManager;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolDetailData;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.IPPoolConstant;
import com.elitecore.elitesm.util.ippool.IPPoolUtil;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.util.validation.CustomValidator;
import com.elitecore.elitesm.ws.rest.data.Status;


public class IPPoolBLManager extends BaseBLManager{
	private static final String MODULE = "IPPOOL";
	private static final int MAX_IP_COUNT = 65536;
	/**
	 * 
	 * @param ippoolData
	 * @param pageNo
	 * @param pageSize
	 * @param staffData 
	 * @return
	 * @throws DataManagerException
	 */
	
	public PageList search(IIPPoolData ippoolData, int pageNo, int pageSize, IStaffData staffData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IPPoolDataManager ippoolDataManager = getIPPoolDataManager(session);

		PageList lstIPPoolList;

		if (ippoolDataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}

		try{
			session.beginTransaction();

			lstIPPoolList = ippoolDataManager.search(ippoolData, pageNo, pageSize);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_IP_POOL_ACTION);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
		return lstIPPoolList;

	}

    public List<IIPPoolData> getList(IIPPoolData ippoolData) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        IPPoolDataManager ippoolDataManager = getIPPoolDataManager(session);
        List<IIPPoolData> lstIPPoolList;
        
    	if(ippoolDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	}
    	
    	try {
    		lstIPPoolList = ippoolDataManager.getList(ippoolData);
        	return lstIPPoolList;
		}catch(DataManagerException exp){
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
    }
    
  
    public void updateStatus(List<String> list,String commonStatus,IStaffData staffData,String actionAlias) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
    	
    	if(ipPoolDataManager == null ){
    		throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
    	}
    	
    	try{
    		updateStatusValidate(commonStatus);
	    	session.beginTransaction();
	    	for(String ipPoolId : list) {
	    		ipPoolDataManager.updateStatus(ipPoolId, commonStatus, new Timestamp(System.currentTimeMillis()),staffData,actionAlias);
	    		AuditUtility.doAuditing(session, staffData, ConfigConstant.CHANGE_IP_POOL_STATUS_ACTION);
	    	}
	    	commit(session);
    	}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
    }
    
    public void updateStatus(String ipPoolId,String commonStatus,IStaffData staffData,String actionAlias) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
    	
    	if(ipPoolDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
    	}
    	
    	try{
    		updateStatusValidate(commonStatus);
	    	session.beginTransaction();
	    	ipPoolDataManager.updateStatus(ipPoolId, commonStatus, new Timestamp(System.currentTimeMillis()),staffData,actionAlias);
	    	AuditUtility.doAuditing(session, staffData, ConfigConstant.CHANGE_IP_POOL_STATUS_ACTION);
	    	commit(session);
    	}catch(DataManagerException exp){
			rollbackSession(session);
			throw exp;
		}catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
    }
    
    public void create(IIPPoolData ippoolData, IStaffData staffData) throws DataManagerException{
    	List<IIPPoolData> iipPoolDataList = new ArrayList<IIPPoolData>();
    	iipPoolDataList.add(ippoolData);
    	create(iipPoolDataList, staffData, "false");
    }
    
    public Map<String, List<Status>> create(List<IIPPoolData> ippoolDataList, IStaffData staffData, String partialSuccess) throws DataManagerException{

    	for(IIPPoolData ipPoolData : ippoolDataList){
    		ipPoolData.setSystemGenerated("N");
    		ipPoolData.setEditable("Y");

    		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis()); 
    		ipPoolData.setStatusChangedDate(currentTimestamp);
    		ipPoolData.setLastModifiedDate(currentTimestamp);
    		ipPoolData.setCreateDate(currentTimestamp);

    		ipPoolData.setCreatedByStaffId(staffData.getStaffId());
    		ipPoolData.setLastModifiedByStaffId(staffData.getStaffId());
    	}
    	return insertRecords(IPPoolDataManager.class, ippoolDataList, staffData, ConfigConstant.CREATE_IP_POOL_ACTION, partialSuccess);
    }
    
    public IIPPoolData getIPPoolById(String ipPoolId) throws DataManagerException{
    	return getIPPool(ipPoolId, BY_ID);
    }
    
    public IIPPoolData getIPPoolByName(String ipPoolName) throws DataManagerException{
    	return getIPPool(ipPoolName, BY_NAME);
    }
    
    private IIPPoolData getIPPool(Object searchVal, boolean isByIdOrName) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
		
    	try {
    		IIPPoolData ipPoolData = null;
    		
    		if(isByIdOrName){
    			ipPoolData = ipPoolDataManager.getIPPoolById((String)searchVal);
    		}else{
    			ipPoolData = ipPoolDataManager.getIPPoolByName((String)searchVal);
    		}
    		
    		return ipPoolData ;
    	} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
    }
    
    public List<IIPPoolDetailData> getIPoolDetailDataList(IIPPoolDetailData ipPoolDetailData, int pageNo, int pageSize) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
		
    	try {
    		List<IIPPoolDetailData> ipPoolDetailDataList = ipPoolDataManager.getIPPoolDetailList(ipPoolDetailData, pageNo, pageSize);
        	return ipPoolDetailDataList;
		}catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
    }
    
    public List<IIPPoolDetailData> getDistinctIPPoolDetailByRangeList(IIPPoolDetailData ipPoolDetailData) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
		
    	try {
    		List<Object[]> ipAddressRangeDetailList = ipPoolDataManager.getDistinctIPPoolDetailByRangeList(ipPoolDetailData);
        	List<IIPPoolDetailData> ipPoolDetailDataList =  new ArrayList<IIPPoolDetailData>(ipAddressRangeDetailList.size());
        	for(Object object[] : ipAddressRangeDetailList){
        		if(object.length > 1){
        			IIPPoolDetailData ipDetailData = new IPPoolDetailData();
        			ipDetailData.setIpAddressRange((String) object[0]);
        			ipDetailData.setIpAddressRangeId((String) object[1]);
        			ipPoolDetailDataList.add(ipDetailData);
        		}
        	}
        	return ipPoolDetailDataList;
    	} catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
    }
    
    public long getIPPoolDetailCount(IIPPoolDetailData ipPoolDetailData) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
		
    	try {
    		return ipPoolDataManager.getIPPoolDetailTotalCount(ipPoolDetailData).longValue();
    	}catch (DataManagerException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		}finally {
			closeSession(session);
		}
    }
    
    public void update(IIPPoolData ipPoolData, boolean isDelete,IStaffData staffData,String actionAlias) throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
    	
		if(ipPoolDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
    	
    	try{
    		updateIPPoolDetailValidate(ipPoolData);
    		validateIPPool(ipPoolData);
    		session.beginTransaction();
    		
    		ipPoolDataManager.update(ipPoolData,staffData,actionAlias);
    		
    		if(isDelete){
    			ipPoolDataManager.deleteIPPoolDetailById(ipPoolData.getIpPoolId());
    			ipPoolDataManager.insertIPPoolDetails(ipPoolData, IPPoolDataManager.CREATE_IPPOOL_DETAIL);
    		}else{
    			ipPoolDataManager.insertIPPoolDetails(ipPoolData, IPPoolDataManager.UPDATE_IPPOOL_DETAIL);
    		}
    		
    		commit(session);
    	} catch(DataManagerException exp) {
    		rollbackSession(session);
    		throw exp;
    	} catch(Exception exp) {
    		exp.printStackTrace();
    		rollbackSession(session);
    		throw new DataManagerException(exp.getMessage(),exp);
    	} finally {
    		closeSession(session);
    	}
	}
    
    public void updateByName(IIPPoolData ipPoolData, boolean isDelete,IStaffData staffData,String actionAlias) throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
    	
		if(ipPoolDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}
    	
    	try{
    		updateIPPoolDetailValidate(ipPoolData);
    		validateIPPool(ipPoolData);
    		session.beginTransaction();
    		
    		ipPoolDataManager.updateByName(ipPoolData,staffData,actionAlias);
    		
    		if(isDelete){
    			ipPoolDataManager.deleteIPPoolDetailById(ipPoolData.getIpPoolId());
    			ipPoolDataManager.insertIPPoolDetails(ipPoolData, IPPoolDataManager.CREATE_IPPOOL_DETAIL);
    		}else{
    			ipPoolDataManager.insertIPPoolDetails(ipPoolData, IPPoolDataManager.UPDATE_IPPOOL_DETAIL);
    		}
    		
    		commit(session);
    	} catch(DataManagerException exp) {
    		rollbackSession(session);
    		throw exp;
    	} catch(Exception exp) {
    		exp.printStackTrace();
    		rollbackSession(session);
    		throw new DataManagerException(exp.getMessage(),exp);
    	} finally {
    		closeSession(session);
    	}
	}
    
    public void deleteByName(List<String> ipPoolNames, StaffData staffData) throws DataManagerException {
    	delete(ipPoolNames, staffData, BY_NAME);
	}
    
    public void deleteById(List<String> ipPoolIds,IStaffData staffData) throws DataManagerException {
		delete(ipPoolIds, staffData, BY_ID);
	}
    
    public void delete(List<String> ipPool,IStaffData staffData,boolean byIdOrName) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
    	String ipPoolName = null;
    	if(ipPoolDataManager == null ){
    		throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
    	}
    	
    	try{
			session.beginTransaction();
			if (Collectionz.isNullOrEmpty(ipPool) == false) {
				int size = ipPool.size();
				for (int i=0;i<size;i++) {
					if (ipPool.get(i) != null) {
						String idOrName = ipPool.get(i).toString().trim();
						if ( byIdOrName ) {
							ipPoolName = ipPoolDataManager.deleteById(idOrName);
						} else {
							ipPoolName = ipPoolDataManager.deleteByName(idOrName);			
						}
						staffData.setAuditName(ipPoolName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_IP_POOL_ACTION);
					}
				}
				commit(session);
			}
		}catch(DataManagerException dme){
			rollbackSession(session);
			throw dme;
		}catch(Exception exp){
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
    	
    }

    public void deleteIPPoolDetailByIPAddress(List<String> lstIpPoolId, String ipPoolId, IStaffData staffData) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);

    	if(ipPoolDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
    	}

    	try{
    		session.beginTransaction();
    		for(int i=0;i<lstIpPoolId.size();i++){
    			if(lstIpPoolId.get(i) != null && !"".equals(lstIpPoolId.get(i))){
    				ipPoolDataManager.deleteIPPoolDetailByIPAddress(ipPoolId,lstIpPoolId.get(i));
    				AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_IP_ADDRESS);
    			}
    		}
    		commit(session);
    	}catch(DataManagerException dme){
    		rollbackSession(session);
    		throw dme;
    	}catch(Exception exp){
    		exp.printStackTrace();
    		rollbackSession(session);
    		throw new DataManagerException(exp.getMessage(),exp);
    	} finally {
    		closeSession(session);
    	}
    }
    
    public void deleteIPPoolDetailByRange(String ipPoolId, String ipAddressRangeId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);

    	if(ipPoolDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
    	}

    	try{
    		session.beginTransaction();
    		ipPoolDataManager.deleteIPPoolDetailByRange(ipPoolId, ipAddressRangeId);
    		commit(session);
    	}catch(DataManagerException dme){
    		rollbackSession(session);
    		throw dme;
    	}catch(Exception exp){
    		exp.printStackTrace();
    		rollbackSession(session);
    		throw new DataManagerException(exp.getMessage(),exp);
    	} finally {
    		closeSession(session);
    	}
    }
    
    
    public Map<String, Integer> getIPPoolCheckAddressMap(List<String> ipAddresses, String nasIPAddress, String ipPoolId) throws DataManagerException{
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
    	
    	if(ipPoolDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	}
    	
    	try {
    		Map<String, Integer> ipPoolMap = new LinkedHashMap<String, Integer>(ipAddresses.size());
        	for(String ipAddress : ipAddresses) {
        		ipPoolMap.put(ipAddress, ipPoolDataManager.getIPPoolCount(ipAddress, nasIPAddress, ipPoolId));
        	}
        	
        	return ipPoolMap;
		}catch(DataManagerException dme){
    		throw dme;
    	}catch(Exception exp){
    		exp.printStackTrace();
    		throw new DataManagerException(exp.getMessage(),exp);
    	} finally {
			closeSession(session);
		}
    }
    
    public List<IPPoolData> getIPPoolData(IIPPoolDetailData iipPoolDetailData) throws DataManagerException {
    	IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
    	IPPoolDataManager ipPoolDataManager = getIPPoolDataManager(session);
    	
    	if(ipPoolDataManager == null){
    		throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
    	}
    	
    	try {
    		List<IPPoolData> ipPoolDetaildataList = ipPoolDataManager.getIPPoolDetailList(iipPoolDetailData); 
        	return ipPoolDetaildataList;
    	}catch(DataManagerException dme){
    		throw dme;
    	}catch(Exception exp){
    		exp.printStackTrace();
    		throw new DataManagerException(exp.getMessage(),exp);
    	} finally {
			closeSession(session);
		}
    }
	
    /**
     * @return Returns Data Manager instance for ippool data.
     */
    public IPPoolDataManager getIPPoolDataManager(IDataManagerSession session) {
        IPPoolDataManager ippoolDataManager = (IPPoolDataManager)DataManagerFactory.getInstance().getDataManager(IPPoolDataManager.class, session);
        return ippoolDataManager; 
    }
    
    private void validateIPPool(IIPPoolData ippoolData) throws DataManagerException{
    	// Name
    	if(EliteGenericValidator.isBlankOrNull(ippoolData.getName())){
    		throw (new DataValidationException("Invalid IPPOOL Name",(MODULE+"."+"Name").toLowerCase()));
    	}
    	// CommonStatusId
    	if(EliteGenericValidator.isBlankOrNull(ippoolData.getCommonStatusId())){
    		throw (new DataValidationException("Invalid IPPOOL CommonStatusId",(MODULE+"."+"CommonStatusId").toLowerCase()));
    	}
    	// CreatedByStaffId
    	if(EliteGenericValidator.isBlankOrNull(ippoolData.getCreatedByStaffId())){
    		throw (new DataValidationException("Invalid IPPOOL CreatedByStaffId",(MODULE+"."+"CreatedByStaffId").toLowerCase()));
    	}
    	// LastModifiedByStaffId
    	if(EliteGenericValidator.isBlankOrNull(ippoolData.getLastModifiedByStaffId())){
    		throw (new DataValidationException("Invalid IPPOOL LastModifiedByStaffId",(MODULE+"."+"LastModifiedByStaffId").toLowerCase()));
    	}
    	// CreateDate
    	if(EliteGenericValidator.isBlankOrNull(ippoolData.getCreateDate())){
    		throw (new DataValidationException("Invalid IPPOOL CreateDate",(MODULE+"."+"CreateDate").toLowerCase()));
    	}
    	// LastModifiedDate
    	if(EliteGenericValidator.isBlankOrNull(ippoolData.getLastModifiedDate())){
    		throw (new DataValidationException("Invalid IPPOOL LastModifiedDate",(MODULE+"."+"LastModifiedDate").toLowerCase()));
    	}
    	// SystemGenerated
    	if(EliteGenericValidator.isBlankOrNull(ippoolData.getSystemGenerated())){
    		throw (new DataValidationException("Invalid IPPOOL SystemGenerated",(MODULE+"."+"SystemGenerated").toLowerCase()));
    	}
    	// Editable
    	if(EliteGenericValidator.isBlankOrNull(ippoolData.getEditable())){
    		throw (new DataValidationException("Invalid IPPOOL Editable",(MODULE+"."+"Editable").toLowerCase()));
    	}
    	
    }
    
    private void updateIPPoolDetailValidate(IIPPoolData iPPoolData)throws DataManagerException{
        // IpPoolId
		if (EliteGenericValidator.isBlankOrNull(iPPoolData.getIpPoolId())) {
    		throw (new DataValidationException("Invalid IPPOOL IpPoolId",(MODULE+"."+"IpPoolId").toLowerCase()));
    	}
    }
    
    private void updateStatusValidate(String commonStatusId)throws DataManagerException{
        // commonStatusId
    	if(EliteGenericValidator.isBlankOrNull(commonStatusId)){
    		throw (new DataValidationException("Invalid IPPOOL commonStatusId",(MODULE+"."+"commonStatusId").toLowerCase()));
    	}
    }
    
    public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }
    
    public Set<IPPoolDetailData> getIPPoolDetailByCSVFile(String fileName,BufferedReader bufferedReader) throws IOException, DataValidationException {
		Set<IPPoolDetailData> ipPoolDetailDataSet = new LinkedHashSet<IPPoolDetailData>();
		if(fileName.endsWith(".csv")){
			String lineData = bufferedReader.readLine();
			Logger.logDebug(MODULE, "Header Row:"+lineData);
			if(!IPPoolConstant.CSV_FILE_HEADERROW.equalsIgnoreCase(lineData)){
				Logger.logWarn(MODULE, "Invalid CSV File Header.");
			}
			
			// Read and validate Header Column
			while( (lineData = bufferedReader.readLine()) != null ) {
				String[] fileDataArray = lineData.split(",");
				if(fileDataArray != null && fileDataArray.length >= 2) {
					if(CustomValidator.isValidIP4Address(fileDataArray[1])){
						ipPoolDetailDataSet.add(getIPAddress(fileDataArray[1], null, null));
					}
				}
			}
		}else{
			throw (new DataValidationException("File must be a valid CSV File."));
		}
		return ipPoolDetailDataSet; 
	}
    
    public Set<IPPoolDetailData> getIPPoolDetailByIPRange(String[]  ipAddressRangeArray, String[] ipAddressRangeIdArray, String[] oldIPAddressRangeArray, String[] oldIPAddressRangeIdArray ) throws DataValidationException{
		Set<IPPoolDetailData> ipPoolDetailDataSet = new LinkedHashSet<IPPoolDetailData>();
		
		if(ipAddressRangeArray != null && ipAddressRangeIdArray != null && ipAddressRangeArray.length == ipAddressRangeIdArray.length){
			long[][] ipAddressLongValues = getLongRangeValuesFromRange(ipAddressRangeArray, ipAddressRangeIdArray);
			
			long [][] oldIPAddressLongValues = null;
			if(oldIPAddressRangeArray != null && oldIPAddressRangeIdArray != null && oldIPAddressRangeArray.length == oldIPAddressRangeIdArray.length){
				oldIPAddressLongValues = getLongRangeValuesFromRange(oldIPAddressRangeArray, oldIPAddressRangeIdArray);
			}
			if(oldIPAddressLongValues != null){
				long [][] newipAddressLongValues = new long[oldIPAddressLongValues.length+ipAddressLongValues.length][2];
				for(int i=0;i<oldIPAddressLongValues.length;i++){
					newipAddressLongValues[i][0] = oldIPAddressLongValues[i][0];
					newipAddressLongValues[i][1] = oldIPAddressLongValues[i][1];
				}
				for(int j=0,i=oldIPAddressLongValues.length; j<ipAddressLongValues.length;j++,i++){
					newipAddressLongValues[i][0] = ipAddressLongValues[j][0];
					newipAddressLongValues[i][1] = ipAddressLongValues[j][1];
				}
				validateRangeIds((String[]) ArrayUtils.addAll(oldIPAddressRangeIdArray, ipAddressRangeIdArray));
				validateIPAddressRanges(newipAddressLongValues,(String[])ArrayUtils.addAll(oldIPAddressRangeArray, ipAddressRangeArray));
			}else{
				validateRangeIds(ipAddressRangeIdArray);
				validateIPAddressRanges(ipAddressLongValues,ipAddressRangeArray);
			}
			for(int index=0; index<ipAddressLongValues.length;index++){
				long startIPLong = ipAddressLongValues[index][0];
				long endIPLong = ipAddressLongValues[index][1];
				if(startIPLong == endIPLong){
					/*Case of single IP selected*/
					ipPoolDetailDataSet.add(getIPAddress(IPPoolUtil.longToIp(startIPLong), ipAddressRangeIdArray[index], ipAddressRangeArray[index]));
				}else if(startIPLong > endIPLong){
					/*swap value*/
					startIPLong ^= endIPLong;
					endIPLong ^= startIPLong;
					startIPLong ^= endIPLong;
				}
				int count = 0;
				while(startIPLong != endIPLong){
					ipPoolDetailDataSet.add(getIPAddress(IPPoolUtil.longToIp(startIPLong), ipAddressRangeIdArray[index], ipAddressRangeArray[index]));
					startIPLong++;
					if(count++ > MAX_IP_COUNT) break;
				}
			}
		}
		return ipPoolDetailDataSet;
	}

    private long[][] getLongRangeValuesFromRange(String[]  ipAddressRangeArray, String[] ipAddressRangeIdArray) throws DataValidationException{
		long[][] ipAddressLongValues = new long[ipAddressRangeArray.length][2];
		for(int index=0 ; index < ipAddressRangeArray.length; index++){
			if(ipAddressRangeArray[index].indexOf("-") != -1){
				String[] ipAddresseArray =  ipAddressRangeArray[index].split("-");
				if(CustomValidator.isValidIP4Address(ipAddresseArray[0]) && ipAddresseArray.length>1){
					if(NumberUtils.isNumber(ipAddresseArray[1])){
						long ipLongValue = IPPoolUtil.ipToLong(ipAddresseArray[0]);
						ipAddressLongValues[index][0] = ipLongValue;
						ipAddressLongValues[index][1] = ipLongValue+Long.parseLong(ipAddresseArray[1]);
					}else if(CustomValidator.isValidIP4Address(ipAddresseArray[1])){
						ipAddressLongValues[index][0] = IPPoolUtil.ipToLong(ipAddresseArray[0]);
						ipAddressLongValues[index][1] = IPPoolUtil.ipToLong(ipAddresseArray[1]) + 1;
					}else {
						throw (new DataValidationException("Invalid IP Address Range:"+ipAddresseArray[index] ,(MODULE+".IP Address Range")));
					}
				}else{
					throw (new DataValidationException("Invalid IP Address Range:"+ipAddresseArray[index] ,(MODULE+".IP Address Range")));
				}
			}/*else if(ipAddressRangeArray[index].indexOf("/") != -1){
				// Code for Handle Subnet Mask
			}*/else if(CustomValidator.isValidIP4Address(ipAddressRangeArray[index])){
				ipAddressLongValues[index][0] = IPPoolUtil.ipToLong(ipAddressRangeArray[index]);
				ipAddressLongValues[index][1] = IPPoolUtil.ipToLong(ipAddressRangeArray[index]);
			}else{
				throw (new DataValidationException("Invalid IP Address Range:"+ipAddressRangeArray[index] ,(MODULE+".IP Address Range")));
			}
		}
		return ipAddressLongValues;
	}
    
    private void validateRangeIds(String[] rangeIdArray) throws DataValidationException{
		for(int i=0 ; i < rangeIdArray.length ; i++){
			for(int j=i+1 ; j < rangeIdArray.length ; j++){
				if(rangeIdArray[i].equals(rangeIdArray[j])){
					throw (new DataValidationException("RangeId should be unique for IP Pool Detail.Please Correct it." ,(MODULE+".IP Address Range")));
				}
			}
		}
	}
	
	private void validateIPAddressRanges(long[][] ipAddressLongValues, String[] ipAddressRangeArray) throws DataValidationException {
		for(int i =0 ; i<ipAddressLongValues.length; i++){
			for(int j=i+1; j<ipAddressLongValues.length; j++){
				if((ipAddressLongValues[j][0] > ipAddressLongValues[i][0] && ipAddressLongValues[j][0] < ipAddressLongValues[i][1]) ||
				   (ipAddressLongValues[j][1] > ipAddressLongValues[i][0] && ipAddressLongValues[j][1] < ipAddressLongValues[i][1])) {
						throw (new DataValidationException("Range "+ipAddressRangeArray[j]+" overlaps  Range "+ipAddressRangeArray[i]+".Please Correct it." ,(MODULE+".IP Address Range")));
				}
			}
		}
	}
		
	private IPPoolDetailData getIPAddress(String ipAddress, String ipRangeId, String ipRange){
		IPPoolDetailData ipPoolDetailData = new IPPoolDetailData();
		ipPoolDetailData.setIpAddress(ipAddress);
		ipPoolDetailData.setAssigned("N");
		ipPoolDetailData.setReserved("N");
		if(ipRangeId != null) {
			ipPoolDetailData.setIpAddressRangeId(ipRangeId);
		}
		if(ipRange != null) {
			ipPoolDetailData.setIpAddressRange(ipRange);
		}
		return ipPoolDetailData;
	}
}
