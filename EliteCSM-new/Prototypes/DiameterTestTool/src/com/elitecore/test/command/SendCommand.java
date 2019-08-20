package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.commons.base.Optional;
import com.elitecore.test.ExecutionContext;
import com.elitecore.test.dependecy.diameter.DiameterAVPConstants;
import com.elitecore.test.dependecy.diameter.VirtualGateway;
import com.elitecore.test.dependecy.diameter.packet.DiameterAnswer;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.dependecy.diameter.packet.DiameterRequest;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;
import com.elitecore.test.diameter.jaxb.PacketData;

import java.util.List;
import java.util.concurrent.Future;

public class SendCommand extends CompositeCommand{
	
	private static final String MODULE = "SEND-CMD";
	private PacketData packetData;
	private VirtualGateway virtulaGateway;
	

	public SendCommand(List<Command> commands, PacketData packetData,
			VirtualGateway virtulaGateway,String name) {
		super(name, commands);
		this.packetData = packetData;
		this.virtulaGateway = virtulaGateway;
	}


	@Override
	public void execute(ExecutionContext context) throws Exception {
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing send("+getName()+") command");
		
		if(context.getProcess().isStopRequested()){
			System.out.println("stop requested so skip sending packet");
			return;
		}
		
		DiameterPacket diameterPacket = context.getPacketFactory().createDiameterPacket(packetData, context.getValueProvider());
		
		if(diameterPacket.isRequest()){
			Future<DiameterAnswer> future = virtulaGateway.send((DiameterRequest)diameterPacket);
			context.set(Integer.toString(diameterPacket.getHop_by_hopIdentifier()),future);
			SendCommandContext sendCommandContext = new SendCommandContextImpl(context);
			
			sendCommandContext.set(SendCommandContextImpl.SEND_DATA, diameterPacket);
			executeChild(sendCommandContext);
		} else {
			Optional<Object> data = context.get(ReceivePacketContext.RCVD_REQ);
			
			if(data == null){
				throw new Exception("Received packet not found from context");
			}
			
			DiameterRequest diameterRequest = (DiameterRequest)data.get();
			
			diameterPacket.setEnd_to_endIdentifier(diameterRequest.getEnd_to_endIdentifier());
			diameterPacket.setHop_by_hopIdentifier(diameterRequest.getHop_by_hopIdentifier());
			IDiameterAVP diameterAVP = diameterPacket.getAVP(DiameterAVPConstants.SESSION_ID);
			if(diameterRequest.getAVP(DiameterAVPConstants.SESSION_ID) != null){
				if(diameterAVP != null){
					diameterAVP.setStringValue(diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID));
				} else {
					diameterPacket.addAvp(diameterRequest.getAVP(DiameterAVPConstants.SESSION_ID));
				}
			}
			
			virtulaGateway.send((DiameterAnswer)diameterPacket);
		}
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Send("+getName()+") command executing completed");
		
		
	}



	private void executeChild(SendCommandContext context) throws Exception {
		for(Command command :  commands){
			if(context.getProcess().isStopRequested()){
				return;
			}
			command.execute(context); 
		}
	}


	@Override
	public String getName() {
		return name;
	}
}
