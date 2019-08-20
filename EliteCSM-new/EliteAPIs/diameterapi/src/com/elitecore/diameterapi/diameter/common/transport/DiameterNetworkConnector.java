/**
 * 
 */
package com.elitecore.diameterapi.diameter.common.transport;

import com.elitecore.core.commons.tls.EliteSSLParameter;
import com.elitecore.diameterapi.core.common.transport.constant.SecurityStandard;
import com.elitecore.diameterapi.core.common.transport.tcp.TCPNetworkConnector;
import com.elitecore.diameterapi.core.common.transport.tcp.connection.ConnectionFactory;
import com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;
import com.elitecore.diameterapi.diameter.stack.DiameterStack;

/**
 * @author pulin
 *
 */
public class DiameterNetworkConnector extends TCPNetworkConnector {
	private int socketReceiveBufferSize = 32767;
	private int socketSendBufferSize = 32767;
	private String networkAddress = "127.0.0.1";
	private int networkPort = DiameterConstants.DIAMETER_SERVICE_PORT;
	private EliteSSLParameter sslParameter;
	private SecurityStandard securityStandard = SecurityStandard.NONE;

	public DiameterNetworkConnector(DiameterStack diameterStack) {
		super(diameterStack);
	}

	@Override
	public boolean start(ConnectionFactory connectionFactory) {
		return super.start(connectionFactory);
	}

	public void setSocketReceiveBufferSize(int socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
	}

	@Override
	protected int getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}

	public void setSocketSendBufferSize(int socketSendBufferSize) {
		this.socketSendBufferSize = socketSendBufferSize;
	}

	@Override
	protected int getSocketSendBufferSize() {
		return socketSendBufferSize;
	}
	
	public void setNetworkAddress(String address) {
		this.networkAddress = address;
	}
	
	@Override
	public String getNetworkAddress() {
		return this.networkAddress;
	}
	
	public void setNetworkPort(int port) {
		this.networkPort = port;
	}
	
	@Override
	public int getNetworkPort() {
		return this.networkPort;
	}

	@Override
	protected String getThreadIdentifier() {
		return CommonConstants.DIAMETER_STACK_IDENTIFIER + "-CONN";
	}
	
	public boolean stop() {
		return super.stop();
	}
	
	@Override
	public EliteSSLParameter getDefalutSSLParameter() {
		return sslParameter;
	}
	
	public void setDefalutSSLParameter(EliteSSLParameter eliteSSLParameter) {
		this.sslParameter = eliteSSLParameter;
	}
	
	public void setSecurityStandard(SecurityStandard securityStandard) {
		
		this.securityStandard = securityStandard;
	}

	@Override
	public SecurityStandard geSecurityStandard() {
		return securityStandard;
	}
	
}
