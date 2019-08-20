package com.elitecore.test.command.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.AnyOrderedCommand;

@XmlRootElement(name = "any-order")
public class AnyOrderedCommandData implements CommandData{
	
	@XmlElementRefs({
		@XmlElementRef(name = "send", type = SendCommandData.class),
		@XmlElementRef(name = "receive-response", type = ReceiveResponseCommandData.class),
		@XmlElementRef(name = "receive-request", type = ReceiveRequestCommandData.class)
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

	@Override
	public Command create(ScenarioContext context) throws Exception {
		
		List<Command> commands = new ArrayList<Command>();
		for (CommandData commandData : commandDatas) {
			commands.add(commandData.create(context));
		}
		
		return new AnyOrderedCommand(commands,name);
	}


	

}