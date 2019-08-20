package com.elitecore.netvertex.gateway.file;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.service.offlinernc.conf.OfflineRnCServiceConfiguration;

public class FileGatewayController {

	private List<FileGateway> fileGateways;
	private NetVertexServerContext serverContext;
	private FileGatewayEventListener fileGatewayEventListener;
	
	public FileGatewayController(NetVertexServerContext serverContext, FileGatewayEventListener fileGatewayEventListener ) {
		this.serverContext = serverContext;
		this.fileGatewayEventListener = fileGatewayEventListener;
		fileGateways = new ArrayList<>();
	}

	public void init() {
		 List<FileGatewayConfiguration> listOfFileGatewayConfigrations;
		 OfflineRnCServiceConfiguration offlineRnCServiceConfiguration;
		 BlockingQueue<Runnable> threadTaskQueue;
		
		
		listOfFileGatewayConfigrations = serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getListOfFileGatewayConfiguration();
		offlineRnCServiceConfiguration = serverContext.getServerConfiguration().getNetvertexServerGroupConfiguration().getRunningServerInstance().getOfflineRnCServiceConfiguration();
		
		threadTaskQueue = new LinkedBlockingQueue<>(offlineRnCServiceConfiguration.getFileBatchQueueSize());
		ExecutorService threadPoolExecutor = new ThreadPoolExecutor(offlineRnCServiceConfiguration.getMinThread(),
				offlineRnCServiceConfiguration.getMaxThread(),
				1000,TimeUnit.MILLISECONDS,threadTaskQueue);
		
		ExecutorService intermediateThreadPoolExecutor = new ThreadPoolExecutor(offlineRnCServiceConfiguration.getMinThread(),
				offlineRnCServiceConfiguration.getMaxThread(),
				1000,TimeUnit.MILLISECONDS,threadTaskQueue);
		 
		for(FileGatewayConfiguration fileGatewayConfiguration : listOfFileGatewayConfigrations){
			fileGateways.add(new FileGateway(serverContext, fileGatewayConfiguration, threadPoolExecutor, intermediateThreadPoolExecutor, offlineRnCServiceConfiguration, fileGatewayEventListener));
		}
		
		for (FileGateway fileGateway : fileGateways) {
			try {
				fileGateway.init();
				fileGateway.start();
			} catch (InitializationFailedException | ServiceInitializationException e) {
				LogManager.getLogger().trace(e);
			}
		}
	}

	public void stop() {
		fileGateways.stream().forEach(FileGateway::stop);
	}
	
}
