package com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data.DiameterDictionaryData;
import com.elitecore.netvertexsm.datamanager.servermgr.dictionary.diameter.data.DiameterDictionaryParamDetailData;

public interface DiameterDictionaryDataManager extends DataManager {

	List getDatatype() throws DataManagerException;

	void create(DiameterDictionaryData dictionaryData) throws DataManagerException;

	List<DiameterDictionaryData> getAllList(DiameterDictionaryData data) throws DataManagerException;

	List<DiameterDictionaryParamDetailData> getDictionaryParameterDetailList(Long dictionaryId) throws DataManagerException;

	void updateDictionary(DiameterDictionaryData serverData) throws DataManagerException;

	void updateStatus(long dictionaryId, String commonStatusId,Timestamp timestamp)throws DataManagerException;

	void delete(long dictionaryId)throws DataManagerException;

	public List<DiameterDictionaryData> getOnlyDiameterDictionaryDataList() throws DataManagerException;
	
	List<DiameterDictionaryParamDetailData> getOnlyDiameterDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException;
	
}
