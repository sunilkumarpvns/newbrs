package com.elitecore.elitesm.blmanager.radius.policies.radiuspolicy;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.RadiusPolicyDataManager;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.IRadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyData;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.data.RadiusPolicyTimePeriod;
import com.elitecore.elitesm.datamanager.radius.policies.radiuspolicy.exception.DuplicateRadiusPolicyNameException;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.RadiusPolicyForm;
import com.elitecore.elitesm.ws.rest.data.Status;

public class RadiusPolicyBLManager extends BaseBLManager {
	private static final String EMPTY = "";
	private static final String MODULE = "RadiusPolicy";
	
	public IRadiusPolicyData getRadiusPolicyDataById (String radiusPolicyId) throws DataManagerException {
		return getRadiusPolicyData(radiusPolicyId, BY_ID, false);
	}
	
	public IRadiusPolicyData getRadiusPolicyDataByName(String radiusPolicyName, boolean caseSensitivity) throws DataManagerException {
		return getRadiusPolicyData(radiusPolicyName.trim(), BY_NAME, caseSensitivity);
	}
	
	private IRadiusPolicyData getRadiusPolicyData(Object searchVal, boolean isByIdOrName, boolean caseSensitivity) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);
		
		if (radiusPolicyDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{
			IRadiusPolicyData radiusPolicyData = null;
			if (isByIdOrName) {
				radiusPolicyData = radiusPolicyDataManager.getRadiusPolicyDataById((String) searchVal);
			} else {
				if(caseSensitivity){
					radiusPolicyData = (RadiusPolicyData) verifyNameWithIgnoreCase(RadiusPolicyData.class, (String) searchVal, true);
				} else {
					radiusPolicyData = radiusPolicyDataManager.getRadiusPolicyDataByName((String) searchVal);
				}
			}
			return radiusPolicyData;

		} catch (DataManagerException dme) {
			dme.printStackTrace();
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}

	}

	/**
	 * 
	 * @param radiusPolicyData
	 * @param staffData 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws DataManagerException
	 */
	public PageList search(IRadiusPolicyData radiusPolicyData, IStaffData staffData, int pageNo, int pageSize) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);

		PageList lstRadiusPolicyList;

		if (radiusPolicyDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			lstRadiusPolicyList = radiusPolicyDataManager.search(radiusPolicyData, pageNo, pageSize);
			
			AuditUtility.doAuditing(session, staffData, ConfigConstant.SEARCH_RADIUS_POLICY_ACTION);
			commit(session);
		} catch(DataManagerException dme) {
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
		return lstRadiusPolicyList; 

	}
	
	public void create(IRadiusPolicyData radiusPolicyData, IStaffData staffData, boolean casesensitivity) throws DataManagerException {
		List<IRadiusPolicyData> radiusPolicyDataList = new ArrayList<IRadiusPolicyData>();
		radiusPolicyDataList.add(radiusPolicyData);
		create(radiusPolicyDataList, staffData, EMPTY,casesensitivity);
	}
	
	/**
	 * 
	 * @param radiusPolicyDataList
	 * @param staffData 
	 * @param casesensitivity 
	 * @return 
	 * @return
	 * @throws DataManagerException
	 */
	public Map<String, List<Status>> create(List<IRadiusPolicyData> radiusPolicyDataList, IStaffData staffData,String isPartialSuccess, boolean casesensitivity) throws DataManagerException {
		
		for (IRadiusPolicyData radiusPolicyData : radiusPolicyDataList) {
			createValidate(radiusPolicyData);
			
			if(casesensitivity){
				verifyNameWithIgnoreCase(RadiusPolicyData.class, radiusPolicyData.getName(), false);
			}
		}
		return insertRecords(RadiusPolicyDataManager.class, radiusPolicyDataList, staffData, ConfigConstant.CREATE_RADIUS_POLICY_ACTION, isPartialSuccess);
	}

	public void updateStatus(List<String> lstRadiusPolicyIds, IStaffData staffData, String commonStatusId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);

		Date currentDate = new Date();

		if(radiusPolicyDataManager == null ) {
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			updateStatusValidate(lstRadiusPolicyIds,commonStatusId);
			session.beginTransaction();

			if(Collectionz.isNullOrEmpty(lstRadiusPolicyIds) == false){
				int noOfRadiusPolicy = lstRadiusPolicyIds.size();
				for(int i=0;i<noOfRadiusPolicy;i++){
					if(lstRadiusPolicyIds.get(i) != null){
						String radiusPolicyId =lstRadiusPolicyIds.get(i);
						String radiusPolicyName = radiusPolicyDataManager.updateStatus(radiusPolicyId,commonStatusId, new Timestamp(currentDate.getTime()));
						staffData.setAuditName(radiusPolicyName);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.CHANGE_RADIUS_POLICY_STATUS);
					}
				}
				commit(session);
			}
		} catch(DataManagerException dme) {
			rollbackSession(session);
			dme.printStackTrace();
			throw dme;
		} catch(Exception e) {
			rollbackSession(session);
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * @return Returns Data Manager instance for dictionary data.
	 */
	public RadiusPolicyDataManager getRadiusPolicyDataManager(IDataManagerSession session) {
		RadiusPolicyDataManager radiusPolicyDataManager = (RadiusPolicyDataManager)DataManagerFactory.getInstance().getDataManager(RadiusPolicyDataManager.class,session);
		return radiusPolicyDataManager; 
	}

	public void updateBasicDetailsByName(RadiusPolicyData radiusPolicyData,IStaffData staffData,String queryOrPathParam, boolean caseSensitivity) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);

		if(radiusPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try {
			updateBasicDetailsValidate(radiusPolicyData);
			session.beginTransaction();
			
			if (queryOrPathParam == null) {
				radiusPolicyDataManager.updateById(radiusPolicyData, staffData, radiusPolicyData.getRadiusPolicyId());
			} else {
				if(caseSensitivity){
					RadiusPolicyData radiusPolicyIgnoreCaseData = (RadiusPolicyData) verifyNameWithIgnoreCase(RadiusPolicyData.class, (String) queryOrPathParam, true);
					radiusPolicyDataManager.updateById(radiusPolicyData, staffData, radiusPolicyIgnoreCaseData.getRadiusPolicyId());
				}else {
					radiusPolicyDataManager.updateByName(radiusPolicyData, staffData, queryOrPathParam.trim());
				}
			}
			commit(session);
			
		} catch(DataManagerException dme) {
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public void updateBasicDetailsById(RadiusPolicyData radiusPolicyData,IStaffData staffData) throws DataManagerException {
		updateBasicDetailsByName( radiusPolicyData, staffData, null, false);
	}

	public void updateRadiusPolicyParamDetails(List lstRadiusPolicyParamDetails, String radiusPolicyId, String parameterUsage,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(radiusPolicyDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			updateRadiusPolicyParamDetailsValidate(lstRadiusPolicyParamDetails,radiusPolicyId,parameterUsage);
			session.beginTransaction();

			radiusPolicyDataManager.updateRadiusPolicyParamByItems(lstRadiusPolicyParamDetails, radiusPolicyId, parameterUsage);
			String transactionId = radiusPolicyId;
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);

			commit(session);
			
		} catch (DataManagerException dme){
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		} catch(Exception exp){
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public void deleteById(List<String> lstRadiusPolicyIds, IStaffData staffData) throws DataManagerException {
		delete(lstRadiusPolicyIds, staffData, BY_ID, false);
	}
	
	public void deleteByName(List<String> lstRadiusPolicyIds, IStaffData staffData, boolean caseSensitivity) throws DataManagerException {
		delete(lstRadiusPolicyIds, staffData, BY_NAME, caseSensitivity);
	}

	private void delete(List<String> lstRadiusPolicyIdsOrName, IStaffData staffData, Boolean byIdOrName, boolean caseSensitivity) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);

		if(radiusPolicyDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			session.beginTransaction();
			if(Collectionz.isNullOrEmpty(lstRadiusPolicyIdsOrName) == false) {
				int noOfRadiusPolicy = lstRadiusPolicyIdsOrName.size();
				for(int i=0;i<noOfRadiusPolicy;i++){
					if(Strings.isNullOrEmpty(lstRadiusPolicyIdsOrName.get(i)) == false){
						String radiusPolicyIdOrName =  lstRadiusPolicyIdsOrName.get(i).trim();
						String name = null;
						if(byIdOrName) {
							name  = radiusPolicyDataManager.deleteById(radiusPolicyIdOrName);
						} else {
							if(caseSensitivity){
								RadiusPolicyData radiusPolicyIgnoreCaseData = (RadiusPolicyData) verifyNameWithIgnoreCase(RadiusPolicyData.class, (String) radiusPolicyIdOrName, true);
								name  = radiusPolicyDataManager.deleteById(radiusPolicyIgnoreCaseData.getRadiusPolicyId());
							} else {
								name = radiusPolicyDataManager.deleteByName(radiusPolicyIdOrName);
							}
						}
						staffData.setAuditName(name);
						AuditUtility.doAuditing(session,staffData, ConfigConstant.DELETE_RADIUS_POLICY_ACTION);
					}
				}
				commit(session);
			} 
			
		} 	catch(DataManagerException dme) {
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		}	catch (Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	public void createValidate(IRadiusPolicyData radiusPolicyData)throws DataValidationException{

		// Name
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getName())){
			throw (new DataValidationException("Invalid RadiusPolicy Name",(MODULE+"."+"name").toLowerCase()));
		}
		// SystemGenerated
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getSystemGenerated())){
			throw (new DataValidationException("Invalid RadiusPolicy SystemGenerated",(MODULE+"."+"SystemGenerated").toLowerCase()));
		}
		// CreateDate
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getCreateDate())){
			throw (new DataValidationException("Invalid RadiusPolicy CreateDate",(MODULE+"."+"CreateDate").toLowerCase()));
		}
		// CommonStatusId
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getCommonStatusId())){
			throw (new DataValidationException("Invalid RadiusPolicy CommonStatusId",(MODULE+"."+"CommonStatusId").toLowerCase()));
		}
		// StatusChangeDate
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getStatusChangeDate())){
			throw (new DataValidationException("Invalid RadiusPolicy StatusChangeDate",(MODULE+"."+"StatusChangeDate").toLowerCase()));
		}
		// LastUpdated
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getLastUpdated())){
			throw (new DataValidationException("Invalid RadiusPolicy LastUpdated",(MODULE+"."+"LastUpdated").toLowerCase()));
		}
		// LastModifiedByStaffId
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getLastModifiedByStaffId())){
			throw (new DataValidationException("Invalid RadiusPolicy LastModifiedByStaffId",(MODULE+"."+"LastModifiedByStaffId").toLowerCase()));
		}
		// Editable
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getEditable())){
			throw (new DataValidationException("Invalid RadiusPolicy Editable",(MODULE+"."+"Editable").toLowerCase()));
		}

		// CreatedByStaffId
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getCreatedByStaffId())){
			throw (new DataValidationException("Invalid RadiusPolicy CreatedByStaffId",(MODULE+"."+"CreatedByStaffId").toLowerCase()));
		}
	}
	public void updateStatusValidate(List lstRadiusPolicyIds,String commonStatusId)throws DataValidationException{

		// commonStatusId
		if(EliteGenericValidator.isBlankOrNull(commonStatusId)){
			throw (new DataValidationException("Invalid RadiusPolicy commonStatusId",(MODULE+"."+"commonStatusId").toLowerCase()));
		}


	}
	public void updateBasicDetailsValidate(IRadiusPolicyData radiusPolicyData)throws DataValidationException{

		// Name
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getName())){
			throw (new DataValidationException("Invalid RadiusPolicy Name",(MODULE+"."+"Name").toLowerCase()));
		}
		// LastUpdated
		if(EliteGenericValidator.isBlankOrNull(radiusPolicyData.getLastUpdated())){
			throw (new DataValidationException("Invalid RadiusPolicy LastUpdated",(MODULE+"."+"LastUpdated").toLowerCase()));
		}


	}
	public void updateRadiusPolicyParamDetailsValidate(List lstRadiusPolicyParamDetails, String radiusPolicyId, String parameterUsage)throws DataValidationException{

		// radiusPolicyId
		if(Strings.isNullOrBlank(radiusPolicyId)== true){
			throw (new DataValidationException("Invalid RadiusPolicy radiusPolicyId",(MODULE+"."+"radiusPolicyId").toLowerCase()));
		}

	}
	
	public void verifyRadiusPolicyName(String radiusPolicyId, String policyName)throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();

		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);
		boolean isPolicyName;

		if (radiusPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try {
			isPolicyName = radiusPolicyDataManager.verifyRadiusPolicyName(radiusPolicyId, policyName);
			if(isPolicyName){
				throw new DuplicateRadiusPolicyNameException("Duplicate Radius Policy Name Exception");
			}
		} catch ( DataManagerException dme) {
			dme.printStackTrace();
			throw dme;
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}
	public void updateCheckItem(String radiusPolicyId,String checkItem) throws DataManagerException
	{

		IDataManagerSession session= DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);
		if (radiusPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			radiusPolicyDataManager.updateCheckItem(radiusPolicyId, checkItem);
			commit(session);
		} catch ( DataManagerException dme) {
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	public void updateRejectItem(String radiusPolicyId,String rejectItem) throws DataManagerException
	{
		IDataManagerSession session= DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);
		if (radiusPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			radiusPolicyDataManager.updateRejectItem(radiusPolicyId, rejectItem);
			commit(session);
		} catch ( DataManagerException dme){
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	public void updateReplyItem(String radiusPolicyId,String replyItem)throws DataManagerException
	{

		IDataManagerSession session= DataManagerSessionFactory.getInstance().getDataManagerSession();
		RadiusPolicyDataManager radiusPolicyDataManager = getRadiusPolicyDataManager(session);
		if (radiusPolicyDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			radiusPolicyDataManager.updateReplyItem(radiusPolicyId, replyItem);
			commit(session);
		} catch ( DataManagerException dme) { 
			dme.printStackTrace();
			rollbackSession(session);
			throw dme;
		} catch(Exception exp) {
			exp.printStackTrace();
			rollbackSession(session);
			throw new DataManagerException(exp.getMessage(), exp);
		} finally {
			closeSession(session);
		}
	}
	
	public List<RadiusPolicyTimePeriod> getRadiusPolicyTimePeriodList(RadiusPolicyForm radiusPolicyForm) {
		String[] monthOfYear = radiusPolicyForm.getMonthOfYear();
		String[] dayOfMonth = radiusPolicyForm.getDayOfMonth();
		String[] dayOfWeek = radiusPolicyForm.getDayOfWeek();
		String[] timePeriod = radiusPolicyForm.getTimePeriod();
		
		if(monthOfYear == null || dayOfMonth == null || dayOfWeek == null || timePeriod == null) {
			return null;
		} else {
			int length = monthOfYear.length;
			if(length != dayOfMonth.length || length != dayOfWeek.length || length != timePeriod.length)
				return null;
			List<RadiusPolicyTimePeriod> radiusPolicyTimePeriodList = new ArrayList<RadiusPolicyTimePeriod>(length);
			for(int i =0 ; i<length ; i++) {
				RadiusPolicyTimePeriod radiusPolicyTimePeriod = new RadiusPolicyTimePeriod();
				if(monthOfYear[i].trim().length() > 0){
					radiusPolicyTimePeriod.setMonthOfYear(monthOfYear[i]);
				}
				if(dayOfMonth[i].trim().length() > 0){
					radiusPolicyTimePeriod.setDayOfMonth(dayOfMonth[i]);
				}
				if(dayOfWeek[i].trim().length() > 0){
					radiusPolicyTimePeriod.setDayOfWeek(dayOfWeek[i]);
				}
				if(timePeriod[i].trim().length() > 0){
					radiusPolicyTimePeriod.setTimePeriod(timePeriod[i]);
				}
				radiusPolicyTimePeriodList.add(radiusPolicyTimePeriod);
			}
			return radiusPolicyTimePeriodList;
		}
			
	}
	
}
