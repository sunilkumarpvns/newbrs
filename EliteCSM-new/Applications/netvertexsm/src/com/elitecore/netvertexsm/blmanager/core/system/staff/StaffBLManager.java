package com.elitecore.netvertexsm.blmanager.core.system.staff;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import com.elitecore.exprlib.parser.exception.InvalidSymbolException;
import com.elitecore.exprlib.scanner.Scanner;
import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.scanner.impl.ScannerImpl;
import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.profilemanagement.ProfileBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.systemparameter.PasswordSelectionPolicyBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleSubBISModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ISubBISModuleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.SubBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.StaffDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.util.PasswordUtility;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.StaffConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

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

public class StaffBLManager extends BaseBLManager{
	private static final String MODULE = "STAFF";
	public static final int BASE16 = 16;
	
	/**
	 * @author  dhavalraval
	 * @param 	radiusPolicyData
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of StaffData.
	 */
	
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

	public List getList(IStaffData staffData) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		List lstStaffList;
		try{
		if (staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		lstStaffList = staffDataManager.getList(staffData);
		}finally{
		session.close();
		}
		return lstStaffList;
	}

	/**
	 * @author  dhavalraval
	 * @param   staffId
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returs the list for specific StaffId.
	 */
	public List getStaffRoleRelList(long staffId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		List<StaffGroupRoleRelData> lstStaffRoleActionRelList;
		List<RoleData> roleData;

		try{

		if(staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		lstStaffRoleActionRelList = staffDataManager.getStaffGroupRoleRelList(staffId);
		  roleData= Collectionz.newArrayList();
		if(Collectionz.isNullOrEmpty(lstStaffRoleActionRelList) == false){
			for(StaffGroupRoleRelData relationData : lstStaffRoleActionRelList){
				roleData.add(relationData.getRoleData());
			}
		}
		}catch(DataManagerException e){
			throw e ;
		}finally{
			session.close();
		}
		return roleData;
	}

	
	/**
	 * @author  ishani.bhatt
	 * @param   staffId
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of relation between staff-group and role based on the staffId.
	 */
	public List<StaffGroupRoleRelData> getStaffGroupRoleRelList(long staffId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		List<StaffGroupRoleRelData> staffGroupRoleRelList;
        try{
		if(staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		staffGroupRoleRelList = staffDataManager.getStaffGroupRoleRelList(staffId);
        }finally{
        	session.close();
        }
		return staffGroupRoleRelList;
	}
	/**
	 * @author  dhavalraval
	 * @return  List
	 * @throws  DataManagerException
	 * @purpose This method returns the list of whole the StaffData. 
	 */
	public List getList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		List lstStaffList;
		try{
		if(staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		lstStaffList = staffDataManager.getList();
		}finally{
		session.close();
		}
		return lstStaffList;
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
	public PageList search(IStaffData staffData, int pageNo, int pageSize,IStaffData staffData1,String actionAlias) throws DataManagerException {

		List lstHiddenUser = new ArrayList();
		lstHiddenUser.add(BaseConstant.ADMIN_USER_NAME);
		lstHiddenUser.add(BaseConstant.PROFILE_USER_NAME);



		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		PageList lstStaffList;
		if (staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			lstStaffList = staffDataManager.search(staffData, pageNo, pageSize,lstHiddenUser); 
		}
		catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}
		return lstStaffList;

	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @throws  DataManagerException
	 * @purpose This method is generated to create StaffData.
	 */
	public void create(IStaffData staffData,IStaffData staffData1, String actionAlias,StaffProfilePictureData staffProfilePicture) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (staffDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			createValidate(staffData);
			session.beginTransaction();


			List<IStaffData> staffDataList = staffDataManager.getStaffData(staffData.getUserName());
			if(staffDataList!=null && staffDataList.size()>0){
				throw new DuplicateParameterFoundExcpetion("Duplicate User Name.");
			}

			staffDataManager.create(staffData);
			staffProfilePicture.setId(staffData.getStaffId()+"");
			staffDataManager.create(staffProfilePicture);
			String transactionId = String.valueOf(staffData.getStaffId());
			systemAuditDataManager.updateTbltSystemAudit(staffData1, actionAlias,transactionId);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion exp){
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate User Name. : "+exp.getMessage());
		}catch(ConstraintViolationException exp){
			session.rollback();
			throw new ConstraintViolationException("Duplicate Composite Primay Key Found. : "+exp.getMessage());
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage());
		}finally{
			session.close();
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
		try{
		List lstStaff = staffDataManager.getList(staffData);
		if(lstStaff != null && lstStaff.size() >= 1){
			staffData =(IStaffData) lstStaff.get(0);
		}
			return staffData ;
		}finally{
		session.close();
	}
		
	}
	public IStaffData getStaff(IStaffData staffData,IStaffData staffData1,String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		if (staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{

			List lstStaff = staffDataManager.getList(staffData);

			if(lstStaff != null && lstStaff.size() >= 1){
				staffData =(IStaffData) lstStaff.get(0);
			}
		}
		catch(Exception e){
				throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
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
						Long staffid = Long.parseLong(lstStaffIds.get(i).toString());
						staffDataManager.updateStatus(staffid,commonStatusId,new Timestamp(currentDate.getTime()));

						systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
					}
				}
				session.commit();
			}else{
				throw new DataManagerException("Data Manager implementation not found for ");    		
			}
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Data Manager implementation not found for "+exp.getMessage());
		}finally{
			session.close();
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffId
	 * @param   oldPassword
	 * @param   newPassword
	 * @throws  DataManagerException
	 * @throws EncryptionFailedException 
	 * @throws DecryptionFailedException 
	 * @throws NumberFormatException 
	 * @purpose This method is generated to change Password of StaffData.
	 */
	public void changePassword(Long staffId, String oldPassword, String newPassword,IStaffData staffData1, String actionAlias) throws DataManagerException, NumberFormatException, DecryptionFailedException, EncryptionFailedException, DecryptionNotSupportedException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		IStaffData staffData = null;
		Date currentDate = new Date();

		if(staffDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		if(staffId == null)
			throw new DataManagerException("StaffId is null "+getClass().getName());

		if(oldPassword == null)
			throw new DataManagerException("OldPassword is null "+getClass().getName());

		try{

			session.beginTransaction();
			changePasswordValidate(staffId, oldPassword, newPassword);
			staffData = new StaffData();
			staffData.setStaffId(Long.valueOf(staffId));
			staffData = (IStaffData) getList(staffData).get(0);

			if(oldPassword.equals(newPassword)){
				throw new DataManagerException(StaffConstant.SAME_OLD_AND_NEW_PASSWORDS);
			}
			String encryptedRecentPassword = prepareRecentPasswords(oldPassword, newPassword, staffData, currentDate);
			staffDataManager.changePassword(staffId, oldPassword, newPassword,new Timestamp(currentDate.getTime()), encryptedRecentPassword);

			String transactionId = Long.toString(staffId);
			systemAuditDataManager.updateTbltSystemAudit(staffData1, actionAlias,transactionId);

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
			throw new DataManagerException("Data Manager implementation not found for "+e.getMessage(), e);
		}
		catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Data Manager implementation not found for "+exp.getMessage(),exp);
		}  finally
		{
			if(session != null)
				session.close();
		}
	}

	public void resetPassword(Long staffId ,String newPassword,IStaffData staffData, String actionAlias) throws DataManagerException,EncryptionFailedException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);


		if(staffDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		if(staffId == null) {
			throw new DataManagerException("StaffId is null " + getClass().getName());
		}
		try {

			session.beginTransaction();
			staffDataManager.resetPassword(staffId,newPassword,new Timestamp(new Date().getTime()));

			String transactionId = Long.toString(staffId);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);

			session.commit();

		} catch(DataValidationException e) {
			session.rollback();
			throw e;
		} catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Data Manager implementation not found for "+exp.getMessage(),exp);
		}  finally {
			if(session != null)
				session.close();
		}
	}

	private String prepareRecentPasswords(String oldPassword,
		String newPassword, IStaffData staffData, Date currentDate)
			throws DataManagerException, NoSuchEncryptionException, DecryptionFailedException, NumberFormatException, EncryptionFailedException, DecryptionNotSupportedException {
	StringBuilder newRecentPasswords = new StringBuilder();
	
	PasswordSelectionPolicyBLManager passwordBLManager = new PasswordSelectionPolicyBLManager();
	PasswordPolicyConfigData passwordPolicySelectionData = passwordBLManager.getPasswordSelectionPolicy();
	Integer historicalPasswords = passwordPolicySelectionData.getTotalHistoricalPasswords();
	
	String recentPasswordsStr = staffData.getRecentPasswords();;
	String tempOldPassword = PasswordUtility.getEncryptedPassword(oldPassword);
	tempOldPassword = tempOldPassword.replace("\\","\\\\");
	newRecentPasswords.append(tempOldPassword.replace(",","\\,"));					
	
	if( recentPasswordsStr != null ){
		
		List<String> oldPassList =  new ArrayList<String>();
		try {
			List<Symbol> oldPasswords = scanner.getSymbols(recentPasswordsStr);					
			for(Symbol pass : oldPasswords){						
				if(pass.getName()!=null && pass.getName().equalsIgnoreCase(",")==false){
					oldPassList.add(pass.getName());							 
				}
			}
		} catch (InvalidSymbolException e) {
			throw new DataManagerException("Error while separating recents passwords, Reason: "+e.getMessage(), e);
		}
		
		for(int i=0; i<historicalPasswords; i++){
			if(oldPassList.size()>i){					
				String tempPassword = oldPassList.get(i);						
				if(tempPassword!=null && tempPassword.length()>0){
					if (tempPassword.equals(PasswordUtility.getEncryptedPassword(newPassword))){
						throw new DataManagerException(StaffConstant.SAME_NEW_AND_HISTORICAL_PASSWORD+" : "+historicalPasswords);
					}else{
						if(i<historicalPasswords-1){
							newRecentPasswords.append(",");					
							tempPassword = tempPassword.replace("\\","\\\\");
							newRecentPasswords.append(tempPassword.replace(",", "\\,"));
						}
					}
				}
			}
		}
	}

	if(staffData.getPassword().equals(PasswordUtility.getEncryptedPassword(oldPassword)) || (staffData.getPasswordChangeDate() == null && staffData.getPassword().equals(oldPassword))){
		staffData.setPassword(newPassword);
		staffData.setLastModifiedDate(new Timestamp(currentDate.getTime()));
		staffData.setPasswordChangeDate(new Timestamp(currentDate.getTime()));
	}else{
		throw new DataManagerException(StaffConstant.OLD_PASSWORD_DB_AND_GUI_DONT_MATCH);                                         
	}
	String encryptedRecentPassword = newRecentPasswords.toString();
	return encryptedRecentPassword;
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

			if (staffDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			IStaffData staffData = new StaffData();
			staffData.setUserName(userName);
			List lstStaffData = staffDataManager.getList(staffData);

			for(int i=0;i< lstStaffData.size();i++){
				staffData = (IStaffData)lstStaffData.get(i);
				//             if(PasswordEncryption.getInstance().matches(staffData.getPassword(),password,Integer.parseInt(ConfigManager.get(BaseConstant.ENCRYPTION_MODE)))
				//            && staffData.getCommonStatusId().equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID)){
				if(staffData.getPassword().equals(password)&& staffData.getCommonStatusId().equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID)){
					userId = String.valueOf(staffData.getStaffId());
					staffData.setLastLoginTime(getCurrentTimeStemp());
					session.beginTransaction();
					staffDataManager.updateLoginInfo(staffData);
					session.commit();
					
				}
			}
		} catch(DataManagerException e){
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

	public StaffData validateLoginUser(String userName, String password) throws DataManagerException {

		StaffData staffData= null;
		IDataManagerSession session = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			StaffDataManager staffDataManager = getStaffDataManager(session);

			if (staffDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			staffData = new StaffData();
			staffData.setUserName(userName);
			List lstStaffData = staffDataManager.getList(staffData);
            if(Collectionz.isNullOrEmpty(lstStaffData)==false){
				return  (StaffData)lstStaffData.get(0);
			}else{
				staffData = null;
			}
			/*for(int i=0;i< lstStaffData.size();i++){
				staffData = (IStaffData)lstStaffData.get(i);
				//             if(PasswordEncryption.getInstance().matches(staffData.getPassword(),password,Integer.parseInt(ConfigManager.get(BaseConstant.ENCRYPTION_MODE)))
				//            && staffData.getCommonStatusId().equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID)){
				if(staffData.getPassword().equals(password)&& staffData.getCommonStatusId().equalsIgnoreCase(BaseConstant.SHOW_STATUS_ID)){
					userId = String.valueOf(staffData.getStaffId());
					staffData.setLastLoginTime(getCurrentTimeStemp());
					session.beginTransaction();
					staffDataManager.updateLoginInfo(staffData);
					session.commit();

				}
			}*/
		} catch(DataManagerException e){
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
		return staffData;
	}


	/**
	 * @author  aneri.chavda
	 * @param   userId
	 * @return
	 * @throws  DataManagerException
	 * @purpose This method is generated to update the LastLoginDuration of the user.
	 */

	public void updateLoginInfo(String userId)  throws DataManagerException{

		IDataManagerSession session = null;
		StaffDataManager staffDataManager = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			staffDataManager = getStaffDataManager(session);
			if (staffDataManager == null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			IStaffData staffData = getStaffData(userId);
			session.beginTransaction();
			staffData.setLastLoginTime(getCurrentTimeStemp());
			staffDataManager.updateLoginInfo(staffData);
			session.commit();
		}catch(DataManagerException e){
			session.rollback();
			throw e;
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException(e.getMessage(),e);
		}
		finally{
			if (session != null){
				session.close();
			}
		}
	}



	/**
	 * @author  aneri.chavda
	 * @param   userId
	 * @return  
	 * @throws  DataManagerException
	 * @purpose This method is generated to update the LastLoginDuration of the user.
	 */
	
	public void updateLogoutInfo(String userId)  throws DataManagerException{

		IDataManagerSession session = null;
		StaffDataManager staffDataManager = null;
		try{
			session = DataManagerSessionFactory.getInstance().getDataManagerSession();
			staffDataManager = getStaffDataManager(session);
			if (staffDataManager == null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			IStaffData staffData = getStaffData(userId);
			session.beginTransaction();
			staffDataManager.updateLogoutInfo(staffData);
			session.commit();
		}catch(DataManagerException e){
			session.rollback();
			throw e;
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException(e.getMessage(),e);
		}
		finally{
			if (session != null){
				session.close();
			}
		}
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
						//						staffDataManager.deleteStaffRoleRel(lstStaffIds.get(i).toString());
						String transactionId = lstStaffIds.get(i).toString();
						Long staffId = Long.parseLong(lstStaffIds.get(i).toString());
						staffDataManager.delete(staffId);

						systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
					}
				}
				session.commit();
			}else{
				throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
			}
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage());
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed : "+e.getMessage());
		}finally{
			session.close();
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
	 * @param   staffData
	 * @param   staffData1
	 * @param staffProfilePicture
	 * @throws  DataManagerException
	 * @purpose This method is generated to update StaffBasicDetais.
	 */
	public void updateBasicDetail(IStaffData staffData, IStaffData staffData1, String actionAlias, StaffProfilePictureData staffProfilePicture) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session); 
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		Timestamp currentDate = new Timestamp((new Date()).getTime());

		if(staffDataManager == null || systemAuditDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());
		}

		try{
			updateBasicDetailValidate(staffData);
			staffData.setLastModifiedDate(currentDate);
			session.beginTransaction();

			staffDataManager.updateBasicDetail(staffData,currentDate);
			staffDataManager.update(staffProfilePicture);
            String transactionId = Long.toString(staffData.getStaffId());
			systemAuditDataManager.updateTbltSystemAudit(staffData1, actionAlias,transactionId);
			session.commit();
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}finally{
			session.close();
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @throws  DataManagerException
	 * @purpose This method is generated to UpdateStaffAccessGroup details.
	 */
	public void updateStaffGroupRoleRelation(IStaffData staffData,IStaffData staffData1, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);

		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(staffDataManager == null || systemAuditDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for"+getClass().getName());
		}

		try {
			session.beginTransaction();

			deleteStaffGroupRoleRelation(staffData,staffDataManager);
			updateStaffGroupRoleRelation(staffData,staffDataManager);
			String transactionId = String.valueOf(staffData.getStaffId());
			systemAuditDataManager.updateTbltSystemAudit(staffData1, actionAlias,transactionId);
			session.commit();
		} catch (DataManagerException exp) {
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage());
		}finally{
			session.close();
		}
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @param   staffDataManager
	 * @throws  DataManagerException
	 * @purpose This method is generated to DeleteStaffAccessGroup.
	 */
	public void deleteStaffGroupRoleRelation(IStaffData staffData,StaffDataManager staffDataManager) throws DataManagerException{
		staffDataManager.deleteStaffGroupRoleRelation((StaffData)staffData);
	}

	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @param   staffDataManager
	 * @throws  DataManagerException
	 * @purpose This method is generated to UpdateStaffAccessGroup.
	 */
	public void updateStaffGroupRoleRelation(IStaffData staffData,StaffDataManager staffDataManager) throws DataManagerException{
		staffDataManager.updateStaffGroupRoleRelation(staffData);
	}




	/**
	 * @author  dhavalraval
	 * @param   staffData
	 * @throws  DataManagerException
	 * @purpose This method is generated to ChangeUserName of StaffData.
	 */
	public void changeUserName(IStaffData staffData,IStaffData staffData1, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		Date currentDate = new Date();

		if(staffDataManager == null || systemAuditDataManager == null){
			throw new DataManagerException("Data Manager implemention not found for "+getClass().getName());
		}
		try{
			changeUserNameValidate(staffData);
			session.beginTransaction();
			staffDataManager.changeUserName(staffData,new Timestamp(currentDate.getTime()));
			String transactionId = Long.toString(staffData.getStaffId());
			systemAuditDataManager.updateTbltSystemAudit(staffData1, actionAlias,transactionId);
			session.commit();
		}catch(DuplicateParameterFoundExcpetion exp){
			session.rollback();
			throw new DuplicateParameterFoundExcpetion("Duplicate User Name. : "+exp.getMessage());
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action Failed :"+exp.getMessage());
		}finally{
			session.close();
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
			exp.printStackTrace();
		}
		return userControlMap;
	} 

	public HashMap getComparedMap(HashMap profileMap,HashMap userControlActionMap){
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
			exp.printStackTrace();
		}
		return userControlActionMap;
	} 

	public Set getActionSets(String staffId) throws DataManagerException{
		AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
		ProfileBLManager profileBLManager = new ProfileBLManager();
		Set actionSet = null; 
		try {
			IStaffData staffData = getStaffData(staffId);

			List<IRoleData> lstStaffRoleRel = getStaffRoleRelList(Long.parseLong(staffId));

			Map<String,IActionData> actionsMap =new HashMap<String,IActionData>();
			List<Long> roleIds = null;
			if(lstStaffRoleRel!=null && lstStaffRoleRel.size()>0){
				roleIds = new ArrayList<Long>();

				for (int i = 0; i < lstStaffRoleRel.size(); i++) {
					IRoleData roleData = (IRoleData)lstStaffRoleRel.get(i);
					roleIds.add(roleData.getRoleId());
				}
				List lstRoleActionRelList = accessGroupBLManager.getRoleActionRelData(roleIds);
				for(int j=0;j<lstRoleActionRelList.size();j++){
					IRoleActionRelData actionRelData =(IRoleActionRelData)lstRoleActionRelList.get(j); 
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
			List lstStaffRoleRel = getStaffRoleRelList(Long.parseLong(staffId));
			SortedSet actionSet = new TreeSet();
			Map<String,IActionData> actionsMap =new HashMap<String,IActionData>();
			List<Long> roleIds = null;
			if(lstStaffRoleRel!=null && lstStaffRoleRel.size()>0){
				roleIds = new ArrayList<Long>();

				for (int i = 0; i < lstStaffRoleRel.size(); i++) {
					IRoleData groupData = (IRoleData)lstStaffRoleRel.get(i);
					roleIds.add(groupData.getRoleId());
				}
				List lstGroupActionRelList = accessGroupBLManager.getRoleActionRelData(roleIds);
				for(int j=0;j<lstGroupActionRelList.size();j++){
					IRoleActionRelData actionRelData =(IRoleActionRelData)lstGroupActionRelList.get(j); 
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
			exp.printStackTrace();
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
		IStaffData staffData = new StaffData();
		try {
		staffData.setStaffId(Long.parseLong(staffId));
		List lstStaff = staffDataManager.getList(staffData);
		if(lstStaff != null && lstStaff.size() >= 1){
			staffData = (IStaffData)lstStaff.get(0);
		}else{
			staffData = null;
		}
		} catch (Exception e) {
			throw new DataManagerException(e);
		}finally{
		session.close();
		}
		return staffData;
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
		if(EliteGenericValidator.isBlankOrNull(staffData.getUserName())){
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
		if(EliteGenericValidator.isBlankOrNull(staffData.getUserName())){
			throw (new DataValidationException("Invalid Staff UserName",(MODULE+"."+"UserName").toLowerCase()));
		}
		/*
        // StatusChangeDate
        if(EliteGenericValidator.isBlankOrNull(staffData.getStatusChangeDate())){
        	throw (new DataValidationException("Invalid Staff StatusChangeDate",(MODULE+"."+"StatusChangeDate").toLowerCase()));
        }
		 */

	}
	public void changePasswordValidate(Long staffId, String oldPassword, String newPassword) throws DataManagerException {
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
			List<IRoleData> lstStaffRoleRel = getStaffRoleRelList(Long.parseLong(staffId));

			Map<String,IActionData> actionsMap =new HashMap<String,IActionData>();
			List<Long> roleIds = null;
			if(lstStaffRoleRel!=null && lstStaffRoleRel.size()>0){
				roleIds = new ArrayList<Long>();

				for (int i = 0; i < lstStaffRoleRel.size(); i++) {
					IRoleData groupData = (IRoleData)lstStaffRoleRel.get(i);
					roleIds.add(groupData.getRoleId());
				}
				List lstGroupActionRelList = accessGroupBLManager.getRoleActionRelData(roleIds);
				for(int j=0;j<lstGroupActionRelList.size();j++){
					IRoleActionRelData actionRelData =(IRoleActionRelData)lstGroupActionRelList.get(j); 
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
			session.close();
		}
		return lastModifiedDate;
	}

	public void create(StaffProfilePictureData staffProfilePicture) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		Date lastModifiedDate = null;
		if(staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			staffDataManager.create(staffProfilePicture);
			session.commit();
		}catch(Exception exp){
			Logger.logError(MODULE,"failed to save profile picture for staff with id :" +staffProfilePicture.getId());
			Logger.logTrace(MODULE,exp);
			session.rollback();
		}finally {
			session.close();
		}
	}

	public Map<String, RoleData> getGroupIdVsRoleMap(IStaffData staffData) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Method called getGroupIdVsRoleMap()");
		}

		Map<String, RoleData> groupIdVsStaffGroupRoleRelMap = Maps.newHashMap();
		try {

			List<StaffGroupRoleRelData> staffGroupRoleRelDatas = getStaffGroupRoleRelList(staffData.getStaffId());
			for (StaffGroupRoleRelData staffGroupRoleRelData : staffGroupRoleRelDatas) {
				groupIdVsStaffGroupRoleRelMap.put(staffGroupRoleRelData.getGroupData().getId(), staffGroupRoleRelData.getRoleData());
			}

		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while fetching staff-Group-role-actions. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return groupIdVsStaffGroupRoleRelMap;
	}

	public void update(StaffProfilePictureData staffProfilePicture) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		if(staffDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		try{
			session.beginTransaction();
			staffDataManager.update(staffProfilePicture);
			session.commit();
		}catch(Exception exp){
			Logger.logError(MODULE,"failed to save profile picture for staff with id :" +staffProfilePicture.getId());
			Logger.logTrace(MODULE,exp);
			session.rollback();
		}finally {
			session.close();
		}
	}

	public StaffProfilePictureData getStaffProfilePicture(Long staffId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		StaffDataManager staffDataManager = getStaffDataManager(session);
		try{
			return staffDataManager.getStaffProfilePicture(staffId);
		}catch(Exception e){
			Logger.logError(MODULE, "Error getting profile picture with staff id :" +staffId);
			Logger.logTrace(MODULE,e);
		}finally{
			session.close();
		}
		return null;
	}

}
