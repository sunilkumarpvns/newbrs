package com.elitecore.netvertexsm.hibernate.servermgr.spr.ddf;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.spr.ddf.DDFEntryData;
import com.elitecore.corenetvertex.spr.ddf.DDFTableData;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.servermgr.spr.ddf.DDFDataManager;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.EliteUtility;

public class HDDFDataManager extends HBaseDataManager implements DDFDataManager {

	@Override
	public void update(DDFTableData ddfData) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DDFTableData.class);
			criteria.add(Restrictions.eq("id", ddfData.getId()));

			DDFTableData ddfTableData = (DDFTableData) criteria.list().get(0);
			ddfTableData.setDefaultSPR(ddfData.getDefaultSPR());
			ddfTableData.setStripPrefixes(ddfData.getStripPrefixes());
			
			setUpdateAuditDetail(ddfTableData);
			session.update(ddfTableData);
			update(ddfData.getDdfEntries(), ddfData);
			
		} catch (ConstraintViolationException hExp) {
			throw hExp;
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}

	}

	@Override
	public DDFTableData getDDFTableData() throws DataManagerException {
		DDFTableData ddfTableData = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DDFTableData.class);
			List list = criteria.list();
			if (list.isEmpty() == false) {
				ddfTableData = (DDFTableData) list.get(0);
			}
			return ddfTableData;

		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public void create(DDFTableData ddfData) throws DataManagerException {
		try {
			setCreateAuditDetail(ddfData);
			getSession().save(ddfData);
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	private void update(List<DDFEntryData> ddfEntries, DDFTableData ddfTableData) throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DDFEntryData.class);
			criteria.add(Restrictions.eq("ddfTableData", ddfTableData));
			
			List<DDFEntryData> oldDdfEntries = criteria.list();
			
			if (oldDdfEntries.isEmpty() == false) {
				for(DDFEntryData entryData : oldDdfEntries) {
					session.delete(entryData);
				}
			}

			session.flush();
			
			if (Collectionz.isNullOrEmpty(ddfEntries) == false) {
				for (DDFEntryData entryData : ddfEntries) {
					session.save(entryData);
				} 
			}
		} catch (ConstraintViolationException hExp) {
			throw hExp;
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	private void setCreateAuditDetail(DDFTableData ddfTableData) {
		ddfTableData.setClientIp(EliteUtility.getClientIP());
		ddfTableData.setCreatedDate(EliteUtility.getCurrentTimeStamp());
		ddfTableData.setCreatedByStaffId(EliteUtility.getCurrentUserId());
	}
	
	private void setUpdateAuditDetail(DDFTableData ddfTableData) {
		ddfTableData.setClientIp(EliteUtility.getClientIP());
		ddfTableData.setModifiedDate(EliteUtility.getCurrentTimeStamp());
		ddfTableData.setModifiedByStaffId(EliteUtility.getCurrentUserId());
	}
}
