package com.elitecore.elitesm.hibernate.core.system.staff;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;

import org.apache.log4j.MDC;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.staff.StaffDataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffGroupRelData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffGroupRelData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.systemaudit.Data.ISystemAuditData;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditData;
import com.elitecore.elitesm.datamanager.systemaudit.Data.SystemAuditDetails;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.passwordutil.PasswordEncryption;

public class HStaffDataManager extends HBaseDataManager implements StaffDataManager{
	/**
	 * @author  dhavalraval
	 * @param 	radiusPolicyData
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of StaffData.
	 */
	private static final String MODULE = "STAFF DATA MANAGER";
	public List<StaffData> getList() throws DataManagerException{
		List<StaffData> staffList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(StaffData.class);

			staffList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return staffList;
	}

	/*public List getStaffGroupRelList(String staffId) throws DataManagerException{
			List lstStaffGroupRelList = null;
			try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(StaffGroupRelData.class);
				criteria.add(Restrictions.eq("staffId",staffId));
				criteria.createAlias("groupData","groupData");
//				criteria.add(Restrictions.eq("groupData.",))
				lstStaffGroupRelList = criteria.list();
			} catch (HibernateException hExp) {
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
			return lstStaffGroupRelList;
		}*/

	/**
	 * @author  dhavalraval
	 * @param   staffId
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returs the list of GroupData for specific StaffId.
	 */
	public List getStaffGroupRelList(String staffId) throws DataManagerException{
		List lstStaffGroupRelList = null;

		try {
			Session session = getSession();

			Criteria criteria = session.createCriteria(GroupData.class);
			criteria.createAlias("staffGroupRel","staffGroupRelData");
			criteria.add(Restrictions.eq("staffGroupRelData.staffId",staffId));

			lstStaffGroupRelList = criteria.list();
		} catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lstStaffGroupRelList;
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @return  IStaffData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to get list of StaffData.
	 */
	public List getList(IStaffData staffData) throws DataManagerException{
		List staffList = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(StaffData.class);

			if(Strings.isNullOrBlank(staffData.getStaffId()) == false)         
				criteria.add(Restrictions.eq("staffId",staffData.getStaffId()));

			if(staffData.getName() != null)
				criteria.add(Restrictions.eq("name",staffData.getName()));

			if(staffData.getUsername() != null)
				criteria.add(Restrictions.eq("username",staffData.getUsername()));  

			staffList = criteria.list();

		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return staffList;
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @param   pageNo
	 * @param   pageSize
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method is return list with specific arguments.
	 */
	public PageList search(IStaffData staffData, int pageNo, int pageSize) throws DataManagerException{Session session = getSession();
	Criteria criteria = session.createCriteria(StaffData.class);
	PageList pageList = null;

	try{

		if((staffData.getName() != null && staffData.getName().length()>0 )){
			criteria.add(Restrictions.ilike("name","%"+staffData.getName()+"%"));
		}

		if((staffData.getUsername() != null && staffData.getUsername().length()>0 )){
			criteria.add(Restrictions.ilike("username","%"+staffData.getUsername()+"%"));
		}

		if(!(staffData.getCommonStatusId().equalsIgnoreCase("All")) ){

			criteria.add(Restrictions.ilike("commonStatusId",staffData.getCommonStatusId()));
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

	}catch(HibernateException hbe){
		throw new DataManagerException(hbe.getMessage(),hbe);
	}catch(Exception e){
		throw new DataManagerException(e.getMessage(),e);
	}
	}


	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @throws  DataManagerException
	 * @purpose This method is generated to UpdateStaffAccessGroup details.
	 */
	public void updateStaffAccessGroup(IStaffData staffData,String actionAlias,List<StaffGroupRelData> staffGroupRelList) throws DataManagerException{
		try {
			Session session = getSession();

			StaffData oldStaffData=new StaffData();
			
			oldStaffData.setAddress1(staffData.getAddress1());
			oldStaffData.setAddress2(staffData.getAddress2());
			oldStaffData.setAuditId(staffData.getAuditUId());
			oldStaffData.setAuditName(staffData.getAuditName());
			oldStaffData.setAuditUId(staffData.getAuditUId());
			oldStaffData.setBirthDate(staffData.getBirthDate());
			oldStaffData.setCity(staffData.getCity());
			oldStaffData.setCommonStatus(staffData.getCommonStatus());
			oldStaffData.setCommonStatusId(staffData.getCommonStatusId());
			oldStaffData.setCountry(staffData.getCountry());
			oldStaffData.setCreateDate(staffData.getCreateDate());
			oldStaffData.setCreatedByStaffId(staffData.getCreatedByStaffId());
			oldStaffData.setEmailAddress(staffData.getEmailAddress());
			oldStaffData.setLastChangePasswordDate(staffData.getLastChangePasswordDate());
			oldStaffData.setLastLoginTime(staffData.getLastLoginTime());
			oldStaffData.setLastModifiedByStaffId(staffData.getLastModifiedByStaffId());
			oldStaffData.setLastModifiedDate(staffData.getLastModifiedDate());
			oldStaffData.setMobile(staffData.getMobile());
			oldStaffData.setName(staffData.getName());
			oldStaffData.setPageNumber(staffData.getPageNumber());
			oldStaffData.setPassword(staffData.getPassword());
			oldStaffData.setPhone(staffData.getPhone());
			oldStaffData.setReason(staffData.getReason());
			oldStaffData.setStaffId(staffData.getStaffId());
			oldStaffData.setState(staffData.getState());
			oldStaffData.setStatus(staffData.getStatus());
			oldStaffData.setStatusChangeDate(staffData.getStatusChangeDate());
			oldStaffData.setSystemGenerated(staffData.getSystemGenerated());
			oldStaffData.setTotalPages(staffData.getTotalPages());
			oldStaffData.setTotalRecords(staffData.getTotalRecords());
			oldStaffData.setUsername(staffData.getUsername());
			oldStaffData.setZip(staffData.getZip());
			oldStaffData.setStaffGroupRel(new HashSet<StaffGroupRelData>(staffGroupRelList));
			
			JSONArray jsonArray=ObjectDiffer.diff(oldStaffData,(StaffData)staffData);
			
			for(Iterator staffGroupRelIterator = staffData.getStaffGroupRel().iterator();staffGroupRelIterator.hasNext();){
				IStaffGroupRelData iStaffGroupRelData = (IStaffGroupRelData)staffGroupRelIterator.next();
				iStaffGroupRelData.setStaffId(staffData.getStaffId());
				session.save(iStaffGroupRelData);
			}
			
			doAuditingJson(jsonArray.toString(),oldStaffData,actionAlias);
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @throws  DataManagerException
	 * @purpose This method is generated to create StaffData.
	 */
	public void create(IStaffData staffData) throws DataManagerException{
		try{
			Session session = getSession();
			String auditId = UUIDGenerator.generate();
			staffData.setAuditUId(auditId);
			
			session.save(staffData);

			for(Iterator staffGroupRelIterator = staffData.getStaffGroupRel().iterator();staffGroupRelIterator.hasNext();){
				IStaffGroupRelData iStaffGroupRelData = (IStaffGroupRelData)staffGroupRelIterator.next();
				iStaffGroupRelData.setStaffId(staffData.getStaffId());
				session.save(iStaffGroupRelData);
			}

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   lstStaffIds
	 * @param   commonStatusId
	 * @throws  DataManagerException
	 * @purpose This method is generated to update the Status of StaffData.
	 */
	public String updateStatus(String staffId, String commonStatus, Timestamp statusChangeDate) throws DataManagerException{	
		Session session = getSession();			
		StaffData staffData = null;

		try{
			Criteria criteria = session.createCriteria(StaffData.class);
			staffData = (StaffData)criteria.add(Restrictions.eq("staffId",staffId))
			.uniqueResult();

			staffData.setCommonStatusId(commonStatus);
			staffData.setStatusChangeDate(statusChangeDate);
			staffData.setLastModifiedDate(statusChangeDate);
			session.update(staffData);
			session.flush();
			return staffData.getName();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   lstStaffIds
	 * @throws  DataManagerException
	 * @purpose This method is generated to delete StaffData.
	 */
	public String delete(String staffId)  throws DataManagerException{
		StaffData staffData = null;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(StaffData.class);
			staffData = (StaffData)criteria.add(Restrictions.like("staffId",staffId))
			.uniqueResult();

			session.delete(staffData);
			
			return staffData.getName();
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	/*public void deleteStaffGroupRel(String staffId) throws DataManagerException{
			StaffGroupRelData staffGroupRelData = null;

			try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(StaffGroupRelData.class)
										   .add(Restrictions.eq("staffId",staffId));

				List lstStaffGroupRelList = criteria.list();

				for(int i=0;i<lstStaffGroupRelList.size();i++){
					staffGroupRelData = (StaffGroupRelData)lstStaffGroupRelList.get(i);
					session.delete(staffGroupRelData);
				}
			} catch (HibernateException hExp) {
				hExp.printStackTrace();
				throw new DataManagerException(hExp.getMessage(),hExp);
			} catch (Exception exp) {
				exp.printStackTrace();
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}*/

	/*public void deleteStaffAccessGroup(IStaffData staffData) throws DataManagerException{
			 IStaffGroupRelData staffGroupRelData = null;
			 try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(StaffGroupRelData.class)
										   .add(Restrictions.eq("staffId",staffData.getStaffId()));
				//staffData = (StaffData)criteria.uniqueResult();
				List lstStaffGroupRelList = criteria.list();
				//List lstStaffGroupRelList = Arrays.asList(staffData.getStaffGroupRel().toArray()) ;
				System.out.println("%%%%%%%%%%% delete side the size is %%%%%%%%%"+lstStaffGroupRelList.size());
				for(int i=0;i<lstStaffGroupRelList.size();i++){
					staffGroupRelData = (StaffGroupRelData)lstStaffGroupRelList.get(i);
					System.out.println("&&&&& value of the i is :"+i+" groupid"+staffGroupRelData.getGroupId());
					session.delete(staffGroupRelData);
				}
			} catch(HibernateException hExp){
				 hExp.printStackTrace();
				 throw new DataManagerException(hExp.getMessage(), hExp);
			 }catch(Exception exp){
				 exp.printStackTrace();
				 throw new DataManagerException(exp.getMessage(),exp);
			 }
		 }*/

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @param   staffDataManager
	 * @throws  DataManagerException
	 * @purpose This method is generated to DeleteStaffAccessGroup.
	 */
	public List<StaffGroupRelData> deleteStaffAccessGroup(String staffId) throws DataManagerException{
		IStaffGroupRelData staffGroupRelData = null;

		try {
			Session session  = getSession();
			Criteria criteria = session.createCriteria(StaffGroupRelData.class)
			.add(Restrictions.eq("staffId",staffId));

			List<StaffGroupRelData> lstStaffGroupRelList = criteria.list();

			for(int i=0;i<lstStaffGroupRelList.size();i++){
				staffGroupRelData = (StaffGroupRelData)lstStaffGroupRelList.get(i);
				session.delete(staffGroupRelData);
			}
			return lstStaffGroupRelList;
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	} 

	/*public void update(IStaffData istaffData,String staffId) throws DataManagerException{
				Session session = getSession();
//				Transaction tx = session.beginTransaction();
				StaffData staffData = null;

				try{
					Criteria criteria = session.createCriteria(StaffData.class);

					staffData = (StaffData)criteria.add(Restrictions.eq("staffId",staffId))
												   .uniqueResult();

					session.update(staffData);
					//session.flush();

				}catch(HibernateException hExp){
					hExp.printStackTrace();
					throw new DataManagerException(hExp.getMessage(),hExp);
				}catch(Exception exp){
					throw new DataManagerException(exp.getMessage(),exp);
				}
		}*/

	/**
	 * @author  dhavalraval
	 * @param   lstStaffIds
	 * @param   statusChangeDate
	 * @throws  DataManagerException
	 * @purpose This method is generated to update StaffBasicDetais.
	 */
	public void updateBasicDetail(IStaffData istaffData,Timestamp statusChangeDate,String actionAlias) throws DataManagerException{
		Session session = getSession();
		StaffData staffData = null;

		if(istaffData != null){
			try{
				Criteria criteria = session.createCriteria(StaffData.class);
				staffData = (StaffData)criteria.add(Restrictions.eq("staffId",istaffData.getStaffId()))
				.uniqueResult();
				
				StaffData newStaffData=new StaffData();
				newStaffData=(StaffData)istaffData;

				JSONArray jsonArray=ObjectDiffer.diff(staffData, newStaffData);
				
				staffData.setName(istaffData.getName());
				staffData.setBirthDate(istaffData.getBirthDate());
				staffData.setAddress1(istaffData.getAddress1());
				staffData.setAddress2(istaffData.getAddress2());
				staffData.setCity(istaffData.getCity());
				staffData.setState(istaffData.getState());
				staffData.setCountry(istaffData.getCountry());
				staffData.setZip(istaffData.getZip());
				staffData.setEmailAddress(istaffData.getEmailAddress());
				staffData.setPhone(istaffData.getPhone());
				staffData.setMobile(istaffData.getMobile());
				staffData.setLastModifiedDate(statusChangeDate);
				staffData.setAuditId(istaffData.getAuditId());
				staffData.setAuditName(istaffData.getAuditName());
				
				if (staffData.getAuditUId() == null) {
					String auditId = UUIDGenerator.generate();
					staffData.setAuditUId(auditId);
					staffData.setAuditId(auditId);
				}
				staffData.setAuditUId(istaffData.getAuditUId());
				staffData.setAuditName(istaffData.getAuditName());
				
				session.update(staffData);
				session.flush();
				
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(),hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException("UpdateBasicDetail Failed");
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @param   statusChangeDate
	 * @throws  DataManagerException
	 * @purpose This method is generated to ChangeUserName of StaffData.
	 */
	public void changeUserName(IStaffData istaffData,Timestamp statusChangeDate,String actionAlias) throws DataManagerException, DuplicateParameterFoundExcpetion{
		Session session = getSession();
		StaffData staffData = null;

		if(istaffData != null){
			try{
				Criteria criteria = session.createCriteria(StaffData.class);
				staffData = (StaffData)criteria.add(Restrictions.eq("staffId",istaffData.getStaffId()))
				.uniqueResult();
				
				StaffData newStaffData=new StaffData();
				newStaffData=(StaffData)istaffData;
				
				JSONArray jsonArray=ObjectDiffer.diff(staffData, newStaffData);
				
				if (staffData.getAuditUId() == null) {
					String auditId = UUIDGenerator.generate();
					staffData.setAuditUId(auditId);
					staffData.setAuditId(auditId);
				}
				
				if(staffData.getUsername()!=null && !staffData.getUsername().equals(istaffData.getUsername().trim())){
					List list = getStaffDatas(istaffData.getUsername());
					if(list!=null && list.size()>0){
						throw new DuplicateParameterFoundExcpetion("Duplicate User Name.");
					}

				}
				staffData.setUsername(istaffData.getUsername().trim());
				staffData.setLastModifiedDate(statusChangeDate);
				staffData.setAuditId(istaffData.getAuditId());
				staffData.setAuditName(istaffData.getAuditName());
				session.update(staffData);
				session.flush();
				
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			}catch(DuplicateParameterFoundExcpetion dpfExp){
				throw new DuplicateParameterFoundExcpetion("Duplicate User Name.");
			}catch(HibernateException hExp){
				throw new DataManagerException(hExp.getMessage(),hExp);
			}catch(Exception exp){
				throw new DataManagerException(exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException("ChangeUserName failed");
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffId
	 * @param   oldPassword
	 * @param   newPassword
	 * @throws  DataManagerException
	 * @purpose This method is generated to change Password of StaffData.
	 */
	public void changePassword(String staffId, String oldPassword,String newPassword,Timestamp statusChangeDate) throws DataManagerException{
		Session session = getSession();			
		StaffData staffData = null;

		try{
			if(Strings.isNullOrBlank(staffId) == false && oldPassword != null && newPassword != null){
				Criteria criteria = session.createCriteria(StaffData.class);
				staffData = (StaffData)criteria.add(Restrictions.eq("staffId",staffId)).uniqueResult();
				if(PasswordEncryption.getInstance().matches(staffData.getPassword(),oldPassword,Integer.parseInt(ConfigManager.get(BaseConstant.ENCRYPTION_MODE)))){
					staffData.setPassword(newPassword);
					staffData.setLastModifiedDate(statusChangeDate);
					session.update(staffData);
					session.flush();
					staffData = null;
					criteria = null;
				}else{
					throw new DataManagerException("Old Password(DB) and Old Passwords(GUI) Do not match");						
				}
			}else{
				throw new DataManagerException("Invalid parameters");						
			}
		}
		catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	public List<IStaffData> getStaffDatas(String userName) throws DataManagerException {
		EliteAssert.notNull(userName, "userName must be supplied");
		Session session = getSession();
		List<IStaffData> staffDataList = null;
		try{
			Criteria criteria = session.createCriteria(StaffData.class);
			return staffDataList = (List<IStaffData>)criteria.add(Restrictions.eq("username",userName)).list();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(),hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}

	}

	public Date getLastChangedPwdDate(String userName) throws DataManagerException{
		Session session = getSession();
		Date lastchangedPwdDate=null;
		Timestamp modifyDate=null;
		try{
			Criteria criteria = session.createCriteria(StaffData.class);
			List<IStaffData> staffDataList;
			staffDataList = (List<IStaffData>)criteria.add(Restrictions.eq("username",userName)).list();
			
			for(IStaffData istaffData :staffDataList){
				modifyDate=istaffData.getLastChangePasswordDate();
			}
			if(modifyDate == null){
				return null;
			}
			
			lastchangedPwdDate = new Date(modifyDate.getTime());
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
		return lastchangedPwdDate;
		
	}
	public void updateLoginInfo(IStaffData staffData) throws DataManagerException {
		Session session = getSession();			
		try{	
			Criteria criteria = session.createCriteria(StaffData.class);
			IStaffData existingStaffData = (StaffData)criteria.add(Restrictions.eq("staffId",staffData.getStaffId())).uniqueResult();
			existingStaffData.setLastLoginTime(staffData.getLastLoginTime());      
			session.update(existingStaffData);
			session.flush();
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	public void checkPermission(String userName, String actionAlias) throws ActionNotPermitedException, DataManagerException {

		Session session = getSession();			
		try{	
		     
			String strQuery = "select actionId from ActionData where alias ='"+actionAlias+"' and actionId in "+
								"(select actionId from GroupActionRelData where groupId in "+
								"(select groupId from StaffGroupRelData where staffId in ( select staffId from StaffData where userName = '"+userName+"')))";
			Query query  = session.createQuery(strQuery);
			List list =  query.list();
			if(list==null || list.isEmpty()){
				throw new ActionNotPermitedException("[ "+ actionAlias +" ] is not permitted to [ "+ userName +" ]");
			}
			
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
	
	public void doAuditingJson(String JsonArray, IStaffData staffData,String actionAlias) throws DataManagerException {
		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(ActionData.class);
			criteria.add(Restrictions.eq("alias", actionAlias));
			List lstActionId = criteria.list();
			IActionData actionData = (ActionData) lstActionId.get(0);
			String actionId = actionData.getActionId();
			String staffId = staffData.getStaffId();
			ISystemAuditData systemAuditData = new SystemAuditData();

			systemAuditData.setActionId(actionId);
			systemAuditData.setSystemUserId(staffId);
			systemAuditData.setSystemUserName(staffData.getUsername());
			systemAuditData.setRemarks("");
			systemAuditData.setTransactionId(null);
			systemAuditData.setClientIP(getClientIP());
			Date auditDate = new Date();
			systemAuditData.setAuditDate(auditDate);
			systemAuditData.setAuditId(staffData.getAuditId());
			systemAuditData.setAuditName(staffData.getAuditName());
			Logger.logDebug("MODULE", systemAuditData);

			session.save(systemAuditData);

			criteria = session.createCriteria(SystemAuditDetails.class);

			SystemAuditDetails systemAuditDetails = new SystemAuditDetails();
			systemAuditDetails.setSystemAuditId(systemAuditData.getSystemAuditId());
			systemAuditDetails.setHistory(JsonArray.getBytes());
			session.save(systemAuditDetails);
			session.flush();

			Logger.logDebug("MODULE", "Difference Save Succesfully");
			
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	private String getClientIP(){
		String clientAddress = "Unknown";
		String remoteAddress = (String)MDC.get("remoteaddress");
		
		if(remoteAddress!=null){
			clientAddress  = remoteAddress;
		}
		return clientAddress;
	}

	@Override
	public boolean isValidUser(String systemUserName, String password) throws DataManagerException {
		Session session = getSession();			
		StaffData staffData = null;

		try{
			Criteria criteria = session.createCriteria(StaffData.class);
			staffData = (StaffData)criteria.add(Restrictions.eq("username",systemUserName)).add(Restrictions.eq("password", password))
			.uniqueResult();

			if( staffData != null ){
				return true;
			}else{
				return false;
			}
		}catch(HibernateException hExp){
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}

	@Override
	public IStaffData getStaffData(String staffId) throws DataManagerException {
		try{
			Session session = getSession();			
			IStaffData staffData = null;
			
			Criteria criteria = session.createCriteria(StaffData.class);
			staffData = (StaffData)criteria.add(Restrictions.eq("staffId",staffId)).uniqueResult();

			return staffData;
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		}
	}
}

