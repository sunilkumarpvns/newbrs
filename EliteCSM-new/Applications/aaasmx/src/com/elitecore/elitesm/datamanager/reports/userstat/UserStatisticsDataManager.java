/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   ASMDataManager.java                 		
 * ModualName ASM    			      		
 * Created on 6 December, 2007
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.reports.userstat;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.reports.userstat.data.IUserStatisticsData;


public interface UserStatisticsDataManager extends DataManager{
	public PageList search(IUserStatisticsData userStatisticsData, int pageNo,int pageSize) throws DataManagerException;

}
