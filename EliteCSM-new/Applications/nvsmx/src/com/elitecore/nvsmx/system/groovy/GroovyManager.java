package com.elitecore.nvsmx.system.groovy;

import static com.elitecore.commons.logging.LogManager.getLogger;
import groovy.lang.GroovyObject;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.nvsmx.system.ConfigurationProvider;

/**
 * <PRE>
 * GroovyManager reads scripts from 'DEPLOYMENT_PATH/scripts'. 
 * Currently keeps groovy for SubscriberProvisioningWS and SubscriptionWS 
 * 
 * @author Chetan.Sankhala
 */
public class GroovyManager {

	private static final String MODULE = "NVSMX-GROOVY-MGR";
	private List<SubscriberProvisioningWsScript> subscriberProvisioningGroovyScripts;
	private List<SubscriptionWsScript> subscriptionGroovyScripts;
	private List<ResetUsageWsScript> resetUsageGroovyScripts; 
	private List<SessionManagementWsScript> sessionManagementWsScripts;

	private static GroovyManager instance;

	private GroovyManager() {
		subscriberProvisioningGroovyScripts = Collectionz.newArrayList();
		subscriptionGroovyScripts = Collectionz.newArrayList();
		resetUsageGroovyScripts = Collectionz.newArrayList();
		sessionManagementWsScripts = Collectionz.newArrayList();
	}

	static {
		instance = new GroovyManager();
	}

	public static GroovyManager getInstance() {
		return instance;
	}

	public void initScripts() {

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Groovy Manager initialization started");
		}

		String scriptsFolderPath = ConfigurationProvider.getInstance().getDeploymentPath() + File.separator + "scripts";
		
		FileFilter fileFilter = new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.isFile() && file.canRead() && file.getName().endsWith(".groovy");
			}
		};

		for (WSNames wsName : WSNames.values()) {
			
			String groovyPath = scriptsFolderPath + File.separator + wsName.groovyFolderName;
			
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Initializing Groovy Object for " + wsName.name + " from " + groovyPath);
			}

			File scriptFolder = new File(groovyPath);
			
			if (scriptFolder.exists() == false) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Error while initializing groovy scripts for WS " + wsName.name + ". Reason: Directory("
							+ scriptFolder.getAbsolutePath() + ") does not exist");
				}
				continue;
			}

			if (scriptFolder.isDirectory() == false) {
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Error while initializing groovy scripts for WS " + wsName.name + ". Reason: " 
												+ scriptFolder.getAbsolutePath() + " not a directory");
				}
				continue;
			}

			File[] fileList = scriptFolder.listFiles(fileFilter);

			if (fileList.length == 0) {
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Groovy Object creation is skipped for " + wsName.name 
							+ ". Reason: Groovy not configured in " + wsName.groovyFolderName);
				}
				continue;
			}
			
			// File name wise sorting
			Arrays.sort(fileList);
			
			for (File goovyFile : fileList) {
				loadScripts(goovyFile, wsName);
			}
			
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Groovy object for " + wsName.name + " is initialized successfully");
			}
		}

		if (getLogger().isInfoLogLevel()) {
			getLogger().info(MODULE, "Groovy Manager initialization completed successfully");
		}
	}

	private void loadScripts(File goovyFile, WSNames groovyName) {
		try {

			GroovyObject groovyObject = ExternalScriptsManager.createGroovyObject(goovyFile, new Class<?>[] {}, new Object[] {});

			if (groovyObject == null) {
				getLogger().warn(MODULE, "Can't add Groovy Object for script file \"" + goovyFile.getName() + "\""
						+ ". Reason: groovy scripts object not created for script file");
				return;
			}

			if (WSNames.SUBSCRIBERPROVISIONING_WS == groovyName) {
				if (groovyObject instanceof SubscriberProvisioningWsScript) {
					SubscriberProvisioningWsScript provisioningGroovyScript = (SubscriberProvisioningWsScript) groovyObject;
					provisioningGroovyScript.init();
					this.subscriberProvisioningGroovyScripts.add(provisioningGroovyScript);
					
					if (getLogger().isInfoLogLevel()) {
						getLogger().info(MODULE, "Groovy: " + provisioningGroovyScript.getName() + " initialized successfully");
					}
					
				} else {
					getLogger().error(MODULE, "Can't add Groovy Object for script file \"" + goovyFile.getName() + "\""
							+ ". Reason: Invalid groovy script format");
				}
				
			} else if (WSNames.SUBSCRIPTION_WS == groovyName) {
				if (groovyObject instanceof SubscriptionWsScript) {
					SubscriptionWsScript subscriptionGroovyScript = (SubscriptionWsScript) groovyObject;
					subscriptionGroovyScript.init();
					this.subscriptionGroovyScripts.add(subscriptionGroovyScript);
					if (getLogger().isInfoLogLevel()) {
						getLogger().info(MODULE, "Groovy: " + subscriptionGroovyScript.getName() + " initialized successfully");
					}
				} else {
					getLogger().error(MODULE, "Can't add Groovy Object for script file \"" + goovyFile.getName()
							+ ". Reason: Invalid groovy script format");
				}
			}  else if (WSNames.RESET_USAGE_WS == groovyName) {
				if (groovyObject instanceof ResetUsageWsScript) {
					ResetUsageWsScript resetUsageWsScript = (ResetUsageWsScript) groovyObject;
					resetUsageWsScript.init();
					this.resetUsageGroovyScripts.add(resetUsageWsScript);
					if (getLogger().isInfoLogLevel()) {
						getLogger().info(MODULE, "Groovy: " + resetUsageWsScript.getName() + " initialized successfully");
					}
				} else {
					getLogger().error(MODULE, "Can't add Groovy Object for script file \"" + goovyFile.getName()
							+ ". Reason: Invalid groovy script format");
				}
			} else if (WSNames.SESSION_MANAGEMENT_WS == groovyName) {
				if (groovyObject instanceof SessionManagementWsScript) {
					SessionManagementWsScript sessionManagementWsScript = (SessionManagementWsScript) groovyObject;
					sessionManagementWsScript.init();
					this.sessionManagementWsScripts.add(sessionManagementWsScript);
					if (getLogger().isInfoLogLevel()) {
						getLogger().info(MODULE, "Groovy: " + sessionManagementWsScript.getName() + " initialized successfully");
					}
				} else {
					getLogger().error(MODULE, "Can't add Groovy Object for script file \"" + goovyFile.getName()
							+ ". Reason: Invalid groovy script format");
				}
			}

		} catch (Throwable ex) {
			getLogger().error(MODULE, "Can't add Groovy Object for script file \"" + goovyFile.getName()
					+ ". Reason: " + ex.getMessage());
			getLogger().trace(MODULE, ex);
		}
	}

	public enum WSNames {
		
		SUBSCRIBERPROVISIONING_WS("SubscriberProvisioningWS", "SubscriberProvisioningScripts"),
		SUBSCRIPTION_WS("SubscriptionWS", "SubscriptionScripts"),
		RESET_USAGE_WS("ResetUsageWS","ResetUsageScripts"),
		SESSION_MANAGEMENT_WS("SessionManagementWS", "SessionManagementScripts");
		
		public String groovyFolderName;
		public String name;
		private WSNames(String name, String folderName) {
			this.name = name;
			this.groovyFolderName = folderName;
		}
	}
	
	public List<SubscriberProvisioningWsScript> getSubscriberProvisioningGroovyScripts() {
		return subscriberProvisioningGroovyScripts;
	}

	public List<SubscriptionWsScript> getSubscriptionGroovyScripts() {
		return subscriptionGroovyScripts;
	}
	
	public List<ResetUsageWsScript> getResetUsageGroovyScripts(){
		return resetUsageGroovyScripts;
	}

	public List<SessionManagementWsScript> getSessionManagementWsScripts() {
		return sessionManagementWsScripts;
	}
}
