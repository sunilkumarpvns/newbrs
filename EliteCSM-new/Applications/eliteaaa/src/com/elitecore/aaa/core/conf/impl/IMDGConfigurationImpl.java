package com.elitecore.aaa.core.conf.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.imdg.config.IMDGConfiguration;

public class IMDGConfigurationImpl implements IMDGConfiguration {

	private static final String MODULE = "IMDG-CONF";
	private final ImdgConfigData imdgConfigData;
	private final ClusterGroupData selectedGroupData;
	private final Properties properties;
	private final List<String> memberIps;
	private final boolean isMancenterEnabled;

	public IMDGConfigurationImpl(@Nonnull ImdgConfigData imdgConfigData,
			@Nonnull ClusterGroupData selectedGroupData) {
		this.imdgConfigData = imdgConfigData;
		this.selectedGroupData = selectedGroupData;
		this.properties = imdgConfigData.getProperties();
		this.memberIps = new ArrayList<String>();
		for(MemberData data : selectedGroupData.getMemberDatas()) {
			this.memberIps.add(data.getIp());
		}
		this.isMancenterEnabled = Strings.isNullOrBlank(imdgConfigData.getMancenterUrl()) == false;
	}

	@Override
	public String getGroupName() {
		return selectedGroupData.getName();
	}

	@Override
	public String getPassword() {
		return selectedGroupData.getPasswd();
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public boolean isMancenterEnabled() {
		return isMancenterEnabled;
	}

	@Override
	public String getUrl() {
		return imdgConfigData.getMancenterUrl();
	}

	@Override
	public List<String> getMemberIps() {
		return memberIps;
	}

	@Override
	public int getPortCount() {
		return imdgConfigData.getStartPortCount();
	}

	@Override
	public int getStartPort() {
		return imdgConfigData.getStartPort();
	}

	@Override
	public Collection<String> getOutBoundPorts() {
		return imdgConfigData.getOutbounds();
	}

	@Override
	public String getInMemoryFormat() {
		return imdgConfigData.getInMemoryFormat();
	}
	
	@Override
	public Map<String, String> getMemberData() {
		Map<String, String> nameToHostnameMap = new HashMap<String, String>();
		List<MemberData> memberData =  selectedGroupData.getMemberDatas();
		for(MemberData member : memberData) {
			nameToHostnameMap.put(member.getName(), member.getIp());
		}
		return nameToHostnameMap;
	}
}
