package com.elitecore.aaa.core.radius.dictionary;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;

import com.elitecore.coreradius.commons.util.Dictionary;

public class RadiusDictionaryTestHarness {
	private static Dictionary dictionary;
	static {		
		try {
			URL currentPath = RadiusDictionaryTestHarness.class.getClassLoader().getResource("dictionary/radius");
			File file = new File(URLDecoder.decode(currentPath.getPath(),"UTF-8"));
			System.out.println(file.getAbsolutePath());
			System.out.println("exist : " + file.exists());
			Dictionary.getInstance().loadDictionary(file);
			dictionary = Dictionary.getInstance();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static Dictionary getInstance(){
		return dictionary;
	}
}
