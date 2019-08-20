package com.elitecore.diameterapi.diameter.translator.operations;

import java.util.List;

import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;

/**
 * This is a Procedure Interface, 
 * where individual Operations can provide 
 * their specific behavior for retrieving Destination Attributes from Packet 
 * and process Destination Attributes in their way
 * 
 * @author monica.lulla
 *
 */
public interface DestinationKeyProcessor<A> {

	public void process();
	public List<A> provideAttributes(NonGroupedKey destinationKey);
	public List<A> provideParentAttributes(NonGroupedKey destinationKey);
}
