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

package com.elitecore.core.commons.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Collection;

import com.elitecore.core.commons.attributes.IPacketAttribute;

/**
 * Interface describing an object capabale of addressing packet
 * of any message type.
 */
public interface IPacket extends Serializable{

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
    public Collection<IPacketAttribute> getAttributes();

    /**
     * Returns the attribute that is mapped to the specified id. Null will be
     * returned in case no attribute is mapped to the given id.
     *  
     * @param id The identification value of the attirbute.
     * @return the attribute mapped to the id
     */
     public IPacketAttribute getAttribute(int id);

    /**
     * Returns the attribute that is mapped to the specified id. Null will be
     * returned in case no attribute is mapped to the given id.
     *  
     * @param id The identification value of the attirbute.
     * @return the attribute mapped to the id
     */
    public IPacketAttribute getAttribute(String id);
   
    /**
     * Returns length (size) of the packet.
     * 
     * @return The length of the packet.
     */
    public int getLength();
   
    /**
	 * Reads packet contents from the given char stream.
     *
     * @param sourceStream The source stream from where to read the contents.
     * @throws java.io.IOException
     */
    public int readFrom(Reader sourceReader) throws IOException; 

   
	/**
	 * Writes packet contents to the given char stream.
     *
     * @param destinationWriter The destination stream to write the contents.
     * @throws java.io.IOException
     */
    public void writeTo(Writer destinationWriter) throws IOException; 
   
    public long creationTimeMillis();

}
