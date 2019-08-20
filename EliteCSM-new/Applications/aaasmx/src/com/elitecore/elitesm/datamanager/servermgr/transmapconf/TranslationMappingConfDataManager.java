package com.elitecore.elitesm.datamanager.servermgr.transmapconf;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;


public interface TranslationMappingConfDataManager  extends DataManager {
	
	public List<TranslatorTypeData> getToTranslatorTypeDataList() throws DataManagerException;
	
	public List<TranslatorTypeData> getFromTranslatorTypeDataList() throws DataManagerException;
	
	public List<TranslationMappingConfData> getTranslationMappingConfigList(String toType,String fromType) throws DataManagerException;
	
	@Override
	public String create(Object object) throws DataManagerException;
	
	public PageList search(TranslationMappingConfData translationMappingConfData ,int pageNo, int pageSize) throws DataManagerException;
	
	public TranslationMappingConfData getTranslationMappingConfData(String translationMapConfigId) throws DataManagerException;
	
	public TranslatorTypeData getTranslatorTypeData(String translatorTypeId) throws DataManagerException;
	
	public List<TranslationMappingConfData> getTranslationMappingConfigList(TranslationMappingConfData translationMappingConfData) throws DataManagerException;
	
	public List<TranslationMappingConfData> getRadiusToRadiusTranslationMappingList() throws DataManagerException;
	
	public void updateBasicDetail(TranslationMappingConfData translationMappingConfData,String translationMappingId,IStaffData staffData,String actionAlias)throws DataManagerException;

	public List<TranslationMappingConfData> getDiaToDiaTranslationMapping()throws DataManagerException;

	public List<TranslationMappingConfData> getTranslationMappingList()throws DataManagerException;

	public List<TranslationMappingConfData> getDiaToRadTranslationMappingList()throws DataManagerException;

	public TranslationMappingConfData getTranslationMappingConfDataById(String translationMappingId) throws DataManagerException;

	public TranslationMappingConfData getTranslationMappingConfDataByName(String translationMappingName) throws DataManagerException;

	public String deleteById(String translationMappingId) throws DataManagerException;

	public String deleteByName(String translationMappingName) throws DataManagerException;

	public void updateById(TranslationMappingConfData translationMappingData, IStaffData staffData, String translationMappingId) throws DataManagerException;

	public void updateByName(TranslationMappingConfData translationMappingData, IStaffData staffData, String translationMappingName) throws DataManagerException;
}
