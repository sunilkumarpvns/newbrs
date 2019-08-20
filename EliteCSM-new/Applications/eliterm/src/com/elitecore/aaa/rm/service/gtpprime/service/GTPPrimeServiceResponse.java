
/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.service;

import java.util.List;

import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientData;
import com.elitecore.core.servicex.UDPServiceResponse;
import com.elitecore.coregtp.commons.elements.BaseGTPPrimeElement;

/**
 * @author dhaval.jobanputra
 *
 */
public interface GTPPrimeServiceResponse extends UDPServiceResponse {

	void setMessageType(int i);
	void setPayloadLength(int i);
	void setSeqNumber(int seqNumber);
	void setVersion(byte version);
	void setProtocolType(byte protocolType);
	void setSparebits(byte spareBits);
	void setHeaderType(byte headerType);
	void setElementList(List<BaseGTPPrimeElement> toSendIEList);
	void setClientData(GTPPrimeClientData client);
	GTPPrimeClientData getClientData();
	int getMessageType();
	void setFailure();
	boolean isFailure();

}