package com.elitecore.coreradius.commons.attributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

public class VendorSpecificAttribute extends BaseRadiusAttribute implements IRadiusVendorSpecificAttribute{

	private static final long serialVersionUID = 1L;
	private IVendorSpecificAttribute vendorSpecificAttribute = null;
	
	public VendorSpecificAttribute(){
		setType(RadiusAttributeConstants.VENDOR_SPECIFIC);
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException {
		
		vendorID = 0;
		length = sourceStream.read();
		vendorID = (vendorID << 8) | (sourceStream.read() & 0xFF);
		vendorID = (vendorID << 8) | (sourceStream.read() & 0xFF);
		vendorID = (vendorID << 8) | (sourceStream.read() & 0xFF);
		vendorID = (vendorID << 8) | (sourceStream.read() & 0xFF);
		
		setVendorID(vendorID);
		int totalBytes = 5;
		
		vendorSpecificAttribute = Dictionary.getInstance().getVendorAttributeType(vendorID);
		
		totalBytes += vendorSpecificAttribute.readFromForLength(sourceStream, getLength());
		
		return totalBytes ;
		
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public void writeTo(OutputStream destinationStream) throws IOException {
		
		destinationStream.write((byte)getType());
		destinationStream.write((getLength()>255)?255:getLength());
		//write first byte of vendor id as 0
		destinationStream.write((byte)(vendorID >>> 24));
		destinationStream.write((byte)(vendorID >>> 16));
		destinationStream.write((byte)(vendorID >>> 8));
		destinationStream.write((byte)(vendorID));
		if(vendorSpecificAttribute != null){
			vendorSpecificAttribute.writeTo(destinationStream);
		}
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		
		if(Dictionary.getInstance().getVendorName(getVendorID()) != null){
			out.print("\t" + Dictionary.getInstance().getVendorName(getVendorID()));				
		if(vendorSpecificAttribute != null){
			out.print(vendorSpecificAttribute.toString());
		}
		}else{
			out.print("\tUnknown VSA = " + RadiusUtility.bytesToHex(getBytes()));
		}

		out.flush();
		out.close();
		return stringBuffer.toString();
	}
	
	public IRadiusAttribute getAttribute(int attributeID) {
		if(vendorSpecificAttribute != null){
			return vendorSpecificAttribute.getAttribute(attributeID);
		}
		return null;
	}

	public Collection<IRadiusAttribute> getAttributes(int attributeID) {
		return getAttributes(String.valueOf(attributeID));
	}
	
	public IRadiusAttribute getAttribute(String attributeID) {
		if(vendorSpecificAttribute != null){
			return vendorSpecificAttribute.getAttribute(attributeID);
		}
		return null;
	}

	public Collection<IRadiusAttribute> getAttributes(String attributeID) {
		if(vendorSpecificAttribute != null){
			return vendorSpecificAttribute.getAttributes(attributeID);
		}
		return null;
	}
	
	public ArrayList<IRadiusAttribute> getAttributes() {
		if(vendorSpecificAttribute != null){
			return vendorSpecificAttribute.getAttributes();
		}
		return null;
	}
	
	
	/**
	 * Will not perform complete clonning. Upto first level deep cloning is implemented. 
	 */
	public Object clone() throws CloneNotSupportedException {
		VendorSpecificAttribute result = null;
		result = (VendorSpecificAttribute)super.clone();
		if(this.vendorSpecificAttribute == null)
			result.vendorSpecificAttribute = null;
		else
			result.vendorSpecificAttribute = (IVendorSpecificAttribute) this.vendorSpecificAttribute.clone();
		return result;
	}
	
	public void refreshAttributeHeader(){
		if(vendorSpecificAttribute != null){
			vendorSpecificAttribute.refreshAttributeHeader();
			length = 6+vendorSpecificAttribute.getBytes().length;
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn("Vendor Specific Attribute", "The vendor specific attribute for vendor id " + this.getVendorID() + " does not contain any actual attribute, setting length as 6");
			length = 6;
		}
	}

	public byte[] getBytes() {
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeTo(buffer);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return buffer.toByteArray();
	}

	public void setStringValue(String value) {
		// TODO Auto-generated method stub
		
	}

	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		
	}

	public String getStringValue() {
		if(vendorSpecificAttribute != null)
			return vendorSpecificAttribute.getStringValue();
		return null;
	}

	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setVendorTypeAttribute(IVendorSpecificAttribute attribute){
		vendorSpecificAttribute = attribute;
	}

	public IVendorSpecificAttribute getVendorTypeAttribute() {
		return vendorSpecificAttribute;
	}

	public IRadiusAttribute getAttribute(int ... attributeIds) {
		if(vendorSpecificAttribute != null)
			return vendorSpecificAttribute.getAttribute(attributeIds);
		return null;
	}
	
	public Collection<IRadiusAttribute> getAttributes(int ... attributeIds) {
		if(vendorSpecificAttribute != null)
			return vendorSpecificAttribute.getAttributes(attributeIds);
		return null;
	}
	
	public void reencryptValue(String oldSecret, byte[] oldAuthenticator, String newSecret, byte[] newAuthenticator){
		if(vendorSpecificAttribute != null){
			vendorSpecificAttribute.reencryptValue(oldSecret, oldAuthenticator, newSecret, newAuthenticator);
		}
	}
	public boolean isVendorSpecific() {		
		return true;
	}
	
	
}
