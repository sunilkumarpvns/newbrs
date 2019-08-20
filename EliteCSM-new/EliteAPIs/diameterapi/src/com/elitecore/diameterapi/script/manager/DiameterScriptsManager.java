package com.elitecore.diameterapi.script.manager;

import groovy.lang.GroovyObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.serverx.manager.scripts.ScriptsExecutor;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.DiameterStackContext;

public class DiameterScriptsManager implements ScriptsExecutor {
	
	private static Map<String,GroovyObject> scriptsMap = new HashMap<String, GroovyObject>();
	private static DiameterScriptsManager diameterScriptsManager;
	
	public static DiameterScriptsManager getInstance(){
		if(diameterScriptsManager == null){
			diameterScriptsManager = new DiameterScriptsManager();
		}
		return diameterScriptsManager;
	}
	
	public void init(DiameterStackContext stackContext, Map<String, List<File>> diameterScriptMap) 
			throws InitializationFailedException{
		try{
			DiameterScriptLoader scriptLoader = new DiameterScriptLoader(stackContext,diameterScriptMap);
			scriptLoader.load();
			if(scriptLoader.getLoadedScripts() != null)
				scriptsMap.putAll(scriptLoader.getLoadedScripts());
			
		}catch(Throwable ex){
			throw new InitializationFailedException("Error in initializing Diameter Scripts manager. Reason: " + ex.getMessage(), ex);
		}
	}
	
	/**
	 * This method allows the users of the external scripting framework to call the script directly with the required args.
	 * 
	 * @param scriptName the name of the script to be executed
	 * @param scriptType the type of the script
	 * @param methodName the name of the method to be executed
	 * @param argTypes the type of the arguments 
	 * @param args the actual arguments
	 * 
	 * @throws Exception throws exception when the script is not compiled or the method with given argument types is not found
	 */
	public void execute(String scriptName, Class<?> scriptType, String methodName, Class<?>[] argTypes,Object[] args) throws Exception{
		if(scriptName == null || methodName == null || scriptType == null){
			throw new IllegalArgumentException("Invalid arguments. Either of script name, script type or method name is null.");
		}
		
		GroovyObject groovyObject = scriptsMap.get(scriptName);
		if(groovyObject != null){
			if(scriptType.isInstance(groovyObject)){
				try {
					Method method = scriptType.getMethod(methodName.trim(), argTypes);
					method.invoke(groovyObject, args);
				} catch (SecurityException e) {
					throw new IllegalArgumentException(e.getMessage(), e);
					
				} catch (NoSuchMethodException e) {
					throw new IllegalArgumentException("Method with name : " + methodName + 
							" not found in script : " + scriptName);
					
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException("Method not found with signature : (" + 
							StringUtility.getDelimiterSeparatedString(argTypes, ",") + ")",e);
					
				} catch (IllegalAccessException e) {
					throw new IllegalArgumentException("Method access is not available for method: " + 
							methodName + " in script: " + scriptName + ". Make sure method is public.", e);
					
				} catch (InvocationTargetException e) {
					throw new IllegalArgumentException("Exception in method: " + methodName + 
							" in script: " + scriptName + ". Reason : " + e.getMessage(), e);
				}
			}else{
				throw new IllegalArgumentException("Script with name: " + scriptName + 
						" is not of type - " + scriptType.getName() + 
						". Make sure it extends or implements this type.");
			}
		}else{
			throw new IllegalArgumentException("Script with name: " + scriptName +
					" not found. Make sure the script path is correct and the script is compiled successfully.");
		}
	}
}
