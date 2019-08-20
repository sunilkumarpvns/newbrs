package com.elitecore.elitesm.blmanager.core.system.staff;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.json.JSONArray;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.BaseBLManager;
import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.blmanager.core.system.profilemanagement.ProfileBLManager;
import com.elitecore.elitesm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupActionRelData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleSubBISModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleActionRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.SubBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.staff.StaffDataManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffGroupRelData;
import com.elitecore.elitesm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.AuditUtility;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.exprlib.parser.exception.InvalidSymbolException;
import com.elitecore.exprlib.scanner.Scanner;
import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.scanner.impl.ScannerImpl;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class StaffBLManager extends BaseBLManager{
	private static final String MODULE = "STAFF";

	private static Scanner scanner ;
	
	static {
		scanner = new ScannerImpl(){
			protected boolean isOperator(char ch) {
				return( ch==',' );
			}
			
			protected boolean isWhitespace(char ch) {
				return false;
			}
		};
	}
	/**
	 * @author  dhavalraval
	 * @param 	radiusPolicyData
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of StaffData.
	 */
	public List getList(IStaffData staffData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		try {
			List lstStaffList;
			if (staffDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			lstStaffList = staffDataManager.getList(staffData);
			return lstStaffList;
		}catch(DataManagerException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive staff list, reason:  "+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffId
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returs the list for specific StaffId.
	 */
	public List getStaffGroupRelList(String staffId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		try {
			List lstStaffGroupActionRelList;

			if(staffDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

			lstStaffGroupActionRelList = staffDataManager.getStaffGroupRelList(staffId);
			return lstStaffGroupActionRelList;

		}catch(DataManagerException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive staff list, reason:  "+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of whole the StaffData. 
	 */
	public List<StaffData> getList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		try {
			List<StaffData> lstStaffList;
			if(staffDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			lstStaffList = staffDataManager.getList();
			return lstStaffList;
		}catch(DataManagerException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive staff list, reason:  "+ e.getMessage(),e);
		} finally {
			closeSession(session);
		}
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
	public PageList search(IStaffData searchStaffData ,int pageNo, Integer pageSize) throws DataManagerException{

		IDataManagerSession session =DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{				     
			StaffDataManager staffDataManager = getStaffDataManager(session);

			if(staffDataManager==null){
				throw new DataManagerException("Data Manager Not Found: Staff.");
			}
			session.beginTransaction();
			PageList pageList = staffDataManager.search(searchStaffData, pageNo, pageSize);
			session.commit();
			
			return pageList;

		}catch(DataManagerException e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @throws  DataManagerException
	 * @purpose This method is generated to create StaffData.
	 */
	public void create(IStaffData staffData) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		if (staffDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			createValidate(staffData);
			session.beginTransaction();


			List<IStaffData> staffDataList = staffDataManager.getStaffDatas(staffData.getUsername());
			if(staffDataList!=null && staffDataList.size()>0){
				throw new DuplicateParameterFoundExcpetion("Duplicate User Name.");
			}
			
			String auditId = UUIDGenerator.generate();
			
			staffData.setAuditUId(auditId);

			staffDataManager.create(staffData);

			session.commit();
		}catch(DuplicateParameterFoundExcpetion exp){
			exp.printStackTrace();
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate User Name. : "+exp.getMessage(),exp);
		}catch(DataManagerException exp){
			exp.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @return  IStaffData object
	 * @throws  DataManagerException
	 * @purpose This method is generated to get StaffData object.
	 */
	public IStaffData getStaff(IStaffData staffData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		try {
			List lstStaff = staffDataManager.getList(staffData);

			if(lstStaff != null && lstStaff.size() >= 1){
				staffData =(IStaffData) lstStaff.get(0);
			}
			return staffData;
		}catch(DataManagerException exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive staff list, reason:  "+ exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}
	
	public IStaffData getStaffDetails(IStaffData staffData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		if (staffDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{

			session.beginTransaction();
			List lstStaff = staffDataManager.getList(staffData);

			if(lstStaff != null && lstStaff.size() >= 1){
				staffData =(IStaffData) lstStaff.get(0);
			}

			session.commit();

		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			closeSession(session);
		}
		return staffData ;
	}

	/**
	 * @author  dhavalraval
	 * @param   lstStaffIds
	 * @param   commonStatusId
	 * @throws  DataManagerException
	 * @purpose This method is generated to update the Status of StaffData.
	 */
	public void updateStatus(List lstStaffIds,String commonStatusId,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		Date currentDate = new Date();

		if(staffDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			updateStatusValidate(lstStaffIds,commonStatusId);
			session.beginTransaction();
			if(lstStaffIds != null){
				for(int i=0;i<lstStaffIds.size();i++){
					if(lstStaffIds.get(i) != null){

						String transactionId = lstStaffIds.get(i).toString();
						String staffid = lstStaffIds.get(i).toString();
						String staffName = staffDataManager.updateStatus(staffid,commonStatusId,new Timestamp(currentDate.getTime()));
						staffData.setAuditName(staffName);
						AuditUtility.doAuditing(session, staffData, actionAlias);
					}
				}
				session.commit();
			}else{
				throw new DataManagerException("Data Manager implementation not found for ");    		
			}
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Data Manager implementation not found for "+exp.getMessage(),exp);
		} finally {
			closeSession(session);
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
	public void changePassword(String staffId, String oldPassword, String newPassword,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		
		IStaffData staffData = null;
		Date currentDate = new Date();

		String oldHistoricalPassword;
		String newHistoricalPassword;
		if(staffDataManager == null )
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		if(Strings.isNullOrBlank(staffId) == true)
			throw new DataManagerException("StaffId is null "+getClass().getName());

		if(oldPassword == null)
			throw new DataManagerException("OldPassword is null "+getClass().getName());
		if(oldPassword.equals(newPassword)){
			throw new DataManagerException("Old Password and New Password cannot be same "+getClass().getName());
		}
		try{

			session.beginTransaction();
			changePasswordValidate(staffId, oldPassword, newPassword);
			staffData = new StaffData();
			staffData.setStaffId(staffId);
			staffData = (IStaffData) staffDataManager.getList(staffData).get(0);
			
			if (staffData.getAuditUId() == null) {
				String auditId = UUIDGenerator.generate();
				staffData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			if(PasswordEncryption.getInstance().matches(staffData.getPassword(),oldPassword,Integer.parseInt(ConfigManager.get(BaseConstant.ENCRYPTION_MODE)))){
				String tempNewPassword=null;
				try {
					tempNewPassword = PasswordEncryption.getInstance().decrypt(newPassword, PasswordEncryption.ELITE_PASSWORD_CRYPT);
				} catch (DecryptionNotSupportedException e) {
					throw new DataManagerException("Error while decrypting recents passwords");
				} catch (DecryptionFailedException e) {
					throw new DataManagerException("Error while decrypting recents passwords");
				}
				
				oldHistoricalPassword = staffData.getHistoricalPassword();
				newHistoricalPassword = manageHistoricalPasswords(tempNewPassword, oldHistoricalPassword);
				
				staffData.setHistoricalPassword(newHistoricalPassword);
				staffData.setPassword(newPassword);
				staffData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
				staffData.setLastChangePasswordDate(new Timestamp(currentDate.getTime()));
				staffData.setAuditName(staffData.getName());
				staffData.setAuditId(staffData.getAuditUId());
			}else{
				throw new DataManagerException("Old Password(DB) and Old Passwords(GUI) Do not match");                                         
			}
			
			StaffData oldStaffData= new StaffData();
			StaffData newStaffData=new StaffData();
			oldStaffData.setPassword(oldPassword);
			newStaffData.setPassword(newPassword);
			oldStaffData.setHistoricalPassword(oldHistoricalPassword);
			newStaffData.setHistoricalPassword(newHistoricalPassword);
			JSONArray jsonArray=ObjectDiffer.diff(oldStaffData, newStaffData);
			
			staffDataManager.doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			session.commit();
		}
		catch(DataValidationException e)
		{
			session.rollback();
			throw e;
		}
		catch(NoSuchEncryptionException e)
		{
			session.rollback();
			throw new DataManagerException("Data Manager implementation not found for "+e.getMessage(),e);
		} catch(EncryptionFailedException e) {
			session.rollback();
			throw new DataManagerException("Data Manager implementation not found for "+e.getMessage(),e);
		} catch(DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Data Manager implementation not found for "+exp.getMessage(),exp);
		}
		finally
		{
			if(session != null)
				session.close();
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   userName
	 * @param   password
	 * @return  String (userId)
	 * @throws  DataManagerException
	 * @purpose This method is generated to validate the UserName and Password.
	 */
	public String validateLogin(String userName, String password) throws DataManagerException {

		String userId = null;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			StaffDataManager staffDataManager = getStaffDataManager(session);
			session.beginTransaction();
			if (staffDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			IStaffData staffData = new StaffData();
			staffData.setUsername(userName);
			List lstStaffData = staffDataManager.getList(staffData);

			for(int i=0;i< lstStaffData.size();i++){
				staffData = (IStaffData)lstStaffData.get(i);
				//             if(PasswordEncryption.getInstance().matches(staffData.getPassword(),password,Integer.parseInt(ConfigManager.get(BaseConstant.ENCRYPTION_MODE)))
				//            && staffData.getCommonStatusId().equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID)){
				if(staffData.getPassword().equals(password)&& staffData.getCommonStatusId().equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID)){
					userId = String.valueOf(staffData.getStaffId());
					staffData.setLastLoginTime(getCurrentTimeStemp());
					staffDataManager.updateLoginInfo(staffData);
				}
			}
			session.commit();
		} catch(DataManagerException e)
		{
			session.rollback();
			throw e;
		}
		catch(Exception e)
		{
			session.rollback();
			throw new DataManagerException(e.getMessage(),e);
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return userId;
	}
	
	public boolean validateUserName(String userName) throws DataManagerException {

		boolean userId = false;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			StaffDataManager staffDataManager = getStaffDataManager(session);
			session.beginTransaction();
			if (staffDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			IStaffData staffData = new StaffData();
			staffData.setUsername(userName);
			List lstStaffData = staffDataManager.getList(staffData);

			for(int i=0;i< lstStaffData.size();i++){
				staffData = (IStaffData)lstStaffData.get(i);
				if(staffData.getUsername().equals(userName)){
					userId=true;
				}
			}
		} catch(DataManagerException e)
		{
			session.rollback();
			throw e;
		}
		catch(Exception e)
		{
			session.rollback();
			throw new DataManagerException(e.getMessage(),e);
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return userId;
	}

	/**
	 * @author  dhavalraval
	 * @param   lstStaffIds
	 * @throws  DataManagerException
	 * @purpose This method is generated to delete StaffData.
	 */
	public void delete(List lstStaffIds,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(staffDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			session.beginTransaction();
			if(lstStaffIds != null){
				for(int i=0;i<lstStaffIds.size();i++){
					if(lstStaffIds.get(i) != null){
						//						staffDataManager.deleteStaffGroupRel(lstStaffIds.get(i).toString());
						String transactionId = lstStaffIds.get(i).toString();
						String staffId = lstStaffIds.get(i).toString();
						String staffName = staffDataManager.delete(staffId);
						staffData.setAuditName(staffName);
						AuditUtility.doAuditing(session, staffData, actionAlias);
					}
				}
				session.commit();
			}else{
				session.rollback();
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			}
		}catch(DataManagerException exp){
			exp.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed : "+e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}

	/*public void update(IStaffData staffData,String staffId) throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		if(staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			staffDataManager.update(staffData,staffId);
			session.commit();
			session.close();
		}catch(DataManagerException exp){
			session.rollback();
			session.close();
			throw new DataManagerException("Update operation failed : "+exp.getMessage());
		}
	}
	 */	

	/**
	 * @author  dhavalraval
	 * @param   lstStaffIds
	 * @throws  DataManagerException
	 * @purpose This method is generated to update StaffBasicDetais.
	 */
	public void updateBasicDetail(IStaffData staffData,String actionAlias) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session); 

		Timestamp currentDate = new Timestamp((new Date()).getTime());

		if(staffDataManager == null ){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			updateBasicDetailValidate(staffData);
			staffData.setLastModifiedDate(currentDate);
			session.beginTransaction();

			staffDataManager.updateBasicDetail(staffData,currentDate,actionAlias);

			session.commit();
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @throws  DataManagerException
	 * @purpose This method is generated to UpdateStaffAccessGroup details.
	 */
	public void updateStaffAccessGroup(IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		if(staffDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for"+getClass().getName());
		}

		try {
			session.beginTransaction();
			List<StaffGroupRelData> staffGroupRelList= null;
			staffGroupRelList=deleteStaffAccessGroup(staffData,staffDataManager);
			updateStaffAccessGroup(staffData,staffDataManager,actionAlias,staffGroupRelList);

			session.commit();
		} catch (DataManagerException exp) {
			exp.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @param   staffDataManager
	 * @throws  DataManagerException
	 * @purpose This method is generated to DeleteStaffAccessGroup.
	 */
	public List<StaffGroupRelData> deleteStaffAccessGroup(IStaffData staffData,StaffDataManager staffDataManager) throws DataManagerException{
		List<StaffGroupRelData> staffGroupRel=null;
		staffGroupRel=staffDataManager.deleteStaffAccessGroup(staffData.getStaffId());
		return staffGroupRel;
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @param   staffDataManager
	 * @throws  DataManagerException
	 * @purpose This method is generated to UpdateStaffAccessGroup.
	 */
	public void updateStaffAccessGroup(IStaffData staffData,StaffDataManager staffDataManager,String actionAlias,List<StaffGroupRelData> staffGroupRelList) throws DataManagerException{
		staffDataManager.updateStaffAccessGroup(staffData,actionAlias,staffGroupRelList);
	}




	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @throws  DataManagerException
	 * @purpose This method is generated to ChangeUserName of StaffData.
	 */
	public void changeUserName(IStaffData staffData,String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		Date currentDate = new Date();

		if(staffDataManager == null){
			throw new DataManagerException("Data Manager implemention not found for "+getClass().getName());
		}

		try{
			changeUserNameValidate(staffData);
			session.beginTransaction();

			staffDataManager.changeUserName(staffData,new Timestamp(currentDate.getTime()),actionAlias);

			session.commit();
		}catch(DuplicateParameterFoundExcpetion exp){
			exp.printStackTrace();
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate User Name. : "+exp.getMessage(),exp);
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action Failed :"+exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}


	public HashMap getUserControl(String staffId) throws DataManagerException{
		ProfileBLManager profileBLManager = new ProfileBLManager();
		HashMap userControlMap = new HashMap();

		try{
			IStaffData staffData = getStaffData(staffId);
			userControlMap.put("staffId",staffId);
			userControlMap.put("name",staffData.getName());
			List lstBISModelList = profileBLManager.getBusinessModelList();
			HashMap modelMap = new HashMap();
			int lstBISModelSize = lstBISModelList.size();
			for(int i=0;i<lstBISModelSize;i++){

				IBISModelData bisModelData=(BISModelData)lstBISModelList.get(i);

				Set businessModelModuleRelList = bisModelData.getBusinessModelModuleRel();
				HashMap modelObjectMap = new HashMap();
				modelObjectMap.put("businessModelId",bisModelData.getBusinessModelId());
				modelObjectMap.put("businessModelName",bisModelData.getName());
				modelObjectMap.put("businessModelStatus","D");
				modelObjectMap.put("businessModelAlias",bisModelData.getAlias());

				HashMap moduleMap = new HashMap();
				Iterator itModelModuleRel=businessModelModuleRelList.iterator();
				while(itModelModuleRel.hasNext()){
					HashMap moduleObjectMap = new HashMap();
					
					IBISModelModuleRelData bisModelModuleRelData =(IBISModelModuleRelData) itModelModuleRel.next();
					IBISModuleData businessModuleData = profileBLManager.getBISModule(bisModelModuleRelData.getBusinessModuleId());
					moduleObjectMap.put("businessModuleId",businessModuleData.getBusinessModuleId());
					moduleObjectMap.put("businessModuleName",businessModuleData.getName());
					moduleObjectMap.put("businessModuleStatus","D");
					moduleObjectMap.put("businessModuleAlias",businessModuleData.getAlias());
					Set setModuleSubModuleRel = businessModuleData.getBisModuleSubBisModuleRel();
					HashMap subModuleMap = new HashMap();
					
					Iterator itModuleSubModuleRel = setModuleSubModuleRel.iterator();
					while(itModuleSubModuleRel.hasNext()){
						HashMap subModuleObjectMap = new HashMap();
						
						IActionData actionData = new ActionData();
						IBISModuleSubBISModuleRelData bISModuleSubBISModuleRelData = (IBISModuleSubBISModuleRelData)itModuleSubModuleRel.next();
						ISubBISModuleData subBusinessModuleData = profileBLManager.getSubBISModule(bISModuleSubBISModuleRelData.getSubBusinessModuleId());
						subModuleObjectMap.put("subBusinessModuleId",subBusinessModuleData.getSubBusinessModuleId());
						subModuleObjectMap.put("subBusinessModuleName",subBusinessModuleData.getName());
						subModuleObjectMap.put("subBusinessModuleStatus","D");
						subModuleObjectMap.put("subBusinessModuleAlias",subBusinessModuleData.getAlias());
						List subBusinessModuleActionRelList = profileBLManager.getSubBISModuleActionRelList(subModuleObjectMap.get("subBusinessModuleId").toString());
						
						HashMap actionMap = new HashMap();
						int subBusinessModuleActionRelListSize= subBusinessModuleActionRelList.size();
						for(int l=0;l<subBusinessModuleActionRelListSize;l++){
							HashMap actionObjectMap = new HashMap();
							actionData = profileBLManager.getActionData(((ISubBISModuleActionRelData)subBusinessModuleActionRelList.get(l)).getActionId());
							actionObjectMap.put("actionId",actionData.getActionId());
							actionObjectMap.put("actionName",actionData.getName());
							actionObjectMap.put("actionStatus","D");
							actionObjectMap.put("actionAlias",actionData.getAlias());

							actionMap.put(((ISubBISModuleActionRelData)subBusinessModuleActionRelList.get(l)).getActionData().getAlias(),actionObjectMap);
						}
						subModuleObjectMap.put("actionMap",actionMap);
						subModuleMap.put(subBusinessModuleData.getAlias(),subModuleObjectMap);
					}
					moduleObjectMap.put("subModuleMap",subModuleMap);
					moduleMap.put(businessModuleData.getAlias(),moduleObjectMap);
				}
				modelObjectMap.put("moduleMap",moduleMap);
				modelMap.put(bisModelData.getAlias(),modelObjectMap);
			}
			userControlMap.put("modelMap",modelMap);
		}catch(Exception exp){
			throw new DataManagerException("DataManager Exception: "+exp.getMessage(),exp);
		}
		return userControlMap;
	} 

	public HashMap getComparedMap(HashMap profileMap,HashMap userControlActionMap) throws DataManagerException{
		//		HashMap hashMap = userControlActionMap;
		ProfileBLManager profileBLManager = new ProfileBLManager();
		try {
			/*			/*HashMap modelMap = (HashMap)profileMap.get("modelMap");
			HashMap userControlActionModelMap = (HashMap)userControlActionMap.get("modelMap");*/
			HashMap profileModelMap = (HashMap)profileMap.get("modelMap");
			HashMap modelMap = (HashMap)userControlActionMap.get("modelMap");
			for(Iterator w = profileModelMap.keySet().iterator();w.hasNext();){
				String modelAlias = (String)w.next();
				IBISModelData bisModelData = new BISModelData();
				bisModelData = profileBLManager.getBISModelAlias(modelAlias);
				/*				HashMap modelObjectMap = (HashMap)profileModelMap.get(modelAlias);
				HashMap userControlObjectMap = (HashMap)modelMap.get(modelAlias);*/
				HashMap profileModelObjectMap = (HashMap)profileModelMap.get(modelAlias);
				HashMap modelObjectMap = (HashMap)modelMap.get(modelAlias);

				if((profileModelObjectMap.get("businessModelStatus").equals("E")) && (modelObjectMap.get("businessModelStatus").equals("E"))){

					bisModelData.setStatus("E");
					modelObjectMap.put("businessModelStatus",bisModelData.getStatus());
					//					modelObjectMap.put("businessModelStatus","E");
				}else{
					bisModelData.setStatus("D");
					modelObjectMap.put("businessModelStatus",bisModelData.getStatus());
					//					modelObjectMap.put("businessmodelStatus",bisModelData.getStatus());
				}
				/*				HashMap moduleMap = (HashMap)modelObjectMap.get("moduleMap");
				HashMap userControlActionModuleMap = (HashMap)userControlObjectMap.get("moduleMap");*/
				HashMap profileModuleMap = (HashMap)profileModelObjectMap.get("moduleMap");
				HashMap moduleMap = (HashMap)modelObjectMap.get("moduleMap");
				for(Iterator w1 = profileModuleMap.keySet().iterator();w1.hasNext();){
					String moduleAlias = (String)w1.next();
					IBISModuleData bisModuleData = new BISModuleData();
					bisModuleData = profileBLManager.getBISModuleAlias(moduleAlias);
					/*					HashMap moduleObjectMap = (HashMap)profileModuleMap.get(moduleAlias);
					HashMap userControlModuleObjectMap = (HashMap)moduleMap.get(moduleAlias);*/
					HashMap profileModuleObjectMap = (HashMap)profileModuleMap.get(moduleAlias);
					HashMap moduleObjectMap = (HashMap)moduleMap.get(moduleAlias);

					if((moduleObjectMap.get("businessModuleStatus").equals("E")) && (profileModuleObjectMap.get("businessModuleStatus").equals("E"))){

						bisModuleData.setStatus("E");
						moduleObjectMap.put("businessModuleStatus",bisModuleData.getStatus());
					}else{

						bisModuleData.setStatus("D");
						moduleObjectMap.put("businessModuleStatus",bisModuleData.getStatus());
					}
					/*					HashMap subModuleMap = (HashMap)moduleObjectMap.get("subModuleMap");
					HashMap userControlActionSubModuleMap = (HashMap)profileModuleObjectMap.get("subModuleMap");*/
					HashMap profileSubModuleMap = (HashMap)profileModuleObjectMap.get("subModuleMap");
					HashMap subModuleMap = (HashMap)moduleObjectMap.get("subModuleMap");
					for(Iterator w2 = profileSubModuleMap.keySet().iterator();w2.hasNext();){
						String subModuleAlias = (String)w2.next();
						ISubBISModuleData subBISModuleData = new SubBISModuleData();
						subBISModuleData = profileBLManager.getSubBISModuleAlias(subModuleAlias);
						/*HashMap subModuleObjectMap = (HashMap)profileSubModuleMap.get(subModuleAlias);
						HashMap userControlSubModuleObjectMap = (HashMap)userControlActionSubModuleMap.get(subModuleAlias);*/
						HashMap profileSubModuleObjectMap = (HashMap)profileSubModuleMap.get(subModuleAlias);
						HashMap subModuleObjectMap = (HashMap)subModuleMap.get(subModuleAlias);
						if((subModuleObjectMap.get("subBusinessModuleStatus").equals("E")) && (profileSubModuleObjectMap.get("subBusinessModuleStatus").equals("E"))){
							subBISModuleData.setStatus("E");
							subModuleObjectMap.put("subBusinessModuleStatus",subBISModuleData.getStatus());
						}else{
							subBISModuleData.setStatus("D");
							subModuleObjectMap.put("subBusinessModuleStatus",subBISModuleData.getStatus());
						}
						/*						HashMap actionMap = (HashMap)subModuleObjectMap.get("actionMap");
						HashMap usercontrolActionMap = (HashMap)userControlSubModuleObjectMap.get("actionMap");*/
						HashMap profileActionMap = (HashMap)profileSubModuleObjectMap.get("actionMap");
						HashMap actionMap = (HashMap)subModuleObjectMap.get("actionMap");
						for(Iterator w3 = actionMap.keySet().iterator();w3.hasNext();){
							String actionAlias = (String)w3.next();
							IActionData actionData = new ActionData();
							actionData = profileBLManager.getActionAlias(actionAlias);
							/*							HashMap actionObjectMap = (HashMap)actionMap.get(actionAlias);
							HashMap userControlActionObjectMap = (HashMap)usercontrolActionMap.get(actionAlias);*/
							HashMap profileActionObjectMap = (HashMap)profileActionMap.get(actionAlias);
							HashMap actionObjectMap = (HashMap)actionMap.get(actionAlias);
							if((actionObjectMap.get("actionStatus").equals("E")) && (profileActionObjectMap.get("actionStatus").equals("E"))){
								actionData.setStatus("E");
								actionObjectMap.put("actionStatus",actionData.getStatus());
							}else{
								actionData.setStatus("D");
								actionObjectMap.put("actionStatus",actionData.getStatus());
							}
						}
					}
				}
			}
		} catch (Exception exp) {
			throw new DataManagerException("DataManager Exception: "+exp.getMessage(),exp);
		}
		return userControlActionMap;
	} 

	public Set getActionSets(String staffId) throws DataManagerException{
		AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
		ProfileBLManager profileBLManager = new ProfileBLManager();
		Set actionSet = null; 
		try {
			

			List<IGroupData> lstStaffGroupRel = getStaffGroupRelList(staffId);

			Map<String,IActionData> actionsMap =new HashMap<String,IActionData>();
			List<String> groupIds = null;
			if(lstStaffGroupRel!=null && lstStaffGroupRel.size()>0){
				groupIds = new ArrayList<String>();

				for (int i = 0; i < lstStaffGroupRel.size(); i++) {
					IGroupData groupData = (IGroupData)lstStaffGroupRel.get(i);
					groupIds.add(groupData.getGroupId());
				}
				List lstGroupActionRelList = accessGroupBLManager.getGroupActionRelData(groupIds);
				for(int j=0;j<lstGroupActionRelList.size();j++){
					IGroupActionRelData actionRelData =(IGroupActionRelData)lstGroupActionRelList.get(j); 
					actionsMap.put(actionRelData.getActionId(),actionRelData.getActionData());
				}

			}
			actionSet = actionsMap.keySet();
			return actionSet;
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return actionSet;
	}

	public HashMap getUserControlAction(String staffId) throws DataManagerException{
		AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager(); 
		ProfileBLManager profileBLManager = new ProfileBLManager();
		HashMap userControlMap = getUserControl(staffId);

		try {
			IStaffData staffData = getStaffData(staffId);
			List lstStaffGroupRel = getStaffGroupRelList(staffId);
			SortedSet actionSet = new TreeSet();
			Map<String,IActionData> actionsMap =new HashMap<String,IActionData>();
			List<String> groupIds = null;
			if(lstStaffGroupRel!=null && lstStaffGroupRel.size()>0){
				groupIds = new ArrayList<String>();

				for (int i = 0; i < lstStaffGroupRel.size(); i++) {
					IGroupData groupData = (IGroupData)lstStaffGroupRel.get(i);
					groupIds.add(groupData.getGroupId());
				}
				List lstGroupActionRelList = accessGroupBLManager.getGroupActionRelData(groupIds);
				for(int j=0;j<lstGroupActionRelList.size();j++){
					IGroupActionRelData actionRelData =(IGroupActionRelData)lstGroupActionRelList.get(j); 
					actionsMap.put(actionRelData.getActionId(),actionRelData.getActionData());
					actionSet.add(actionRelData.getActionId());
				}

			}

			
			for(Iterator iterator=actionSet.iterator();iterator.hasNext();){
				String  actionId = (String)iterator.next();
				IActionData actionData = actionsMap.get(actionId);
				HashMap modelMap = (HashMap)userControlMap.get("modelMap");

				for(Iterator w=modelMap.keySet().iterator();w.hasNext();){
					String modelAlias = (String)w.next();
					HashMap modelObjectMap = (HashMap)modelMap.get(modelAlias);
					String modelStatus = (String)modelObjectMap.get("businessModelStatus");
					HashMap moduleMap = (HashMap)modelObjectMap.get("moduleMap");
				
					boolean moduleFlag = false;
					for(Iterator w1=moduleMap.keySet().iterator();w1.hasNext();){
						String moduleAlias = (String)w1.next();
						HashMap moduleObjectMap = (HashMap)moduleMap.get(moduleAlias);
						String moduleStatus = (String)moduleObjectMap.get("businessModuleStatus");
						HashMap subModuleMap = (HashMap)moduleObjectMap.get("subModuleMap");
				
						boolean subModuleFlag = false;
						for(Iterator w2=subModuleMap.keySet().iterator();w2.hasNext();){
							String subModuleAlias = (String)w2.next();
							HashMap subModuleObjectMap = (HashMap)subModuleMap.get(subModuleAlias);
							String subModuleStatus = (String)subModuleObjectMap.get("subBusinessModuleStatus");
							HashMap actionMap = (HashMap)subModuleObjectMap.get("actionMap");


							boolean actionFlag = false;
							for(Iterator w3=actionMap.keySet().iterator();w3.hasNext();){
								String actionAlias = (String)w3.next();
								HashMap actionObjectMap = (HashMap)actionMap.get(actionAlias);
								if(actionAlias.equalsIgnoreCase(actionData.getAlias())){

									actionObjectMap.put("actionId",actionData.getActionId());
									actionObjectMap.put("actionName",actionData.getName());
									actionObjectMap.put("actionStatus",actionData.getStatus());

									actionObjectMap.put("actionAlias",actionData.getAlias());
									actionMap.put(actionData.getAlias(),actionObjectMap);
									actionFlag = true;
								}
							}
							if(actionFlag){
								subModuleObjectMap.put("subBusinessModuleStatus","E");
								subModuleFlag = true;
							}else{
								
							}
						}
						if(subModuleFlag){
							moduleObjectMap.put("businessModuleStatus","E");
							moduleFlag = true;
						}
					}
					if(moduleFlag){
						modelObjectMap.put("businessModelStatus","E");
					}
				}
			}

		} catch (Exception exp) {
			throw new DataManagerException("DataManager Exception: "+exp.getMessage(),exp);
		}
		return userControlMap;
	}



	/**
	  * @author  dhavalraval
	  * @param   staffId
	  * @return  IStaffData Object
	  * @throws  DataManagerException
	  * @purpose This method returns the Object of IStaffData.
	  */
	public IStaffData getStaffData(String staffId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
	
		try {
			IStaffData staffData = new StaffData();
			
			staffData = staffDataManager.getStaffData(staffId);

			return staffData;
		} catch (DataManagerException exp) {
			exp.printStackTrace();
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		} finally {
			closeSession(session);
		}
	}

	/**
	 * @author dhavalraval
	 * @return Returns Data Manager instance for staff data.
	 */
	public StaffDataManager getStaffDataManager(IDataManagerSession session) {
		StaffDataManager staffDataManager = (StaffDataManager) DataManagerFactory.getInstance().getDataManager(StaffDataManager.class, session);
		return staffDataManager; 
	}

	public void createValidate(IStaffData staffData)throws DataValidationException{
		// Name
		if(EliteGenericValidator.isBlankOrNull(staffData.getName())){
			throw (new DataValidationException("Invalid Staff Name",(MODULE+"."+"name").toLowerCase()));
		}
		// UserName
		if(EliteGenericValidator.isBlankOrNull(staffData.getUsername())){
			throw (new DataValidationException("Invalid Staff USerName",(MODULE+"."+"UserName").toLowerCase()));
		}
		// CreateDate
		if(EliteGenericValidator.isBlankOrNull(staffData.getCreateDate())){
			throw (new DataValidationException("Invalid Staff CreateDate",(MODULE+"."+"createDate").toLowerCase()));
		}
		// Password
		if(EliteGenericValidator.isBlankOrNull(staffData.getPassword())){
			throw (new DataValidationException("Invalid Staff Password",(MODULE+"."+"password").toLowerCase()));
		}
		// BirthDate
		if(EliteGenericValidator.isBlankOrNull(staffData.getBirthDate())){
			throw (new DataValidationException("Invalid Staff BirthDate",(MODULE+"."+"birthDate").toLowerCase()));
		}
		// LastModifiedDate
		if(EliteGenericValidator.isBlankOrNull(staffData.getLastModifiedDate())){
			throw (new DataValidationException("Invalid Staff LastModifiedDate",(MODULE+"."+"lastModifiedDate").toLowerCase()));
		}
		// EmailAddress
		if(
				( EliteGenericValidator.isBlankOrNull(staffData.getEmailAddress())  ||
						!EliteGenericValidator.isEmail(staffData.getEmailAddress())
				) 
		){
			throw (new DataValidationException("Invalid Staff EmailAdress",(MODULE+"."+"emailAddress").toLowerCase()));
		}
		// Address1
		if(EliteGenericValidator.isBlankOrNull(staffData.getAddress1())){
			throw (new DataValidationException("Invalid Staff Address",(MODULE+"."+"address1").toLowerCase()));
		}
		// Zip
		if(EliteGenericValidator.isBlankOrNull(staffData.getZip())){
			throw (new DataValidationException("Invalid Staff Zip",(MODULE+"."+"zip").toLowerCase()));
		}
		// City
		if(EliteGenericValidator.isBlankOrNull(staffData.getCity())){
			throw (new DataValidationException("Invalid Staff City",(MODULE+"."+"city").toLowerCase()));
		}
		// State
		if(EliteGenericValidator.isBlankOrNull(staffData.getState())){
			throw (new DataValidationException("Invalid Staff State",(MODULE+"."+"state").toLowerCase()));
		}
		// Country
		if(EliteGenericValidator.isBlankOrNull(staffData.getCountry())){
			throw (new DataValidationException("Invalid Staff Country",(MODULE+"."+"country").toLowerCase()));
		}
		// CommonStatusId
		if(EliteGenericValidator.isBlankOrNull(staffData.getCommonStatusId())){
			throw (new DataValidationException("Invalid Staff Status",(MODULE+"."+"status").toLowerCase()));
		}
		// CreatedByStaffId
		if(EliteGenericValidator.isBlankOrNull(staffData.getCreatedByStaffId()))        				
		{
			throw (new DataValidationException("Invalid Staff CreatedByStaffId",(MODULE+"."+"CreatedByStaffId").toLowerCase()));
		}
		// LastModifiedByStaffId
		if(EliteGenericValidator.isBlankOrNull(staffData.getLastModifiedByStaffId())){
			throw (new DataValidationException("Invalid Staff LastModifiedByStaff",(MODULE+"."+"LastModifiedByStaffId").toLowerCase()));
		}
		// SystemGenerated
		if(EliteGenericValidator.isBlankOrNull(staffData.getSystemGenerated())){
			throw (new DataValidationException("Invalid Staff SystemGenerated",(MODULE+"."+"SystemGenerated").toLowerCase()));
		}
	}
	public void updateBasicDetailValidate(IStaffData staffData)throws DataValidationException{
		// Name
		if(EliteGenericValidator.isBlankOrNull(staffData.getName())){
			throw (new DataValidationException("Invalid Staff Name",(MODULE+"."+"name").toLowerCase()));
		}
		// BirthDate
		if(EliteGenericValidator.isBlankOrNull(staffData.getBirthDate())){
			throw (new DataValidationException("Invalid Staff BirthDate",(MODULE+"."+"birthDate").toLowerCase()));
		}
		// Address1
		if(EliteGenericValidator.isBlankOrNull(staffData.getAddress1())){
			throw (new DataValidationException("Invalid Staff Address",(MODULE+"."+"address1").toLowerCase()));
		}
		// City
		if(EliteGenericValidator.isBlankOrNull(staffData.getCity())){
			throw (new DataValidationException("Invalid Staff City",(MODULE+"."+"city").toLowerCase()));
		}
		// State
		if(EliteGenericValidator.isBlankOrNull(staffData.getState())){
			throw (new DataValidationException("Invalid Staff State",(MODULE+"."+"state").toLowerCase()));
		}
		// Country
		if(EliteGenericValidator.isBlankOrNull(staffData.getCountry())){
			throw (new DataValidationException("Invalid Staff Country",(MODULE+"."+"country").toLowerCase()));
		}
		// Zip
		if(EliteGenericValidator.isBlankOrNull(staffData.getZip())){
			throw (new DataValidationException("Invalid Staff Zip",(MODULE+"."+"zip").toLowerCase()));
		}
		// EmailAddress
		if(
				( EliteGenericValidator.isBlankOrNull(staffData.getEmailAddress())  ||
						!EliteGenericValidator.isEmail(staffData.getEmailAddress())
				) 
		){
			throw (new DataValidationException("Invalid Staff EmailAdress",(MODULE+"."+"emailAddress").toLowerCase()));
		}
		// LastModifiedDate

		if(EliteGenericValidator.isBlankOrNull(staffData.getLastModifiedDate())){
			throw (new DataValidationException("Invalid Staff LastModifiedDate",(MODULE+"."+"LastModifiedDate").toLowerCase()));
		}
		/* StatusChangedDate
    	if(EliteGenericValidator.isBlankOrNull(staffData.getStatusChangeDate())){
    		throw (new DataValidationException("Invalid Staff StatusChangeDate",(MODULE+"."+"StatusChangeDate").toLowerCase()));
    	}*/
	}
	public void changeUserNameValidate(IStaffData staffData) throws DataManagerException {
		// UserName
		if(EliteGenericValidator.isBlankOrNull(staffData.getUsername())){
			throw (new DataValidationException("Invalid Staff UserName",(MODULE+"."+"UserName").toLowerCase()));
		}
		/*
        // StatusChangeDate
        if(EliteGenericValidator.isBlankOrNull(staffData.getStatusChangeDate())){
        	throw (new DataValidationException("Invalid Staff StatusChangeDate",(MODULE+"."+"StatusChangeDate").toLowerCase()));
        }
		 */

	}
	public void changePasswordValidate(String staffId, String oldPassword, String newPassword) throws DataManagerException {
		// Password
		if(EliteGenericValidator.isBlankOrNull(newPassword)){
			throw (new DataValidationException("Invalid Staff password",(MODULE+"."+"password").toLowerCase()));
		}
		/*
       // StatusChangeDate
       if(EliteGenericValidator.isBlankOrNull(staffData.getStatusChangeDate())){
       	throw (new DataValidationException("Invalid Staff StatusChangeDate",(MODULE+"."+"StatusChangeDate").toLowerCase()));
       }
		 */
	}
	public void updateStatusValidate(List lstStaffIds,String commonStatusId) throws DataManagerException {
		// CommonStatusId
		if(EliteGenericValidator.isBlankOrNull(commonStatusId)){
			throw (new DataValidationException("Invalid Staff commonStatusId",(MODULE+"."+"commonStatusId").toLowerCase()));
		}
		/*	   
	  // StatusChangeDate
	   if(EliteGenericValidator.isBlankOrNull(staffData.getStatusChangeDate())){
	    throw (new DataValidationException("Invalid Staff StatusChangeDate",(MODULE+"."+"statuschangedate").toLowerCase()));
	   }
		 */	   
	}
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}
	public HashMap getFilteredMap( HashMap compareMap , HashMap licenseMap ) {
		ProfileBLManager profileBLManager = new ProfileBLManager();
		try {
			HashMap compareModelMap = (HashMap) compareMap.get("modelMap");
			HashMap modelMap = (HashMap) licenseMap.get("modelMap");
			for ( Iterator w = compareModelMap.keySet().iterator(); w.hasNext(); ) {
				String modelAlias = (String) w.next();

				boolean flag = true;
				//		Right now not having support for License in the Server Manager so the below lines are commented out.
				//                boolean flag = false;
				//                for ( Iterator m = licenseMap.keySet().iterator(); m.hasNext(); ) {
				//                    String licenseKey = (String) m.next();
				//                    
				//                    if (modelAlias.equalsIgnoreCase(licenseKey)) {
				//                        flag = true;
				//                        break;
				//                    }
				//                }

				if (flag) {


					if (modelMap != null) {
						IBISModelData bisModelData = profileBLManager.getBISModelAlias(modelAlias);

						HashMap compareModelObjectMap = (HashMap) compareModelMap.get(modelAlias);
						HashMap modelObjectMap = (HashMap) modelMap.get(modelAlias);
						if(modelObjectMap!=null){
							if (( modelObjectMap.get("businessModelStatus").equals("E")) && (compareModelObjectMap.get("businessModelStatus").equals("E")) ) {
								bisModelData.setStatus("E");
								compareModelObjectMap.put("businessModelStatus", bisModelData.getStatus());
							} else {
								bisModelData.setStatus("D");
								compareModelObjectMap.put("businessModelStatus", bisModelData.getStatus());
							}
							HashMap compareModuleMap = (HashMap) compareModelObjectMap.get("moduleMap");
							HashMap moduleMap = (HashMap) modelObjectMap.get("moduleMap");

							for ( Iterator w1 = compareModuleMap.keySet().iterator(); w1.hasNext(); ) {
								String moduleAlias = (String) w1.next();
								IBISModuleData bisModuleData = new BISModuleData();
								bisModuleData = profileBLManager.getBISModuleAlias(moduleAlias);

								HashMap compareModuleObjectMap = (HashMap) compareModuleMap.get(moduleAlias);
								HashMap moduleObjectMap = (HashMap) moduleMap.get(moduleAlias);
								if(moduleObjectMap!=null){
									if ((moduleObjectMap.get("businessModuleStatus").equals("E")) && (compareModuleObjectMap.get("businessModuleStatus").equals("E")) ) {
										bisModuleData.setStatus("E");
										compareModuleObjectMap.put("businessModuleStatus", bisModuleData.getStatus());
									} else {
										bisModuleData.setStatus("D");
										compareModuleObjectMap.put("businessModuleStatus", bisModuleData.getStatus());
									}
									HashMap compareSubModuleMap = (HashMap) compareModuleObjectMap.get("subModuleMap");
									HashMap subModuleMap = (HashMap) moduleObjectMap.get("subModuleMap");

									for ( Iterator w2 = compareSubModuleMap.keySet().iterator(); w2.hasNext(); ) {
										String subModuleAlias = (String) w2.next();
										ISubBISModuleData subBISModuleData = new SubBISModuleData();
										subBISModuleData = profileBLManager.getSubBISModuleAlias(subModuleAlias);

										HashMap compareSubModuleObjectMap = (HashMap) compareSubModuleMap.get(subModuleAlias);
										HashMap subModuleObjectMap = (HashMap) subModuleMap.get(subModuleAlias);
										if(subModuleObjectMap!=null){
											if ((subModuleObjectMap.get("subBusinessModuleStatus").equals("E")) && (compareSubModuleObjectMap.get("subBusinessModuleStatus").equals("E"))) {
												subBISModuleData.setStatus("E");
												compareSubModuleObjectMap.put("subBusinessModuleStatus", subBISModuleData.getStatus());
											} else {
												subBISModuleData.setStatus("D");
												compareSubModuleObjectMap.put("subBusinessModuleStatus", subBISModuleData.getStatus());
											}
											HashMap compareActionMap = (HashMap) compareSubModuleObjectMap.get("actionMap");
											HashMap actionMap = (HashMap) subModuleObjectMap.get("actionMap");

											for ( Iterator w3 = compareActionMap.keySet().iterator(); w3.hasNext(); ) {
												String actionAlias = (String) w3.next();
												IActionData actionData = new ActionData();
												actionData = profileBLManager.getActionAlias(actionAlias);

												HashMap compareActionObjectMap = (HashMap) compareActionMap.get(actionAlias);
												HashMap actionObjectMap = (HashMap) actionMap.get(actionAlias);
												if(actionObjectMap!=null){
													if ((actionObjectMap.get("actionStatus").equals("E")) && (compareActionObjectMap.get("actionStatus").equals("E"))) {
														actionData.setStatus("E");
														compareActionObjectMap.put("actionStatus", actionData.getStatus());
													} else {
														actionData.setStatus("D");
														compareActionObjectMap.put("actionStatus", actionData.getStatus());
													}
												}
											}
										}
									}
								}
							}
						}
					}
				} else {
					/*    
					 * Right now not having support for License in the Server Manager so the below lines are commented out.
					 *  IBISModelData bisModelData = new BISModelData();
                    bisModelData = profileBLManager.getBISModelAlias(modelAlias);

                    HashMap compareModelObjectMap = (HashMap) compareModelMap.get(modelAlias);
                    bisModelData.setStatus("D");

                    compareModelObjectMap.put("businessModelStatus", bisModelData.getStatus());

                    HashMap compareModuleMap = (HashMap) compareModelObjectMap.get("moduleMap");

                    for ( Iterator w1 = compareModuleMap.keySet().iterator(); w1.hasNext(); ) {
                        String moduleAlias = (String) w1.next();
                        IBISModuleData bisModuleData = new BISModuleData();
                        bisModuleData = profileBLManager.getBISModuleAlias(moduleAlias);

                        HashMap compareModuleObjectMap = (HashMap) compareModuleMap.get(moduleAlias);

                        bisModuleData.setStatus("D");
                        compareModuleObjectMap.put("businessModuleStatus", bisModuleData.getStatus());

                        HashMap compareSubModuleMap = (HashMap) compareModuleObjectMap.get("subModuleMap");

                        for ( Iterator w2 = compareSubModuleMap.keySet().iterator(); w2.hasNext(); ) {
                            String subModuleAlias = (String) w2.next();
                            ISubBISModuleData subBISModuleData = new SubBISModuleData();
                            subBISModuleData = profileBLManager.getSubBISModuleAlias(subModuleAlias);

                            HashMap compareSubModuleObjectMap = (HashMap) compareSubModuleMap.get(subModuleAlias);
                            subBISModuleData.setStatus("D");
                            compareSubModuleObjectMap.put("subBusinessModuleStatus", subBISModuleData.getStatus());

                            HashMap compareActionMap = (HashMap) compareSubModuleObjectMap.get("actionMap");

                            for ( Iterator w3 = compareActionMap.keySet().iterator(); w3.hasNext(); ) {
                                String actionAlias = (String) w3.next();
                                IActionData actionData = new ActionData();
                                actionData = profileBLManager.getActionAlias(actionAlias);

                                HashMap compareActionObjectMap = (HashMap) compareActionMap.get(actionAlias);
                                actionData.setStatus("D");
                                compareActionObjectMap.put("actionStatus", actionData.getStatus());
                            }
                        }
                    }*/
				}
			}
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		return compareMap;
	}


	public Set<String> getActionAliasSets(String staffId) throws DataManagerException{
		AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
		ProfileBLManager profileBLManager = new ProfileBLManager();
		Set<String> actionAliasSet = new HashSet<String>();
		try {
			List<IGroupData> lstStaffGroupRel = getStaffGroupRelList(staffId);

			Map<String,IActionData> actionsMap =new HashMap<String,IActionData>();
			List<String> groupIds = null;
			if(lstStaffGroupRel!=null && lstStaffGroupRel.size()>0){
				groupIds = new ArrayList<String>();

				for (int i = 0; i < lstStaffGroupRel.size(); i++) {
					IGroupData groupData = (IGroupData)lstStaffGroupRel.get(i);
					groupIds.add(groupData.getGroupId());
				}
				List lstGroupActionRelList = accessGroupBLManager.getGroupActionRelData(groupIds);
				for(int j=0;j<lstGroupActionRelList.size();j++){
					IGroupActionRelData actionRelData =(IGroupActionRelData)lstGroupActionRelList.get(j); 
					actionAliasSet.add(actionRelData.getActionData().getAlias());
				}

			}
			Set actionSet = actionsMap.keySet();
		}catch(DataManagerException e){
			throw  e;
		} catch (Exception exp) {
			Logger.logDebug(MODULE,"Error to Get Ation Alias Set. Reason :-" + exp.getMessage());
			Logger.logTrace(MODULE,exp);
		}
		return actionAliasSet;
	}
	
	/**
	 * @author pratik.chauhan
	 * @param   staffData
	 * @param   actionAlias
	 * @return  void
	 * @throws  DataManagerException
	 * @purpose This method is generated to check permission for web service Method Access
	 */

	public void checkPermission(IStaffData staffData, String actionAlias) throws DataManagerException{

		boolean isUserPermitted=false;

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		if(staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{

			if(!staffData.getUsername().equals("admin")){
				List<IStaffData> staffDataList = staffDataManager.getList(staffData);
				if(staffDataList != null && !staffDataList.isEmpty()){
					staffData = staffDataList.get(0);
				}else
					throw new DataManagerException(" UserName "+ staffData.getUsername() +"  Not Found " );

				staffDataManager.checkPermission(staffData.getUsername(), actionAlias);
			}

		}catch(DataManagerException dexp){
			throw new DataManagerException("Failed to check permission of user, Reason :" + dexp.getMessage(),dexp);
		}catch(Exception exp){
			Logger.logTrace(MODULE,exp);
			throw new DataManagerException("Failed to check permission of user, Reason :" + exp.getMessage(),exp);
		}finally{
			closeSession(session);
		}

	}
	public Date getLastChangedPwdDate(String userName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		 Date lastModifiedDate = null;
		if(staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{

		    lastModifiedDate = staffDataManager.getLastChangedPwdDate(userName);
		    
		}catch(Exception exp){
			Logger.logTrace(MODULE,exp);
		}finally{
			closeSession(session);
		}
		return lastModifiedDate;
	}

	public boolean isValidUser(String systemUserName, String password) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		Date lastModifiedDate = null;
		boolean isValidUser = false;
		
		if(staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try{

			isValidUser = staffDataManager.isValidUser(systemUserName, password);
		    
		}catch(Exception exp){
			Logger.logTrace(MODULE,exp);
			throw new DataManagerException("Failed to check of valid user or not, Reason :" + exp.getMessage(),exp);
		}finally{
			closeSession(session);
		}
		return isValidUser;
	}
	
	/**
	 * This method checks that the new-password is similar to any historical(previous) password. If so it throws 
	 * exception else it would give you the string with the updated historical password
	 * @param newPassword New password that the user wants to set
	 * @param historyPassword String containing historical password
	 * @return String of historical password
	 * @throws DataManagerException
	 */
	private String manageHistoricalPasswords(String newPassword, String historyPassword) throws DataManagerException {
		List<String> historyPasswordList = null;
		String newHistoryPassword=null;
		StringBuilder historyPasswordSB = new StringBuilder();
		
		if(historyPassword != null){
			historyPasswordList = getHistoricalPasswordList(historyPassword);
			int maxHistoricalPassword=maxHistoricalPassword();
			if(checkPasswordInList(newPassword, historyPasswordList, maxHistoricalPassword)){
				throw new DataManagerException("Do not enter any historical password");
			} else {
				int size = historyPasswordList.size();
				int i = 0;
				if(maxHistoricalPassword <= size){
					i = size - maxHistoricalPassword + 1;
				}
				for (; i < size; i++) {
					String tempPassword = historyPasswordList.get(i);
					tempPassword = tempPassword.replace("\\","\\\\");
					tempPassword = tempPassword.replace(",", "\\,");
					historyPasswordSB.append(tempPassword).append(",");
				}
			}
		}

		newPassword = newPassword.replace("\\","\\\\");
		newPassword = newPassword.replace(",", "\\,");
		historyPasswordSB.append(newPassword).append(",");

		try {
			newHistoryPassword = PasswordEncryption.getInstance().crypt(historyPasswordSB.toString(), PasswordEncryption.ELITE_PASSWORD_CRYPT);
		} catch (NoSuchEncryptionException e) {
			throw new DataManagerException("Error while encrypting recents passwords");
		} catch (EncryptionFailedException e) {
			throw new DataManagerException("Error while encrypting recents passwords");
		}
		return newHistoryPassword;
	}
	
	/**
	 * This method checks password is a historical password or not if yes then returns the max number of historical 
	 * password value else will return -1.
	 * @param staffId User id
	 * @param newPassword new password entered by the user
	 * @return string
	 * @throws DataManagerException
	 */
	public String passwordIsHistoricalPassword(String staffId, String newPassword) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		
		try {
			List<String> historyPasswordList = new ArrayList<String>();
			if(staffId == null)
				return "0";
			
			IStaffData staffData = getStaffData(staffId);
			
			if(staffData.getHistoricalPassword() != null){
				historyPasswordList = getHistoricalPasswordList(staffData.getHistoricalPassword());
				
				int maxHistoricalPassword = maxHistoricalPassword();
				if(checkPasswordInList(newPassword, historyPasswordList, maxHistoricalPassword)){
					return Integer.toString(maxHistoricalPassword);
				}
			}
			return "-1";
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to check historical password, Reason :" + e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
	
	/**
	 * To get max number of historical password
	 * @return max number of historical password
	 * @throws DataManagerException
	 */
	public int maxHistoricalPassword() throws DataManagerException{
		PasswordSelectionPolicyBLManager passwordBLManager=new PasswordSelectionPolicyBLManager();
		
		PasswordPolicyConfigData passwordPolicySelectionData=passwordBLManager.getPasswordSelectionPolicy();
		return passwordPolicySelectionData.getMaxHistoricalPasswords();
	}
	
	/**
	 * Get list of historical password from historical password string
	 * @param historyPassword String of historical password
	 * @return List of historical password
	 * @throws DataManagerException
	 */
	private List getHistoricalPasswordList(String historyPassword) throws DataManagerException {
		List<Symbol> historyPasswordSymList = null;
		List<String> historyPasswordList = new ArrayList<String>();
		
		try {
			historyPassword = PasswordEncryption.getInstance().decrypt(historyPassword, PasswordEncryption.ELITE_PASSWORD_CRYPT);
			
			historyPasswordSymList = scanner.getSymbols(historyPassword);
		} catch (InvalidSymbolException e) {
			throw new DataManagerException("Error while retrieving recents passwords");
		} catch (NoSuchEncryptionException e) {
			new DataManagerException("Error while decrypting recents passwords");
		} catch (DecryptionNotSupportedException e) {
			new DataManagerException("Error while decrypting recents passwords");
		} catch (DecryptionFailedException e) {
			new DataManagerException("Error while decrypting recents passwords");
		}
		
		for(Symbol password:historyPasswordSymList){
			String tempPassword = password.getName();

			if(!tempPassword.equals(",")){
				historyPasswordList.add(tempPassword);
			}
		}
		
		return historyPasswordList;
	}
	
	private boolean checkPasswordInList(String newPassword, List historicalPasswordList,int maxHistoricalPassword) {
		int size = historicalPasswordList.size();
		boolean passwordExist = false;
		int i = 0;
		if(maxHistoricalPassword <= size){
			i = size - maxHistoricalPassword;
		}
		for (; i < size; i++) {
			if(historicalPasswordList.get(i).toString().equals(newPassword)){
				passwordExist = true;
				break;
			}
		}
		return passwordExist;
	}
	
		
	/**
	 * Returns StaffData object for the specified user-name.
	 * @param userName user-name of the staff
	 * @return StaffData object of the given user-name.
	 * @throws DataManagerException
	 */
	public IStaffData getStaffDataByUserName(String userName) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		
		try {
			IStaffData staffData = new StaffData();
			staffData.setUsername(userName);

			List lstStaff = staffDataManager.getList(staffData);

			if(lstStaff != null && lstStaff.size() >= 1){
				staffData = (IStaffData)lstStaff.get(0);
			}else{
				staffData = null;
			}
			return staffData;
		} catch (DataManagerException e) {
			throw new DataManagerException("Failed to retrive staff data by username"+e.getMessage(),e);
		} finally {
			closeSession(session);
		}
	}
}
