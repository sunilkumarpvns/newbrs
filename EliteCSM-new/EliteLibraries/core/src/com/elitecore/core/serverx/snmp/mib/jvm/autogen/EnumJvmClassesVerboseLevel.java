package com.elitecore.core.serverx.snmp.mib.jvm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling JVM-MANAGEMENT-MIB.
//

// java imports
//
import java.io.Serializable;
import java.util.Hashtable;

// RI imports
//
import com.sun.jdmk.Enumerated;

/**
 * The class is used for representing "JvmClassesVerboseLevel".
 */
public class EnumJvmClassesVerboseLevel extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(2), "verbose");
        intTable.put(new Integer(1), "silent");
        stringTable.put("verbose", new Integer(2));
        stringTable.put("silent", new Integer(1));
    }

    public EnumJvmClassesVerboseLevel(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumJvmClassesVerboseLevel(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumJvmClassesVerboseLevel() throws IllegalArgumentException {
        super();
    }

    public EnumJvmClassesVerboseLevel(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}
