package com.elitecore.aaa.license.nfv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.elitecore.license.base.MultiLicenseManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.license.AAALicenseManager;
import com.elitecore.aaa.license.LicenseExpiryListener;
import com.elitecore.aaa.license.nfv.packets.HttpPostRequestBuilder;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.base.exception.licensetype.InvalidLicenseException;
import com.elitecore.license.formatter.LicFormatter;
import com.elitecore.license.nfv.RequestData;
import com.elitecore.license.nfv.ResponseData;
import com.elitecore.license.publickey.ElitePublickeyGenerator;

import net.sf.json.JSONObject;

/**
 * License manager in case a centralized license is obtained from SM.
 * It acquires license from server manager, loads it and schedules
 * authenticity validation tasks.
 * 
 * <p>
 * It acquires license by making web service calls to server manager.
 * Whenever a critical action happens like allocation of license,
 * validation failure it also generates critical level alerts.
 * </p>
 * 
 * @author vicky.singh
 */
public class NFVLicenseManager implements AAALicenseManager {
	
	private static final int STATUS_SUCCESS_CODE = 200;
	private static final String MODULE = "NFV-LIC-MANAGER";
	private final AAAServerContext aaaServerContext;
	private final String licenseFileName;
	private final LicenseExpiryListener expiryListener;
	private MultiLicenseManager licenseManager;

	public NFVLicenseManager(AAAServerContext serverContext, LicenseExpiryListener expiryListener) {
		this.aaaServerContext = serverContext;
		this.licenseFileName = serverContext.getServerHome() + File.separator + LicenseConstants.LICENSE_DIRECTORY + 
				File.separator + LicenseConstants.NFV_LICENSE_FILE_NAME + LicenseConstants.LICESE_FILE_EXT;
		this.expiryListener = expiryListener;
	}

	@Override
	public void init() throws InvalidLicenseException {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Contacting SM for license.");
		}
		try {
			usingPost();
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Received license from Server Manager.");
			}
			licenseManager = new MultiLicenseManager();
			licenseManager.add(new File(this.licenseFileName), aaaServerContext.getServerMajorVersion());
		} catch (IOException e) {
			LogManager.getLogger().warn(MODULE, "Unable to communicate with SM, Reason: " + e.getMessage() + 
					"." + " Trying to load stored license.");
			licenseManager = new MultiLicenseManager();
			licenseManager.add(new File(this.licenseFileName), aaaServerContext.getServerMajorVersion());
		} catch (Exception e) {
			LogManager.getLogger().warn(MODULE, "SM denied license request, Reason: " + e.getMessage());
			licenseManager = new MultiLicenseManager();
			throw new InvalidLicenseException("SM Denied license request, Reason: " + e.getMessage(), e);
		}
	}

	@Override
	public void startLicenseValidationTask() {
		LicenseAuthenticityValidator validator = new LicenseAuthenticityValidator(aaaServerContext, expiryListener, licenseManager);
		aaaServerContext.getTaskScheduler().scheduleIntervalBasedTask(validator);
	}

	@Override
	public void removeLicenseFile() {
		File file = new File(licenseFileName);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public boolean validateLicense(String key, String value) {
		return licenseManager.validateLicense(key, value);
	}
	
	@Override
	public String getLicenseKey() {
		return licenseManager.upgrade().generate().format(new LicFormatter());
	}
	
	private String usingPost() throws Exception {
		String inputLine = "";
		BufferedReader in = null;
		try {
			ElitePublickeyGenerator elitePublickeyGenerator = new ElitePublickeyGenerator(ElitePublickeyGenerator.PLAIN_TEXT_FORMAT);
			String data = elitePublickeyGenerator.generatePublicKey(aaaServerContext.getServerHome(), LicenseConstants.DEFAULT_ADDITIONAL_KEY, aaaServerContext.getServerInstanceId(), aaaServerContext.getServerInstanceName());

			HttpClient httpClient = new DefaultHttpClient();

			WebServiceParams webServiceParams = new WebServiceParams(aaaServerContext.getServerHome() + File.separator + "system");
			webServiceParams.readDetails();
			HttpPost httpPost = new HttpPostRequestBuilder("http", webServiceParams.getAddress(), webServiceParams.getPort(), webServiceParams.getContextPath() + "/cxfservices/restful/v1" + "/server/elitecsmserver/license")
					.authDetails(webServiceParams.getUserName(), webServiceParams.getPassword())
					.body(new RequestData(aaaServerContext.getServerInstanceName(), aaaServerContext.getServerInstanceId(), data, aaaServerContext.getServerMajorVersion()))
					.build();

			HttpResponse httpResponse = httpClient.execute(httpPost);
			in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			inputLine  = in.readLine();
			if (httpResponse.getStatusLine().getStatusCode() != STATUS_SUCCESS_CODE) {
				//TODO send only the required content instead of full body of response once framework for spring exception mapper is ready
				throw new InvalidLicenseException(inputLine);
			}
			ResponseData responseData = (ResponseData) JSONObject.toBean(JSONObject.fromObject(inputLine), ResponseData.class);
			if(responseData.getMessageCode() == ResponseData.SUCCESS || responseData.getMessageCode() == ResponseData.ALREADY_ALLOCATED) {
				//TODO vicky : The message doesn't clearly defines is it a fresh allocation or a pre allocated copy ?
				this.aaaServerContext.generateSystemAlert(AlertSeverity.INFO, Alerts.NFV_LICENSE_RECEIVED, MODULE, 
						"Successfully Received license from SM");
				writeToFile(responseData);
				return responseData.getMessage();
			} else  {
				this.aaaServerContext.generateSystemAlert(AlertSeverity.ERROR, Alerts.NFV_LICENSE_DENIED, MODULE, 
						" SM denied request for license");
				throw new InvalidLicenseException(responseData.getMessage());
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void writeToFile(ResponseData responseData) throws IOException {
		File licenseFile = null;
		FileOutputStream licenseFileStream = null;
		
		try {
			licenseFile = new File(this.licenseFileName);
			if (licenseFile .exists() == false) {
				if (licenseFile.getParentFile().exists() == false) {
					licenseFile.getParentFile().mkdirs();
				}
				licenseFile.createNewFile();
			}
			licenseFileStream = new FileOutputStream(licenseFile); //NOSONAR - Reason: Resources should be closed
			licenseFileStream.write(responseData.getMessage().getBytes());
		} finally {
			if (licenseFileStream != null) { 
				licenseFileStream.flush();
				licenseFileStream.close();
			}
		}
	}

	@Override
	public String getLicenseValue(String key) {
		return licenseManager.getLicenseValue(key);
	}
	
}
