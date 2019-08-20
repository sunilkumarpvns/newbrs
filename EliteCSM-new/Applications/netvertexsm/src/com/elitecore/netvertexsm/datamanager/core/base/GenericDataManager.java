package com.elitecore.netvertexsm.datamanager.core.base;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;

public interface GenericDataManager extends DataManager{
	
	public PageList getAllRecords(Class<?> instanceClass,String orderByProperty,boolean isAsc) throws DataManagerException;
	public List<?> getAllRecords(Class<?> instanceClass,String statusProperty,String orderByProperty,boolean isAsc) throws DataManagerException ;
	public void saveOrderOfData(String className, Long[] ids) throws DataManagerException;
}
