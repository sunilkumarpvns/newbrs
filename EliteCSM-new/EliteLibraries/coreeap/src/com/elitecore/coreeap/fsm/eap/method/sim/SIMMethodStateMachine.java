package com.elitecore.coreeap.fsm.eap.method.sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.fsm.EAPFailureReasonConstants;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.sim.SIMAttributeDictionary;
import com.elitecore.coreeap.packet.types.sim.SimEapType;
import com.elitecore.coreeap.packet.types.sim.attributes.ISIMAttribute;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.events.SimEvents;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.constants.sim.message.SIMMessageTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class SIMMethodStateMachine extends BaseSIMMethodStateMachine {

	private static final String MODULE = "SIM STATE MACHINE";
	
	public SIMMethodStateMachine(
			IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
		// TODO Auto-generated constructor stub
	}
	public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), "SIM Packet Processing in State :" + getCurrentState());		
		setSuccess(false);
		setFailure(false);
		setDone(false);
		SimEvents event = (SimEvents)getEvent(aaaEapRespData);
		setCurrentEvent(event);
		try {
		handleEvent(event, aaaEapRespData,provider);
		} catch (Exception e){
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().warn(MODULE, "Failed to handle EAP-SIM event: " + event);
			setSuccess(false);
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.SIM_FAILURE);
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(getModuleName(), "SIM Packet has been processed");
		return getCurrentEvent();
	}
	@Override
	public AAAEapRespData handleEvent(IEnum event, AAAEapRespData aaaEapRespData, ICustomerAccountInfoProvider provider) throws EAPException {
		applyActions(event, aaaEapRespData, provider);
		return null;
	}
	
	public void applyActions(IEnum event, AAAEapRespData aaaEapRespData, ICustomerAccountInfoProvider provider) throws EAPException {
		switch ((SimEvents)event){
		case SimResponseIdentityReceived:
			actionOnResponseIdentityReceived(aaaEapRespData, provider);
			generateStartMessage(aaaEapRespData, provider);
					break;
		case SimStartReceivedWithPseudonymId:
			generateChallengeMessage(aaaEapRespData, provider);
					break;
		case SimStartReceivedWithPermanentId:
			generateChallengeMessage(aaaEapRespData, provider);
					break;
		case SimStartReceivedWithFastReauthId:
			generateChallengeMessage(aaaEapRespData, provider);
					break;
		case SimStartReceivedWithUnknownId:
			//buildFailure(aaaEapRespData, provider);
			generateStartMessage(aaaEapRespData, provider);
					break;
		case SimChallenge:
			validateSIMResponse(aaaEapRespData,provider);
			break;
		case SimNAKReceived:
			actionOnNAKReceived(aaaEapRespData, provider);
					generateStartMessage(aaaEapRespData, provider);
					break;
		case SimRequestReceived:
			actionOnRequestReceived(aaaEapRespData, provider);
					generateResponse(aaaEapRespData,provider);
					break;
		case SimSuccess:
			buildSuccess(aaaEapRespData, provider);
					break;
		case SimInvalidRequest:
			discardRequest(aaaEapRespData, provider);
			buildFailure(aaaEapRespData, provider);
			break;
		case SimFailure:
			buildFailure(aaaEapRespData, provider);
			break;
		case SimUnconditionalEvent:
			discardRequest(aaaEapRespData, provider);
			buildFailure(aaaEapRespData, provider);
			break;
		case SimReauthentication:
			validateSIMResponse(aaaEapRespData, provider);
				}
			}
	
	public boolean check(AAAEapRespData aaaEapRespData) {
		// TODO Auto-generated method stub
		parseSIMResp(aaaEapRespData);
		return isValidRequest();
	}

	public int getMethodCode() {	
		return EapTypeConstants.SIM.typeId;
	}
	
	public void process(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		processRequest(aaaEapRespData,provider);		
	}
	
	public void postProcess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException{
		EAPPacket eapPacket = aaaEapRespData.getEapReqPacket();
		SimEapType simEapType =  (SimEapType) eapPacket.getEAPType();
		if(simEapType.getSimMessageType() == SIMMessageTypeConstants.SIM_CHALLENGE.Id){			
			SimEapType receivedSimEapType = (SimEapType)aaaEapRespData.getEapRespPacket().getEAPType();
			Collection<byte[]> simAttrs = receivedSimEapType.getSimAttributes();
			Iterator<byte[]> simAttrsItr = simAttrs.iterator();
			HashMap<String, ISIMAttribute> receivedAttributes = new HashMap<String, ISIMAttribute>();
			while(simAttrsItr.hasNext()){
				byte[] simAttribute = simAttrsItr.next();
				ISIMAttribute simAttribute2 = SIMAttributeDictionary.getInstance().getAttribute((simAttribute[0] & 0xFF));
				simAttribute2.setBytes(simAttribute);
				receivedAttributes.put(SIMAttributeTypeConstants.getName(simAttribute[0] & 0xFF) , simAttribute2);
			}
			
			byte[] nonce_Mt=null;
			if(receivedAttributes.containsKey(SIMAttributeTypeConstants.AT_NONCE_MT.name))
				nonce_Mt =receivedAttributes.get(SIMAttributeTypeConstants.AT_NONCE_MT.name).getValueBytes();
			
//			nonce_Mt = TLSUtility.HexToBytes("0x4545ef027b8d04004645ef027b8d0400");
			
			LogManager.getLogger().info(getModuleName(), "EAP Packet into Post Process : " + eapPacket);
			byte[] macBytes = calculateAtMACForSimChallenge(eapPacket, nonce_Mt);

			byte []finalMacBytes=new byte[16];
			System.arraycopy(macBytes, 0, finalMacBytes,0, 16);
			LogManager.getLogger().trace(getModuleName(), "MAC : " +TLSUtility.bytesToHex(macBytes));

			
			LogManager.getLogger().info(getModuleName(), "SIM EAP Packet : " + eapPacket);
			List<ISIMAttribute> simAttrList = ((SimEapType)eapPacket.getEAPType()).getSimAttrList();
			for(ISIMAttribute simAttribute : simAttrList){
				LogManager.getLogger().debug(getModuleName(), "Attr : " + TLSUtility.bytesToHex(simAttribute.getBytes()));
			}
			for(ISIMAttribute simAttribute : simAttrList){
				if(simAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){
					LogManager.getLogger().info(getModuleName(), "AT_MAC Length :  " + simAttribute.getValueBytes().length);
					simAttribute.setValueBytes(finalMacBytes);
				}
			}
			((SimEapType)eapPacket.getEAPType()).setSimAttrList(simAttrList);
			LogManager.getLogger().info(getModuleName(), "SIM EAP Packet : " + eapPacket);
			try {
				aaaEapRespData.getEapReqPacket().setEAPType((EAPType)simEapType);
			} catch (InvalidEAPPacketException e) {
				throw new EAPException("problem creating the packet");
			}
			LogManager.getLogger().trace(getModuleName(), "Completed the post Process");
		} else if (simEapType.getSimMessageType() == SIMMessageTypeConstants.RE_AUTHENTICATION.Id) {			

			LogManager.getLogger().info(getModuleName(), "EAP Packet into Post Process : " + eapPacket);
			byte[] macBytes = calculateAtMACForFastReauth(eapPacket);

			byte []finalMacBytes=new byte[16];
			System.arraycopy(macBytes, 0, finalMacBytes,0, 16);
			LogManager.getLogger().trace(getModuleName(), "MAC : " +TLSUtility.bytesToHex(macBytes));

			List<ISIMAttribute> simAttrList = ((SimEapType)eapPacket.getEAPType()).getSimAttrList();
			for(ISIMAttribute simAttribute : simAttrList){
				LogManager.getLogger().debug(getModuleName(), "Attr : " + TLSUtility.bytesToHex(simAttribute.getBytes()));
			}
			for(ISIMAttribute simAttribute : simAttrList){
				if(simAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){
					LogManager.getLogger().info(getModuleName(), "AT_MAC Length :  " + simAttribute.getValueBytes().length);
					simAttribute.setValueBytes(finalMacBytes);
				}
			}
			((SimEapType)eapPacket.getEAPType()).setSimAttrList(simAttrList);
			LogManager.getLogger().info(getModuleName(), "SIM EAP Packet : " + eapPacket);
			try {
				aaaEapRespData.getEapReqPacket().setEAPType((EAPType)simEapType);
			} catch (InvalidEAPPacketException e) {
				throw new EAPException("problem creating the packet");
			}
			LogManager.getLogger().trace(getModuleName(), "Completed the post Process");
		}
	}

	private byte[] calculateAtMACForSimChallenge(EAPPacket eapPacket, byte[] nonce_Mt){
		byte [] macBytes = eapPacket.getBytes();
		macBytes = TLSUtility.appendBytes(macBytes, nonce_Mt);
		LogManager.getLogger().trace(getModuleName(), "MAC Input: " +TLSUtility.bytesToHex(macBytes));
		return TLSUtility.HMAC("SHA-1", macBytes, getK_aut());
	}
	
	private byte[] calculateAtMACForFastReauth(EAPPacket eapPacket){
		byte [] macBytes = eapPacket.getBytes();
		LogManager.getLogger().trace(getModuleName(), "MAC Input: " +TLSUtility.bytesToHex(macBytes));
		return TLSUtility.HMAC("SHA-1", macBytes, getK_aut());
	}
	
	@Override
	public String getModuleName() {
		return MODULE;
	}

	public static void main(String[] args) {
		byte []rand = TLSUtility.HexToBytes("0xabcd1234abcd1234abcd1234abcd1234bcd1234abcd1234abcd1234abcd1234acd1234abcd1234abcd1234abcd1234ab");
		byte[] nonce_Mt=TLSUtility.HexToBytes("0x8997d16e641b2efa0aead025bebd1177");
	
//		byte[] test1 = TLSUtility.HexToBytes("0x0123456789abcdef");
//		String test2 = TLSUtility.bytesToHex(test1);
//		System.out.println(test2);

//		byte[] digest = TLSUtility.HMAC("MD5",TLSUtility.HexToBytes("0xDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD"),TLSUtility.HexToBytes("0xAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
//		System.out.println("HMAC " + TLSUtility.bytesToHex(digest));
		
		
//		byte [] macBytes = eapPacket.getBytes();
//		macBytes = TLSUtility.appendBytes(macBytes, nonce_Mt);
//		macBytes = TLSUtility.HMAC("SHA-1", macBytes, getKauthKey());
		
//		byte []finalMacBytes=new byte[16];
//		System.arraycopy(macBytes, 0, finalMacBytes,0, 16);
//		System.out.println(TLSUtility.bytesToHex(macBytes));
	}
}
