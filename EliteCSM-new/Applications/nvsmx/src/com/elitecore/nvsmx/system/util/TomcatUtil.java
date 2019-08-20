package com.elitecore.nvsmx.system.util;

import com.elitecore.commons.base.Collectionz;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by aditya on 5/22/17.
 */
public class TomcatUtil {


    public static String getContextPort() throws MalformedObjectNameException {

        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> httpConnectorObjects = platformMBeanServer.queryNames(new ObjectName("*:type=Connector,*"),
                Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));

        if(Collectionz.isNullOrEmpty(httpConnectorObjects)){
            return null;
        }
        Iterator<ObjectName> httConnectorIterator = httpConnectorObjects.iterator();
        if(httConnectorIterator.hasNext() == false ){
            return null;
        }
        return  httConnectorIterator.next().getKeyProperty("port");

    }
}
