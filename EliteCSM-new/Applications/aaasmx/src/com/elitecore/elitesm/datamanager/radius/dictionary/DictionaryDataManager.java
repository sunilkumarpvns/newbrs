package com.elitecore.elitesm.datamanager.radius.dictionary;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryParameterDetailData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;

public interface DictionaryDataManager extends DataManager {

    /**
     * This method returns all the available dictionaries.
     * @return
     */
    public List getAllList() throws DataManagerException;

    /**
     * This method returns all the available active dictionaries.
     * @return
     */
    public List getList() throws DataManagerException;
    
    public List<DictionaryData> getOnlyDictionaryDataList() throws DataManagerException;
    
    public List<DictionaryData> getDictionaryDataList() throws DataManagerException;
    
    /**
     * This method returns all the dictionary matching the given criteria.
     * All the not null fields will be compared for equality.
     * @param dictionaryData
     * @return
     */
    public List getList(IDictionaryData dictionaryData) throws DataManagerException;

    /**
     * This method returns all of the visible dictionaries matching the given criteria.
     * All the not null fields will be compared for equality.
     * @param dictionaryData
     * @return
     */
    public List getAllList(IDictionaryData dictionaryData) throws DataManagerException;


    /**
     * This method returns all the dictionary matching the give criteria.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param dictionaryData
     * @return
     */
    public List search(IDictionaryData dictionaryData) throws DataManagerException;
    
    
    /**
     * This method updates the Status of Radius Policy Dictionary.
     * Object of Dictionary Id must be supplied to it along with required Status.
     * @param radiusPolicyData
     * @return
     */
    public List updateStatus(String dictionaryId,String commonStatus,Timestamp statusChangeDate) throws DataManagerException;

    /**
     * This method returns List of Dictionary Parameters for given Dictionary Name.
     * Object of Dictionary Name must be supplied to it.
     * @param radiusPolicyData
     * @return
     */
    public List getDictionaryParameterDetailList(IDictionaryParameterDetailData dictionaryParameterDetailData) throws DataManagerException;
    
    /**
     * This method created new Dictionary along with all its details.
     * contains operator.
     * 
     * @param dictionaryData
     * @return
     */
    public void create(IDictionaryData dictionaryData) throws DataManagerException;

    /**
     * This method updates Basic Details like Name, Description and Status.
     * 
     * @param dictionaryData
     * @return
     */
    public void updateBasicDetails(IDictionaryData dictionaryData,Timestamp statusChangeDate) throws DataManagerException;
    /**
     * This method Deletes selected Dictionary and all its Parameters.
     * 
     * @param dictionaryData
     * @return
     */
    public void delete(String dictionaryId) throws DataManagerException;
    

    /**
     * This method returns a dictionary identified by Vendor ID.
     * Vendor ID will be compared for equality.
     * @param dictionaryData
     * @return
     */
    public IDictionaryData getAllListByVendor(IDictionaryData dictionaryData) throws DataManagerException;
    
    
    /**
     * This method returns a List of DataType
     *  @return LIst 
     */
    public List getDatatype() throws DataManagerException;
    
    public DictionaryData getDictionaryDataByVendor(long vendorId) throws DataManagerException;
    
    public String getDictionaryParameterName(String vendorId,String dictionaryParameterId) throws DataManagerException;
    
    
    
    public List getDictionaryParameterDetailList(IDictionaryParameterDetailData dictionaryParameterDetailData, String criteriaName) throws DataManagerException;
    
    public List getDictionaryParameterDetailAllList() throws DataManagerException;
    
    public List getDictionaryParameterDetailAllList(String searchByName) throws DataManagerException;
    
    public IDictionaryParameterDetailData getDictionaryIdByName(String name) throws DataManagerException;
    
    public String getDictionaryParamDetail(String vendorId,String dictionaryParameterId) throws DataManagerException;
    
    public DictionaryData getDictionaryByName(String dictionaryId, String searchByName)throws DataManagerException;

	public List<DictionaryParameterDetailData> getDictionaryParameterDetailList(Long dictionaryId)throws DataManagerException;

	public DictionaryData getDictionaryData(String dictionaryId) throws DataManagerException ;

	public DictionaryData updateDictionary(IDictionaryData dictionaryData) throws DataManagerException;

	public List<DictionaryParameterDetailData> getOnlyDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException;
	
	public DictionaryParameterDetailData getDictionaryParameterDetailData(String dictionaryParameterId) throws DataManagerException;

	public DictionaryParameterDetailData getOnlyDictionaryParametersByAttributeId(String attributeId) throws DataManagerException;
}
