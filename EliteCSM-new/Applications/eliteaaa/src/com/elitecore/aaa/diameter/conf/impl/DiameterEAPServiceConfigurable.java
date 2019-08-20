
package com.elitecore.aaa.diameter.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.diameter.conf.DiameterServiceConfigurable;
import com.elitecore.aaa.util.constants.AAAServerConstants;
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
@XmlRootElement(name = "eap-application")
@ConfigurationProperties(moduleName ="EAP_APPLICATION_CONFIGURABLE",readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey = AAAServerConstants.DIA_EAP_SERVICE_ID)
@XMLProperties(name = "eap-application", schemaDirectories = {"system","schema"}, configDirectories = {"conf","diameter"})
public class DiameterEAPServiceConfigurable extends DiameterServiceConfigurable{
	
	
	private ApplicationEnum supportedApplication;
	
	public DiameterEAPServiceConfigurable() {
	}
	
	@Override
	public String toString () {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		out.println();
		out.println();
		out.println(" -- Diameter EAP Service Configuration -- ");
		out.println();
		out.println("    Application Id = " + supportedApplication);
		out.println("    Application Enabled = " +getIsEnabled());
		out.println("    Logging:");
		
		ApplicationLoggerDetail logger = getLogger();
		
		out.println("      Application Logger Enabled  = " +getLogger());
		out.println("      Level = " + logger.getLogLevel());
		out.println("      Rolling Type = " + logger.getLogRollingType());
		out.println("      Rolling Unit = " + logger.getLogRollingUnit());
		out.println("      Compress Rolled Unit = " + logger.getIsbCompressRolledUnit());
		out.println("      Max Rolled Unit = " + logger.getLogMaxRolledUnits());
		out.println("      Log Location = " + getLogLocation());
		out.println();
		out.println("	Plugin Details: ");
		out.println("			In-Plugin List : " + getInPlugins());
		out.println("			Out-Plugin List: " + getOutPlugins());
		out.println();
		out.close();

		return stringBuffer.toString();
	}

	@Override
	public int getAsyncMaxPoolSize() {
		return DiameterServiceConfigurable.ASYNC_POOL_UNUSED;
	}

	@Override
	public int getAsyncCorePoolSize() {
		return DiameterServiceConfigurable.ASYNC_POOL_UNUSED;
	}

	@Override
	@PostRead
	public void postReadProcessing() {
		super.postReadProcessing();
		
		postReadApplicationEnums();
		
	}
	
	private void postReadApplicationEnums() {

		if (Strings.isNullOrBlank(getStrApplicationId())) {
			supportedApplication = ApplicationIdentifier.EAP;  
		} else {

			long applicationId ;
			long vendorId;

			String[] tokens = Strings.splitter(':').trimTokens().splitToArray(getStrApplicationId());

			try {
				if (tokens.length == 2) {
					vendorId = Long.parseLong(tokens[0]);
					applicationId = Long.parseLong(tokens[1]);

				} else {
					vendorId = ApplicationIdentifier.BASE.getVendorId();
					applicationId = Long.parseLong(tokens[0]);
				}
				supportedApplication = DiameterUtility.createApplicationEnumLeniently(applicationId, vendorId, ServiceTypes.AUTH, Application.EAP);
			} catch (NumberFormatException e) {
				supportedApplication = ApplicationIdentifier.EAP;
			}
		}

	}

	@XmlTransient
	public ApplicationEnum getSupportedApplication() {
		return supportedApplication;
	}

	@Override
	public ServiceTypeConstants getServiceType() {
		return ServiceTypeConstants.DIA_EAP;
	}
}

