package com.elitecore.diameterapi.diameter.translator.delegator;

import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.exprlib.parser.expression.ValueProvider;

/**
 * Packet Delegator is an Interface for correlating two different Packet data structures
 * where Packet data structure implementations are
 * having similar (not exactly same) functionality, 
 * but data structure implementations are different.
 * 
 * Packet Structure is formed as:
 * 
 * <pre>
 * Packet(P) =
 *      Attribute(A) = value
 *      Attribute(A) = value
 *      Group-Attribute(G) =
 *          Attribute(A) = value
 *          Group-Attribute(G) =
 *              Attribute(A) = value
 *  
 * </pre>
 * 
 * Here, Packet consists of list of Attributes. 
 * Each attribute is identified by attribute-Id. 
 * Attributes are of two types:
 * <br />
 * 1. Simple Attribute, represented as Attribute[A].
 * <br />
 * 
 * 2. Grouped Attribute, represented as Group-Attribute[G]. 
 * Group-Attribute is itself an Attribute
 * and a nested data structure containing, list of Attributes.
 * <br />
 * 
 * 3. Key-Valued Attribute, this is optional implementation. 
 * Key-Valued Attribute is itself an Attribute. 
 * The value contained in Attribute is in Key-Value form. 
 * <br />
 * 
 * As Group-Attribute is a nested Data-Structure, 
 * inner(child) attribute can be directly accessible by composite attribute-Id.
 * 
 * For example,
 * 
 * Packet contains a Group-Attribute with id 'x', contains sub-attribute with id 'y',
 * <pre> 
 * P = 
 *   G[x] =
 *   	A[y] = value
 * 	</pre>
 * Attribute with id 'y' can be directly accessible from Packet with id 'x.y', 
 * where . is Application-Id Separator (may vary as per implementation).
 * 
 * @author monica.lulla
 *
 * @param <P> is interface/implementation representing Packet
 * @param <A> is interface/implementation representing Attribute
 * @param <G> is interface/implementation representing Group-Attribute
 */
public interface PacketDelegator <P, A, G extends A> {

	/**
	 * Add list of Attributes to Packet
	 * 
	 * @param packet to which attributes will be added
	 * @param attributes list of attributes
	 */
	public void addAttributesToPacket(P packet, List<A> attributes);

	/**
	 * Add Attribute to Packet
	 * 
	 * @param packet to which attributes will be added
	 * @param attribute
	 */
	public void addAttributeToPacket(P packet, A attribute);

	/**
	 * Create an Attribute with some Attribute Id. 
	 * If the attribute is some inner(child) attribute 
	 * and attribute structure is such that 
	 * inner(child) attribute is dependent on outer(parent) attribute, 
	 * provide outer(parent) Attribute.  
	 * 
	 * @param attributeId of attribute
	 * @param parentAttribute provide if attribute creation is dependent upon parent. 
	 * 
	 * @return instance of attribute with given attribute-id
	 */
	public A createAttribute(String attributeId, @Nullable A parentAttribute);

	/**
	 * 
	 * @param attribute
	 * @return true if attribute is of Type Group-Attribute[G]
	 */
	public boolean isAttributeGrouped(A attribute);

	/**
	 * Add list of sub attributes to group attribute 
	 * 
	 * @param groupAttribute
	 * @param subAttributes
	 */
	public void addSubAttributesToGroupedAttribute(G groupAttribute, List<A> subAttributes);

	/**
	 * Add a sub attribute to group attribute 
	 * 
	 * @param groupAttribute
	 * @param subAttribute
	 */
	public void addSubAttributeToGroupedAttribute(G groupAttribute, A subAttribute);

	/**
	 * Set String value to attribute
	 * 
	 * @param attribute
	 * @param value
	 */
	public void setStringValue(A attribute, String value);

	/**
	 * @param attribute
	 * @return String value of an Attribute
	 */
	public String getStringValue(A attribute);

	/**
	 * 
	 * @param attributes
	 * @return long value of an Attribute
	 */
	public long getLongValue(A attributes);

	/**
	 * @param attribute
	 * @return attribute-Id of attribute
	 */
	public String getAttributeID(A attribute);

	/**
	 * Get List of Attribute from Packet based on attribute-Id
	 * @param packet
	 * @param attributeID
	 * @return list of attributes from packet having provided attributeID
	 */
	public List<A> getAttributeList(P packet, String attributeID);

	/**
	 * Get Attribute from Packet based on attribute-Id. 
	 * If there are multiple attributes of same attribute-Id, 
	 * one of those will be fetched.
	 * Attribute selected is based on implementation.
	 * 
	 * @param packet
	 * @param attributeId
	 * @return attribute from packet having provided attributeID
	 */
	public A getAttribute(P packet, String attributeId);

	/**
	 * Get List of sub-Attribute (inner or child attribute) from Group-Attribute based on attribute-Id
	 * 
	 * @param groupedAttribute
	 * @param attributeId
	 * @return list of sub-attributes
	 */
	public List<A> getSubAttributesList(G groupedAttribute, String attributeId);

	/**
	 * Get Sub-Attribute from Group-Attribute based on attribute-Id. 
	 * If there are multiple attributes of same attribute-Id, 
	 * one of those will be fetched.
	 * Attribute selected is based on implementation.
	 * 
	 * @param groupedAttribute
	 * @param attributeId
	 * 
	 * @return attribute with provided attribute-Id
	 */
	public A getSubAttribute(G groupedAttribute, String attributeId);

	/**
	 * @param groupedAttribute
	 * @return list of all the Sub-Attributes contained in group-attribute.
	 */
	public List<A> getSubAttributesList(G groupedAttribute);

	/**
	 * Attribute-Id can be both simple or composite. 
	 * This will split composite attribute-Id and 
	 * return array of attribute-Ids.
	 *   
	 * For example,
	 * 
	 * Packet contains a Group-Attribute with id 'x', contains sub-attribute with id 'y',
	 * <pre> 
	 * P = 
	 *   G[x] =
	 *   	A[y] = value
	 * 	</pre>
	 * if attributeId = 'x.y', it will return [x, y]
	 * if attributeId = 'x', it will return [x]
	 * @param attributeId
	 * 
	 * @return array of attribute-IDs
	 */
	public String[] splitAttributeIds(String attributeId);

	/**
	 * 
	 * @return true if Key-Valued Attribute type is supported by implementation 
	 */
	public boolean isKeyValueSupported();

	/**
	 * 
	 * @param attribute 
	 * @param key
	 * @return value w.r.t key from attribute
	 */
	public String getKeyValue(A attribute, String key);

	/**
	 * 
	 * @param packet
	 * @return ValueProvider for Packet
	 * 
	 * @see ValueProvider
	 */
	public ValueProvider getValueProvider(P packet);

	/**
	 * Removes attribute from packet
	 * 
	 * @param packet
	 * @param attribute
	 */
	public void remove(P packet, A attribute);

	/**
	 * Clones an attribute. 
	 * 
	 * @param attribute
	 * @return cloned attribute
	 * 
	 * @throws CloneNotSupportedException if attribute in implementation does not implement {@link Cloneable}
	 */
	public A cloneAttribute(A attribute) throws CloneNotSupportedException;

	/**
	 * 
	 * @param attribute
	 * @return true if attribute has a value.
	 */
	public boolean hasValue(A attribute);

}
