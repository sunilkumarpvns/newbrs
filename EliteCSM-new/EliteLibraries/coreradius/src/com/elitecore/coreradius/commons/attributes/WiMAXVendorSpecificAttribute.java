package com.elitecore.coreradius.commons.attributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
 
public class WiMAXVendorSpecificAttribute extends BaseRadiusAttribute implements IVendorSpecificAttribute {

	private static final long serialVersionUID = 1L;
	private ArrayList<IRadiusAttribute> attributeList;
	
	public WiMAXVendorSpecificAttribute(){
		setType(RadiusAttributeConstants.VENDOR_SPECIFIC);
		attributeList = new ArrayList<IRadiusAttribute>();
	}
	
	public int readFromForLength(InputStream sourceStream, int length) throws IOException {
		attributeList = new ArrayList<IRadiusAttribute>();
		int totalBytes = 0;
		int attributeType = 0;
		while (totalBytes < (length - 6) && (attributeType = sourceStream.read()) != -1 ) {
			totalBytes++;
			BaseRadiusAttribute attribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(getVendorID(),attributeType);
			totalBytes += attribute.readLengthOnwardsFrom(sourceStream);
			
			attributeList.add(attribute);
		}
		return totalBytes;
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public void writeTo(OutputStream destinationStream) throws IOException {
		final int size = attributeList.size();

		for(int i=0; i<size; i++){
			IRadiusAttribute radiusAttribute = (IRadiusAttribute)attributeList.get(i);
			destinationStream.write(radiusAttribute.getBytes());
		}
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		
		Iterator<IRadiusAttribute> iterator = attributeList.iterator();
		
		while (iterator.hasNext()) {
			IRadiusAttribute radiusAttribute = iterator.next();
			out.print(" " + Dictionary.getInstance().getAttributeName(RadiusConstants.WIMAX_VENDOR_ID, radiusAttribute.getID()) + radiusAttribute);
			if (iterator.hasNext()){
				out.println();
			}
		}

		out.flush();
		out.close();
		return stringBuffer.toString();
	}
	
	public void addAttribute(IRadiusAttribute attribute) {
		attributeList.add(attribute);
	}
	
	public void setAttribute(IRadiusAttribute attribute){
		Iterator<IRadiusAttribute> iterator = attributeList.iterator();
		IRadiusAttribute oldAttribute = null;
			
		while (iterator.hasNext()) {
			IRadiusAttribute radiusAttribute = ((IRadiusAttribute)iterator.next());
			if(radiusAttribute.getType() == attribute.getType()){
				oldAttribute = radiusAttribute;
				break;
			}
		}
		
		if(oldAttribute != null){
			attributeList.remove(oldAttribute);
		}
		
		attributeList.add(attribute);
	}
	
	public IRadiusAttribute getAttribute(int attributeID) {
		for(int i=0 ; i < attributeList.size(); i++){
			if(attributeID == attributeList.get(i).getType())
				return attributeList.get(i);
		}
		return null;
	}
	
	public ArrayList<IRadiusAttribute> getAttributes(int attributeID) {
		ArrayList<IRadiusAttribute> attrList = new ArrayList<IRadiusAttribute>();
		for(int i =0 ; i< attributeList.size() ;i++){
			if(attributeID == attributeList.get(i).getType())
				attrList.add(attributeList.get(i));
 		}
		if(!attrList.isEmpty())
			return attrList;
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
		return attributeList;
	}
	
	
	/**
	 * Will not perform complete clonning. Upto first level deep cloning is implemented. 
	 */
	public Object clone() throws CloneNotSupportedException {
		WiMAXVendorSpecificAttribute result = null;
		result = (WiMAXVendorSpecificAttribute)super.clone();
		result.attributeList = new ArrayList<IRadiusAttribute>();
		
		for(int i=0;i<attributeList.size();i++){
			BaseRadiusAttribute attribute = (BaseRadiusAttribute)this.attributeList.get(i).clone(); 
			result.attributeList.add(attribute);
			
		}

		return result;
	}
	
	public void refreshAttributeHeader(){
		int length = 0;
		Iterator<IRadiusAttribute> iterator = attributeList.iterator();
		while(iterator.hasNext()){
			IRadiusAttribute radiusAttribute = iterator.next();
			((BaseRadiusAttribute)radiusAttribute).refreshAttributeHeader();
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
		final int listSize = attributeList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute radiusAttribute = attributeList.get(i);
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
