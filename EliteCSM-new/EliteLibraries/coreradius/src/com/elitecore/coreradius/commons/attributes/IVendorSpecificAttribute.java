package com.elitecore.coreradius.commons.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public interface IVendorSpecificAttribute extends IRadiusGroupedAttribute {

	public ArrayList<IRadiusAttribute> getAttributes(int attributeId);
	
	public ArrayList<IRadiusAttribute> getAttributes(int ... attributeId);

	public ArrayList<IRadiusAttribute> getAttributes();

	public int readFromForLength(InputStream sourceStream, int length) throws IOException;

	public void addAttribute(IRadiusAttribute attribute);
	
	public void refreshAttributeHeader();

	public IRadiusAttribute getAttribute(String attributeID);

	public ArrayList<IRadiusAttribute> getAttributes(String attributeID);

	public IRadiusAttribute getAttribute(int id);
	
	public IRadiusAttribute getAttribute(int ... attributeIds);
	
	/**
	 * DO NOT USE
	 * 
	 * @deprecated It is deprecated as {@link #addAttribute(IRadiusAttribute)} is present. 
	 * Overriding to mark it Deprecated. 
	 */
	@Override
	@Deprecated
	public void addTLVAttribute(IRadiusAttribute attribute);
}
