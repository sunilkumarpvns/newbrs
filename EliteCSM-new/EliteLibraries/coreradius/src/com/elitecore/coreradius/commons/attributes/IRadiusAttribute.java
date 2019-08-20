package com.elitecore.coreradius.commons.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.elitecore.commons.base.Function;


public interface IRadiusAttribute extends Cloneable,Serializable {
	
	public static final Function<IRadiusAttribute, String> STRING_VALUE_FUNCTION = new Function<IRadiusAttribute, String>() {

		@Override
		public String apply(IRadiusAttribute input) {
			return input.getStringValue();
		}
	};
	
	public int getType();
	public void setType(int type);
	
	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException ;
	public Object clone() throws CloneNotSupportedException;
	
	
	
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
    * Changes the representation of the value part of the attribute by 
    * decoding the specified array of bytes.
    *
    * @param valueBytes The bytes to be decoded to value part of the attribute.
    */
   public void setValueBytes(byte[] valueBytes);

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
   
    public long getVendorID();

	public void setVendorID(long vendorID) ;
   
   
	public int getIntValue();
	public void setIntValue(int value);
	public long getLongValue();
	public void setLongValue(long value);
	public void setStringValue(String value);
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException;
	public String getStringValue();
	public String getStringValue(String charsetName) throws UnsupportedEncodingException;
	
	/**
	 * Here if useDictionary boolean parameter 
	 * is true , then it will try to find String equivalent 
	 * value of attribute in dictionary and return the same.
	 * 
	 * @param bUseDictionary
	 * @return
	 * 
	 */
	public  String getStringValue(boolean bUseDictionary) ;

	/**
	 * This method will return the String value
	 * of the attribute based on vendorID.
	 * If vendorID is 0 then it will consider 
	 * standard radius attribute.
	 * 
	 * @param vendorID
	 * @param bUseDictionary
	 * @return
	 */
	public String getStringValue(long vendorID ,boolean bUseDictionary);
	
	/**
	 * This method will add the new value to the 
	 *  existing attribute value .
	 * @param value
	 */
	public void doPlus(String value);	
	public boolean patternCompare(String patternString);
	public boolean stringCompare(String value);
	public int getTag();
	public void setTag(int tag);
	public String getKeyValue(String key);
	public boolean isAvpair();
	public boolean isIgnoreCase();
	public int getEncryptStandard();
	public void setEncryptStandard(int encryptStandard);
	public boolean hasTag();
	public void setValue(byte[] value, int salt, String secret, byte[] authenticator);
	public byte[] getValue(String secret, byte[] authenticator);
	public String getParentId();
	public int getLevel();
	public void reencryptValue(String oldSecret, byte[] oldAuthenticator, String newSecret, byte[] newAuthenticator);
	public boolean isVendorSpecific();
	
	/**
	 * Returns decrypted value of the attribute as string if attribute is encrypted, 
	 * else behaves same as {@link #getStringValue()}
	 * 
	 * @param sharedSecret a non-null shared secret
	 * @param authenticator a non-null request authenticator
	 */
	public String getStringValue(String sharedSecret, byte[] authenticator);
	
	/**
	 * This will set encrypted value in attribute if attribute has encryption standard
	 * @param value plain text value 
	 * @param sharedSecret a non-null shared secret using which value will be encrypted
	 * @param newAuthenticator a non-null packet authenticator
	 */
	public void setStringValue(String value, String sharedSecret, byte[] newAuthenticator);
}
