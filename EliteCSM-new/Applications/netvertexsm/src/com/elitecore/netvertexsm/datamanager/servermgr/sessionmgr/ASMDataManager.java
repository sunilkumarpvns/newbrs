/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   CoreSessDataManager.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr;

import java.util.List;
import java.util.Map;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionManagerDBConfiguration;


public interface ASMDataManager extends DataManager{

	public void closeSession(Long userId,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public void closeSession(String userIds[],SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	//public PageList searchGroupByCriteria(CoreSessData coreSessData, int pageNo, int pageSize,SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public List<Map<String,Object>> purgeClosedSession(SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	public void purgeAllSession(SessionManagerDBConfiguration dbConfiguration) throws DataManagerException;
	
}
