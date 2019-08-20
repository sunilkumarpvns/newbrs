package com.elitecore.netvertex.core.conf.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.corenetvertex.sm.gateway.GroovyScriptData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceGroovyScriptData;
import com.elitecore.netvertex.core.data.ScriptData;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class ScriptDataFactory {

	public List<ScriptData> create(List<GroovyScriptData> groovyScriptDatas) {
		
		List<ScriptData> scriptDatas = new ArrayList<ScriptData>();
		
		for (GroovyScriptData groovyScriptData : groovyScriptDatas) {
			scriptDatas.add(new ScriptData(groovyScriptData.getScriptName(), groovyScriptData.getArgument()));
		} 
		
		return scriptDatas;
	}

	public List<ScriptData> createServerInstanceGroovyData(List<ServerInstanceGroovyScriptData> groovyScriptDatas) {

		List<ScriptData> scriptDatas = new ArrayList<ScriptData>();

		for (ServerInstanceGroovyScriptData groovyScriptData : groovyScriptDatas) {
			scriptDatas.add(new ScriptData(groovyScriptData.getScriptName(), groovyScriptData.getArgument()));
		}

		return scriptDatas;
	}
}
