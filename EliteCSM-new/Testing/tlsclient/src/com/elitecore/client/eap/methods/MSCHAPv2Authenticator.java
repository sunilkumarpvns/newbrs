package com.elitecore.client.eap.methods;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.AuthenticationException;

import com.elitecore.client.configuration.PeapConfiguration;
import com.elitecore.client.eap.EapMethodAuthenticator;
import com.elitecore.client.util.constants.CommunicationStates;
import com.elitecore.client.util.constants.MSCHAPStates;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MD4;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers.MSCHAP2Handler;
import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.mschapv2.MSCHAPv2EAPType;
import com.elitecore.coreeap.packet.types.mschapv2.packet.ChallengeMSCHAPv2Type;
import com.elitecore.coreeap.packet.types.mschapv2.packet.IMSCHAPv2Type;
import com.elitecore.coreeap.packet.types.mschapv2.packet.MSCHAPv2TypeDictionary;
import com.elitecore.coreeap.packet.types.mschapv2.packet.ResponseMSCHAPv2Type;
import com.elitecore.coreeap.packet.types.mschapv2.packet.SuccessMSCHAPv2Type;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.mschapv2.OpCodeConstants;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.tls.TLSUtility;
/**
 * 
 * Handles EAP - MSCHAPv2 for PEAP. 
 * It generates MSChapv2 Data and also handles Success Response of MSCHAPv2 Type.
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public class MSCHAPv2Authenticator implements EapMethodAuthenticator{
	private static final String MODULE = "MSCHAPv2_AUTHENTICATOR";
	private final String innerIdentity;
	private final String innerPasswd;
	private MSCHAPStates mschapv2states;
	private int msChapId;
	private List<EAPType> eapTypes;
	private byte[] receivedServerChallenge;
	private byte[] sentResponse;

	public MSCHAPv2Authenticator(PeapConfiguration peapConfiguration) {
		this.innerIdentity = peapConfiguration.getInnerIdentity();
		this.innerPasswd = peapConfiguration.getInnerPasswd();
		mschapv2states = MSCHAPStates.MSCHAP;
		eapTypes = new ArrayList<EAPType>(3);
	}

	@Override
	public void reset() {
		mschapv2states = MSCHAPStates.MSCHAP;
		eapTypes = new ArrayList<EAPType>(3);
	}

	/**
	 * It will first generate MSCHAPv2 Data and then create success type.
	 */
	@Override
	public CommunicationStates process(byte[] inTlsData) throws AuthenticationException {
		LogManager.getLogger().info(MODULE, "Generating MSCHAPv2 Data");
		switch(mschapv2states) {
		case MSCHAP:
			/*
			 * For generating MSChapv2 data
			 */
			generateMschapv2Data(inTlsData);
			return CommunicationStates.CONTINUE;
			
		case FINISHED:
			/*
			 * For generating Mschapv2 success response
			 */
			generateMschapv2DataResult(inTlsData);
			return CommunicationStates.COMPLETED_PROCESSING;
		
		default:
			LogManager.getLogger().error(MODULE, "Not in proper state. Something in fishy");
			throw new AuthenticationException(MODULE + " not in proper state");
		}
	}

	/**
	 * Generate MSChapv2 Data that is creating MSCHAPv2 Response from MSCHAPv2 Challenge sent by the authenticator.
	 * and also append User Name in Response. 
	 * @param inTlsData
	 */
	private void generateMschapv2Data(byte[] inTlsData) throws AuthenticationException{
		this.sentResponse = generateMschap2ResponseBytes((ChallengeMSCHAPv2Type)extractMSCHAPv2Type(inTlsData));
		LogManager.getLogger().info(MODULE, "MSCHAPv2 Response Generated: " + Utility.bytesToHex(this.sentResponse) + " Length: " + this.sentResponse.length);

		generateMschap2EapType(generateMschap2Response(this.sentResponse), OpCodeConstants.RESPONSE);
		mschapv2states = MSCHAPStates.FINISHED;
	}

	/**
	 * 	Extract the ChallengeMSCHAPv2Type from EAPType data bytes containing mschapv2 challenge.   
	 * 
	 * @param inTlsData - EAPType data bytes contains ChallengeMSCHAPv2Type 
	 * @return
	 * @throws AuthenticationException 
	 * @throws InvalidEAPTypeException
	 */
	private IMSCHAPv2Type extractMSCHAPv2Type(byte[] inTlsData) throws AuthenticationException {
		try {
			EAPType eapType = new EAPType(inTlsData);
			MSCHAPv2EAPType chapEapType = new MSCHAPv2EAPType();
			chapEapType.setData(eapType.getData());
			return chapEapType.getMsCHAPv2Type();
		} catch (InvalidEAPTypeException e) {
			LogManager.getLogger().error(MODULE, "Invalid Eap Type. Reason:" + e.getLocalizedMessage());
			throw new AuthenticationException("Inavalid Eap Type");
		}
	}

	private byte[] generateMschap2ResponseBytes(ChallengeMSCHAPv2Type challengeMSCHAPv2Type){
		this.receivedServerChallenge = challengeMSCHAPv2Type.getChallenge();
		LogManager.getLogger().info(MODULE, "Challenge Received: " + Utility.bytesToHex(this.receivedServerChallenge) + " Length: " + this.receivedServerChallenge.length);
		this.msChapId = nextId(challengeMSCHAPv2Type.getIdentifier());
		return MSCHAP2Handler.getMsChap2Response(this.msChapId, this.receivedServerChallenge, this.receivedServerChallenge, this.innerIdentity, this.innerPasswd);
	}

	//TODO ask kuldeep about this constants in below method any make constants for that
	private IMSCHAPv2Type generateMschap2Response(byte[] chapResponse){
		byte[] responseBytes = new byte[50 + this.innerIdentity.getBytes().length];
		responseBytes[0] = (byte) 49;
		System.arraycopy(chapResponse, 2, responseBytes, 1, chapResponse.length - 2);
		System.arraycopy(this.innerIdentity.getBytes(), 0, responseBytes, chapResponse.length, this.innerIdentity.getBytes().length);
		IMSCHAPv2Type responseChapEapType = (ResponseMSCHAPv2Type) MSCHAPv2TypeDictionary.getInstance().createMSCHAPv2Type(OpCodeConstants.RESPONSE.opCode);
		((ResponseMSCHAPv2Type)responseChapEapType).setIdentifier(this.msChapId);
		((ResponseMSCHAPv2Type)responseChapEapType).setMsLength(responseBytes.length + 4);
		((ResponseMSCHAPv2Type)responseChapEapType).setValueBuffer(responseBytes);
		return responseChapEapType;
	}

	/**
	 * Generating Success MSCHAPv2 Type
	 * @param inTlsData
	 */
	private void generateMschapv2DataResult(byte[] inTlsData) throws AuthenticationException{
		SuccessMSCHAPv2Type successMSCHAPv2Type = (SuccessMSCHAPv2Type)extractMSCHAPv2Type(inTlsData);
		String result = generateExpectedMSCHAP2SuccessValue();
		if(!Arrays.equals(result.getBytes(), successMSCHAPv2Type.getMessage())){
			LogManager.getLogger().error(MODULE, "MschapV2 Success Message not varified");
			throw new AuthenticationException("Invalid MsChapV2 Success Message.");
		}

		SuccessMSCHAPv2Type successChapEapType = new SuccessMSCHAPv2Type();
		successChapEapType.setMsLength(4);

		generateMschap2EapType(successChapEapType, OpCodeConstants.SUCCESS);
	}

	private String generateExpectedMSCHAP2SuccessValue(){
		byte[] pwHash = new byte[16];
		byte[] pwHashHash = new byte[16];
		byte[] passBytes = null;
		try{
			passBytes = innerPasswd.getBytes("UTF-16LE");					
		}catch(UnsupportedEncodingException e){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, "User password can not be converted into UTF-16LE");	            
			}
		}
		MD4.getDigest(passBytes, 0, passBytes.length, pwHash, 0);
		MD4.getDigest(pwHash, 0, pwHash.length, pwHashHash, 0);
		return MSCHAP2Handler.getMsChap2SuccessValue(
				this.sentResponse, 
				Arrays.copyOfRange(
						TLSUtility.doHash(
								this.receivedServerChallenge, 
								this.receivedServerChallenge, 
								innerIdentity.getBytes(), 
								HashAlgorithm.SHA1),
								0, 
								8
						),
						pwHashHash
				);
	}

	private void generateMschap2EapType(IMSCHAPv2Type mschap2EapType, OpCodeConstants constant){
		MSCHAPv2EAPType sendChapEapType = (MSCHAPv2EAPType) EAPTypeDictionary.getInstance().createEAPType(EapTypeConstants.MSCHAPv2.typeId);
		sendChapEapType.setMsCHAPv2Type(mschap2EapType);
		sendChapEapType.setOpCode(constant.opCode);
		LogManager.getLogger().info(MODULE, "MSCHAPv2 Type Generated: " + sendChapEapType);
		eapTypes.add(sendChapEapType);
	}

	@Override
	public byte[] getOutData() {
		return eapTypes.remove(0).toBytes();
	}
	
	/**
	 * Provides next identifier for the next MSCHAPv2 Type (MS-CHAPv2-ID)
	 * @param identifier
	 * @return
	 */
	public int nextId(int identifier){
		identifier %= 255;
		return ++identifier;
	}
}