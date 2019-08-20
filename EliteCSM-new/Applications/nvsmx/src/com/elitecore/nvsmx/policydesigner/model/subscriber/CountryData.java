package com.elitecore.nvsmx.policydesigner.model.subscriber;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TBLSCOUNTRY")
public class CountryData {
	private Long countryID;
	private String name;
	private String code;

	@Id
	@Column(name = "COUNTRYID")
	public Long getCountryID() {
		return countryID;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	@Column(name = "CODE")
	public String getCode() {
		return code;
	}

	public void setCountryID(Long countryID) {
		this.countryID = countryID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
