package com.elitecore.test;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.test.command.Command;
import com.elitecore.test.command.data.CommandData;

@XmlRootElement(name = "echo")
public class EchoCommandData implements CommandData {

	private String message;
	
	@XmlAttribute(name = "message")
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public Command create(ScenarioContext context) throws Exception {
		return new EchoCommand(this);
	}

	@Override
	public String getName() {
		return "echo";
	}

}
