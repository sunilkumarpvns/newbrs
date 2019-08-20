package com.elitecore.diameterapi.diameter.common.packet.avps.derived;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpOctetString;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;

public class AvpUserEquipmentInfoValue extends AvpOctetString {
	public static final String MODULE = "AVP-USR-EQUIPMNT-INFO-VALUE";
	private Map<String, String> elementsMap = new HashMap<String, String>();
	
	public static final String SVN = "SVN";
	public static final String SNR = "SNR";
	public static final String TAC = "TAC";
	
	public static final String MAC = "MAC";
	
	public static final String EUI64 = "EUI64";
	
	public static final String MODIFIED_EUI64 = "MODIFIED-EUI64";
	
	
	
	public AvpUserEquipmentInfoValue(int intAVPCode, int intVendorId,
			byte bAVPFlag, String strAvpId, String strAVPEncryption) {
		super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);		
	}
	
	public Map<String, String> getIMEISV(){
		byte [] valueBytes = getValueBytes();
		
		String strIMEISV;
		if(valueBytes.length >= 15){
			strIMEISV = new String(valueBytes);
		}else{
			strIMEISV = DiameterUtility.bytesToHex(valueBytes);
		}
		
		if(strIMEISV.length() >= 15){
			elementsMap.put(SVN, strIMEISV.substring(14));
			elementsMap.put(SNR, strIMEISV.substring(8,14));
			elementsMap.put(TAC, strIMEISV.substring(0, 8));
		}

		return elementsMap;
	}
	
	//The 48-bit MAC address
	public String getMacAddress(){
		String val = getStringValue();
		if(val != null && val.length() > 0)
			elementsMap.put(MAC, val);
		return val;
	}
	
	//The 64-bit EUI64 identifier
	public String getEUI64(){
		String val = getStringValue();
		if(val != null && val.length() > 0)
			elementsMap.put(EUI64, val);
		return val;
	}
	//MODIFIED_EUI64
	public String getModifiedEUI64(){
		String val = getStringValue();
		if(val != null && val.length() > 0)
			elementsMap.put(MODIFIED_EUI64, val);
		return val;
	}
	
	@Override
	public final String getLogValue(){
		return this.elementsMap.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AvpUserEquipmentInfoValue clonedAvpUserEquiInfoValue = (AvpUserEquipmentInfoValue) super.clone();

		Map<String, String> elementsMap = new HashMap<String, String>(this.elementsMap.size());
		
		for(Map.Entry<String, String> element : this.elementsMap.entrySet()){
			elementsMap.put(element.getKey(), element.getValue());
		}

		clonedAvpUserEquiInfoValue.elementsMap = elementsMap;

		return clonedAvpUserEquiInfoValue;
	}
}
