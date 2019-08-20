package com.elitecore.coreradius.commons.attributes;

import java.util.Collection;

public interface IRadiusGroupedAttribute extends IRadiusAttribute {

	public void addTLVAttribute(IRadiusAttribute attribute); 
	public IRadiusAttribute getSubAttribute(int ... attributeIds);
	public Collection<IRadiusAttribute> getSubAttributes(int ... attributeIds);
	public Collection<IRadiusAttribute> getAttributes();
	
	/**
	 * Adds provided Attributes to the Group Attribute.
	 * 
	 * @param subAttributes list of attributes to add
	 */
	public void addSubAttributes(IRadiusAttribute... subAttributes);
	
}
