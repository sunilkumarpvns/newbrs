package com.elitecore.coreeap.packet.types.sim;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.packet.types.sim.attributes.ISIMAttribute;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtAnyIdReq;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtAutn;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtAuts;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtCheckCode;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtClientErrorCode;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtCounter;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtCounterTooSmall;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtEncrData;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtFullAuthIdReq;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtIV;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtIdentity;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtKdf;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtKdfInput;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtMac;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtNextPseudonym;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtNextReAuthId;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtNonceMt;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtNonceS;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtNotification;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtPadding;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtPermanentIdReq;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtRand;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtRes;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtResultInd;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtSelectedVersion;
import com.elitecore.coreeap.packet.types.sim.attributes.types.AtVersionList;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class SIMAttributeDictionary {
	private static final String MODULE = "SIM ATTRIBUTE DICTIONARY";
	
	private static SIMAttributeDictionary simAttributeDictionary;
	
	private Map<String, ISIMAttribute> simAttributeInstanceMap;
	
	private SIMAttributeDictionary(){
		loadSIMAttributeInstances();
	}
	
	private void loadSIMAttributeInstances(){
		
		simAttributeInstanceMap = new HashMap<String, ISIMAttribute>();
		
		AtAnyIdReq atAnyIdReq = new AtAnyIdReq();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_ANY_ID_REQ.name, atAnyIdReq);
		
		AtClientErrorCode atClientErrorCode = new AtClientErrorCode();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_CLIENT_ERROR_CODE.name, atClientErrorCode);
		
		AtCounter atCounter = new AtCounter();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_COUNTER.name, atCounter);
		
		AtCounterTooSmall atCounterTooSmall = new AtCounterTooSmall();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_COUNTER_TOO_SMALL.name, atCounterTooSmall);
		
		AtEncrData atEncrData = new AtEncrData();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_ENCR_DATA.name, atEncrData);
		
		AtFullAuthIdReq atFullAuthIdReq = new AtFullAuthIdReq();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_FULLAUTH_ID_REQ.name, atFullAuthIdReq);
		
		AtIdentity atIdentity = new AtIdentity();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_IDENTITY.name, atIdentity);
		
		AtIV atIV = new AtIV();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_IV.name, atIV);
		
		AtMac atMac = new AtMac();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_MAC.name, atMac);
		
		AtNextPseudonym atNextPseudonym = new AtNextPseudonym();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_NEXT_PSEUDONYM.name, atNextPseudonym);
		
		AtNextReAuthId atNextReAuthId = new AtNextReAuthId();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_NEXT_REAUTH_ID.name, atNextReAuthId);
		
		AtNonceMt atNonceMt = new AtNonceMt();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_NONCE_MT.name, atNonceMt);
		
		AtNonceS atNonceS = new AtNonceS();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_NONCE_S.name, atNonceS);
		
		AtNotification atNotification = new AtNotification();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_NOTIFICATION.name, atNotification);
		
		AtPadding atPadding = new AtPadding();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_PADDING.name, atPadding);
		
		AtPermanentIdReq atPermanentIdReq = new AtPermanentIdReq();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_PERMANENT_ID_REQ.name, atPermanentIdReq);
		
		AtRand atRand = new AtRand();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_RAND.name, atRand);
		
		AtResultInd atResultInd = new AtResultInd();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_RESULT_IND.name, atResultInd);
		
		AtSelectedVersion atSelectedVersion = new AtSelectedVersion();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_SELECTED_VERSION.name, atSelectedVersion);
		
		AtVersionList atVersionList = new AtVersionList();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_VERSION_LIST.name, atVersionList);
		
		AtAutn atAutn = new AtAutn();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_AUTN.name, atAutn);
		
		AtRes atRes = new AtRes();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_RES.name, atRes);
		
		AtAuts atAuts = new AtAuts();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_AUTS.name, atAuts);
		
		AtCheckCode atCheckCode = new AtCheckCode();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_CHECKCODE.name, atCheckCode);
		
		AtKdfInput atKdfInput = new AtKdfInput();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_KDF_INPUT.name, atKdfInput);
		
		AtKdf atKdf = new AtKdf();
		simAttributeInstanceMap.put(SIMAttributeTypeConstants.AT_KDF.name, atKdf);
	}
	
	static {
		simAttributeDictionary = new SIMAttributeDictionary();
	}
	
	public static SIMAttributeDictionary getInstance(){
		return simAttributeDictionary;
	}
	
	public ISIMAttribute getAttribute(int id){		
		return(getAttribute(SIMAttributeTypeConstants.getName(id)));
	}
	
	public ISIMAttribute getAttribute(String name){
		if(name == null)			
			return null;
		try {
			return (ISIMAttribute) simAttributeInstanceMap.get(name).clone();
		} catch (CloneNotSupportedException e) {
			LogManager.getLogger().trace(MODULE, "Clone not supported for " + name);
			LogManager.getLogger().trace(MODULE, e);
		}
		return null;
	}
}
