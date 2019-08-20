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
 * The class is used for representing "EgpNeighMode".
 */
public class EnumEgpNeighMode extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(2), "passive");
        intTable.put(new Integer(1), "active");
        stringTable.put("passive", new Integer(2));
        stringTable.put("active", new Integer(1));
    }

    public EnumEgpNeighMode(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumEgpNeighMode(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumEgpNeighMode() throws IllegalArgumentException {
        super();
    }

    public EnumEgpNeighMode(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}