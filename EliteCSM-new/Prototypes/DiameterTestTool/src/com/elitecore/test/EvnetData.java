package com.elitecore.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.test.command.data.CommandData;
import com.elitecore.test.command.data.ReceiveRequestCommandData;
import com.elitecore.test.command.data.SendCommandData;

@XmlRootElement(name="event")
public class EvnetData {
	
	private String name;
	
	@XmlElementRefs({
		@XmlElementRef(name = "send", type = SendCommandData.class),
		@XmlElementRef(name = "receive-request", type = ReceiveRequestCommandData.class)
	})
	private List<CommandData> commandDatas = new ArrayList<CommandData>();
	
	
	@XmlAttribute(name="name",required=true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<CommandData> getCommandDatas() {
		return commandDatas;
	}

}
