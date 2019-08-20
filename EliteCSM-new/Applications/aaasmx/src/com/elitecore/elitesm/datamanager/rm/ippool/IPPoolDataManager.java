package com.elitecore.elitesm.datamanager.rm.ippool;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData;
import com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData;

public interface IPPoolDataManager extends DataManager {
	
	public static final int CREATE_IPPOOL_DETAIL = 0;
	public static final int UPDATE_IPPOOL_DETAIL = -1;
	

    /**
     * This method returns all the available IPPool Entities.
     * @return
     */
    public List<IIPPoolData> getAllList() throws DataManagerException;

    /**
     * This method returns all the available IPPool Entities.
     * @return
     */
    public List<IIPPoolData> getList() throws DataManagerException;
    
    /**
     * This method returns all the IPPool Entities matching the given criteria.
     * All the not null fields will be compared for equality.
     * @param iPPoolData
     * @return
     */
    public List<IIPPoolData> getList(IIPPoolData iPPoolData) throws DataManagerException;



    /**
     * This method returns all the dictionary matching the give criteria.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param dictionaryData
     * @return
     */
    public PageList search(IIPPoolData iPPoolData, int pageNo,int pageSize) throws DataManagerException;
    
    /**
     * This method changes Status of the IP Pool whose id is supplied.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param ipPoolId, commonStatus
     * @return
     */
    public void updateStatus(String ipPoolId, String commonStatus, Timestamp statusChangeDate,IStaffData staffData,String actionAlias) throws DataManagerException;

    /**
     * This method deletes IP Pool whose id is supplied.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param ipPoolId
     * @return
     */
    public void delete(String ipPoolId) throws DataManagerException;
    
    @Override
    public String create(Object object) throws DataManagerException;
    
    public void update(IIPPoolData iPPoolData,IStaffData staffData,String actionAlias) throws DataManagerException;
    
    public List<IPPoolData> getIPPoolDetailList(IIPPoolDetailData iipPoolDetailData) throws DataManagerException;
    
    public Integer getIPPoolCount(String ipAdress, String nasIPAddress, String ipPoolId) throws DataManagerException;

	public List<IIPPoolDetailData> getIPPoolDetailList(IIPPoolDetailData ipPoolDetailData, int pageNo, int pageSize) throws DataManagerException;

	public Long getIPPoolDetailTotalCount(IIPPoolDetailData ipPoolDetailData) throws DataManagerException;

	public List<Object[]> getDistinctIPPoolDetailByRangeList(IIPPoolDetailData ipPoolDetailData) throws DataManagerException;

	public void insertIPPoolDetails(IIPPoolData iPPoolData, long serialNumber) throws DataManagerException;
	
	public void deleteIPPoolDetailByRange(String ipPoolId, String ipAddressRangeId)throws DataManagerException;
	
	public void deleteIPPoolDetailByIPAddress(String ipPoolId,String ipAddress) throws DataManagerException;

	public void deleteIPPoolDetailById(String ipPoolId) throws DataManagerException;

	public IPPoolData getIPPoolById(String ipPoolId) throws DataManagerException;

	public IPPoolData getIPPoolByName(String ipPoolName) throws DataManagerException;

	public String deleteById(String ipPoolId) throws DataManagerException;

	public String deleteByName(String idOrName)throws DataManagerException;

	public void updateByName(IIPPoolData ipPoolData, IStaffData staffData,
			String actionAlias) throws DataManagerException;
}
