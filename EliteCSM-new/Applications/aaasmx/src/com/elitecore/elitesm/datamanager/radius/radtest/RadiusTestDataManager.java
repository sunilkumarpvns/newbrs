/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   RadiusDataManager.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.datamanager.radius.radtest;

import java.util.List;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.DataManager;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestParamData;

public interface RadiusTestDataManager extends DataManager{
    
    public void create(IRadiusTestData radData) throws DataManagerException;
    
    public void createParam(IRadiusTestParamData radParamData) throws DataManagerException;
    
    public List getList() throws DataManagerException;
    
    public void delete(String radiusTestId) throws DataManagerException;
    
    public IRadiusTestData getRadiusObj(String radiusTestId) throws DataManagerException;
    
    public void update(IRadiusTestData radData,List paramList) throws DataManagerException;
}
