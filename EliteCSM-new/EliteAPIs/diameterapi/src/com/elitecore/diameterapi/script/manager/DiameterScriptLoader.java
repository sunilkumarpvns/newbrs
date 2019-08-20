package com.elitecore.diameterapi.script.manager;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.codehaus.groovy.control.CompilationFailedException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.stack.DiameterStack.DiameterStackContext;

public class DiameterScriptLoader {

	private static final String MODULE = "DIA-SCRPT-LOADER";
	private final DiameterStackContext stackContext;
	private String externalScriptsPath;
	private Map<String,GroovyObject> scriptsMap = new HashMap<String, GroovyObject>();
	private Map<String, List<File>> scriptToFile;
	
	public DiameterScriptLoader(DiameterStackContext stackContext,
			Map<String, List<File>> diameterScriptMap) {

		this.stackContext = stackContext;
		this.scriptToFile = diameterScriptMap;
	}

	public void load() throws InitializationFailedException{
		try{
			loadGroovyFiles(scriptToFile);
		}catch(Throwable ex){
			LogManager.getLogger().trace(ex);
			throw new InitializationFailedException("Error in initializing Diameter Scripts manager. Reason: " + ex.getMessage());
		}
	}
	
	private void loadGroovyFiles(Map<String, List<File>> filemap){
		ClassLoader classLoader = DiameterScriptsManager.class.getClassLoader();
		GroovyClassLoader groovyLoader = new GroovyClassLoader(classLoader);
		DiameterScriptContext scriptContext = createScriptContext();
		Set<Entry<String, List<File>>> filedetail  = filemap.entrySet();
		for(Entry<String, List<File>> entry : filedetail) {
			for(File file: entry.getValue()){
				try{
					Class<?> groovyClass = groovyLoader.parseClass(file);
					Constructor<?> constructor = groovyClass.getConstructor(new Class<?>[]{DiameterScriptContext.class});
					GroovyObject groovyScriptObj = (GroovyObject)constructor.newInstance(new Object[]{scriptContext});
					scriptsMap.put(entry.getKey(), groovyScriptObj);
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "Groovy: " + file + " loaded successfully.");
					}

				}catch(CompilationFailedException ex){
					LogManager.getLogger().trace(MODULE, ex);
					LogManager.getLogger().debug(MODULE, "Error in loading external Script:" + file.getAbsolutePath() + 
							" Reason: " + ex.getMessage());

				} catch (InstantiationException ex) {
					LogManager.getLogger().trace(MODULE, ex);
					LogManager.getLogger().debug(MODULE, "Error in loading external Script:" + file.getAbsolutePath()  + 
							" Reason: " + ex.getMessage());

				} catch (IllegalAccessException ex) {
					LogManager.getLogger().trace(MODULE, ex);
					LogManager.getLogger().debug(MODULE, "Error in loading external Script:" + file.getAbsolutePath()  + 
							" Reason: " + ex.getMessage());

				}catch(ExceptionInInitializerError ex){
					LogManager.getLogger().trace(MODULE, ex);
					LogManager.getLogger().debug(MODULE, "Error in loading external Script:" + file.getAbsolutePath()  + 
							" Reason: " + ex.getMessage());

				}catch(Throwable ex){
					LogManager.getLogger().trace(MODULE, ex);
					LogManager.getLogger().debug(MODULE, "Unknwown Error in loading external Script:" + file.getAbsolutePath()  + 
							" Reason: " + ex.getMessage());
				}
			}
		}
	}

	private DiameterScriptContext createScriptContext(){
		return new DiameterScriptContext() {
			
			@Override
			public PeerData getPeerData(String hostIdentity) {
				return stackContext.getPeerData(hostIdentity);
			}
			
			@Override
			public TaskScheduler getTaskSchedular() {
				return new TaskScheduler() {
					@Override
					public Future<?> scheduleSingleExecutionTask(SingleExecutionAsyncTask task) {
						return stackContext.scheduleSingleExecutionTask(task);
					}

					@Override
					public Future<?> scheduleIntervalBasedTask(IntervalBasedTask task) {
						return stackContext.scheduleIntervalBasedTask(task);
					}

					@Override
					public void execute(Runnable command) {
						stackContext.scheduleSingleExecutionTask(command);
					}
				};
			}

			@Override
			public DiameterStackContext getStackContext() {
				return stackContext;
			}
		};
	}

	public Map<String, GroovyObject> getLoadedScripts() {
		return scriptsMap;
	}
}
