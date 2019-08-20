package com.elitecore.netvertexsm.datamanager.systemaudit;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.systemaudit.Data.ISystemAuditData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public interface SystemAuditDataManager {

	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias, String transactionId) throws DataManagerException;
	public List getAllAction() throws DataManagerException;
	public List getAllUsers() throws DataManagerException;
	public PageList getAuditDetails(ISystemAuditData systemAuditData,int pageNo, int pageSize) throws DataManagerException;
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias, String transactionId, String remarks) throws DataManagerException;
	public void updateTbltSystemAudit(IStaffData staffData, String actionAlias) throws DataManagerException;
}
