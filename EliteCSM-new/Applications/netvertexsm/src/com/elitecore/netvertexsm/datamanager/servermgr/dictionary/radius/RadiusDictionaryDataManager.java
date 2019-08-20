package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.data.DataTypeData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.RadiusDictionaryParamDetailData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.IRadiusDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.radius.data.IRadiusDictionaryParamDetailData;

public interface RadiusDictionaryDataManager extends DataManager {

    public List<RadiusDictionaryData> getAllList() throws DataManagerException;

    public List<RadiusDictionaryData> getList() throws DataManagerException;
    
    public List<RadiusDictionaryData> getOnlyDictionaryDataList() throws DataManagerException;
    
    public List<RadiusDictionaryData> getList(IRadiusDictionaryData dictionaryData) throws DataManagerException;

    public List<RadiusDictionaryData> getAllList(IRadiusDictionaryData dictionaryData) throws DataManagerException;

    public RadiusDictionaryData updateStatus(long dictionaryId,String commonStatus,Timestamp statusChangeDate) throws DataManagerException;

    public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailList(IRadiusDictionaryParamDetailData dictionaryParameterDetailData) throws DataManagerException;
    
    public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailList(Long dictionaryId)throws DataManagerException;
    
    public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailList(IRadiusDictionaryParamDetailData dictionaryParameterDetailData, String criteriaName) throws DataManagerException;
    
    public void create(IRadiusDictionaryData dictionaryData) throws DataManagerException;

    public void updateBasicDetails(IRadiusDictionaryData dictionaryData,Timestamp statusChangeDate) throws DataManagerException;

    public void delete(long dictionaryId) throws DataManagerException;
    
    public IRadiusDictionaryData getAllListByVendor(IRadiusDictionaryData dictionaryData) throws DataManagerException;
    
    public List<DataTypeData> getDatatype() throws DataManagerException;
    
    public RadiusDictionaryData getDictionaryDataByVendor(long vendorId) throws DataManagerException;
    
    public String getDictionaryParameterName(String vendorId,long dictionaryParameterId) throws DataManagerException;
    
    public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailAllList() throws DataManagerException;
    
    public List<RadiusDictionaryParamDetailData> getDictionaryParameterDetailAllList(String searchByName) throws DataManagerException;
    
    public IRadiusDictionaryParamDetailData getDictionaryIdByName(String name) throws DataManagerException;
    
    public String getDictionaryParamDetail(String vendorId,long dictionaryParameterId) throws DataManagerException;
    
    public RadiusDictionaryData getDictionaryByName(long dictionaryId, String searchByName)throws DataManagerException;

	public RadiusDictionaryData getDictionaryData(Long dictionaryId) throws DataManagerException ;

	public RadiusDictionaryData updateDictionary(IRadiusDictionaryData dictionaryData) throws DataManagerException;

	public List<RadiusDictionaryParamDetailData> getOnlyDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException;
	
	public RadiusDictionaryParamDetailData getDictionaryParameterDetailData(Long dictionaryParameterId) throws DataManagerException;

	public RadiusDictionaryParamDetailData getOnlyDictionaryParametersByAttributeId(String attributeId) throws DataManagerException;
}
