package com.elitecore.elitesm.blmanager.diameter.routingconf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.routingconf.DiameterRoutingConfDataManager;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.routingentry.DiameterRoutingEntryDataManager;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.rest.data.Status;

public class DiameterRoutingConfBLManager extends BaseBLManager {
	public DiameterRoutingConfDataManager getDiameterRoutingConfDataManager(IDataManagerSession session) { 
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = (DiameterRoutingConfDataManager) DataManagerFactory.getInstance().getDataManager(DiameterRoutingConfDataManager.class, session);
		return diameterRoutingConfDataManager;
	}
	
	public PageList search(DiameterRoutingConfData diameterRoutingConfData, IStaffData staffData, Map infoMap) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		PageList lstDiameterRoutingConfList;
	
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try{
			session.beginTransaction();	
			lstDiameterRoutingConfList = diameterRoutingConfDataManager.search(diameterRoutingConfData, infoMap);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DIAMETER_ROUTING_TABLE);
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return lstDiameterRoutingConfList; 
	}
	
	public PageList searchRoutingTable(IStaffData staffData, Map infoMap) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		PageList lstDiameterRoutingTableList;
		
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try{
			session.beginTransaction();	
			lstDiameterRoutingTableList = diameterRoutingConfDataManager.searchRoutingTable(infoMap);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DIAMETER_ROUTING_TABLE);
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return lstDiameterRoutingTableList; 
	}
	
	public DiameterRoutingConfData getDiameterRoutingConfData(String routingConfId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		DiameterRoutingConfData diameterRoutingConfData = null;
		try{
			diameterRoutingConfData = diameterRoutingConfDataManager.getDiameterRoutingConfData(routingConfId);
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return diameterRoutingConfData;
	}
	
	public List getDiameterRoutingConfList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		List diameterRoutingConfList = null;
		List diameterRoutingTableList = new ArrayList();
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}
		
		try{
			
			GenericBLManager genericBLManager =new GenericBLManager();
			PageList pageList = genericBLManager.getAllRecords(DiameterRoutingConfData.class,"name",true);
			diameterRoutingConfList=pageList.getListData();
			if(diameterRoutingConfList != null && !diameterRoutingConfList.isEmpty())
			{	
				for(int i=0;i<diameterRoutingConfList.size();i++)
				{
					DiameterRoutingConfData diameterRoutingConfData=(DiameterRoutingConfData)diameterRoutingConfList.get(i);
					DiameterRoutingTableData diameterRoutingTableData=diameterRoutingConfData.getDiameterRoutingTableData();
					if(!diameterRoutingTableList.contains(diameterRoutingTableData.getRoutingTableName()))
					{
						diameterRoutingTableList.add(diameterRoutingTableData.getRoutingTableName());
					}	
				}
				Collections.sort(diameterRoutingTableList);
			}	
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return diameterRoutingTableList;
	}
	
	public List getDiameterRoutingTableList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		List diameterRoutingConfList = null;
		List diameterRoutingTableList = new ArrayList();
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}
		
		try{
			
			GenericBLManager genericBLManager =new GenericBLManager();
			PageList pageList = genericBLManager.getAllRecords(DiameterRoutingConfData.class,"name",true);
			diameterRoutingConfList=pageList.getListData();
			if(diameterRoutingConfList != null && !diameterRoutingConfList.isEmpty())
			{	
				for(int i=0;i<diameterRoutingConfList.size();i++)
				{
					DiameterRoutingConfData diameterRoutingConfData=(DiameterRoutingConfData)diameterRoutingConfList.get(i);
					DiameterRoutingTableData diameterRoutingTableData=diameterRoutingConfData.getDiameterRoutingTableData();
					if(!diameterRoutingTableList.contains(diameterRoutingTableData))
					{
						diameterRoutingTableList.add(diameterRoutingTableData);
					}	
				}
				Collections.sort(diameterRoutingTableList);
			}	
			
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return diameterRoutingTableList;
	}
	
	public void update(DiameterRoutingConfData diameterRoutingConfData,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try{
			session.beginTransaction();
			diameterRoutingConfDataManager.update(diameterRoutingConfData,staffData,actionAlias);
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateDiameterPeer(DiameterRoutingConfData diameterRoutingConfData,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}
		
		try{
			session.beginTransaction();
			diameterRoutingConfDataManager.updateDiameterPeer(diameterRoutingConfData,staffData,actionAlias);
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}
	
	public PageList searchRoutingFromRoutingTable(String routingTableId, IStaffData staffData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		PageList lstDiameterRoutingConfList;
		
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try{
			session.beginTransaction();	
			lstDiameterRoutingConfList = diameterRoutingConfDataManager.searchRoutingFromRoutingTable(routingTableId);
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_DIAMETER_ROUTING_TABLE);
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return lstDiameterRoutingConfList; 
	}
	
	public DiameterRoutingTableData getDiameterRoutingTableData(String routingTableId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		DiameterRoutingTableData diameterRoutingTableData = null;
		try{
			diameterRoutingTableData = diameterRoutingConfDataManager.getDiameterRoutingTableData(routingTableId);
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return diameterRoutingTableData;
	}
	
	public DiameterRoutingTableData getDiameterRoutingTableDataByName(String routingTableName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		DiameterRoutingTableData diameterRoutingTableData = null;
		try{
			diameterRoutingTableData = diameterRoutingConfDataManager.getDiameterRoutingTableDataByName(routingTableName);
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return diameterRoutingTableData;
	}
			
	public void updateOverloadConfiguration(DiameterRoutingTableData diameterRoutingTableData,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try{
			session.beginTransaction();
			diameterRoutingConfDataManager.updateOverloadConfiguration(diameterRoutingTableData, staffData, actionAlias);
			staffData.setAuditName(diameterRoutingTableData.getRoutingTableName());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.UPDATE_DIAMETER_ROUTING_TABLE);
			commit(session);
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
	}

	public String getDiameterPeerNameById(String peerId)  throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		String peerName = null;
		try{
			peerName = diameterRoutingConfDataManager.getDiameterPeerNameById(peerId);
		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		return peerName;
	}

	
	public DiameterRoutingTableData getDiameterRoutingTableById(String diameterRoutingTableId) throws DataManagerException {
		
		return getDiameterRoutingTable(diameterRoutingTableId, BY_ID);
		
	}
	
	public DiameterRoutingTableData getDiameterRoutingTableByName(String diameterRoutingTableName) throws DataManagerException {
		
		return getDiameterRoutingTable(diameterRoutingTableName.trim(), BY_NAME);
		
	}
	
	private DiameterRoutingTableData getDiameterRoutingTable(Object diameterRoutingTableIdOrName, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}
		
		try {

			DiameterRoutingTableData diameterRoutingTable;

			if (isIdOrName) {
				diameterRoutingTable = diameterRoutingConfDataManager.getDiameterRoutingTableById((String) diameterRoutingTableIdOrName);
			} else {
				diameterRoutingTable = diameterRoutingConfDataManager.getDiameterRoutingTableByName((String) diameterRoutingTableIdOrName);
			}

			return diameterRoutingTable;

		} catch (DataManagerException dme) {
			throw dme;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void createRoutingTable(DiameterRoutingTableData diameterRoutingTable, IStaffData staffData) throws DataManagerException {
		
		List<DiameterRoutingTableData> diameterRoutingTables = new ArrayList<DiameterRoutingTableData>();
		diameterRoutingTables.add(diameterRoutingTable);
		createRoutingTable(diameterRoutingTables, staffData, "");
		
	}

	public Map<String, List<Status>> createRoutingTable(List<DiameterRoutingTableData> diameterRoutingTables, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(DiameterRoutingConfDataManager.class, diameterRoutingTables, staffData, ConfigConstant.CREATE_DIAMETER_ROUTING_TABLE, partialSuccess);
	}
	
	
	public void createRoutingEntry(DiameterRoutingConfData diameterRoutingTableEntry, IStaffData staffData) throws DataManagerException {
		
		List<DiameterRoutingConfData> diameterRoutingTableEntries = new ArrayList<DiameterRoutingConfData>();
		diameterRoutingTableEntries.add(diameterRoutingTableEntry);
		createRoutingEntry(diameterRoutingTableEntries, staffData, "");
		
	}

	public Map<String, List<Status>> createRoutingEntry(List<DiameterRoutingConfData> diameterRoutingTableEntries, IStaffData staffData, String partialSuccess) throws DataManagerException {
		return insertRecords(DiameterRoutingEntryDataManager.class, diameterRoutingTableEntries, staffData, ConfigConstant.CREATE_DIAMETER_ROUTING_TABLE, partialSuccess);
	}
	
	
	public void updateRoutingTableById(DiameterRoutingTableData diameterRoutingTable, IStaffData staffData) throws DataManagerException {
		
		updateRoutingTable(diameterRoutingTable, staffData, null);
		
	}

	public void updateRoutingTableByName(DiameterRoutingTableData diameterRoutingTable, IStaffData staffData, String diameterRoutingTableName) throws DataManagerException {
		
		updateRoutingTable(diameterRoutingTable, staffData, diameterRoutingTableName.trim());
		
	}
	
	private void updateRoutingTable(DiameterRoutingTableData diameterRoutingTable, IStaffData staffData, String diameterRoutingTableName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			if (diameterRoutingTableName == null) {
				diameterRoutingConfDataManager.updateRoutingTableById(diameterRoutingTable, diameterRoutingTable.getRoutingTableId());
			} else {
				diameterRoutingConfDataManager.updateRoutingTableByName(diameterRoutingTable, diameterRoutingTableName.trim());
			}
			
			staffData.setAuditName(diameterRoutingTable.getRoutingTableName());
			AuditUtility.doAuditing(session, staffData, ConfigConstant.UPDATE_DIAMETER_ROUTING_TABLE);

			commit(session);
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void updateRoutingEntryById(DiameterRoutingConfData diameterRoutingTableEntry, IStaffData staffData) throws DataManagerException {
		
		updateRoutingEntry(diameterRoutingTableEntry, staffData, null);
		
	}

	public void updateRoutingEntryByName(DiameterRoutingConfData diameterRoutingTableEntry, IStaffData staffData, String diameterRoutingTableEntryName) throws DataManagerException {
		
		updateRoutingEntry(diameterRoutingTableEntry, staffData, diameterRoutingTableEntryName.trim());
		
	}

	private void updateRoutingEntry(DiameterRoutingConfData diameterRoutingTableEntry, IStaffData staffData, String diameterRoutingTableEntryName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try {
			
			session.beginTransaction();
			
			if (diameterRoutingTableEntryName == null) {
				diameterRoutingConfDataManager.updateRoutingEntryById(diameterRoutingTableEntry, staffData, diameterRoutingTableEntry.getRoutingConfigId());
			} else {
				diameterRoutingConfDataManager.updateRoutingEntryByName(diameterRoutingTableEntry, staffData, diameterRoutingTableEntryName.trim());
			}

			commit(session);
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void deleteRoutingTableById(List<String> diameterRoutingTableIds, IStaffData staffData) throws DataManagerException {
		
		deleteRoutingTable(diameterRoutingTableIds, staffData, BY_ID);

	}
	
	public void deleteRoutingTableByName(List<String> diameterRoutingTableNames, IStaffData staffData) throws DataManagerException {
		
		deleteRoutingTable(diameterRoutingTableNames, staffData, BY_NAME);
		
	}
	
	private void deleteRoutingTable(List<String> diameterRoutingTableIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(diameterRoutingTableIdOrNames) == false) {

				int size = diameterRoutingTableIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(diameterRoutingTableIdOrNames.get(i)) == false) {

						String diameterRoutingTableIdOrName = diameterRoutingTableIdOrNames.get(i).trim();
						
						String diameterRoutingTableName;

						if (isIdOrName) {
							diameterRoutingTableName = diameterRoutingConfDataManager.deleteRoutingTableById(diameterRoutingTableIdOrName);
						} else {
							diameterRoutingTableName = diameterRoutingConfDataManager.deleteRoutingTableByName(diameterRoutingTableIdOrName);
						}

						staffData.setAuditName(diameterRoutingTableName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_DIAMETER_ROUTING_TABLE);

					}

				}

				commit(session);
			}
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}

	
	public void deleteRoutingEntryById(List<String> diameterRoutingTableEntryIds, IStaffData staffData) throws DataManagerException {
		
		deleteRoutingEntry(diameterRoutingTableEntryIds, staffData, BY_ID);

	}

	public void deleteRoutingEntryByName(List<String> diameterRoutingTableEntryNames, IStaffData staffData) throws DataManagerException {
		
		deleteRoutingEntry(diameterRoutingTableEntryNames, staffData, BY_NAME);
		
	}

	private void deleteRoutingEntry(List<String> diameterRoutingTableEntryIdOrNames, IStaffData staffData, boolean isIdOrName) throws DataManagerException {
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		DiameterRoutingConfDataManager diameterRoutingConfDataManager = getDiameterRoutingConfDataManager(session);
		
		if (diameterRoutingConfDataManager == null) {
			throw new DataManagerException("Data Manager implementation not found for DataManager: " + getClass().getName());
		}

		try {
			
			session.beginTransaction();

			if (Collectionz.isNullOrEmpty(diameterRoutingTableEntryIdOrNames) == false) {

				int size = diameterRoutingTableEntryIdOrNames.size();
				for (int i = 0; i < size; i++) {

					if (Strings.isNullOrBlank(diameterRoutingTableEntryIdOrNames.get(i)) == false) {

						String diameterRoutingTableEntryIdOrName = diameterRoutingTableEntryIdOrNames.get(i).trim();
						
						String diameterRoutingTableEntryName;

						if (isIdOrName) {
							diameterRoutingTableEntryName = diameterRoutingConfDataManager.deleteRoutingEntryById(diameterRoutingTableEntryIdOrName);
						} else {
							diameterRoutingTableEntryName = diameterRoutingConfDataManager.deleteRoutingEntryByName(diameterRoutingTableEntryIdOrName);
						}

						staffData.setAuditName(diameterRoutingTableEntryName);
						AuditUtility.doAuditing(session, staffData, ConfigConstant.DELETE_DIAMETER_ROUTING_TABLE);

					}

				}

				commit(session);
			}
			
		} catch (DataManagerException dme) {
			rollbackSession(session);
			throw dme;
		} catch (Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		} finally {
			closeSession(session);
		}
		
	}
	
	
}