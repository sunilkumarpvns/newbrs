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

package com.elitecore.license.core;

import java.util.Iterator;
import java.util.List;

import com.elitecore.license.base.BaseLicenseException;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.exception.InvalidLicenseDataException;
import com.elitecore.license.util.EncryptDecrypt;

/**
 * @author EliteCore Technologies Ltd.
 */

public class EliteLicenceKeyGenerator {

	public String genreateLicense( List<LicenseData> lstLicenseData ) {
		try {
			Iterator<LicenseData> itLstLicenseData = lstLicenseData.iterator();
			LicenseData licenseData = null;
			String lickey = "";
			while (itLstLicenseData.hasNext()) {

				licenseData = itLstLicenseData.next();
				setOpenFieldValue(licenseData);
				try{
					lickey = lickey + generateLicenseKey(licenseData);
					if (itLstLicenseData.hasNext()) {
						lickey = lickey + LicenseConstants.LICENSE_SEPRATOR;
					}
				}catch(BaseLicenseException e){
					throw new BaseLicenseException("Unable to get Lisense Genrator of type" + licenseData.getType(), e);
				}
			}
			return lickey;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setOpenFieldValue( LicenseData licenseData ) {

		if (licenseData.getAdditionalKey() == null || licenseData.getAdditionalKey().equalsIgnoreCase("")) {
			licenseData.setAdditionalKey(LicenseConstants.DEFAULT_ADDITIONAL_KEY);
		}

		if (licenseData.getStatus() == null || licenseData.getStatus().equalsIgnoreCase("")) {
			licenseData.setStatus(LicenseConstants.DEFAULT_STATUS);
		}
	}

	public String generateLicenseKey( LicenseData licenseData ) throws BaseLicenseException {
		String name,module,version,status,additionalKey,displayName,value,type,valueType,operator;
		try {
			if (licenseData == null) 									{ throw new InvalidLicenseDataException("licenseData Object is null"); }
			if ((value=licenseData.getValue().toString()) == null) 		{ throw new InvalidLicenseDataException("Value  is null"); }
			if ((module=licenseData.getModule()) == null) 				{ throw new InvalidLicenseDataException("Modual Name is null"); }
			if ((name=licenseData.getName()) == null) 					{ throw new InvalidLicenseDataException("Name is null"); }
			if ((version=licenseData.getVersion()) == null) 			{ throw new InvalidLicenseDataException("Version is null"); }
			if ((status=licenseData.getStatus()) == null) 				{ throw new InvalidLicenseDataException("Status is null"); }
			if ((additionalKey=licenseData.getAdditionalKey()) == null) { throw new InvalidLicenseDataException("Additional Key is null"); }
			if ((displayName=licenseData.getDisplayName()) == null ) 	{ throw new InvalidLicenseDataException("Display Name is null");}
			if ((type=licenseData.getType()) == null ) 					{ throw new InvalidLicenseDataException("Type is null");}
			if ((valueType=licenseData.getValueType())==null)			{ throw new InvalidLicenseDataException("ValueType is null");}
			if ((operator=licenseData.getOperator())==null)				{ throw new InvalidLicenseDataException("Operator is null");}
			
			//Create the license Key and set in the license Object.
			String licenseStr = name + LicenseConstants.LICENSE_KEY_SEPRATOR + module + LicenseConstants.LICENSE_KEY_SEPRATOR + type + LicenseConstants.LICENSE_KEY_SEPRATOR + value
			            		+ LicenseConstants.LICENSE_KEY_SEPRATOR + version + LicenseConstants.LICENSE_KEY_SEPRATOR + status + LicenseConstants.LICENSE_KEY_SEPRATOR + additionalKey 
			            		+ LicenseConstants.LICENSE_KEY_SEPRATOR + displayName + LicenseConstants.LICENSE_KEY_SEPRATOR + valueType + LicenseConstants.LICENSE_KEY_SEPRATOR + operator;
			String licenseKey = EncryptDecrypt.encrypt(licenseStr); 

			return licenseKey;
		} catch (InvalidLicenseDataException invException) {
			throw invException;
		} catch (Exception e) {
			throw new BaseLicenseException(licenseData.getType() + " license generation Failed", e);
		}
	}

}
