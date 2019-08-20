package com.elitecore.coreradius.commons.util.dictionary;

import java.util.Map;

public class DictionaryModel {

	private Map<Long, VendorInformation> idToVendorInformation;

	public DictionaryModel(Map<Long, VendorInformation> idToVendorInformation) {
		this.idToVendorInformation = idToVendorInformation;
	}

	public Map<Long, VendorInformation> getIdToVendorInformation() {
		return idToVendorInformation;
	}

	public void setIdToVendorInformation(Map<Long, VendorInformation> idToVendorInformation) {
		this.idToVendorInformation = idToVendorInformation;
	}
}
