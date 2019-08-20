/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ASMDataManager.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.sessionmanager;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.sessionmanager.data.IASMData;
import com.elitecore.elitesm.datamanager.sessionmanager.data.SessionManagerDBConfiguration;


public interface ASMDataManager extends DataManager{

	public void closeSession(Long userId,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public void closeSession(String userIds[],SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public PageList search(IASMData asmData, int pageNo,int pageSize,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public PageList search(IASMData asmData,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public PageList searchGroupByCriteria(IASMData asmData, int pageNo, int pageSize,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public void closeAllSession(IASMData asmData,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public List<Map<String,Object>> purgeClosedSession(SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public void purgeAllSession(SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	
}
