package com.elitecore.nvsmx.commons.model.audit;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.elitecore.corenetvertex.sm.audit.AuditData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditCriteriaData;
import com.elitecore.nvsmx.commons.model.staff.StaffDAO;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;

public class AuditDAO {

	private static final String MODULE = AuditDAO.class.getSimpleName();

	@SuppressWarnings("unchecked")
	public static List<AuditData> searchCriteria(AuditCriteriaData auditCriteriaData, int firstResult, int maxResults, String sortColumnName, String sortColumnOrder)throws HibernateDataException {
		Session session = null;
		try {
			session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(AuditData.class);
			if (auditCriteriaData.getFromDate() != null) {
				criteria.add(Restrictions.ge("auditDate",auditCriteriaData.getFromDate()));
			}
			if (auditCriteriaData.getToDate() != null) {
				auditCriteriaData.setToDate(getEndOfDay(auditCriteriaData.getToDate()));
				criteria.add(Restrictions.le("auditDate",auditCriteriaData.getToDate()));
			}
			if (Strings.isNullOrEmpty(auditCriteriaData.getStaffUserName()) == false) {
			    criteria.add(Restrictions.eq("staffData", StaffDAO.getStaffByUserName(auditCriteriaData.getStaffUserName())));
			}
			if (sortColumnName != null && sortColumnName.trim().length() > 0) {
				if (sortColumnOrder != null && sortColumnOrder.equalsIgnoreCase("desc")) {
					criteria.addOrder(Order.desc(sortColumnName));
				} else {
					criteria.addOrder(Order.asc(sortColumnName));
				}
			} else {
				criteria.addOrder(Order.desc("auditDate"));
			}
			if(firstResult >=0 && maxResults >= 0) {
				criteria.setFirstResult(firstResult);
				criteria.setMaxResults(maxResults);
			}
			return ((List<AuditData>) criteria.list());

		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to search entity "+ auditCriteriaData.getClass().getName()+ ". Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to search entity "+ auditCriteriaData.getClass().getName()+ ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}
	
	/**
	 * It will return end of the day of the given date
	 * @param date
	 * @return Timestamp
	 */
	private static Timestamp getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
        return timestamp;
    }

	/**
	 * This method is used to fetch all the audit logs
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param sortColumnName
	 * @param sortColumnOrder
	 * @return List<AuditData>
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static List<AuditData> findAll(int firstResult, int maxResults,String sortColumnName, String sortColumnOrder)throws HibernateDataException {
		try {
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(AuditData.class);
			if (sortColumnName != null && sortColumnName.trim().length() > 0) {
				if (sortColumnOrder != null && sortColumnOrder.equalsIgnoreCase("desc")) {
					criteria.addOrder(Order.desc(sortColumnName));
				} else {
					criteria.addOrder(Order.asc(sortColumnName));
				}
			} else {
				criteria.addOrder(Order.desc("auditDate"));
			}
			if(firstResult >=0 && maxResults >= 0) {
				criteria.setFirstResult(firstResult);
				criteria.setMaxResults(maxResults);
			}
			return ((List<AuditData>) criteria.list());

		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to get list of type AuditData. Reason: "+ he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to get list of type AuditData. Reason: "+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<AuditData> searchHistory(String auditableId,String actualId) throws HibernateDataException {
		Session session = null;
		try {
			session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(AuditData.class);

			criteria.add(Restrictions.eq("resourceId", auditableId));
			criteria.addOrder(Order.desc("auditDate"));

			List<AuditData> auditDatas = Collectionz.newArrayList();
			List<AuditData> auditDatasDummy = criteria.list();

			if (auditableId.equals(actualId)) {
				return auditDatasDummy;
			}
			for (AuditData auditData : auditDatasDummy) {
				if (Strings.isNullOrEmpty(auditData.getHierarchy()) == false
						&& auditData.getHierarchy().contains(actualId)) {
					auditDatas.add(auditData);
				}
			}
			return auditDatas;
		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to search history.Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to search history.Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

}
