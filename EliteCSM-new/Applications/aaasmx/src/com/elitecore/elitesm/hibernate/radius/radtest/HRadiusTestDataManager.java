/**                                                     
 * Copyright (C) Elitecore Technologies Ltd.            
 * FileName   HRadiusDataManager.java                 		
 * ModualName radius    			      		
 * Created on 2 April, 2008
 * Last Modified on                                     
 * @author :  SMCodeGen
 */                                                     
package com.elitecore.elitesm.hibernate.radius.radtest;
   
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.radtest.RadiusTestDataManager;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.IRadiusTestParamData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestData;
import com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestParamData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
      
public class HRadiusTestDataManager extends HBaseDataManager implements RadiusTestDataManager{
    public void create(IRadiusTestData radData) throws DataManagerException {
        try{
            Session session = getSession();
            session.save(radData);
            session.flush();
            
            List paramList = radData.getParamList();
            int orderNumber = 1;
            int size = paramList.size();
			for(int index=0;index<size;index++) {
                IRadiusTestParamData radParamData = (IRadiusTestParamData)paramList.get(index);
                radParamData.setNtradId(radData.getNtradId());
                radParamData.setOrderNumber(orderNumber++);
                session.save(radParamData);
            }
            session.flush();
            
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
            
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }
    }
    
    public void createParam(IRadiusTestParamData radParamData) throws DataManagerException {
        try{
            Session session = getSession();
            session.save(radParamData);
            session.flush();
            
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
            
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }
    }
    
    public List getList() throws DataManagerException {
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(RadiusTestData.class);
            return criteria.list();
            
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
            
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }
    }
    
    public void delete(String radiusTestId)  throws DataManagerException{
        RadiusTestData radiusTestData = null;
        
        try{
            Session session = getSession();
            
            Criteria criteria = session.createCriteria(RadiusTestParamData.class).add(Restrictions.like("ntradId", radiusTestId));
            List radParamSet = criteria.list();
            for(Iterator iter = radParamSet.iterator();iter.hasNext();) {
                RadiusTestParamData radParamData = (RadiusTestParamData)iter.next();
                session.delete(radParamData);
            }
            session.flush();
            
            Criteria criteria1 = session.createCriteria(RadiusTestData.class);
            radiusTestData = (RadiusTestData)criteria1.add(Restrictions.like("ntradId",radiusTestId)).uniqueResult();
            session.delete(radiusTestData);
            session.flush();
                 
         }catch(HibernateException hExp){
             throw new DataManagerException(hExp.getMessage(), hExp);
             
         }catch(Exception exp){
             throw new DataManagerException(exp.getMessage(),exp);
         }
    }
    
    public IRadiusTestData getRadiusObj(String radiusTestId) throws DataManagerException {
       try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(RadiusTestData.class).add(Restrictions.like("ntradId", radiusTestId));
            return (RadiusTestData)criteria.uniqueResult();
            
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
            
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }
    }
    
    public void update(IRadiusTestData radData, List paramList) throws DataManagerException {
        try{
            Session session = getSession();
            session.saveOrUpdate(radData);
            session.flush();
            
            Criteria criteria = session.createCriteria(RadiusTestParamData.class).add(Restrictions.like("ntradId", radData.getNtradId()));
            List radParamSet = criteria.list();
            for(Iterator iter = radParamSet.iterator();iter.hasNext();) {
                RadiusTestParamData radParamData = (RadiusTestParamData)iter.next();
                session.delete(radParamData);
            }
            session.flush();
            
            int size = paramList.size();
            int orderNumber = 1;
			for(int index=0;index<size;index++) {
                IRadiusTestParamData radParamData = (IRadiusTestParamData)paramList.get(index);
                radParamData.setNtradId(radData.getNtradId());
                radParamData.setOrderNumber(orderNumber++);
                session.save(radParamData);
            }
            session.flush();

            
        }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
            
        }catch(Exception exp){
            throw new DataManagerException(exp.getMessage(),exp);
        }
    }
}
