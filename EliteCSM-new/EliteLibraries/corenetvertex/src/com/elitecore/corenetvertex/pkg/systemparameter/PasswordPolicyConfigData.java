package com.elitecore.corenetvertex.pkg.systemparameter;


import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity(name="com.elitecore.corenetvertex.pkg.systemparameter.PasswordPolicyConfigData")
@Table(name="TBLM_PASSWORD_POLICY")
/**
 * For password policy related changes, configurations are provided in netvertexsm.
 * There is requirement of reading these information for displaying password related information in staff profile view page.
 * So this class is created.
 *
 */
public class PasswordPolicyConfigData extends DefaultGroupResourceData implements IPasswordPolicyConfigData, Serializable{
	private String passwordRange;
	private Integer alphabetRange;
	private Integer digitsRange;
	private Integer specialCharRange;
	private String prohibitedChars;
	private Integer passwordValidity;
	private boolean changePwdOnFirstLogin = true;
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

	@Column(name="PASSWORD_RANGE")
	public String getPasswordRange() {
		return passwordRange;
	}
	public void setPasswordRange(String passwordRange) {
		this.passwordRange = passwordRange;
		if(Strings.isNullOrBlank(passwordRange) == false){
			this.passwordRange = passwordRange.replace(" ", "");
			String[] tokens = passwordRange.split("-");
			if(tokens != null && tokens.length == 2){
				try{
					setMinPasswordLength(Integer.valueOf(tokens[0].trim()));
					setMaxPasswordLength(Integer.valueOf(tokens[1].trim()));
					this.passwordRange = String.valueOf(getMinPasswordLength())+"-"+getMaxPasswordLength();
				}catch (NumberFormatException e) {
					this.isLengthCheckReq = false;
				}catch (Exception e) { //NOSONAR - Reason: Exception handlers should preserve the original exceptions
					this.isLengthCheckReq = false;
				}				
			} else {
				this.isLengthCheckReq = false;
			}
		}else{
			isLengthCheckReq = false;
		}
	}
	@Column(name="ALPHABET_RANGE")
	public Integer getAlphabetRange() {
		return alphabetRange;
	}
	public void setAlphabetRange(Integer alphabetRange) {
		this.alphabetRange = alphabetRange;
 
		if(alphabetRange != null && alphabetRange < 0){			 				
			isAlphabetCheckReq = false;				
		} 
 
	}

	@Column(name="DIGIT_RANGE")
	public Integer getDigitsRange() {
		return digitsRange;
	}
	
	public void setDigitsRange(Integer digitsRange) {
		this.digitsRange = digitsRange;
		 if(digitsRange!=null && digitsRange<0){ 
			isDigitCheckReq = false;
		}
	}
	@Column(name="SPECIAL_CHAR_RANGE")
	public Integer getSpecialCharRange() {
		return specialCharRange;
	}
	public void setSpecialCharRange(Integer specialCharRange) {
		this.specialCharRange = specialCharRange;

		if(specialCharRange!=null && specialCharRange<0){
			isSpecialCharCheckReq = false;
		}
	}
	@Column(name="PROHIBITED_CHARS")
	public String getProhibitedChars() {
		return prohibitedChars;
	}
	public void setProhibitedChars(String prohibitedChars) {
		this.prohibitedChars = prohibitedChars;
	}

	@Column(name="PASSWORD_VALIDITY")
	public Integer getPasswordValidity() {
		return passwordValidity;
	}
	public void setPasswordValidity(Integer passwordValidity) {
		this.passwordValidity = passwordValidity;
	}
	public void setMinPasswordLength(Integer minPasswordLength) {
		this.minPasswordLength = minPasswordLength;
	}

	@Transient
	@JsonIgnore
	public Integer getMaxPasswordLength() {
		return maxPasswordLength;
	}
	public void setMaxPasswordLength(Integer maxPasswordLength) {
		this.maxPasswordLength = maxPasswordLength;
	}

	@Transient
	@JsonIgnore
	public boolean isLengthCheckReq() {
		return isLengthCheckReq;
	}

	public void setLengthCheckReq(boolean isLengthCheckReq) {
		this.isLengthCheckReq = isLengthCheckReq;
	}

	@Transient
	@JsonIgnore
	public boolean isAlphabetCheckReq() {
		return isAlphabetCheckReq;
	}

	public void setAlphabetCheckReq(boolean isAlphabetCheckReq) {
		this.isAlphabetCheckReq = isAlphabetCheckReq;
	}

	@Transient
	@JsonIgnore
	public boolean isDigitCheckReq() {
		return isDigitCheckReq;
	}

	public void setDigitCheckReq(boolean isDigitCheckReq) {
		this.isDigitCheckReq = isDigitCheckReq;
	}

	@Transient
	@JsonIgnore
	public boolean isSpecialCharCheckReq() {
		return isSpecialCharCheckReq;
	}
	public void setSpecialCharCheckReq(boolean isSpecialCharCheckReq) {
		this.isSpecialCharCheckReq = isSpecialCharCheckReq;
	}

	@Column(name="CHANGE_PWD_ON_FIRST_LOGIN")
	public boolean getChangePwdOnFirstLogin() {
		return changePwdOnFirstLogin;
	}

	public void setChangePwdOnFirstLogin(boolean changePwdOnFirstLogin) {
		this.changePwdOnFirstLogin = changePwdOnFirstLogin;
	}

	@Transient
	@JsonIgnore
	public Integer getMinPasswordLength() {
		return minPasswordLength;
	}

	@Column(name="HISTORICAL_PASSWORDS")
	public Integer getTotalHistoricalPasswords() {
		return totalHistoricalPasswords;
	}
	public void setTotalHistoricalPasswords(Integer totalHistoricalPasswords) {
		this.totalHistoricalPasswords = totalHistoricalPasswords;
	}

	@Transient
	@JsonIgnore
	public boolean isDefaultPasswordPolicy() {
		return isDefaultPasswordPolicy;
	}
	public void setDefaultPasswordPolicy(boolean isDefaultPasswordPolicy) {
		this.isDefaultPasswordPolicy = isDefaultPasswordPolicy;
	}

	@Override
	public JsonObject toJson(){
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("Min-Max Password Length", passwordRange);
		jsonObject.addProperty("Required Alphabets", alphabetRange);
		jsonObject.addProperty("Required Digits", digitsRange);
		jsonObject.addProperty("Required Special Characters", specialCharRange);
		jsonObject.addProperty("Prohibited Characters", Strings.isNullOrBlank(prohibitedChars)?null:prohibitedChars);
		jsonObject.addProperty("Password Validity", passwordValidity);
		jsonObject.addProperty("Change Password On First Login", changePwdOnFirstLogin);
		jsonObject.addProperty("Number of Historical Passwords", totalHistoricalPasswords);
		return jsonObject;
	}

	@Override
	@Transient
	public String getResourceName() {
		return "PasswordPolicy";
	}
}