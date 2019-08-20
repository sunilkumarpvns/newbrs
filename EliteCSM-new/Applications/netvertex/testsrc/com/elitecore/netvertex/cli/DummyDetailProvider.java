package com.elitecore.netvertex.cli;

import java.util.HashMap;

import com.elitecore.core.util.cli.cmd.DetailProvider;

/**
 * Used for test cases of Any Detail provider
 */
public class DummyDetailProvider extends DetailProvider {

	private final String key;
	private final String helpMessage;
	private final HashMap<String, DetailProvider> detailProviderMap;

	public DummyDetailProvider(String key, String helpMessage,
			HashMap<String, DetailProvider> map) {
		this.key = key;
		this.helpMessage = helpMessage;
		this.detailProviderMap = map;
	}

	@Override
	public String execute(String[] parameters) {
		return key + " executed";
	}

	@Override
	public String getHelpMsg() {
		return helpMessage;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public HashMap<String, DetailProvider> getDetailProviderMap() {
		return detailProviderMap;
	}
}

