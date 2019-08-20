package com.elitecore.netvertexsm.datamanager.core.system.systemparameter;

import java.util.List;
import java.util.Set;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.ISystemParameterData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public interface SystemParameterDataManager extends DataManager {
	
    /**
     * This method updateBasicDetail update the Value field in a list.
     * Object of ISystemParameterData must be supplied to it.
     * @param systemParameterData
     * @return
     */
    public void updateBasicDetail(List lstValueData) throws DataManagerException;
    
	/**
     * This method returns all the available SystemParameters.
     * @return
     */
    public List getList() throws DataManagerException;	
    
    /**
     * This method returns all the available SystemParameters.
     * @return
     */
  //  public List getList(ISystemParameterData systemParameterData) throws DataManagerException;
    
    /**
     * This method creates a new User.
     * Object of ISystemParameterData must be supplied to it.
     * @param systemParameterData
     * @return
     */
    //public void  create(ISystemParameterData systemParameterData) throws DataManagerException;	
    
    /**
     * This method returns all the SystemParameterData  matching the give criteria with Paging.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param systemParameterData, pageNo, pageSize
     * @return
     */
   // public PageList search(ISystemParameterData systemParameterData, int pageNo, int pageSize) throws DataManagerException;
      
    /**
     * This method returns all the available SystemParameters.
     * @return
     */
    //public ISystemParameterData getSystemParameter(ISystemParameterData systemParameterData) throws DataManagerException;

    //public void delete(String lstPrameterIds) throws DataManagerException;
    public ISystemParameterData getSystemParameter(String profileAlias) throws DataManagerException;

	public List getSystemParameterValuePoolList(long parameterId) throws DataManagerException;
}
