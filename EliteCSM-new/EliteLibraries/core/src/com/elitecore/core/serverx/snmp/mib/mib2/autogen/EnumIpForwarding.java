package com.elitecore.core.serverx.snmp.mib.mib2.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RFC1213-MIB.
//

// java imports
//
import java.io.Serializable;
import java.util.Hashtable;

import com.sun.jdmk.Enumerated;

/**
 * The class is used for representing "IpForwarding".
 */
public class EnumIpForwarding extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(1), "forwarding");
        intTable.put(new Integer(2), "not-forwarding");
        stringTable.put("forwarding", new Integer(1));
        stringTable.put("not-forwarding", new Integer(2));
    }

    public EnumIpForwarding(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumIpForwarding(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumIpForwarding() throws IllegalArgumentException {
        super();
    }

    public EnumIpForwarding(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}