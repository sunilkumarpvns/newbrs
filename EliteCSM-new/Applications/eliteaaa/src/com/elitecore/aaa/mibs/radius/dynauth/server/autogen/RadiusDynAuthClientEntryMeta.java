package com.elitecore.aaa.mibs.radius.dynauth.server.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-DYNAUTH-SERVER-MIB in standard metadata mode.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import javax.management.MBeanServer;
import com.sun.management.snmp.SnmpCounter;
import com.sun.management.snmp.SnmpCounter64;
import com.sun.management.snmp.SnmpGauge;
import com.sun.management.snmp.SnmpInt;
import com.sun.management.snmp.SnmpUnsignedInt;
import com.sun.management.snmp.SnmpIpAddress;
import com.sun.management.snmp.SnmpTimeticks;
import com.sun.management.snmp.SnmpOpaque;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpStringFixed;
import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpNull;
import com.sun.management.snmp.SnmpValue;
import com.sun.management.snmp.SnmpVarBind;
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMibNode;
import com.sun.management.snmp.agent.SnmpMib;
import com.sun.management.snmp.agent.SnmpMibEntry;
import com.sun.management.snmp.agent.SnmpStandardObjectServer;
import com.sun.management.snmp.agent.SnmpStandardMetaServer;
import com.sun.management.snmp.agent.SnmpMibSubRequest;
import com.sun.management.snmp.agent.SnmpMibTable;
import com.sun.management.snmp.EnumRowStatus;
import com.sun.management.snmp.SnmpDefinitions;

/**
 * The class is used for representing SNMP metadata for the "RadiusDynAuthClientEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.146.1.2.1.
 */
public class RadiusDynAuthClientEntryMeta extends SnmpMibEntry
     implements Serializable, SnmpStandardMetaServer {

    /**
     * Constructor for the metadata associated to "RadiusDynAuthClientEntry".
     */
    public RadiusDynAuthClientEntryMeta(SnmpMib myMib, SnmpStandardObjectServer objserv) {
        objectserver = objserv;
        varList = new int[26];
        varList[0] = 19;
        varList[1] = 18;
        varList[2] = 17;
        varList[3] = 16;
        varList[4] = 15;
        varList[5] = 14;
        varList[6] = 13;
        varList[7] = 12;
        varList[8] = 11;
        varList[9] = 10;
        varList[10] = 9;
        varList[11] = 8;
        varList[12] = 7;
        varList[13] = 27;
        varList[14] = 6;
        varList[15] = 5;
        varList[16] = 26;
        varList[17] = 4;
        varList[18] = 25;
        varList[19] = 24;
        varList[20] = 3;
        varList[21] = 2;
        varList[22] = 23;
        varList[23] = 22;
        varList[24] = 21;
        varList[25] = 20;
        SnmpMibNode.sort(varList);
    }

    /**
     * Get the value of a scalar variable
     */
    public SnmpValue get(long var, Object data)
        throws SnmpStatusException {
        switch((int)var) {
            case 19:
                return new SnmpCounter(node.getRadiusDynAuthServCoANaks());

            case 18:
                return new SnmpCounter(node.getRadiusDynAuthServCoAAcks());

            case 17:
                return new SnmpCounter(node.getRadiusDynAuthServDupCoARequests());

            case 16:
                return new SnmpCounter(node.getRadiusDynAuthServCoAAuthOnlyRequests());

            case 15:
                return new SnmpCounter(node.getRadiusDynAuthServCoARequests());

            case 14:
                return new SnmpCounter(node.getRadiusDynAuthServDisconPacketsDropped());

            case 13:
                return new SnmpCounter(node.getRadiusDynAuthServDisconBadAuthenticators());

            case 12:
                return new SnmpCounter(node.getRadiusDynAuthServMalformedDisconRequests());

            case 11:
                return new SnmpCounter(node.getRadiusDynAuthServDisconUserSessRemoved());

            case 10:
                return new SnmpCounter(node.getRadiusDynAuthServDisconNakSessNoContext());

            case 9:
                return new SnmpCounter(node.getRadiusDynAuthServDisconNakAuthOnlyRequests());

            case 8:
                return new SnmpCounter(node.getRadiusDynAuthServDisconNaks());

            case 7:
                return new SnmpCounter(node.getRadiusDynAuthServDisconAcks());

            case 27:
                return new SnmpTimeticks(node.getRadiusDynAuthServerCounterDiscontinuity());

            case 6:
                return new SnmpCounter(node.getRadiusDynAuthServDupDisconRequests());

            case 5:
                return new SnmpCounter(node.getRadiusDynAuthServDisconAuthOnlyRequests());

            case 26:
                return new SnmpCounter(node.getRadiusDynAuthServUnknownTypes());

            case 4:
                return new SnmpCounter(node.getRadiusDynAuthServDisconRequests());

            case 25:
                return new SnmpCounter(node.getRadiusDynAuthServCoAPacketsDropped());

            case 24:
                return new SnmpCounter(node.getRadiusDynAuthServCoABadAuthenticators());

            case 3:
                return new SnmpString(node.getRadiusDynAuthClientAddress());

            case 2:
                return new SnmpInt(node.getRadiusDynAuthClientAddressType());

            case 23:
                return new SnmpCounter(node.getRadiusDynAuthServMalformedCoARequests());

            case 22:
                return new SnmpCounter(node.getRadiusDynAuthServCoAUserSessChanged());

            case 1:
                throw new SnmpStatusException(SnmpStatusException.noSuchInstance);
            case 21:
                return new SnmpCounter(node.getRadiusDynAuthServCoANakSessNoContext());

            case 20:
                return new SnmpCounter(node.getRadiusDynAuthServCoANakAuthOnlyRequests());

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
            case 19:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 18:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 17:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 16:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 27:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 26:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 25:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 24:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 23:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 22:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 21:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 20:
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
            case 19:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 18:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 17:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 16:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 15:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 14:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 13:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 12:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 11:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 10:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 9:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 8:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 7:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 27:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 6:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 5:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 26:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 4:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 25:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 24:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 3:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 2:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 23:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 22:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 1:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 21:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            case 20:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);

            default:
                throw new SnmpStatusException(SnmpStatusException.snmpRspNotWritable);
        }
    }

    /**
     * Allow to bind the metadata description to a specific object.
     */
    protected void setInstance(RadiusDynAuthClientEntryMBean var) {
        node = var;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "get" method defined in "SnmpMibEntry".
    // See the "SnmpMibEntry" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void get(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.get(this,req,depth);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "set" method defined in "SnmpMibEntry".
    // See the "SnmpMibEntry" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public void set(SnmpMibSubRequest req, int depth)
        throws SnmpStatusException {
        objectserver.set(this,req,depth);
    }


    // ------------------------------------------------------------
    // 
    // Implements the "check" method defined in "SnmpMibEntry".
    // See the "SnmpMibEntry" Javadoc API for more details.
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
            case 19:
            case 18:
            case 17:
            case 16:
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
            case 9:
            case 8:
            case 7:
            case 27:
            case 6:
            case 5:
            case 26:
            case 4:
            case 25:
            case 24:
            case 3:
            case 2:
            case 23:
            case 22:
            case 1:
            case 21:
            case 20:
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
            case 19:
            case 18:
            case 17:
            case 16:
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
            case 9:
            case 8:
            case 7:
            case 27:
            case 6:
            case 5:
            case 26:
            case 4:
            case 25:
            case 24:
            case 3:
            case 2:
            case 23:
            case 22:
            case 21:
            case 20:
                return true;
            default:
                break;
        }
        return false;
    }


    // ------------------------------------------------------------
    // 
    // Implements the "skipVariable" method defined in "SnmpMibEntry".
    // See the "SnmpMibEntry" Javadoc API for more details.
    // 
    // ------------------------------------------------------------

    public boolean  skipVariable(long var, Object data, int pduVersion) {
        switch((int)var) {
            case 1:
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * Return the name of the attribute corresponding to the SNMP variable identified by "id".
     */
    public String getAttributeName(long id)
        throws SnmpStatusException {
        switch((int)id) {
            case 19:
                return "RadiusDynAuthServCoANaks";

            case 18:
                return "RadiusDynAuthServCoAAcks";

            case 17:
                return "RadiusDynAuthServDupCoARequests";

            case 16:
                return "RadiusDynAuthServCoAAuthOnlyRequests";

            case 15:
                return "RadiusDynAuthServCoARequests";

            case 14:
                return "RadiusDynAuthServDisconPacketsDropped";

            case 13:
                return "RadiusDynAuthServDisconBadAuthenticators";

            case 12:
                return "RadiusDynAuthServMalformedDisconRequests";

            case 11:
                return "RadiusDynAuthServDisconUserSessRemoved";

            case 10:
                return "RadiusDynAuthServDisconNakSessNoContext";

            case 9:
                return "RadiusDynAuthServDisconNakAuthOnlyRequests";

            case 8:
                return "RadiusDynAuthServDisconNaks";

            case 7:
                return "RadiusDynAuthServDisconAcks";

            case 27:
                return "RadiusDynAuthServerCounterDiscontinuity";

            case 6:
                return "RadiusDynAuthServDupDisconRequests";

            case 5:
                return "RadiusDynAuthServDisconAuthOnlyRequests";

            case 26:
                return "RadiusDynAuthServUnknownTypes";

            case 4:
                return "RadiusDynAuthServDisconRequests";

            case 25:
                return "RadiusDynAuthServCoAPacketsDropped";

            case 24:
                return "RadiusDynAuthServCoABadAuthenticators";

            case 3:
                return "RadiusDynAuthClientAddress";

            case 2:
                return "RadiusDynAuthClientAddressType";

            case 23:
                return "RadiusDynAuthServMalformedCoARequests";

            case 22:
                return "RadiusDynAuthServCoAUserSessChanged";

            case 1:
                return "RadiusDynAuthClientIndex";

            case 21:
                return "RadiusDynAuthServCoANakSessNoContext";

            case 20:
                return "RadiusDynAuthServCoANakAuthOnlyRequests";

            default:
                break;
        }
        throw new SnmpStatusException(SnmpStatusException.noSuchObject);
    }

    protected RadiusDynAuthClientEntryMBean node;
    protected SnmpStandardObjectServer objectserver = null;
}
