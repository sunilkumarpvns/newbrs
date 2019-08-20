package com.elitecore.aaa.core.server.axixserver;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.axis.configuration.FileProvider;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.tls.CRLConfiguration;
import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.core.commons.tls.EliteSSLContextFactory;
import com.elitecore.core.commons.tls.EliteSSLParameter;
import com.elitecore.core.commons.tls.ServerCertificateProfile;
import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.core.commons.tls.TrustedCAConfiguration;
import com.elitecore.core.commons.tls.cipher.CipherSuites;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

public class HTTPSEliteWebServiceServer extends EliteWebServiceServer{

	private String serverCertificateId;

	public HTTPSEliteWebServiceServer(ServerContext ctx,String serviceIPAddress,int servicePort,int threadPoolSize,int maxSession, String serverCertificateId) {
		super(ctx,serviceIPAddress,servicePort,threadPoolSize,maxSession);
		this.serverCertificateId = serverCertificateId;
	}
	
	protected boolean startService() {
		try {
			
			boundAddress = new InetSocketAddress(getSocketDetail().getIPAddress(), getSocketDetail().getPort());
			
			server = HttpsServer.create(boundAddress, 0);
			server.createContext("/", new RequestHandler());

			server.setExecutor(synchronousTaskExecutor);
			
			SSLContext sslContext = generateSSLContext();
			
			((HttpsServer)server).setHttpsConfigurator(new HttpsConfigurator(sslContext){
				@Override
				public void configure(HttpsParameters arg0) {
					arg0.setSSLParameters(getSSLContext().getSupportedSSLParameters());
				}
			});

			server.start();
			
			setStartDate(new Date(TimeSource.systemTimeSource().currentTimeInMillis()));

			serverContext.getTaskScheduler().scheduleIntervalBasedTask(new WebServiceSessionCleanupTask()); 
			
			// Hard coding web service file name and file directory for the time being 
           	this.myConfig = new FileProvider(getServerContext().getServerHome()+File.separator+"system"+File.separator+"ws","server-config.wsdd");
           	
		} catch (IOException e) {
			remark = ServiceRemarks.PROBLEM_BINDING_IP_PORT.remark + " (" + e.getMessage() + ")";
			LogManager.getLogger().error(MODULE, "Problem starting " + getServiceIdentifier() + ", reason: " + e);
			return false;
		}catch (InitializationFailedException e) {
			remark = ServiceRemarks.HTTPS_FAILURE.remark + " (" + e.getMessage() + ")";
			LogManager.getLogger().error(MODULE, "Problem starting " + getServiceIdentifier() + ", reason: " + e);
			return false;
		}
		return true;
	}
	private SSLContext generateSSLContext() throws InitializationFailedException{
		if(WebServiceConfiguration.SERVER_CERTIFICATE_PROFILE_ID_NOT_SET_STR.equals(serverCertificateId)) {
			throw new InitializationFailedException("Server certificate profile not found");
		}

		ServerCertificateProfile serverCertificateProfile = serverContext.getServerConfiguration().getServerCertificateConfiguration().getServerCertificateProfileById(serverCertificateId);
		if(serverCertificateProfile == null) {
			throw new InitializationFailedException("server certificate profile not found");
		}

		TrustedCAConfiguration trustedCAConfig = serverContext.getServerConfiguration().getTrustedCAConfiguration();
		CRLConfiguration crlConfig = serverContext.getServerConfiguration().getCRLConfiguration();
		EliteSSLContextFactory eliteSSLContextFactory = new EliteSSLContextFactory(trustedCAConfig, crlConfig);
		
		EliteSSLParameter sslParameter = new EliteSSLParameter(serverCertificateProfile, TLSVersion.TLS1_0, TLSVersion.TLS1_0);
		sslParameter.setClientCertificateRequestRequired(false);
		List<CipherSuites> cipherSuites = new ArrayList<CipherSuites>();
		cipherSuites.add(CipherSuites.TLS_RSA_WITH_AES_128_CBC_SHA);
		sslParameter.addEnabledCiphersuites(cipherSuites);
		
		EliteSSLContextExt eliteSSLContext = null;
		try {
			 eliteSSLContext = eliteSSLContextFactory.createSSLContext(sslParameter);
		} catch (Exception e) {
			throw new InitializationFailedException(e);
		}
		
		return eliteSSLContext.getSslContext();
	}

	@Override
	public String getServiceName() {
		return  "HTTPS Web Service";

	}
	@Override
	public String getServiceIdentifier() {
		return "HTTPS-WEB-SERVICE";
	}
	
	@Override
	protected String getProtocol() {
		return "https";
	}

}
