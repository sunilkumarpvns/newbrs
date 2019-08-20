/*package com.elitecore.elitesm.web.dashboard;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.elitesm.web.dashboard.widget.WidgetDataSender;
import com.elitecore.elitesm.web.dashboard.widget.WidgetRequest;
import com.elitecore.elitesm.web.dashboard.widget.WidgetService;
import com.elitecore.elitesm.web.dashboard.widget.impl.WidgetRequestImpl;

*//**
 * Provide Implementation of Web Socket for Dashboard.
 *  
 * @author punit.j.patel
 *
 *//*
@WebServlet("/dashboardSocket")
public class DashBoardSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "DASHBOARDSERVLET";
	private WidgetService widgetService;
	private AtomicLong nextID = new AtomicLong();
	
	@Override
	public void init() throws ServletException {
		if( LogManager.getLogger().isLogLevel(LogLevel.DEBUG) ) {
			LogManager.getLogger().debug(MODULE, "Initializing Dashboard Servlet");
		}
		widgetService = new WidgetService();
		try {
			widgetService.init();
		} catch (InitializationFailedException e) {
			LogManager.getLogger().error(MODULE, "Error while send initializing Widget Service, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while send initializing Widget Service, reason : " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		if( LogManager.getLogger().isLogLevel(LogLevel.DEBUG) ) {
			LogManager.getLogger().debug(MODULE, "Dashboard Servlet initialized successfully");
		}
	}
	
	@Override
	public void destroy() {
		widgetService.destroy();
	}
	
	public long nextID() {
		return nextID.incrementAndGet();
	}
	

	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
		return new WidgetMessageInbound();
	}
	
	
    private class WidgetMessageInbound extends MessageInbound {
    	private static final String MODULE = "WIDGETMESSAGEINBOUND";
    	private WidgetDataSender widgetDataSender = createWidgetDataSender();
    	private String socketID = String.valueOf(nextID());  
    	
    	@Override
		protected void onTextMessage(CharBuffer message) throws IOException {
    		if( message != null) {
    			WidgetRequest widgetRequest = new WidgetRequestImpl(socketID, message.toString(), widgetDataSender);
    			widgetService.processWidgetRequest(widgetRequest);
    		} 
		}
    	
    	@Override
    	protected void onClose(int status) {
    		widgetService.deRegister(socketID);
    	}
    	
    	
		private WidgetDataSender createWidgetDataSender() {
			return new WidgetDataSender() {
				
				@Override
				public void send(String data) {
					if ( data == null ) 
						return;
					
					try {
						if( LogManager.getLogger().isLogLevel(LogLevel.DEBUG) ) {
							LogManager.getLogger().debug(MODULE, "Sending Data : " + data);
						}
						getWsOutbound().writeTextMessage(CharBuffer.wrap(data));
						getWsOutbound().flush();
					} catch (IOException e) {
						LogManager.getLogger().error(MODULE, "Error while send data to socket, reason : " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					} catch (Exception e) {
						LogManager.getLogger().error(MODULE, "Error while send data to socket, reason : " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
					}
					
				}
			};
		}
		
		@Override
		protected void onBinaryMessage(ByteBuffer message) throws IOException {
			
		}
    	
    }
    
	

}
*/