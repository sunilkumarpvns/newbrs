package com.elitecore.netvertex.gateway.file.parsing;

import com.elitecore.corenetvertex.spr.ProcessFailException;


public interface ParsingPlugin {

	boolean assignRequest(ParsingPluginRequest pluginRequest);

	void processRequest(ParsingPluginRequest pluginRequest) throws ProcessFailException;

}
