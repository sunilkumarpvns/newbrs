package com.elitecore.test.command.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

public class BaseCommandData {
	@XmlElementRefs({
		@XmlElementRef(name = "receive-response", type = ReceiveResponseCommandData.class),
		@XmlElementRef(name = "receive-request", type = ReceiveRequestCommandData.class),
		@XmlElementRef(name = "validate", type = ValidationData.class),
		@XmlElementRef(name = "any-order", type = AnyOrderedCommandData.class),
		@XmlElementRef(name = "parallel", type = ParallelCommandData.class)
	})
	private List<CommandData> commandDatas = new ArrayList<CommandData>();
	
	public List<CommandData> getCommandDatas() {
		return commandDatas;
	}

}
