package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;
import java.util.Hashtable;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibTable;
import com.sun.management.snmp.agent.SnmpStandardObjectServer;
// jmx imports
//
// jdmk imports
//

/**
 * The class is used for representing "DIAMETER-STACK-MIB".
 * You can edit the file if you want to modify the behavior of the MIB.
 */
public class DIAMETER_STACK_MIB extends SnmpMib implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public DIAMETER_STACK_MIB() {
        mibName = "DIAMETER_STACK_MIB";
    }

    /**
     * Initialization of the MIB with no registration in Java DMK.
     */
    public void init() throws IllegalAccessException {
        // Allow only one initialization of the MIB.
        //
        if (isInitialized == true) {
            return ;
        }

        try  {
            populate(null, null);
        } catch(IllegalAccessException x)  {
            throw x;
        } catch(RuntimeException x)  {
            throw x;
        } catch(Exception x)  {
            throw new Error(x.getMessage());
        }

        isInitialized = true;
    }

    /**
     * Initialization of the MIB with AUTOMATIC REGISTRATION in Java DMK.
     */
    public ObjectName preRegister(MBeanServer server, ObjectName name)
            throws Exception {
        // Allow only one initialization of the MIB.
        //
        if (isInitialized == true) {
            throw new InstanceAlreadyExistsException();
        }

        // Initialize MBeanServer information.
        //
        this.server = server;

        populate(server, name);

        isInitialized = true;
        return name;
    }

    /**
     * Initialization of the MIB with no registration in Java DMK.
     */
    public void populate(MBeanServer server, ObjectName name) 
        throws Exception {
        // Allow only one initialization of the MIB.
        //
        if (isInitialized == true) {
            return ;
        }

        if (objectserver == null) 
            objectserver = new SnmpStandardObjectServer();

        // Initialization of the "DiameterStack" group.
        // To disable support of this group, redefine the 
        // "createDiameterStackMetaNode()" factory method, and make it return "null"
        //
        initDiameterStack(server);

        // Initialization of the "PeerStatistics" group.
        // To disable support of this group, redefine the 
        // "createPeerStatisticsMetaNode()" factory method, and make it return "null"
        //
        initPeerStatistics(server);

        // Initialization of the "ApplicationWiseStatistics" group.
        // To disable support of this group, redefine the 
        // "createApplicationWiseStatisticsMetaNode()" factory method, and make it return "null"
        //
        initApplicationWiseStatistics(server);

        // Initialization of the "ResultCodeWiseStatistics" group.
        // To disable support of this group, redefine the 
        // "createResultCodeWiseStatisticsMetaNode()" factory method, and make it return "null"
        //
        initResultCodeWiseStatistics(server);

        // Initialization of the "CommandCodeWiseStatistics" group.
        // To disable support of this group, redefine the 
        // "createCommandCodeWiseStatisticsMetaNode()" factory method, and make it return "null"
        //
        initCommandCodeWiseStatistics(server);

        // Initialization of the "ConnectionStatistics" group.
        // To disable support of this group, redefine the 
        // "createConnectionStatisticsMetaNode()" factory method, and make it return "null"
        //
        initConnectionStatistics(server);

        // Initialization of the "AppStatistics" group.
        // To disable support of this group, redefine the 
        // "createAppStatisticsMetaNode()" factory method, and make it return "null"
        //
        initAppStatistics(server);

        // Initialization of the "StackStatistics" group.
        // To disable support of this group, redefine the 
        // "createStackStatisticsMetaNode()" factory method, and make it return "null"
        //
        initStackStatistics(server);

        isInitialized = true;
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "DiameterStack" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "DiameterStack" group.
     * 
     * To disable support of this group, redefine the 
     * "createDiameterStackMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initDiameterStack(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("DiameterStack", "1.3.6.1.4.1.21067.5");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("DiameterStack", oid, mibName + ":name=com.elitecore.diameterapi.mibs.custom.autogen.DiameterStack");
        }
        final DiameterStackMeta meta = createDiameterStackMetaNode("DiameterStack", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "DiameterStackMBean"
            // interface.
            //
            final DiameterStackMBean group = (DiameterStackMBean) createDiameterStackMBean("DiameterStack", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("DiameterStack", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "DiameterStack" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("DiameterStack")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "DiameterStack" group (DiameterStackMeta)
     * 
     **/
    protected DiameterStackMeta createDiameterStackMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new DiameterStackMeta(this, objectserver);
    }


    /**
     * Factory method for "DiameterStack" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("DiameterStack")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "DiameterStack" group (DiameterStack)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "DiameterStackMBean"
     * interface.
     **/
    protected Object createDiameterStackMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "DiameterStackMBean"
        // interface.
        //
        if (server != null) 
            return new DiameterStack(this,server);
        else 
            return new DiameterStack(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "PeerStatistics" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "PeerStatistics" group.
     * 
     * To disable support of this group, redefine the 
     * "createPeerStatisticsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initPeerStatistics(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("PeerStatistics", "1.3.6.1.4.1.21067.5.102");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("PeerStatistics", oid, mibName + "_DiameterStack:name=com.elitecore.diameterapi.mibs.custom.autogen.PeerStatistics");
        }
        final PeerStatisticsMeta meta = createPeerStatisticsMetaNode("PeerStatistics", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "PeerStatisticsMBean"
            // interface.
            //
            final PeerStatisticsMBean group = (PeerStatisticsMBean) createPeerStatisticsMBean("PeerStatistics", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("PeerStatistics", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "PeerStatistics" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("PeerStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "PeerStatistics" group (PeerStatisticsMeta)
     * 
     **/
    protected PeerStatisticsMeta createPeerStatisticsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new PeerStatisticsMeta(this, objectserver);
    }


    /**
     * Factory method for "PeerStatistics" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("PeerStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "PeerStatistics" group (PeerStatistics)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "PeerStatisticsMBean"
     * interface.
     **/
    protected Object createPeerStatisticsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "PeerStatisticsMBean"
        // interface.
        //
        if (server != null) 
            return new PeerStatistics(this,server);
        else 
            return new PeerStatistics(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "ApplicationWiseStatistics" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "ApplicationWiseStatistics" group.
     * 
     * To disable support of this group, redefine the 
     * "createApplicationWiseStatisticsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initApplicationWiseStatistics(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("ApplicationWiseStatistics", "1.3.6.1.4.1.21067.5.102.3");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("ApplicationWiseStatistics", oid, mibName + "_DiameterStack_PeerStatistics:name=com.elitecore.diameterapi.mibs.custom.autogen.ApplicationWiseStatistics");
        }
        final ApplicationWiseStatisticsMeta meta = createApplicationWiseStatisticsMetaNode("ApplicationWiseStatistics", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "ApplicationWiseStatisticsMBean"
            // interface.
            //
            final ApplicationWiseStatisticsMBean group = (ApplicationWiseStatisticsMBean) createApplicationWiseStatisticsMBean("ApplicationWiseStatistics", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("ApplicationWiseStatistics", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "ApplicationWiseStatistics" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("ApplicationWiseStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "ApplicationWiseStatistics" group (ApplicationWiseStatisticsMeta)
     * 
     **/
    protected ApplicationWiseStatisticsMeta createApplicationWiseStatisticsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new ApplicationWiseStatisticsMeta(this, objectserver);
    }


    /**
     * Factory method for "ApplicationWiseStatistics" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("ApplicationWiseStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "ApplicationWiseStatistics" group (ApplicationWiseStatistics)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "ApplicationWiseStatisticsMBean"
     * interface.
     **/
    protected Object createApplicationWiseStatisticsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "ApplicationWiseStatisticsMBean"
        // interface.
        //
        if (server != null) 
            return new ApplicationWiseStatistics(this,server);
        else 
            return new ApplicationWiseStatistics(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "ResultCodeWiseStatistics" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "ResultCodeWiseStatistics" group.
     * 
     * To disable support of this group, redefine the 
     * "createResultCodeWiseStatisticsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initResultCodeWiseStatistics(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("ResultCodeWiseStatistics", "1.3.6.1.4.1.21067.5.102.3.3");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("ResultCodeWiseStatistics", oid, mibName + "_DiameterStack_PeerStatistics_ApplicationWiseStatistics:name=com.elitecore.diameterapi.mibs.custom.autogen.ResultCodeWiseStatistics");
        }
        final ResultCodeWiseStatisticsMeta meta = createResultCodeWiseStatisticsMetaNode("ResultCodeWiseStatistics", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "ResultCodeWiseStatisticsMBean"
            // interface.
            //
            final ResultCodeWiseStatisticsMBean group = (ResultCodeWiseStatisticsMBean) createResultCodeWiseStatisticsMBean("ResultCodeWiseStatistics", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("ResultCodeWiseStatistics", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "ResultCodeWiseStatistics" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("ResultCodeWiseStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "ResultCodeWiseStatistics" group (ResultCodeWiseStatisticsMeta)
     * 
     **/
    protected ResultCodeWiseStatisticsMeta createResultCodeWiseStatisticsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new ResultCodeWiseStatisticsMeta(this, objectserver);
    }


    /**
     * Factory method for "ResultCodeWiseStatistics" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("ResultCodeWiseStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "ResultCodeWiseStatistics" group (ResultCodeWiseStatistics)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "ResultCodeWiseStatisticsMBean"
     * interface.
     **/
    protected Object createResultCodeWiseStatisticsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "ResultCodeWiseStatisticsMBean"
        // interface.
        //
        if (server != null) 
            return new ResultCodeWiseStatistics(this,server);
        else 
            return new ResultCodeWiseStatistics(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "CommandCodeWiseStatistics" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "CommandCodeWiseStatistics" group.
     * 
     * To disable support of this group, redefine the 
     * "createCommandCodeWiseStatisticsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initCommandCodeWiseStatistics(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("CommandCodeWiseStatistics", "1.3.6.1.4.1.21067.5.102.3.2");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("CommandCodeWiseStatistics", oid, mibName + "_DiameterStack_PeerStatistics_ApplicationWiseStatistics:name=com.elitecore.diameterapi.mibs.custom.autogen.CommandCodeWiseStatistics");
        }
        final CommandCodeWiseStatisticsMeta meta = createCommandCodeWiseStatisticsMetaNode("CommandCodeWiseStatistics", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "CommandCodeWiseStatisticsMBean"
            // interface.
            //
            final CommandCodeWiseStatisticsMBean group = (CommandCodeWiseStatisticsMBean) createCommandCodeWiseStatisticsMBean("CommandCodeWiseStatistics", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("CommandCodeWiseStatistics", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "CommandCodeWiseStatistics" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("CommandCodeWiseStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "CommandCodeWiseStatistics" group (CommandCodeWiseStatisticsMeta)
     * 
     **/
    protected CommandCodeWiseStatisticsMeta createCommandCodeWiseStatisticsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new CommandCodeWiseStatisticsMeta(this, objectserver);
    }


    /**
     * Factory method for "CommandCodeWiseStatistics" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("CommandCodeWiseStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "CommandCodeWiseStatistics" group (CommandCodeWiseStatistics)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "CommandCodeWiseStatisticsMBean"
     * interface.
     **/
    protected Object createCommandCodeWiseStatisticsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "CommandCodeWiseStatisticsMBean"
        // interface.
        //
        if (server != null) 
            return new CommandCodeWiseStatistics(this,server);
        else 
            return new CommandCodeWiseStatistics(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "ConnectionStatistics" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "ConnectionStatistics" group.
     * 
     * To disable support of this group, redefine the 
     * "createConnectionStatisticsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initConnectionStatistics(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("ConnectionStatistics", "1.3.6.1.4.1.21067.5.102.2");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("ConnectionStatistics", oid, mibName + "_DiameterStack_PeerStatistics:name=com.elitecore.diameterapi.mibs.custom.autogen.ConnectionStatistics");
        }
        final ConnectionStatisticsMeta meta = createConnectionStatisticsMetaNode("ConnectionStatistics", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "ConnectionStatisticsMBean"
            // interface.
            //
            final ConnectionStatisticsMBean group = (ConnectionStatisticsMBean) createConnectionStatisticsMBean("ConnectionStatistics", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("ConnectionStatistics", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "ConnectionStatistics" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("ConnectionStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "ConnectionStatistics" group (ConnectionStatisticsMeta)
     * 
     **/
    protected ConnectionStatisticsMeta createConnectionStatisticsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new ConnectionStatisticsMeta(this, objectserver);
    }


    /**
     * Factory method for "ConnectionStatistics" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("ConnectionStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "ConnectionStatistics" group (ConnectionStatistics)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "ConnectionStatisticsMBean"
     * interface.
     **/
    protected Object createConnectionStatisticsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "ConnectionStatisticsMBean"
        // interface.
        //
        if (server != null) 
            return new ConnectionStatistics(this,server);
        else 
            return new ConnectionStatistics(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "AppStatistics" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "AppStatistics" group.
     * 
     * To disable support of this group, redefine the 
     * "createAppStatisticsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initAppStatistics(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("AppStatistics", "1.3.6.1.4.1.21067.5.101");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("AppStatistics", oid, mibName + "_DiameterStack:name=com.elitecore.diameterapi.mibs.custom.autogen.AppStatistics");
        }
        final AppStatisticsMeta meta = createAppStatisticsMetaNode("AppStatistics", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "AppStatisticsMBean"
            // interface.
            //
            final AppStatisticsMBean group = (AppStatisticsMBean) createAppStatisticsMBean("AppStatistics", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("AppStatistics", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "AppStatistics" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("AppStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "AppStatistics" group (AppStatisticsMeta)
     * 
     **/
    protected AppStatisticsMeta createAppStatisticsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new AppStatisticsMeta(this, objectserver);
    }


    /**
     * Factory method for "AppStatistics" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("AppStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "AppStatistics" group (AppStatistics)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "AppStatisticsMBean"
     * interface.
     **/
    protected Object createAppStatisticsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "AppStatisticsMBean"
        // interface.
        //
        if (server != null) 
            return new AppStatistics(this,server);
        else 
            return new AppStatistics(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "StackStatistics" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "StackStatistics" group.
     * 
     * To disable support of this group, redefine the 
     * "createStackStatisticsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initStackStatistics(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("StackStatistics", "1.3.6.1.4.1.21067.5.100");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("StackStatistics", oid, mibName + "_DiameterStack:name=com.elitecore.diameterapi.mibs.custom.autogen.StackStatistics");
        }
        final StackStatisticsMeta meta = createStackStatisticsMetaNode("StackStatistics", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "StackStatisticsMBean"
            // interface.
            //
            final StackStatisticsMBean group = (StackStatisticsMBean) createStackStatisticsMBean("StackStatistics", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("StackStatistics", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "StackStatistics" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("StackStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "StackStatistics" group (StackStatisticsMeta)
     * 
     **/
    protected StackStatisticsMeta createStackStatisticsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new StackStatisticsMeta(this, objectserver);
    }


    /**
     * Factory method for "StackStatistics" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("StackStatistics")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "StackStatistics" group (StackStatistics)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "StackStatisticsMBean"
     * interface.
     **/
    protected Object createStackStatisticsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "StackStatisticsMBean"
        // interface.
        //
        if (server != null) 
            return new StackStatistics(this,server);
        else 
            return new StackStatistics(this);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "registerTableMeta" method defined in "SnmpMib".
    // See the "SnmpMib" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void registerTableMeta( String name, SnmpMibTable meta) {
        if (metadatas == null) return;
        if (name == null) return;
        metadatas.put(name,meta);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "getRegisteredTableMeta" method defined in "SnmpMib".
    // See the "SnmpMib" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public SnmpMibTable getRegisteredTableMeta( String name ) {
        if (metadatas == null) return null;
        if (name == null) return null;
        return (SnmpMibTable) metadatas.get(name);
    }

    public SnmpStandardObjectServer getStandardObjectServer() {
        if (objectserver == null) 
            objectserver = new SnmpStandardObjectServer();
        return objectserver;
    }

    private boolean isInitialized = false;

    protected SnmpStandardObjectServer objectserver;

    protected final Hashtable metadatas = new Hashtable();
}