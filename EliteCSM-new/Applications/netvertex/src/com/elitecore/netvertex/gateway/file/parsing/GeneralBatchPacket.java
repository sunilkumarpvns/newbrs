package com.elitecore.netvertex.gateway.file.parsing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GeneralBatchPacket {

	private Collection<? extends Object> batch;
	private Map<Object,Object> map;

	public GeneralBatchPacket(Collection<? extends Object> batch) {
		this.batch = batch;
		map = new HashMap<>();
	}

	public Collection<? extends Object> getBatch() {
		return  batch;
	}

	public Object getParameter(String key) {
		return map.get(key);
	}

	public void setParameter(String key, Object object) {
		map.put(key, object);
	}
}