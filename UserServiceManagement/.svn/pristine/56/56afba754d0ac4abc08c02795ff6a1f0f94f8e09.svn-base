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
 * created by sathish on 20/6/2018.
 */
@Entity
@Table(name = "user_otp", schema = "UserServiceDB")
public class UserOtp {

	/** The user otp id. */
	@Id
	@Column(name = "user_otp_id")
	@GenericGenerator(name = "otpIdGenerator", strategy = "increment")
	@GeneratedValue(generator = "otpIdGenerator")
	private Integer userOtpId;

	/** The generated time. */
	@Column(name = "generated_time")
	@CreationTimestamp
	private Timestamp generatedTime;

	/** The otp. */
	@Column(name = "otp")
	private String otp;

	/** The status enum. */
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private StatusEnum statusEnum;

	/** The login details. */
	@ManyToOne
	@JoinColumn(name = "user_id")
	private LoginDetails loginDetails;

	/**
	 * Gets the user otp id.
	 *
	 * @return the user otp id
	 */
	public Integer getUserOtpId() {
		return userOtpId;
	}

	/**
	 * Sets the user otp id.
	 *
	 * @param userOtpId
	 *            the new user otp id
	 */
	public void setUserOtpId(Integer userOtpId) {
		this.userOtpId = userOtpId;
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
	 * Gets the otp.
	 *
	 * @return the otp
	 */
	public String getOtp() {
		return otp;
	}

	/**
	 * Sets the otp.
	 *
	 * @param otp
	 *            the new otp
	 */
	public void setOtp(String otp) {
		this.otp = otp;
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
