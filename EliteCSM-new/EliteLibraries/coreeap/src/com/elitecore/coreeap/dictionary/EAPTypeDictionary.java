/*
 *	Radius EAP-Module API
 *	Elitecore Technologies Ltd.
 *	904, Silicon Tower, Law Garden
 *	Ahmedabad, India - 380009
 *
 *	Created on Jan 7, 2007
 *	Created By Dhirendra Kumar Singh
 */

package com.elitecore.coreeap.dictionary;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.packet.types.EAPExpandedType;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.packet.types.IdentityEAPType;
import com.elitecore.coreeap.packet.types.NAKEAPType;
import com.elitecore.coreeap.packet.types.NotificationEAPType;
import com.elitecore.coreeap.packet.types.aka.AkaEapType;
import com.elitecore.coreeap.packet.types.akaprime.AkaPrimeEapType;
import com.elitecore.coreeap.packet.types.gtc.GtcEAPType;
import com.elitecore.coreeap.packet.types.md5.MD5ChallengeEAPType;
import com.elitecore.coreeap.packet.types.md5.MD5ChallengeExpandedEAPType;
import com.elitecore.coreeap.packet.types.mschapv2.MSCHAPv2EAPType;
import com.elitecore.coreeap.packet.types.sim.SimEapType;
import com.elitecore.coreeap.packet.types.tls.TLSEAPType;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.VendorSpecificEapTypeConstants;
 
/**
 *It creates appropriate EAP-Type based on the type field
 *It will also upload all types of EAP-types from the EAP-Dictionary file
 */
public class EAPTypeDictionary {
	
	private static final String MODULE = "EAP_TYPE FACTORY";
	private static final String ID_TYPE_SEPARATOR = ":";
	
	static private EAPTypeDictionary eapTypeFactory;
	
	/**
	 * Contains TypeCode-EAPType pair. Here, TypeCode 
	 * for Expanded-Type is in &lt;vendorID>:&lt;vendorType> format e.g.
	 * Expanded-Identity type will have key "0:1" while its legacy 
	 * type will have "1".
	 */
	private HashMap<String, EAPType> eapTypeInstanceMap;	
	
	private EAPTypeDictionary(){
		loadEAPInstances();
//		Logger.logTrace(MODULE, "ID-Instance=\n"+this.eapTypeInstanceMap);		
	}
	
	private void loadEAPInstances(){
		eapTypeInstanceMap = new HashMap<String, EAPType>();
		
		/*//FOR NO-ALTERNATIVES
		EAPType eapType = new EAPType();
		eapType.setType(0);
		eapTypeInstanceMap.put(String.valueOf(NO_ALTERNATIVE_TYPE), eapType);
		*/
		//FOR LEGACY IDENTITY
		IdentityEAPType identityEAPType = new IdentityEAPType();		
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.IDENTITY.typeId), identityEAPType);
		
		//FOR LEGACY NOTIFICATION
		NotificationEAPType notificationEAPType = new NotificationEAPType();
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.NOTIFICATION.typeId), notificationEAPType);
		
		//FOR LEGACY NAK
		NAKEAPType nakEAPType = new NAKEAPType();
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.NAK.typeId), nakEAPType);
		
		//FOR LEGACY MD5-CHALLENGE
		MD5ChallengeEAPType md5ChallengeEAPType = new MD5ChallengeEAPType();
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.MD5_CHALLENGE.typeId), md5ChallengeEAPType);

		GtcEAPType gtcEAPType = new GtcEAPType();
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.GTC.typeId), gtcEAPType);
		
//		Logger.logTrace(MODULE, "TLSEaptype : "+ eapTypeInstanceMap.keySet());
		TLSEAPType tlsEapType = new TLSEAPType();
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.TLS.typeId), tlsEapType);
		
		TLSEAPType ttlsEAPType = new TLSEAPType();
		ttlsEAPType.setTLSType(EapTypeConstants.TTLS.typeId);
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.TTLS.typeId),ttlsEAPType);

		TLSEAPType peapEAPType = new TLSEAPType();
		peapEAPType.setTLSType(EapTypeConstants.PEAP.typeId);
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.PEAP.typeId),peapEAPType);

		MSCHAPv2EAPType mschapv2EAPType = new MSCHAPv2EAPType();		
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.MSCHAPv2.typeId),mschapv2EAPType);

		SimEapType simEapType = new SimEapType();
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.SIM.typeId), simEapType);
		
		AkaEapType akaEapType = new AkaEapType();
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.AKA.typeId), akaEapType);
		
		AkaPrimeEapType akaPrimeEapType = new AkaPrimeEapType();
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.AKA_PRIME.typeId), akaPrimeEapType);

//		Logger.logTrace(MODULE, "TLSEaptype : "+ eapTypeInstanceMap.keySet());
		//FOR LEGACY OTP-CHALLENGE
		EAPType eapType = new EAPType(EapTypeConstants.OTP_CHALLENGE.typeId);
		eapTypeInstanceMap.put(String.valueOf(EapTypeConstants.OTP_CHALLENGE.typeId), eapType);
		
		//FOR EXPANDED IDENTITY
		EAPExpandedType eapExpandedType = new EAPExpandedType();
		eapExpandedType.setVendorType(EapTypeConstants.IDENTITY.typeId);		
		eapTypeInstanceMap.put(VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId+ID_TYPE_SEPARATOR+EapTypeConstants.IDENTITY.typeId, 
				eapExpandedType);
		
		//FOR EXPANDED NOTIFICATION
		eapExpandedType = new EAPExpandedType();
		eapExpandedType.setVendorType(EapTypeConstants.NOTIFICATION.typeId);
		eapTypeInstanceMap.put(VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId+ID_TYPE_SEPARATOR+EapTypeConstants.NOTIFICATION.typeId, 
				eapExpandedType);
		
		//FOR EXPANDED NAK
		eapExpandedType = new EAPExpandedType();
		eapExpandedType.setVendorType(EapTypeConstants.NAK.typeId);
		eapTypeInstanceMap.put(VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId+ID_TYPE_SEPARATOR+EapTypeConstants.NAK.typeId, 
				eapExpandedType);
		
		//FOR NAK with NO-ALTERNATIVE
		eapExpandedType = new EAPExpandedType();
		eapTypeInstanceMap.put(VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId+ID_TYPE_SEPARATOR+EapTypeConstants.NO_ALTERNATIVE.typeId, 
				eapExpandedType);
		
		//FOR EXPANDED MD5-CHALLENGE
		eapExpandedType = new MD5ChallengeExpandedEAPType();
		eapExpandedType.setVendorType(EapTypeConstants.MD5_CHALLENGE.typeId);
		eapTypeInstanceMap.put(VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId+ID_TYPE_SEPARATOR+EapTypeConstants.MD5_CHALLENGE.typeId, 
				eapExpandedType);
	
		
	}
	
	public static EAPTypeDictionary getInstance(){
		if (eapTypeFactory==null){
			eapTypeFactory = new EAPTypeDictionary();
		}
		return eapTypeFactory;
	}
	
	public EAPType createEAPType(int eapTypeCode) {

//		Logger.logTrace(MODULE,"eap hash map : " + this.eapTypeInstanceMap );
		EAPType eapType = this.eapTypeInstanceMap.get(String.valueOf(eapTypeCode));
//		Logger.logTrace(MODULE,"eap type : "+eapType );
		EAPType newEAPType = null;
		if (eapType!=null){
			try {
				newEAPType = (EAPType)eapType.clone();
			} catch (CloneNotSupportedException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Unable to create EAPType instance, reason : "+e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}else{
			newEAPType = new EAPType(eapTypeCode);
		}
		return newEAPType;
	}
	
	public EAPExpandedType createEAPExpandedType(int vendorId, long vendorType) {
		EAPExpandedType eapExpandedType = (EAPExpandedType)this.eapTypeInstanceMap.get(vendorId+ID_TYPE_SEPARATOR+vendorType);
		EAPExpandedType newEAPExpandedType = null;
		if (eapExpandedType!=null){
			try {
				newEAPExpandedType = (EAPExpandedType)eapExpandedType.clone();
			} catch (CloneNotSupportedException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "Unable to create EAPExpandedType instance, reason : "+e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		return newEAPExpandedType;
	}
	
	public int getEAPTypeCode(String eapTypeName){
		int eapTypeCode = -1;
		
		if (!eapTypeName.startsWith(EapTypeConstants.EXPANDED.name)){
			String strEAPTypeCode = String.valueOf(EapTypeConstants.getTypeId(eapTypeName));
			
			if (strEAPTypeCode!=null && strEAPTypeCode.length()>0){
				try{
					eapTypeCode = Integer.parseInt(strEAPTypeCode);
				}catch(NumberFormatException e){
					
				}
			}
		}
		
		return eapTypeCode;
	}
	
	public long getVendorTypeCode(String eapTypeName){
		long vendorTypeCode = -1;
		
		if (eapTypeName.startsWith(EapTypeConstants.EXPANDED.name)){
			String strEAPTypeCode = String.valueOf(EapTypeConstants.getTypeId(eapTypeName));
			if (strEAPTypeCode!=null && strEAPTypeCode.length()>0){
				if (strEAPTypeCode.indexOf(ID_TYPE_SEPARATOR)>0){
					String strVendorTypeCode = strEAPTypeCode.substring(
							strEAPTypeCode.indexOf(ID_TYPE_SEPARATOR)+1);
					try{
						vendorTypeCode = Long.parseLong(strVendorTypeCode);
					}catch(NumberFormatException e){
						
					}
				}
			}
		}
		
		return vendorTypeCode;
	}
	
	public int getVendorID(String eapTypeName){
		int vendorID = -1;
		
		if (eapTypeName.startsWith(EapTypeConstants.EXPANDED.name)){
			String strEAPTypeCode = String.valueOf(EapTypeConstants.getTypeId(eapTypeName));
			
			if (strEAPTypeCode!=null && strEAPTypeCode.length()>0){
				if (strEAPTypeCode.indexOf(ID_TYPE_SEPARATOR)>0){
					String strVendorID = strEAPTypeCode.substring(0, 
							strEAPTypeCode.indexOf(ID_TYPE_SEPARATOR));
					try{
						vendorID = Integer.parseInt(strVendorID);
					}catch(NumberFormatException e){
						
					}
				}
			}
		}
		
		return vendorID;
	}
	
//	public String getEAPTypeName(int vendorID, long vendorType){
//		return eapTypeIDNameMap.get(vendorID+ID_TYPE_SEPARATOR+vendorType);
//	}
	
	public static void main(String[] args) {
		EAPTypeDictionary eapTypeFactory = EAPTypeDictionary.getInstance();
		//System.out.println(eapTypeFactory.getVendorID(EXPANDED_NAME+" "+IDENTITY_NAME));
		//System.out.println(eapTypeFactory.getVendorTypeCode(EXPANDED_NAME+" "+IDENTITY_NAME));
		/*
		EAPType identityType = eapTypeFactory.createEAPType(IDENTITY_TYPE);
		EAPType identityType2 = eapTypeFactory.createEAPType(IDENTITY_TYPE);
		identityType.setData("HI".getBytes());
		System.out.println("Identity1:"+identityType);
		System.out.println("Identity2:"+identityType2);
		*/
		EAPExpandedType identityType = eapTypeFactory.createEAPExpandedType(VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId, EapTypeConstants.IDENTITY.typeId);
		EAPExpandedType identityType2 = eapTypeFactory.createEAPExpandedType(VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId, EapTypeConstants.IDENTITY.typeId);
		try{
			identityType.setData("HI".getBytes(CommonConstants.UTF8));
		}catch(UnsupportedEncodingException e){
			identityType.setData("HI".getBytes());
		}
		System.out.println("Identity1:"+identityType);
		System.out.println("Identity2:"+identityType2);
		//System.out.println("Expanded Identity:"+eapTypeFactory.createEAPExpandedType(IETF_VENDOR_ID, IDENTITY_TYPE));
	}
}

