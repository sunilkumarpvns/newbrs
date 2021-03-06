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
 * The class is used for representing "DbpPeerSecurity".
 */
public class EnumDbpPeerSecurity extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(1), "other");
        intTable.put(new Integer(3), "ipsec");
        intTable.put(new Integer(2), "tls");
        stringTable.put("other", new Integer(1));
        stringTable.put("ipsec", new Integer(3));
        stringTable.put("tls", new Integer(2));
    }

    public EnumDbpPeerSecurity(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpPeerSecurity(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpPeerSecurity() throws IllegalArgumentException {
        super();
    }

    public EnumDbpPeerSecurity(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}
