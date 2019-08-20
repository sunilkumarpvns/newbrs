package com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableGatewayRelData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public interface MCCMNCRoutingTableDataManager {

	public PageList search(RoutingTableData routingTableData, int requiredPageNo,Integer pageSize) throws DataManagerException;
	public void create(RoutingTableData routingTableData) throws DataManagerException;
	public List<RoutingTableData> getRoutingTableDataList() throws DataManagerException;
	public void delete(Long[] routingTableIds)  throws DataManagerException;
	public RoutingTableData getRoutingTableData(long routingTableId) throws DataManagerException;
	public void update(RoutingTableData routingTableData) throws DataManagerException;
	public  List<RoutingTableGatewayRelData> getRoutingTableGatewayRelList(Long routingTableId) throws DataManagerException;
	public List<RoutingTableGatewayRelData> getRoutingTableGatewayRelList() throws DataManagerException;
	public void deleteRoutingTableGatewayRelData(Long routingTableId) throws DataManagerException;
	public void changeRoutingEntryOrder(String[] routingEntryIds) throws DataManagerException;
	public RoutingTableData getRoutingTableByMCCMNCGroup(long mccmncGroupId)throws DataManagerException;
	
	
}
