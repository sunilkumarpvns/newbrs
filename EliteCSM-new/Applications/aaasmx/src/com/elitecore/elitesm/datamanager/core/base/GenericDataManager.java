package com.elitecore.elitesm.datamanager.core.base;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.util.PageList;

public interface GenericDataManager extends DataManager{
	
	public PageList getAllRecords(Class<?> instanceClass,String orderByProperty,boolean isAsc) throws DataManagerException;
	public List<?> getAllRecords(Class<?> instanceClass,String statusProperty,String orderByProperty,boolean isAsc) throws DataManagerException ;
	public void saveOrderOfData(String className, String[] ids) throws DataManagerException;
}
