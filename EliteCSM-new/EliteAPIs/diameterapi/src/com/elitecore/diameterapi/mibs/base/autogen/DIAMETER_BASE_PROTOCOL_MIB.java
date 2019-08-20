package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB in standard metadata mode.
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
 * The class is used for representing "DIAMETER-BASE-PROTOCOL-MIB".
 * You can edit the file if you want to modify the behavior of the MIB.
 */
public class DIAMETER_BASE_PROTOCOL_MIB extends SnmpMib implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public DIAMETER_BASE_PROTOCOL_MIB() {
        mibName = "DIAMETER_BASE_PROTOCOL_MIB";
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

        // Initialization of the "DbpRealmStats" group.
        // To disable support of this group, redefine the 
        // "createDbpRealmStatsMetaNode()" factory method, and make it return "null"
        //
        initDbpRealmStats(server);

        // Initialization of the "DbpRealmCfgs" group.
        // To disable support of this group, redefine the 
        // "createDbpRealmCfgsMetaNode()" factory method, and make it return "null"
        //
        initDbpRealmCfgs(server);

        // Initialization of the "DbpPeerInfo" group.
        // To disable support of this group, redefine the 
        // "createDbpPeerInfoMetaNode()" factory method, and make it return "null"
        //
        initDbpPeerInfo(server);

        // Initialization of the "DbpPeerCfgs" group.
        // To disable support of this group, redefine the 
        // "createDbpPeerCfgsMetaNode()" factory method, and make it return "null"
        //
        initDbpPeerCfgs(server);

        // Initialization of the "EliteDSC" group.
        // To disable support of this group, redefine the 
        // "createEliteDSCMetaNode()" factory method, and make it return "null"
        //
        initEliteDSC(server);

        // Initialization of the "DbpLocalStats" group.
        // To disable support of this group, redefine the 
        // "createDbpLocalStatsMetaNode()" factory method, and make it return "null"
        //
        initDbpLocalStats(server);

        // Initialization of the "DbpLocalCfgs" group.
        // To disable support of this group, redefine the 
        // "createDbpLocalCfgsMetaNode()" factory method, and make it return "null"
        //
        initDbpLocalCfgs(server);

        // Initialization of the "DbpNotifCfgs" group.
        // To disable support of this group, redefine the 
        // "createDbpNotifCfgsMetaNode()" factory method, and make it return "null"
        //
        initDbpNotifCfgs(server);

        isInitialized = true;
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "DbpRealmStats" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "DbpRealmStats" group.
     * 
     * To disable support of this group, redefine the 
     * "createDbpRealmStatsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initDbpRealmStats(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("DbpRealmStats", "1.3.6.1.2.1.119.1.6");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("DbpRealmStats", oid, mibName + ":name=com.elitecore.diameterapi.mibs.base.autogen.DbpRealmStats");
        }
        final DbpRealmStatsMeta meta = createDbpRealmStatsMetaNode("DbpRealmStats", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "DbpRealmStatsMBean"
            // interface.
            //
            final DbpRealmStatsMBean group = (DbpRealmStatsMBean) createDbpRealmStatsMBean("DbpRealmStats", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("DbpRealmStats", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "DbpRealmStats" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpRealmStats")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "DbpRealmStats" group (DbpRealmStatsMeta)
     * 
     **/
    protected DbpRealmStatsMeta createDbpRealmStatsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new DbpRealmStatsMeta(this, objectserver);
    }


    /**
     * Factory method for "DbpRealmStats" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpRealmStats")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "DbpRealmStats" group (DbpRealmStats)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "DbpRealmStatsMBean"
     * interface.
     **/
    protected Object createDbpRealmStatsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "DbpRealmStatsMBean"
        // interface.
        //
        if (server != null) 
            return new DbpRealmStats(this,server);
        else 
            return new DbpRealmStats(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "DbpRealmCfgs" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "DbpRealmCfgs" group.
     * 
     * To disable support of this group, redefine the 
     * "createDbpRealmCfgsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initDbpRealmCfgs(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("DbpRealmCfgs", "1.3.6.1.2.1.119.1.5");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("DbpRealmCfgs", oid, mibName + ":name=com.elitecore.diameterapi.mibs.base.autogen.DbpRealmCfgs");
        }
        final DbpRealmCfgsMeta meta = createDbpRealmCfgsMetaNode("DbpRealmCfgs", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "DbpRealmCfgsMBean"
            // interface.
            //
            final DbpRealmCfgsMBean group = (DbpRealmCfgsMBean) createDbpRealmCfgsMBean("DbpRealmCfgs", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("DbpRealmCfgs", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "DbpRealmCfgs" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpRealmCfgs")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "DbpRealmCfgs" group (DbpRealmCfgsMeta)
     * 
     **/
    protected DbpRealmCfgsMeta createDbpRealmCfgsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new DbpRealmCfgsMeta(this, objectserver);
    }


    /**
     * Factory method for "DbpRealmCfgs" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpRealmCfgs")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "DbpRealmCfgs" group (DbpRealmCfgs)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "DbpRealmCfgsMBean"
     * interface.
     **/
    protected Object createDbpRealmCfgsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "DbpRealmCfgsMBean"
        // interface.
        //
        if (server != null) 
            return new DbpRealmCfgs(this,server);
        else 
            return new DbpRealmCfgs(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "DbpPeerInfo" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "DbpPeerInfo" group.
     * 
     * To disable support of this group, redefine the 
     * "createDbpPeerInfoMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initDbpPeerInfo(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("DbpPeerInfo", "1.3.6.1.2.1.119.1.4.5");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("DbpPeerInfo", oid, mibName + ":name=com.elitecore.diameterapi.mibs.base.autogen.DbpPeerInfo");
        }
        final DbpPeerInfoMeta meta = createDbpPeerInfoMetaNode("DbpPeerInfo", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "DbpPeerInfoMBean"
            // interface.
            //
            final DbpPeerInfoMBean group = (DbpPeerInfoMBean) createDbpPeerInfoMBean("DbpPeerInfo", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("DbpPeerInfo", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "DbpPeerInfo" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpPeerInfo")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "DbpPeerInfo" group (DbpPeerInfoMeta)
     * 
     **/
    protected DbpPeerInfoMeta createDbpPeerInfoMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new DbpPeerInfoMeta(this, objectserver);
    }


    /**
     * Factory method for "DbpPeerInfo" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpPeerInfo")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "DbpPeerInfo" group (DbpPeerInfo)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "DbpPeerInfoMBean"
     * interface.
     **/
    protected Object createDbpPeerInfoMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "DbpPeerInfoMBean"
        // interface.
        //
        if (server != null) 
            return new DbpPeerInfo(this,server);
        else 
            return new DbpPeerInfo(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "DbpPeerCfgs" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "DbpPeerCfgs" group.
     * 
     * To disable support of this group, redefine the 
     * "createDbpPeerCfgsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initDbpPeerCfgs(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("DbpPeerCfgs", "1.3.6.1.2.1.119.1.3");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("DbpPeerCfgs", oid, mibName + ":name=com.elitecore.diameterapi.mibs.base.autogen.DbpPeerCfgs");
        }
        final DbpPeerCfgsMeta meta = createDbpPeerCfgsMetaNode("DbpPeerCfgs", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "DbpPeerCfgsMBean"
            // interface.
            //
            final DbpPeerCfgsMBean group = (DbpPeerCfgsMBean) createDbpPeerCfgsMBean("DbpPeerCfgs", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("DbpPeerCfgs", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "DbpPeerCfgs" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpPeerCfgs")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "DbpPeerCfgs" group (DbpPeerCfgsMeta)
     * 
     **/
    protected DbpPeerCfgsMeta createDbpPeerCfgsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new DbpPeerCfgsMeta(this, objectserver);
    }


    /**
     * Factory method for "DbpPeerCfgs" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpPeerCfgs")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "DbpPeerCfgs" group (DbpPeerCfgs)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "DbpPeerCfgsMBean"
     * interface.
     **/
    protected Object createDbpPeerCfgsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "DbpPeerCfgsMBean"
        // interface.
        //
        if (server != null) 
            return new DbpPeerCfgs(this,server);
        else 
            return new DbpPeerCfgs(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "EliteDSC" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "EliteDSC" group.
     * 
     * To disable support of this group, redefine the 
     * "createEliteDSCMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initEliteDSC(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("EliteDSC", "1.3.6.1.2.1.119.1.11");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("EliteDSC", oid, mibName + ":name=com.elitecore.diameterapi.mibs.base.autogen.EliteDSC");
        }
        final EliteDSCMeta meta = createEliteDSCMetaNode("EliteDSC", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "EliteDSCMBean"
            // interface.
            //
            final EliteDSCMBean group = (EliteDSCMBean) createEliteDSCMBean("EliteDSC", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("EliteDSC", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "EliteDSC" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("EliteDSC")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "EliteDSC" group (EliteDSCMeta)
     * 
     **/
    protected EliteDSCMeta createEliteDSCMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new EliteDSCMeta(this, objectserver);
    }


    /**
     * Factory method for "EliteDSC" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("EliteDSC")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "EliteDSC" group (EliteDSC)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "EliteDSCMBean"
     * interface.
     **/
    protected Object createEliteDSCMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "EliteDSCMBean"
        // interface.
        //
        if (server != null) 
            return new EliteDSC(this,server);
        else 
            return new EliteDSC(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "DbpLocalStats" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "DbpLocalStats" group.
     * 
     * To disable support of this group, redefine the 
     * "createDbpLocalStatsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initDbpLocalStats(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("DbpLocalStats", "1.3.6.1.2.1.119.1.2");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("DbpLocalStats", oid, mibName + ":name=com.elitecore.diameterapi.mibs.base.autogen.DbpLocalStats");
        }
        final DbpLocalStatsMeta meta = createDbpLocalStatsMetaNode("DbpLocalStats", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "DbpLocalStatsMBean"
            // interface.
            //
            final DbpLocalStatsMBean group = (DbpLocalStatsMBean) createDbpLocalStatsMBean("DbpLocalStats", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("DbpLocalStats", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "DbpLocalStats" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpLocalStats")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "DbpLocalStats" group (DbpLocalStatsMeta)
     * 
     **/
    protected DbpLocalStatsMeta createDbpLocalStatsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new DbpLocalStatsMeta(this, objectserver);
    }


    /**
     * Factory method for "DbpLocalStats" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpLocalStats")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "DbpLocalStats" group (DbpLocalStats)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "DbpLocalStatsMBean"
     * interface.
     **/
    protected Object createDbpLocalStatsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "DbpLocalStatsMBean"
        // interface.
        //
        if (server != null) 
            return new DbpLocalStats(this,server);
        else 
            return new DbpLocalStats(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "DbpLocalCfgs" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "DbpLocalCfgs" group.
     * 
     * To disable support of this group, redefine the 
     * "createDbpLocalCfgsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initDbpLocalCfgs(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("DbpLocalCfgs", "1.3.6.1.2.1.119.1.1");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("DbpLocalCfgs", oid, mibName + ":name=com.elitecore.diameterapi.mibs.base.autogen.DbpLocalCfgs");
        }
        final DbpLocalCfgsMeta meta = createDbpLocalCfgsMetaNode("DbpLocalCfgs", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "DbpLocalCfgsMBean"
            // interface.
            //
            final DbpLocalCfgsMBean group = (DbpLocalCfgsMBean) createDbpLocalCfgsMBean("DbpLocalCfgs", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("DbpLocalCfgs", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "DbpLocalCfgs" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpLocalCfgs")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "DbpLocalCfgs" group (DbpLocalCfgsMeta)
     * 
     **/
    protected DbpLocalCfgsMeta createDbpLocalCfgsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new DbpLocalCfgsMeta(this, objectserver);
    }


    /**
     * Factory method for "DbpLocalCfgs" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpLocalCfgs")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "DbpLocalCfgs" group (DbpLocalCfgs)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "DbpLocalCfgsMBean"
     * interface.
     **/
    protected Object createDbpLocalCfgsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "DbpLocalCfgsMBean"
        // interface.
        //
        if (server != null) 
            return new DbpLocalCfgs(this,server);
        else 
            return new DbpLocalCfgs(this);
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "DbpNotifCfgs" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "DbpNotifCfgs" group.
     * 
     * To disable support of this group, redefine the 
     * "createDbpNotifCfgsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initDbpNotifCfgs(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("DbpNotifCfgs", "1.3.6.1.2.1.119.1.7");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("DbpNotifCfgs", oid, mibName + ":name=com.elitecore.diameterapi.mibs.base.autogen.DbpNotifCfgs");
        }
        final DbpNotifCfgsMeta meta = createDbpNotifCfgsMetaNode("DbpNotifCfgs", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "DbpNotifCfgsMBean"
            // interface.
            //
            final DbpNotifCfgsMBean group = (DbpNotifCfgsMBean) createDbpNotifCfgsMBean("DbpNotifCfgs", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("DbpNotifCfgs", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "DbpNotifCfgs" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpNotifCfgs")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "DbpNotifCfgs" group (DbpNotifCfgsMeta)
     * 
     **/
    protected DbpNotifCfgsMeta createDbpNotifCfgsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new DbpNotifCfgsMeta(this, objectserver);
    }


    /**
     * Factory method for "DbpNotifCfgs" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("DbpNotifCfgs")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "DbpNotifCfgs" group (DbpNotifCfgs)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "DbpNotifCfgsMBean"
     * interface.
     **/
    protected Object createDbpNotifCfgsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "DbpNotifCfgsMBean"
        // interface.
        //
        if (server != null) 
            return new DbpNotifCfgs(this,server);
        else 
            return new DbpNotifCfgs(this);
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