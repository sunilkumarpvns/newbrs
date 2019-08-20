package com.elitecore.test.command.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.ResponseReceivedCommand;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "receive-response")
public class ReceiveResponseCommandData implements CommandData {
	
	@SerializedName("time-out") private long timeout = -1;
	@XmlElementRefs({
		@XmlElementRef(name = "validate", type = ValidationData.class),
		@XmlElementRef(name = "store", type = StoreCommandData.class),
		@XmlElementRef(name = "wait", type = WaitCommandData.class)
		
	})
	@SerializedName("validate") private List<CommandData> validationDatas = new ArrayList<CommandData>();
	private String name ="no-name";
	
	
	@Override
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<CommandData> getValidationDatas() {
		return validationDatas;
	}

	@XmlAttribute(name = "time-out",required=false)
	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	@Override
	public Command create(ScenarioContext context) throws Exception{
		
		List<Command> commands = new ArrayList<Command>();
		for(CommandData commandData :  validationDatas){
			commands.add(commandData.create(context));
		}
		
		return new ResponseReceivedCommand(timeout,commands,name);
		
		
	}

}
