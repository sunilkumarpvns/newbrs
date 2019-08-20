package com.elitecore.netvertexsm.hibernate.servermgr.spr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.SPRDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.data.SPRData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteUtility;

public class HSPRDataManager extends HBaseDataManager implements SPRDataManager {

    private static final String SPR_ID = "id";
    private static final String SPR_NAME = "sprName";

    @Override
    public void delete(String[] selectedIds) throws DataManagerException {
	try {
	    Session session = getSession();
	    List sprDatas = session.createCriteria(SPRData.class).add(Restrictions.in(SPR_ID, selectedIds)).list();
	    deleteObjectList(sprDatas, session);
	} catch (HibernateException hExp) {
	    throw new DataManagerException(hExp.getMessage(), hExp);
	} catch (Exception exp) {
	    throw new DataManagerException(exp.getMessage(), exp);
	}

    }

    @Override
    public void create(SPRData sprData) throws DataManagerException {
	try {

	    Session session = getSession();
	    //sprData.setCreatedDate(EliteUtility.getCurrentTimeStamp());
	    sprData.setClientIp(EliteUtility.getClientIP());
	    session.save(sprData);
	    session.flush();

	} catch (ConstraintViolationException hExp) {
	    throw hExp;
	} catch (HibernateException hExp) {
	    throw new DataManagerException(hExp.getMessage(), hExp);
	} catch (Exception exp) {
	    throw new DataManagerException(exp.getMessage(), exp);
	}

    }

    @Override
    public PageList search(SPRData sprData, int requiredPageNo, int pageSize)
	    throws DataManagerException {
	PageList pageList = null;
	try {
	    Session session = getSession();
	    Criteria criteria = session.createCriteria(SPRData.class).addOrder(Order.asc(SPR_NAME));
	    if (Strings.isNullOrBlank(sprData.getSprName()) == false) {
		criteria.add(Restrictions.ilike(SPR_NAME, sprData.getSprName(), MatchMode.ANYWHERE));
	    }
	    Criteria criteriaNoOfRecords = session.createCriteria(SPRData.class).addOrder(Order.asc(SPR_NAME));
	    if (Strings.isNullOrBlank(sprData.getSprName()) == false) {
		criteriaNoOfRecords.add(Restrictions.ilike(SPR_NAME, sprData.getSprName(), MatchMode.ANYWHERE));
	    }

	    Long totalItems = (Long) criteriaNoOfRecords.setProjection(Projections.rowCount()).uniqueResult();
	    criteria.setFirstResult(((requiredPageNo - 1) * pageSize));
	    if (pageSize > 0) {
		criteria.setMaxResults(pageSize);
	    }
	    List sprDataList = criteria.list();
	    long totalPages = (long) Math.ceil(totalItems / pageSize);
	    if (totalItems % pageSize == 0) {
		totalPages -= 1;
	    }
	    pageList = new PageList(sprDataList, requiredPageNo, totalPages, totalItems);
	} catch (HibernateException hExe) {
	    throw new DataManagerException(hExe.getMessage(), hExe);
	} catch (Exception exp) {
	    throw new DataManagerException(exp.getMessage(), exp);
	}
	return pageList;
    }

    @Override
    public void update(SPRData sprData) throws DataManagerException {
	try {
	    Session session = getSession();
	    Criteria criteria = session.createCriteria(SPRData.class);
	  //  criteria.add(Restrictions.eq(SPR_ID, sprData.getId()));
	    SPRData sprInfo = (SPRData) criteria.list().get(0);
	    //sprInfo.setId(sprData.getId());
	    sprInfo.setSprName(sprData.getSprName());
	    sprInfo.setDescription(sprData.getDescription());
	    sprInfo.setDriverInstanceId(sprData.getDriverInstanceId());
	    sprInfo.setDatabaseDSId(sprData.getDatabaseDSId());
	    sprInfo.setBatchSize(sprData.getBatchSize());
	    sprInfo.setAlternateIdField(sprData.getAlternateIdField());
	    
	    sprInfo.setGroups(sprData.getGroups());
	    //sprInfo.setModifiedByStaff(sprData.getModifiedByStaff());
	    //sprInfo.setModifiedDate(EliteUtility.getCurrentTimeStamp());
	    sprInfo.setClientIp(EliteUtility.getClientIP());
	    session.update(sprInfo);
	} catch (ConstraintViolationException hExp) {
	    throw hExp;
	} catch (HibernateException hExp) {
	    throw new DataManagerException(hExp.getMessage(), hExp);
	} catch (Exception exp) {
	    throw new DataManagerException(exp.getMessage(), exp);
	}

    }

    @Override
    public SPRData getSPRData(String sprId) throws DataManagerException {
	SPRData sprData = null;
	try {
	    Session session = getSession();
	    List sprDatas = session.createCriteria(SPRData.class).add(Restrictions.eq(SPR_ID, sprId)).list();
	    if (Collectionz.isNullOrEmpty(sprDatas) == false) {
		sprData = (SPRData) sprDatas.get(0);
	    }
	    return sprData;
	} catch (HibernateException hExp) {
	    throw new DataManagerException(hExp.getMessage(), hExp);
	} catch (Exception exp) {
	    throw new DataManagerException(exp.getMessage(), exp);
	}
    }

    @Override
    public List<SPRData> getSPRDataList() throws DataManagerException {
	List sprList = null;
	try {
	    Session session = getSession();
	    sprList = session.createCriteria(SPRData.class).list();
	    return sprList;
	} catch (HibernateException hExp) {
	    throw new DataManagerException(hExp.getMessage(), hExp);
	} catch (Exception exp) {
	    throw new DataManagerException(exp.getMessage(), exp);
	}
    }

}
