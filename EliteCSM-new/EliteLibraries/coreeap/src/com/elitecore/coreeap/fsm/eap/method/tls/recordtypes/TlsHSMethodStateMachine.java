package com.elitecore.coreeap.fsm.eap.method.tls.recordtypes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.commons.util.cipher.ICipherProvider;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.data.tls.TLSSecurityKeys;
import com.elitecore.coreeap.data.tls.TLSSecurityParameters;
import com.elitecore.coreeap.dictionary.tls.TLSRecordTypeDictionary;
import com.elitecore.coreeap.packet.TLSAlertException;
import com.elitecore.coreeap.packet.types.tls.TLSException;
import com.elitecore.coreeap.packet.types.tls.record.ITLSRecord;
import com.elitecore.coreeap.packet.types.tls.record.TLSPlaintext;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ContentType;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.packet.types.tls.record.types.ITLSRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Certificate;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.CertificateRequest;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.CertificateVerify;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ClientHello;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ClientKeyExchange;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Finished;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.HandshakeMessageRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ITLSHandshakeMessage;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ServerHello;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ServerKeyExchange;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Tls1_2CertificateRequest;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.Tls1_2CertificateVerify;
import com.elitecore.coreeap.session.IEAPMethodSession;
import com.elitecore.coreeap.session.method.tls.TLSConnectionState;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.events.HandshakeEvents;
import com.elitecore.coreeap.util.constants.fsm.states.HandshakeStates;
import com.elitecore.coreeap.util.constants.tls.HandshakeMessageConstants;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.constants.tls.SignatureAlgorithm;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;
import com.elitecore.coreeap.util.constants.tls.alert.TLSAlertDescConstants;
import com.elitecore.coreeap.util.constants.tls.alert.TLSAlertLevelConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;
import com.elitecore.coreeap.util.tls.keyexchange.KeyExchange;
import com.elitecore.coreeap.util.tls.signature.Signature;
import com.elitecore.coreeap.util.tls.signature.Tls1_2DSASignature;
import com.elitecore.coreeap.util.tls.signature.Tls1_2RSASignature;

public class TlsHSMethodStateMachine extends BaseMethodTypesStateMachine{

	public static final String MODULE = "HANDSHAKE STATE MACHINE";
	private Queue<ITLSRecordType> responseRecordQueue = new LinkedBlockingQueue<ITLSRecordType>();
	private String oui = null;
	private boolean isSendCertificateRequired;
	private int iSessionResumptionLimit=0; 
	public final static String ALL_HANDSHAKE_MESSAGES = "ALL_HANDSHAKE_MESSAGES";
	private final static byte[] PMS = {0x03,0x01,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x10
		,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1a,0x1b,0x1c,0x1d,0x1e,0x1f,0x20,0x21,0x22,0x23,0x24,0x25,0x26,0x27,0x28,0x29,0x2a
		,0x2b,0x2c,0x2d,0x2e};
	private static final int SERVER_RANDOM_SIZE_IN_BYTE = 32;

	private ICustomerAccountInfo customerAccountInfo = null;
	private String failureReason;
	private byte[] certificateMessageBytes = null;
	private int method;
	private KeyExchange keyExchange;
	private Signature signature;
	
	public TlsHSMethodStateMachine(IEapConfigurationContext eapConfigurationContext, int method) {
		super(eapConfigurationContext);
		changeCurrentState(HandshakeStates.INITIALIZED);
		iSessionResumptionLimit=getEapConfigurationContext().getSessionResumptionLimit();
		this.method = method;
	}

	public boolean check(ITLSRecord tlsRecord) {
		// TODO Auto-generated method stub
		return true;
	}

	public void process(ITLSRecord tlsRecord,ICustomerAccountInfoProvider provider) {	
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "TLS handshake packet being processed in state :" + getCurrentState());		
		reset();
		TLSPlaintext tlsPlaintextRecord = (TLSPlaintext)tlsRecord;
		Collection<ITLSRecordType> handshakeMessages = tlsPlaintextRecord.getContent();
		Iterator<ITLSRecordType> handshakeMessageIterator = handshakeMessages.iterator();
		while(handshakeMessageIterator.hasNext()){
			ITLSRecordType tlsRecordType = handshakeMessageIterator.next();		
			HandshakeEvents event = (HandshakeEvents)getEvent(tlsRecordType);
			setCurrentEvent(event);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "TLS handshake event raised : "+ event);
			handleEvent(event, tlsRecordType,provider);
			IEnum state = getNextState(event);
			changeCurrentState(state);
		}							
	}

	public void reset() {
		setSuccess(false);
		setFailure(false);
		setDone(false);
		clearCustomerAccountInfo();
	}
	
	public IEnum getNextState(IEnum event){

		switch((HandshakeEvents)event){
		case HSCertificateReceived:
			return HandshakeStates.CLIENT_KEY_EXCHANGE;
		case HSCertificateVerifyReceived:
			return HandshakeStates.FINISHED;			
		case HSClientHelloReceived:
			return HandshakeStates.SERVERHELLO_DONE;
		case HSClientKeyExchangeReceived:
			if(isSendCertificateRequest()){
				return HandshakeStates.CERTIFICATE_VERIFY;
			}else{
				return HandshakeStates.FINISHED;
			}
		case HSSuccess:
			return HandshakeStates.SUCCESS;
		case HSFinishedReceived:
			return HandshakeStates.FINISHED;
		case HSGenerateAlert:
			if(isSuccess()){
				return HandshakeStates.SUCCESS;
			}else if(isFailure()){
				return HandshakeStates.FAILURE;
			}
			return HandshakeStates.ALERT;
		case HSGenerateCertificate:
			if(isSendCertificateRequest()){
				return HandshakeStates.CERTIFICATE_REQUEST;
			}else {
				return HandshakeStates.SERVERHELLO_DONE;
			}
		case HSGenerateCertificateRequest:
			return HandshakeStates.SERVERHELLO_DONE;
		case HSGenerateServerHelloDone:
			//Can not define any state after the server hello done.
			break;
		case HSClientHelloWithSessionResumptionReceived:
			if(this.method == EapTypeConstants.TTLS.typeId || this.method == EapTypeConstants.PEAP.typeId){
				return HandshakeStates.FINISHED;
			}
			return HandshakeStates.SERVERHELLO_DONE;
		case HSGenerateFinished:
			if(isSuccess()){
				return HandshakeStates.SUCCESS;
			}else if(isFailure()){
				return HandshakeStates.FAILURE;
			}
			break;
		case HSGenerateServerHello:
			return HandshakeStates.CERTIFICATE;
		case HSGenerateServerKeyExchange:
			if(isSendCertificateRequest()){
				return HandshakeStates.CERTIFICATE_REQUEST;
			}else{
				return HandshakeStates.SERVERHELLO_DONE;
			}
		case HSInvalidRequest:
			return HandshakeStates.DISCARD;
		case HSRequestReceived:		 
			return HandshakeStates.RECEIVED;
		default:
		}
		return null;
	}
	
	
	public IEnum getEvent(ITLSRecordType tlsRecord) {
		ITLSHandshakeMessage handshakeMessg = (((HandshakeMessageRecordType)tlsRecord)).getHandshakeMessage();
		switch(HandshakeMessageConstants.get(handshakeMessg.getType())){
		case Hello:
			/***
			 *  we are not supporting Hello(0) Request.
			 *  Right now there is not requirement to start re-negotiating session.
			 *  This message is send by server only.
			 */
			break;
		case ClientHello:

			int iSessionResumedCount = (Integer)getTLSConnectionState().getParameterValue(TLSConnectionState.SESSION_RESUMPTION_COUNTER);
			if(((ClientHello)handshakeMessg).getSessionId()==null || iSessionResumptionLimit == 0){
				setSessionResumed(false);
				getTLSConnectionState().reset();
				return HandshakeEvents.HSClientHelloReceived;
			}
			if(isSessionResumed()){
				return HandshakeEvents.HSGenerateFinishMessageReceived;
			}
			if(iSessionResumptionLimit==-1 || iSessionResumedCount < iSessionResumptionLimit) { 
				//Verify the Session Id of the server and that sent by the client. This must match for session resumption.
				if(Arrays.equals((byte [])getTLSConnectionState().getParameterValue(IEAPMethodSession.SESSION_ID),((ClientHello)handshakeMessg).getSessionId())){
					TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
					//Also the Cipher suite must sent earlier and sent during session resumtpion must be same 
					if(((ClientHello)handshakeMessg).getCiphersuitesList().contains(tlsSecurityParameters.getCipher().code)){
						setSessionResumed(true);
						return HandshakeEvents.HSClientHelloWithSessionResumptionReceived;
					}
				}
			}	
			setSessionResumed(false);
			getTLSConnectionState().reset();
			return HandshakeEvents.HSClientHelloReceived;

		case ServerHello:
			/***
			 * Server hello is send by server only.
			 */
			break;
		case Certificate:
			return HandshakeEvents.HSCertificateReceived;
		case ServerKeyExchange:
			/***
			 * server key exchange is send by server only.
			 */
			break;
		case CertificateRequest:
			/***
			 * certificate request is send by server only.
			 */
		case ServerHelloDone:
			/***
			 * server hello done is send by server only.
			 */
		case CertificateVerify:
			return HandshakeEvents.HSCertificateVerifyReceived;
		case ClientKeyExchange:
			return HandshakeEvents.HSClientKeyExchangeReceived;
		case Finished:					
			return HandshakeEvents.HSFinishedReceived;
		default: 
		}	
		return null;
	}
	
	public IEnum handleEvent(IEnum event, ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider) {
		applyActions(event, tlsRecord, provider);
		return null;
	}
	
//	public void actionOnProcessRequest(ITLSRecordType tlsRecord){
//		Logger.logTrace(MODULE, "CAll - Handshake Action for ProcessRequest, State : " + getCurrentState());		
//	}
//	public void actionOnGenerateAlert(ITLSRecordType tlsRecord){
//		Logger.logTrace(MODULE, "CAll - Handshake Action for GenerateAlert, State : " + getCurrentState());
//	}
	
	/***
	 * This function perform all the task that is related to client hello.
	 * Parse the client hello and update the session parameter according 
	 * to the received ciphersuite.
	 * @param tlsRecord
	 */
	public void actionOnHandleClientHello(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for HandleClientHello, State : " + getCurrentState());
		
		ClientHello clientHello = (ClientHello)((HandshakeMessageRecordType)tlsRecord).getHandshakeMessage();
		byte[] clientRandom = clientHello.getClientRandom();
		
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		TLSSecurityKeys tlsSecurityKeys = (TLSSecurityKeys)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_KEY);
		
		doTLSVersionNegotiation(clientHello, tlsSecurityParameters);
		
//		byte[] tempHandshakeRecordBytes = (byte[])getTLSConnectionState().getParameterValue(IEAPMethodSession.ALL_HANDSHAKE_MESSAGES);
//		tempHandshakeRecordBytes = TLSUtility.appendBytes(tempHandshakeRecordBytes,tlsRecord.getBytes());
		getTLSConnectionState().setParameterValue(IEAPMethodSession.ALL_HANDSHAKE_MESSAGES,tlsRecord.getBytes());
		
		//store a client random in a session.
		tlsSecurityKeys.setClientRandom(clientRandom);
		//store a sessionId into a session
		
		byte[] sessionId = new byte[32];
		if(!isSessionResumed()){
			if(!(getEapConfigurationContext().isTestMode())){
				sessionId = TLSUtility.generateSessionIdentifier();
			}
		}else{
			sessionId=clientHello.getSessionId();
		}
		
		getTLSConnectionState().setParameterValue(IEAPMethodSession.SESSION_ID, sessionId);
		
		List<Integer> receivedCiphersuiteList = clientHello.getCiphersuiteList();		
		int size = receivedCiphersuiteList.size();
		int ciphersuiteCode;		
		List<Integer> supportedCiphersuiteIDs = getEapConfigurationContext().getSupportedCiphersuiteIDs();
		for(int i=0;i<size;i++){
			ciphersuiteCode = receivedCiphersuiteList.get(i);
			if(supportedCiphersuiteIDs.contains(ciphersuiteCode) && 
					CipherSuites.isSupported(ciphersuiteCode, tlsSecurityParameters.getProtocolVersion())){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Selected CipherSuite : "  + CipherSuites.fromCipherCode(ciphersuiteCode)+"("+ ciphersuiteCode +") is BlockCipherSuite");
				/***
				 * Here in the previous version there is a code for assigning 
				 * MAC algorithm, Encryption Type, Key Exchange.
				 * Right now this task has been taken from the application side(Server). 
				 */
				CipherSuites cipher = CipherSuites.fromCipherCode(ciphersuiteCode);
				tlsSecurityParameters.setCipher(cipher);
				ICipherProvider cipherProvider = getEapConfigurationContext().getCipherProvider(cipher, tlsSecurityParameters.getProtocolVersion());
				tlsSecurityParameters.setCipherProvider(cipherProvider);
				tlsSecurityParameters.setPRFAlgorithm(cipherProvider.getHashAlgo());
				tlsSecurityParameters.setMACAlgo(cipherProvider.getMACAlgo());
				createKeyExchangeAndSignature(cipher, tlsSecurityParameters.getProtocolVersion());
				break;
			}
		}
		if(tlsSecurityParameters.getCipher() == null)
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.HANDSHAKE_FAILURE.typeId);
	}
	/**
	 * 	Below method creates new Key Exchange and Signature algorithm.
	 *  Which class to load is selected on the basis of cipher suite code passed as a parameter.
	 * @param cipherCode 
	 * @param tls minor protocol version
	 */
	private void createKeyExchangeAndSignature(CipherSuites cipher, ProtocolVersion protocolVersion) {
		
		PrivateKey priKey = null;
		if(oui != null){
			priKey = getEapConfigurationContext().getServerPrivateKey(oui);
			if(priKey == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Private key for OUI : " + oui + " not found");
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Found private key for OUI : " + oui);
			}
		}
		if (priKey == null) {
			priKey = getEapConfigurationContext().getServerPrivateKey();
			if (priKey != null) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Using default server private key");
				}
			} else {
				LogManager.getLogger().error(MODULE, "Default private key for server not found");
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId, TLSAlertDescConstants.INTERNAL_ERROR.typeId);	
			}
		}
		
		//TODO If new cipher suite is introduced than add the respective Key Exchange and Signature algorithm in below ladder.
		keyExchange = getEapConfigurationContext().getKeyExchangeFactory().createKeyExchange(cipher.getKeyExchangeAlgorithm(), priKey);
		signature = getEapConfigurationContext().getSignatureFactory().createSignature(cipher.getSignatureAlgorithm(), protocolVersion, priKey);
	}
	
	/**
	 * Perform TLS version negotiation while handling Client Hello sent by the client.
	 * if received version is less than minimum version supported by server then Protocol_version alert is generated.
	 * if received version is greater then maximum version supported by the server then server will use maximum version supported. 
	 * @param clientHello
	 */
	private void doTLSVersionNegotiation(ClientHello clientHello, TLSSecurityParameters tlsSecurityParameters) {
		ProtocolVersion minVersion = getEapConfigurationContext().getMinProtocolVersion();
		ProtocolVersion maxVerison = getEapConfigurationContext().getMaxProtocolVersion();
		ProtocolVersion receivedVersion = clientHello.getProtocolVersion();
		ProtocolVersion selectedVersion;
		if(receivedVersion.getMajor() != ProtocolVersion.MAJOR_PROTOCOL_VERSION 
				|| receivedVersion.isSmaller(minVersion)) {
			tlsSecurityParameters.setProtocolVersion(receivedVersion);
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.PROTOCOL_VERSION.typeId);
		} else {
			selectedVersion = receivedVersion.isGreater(maxVerison) ? maxVerison : receivedVersion;
			tlsSecurityParameters.setProtocolVersion(selectedVersion);
		}
		
		
	}
	
	/***
	 * This function generated the server hello message.
	 * Server hello message is generated according to the 
	 * RFC-2246, Section - 7.4.3.1,Server Hello
	 * @param tlsRecord
	 */
	public void actionOnGenerateServerHello(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for GenerateServerHello, State : " + getCurrentState());
		
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		TLSSecurityKeys tlsSecurityKeys = (TLSSecurityKeys)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_KEY);
		ServerHello serverHello = new ServerHello();
		//set protocol version into a serverhello object
		
		serverHello.setProtocolVersion(tlsSecurityParameters.getProtocolVersion());
		//set server random into a serverhello object
		byte[] serverRandom = new byte[SERVER_RANDOM_SIZE_IN_BYTE];
		if(!(getEapConfigurationContext().isTestMode())){
			serverRandom = TLSUtility.generateSecureRandom(SERVER_RANDOM_SIZE_IN_BYTE);
		}
		serverHello.setServerRandom(serverRandom);			
		tlsSecurityKeys.setServerRandom(serverRandom);

		//set session id length into server hello object.
		final int sessionIdLength = ((Integer)getTLSConnectionState().getParameterValue(IEAPMethodSession.SESSION_ID_LENGTH)).intValue();
		serverHello.setSessionIdLength(sessionIdLength);
//		serverHello.setSessionIdLength(0);
		//if the sessionIdlength is 0,then there is not require to add a sessionid.
		if(sessionIdLength > 0){
			serverHello.setSessionId((byte[])getTLSConnectionState().getParameterValue(IEAPMethodSession.SESSION_ID));
		}else{
			serverHello.setSessionId(new byte[0]);
		}
		serverHello.setCiphersuite(tlsSecurityParameters.getCipher().code);
		//here i have assume that the compression method is 0. becuase till the tlsv1.2 compression method is not used.
		serverHello.setCompressionMethod(0);
		
		ITLSRecordType recordType = (ITLSRecordType)TLSRecordTypeDictionary.getInstance().createTLSRecord(TLSRecordConstants.Handshake.value, tlsSecurityParameters.getProtocolVersion());
		((HandshakeMessageRecordType)recordType).setHandshakeMessage(serverHello);
		((HandshakeMessageRecordType)recordType).refreshHeader();

		
		appendTLSRecord(recordType);
		
		addResponseRecord(recordType);
	}
	public void actionOnGenerateCertificateMessage(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider){
 		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for GenerateCertificateMessage, State : " + getCurrentState());
		
		Certificate certificateMessage = new Certificate();
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		
		List<byte[]> certificateChain = null;
		if(oui != null){
			certificateChain = getEapConfigurationContext().getServerCertificateChain(oui);
			if(certificateChain.isEmpty()){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Server certificate chain for OUI: " + oui + " not found, using default server certificate chain");
//				server certificate chain
				certificateChain = getEapConfigurationContext().getServerCertificateChain();
				if(certificateChain.isEmpty()){
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(MODULE, "Default server certificate chain not found");
					throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.INTERNAL_ERROR.typeId);
				}
				certificateMessage.setCertificate(certificateChain);
			} else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Adding vendor specific certificates for OUI " + oui);
//				add vendor server certificate chain
				certificateMessage.setCertificate(certificateChain);
			}
		}else{
			//adding server certificate chain
			certificateChain = getEapConfigurationContext().getServerCertificateChain();
			if(certificateChain.isEmpty()){
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Default server certificate chain not found");
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.INTERNAL_ERROR.typeId);
			}
			certificateMessage.setCertificate(certificateChain);
			
		}
		certificateMessage.resetLength();
		
		ITLSRecordType recordType = (ITLSRecordType)TLSRecordTypeDictionary.getInstance().createTLSRecord(TLSRecordConstants.Handshake.value, tlsSecurityParameters.getProtocolVersion());
		((HandshakeMessageRecordType)recordType).setHandshakeMessage(certificateMessage);
		((HandshakeMessageRecordType)recordType).refreshHeader();

		appendTLSRecord(recordType);
		
		addResponseRecord(recordType);
	}
	
	public void actionOnGenerateServerKeyExchange(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider) {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for GenerateServerKeyExchange, State : " + getCurrentState());
		
		ServerKeyExchange serverKeyExchange = new ServerKeyExchange();
		
		TLSSecurityKeys tlsSecurityKeys = (TLSSecurityKeys)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_KEY);

		byte[] serverParams = keyExchange.generateParameters();
		if(serverParams == null)
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.INTERNAL_ERROR.typeId);
		
		serverKeyExchange.setServerParams(serverParams);
		
		if(signature.isSigningCapable()) {
			try{
				byte[] signatureBytes = signature.sign(serverParams, tlsSecurityKeys);
				serverKeyExchange.setSignature(signatureBytes);
			}catch (TLSException e) {
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.INTERNAL_ERROR.typeId);
			}
		}
		
		ITLSRecordType recordType = (ITLSRecordType)TLSRecordTypeDictionary.getInstance().createTLSRecord(TLSRecordConstants.Handshake.value, ((TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER)).getProtocolVersion());
		((HandshakeMessageRecordType)recordType).setHandshakeMessage(serverKeyExchange);
		((HandshakeMessageRecordType)recordType).refreshHeader();
		
		appendTLSRecord(recordType);

		addResponseRecord(recordType);
	}
	
	public void actionOnGenerateCertificateRequest(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for GenerateCertificateRequest, State : " + getCurrentState());
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		CertificateRequest certificateRequest;
		if(tlsSecurityParameters.getProtocolVersion() == ProtocolVersion.TLS1_2){
			certificateRequest = actionOnGenerateTLSv1_2CertificateRequest(tlsRecord, provider, tlsSecurityParameters);
		}else {
			certificateRequest = actionOnGenerateTLSv1_0and1_1CertificateRequest(tlsRecord, provider, tlsSecurityParameters);
		}
		ITLSRecordType recordType = (ITLSRecordType)TLSRecordTypeDictionary.getInstance().createTLSRecord(TLSRecordConstants.Handshake.value, tlsSecurityParameters.getProtocolVersion());
		((HandshakeMessageRecordType)recordType).setHandshakeMessage(certificateRequest);
		((HandshakeMessageRecordType)recordType).refreshHeader();

		appendTLSRecord(recordType);

		addResponseRecord(recordType);
		
	}
	
	private CertificateRequest actionOnGenerateTLSv1_2CertificateRequest(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider, TLSSecurityParameters tlsSecurityParameters){
		Tls1_2CertificateRequest tls1_2CertificateRequest = new Tls1_2CertificateRequest();
		addTrustedDNList(tls1_2CertificateRequest);
		addSignatureAndHashAlgo(tls1_2CertificateRequest);
		addCertificateTypes(tls1_2CertificateRequest);
		return tls1_2CertificateRequest;
	}

	private CertificateRequest actionOnGenerateTLSv1_0and1_1CertificateRequest(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider, TLSSecurityParameters tlsSecurityParameters){
		CertificateRequest certificateRequest = new CertificateRequest();
		addTrustedDNList(certificateRequest);
		addCertificateTypes(certificateRequest);
		return certificateRequest;
	}
	
	private void addSignatureAndHashAlgo(Tls1_2CertificateRequest tls1_2CertificateRequest) {
		/*
		 *  In TLS 1.2, this functionality has been obsoleted by the supported_signature_algorithms,
		 *  and the certificate type no longer restricts the algorithm used to sign the certificate. 
		 *  For example, if the server sends dss_fixed_dh certificate type and {{sha1, dsa}, {sha1, rsa}} 
		 *  signature types, the client MAY reply with a certificate containing a static DH key, signed with RSA-SHA1.
		 */
		
//		tls1_2CertificateRequest.setSignatureAndHashAlgorithm(HashAlgorithm.SHA512.value, SignatureAlgorithm.RSA.value);
//		tls1_2CertificateRequest.setSignatureAndHashAlgorithm(HashAlgorithm.SHA384.value, SignatureAlgorithm.RSA.value);
//		tls1_2CertificateRequest.setSignatureAndHashAlgorithm(HashAlgorithm.SHA256.value, SignatureAlgorithm.RSA.value);
//		tls1_2CertificateRequest.setSignatureAndHashAlgorithm(HashAlgorithm.SHA224.value, SignatureAlgorithm.RSA.value);
		tls1_2CertificateRequest.setSignatureAndHashAlgorithm(HashAlgorithm.SHA1.getValue(), SignatureAlgorithm.RSA.getValue());
		tls1_2CertificateRequest.setSignatureAndHashAlgorithm(HashAlgorithm.SHA1.getValue(), SignatureAlgorithm.DSA.getValue());
//		tls1_2CertificateRequest.setSignatureAndHashAlgorithm(HashAlgorithm.MD5.value, SignatureAlgorithm.DSA.value);
//		tls1_2CertificateRequest.setSignatureAndHashAlgorithm(HashAlgorithm.MD5.value, SignatureAlgorithm.RSA.value);
		
	}

	
	
	private void addCertificateTypes(CertificateRequest certificateRequest) {
		int[] supportedCertificateTypes = getEapConfigurationContext().getSupportedCertificateTypes();
		for(int certType : supportedCertificateTypes)
			certificateRequest.setRequestedCertificateType(certType);
	}

	private void addTrustedDNList(CertificateRequest certificateRequest) {
		List<X509Certificate> trustedCertList = getEapConfigurationContext().getTrustedX509CertificatesList();
		if(trustedCertList != null && !trustedCertList.isEmpty()){
			int trustedCertListSize = trustedCertList.size();
			certificateRequest.setNumberOfDN(trustedCertListSize);
			for(int i=0;i<trustedCertListSize;i++){
				byte[] distinguishedNameOfCertificate = trustedCertList.get(i).getSubjectX500Principal().getEncoded();
				certificateRequest.setDistinguishedName(distinguishedNameOfCertificate);
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "Server certificate Distinguished Name : " + Utility.bytesToHex(distinguishedNameOfCertificate));
			}
		}
	}

	public void actionOnGenerateServerHelloDone(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for GenerateServerHelloDone, State : " + getCurrentState());
		ITLSRecordType recordType = null;
		
		HandshakeMessageRecordType handshakeMessageRecordType = new HandshakeMessageRecordType();
		handshakeMessageRecordType.setHandshakeMessageType(HandshakeMessageConstants.ServerHelloDone.value);
		handshakeMessageRecordType.setHandshakeMessagelength(0);
		recordType = (ITLSRecordType)handshakeMessageRecordType ;

		appendTLSRecord(recordType);
		
		addResponseRecord(recordType);			
		setSuccess(false);
		setFailure(false);
		setDone(true);		
	}
	
	public void actionOnHandleCertificateMessage(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for HandleCertificateMessage, State : " + getCurrentState());
		
		if(!isSendCertificateRequest()){
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.UNEXPECTED_MESSAGE.typeId);
		}
		
		Certificate certificate = (Certificate)((HandshakeMessageRecordType)tlsRecord).getHandshakeMessage();		
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Number of certificates received : " + certificate.getCertificateList().size());
		
		if(certificate.getCertificateList().size() > 0){
			this.certificateMessageBytes = certificate.getCertificateList().get(0);
			validateClientCertificate(certificate);
		}else{
			actionOnEmptyClientCertificateMessage();
		}
		
		appendTLSRecord(tlsRecord);
	}

	private void actionOnEmptyClientCertificateMessage() {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
			LogManager.getLogger().trace(MODULE, "No client certificate received.");
		}

		if(getEapConfigurationContext().isValidateClientCertificate()) {
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.HANDSHAKE_FAILURE.typeId);
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
			LogManager.getLogger().trace(MODULE, "Continuing handshake process on empty client certificate message.");
		}
	}

	private void appendTLSRecord(ITLSRecordType tlsRecord) {
		byte[] tempHandshakeRecordBytes = (byte[])getTLSConnectionState().getParameterValue(IEAPMethodSession.ALL_HANDSHAKE_MESSAGES);
		tempHandshakeRecordBytes = TLSUtility.appendBytes(tempHandshakeRecordBytes,tlsRecord.getBytes());
		getTLSConnectionState().setParameterValue(IEAPMethodSession.ALL_HANDSHAKE_MESSAGES,tempHandshakeRecordBytes);
	}

	private void validateClientCertificate(Certificate certificate) {
		List<byte[]> certificates = certificate.getCertificateList();
		boolean bCertificateValid = false;
		for(int i=0; i<certificates.size(); i++){
			try{
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				InputStream inStream = new ByteArrayInputStream(certificates.get(i));
				X509Certificate subjectCert = (X509Certificate)cf.generateCertificate(inStream);
				
				if(i == 0){
					extractCNFromClientCertificate(subjectCert);
				}

				validateClientCertificateExpiry(subjectCert);

				validateClientCertificateRevocation(subjectCert);
				
				//If client has sent a chain of certificates, verify the whole chain.
				if(i+1 < certificates.size()){
					inStream = new ByteArrayInputStream(certificates.get(i+1));
					X509Certificate issuerCert = (X509Certificate)cf.generateCertificate(inStream);
					subjectCert.verify(issuerCert.getPublicKey());
					bCertificateValid = true;
				}else{
					//check if last certificate in the chain is trusted by the server.
					List<X509Certificate> trustedCertificates = getEapConfigurationContext().getTrustedX509CertificatesList();
					if(trustedCertificates != null && !trustedCertificates.isEmpty()){
						final int trustedCertSize = trustedCertificates.size();
						for(int j=0; j<trustedCertSize; j++){
							X509Certificate trustedCert = trustedCertificates.get(j);
							try{
								if(subjectCert.getIssuerDN().equals(trustedCert.getSubjectDN())){
									subjectCert.verify(trustedCert.getPublicKey());
									bCertificateValid = true;
									break;
								}
							} catch (Exception e) {
								LogManager.getLogger().trace(MODULE, e);
							} 
						}
					}
				}
			}  catch (GeneralSecurityException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
					LogManager.getLogger().trace(MODULE, "Invalid client certificate received, reason :" + e.getMessage());
				}
				LogManager.getLogger().trace(MODULE, e);
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.BAD_CERTIFICATE.typeId);
			}
		}
		
		if(!bCertificateValid){
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE));
			LogManager.getLogger().trace(MODULE, "Client certificate received is invalid.");
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.BAD_CERTIFICATE.typeId);
		}
	}

	private void validateClientCertificateRevocation(X509Certificate subjectCert) {
		List<X509CRL> crlList = getEapConfigurationContext().getCRLList();
		
		if(crlList == null || crlList.isEmpty()){
			return;
		}
		
		final int crlListSize = crlList.size();
		for(int c=0; c<crlListSize; c++){
			X509CRL crl = crlList.get(c);
			if(!subjectCert.getIssuerDN().equals(crl.getIssuerDN())){
				continue;
			}

			if(!crl.isRevoked(subjectCert)){
				continue;
			}

			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "Client certificate received has been revoked with DN: " + subjectCert.getSubjectDN());
			}

			if(getEapConfigurationContext().isValidateCertificateRevocation()) {
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.CERTIFICATE_REVOKED.typeId);	
			}

			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "Allowing revoked client certificate with DN: " + subjectCert.getSubjectDN());
			}
		}
	}

	private void validateClientCertificateExpiry(X509Certificate subjectCert) {
		try {
			subjectCert.checkValidity();
		} catch (CertificateExpiredException e) {
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "Client certificate received is expired, " + e.getMessage());
			}

			if(getEapConfigurationContext().isValidateCertificateExpiry()) {
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.CERTIFICATE_EXPIRED.typeId);
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "Allowing expired client certificate with DN: " + subjectCert.getSubjectDN());
			}
		}catch (CertificateNotYetValidException e) {
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "Client certificate received is not yet valid, " + e.getMessage());
			}
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.BAD_CERTIFICATE.typeId);
		}
	}

	private void extractCNFromClientCertificate(X509Certificate subjectCert) {
		
			String subjectDN = subjectCert.getSubjectX500Principal().toString();
			StringTokenizer stk = new StringTokenizer(subjectDN, ",");
			while(stk.hasMoreTokens()){
				String token = stk.nextToken().trim();
				String[] strTokenArr = token.split("=");
				if(strTokenArr.length == 2){
					String identifier = strTokenArr[0].trim();
					String value = strTokenArr[1].trim();
					if(identifier.equals("CN")){
						if(value!=null){
							String[] cnValue = value.split(" ");
							getTLSConnectionState().setParameterValue(TLSConnectionState.MACADDRESS, cnValue[0].trim());
						}
					}
				}
			}
			if(getTLSConnectionState().getParameterValue(TLSConnectionState.MACADDRESS)==null){
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(MODULE, "The CN not Found in the Subject of the Certificate, which may result in the failure of MAC Validation");
			}
	}
	
	public void actionOnHandleCertificateVerifyMessage(ITLSRecordType tlsRecord, ICustomerAccountInfoProvider provider){
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for HandleCertificateVerifyMessage, State : " + getCurrentState());
		
		if(this.certificateMessageBytes == null)
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.UNEXPECTED_MESSAGE.typeId);
		
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		byte[] tempHandshakeRecordBytes = (byte[])getTLSConnectionState().getParameterValue(IEAPMethodSession.ALL_HANDSHAKE_MESSAGES);
		if(tlsSecurityParameters.getProtocolVersion() == ProtocolVersion.TLS1_2){
			actionOnHandleTLSv1_2CertificateVerify(tlsRecord, provider, tempHandshakeRecordBytes);
		}else {
			actionOnHandleTLSv1_0andv1_1CertificareVerifyMessage(tlsRecord, provider, tempHandshakeRecordBytes);
		}
		tempHandshakeRecordBytes = TLSUtility.appendBytes(tempHandshakeRecordBytes,tlsRecord.getBytes());
		getTLSConnectionState().setParameterValue(IEAPMethodSession.ALL_HANDSHAKE_MESSAGES,tempHandshakeRecordBytes);
	}
	
	private void actionOnHandleTLSv1_0andv1_1CertificareVerifyMessage(ITLSRecordType tlsRecord, ICustomerAccountInfoProvider provider, byte[] tempHandshakeRecordBytes){
		if(!signature.verify(((CertificateVerify)((HandshakeMessageRecordType)tlsRecord).getHandshakeMessage()).getSignature(), tempHandshakeRecordBytes, getX509Certificate().getPublicKey())){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Invalid signature received!");
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.DECRYPT_ERROR.typeId);
		}
	}
	
	private void actionOnHandleTLSv1_2CertificateVerify(ITLSRecordType tlsRecord, ICustomerAccountInfoProvider provider, byte[] tempHandshakeRecordBytes) {
		Tls1_2CertificateVerify certificateVerify = (Tls1_2CertificateVerify)((HandshakeMessageRecordType)tlsRecord).getHandshakeMessage();
		
		Signature sig = null;
		/**
		 * 	In below both cases RSA and DSA signature are used for verification
		 * 	so no requirement for passing private key so passing null value
		 */
		if(certificateVerify.getSignatureAlgorithm() == SignatureAlgorithm.RSA.getValue()){
			sig = new Tls1_2RSASignature(null,getEapConfigurationContext().isTestMode());
		} else if (certificateVerify.getSignatureAlgorithm() == SignatureAlgorithm.DSA.getValue()){
			sig = new Tls1_2DSASignature(null,getEapConfigurationContext().isTestMode());
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Signature Algorithm value: " + certificateVerify.getSignatureAlgorithm() + " not supported");
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.INTERNAL_ERROR.typeId);
		}
		
		if(!sig.verify(certificateVerify.getSignature(), tempHandshakeRecordBytes, getX509Certificate().getPublicKey())){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Invalid signature received!");
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.DECRYPT_ERROR.typeId);
		}
	}
	
	private X509Certificate getX509Certificate() {
		CertificateFactory cf;
		X509Certificate x509cert;
		try {				
			cf = CertificateFactory.getInstance("X.509");
			InputStream inStream = new ByteArrayInputStream(this.certificateMessageBytes);
			java.security.cert.Certificate cert = cf.generateCertificate(inStream);
			x509cert = (X509Certificate) cert;
		} catch (CertificateException e) {
			LogManager.getLogger().trace(MODULE, e);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE));
			LogManager.getLogger().trace(MODULE, "Invalid client certificate received, can not convert into X.509 standard, reason :" + e.getMessage());
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.BAD_CERTIFICATE.typeId);
		}
		return x509cert;
	}

	public void actionOnHandleClientKeyExchangeMessage(ITLSRecordType tlsRecord, ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for HandleClientKeyExchangeMessage, State : " + getCurrentState());
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		TLSSecurityKeys tlsSecurityKeys = (TLSSecurityKeys)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_KEY);
		
		byte[] masterSecret = null;
		
		if(!isSessionResumed()){

			appendTLSRecord(tlsRecord);
			
			byte[] keyExchangeValue = ((ClientKeyExchange)((HandshakeMessageRecordType)tlsRecord).getHandshakeMessage()).getKeyExchangeValue();
			if(keyExchangeValue == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Client Key Exchange received is empty");
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.DECRYPT_ERROR.typeId);
			}
			byte[] pMS = keyExchange.generatePMS(keyExchangeValue);
			if(pMS == null)
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.DECRYPT_ERROR.typeId);

			tlsSecurityKeys.setPMS(pMS);

			masterSecret = TLSUtility.generateMS(pMS, tlsSecurityKeys.getServerRandom(), tlsSecurityKeys.getClientRandom(), tlsSecurityParameters);
			tlsSecurityKeys.setMasterSecret(masterSecret);

		}else{
			masterSecret = tlsSecurityKeys.getMasterSecret();
			tlsSecurityKeys.resetReadSeq();
			tlsSecurityKeys.resetWriteSeq();
		}
		
		ICipherProvider cipherProvider = tlsSecurityParameters.getCipherProvider();
		int keyBlockSize = cipherProvider.getKeyBlockSize();
		byte[] keyBlock = TLSUtility.generateKeyBlock(masterSecret, "key expansion", tlsSecurityKeys.getServerRandom(), tlsSecurityKeys.getClientRandom(),keyBlockSize,tlsSecurityParameters);

		int hashSize = cipherProvider.getHashSize();
		// set ClientWriteMACSecret
		tlsSecurityKeys.setMacRead(TLSUtility.getClientWriteMACSecret(keyBlock,hashSize));

		// set ServerWriteMACSecret
		tlsSecurityKeys.setMacWrite(TLSUtility.getServerWriteMACSecret(keyBlock,hashSize));

		int keyMaterialSize = cipherProvider.getKeyMaterialSize();

		// set ClientWriteKey
		tlsSecurityKeys.setEncryptionRead(TLSUtility.getClientWriteKey(keyBlock,hashSize,keyMaterialSize));

		// set ServerWriteKey
		tlsSecurityKeys.setEncryptionWrite(TLSUtility.getServerWriteKey(keyBlock,hashSize,keyMaterialSize));

		int ivSize = cipherProvider.getIVSize();
		// set ClientWriteIV only used by TLSv1.0 and not by TLSv1.1
		tlsSecurityKeys.setClientIV(TLSUtility.getClientWriteIV(keyBlock,hashSize,keyMaterialSize,ivSize));

		// set ServerWriteIV only used by TLSv1.0 and not by TLSv1.1
		tlsSecurityKeys.setServerIV(TLSUtility.getServerWriteIV(keyBlock,hashSize,keyMaterialSize,ivSize));
		
	}

	public void actionOnHandleFinishedMessage(ITLSRecordType tlsRecord, ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for HandleFinishedMessage, State : " + getCurrentState());	
		TLSSecurityKeys tlsSecurityKeys = (TLSSecurityKeys)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_KEY);
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);

		byte[] verifyData = TLSUtility.generateVerifyData(tlsSecurityKeys.getMasterSecret()	, "client finished", (byte[])getTLSConnectionState().getParameterValue(IEAPMethodSession.ALL_HANDSHAKE_MESSAGES), tlsSecurityParameters);
		Finished finishMessage = (Finished) ((HandshakeMessageRecordType)tlsRecord).getHandshakeMessage();
		byte[] receivedVerifyData = finishMessage.getBytes();
		if(receivedVerifyData == null || !Arrays.equals(verifyData, receivedVerifyData)){
			LogManager.getLogger().trace(MODULE, "Verify data is not matched.");
			LogManager.getLogger().trace(MODULE, "Receieved Verify Data :"  + Utility.bytesToHex(receivedVerifyData)+
			"Calculated Verify Data : " + Utility.bytesToHex(verifyData));
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.DECRYPT_ERROR.typeId);
		}
		
		appendTLSRecord(tlsRecord);
		
		if(isSessionResumed()){ 
			tlsSessionResumptionFinished();
		}
			
	}
	public void actionOnGenerateFinishedMessage(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake action for GenerateFinishedMessage, State : " + getCurrentState());
		TLSSecurityKeys tlsSecurityKeys = (TLSSecurityKeys)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_KEY);
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		
		byte[] verifyData = TLSUtility.generateVerifyData(tlsSecurityKeys.getMasterSecret(), "server finished",(byte[])getTLSConnectionState().getParameterValue(IEAPMethodSession.ALL_HANDSHAKE_MESSAGES), tlsSecurityParameters);
		
		Finished finished = new Finished();
		finished.setVerifyData(verifyData);			

		ITLSRecordType recordType = (ITLSRecordType)TLSRecordTypeDictionary.getInstance().createTLSRecord(TLSRecordConstants.Handshake.value, tlsSecurityParameters.getProtocolVersion());
		((HandshakeMessageRecordType)recordType).setHandshakeMessage(finished);
		((HandshakeMessageRecordType)recordType).refreshHeader();		
		
		appendTLSRecord(recordType);
		
		addResponseRecord(recordType);
		setDone(true);
		setSuccess(false);
		setFailure(false);
	}
//	public void actionOnBuildAlertRecord(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider){
//		Logger.logTrace(MODULE, "CAll - Handshake Action for BuildAlertRecord, State : " + getCurrentState());
//	}
//	public void actionOnGeneratingKeysForReponse(ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider){
//		Logger.logTrace(MODULE, "CAll - Handshake Action for GeneratingKeysForReponse, State : " + getCurrentState());
//	}
	public void addResponseRecord(ITLSRecordType tlsRecordType){
		this.responseRecordQueue.add(tlsRecordType);
	}
	public ITLSRecordType getResponseRecordType(){
		return this.responseRecordQueue.poll();
	}
	public boolean hasMoreResponseRecordType(){
		return !this.responseRecordQueue.isEmpty();
	}
	public void resetResponseRecordTypeQueue(){
		this.responseRecordQueue = new LinkedBlockingQueue<ITLSRecordType>();
	}

/*	@Override
	public byte[] getResponseTLSRecord() {
		TLSPlaintext tlsResponseRecord = new TLSPlaintext();
		tlsResponseRecord.init();
		tlsResponseRecord.setContentType(new ContentType(TLSRecordConstants.Handshake.value));
		final int majorVersion = getEapConfigurationContext().getSSLProtocolVersion();		
		final int minorVersion = getEapConfigurationContext().getTLSProtocolVersion();
		tlsResponseRecord.setProtocolVersion(new ProtocolVersion(majorVersion,minorVersion));
		ITLSRecordType tlsRecordType =null;
		while(hasMoreResponseRecordType()){
			tlsRecordType = getResponseRecordType();
			tlsResponseRecord.setContent(tlsRecordType);
		}
		tlsResponseRecord.refreshHeader();
		return tlsResponseRecord.getBytes();		
	}*/

   @Override
   public byte[] getResponseTLSRecord() {
		byte[] responseBytes = null;
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(MODULE, "Handshake Response Generated");
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		while (hasMoreResponseRecordType()) {
			TLSPlaintext tlsResponseRecord = new TLSPlaintext();
			tlsResponseRecord.setContentType(new ContentType(TLSRecordConstants.Handshake.value));
			tlsResponseRecord.setProtocolVersion(tlsSecurityParameters.getProtocolVersion());
			ITLSRecordType tlsRecordType = null;
			tlsRecordType = getResponseRecordType();
			tlsResponseRecord.setContent(tlsRecordType);
			tlsResponseRecord.refreshHeader();
			
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, tlsResponseRecord.toString());
			
			responseBytes = TLSUtility.appendBytes(responseBytes, tlsResponseRecord.getBytes());
		}		
		return responseBytes;
	}


	public int getType() {
		return TLSRecordConstants.Handshake.value;
	}
	
	public ICustomerAccountInfo getCustomerAccountInfo() {
		return customerAccountInfo;
	}
	
	public void setCustomerAccountInfo(ICustomerAccountInfo customerAccountInfo) {
		this.customerAccountInfo = customerAccountInfo;
	}
	
	public void clearCustomerAccountInfo(){
		this.customerAccountInfo = null;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public void setOUI(String oui) {
		this.oui = oui;
	}
	
	public void setSendCertificateRequest(boolean isSendCertificateRequest){
		this.isSendCertificateRequired=isSendCertificateRequest;
	}
	
	public boolean isSendCertificateRequest(){
		return this.isSendCertificateRequired;
	}
	
	public boolean isSessionResumed(){
		return (Boolean)(getTLSConnectionState().getParameterValue(TLSConnectionState.IS_SESSION_RESUMED));
	}
	
	public void setSessionResumed(boolean sessionResumed){
		getTLSConnectionState().setParameterValue(TLSConnectionState.IS_SESSION_RESUMED,sessionResumed);
	}
	
	public void tlsSessionResumptionFinished(){
		setDone(true);
		setSuccess(true);
		setFailure(false);
		setSessionResumed(false);
		int iSessionCntr= (Integer)getTLSConnectionState().getParameterValue(TLSConnectionState.SESSION_RESUMPTION_COUNTER);
		getTLSConnectionState().setParameterValue(TLSConnectionState.SESSION_RESUMPTION_COUNTER,++iSessionCntr);
		changeCurrentState(HandshakeStates.INITIALIZED);
	}
	public  void applyActions(IEnum event,ITLSRecordType tlsRecord,ICustomerAccountInfoProvider provider) {
		
		TLSSecurityParameters tlsSecurityParameters = (TLSSecurityParameters)getTLSConnectionState().getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		switch((HandshakeEvents )event){
		case HSCertificateReceived:
			actionOnHandleCertificateMessage(tlsRecord, provider);
			break;
		case HSCertificateVerifyReceived:
			actionOnHandleCertificateVerifyMessage(tlsRecord, provider);
			break;
		case HSClientHelloWithSessionResumptionReceived:
			actionOnHandleClientHello(tlsRecord, provider);			
			actionOnGenerateServerHello(tlsRecord, provider);
			actionOnHandleClientKeyExchangeMessage(tlsRecord, provider);
			break;
		case HSClientHelloReceived:
			actionOnHandleClientHello(tlsRecord, provider);
			actionOnGenerateServerHello(tlsRecord, provider);
			if(tlsSecurityParameters.getCipher().certificateMessageRequired) {
				actionOnGenerateCertificateMessage(tlsRecord, provider);				
			}
			if(tlsSecurityParameters.getCipher().serverKeyExchangeRequired) {
				actionOnGenerateServerKeyExchange(tlsRecord, provider);
			}
			if(tlsSecurityParameters.getCipher().certificateMessageRequired && isSendCertificateRequest()){
				actionOnGenerateCertificateRequest(tlsRecord, provider);
			}
			actionOnGenerateServerHelloDone(tlsRecord, provider);
			break;
		case HSClientKeyExchangeReceived:
			actionOnHandleClientKeyExchangeMessage(tlsRecord, provider);
			break;
		case HSSuccess:
			break;
		case HSFinishedReceived:			
			if(!isSessionResumed()){
				actionOnHandleFinishedMessage(tlsRecord, provider);
				actionOnGenerateFinishedMessage(tlsRecord, provider);

			}else{
				actionOnHandleFinishedMessage(tlsRecord, provider);
			}
			break;
		case HSGenerateAlert:
			break;
		case HSInvalidRequest:
			break;
		case HSRequestReceived:
			break;
		case HSGenerateFinishMessageReceived:
			actionOnGenerateFinishedMessage(tlsRecord, provider);
			break;
		}
	}

}
