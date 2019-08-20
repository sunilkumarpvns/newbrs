package com.elitecore.aaa.script;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public abstract class TranslationMappingScript {

	private ScriptContext scriptContext;
	
	public TranslationMappingScript(ScriptContext scriptContext){
		this.scriptContext = scriptContext;
	}
	
	public ServerContext getServerContext(){
		return scriptContext.getServerContext();
	}
	
	public final void requestTranslationExtension(TranslatorParams params){
		try{
			requestTranslationExt(params);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(getName(), "Error in executing \"pre\" method of translation script: " + getName() + ". Reason: " + ex.getMessage());
			}
			
		}
	}
	
	public final void responseTranslationExtension(TranslatorParams params){
		try{
			responseTranslationExt(params);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(getName(), "Error in executing \"post\" method of translation script: " + getName() + ". Reason: " + ex.getMessage());
			}
		}
	}
	
	/**
	 * This hook is available before the translated request is sent to third party system
	 * @param params this object contains all the request and response objects required for translation
	 * @see TranslatorParams
	 */
	protected abstract void requestTranslationExt(TranslatorParams params);
	/**
	 * This hook is available after the translation has taken place
	 * @param params this object contains all the request and response objects required for translation
	 * @see TranslatorParams
	 */
	protected abstract void responseTranslationExt(TranslatorParams params);
	/**
	 * This is the most important method. This method identifies the script in the EliteAAA Framework.
	 * <br/><br/>
	 * The same name should be used anywhere to configure the script.
	 * <br/><br/>
	 * <b>NOTE: Do not keep the name null or blank. Also name is case-sensitive </b>
	 * @return the name of the script for identifying the script in EliteAAA Framework. 
	 */
	protected abstract String getName();
}
