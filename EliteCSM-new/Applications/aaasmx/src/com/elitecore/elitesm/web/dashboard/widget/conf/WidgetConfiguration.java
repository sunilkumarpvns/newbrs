package com.elitecore.elitesm.web.dashboard.widget.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.elitesm.web.dashboard.widget.constants.WidgetTypeConstants;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.WidgetDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.cpuusage.CpuUsageDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.dynaauthclient.RadiusDynaAuthClientDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.dynaauthserver.RadiusDynaAuthServerDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.jvmdetailmemory.JVMDetailMemoryDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.memoryusgae.MemoryUsageDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.nasesistatistics.NasESIStatDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.radacctclient.RadiusAcctClientDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.radacctesistatistics.RadAcctESIStatDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.radacctserv.RadiusAcctServDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.radauthclient.RadiusAuthClientDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.radauthesistatistics.RadAuthESIStatDataProvider;
import com.elitecore.elitesm.web.dashboard.widget.dataprovider.radauthserv.RadiusAuthServDataProvider;

public class WidgetConfiguration {
	private Map<String, WidgetDataProvider> widgetDataProviderMap;
	private List<String> widgetTypes;
	private static int WIDGET_LISTENER_INTERVAL = 10;
	private static int WIDGET_ALERT_COUNT = 3;

	public WidgetConfiguration() {
		widgetDataProviderMap = new HashMap<String, WidgetDataProvider>();
		widgetTypes = new ArrayList<String>();
	}
	
	public void init() {
		// TODO Read from Widget Configuration
		/*widgetTypes.add(WidgetTypeConstants.ESI.name());
		widgetTypes.add(WidgetTypeConstants.PEER.name());*/
		
		widgetTypes.add(WidgetTypeConstants.MEMUSAGE.name());
		widgetTypes.add(WidgetTypeConstants.RAD_AUTH_ESI_STATISTICS.name());
		//widgetTypes.add(WidgetTypeConstants.REPLYMESSAGESTATSUMMARY.name());
		widgetTypes.add(WidgetTypeConstants.RAD_AUTH_CLIENT.name());
		widgetTypes.add(WidgetTypeConstants.RAD_AUTH_SERV.name());
		widgetTypes.add(WidgetTypeConstants.RAD_ACCT_SERV.name());
		widgetTypes.add(WidgetTypeConstants.RAD_ACCT_CLIENT.name());
		widgetTypes.add(WidgetTypeConstants.JVM_DETAIL_MEMUSAGE.name());
		widgetTypes.add(WidgetTypeConstants.CPU_USAGE.name());
		widgetTypes.add(WidgetTypeConstants.RAD_DYNA_AUTH_CLIENT.name());
		widgetTypes.add(WidgetTypeConstants.RAD_DYNA_AUTH_SERVER.name());
		widgetTypes.add(WidgetTypeConstants.RAD_ACCT_ESI_STATISTICS.name());
		widgetTypes.add(WidgetTypeConstants.NAS_ESI_STATISTICS.name());
		
		
		/*widgetDataProviderMap.put(WidgetTypeConstants.ESI.name(), new ESIWidgetDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.PEER.name(), new PeerWidgetDataProvider());*/
		widgetDataProviderMap.put(WidgetTypeConstants.MEMUSAGE.name(), new MemoryUsageDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.RAD_AUTH_ESI_STATISTICS.name(), new RadAuthESIStatDataProvider());
		//widgetDataProviderMap.put(WidgetTypeConstants.REPLYMESSAGESTATSUMMARY.name(), new ReplyMessageStatSummaryDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.RAD_AUTH_CLIENT.name(), new RadiusAuthClientDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.RAD_AUTH_SERV.name(), new RadiusAuthServDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.RAD_ACCT_CLIENT.name(), new RadiusAcctClientDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.RAD_ACCT_SERV.name(), new RadiusAcctServDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.JVM_DETAIL_MEMUSAGE.name(), new JVMDetailMemoryDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.CPU_USAGE.name(), new CpuUsageDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.RAD_DYNA_AUTH_CLIENT.name(),new RadiusDynaAuthClientDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.RAD_DYNA_AUTH_SERVER.name(),new RadiusDynaAuthServerDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.RAD_ACCT_ESI_STATISTICS.name(), new RadAcctESIStatDataProvider());
		widgetDataProviderMap.put(WidgetTypeConstants.NAS_ESI_STATISTICS.name(), new NasESIStatDataProvider());
	}
	
	public WidgetDataProvider getWidgetDataProvider(String widgetType) {
		return widgetDataProviderMap.get(widgetType);
	}
	
	public List<String> getWidgetTypes() {
		return widgetTypes;
	}
	
	public int getInterval() {
		return WIDGET_LISTENER_INTERVAL;
	}
	
	public static int getAlertCount() {
		return WIDGET_ALERT_COUNT;
	}
	
	
}
