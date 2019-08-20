package com.elitecore.netvertexsm.hibernate.core.system.systemparameter;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.PasswordPolicyDataManager;
import com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data.PasswordPolicyConfigData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;
import com.elitecore.netvertexsm.util.logger.Logger;

public class HPasswordPolicyDataManager extends HBaseDataManager implements PasswordPolicyDataManager{
    	private static final String HPPDM = "HPPDM";
    
	private static final String 	passwordRange 	 = "5-50";
	private static final Integer 	alphabetRange 	 = 1;
	private static final Integer	digitsRange 	 = 1;
	private static final Integer 	specialCharRange =  1;
 
	private static final String 	prohibitedChars  = "$@";
	private static final Integer 	passwordValidity = 180;
	private static final String 	changePwdOnFirstLogin = "True";
	private static final Integer 	totalHistoricalPasswords = 10;

	public void updatePolicyDetail(PasswordPolicyConfigData passwordPolicyConfigData) throws DataManagerException{
		Session session = getSession();
		try {
			if(passwordPolicyConfigData !=null){
				session.update(passwordPolicyConfigData);
			}else{
				throw new DataManagerException();
			}
		 }catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	public PasswordPolicyConfigData viewPasswordSelectionPolicy() throws DataManagerException{
		List parameterList = null;
		PasswordPolicyConfigData passwordPolicyConfigData = null;
		boolean policyFlag = false;
		Session session = getSession();
		try {
			Criteria criteria = session.createCriteria(PasswordPolicyConfigData.class);
			parameterList = criteria.list();
			passwordPolicyConfigData=(PasswordPolicyConfigData) parameterList.get(0);
			policyFlag = isValidPasswordPolicy(passwordPolicyConfigData);			 
		} catch (HibernateException hExp) {			
			Logger.logError(HPPDM, "Error while fetching Password Policy. Reason:" + hExp.getMessage());			
		} catch (Exception exp) {
			Logger.logError(HPPDM, "Exception while fetching Password Policy. Reason:" + exp.getMessage());			
		} finally {

		    	if(policyFlag==false){
		    	    passwordPolicyConfigData.setDefaultPasswordPolicy(true);
		    	}
		}
		
		return passwordPolicyConfigData;
	}	
	
	public PasswordPolicyConfigData getPasswordSelectionPolicy() throws DataManagerException{
		List parameterList = null;
		PasswordPolicyConfigData passwordPolicyConfigData = null;
		boolean policyFlag = false;

		Session session =null;
		try {
			session=getSession();
			Criteria criteria = session.createCriteria(PasswordPolicyConfigData.class);
			parameterList = criteria.list();
			passwordPolicyConfigData=(PasswordPolicyConfigData) parameterList.get(0);
			policyFlag = isValidPasswordPolicy(passwordPolicyConfigData);
			
		} catch (HibernateException hExp) {			
			Logger.logError(HPPDM, "Error while fetching Password Policy. Reason:" + hExp.getMessage());
			policyFlag = false;			
			
		} catch (Exception exp) {
			Logger.logError(HPPDM, "Exception while fetching Password Policy. Reason:" + exp.getMessage());
			policyFlag = false;	
			
		} finally{
			
			if( policyFlag == false ){
				Logger.logWarn(HPPDM, "Taking Default Password Policy");
				passwordPolicyConfigData =  new PasswordPolicyConfigData();
 				setDefaultPasswordPolicy(passwordPolicyConfigData);
			}
		}
		return passwordPolicyConfigData;
	}

	private void setDefaultPasswordPolicy(PasswordPolicyConfigData passwordPolicyConfigData) {
	    passwordPolicyConfigData.setPasswordRange(passwordRange);
	    passwordPolicyConfigData.setAlphabetRange(alphabetRange);
	    passwordPolicyConfigData.setDigitsRange(digitsRange);
	    passwordPolicyConfigData.setSpecialCharRange(specialCharRange);
	    passwordPolicyConfigData.setProhibitedChars(prohibitedChars);
	    passwordPolicyConfigData.setPasswordValidity(passwordValidity);
	    passwordPolicyConfigData.setTotalHistoricalPasswords(totalHistoricalPasswords);
	    passwordPolicyConfigData.setChangePwdOnFirstLogin(changePwdOnFirstLogin);
	    passwordPolicyConfigData.setDefaultPasswordPolicy(true);
	}
			
	
	private boolean isValidPasswordPolicy(PasswordPolicyConfigData passwordPolicyConfigData){
		Logger.logDebug(HPPDM, "Method called isValidPasswordPolicy()");
		if(passwordPolicyConfigData==null){
			Logger.logError(HPPDM, "Empty password Policy");
			return false;
		}
		
		if( passwordPolicyConfigData.getPasswordRange()==null || passwordPolicyConfigData.getPasswordRange().trim().length()==0){
			Logger.logError(HPPDM, "Empty Password Range");
			return false;
		}
		
		if(passwordPolicyConfigData.getTotalHistoricalPasswords()==null || passwordPolicyConfigData.getTotalHistoricalPasswords() > 10){
			Logger.logError(HPPDM, "Historical Passwords exceeds max value");
			return false;
		}
		
		boolean checkValid=true;		
	    if(passwordPolicyConfigData.isAlphabetCheckReq() == true || passwordPolicyConfigData.isDigitCheckReq() == true
	    		|| passwordPolicyConfigData.isLengthCheckReq()== true || passwordPolicyConfigData.isSpecialCharCheckReq() == true){
	    	
	    	if(passwordPolicyConfigData.getPasswordRange() != null){
	    		if(passwordPolicyConfigData.getMinPasswordLength() < 5 ){
	    			Logger.logError(HPPDM, "Minimum password length value must be at least Five(5)");
					return checkValid=false;
	    		}
	    		
	    		if(passwordPolicyConfigData.getMaxPasswordLength() > 50){
	    			Logger.logError(HPPDM, "Maximum password length value must be less than or equal to Fifty(50)");
					return checkValid=false;
	    		}
	    		
	    		if(passwordPolicyConfigData.getMinPasswordLength() > passwordPolicyConfigData.getMaxPasswordLength()){
	    			Logger.logError(HPPDM, "Invalid password Range");
					return checkValid=false;
	    		}
	    	}
	    	
	    	int totalAlphabets = 0;
	    	
	    	if(passwordPolicyConfigData.getAlphabetRange() != null){
	    		totalAlphabets = passwordPolicyConfigData.getAlphabetRange();
	    		if(passwordPolicyConfigData.getAlphabetRange()<0 || passwordPolicyConfigData.getAlphabetRange() > passwordPolicyConfigData.getMaxPasswordLength()){
	    			Logger.logError(HPPDM, "Invalid Required Alphabets");
					return checkValid=false;
	    		}
	    	}
	    	
	    	int totalDigits = 0;
	    	if(passwordPolicyConfigData.getDigitsRange() != null){
	    		totalDigits = passwordPolicyConfigData.getDigitsRange();
	    		if(passwordPolicyConfigData.getDigitsRange()<0 || passwordPolicyConfigData.getDigitsRange() > passwordPolicyConfigData.getMaxPasswordLength()){
	    			Logger.logError(HPPDM, "Invalid Required Digits");
	    			return checkValid=false;
	    		}
	    	}
	    	
	    	int totalSpecialChars = 0;
	    	if(passwordPolicyConfigData.getSpecialCharRange() != null){

	    		totalSpecialChars = passwordPolicyConfigData.getSpecialCharRange();
	    		if(passwordPolicyConfigData.getSpecialCharRange()<0 || passwordPolicyConfigData.getSpecialCharRange() > passwordPolicyConfigData.getMaxPasswordLength()){
	    			Logger.logError(HPPDM, "Invalid Required Special Characters");
	    			return checkValid=false;
 
	    		}
	    	}	    	
	    	
	    	int totalAll = totalAlphabets + totalDigits + totalSpecialChars;
	    	if(totalAll > passwordPolicyConfigData.getMinPasswordLength()){
	    		Logger.logError(HPPDM, "Total of Require Alphabets, Digits and Special characters should not be greater than min password length");
			return checkValid=false;
	    	}
	    	
	    }else{
	    	checkValid=true;
	    }
	   return checkValid;
	}

	
}
