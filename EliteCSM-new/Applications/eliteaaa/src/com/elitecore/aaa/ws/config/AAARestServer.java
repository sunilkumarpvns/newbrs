package com.elitecore.aaa.ws.config;

import java.net.InetSocketAddress;
import java.util.EnumSet;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.DispatcherType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.logging.LogManager;

/**
 * <p>This is responsible for starting REST server for serving REST service call in EliteAAA.
 * It starts REST server(jetty) on given Socket address and stops server when AAA stops it services.<p>
 * 
 * @author chirag i. prajapati
 */
public class AAARestServer {

	private static final String MODULE = "AAA-REST-SERVER";
	private Server restServer;
	private boolean isStarted = false;

	private static AAAServerContext serverContext;
	private InetSocketAddress serviceSocketAddress;
	
	public AAARestServer(AAAServerContext aaaServerContext, InetSocketAddress serviceSocketAddress){
		this.serverContext = aaaServerContext;
		this.serviceSocketAddress = serviceSocketAddress;
	}
	
	public static AAAServerContext getServerContext(){
		return serverContext;
	}
	
	public void start() {

		LogManager.getLogger().info(MODULE, " Starting REST Service");
		
		try {
			
			restServer = new Server(serviceSocketAddress);

			// Register and map the dispatcher servlet
			final ServletHolder servletHolder = new ServletHolder(new CXFServlet());
			final ServletContextHandler context = new ServletContextHandler();

			context.setContextPath("/");
			context.addServlet(servletHolder, "/eliteaaa/*");
			context.addEventListener(new ContextLoaderListener());
			context.setAttribute("aaaservercontext", this.serverContext);
			context.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
			context.setInitParameter("contextConfigLocation", AAARestConfig.class.getName());

			// Add Spring Security Filter by the name
			context.addFilter(new FilterHolder(new DelegatingFilterProxy("springSecurityFilterChain")), 
					"/*",EnumSet.allOf(DispatcherType.class));
			
			restServer.setHandler(context);
			restServer.start();
			isStarted = true;
			
			
			LogManager.getLogger().info(MODULE, " Published REST service successfully on URL http://" + serviceSocketAddress.getAddress().getHostAddress() + ":" + serviceSocketAddress.getPort() +"/eliteaaa");
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to start REST service, Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	public void stop() {
		LogManager.getLogger().info(MODULE, "Stopping REST service");

		if (restServer == null) {
			return;
		}
		
		try {
			restServer.stop();
			isStarted = false;
			
			LogManager.getLogger().info(MODULE, "REST service stopped successfully");
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to stop REST service, Reason: " + e.getMessage());
			LogManager.getLogger().trace(e);
		}
	}

	public boolean isStarted() {
		return isStarted;
	}
}
