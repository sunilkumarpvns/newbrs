package com.elitecore.elitesm.datamanager.core.system.systemparameter.data;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

import net.sf.json.JSONObject;


public class PasswordPolicyConfigData extends BaseData implements IPasswordPolicyConfigData, Differentiable{
	private String passwordPolicyId;
	private String passwordRange;
	private String alphabetRange;
	private String digitsRange;
	private String specialCharRange;
	private String prohibitedChars;
	private Integer passwordValidity;
	private String changePwdOnFirstLogin;
	// Min-Max Range Parameters

	private Integer minPasswordLength;
	private Integer maxPasswordLength;
	private Integer minAlphabates;
	private Integer maxAlphabates;
	private Integer minDigists;
	private Integer maxDigits;
	private Integer minSpecialChars;
	private Integer maxSpecialChars;

	// Min-Max Range Validation

	private boolean isLengthCheckReq = true;
	private boolean isAlphabetCheckReq = true;
	private boolean isDigitCheckReq = true;
	private boolean isSpecialCharCheckReq = true;
	
	private int maxHistoricalPasswords;
	
	public String getPasswordRange() {
		return passwordRange;
	}
	public void setPasswordRange(String passwordRange) {
		this.passwordRange = passwordRange;
		if(passwordRange != null && passwordRange.length() > 0){
			String[] tokens = passwordRange.split("-");
			if(tokens != null && tokens.length == 2){
				try{
					setMinPasswordLength(Integer.valueOf(tokens[0]));
					setMaxPasswordLength(Integer.valueOf(tokens[1]));
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
	public String getAlphabetRange() {
		return alphabetRange;
	}
	public void setAlphabetRange(String alphabetRange) {
		this.alphabetRange = alphabetRange;
		if(alphabetRange != null && alphabetRange.length() > 0){
			String[] tokens = alphabetRange.split("-");
			if(tokens != null && tokens.length == 2){
				try{
					setMinAlphabates(Integer.parseInt(tokens[0]));
					setMaxAlphabates(Integer.parseInt(tokens[1]));
				}catch (NumberFormatException e) {
					isAlphabetCheckReq = false;
				}catch (Exception e) {
					isAlphabetCheckReq = false;
				}
			}
		}else{
			isAlphabetCheckReq = false;
		}
	}
	public String getDigitsRange() {
		return digitsRange;
	}
	public void setDigitsRange(String digitsRange) {
		this.digitsRange = digitsRange;
		if(digitsRange != null && digitsRange.length() > 0){
			String[] tokens = digitsRange.split("-");
			if(tokens != null && tokens.length == 2){
				try{
					setMinDigists(Integer.parseInt(tokens[0]));
					setMaxDigits(Integer.parseInt(tokens[1]));
				}catch (NumberFormatException e) {
					isDigitCheckReq = false;
				}catch (Exception e) {
					isDigitCheckReq = false;
				}
			}
		}else{
			isDigitCheckReq = false;
		}
	}
	public String getSpecialCharRange() {
		return specialCharRange;
	}
	public void setSpecialCharRange(String specialCharRange) {
		this.specialCharRange = specialCharRange;
		if(specialCharRange != null && specialCharRange.length() > 0){
			String[] tokens = specialCharRange.split("-");
			if(tokens != null && tokens.length == 2){
				try{
					setMinSpecialChars(Integer.parseInt(tokens[0]));
					setMaxSpecialChars(Integer.parseInt(tokens[1]));
				}catch (NumberFormatException e) {
					isSpecialCharCheckReq = false;
				}catch (Exception e) {
					isSpecialCharCheckReq = false;
				}
			}
		}else{
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
	public Integer getMinAlphabates() {
		return minAlphabates;
	}
	public void setMinAlphabates(Integer minAlphabates) {
		this.minAlphabates = minAlphabates;
	}
	public Integer getMaxAlphabates() {
		return maxAlphabates;
	}
	public void setMaxAlphabates(Integer maxAlphabates) {
		this.maxAlphabates = maxAlphabates;
	}
	public Integer getMinDigits() {
		return minDigists;
	}
	public void setMinDigists(Integer minDigists) {
		this.minDigists = minDigists;
	}
	public Integer getMaxDigits() {
		return maxDigits;
	}
	public void setMaxDigits(Integer maxDigits) {
		this.maxDigits = maxDigits;
	}
	public Integer getMinSpecialChars() {
		return minSpecialChars;
	}
	public void setMinSpecialChars(Integer minSpecialChars) {
		this.minSpecialChars = minSpecialChars;
	}
	public Integer getMaxSpecialChars() {
		return maxSpecialChars;
	}
	public void setMaxSpecialChars(Integer maxSpecialChars) {
		this.maxSpecialChars = maxSpecialChars;
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
	public String getPasswordPolicyId() {
		return passwordPolicyId;
	}
	public void setPasswordPolicyId(String passwordPolicyId) {
		this.passwordPolicyId = passwordPolicyId;
	}
	public String getChangePwdOnFirstLogin() {
		return changePwdOnFirstLogin;
	}
	public void setChangePwdOnFirstLogin(String changePwdOnFirstLogin) {
		this.changePwdOnFirstLogin = changePwdOnFirstLogin;
	}
	public int getMaxHistoricalPasswords() {
		return maxHistoricalPasswords;
	}
	public void setMaxHistoricalPasswords(int maxHistoricalPasswords) {
		this.maxHistoricalPasswords = maxHistoricalPasswords;
	}

	@Override
	public JSONObject toJson() {
		JSONObject outterObject = new JSONObject();
		JSONObject innerObject = new JSONObject();
	
			innerObject.put("Password Range", this.passwordRange);
			innerObject.put("Alphabet Range", this.alphabetRange);
			innerObject.put("Change Password On First Login", this.changePwdOnFirstLogin);
			innerObject.put("Digits Range", this.digitsRange);
			innerObject.put("Maximum Historical Password", this.maxHistoricalPasswords);
			innerObject.put("Password Validity", this.passwordValidity);
			innerObject.put("Prohibited Character", this.prohibitedChars);
			innerObject.put("Special Character Range", this.specialCharRange);
			outterObject.put("Password Selection Policy", innerObject);
		
		return outterObject;
	}
}