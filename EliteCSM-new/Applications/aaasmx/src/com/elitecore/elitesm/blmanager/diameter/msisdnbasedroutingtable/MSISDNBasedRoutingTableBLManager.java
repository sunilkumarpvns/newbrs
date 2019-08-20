/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyBLManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.blmanager.diameter.msisdnbasedroutingtable;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.MSISDNBasedRoutingTableDataManager;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNFieldMappingData;

public class MSISDNBasedRoutingTableBLManager extends BaseBLManager {
	private static final String MODULE = "IMSI-BASED-ROUTING-TABLE";

	public MSISDNBasedRoutingTableDataManager getMSISDNBasedRoutingDataManager(IDataManagerSession session) { 
		MSISDNBasedRoutingTableDataManager msisdnBasedDataManager = (MSISDNBasedRoutingTableDataManager) DataManagerFactory.getInstance().getDataManager(MSISDNBasedRoutingTableDataManager.class, session);
		return msisdnBasedDataManager;
	}

	public List<MSISDNBasedRoutingTableData> searchMSISDNBasedRoutingTable() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
		 List<MSISDNBasedRoutingTableData> msisdnBasedRoutingTableList;
		
		if (msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			msisdnBasedRoutingTableList = msisdnBasedRoutingTableDataManager.searchMSISDNBasedRoutingTable();
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}

		return msisdnBasedRoutingTableList; 
	}
	
	public void create(MSISDNBasedRoutingTableData msisdnBasedRoutingTableData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);

		if (msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			msisdnBasedRoutingTableDataManager.create(msisdnBasedRoutingTableData);
			commit(session);
		}
		catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);

			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}
	}
	
	public MSISDNBasedRoutingTableData getMSISDNBasedRoutingTableData(String routingTableId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);

		
		MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = null;

		if (msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			msisdnBasedRoutingTableData = msisdnBasedRoutingTableDataManager.getMsisdnBasedRoutingTableData(routingTableId);
		}
		catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}

		return msisdnBasedRoutingTableData; 
	}


	public String addEntries(MSISDNFieldMappingData msisdnFieldMappingData, IStaffData staffData, String actionAlias)throws DataManagerException  {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
			
		String fieldMapId= null;
		if (msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			fieldMapId =msisdnBasedRoutingTableDataManager.addEntries(msisdnFieldMappingData, staffData, actionAlias);
			commit(session);
		}
		catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}
		return fieldMapId;
	}


	public void updateEntries(MSISDNFieldMappingData msisdnFieldMappingData,IStaffData staffData, String actionAlias) throws DataManagerException  {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
		
		if (msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			msisdnBasedRoutingTableDataManager.updateEntries(msisdnFieldMappingData, staffData, actionAlias);
			commit(session);
		}
		catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}
		
	}


	public void updateRoutingTable( MSISDNBasedRoutingTableData msisdnBasedRoutingTableData, IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
		
		try{
			if(msisdnBasedRoutingTableDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
			session.beginTransaction();
			msisdnBasedRoutingTableDataManager.updateRoutingTable(msisdnBasedRoutingTableData, staffData, actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		
	}


	public void deleteEntries(List<String> lstFieldMapIds,IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
		
		try{
			if(msisdnBasedRoutingTableDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
			session.beginTransaction();
			if(lstFieldMapIds != null){
				for(int i=0;i<lstFieldMapIds.size();i++){
					if(lstFieldMapIds.get(i) != null){
						String fieldMapId =  lstFieldMapIds.get(i);
						msisdnBasedRoutingTableDataManager.deleteEntries(fieldMapId, staffData, actionAlias);
					}
				}
			}else{
				throw new DataManagerException("Data Manager implementation not found for ");    		
			}
			commit(session);			
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}


	public void deleteRoutingTable(List<String> listSelectedIDs) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
		
		try{
			if(msisdnBasedRoutingTableDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
			session.beginTransaction();
			if(listSelectedIDs != null){
				for(int i=0;i<listSelectedIDs.size();i++){
					if(listSelectedIDs.get(i) != null){
						String fieldMapId =  listSelectedIDs.get(i);
						msisdnBasedRoutingTableDataManager.deleteRoutingTable(fieldMapId);
					}
				}
			}else{
				throw new DataManagerException("Data Manager implementation not found for ");    		
			}
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
	}
	
	
	public List<MSISDNFieldMappingData> getMSISDNConfigDataList(String routingTableId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
		
		List<MSISDNFieldMappingData> imsiConfigList;
		
		if(msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			imsiConfigList = msisdnBasedRoutingTableDataManager.getMSISDNConfigDataList(routingTableId);
		}catch(DuplicateParameterFoundExcpetion dpe){
			dpe.printStackTrace();
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException("Action Failed .",de);
		}finally{
			closeSession(session);
		}
		return imsiConfigList; 
	}

	
	public List<MSISDNFieldMappingData> getMSISDNConfigDataList(String routingTableId,String peerId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
		
		List<MSISDNFieldMappingData> imsiConfigList;
		
		if(msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			imsiConfigList = msisdnBasedRoutingTableDataManager.getMSISDNConfigDataList(routingTableId,peerId);
		}catch(DuplicateParameterFoundExcpetion dpe){
			dpe.printStackTrace();
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException("Action Failed .",de);
		}finally{
			closeSession(session);
		}
		return imsiConfigList; 
	}

	public void importMSISDNBasedConfigurtaion(MSISDNBasedRoutingTableData msisdnBasedRoutingTableData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
		
		if(msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			msisdnBasedRoutingTableDataManager.importMsisdnBasedConfigurtaion(msisdnBasedRoutingTableData);
			session.commit();
		}catch(Exception de){
			de.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed .",de);
		}finally{
			closeSession(session);
		}
	}


	public List<MSISDNBasedRoutingTableData> getMSISDNBasedRoutingTableList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
				
		List<MSISDNBasedRoutingTableData> msisdnBasedConfigList;
		
		if(msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			msisdnBasedConfigList = msisdnBasedRoutingTableDataManager.getMSISDNBasedRoutingTableList();
		}catch(DuplicateParameterFoundExcpetion dpe){
			dpe.printStackTrace();
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception de){
			de.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action Failed .",de);
		}finally{
			closeSession(session);
		}
		return msisdnBasedConfigList;
	}

	public MSISDNBasedRoutingTableData getMSISDNDataByName(String routingTableName) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);

		
		MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = null;

		if (msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			msisdnBasedRoutingTableData = msisdnBasedRoutingTableDataManager.getMSISDNDataByName(routingTableName);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Action failed :"+e.getMessage());
		} finally {
			closeSession(session);
		}

		return msisdnBasedRoutingTableData; 
	}

	public PageList searchSubscriberBasedOnMsisdn(String subscriberDetails, Map infoMap) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		MSISDNBasedRoutingTableDataManager msisdnBasedRoutingTableDataManager = getMSISDNBasedRoutingDataManager(session);
		PageList msisdnBasedRoutingTableList;
		
		if (msisdnBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			msisdnBasedRoutingTableList = msisdnBasedRoutingTableDataManager.searchSubscriberBasedOnMsisdn(subscriberDetails, infoMap);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Action failed :"+e.getMessage());
		} finally {
			closeSession(session);
		}

		return msisdnBasedRoutingTableList; 
	}
}
