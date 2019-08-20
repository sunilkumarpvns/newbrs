package com.brs.userservicemanagement.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import com.brs.userservicemanagement.enums.StatusEnum;


/**
 * created by sathish 21/06/2018
 */
@Entity
@Table(name = "Employee_Profile", schema = "UserServiceDB")
public class EmployeeProfile {
	
	/** The user id. */
	@Id
	@Column(name = "user_id")
	@GenericGenerator(name = "userIdGenerator", strategy = "foreign", parameters = {
			@Parameter(name = "property", value = "loginDetails") })
	@GeneratedValue(generator = "userIdGenerator")
	private Long userId;
	
	/** The first name. */
	@Column(name = "first_name")
	private String firstName;
	
	/** The last name. */
	@Column(name = "last_name")
	private String lastName;
	
	/** The dob. */
	@Column(name = "dob")
	@Temporal(TemporalType.DATE)
	private Date dob;
	
	/** The gender. */
	@Column(name = "gender")
	private String gender;
	
	/** The pan number. */
	@Column(name = "pan_number")
	private String pan_number;
	
	/** The aadhar number. */
	@Column(name = "aadhar_number")
	private String aadharNumber;
	
	/** The address. */
	@Column(name = "address")
	private String address;
	
	/** The city. */
	@Column(name = "city")
	private String city;
	
	/** The state. */
	@Column(name = "state")
	private String state;
	
	/** The zipcode. */
	@Column(name = "zipcode")
	private String zipcode;
	
	/** The status. */
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private StatusEnum status;
	
	/** The created by. */
	@Column(name = "created_by")
	private Long createdBy;
	
	/** The last updated by. */
	@Column(name = "last_updated_by")
	private Long lastUpdatedBy;
	
	/** The created date. */
	@Column(name = "created_date")
	@CreationTimestamp
	private Timestamp createdDate;
	
	/** The last updated date. */
	@Column(name = "last_updated_date")
	@UpdateTimestamp
	private Timestamp lastUpdatedDate;
	
	/** The login details. */
	@OneToOne
	@PrimaryKeyJoinColumn(name = "user_id")
	private LoginDetails loginDetails;

	/** The date of joining. */
	@Column(name = "date_of_joining")
	@Temporal(TemporalType.DATE)
	private Date dateOfJoining;
	
	/** The designation. */
	@Column(name = "designation")
	private String designation;
	
	/** The blood group. */
	@Column(name = "blood_group")
	private String bloodGroup;
	
	/** The qualification. */
	@Column(name = "qualification")
	private String qualification;
	
	/** The salary. */
	@Column(name = "salary")
	private Double salary;

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the dob.
	 *
	 * @return the dob
	 */
	public Date getDob() {
		return dob;
	}

	/**
	 * Sets the dob.
	 *
	 * @param dob the new dob
	 */
	public void setDob(Date dob) {
		this.dob = dob;
	}

	/**
	 * Gets the gender.
	 *
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * Sets the gender.
	 *
	 * @param gender the new gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the city.
	 *
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city.
	 *
	 * @param city the new city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Gets the zipcode.
	 *
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * Sets the zipcode.
	 *
	 * @param zipcode the new zipcode
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public StatusEnum getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	/**
	 * Gets the created by.
	 *
	 * @return the created by
	 */
	public Long getCreatedBy() {
		return createdBy;
	}

	/**
	 * Sets the created by.
	 *
	 * @param createdBy the new created by
	 */
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Gets the last updated by.
	 *
	 * @return the last updated by
	 */
	public Long getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	/**
	 * Sets the last updated by.
	 *
	 * @param lastUpdatedBy the new last updated by
	 */
	public void setLastUpdatedBy(Long lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	/**
	 * Gets the created date.
	 *
	 * @return the created date
	 */
	public Timestamp getCreatedDate() {
		return createdDate;
	}

	/**
	 * Sets the created date.
	 *
	 * @param createdDate the new created date
	 */
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Gets the last updated date.
	 *
	 * @return the last updated date
	 */
	public Timestamp getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	/**
	 * Sets the last updated date.
	 *
	 * @param lastUpdatedDate the new last updated date
	 */
	public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	/**
	 * Gets the pan number.
	 *
	 * @return the pan number
	 */
	public String getPan_number() {
		return pan_number;
	}

	/**
	 * Sets the pan number.
	 *
	 * @param pan_number the new pan number
	 */
	public void setPan_number(String pan_number) {
		this.pan_number = pan_number;
	}

	/**
	 * Gets the aadhar number.
	 *
	 * @return the aadhar number
	 */
	public String getAadharNumber() {
		return aadharNumber;
	}

	/**
	 * Sets the aadhar number.
	 *
	 * @param aadharNumber the new aadhar number
	 */
	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
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
	 * @param loginDetails the new login details
	 */
	public void setLoginDetails(LoginDetails loginDetails) {
		this.loginDetails = loginDetails;
	}

	/**
	 * Gets the date of joining.
	 *
	 * @return the date of joining
	 */
	public Date getDateOfJoining() {
		return dateOfJoining;
	}

	/**
	 * Sets the date of joining.
	 *
	 * @param dateOfJoining the new date of joining
	 */
	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	/**
	 * Gets the designation.
	 *
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * Sets the designation.
	 *
	 * @param designation the new designation
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * Gets the blood group.
	 *
	 * @return the blood group
	 */
	public String getBloodGroup() {
		return bloodGroup;
	}

	/**
	 * Sets the blood group.
	 *
	 * @param bloodGroup the new blood group
	 */
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	/**
	 * Gets the qualification.
	 *
	 * @return the qualification
	 */
	public String getQualification() {
		return qualification;
	}

	/**
	 * Sets the qualification.
	 *
	 * @param qualification the new qualification
	 */
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	/**
	 * Gets the salary.
	 *
	 * @return the salary
	 */
	public Double getSalary() {
		return salary;
	}

	/**
	 * Sets the salary.
	 *
	 * @param salary the new salary
	 */
	public void setSalary(Double salary) {
		this.salary = salary;
	}

}
