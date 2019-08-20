package com.elitecore.core.systemx.esix.udp.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.core.systemx.esix.statistics.PermanentFailureStatistics;
import com.elitecore.core.systemx.esix.udp.UDPCommunicator;
import com.elitecore.core.systemx.esix.udp.UDPCommunicatorManager;
import com.elitecore.core.systemx.esix.udp.UDPExternalSystemData;
import com.elitecore.core.util.cli.cmd.BaseESIScanCommand;
import com.elitecore.core.util.cli.cmd.BaseESIScanCommand.ESIStatus;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;

public abstract class UDPCommunicatorManagerImpl implements UDPCommunicatorManager {
	
	/* ------- Entries for statistics of all UDP Communicators  ------- */
	private static final Map<String,ESIStatistics> udpStatisticsByName;
	private static final String MODULE = "UDP-COMM-MGR";
	
	static{
		udpStatisticsByName = new ConcurrentHashMap<String, ESIStatistics>();
	}
	
	
	Map<String,UDPCommunicator> communicatorIdToCommunicator;
	Map<URLData, UDPCommunicator> urlToCommunicator;
	private boolean isShutdown = false;
	
	public UDPCommunicatorManagerImpl() {
		communicatorIdToCommunicator = new ConcurrentHashMap<String, UDPCommunicator>();
		urlToCommunicator = new ConcurrentHashMap<URLData, UDPCommunicator>();
	}
	
	public static Collection<ESIStatistics> getAllUDPCommunicatorStatistics(){
		return Collections.unmodifiableCollection(udpStatisticsByName.values());
	}
	
	public static ESIStatistics getUDPCommunicatorStatisticsByName(String udpCommunicatorName){
		return udpStatisticsByName.get(udpCommunicatorName);
	}
	
	@Override
	public UDPCommunicator findCommunicatorByID(String UUID) {
		return communicatorIdToCommunicator.get(UUID);
	}
	
	@Override
	public @Nonnull UDPCommunicator findCommunicatorByIDOrCreate(String UUID,
			ServerContext serverContext, UDPExternalSystemData udpES)
			throws InitializationFailedException {
		
		UDPCommunicator communicator = findCommunicatorByID(UUID);
		if (communicator == null) {
			synchronized (this) {
				communicator = findCommunicatorByID(UUID);
				if (communicator == null) {
					communicator = initUDPExternalSystem(serverContext, udpES);
				}
			}
		}
		
		return communicator;
	}
	
	@Override
	public UDPCommunicator findCommunicatorByURL(String url) throws InvalidURLException {
		URLData urlData = URLParser.parse(url);
		return urlToCommunicator.get(urlData);
	}
	
	@Override
	public UDPCommunicator findCommunicatorByURL(URLData url) {
		return urlToCommunicator.get(url);
	}
	
	@Override
	public @Nonnull UDPCommunicator findCommunicatorByURLOrCreate(String url, ServerContext serverContext, UDPExternalSystemData udpES) throws InvalidURLException, InitializationFailedException {
		URLData urlData = URLParser.parse(url);
		return findCommunicatorByURLOrCreate(urlData, serverContext, udpES);
	}
	
	@Override
	public @Nonnull UDPCommunicator findCommunicatorByURLOrCreate(URLData url, ServerContext serverContext, UDPExternalSystemData udpES) throws InitializationFailedException {
		UDPCommunicator communicator = findCommunicatorByURL(url);
		if (communicator == null) {
			synchronized (this) {
				communicator = findCommunicatorByURL(url);
				if (communicator == null) {
					communicator = initUDPExternalSystem(serverContext, udpES);
				}
			}
		}
		
		return communicator;
	}

	@GuardedBy("this")
	private UDPCommunicator initUDPExternalSystem(ServerContext serverContext, UDPExternalSystemData udpES)
			throws InitializationFailedException {
		if (udpES == null) {
			throw new InitializationFailedException("Configuration not found for UDPExternal system");
		}

		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Creating communicator with name: " + udpES.getName());
		}
		
		UDPCommunicator udpCommunicator = createUDPCommunicator(serverContext, udpES);
		try {
			udpCommunicator.init();

			BaseESIScanCommand.registerESI(new UDPESIStatus(udpCommunicator));

			//registering for statistics if initialized successfully
			udpStatisticsByName.put(udpCommunicator.getStatistics().getName(),
					udpCommunicator.getStatistics());

			udpCommunicator.addESIEventListener(getESIEventListener(serverContext));
			communicatorIdToCommunicator.put(udpES.getUUID(), udpCommunicator);
			urlToCommunicator.put(udpES.getURL(), udpCommunicator);
		} catch (InitializationFailedException ex) {
			BaseESIScanCommand.registerESI(new PermanentFailureStatus(udpES.getName()));
			udpStatisticsByName.put(udpES.getName(), 
					new PermanentFailureStatistics(udpES.getName(),
					UDPCommunicator.COMMAND_KEY));
			throw ex;
		}
		
		return udpCommunicator;
	}

	@Override
	public void reInit() throws InitializationFailedException {
		//re-initializing all the successfully initialized communicators
		for(UDPCommunicator udpCommunicator : communicatorIdToCommunicator.values()){
			try{
				udpCommunicator.reInit();
			}catch (Exception e) {
				//FIXME do appropriate logging
			}
		}	
	}
	
	public void shutdown() {
		Collection <UDPCommunicator> commList =  communicatorIdToCommunicator.values();
		Iterator<UDPCommunicator> iterator = commList.iterator();
		while(iterator.hasNext()) {
			UDPCommunicator udpCommunicator = iterator.next();
			udpCommunicator.shutdown();
		}
		this.isShutdown = true;
	}
	
	public boolean isShutdown() {
		return isShutdown;
	}
	
	protected class UDPESIStatus implements BaseESIScanCommand.ESIStatus{
		UDPCommunicator udpCommunicator;
		
		public UDPESIStatus(UDPCommunicator udpCommunicator) {
			this.udpCommunicator = udpCommunicator;
		}
		
		@Override
		public String getESStatus() {
			udpCommunicator.scan();
			if (udpCommunicator.isAlive()) {
				return BaseESIScanCommand.ESIStatus.ALIVE;
			} else {
				return BaseESIScanCommand.ESIStatus.DEAD;
			}
		}

		@Override
		public String getESName() {
			return udpCommunicator.getCommunicatorContext().getExternalSystem().getName() 
				+ "(" + udpCommunicator.getCommunicatorContext().getIPAddress()
				+ ":" + udpCommunicator.getCommunicatorContext().getPort() + ")";
		}

		@Override
		public String getConfiguredESType() {
			return UDPCommunicator.COMMAND_KEY;
		}
	}
	
	private static class PermanentFailureStatus implements ESIStatus {
		private final String udpCommunicatorName;

		public PermanentFailureStatus(String udpCommunicatorName) {
			this.udpCommunicatorName = udpCommunicatorName;
		}
		
		@Override
		public String getESStatus() {
			return BaseESIScanCommand.ESIStatus.FAIL;
		}

		@Override
		public String getESName() {
			return udpCommunicatorName;
		}

		@Override
		public String getConfiguredESType() {
			return UDPCommunicator.COMMAND_KEY;
		}
		
	}
	
	protected abstract UDPCommunicator createUDPCommunicator(ServerContext serverContext, UDPExternalSystemData udpES);
	protected abstract ESIEventListener<ESCommunicator> getESIEventListener(ServerContext serverContext);
}
