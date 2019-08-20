package com.elitecore.diameterapi.core.common.transport.tcp.connection;

import java.net.Socket;

import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.diameterapi.core.common.transport.Connection;
import com.elitecore.diameterapi.core.common.transport.tcp.ConnectorContext;
import com.elitecore.diameterapi.core.common.transport.tcp.exception.HandShakeFailException;
import com.elitecore.diameterapi.core.common.util.constant.ConnectionRole;

public interface ConnectionFactory {
	public Connection createConnection(Socket clientSocket, ConnectionRole connectionRole, ConnectorContext context) throws Exception;
	public Connection secureConnectionWithTLS(Connection connection, ConnectionRole connectionRole, 
			ConnectorContext context, EliteSSLContextExt eliteSSLContext) throws HandShakeFailException;
}
