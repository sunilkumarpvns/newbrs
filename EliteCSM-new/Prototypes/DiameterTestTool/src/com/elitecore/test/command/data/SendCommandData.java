package com.elitecore.test.command.data;

import com.elitecore.test.EchoCommandData;
import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.SendCommand;
import com.elitecore.test.diameter.jaxb.PacketData;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;
@XmlRootElement(name = "send")
public class SendCommandData implements CommandData {
	
	private PacketData packetData;
	@XmlElementRefs({
		@XmlElementRef(name = "receive-response", type = ReceiveResponseCommandData.class),
		@XmlElementRef(name = "receive-request", type = ReceiveRequestCommandData.class),
		@XmlElementRef(name = "validate", type = ValidationData.class),
		@XmlElementRef(name = "any-order", type = AnyOrderedCommandData.class),
		@XmlElementRef(name = "parallel", type = ParallelCommandData.class),
		@XmlElementRef(name = "loop", type = LoopCommandData.class),
		@XmlElementRef(name = "wait", type = WaitCommandData.class),
		@XmlElementRef(name = "condition", type = ConditionCommandData.class),
		@XmlElementRef(name = "send", type = SendCommandData.class),
		@XmlElementRef(name = "store", type = StoreCommandData.class),
		@XmlElementRef(name = "echo", type = EchoCommandData.class)
		
	})
	private List<CommandData> commandDatas = new ArrayList<CommandData>();
	private String name ="no-name";
	
	
	@Override
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public List<CommandData> getCommandDatas() {
		return commandDatas;
	}


	@XmlElement(name = "packet", required=true)
	public PacketData getPacketData() {
		return packetData;
	}



	public void setPacketData(PacketData packetData) {
		this.packetData = packetData;
	}

	@Override
	public Command create(ScenarioContext context) throws Exception{
		List<Command> commands = new ArrayList<Command>();
		for(CommandData commandData : commandDatas){
			commands.add(commandData.create(context));
		}
		
		return new SendCommand(commands, packetData, context.getVirtualGateway(),name);
	}


}
