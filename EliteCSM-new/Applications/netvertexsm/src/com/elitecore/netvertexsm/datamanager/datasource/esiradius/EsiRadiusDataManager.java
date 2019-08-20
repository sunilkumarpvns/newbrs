package com.elitecore.netvertexsm.datamanager.datasource.esiradius;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.esiradius.data.IEsiRadiusData;


public interface EsiRadiusDataManager extends DataManager {
	
	  public void create(IEsiRadiusData esiRadiusData) throws DataManagerException,DuplicateParameterFoundExcpetion;	  
	  public PageList search (IEsiRadiusData esiRadiusData, int pageNo, int pageSize) throws DataManagerException;
	  public List getEsiRadiusList() ;
}
