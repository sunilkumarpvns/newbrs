package com.elitecore.commons.kpi.mib.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling TEST-MIB in standard metadata mode.
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

/**
 * The class is used for representing "TEST-MIB".
 * You can edit the file if you want to modify the behavior of the MIB.
 */
@SuppressWarnings("serial")
public class TEST_MIB extends SnmpMib implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public TEST_MIB() {
        mibName = "TEST_MIB";
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

        // Initialization of the "Kpitesting" group.
        // To disable support of this group, redefine the 
        // "createKpitestingMetaNode()" factory method, and make it return "null"
        //
        initKpitesting(server);

        isInitialized = true;
    }


    // ------------------------------------------------------------
    // 
    // Initialization of the "Kpitesting" group.
    // 
    // ------------------------------------------------------------


    /**
     * Initialization of the "Kpitesting" group.
     * 
     * To disable support of this group, redefine the 
     * "createKpitestingMetaNode()" factory method, and make it return "null"
     * 
     * @param server    MBeanServer for this group (may be null)
     * 
     **/
    protected void initKpitesting(MBeanServer server) 
        throws Exception {
        final String oid = getGroupOid("Kpitesting", "1.3.6.1.4.1.21067.1");
        ObjectName objname = null;
        if (server != null) {
            objname = getGroupObjectName("Kpitesting", oid, mibName + ":name=kpi.mib.autogen.Kpitesting");
        }
        final KpitestingMeta meta = createKpitestingMetaNode("Kpitesting", oid, objname, server);
        if (meta != null) {
            meta.registerTableNodes( this, server );

            // Note that when using standard metadata,
            // the returned object must implement the "KpitestingMBean"
            // interface.
            //
            final KpitestingMBean group = (KpitestingMBean) createKpitestingMBean("Kpitesting", oid, objname, server);
            meta.setInstance( group );
            registerGroupNode("Kpitesting", oid, objname, meta, group, server);
        }
    }


    /**
     * Factory method for "Kpitesting" group metadata class.
     * 
     * You can redefine this method if you need to replace the default
     * generated metadata class with your own customized class.
     * 
     * @param groupName Name of the group ("Kpitesting")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the metadata class generated for the
     *         "Kpitesting" group (KpitestingMeta)
     * 
     **/
    protected KpitestingMeta createKpitestingMetaNode(String groupName,
                String groupOid, ObjectName groupObjname, MBeanServer server)  {
        return new KpitestingMeta(this, objectserver);
    }


    /**
     * Factory method for "Kpitesting" group MBean.
     * 
     * You can redefine this method if you need to replace the default
     * generated MBean class with your own customized class.
     * 
     * @param groupName Name of the group ("Kpitesting")
     * @param groupOid  OID of this group
     * @param groupObjname ObjectName for this group (may be null)
     * @param server    MBeanServer for this group (may be null)
     * 
     * @return An instance of the MBean class generated for the
     *         "Kpitesting" group (Kpitesting)
     * 
     * Note that when using standard metadata,
     * the returned object must implement the "KpitestingMBean"
     * interface.
     **/
    protected Object createKpitestingMBean(String groupName,
                String groupOid,  ObjectName groupObjname, MBeanServer server)  {

        // Note that when using standard metadata,
        // the returned object must implement the "KpitestingMBean"
        // interface.
        //
        if (server != null) 
            return new Kpitesting(this,server);
        else 
            return new Kpitesting(this);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "registerTableMeta" method defined in "SnmpMib".
    // See the "SnmpMib" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("rawtypes")
	protected final Hashtable metadatas = new Hashtable();
}