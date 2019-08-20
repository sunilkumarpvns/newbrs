package com.elitecore.aaa.statistics.radius.auth;

import com.elitecore.aaa.statistics.StatisticsData;

public class RadAuthRejectReasonsData  extends StatisticsData{

	private long userNotFound;
	private long invalidPassword;
	private long invalidCHAPPassword;
	private long invalidMSCHAPv1Password;
	private long invalidMSCHAPv2Password;
	private long eapFailure;
	private long authenticationFailed;
	private long accountIsNotActive;
	private long accountExpired;
	private long creditLimitExceeded;
	private long digestFailure;
	private long invalidDigestPassword;
	private long rmCommTimeOut;
	

	public long getUserNotFound() {
		return userNotFound;
	}
	public void setUserNotFound(long userNotFound) {
		this.userNotFound = userNotFound;
		setChanged();
	}
	public long getInvalidPassword() {
		return invalidPassword;
	}
	public void setInvalidPassword(long invalidPassword) {
		this.invalidPassword = invalidPassword;
		setChanged();
	}
	public long getInvalidCHAPPassword() {
		return invalidCHAPPassword;
	}
	public void setInvalidCHAPPassword(long invalidCHAPPassword) {
		this.invalidCHAPPassword = invalidCHAPPassword;
		setChanged();
	}
	public long getInvalidMSCHAPv1Password() {
		return invalidMSCHAPv1Password;
	}
	public void setInvalidMSCHAPv1Password(long invalidMSCHAPv1Password) {
		this.invalidMSCHAPv1Password = invalidMSCHAPv1Password;
		setChanged();
	}
	public long getInvalidMSCHAPv2Password() {
		return invalidMSCHAPv2Password;
	}
	public void setInvalidMSCHAPv2Password(long invalidMSCHAPv2Password) {
		this.invalidMSCHAPv2Password = invalidMSCHAPv2Password;
		setChanged();
	}
	
	public long getEapFailure() {
		return eapFailure;
	}
	public void setEapFailure(long eapFailure) {
		this.eapFailure = eapFailure;
		setChanged();
	}
	public long getAuthenticationFailed() {
		return authenticationFailed;
	}
	public void setAuthenticationFailed(long authenticationFailed) {
		this.authenticationFailed = authenticationFailed;
		setChanged();
	}
	public long getAccountIsNotActive() {
		return accountIsNotActive;
	}
	public void setAccountIsNotActive(long accountIsNotActive) {
		this.accountIsNotActive = accountIsNotActive;
		setChanged();
	}
	public long getAccountExpired() {
		return accountExpired;
	}
	public void setAccountExpired(long accountExpired) {
		this.accountExpired = accountExpired;
		setChanged();
	}
	public long getCreditLimitExceeded() {
		return creditLimitExceeded;
	}
	public void setCreditLimitExceeded(long creditLimitExceeded) {
		this.creditLimitExceeded = creditLimitExceeded;
		setChanged();
	}
	public long getDigestFailure() {
		return digestFailure;
	}
	public void setDigestFailure(long digestFailure) {
		this.digestFailure = digestFailure;
		setChanged();
	}
	public long getInvalidDigestPassword() {
		return invalidDigestPassword;
	}
	public void setInvalidDigestPassword(long invalidDigestPassword) {
		this.invalidDigestPassword = invalidDigestPassword;
		setChanged();
	}
	public long getRmCommTimeOut() {
		return rmCommTimeOut;
	}
	public void setRmCommTimeOut(long rmCommTimeOut) {
		this.rmCommTimeOut = rmCommTimeOut;
		setChanged();
	}

}


