/*
 *  License Module
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 10th September 2007
 *  Created By Ezhava Baiju D
 */

package com.elitecore.license.publickey;

import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.util.EncryptDecrypt;
import com.elitecore.license.util.SystemUtil;

/**
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public class ElitePublickeyGenerator {
	
	public static final PublicKeyEncryptor PLAIN_TEXT_FORMAT = new PlainTextPublicKey();
	
	private final PublicKeyEncryptor encryptor;

	public ElitePublickeyGenerator(PublicKeyEncryptor encryptor) {
		this.encryptor = encryptor;
	}
	
	public ElitePublickeyGenerator() {
		this.encryptor = new DefaultPublicKeyEncryptor();
	}
    
	public String generatePublicKey(String ServerHome, String additionalKey ) {

		if (additionalKey == null)
			additionalKey = "no additional key";

		String finalKey = SystemUtil.getSystemKey() + LicenseConstants.PUBLIC_KEY_SEPRATOR + additionalKey + LicenseConstants.PUBLIC_KEY_SEPRATOR + ServerHome;
		//System.out.println("Final Key : " + finalKey);
		return encryptor.encrypt(finalKey);

	}

	public String generatePublicKey(String ServerHome, String additionalKey, String instanceId, String instanceName) {

		if (additionalKey == null)
			additionalKey = "no additional key";

		String finalKey = SystemUtil.getSystemKey() + LicenseConstants.PUBLIC_KEY_SEPRATOR + additionalKey + 
				LicenseConstants.PUBLIC_KEY_SEPRATOR + ServerHome + LicenseConstants.PUBLIC_KEY_SEPRATOR + instanceId +
				LicenseConstants.PUBLIC_KEY_SEPRATOR + instanceName;
		//System.out.println("Final Key : " + finalKey);
		return encryptor.encrypt(finalKey);

	}
    
    public interface PublicKeyEncryptor {
    	String encrypt(String key);
    }
    
    public static class DefaultPublicKeyEncryptor implements PublicKeyEncryptor {
		@Override
		public String encrypt(String key) {
			return EncryptDecrypt.encrypt(key);
		}
    	
    }
    
    public static class PlainTextPublicKey implements PublicKeyEncryptor {
		@Override
		public String encrypt(String key) {
			return key;
		}
    	
    }
}
