package com.elitecore.core.systemx.esix.udp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.ReInitializable;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;

public interface UDPCommunicatorManager extends ReInitializable{
	
	/**
	 * Returns the UDP Communicator that is associated with id specified, or null if
	 * no communicator is found associated with the id passed
	 * 
	 * @param UUID - External System id
	 * @return UDP Communicator instance to communicate with external system
	 */
	@Nullable UDPCommunicator findCommunicatorByID(String UUID);
	
	
	/**
	 * Returns the UDP communicator that is associated with id specified if found.
	 * If UDP communicator is not found for the specified id then it will create
	 * a new UDP communicator for the provided external system
	 * 
	 * @param UUID - external system id
	 * @param serverContext - the context of the server
	 * @param udpES - external system data to create UDP communicator
	 * @return UDP Communicator instance to communicate with external system
	 * @throws InitializationFailedException - If failed to create UDP communicator 
	 * or udpES is null
	 */
	@Nonnull UDPCommunicator findCommunicatorByIDOrCreate(String UUID, ServerContext serverContext,
			@Nonnull UDPExternalSystemData udpES) throws InitializationFailedException;
	
	/**
	 * Returns the UDP Communicator that is associated with url data provided or null if
	 * no communicator is associated with the url passed
	 * 
	 * @param url - an instance of {@link URLData} for which you required UDP communicator
	 * @return UDP Communicator instance to communicate with external system
	 */
	@Nullable UDPCommunicator findCommunicatorByURL(@Nonnull URLData url);
	
	/**
	 * Returns the UDP Communicator that is associated with url string or null if no
	 * communicator is associated with the url passed
	 * 
	 * @param url - a string in url format
	 * @return UDP Communicator instance to communicate with external system
	 * @throws InvalidURLException - If the format of the url string specified is not valid
	 */
	@Nullable UDPCommunicator findCommunicatorByURL(@Nonnull String url) throws InvalidURLException;
	
	/**
	 * Returns the UDP Communicator that is associated with url data provided. 
	 * If the required UDP communicator is not found then a new UDP communicator
	 * is created.
	 * 
	 * @param url - an instance of {@link URLData} for which you required UDP communicator
	 * @param serverContext - The context of the server
	 * @param udpES - external system data to create UDP communicator
	 * @return UDP Communicator instance to communicate with external system
	 * @throws InitializationFailedException - If failed to create UDP communicator or udpES is null
	 */
	@Nonnull UDPCommunicator findCommunicatorByURLOrCreate(@Nonnull URLData url,
			@Nonnull ServerContext serverContext,
			@Nonnull UDPExternalSystemData udpES) throws InitializationFailedException;
	
	/**
	 * Returns the UDP Communicator that is associated with url as a string specified.
	 * If the required UDP communicator is not found then a new UDP communicator is created.
	 * 
	 * @param url - a string in url format
	 * @param serverContext - The context of the server
	 * @param udpES - external system data to create UDP communicator
	 * @return UDP Communicator instance to communicate with external system
	 * @throws InvalidURLException - If the format of the url string specified is not valid
	 * @throws InitializationFailedException - If failed to create UDP communicator
	 */
	@Nonnull UDPCommunicator findCommunicatorByURLOrCreate(@Nonnull String url, 
			@Nonnull ServerContext serverContext,
			@Nonnull UDPExternalSystemData udpES)
	throws InvalidURLException, InitializationFailedException;
	
	public void shutdown();
	
	public boolean isShutdown();
}
