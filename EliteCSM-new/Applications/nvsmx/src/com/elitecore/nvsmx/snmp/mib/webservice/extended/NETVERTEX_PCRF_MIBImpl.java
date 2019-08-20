package com.elitecore.nvsmx.snmp.mib.webservice.extended;

import javax.annotation.Nonnull;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.NETVERTEX_PCRF_MIB;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.resetws.ResetUsageWSStatisticsProvider;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.sessionws.SessionManagementWSStatisticsProvider;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws.SubscriberProvisioningWSStatisticsProvider;
import com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws.SubscriptionWSStatisticsProvider;

public class NETVERTEX_PCRF_MIBImpl extends NETVERTEX_PCRF_MIB {

	private static final long serialVersionUID = 1L;
	
	@Nonnull private final NetvertexWebServiceStatisticsProvider 		netvertexWebServiceStatisticsProvider;
	@Nonnull private final ResetUsageWSStatisticsProvider 				resetUsageWSStatisticsProvider;
	@Nonnull private final SessionManagementWSStatisticsProvider 		sessionManagementWSStatisticsProvider;
	@Nonnull private final SubscriberProvisioningWSStatisticsProvider 	subscriberProvisioningWSStatisticsProvider;
	@Nonnull private final SubscriptionWSStatisticsProvider 			subscriptionWSStatisticsProvider;
	
	public NETVERTEX_PCRF_MIBImpl( 	NetvertexWebServiceStatisticsProvider 		netvertexWebServiceStatisticsMBean,
									ResetUsageWSStatisticsProvider 			resetUsageWSStatisticsMBean,
									SessionManagementWSStatisticsProvider 		sessionManagementWSStatisticsMBean,
									SubscriberProvisioningWSStatisticsProvider subscriberProvisioningWSStatisticsMBean,
									SubscriptionWSStatisticsProvider 			subscriptionWSStatisticsMBean ) {
		
		this.netvertexWebServiceStatisticsProvider 	 =  netvertexWebServiceStatisticsMBean;
		this.resetUsageWSStatisticsProvider 			 = resetUsageWSStatisticsMBean;
		this.sessionManagementWSStatisticsProvider 	 = sessionManagementWSStatisticsMBean;
		this.subscriptionWSStatisticsProvider 			 = subscriptionWSStatisticsMBean;
		this.subscriberProvisioningWSStatisticsProvider = subscriberProvisioningWSStatisticsMBean;
		 
	}
 
	@Override
	protected Object createNetvertexWebServiceStatisticsMBean(String groupName,
            String groupOid,  ObjectName groupObjname, MBeanServer server)  {
		return netvertexWebServiceStatisticsProvider;
	}
	
	@Override
	protected Object createSubscriberProvisioningWSStatisticsMBean(String groupName,
            String groupOid,  ObjectName groupObjname, MBeanServer server)  {
		return subscriberProvisioningWSStatisticsProvider;
	}

	@Override
	protected Object createSubscriptionWSStatisticsMBean(String groupName,
             String groupOid,  ObjectName groupObjname, MBeanServer server)  {
		return subscriptionWSStatisticsProvider;
	}

	@Override
	protected Object createResetUsageWSStatisticsMBean(String groupName,
              String groupOid,  ObjectName groupObjname, MBeanServer server)  {
	   	return resetUsageWSStatisticsProvider;
	}

	@Override
	protected Object createSessionManagementWSStatisticsMBean(String groupName,
              String groupOid,  ObjectName groupObjname, MBeanServer server)  {
		return sessionManagementWSStatisticsProvider;
	}
	
	@Override
	protected ObjectName getGroupObjectName(String name, String oid,
			String defaultName) throws MalformedObjectNameException {
		return new ObjectName(MBeanConstants.STATISTICS_PROTOCOL_PCRF + "auth,data=" + name);
	}
}