package com.elitecore.elitesm.util;

import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;

/**
 * Server Manager keep track of each transaction(Create,Update,Delete,Search
 * etc) along with Logged In Users Information,IP Address and Transaction time of User's Activity.
 * 
 * @author Nayana Rathod
 */
public class AuditUtility {

	/**
	 * This function is used to maintain transaction activity into a database.
	 * @param session Current data manager session
	 * @param staffData LoggedIn staff information
	 * @param actionAlias Action performed by user
	 * @throws DataManagerException
	 */
	public static void doAuditing(IDataManagerSession session, IStaffData staffData, String actionAlias) throws DataManagerException {
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for System Audit");

		try {
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException(e.getMessage(), e.getCause());
		}
	}
	
	public static SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager) DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager;
	}
}
