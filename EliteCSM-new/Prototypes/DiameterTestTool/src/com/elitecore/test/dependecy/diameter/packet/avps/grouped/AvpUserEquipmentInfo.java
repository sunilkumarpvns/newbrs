package com.elitecore.test.dependecy.diameter.packet.avps.grouped;

import com.elitecore.test.dependecy.diameter.DiameterAVPConstants;
import com.elitecore.test.dependecy.diameter.DiameterUtility;
import com.elitecore.test.dependecy.diameter.packet.DiameterAttributeValueConstants;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;
import com.elitecore.test.dependecy.diameter.packet.avps.derived.AvpUserEquipmentInfoValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AvpUserEquipmentInfo extends AvpGrouped {
	private Map<String, String> elementsMap;
	public AvpUserEquipmentInfo(int intAVPCode, int intVendorId, byte bAVPFlag,
			String strAvpId, String strAVPEncryption,
			ArrayList<AvpRule> fixedArrayList,
			ArrayList<AvpRule> requiredaArrayList,
			ArrayList<AvpRule> optionalArrayList) {
		super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption,
				fixedArrayList, requiredaArrayList, optionalArrayList);
		
		elementsMap = new HashMap<String, String>();
	}
	
	
	protected void parseGroupedAttribute(){
		super.parseGroupedAttribute();
		
		IDiameterAVP equipmentInfoType = getSubAttribute(DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE);
		AvpUserEquipmentInfoValue equipmentInfoValue = (AvpUserEquipmentInfoValue)getSubAttribute(DiameterAVPConstants.USER_EQUIPMENT_INFO_VALUE);
		if(equipmentInfoType != null && equipmentInfoValue != null){		
			String strValue = null;
			switch ((int)equipmentInfoType.getInteger()) {
			
			case DiameterAttributeValueConstants.DIAMETER_IMEISV:
				elementsMap.putAll(equipmentInfoValue.getIMEISV());
				break;
				
			case DiameterAttributeValueConstants.DIAMETER_MAC:
				strValue = equipmentInfoValue.getMacAddress();
				if(strValue != null && strValue.length() > 0)
					elementsMap.put("MAC", strValue);
				break;
			case DiameterAttributeValueConstants.DIAMETER_EUI64:
				strValue = equipmentInfoValue.getEUI64();
				if(strValue != null && strValue.length() > 0)
					elementsMap.put("EUI64", equipmentInfoValue.getEUI64());
				break;
				
			case DiameterAttributeValueConstants.DIAMETER_MODIFIED_EUI64:
				strValue = equipmentInfoValue.getModifiedEUI64();
				if(strValue != null && strValue.length() > 0)
					elementsMap.put("MODIFIED_EUI64", equipmentInfoValue.getModifiedEUI64());
				break;			

			default:				
				break;
			}
		}
		
	}
	@Override
	public String getKeyStringValue(String key) {
		String value = this.elementsMap.get(key);
		if(value == null || value.equalsIgnoreCase("NULL")) {			 
			byte valueByte[] = this.getValueBytes();			
			byte[] result = new byte[valueByte.length-1];
			System.arraycopy(valueByte, 1, result, 0, valueByte.length-1);		
			return DiameterUtility.bytesToHex(result);
		}		
		return value;
	}
	@Override
	public Set<String> getKeySet(){
		return this.elementsMap.keySet();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		AvpUserEquipmentInfo clonedAvpUserEquiInfo = (AvpUserEquipmentInfo) super.clone();

		Map<String, String> elementsMap = new HashMap<String, String>(this.elementsMap.size());
		
		for(Map.Entry<String, String> element : this.elementsMap.entrySet()){
			elementsMap.put(element.getKey(), element.getValue());
		}

		clonedAvpUserEquiInfo.elementsMap = elementsMap;

		return clonedAvpUserEquiInfo;
	}
}
