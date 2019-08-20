package com.elitecore.core.imdg.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IMDG-MIB.
//

// java imports
//
import java.io.Serializable;
import java.util.Hashtable;

// RI imports
//
import com.sun.jdmk.Enumerated;

/**
 * The class is used for representing "InstanceStatus".
 */
public class EnumInstanceStatus extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(1), "inactive");
        intTable.put(new Integer(0), "active");
        stringTable.put("inactive", new Integer(1));
        stringTable.put("active", new Integer(0));
    }

    public EnumInstanceStatus(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumInstanceStatus(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumInstanceStatus() throws IllegalArgumentException {
        super();
    }

    public EnumInstanceStatus(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}
