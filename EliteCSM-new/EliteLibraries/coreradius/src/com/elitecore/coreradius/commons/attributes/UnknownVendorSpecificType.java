package com.elitecore.coreradius.commons.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

public class UnknownVendorSpecificType extends BaseRadiusAttribute implements IVendorSpecificAttribute{
	
	private static final long serialVersionUID = 1L;

	public UnknownVendorSpecificType(){
		setType(RadiusAttributeConstants.VENDOR_SPECIFIC);
	}

	@Override
	public void setStringValue(String value) {
		if (value != null) {
			if(value.startsWith("0x") || value.startsWith("0X")){
				setValueBytes(RadiusUtility.getBytesFromHexValue(value));
			} else{
				try{
					setValueBytes(value.getBytes(CommonConstants.UTF8));
				}catch(UnsupportedEncodingException e){
					setValueBytes(value.getBytes());
				}
			}
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
		byte data[] = getValueBytes();
		return RadiusUtility.bytesToHex(data);
	}
	
	@Override
	public String toString(){
		return getStringValue();
	}

	@Override
	public ArrayList<IRadiusAttribute> getAttributes(int attributeId) {
		return null;
	}

	@Override
	public ArrayList<IRadiusAttribute> getAttributes(int... attributeId) {
		return null;
	}

	@Override
	public int readFromForLength(InputStream sourceStream, int length)	throws IOException {
		byte[] buf = new byte[length-6];
		int lengthRead = sourceStream.read(buf);
		setValueBytes(buf);
		return lengthRead;
	}

	@Override
	public void writeTo(OutputStream destinationStream) throws IOException {
		destinationStream.write(getValueBytes());
	}
	@Override
	public void addAttribute(IRadiusAttribute attribute) {
		byte[] valueBytes = getValueBytes();
		valueBytes = RadiusUtility.appendBytes(valueBytes, attribute.getBytes());
		setValueBytes(valueBytes);
	}
	
	@Override
	public void refreshAttributeHeader() {
		length = getValueBytes().length + 6; 
	}

	@Override
	public IRadiusAttribute getAttribute(String attributeID) {
		return null;
	}

	@Override
	public ArrayList<IRadiusAttribute> getAttributes(String attributeID) {
		return null;
	}

	@Override
	public IRadiusAttribute getAttribute(int... attributeIds) {
		return null;
	}
	
	@Override
	public byte[] getBytes() {		
		return getValueBytes();
	}

	@Override
	public void addSubAttributes(IRadiusAttribute... subAttributes) {
		if(subAttributes == null){
			return;
		}
		for(int i = 0 ; i < subAttributes.length ; i++){
			addAttribute(subAttributes[i]);
		}
	}
	
	@Override
	public void addTLVAttribute(IRadiusAttribute tlvAttr) {
		addAttribute(tlvAttr);
	}
}
