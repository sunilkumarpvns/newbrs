package com.elitecore.nvsmx.policydesigner.model.subscriber;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.sm.spr.SprData;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;

import com.elitecore.corenetvertex.spr.data.DBSPInterfaceData;
import com.elitecore.corenetvertex.spr.data.LDAPSPInterfaceData;
import com.elitecore.corenetvertex.spr.data.SubscriberRepositoryData;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * 
 * 
 * @author Chetan.Sankhala
 */
public class SubscriberRepositoryDAO {

	private static final String MODULE="SPR-DAO";
	
	public static SprData getSubscriberRepositoryData(String id) throws HibernateDataException {

		Session session = null;
		SprData subscriberRepositoryData = null;
		try{
			session = HibernateSessionFactory.getNewSession();
			session.beginTransaction();
			subscriberRepositoryData = (SprData) session.get(SprData.class, id);
		}finally{
			HibernateSessionUtil.closeSession(session);
		}
		return subscriberRepositoryData;
	}
	
	public static DBSPInterfaceData getDBSPInterfaceData(Long id) throws HibernateDataException {

		Session session = null;
		DBSPInterfaceData dbSPInterfaceData = null;
		try{
			session = HibernateSessionFactory.getNewSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(DBSPInterfaceData.class)
					.add(Restrictions.eq("driverInstanceId", id));
			List<DBSPInterfaceData> dbspInterfaceDataList =  criteria.list();
			if (Collectionz.isNullOrEmpty(dbspInterfaceDataList)==false){
				dbSPInterfaceData = dbspInterfaceDataList.get(0);
			}
		}finally{
			HibernateSessionUtil.closeSession(session);
		}
		return dbSPInterfaceData;
	}
	
	public static LDAPSPInterfaceData getLDAPSPInterfaceData(Long id) throws HibernateDataException {

		Session session = null;
		LDAPSPInterfaceData ldapSPInterfaceData = null;
		try{
			session = HibernateSessionFactory.getNewSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(LDAPSPInterfaceData.class)
					.add(Restrictions.eq("driverInstanceId", id));
			List<LDAPSPInterfaceData> ldapspInterfaceDataList =  criteria.list();
			if (Collectionz.isNullOrEmpty(ldapspInterfaceDataList)==false){
				ldapSPInterfaceData = ldapspInterfaceDataList.get(0);
			}
		}finally{
			HibernateSessionUtil.closeSession(session);
		}
		return ldapSPInterfaceData;
	}
	
	
}
