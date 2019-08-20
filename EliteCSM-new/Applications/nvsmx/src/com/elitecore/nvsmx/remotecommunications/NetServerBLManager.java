package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.nvsmx.commons.model.sessionmanager.NetServerInstanceData;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 
 * @author Jay Trivedi
 *
 */

public class NetServerBLManager {

	/**
     * @return List
     * @throws DataManagerException
     * @purpose This method returns the list of all the records of
     *          NetServerInstance table.
     */
    public List<NetServerInstanceData> getNetServerInstanceList( ) throws HibernateDataException{
    	Session session = null;
        try {
            List<NetServerInstanceData> lstNetServerInstanceList = null;
            	session = HibernateSessionFactory.getSession();
            	session.beginTransaction();
            	Criteria criteria = session.createCriteria(NetServerInstanceData.class)
            			.add(Restrictions.eq("systemGenerated","N"));
            	criteria.addOrder(Order.asc("netServerTypeId"));
            	criteria.addOrder(Order.asc("netServerId"));
            	lstNetServerInstanceList = criteria.list(); 
          
            return lstNetServerInstanceList;
        } catch (HibernateException e) {
			throw new HibernateDataException(e.getMessage(), e);
        } catch (Exception e) {
			throw new HibernateDataException("getNetServerInstanceList operation failed. Reason:" + e.getMessage(), e);
        } finally {
        	HibernateSessionUtil.closeSession(session);
        }
        
    }
    
    public @Nullable NetServerInstanceData getNetServerInstance(String netServerCode) throws HibernateDataException {
    	Session session = null;
        try {
            List<NetServerInstanceData> lstNetServerInstanceList = null;
            try {
            	session = HibernateSessionFactory.getSession();
            	session.beginTransaction();
            	Criteria criteria = session.createCriteria(NetServerInstanceData.class)
            			.add(Restrictions.eq("systemGenerated","N"));
            	criteria.add(Restrictions.eq("netServerCode", netServerCode));
            	criteria.addOrder(Order.asc("netServerTypeId"));
            	criteria.addOrder(Order.asc("netServerId"));
            	lstNetServerInstanceList = criteria.list(); 
            } catch (Exception e){
            	throw new HibernateDataException(e.getMessage(), e);
            }
            if (Collectionz.isNullOrEmpty(lstNetServerInstanceList)) {
            	return null;
            }
            return lstNetServerInstanceList.get(0);
        } catch (HibernateException e) {
            throw new HibernateDataException(e.getMessage(), e);
        }catch (Exception e) {
            throw new HibernateDataException("getNetServerInstance operation failed. Reason:" + e.getMessage(), e);
        }
        finally {
            HibernateSessionUtil.closeSession(session);
        }
        
    }
    
}
