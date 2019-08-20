/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterPeerDataManager.java                 		
 * ModualName diameterpeer    			      		
 * Created on 13 march, 2012
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNFieldMappingData;

public interface MSISDNBasedRoutingTableDataManager extends DataManager{
	
	public List<MSISDNBasedRoutingTableData> searchMSISDNBasedRoutingTable() throws DataManagerException;

	public void create(MSISDNBasedRoutingTableData imsiBasedRoutingTableData) throws DataManagerException;

	public MSISDNBasedRoutingTableData getMsisdnBasedRoutingTableData(String routingTableId)throws DataManagerException;

	public String addEntries(MSISDNFieldMappingData imsiFieldMappingData, IStaffData staffData, String actionAlias)throws DataManagerException;

	public void updateEntries(MSISDNFieldMappingData imsiFieldMappingData,IStaffData staffData, String actionAlias)throws DataManagerException;

	public void updateRoutingTable(MSISDNBasedRoutingTableData imsiBasedRoutingTableData, IStaffData staffData, String actionAlias)throws DataManagerException;

	public void deleteEntries(String fieldMappingId, IStaffData staffData, String actionAlias) throws DataManagerException;

	public void deleteRoutingTable(String fieldMapId)throws DataManagerException;

	public List<MSISDNFieldMappingData> getMSISDNConfigDataList(String routingTableId) throws DataManagerException;

	public void importMsisdnBasedConfigurtaion( MSISDNBasedRoutingTableData imsiBasedRoutingTableData)throws DataManagerException;

	public List<MSISDNFieldMappingData> getMSISDNConfigDataList( String routingTableId, String peerId)throws DataManagerException;

	public List<MSISDNBasedRoutingTableData> getMSISDNBasedRoutingTableList()throws DataManagerException;

	public MSISDNBasedRoutingTableData getMSISDNDataByName(String routingTableName)throws DataManagerException;

	public PageList searchSubscriberBasedOnMsisdn(String subscriberDetails, Map infoMap)throws DataManagerException;
	
}
