package com.elitecore.elitesm.datamanager.diameter.dictionary;

import java.sql.Timestamp;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicData;
import com.elitecore.elitesm.datamanager.diameter.dictionary.data.DiameterdicParamDetailData;

public interface DictionaryDataManager extends DataManager {

	List getDatatype() throws DataManagerException;

	void create(DiameterdicData dictionaryData) throws DataManagerException;

	List<DiameterdicData> getAllList(DiameterdicData data) throws DataManagerException;

	List<DiameterdicParamDetailData> getDictionaryParameterDetailList(String dictionaryId) throws DataManagerException;

	void updateDictionary(DiameterdicData serverData) throws DataManagerException;

	void updateStatus(String dictionaryId, String commonStatusId,Timestamp timestamp)throws DataManagerException;

	void delete(String dictionaryId)throws DataManagerException;

	public List<DiameterdicData> getOnlyDiameterDictionaryDataList() throws DataManagerException;
	
	List<DiameterdicParamDetailData> getOnlyDiameterDictionaryParameterList(String searchNameOrAttributeId) throws DataManagerException;
	
}
