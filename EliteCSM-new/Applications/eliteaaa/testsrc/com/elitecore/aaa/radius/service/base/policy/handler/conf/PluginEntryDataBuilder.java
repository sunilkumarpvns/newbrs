package com.elitecore.aaa.radius.service.base.policy.handler.conf;

public class PluginEntryDataBuilder {
	private static final String DUMMY_ARGUMENT = "DUMMY_ARGUMENT";
	public PluginEntryData pluginEntryData = new PluginEntryData();

	public PluginEntryDataBuilder() {
		pluginEntryData.setPluginName("plugin1");
		pluginEntryData.setPluginArgument(DUMMY_ARGUMENT);
	}

	public PluginEntryDataBuilder withRuleset(String ruleset) {
		pluginEntryData.setRuleset(ruleset);
		return this;
	}

	public PluginEntryDataBuilder pluginName(String pluginName) {
		pluginEntryData.setPluginName(pluginName);
		return this;
	}

	public PluginEntryDataBuilder onResponse() {
		pluginEntryData.setOnResponse(true);
		return this;
	}

	public PluginEntryDataBuilder onRequest() {
		pluginEntryData.setOnResponse(false);
		return this;
	}
}
