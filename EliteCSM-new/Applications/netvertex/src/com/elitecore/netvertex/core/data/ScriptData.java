package com.elitecore.netvertex.core.data;


import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class ScriptData implements ToStringable{
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

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.incrementIndentation()
				.append("Script", scriptName)
				.append("Script Argument", scriptArg);
	}
}
