package com.elitecore.test.command.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.ParallelCommand;

@XmlRootElement(name = "parallel")
public class ParallelCommandData implements CommandData{
	
	@XmlElementRefs({
		@XmlElementRef(name = "send", type = SendCommandData.class),
		@XmlElementRef(name = "receive-request", type = ReceiveRequestCommandData.class),
		@XmlElementRef(name = "any-order", type = AnyOrderedCommandData.class),
		@XmlElementRef(name = "parallel", type = ParallelCommandData.class),
		@XmlElementRef(name = "loop", type = LoopCommandData.class),
		@XmlElementRef(name = "condition", type = ConditionCommandData.class),
		@XmlElementRef(name = "wait", type = WaitCommandData.class)
	})
	private List<CommandData> commandDatas = new ArrayList<CommandData>();
	
	private String name ="no-name";
	

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
		
		return new ParallelCommand(name, commands);
	}

	

}