package com.sterlite.netvertex.nvsampler.cleanup.util;


import org.apache.log.Logger;

public class SamplerLogger {
	private static org.apache.log.Logger logger;
	public static void setLogger(org.apache.log.Logger logger) {
		SamplerLogger.logger = logger;
	}

	public static Logger getLogger() {
		return logger;
	}
}
