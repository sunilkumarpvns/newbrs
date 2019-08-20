package com.elitecore.netvertex.service.offlinernc.productspec;

import com.elitecore.netvertex.service.offlinernc.rncpackage.RnCPackage;

public class ProductSpecService {

	private String name;
	private String alias;
	private RnCPackage rncPackage;

	public ProductSpecService(String name, String alias, RnCPackage rncPackage) {
		this.name = name;
		this.alias = alias;
		this.rncPackage = rncPackage;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public RnCPackage getRncPackage() {
		return rncPackage;
	}
}
