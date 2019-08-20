package com.elitecore.client.eap;

import javax.naming.AuthenticationException;

import com.elitecore.client.util.constants.CommunicationStates;
import com.elitecore.core.commons.InitializationFailedException;

/**
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public interface EapMethodAuthenticator {

	public CommunicationStates process(byte[] inTlsData) throws AuthenticationException;
	public byte[] getOutData();
	public void reset() throws InitializationFailedException ;
}
