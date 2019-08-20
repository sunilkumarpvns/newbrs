package com.elitecore.elitesm.hibernate.datasource.ldap;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ReferenceFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.datasource.ldap.LDAPDSDataManager;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.ILDAPBaseDnDetailData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.ILDAPDatasourceData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPBaseDnDetailData;
import com.elitecore.elitesm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.elitesm.datamanager.digestconf.data.DigestConfigInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthDriverData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;

import net.sf.json.JSONArray;

public class HLDAPDatasourceDataManager extends HBaseDataManager implements LDAPDSDataManager{

	private static final String LDAP_DS_ID = "ldapDsId";
	private static final String LDAPDS_NAME = "name";
	private static final String MODULE = "HLDAPDatasourceDataManager";

	@Override
	public String create(Object obj) throws DataManagerException {
		ILDAPDatasourceData ldapDatasourceData = (ILDAPDatasourceData) obj;
		try {
			Session session = getSession();	
			session.clear();
			
			ldapDatasourceData.setAuditUId(UUIDGenerator.generate().toString());

			session.save(ldapDatasourceData);
			session.flush();
			session.clear();
			List baseDnDetailList = ldapDatasourceData.getSearchDnDetailList();

			if(Collectionz.isNullOrEmpty(baseDnDetailList) == false){
				int size = baseDnDetailList.size();
				int orderNumber = 1;
				for(int i=0;i<size;i++){
						ILDAPBaseDnDetailData ldapBaseDnDetail = (ILDAPBaseDnDetailData)baseDnDetailList.get(i);
						ldapBaseDnDetail.setLdapDsId(ldapDatasourceData.getLdapDsId());
						ldapBaseDnDetail.setOrderNumber(orderNumber++);
						session.save(ldapBaseDnDetail);
						session.flush();
						session.clear();
					}
				}
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + ldapDatasourceData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch (HibernateException e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + ldapDatasourceData.getName() + REASON +e.getMessage() ,e);
		}catch (Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + ldapDatasourceData.getName() + REASON +e.getMessage() ,e);
		}
		return ldapDatasourceData.getName();
	}
	
	public ILDAPDatasourceData getLDAPDSByName(String databaseName) throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ILDAPDatasourceData.class).add(Restrictions.eq(LDAPDS_NAME,databaseName.trim()));
			ILDAPDatasourceData data = (ILDAPDatasourceData) criteria.uniqueResult() ;
			return data;
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
		
	public PageList search(ILDAPDatasourceData ldapDatasourceData,int pageNo, int pageSize) throws DataManagerException{
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
			PageList pageList = null;

	        
			if((ldapDatasourceData.getName() != null && ldapDatasourceData.getName().length()>0 )){
	        	criteria.add(Restrictions.ilike(LDAPDS_NAME,"%"+ldapDatasourceData.getName()+"%"));
	        }

			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List policyList = criteria.list();
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(policyList, pageNo, totalPages ,totalItems);
			return  pageList;
		} catch (Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	public List getListOfLDAP(){
		List<ILDAPDatasourceData> listOfLDAP = new ArrayList<ILDAPDatasourceData>();
		
		Session session = getSession();
		Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
		listOfLDAP = criteria.list();	
		return listOfLDAP;
	}

	public List<LDAPBaseDnDetailData> getLdapBaseDnDetailDataByLdapId(String ldapDsId) throws DataManagerException {
		try{
			Session session = getSession();		
			Criteria criteria = session.createCriteria(LDAPBaseDnDetailData.class);
			List<LDAPBaseDnDetailData>tempList = criteria.add(Restrictions.eq(LDAP_DS_ID, ldapDsId)).addOrder(Order.asc("orderNumber")).list();			
			return tempList;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive LDAP Datasource. Reason: "+ hbe.getMessage(),hbe);
		}
	}

	public List getLDAPDetailsById(String LdapDsId){
		
		Session session = getSession();		
		Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
		List ldapDetailsList = criteria.add(Restrictions.eq(LDAP_DS_ID, LdapDsId)).list();
		
		return ldapDetailsList;
	}

	@Override
	public List getList(ILDAPDatasourceData ldapDataSourceData)throws DataManagerException {
		  List ldapDSList = null;
	      try{
	          Session session = getSession();
	          Criteria criteria = session.createCriteria(LDAPDatasourceData.class);

	          if(Strings.isNullOrBlank(ldapDataSourceData.getLdapDsId()) == false)
	          	criteria.add(Restrictions.eq(LDAP_DS_ID,ldapDataSourceData.getLdapDsId()));
	       
	          ldapDSList = criteria.list();

	      }catch(HibernateException hExp){
	          throw new DataManagerException(hExp.getMessage(), hExp);
	      }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(), exp);
	      }
	      return ldapDSList;
	}

	@Override
	public LDAPDatasourceData getLDAPDatasourceDataById(String digestConfId)throws DataManagerException {
		return getLDAPDatasouceByIdOrName(LDAP_DS_ID,digestConfId);
	}

	@Override
	public LDAPDatasourceData getLDAPDatasourceDataByName(String digestCofuguration) throws DataManagerException {
		return getLDAPDatasouceByIdOrName(LDAPDS_NAME,digestCofuguration);
	}
	
	private LDAPDatasourceData getLDAPDatasouceByIdOrName(String propertyName, Object propertyValue) throws DataManagerException{
		ILDAPDatasourceData ldapDSData = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ILDAPDatasourceData.class).add(Restrictions.eq(propertyName,propertyValue));
			 ldapDSData = (ILDAPDatasourceData) criteria.uniqueResult() ;
			
			if(ldapDSData == null){
				throw new InvalidValueException("LDAP Datasource not found");
			}
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retive LDAP Datasource, Reason: " +hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retive LDAP Datasource, Reason: "+exp.getMessage(), exp);
		}
		return (LDAPDatasourceData) ldapDSData;
	}
	
	@Override
	public String deleteById(String ldapDSId) throws DataManagerException {
		return delete(LDAP_DS_ID, ldapDSId);
	}
	
	@Override
	public String deleteByName(String ldapDSName)throws DataManagerException {
		return delete(LDAPDS_NAME, ldapDSName);
	}
	
	private String delete(String propertyName,Object propertyValue) throws DataManagerException {
		String datasourceName = (LDAPDS_NAME.equals(propertyName)) ? (String) propertyValue : "LDAP Datasource";
		try{
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(LDAPDatasourceData.class).add(Restrictions.eq(propertyName,propertyValue));
			LDAPDatasourceData ldapDSData = (LDAPDatasourceData) criteria.uniqueResult();
			
			if(ldapDSData == null){
				throw new InvalidValueException("LDAP Datasource not found");
			}
			
			criteria = session.createCriteria(LDAPAuthDriverData.class);
			LDAPAuthDriverData data = (LDAPAuthDriverData)criteria.add(Restrictions.eq(LDAP_DS_ID,ldapDSData.getLdapDsId())).uniqueResult();
			if(data != null){
				datasourceName = ldapDSData.getName();
				throw new ReferenceFoundException("LDAP datasource is already configured with driver(s)",ldapDSData.getName());
			}
			
			criteria = session.createCriteria(LDAPBaseDnDetailData.class).add(Restrictions.eq(LDAP_DS_ID, ldapDSData.getLdapDsId()));
			List<LDAPBaseDnDetailData> baseDetailList = criteria.list();
			deleteObjectList(baseDetailList, session);
			session.delete(ldapDSData);
			session.flush();
			
			return ldapDSData.getName();
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete "+ datasourceName +", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete "+ datasourceName +", Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete "+ datasourceName +", Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public void updateById(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData, String ldapDSId) throws DataManagerException {
		update(ldapDatasourceData, staffData, LDAP_DS_ID, ldapDSId);
	}

	@Override
	public void updateByName(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData, String queryOrPathParam) throws DataManagerException {
		update(ldapDatasourceData, staffData, LDAPDS_NAME, queryOrPathParam);
	}
	
	private void update(ILDAPDatasourceData ldapDatasourceData,IStaffData staffData,String propertyName,Object propertyValue) throws DataManagerException,DuplicateInstanceNameFoundException {
		String ldapDSName = null;
		try{

			verifyLDAPDatasourceDataName(ldapDatasourceData);

			Session session = getSession();		

			Criteria criteria = session.createCriteria(LDAPDatasourceData.class);
			LDAPDatasourceData tempLDAPDatasourceData = (LDAPDatasourceData)criteria.add(Restrictions.eq(propertyName, propertyValue)).uniqueResult();

			if( tempLDAPDatasourceData == null ){
				throw new InvalidValueException("LDAP Datasource not found");
			}

			LDAPDatasourceData newLdapData = new LDAPDatasourceData();

			newLdapData=(LDAPDatasourceData)ldapDatasourceData;

			criteria = session.createCriteria(LDAPBaseDnDetailData.class);
			List<LDAPBaseDnDetailData> actualBaseDnDetailList = criteria.add(Restrictions.eq(LDAP_DS_ID, tempLDAPDatasourceData.getLdapDsId())).list();
			tempLDAPDatasourceData.setSearchDnDetailList(actualBaseDnDetailList);

			ldapDSName = tempLDAPDatasourceData.getName();
			JSONArray jsonArray=ObjectDiffer.diff(tempLDAPDatasourceData,newLdapData);   

			tempLDAPDatasourceData.setName(ldapDatasourceData.getName());
			tempLDAPDatasourceData.setAddress(ldapDatasourceData.getAddress());

			tempLDAPDatasourceData.setTimeout(ldapDatasourceData.getTimeout());
			tempLDAPDatasourceData.setStatusCheckDuration(ldapDatasourceData.getStatusCheckDuration());
			tempLDAPDatasourceData.setLdapSizeLimit(ldapDatasourceData.getLdapSizeLimit());
			tempLDAPDatasourceData.setAdministrator(ldapDatasourceData.getAdministrator());
			tempLDAPDatasourceData.setPassword(ldapDatasourceData.getPassword());

			tempLDAPDatasourceData.setUserDNPrefix(ldapDatasourceData.getUserDNPrefix());
			tempLDAPDatasourceData.setMinimumPool(ldapDatasourceData.getMinimumPool());
			tempLDAPDatasourceData.setMaximumPool(ldapDatasourceData.getMaximumPool());
			tempLDAPDatasourceData.setLdapVersion(ldapDatasourceData.getLdapVersion());

			session.update(tempLDAPDatasourceData);
			session.flush();

			//get new data here...
			List<LDAPBaseDnDetailData> baseDnDetailList = ldapDatasourceData.getSearchDnDetailList();///users updated list ...
			tempLDAPDatasourceData.setSearchDnDetailList(ldapDatasourceData.getSearchDnDetailList());

			if(Collectionz.isNullOrEmpty(actualBaseDnDetailList) == false){
				for(int i=0;i<actualBaseDnDetailList.size();i++){
					LDAPBaseDnDetailData tempBaseDnDetailData = actualBaseDnDetailList.get(i);			
					session.delete(tempBaseDnDetailData);
				}			
			}

			if(Collectionz.isNullOrEmpty(baseDnDetailList) == false){
				int size = baseDnDetailList.size();
				int orderNumber = 1;
				for (int i = 0; i < size; i++) {
					ILDAPBaseDnDetailData ldapBaseDnDetail = (ILDAPBaseDnDetailData)baseDnDetailList.get(i);
					ldapBaseDnDetail.setLdapDsId(tempLDAPDatasourceData.getLdapDsId());
					ldapBaseDnDetail.setOrderNumber(orderNumber++);
					session.save(ldapBaseDnDetail);
					session.flush();
				}
			}

			staffData.setAuditId(tempLDAPDatasourceData.getAuditUId());
			staffData.setAuditName(ldapDatasourceData.getName());

			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DATABASE_DATASOURCE);

		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update "+ ldapDSName +", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update "+ ldapDSName +", Reason: "+ e.getMessage(),e);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update "+ ldapDSName +", Reason: "+ e.getMessage(),e);
		}
	}
	
	private void verifyLDAPDatasourceDataName(ILDAPDatasourceData ldapDSData) throws DuplicateInstanceNameFoundException {
		Criteria criteria = null;
		Session session = getSession();
		criteria = session.createCriteria(DigestConfigInstanceData.class);
		List list = criteria.add(Restrictions.eq(LDAPDS_NAME,ldapDSData.getName())).list();
		if (Collectionz.isNullOrEmpty(list)) {
			return;
		} else {
			throw new DuplicateInstanceNameFoundException("Duplicate LDAP Datasource name found");
		}
	}

	@Override
	public String getDatabaseIdFromName(String ldapDataSourceName) throws DataManagerException {
		String ldapDataSourceID = null;
		try{
			Session session = getSession();		
			Criteria criteria = session.createCriteria(LDAPDatasourceData.class).
			add(Restrictions.eq("name", ldapDataSourceName)).setProjection((Projections.property("ldapDsId")));
			
			ldapDataSourceID = (String) criteria.uniqueResult();
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive LDAP datasource Id, Reason :"+hbe.getMessage(),hbe);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive LDAP datasource Id, Reason :"+e.getMessage(),e);
		}
		return ldapDataSourceID;
	}
}