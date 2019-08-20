/**
 * 
 */
package com.elitecore.aaa.rm.service.rdr.service;

import java.util.HashMap;

import com.elitecore.aaa.rm.service.rdr.RDRPacketImpl;
import com.elitecore.aaa.rm.service.rdr.tlv.BaseRDRTLV;
import com.elitecore.core.servicex.TCPServiceRequest;

/**
 * @author nitul.kukadia
 *
 */
public interface RDRServiceRequest extends TCPServiceRequest {
	public HashMap<Integer, BaseRDRTLV> getFields();
	
	public String toString();

	public RDRPacketImpl getRequestPacket();
}
