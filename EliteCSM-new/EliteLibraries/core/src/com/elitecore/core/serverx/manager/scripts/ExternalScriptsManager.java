package com.elitecore.core.serverx.manager.scripts;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.script.ScriptPlugin;
import com.elitecore.core.commons.util.StringUtility;
import com.elitecore.core.serverx.ServerContext;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.control.CompilationFailedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class does management of all the external scripts that are loaded dynamically in 
 * EliteAAA framework.
 * This manager scans for the static scripts directory to find external scripts and external
 * plugins that are not included by default in server.
 * 
 * @author narendra.pathai
 *
 */
public class ExternalScriptsManager implements ScriptsExecutor {
	private static final String MODULE = "EXTRNL_SCRIPTS_MNGR";
	private final ServerContext serverContext;
	Map<String,GroovyObject> scriptsMap = new HashMap<String, GroovyObject>();
	private GroovyClassLoader groovyLoader = new GroovyClassLoader(getClass().getClassLoader());
	private Map<String, List<File>> scriptToFiles = new HashMap<String, List<File>>();
	
	
	public ExternalScriptsManager(ServerContext serverContext, Map<String,List<File>> map){
		this.serverContext = serverContext;
		this.scriptToFiles = map;
	}
	
	public void init() {
		if(scriptToFiles.isEmpty()) {
			LogManager.getLogger().debug(MODULE, "No script files found to compile.");
			return;
		}
		loadGroovyFiles(scriptToFiles);
	}
	
	public List<ScriptPlugin> loadExternalPlugins(String pluginsPath, PluginContext pluginContext){
		File[] externalPlugins = scanPlugins(pluginsPath);
		return loadExternalPlugins(externalPlugins,pluginContext);
	}
	
	public static GroovyObject createGroovyObject(File file, Class<?>[] argTypes,Object[] args) 
			throws	CompilationFailedException,
					InstantiationException,
					IllegalAccessException,
					ExceptionInInitializerError,
					IOException,
					NoSuchMethodException,
					SecurityException,
					IllegalArgumentException, 
					InvocationTargetException {


		if (file == null) {
			throw new IllegalArgumentException("file is null");
		}

		if (file.exists() == false) {
			throw new FileNotFoundException(file.getAbsolutePath() + " is not exist");
		}

		if (file.isDirectory()) {
			throw new IllegalArgumentException(file.getAbsolutePath() + " is directory");
		}

		if (argTypes == null) {
			throw new IllegalArgumentException("argTypes is null");
		}

		if (args == null) {
			throw new IllegalArgumentException("args is null");
		}

		if (argTypes.length != args.length) {
			throw new IllegalArgumentException("length of argsType and args is not equal");
		}

		if (file.getName().endsWith(".groovy") == false) {
			throw new IllegalArgumentException(file.getAbsolutePath() + " is of invalid type");
		}


		ClassLoader classLoader = ExternalScriptsManager.class.getClassLoader();
		try(GroovyClassLoader groovyLoader = new GroovyClassLoader(classLoader)) {
			Class<?> groovyClass = groovyLoader.parseClass(file);
			Constructor<?> constructor = groovyClass.getConstructor(argTypes);
			return (GroovyObject) constructor.newInstance(args);
		}




	}
	
	private File[] scanPlugins(String pluginsPath){
		try{
			File file = new File(pluginsPath);
			if(!file.exists()){
				LogManager.getLogger().debug(MODULE, "External Plugins directory: " + pluginsPath + " not present");
				return null;
			}
			if(file.isDirectory() == false){
				LogManager.getLogger().debug(MODULE, "External Plugins directory: " + pluginsPath + " not present");
				return null;
			}
			return file.listFiles();
		}catch(SecurityException ex){
			LogManager.getLogger().debug(MODULE, "External Plugins directory: " + pluginsPath + " not accessible");
			LogManager.ignoreTrace(ex);
			return null;
		}
	}
	
	private List<ScriptPlugin> loadExternalPlugins(File[] externalPlugins,PluginContext context){
		List<ScriptPlugin> scriptPlugins = new ArrayList<ScriptPlugin>();
		if(externalPlugins != null){
			List<File> groovyPluginsList = new ArrayList<File>();
			for(File file: externalPlugins){
				if(file.isFile()){
					if(file.getName().endsWith(".groovy")){
						groovyPluginsList.add(file);
					}else{
						LogManager.getLogger().debug(MODULE, "Unknown Plugin type: " + file.getName() + ". Cannot be loaded");
					}
				}
			}
			
			scriptPlugins.addAll(loadGroovyPlugins(groovyPluginsList,context));
		}
		return scriptPlugins;
	}
	
	private List<ScriptPlugin> loadGroovyPlugins(List<File> files,PluginContext pluginContext){
		List<ScriptPlugin> groovyPluigins = new ArrayList<ScriptPlugin>();
		for(File file: files){

			GroovyObject obj = createGroovyObject(file, pluginContext);
			if (obj instanceof ScriptPlugin)
				groovyPluigins.add((ScriptPlugin)obj);
			else{
				LogManager.getLogger().debug(MODULE, "Plugin super class unknown, will not be loaded. Extend \"RadiusScriptPlugin\" for Radius plugin or extend \"DiameterScriptPlugin\" for Diameter plugin");
			}

		}
		return groovyPluigins;
	}

	public GroovyObject createGroovyObject(File file, 
			PluginContext pluginContext) {
		GroovyObject obj = null;
		try{
			Class<?> groovyClass = groovyLoader.parseClass(file);
			Constructor<?> groovyConst = groovyClass.getConstructor(PluginContext.class);
			obj = (GroovyObject) groovyConst.newInstance(new Object[]{pluginContext});
		} catch (CompilationFailedException | SecurityException | NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException | IOException ex) {
			LogManager.getLogger().trace(MODULE, ex);
			LogManager.getLogger().debug(MODULE, "Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
		}catch(Exception ex){
			LogManager.getLogger().trace(MODULE, ex);
			LogManager.getLogger().debug(MODULE, "Unknown Error in loading external Plugin:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
		}
		return obj;
	}
	
	
	private void loadGroovyFiles(Map<String, List<File>> filemap){
		ClassLoader classLoader = getClass().getClassLoader();
		try(GroovyClassLoader groovyClassLoader = new GroovyClassLoader(classLoader)) {
			ScriptContext scriptContext = createScriptContext();
			Set<Entry<String, List<File>>> filedetail = filemap.entrySet();
			for (Entry<String, List<File>> entry : filedetail) {
				for (File file : entry.getValue()) {
					loadGroovyObjectFromFile(groovyClassLoader, scriptContext, entry, file);
				}
			}
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error while loading groovy scripts. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}


	}

	private void loadGroovyObjectFromFile(GroovyClassLoader groovyClassLoader, ScriptContext scriptContext, Entry<String, List<File>> entry, File file) {
		try {
            Class<?> groovyClass = groovyClassLoader.parseClass(file);

            Constructor<?> constructor = groovyClass.getConstructor(new Class<?>[]{ScriptContext.class});
            GroovyObject groovyScriptObj = (GroovyObject) constructor.newInstance(new Object[]{scriptContext});
            scriptsMap.put(entry.getKey(), groovyScriptObj);
            if (LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
                LogManager.getLogger().info(MODULE, "Groovy: " + file + " loaded successfully.");
            }
        } catch (InvocationTargetException | IOException | NoSuchMethodException | ExceptionInInitializerError | IllegalAccessException | CompilationFailedException | InstantiationException ex) {
            LogManager.getLogger().trace(MODULE, ex);
            LogManager.getLogger().debug(MODULE, "Error in loading external Script:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
        } catch (Exception ex) {
            LogManager.getLogger().trace(MODULE, ex);
            LogManager.getLogger().debug(MODULE, "Unknwown Error in loading external Script:" + file.getAbsolutePath() + " Reason: " + ex.getMessage());
        }
	}


	//creating the script context
		private ScriptContext createScriptContext(){
			return () -> ExternalScriptsManager.this.serverContext;
		}

		/**
		 * This method allows the users of the external scripting framework to call the script directly with the required args.
		 * 
		 * @param scriptName the name of the script to be executed
		 * @param scriptType the type of the script
		 * @param methodName the name of the method to be executed
		 * @param argTypes the type of the arguements 
		 * @param args the actual arguments
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
						throw new IllegalArgumentException("Method with name : " + methodName + " not found in script : " + scriptName);
					} catch (IllegalArgumentException e) {
						throw new IllegalArgumentException("Method not found with signature : (" + StringUtility.getDelimiterSeparatedString(argTypes, ",") + ")", e);
					} catch (IllegalAccessException e) {
						throw new IllegalArgumentException("Method access is not available for method: " + methodName + " in script: " + scriptName + ". Make sure method is public.", e);
					} catch (InvocationTargetException e) {
						throw new IllegalArgumentException("Exception in method: " + methodName + " in script: " + scriptName + ". Reason : " + e.getMessage(), e);
					}
				}else{
					throw new IllegalArgumentException("Script with name: " + scriptName + " is not of type - " + scriptType.getName() + ". Make sure it extends or implements this type.");
				}
			}else{
				throw new IllegalArgumentException("Script with name: " + scriptName + " not found. Make sure the script path is correct and the script is compiled successfully.");
			}
		}

		public <T> T getScript(String scriptName, Class<T> scriptType) {
			if (scriptName == null || scriptType == null) {
				throw new IllegalArgumentException("scriptName or scriptType is null");
			}

			GroovyObject groovyObject = scriptsMap.get(scriptName);
			if (groovyObject != null) {
				if (scriptType.isInstance(groovyObject)) {
					return scriptType.cast(groovyObject);
				} else {
					throw new IllegalArgumentException("Script with name: " + scriptName + " is not of type - " + scriptType.getName() + ". Make sure it extends or implements this type.");
				}
			} else {
				throw new IllegalArgumentException("Script with name: " + scriptName + " not found. Make sure the script path is correct and the script is compiled successfully.");
			}
		}

}
