package com.elitecore.elitesm.hibernate.radius.policies.radiuspolicy.radiuspolicytype;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.radiuspolicytype.RadiusPolicyTypeDataManager;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.radiuspolicytype.data.IRadiusPolicyParamTypeData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.radiuspolicytype.data.RadiusPolicyParamTypeData;

import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;


public class HRadiusPolicyTypeDataManager extends HBaseDataManager implements RadiusPolicyTypeDataManager {

    public List getList() throws DataManagerException {
        
        List radiusPolicyParamTypeList = null;
        
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(RadiusPolicyParamTypeData.class);
            criteria.addOrder (Order.asc("orderBy"));
            
            radiusPolicyParamTypeList = criteria.list();

//            session.close();
         }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        return radiusPolicyParamTypeList;
    } 

    public IRadiusPolicyParamTypeData getRadiusPolicyParamType(String paramUsageType) throws DataManagerException{
        IRadiusPolicyParamTypeData radiusPolicyParamTypeData = null;
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(RadiusPolicyParamTypeData.class);
        		criteria.add(Restrictions.eq("parameterUsage",paramUsageType));

        	radiusPolicyParamTypeData = (IRadiusPolicyParamTypeData)criteria.uniqueResult();

//        	session.close();
         }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
        return radiusPolicyParamTypeData;

    }

}
