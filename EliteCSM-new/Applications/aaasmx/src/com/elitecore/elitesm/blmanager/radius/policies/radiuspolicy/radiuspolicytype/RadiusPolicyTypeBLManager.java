package com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy.radiuspolicytype;

import java.util.List;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.radiuspolicytype.RadiusPolicyTypeDataManager;

public class RadiusPolicyTypeBLManager extends BaseBLManager {
	private static RadiusPolicyTypeDataManager radiusPolicyTypeDataManager;
	/*
    private LinkedHashMap getHashMap(){
    	LinkedHashMap map=new LinkedHashMap();
		map.put("C","Check Item");
		map.put("R","Reply Item");		
		map.put("J","Reject Item");		
		map.put("A","Replace Item");
		map.put("E","DynamiceCheck Item");
		map.put("F","DynamicReject Item");
		map.put("B","DynamicReplace Item");
		map.put("D","DynamicReply Item");
		map.put("U","Update Item");
		return map;
	}
	*/
	public List getItemsList() throws DataManagerException{
    	
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        RadiusPolicyTypeDataManager radiusPolicyTypeDataManager = getRadiusPolicyTypeDataManager(session);
        List lstItemList;
        
        if (radiusPolicyTypeDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

        try {
        	 lstItemList = radiusPolicyTypeDataManager.getList();
             return lstItemList;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive items list, reason :"+ e.getMessage(),e);
		}finally{
			closeSession(session);
		}
    }

    public List getUsageItemsList() throws DataManagerException{

        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        RadiusPolicyTypeDataManager radiusPolicyTypeDataManager = getRadiusPolicyTypeDataManager(session);
        List lstUsageItmesList;
        
        if (radiusPolicyTypeDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

        try {
        	lstUsageItmesList = radiusPolicyTypeDataManager.getList();
        	return lstUsageItmesList;
			
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive usage items list, reason :"+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
    }

    public String getItem(String strKey) throws DataManagerException{
    	
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        RadiusPolicyTypeDataManager radiusPolicyTypeDataManager = getRadiusPolicyTypeDataManager(session);
        String item;
        
        if (radiusPolicyTypeDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

        try {
        	 item = radiusPolicyTypeDataManager.getRadiusPolicyParamType(strKey).getName();
             return item;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive item, reason :"+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
    }

	/**
     * @return Returns Data Manager instance for dictionary data.
     */
    public RadiusPolicyTypeDataManager getRadiusPolicyTypeDataManager(IDataManagerSession session) {
        RadiusPolicyTypeDataManager radiusPolicyTypeDataManager = (RadiusPolicyTypeDataManager)DataManagerFactory.getInstance().getDataManager(RadiusPolicyTypeDataManager.class, session);
        return radiusPolicyTypeDataManager; 
    }
}
