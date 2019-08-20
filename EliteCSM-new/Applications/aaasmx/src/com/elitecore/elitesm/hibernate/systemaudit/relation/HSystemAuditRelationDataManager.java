package com.elitecore.elitesm.hibernate.systemaudit.relation;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditRelationData;
import com.elitecore.elitesm.datamanager.systemaudit.relation.SystemAuditRelationDataManager;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;

public class HSystemAuditRelationDataManager extends HBaseDataManager implements SystemAuditRelationDataManager {

	@Override
	public SystemAuditRelationData getSystemAuditId(String moduleName) throws DataManagerException {
		try{

			Session session = getSession();
            Criteria criteria = session.createCriteria(SystemAuditRelationData.class);
    		criteria.add(Restrictions.eq("moduleName",moduleName));
    		
    		Object auditId = criteria.uniqueResult();
    		return (SystemAuditRelationData)auditId;

         }catch(HibernateException hExp){
            throw new DataManagerException(hExp.getMessage(), hExp);
        }
	}

	@Override
	public void create(SystemAuditRelationData data) throws DataManagerException {
		Session session = getSession();
		try{
			session.save(data);
			session.flush();
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to create " + data.getModuleName() + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to create " + data.getModuleName() +", Reason: " +  hbe.getMessage(),hbe);
		} catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to create " + data.getModuleName() + ", Reason: "+e.getMessage(),e);
		}
		
	}
}
