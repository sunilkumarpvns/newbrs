package com.elitecore.elitesm.datamanager.systemaudit.relation;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditRelationData;

public interface SystemAuditRelationDataManager {
	public SystemAuditRelationData getSystemAuditId(String moduleName) throws DataManagerException;
	public void create(SystemAuditRelationData data) throws DataManagerException;
}
