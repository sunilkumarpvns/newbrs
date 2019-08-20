package com.elitecore.elitesm.web.plugins;

import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.quotamgrplugin.data.QuotaMgtPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.TransactionLoggerData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.UniversalPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.usrstatpostauthplugin.data.UserStatPostAuthPluginData;

public class CreatePluginConfig {
	
	private PluginInstData pluginInstData;
	private UniversalPluginData universalPluginData;
	private GroovyPluginData groovyPluginData;
	private TransactionLoggerData transactionLoggerData;
	private QuotaMgtPluginData quotaMgtPluginData;
	private UserStatPostAuthPluginData userStatPostAuthPluginData;

	
	public PluginInstData getPluginInstData() {
		return pluginInstData;
	}
	public void setPluginInstData(PluginInstData pluginInstData) {
		this.pluginInstData = pluginInstData;
	}
	public UniversalPluginData getUniversalPluginData() {
		return universalPluginData;
	}
	public void setUniversalPluginData(UniversalPluginData universalPluginData) {
		this.universalPluginData = universalPluginData;
	}
	public GroovyPluginData getGroovyPluginData() {
		return groovyPluginData;
	}
	public void setGroovyPluginData(GroovyPluginData groovyPluginData) {
		this.groovyPluginData = groovyPluginData;
	}
	public TransactionLoggerData getTransactionLoggerData() {
		return transactionLoggerData;
	}
	public void setTransactionLoggerData(TransactionLoggerData transactionLoggerData) {
		this.transactionLoggerData = transactionLoggerData;
	}
	public QuotaMgtPluginData getQuotaMgtPluginData() {
		return quotaMgtPluginData;
	}
	public void setQuotaMgtPluginData(QuotaMgtPluginData quotaMgtPluginData) {
		this.quotaMgtPluginData = quotaMgtPluginData;
	}
	public UserStatPostAuthPluginData getUserStatPostAuthPluginData() {
		return userStatPostAuthPluginData;
	}
	public void setUserStatPostAuthPluginData(
			UserStatPostAuthPluginData userStatPostAuthPluginData) {
		this.userStatPostAuthPluginData = userStatPostAuthPluginData;
	}
		
}
