package com.elitecore.aaa.diameter.conf;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elitecore.diameterapi.core.common.transport.priority.Priority;

// FIXME Chetan or Monica please replace this with CaseInsensitiveEnumAdapter here
public class PriorityAdapter extends XmlAdapter<String, Priority> {

	@Override
	public Priority unmarshal(String priority) throws Exception {
		return Priority.fromPriority(priority);
	}

	@Override
	public String marshal(Priority priorityEnum) throws Exception {
		return priorityEnum.priority;
	}
}
