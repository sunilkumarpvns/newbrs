package com.elitecore.netvertexsm.datamanager.servermgr.spr.ddf;

import com.elitecore.corenetvertex.spr.ddf.DDFTableData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;

public interface DDFDataManager extends DataManager {
	
	public void create(DDFTableData ddfData) throws DataManagerException;
	
	public void update(DDFTableData ddfData) throws DataManagerException;
	
	public DDFTableData getDDFTableData() throws DataManagerException;
}
