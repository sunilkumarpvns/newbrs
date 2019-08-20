package com.elitecore.elitesm.web.radius.utilities.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class VerifyPasswordForm extends BaseWebForm {
	String encryptType = "PAP";
	String sharedSecret = null;
	String reqAuthenticator = null;
	String chapChallenge = null;
	String msgPassword = "";
	String chapPassword = null;
	String userPassword = null;
	
	/*Digest parameters */
	String digestRealm =null;
	String digestNonce = null;
	String digestMethod = null;
	String digestUri = null;
	String digestQoP = null;
	String digestAlgorithm = null;
	String digestBody = null;
	String digestCNonce = null;
	String digestNonceCount =null;
	String digestUserName = null;
	
	
	public String getEncryptType() {
		return encryptType;
	}
	public void setEncryptType(String encryptType) {
		this.encryptType = encryptType;
	}
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	public String getReqAuthenticator() {
		return reqAuthenticator;
	}
	public void setReqAuthenticator(String reqAuthenticator) {
		this.reqAuthenticator = reqAuthenticator;
	}
	public String getChapChallenge() {
		return chapChallenge;
	}
	public void setChapChallenge(String chapChallenge) {
		this.chapChallenge = chapChallenge;
	}
	public String getMsgPassword() {
		return msgPassword;
	}
	public void setMsgPassword(String msgPassword) {
		this.msgPassword = msgPassword;
	}
	public String getChapPassword() {
		return chapPassword;
	}
	public void setChapPassword(String chapPassword) {
		this.chapPassword = chapPassword;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getDigestRealm() {
		return digestRealm;
	}
	public void setDigestRealm(String digestRealm) {
		this.digestRealm = digestRealm;
	}
	public String getDigestNonce() {
		return digestNonce;
	}
	public void setDigestNonce(String digestNonce) {
		this.digestNonce = digestNonce;
	}
	public String getDigestMethod() {
		return digestMethod;
	}
	public void setDigestMethod(String digestMethod) {
		this.digestMethod = digestMethod;
	}
	public String getDigestUri() {
		return digestUri;
	}
	public void setDigestUri(String digestUri) {
		this.digestUri = digestUri;
	}
	public String getDigestQoP() {
		return digestQoP;
	}
	public void setDigestQoP(String digestQoP) {
		this.digestQoP = digestQoP;
	}
	public String getDigestAlgorithm() {
		return digestAlgorithm;
	}
	public void setDigestAlgorithm(String digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}
	public String getDigestBody() {
		return digestBody;
	}
	public void setDigestBody(String digestBody) {
		this.digestBody = digestBody;
	}
	public String getDigestCNonce() {
		return digestCNonce;
	}
	public void setDigestCNonce(String digestCNonce) {
		this.digestCNonce = digestCNonce;
	}
	public String getDigestNonceCount() {
		return digestNonceCount;
	}
	public void setDigestNonceCount(String digestNonceCount) {
		this.digestNonceCount = digestNonceCount;
	}
	public String getDigestUserName() {
		return digestUserName;
	}
	public void setDigestUserName(String digestUserName) {
		this.digestUserName = digestUserName;
	}
}
