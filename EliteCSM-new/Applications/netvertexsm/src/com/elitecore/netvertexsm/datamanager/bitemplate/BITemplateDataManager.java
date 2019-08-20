package com.elitecore.netvertexsm.datamanager.bitemplate;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BITemplateData;
import com.elitecore.netvertexsm.datamanager.bitemplate.data.BISubKeyData;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public interface BITemplateDataManager {
	
	public PageList search(BITemplateData templateData, int pageNo, int pageSize) throws DataManagerException;
	
	public List<BITemplateData> getBITemplateList() throws DataManagerException ;
	
	public void deleteBITemplate(List<Long> biTemplateIDList, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion;
	
	public void create(BITemplateData biTemplateData) throws DataManagerException,DuplicateParameterFoundExcpetion;
	
	public BITemplateData getBITemplateData(BITemplateData biTemplateData) throws DataManagerException;
	
	public List<BISubKeyData> getBISubKeyList(Long biTemplateId) throws DataManagerException;
	
	public void updateBITemplate(BITemplateData biTemplateData) throws DataManagerException,DuplicateParameterFoundExcpetion;
	
	public void uploadFile(List<BISubKeyData> subKeyList) throws DataManagerException,DuplicateParameterFoundExcpetion;
	
}
