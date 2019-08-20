package com.elitecore.elitesm.util;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
/**
 * 
 * @author Tejas.P.Shah
 * This utility made for achieved common operation for creating driver.
 *
 */
public class DriverUtility {
	public static List<String> getUniqueLogicalMultipleAllowList(List<LogicalNameValuePoolData> logicalNameMultipleAllowList, List<LogicalNameValuePoolData> logicalNameList) throws DataManagerException{
		List<String> uniqueLogicalNameList = new ArrayList<String>();
		List<String> multipleAllowLogicalName = new ArrayList<String>();

		for(LogicalNameValuePoolData poolData : logicalNameMultipleAllowList) {
			multipleAllowLogicalName.add(poolData.getName());
		}
		
		for(LogicalNameValuePoolData logicalNameValuePoolData : logicalNameList ){
			
			if(multipleAllowLogicalName.contains(logicalNameValuePoolData.getName()) == false){
				uniqueLogicalNameList.add(logicalNameValuePoolData.getName());
			}
		}
		return uniqueLogicalNameList;
	}
}
