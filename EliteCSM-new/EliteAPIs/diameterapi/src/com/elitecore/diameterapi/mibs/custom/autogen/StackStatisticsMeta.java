package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;

import javax.management.MBeanServer;

import com.sun.management.snmp.SnmpCounter;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.SnmpValue;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibGroup;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpMibTable;
import com.sun.management.snmp.agent.SnmpStandardMetaServer;
import com.sun.management.snmp.agent.SnmpStandardObjectServer;
// jmx imports
//
// jdmk imports
//

/**
 * The class is used for representing SNMP metadata for the "StackStatistics" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.5.100.
 */
public class StackStatisticsMeta extends SnmpMibGroup
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "StackStatistics".
     */
    public StackStatisticsMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        try {
            registerObject(9);
            registerObject(8);
            registerObject(7);
            registerObject(6);
            registerObject(5);
            registerObject(13);
            registerObject(4);
            registerObject(12);
            registerObject(3);
            registerObject(11);
            registerObject(2);
            registerObject(10);
            registerObject(1);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 9:
                return new SnmpCounter(node.getTotalDuplicateRequest());

            case 8:
                return new SnmpCounter(node.getTotalUnknownHbHAnswerDropped());

            case 7:
                return new SnmpCounter(node.getTotalRequestRetransmitted());

            case 6:
                return new SnmpCounter(node.getTotalRequestDr());

            case 5:
                return new SnmpCounter(node.getTotalAnswerDr());

            case 13:
                return new SnmpCounter(node.getTotalRequestPn());

            case 4:
                return new SnmpCounter(node.getTotalAnswerTx());

            case 12:
                return new SnmpCounter(node.getTotalRequestTimeOut());

            case 3:
                return new SnmpCounter(node.getTotalAnswerRx());

            case 11:
                return new SnmpCounter(node.getTotalMalformedPacketRx());

            case 2:
                return new SnmpCounter(node.getTotalRequestTx());

            case 10:
                return new SnmpCounter(node.getTotalDuplicateEtEAnswer());

            case 1:
                return new SnmpCounter(node.getTotalRequestRx());

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    /**
     * Set the value of a scalar variable
     */
    public SnmpValue set(SnmpValue x, long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
    }

    /**
     * Check the value of a scalar variable
     */
    public void check(SnmpValue x, long var, Object data)
        throws SnmpStatusException {
        switch((int) var) {
            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(StackStatisticsMBean var) {
        node = var;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "get" method defined in "SnmpMibGroup".
    // See the "SnmpMibGroup" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void get(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.get(this,req,depth);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "set" method defined in "SnmpMibGroup".
    // See the "SnmpMibGroup" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void set(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.set(this,req,depth);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "check" method defined in "SnmpMibGroup".
    // See the "SnmpMibGroup" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void check(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.check(this,req,depth);
    }

    /**
     * Returns true if "arc" identifies a scalar object.
     */
    public boolean isVariable(long arc) {

        switch((int)arc) {
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 13:
            case 4:
            case 12:
            case 3:
            case 11:
            case 2:
            case 10:
            case 1:
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Returns true if "arc" identifies a readable scalar object.
     */
    public boolean isReadable(long arc) {

        switch((int)arc) {
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 13:
            case 4:
            case 12:
            case 3:
            case 11:
            case 2:
            case 10:
            case 1:
                return true;
            default:
                break;
        }
        return false;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "skipVariable" method defined in "SnmpMibGroup".
    // See the "SnmpMibGroup" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public boolean  skipVariable(long var, Object data, int pduVersion) {
        return false;
    }

    /**
     * Return the name of the attribute corresponding to the SNMP variable identified by "id".
     */
    public String getAttributeName(long id)
        throws SnmpStatusException {
        switch((int)id) {
            case 9:
                return "TotalDuplicateRequest";

            case 8:
                return "TotalUnknownHbHAnswerDropped";

            case 7:
                return "TotalRequestRetransmitted";

            case 6:
                return "TotalRequestDr";

            case 5:
                return "TotalAnswerDr";

            case 13:
                return "TotalRequestPn";

            case 4:
                return "TotalAnswerTx";

            case 12:
                return "TotalRequestTimeOut";

            case 3:
                return "TotalAnswerRx";

            case 11:
                return "TotalMalformedPacketRx";

            case 2:
                return "TotalRequestTx";

            case 10:
                return "TotalDuplicateEtEAnswer";

            case 1:
                return "TotalRequestRx";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    /**
     * Returns true if "arc" identifies a table object.
     */
    public boolean isTable(long arc) {

        switch((int)arc) {
            default:
                break;
        }
        return false;
    }

    /**
     * Returns the table object identified by "arc".
     */
    public SnmpMibTable getTable(long arc) {
        return null;
    }

    /**
     * Register the group's SnmpMibTable objects with the meta-data.
     */
    public void registerTableNodes(SnmpMib mib, MBeanServer server) {
    }

    protected StackStatisticsMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}