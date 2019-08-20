package com.elitecore.elitesm.web.servicepolicy.dynauth;

import java.util.List;

public class NasClientData {
	private List<NasClients> nasClientsList;

	public List<NasClients> getNasClientsList() {
		return nasClientsList;
	}

	public void setNasClientsList(List<NasClients> nasClientsList) {
		this.nasClientsList = nasClientsList;
	}

	@Override
	public String toString() {
		return "NasClientData [nasClientsList=" + nasClientsList + "]";
	}
}
