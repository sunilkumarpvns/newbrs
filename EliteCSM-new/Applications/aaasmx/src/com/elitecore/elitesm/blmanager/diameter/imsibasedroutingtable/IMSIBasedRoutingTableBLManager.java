/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterpolicyBLManager.java                 		
 * ModualName diameterpolicy    			      		
 * Created on 16 May, 2011
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.blmanager.diameter.imsibasedroutingtable;

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
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.IMSIBasedRoutingTableDataManager;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData;

public class IMSIBasedRoutingTableBLManager extends BaseBLManager {
	private static final String MODULE = "IMSI-BASED-ROUTING-TABLE";

	public IMSIBasedRoutingTableDataManager getIMSIBasedRoutingDataManager(IDataManagerSession session) { 
		IMSIBasedRoutingTableDataManager diameterPeerDataManager = (IMSIBasedRoutingTableDataManager) DataManagerFactory.getInstance().getDataManager(IMSIBasedRoutingTableDataManager.class, session);
		return diameterPeerDataManager;
	}
	

	public List<IMSIBasedRoutingTableData> searchImsiBasedRoutingTable() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
		List<IMSIBasedRoutingTableData> imsiBasedRoutingDataList;
		
		if (imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			imsiBasedRoutingDataList = imsiBasedRoutingTableDataManager.searchImsiBasedRoutingTable();
			session.commit();
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Action failed :"+e.getMessage());
		} finally {
			closeSession(session);
		}

		return imsiBasedRoutingDataList; 
	}
	
	public void create(IMSIBasedRoutingTableData imsiBasedRoutingTableData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
		
		if (imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			imsiBasedRoutingTableDataManager.create(imsiBasedRoutingTableData);
			session.commit();
		}
		catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		} finally {
			closeSession(session);
		}
	}
	
	public IMSIBasedRoutingTableData getImsiBasedRoutingTableData(String routingTableId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
				
		IMSIBasedRoutingTableData IMSIBasedRoutingTableData = null;

		if (imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			IMSIBasedRoutingTableData = imsiBasedRoutingTableDataManager.getImsiBasedRoutingTableData(routingTableId);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Action failed :"+e.getMessage());
		} finally {
			closeSession(session);
		}

		return IMSIBasedRoutingTableData; 
	}


	public String addEntries(IMSIFieldMappingData imsiFieldMappingData, IStaffData staffData, String actionAlias)throws DataManagerException  {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
				
		IMSIBasedRoutingTableData IMSIBasedRoutingTableData = null;
		String fieldMapId= null;
		if (imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			fieldMapId =imsiBasedRoutingTableDataManager.addEntries(imsiFieldMappingData,staffData,actionAlias);
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


	public void updateEntries(IMSIFieldMappingData imsiFieldMappingData, IStaffData staffData, String actionAlias) throws DataManagerException  {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
				
		if (imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			imsiBasedRoutingTableDataManager.updateEntries(imsiFieldMappingData,staffData,actionAlias);
			commit(session);
		}
		catch(Exception e){
			e.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Action failed :"+e.getMessage());
		} finally {
			closeSession(session);
		}
	}


	public void updateRoutingTable( IMSIBasedRoutingTableData imsiBasedRoutingTableData,IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
	         
		try{
			if(imsiBasedRoutingTableDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
			session.beginTransaction();
			imsiBasedRoutingTableDataManager.updateRoutingTable(imsiBasedRoutingTableData,staffData,actionAlias);
			commit(session);
		}catch(DataManagerException exp){
			rollbackSession(session);
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception e){
			rollbackSession(session);
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}


	public void deleteEntries(List<String> lstFieldMapIds , IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
	         
		try{
			if(imsiBasedRoutingTableDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
			session.beginTransaction();
			if(lstFieldMapIds != null){
				for(int i=0;i<lstFieldMapIds.size();i++){
					if(lstFieldMapIds.get(i) != null){
						String fieldMapId =  lstFieldMapIds.get(i);
						imsiBasedRoutingTableDataManager.deleteEntries(fieldMapId, staffData, actionAlias);
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
		} finally {
			closeSession(session);
		}
	}


	public void deleteRoutingTable(List<String> listSelectedIDs) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
	         
		try{
			if(imsiBasedRoutingTableDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
			session.beginTransaction();
			if(listSelectedIDs != null){
				for(int i=0;i<listSelectedIDs.size();i++){
					if(listSelectedIDs.get(i) != null){
						String fieldMapId =  listSelectedIDs.get(i);
						imsiBasedRoutingTableDataManager.deleteRoutingTable(fieldMapId);
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
		} finally {
			closeSession(session);
		}
	}
	
	
	public List<IMSIFieldMappingData> getIMSIConfigDataList(String routingTableId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
		List<IMSIFieldMappingData> imsiConfigList;
		
		if(imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			imsiConfigList = imsiBasedRoutingTableDataManager.getIMSIConfigDataList(routingTableId);
		}catch(DuplicateParameterFoundExcpetion dpe){
			dpe.printStackTrace();
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception de){
			de.printStackTrace();
			throw new DataManagerException("Action Failed .",de);
		} finally {
			closeSession(session);
		}
		return imsiConfigList; 
	}

	
	public List<IMSIFieldMappingData> getIMSIConfigDataList(String routingTableId,String peerId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
		List<IMSIFieldMappingData> imsiConfigList;
		
		if(imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			imsiConfigList = imsiBasedRoutingTableDataManager.getIMSIConfigDataList(routingTableId,peerId);
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

	public void importImsiBasedConfigurtaion(IMSIBasedRoutingTableData imsiBasedRoutingTableData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
		  
		if(imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			imsiBasedRoutingTableDataManager.importImsiBasedConfigurtaion(imsiBasedRoutingTableData);
			commit(session);			
		}catch(Exception de){
			de.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Action Failed .",de);
		}finally {
			closeSession(session);
		}
	}


	public List<IMSIBasedRoutingTableData> getIMSIBasedRoutingTableList() throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
		List<IMSIBasedRoutingTableData> imsiConfigList;
		
		if(imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			imsiConfigList = imsiBasedRoutingTableDataManager.getIMSIBasedRoutingTableList();
		}catch(DuplicateParameterFoundExcpetion dpe){
			dpe.printStackTrace();
			rollbackSession(session);
			throw new DuplicateParameterFoundExcpetion("Duplicate name used.");
		}catch(Exception de){
			de.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException("Action Failed .",de);
		}finally{
			rollbackSession(session);
		}
		return imsiConfigList;
	}


	public IMSIBasedRoutingTableData getIMSIDataByName(String routingTableName)throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
		
		IMSIBasedRoutingTableData imsiBasedRoutingTableData = null;

		if (imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			imsiBasedRoutingTableData = imsiBasedRoutingTableDataManager.getIMSIDataByName(routingTableName);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}

		return imsiBasedRoutingTableData; 
	}


	public PageList searchSubscriberBasedOnImsi(String subscriberDetails, Map infoMap) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		IMSIBasedRoutingTableDataManager imsiBasedRoutingTableDataManager = getIMSIBasedRoutingDataManager(session);
		PageList routingTableSubscriberDetailsList;
		
		if (imsiBasedRoutingTableDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			routingTableSubscriberDetailsList = imsiBasedRoutingTableDataManager.searchSubscriberBasedOnImsi(subscriberDetails, infoMap);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			closeSession(session);
		}

		return routingTableSubscriberDetailsList; 
	}
}
