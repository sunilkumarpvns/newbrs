package com.elitecore.test.command.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "default")
public class DefaultData{
	
	@XmlElementRefs({
		@XmlElementRef(name = "send", type = SendCommandData.class),
		@XmlElementRef(name = "receive-response", type = ReceiveResponseCommandData.class),
		@XmlElementRef(name = "receive-request", type = ReceiveRequestCommandData.class),
		@XmlElementRef(name = "validate", type = ValidationData.class),
		@XmlElementRef(name = "any-order", type = AnyOrderedCommandData.class),
		@XmlElementRef(name = "parallel", type = ParallelCommandData.class),
		@XmlElementRef(name = "loop", type = LoopCommandData.class),
		@XmlElementRef(name = "condition", type = ConditionCommandData.class),
		@XmlElementRef(name = "wait", type = ConditionCommandData.class),
		@XmlElementRef(name = "store", type = ConditionCommandData.class)
	})
	private List<CommandData> commandDatas = new ArrayList<CommandData>();

	public List<CommandData> getCommandDatas() {
		return commandDatas;
	}
	
}
