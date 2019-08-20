package com.elitecore.core.serverx.snmp.mib.jvm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling JVM-MANAGEMENT-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "JvmRTInputArgsEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.42.2.145.3.163.1.1.4.20.1.
 */
public class JvmRTInputArgsEntry implements JvmRTInputArgsEntryMBean, Serializable {

    /**
     * Variable for storing the value of "JvmRTInputArgsItem".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.4.20.1.2".
     *
     * "An input argument at index jvmRTInputArgsIndex, as in the array
     * returned by RuntimeMXBean.getInputArguments().
     * 
     * Note that the SNMP agent may have to truncate the string returned
     * by the underlying API if it does not fit in the JvmArgValueTC
     * (1023 bytes max).
     * 
     * See java.lang.management.RuntimeMXBean.getInputArguments()
     * "
     *
     */
    protected Byte[] JvmRTInputArgsItem = { new Byte("74"), new Byte("68"), new Byte("77"), new Byte("75")};

    /**
     * Variable for storing the value of "JvmRTInputArgsIndex".
     * The variable is identified by: "1.3.6.1.4.1.42.2.145.3.163.1.1.4.20.1.1".
     *
     * "The index of the input argument, as in the array returned
     * by RuntimeMXBean.getInputArguments().
     * 
     * See java.lang.management.RuntimeMXBean.getInputArguments()
     * "
     *
     */
    protected Integer JvmRTInputArgsIndex = new Integer(1);


    /**
     * Constructor for the "JvmRTInputArgsEntry" group.
     */
    public JvmRTInputArgsEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "JvmRTInputArgsItem" variable.
     */
    public Byte[] getJvmRTInputArgsItem() throws SnmpStatusException {
        return JvmRTInputArgsItem;
    }

    /**
     * Getter for the "JvmRTInputArgsIndex" variable.
     */
    public Integer getJvmRTInputArgsIndex() throws SnmpStatusException {
        return JvmRTInputArgsIndex;
    }

}