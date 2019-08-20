package com.elitecore.core.serverx.manager.scripts;

public interface ScriptsExecutor {

	public void execute(String scriptName, 
			Class<?> scriptType, 
			String methodName, 
			Class<?>[] argTypes,
			Object[] args) throws Exception;
	
}
