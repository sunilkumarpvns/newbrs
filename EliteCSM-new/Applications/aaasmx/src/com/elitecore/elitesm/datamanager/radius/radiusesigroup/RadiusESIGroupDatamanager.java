package com.elitecore.elitesm.datamanager.radius.radiusesigroup;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
/**
 * 
 * @author Tejas Shah
 *
 */
public interface RadiusESIGroupDatamanager extends DataManager{

	PageList searchRadiusESIGroupData(RadiusESIGroupData radiusESIGroupData, int requiredPageNo, Integer pageSize) throws DataManagerException;

	List<RadiusESIGroupData> getRadiusESIGroupDataList()throws DataManagerException;
	
	RadiusESIGroupData getRadiusESIGroupById(String radiusESIGroupId) throws DataManagerException;

	RadiusESIGroupData getRadiusESIGroupByName(String radiusESIGroupName) throws DataManagerException;

	@Override
	public String create(Object object) throws DataManagerException;

	void updateRadiusESIGroupById(RadiusESIGroupData radiusESIGroup, IStaffData staffData, String radiusESIGroupId) throws DataManagerException;

	void updateRadiusESIGroupByName(RadiusESIGroupData radiusESIGroup, IStaffData staffData, String radiusESIGroupName) throws DataManagerException;

	String deleteRadiusESIGroupById(String radiusESIGroupId) throws DataManagerException;

	String deleteRadiusESIGroupByName(String radiusESIGroupName) throws DataManagerException;

}
