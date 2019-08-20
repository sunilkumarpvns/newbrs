package com.elitecore.netvertexsm.datamanager.customizedmenu;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public interface CustomizedMenuDataManager {
	public void create(CustomizedMenuData customizedMenuData) throws DataManagerException; 
	public void update(CustomizedMenuData customizedMenuData) throws DataManagerException;
	public void delete(Long[] customizedMenuIDs,List<CustomizedMenuData> menuList) throws DataManagerException;
	public CustomizedMenuData getCustomizedMenuDetailData(String title) throws DataManagerException;
	public PageList search(CustomizedMenuData customizedMenuData, int pageNo, int pageSize) throws DataManagerException;
	public List<CustomizedMenuData> getCustomizeMenuList() throws DataManagerException;
	public List<CustomizedMenuData> getCustomizedMenuList() throws DataManagerException;
	public List<CustomizedMenuData> getCustomizedMenuItem(Long parentId) throws DataManagerException;
	public List<CustomizedMenuData> getCustomizedMenuList(String title , Long id) throws DataManagerException;

}
