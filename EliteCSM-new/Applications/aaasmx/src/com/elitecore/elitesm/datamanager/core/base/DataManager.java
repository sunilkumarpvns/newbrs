package com.elitecore.elitesm.datamanager.core.base;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;

public interface DataManager {
    
    public void setDataManagerSession(IDataManagerSession session);
    public String create(Object object) throws DataManagerException;
}
