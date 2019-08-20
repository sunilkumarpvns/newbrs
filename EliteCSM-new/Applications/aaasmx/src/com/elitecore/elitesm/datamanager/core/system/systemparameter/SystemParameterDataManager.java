package com.elitecore.elitesm.datamanager.core.system.systemparameter;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.ISystemParameterData;

public interface SystemParameterDataManager extends DataManager {
	
    /**
     * This method updateBasicDetail update the Value field in a list.
     * Object of ISystemParameterData must be supplied to it.
     * @param dbSystemParameterList 
     * @param lstValueData
     * @param staffData 
     * @return
     */
    public void updateBasicDetail( List<ISystemParameterData> dbSystemParameterList,List lstValueData, IStaffData staffData) throws DataManagerException;
    
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

	public void updateCaseSesitivity(String policyCaseSensitivity, String subscriberCaseSensitivity) throws DataManagerException;
}
