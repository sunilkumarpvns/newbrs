package com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data;

import com.elitecore.netvertexsm.datamanager.core.base.data.BaseData;





public class PasswordPolicyConfigData extends BaseData implements IPasswordPolicyConfigData{
	private static final String MODULE = "PASSWORD-POLICY-CONFIG-DATA";
	private long passwordPolicyId;
	private String passwordRange;
	private Integer alphabetRange;
	private Integer digitsRange;
	private Integer specialCharRange;
	private String prohibitedChars;
	private Integer passwordValidity;
	private String changePwdOnFirstLogin;
	// Min-Max Range Parameters

	private Integer minPasswordLength;
	private Integer maxPasswordLength;
 
	private Integer totalHistoricalPasswords;

	// Min-Max Range Validation

	private boolean isLengthCheckReq = true;
	private boolean isAlphabetCheckReq = true;
	private boolean isDigitCheckReq = true;
	private boolean isSpecialCharCheckReq = true;
	
	private boolean isDefaultPasswordPolicy = false;
	
	public String getPasswordRange() {
		return passwordRange;
	}
	public void setPasswordRange(String passwordRange) {
		this.passwordRange = passwordRange;
		if(passwordRange != null && passwordRange.length() > 0){
			this.passwordRange = passwordRange.replace(" ", "");
			String[] tokens = passwordRange.split("-");
			if(tokens != null && tokens.length == 2){
				try{
					setMinPasswordLength(Integer.valueOf(tokens[0].trim()));
					setMaxPasswordLength(Integer.valueOf(tokens[1].trim()));
					this.passwordRange = String.valueOf(getMinPasswordLength())+"-"+String.valueOf(getMaxPasswordLength());
				}catch (NumberFormatException e) {
					isLengthCheckReq = false;
				}catch (Exception e) {
					isLengthCheckReq = false;
				}				
			}
		}else{
			isLengthCheckReq = false;
		}
	}
	public Integer getAlphabetRange() {
		return alphabetRange;
	}
	public void setAlphabetRange(java.lang.Integer alphabetRange) {
		this.alphabetRange = alphabetRange;
 
		if(alphabetRange != null && alphabetRange < 0){			 				
			isAlphabetCheckReq = false;				
		} 
 
	}
	
	public Integer getDigitsRange() {
		return digitsRange;
	}
	
	public void setDigitsRange(Integer digitsRange) {
		this.digitsRange = digitsRange;
		 if(digitsRange!=null && digitsRange<0){ 
			isDigitCheckReq = false;
		}
	}
	public Integer getSpecialCharRange() {
		return specialCharRange;
	}
	public void setSpecialCharRange(Integer specialCharRange) {
		this.specialCharRange = specialCharRange;

		if(specialCharRange!=null && specialCharRange<0){
			isSpecialCharCheckReq = false;
		}
	}
	public String getProhibitedChars() {
		return prohibitedChars;
	}
	public void setProhibitedChars(String prohibitedChars) {
		this.prohibitedChars = prohibitedChars;
	}
	public Integer getPasswordValidity() {
		return passwordValidity;
	}
	public void setPasswordValidity(Integer passwordValidity) {
		this.passwordValidity = passwordValidity;
	}
	public Integer getMinPasswordLength() {
		return minPasswordLength;
	}
	public void setMinPasswordLength(Integer minPasswordLength) {
		this.minPasswordLength = minPasswordLength;
	}
	public Integer getMaxPasswordLength() {
		return maxPasswordLength;
	}
	public void setMaxPasswordLength(Integer maxPasswordLength) {
		this.maxPasswordLength = maxPasswordLength;
	}	 
 
	public boolean isLengthCheckReq() {
		return isLengthCheckReq;
	}
	public void setLengthCheckReq(boolean isLengthCheckReq) {
		this.isLengthCheckReq = isLengthCheckReq;
	}
	public boolean isAlphabetCheckReq() {
		return isAlphabetCheckReq;
	}
	public void setAlphabetCheckReq(boolean isAlphabetCheckReq) {
		this.isAlphabetCheckReq = isAlphabetCheckReq;
	}
	public boolean isDigitCheckReq() {
		return isDigitCheckReq;
	}
	public void setDigitCheckReq(boolean isDigitCheckReq) {
		this.isDigitCheckReq = isDigitCheckReq;
	}
	public boolean isSpecialCharCheckReq() {
		return isSpecialCharCheckReq;
	}
	public void setSpecialCharCheckReq(boolean isSpecialCharCheckReq) {
		this.isSpecialCharCheckReq = isSpecialCharCheckReq;
	}
	public long getPasswordPolicyId() {
		return passwordPolicyId;
	}
	public void setPasswordPolicyId(long passwordPolicyId) {
		this.passwordPolicyId = passwordPolicyId;
	}
	public String getChangePwdOnFirstLogin() {
		return changePwdOnFirstLogin;
	}
	public void setChangePwdOnFirstLogin(String changePwdOnFirstLogin) {
		this.changePwdOnFirstLogin = changePwdOnFirstLogin;
	}
	public Integer getTotalHistoricalPasswords() {
		return totalHistoricalPasswords;
	}
	public void setTotalHistoricalPasswords(Integer totalHistoricalPasswords) {
		this.totalHistoricalPasswords = totalHistoricalPasswords;
	}
	public boolean isDefaultPasswordPolicy() {
		return isDefaultPasswordPolicy;
	}
	public void setDefaultPasswordPolicy(boolean isDefaultPasswordPolicy) {
		this.isDefaultPasswordPolicy = isDefaultPasswordPolicy;
	}
}