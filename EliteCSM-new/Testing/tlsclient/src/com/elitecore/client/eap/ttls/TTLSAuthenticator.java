package com.elitecore.client.eap.ttls;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;

import javax.naming.AuthenticationException;
import javax.net.ssl.SSLException;

import com.elitecore.client.configuration.TlsConfiguration;
import com.elitecore.client.configuration.TtlsConfiguration;
import com.elitecore.client.eap.EapMethodAuthenticator;
import com.elitecore.client.eap.tls.TLSAuthenticator;
import com.elitecore.client.util.constants.CommunicationStates;
import com.elitecore.client.util.constants.OuterMethodEvents;
import com.elitecore.commons.base.Bytes;
import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreeap.data.tls.TLSSecurityParameters;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MD4;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MSCHAP2Handler;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MSCHAPv1Handler;
import com.elitecore.coreeap.packet.types.tls.record.TLSPlaintext;
import com.elitecore.coreeap.packet.types.tls.record.types.AVP;
import com.elitecore.coreeap.packet.types.tls.record.types.ApplicationDataRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.ITLSRecordType;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.AttributeConstants;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;
import com.elitecore.coreeap.util.constants.tls.application.ApplicationMethodConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

/**
 *  Handles EAP-TTLS Method.
 *  Supports PAP, CHAP, MSCHAP and MSCHAPv2 as inner TTLS types.
 *  Below are code for these inner-method for configuration.
 *  PAP		 --> 1
 *  CHAP 	 --> 2
 *  MSCHAP	 --> 4
 *  MSCHAPv2 --> 5
 *  
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public class TTLSAuthenticator implements EapMethodAuthenticator{
	
	private static final String MODULE = "TTLS_Authenticator";
	private static final String MSCHAPV2_CHALLENGE = "MSCHAPV2CHALLENGE";
	private static final String MSCHAPV2_RESPONSE = "MSCHAPV2RESPONSE";
	private static final int MD4_HASH_SIZE = 16;
	private static final int RESPONSE_CHALLENGE_SIZE = 16;
	private static final int CHAP_PASSWD_SIZE = 17;
	private OuterMethodEvents event = OuterMethodEvents.HANDSHAKE_MESSAGE_RECEIVED;
	private TLSAuthenticator tlsAuthenticator;
	private byte[] outData;
	private final ApplicationMethodConstants innerType;
	private final String innerIdentity;
	private final String innerPasswd;
	private HashMap<String,AVP> lastSentAvps = new HashMap<String,AVP>(5);

	public TTLSAuthenticator(TtlsConfiguration ttlsConfiguration, TlsConfiguration tlsConfiguration) throws InitializationFailedException {
		tlsAuthenticator = new TLSAuthenticator(tlsConfiguration);
		innerType = ApplicationMethodConstants.get(ttlsConfiguration.getInnerMethod());
		innerIdentity = ttlsConfiguration.getInnerIdentity();
		innerPasswd = ttlsConfiguration.getInnerPasswd();
	}

	@Override
	public CommunicationStates process(byte[] inTlsData) throws AuthenticationException {
		
		switch (event) {
		case HANDSHAKE_MESSAGE_RECEIVED:
			setOutData(tlsAuthenticator.actionOnTlsHsRunning(inTlsData));
			/*
			 * Here finished and length is checked because if TLS authenticator is finished doing handshake
			 * and it doesn't have any data to send then it was sending ACK to the server to this server 
			 * directly send success without performing inner-method authentication. So, in this case we 
			 * allow the flow to perform action on application data. 
			 */
			if(tlsAuthenticator.isFinished() == false || getOutData().length != 0) {
				return CommunicationStates.CONTINUE;
			} 

			event = OuterMethodEvents.APPLICATION_DATA_RECEIVED;
		
		case APPLICATION_DATA_RECEIVED:
			try {
				setOutData(tlsAuthenticator.encrypt(actionOnApplicationData(inTlsData)));
			} catch (SSLException e) {
				LogManager.getLogger().trace(e);
				throw new AuthenticationException("Error during Encryption");
			}
			return CommunicationStates.CONTINUE;
			
		case PROCESSING_COMPLETED:
			return CommunicationStates.COMPLETED_PROCESSING;

		default:
			LogManager.getLogger().error(MODULE, "Not in proper state. Something in fishy");
			throw new AuthenticationException(MODULE + " not in proper state");
		}
	}
	
	private byte[] actionOnApplicationData(byte[] inTlsData) throws SSLException {
		switch (innerType) {
		case PAP:
			return generatePapData(inTlsData);
			
		case CHAP:
			return generateChapData(inTlsData);
			
		case MSCHAP:
			return generateMschapData();
			
		case MSCHAPv2:
			return generateMschapv2Data(inTlsData);
			
		default:
			throw new SSLException(MODULE + "Requested" + innerType.name() + " method not supported.");
		}
	}

	private byte[] generateMschapv2Data(byte[] inTlsData) throws SSLException {
		TLSPlaintext record = new TLSPlaintext(inTlsData);
		if(record.getContentType().getType() == TLSRecordConstants.ChangeCipherSpec.value) {
			return generateMschapV2RequestData();
		}
		if(record.getContentType().getType() == TLSRecordConstants.ApplicationData.value) {
			return handleMSchapV2SuccesData(tlsAuthenticator.decrypt(inTlsData));
		}
		throw new SSLException("Record type is not Expected in TTLS MschapV2 communication");
	}

	private byte[] handleMSchapV2SuccesData(byte[] record) {
		AVP successAvp = new AVP(record);
		String result = generateExpectedMSCHAP2SuccessValue();
		if(!Arrays.equals(result.getBytes(), successAvp.getBytes())){
			LogManager.getLogger().error(MODULE, "MschapV2 Success Message not varified");
		}
		LogManager.getLogger().info(MODULE, "MschapV2 Success Message : " + successAvp);
		event = OuterMethodEvents.PROCESSING_COMPLETED;
		return new byte[0];
	}
	
	private String generateExpectedMSCHAP2SuccessValue(){
		byte[] pwHash = new byte[MD4_HASH_SIZE];
		byte[] pwHashHash = new byte[MD4_HASH_SIZE];
		MD4.getDigest(innerPasswd.getBytes(), 0, innerPasswd.getBytes().length, pwHash, 0);
		MD4.getDigest(pwHash, 0, pwHash.length, pwHashHash, 0);
		return MSCHAP2Handler.getMsChap2SuccessValue(lastSentAvps.get(MSCHAPV2_RESPONSE).getValue(), lastSentAvps.get(MSCHAPV2_CHALLENGE).getValue(), pwHashHash);
	}

	private byte[] generateMschapV2RequestData() throws SSLException{
		ITLSRecordType applicationDataAVP = new ApplicationDataRecordType();
		byte[] authChallenge = TLSUtility.generateSecureRandom(RESPONSE_CHALLENGE_SIZE);
		byte[] chapResponse = MSCHAP2Handler.getMsChap2Response(0, authChallenge, authChallenge, innerIdentity, innerPasswd);
		
		//adding user name AVP
		lastSentAvps.put("USERNAME", addUserNameAvp(applicationDataAVP));
		
		//adding MSCHAP_CHALLENGE AVP
		lastSentAvps.put(MSCHAPV2_CHALLENGE,addMschapChallenge(applicationDataAVP, authChallenge));

		//adding MSCHAP2_RESPONSE AVP
		lastSentAvps.put(MSCHAPV2_RESPONSE,addMschapV2ResponseAvp(applicationDataAVP, chapResponse));
		
		return applicationDataAVP.getBytes();
	}
	
	private byte[] generateMschapData() throws SSLException {
		ITLSRecordType applicationDataAVP = new ApplicationDataRecordType();
		byte[] authChallenge = TLSUtility.generateSecureRandom(RESPONSE_CHALLENGE_SIZE);
		byte[] chapResponse = MSCHAPv1Handler.getMsChapResponse(innerPasswd.getBytes(), authChallenge, (byte)0);
		
		//adding user name AVP
		addUserNameAvp(applicationDataAVP);
		
		//adding MSCHAP_CHALLENGE AVP
		addMschapChallenge(applicationDataAVP, authChallenge);

		//adding MSCHAP_RESPONSE AVP
		addMschapResponseAvp(applicationDataAVP, chapResponse);
		
		return applicationDataAVP.getBytes();
	}

	private byte[] generateChapData(byte[] inTlsData) throws SSLException {
	
		ITLSRecordType applicationDataAVP = new ApplicationDataRecordType();
		byte[] challenge = TLSUtility.generateTTLSChapChallange(tlsAuthenticator.getMsk(), tlsAuthenticator.getClientRandom(), tlsAuthenticator.getServerRandom(), new TLSSecurityParameters());
		//TODO  below code should be used & above challenge should be deleted
		//		& also delete the code for generating server random, client
		//		random & msk from class TLSAuthenticator.

//		byte[] challenge = new byte[17];
		
		//adding user name AVP
		addUserNameAvp(applicationDataAVP);
		
		//adding chap challenge
		addChapChallengeAvp(applicationDataAVP, challenge);
		
		//adding chap passwd
		addChapPasswdAvp(applicationDataAVP, challenge);
		
		return applicationDataAVP.getBytes();
	}

	private byte[] generatePapData(byte[] inTlsData) throws SSLException {
	
		ITLSRecordType applicationDataAVP = new ApplicationDataRecordType();
		
		//adding userName AVP
		addUserNameAvp(applicationDataAVP);

		//adding password AVP
		addPasswdAvp(applicationDataAVP);
		
		return applicationDataAVP.getBytes();
	}
	
	private AVP addUserNameAvp(ITLSRecordType applicationDataAVP){
		AVP userNameAvp = createAvp(AttributeConstants.STANDARD_VENDOR_ID, AttributeConstants.USER_NAME, 0, innerIdentity.getBytes());
		setAVP(applicationDataAVP, userNameAvp);
		userNameAvp.refreshHeader();
		LogManager.getLogger().info(MODULE, "userNameAvp:" + userNameAvp);
		return userNameAvp;
	}
	
	private AVP addPasswdAvp(ITLSRecordType applicationDataAVP){
		AVP passwordAvp = createAvp(AttributeConstants.STANDARD_VENDOR_ID, AttributeConstants.USER_PASSWORD, 0, innerPasswd.getBytes());
		setAVP(applicationDataAVP, passwordAvp);
		passwordAvp.refreshHeader();
		LogManager.getLogger().info(MODULE, "passwdAvp:" + passwordAvp);
		return passwordAvp;
	}
	
	private AVP addChapChallengeAvp(ITLSRecordType applicationDataAVP, byte[] challenge){
		AVP challengeAvp = createAvp(AttributeConstants.STANDARD_VENDOR_ID, AttributeConstants.CHAP_CHALLENGE, 0, Arrays.copyOfRange(challenge, 0, RESPONSE_CHALLENGE_SIZE));
		setAVP(applicationDataAVP, challengeAvp);
		challengeAvp.refreshHeader();
		LogManager.getLogger().info(MODULE, "chapChallengeAvp:" + challengeAvp);
		return challengeAvp;
	}
	
	private AVP addChapPasswdAvp(ITLSRecordType applicationDataAVP, byte[] challenge){
		byte chapPasswd[] = new byte[CHAP_PASSWD_SIZE];
		MessageDigest messageDigest = Utility.getMessageDigest(HashAlgorithm.MD5.name());
		messageDigest.reset();
	 	messageDigest.update(challenge[16]);
 		messageDigest.update(innerPasswd.getBytes());
	 	messageDigest.update(Arrays.copyOfRange(challenge, 0, 16));
	 	chapPasswd[0] = challenge[16];
	 	challenge = messageDigest.digest();
	 	System.arraycopy(challenge, 0, chapPasswd, 1, 16);
	 	
	 	AVP passwordAvp = createAvp(AttributeConstants.STANDARD_VENDOR_ID, AttributeConstants.CHAP_PASSWORD, 0, chapPasswd);
		setAVP(applicationDataAVP, passwordAvp);
		passwordAvp.refreshHeader();
		LogManager.getLogger().info(MODULE, "chapPasswdAvp:" + passwordAvp);
		return passwordAvp;
	}
	
	private AVP addMschapChallenge(ITLSRecordType applicationDataAVP, byte[] authChallenge) {
		AVP mschapChallenge = createAvp(AttributeConstants.MICROSOFT_VENDOR_ID, RadiusAttributeConstants.MSCHAP_CHALLENGE, 128, authChallenge);
		setAVP(applicationDataAVP, mschapChallenge);
		mschapChallenge.refreshHeader();
		LogManager.getLogger().info(MODULE, "mschapChallenge:" + mschapChallenge);
		return mschapChallenge;
	}
	
	private AVP addMschapResponseAvp(ITLSRecordType applicationDataAVP, byte[] chapResponse){
		AVP mschapResponse = createAvp(AttributeConstants.MICROSOFT_VENDOR_ID, RadiusAttributeConstants.MSCHAP_RESPONSE, 128, chapResponse);
		setAVP(applicationDataAVP, mschapResponse);
		LogManager.getLogger().info(MODULE, "mschapResponse" + mschapResponse);
		return mschapResponse;
	}
	
	private AVP addMschapV2ResponseAvp(ITLSRecordType applicationDataAVP, byte[] chapResponse){
		AVP mschapv2Response = createAvp(AttributeConstants.MICROSOFT_VENDOR_ID, RadiusAttributeConstants.MSCHAP2_RESPONSE, 192, chapResponse);
		setAVP(applicationDataAVP, mschapv2Response);
		mschapv2Response.refreshHeader();
		LogManager.getLogger().info(MODULE, "mschapV2Response: " + mschapv2Response);
		return mschapv2Response;
	}
	
	private AVP createAvp(int vendor, int attribute, int flag, byte[] data){
		AVP newAvp = new AVP();
		newAvp.setVendorID(vendor);
		newAvp.setId(attribute);
		newAvp.setFlag(flag);
		newAvp.setValue(data);
		newAvp.refreshHeader();
		return newAvp;
	}
	
	private void setAVP(ITLSRecordType applicationDataAVP, AVP avp) {
		((ApplicationDataRecordType)applicationDataAVP).setAVP(avp);
		((ApplicationDataRecordType)applicationDataAVP).reset();
		((ApplicationDataRecordType)applicationDataAVP).setAVPs(((ApplicationDataRecordType)applicationDataAVP).getBytes());	
	}
	
	public void reset() throws InitializationFailedException {
		event = OuterMethodEvents.HANDSHAKE_MESSAGE_RECEIVED;
		tlsAuthenticator.reset();
	}
	
	@Override
	public byte[] getOutData() {
		return outData;
	}

	private void setOutData(byte[] outData) {
		this.outData = outData; 
	}

	public static void main(String[] args) {
		int n = Bytes.toInt(new byte[] {15, 107, 117});
		System.out.println("n = " + n);
		
		int on = 6054308;
		byte[] lenBytes = Numbers.toByteArray(on, 3);
		System.out.println(Arrays.toString(lenBytes));
	}
}