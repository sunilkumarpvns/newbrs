package com.elitecore.corenetvertex.pkg.systemparameter;


public interface IPasswordPolicyConfigData {
	
	public String getId();
	public void setId(String id);
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
