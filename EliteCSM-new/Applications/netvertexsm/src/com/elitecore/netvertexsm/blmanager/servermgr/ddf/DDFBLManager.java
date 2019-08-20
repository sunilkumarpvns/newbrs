package com.elitecore.netvertexsm.blmanager.servermgr.ddf;

import com.elitecore.commons.base.Preconditions;
import com.elitecore.corenetvertex.spr.ddf.DDFTableData;
import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.ddf.DDFDataManager;

public class DDFBLManager extends BaseBLManager {

	private static final String MODULE = "DDF-BL-MNGR";

	private static DDFBLManager ddfBLManager;

	private DDFBLManager() {}
	
	public static final DDFBLManager getInstance() {
		if (ddfBLManager == null) {
			synchronized (DDFBLManager.class) {
				if (ddfBLManager == null) {
					ddfBLManager = new DDFBLManager();
				}
			}
		}
		return ddfBLManager;
	}

	public void createDDFTableData(DDFTableData ddfTableData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DDFDataManager ddfDataManager = getDDFDataManager(session);
		
		checkNotNullDataManager(ddfDataManager);

		try {
			session.beginTransaction();
			ddfDataManager.create(ddfTableData);
			session.commit();

		} catch (DuplicateParameterFoundExcpetion e) {
			rollbackSession(session);
			throw new DuplicateParameterFoundExcpetion("Duplicate name used", e);
		} catch (Exception e) {
			rollbackSession(session);
			throw new DataManagerException("Action Failed. Reasion: " + e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	private void checkNotNullDataManager(DDFDataManager ddfDataManager) {
		Preconditions.checkNotNull(ddfDataManager, "Data Manager implementation not found for " + getClass().getName()); //TODO which class name needed here?? --chetan
	}
	
	public void updateDDFTableData(DDFTableData ddfTableData, IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DDFDataManager ddfDataManager = getDDFDataManager(session);
		//SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		checkNotNullDataManager(ddfDataManager);
		try {
			session.beginTransaction();
			ddfDataManager.update(ddfTableData);
			//systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();

		} catch (DuplicateParameterFoundExcpetion e) {
			rollbackSession(session);
			throw new DuplicateParameterFoundExcpetion("Duplicate name used", e);
		} catch (Exception e) {
			rollbackSession(session);
			throw new DataManagerException("Action Failed. Reasion: " + e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public DDFTableData getDDFTableData() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DDFDataManager ddfDataManager = getDDFDataManager(session);
		//SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		checkNotNullDataManager(ddfDataManager);
		try {
			session.beginTransaction();
			//systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			return ddfDataManager.getDDFTableData();

		} catch (Exception e) {
			rollbackSession(session);
			throw new DataManagerException("Action Failed. Reasion: " + e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	private DDFDataManager getDDFDataManager(IDataManagerSession session) {
		return (DDFDataManager) DataManagerFactory.getInstance().getDataManager(DDFDataManager.class, session);
	}
}