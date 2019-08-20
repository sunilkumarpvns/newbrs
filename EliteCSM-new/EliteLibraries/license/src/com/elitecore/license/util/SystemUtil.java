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

package com.elitecore.license.util;

import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.commons.LicenseDataManager;
import com.elitecore.license.base.commons.LicenseTypeConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @author Elitecore Technologies Ltd.
 */
public class SystemUtil {

	private static final char[] LHEX = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String getSystemKey() {
		String finalKey = "";
		String tempKey = null;
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost();
			tempKey = inetAddress.getHostAddress();
			if (tempKey == null)
				tempKey = "no address";

			finalKey = tempKey;
		} catch (UnknownHostException e) {
			// No need to handle this exception.
		}

		finalKey = finalKey + LicenseConstants.PUBLIC_KEY_SEPRATOR	+ System.getProperty("os.name");
		tempKey = getMacAddress(inetAddress);
		finalKey = finalKey + LicenseConstants.PUBLIC_KEY_SEPRATOR + tempKey;

		return finalKey;
	}

	public static String getIPAddress() throws UnknownHostException{

		InetAddress inetAddress = InetAddress.getLocalHost();
		String ipAddress = inetAddress.getHostAddress();
		if (ipAddress == null)
			ipAddress = "no address";

		return ipAddress;
	}

	/* This method will seprate all licensekey from compounedLicenseKey */

	private static List<String> getEncryptedLicenseKeyList(
			String compounedLicenseKey) {

		StringTokenizer licenseKeyTokenizer = new StringTokenizer(compounedLicenseKey, LicenseConstants.LICENSE_SEPRATOR);
		List<String> lstEncryptedLicenseKey = new ArrayList<String>();
		while (licenseKeyTokenizer.hasMoreTokens()) {
			lstEncryptedLicenseKey.add(licenseKeyTokenizer.nextToken());
		}
		return lstEncryptedLicenseKey;
	}

	private static List<String> getDecryptedLicenseKeyList(
			List<String> lstEncryptedLicenseKey)
			throws InvalidLicenseKeyException {

		Iterator<String> itLstLicKey = lstEncryptedLicenseKey.iterator();
		List<String> lstDecryptedLicenseKey = new ArrayList<String>();
		String licenseKey = null;
		while (itLstLicKey.hasNext()) {
			try {
				licenseKey = EncryptDecrypt.decrypt(itLstLicKey.next());
				lstDecryptedLicenseKey.add(licenseKey.trim());
			} catch (Exception e) {
				throw new InvalidLicenseKeyException("Unable to Decrypt LicenseKey", e);
			}
		}
		return lstDecryptedLicenseKey;
	}

	/* Convert String to LicenseData */
	public static Map<String, LicenseData> getLicenseDataList(
			List<String> lstDecryptedLicenseKey,
			boolean publicKeyDecryptionStatus)
			throws InvalidLicenseKeyException {

		Iterator<String> itDecrytedLicenseKey = lstDecryptedLicenseKey
				.iterator();
		Map<String, LicenseData> maplicData = new LinkedHashMap<String, LicenseData>();
		LicenseData licData = null;
		while (itDecrytedLicenseKey.hasNext()) {
			List<String> lstKeyToken = getLicenseTokenList(itDecrytedLicenseKey.next());
			licData = new LicenseData();
			licData.setName(lstKeyToken.get(LicenseConstants.LICENSE_NAME_INDEX - 1));
			licData.setModule(lstKeyToken.get(LicenseConstants.LICENSE_MODULE_INDEX - 1));
			licData.setType(lstKeyToken.get(LicenseConstants.LICENSE_TYPE_INDEX - 1));
			licData.setValue(lstKeyToken.get(LicenseConstants.LICENSE_VALUE_INDEX - 1));
			licData.setVersion(lstKeyToken.get(LicenseConstants.LICENSE_VERSION_INDEX - 1));
			licData.setStatus(lstKeyToken.get(LicenseConstants.LICENSE_STATUS_INDEX - 1));
			licData.setAdditionalKey(lstKeyToken.get(LicenseConstants.LICENSE_ADDITIONALKEY_INDEX - 1));
			licData.setDisplayName(lstKeyToken.get(LicenseConstants.LICENSE_DISPLAYNAME_INDEX - 1));
			licData.setValueType(lstKeyToken.get(LicenseConstants.LICENSE_VALUETYPE_INDEX - 1));
			licData.setOperator(lstKeyToken.get(LicenseConstants.LICENSE_OPERATOR_INDEX - 1));
			if (licData.getType().equalsIgnoreCase(LicenseTypeConstants.NODE)
					&& (publicKeyDecryptionStatus)) {
				try {
					licData.setValue(EncryptDecrypt.decrypt((String) licData
							.getValue()));
				} catch (Exception e) {
					throw new InvalidLicenseKeyException(
							"Unable to Decrypt LicenseKey", e);
				}
			}
			maplicData.put(licData.getName() + ":" + licData.getModule(),
					licData);
		}
		return maplicData;
	}

	public static Map<String, LicenseData> getLicenseInformationMap(
			String compounedLicenseKey, boolean publicKeyDecryptionStatus)
			throws InvalidLicenseKeyException {
		List<String> lstEncryptedLicenseKey = getEncryptedLicenseKeyList(compounedLicenseKey);
		List<String> lstDecryptedLicenseKey = getDecryptedLicenseKeyList(lstEncryptedLicenseKey);
		return getLicenseDataList(lstDecryptedLicenseKey,publicKeyDecryptionStatus);
	}

	public static Map<String, LicenseData> getLicenseInformationMap(
			String compounedLicenseKey) throws InvalidLicenseKeyException {
		return getLicenseInformationMap(compounedLicenseKey, true);
	}

	public static Map<String, LicenseData> populateAdditionalInformations(
			Map<String, LicenseData> mapLicData) {

		Map<String, LicenseData> licenseDataManagerLicenseData = new HashMap<String, LicenseData>();
		licenseDataManagerLicenseData = SystemUtil.getSequenceLicData(
				LicenseDataManager.getLicenseData(),
				0,
				licenseDataManagerLicenseData);

		Iterator<String> itkeySet = mapLicData.keySet().iterator();
		String key = null;

		while (itkeySet.hasNext()) {
			key = itkeySet.next();
			LicenseData licenseDataManagerLicData = licenseDataManagerLicenseData.get(key.trim());

			if (licenseDataManagerLicData != null) {
				mapLicData.get(key).setDisplayName(licenseDataManagerLicData.getDisplayName());
				mapLicData.get(key).setDescription(licenseDataManagerLicData.getDescription());
			}
		}
		return mapLicData;

	}

	public static List<LicenseData> sequenceingLicense(
			Map<String, LicenseData> mapLicData) {

		Map<String, LicenseData> licenseDataManagerLicenseData = new LinkedHashMap<String, LicenseData>();
		List<LicenseData> lstData = new ArrayList<LicenseData>();

		licenseDataManagerLicenseData = SystemUtil.getSequenceLicData(LicenseDataManager.getLicenseData(), 0,licenseDataManagerLicenseData);

		Iterator<String> itkeySet = licenseDataManagerLicenseData.keySet().iterator();
		String key = null;
				
		while (itkeySet.hasNext()) {
			key = itkeySet.next();
			if (mapLicData.containsKey(key.trim())) {
				mapLicData.get(key).setIndex(
						licenseDataManagerLicenseData.get(key).getIndex());
				lstData.add(mapLicData.get(key));
				mapLicData.remove(key);
			}

		}

		/*
		 * if any license is not available in licensedatamanager then it will
		 * add that license at end.
		 */
		itkeySet = mapLicData.keySet().iterator();
		while (itkeySet.hasNext()) {
			key = itkeySet.next();
			mapLicData.get(key).setIndex(0);
			mapLicData.get(key).setDisplayName("Deprecated License");
			mapLicData.get(key).setDescription("This license is Deprecated (Note :- not availble in current license module");
			mapLicData.get(key).setIndex(0);
			lstData.add(mapLicData.get(key));
		}
		return lstData;

	}

	/* resolve parent child relation and convert graph to list */
	public static List<LicenseData> getSequenceLicData(LicenseData licData,
			int index, List<LicenseData> lstLicData) {
		index++;

		if (licData.getLicenseData() != null) {
			List<LicenseData> subLicenseList = licData.getLicenseData();
			Iterator<LicenseData> itSubLicenseList = subLicenseList.iterator();
			while (itSubLicenseList.hasNext()) {
				LicenseData tempLicData = itSubLicenseList.next();
				tempLicData.setIndex(index);
				lstLicData.add(tempLicData);
				if (tempLicData.getType().equalsIgnoreCase(
						LicenseTypeConstants.MODULE)) {
					lstLicData = getSequenceLicData(tempLicData, index,lstLicData);
				}
			}
		}
		return lstLicData;
	}

	/* resolve parent child relation and convert graph to Map */
	public static Map<String, LicenseData> getSequenceLicData(
			LicenseData licData, int index, Map<String, LicenseData> mapLicData) {
		index++;
		if (licData.getLicenseData() != null) {

			List<LicenseData> subLicenseList = licData.getLicenseData();

			Iterator<LicenseData> itSubLicenseList = subLicenseList.iterator();
			while (itSubLicenseList.hasNext()) {

				LicenseData tempLicData = itSubLicenseList.next();
				tempLicData.setIndex(index);
				mapLicData.put(tempLicData.getName() + ":"
						+ tempLicData.getModule(), tempLicData);
				if (tempLicData.getType().equalsIgnoreCase(LicenseTypeConstants.MODULE)) {
					mapLicData = getSequenceLicData(tempLicData, index,	mapLicData);
				}
			}
		}
		return mapLicData;
	}

	public static int getAvailableProcessor() {
		return Runtime.getRuntime().availableProcessors();
	}

	public static String getMacAddress(InetAddress inetAddress) {
	/*	byte[] macAddressBytes = null;
		try {
			macAddressBytes = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
		} catch (SocketException exp) {
			System.out.println("Problem while getting Mac Address : "+ exp.getMessage());
			return "no macaddress";
		}
		return bytesToHex(macAddressBytes);
		*/
		return "no macaddress";
	}

	public static String bytesToHex(byte[] buf) {
		int length = buf.length;
		StringBuilder hexbuf = new StringBuilder(length << 1);
		for (int i = 0; i < length; i++) {
			hexbuf.append(LHEX[buf[i] >> 4 & 0xf]);
			hexbuf.append(LHEX[buf[i] & 0xf]);
			if (i < length - 1)
				hexbuf.append("-");
		}

		return hexbuf.toString();
	}

	public static List<String> getLicenseTokenList( String decryptedLicenseKey ) throws InvalidLicenseKeyException {

		StringTokenizer licenseKeyTokenizer = new StringTokenizer(decryptedLicenseKey, LicenseConstants.LICENSE_KEY_SEPRATOR);

		List<String> lstKeyTokens = new ArrayList<String>();
		if (licenseKeyTokenizer.countTokens() == LicenseConstants.LICENSE_KEY_TOKEN_COUNT) {
			for ( int i = 0; i < LicenseConstants.LICENSE_KEY_TOKEN_COUNT; i++ ) {
				String value = licenseKeyTokenizer.nextToken();
				lstKeyTokens.add(value);
			}
		} else {
			throw new InvalidLicenseKeyException("License key Token count not match with license key");
		}
		return lstKeyTokens;
	}
	
	public static String readStoredLicense(File licenseFile) throws IOException {
		String licenseKey;
		if (licenseFile.exists() == false){
			throw new FileNotFoundException(licenseFile.getName() + " not found.");
		}
		if (licenseFile.canRead() == false){
			throw new IOException("Unable to read " + licenseFile.getName() + ".");
		}
		BufferedReader reader =  new BufferedReader(new FileReader(licenseFile));
		try {
			licenseKey = reader.readLine();
		} finally {
				reader.close();
		}
		return licenseKey;
	}
}
