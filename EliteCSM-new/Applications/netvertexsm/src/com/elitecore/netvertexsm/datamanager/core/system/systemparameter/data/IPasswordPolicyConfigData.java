package com.elitecore.netvertexsm.datamanager.core.system.systemparameter.data;


public interface IPasswordPolicyConfigData {
	
	public long getPasswordPolicyId();
	public void setPasswordPolicyId(long passwordPolicyId);
	public String getPasswordRange();
	public void setPasswordRange(String passwordRange);
	public Integer getAlphabetRange();
	public void setAlphabetRange(Integer alphabetRange);
	public Integer getDigitsRange();
	public void setDigitsRange(Integer digitsRange);
	public Integer getSpecialCharRange();
	public void setSpecialCharRange(Integer specialCharRange);
	public String getProhibitedChars();
	public void setProhibitedChars(String prohibitedChars);
	public Integer getPasswordValidity();
	public void setPasswordValidity(Integer passwordValidity);

}
