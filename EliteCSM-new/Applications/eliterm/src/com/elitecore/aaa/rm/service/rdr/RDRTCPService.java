/**
 * 
 */
package com.elitecore.aaa.rm.service.rdr;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import com.elitecore.aaa.core.conf.RMServerConfiguration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.rm.conf.RDRConfiguration;
import com.elitecore.aaa.rm.conf.RdrDetailLocalConfiguration;
import com.elitecore.aaa.rm.data.RDRClientData;
import com.elitecore.aaa.rm.data.RDRClientDataImpl;
import com.elitecore.aaa.rm.service.rdr.service.RDRServiceContext;
import com.elitecore.aaa.rm.service.rdr.service.RDRServiceRequest;
import com.elitecore.aaa.rm.service.rdr.service.Handlers.RDRServiceHandlerImpl;
import com.elitecore.aaa.rm.service.rdr.tlv.BaseRDRTLV;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.packet.IPacket;
import com.elitecore.core.commons.packet.PacketFactory;
import com.elitecore.core.commons.packet.RDRPacketFactory;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.EliteTCPService;
import com.elitecore.core.servicex.IConnectionContext;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceInitializationException;
import com.elitecore.core.servicex.TCPServiceRequest;
import com.elitecore.core.servicex.TCPServiceResponse;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.ratingapi.util.logger.Logger;

/**
 * @author nitul.kukadia
 *
 */	
public class RDRTCPService extends EliteTCPService {
	
	private static final String MODULE = "RDR-TCP-Service";
	private static final String SERVICE_ID = "RDR_SER";
	private RDRConfiguration rdrConfiguration;
	private RDRServiceHandlerImpl rdrServiceHandler;
	private RDRServiceContext rdrServiceContext;
	
	private RdrDetailLocalConfiguration rdrDetailLocalConfg;
	
	StringWriter stringBuffer = new StringWriter();
	PrintWriter out = new PrintWriter(stringBuffer);
	
	public RDRTCPService(AAAServerContext serverContext) {
		super(serverContext);
		rdrConfiguration=((RMServerConfiguration)serverContext.getServerConfiguration()).getRDRConfiguration();
		
		this.rdrDetailLocalConfg= ((RMServerConfiguration)serverContext.getServerConfiguration()).getRdrDetailLocalConfiguration();
	}
	
	protected void initService() throws ServiceInitializationException {
		RDRDictionary.getInstance().initDictionary();
		rdrServiceContext=new RDRServiceContext() {			
			@Override
			public ServerContext getServerContext() {
				return RDRTCPService.this.getServerContext();
			}
			
			@Override
			public RDRConfiguration getRDRConfiguration() {
				return RDRTCPService.this.rdrConfiguration;
			}

			@Override
			public RdrDetailLocalConfiguration getRdrDetailLocalConfiguration() {
				return getRdrDetailLocalConfig();
			}
		};	
		
		super.initService();
		try {
			rdrServiceHandler=new RDRServiceHandlerImpl(rdrServiceContext);
			rdrServiceHandler.init();
		} catch (InitializationFailedException e) {
			e.printStackTrace();
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"RDR handler Initialization failed Reason: " + e.getMessage());
			}
		}catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE,"RDR Error occured in reading dictionary Reason: " + e.getMessage());
			}
		}

		List<RDRClientDataImpl> clientList = rdrConfiguration.getRDRClients();
		RDRClientData client;
		File fileLocation;
		String fileName;
		String ext = "";
		if (clientList != null){
			for (int i=0 ; i<clientList.size() ; i++) {
				client = clientList.get(i);
				try  {
					fileLocation = new File( client.getFileLocation() + File.separator + client.getClientIP());
					fileName = client.getFileName();
					if ((fileName.lastIndexOf('.') + 1) != -1){
						ext = fileName.substring(fileName.lastIndexOf('.') + 1);
					}
					if (fileLocation.isDirectory()){

						File[] files = fileLocation.listFiles();
						changeFileExtension(files, fileLocation, ext);

					} else {
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, "Given file location does not Exists. client: " + client.getClientIP() + " File Location: " + fileLocation);
						}
					}
				} catch (Exception e){
					if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
						LogManager.getLogger().error(MODULE, "Files could not be Renamed for client: " + client.getClientIP() + ". Reason: " + e.getMessage());
					}
				}
			}
		}
	
	}
	private void changeFileExtension(File[] files, File location, String ext){
		String fileName;
		for ( int i=0 ; i<files.length ; i++){
			File file = files[i];
			try{
				if (file.isFile()){
					if (file.getName().endsWith(".inp")){
						fileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + "." + ext ;
						if (file.renameTo(new File (location, fileName))){
							if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
								LogManager.getLogger().info(MODULE, "file renamed to " + file.getName());
							}
						} else {
							if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
								LogManager.getLogger().warn(MODULE, "file could not renamed: " + file.getName());
							}
						}
					}
				}
			}catch (Exception e){
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error Occured renamig file: " + file.getName() + ". Reason: " + e.getMessage());
				}
			}
		} 
	}
	
	protected boolean startService() {
		boolean isStarted = false;
		if (rdrConfiguration == null ) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE,"Configurationg found null. May be configuration file is Missing. Service will not be started");
				Logger.logError(MODULE,"Configurationg found null. May be configuration file is Missing. Service will not be started");
			}
			return false;
		}
		
		isStarted=super.startService();
		return isStarted;
	}
	
	public boolean stopService() {
		rdrServiceHandler.stop();
		return super.stopService();
	}
	
	@Override
	public void handleServiceRequest(TCPServiceRequest request,TCPServiceResponse response) {
		RDRClientData client= this.rdrConfiguration.getRDRClient(request.getSourceAddress());
		if (client == null) {
			response.markForDropRequest();
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE,"Request Received from Invalid client: "+ request.getSourceAddress() + ":"+ request.getSourcePort());
				LogManager.getLogger().error(MODULE,"Request marked for dropped");
			}
		} else {
			System.out.println("known Client");
			rdrServiceHandler.handleRequest(request, response, ISession.NO_SESSION);	
		}		
	}
	
	
	@Override
	public void handleRequest(IPacket packet,IConnectionContext connectionContext) {		
	}

	@Override
	public TCPServiceRequest formServiceSpecificRequest(InetAddress sourceAddress, int sourcePort, byte[] requestBytes) {
		return new RDRServiceRequestImpl(sourceAddress,sourcePort,requestBytes);
	}
	
	private class RDRServiceRequestImpl implements RDRServiceRequest{

		private static final String MODULE = "RDR-Service-Request";
		private RDRPacketImpl rdrRequestPacket;
		private InetAddress sourceAddress;
		private int sourcePort;		
		byte[] fieldBytes;
		
		public RDRServiceRequestImpl(InetAddress sourceAddress, int sourcePort,	byte[] requestBytes) {
			rdrRequestPacket=new RDRPacketImpl();
			rdrRequestPacket.setClientIP(sourceAddress.getHostAddress());
			rdrRequestPacket.setBytes(requestBytes);
			rdrRequestPacket.setClientPort(sourcePort);
			this.sourceAddress=sourceAddress;
			this.sourcePort=sourcePort;
			this.fieldBytes=new byte[requestBytes.length-20];
			System.arraycopy(requestBytes, 20, this.fieldBytes, 0, requestBytes.length-20);
			//System.out.println("RDR : "+rdrRequestPacket.toString());
			LogManager.getLogger().info(MODULE, rdrRequestPacket.toString());
		}
		
		@Override
		public void setParameter(String key, Object parameterValue) {
		}

		@Override
		public Object getParameter(String str) {
			return null;
		}

		@Override
		public long getRequestReceivedNano() {
			return 0;
		}

		@Override
		public InetAddress getSourceAddress() {
			return sourceAddress;
		}

		@Override
		public int getSourcePort() {
			return sourcePort;
		}

		@Override
		public byte[] getRequestBytes() {
			return fieldBytes;
		}
		public String toString(){
			return (rdrRequestPacket.toString());
		}

		@Override
		public HashMap<Integer, BaseRDRTLV> getFields() {
			return rdrRequestPacket.getField();
		}
		@Override
		public RDRPacketImpl getRequestPacket(){
			return rdrRequestPacket;
		}
	}
	
	@Override
	public TCPServiceResponse formServiceSpecificResposne(
			TCPServiceRequest serviceRequest) {
		return null;
	}
	
	@Override
	protected ServiceContext getServiceContext() {
		return null;
	}
	@Override
	public void readConfiguration() throws LoadConfigurationException {
	}
	@Override
	public String getKey() {
		return "RDR-Servie";
	}
	@Override
	public SocketDetail getSocketDetail() {
		return rdrConfiguration.getSocketDetail();
	}
	@Override
	public String getServiceIdentifier() {
		return SERVICE_ID;
	}
	
	@Override
	protected int getMinThreadPoolSize() {		
		return rdrConfiguration.minThreadPoolSize();
	}
	@Override
	protected int getMaxThreadPoolSize() {		
		return rdrConfiguration.maxThreadPoolSize();
	}

	@Override
	protected int getMainThreadPriority() {		
		return rdrConfiguration.mainThreadPriority();
	}

	@Override
	protected int getWorkerThreadPriority() {		
		return rdrConfiguration.workerThreadPriority();
	}

	@Override
	protected int getThreadKeepAliveTime() {		
		return (60*60*1000);
	}
	
	@Override
	protected int getMaxRequestQueueSize() {		
		return rdrConfiguration.maxRequestQueueSize();
	}

	@Override
	protected int getSocketReceiveBufferSize() {
		return rdrConfiguration.socketReceiveBufferSize();
	}
	@Override
	protected int getSocketSendBufferSize() {
		return rdrConfiguration.socketSendBufferSize();
	}
	@Override
	public String getThreadIdentifier() {
		return "Thread ID";
	}
	@Override
	public String getName() {
		return "RDR Services";
	}
	@Override
	public IPacket getPacket() {
		return new RDRPacketImpl();
	}
	public PacketFactory getPacketFactory(){
		return new RDRPacketFactory();
	}
	public RdrDetailLocalConfiguration getRdrDetailLocalConfig(){
		return this.rdrDetailLocalConfg;
	}

	@Override
	public String getServiceName() {
		return "RDR TCP Service";
		
	}
}
