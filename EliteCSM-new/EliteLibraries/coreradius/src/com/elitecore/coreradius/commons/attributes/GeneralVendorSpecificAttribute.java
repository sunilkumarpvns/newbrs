package com.elitecore.coreradius.commons.attributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

public class GeneralVendorSpecificAttribute extends BaseRadiusAttribute implements IVendorSpecificAttribute {

	private static final long serialVersionUID = 1L;

	private ArrayList<IRadiusAttribute> vendorAttributeList;
	
	private static final int DEFAULT_ATTRIBUTE_MAP_SIZE = 0;
	
	public GeneralVendorSpecificAttribute(){
		setType(RadiusAttributeConstants.VENDOR_SPECIFIC);
		vendorAttributeList = new ArrayList<IRadiusAttribute>(DEFAULT_ATTRIBUTE_MAP_SIZE);
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public int readFromForLength(InputStream sourceStream, int length) throws IOException {
		vendorAttributeList = new ArrayList<IRadiusAttribute>(DEFAULT_ATTRIBUTE_MAP_SIZE);
		int attributeType = 0;
		int totalBytes = 0;
		while (totalBytes < (length - 6) && (attributeType = sourceStream.read()) != -1 ) {
			//the order of the above chek is important, do not modify the order.
			BaseRadiusAttribute radiusAttribute = (BaseRadiusAttribute) Dictionary.getInstance().getAttribute(getVendorID(), attributeType);
			totalBytes++; // to increase the attribute type read.
			totalBytes += radiusAttribute.readLengthOnwardsFrom(sourceStream);

			vendorAttributeList.add(radiusAttribute);
		}
		return totalBytes ;
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public void writeTo(OutputStream destinationStream) throws IOException {
		final int listSize = vendorAttributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = ((IRadiusAttribute)vendorAttributeList.get(i));
			destinationStream.write(radiusAttribute.getBytes());
		}
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		
		final int listSize = vendorAttributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = ((IRadiusAttribute)vendorAttributeList.get(i));
			out.print(" " + Dictionary.getInstance().getAttributeName(getVendorID(), radiusAttribute.getID()) + radiusAttribute.toString());
		}
			
		out.flush();
		out.close();
		return stringBuffer.toString();
	}
	
	public void addAttribute(IRadiusAttribute attribute) {
		vendorAttributeList.add(attribute);
	}
	
	public void setAttribute(IRadiusAttribute attribute){
		IRadiusAttribute oldAttribute = null;
		
		final int listSize = vendorAttributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute= ((IRadiusAttribute)vendorAttributeList.get(i));
			if(radiusAttribute.getType() == attribute.getType()){
				oldAttribute = radiusAttribute;
				break;
			}
		}
		
		if(oldAttribute != null){
			vendorAttributeList.remove(oldAttribute);
		}
		
		vendorAttributeList.add(attribute);
	}
	
	public IRadiusAttribute getAttribute(int attributeID) {
		for(int i=0 ; i < vendorAttributeList.size() ; i++){
			if(vendorAttributeList.get(i).getType() == attributeID){
				return vendorAttributeList.get(i);
			}
		}
		return null;
	}

	public ArrayList<IRadiusAttribute> getAttributes(int attributeID) {
		ArrayList<IRadiusAttribute> attributeList = new ArrayList<IRadiusAttribute>();
		for(int i=0; i< vendorAttributeList.size() ; i++){
			if(attributeID == vendorAttributeList.get(i).getType()){
				attributeList.add(vendorAttributeList.get(i));
			}
		}
		if(!attributeList.isEmpty())
			return attributeList;
		return null;
	}
	
	public IRadiusAttribute getAttribute(String attributeID) {
		return getAttribute(Integer.parseInt(attributeID));		
	}

	public IRadiusAttribute getSubAttribute(int ... attributeIds) {
       IRadiusAttribute attr = null;
       int id1 = attributeIds[0];
       int newAttrIds[] = new int[attributeIds.length-1];
       System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
       attr = (BaseRadiusAttribute)getAttribute(id1);
       if(attr != null){
               attr = ((BaseRadiusAttribute)attr).getSubAttribute(newAttrIds);
       }
       return attr;
    }

    public IRadiusAttribute getAttribute(int ... attributeIds){
       if(attributeIds.length == 1){
              return getAttribute(attributeIds[0]);
       }else{
               return getSubAttribute(attributeIds);
       }
    }
		
    public ArrayList<IRadiusAttribute> getAttributes(int ... attributeIds) {
		if(attributeIds.length == 1){
			return getAttributes(String.valueOf(attributeIds[0]));
		}else{
			return getSubAttributes(attributeIds);
		}
	}    

    public ArrayList<IRadiusAttribute> getSubAttributes(int ... attributeIds) {
    	ArrayList<IRadiusAttribute> retAttrList = null;
		int id1 = attributeIds[0];
		int newAttrIds[] = new int[attributeIds.length-1];
		System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
		ArrayList<IRadiusAttribute> mainAttrList = getAttributes(id1);
		if(mainAttrList != null){
			final int listSize = mainAttrList.size();
			for(int i=0;i<listSize;i++) {
				ArrayList<IRadiusAttribute> subAttrList = ((BaseRadiusAttribute)mainAttrList.get(i)).getSubAttributes(newAttrIds);
				if(subAttrList != null){
					if(retAttrList == null){
						retAttrList = new ArrayList<IRadiusAttribute>();
					}
					retAttrList.addAll(subAttrList);
				}
			}
		}
		return retAttrList;
	}
    
	public ArrayList<IRadiusAttribute> getAttributes(String attributeID) {
		return getAttributes(Integer.parseInt(attributeID));
	}
	
	public ArrayList<IRadiusAttribute> getAttributes() {
		return vendorAttributeList;
	}
	
	
	/**
	 * Will not perform complete clonning. Upto first level deep cloning is implemented. 
	 */
	public Object clone() throws CloneNotSupportedException {
		GeneralVendorSpecificAttribute result = null;
		result = (GeneralVendorSpecificAttribute)super.clone();
		result.vendorAttributeList = new ArrayList<IRadiusAttribute>();
		
		final int listSize = vendorAttributeList.size();
		for(int i=0;i<listSize;i++) {
			BaseRadiusAttribute radiusAttribute = (BaseRadiusAttribute)this.vendorAttributeList.get(i).clone(); 
			result.vendorAttributeList.add(radiusAttribute);
		}
		return result;
	}
	
	public void refreshAttributeHeader(){

		int length = 0;
		final int listSize = vendorAttributeList.size();
		for(int i=0;i<listSize;i++) {
			BaseRadiusAttribute radiusAttribute = (BaseRadiusAttribute)vendorAttributeList.get(i);
			radiusAttribute.refreshAttributeHeader();
			length = length + radiusAttribute.getLength();
		}
		this.length = 6+length;
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
		return super.getStringValue();
	}

	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return new String(getValueBytes(), charsetName);
	}

	public void reencryptValue(String oldSecret, byte[] oldAuthenticator, String newSecret, byte[] newAuthenticator){
		final int listSize = vendorAttributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = vendorAttributeList.get(i);
			radiusAttribute.reencryptValue(oldSecret, oldAuthenticator, newSecret, newAuthenticator);
		}
	}
	
	public byte[] getValueBytes() {
		return getBytes();
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
