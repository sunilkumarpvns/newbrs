package com.elitecore.core.notification.sms.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.SMSNotifierKeyReplacer;
import com.elitecore.core.notification.sms.SMSNotifier;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;

public class SMSHTTPNotifier extends ESCommunicatorImpl implements SMSNotifier {

	private String MODULE = "SMS-NTFR";

	private StringBuilder serviceURL;
	private SMSHTTPConfiguration configuration;

	private final String ENCODING_TYPE_UTF_8 = "UTF-8";
	private PoolingClientConnectionManager httpConnectionManager;
	private HttpClient httpClient;


	public SMSHTTPNotifier(SMSHTTPConfiguration configuration, TaskScheduler taskScheduler) {
		super(taskScheduler);
		this.configuration = configuration;
		httpConnectionManager = new PoolingClientConnectionManager();
		httpClient = new DefaultHttpClient(httpConnectionManager);
	}

	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initializing SMSHTTP Notifier");
		
		if(configuration.getServiceURL() == null) {
			LogManager.getLogger().error(MODULE, "Error while initializing SMS HTTP Notifier. Reason: Service URL not found");
			throw new InitializationFailedException("Invalid Service URL");
		}
		super.init();
		int maxConnection = 50;
		String strMaxConnection = System.getProperty("http.connections");
		if(strMaxConnection != null){
			try{
				maxConnection = Integer.parseInt(strMaxConnection);
			}catch(Exception e){
				LogManager.getLogger().error(MODULE, "Invalid value for property http.connection:" + strMaxConnection);
			}
		}
		LogManager.getLogger().info(MODULE, "Max HTTP Connections: " + maxConnection );
		httpConnectionManager.setMaxTotal(maxConnection);

		serviceURL = new StringBuilder(configuration.getServiceURL());

		try {
			SMSNotifierKeyReplacer.USERNAME.replace(serviceURL, configuration.getUserName(), ENCODING_TYPE_UTF_8);
			SMSNotifierKeyReplacer.PASSWORD.replace(serviceURL, configuration.getPassword(), ENCODING_TYPE_UTF_8);
			SMSNotifierKeyReplacer.SENDER.replace(serviceURL, configuration.getSender(), ENCODING_TYPE_UTF_8);
			SMSNotifierKeyReplacer.ISFLASH.replace(serviceURL, configuration.isFlash(), ENCODING_TYPE_UTF_8);
			SMSNotifierKeyReplacer.DLR_URL.replace(serviceURL, configuration.getDeliveryURL(), ENCODING_TYPE_UTF_8);
			SMSNotifierKeyReplacer.PARAM1.replace(serviceURL, configuration.getParam1(), ENCODING_TYPE_UTF_8);
			SMSNotifierKeyReplacer.PARAM2.replace(serviceURL, configuration.getParam2(), ENCODING_TYPE_UTF_8);
			SMSNotifierKeyReplacer.PARAM3.replace(serviceURL, configuration.getParam3(), ENCODING_TYPE_UTF_8);

		} catch(UnsupportedEncodingException e) {
			throw new InitializationFailedException("Error while encoding URL", e);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				LogManager.getLogger().info(MODULE, "Shutting Down HTTP Connection Manager");
				httpClient.getConnectionManager().shutdown();
			}
		});
		LogManager.getLogger().info(MODULE, "SMSHTTP Notifier Initialized SuccessFully");
	}

	@Override
	public boolean send(String message, String number) {
		return send(message, number, null);
	}
	
	@Override
	public boolean send(String message, String number, Map<SMSNotifierKeyReplacer, String> keyReplacerMap) {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Sending SMS over HTTP");

		StringBuilder tmpServiceURL = new StringBuilder(serviceURL);
		if(keyReplacerMap != null && !keyReplacerMap.isEmpty()){
			for(Map.Entry<SMSNotifierKeyReplacer, String> entry : keyReplacerMap.entrySet()){
			try {
					entry.getKey().replace(tmpServiceURL, entry.getValue(), ENCODING_TYPE_UTF_8);
			} catch (UnsupportedEncodingException e) {
				LogManager.getLogger().error(MODULE, "Error while encoding URL. Reason: " + e);
				LogManager.getLogger().trace(MODULE, e);
				return false;
			}
		}
		}
			try {
			SMSNotifierKeyReplacer.MSGTXT.replace(tmpServiceURL, message, ENCODING_TYPE_UTF_8);
			SMSNotifierKeyReplacer.RECIPIENT.replace(tmpServiceURL, number, ENCODING_TYPE_UTF_8);
			} catch (UnsupportedEncodingException e) {
				LogManager.getLogger().error(MODULE, "Error while encoding URL. Reason: " + e);
				LogManager.getLogger().trace(MODULE, e);
				return false;
			}

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Sending SMS Notification to " + number);
		
		String response = submit(tmpServiceURL.toString());

		try {
			if(response != null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Response: " + URLDecoder.decode(response, ENCODING_TYPE_UTF_8));
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogger().error(MODULE, "Error while encoding response. Reason: " + e);
			LogManager.getLogger().trace(MODULE, e);
		}
		return false;
	}

	private String submit(String serviceUrl) {
		String response = null;
		HttpGet httpget = new HttpGet(serviceUrl);
		try {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Sending HTTP Get Request: " + httpget.toString());
			HttpResponse httpResponse = httpClient.execute(httpget);
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
                byte[] bytes = EntityUtils.toByteArray(entity);
                response = new String(bytes);
            }else{
            	LogManager.getLogger().error(MODULE, "Error in getting HTTP data.  HTTP Response Code: " + httpResponse.getStatusLine().getStatusCode());
            }
		} catch (ClientProtocolException e) {
			LogManager.getLogger().error(MODULE, "Error in sending sms. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error in sending sms. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return response;
	}
	

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	public void scan() {
		
	}

	@Override
	protected int getStatusCheckDuration() {
		return ESCommunicator.NO_SCANNER_THREAD;
	}

	@Override
	public String getName() {
		return MODULE;
	}

	@Override
	public String getTypeName() {
		return MODULE;
	}


}
