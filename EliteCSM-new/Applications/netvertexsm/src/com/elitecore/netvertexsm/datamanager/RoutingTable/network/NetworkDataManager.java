package com.elitecore.netvertexsm.datamanager.RoutingTable.network;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandOperatorRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public interface NetworkDataManager extends DataManager{
	
	public void create(NetworkData networkData) throws DataManagerException;
	
	public void update(NetworkData networkData) throws DataManagerException;
	
	public void delete(Long[] networkIDs) throws DataManagerException;
	
	public NetworkData getNetworkDetailData(Long networkID) throws DataManagerException;
	
	public PageList search(NetworkData networkData, int pageNo, int pageSize) throws DataManagerException;
	
	public List<CountryData> getCountryDataList() throws DataManagerException;
	
	public List<OperatorData> getOperatorDataList() throws DataManagerException;
	
	public List<BrandData> getBrandDataList() throws DataManagerException;
	
	public List<BrandOperatorRelData> getBrandOperatorDataRelList() throws DataManagerException;
	
	public List<NetworkData> getNetworkDataList(String restrictionSqlQuery) throws DataManagerException;

	public List<NetworkData> getNetworkDataList() throws DataManagerException;
	

}
