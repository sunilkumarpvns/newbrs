package com.brs.userservicemanagement.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.brs.userservicemanagement.enums.StatusEnum;

/**
 * created by sathish on 20/6/2018
 */
@Entity
@Table(name = "User_Authorization", schema = "UserServiceDB")
public class UserAuthorization {

	/** The user authorization id. */
	@Id
	@Column(name = "user_authorization_id")
	@GenericGenerator(name = "userAuthIdGenerator", strategy = "increment")
	@GeneratedValue(generator = "userAuthIdGenerator")
	private Integer userAuthorizationId;

	/** The token. */
	@Column(name = "token")
	private String token;

	/** The ip address. */
	@Column(name = "ip_address")
	private String ipAddress;

	/** The session id. */
	@Column(name = "session_id")
	private String sessionId;

	/** The login time. */
	@Column(name = "login_time")
	@CreationTimestamp
	private Timestamp loginTime;

	/** The logout time. */
	@Column(name = "logout_time")
    @UpdateTimestamp
	private Timestamp logoutTime;

	/** The status enum. */
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private StatusEnum statusEnum;

	/** The login details. */
	@ManyToOne
	@JoinColumn(name = "user_id")
	private LoginDetails loginDetails;

	/**
	 * Gets the user authorization id.
	 *
	 * @return the user authorization id
	 */
	public Integer getUserAuthorizationId() {
		return userAuthorizationId;
	}

	/**
	 * Sets the user authorization id.
	 *
	 * @param userAuthorizationId
	 *            the new user authorization id
	 */
	public void setUserAuthorizationId(Integer userAuthorizationId) {
		this.userAuthorizationId = userAuthorizationId;
	}

	/**
	 * Gets the token.
	 *
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets the token.
	 *
	 * @param token
	 *            the new token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Gets the ip address.
	 *
	 * @return the ip address
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Sets the ip address.
	 *
	 * @param ipAddress
	 *            the new ip address
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Gets the session id.
	 *
	 * @return the session id
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Sets the session id.
	 *
	 * @param sessionId
	 *            the new session id
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Gets the login time.
	 *
	 * @return the login time
	 */
	public Timestamp getLoginTime() {
		return loginTime;
	}

	/**
	 * Sets the login time.
	 *
	 * @param loginTime
	 *            the new login time
	 */
	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}

	/**
	 * Gets the logout time.
	 *
	 * @return the logout time
	 */
	public Timestamp getLogoutTime() {
		return logoutTime;
	}

	/**
	 * Sets the logout time.
	 *
	 * @param logoutTime
	 *            the new logout time
	 */
	public void setLogoutTime(Timestamp logoutTime) {
		this.logoutTime = logoutTime;
	}

	/**
	 * Gets the status enum.
	 *
	 * @return the status enum
	 */
	public StatusEnum getStatusEnum() {
		return statusEnum;
	}

	/**
	 * Sets the status enum.
	 *
	 * @param statusEnum
	 *            the new status enum
	 */
	public void setStatusEnum(StatusEnum statusEnum) {
		this.statusEnum = statusEnum;
	}

	/**
	 * Gets the login details.
	 *
	 * @return the login details
	 */
	public LoginDetails getLoginDetails() {
		return loginDetails;
	}

	/**
	 * Sets the login details.
	 *
	 * @param loginDetails
	 *            the new login details
	 */
	public void setLoginDetails(LoginDetails loginDetails) {
		this.loginDetails = loginDetails;
	}

}
