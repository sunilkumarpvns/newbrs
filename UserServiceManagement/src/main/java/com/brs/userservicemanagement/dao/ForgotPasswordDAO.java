package com.brs.userservicemanagement.dao;

import com.brs.userservicemanagement.entity.LoginDetails;
import com.brs.userservicemanagement.entity.UserPasswordLinks;

public interface ForgotPasswordDAO {
	public LoginDetails findByEmail(String email);
	public Integer saveDynamicURL(UserPasswordLinks userPasswordLinks);
	public UserPasswordLinks verifyDynamicURL(String verificationURL);

}
