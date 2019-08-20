package com.elitecore.nvsmx.system.groovy;

public class ScriptData{
	private String scriptName;
	private String scriptArg;
	
	public ScriptData(String scriptName, String scriptArg){
		this.scriptName = scriptName;
		this.scriptArg = scriptArg;
	}

	public String getScriptName() {
		return scriptName;
	}

	public String getScriptArgumet() {
		return scriptArg;
	}
}
