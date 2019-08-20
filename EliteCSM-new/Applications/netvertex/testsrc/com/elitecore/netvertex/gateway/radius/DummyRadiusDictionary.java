package com.elitecore.netvertex.gateway.radius;

import java.io.File;
import java.net.URL;

import com.elitecore.coreradius.commons.util.Dictionary;

public class DummyRadiusDictionary {

	private static Dictionary radiusDictionary;
	static{
		try {
			URL currentPath = DummyRadiusDictionary.class.getClassLoader().getResource("com/elitecore/test/netvertex/gateway/radius/dictionary");
			File file = new File(currentPath.getPath());
			Dictionary.getInstance().loadDictionary(file);
			radiusDictionary = Dictionary.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static Dictionary getInstance(){
		return radiusDictionary;
	}
}
