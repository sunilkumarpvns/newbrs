package com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IP-POOL-SERVICE-CLIENT-MIB in standard metadata mode.
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
 * The class is used for representing "IP-POOL-SERVICE-CLIENT-MIB".
 * You can edit the file if you want to modify the behavior of the MIB.
 */
public class IP_POOL_SERVICE_CLIENT_MIB extends SnmpMib implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public IP_POOL_SERVICE_CLIENT_MIB() {
        mibName = "IP_POOL_SERVICE_CLIENT_MIB";
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

        // Initialization of the "IpPoolClientMIBObjects" group.
        // To disable support of this group, redefine the 
        // "createIpPoolClientMIBObjectsMetaNode()" factory method, and make it return "null"
        //
        initIpPoolClientMIBObjects(server);

        isInitialized = true;
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "IpPoolClientMIBObjects" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "IpPoolClientMIBObjects" group.
     * 
     * To disable support of this group, redefine the 
     * "createIpPoolClientMIBObjectsMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initIpPoolClientMIBObjects(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("IpPoolClientMIBObjects", "1.3.6.1.4.1.21067.1.7.2");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("IpPoolClientMIBObjects", oid, mibName + ":name=com.elitecore.aaa.mibs.rm.ippool.client.snmp.autogen.IpPoolClientMIBObjects");
        }
        final IpPoolClientMIBObjectsMeta meta = createIpPoolClientMIBObjectsMetaNode("IpPoolClientMIBObjects", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "IpPoolClientMIBObjectsMBean"
            // interface.
            //
            final IpPoolClientMIBObjectsMBean group = (IpPoolClientMIBObjectsMBean) createIpPoolClientMIBObjectsMBean("IpPoolClientMIBObjects", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("IpPoolClientMIBObjects", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "IpPoolClientMIBObjects" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("IpPoolClientMIBObjects")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "IpPoolClientMIBObjects" group (IpPoolClientMIBObjectsMeta)
     * 
     **/
    protected IpPoolClientMIBObjectsMeta createIpPoolClientMIBObjectsMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new IpPoolClientMIBObjectsMeta(this, objectserver);
    }


    /**
     * Factory method for "IpPoolClientMIBObjects" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("IpPoolClientMIBObjects")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "IpPoolClientMIBObjects" group (IpPoolClientMIBObjects)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "IpPoolClientMIBObjectsMBean"
     * interface.
     **/
    protected Object createIpPoolClientMIBObjectsMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "IpPoolClientMIBObjectsMBean"
        // interface.
        //
        if (server != null) 
            return new IpPoolClientMIBObjects(this,server);
        else 
            return new IpPoolClientMIBObjects(this);
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