package com.elitecore.coreradius.commons.attributes;

import java.io.UnsupportedEncodingException;

public class EgressVLANID extends BaseRadiusAttribute implements Cloneable {
	
	private static final long serialVersionUID = 1L;

	public EgressVLANID(AttributeId attributeDetail) {
		super(attributeDetail);
	}
	public EgressVLANID() {

	}
	
	public Object clone() throws CloneNotSupportedException {
		EgressVLANID egressVlanId = (EgressVLANID) super.clone();
		return egressVlanId;
	}

	@Override
	public void setStringValue(String value){
		if (value != null && value.length() != 0){
			String taglessValue = value;
			if(hasTag()){
				taglessValue = setTagAndGetTaglessValue(value);
				if(getTag()!=49 && getTag()!=50) {
					throw new IllegalArgumentException("Invalid Tag id, It can be 49 or 50 only.");
				}
			}
			int vlanid = 0;
			try {
				vlanid = Integer.parseInt(taglessValue);	
			} catch (NumberFormatException numberFormatExp) {
				throw new IllegalArgumentException("Cannot convert " + taglessValue + " to EgressVLANID Attribute.",numberFormatExp);
			}
			byte[] vLANIdBytes = new byte[3];
			vLANIdBytes[1] = (byte) ((vlanid >> 8) & 0x0F);
			vLANIdBytes[2] = (byte) (vlanid & 0xFF);
			setValueBytes(vLANIdBytes);
		} else {
			throw new IllegalArgumentException("Invalid Value for Egress_VLAN_Id");
		}
	}
	
	@Override
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
	}

	@Override
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}
	
	public String getStringValue() {
		byte[] value = getValueBytes();
		String vlanIdStr = "";
		if(value!=null && value.length!=0) {
			int vlanID = (int)(((value[1] & 0x0F) << 8) | (value[2] & 0xFF));
			vlanIdStr = String.valueOf(vlanID);
			if(hasTag()) {
				vlanIdStr = String.valueOf(getTag()) + ":" + vlanIdStr;
			}
		}
		return vlanIdStr;
	}
	
	/**
	 * This toString() method will be removed once ELITEAAA-2250 will be resolved.
	 */
	@Override
	public String toString() {		
		return " = " + getStringValue();
	}
}
