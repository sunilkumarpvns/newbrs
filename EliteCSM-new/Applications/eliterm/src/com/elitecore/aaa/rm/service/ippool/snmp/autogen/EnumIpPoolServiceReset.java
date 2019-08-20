package com.elitecore.aaa.rm.service.ippool.snmp.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IP-POOL-SERVICE-MIB.
//

// java imports
//
import java.io.Serializable;
import java.util.Hashtable;

// RI imports
//
import com.sun.jdmk.Enumerated;

/**
 * The class is used for representing "IpPoolServiceReset".
 */
public class EnumIpPoolServiceReset extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(1), "other");
        intTable.put(new Integer(4), "running");
        intTable.put(new Integer(3), "initializing");
        intTable.put(new Integer(2), "reset");
        stringTable.put("other", new Integer(1));
        stringTable.put("running", new Integer(4));
        stringTable.put("initializing", new Integer(3));
        stringTable.put("reset", new Integer(2));
    }

    public EnumIpPoolServiceReset(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumIpPoolServiceReset(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumIpPoolServiceReset() throws IllegalArgumentException {
        super();
    }

    public EnumIpPoolServiceReset(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}