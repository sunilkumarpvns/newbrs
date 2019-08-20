package com.elitecore.netvertex.core.conf.impl;

import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.util.url.InvalidURLException;
import com.elitecore.core.util.url.URLData;
import com.elitecore.core.util.url.URLParser;
import com.elitecore.corenetvertex.constants.PolicyEnforcementMethod;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayData;
import com.elitecore.netvertex.gateway.radius.conf.impl.RadiusGatewayConfigurationImpl;
import com.elitecore.netvertex.gateway.radius.conf.impl.RadiusGatewayProfileConfigurationImpl;

import java.net.InetAddress;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RadiusGatewayFactory {

	private static final String MODULE = "RAD-GW-FCTRY";
	private RadiusGatewayProfileFactory radiusGatewayProfileFactory;

	public RadiusGatewayFactory(RadiusGatewayProfileFactory radiusGatewayProfileFactory) {
		this.radiusGatewayProfileFactory = radiusGatewayProfileFactory;
	}
	
	public RadiusGatewayConfigurationImpl create(RadiusGatewayData radiusGatewayData) throws LoadConfigurationException {
		
		String description = radiusGatewayData.getDescription();
		if(description == null){
			description = " ";
		}
		
		String ipAddress;
		int port = 3799;
		int localPort = 0;
		
		String connectionURL = radiusGatewayData.getConnectionURL();
		if(connectionURL != null) {
			String tempIPAddress;
			try{
				URLData urlData = URLParser.parse(connectionURL);
				tempIPAddress = urlData.getHost();
				port = urlData.getPort() == URLParser.UNKNOWN_PORT ? port : urlData.getPort();
			} catch (InvalidURLException iEx){
				throw new LoadConfigurationException("Error while parsing URL: " + connectionURL + ". Reason: Invalid connection URL", iEx);
			} 
			
			try{
				ipAddress = InetAddress.getByName(tempIPAddress).getHostAddress();
			} catch (Exception e) {
				getLogger().error(MODULE, "Setting URL: " + tempIPAddress + " without resolving. Reason: Error while resolving connection URL");
				getLogger().trace(MODULE, e);	
				ipAddress = tempIPAddress;
			}
			
		} else {
			throw new LoadConfigurationException("Error while parsing URL. Reason: connection URL not configured");
		}
		
		PolicyEnforcementMethod enforcementMethod = PolicyEnforcementMethod.fromName(radiusGatewayData.getPolicyEnforcementMethod());
		if(enforcementMethod == null || enforcementMethod.isRadiusMethod() == false){
			
			getLogger().warn(MODULE, "Considering " + PolicyEnforcementMethod.ACCESS_ACCEPT + " as Policy Enforcement Method"+ 
			" for Radius Gateway: " + connectionURL + ". Reason: configured Invalid Policy Enforcement Method: " 
					+ radiusGatewayData.getPolicyEnforcementMethod());
			enforcementMethod = PolicyEnforcementMethod.ACCESS_ACCEPT;
		}

		if (radiusGatewayData.getMinLocalPort() != null) {
			localPort = radiusGatewayData.getMinLocalPort().intValue();
		}

		RadiusGatewayProfileConfigurationImpl radiusGatewayProfileConf = radiusGatewayProfileFactory.create(radiusGatewayData.getRadiusGatewayProfileData());

		if (radiusGatewayProfileConf == null) {
			throw new LoadConfigurationException("Error while reading RADIUS gateway configuration. Reason : gateway profile not configured");
		}

		return new RadiusGatewayConfigurationImpl(radiusGatewayData.getName(), radiusGatewayData.getId(),null
				, description, connectionURL, radiusGatewayData.getSharedSecret(), ipAddress, port, localPort
				, enforcementMethod, radiusGatewayProfileConf);
	}
}
