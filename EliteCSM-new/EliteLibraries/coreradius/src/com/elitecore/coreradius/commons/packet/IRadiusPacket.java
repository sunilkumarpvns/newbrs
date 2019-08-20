/*
 *  Server Framework
 *
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *
 *  Created on 10th April 2006
 *  Author : Ezhava Baiju D
 */

package com.elitecore.coreradius.commons.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

/**
 * Interface describing an object capabale of addressing packet
 * of any message type.
 */
public interface IRadiusPacket extends Serializable{

	/**
	 * Reads packet contents from the given byte stream.
     *
     * @param sourceStream The source stream from where to read the contents.
     * @throws java.io.IOException
     */
    public int readFrom(InputStream sourceStream) throws IOException;

	/**
	 * Writes packet contents to the given byte stream.
     *
     * @param destinationStream The destination stream to write the contents.
     * @throws java.io.IOException
     */
    public void writeTo(OutputStream destinationStream) throws IOException;

    /**
     * Returns the packet in byte sequences.
     *
     * @return The resultant byte array.
     */
    public byte[] getBytes();

    /**
     * Returns the packet in byte sequences including info attributes.
     *
     * @return The resultant byte array.
     */
    public byte[] getBytes(boolean bIncludeInfoAttr);

    /**
     * Changes the representation of the packet by decoding the specified
     * array of bytes.
     *
     * @param valueBytes The bytes to be decoded to an attribute.
     */
    public void setBytes(byte[] value);

    /**
     * Returns the list of attributes of the packet.
     *
     * @return Returns list of attributes.
     */
    public ArrayList<IRadiusAttribute> getRadiusAttributes();

    /**
     * Returns the list of attributes of the packet.
     * @param bIncludeInfoAttr : it will includes all the info attributes into resultant Collection.
     * @return Returns list of attributes.
     */
    public ArrayList<IRadiusAttribute> getRadiusAttributes(boolean bIncludeInfoAttr);

    /**
     * Returns the attribute that is mapped to the specified id. Null will be
     * returned in case no attribute is mapped to the given id.
     *
     * @param id The identification value of the attirbute.
     * @return the attribute mapped to the id
     */
     public IRadiusAttribute getRadiusAttribute(int id);

     /**
      * Returns the attribute that is mapped to the specified id. Null will be
      * returned in case no attribute is mapped to the given id.
      *
      * @param id The identification value of the attirbute.
      * @param bIncludeInfoAttr : if <code>id</code> is not present in list , function will search into info attribute list
      * @return the attribute mapped to the id
      */
      public IRadiusAttribute getRadiusAttribute(int id, boolean bIncludeInfoAttr);

     /**
 	 * provide vendorid:vendorparameterid in <code>id</code>
 	 * return collection of vendor specific attributes with 
 	 * vendorparameterid for specified vendorid
 	 * <code>id</code> 
 	 * @param id  id will contain string id in 
 	 * form of  vendorid:vendorparameterid
 	 * example for Resource-Manager AVPair of vendor 
 	 * Elitecore can be represented as 21067:5
 	 * @return 
 	 */
     public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(String id) throws NumberFormatException;

     /**
  	 * provide vendorid:vendorparameterid in <code>id</code>
  	 * return collection of vendor specific attributes with 
  	 * vendorparameterid for specified vendorid
  	 * <code>id</code> 
  	 * @param id  id will contain string id in 
  	 * form of  vendorid:vendorparameterid
  	 * example for Resource-Manager AVPair of vendor 
  	 * Elitecore can be represented as 21067:5
  	 * @param bIncludeInfoAttr : it will includes all the info attributes into resultant Collection.
  	 * @return 
  	 */
      public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(String id, boolean bIncludeInfoAttr) throws NumberFormatException;

     /**
 	 * return collection of vendor specific attributes for  
 	 * vendorparameterid of specified vendorid
 	 * 
 	 * @param strVendorID strVendorID should be vendorId String 
 	 * like "21067"
 	 * @param strAttributeID strAttributeID should be Attribute Id String 
 	 * like "5"
 	 * @return
 	 */
 	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(String strVendorID, String strAttributeID) throws NumberFormatException;

    /**
 	 * return collection of vendor specific attributes for  
 	 * vendorparameterid of specified vendorid
 	 * 
 	 * @param strVendorID strVendorID should be vendorId String 
 	 * like "21067"
 	 * @param strAttributeID strAttributeID should be Attribute Id String 
 	 * like "5"
 	 * @param bIncludeInfoAttr : it will includes all the info attributes into resultant Collection.
 	 * @return
 	 */
 	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(String strVendorID, String strAttributeID, boolean bIncludeInfoAttr) throws NumberFormatException;

 	/**
	 * return collection of vendor specific attributes for  
	 * vendorparameterid of specified vendorid
	 * 
	 * @param vendorID 
	 * @param attributeID
	 * @return
	 */
	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(long vendorID, int attributeID);

 	/**
	 * return collection of vendor specific attributes for  
	 * vendorparameterid of specified vendorid
	 * 
	 * @param vendorID 
	 * @param attributeID
	 * @param bIncludeInfoAttr : it will includes all the info attributes into resultant Collection.
	 * @return
	 */
	public ArrayList<IRadiusAttribute> getVendorSpeficAttributes(long vendorID, int attributeID, boolean bIncludeInfoAttr);

    /**
     * Returns the attribute that is mapped to the specified id. Null will be
     * returned in case no attribute is mapped to the given id.
     *
     * @param id The identification value of the attirbute.
     * @return the attribute mapped to the id
     */
    public IRadiusAttribute getRadiusAttribute(String id);

    /**
     * Returns the attribute that is mapped to the specified id. Null will be
     * returned in case no attribute is mapped to the given id.
     *
     * @param id The identification value of the attirbute.
     * @param bIncludeInfoAttr : if <code>id</code> is not present in list , function will search into info attribute list
     * @return the attribute mapped to the id
     */
    public IRadiusAttribute getRadiusAttribute(String id, boolean bIncludeInfoAttr);

    public void addAttribute(IRadiusAttribute attribute);
    
    public void addAttribute(IRadiusAttribute attribute, long vendorId);
    
    public void addInfoAttribute(IRadiusAttribute attribute);
    
    public void addInfoAttribute(IRadiusAttribute attribute, long vendorId);
    
    public void refreshPacketHeader();
    
    public void refreshInfoPacketHeader();

    public ArrayList<IRadiusAttribute> getRadiusAttributes(int id) ;
    
    public ArrayList<IRadiusAttribute> getRadiusAttributes(int id, boolean bIncludeInfoAttr) ;

    public ArrayList<IRadiusAttribute> getRadiusAttributes(String id);
    
    public ArrayList<IRadiusAttribute> getRadiusAttributes(String id, boolean bIncludeInfoAttr);

    /**
     * Returns length (size) of the packet.
     *
     * @return The length of the packet.
     */
    public int getLength();

    public String getClientIP();

	public int getClientPort();
	
	public int getPacketType();
	
	public int getIdentifier();
	
	public byte[] getAuthenticator();

	public byte[] getAttributeBytes();

	public long creationTimeMillis();
	public IRadiusAttribute getVendorSpecificAttribute(long vendorID, int ... attributeIds);
	public IRadiusAttribute getVendorSpecificAttribute(long vendorID, boolean bIncludeInfoAttr, int ... attributeIds);
	public ArrayList<IRadiusAttribute> getVendorSpecificAttributes(long vendorID, int ... attributeIds);
	public ArrayList<IRadiusAttribute> getVendorSpecificAttributes(long vendorID, boolean bIncludeInfoAttr, int ... attributeIds);
	
	public IRadiusAttribute getRadiusAttribute(long vendorId, int ...attrId);
	public IRadiusAttribute getRadiusAttribute(long vendorId, boolean bIncludeInfoAttr, int ...attrId);
	public ArrayList<IRadiusAttribute> getRadiusAttributes(long vendorId, int ...attrIds );
	public ArrayList<IRadiusAttribute> getRadiusAttributes(long vendorId, boolean bIncludeInfoAttr, int ...attrIds );
	
	public void removeAttribute(IRadiusAttribute attribute);
}
