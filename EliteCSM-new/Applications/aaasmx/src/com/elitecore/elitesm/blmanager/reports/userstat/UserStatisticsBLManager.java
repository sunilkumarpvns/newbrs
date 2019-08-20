package com.elitecore.elitesm.blmanager.reports.userstat;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.reports.userstat.UserStatisticsDataManager;
import com.elitecore.elitesm.datamanager.reports.userstat.data.IUserStatisticsData;


public class UserStatisticsBLManager extends BaseBLManager{
	private static final String MODULE="USER STATISTICS BLMANAGER";

	public PageList search(IUserStatisticsData userStatisticsData, int pageNo,int pageSize) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		UserStatisticsDataManager userStatisticsDataManager = getUserStatisticsDataManager(session);
		PageList lstUserStatisticsList;
		if (userStatisticsDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+ getClass().getName());
		}
		
		try {
			 lstUserStatisticsList = userStatisticsDataManager.search(userStatisticsData, pageNo, pageSize);
		     return lstUserStatisticsList;
		} catch (Exception e) {
			throw new DataManagerException(e.getMessage(),e.getCause());
		} finally {
			closeSession(session);
		}
	}
	
	public UserStatisticsDataManager getUserStatisticsDataManager(IDataManagerSession session) { 
		UserStatisticsDataManager userStatisticsDataManager = (UserStatisticsDataManager) DataManagerFactory .getInstance().getDataManager(UserStatisticsDataManager.class, session);
		return userStatisticsDataManager;
	}
}
