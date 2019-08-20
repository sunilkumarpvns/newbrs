package com.elitecore.elitesm.datamanager.diameter.routingconf;

import java.util.Map;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData;
import com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingTableData;

public interface DiameterRoutingConfDataManager extends DataManager{
	public PageList search(DiameterRoutingConfData diameterRoutingConfData, Map infoMap) throws DataManagerException;
	public DiameterRoutingConfData getDiameterRoutingConfData(String routingConfId) throws DataManagerException;
	public void update(DiameterRoutingConfData diameterRoutingConfData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public void updateDiameterPeer(DiameterRoutingConfData diameterRoutingConfData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public PageList searchRoutingTable(Map infoMap) throws DataManagerException;
	public PageList searchRoutingFromRoutingTable(String routingTableId) throws DataManagerException;
	public DiameterRoutingTableData getDiameterRoutingTableData(String routingTableId) throws DataManagerException;
	public DiameterRoutingTableData getDiameterRoutingTableDataByName(String routingTableName) throws DataManagerException;
	public void updateOverloadConfiguration(DiameterRoutingTableData diameterRoutingTableData,IStaffData staffData,String actionAlias) throws DataManagerException;
	public String getDiameterPeerNameById(String peerId) throws DataManagerException;
	
	public DiameterRoutingTableData getDiameterRoutingTableById(String diameterRoutingTableId) throws DataManagerException;
	public DiameterRoutingTableData getDiameterRoutingTableByName(String diameterRoutingTableName) throws DataManagerException;
	
	@Override
	public String create(Object object) throws DataManagerException;
	
	public void updateRoutingTableById(DiameterRoutingTableData diameterRoutingTable, String diameterRoutingTableId) throws DataManagerException;
	public void updateRoutingTableByName(DiameterRoutingTableData diameterRoutingTable, String diameterRoutingTableName) throws DataManagerException;
	
	public void updateRoutingEntryById(DiameterRoutingConfData diameterRoutingTableEntry, IStaffData staffData, String diameterRoutingTableEntryId) throws DataManagerException;
	public void updateRoutingEntryByName(DiameterRoutingConfData diameterRoutingTableEntry, IStaffData staffData, String diameterRoutingTableEntryName) throws DataManagerException;
	
	public String deleteRoutingTableById(String diameterRoutingTableId) throws DataManagerException;
	public String deleteRoutingTableByName(String diameterRoutingTableName) throws DataManagerException;
	
	public String deleteRoutingEntryById(String diameterRoutingTableEntryId) throws DataManagerException;
	public String deleteRoutingEntryByName(String diameterRoutingTableEntryName) throws DataManagerException;
	
} 
