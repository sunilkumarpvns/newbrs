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
 * The class is used for representing "DbpPerPeerInfoLastDiscCause".
 */
public class EnumDbpPerPeerInfoLastDiscCause extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(3), "doNotWantToTalk");
        intTable.put(new Integer(1), "rebooting");
        intTable.put(new Integer(4), "election");
        intTable.put(new Integer(2), "busy");
        stringTable.put("doNotWantToTalk", new Integer(3));
        stringTable.put("rebooting", new Integer(1));
        stringTable.put("election", new Integer(4));
        stringTable.put("busy", new Integer(2));
    }

    public EnumDbpPerPeerInfoLastDiscCause(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpPerPeerInfoLastDiscCause(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpPerPeerInfoLastDiscCause() throws IllegalArgumentException {
        super();
    }

    public EnumDbpPerPeerInfoLastDiscCause(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}
