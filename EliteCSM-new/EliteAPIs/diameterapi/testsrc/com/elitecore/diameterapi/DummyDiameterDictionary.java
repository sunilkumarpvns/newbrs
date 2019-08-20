package com.elitecore.diameterapi;

import java.io.File;

import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.ILicenseValidator;

public class DummyDiameterDictionary {
	
	private static DiameterDictionary diameterDictionary;
	static {		
		try {
			ClasspathResource dictionary = ClasspathResource.at("dictionary"); 
			System.out.println(dictionary.getAbsolutePath());
			System.out.println("exist : " + dictionary.exists());
			DiameterDictionary.getInstance().load(new File(dictionary.getAbsolutePath()), ILicenseValidator.SUPPORT_ALL_VENDORS);
			diameterDictionary = DiameterDictionary.getInstance();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static DiameterDictionary getInstance(){
		return diameterDictionary;
	}
	
}
