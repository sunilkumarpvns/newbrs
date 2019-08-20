package com.elitecore.netvertex.service.offlinernc.partner;

import java.sql.Timestamp;
import java.util.Map;

import javax.annotation.Nullable;

import com.elitecore.netvertex.service.offlinernc.account.Account;
import com.elitecore.netvertex.service.offlinernc.prefix.City;

public class Partner {

	private String name;
	private String partnerLegalName;
	private Timestamp registrationDate;
	private City city;
	private Map<String, Account> accountNameToAccount;

	public Partner(String name, String partnerLegalName, 
			Timestamp registrationDate,
			City city, Map<String, Account> accountNameToAccount) {
		this.name = name;
		this.partnerLegalName = partnerLegalName;
		this.registrationDate = registrationDate;
		this.city = city;
		this.accountNameToAccount = accountNameToAccount;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "name=" + getName() + ", partnerLegalName=" + getPartnerLegalName() + ", registrationDate="
				+ getRegistrationDate() + ", city=" + getCity().getName();
	}

	public String getPartnerLegalName() {
		return partnerLegalName;
	}

	public Timestamp getRegistrationDate() {
		return registrationDate;
	}

	public City getCity() {
		return city;
	}

	public @Nullable Account selectAccount(String accountName) {
		return accountNameToAccount.get(accountName);
	}

}
