package com.elitecore.coreeap.fsm.eap.method.akaprime;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.fsm.EAPFailureReasonConstants;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.akaprime.AkaPrimeEapType;
import com.elitecore.coreeap.packet.types.sim.SIMAttributeDictionary;
import com.elitecore.coreeap.packet.types.sim.attributes.ISIMAttribute;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.actions.AkaActions;
import com.elitecore.coreeap.util.constants.fsm.events.AkaEvents;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.constants.sim.message.SIMMessageTypeConstants;
import com.elitecore.coreeap.util.sim.SIMUtility;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AKAPrimeMethodStateMachine extends BaseAKAPrimeMethodStateMachine {

	private static final String MODULE = "AKA PRIME STATE MACHINE";
	
	public AKAPrimeMethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
	}
	public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		LogManager.getLogger().trace(getModuleName(), "AKA PRIME Packet Processing in State :" + getCurrentState());		
		setSuccess(false);
		setFailure(false);
		setDone(false);
		AkaEvents event = (AkaEvents)getEvent(aaaEapRespData);
		setCurrentEvent(event);
		try {
		handleEvent(event, aaaEapRespData,provider);
		} catch (Exception e){
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().warn(MODULE, "Failed to handle EAP-AKA-PRIME event: " + event);
			setSuccess(false);
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.AKA_PRIME_FAILURE);
		}
		LogManager.getLogger().info(getModuleName(), "AKA PRIME Packet has been processed");
		return getCurrentEvent();
	}
	@Override
	public AAAEapRespData handleEvent(IEnum event, AAAEapRespData aaaEapRespData, ICustomerAccountInfoProvider provider)
			throws EAPException {
		LogManager.getLogger().trace(getModuleName(), "Handle - AKA -PRIME Event :"+ event);
		IEnum[] actionList = getActionList(event);
		if(actionList != null){
			LogManager.getLogger().trace(getModuleName(), "AKA PRIME Actions for Event :"+ event + " = " + actionList);
			for(int iCounter=0; iCounter < actionList.length ; iCounter ++){
				switch((AkaActions)actionList[iCounter]){
				case BuildFailure:
					buildFailure(aaaEapRespData,provider);
					break;
				case BuildSuccess:
					buildSuccess(aaaEapRespData,provider);
					break;
				case CheckForIsIdReqRequired:
					checkForIsIdReqRequired(aaaEapRespData, provider);
					break;
				case DiscardRequest:
					discardRequest(aaaEapRespData,provider);
					break;
				case GenerateStart:
					generateIdentityOrChallengeMessage(aaaEapRespData, provider);
					break;
				case GenerateResponse:
					generateResponse(aaaEapRespData,provider);
					break;
				case ProcessResponseIdentity:
					processResponseIdentity(aaaEapRespData, provider);
					break;
				case Validate_Response:
					validateAKAPRIMEResponse(aaaEapRespData,provider);
				}
			}
		}else{
			LogManager.getLogger().trace(getModuleName(), "No AKA PRIME Actions for Event :"+ event);
		}
		return null;
	}
	public boolean check(AAAEapRespData aaaEapRespData) {
		parseAKAPRIMEResp(aaaEapRespData);
		return isValidRequest();
	}

	public int getMethodCode() {	
		return EapTypeConstants.AKA_PRIME.typeId;
	}
	
	public void process(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		processRequest(aaaEapRespData,provider);		
	}
	
	public void preProcess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException{
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		AkaPrimeEapType akaEapType =  (AkaPrimeEapType) eapPacket.getEAPType();
		if(akaEapType.getAkaMessageType() == SIMMessageTypeConstants.AKA_IDENTITY.Id){
			setTotalAKARoundTripBytes(TLSUtility.appendBytes(getTotalAKARoundTripBytes(), aaaEapRespData.getEapRespPacketBytes()));
		}
	}
	
	public void postProcess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider)throws EAPException{
		//#include <freeradius-devel/smodule.h>
		EAPPacket eapPacket = aaaEapRespData.getEapReqPacket();
		AkaPrimeEapType akaEapType =  (AkaPrimeEapType) eapPacket.getEAPType();
		if(akaEapType.getAkaMessageType() == SIMMessageTypeConstants.AKA_IDENTITY.Id){
			//preserving Identity bytes for verifying AT_CHECKCODE
			setTotalAKARoundTripBytes(TLSUtility.appendBytes(getTotalAKARoundTripBytes(), aaaEapRespData.getEapReqPacketBytes()));			
		}else if(akaEapType.getAkaMessageType() == SIMMessageTypeConstants.AKA_CHALLENGE.Id){			
			AkaPrimeEapType receivedAkaEapType = (AkaPrimeEapType)aaaEapRespData.getEapReqPacket().getEAPType();
			Collection<byte[]> akaAttrs = receivedAkaEapType.getAkaAttributes();
			Iterator<byte[]> akaAttrsItr = akaAttrs.iterator();
			HashMap<String, ISIMAttribute> receivedAttributes = new HashMap<String, ISIMAttribute>();
			while(akaAttrsItr.hasNext()){
				byte[] akaAttribute = akaAttrsItr.next();
				ISIMAttribute akaAttribute2 = SIMAttributeDictionary.getInstance().getAttribute((akaAttribute[0] & 0xFF));
				akaAttribute2.setBytes(akaAttribute);
				receivedAttributes.put(SIMAttributeTypeConstants.getName(akaAttribute[0]), akaAttribute2);
			}
			
			byte[] nonce_Mt=null;
			if(receivedAttributes.containsKey(SIMAttributeTypeConstants.AT_NONCE_MT.name))
				nonce_Mt =receivedAttributes.get(SIMAttributeTypeConstants.AT_NONCE_MT.name).getValueBytes();
			
//			nonce_Mt = TLSUtility.HexToBytes("0x4545ef027b8d04004645ef027b8d0400");
			
			LogManager.getLogger().info(getModuleName(), "EAP Packet into Post Process : " + eapPacket);
			byte [] macBytes = eapPacket.getBytes();
			macBytes = TLSUtility.appendBytes(macBytes, nonce_Mt);
			LogManager.getLogger().trace(getModuleName(), "MAC Input: " +TLSUtility.bytesToHex(macBytes));
			try {
				macBytes = SIMUtility.HMAC_SHA256(getK_aut(), macBytes);
			} catch (InvalidKeyException e) {
				LogManager.getLogger().error(getModuleName(), "AKA-Prime post process failed, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			} catch (NoSuchAlgorithmException e) {
				LogManager.getLogger().error(getModuleName(), "AKA-Prime post process failed, Reason: " + e.getMessage());
				LogManager.getLogger().trace(e);
			}
			

			byte []finalMacBytes=new byte[16];
			System.arraycopy(macBytes, 0, finalMacBytes, 0, 16);
			LogManager.getLogger().trace(getModuleName(), "MAC : " +TLSUtility.bytesToHex(macBytes));

			
			List<ISIMAttribute> akaAttrList = ((AkaPrimeEapType)eapPacket.getEAPType()).getAkaAttrList();
			for(ISIMAttribute akaAttribute : akaAttrList){
				if(akaAttribute.getType() == SIMAttributeTypeConstants.AT_MAC.Id){
					LogManager.getLogger().info(getModuleName(), "AT_MAC Length :  " + akaAttribute.getValueBytes().length);
					akaAttribute.setValueBytes(finalMacBytes);
				}
			}
			((AkaPrimeEapType)eapPacket.getEAPType()).setAkaAttrList(akaAttrList);
			try {
				aaaEapRespData.getEapReqPacket().setEAPType((EAPType)akaEapType);
			} catch (InvalidEAPPacketException e) {
				throw new EAPException("problem creating the packet");
			}
			LogManager.getLogger().trace(getModuleName(), "Completed teh post Process");
		}			
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
