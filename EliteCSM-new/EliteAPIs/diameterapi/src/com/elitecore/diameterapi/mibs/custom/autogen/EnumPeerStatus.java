package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB.
//

// java imports
//
import java.io.Serializable;
import java.util.Hashtable;

import com.sun.jdmk.Enumerated;
// RI imports
//

/**
 * The class is used for representing "PeerStatus".
 */
public class EnumPeerStatus extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(7), "closing");
        intTable.put(new Integer(1), "waitConnAck");
        intTable.put(new Integer(5), "r-open");
        intTable.put(new Integer(3), "elect");
        intTable.put(new Integer(0), "closed");
        intTable.put(new Integer(4), "waitReturns");
        intTable.put(new Integer(2), "waitICea");
        intTable.put(new Integer(6), "i-open");
        intTable.put(new Integer(8), "waitConnAckElect");
        stringTable.put("closing", new Integer(7));
        stringTable.put("waitConnAck", new Integer(1));
        stringTable.put("r-open", new Integer(5));
        stringTable.put("elect", new Integer(3));
        stringTable.put("closed", new Integer(0));
        stringTable.put("waitReturns", new Integer(4));
        stringTable.put("waitICea", new Integer(2));
        stringTable.put("i-open", new Integer(6));
        stringTable.put("waitConnAckElect", new Integer(8));
    }

    public EnumPeerStatus(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumPeerStatus(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumPeerStatus() throws IllegalArgumentException {
        super();
    }

    public EnumPeerStatus(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}
