/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   RadiusBLManager.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  Himanshu Dobaria
 */                                                     
package com.elitecore.elitesm.blmanager.radius.radtest;

import java.util.List;

import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.radius.radtest.RadiusTestDataManager;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestParamData;
import com.elitecore.elitesm.util.logger.Logger;

public class RadiusTestBLManager extends BaseBLManager {
	private static final String MODULE = "RADIUS TEST BLMANAGER";

	/**
	 * @return Returns Data Manager instance for Radius data.
	 */
	public RadiusTestDataManager getRadiusDataManager(IDataManagerSession session) { 
		RadiusTestDataManager radiusTestDataManager = (RadiusTestDataManager) DataManagerFactory.getInstance().getDataManager(RadiusTestDataManager.class, session);
		return radiusTestDataManager;
	}
        
        public void create(IRadiusTestData radData) throws DataManagerException {
                IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
                RadiusTestDataManager radiusTestDataManager = getRadiusDataManager(session);
                
                if (radiusTestDataManager == null)
                    throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
                
                try{
                        session.beginTransaction();
                        radiusTestDataManager.create(radData);
                        commit(session);
                        
                }catch(DataManagerException exp){
                        rollbackSession(session);
                        Logger.logError(MODULE, "Create Action failed. Reason : " + exp.getMessage());
                        throw new DataManagerException("Create Action failed : " + exp.getMessage(),exp);
                        
                } catch(Exception exp) {
                        rollbackSession(session);
                        Logger.logError(MODULE, "Create Action failed. Reason : " + exp.getMessage());
                        throw new DataManagerException("Create Action failed : "+exp.getMessage(),exp);
                }
                finally {
                    closeSession(session);
                }
        }
        
        public List getRadiusTestList() throws DataManagerException {
            IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            RadiusTestDataManager radiusTestDataManager = getRadiusDataManager(session);
            
            if (radiusTestDataManager == null)
                throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

            List radiusTestList; 
            try {
                radiusTestList = radiusTestDataManager.getList();
                
            } catch(DataManagerException exp){
                Logger.logError(MODULE, "Get Radius Test List Failed. Reason : " + exp.getMessage());
                throw new DataManagerException("Get Radius Test List Failed : " + exp.getMessage(),exp);
                
            } catch(Exception exp) {
                Logger.logError(MODULE, "Get Radius Test List Failed. Reason : " + exp.getMessage());
                throw new DataManagerException("Get Radius Test List Failed : "+exp.getMessage(),exp);
            }
            finally {
                closeSession(session);
            }
            return radiusTestList;
        }
        
        public void delete(String radiusTestId) throws DataManagerException {
            IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            RadiusTestDataManager radiusTestDataManager = getRadiusDataManager(session);
            
            if (radiusTestDataManager == null)
                throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
            
            try{
                session.beginTransaction();
                radiusTestDataManager.delete(radiusTestId);
                commit(session);
                
            } catch(DataManagerException exp){
                rollbackSession(session);
                Logger.logError(MODULE, "Delete Radius Test Failed. Reason : " + exp.getMessage());
                throw new DataManagerException("Delete Radius Test Failed : " + exp.getMessage(),exp);
                
            } catch(Exception exp) {
                rollbackSession(session);
                Logger.logError(MODULE, "Delete Radius Test Failed. Reason : " + exp.getMessage());
                throw new DataManagerException("Delete Radius Test Failed : "+exp.getMessage(),exp);
            }
            finally {
                closeSession(session);
            } 
        }
        
        public IRadiusTestData getRadiusObjById(String radiusTestId) throws DataManagerException {
            IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            RadiusTestDataManager radiusTestDataManager = getRadiusDataManager(session);
            
            if (radiusTestDataManager == null)
                throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
            
            IRadiusTestData radiusTestData = null;
            try {
                radiusTestData = radiusTestDataManager.getRadiusObj(radiusTestId);
                
            }  catch(DataManagerException exp){
                Logger.logError(MODULE, "Get Radius Object By Id Failed. Reason : " + exp.getMessage());
                throw new DataManagerException("Get Radius Object By Id Failed : " + exp.getMessage(),exp);
                
            } catch(Exception exp) {
                Logger.logError(MODULE, "Get Radius Object By Id Failed. Reason : " + exp.getMessage());
                throw new DataManagerException("Get Radius Object By Id Failed : "+exp.getMessage(),exp);
            }
            finally {
                closeSession(session);
            }
            return radiusTestData;
        }
        
        public void updateRadiusData(IRadiusTestData radData, List paramList) throws DataManagerException {
            IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
            RadiusTestDataManager radiusTestDataManager = getRadiusDataManager(session);
            
            if (radiusTestDataManager == null)
                throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
            
            try{
                    session.beginTransaction();
                    radiusTestDataManager.update(radData,paramList);
                    commit(session);
                    
            }catch(DataManagerException exp){
                    rollbackSession(session);
                    Logger.logError(MODULE, "Update Action failed. Reason : " + exp.getMessage());
                    throw new DataManagerException("Update Action failed : " + exp.getMessage(),exp);
                    
            } catch(Exception exp) {
                    rollbackSession(session);
                    Logger.logError(MODULE, "Update Action failed. Reason : " + exp.getMessage());
                    throw new DataManagerException("Update Action failed : "+exp.getMessage(),exp);
            }
            finally {
                closeSession(session);
            }
    }
}        
