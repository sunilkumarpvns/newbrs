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

import com.brs.userservicemanagement.enums.StatusEnum;


/**
 * created by sathish on 20/6/2018
 */
@Entity
@Table(name = "user_password_links", schema = "UserServiceDB")
public class UserPasswordLinks {

	/** The user pwd link id. */
	@Id
	@Column(name = "user_pwd_link_id")
	@GenericGenerator(name = "userPwdLinkIdGenerator", strategy = "increment")
	@GeneratedValue(generator = "userPwdLinkIdGenerator")
	private Integer userPwdLinkId;

	/** The generated time. */
	@Column(name = "generated_time")
	@CreationTimestamp
	private Timestamp generatedTime;

	/** The dynamic url. */
	@Column(name = "dynamic_url")
	private String dynamicUrl;

	/** The status enum. */
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private StatusEnum statusEnum;

	/** The login details. */
	@ManyToOne
	@JoinColumn(name = "user_id")
	private LoginDetails loginDetails;

	/**
	 * Gets the user pwd link id.
	 *
	 * @return the user pwd link id
	 */
	public Integer getUserPwdLinkId() {
		return userPwdLinkId;
	}

	/**
	 * Sets the user pwd link id.
	 *
	 * @param userPwdLinkId
	 *            the new user pwd link id
	 */
	public void setUserPwdLinkId(Integer userPwdLinkId) {
		this.userPwdLinkId = userPwdLinkId;
	}

	/**
	 * Gets the generated time.
	 *
	 * @return the generated time
	 */
	public Timestamp getGeneratedTime() {
		return generatedTime;
	}

	/**
	 * Sets the generated time.
	 *
	 * @param generatedTime
	 *            the new generated time
	 */
	public void setGeneratedTime(Timestamp generatedTime) {
		this.generatedTime = generatedTime;
	}

	/**
	 * Gets the dynamic url.
	 *
	 * @return the dynamic url
	 */
	public String getDynamicUrl() {
		return dynamicUrl;
	}

	/**
	 * Sets the dynamic url.
	 *
	 * @param dynamicUrl
	 *            the new dynamic url
	 */
	public void setDynamicUrl(String dynamicUrl) {
		this.dynamicUrl = dynamicUrl;
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
