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
 * The class is used for representing "DbpPeerRowStatus".
 */
public class EnumDbpPeerRowStatus extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(5), "createAndWait");
        intTable.put(new Integer(6), "destroy");
        intTable.put(new Integer(4), "createAndGo");
        intTable.put(new Integer(3), "notReady");
        intTable.put(new Integer(1), "active");
        intTable.put(new Integer(2), "notInService");
        stringTable.put("createAndWait", new Integer(5));
        stringTable.put("destroy", new Integer(6));
        stringTable.put("createAndGo", new Integer(4));
        stringTable.put("notReady", new Integer(3));
        stringTable.put("active", new Integer(1));
        stringTable.put("notInService", new Integer(2));
    }

    public EnumDbpPeerRowStatus(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpPeerRowStatus(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpPeerRowStatus() throws IllegalArgumentException {
        super();
    }

    public EnumDbpPeerRowStatus(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}
