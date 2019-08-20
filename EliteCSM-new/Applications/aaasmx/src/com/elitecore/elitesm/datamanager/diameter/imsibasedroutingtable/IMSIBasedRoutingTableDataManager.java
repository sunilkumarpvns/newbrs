/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   DiameterPeerDataManager.java                 		
 * ModualName diameterpeer    			      		
 * Created on 13 march, 2012
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable;

import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData;
import com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData;

public interface IMSIBasedRoutingTableDataManager extends DataManager{
	
	public List<IMSIBasedRoutingTableData> searchImsiBasedRoutingTable() throws DataManagerException;

	public void create(IMSIBasedRoutingTableData imsiBasedRoutingTableData) throws DataManagerException;

	public IMSIBasedRoutingTableData getImsiBasedRoutingTableData(String routingTableId)throws DataManagerException;

	public String addEntries(IMSIFieldMappingData imsiFieldMappingData, IStaffData staffData, String actionAlias)throws DataManagerException;

	public void updateEntries(IMSIFieldMappingData imsiFieldMappingData, IStaffData staffData, String actionAlias)throws DataManagerException;

	public void updateRoutingTable(IMSIBasedRoutingTableData imsiBasedRoutingTableData, IStaffData staffData, String actionAlias)throws DataManagerException;

	public void deleteEntries(String fieldMappingId, IStaffData staffData,String actionAlias) throws DataManagerException;

	public void deleteRoutingTable(String fieldMapId)throws DataManagerException;

	public List<IMSIFieldMappingData> getIMSIConfigDataList(String routingTableId) throws DataManagerException;

	public void importImsiBasedConfigurtaion( IMSIBasedRoutingTableData imsiBasedRoutingTableData)throws DataManagerException;

	public List<IMSIFieldMappingData> getIMSIConfigDataList( String routingTableId, String peerId)throws DataManagerException;

	public List<IMSIBasedRoutingTableData> getIMSIBasedRoutingTableList()throws DataManagerException;

	public IMSIBasedRoutingTableData getIMSIDataByName(String routingTableName)throws DataManagerException;

	public PageList searchSubscriberBasedOnImsi(String subscriberDetails, Map infoMap)throws DataManagerException;
	
}
