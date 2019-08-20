package com.elitecore.coreeap.packet.types.mschapv2.packet;

import java.util.HashMap;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.constants.mschapv2.OpCodeConstants;

public class MSCHAPv2TypeDictionary {
	private static final String MODULE = "MSCHAPv2 FACTORY";
	
	static private MSCHAPv2TypeDictionary msCHAPv2TypeFactory;
	
	/**
	 * Contains TypeCode-EAPType pair. Here, TypeCode 
	 * for Expanded-Type is in &lt;vendorID>:&lt;vendorType> format e.g.
	 * Expanded-Identity type will have key "0:1" while its legacy 
	 * type will have "1".
	 */
	private HashMap<String, IMSCHAPv2Type> msCHAPv2TypeInstanceMap;	
	
	private MSCHAPv2TypeDictionary(){
		loadEAPInstances();
//		Logger.logTrace(MODULE, "ID-Instance=\n"+this.eapTypeInstanceMap);		
	}
	
	private void loadEAPInstances(){
		msCHAPv2TypeInstanceMap = new HashMap<String, IMSCHAPv2Type>();
		
		/*//FOR NO-ALTERNATIVES
		EAPType eapType = new EAPType();
		eapType.setType(0);
		eapTypeInstanceMap.put(String.valueOf(NO_ALTERNATIVE_TYPE), eapType);
		*/
		//FOR LEGACY IDENTITY
		ChallengeMSCHAPv2Type challengeMSCHAPv2Type = new ChallengeMSCHAPv2Type();		
		msCHAPv2TypeInstanceMap.put(String.valueOf(OpCodeConstants.CHALLENGE.opCode),challengeMSCHAPv2Type);
		
		//FOR LEGACY NOTIFICATION
		ResponseMSCHAPv2Type responseMSCHAPv2Type = new ResponseMSCHAPv2Type();
		msCHAPv2TypeInstanceMap.put(String.valueOf(OpCodeConstants.RESPONSE.opCode),responseMSCHAPv2Type);
		
		//FOR LEGACY NAK
		SuccessMSCHAPv2Type successMSCHAPv2Type = new SuccessMSCHAPv2Type();
		msCHAPv2TypeInstanceMap.put(String.valueOf(OpCodeConstants.SUCCESS.opCode),successMSCHAPv2Type);
		
		//FOR LEGACY MD5-CHALLENGE
		FailureMSCHAPv2Type failureMSCHAPv2Type = new FailureMSCHAPv2Type();
		msCHAPv2TypeInstanceMap.put(String.valueOf(OpCodeConstants.FAILURE.opCode),failureMSCHAPv2Type);
		
	}
	
	public static MSCHAPv2TypeDictionary getInstance(){
		if (msCHAPv2TypeFactory==null){
			msCHAPv2TypeFactory = new MSCHAPv2TypeDictionary();
		}
		return msCHAPv2TypeFactory;
	}
	
	public IMSCHAPv2Type createMSCHAPv2Type(int eapTypeCode) {

//		Logger.logTrace(MODULE,"eap hash map : " + this.eapTypeInstanceMap );
		IMSCHAPv2Type eapType = this.msCHAPv2TypeInstanceMap.get(String.valueOf(eapTypeCode));
//		Logger.logTrace(MODULE,"eap type : "+eapType );
		IMSCHAPv2Type newEAPType = null;
		if (eapType!=null){
			try {
				newEAPType = (IMSCHAPv2Type)eapType.clone();
			} catch (CloneNotSupportedException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Unable to create EAPType instance, reason : "+e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}else{
			newEAPType = new BaseMSCHAPv2Type();
		}
		return newEAPType;
	}

}
