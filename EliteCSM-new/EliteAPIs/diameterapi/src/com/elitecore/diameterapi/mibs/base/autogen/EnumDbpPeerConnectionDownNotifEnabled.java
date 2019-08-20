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
 * The class is used for representing "DbpPeerConnectionDownNotifEnabled".
 */
public class EnumDbpPeerConnectionDownNotifEnabled extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(2), "false");
        intTable.put(new Integer(1), "true");
        stringTable.put("false", new Integer(2));
        stringTable.put("true", new Integer(1));
    }

    public EnumDbpPeerConnectionDownNotifEnabled(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpPeerConnectionDownNotifEnabled(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpPeerConnectionDownNotifEnabled() throws IllegalArgumentException {
        super();
    }

    public EnumDbpPeerConnectionDownNotifEnabled(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}
