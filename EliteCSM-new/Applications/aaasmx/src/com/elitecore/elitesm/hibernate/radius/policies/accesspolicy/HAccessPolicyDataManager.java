package com.elitecore.elitesm.hibernate.radius.policies.accesspolicy;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.BaseDatasourceException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.AccessPolicyDataManager;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.AccessPolicyDetailData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyDetailData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IWeekDayData;
import com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.WeekDayData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.exception.Hibernate2DatasourceTranslaterFactory;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

public class HAccessPolicyDataManager extends HBaseDataManager implements AccessPolicyDataManager {
    
    private static final String NAME = "name";
	private static final String ACCESS_POLICY_ID = "accessPolicyId";
	private static final String MDOULE = "HAccessPolicyDataManager";

	public List getList( ) throws DataManagerException {
        List accessPolicyList = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(AccessPolicyData.class);
            
            accessPolicyList = criteria.list();
        } catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy List, Reason: " + e.getMessage(), e);
		}
        return accessPolicyList;
    }
    
    public List getList( IAccessPolicyData accessPolicyData ) throws DataManagerException {
        List accessPolicyList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(AccessPolicyData.class);
            
            if (Strings.isNullOrBlank(accessPolicyData.getAccessPolicyId()) == false)
                criteria.add(Restrictions.eq("accessPolicyId", accessPolicyData.getAccessPolicyId()));
            
            accessPolicyList = criteria.list();
        } catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy List, Reason: " + e.getMessage(), e);
		}
        return accessPolicyList;
    }
    
    public PageList search( IAccessPolicyData accessPolicyData , int pageNo , int pageSize ) throws DataManagerException {
        PageList pageList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(AccessPolicyData.class);
            
            if ((accessPolicyData.getName() != null && accessPolicyData.getName().length() > 0)) {
                criteria.add(Restrictions.ilike(NAME, accessPolicyData.getName()));
            }
            
            if ((accessPolicyData.getCommonStatusId() != null && accessPolicyData.getCommonStatusId().length() > 0)) {
                criteria.add(Restrictions.ilike("commonStatusId", accessPolicyData.getCommonStatusId()));
            }
            
            int totalItems = criteria.list().size();
            
            criteria.setFirstResult((pageNo - 1) * pageSize);
            
            if (pageSize > 0) {
                criteria.setMaxResults(pageSize);
            }
            
            List accessPolicyList = criteria.list();
            
            long totalPages = (long) Math.ceil(totalItems / pageSize);
            if(totalItems%pageSize == 0)
	        totalPages-=1;
            pageList = new PageList(accessPolicyList, pageNo, totalPages, totalItems);
            
        } catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy List, Reason: " + e.getMessage(), e);
		}
        return pageList;
    }
    
    public List getWeekDay( IWeekDayData weekDayData ) throws DataManagerException {
        List accessPolicyList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(WeekDayData.class);
            
            if (weekDayData.getWeekDayId() != null)
                criteria.add(Restrictions.eq("weekDayId", weekDayData.getWeekDayId()));
            
            if (weekDayData.getName() != null)
                criteria.add(Restrictions.eq(NAME, weekDayData.getName()));
            
            accessPolicyList = criteria.list();
        } catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy Week Day List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy Week Day List, Reason: " + e.getMessage(), e);
		}
        return accessPolicyList;
    }
    
    
    public List updateStatus( String accessPolicyId , String commonStatus , Timestamp statusChangeDate ) throws DataManagerException {
        List accessPolicyList = null;
        Session session = getSession();
        AccessPolicyData accessPolicyData = null;
        
        try {
            Criteria criteria = session.createCriteria(AccessPolicyData.class);
            accessPolicyData = (AccessPolicyData) criteria.add(Restrictions.eq(ACCESS_POLICY_ID, accessPolicyId)).uniqueResult();
            
            accessPolicyData.setCommonStatusId(commonStatus);
            
            accessPolicyData.setStatusChangeDate(statusChangeDate);
            
            session.update(accessPolicyData);
            
            accessPolicyData = null;
            criteria = null;
        } catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update Access Policy Status, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update Access Policy Status, Reason: " + e.getMessage(), e);
		}
        return accessPolicyList;
    }
    
    public List getStartWeekDayList( ) throws DataManagerException {
        List accessPolicyList = null;
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(WeekDayData.class);
            accessPolicyList = criteria.list();
        } catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy Start Week Day List, Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Access Policy Start Week Day List, Reason: " + e.getMessage(), e);
		}
        return accessPolicyList;
    }
    
    public boolean verifyAccessPolicyName( String policyName ) throws BaseDatasourceException {
        
        boolean isPolicyName = false;
        List accessPolicyList = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(AccessPolicyData.class);
            criteria.add(Restrictions.eq(NAME, policyName.trim()));
            accessPolicyList = criteria.list();
            if (accessPolicyList != null && accessPolicyList.size() > 0) {
                isPolicyName = true;
            }
            
        }
        catch (HibernateException hExp) {
            throw Hibernate2DatasourceTranslaterFactory.getInstance().translateToDatasourceExceptionWithSource(hExp, "Verify Access Policy Name");
        }
        catch (Exception exp) {
            throw new BaseDatasourceException(exp.getMessage(), "Verify Access Policy Name", exp);
        }
        
        return isPolicyName;
    }
    
    public boolean verifyAccessPolicyName( String accessPolicyId , String policyName ) throws BaseDatasourceException {

        boolean isPolicyName = false;
        List accessPolicyList = null;
        
        try {
            Session session = getSession();
            Criteria criteria = session.createCriteria(AccessPolicyData.class);
            criteria.add(Restrictions.eq(NAME, policyName.trim()));
            criteria.add(Restrictions.ne(ACCESS_POLICY_ID, accessPolicyId));
            accessPolicyList = criteria.list();
            if (accessPolicyList != null && accessPolicyList.size() > 0) {
                isPolicyName = true;
            }
        }
        catch (HibernateException hExp) {
            throw Hibernate2DatasourceTranslaterFactory.getInstance().translateToDatasourceExceptionWithSource(hExp, "Verify Access Policy Name");
        }
        catch (Exception exp) {
            throw new BaseDatasourceException(exp.getMessage(), "Verify Access Policy Name", exp);
        }
        return isPolicyName;
    }
    
    
	@Override
	public IAccessPolicyData getAccessPolicyById(String accessPolicyId) throws DataManagerException {
		
		return getAccessPolicy(ACCESS_POLICY_ID, accessPolicyId);
		
	}

	@Override
	public IAccessPolicyData getAccessPolicyByName(String accessPolicyName) throws DataManagerException {
		
		return getAccessPolicy(NAME, accessPolicyName);
		
	}
	
	private IAccessPolicyData getAccessPolicy(String propertyName, Object value) throws DataManagerException {
		
		String accessPolicyName = (NAME.equals(propertyName)) ? (String) value : "Access Policy";
		
		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(AccessPolicyData.class).add(Restrictions.eq(propertyName, value));
			AccessPolicyData accessPolicy = (AccessPolicyData) criteria.uniqueResult();
			
			if (accessPolicy == null) {
				throw new InvalidValueException("Access Policy not found");
			}
			
			accessPolicyName = accessPolicy.getName();
			
			return accessPolicy;
			
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive " + accessPolicyName + ", Reason : " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive " + accessPolicyName + ", Reason : " + e.getMessage(), e);
		}
		
	}
	

	@Override
	public String create(Object obj) throws DataManagerException {
		IAccessPolicyData accessPolicy = (IAccessPolicyData) obj;
		String accessPolicyName = accessPolicy.getName();

		try {

			Session session = getSession();
			session.clear();
			session.save(accessPolicy);
			
			if (Collectionz.isNullOrEmpty(accessPolicy.getAccessPolicyDetailDataList()) == false) {
				for (Iterator acessPolicyDetailSetIterator = accessPolicy.getAccessPolicyDetailDataList().iterator(); acessPolicyDetailSetIterator.hasNext();) {
					IAccessPolicyDetailData accessPolicyDetailData = (IAccessPolicyDetailData) acessPolicyDetailSetIterator.next();
					accessPolicyDetailData.setAccessPolicyId(accessPolicy.getAccessPolicyId());
					session.save(accessPolicyDetailData);
				}
			}
			
			session.flush();
			session.clear();
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MDOULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + accessPolicyName + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			Logger.logTrace(MDOULE, he);
			throw new DataManagerException(FAILED_TO_CREATE + accessPolicyName + REASON + he.getMessage(), he);
		} catch (Exception e) {
			Logger.logTrace(MDOULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + accessPolicyName + REASON + e.getMessage(), e);
		}
		return accessPolicyName;

	}
	

	@Override
	public void updateAccessPolicyById(IAccessPolicyData accessPolicy, String accessPolicyId) throws DataManagerException {

		updateAccessPolicy(accessPolicy, ACCESS_POLICY_ID, accessPolicyId);
		
	}

	@Override
	public void updateAccessPolicyByName(IAccessPolicyData accessPolicy, String accessPolicyName) throws DataManagerException {

		updateAccessPolicy(accessPolicy, NAME, accessPolicyName);
		
	}
	
	private void updateAccessPolicy(IAccessPolicyData accessPolicy, String propertyName, Object value) throws DataManagerException {

		String accessPolicyName = (NAME.equals(propertyName)) ? (String) value : "Access Policy";

		try {
			
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(AccessPolicyData.class).add(Restrictions.eq(propertyName, value));
			IAccessPolicyData accessPolicyData = (IAccessPolicyData) criteria.uniqueResult();
			
			if (accessPolicyData == null) {
				throw new InvalidValueException("Access Policy not found");
			}
			
			accessPolicyName = accessPolicyData.getName();
			
			criteria = session.createCriteria(AccessPolicyDetailData.class).add(Restrictions.eq(ACCESS_POLICY_ID, accessPolicyData.getAccessPolicyId()));
			
			List accessPolicyDetailDatas = criteria.list();
			
			if (Collectionz.isNullOrEmpty(accessPolicyDetailDatas) == false) {
				
				deleteObjectList(accessPolicyDetailDatas, session);
				
			}
			
			if (Collectionz.isNullOrEmpty(accessPolicy.getAccessPolicyDetailDataList()) == false) {

				List accessPolicyDetails = accessPolicy.getAccessPolicyDetailDataList();

				Iterator iteratorAccessPolicyDetail = accessPolicyDetails.iterator();
				while (iteratorAccessPolicyDetail.hasNext()) {

					IAccessPolicyDetailData accessPolicyDetail = (IAccessPolicyDetailData) iteratorAccessPolicyDetail.next();
					accessPolicyDetail.setAccessPolicyId(accessPolicyData.getAccessPolicyId());

					session.save(accessPolicyDetail);
					session.flush();

				}

			}
			
			if (accessPolicyData.getCommonStatusId().equals(accessPolicy.getCommonStatusId()) == false) {
				accessPolicyData.setStatusChangeDate(new Timestamp(new Date().getTime()));
			}
			accessPolicyData.setCommonStatusId(accessPolicy.getCommonStatusId());
			accessPolicyData.setName(accessPolicy.getName());
			accessPolicyData.setDescription(accessPolicy.getDescription());
			accessPolicyData.setLastUpdated(accessPolicy.getLastUpdated());
			accessPolicyData.setAccessStatus(accessPolicy.getAccessStatus());
			
			session.update(accessPolicyData);
			session.flush();
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " + accessPolicyName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to update " + accessPolicyName + ", Reason : " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to update " + accessPolicyName + ", Reason : " + e.getMessage(), e);
		}
		
	}

	
	@Override
	public String deleteAccessPolicyById(String accessPolicyId) throws DataManagerException {
		
		return deleteAccessPolicy(ACCESS_POLICY_ID, accessPolicyId);
		
	}

	@Override
	public String deleteAccessPolicyByName(String accessPolicyName) throws DataManagerException {
		
		return deleteAccessPolicy(NAME, accessPolicyName);
		
	}

	private String deleteAccessPolicy(String propertyName, Object value) throws DataManagerException {
		
		String accessPolicyName = (NAME.equals(propertyName)) ? (String) value : "Access Policy";

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(AccessPolicyData.class).add(Restrictions.eq(propertyName, value));
			AccessPolicyData accessPolicy = (AccessPolicyData) criteria.uniqueResult();

			if (accessPolicy == null) {
				throw new InvalidValueException("Access Policy not found");
			}

			accessPolicyName = accessPolicy.getName();
			
			deleteObjectList(accessPolicy.getAccessPolicyDetailDataList(), session);
			
			session.delete(accessPolicy);
			session.flush();

			return accessPolicyName;

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + accessPolicyName + ", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to delete " + accessPolicyName + ", Reason : " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to delete " + accessPolicyName + ", Reason : " + e.getMessage(), e);
		} 

	}

	
}
