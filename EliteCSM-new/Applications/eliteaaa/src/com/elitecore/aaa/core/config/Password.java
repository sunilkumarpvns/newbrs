package com.elitecore.aaa.core.config;


import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.passwordutil.PasswordEncryption;

/**
 *
 * @author narendra.pathai
 *
 */
@XmlTransient
public class Password {

	private String password;

	public Password() {
		//Required for JAXB
	}
	
	public Password(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}

	public String getPlainTextPassword(){
		try {
			return PasswordEncryption.getInstance().decrypt(password, PasswordEncryption.ELITE_PASSWORD_CRYPT);
		} catch (Exception e) {
			//this should not occur in normal scenarios, so returning th
			return password;
		}
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
