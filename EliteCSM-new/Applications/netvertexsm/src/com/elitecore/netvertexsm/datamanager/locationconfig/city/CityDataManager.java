package com.elitecore.netvertexsm.datamanager.locationconfig.city;
import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData;

public interface CityDataManager extends DataManager{
	
	public void create(CityData cityData) throws DataManagerException;
	
	public void update(CityData cityData) throws DataManagerException;
	
	public void delete(Long[] cityIds) throws DataManagerException;
	
	public CityData getCityData(Long cityId) throws DataManagerException;
	
	public PageList search(CityData cityData, int pageNo, int pageSize) throws DataManagerException;
	
	public List<CityData> getCityDataList() throws DataManagerException;

	public void createCityByList(List<CityData> cityDataList) throws DataManagerException;

	public List<CityData> getCityDataList(long regionId) throws DataManagerException;

}
