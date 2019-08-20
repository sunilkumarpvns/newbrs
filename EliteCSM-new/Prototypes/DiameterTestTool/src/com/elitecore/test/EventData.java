package com.elitecore.test;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

import com.elitecore.test.command.data.CommandData;
import com.elitecore.test.command.data.ReceiveRequestCommandData;
import com.elitecore.test.command.data.SendCommandData;

public class EventData {
	
	private String name;
	private List<CommandData> commandDatas;

	@XmlElementRefs({
		@XmlElementRef(name = "send", type = SendCommandData.class),
		@XmlElementRef(name = "receive-request", type = ReceiveRequestCommandData.class)
	})
	public List<CommandData> getCommandDatas(){
		return commandDatas;
	}
	
	@XmlAttribute(name="name",required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
