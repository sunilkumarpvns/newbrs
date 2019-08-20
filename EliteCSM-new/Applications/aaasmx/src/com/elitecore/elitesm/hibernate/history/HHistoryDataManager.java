package com.elitecore.elitesm.hibernate.history;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData;
import com.elitecore.elitesm.datamanager.history.HistoryDataManager;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditData;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditDetails;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.history.HistoryData;




public class HHistoryDataManager extends HBaseDataManager implements HistoryDataManager{
	private static final String MODULE = "HHistoryDataManager";
	
	@Override
	public List<DatabaseHistoryData> getDatabaseDSHistoryData(String auditUId) throws DataManagerException {
		PageList pageList = null;
		List<DatabaseHistoryData> databaseDsHistoryList=new ArrayList<DatabaseHistoryData>();;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(SystemAuditData.class);
			criteria.add(Restrictions.eq("auditId",auditUId));
			criteria.addOrder(Order.desc("auditDate"));
            List list = criteria.list();
			int totalItems = list.size();
			List<SystemAuditData> systemAuditDataList= criteria.list();
			
			for(SystemAuditData systemAuditData:systemAuditDataList){
				
				criteria = session.createCriteria(SystemAuditDetails.class);
				criteria.add(Restrictions.eq("systemAuditId",systemAuditData.getSystemAuditId()));
				
	            List<SystemAuditDetails> systemAuditDetailList = criteria.list();
	            List<HistoryData> historyDataList=new ArrayList<HistoryData>();
	            DatabaseHistoryData databaseHistoryData = new DatabaseHistoryData();
	            
				for(SystemAuditDetails systemAuditDetails:systemAuditDetailList){
					
					HistoryData historyData=new HistoryData();
					
					historyData.setHistory(new String(systemAuditDetails.getHistory()));
					
					historyDataList.add(historyData);
				}
				
				if(historyDataList != null && historyDataList.size() > 0){
					databaseHistoryData.setName(systemAuditData.getAuditName());
					databaseHistoryData.setUserName(systemAuditData.getSystemUserName());
					databaseHistoryData.setSystemAuditId(String.valueOf(systemAuditData.getSystemAuditId()));
					SimpleDateFormat auditDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
					String auditDate = auditDateFormat.format(systemAuditData.getAuditDate());
					databaseHistoryData.setLastupdatetime(auditDate);
					databaseHistoryData.setIpAddress(systemAuditData.getClientIP());
					databaseHistoryData.setHistoryData(historyDataList);
					
					databaseDsHistoryList.add(databaseHistoryData);
				}
			}
			
			pageList = new PageList(databaseDsHistoryList,0,0,totalItems);
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		return databaseDsHistoryList;
	}

	@Override
	public IDatabaseDSData getDatabaseDSDataDetails(Long auditId) throws DataManagerException {
		IDatabaseDSData databaseDSData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseDSData.class).add(Restrictions.eq("auditUId",auditId));

			List databaseDSList =  criteria.list();

			if(databaseDSList != null && databaseDSList.size() > 0){
				databaseDSData = (IDatabaseDSData)databaseDSList.get(0);

			}

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return databaseDSData;     
	}

	@Override
	public List<DatabaseHistoryData> getDatabaseDSHistoryDataByName(String moduleName) throws DataManagerException {
		String auditId = getAuditId(moduleName);
		return getDatabaseDSHistoryData(auditId);
	}

}