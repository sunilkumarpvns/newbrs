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
 * The class is used for representing "DbpRealmMessageRouteAction".
 */
public class EnumDbpRealmMessageRouteAction extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(3), "proxy");
        intTable.put(new Integer(1), "local");
        intTable.put(new Integer(4), "redirect");
        intTable.put(new Integer(2), "relay");
        stringTable.put("proxy", new Integer(3));
        stringTable.put("local", new Integer(1));
        stringTable.put("redirect", new Integer(4));
        stringTable.put("relay", new Integer(2));
    }

    public EnumDbpRealmMessageRouteAction(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpRealmMessageRouteAction(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumDbpRealmMessageRouteAction() throws IllegalArgumentException {
        super();
    }

    public EnumDbpRealmMessageRouteAction(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}
