package com.elitecore.test.command.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.RequestReceivedCommand;
import com.google.gson.annotations.SerializedName;
@XmlRootElement(name = "receive-request")
public class ReceiveRequestCommandData implements CommandData {
	
	
	
	@SerializedName("cc") 	    private int commandCode;
	@SerializedName("app-id") 	private int appId;
	@SerializedName("time-out") private long timeout = -1;
	@XmlElementRefs({
		@XmlElementRef(name = "send", type = SendCommandData.class),
		@XmlElementRef(name = "validate", type = ValidationData.class),
		@XmlElementRef(name = "any-order", type = AnyOrderedCommandData.class),
		@XmlElementRef(name = "parallel", type = ParallelCommandData.class),
		@XmlElementRef(name = "loop", type = LoopCommandData.class),
		@XmlElementRef(name = "condition", type = ConditionCommandData.class),
		@XmlElementRef(name = "store", type = StoreCommandData.class),
		@XmlElementRef(name = "wait", type = WaitCommandData.class)
	})
	@SerializedName("command") private List<CommandData> commandDatas = new ArrayList<CommandData>();
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

	@XmlAttribute(name = "app-id",required=true)
	public int getAppId() {
		return appId;
	}

	@XmlAttribute(name = "time-out",required=false)
	public long getTimeout() {
		return timeout;
	}
	
	@XmlAttribute(name = "cc",required=true)
	public int getCommandCode() {
		return commandCode;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public void setAppId(int appId) {
		this.appId = appId;
	}	

	public void setCommandCode(int commandCode) {
		this.commandCode = commandCode;
	} 
	



	@Override
	public Command create(ScenarioContext context) throws Exception{
		
		List<Command> commands = new ArrayList<Command>();
		for(CommandData validationData :  commandDatas){
			commands.add(validationData.create(context));
		}
		return new RequestReceivedCommand(appId,commandCode, timeout, context.getVirtualGateway(),commands,name);
		
		
	}

}
