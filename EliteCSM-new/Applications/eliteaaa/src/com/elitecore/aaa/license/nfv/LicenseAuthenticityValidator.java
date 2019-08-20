package com.elitecore.aaa.license.nfv;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.license.EliteExpiryDateValidationTask;
import com.elitecore.aaa.license.LicenseExpiryListener;
import com.elitecore.aaa.license.nfv.packets.HttpPostRequestBuilder;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.license.base.MultiLicenseManager;
import com.elitecore.license.base.commons.LicenseConstants;
import com.elitecore.license.nfv.RequestData;
import com.elitecore.license.nfv.ResponseData;
import com.elitecore.license.publickey.ElitePublickeyGenerator;
import com.elitecore.license.util.SystemUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Server level scheduled task which validates the centralized license received
 * from server manager is authentic or not on hourly basis. If license authenticity
 * validation fails, all the services are stopped.
 * 
 * @author vicky.singh
 *
 */
public class LicenseAuthenticityValidator extends EliteExpiryDateValidationTask {

	private static final int MAX_UNSUCCESSFULL_SM_CONNECTION_ATTEMPT = 72;
	private static final int STATUS_SUCCESS_CODE = 200;

	private static final String MODULE = "LICENSE AUTHENTICITY VALIDATOR";

	private int unsuccessfullSmConnectionAttempt = 0;
	private String validationKey;

	public LicenseAuthenticityValidator(AAAServerContext serverContext, LicenseExpiryListener expiryListener, MultiLicenseManager licenseManager) {
		super(serverContext, expiryListener, licenseManager);
		this.validationKey = setValidationKey();
	}

	private String setValidationKey() {
		StringBuilder builder = new StringBuilder();
		File licFile = new File(serverContext.getServerHome() + File.separator + LicenseConstants.LICENSE_DIRECTORY + File.separator + LicenseConstants.NFV_LICENSE_FILE_NAME + LicenseConstants.LICESE_FILE_EXT);
		if(licFile.exists()) {
			String fileContent;
			try {
				fileContent = SystemUtil.readStoredLicense(licFile);
				builder.append(fileContent);
			} catch (FileNotFoundException e) {
				LogManager.getLogger().trace(e);
			} catch (IllegalArgumentException e) {
				LogManager.getLogger().trace(e);
			} catch (IOException e) {
				LogManager.getLogger().trace(e);
			} 
		}
		ElitePublickeyGenerator elitePublickeyGenerator = new ElitePublickeyGenerator(ElitePublickeyGenerator.PLAIN_TEXT_FORMAT);
		String generatePublicKey = elitePublickeyGenerator.generatePublicKey(serverContext.getServerHome(), LicenseConstants.DEFAULT_ADDITIONAL_KEY, serverContext.getServerInstanceId(), serverContext.getServerInstanceName());
		builder.append(generatePublicKey);
		return builder.toString();
	}

	@Override
	public void execute(AsyncTaskContext context) {
		/*
		 * send data to SM in  the format name#plainTextLic+public key 
		 * plain text and public key must be in the same format as needed by sm to hash.
		 * 
		 * on sm side , verify if authentic return true, log it properly
		 * 				if false, log in alerts and restore the original license
		 * if connection with sm fails increment the time since when unable to contact sm 
		 * if that time reaches 72 hours , delete license and shut down.
		 * */
		super.execute(context);
		if (isValidLicence == false) {
			return;	
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Intializing License authenticity validation procedure");
		}

		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost  = null;

		try {
			WebServiceParams webServiceParams = new WebServiceParams(serverContext.getServerHome() + File.separator + "system");
			webServiceParams.readDetails();
			
			httpPost = new HttpPostRequestBuilder("http", webServiceParams.getAddress(), webServiceParams.getPort(), webServiceParams.getContextPath() + "/cxfservices/restful/v1" + "/server/elitecsmserver/license/validate")
					.authDetails(webServiceParams.getUserName(), webServiceParams.getPassword())
					.body(new RequestData(serverContext.getServerInstanceName(), serverContext.getServerInstanceId(),
							validationKey, serverContext.getServerMajorVersion()))
					.build();
		} catch (Exception e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Failed to communicate with SM, Reason: " + e.getMessage());
				this.unsuccessfullSmConnectionAttempt++;
			}
		} 			
		
		try {
			HttpResponse response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == STATUS_SUCCESS_CODE) {
				unsuccessfullSmConnectionAttempt = 0;
				String responseContent = new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine();
				ResponseData responseData = (ResponseData) JSONObject.toBean(JSONObject.fromObject(responseContent), ResponseData.class);
				if (responseData.getMessageCode() == ResponseData.LICENSE_AUTHENTICITY_CHECK_SUCCESS) {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "License validated successfully");
					}
				} else if (responseData.getMessageCode() == ResponseData.LICENSE_AUTHENTICITY_CHECK_FAILURE) {
					serverContext.generateSystemAlert(AlertSeverity.CRITICAL, Alerts.NFV_LICENSE_VALIDATION_FAILED, MODULE,
							"License authenticity validation failed");
					
					LogManager.getLogger().error(MODULE, "License validation failed, Reason: " + responseData.getMessage() +
							" Stopping all services.");
					stop();
				}
			} else {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Failed to communicate with SM, Reason: " + 
							response.getStatusLine().getReasonPhrase());
					this.unsuccessfullSmConnectionAttempt++;
				}
			}
		} catch (ClientProtocolException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Failed to communicate with SM, Reason: " + e.getMessage());
				this.unsuccessfullSmConnectionAttempt++;
			}
		} catch (IOException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Failed to communicate with SM, Reason: " + e.getMessage());
				this.unsuccessfullSmConnectionAttempt++;
			}
		}
		if (this.unsuccessfullSmConnectionAttempt >= MAX_UNSUCCESSFULL_SM_CONNECTION_ATTEMPT) {
			stop();
		}
	}

	private void stop() {
		expiryListener.execute();
		isValidLicence = false;
	}

	@Override
	public void preExecute(AsyncTaskContext context) {

	}

	@Override
	public void postExecute(AsyncTaskContext context) {

	}

}
