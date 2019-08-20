package com.sterlite.netvertex.nvsampler.cleanup.util;

import org.apache.jmeter.threads.JMeterContextService;


import static com.sterlite.netvertex.nvsampler.cleanup.util.SamplerLogger.getLogger;

public class CleanUpUtils {
	private static String smContextPath;
	private static final String HTTP = "http://";
	private static final String SLASH = "/";
	private static final String COLON = ":";

	public static String getGlobalVariable(String var) {
		return JMeterContextService.getContext().getVariables().get(var);
	}

	public static String getSmContextPath() {
		if (smContextPath == null) {
			String serverIp = getGlobalVariable("serverip");
			String smserverport = getGlobalVariable("smserverport");
			String pdcontextpath = getGlobalVariable("pdcontextpath");
			smContextPath = new StringBuilder(HTTP).append(serverIp).append(COLON).append(smserverport).append(SLASH).append(pdcontextpath).toString();
			getLogger().info("Using smContextPath: " + smContextPath);
		}

		return smContextPath;
	}
}
