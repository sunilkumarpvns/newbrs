package com.elitecore.core.serverx.snmp.mib.os.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling UCD-SNMP-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import javax.management.MBeanServer;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "SystemStats" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.2021.11.
 */
public class SystemStats implements SystemStatsMBean, Serializable {

    /**
     * Variable for storing the value of "SsCpuRawUser".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.50".
     */
    protected Long SsCpuRawUser = new Long(1);

    /**
     * Variable for storing the value of "SsCpuRawGuestNice".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.66".
     */
    protected Long SsCpuRawGuestNice = new Long(1);

    /**
     * Variable for storing the value of "SsCpuRawGuest".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.65".
     */
    protected Long SsCpuRawGuest = new Long(1);

    /**
     * Variable for storing the value of "SsCpuRawSteal".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.64".
     */
    protected Long SsCpuRawSteal = new Long(1);

    /**
     * Variable for storing the value of "SsRawSwapOut".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.63".
     */
    protected Long SsRawSwapOut = new Long(1);

    /**
     * Variable for storing the value of "SsRawSwapIn".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.62".
     */
    protected Long SsRawSwapIn = new Long(1);

    /**
     * Variable for storing the value of "SsCpuRawSoftIRQ".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.61".
     */
    protected Long SsCpuRawSoftIRQ = new Long(1);

    /**
     * Variable for storing the value of "SsRawContexts".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.60".
     */
    protected Long SsRawContexts = new Long(1);

    /**
     * Variable for storing the value of "SsCpuIdle".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.11".
     */
    protected Integer SsCpuIdle = new Integer(1);

    /**
     * Variable for storing the value of "SsCpuSystem".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.10".
     */
    protected Integer SsCpuSystem = new Integer(1);

    /**
     * Variable for storing the value of "SsCpuUser".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.9".
     */
    protected Integer SsCpuUser = new Integer(1);

    /**
     * Variable for storing the value of "SsSysContext".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.8".
     */
    protected Integer SsSysContext = new Integer(1);

    /**
     * Variable for storing the value of "SsSysInterrupts".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.7".
     */
    protected Integer SsSysInterrupts = new Integer(1);

    /**
     * Variable for storing the value of "SsRawInterrupts".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.59".
     */
    protected Long SsRawInterrupts = new Long(1);

    /**
     * Variable for storing the value of "SsIORawReceived".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.58".
     */
    protected Long SsIORawReceived = new Long(1);

    /**
     * Variable for storing the value of "SsIOReceive".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.6".
     */
    protected Integer SsIOReceive = new Integer(1);

    /**
     * Variable for storing the value of "SsIORawSent".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.57".
     */
    protected Long SsIORawSent = new Long(1);

    /**
     * Variable for storing the value of "SsIOSent".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.5".
     */
    protected Integer SsIOSent = new Integer(1);

    /**
     * Variable for storing the value of "SsSwapOut".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.4".
     */
    protected Integer SsSwapOut = new Integer(1);

    /**
     * Variable for storing the value of "SsCpuRawInterrupt".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.56".
     */
    protected Long SsCpuRawInterrupt = new Long(1);

    /**
     * Variable for storing the value of "SsSwapIn".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.3".
     */
    protected Integer SsSwapIn = new Integer(1);

    /**
     * Variable for storing the value of "SsCpuRawKernel".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.55".
     */
    protected Long SsCpuRawKernel = new Long(1);

    /**
     * Variable for storing the value of "SsErrorName".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.2".
     */
    protected String SsErrorName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "SsCpuRawWait".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.54".
     */
    protected Long SsCpuRawWait = new Long(1);

    /**
     * Variable for storing the value of "SsIndex".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.1".
     */
    protected Integer SsIndex = new Integer(1);

    /**
     * Variable for storing the value of "SsCpuRawIdle".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.53".
     */
    protected Long SsCpuRawIdle = new Long(1);

    /**
     * Variable for storing the value of "SsCpuRawSystem".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.52".
     */
    protected Long SsCpuRawSystem = new Long(1);

    /**
     * Variable for storing the value of "SsCpuRawNice".
     * The variable is identified by: "1.3.6.1.4.1.2021.11.51".
     */
    protected Long SsCpuRawNice = new Long(1);


    /**
     * Constructor for the "SystemStats" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public SystemStats(SnmpMib myMib) {
    }


    /**
     * Constructor for the "SystemStats" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public SystemStats(SnmpMib myMib, MBeanServer server) {
    }

    public SystemStats() {
		// TODO Auto-generated constructor stub
	}
    
    /**
     * Getter for the "SsCpuRawUser" variable.
     */
    public Long getSsCpuRawUser() throws SnmpStatusException {
        return SsCpuRawUser;
    }

    /**
     * Getter for the "SsCpuRawGuestNice" variable.
     */
    public Long getSsCpuRawGuestNice() throws SnmpStatusException {
        return SsCpuRawGuestNice;
    }

    /**
     * Getter for the "SsCpuRawGuest" variable.
     */
    public Long getSsCpuRawGuest() throws SnmpStatusException {
        return SsCpuRawGuest;
    }

    /**
     * Getter for the "SsCpuRawSteal" variable.
     */
    public Long getSsCpuRawSteal() throws SnmpStatusException {
        return SsCpuRawSteal;
    }

    /**
     * Getter for the "SsRawSwapOut" variable.
     */
    public Long getSsRawSwapOut() throws SnmpStatusException {
        return SsRawSwapOut;
    }

    /**
     * Getter for the "SsRawSwapIn" variable.
     */
    public Long getSsRawSwapIn() throws SnmpStatusException {
        return SsRawSwapIn;
    }

    /**
     * Getter for the "SsCpuRawSoftIRQ" variable.
     */
    public Long getSsCpuRawSoftIRQ() throws SnmpStatusException {
        return SsCpuRawSoftIRQ;
    }

    /**
     * Getter for the "SsRawContexts" variable.
     */
    public Long getSsRawContexts() throws SnmpStatusException {
        return SsRawContexts;
    }

    /**
     * Getter for the "SsCpuIdle" variable.
     */
    public Integer getSsCpuIdle() throws SnmpStatusException {
        return SsCpuIdle;
    }

    /**
     * Getter for the "SsCpuSystem" variable.
     */
    public Integer getSsCpuSystem() throws SnmpStatusException {
        return SsCpuSystem;
    }

    /**
     * Getter for the "SsCpuUser" variable.
     */
    public Integer getSsCpuUser() throws SnmpStatusException {
        return SsCpuUser;
    }

    /**
     * Getter for the "SsSysContext" variable.
     */
    public Integer getSsSysContext() throws SnmpStatusException {
        return SsSysContext;
    }

    /**
     * Getter for the "SsSysInterrupts" variable.
     */
    public Integer getSsSysInterrupts() throws SnmpStatusException {
        return SsSysInterrupts;
    }

    /**
     * Getter for the "SsRawInterrupts" variable.
     */
    public Long getSsRawInterrupts() throws SnmpStatusException {
        return SsRawInterrupts;
    }

    /**
     * Getter for the "SsIORawReceived" variable.
     */
    public Long getSsIORawReceived() throws SnmpStatusException {
        return SsIORawReceived;
    }

    /**
     * Getter for the "SsIOReceive" variable.
     */
    public Integer getSsIOReceive() throws SnmpStatusException {
        return SsIOReceive;
    }

    /**
     * Getter for the "SsIORawSent" variable.
     */
    public Long getSsIORawSent() throws SnmpStatusException {
        return SsIORawSent;
    }

    /**
     * Getter for the "SsIOSent" variable.
     */
    public Integer getSsIOSent() throws SnmpStatusException {
        return SsIOSent;
    }

    /**
     * Getter for the "SsSwapOut" variable.
     */
    public Integer getSsSwapOut() throws SnmpStatusException {
        return SsSwapOut;
    }

    /**
     * Getter for the "SsCpuRawInterrupt" variable.
     */
    public Long getSsCpuRawInterrupt() throws SnmpStatusException {
        return SsCpuRawInterrupt;
    }

    /**
     * Getter for the "SsSwapIn" variable.
     */
    public Integer getSsSwapIn() throws SnmpStatusException {
        return SsSwapIn;
    }

    /**
     * Getter for the "SsCpuRawKernel" variable.
     */
    public Long getSsCpuRawKernel() throws SnmpStatusException {
        return SsCpuRawKernel;
    }

    /**
     * Getter for the "SsErrorName" variable.
     */
    public String getSsErrorName() throws SnmpStatusException {
        return SsErrorName;
    }

    /**
     * Getter for the "SsCpuRawWait" variable.
     */
    public Long getSsCpuRawWait() throws SnmpStatusException {
        return SsCpuRawWait;
    }

    /**
     * Getter for the "SsIndex" variable.
     */
    public Integer getSsIndex() throws SnmpStatusException {
        return SsIndex;
    }

    /**
     * Getter for the "SsCpuRawIdle" variable.
     */
    public Long getSsCpuRawIdle() throws SnmpStatusException {
        return SsCpuRawIdle;
    }

    /**
     * Getter for the "SsCpuRawSystem" variable.
     */
    public Long getSsCpuRawSystem() throws SnmpStatusException {
        return SsCpuRawSystem;
    }

    /**
     * Getter for the "SsCpuRawNice" variable.
     */
    public Long getSsCpuRawNice() throws SnmpStatusException {
        return SsCpuRawNice;
    }

}
