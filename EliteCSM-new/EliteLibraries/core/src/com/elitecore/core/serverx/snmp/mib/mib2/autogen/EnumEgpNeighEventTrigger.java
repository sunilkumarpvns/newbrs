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
 * The class is used for representing "EgpNeighEventTrigger".
 */
public class EnumEgpNeighEventTrigger extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(2), "stop");
        intTable.put(new Integer(1), "start");
        stringTable.put("stop", new Integer(2));
        stringTable.put("start", new Integer(1));
    }

    public EnumEgpNeighEventTrigger(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumEgpNeighEventTrigger(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumEgpNeighEventTrigger() throws IllegalArgumentException {
        super();
    }

    public EnumEgpNeighEventTrigger(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}