package com.elitecore.core.notification.sms.smpp;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.SMSNotifierKeyReplacer;
import com.elitecore.core.notification.sms.SMSNotifier;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;

/**
 * @author Manjil Purohit
 *
 */
public class SMPPNotifier extends ESCommunicatorImpl implements SMSNotifier {

	private static final String MODULE = "SMPP-NTFR";

	private static final int STATUS_CHECK_DURATION = 60;  // in seconds
	private static final byte PRIORIRY_FLAG_LEVEL_1 = 1;
	private static final byte DONT_REPLACE = 0;
	private static final byte DEFAULT_MSG_ID_RESERVED = 0;
	private static final byte PROTOCOL_ID_GSM = 0;
	private static final String SERVICE_TYPE = "CMT";	// For Cellular Messaging
	
	private String ipAddress;
	private int port;
	
	private String sourceAddress;
	private TypeOfNumber destAddrTON;
	private NumberingPlanIndicator destAddrNPI;
	private TypeOfNumber srcAddrTON;
	private NumberingPlanIndicator srcAddrNPI;
	
	private ESMClass esmClass;
	private GeneralDataCoding dataCoding;
	private SMPPSession session;
	private TimeFormatter timeFormatter;
	private SMPPConfiguration configuration;
	private RegisteredDelivery registeredDelivary;
	
	private AtomicInteger timeoutCnt;
	
	public SMPPNotifier(SMPPConfiguration configuration, final ServerContext serverContext) {
		super(serverContext.getTaskScheduler());
		this.configuration = configuration;
		sourceAddress = configuration.getSourceAddress();
		srcAddrNPI = configuration.getSourceAddrNPI();
		srcAddrTON = configuration.getSourceAddrTON();
		destAddrNPI = configuration.getDestAddrNPI();
		destAddrTON = configuration.getDestAddrTON();
		timeFormatter = new AbsoluteTimeFormatter();
		timeoutCnt = new AtomicInteger();
	}

	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initializing SMPP Notifier");
		try {
			URLData urlData = URLParser.parse(configuration.getConnectionURL());
			ipAddress = urlData.getHost(); 
			port = urlData.getPort();
			
			if(ipAddress == null || ipAddress.isEmpty()) {
				throw new InvalidURLException("Invalid IP Address: " + ipAddress);
			}
			
			if(port <= 0) {
				throw new InvalidURLException("Invalid port: " + port);
			}
			
			if(configuration.getUserName() == null || configuration.getUserName().isEmpty()) {
				throw new Exception("Invalid username");
			}
			
			super.init();
			
			//	No SMSC Delivery Receipt requested
			registeredDelivary = new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT);
			
			esmClass = new ESMClass();
			esmClass.setMessageMode(configuration.getMessageMode());

			boolean compressed = false; 
			boolean containMessageClass = true;
			dataCoding = new GeneralDataCoding(compressed, containMessageClass, MessageClass.CLASS1, Alphabet.ALPHA_DEFAULT);
			
			try {
				String systemId = connectAndBind();
				if(systemId == null) {
		        	markDead();
		        } 
			} catch (IOException e) {
		    	LogManager.getLogger().error(MODULE, "Error while binding with SMSC. Reason: " + e.getMessage());
		    	LogManager.getLogger().trace(MODULE, e);
		    	markDead();
		    }
			LogManager.getLogger().info(MODULE, "SMSHTTP Notifier Initialized SuccessFully");
		} catch (Exception e) {
			throw new InitializationFailedException("Error while initializing SMPP Notifier. Reason: " + e.getMessage(), e);
		}
	}

	private String connectAndBind() throws IOException {
		/*
		 * default TCP/IP socket timeout is 5000 ms
		 * default transaction timeout 60000 ms
		 */
		if(session != null) {
			session.unbindAndClose();
		}
		String addressRange = null;
		session = new SMPPSession();
		BindParameter bindParameters = new BindParameter(BindType.BIND_TX, configuration.getUserName(), configuration.getPassword(), 
				configuration.getSystemType(), configuration.getSourceAddrTON(), configuration.getSourceAddrNPI(), addressRange);
		session.setTransactionTimer(configuration.getTimeout());

		return session.connectAndBind(ipAddress, port, bindParameters, configuration.getTimeout());
    } 

	/**
	 * Method used to send short message to SMSC and returns status of message whether successfully submitted to SMC or not.
	 * @param message
	 * @param number 
	 */
	@Override
	public boolean send(String message, String number) {
        try {
			String messageId = session.submitShortMessage(SERVICE_TYPE, srcAddrTON, srcAddrNPI, sourceAddress, 
            		destAddrTON, destAddrNPI, number, esmClass, PROTOCOL_ID_GSM, PRIORIRY_FLAG_LEVEL_1, timeFormatter.format(new Date()), 
            		getValidityPeriod(), registeredDelivary, DONT_REPLACE, dataCoding, DEFAULT_MSG_ID_RESERVED, message.getBytes());
            
			timeoutCnt.set(0);

			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
            	LogManager.getLogger().debug(MODULE, "Message submitted, message_id is " + messageId);

            return true;
        } catch (PDUException e) {
        	LogManager.getLogger().error(MODULE, "SMS sending failed for " + number + ". Reason: Invalid PDU parameter");
            LogManager.getLogger().trace(MODULE, e);
        } catch (ResponseTimeoutException e) {
        	LogManager.getLogger().error(MODULE, "SMS sending failed for " + number + ". Reason: Response timeout");
        	if(configuration.getMaxTimeoutCount() != ESCommunicator.ALWAYS_ALIVE) {
	        	int count = timeoutCnt.incrementAndGet();
	        	if(count >= configuration.getMaxTimeoutCount() && isAlive()) {
					LogManager.getLogger().error(MODULE, "Marking SMSC " + configuration.getConnectionURL() + " as DEAD. Reason: Timeout request count: " + timeoutCnt.get());
	        		markDead();
	        	}
        	}
        } catch (InvalidResponseException e) {
        	LogManager.getLogger().error(MODULE, "SMS sending failed for " + number + ". Reason: Receive invalid respose");
        	LogManager.getLogger().trace(MODULE, e);
        	
        	timeoutCnt.set(0);
        } catch (NegativeResponseException e) {
        	LogManager.getLogger().error(MODULE, "SMS sending failed for " + number + ". Reason: Receive negative response");
        	LogManager.getLogger().trace(MODULE, e);
        	
        	timeoutCnt.set(0);
        } catch (IOException e) {
        	LogManager.getLogger().error(MODULE, "SMS sending failed for " + number + ". Reason: IO error occur");
        	LogManager.getLogger().trace(MODULE, e);
        	
			LogManager.getLogger().error(MODULE, "Marking SMSC " + configuration.getConnectionURL() + " as DEAD. Reason: " + e.getMessage());
        	markDead();
        } catch (Exception e) {
        	LogManager.getLogger().error(MODULE, "SMS sending failed for " + number + ". Reason: " + e.getMessage());
        	LogManager.getLogger().trace(MODULE, e);
        } 
		return false;
	}
	
	
	/**
	 * Replacement of Additional Parameter is not supported in SMPP Notifier
	 * @see com.elitecore.core.notification.sms.SMSNotifier#send(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public boolean send(String message, String number, Map<SMSNotifierKeyReplacer, String> additionKeyReplacerMap) {
		return send(message, number);
	}
	
	private String getValidityPeriod() {
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR_OF_DAY, currentDate.get(Calendar.HOUR_OF_DAY) + configuration.getValidityPeriod());
		return timeFormatter.format(currentDate);
	}

	@Override
	public void scan() {
		try {
			String systemId = connectAndBind();
			if(systemId != null) {
				timeoutCnt.set(0);
	        	markAlive();
	        } 
			
			if(isAlive()) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Succsessfully connected and binded with SMSC server");
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Could not bind SMSC. Reason: SMSC server is still down");
			}
			
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error while binding with SMSC. Reason: " + e.getMessage());
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while binding with SMSC. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	@Override
	protected int getStatusCheckDuration() {
		return STATUS_CHECK_DURATION;
	}

	@Override
	public void stop() {
		super.stop();
		if(session != null) {
			session.unbindAndClose();
		}
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
