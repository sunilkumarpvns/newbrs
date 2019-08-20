package com.elitecore.test.command.data;

import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.ValidationCommand;
import com.elitecore.test.diameter.jaxb.AttributeData;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "validate")
public class ValidationData implements CommandData {
	
	private List<AttributeData> attributeDatas = new ArrayList<AttributeData>();
	private String name ="no-name";
	
	
	@Override
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name="attribute")
	public List<AttributeData> getAttributeDatas() {
		return attributeDatas;
	}

	@Override
	public Command create(ScenarioContext context) throws Exception {
		return new ValidationCommand(attributeDatas,name);
		
	}

}
