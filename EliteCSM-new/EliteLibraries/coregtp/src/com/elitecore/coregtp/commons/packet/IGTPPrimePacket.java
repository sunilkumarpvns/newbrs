/**
 * 
 */
package com.elitecore.coregtp.commons.packet;

import java.io.IOException;


/**
 * @author dhaval.jobanputra
 *
 */
public interface IGTPPrimePacket {

	public byte[] getBytes();
	public void setBytes(byte[] bData) throws ParseException, IOException;
}
