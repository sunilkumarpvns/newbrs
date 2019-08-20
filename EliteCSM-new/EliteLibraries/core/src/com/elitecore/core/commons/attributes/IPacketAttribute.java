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

package com.elitecore.core.commons.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Interface describing an object capabale of addressing attribute
 * of any message packet.
 * 
 */

public interface IPacketAttribute {

    /**
     * Returns id of the attribute.
     * 
     * @return id of this attribute.
     */
    public int getID();

    
    /**
     * Returns id of the attribute in byte sequence.
     * 
     * @return The resultant byte array.
     */
    public byte[] getIDBytes();

   /**
    * Returns the attribute in byte sequences.
    * 
    * @return The resultant byte array.
    */
   public byte[] getBytes();

   /**
    * Returns the header part of attribute in byte sequences.
    * 
    * @return The resultant byte array.
    */
   public byte[] getHeaderBytes();

   /**
    * Returns the length of header part of the attribute.
    * 
    * @return The length of header.
    */
   public int getHeaderLength();

   /**
    * Returns the length of the attribute.
    * 
    * @return The length of the attribute.
    */
   public int getLength();

   /**
    * Returns the value of attribute in byte sequences.
    * 
    * @return The resultant byte array.
    */
   public byte[] getValueBytes();

   /**
    * Returns a String object representing the value of the attribute.
    * 
    * @deprecated instead user @see IPacketAttribute#getStringValue()
    * @return  A String representation of attribute value.
    */
   public String getValueString();

   /**
    * Returns a String object representing the value of the attribute using
    * the named charset.
    * 
    * @return  A String representation of attribute value using charset.
    */
   public String getValueString(String charsetName) throws UnsupportedEncodingException;

   /**
    * Reads the attribute contents from the given stream.
    *
    * @param sourceStream The source stream from where to read the contents.
    * @throws java.io.IOException
    */
   public int readFrom(InputStream sourceStream) throws IOException; 

   /**
    * Changes the representation of the attribute by decoding the specified
    * array of bytes.
    *
    * @param valueBytes The bytes to be decoded to an attribute.
    */
   public void setBytes(byte[] valueBytes);

   /**
    * Changes the representation of the header portion of the attribute by 
    * decoding the specified array of bytes.
    *
    * @param headerBytes The bytes to be decoded to header part of the attribute.
    */
   public void setHeaderBytes(byte[] headerBytes);

   /**
    * Sets the length of the attribute to the given value.
    * 
    * @return The length of the attribute.
    */
   public void setHeaderLength(int headerLength);

   /**
    * Changes the representation of the value part of the attribute by 
    * decoding the specified array of bytes.
    *
    * @param valueBytes The bytes to be decoded to value part of the attribute.
    */
   public void setValueBytes(byte[] valueBytes);

   /**
    * Changes the representation of the value part of the attribute by 
    * decoding the string object. The String will be first converted to
    * bytes using the default charset and then the bytes will be decoded.
    *
    * @param value The String to be decoded to value part of the attribute.
    */
   public void setValueString(String value);

   /**
    * Changes the representation of the value part of the attribute by 
    * decoding the string object. The String will be first converted to
    * bytes using the given charset and then the bytes will be decoded.
    *
    * @param value The String to be decoded to value part of the attribute.
    */
   public void setValueString(String value, String charsetName) throws UnsupportedEncodingException;

   /**
    * Writes the attribute contents to the destination stream.
    *
    * @param destinationStream The stream to which the attribute to be written.
    * @throws java.io.IOException
    */
   public void writeTo(OutputStream destinationStream) throws IOException;

   /**
    * Returns the attribute id in the form of String.
    * 
    * @return Returns the attribute id.
    */
   public String getIDString();
   
   /**
    * Returns the size of the attribute.
    * 
    * @return Returns the size of the attribute.
    */
   public int getSize();
   
	public int getIntValue();
	public void setIntValue(int value);
	public long getLongValue();
	public void setLongValue(long value);
	public void setStringValue(String value);
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException;
	public String getStringValue();
	public String getStringValue(String charsetName) throws UnsupportedEncodingException;
   
}
