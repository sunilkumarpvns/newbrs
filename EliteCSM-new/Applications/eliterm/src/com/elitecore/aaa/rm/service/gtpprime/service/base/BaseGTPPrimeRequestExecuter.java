/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.service.base;

import java.net.DatagramSocket;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientData;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseSingleExecutionAsyncTask;

/**
 * @author dhaval.jobanputra
 *
 */
public abstract class BaseGTPPrimeRequestExecuter extends BaseSingleExecutionAsyncTask {

	protected int clientPort;
	protected String clientIP;
	protected int socketTimeout;
	protected int clientRetryCounter;
	protected AAAServerContext context;
	protected DatagramSocket socket;

	private static final int VERSION_SUPPORTED = 2;
	private static final int GTPP_PROTOCOL = 0;
	private static final int SPAREBITS_VALUE = 7;
	private static final int HEADER_TYPE = 0;

	public BaseGTPPrimeRequestExecuter( ServerContext context , GTPPrimeClientData client) {
		super();
		this.context = (AAAServerContext) context;
		this.clientPort = client.getClientPort();
		this.clientIP = client.getClientIP();
		socketTimeout = (int) client.getRequestExpiryTime();
		clientRetryCounter = client.getRequestRetry();
	}

	public byte createFirstByte() {
		int gtpPrimeVersion = VERSION_SUPPORTED;
		int protocolType = GTPP_PROTOCOL;
		int spareBits = SPAREBITS_VALUE;
		int headerType = HEADER_TYPE;
		gtpPrimeVersion = gtpPrimeVersion << 5;
		protocolType = protocolType << 4;
		spareBits = spareBits << 1;
		return new Byte(new Integer(gtpPrimeVersion | protocolType | spareBits | headerType).byteValue());
	}

	public abstract void incrementRequestSentCounter(String clientIP);
	public abstract void incrementResponeReceivedCounter(String clientIP);
	
}