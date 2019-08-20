package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.dependecy.diameter.packet.DiameterAnswer;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;

public class ResponseReceivedCommand extends CompositeCommand{
	
	private static final String MODULE = "RES-RCVD-CMD";
	private long timeout;
	

	public ResponseReceivedCommand(long timeout, List<Command> commands, String name) {
		super(name, commands);
		this.timeout = timeout;
	}
	

	@Override
	public void execute(ExecutionContext context) throws Exception {
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing response-received("+getName()+") command");
		
		if(context instanceof SendCommandContext == false){
			throw new IllegalArgumentException("Invalid context received. Received:"+ context.getClass().getCanonicalName() +  " Expected:"+ SendCommandContext.class.getCanonicalName());
		}
		
		SendCommandContext sendCommandContext = (SendCommandContext) context;

		Optional<DiameterPacket> packetData = sendCommandContext.getSendPacketDetail();
		
		assertTrue("Can not find detail of send packet", packetData.isPresent());
		
		DiameterPacket diameterPacket = packetData.get();
	
		
		Optional<Object> future = context.get(String.valueOf(diameterPacket.getHop_by_hopIdentifier()));
		
		assertTrue("Unable to find Diameter Answer for H2H:"+ diameterPacket.getHop_by_hopIdentifier(), future.isPresent());
		
		@SuppressWarnings("unchecked")
		Future<DiameterAnswer> future2 = (Future<DiameterAnswer>)future.get();
		
		DiameterAnswer diameterAnswer = null;
		if(timeout == -1) {
			diameterAnswer = future2.get();
		} else {			
			diameterAnswer = future2.get(timeout, TimeUnit.SECONDS);
		}
		
		context.set(ReceivePacketContext.RCVD_REQ, diameterAnswer);
		LogManager.getLogger().info(MODULE, "Received Diameter Answer:" + diameterAnswer);
		
		executeChild(context);
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Response-received("+getName()+") command execution completed");
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
