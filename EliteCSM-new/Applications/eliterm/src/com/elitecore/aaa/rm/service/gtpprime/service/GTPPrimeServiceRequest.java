
/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.service;

import java.util.List;

import com.elitecore.coregtp.commons.elements.BaseGTPPrimeElement;
import com.elitecore.core.servicex.UDPServiceRequest;

/**
 * @author dhaval.jobanputra
 *
 */
public interface GTPPrimeServiceRequest extends UDPServiceRequest {

	public byte getVersion();

	public byte getProtocolType();

	public byte getSpareBits();

	public byte getHeaderType();

	public int getSeqNumber();

	public int getMessageType();

	public String toString();

	public List<BaseGTPPrimeElement> getElementList();
	
	public boolean isMalformed();

}
