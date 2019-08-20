package com.elitecore.aaa.diameter.conf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Strings;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.core.commons.plugins.data.ServiceTypeConstants;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.Application;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;

@XmlType(propOrder = {})
@XmlRootElement(name = "tgpp-application")
@ConfigurationProperties(moduleName = "TGPP-SVR-CONFIGURABLE", synchronizeKey = "3GPP_AAA_SERVER", readWith = XMLReader.class, reloadWith = XMLReader.class)
@XMLProperties(name = "tgpp-server", configDirectories = {"conf", "diameter"}, schemaDirectories = {"system", "schema"})
public class DiameterTGPPServerConfigurable extends DiameterServiceConfigurable {

	private DiameterStackConfigurable stackConfigurable;
	private ApplicationEnum[] supportedApplications;

	@Override
	@PostRead
	public void postReadProcessing() {
		super.postReadProcessing();
		stackConfigurable = getConfigurationContext().get(DiameterStackConfigurable.class);
		postReadApplicationEnums();
	}
	
	@Override
	public int getAsyncMaxPoolSize() {
		return stackConfigurable.getMaxThreadPoolSize();
	}

	@Override
	public int getAsyncCorePoolSize() {
		return stackConfigurable.getMinThreadPoolSize();
	}
	
	
	private void postReadApplicationEnums() {

		if (Strings.isNullOrBlank(getStrApplicationId())) {
			//FIXME what todo
			return; 
		} 
		
		List<ApplicationEnum> applications = new ArrayList<ApplicationEnum>();

		String[] strApplications = Strings.splitter(',').trimTokens().splitToArray(getStrApplicationId());

		for (String strApplication : strApplications) {

			long applicationId ;
			long vendorId;

			String[] tokens = Strings.splitter(':').trimTokens().splitToArray(strApplication);

			try {
				if (tokens.length == 2) {
					vendorId = Long.parseLong(tokens[0]);
					applicationId = Long.parseLong(tokens[1]);

				} else {
					vendorId = ApplicationIdentifier.BASE.getVendorId();
					applicationId = Long.parseLong(tokens[0]);
				}
				applications.add(DiameterUtility.createApplicationEnumLeniently(applicationId, vendorId, ServiceTypes.BOTH, Application.UNKNOWN));
			} catch (NumberFormatException e) {
				//FIXME what todo
			}
		}
		if (applications.isEmpty()) {
			// FIXME need some action
			return;
		}
		supportedApplications = applications.toArray(new ApplicationEnum[]{}); 
	}

	@XmlTransient
	public ApplicationEnum[] getSupportedApplications() {
		return supportedApplications;
	}

	@Override
	public ServiceTypeConstants getServiceType() {
		return ServiceTypeConstants.DIA_3GPP;
	}
	
}
