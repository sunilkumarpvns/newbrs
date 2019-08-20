package com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data;


import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.validator.ValidObject;
import com.elitecore.elitesm.ws.rest.validator.Validator;

@XmlType(propOrder={"inPlugins","outPlugins"})
@ValidObject
public class DiameterPluginsDetailData implements Validator {
	
	private InPluginsDetailData inPlugins;
	private OutPluginsDetailData outPlugins;
	
	public DiameterPluginsDetailData() {
		this.inPlugins = new InPluginsDetailData();
		this.outPlugins  = new OutPluginsDetailData();
	}

	@XmlElement(name = "in-plugins")
	@Valid
	public InPluginsDetailData getInPlugins() {
		return inPlugins;
	}

	public void setInPlugins(InPluginsDetailData inPlugins) {
		this.inPlugins = inPlugins;
	}

	@XmlElement(name = "out-plugins")
	@Valid
	public OutPluginsDetailData getOutPlugins() {
		return outPlugins;
	}

	public void setOutPlugins(OutPluginsDetailData outPlugins) {
		this.outPlugins = outPlugins;
	}

	@Override
	public boolean validate(ConstraintValidatorContext context) {
		boolean isValid = true;
		PluginBLManager pluginBLManager = new PluginBLManager();
        try {
            List<PluginInstData> pluginInstDataList = pluginBLManager.getPluginInstanceDataList("DIAMETER-STACK");
            List<String> pluginList = new ArrayList<String>();
            for (PluginInstData pluginData : pluginInstDataList) {
                pluginList.add(pluginData.getName());
            }
            
            // plugin entry detail
            List<PluginEntryDetailData> inPluginList = inPlugins.getInPluginList();
            
            for (PluginEntryDetailData pluginDetail : inPluginList) {
            	String pluginName = pluginDetail.getPluginName();
            	if(Strings.isNullOrBlank(pluginName) == false){
            		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(pluginName) == false){
            			if (pluginList.contains(pluginDetail.getPluginName()) == false) {
            				isValid = false;
            				RestUtitlity.setValidationMessage(context, "Configured " + pluginDetail.getPluginName()+ " In Plugin does not exist or not type of Diameter plugin.");
            				return isValid;
            			}
            		}else{
            			pluginDetail.setPluginName("");
            		}
            	}
            }
            
            List<PluginEntryDetailData> outPluginList = outPlugins.getOutPluginList();
            for (PluginEntryDetailData pluginDetail : outPluginList) {
            	String pluginName = pluginDetail.getPluginName();
            	if(Strings.isNullOrBlank(pluginName) == false){
            		if(RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(pluginName) == false){
            			if (pluginList.contains(pluginName) == false) {
            				isValid = false;
            				RestUtitlity.setValidationMessage(context, "Configured " + pluginDetail.getPluginName()+ " Out Plugin does not exist or not type of Diameter plugin.");
            				return isValid;
            			}
            		}else{
            			pluginDetail.setPluginName("");
            		}
            	}
            }
            
        } catch (DataManagerException e) {
            e.printStackTrace();
        }
		return isValid;

	}

}
