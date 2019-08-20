package com.elitecore.elitesm.datamanager.core.system.systemparameter.data;


public interface IPasswordPolicyConfigData {
	
	public String getPasswordPolicyId();
	public void setPasswordPolicyId(String passwordPolicyId);
	public String getPasswordRange();
	public void setPasswordRange(String passwordRange);
    public String getAlphabetRange();
	public void setAlphabetRange(String alphabetRange);
	public String getDigitsRange();
	public void setDigitsRange(String digitsRange);
	public String getSpecialCharRange();
	public void setSpecialCharRange(String specialCharRange);
	public String getProhibitedChars();
	public void setProhibitedChars(String prohibitedChars);
	public Integer getPasswordValidity();
	public void setPasswordValidity(Integer passwordValidity);

}
