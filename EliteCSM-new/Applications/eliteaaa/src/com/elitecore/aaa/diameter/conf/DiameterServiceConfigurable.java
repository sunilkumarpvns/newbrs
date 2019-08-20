package com.elitecore.aaa.diameter.conf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.conf.impl.ApplicationLoggerDetail;
import com.elitecore.aaa.diameter.conf.impl.DiameterPluginsDetail;
import com.elitecore.aaa.diameter.conf.impl.InPluginsDetail;
import com.elitecore.aaa.diameter.conf.impl.OutPluginsDetail;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.plugins.data.PluginMode;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;

public abstract class DiameterServiceConfigurable extends Configurable{

	private ApplicationLoggerDetail logger;
	private String strApplicationId="0:1";
	private DiameterPluginsDetail diameterPluginsDetail;
	private boolean enabled;
	private List<PluginEntryDetail> inPlugins ;
	private List<PluginEntryDetail> outPlugins ;
	private String logLocation;
	
	private List<String> servicePolicies;
	public static final int ASYNC_POOL_UNUSED = 0;
	
	public DiameterServiceConfigurable() {
		this.inPlugins = new ArrayList<PluginEntryDetail>();
		this.outPlugins =  new ArrayList<PluginEntryDetail>();
		this.diameterPluginsDetail =new DiameterPluginsDetail();
		this.logger = new ApplicationLoggerDetail();
		this.servicePolicies = new ArrayList<String>();
	}
	
	@XmlElementWrapper(name="service-policies")
	@XmlElement(name="service-policy",type=String.class)
	public List<String> getServicePolicies() {
		return servicePolicies;
	}

	public void setServicePolicies(List<String> servicePolicies) {
		this.servicePolicies = servicePolicies;
	}

	@XmlTransient
	public String getLogLocation() {
		return logLocation;
	}

	public void setLogLocation(String logLocation) {
		this.logLocation = logLocation;
	}

	@Reloadable(type=ApplicationLoggerDetail.class)
	@XmlElement(name = "logging")
	public ApplicationLoggerDetail getLogger() {
		return logger;
	}

	public void setLogger(ApplicationLoggerDetail logger) {
		this.logger = logger;
	}

	@XmlElement(name="application-id",type=String.class,defaultValue="0:1")
	public String getStrApplicationId() {
		return strApplicationId;
	}

	public void setStrApplicationId(String applicationId) {
		this.strApplicationId = applicationId;
	}

	@XmlElement(name ="plugin-list")
	public DiameterPluginsDetail getDiameterPluginsDetail() {
		return diameterPluginsDetail;
	}

	public void setDiameterPluginsDetail(DiameterPluginsDetail diameterPluginsDetail) {
		this.diameterPluginsDetail = diameterPluginsDetail;
	}

	@XmlTransient
	public boolean getIsEnabled() {
		return enabled;
	}

	public void setIsEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@XmlTransient
	public List<PluginEntryDetail> getInPlugins() {
		return inPlugins;
	}

	@XmlTransient
	public List<PluginEntryDetail> getOutPlugins() {
		return outPlugins;
	}
	
	public void postReadProcessing() {
		
		if(getDiameterPluginsDetail()!=null) {
			
			InPluginsDetail inPluginObj = getDiameterPluginsDetail().getInPlugins();
			List<PluginEntryDetail> inPluginList = inPluginObj.getInPlugins();
			
			if(Collectionz.isNullOrEmpty(inPluginList) == false){
				setPluginsCallerId(inPluginList, PluginMode.IN);
				((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(inPluginList);
				this.inPlugins = inPluginList;
			}	
			
			OutPluginsDetail outPluginObj = getDiameterPluginsDetail().getOutPlugins();
			List<PluginEntryDetail> outPluginList = outPluginObj.getOutPlugins(); 

			if(Collectionz.isNullOrEmpty(outPluginList) == false){
				setPluginsCallerId(outPluginList, PluginMode.OUT);
				((AAAServerContext)((AAAConfigurationContext)getConfigurationContext()).getServerContext()).registerPlugins(outPluginList);
				this.outPlugins = outPluginList;
			}
		}
		
		List<String> tempServicePolicies = new ArrayList<String>();
		if(this.servicePolicies!=null){
			int numOfPolicies = servicePolicies.size();
			String policyName = null;
			for(int i=0;i<numOfPolicies;i++){
				policyName = servicePolicies.get(i);
				if(policyName!=null && policyName.trim().length()>0){
					tempServicePolicies.add(policyName);
				}
			}
		}
		this.servicePolicies = tempServicePolicies;
	}
	
	private void setPluginsCallerId(List<PluginEntryDetail> plugins, PluginMode mode) {
		for (int index = 0; index < plugins.size(); index++) {
			PluginEntryDetail data = plugins.get(index);
			PluginCallerIdentity key = PluginCallerIdentity.createAndGetIdentity(getServiceType(), mode, index, data.getPluginName()).getId();
			data.setCallerId(key);
		}
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostReload
	public void postReloadProcessing(){
		
	}

	public abstract int getAsyncMaxPoolSize();
	public abstract int getAsyncCorePoolSize();
	public abstract ServiceTypeConstants getServiceType();
}
