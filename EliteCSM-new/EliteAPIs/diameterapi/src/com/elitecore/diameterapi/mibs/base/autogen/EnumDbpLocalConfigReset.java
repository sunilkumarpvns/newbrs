package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB.
//

// java imports
//
import java.io.Serializable;
import java.util.Hashtable;

// RI imports
//
import com.sun.jdmk.Enumerated;

/**
 * The class is used for representing "DbpLocalConfigReset".
 */
public class EnumDbpLocalConfigReset extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(1), "other");
        intTable.put(new Integer(3), "running");
        intTable.put(new Integer(2), "initializing");
        intTable.put(new Integer(4), "reset");
        stringTable.put("other", new Integer(1));
        stringTable.put("running", new Integer(3));
        stringTable.put("initializing", new Integer(2));
        stringTable.put("reset", new Integer(4));
    }

    public EnumDbpLocalConfigReset(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpLocalConfigReset(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpLocalConfigReset() throws IllegalArgumentException {
        super();
    }

    public EnumDbpLocalConfigReset(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}
