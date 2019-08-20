package com.elitecore.netvertex.core.conf;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;
import com.elitecore.netvertex.core.conf.impl.NetvertexServerInstanceConfigurationImpl;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterStackConfiguration;
import com.elitecore.netvertex.gateway.file.FileGatewayConfiguration;
import com.elitecore.netvertex.gateway.radius.conf.RadiusListenerConfiguration;
import com.elitecore.netvertex.service.notification.conf.impl.NotificationServiceConfigurationImpl;
import com.elitecore.netvertex.service.offlinernc.conf.OfflineRnCServiceConfiguration;
import com.elitecore.netvertex.service.pcrf.conf.PCRFServiceConfiguration;

import java.util.List;

/**
 * Created by harsh on 6/20/16.
 */
public class NetvertexServerGroupInstanceConfiguration implements ToStringable{

    private String id;
    private final NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration;
    private final DiameterStackConfiguration diameterListenerConf;
    private final RadiusListenerConfiguration radiusListnerConf;
    private final PCRFServiceConfiguration pcrfServiceConfiguration;

    private final NotificationServiceConfigurationImpl notificationServiceConfiguration;
    private boolean isPrimaryServer;
	private OfflineRnCServiceConfiguration offlineRnCServiceConfiguration;
	private List<FileGatewayConfiguration> listOfFileGatewayConfiguration;

    public NetvertexServerGroupInstanceConfiguration(String id, NetvertexServerInstanceConfigurationImpl netvertexServerInstanceConfiguration,
                                                     DiameterStackConfiguration diameterListenerConf,
                                                     RadiusListenerConfiguration radiusListnerConf,
                                                     PCRFServiceConfiguration pcrfServiceConfiguration,
                                                     NotificationServiceConfigurationImpl notificationServiceConfiguration, OfflineRnCServiceConfiguration offlineRnCServiceConfiguration,
                                                     List<FileGatewayConfiguration> listlistOfFileGatewayConfiguration,
                                                     boolean isPrimaryServer) {
        this.id = id;
        this.netvertexServerInstanceConfiguration = netvertexServerInstanceConfiguration;
        this.diameterListenerConf = diameterListenerConf;
        this.radiusListnerConf = radiusListnerConf;
        this.pcrfServiceConfiguration = pcrfServiceConfiguration;
        this.notificationServiceConfiguration = notificationServiceConfiguration;
        this.offlineRnCServiceConfiguration = offlineRnCServiceConfiguration;
		this.listOfFileGatewayConfiguration=listlistOfFileGatewayConfiguration;
        this.isPrimaryServer = isPrimaryServer;
    }

    public NetvertexServerInstanceConfigurationImpl getNetvertexServerInstanceConfiguration() {
        return netvertexServerInstanceConfiguration;
    }

    public boolean isPrimaryServer() {
        return isPrimaryServer;
    }

    public DiameterStackConfiguration getDiameterListenerConf() {
        return diameterListenerConf;
    }

    public RadiusListenerConfiguration getRadiusListnerConf() {
        return radiusListnerConf;
    }

    public PCRFServiceConfiguration getPcrfServiceConfiguration() {
        return pcrfServiceConfiguration;
    }

    public String getId() {
        return id;
    }
    
    public OfflineRnCServiceConfiguration getOfflineRnCServiceConfiguration() {
    	return offlineRnCServiceConfiguration;
    }

    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- NetVertex server instance -- ");
        toString(builder);
        return builder.toString();
    }

    public NotificationServiceConfigurationImpl getNotificationServiceConfiguration() {
        return notificationServiceConfiguration;
    }

    public List<FileGatewayConfiguration> getListOfFileGatewayConfiguration() {
		return listOfFileGatewayConfiguration;
	}

	@Override
    public void toString(IndentingToStringBuilder builder) {
        builder.append("Id", id);
        builder.append("Is Primary Instance", isPrimaryServer);
        builder.appendChildObject("Server Instance Configuration", netvertexServerInstanceConfiguration);
        builder.appendChildObject("Diameter Listener Configuration", diameterListenerConf);
        builder.appendChildObject("Radius Listener Configuration", radiusListnerConf);
        builder.appendChildObject("Notification Service Configuration", notificationServiceConfiguration);
        builder.appendField("File Location Configuration");
        for (FileGatewayConfiguration fileGatewayConfiguration : listOfFileGatewayConfiguration) {
        	builder.appendValue(fileGatewayConfiguration);
        }
        builder.appendChildObject("Offline RnC Service Configuration", offlineRnCServiceConfiguration);
        builder.appendChildObject("PCRF Service Configuration", pcrfServiceConfiguration);
    }
}
