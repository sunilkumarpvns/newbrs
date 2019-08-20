package com.elitecore.license.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.elitecore.license.base.License;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.base.exception.licensetype.InvalidLicenseException;
import com.elitecore.license.crypt.LicenseDecryptor;
import com.elitecore.license.util.SystemUtil;

/**
 * LicFileParser parses .lic format license files according to  
 * the decryptor passed to it and creates a license corresponding
 * the license key passed to it.
 * 
 * @author malav.desai
 * @author vicky.singh
 *
 */
public class LicFileParser implements Parser {
	
	private String licenseKey;
	private LicenseDecryptor decryptor;
	private String version;

	public LicFileParser(String licenseKey, LicenseDecryptor decryptor, String version) {
		this.licenseKey = licenseKey;
		this.decryptor = decryptor;
		this.version = version;
	}

	@Override
	public License parse() throws InvalidLicenseKeyException {
		Map<String, LicenseData> decryptedLicenseKey = new HashMap<String, LicenseData>();
		
		StringTokenizer licenseKeyTokenizer = new StringTokenizer(licenseKey, LicenseConstants.LICENSE_SEPRATOR);

		List<String> lstLickey = new ArrayList<String>();
		while (licenseKeyTokenizer.hasMoreTokens()) {
			lstLickey.add(licenseKeyTokenizer.nextToken());
		}

		Iterator<String> itLstLicKey = lstLickey.iterator();
		String decryptedLicKey="";
		LicenseData licData = null;
		while (itLstLicKey.hasNext()) {
			String encryptLicKey = itLstLicKey.next();
			try {
				decryptedLicKey = decryptor.decryptLicenseKey(encryptLicKey);
				List<String> lstKeyToken = SystemUtil.getLicenseTokenList(decryptedLicKey);
				licData = new LicenseData();

				String name   	 = lstKeyToken.get(LicenseConstants.LICENSE_NAME_INDEX - 1);
				licData.setName(name);
				licData.setModule(lstKeyToken.get(LicenseConstants.LICENSE_MODULE_INDEX - 1));
				licData.setType(lstKeyToken.get(LicenseConstants.LICENSE_TYPE_INDEX - 1));
				licData.setValueType(lstKeyToken.get(LicenseConstants.LICENSE_VALUETYPE_INDEX - 1));
				licData.setVersion(lstKeyToken.get(LicenseConstants.LICENSE_VERSION_INDEX- 1));
				licData.setDisplayName(lstKeyToken.get(LicenseConstants.LICENSE_DISPLAYNAME_INDEX - 1));
				
				if(!licData.getVersion().equals(this.version))
					throw new InvalidLicenseException("Version does not match");
				
				if(name.equalsIgnoreCase("SYSTEM_NODE")){
					String decryptedPublicKey = null;
					try {
						decryptedPublicKey = decryptor.decryptPublicKey(lstKeyToken.get(LicenseConstants.LICENSE_VALUE_INDEX - 1));
					} catch (Exception e) {
						throw new InvalidLicenseKeyException("Invalid or corrupted License Key.", e);
					}

					licData.setValue(decryptedPublicKey);
				}else{
					licData.setValue(lstKeyToken.get(LicenseConstants.LICENSE_VALUE_INDEX - 1));
				}

				licData.setOperator(lstKeyToken.get(LicenseConstants.LICENSE_OPERATOR_INDEX - 1));

				decryptedLicenseKey.put(name,licData);
			}catch (Exception e) {
				throw new InvalidLicenseKeyException("Problem Decrypting License Key. Reason :" + e.getMessage(), e);
			}
		}
		return new License(version, decryptedLicenseKey);
	}

}
