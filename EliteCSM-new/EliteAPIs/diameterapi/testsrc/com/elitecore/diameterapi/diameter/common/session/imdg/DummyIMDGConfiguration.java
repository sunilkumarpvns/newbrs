package com.elitecore.diameterapi.diameter.common.session.imdg;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.elitecore.core.imdg.config.IMDGConfiguration;
import com.hazelcast.config.InMemoryFormat;

public class DummyIMDGConfiguration implements IMDGConfiguration {
	
	public static final String INSTANCE_NAME = "INSTANCE-NAME";

	@Override
	public boolean isMancenterEnabled() {
		return false;
	}
	
	@Override
	public String getUrl() {
		return null;
	}
	
	@Override
	public int getStartPort() {
		return 5701;
	}
	
	@Override
	public Properties getProperties() {
		return new Properties();
	}
	
	@Override
	public int getPortCount() {
		return 100;
	}
	
	@Override
	public String getPassword() {
		return INSTANCE_NAME;
	}
	
	@Override
	public Collection<String> getOutBoundPorts() {
		return null;
	}
	
	@Override
	public List<String> getMemberIps() {
		return Arrays.asList("127.0.0.1");
	}
	
	@Override
	public String getInMemoryFormat() {
		return InMemoryFormat.BINARY.name();
	}
	
	@Override
	public String getGroupName() {
		return INSTANCE_NAME;
	}

	@Override
	public Map<String, String> getMemberData() {
		Map<String, String> membersData = new HashMap<String, String>();
		membersData.put(INSTANCE_NAME, "127.0.0.1");
		return membersData;
	}
}
