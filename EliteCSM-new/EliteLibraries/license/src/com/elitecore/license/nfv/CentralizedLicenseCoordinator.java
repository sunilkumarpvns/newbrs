package com.elitecore.license.nfv;

import com.elitecore.commons.base.Bytes;
import com.elitecore.license.base.License;
import com.elitecore.license.base.LicenseData;
import com.elitecore.license.base.MultiLicenseManager;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.crypt.PlaintextEncryptor;
import com.elitecore.license.formatter.LicFormatter;
import com.elitecore.license.util.AES;
import com.elitecore.license.util.SystemUtil;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

/**
 * A utility class which is used to allocate a license, validate a license
 * de-register a license and fetch desired information about licenses allocated and 
 * instances to which those licenses have been allocated.
 *  
 * @author vicky.singh
 *
 */
public class CentralizedLicenseCoordinator {

	private static final String REGISTERED = "REGISTERED";
	private static final String DEREGISTERED = "DEREGISTERED";
	private static final String SHA_512 = "SHA-512";
	private static final String LIC_FILE_RELATIVE_PATH = File.separator + "license" + File.separator + "local_node.lic";

	private LicenseDAO licenseDAO;

	public CentralizedLicenseCoordinator(LicenseDAO licenseDAO) {
		this.licenseDAO = licenseDAO;
	}
	
	public ResponseData getLicense(String serverHome, String version, RequestData requestData) throws InvalidLicenseKeyException {
		MultiLicenseManager licenseManager = new MultiLicenseManager();
		licenseManager.add(new File(serverHome + LIC_FILE_RELATIVE_PATH), version);
		//STEP 1 - is license already allocated
		List<LicenseDaoData> fetchActive;
		try {
			fetchActive = licenseDAO.fetchBy(
					encrypt(requestData.getServerName()), 
					encrypt(REGISTERED));
			decrypt(fetchActive);
		} catch (DaoException e) {
			return dbFailureResponse(e);
		}
		if (fetchActive.isEmpty() == false) {
			return createWith(ResponseData.ALREADY_ALLOCATED, new String(fetchActive.get(0).getLicense()));
		}
		
		//STEP 2 - is licensing limit reached
		int activeLicensesCount;
		try {
			activeLicensesCount = licenseDAO.count(encrypt(REGISTERED));
		} catch (DaoException e) {
			return dbFailureResponse(e);
		}
		int allowedLicenseCount = Integer.parseInt(licenseManager.getLicenseValue("SYSTEM_MAX_INSTANCES"));
		if (allowedLicenseCount <= activeLicensesCount && 
				allowedLicenseCount != Integer.parseInt(License.UNLIMITED_ACCESS)) {
			return createWith(ResponseData.LICENSE_LIMIT_REACHED, ResponseData.LICENSE_LIMIT_REACHED_MSG);
		}
		
		//STEP 3 - allocate new license
		String plainTextLicense = licenseManager.
				upgrade().
				modifyValue("SYSTEM_NODE", requestData.getData()).
				remove("SYSTEM_MAX_INSTANCES").
				generate().
				format(new LicFormatter(new PlaintextEncryptor()));
		
		//STEP 4 - insert license db
		LicenseDaoData licenseDaoData = new LicenseDaoData();
		try {
			licenseDaoData.setDigest(licenseDigest(plainTextLicense, requestData.getData()));
		} catch (NoSuchAlgorithmException e) {
			// this will never occur
			//TODO change ResponseData.ENCRYPTION_ERROR
			return createWith(ResponseData.ENCRYPTION_ERROR, e.getMessage());
		}
		licenseDaoData.setInstanceName(requestData.getServerName());
		licenseDaoData.setLicense(plainTextLicense.getBytes());
		licenseDaoData.setStatus(REGISTERED);
		
		
		try {
			encrypt(licenseDaoData);
			licenseDAO.insert(licenseDaoData);
		} catch (DaoException e) {
			return dbFailureResponse(e);
		}
		
		return createWith(ResponseData.SUCCESS, plainTextLicense);
	}
	
	public ResponseData validate(RequestData requestData) {
		//STEP 1 - fetch instance record
		List<LicenseDaoData> fetchActive;
		try {
			fetchActive = licenseDAO.fetchBy(encrypt(requestData.getServerName()), encrypt(REGISTERED));
			decrypt(fetchActive);
		} catch (DaoException e) {
			return dbFailureResponse(e);
		}
		
		if (fetchActive.isEmpty()) {
			return createWith(ResponseData.LICENSE_AUTHENTICITY_CHECK_FAILURE, ResponseData.LICENSE_AUTHENTICITY_CHECK_FAILURE_MSG);
		}
		byte[] storedDigest = Bytes.fromHex(fetchActive.get(0).getDigest());
		
		if( storedDigest == null) {
		//TODO vicky: this case will occur when the record in DB has been tampered with. Make a Unique message and possibly a unique code as well.
			return createWith(ResponseData.LICENSE_AUTHENTICITY_CHECK_FAILURE, ResponseData.LICENSE_AUTHENTICITY_CHECK_FAILURE_MSG);
		}
		
		//STEP 2 - create digest from request
		byte[] calculatedDigest;
		try {
			calculatedDigest = digest(requestData.getData().getBytes());
		} catch (NoSuchAlgorithmException e) {
			// this will never occur
			return createWith(ResponseData.LICENSE_AUTHENTICITY_CHECK_FAILURE, e.getMessage());
		}
		
		//STEP 3 - compare digest
		if (MessageDigest.isEqual(storedDigest, calculatedDigest) == false) {
			return createWith(ResponseData.LICENSE_AUTHENTICITY_CHECK_FAILURE, ResponseData.LICENSE_AUTHENTICITY_CHECK_FAILURE_MSG);
		}
		
		return createWith(ResponseData.LICENSE_AUTHENTICITY_CHECK_SUCCESS, ResponseData.LICENSE_AUTHENTICITY_CHECK_SUCCESS_MSG);
	}
	
	public List<AllocatedLicenseRecordInfo> getLicensedInstances() throws DaoException {
		List<LicenseDaoData> records = null;
			records = licenseDAO.fetchAll(encrypt("REGISTERED"));
			decrypt(records);
			return from(records);
	}
	
	public List<PresentableLicenseData> getLicense(String id) throws DaoException, InvalidLicenseKeyException {
		LicenseDaoData licenseDaoData = licenseDAO.fetchBy(id);
		if (licenseDaoData == null) {
			return null;
		}
		decrypt(licenseDaoData);
		return from(licenseDaoData);
	}
	
	public void deregister (String name) throws DaoException {
		licenseDAO.updateStatus(encrypt(name), encrypt(DEREGISTERED), name);
	}
	
	private List<PresentableLicenseData> from(LicenseDaoData licenseDaoData) throws InvalidLicenseKeyException{
		String plainTextKey = new String(licenseDaoData.getLicense());
		StringTokenizer licenseKeyTokenizer = new StringTokenizer(plainTextKey, LicenseConstants.LICENSE_SEPRATOR);
		List<String> lstDecryptedLicenseKey = new ArrayList<String>();
		while (licenseKeyTokenizer.hasMoreTokens()) {
			lstDecryptedLicenseKey.add(licenseKeyTokenizer.nextToken());
		}	
		
		Map<String, LicenseData> licenseDataMap = SystemUtil.getLicenseDataList(lstDecryptedLicenseKey, false);
		List<PresentableLicenseData> presentableLicenseDatas = new ArrayList<PresentableLicenseData>();
		for(Entry<String, LicenseData> entry: licenseDataMap.entrySet()) {
			PresentableLicenseData presentableLicenseData = new PresentableLicenseData();
			presentableLicenseData.setName(entry.getValue().getDisplayName());
			presentableLicenseData.setVersion(entry.getValue().getVersion());
			presentableLicenseData.setValue(entry.getValue().getValue());
			presentableLicenseData.setType(entry.getValue().getType());
			presentableLicenseDatas.add(presentableLicenseData);
		}
		
		return presentableLicenseDatas;
	}
	 
	private List<AllocatedLicenseRecordInfo> from(List<LicenseDaoData> records) {
		List<AllocatedLicenseRecordInfo> allocatedLicenseRecordInfos = new ArrayList<AllocatedLicenseRecordInfo>();
		AllocatedLicenseRecordInfo allocatedLicenseRecordInfo = null;
		for (LicenseDaoData licenseDaoData : records) {
				allocatedLicenseRecordInfo = new AllocatedLicenseRecordInfo(
				licenseDaoData.getId(),
				licenseDaoData.getInstanceName(),
				licenseDaoData.getStatus());
				
				allocatedLicenseRecordInfos.add(allocatedLicenseRecordInfo);
		}
		
		return allocatedLicenseRecordInfos;
	}

	private byte[] digest(byte[] bytes) throws NoSuchAlgorithmException {
		return MessageDigest.getInstance(SHA_512).digest(bytes);
	}
	
	/**
	 * @return List of LicenseData, each LicenseData represents a row of TBLMSMLICENSE
	 * 
	 */
	private ResponseData createWith(int code, String message) {
		ResponseData responseData = new ResponseData();
		responseData.setMessageCode(code);
		responseData.setMessage(message);
		return responseData;
	}
	
	private ResponseData dbFailureResponse(DaoException e) {
		return createWith(ResponseData.DB_FAILURE, e.getMessage());
	}
	
	private void encrypt(LicenseDaoData licenseData) throws DaoException {
		licenseData.setLicense(encrypt(new String(licenseData.getLicense())).getBytes());
		licenseData.setInstanceName(encrypt(licenseData.getInstanceName()));
		licenseData.setDigest(encrypt(licenseData.getDigest()));
		licenseData.setStatus(encrypt(licenseData.getStatus()));
	}
	
	private String encrypt(String plainString) throws DaoException { 
		try {
			return AES.encrypt(plainString);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		}
	}

	private void decrypt(List<LicenseDaoData> licenseDaoDatas) throws DaoException {
		for (LicenseDaoData licenseDaoData : licenseDaoDatas) {
			decrypt(licenseDaoData);
		}
	}

	private void decrypt(LicenseDaoData licenseData) throws DaoException {
		licenseData.setLicense(decrypt(new String(licenseData.getLicense())).getBytes());
		licenseData.setInstanceName(decrypt(licenseData.getInstanceName()));
		licenseData.setDigest(decrypt(licenseData.getDigest()));
		licenseData.setStatus(decrypt(licenseData.getStatus()));
	}
	
	private String decrypt(String encrypted) throws DaoException {
		try {
			return AES.decrypt(encrypted);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		}
	}
	
	private String licenseDigest(String license, String publicKey) throws NoSuchAlgorithmException {
		return Bytes.toHex(digest((license + publicKey).getBytes()));
	}
	
}
