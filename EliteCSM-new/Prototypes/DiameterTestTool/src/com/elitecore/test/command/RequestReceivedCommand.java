package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.dependecy.diameter.VirtualGateway;
import com.elitecore.test.dependecy.diameter.packet.DiameterRequest;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RequestReceivedCommand extends CompositeCommand {

	private static final String MODULE = "REQ-RCVD-CMD";
	private int commandCode;
	private long timeout;
	private VirtualGateway virtualGateway;
	private int appId;
	
	public RequestReceivedCommand(int appId, int commandCode,
			long timeout, VirtualGateway virtualGateway, List<Command> commands, String name) {
		super(name,commands);
		this.appId = appId;
		this.commandCode = commandCode;
		this.timeout = timeout;
		this.virtualGateway = virtualGateway;
	}


	@Override
	public void execute(ExecutionContext context) throws Exception {
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing request-received("+getName()+") command, wait for "  + commandCode + "-" +appId);

		
		Future<DiameterRequest> future = virtualGateway.receive(commandCode + "-" +appId);
		DiameterRequest diameterRequest = null;
		if(timeout == -1){
			diameterRequest = future.get();
		} else {
			diameterRequest = future.get(timeout, TimeUnit.SECONDS);
		}
		
		
		LogManager.getLogger().info(MODULE, "Received Diameter Request:" + diameterRequest);
		
		ReceivePacketContext receivedRequestContext = new ReceiveRequestContextImpl(context);
		receivedRequestContext.set(ReceivePacketContext.RCVD_REQ, diameterRequest);
		
		executeChild(receivedRequestContext);
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Request-received("+getName()+") command execution completed");
	}
	
	private void executeChild(ExecutionContext context) throws Exception {
		SendCommandContext sendCommandContext = new SendCommandContextImpl(context); 
		for(Command command :  commands){ command.execute(sendCommandContext); }
	}


	@Override
	public String getName() {
		return name;
	}
}
