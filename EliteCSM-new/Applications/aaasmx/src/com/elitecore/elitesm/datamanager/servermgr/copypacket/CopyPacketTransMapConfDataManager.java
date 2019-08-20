package com.elitecore.elitesm.datamanager.servermgr.copypacket;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslatorTypeData;


public interface CopyPacketTransMapConfDataManager extends DataManager {
	
	public CopyPacketTranslationConfData getCopyPacketTransMapConfigData(String copyPacketTransConfId) throws DataManagerException;
	public List<CopyPacketTranslationConfData> getCopyPacketTransMapConfigList(CopyPacketTranslationConfData copyPacketTranslationConfData) throws DataManagerException;
	
	public List<CopyPacketTranslationConfData> getCopyPacketTransMapConfigList(String toType,String fromType) throws DataManagerException;
	
	@Override
	public String create(Object object) throws DataManagerException;
	
	public PageList search(CopyPacketTranslationConfData copyPacketTranslationConfData ,int pageNo, int pageSize) throws DataManagerException;
	
	public TranslatorTypeData getTranslatorTypeData(String translatorTypeId) throws DataManagerException;
	
	public void updateBasicDetail(CopyPacketTranslationConfData copyPacketTranslationConfData,String copyPacketTransConfId,IStaffData staffData,String actionAlias)throws DataManagerException;

	public List<TranslatorTypeData> getToTranslatorTypeData() throws DataManagerException;
	
	public List<TranslatorTypeData> getFromTranslatorTypeData() throws DataManagerException;
	
	public List<CopyPacketTranslationConfData> getCopyPacketMappingList()throws DataManagerException;
	
	public List<CopyPacketTranslationConfData> getdiaTodiaCopyPacketMapping()throws DataManagerException;
	public List<CopyPacketTranslationConfData> getdiaToradCopyPacketMapping()throws DataManagerException;
	public CopyPacketTranslationConfData getCopyPacketTransMapConfDetailDataById(String copyPacketConfigId) throws DataManagerException;
	public CopyPacketTranslationConfData getCopyPacketTransMapConfDetailDataByName(	String copyPacketConfigName) throws DataManagerException;
	public String deleteById(String copyPacketConfigId) throws DataManagerException;
	public String deleteByName(String copyPacketConfigName) throws DataManagerException;
	public void updateById(CopyPacketTranslationConfData copyPacketData, IStaffData staffData, String copyPacketConfigId) throws DataManagerException;
	public void updateByName(CopyPacketTranslationConfData copyPacketData,	IStaffData staffData, String copyPacketConfigName) throws DataManagerException;
	

}
