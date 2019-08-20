package com.elitecore.netvertexsm.datamanager.core.system.util;

import com.elitecore.netvertexsm.datamanager.DataManagerException;

public interface IDataManagerSession {
    
    public void beginTransaction() throws DataManagerException;
    public void commit() throws DataManagerException;
    public void rollback() throws DataManagerException;
    public void close() throws DataManagerException;
    
}
