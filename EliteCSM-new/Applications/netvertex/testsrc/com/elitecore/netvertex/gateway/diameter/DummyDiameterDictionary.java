package com.elitecore.netvertex.gateway.diameter;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.ILicenseValidator;

public class DummyDiameterDictionary {
	
	private static DiameterDictionary diameterDictionary;
	static {
		try {
		URL currentPath = DummyDiameterDictionary.class.getClassLoader().getResource("dictionary/diameter");
		File file = new File(URLDecoder.decode(currentPath.getPath(),"UTF-8"));
			DiameterDictionary.getInstance().load(file, ILicenseValidator.SUPPORT_ALL_VENDORS);
			diameterDictionary = DiameterDictionary.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DiameterDictionary getInstance(){
		return diameterDictionary;
	}
}
