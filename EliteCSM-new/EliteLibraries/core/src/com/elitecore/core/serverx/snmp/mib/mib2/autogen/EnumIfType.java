package com.elitecore.core.serverx.snmp.mib.mib2.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RFC1213-MIB.
//

// java imports
//
import java.io.Serializable;
import java.util.Hashtable;

import com.sun.jdmk.Enumerated;

/**
 * The class is used for representing "IfType".
 */
public class EnumIfType extends Enumerated implements Serializable {

    protected static Hashtable intTable = new Hashtable();
    protected static Hashtable stringTable = new Hashtable();
    static  {
        intTable.put(new Integer(17), "sdlc");
        intTable.put(new Integer(4), "ddn-x25");
        intTable.put(new Integer(22), "propPointToPointSerial");
        intTable.put(new Integer(29), "ultra");
        intTable.put(new Integer(28), "slip");
        intTable.put(new Integer(1), "other");
        intTable.put(new Integer(26), "ethernet-3Mbit");
        intTable.put(new Integer(25), "eon");
        intTable.put(new Integer(15), "fddi");
        intTable.put(new Integer(19), "e1");
        intTable.put(new Integer(27), "nsip");
        intTable.put(new Integer(11), "starLan");
        intTable.put(new Integer(30), "ds3");
        intTable.put(new Integer(6), "ethernet-csmacd");
        intTable.put(new Integer(3), "hdh1822");
        intTable.put(new Integer(18), "ds1");
        intTable.put(new Integer(7), "iso88023-csmacd");
        intTable.put(new Integer(5), "rfc877-x25");
        intTable.put(new Integer(8), "iso88024-tokenBus");
        intTable.put(new Integer(2), "regular1822");
        intTable.put(new Integer(13), "proteon-80Mbit");
        intTable.put(new Integer(32), "frame-relay");
        intTable.put(new Integer(10), "iso88026-man");
        intTable.put(new Integer(23), "ppp");
        intTable.put(new Integer(9), "iso88025-tokenRing");
        intTable.put(new Integer(21), "primaryISDN");
        intTable.put(new Integer(12), "proteon-10Mbit");
        intTable.put(new Integer(16), "lapb");
        intTable.put(new Integer(14), "hyperchannel");
        intTable.put(new Integer(20), "basicISDN");
        intTable.put(new Integer(31), "sip");
        intTable.put(new Integer(24), "softwareLoopback");
        stringTable.put("sdlc", new Integer(17));
        stringTable.put("ddn-x25", new Integer(4));
        stringTable.put("propPointToPointSerial", new Integer(22));
        stringTable.put("ultra", new Integer(29));
        stringTable.put("slip", new Integer(28));
        stringTable.put("other", new Integer(1));
        stringTable.put("ethernet-3Mbit", new Integer(26));
        stringTable.put("eon", new Integer(25));
        stringTable.put("fddi", new Integer(15));
        stringTable.put("e1", new Integer(19));
        stringTable.put("nsip", new Integer(27));
        stringTable.put("starLan", new Integer(11));
        stringTable.put("ds3", new Integer(30));
        stringTable.put("ethernet-csmacd", new Integer(6));
        stringTable.put("hdh1822", new Integer(3));
        stringTable.put("ds1", new Integer(18));
        stringTable.put("iso88023-csmacd", new Integer(7));
        stringTable.put("rfc877-x25", new Integer(5));
        stringTable.put("iso88024-tokenBus", new Integer(8));
        stringTable.put("regular1822", new Integer(2));
        stringTable.put("proteon-80Mbit", new Integer(13));
        stringTable.put("frame-relay", new Integer(32));
        stringTable.put("iso88026-man", new Integer(10));
        stringTable.put("ppp", new Integer(23));
        stringTable.put("iso88025-tokenRing", new Integer(9));
        stringTable.put("primaryISDN", new Integer(21));
        stringTable.put("proteon-10Mbit", new Integer(12));
        stringTable.put("lapb", new Integer(16));
        stringTable.put("hyperchannel", new Integer(14));
        stringTable.put("basicISDN", new Integer(20));
        stringTable.put("sip", new Integer(31));
        stringTable.put("softwareLoopback", new Integer(24));
    }

    public EnumIfType(int valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumIfType(Integer valueIndex) throws IllegalArgumentException {
        super(valueIndex);
    }

    public EnumIfType() throws IllegalArgumentException {
        super();
    }

    public EnumIfType(String x) throws IllegalArgumentException {
        super(x);
    }

    protected Hashtable getIntTable() {
        return intTable ;
    }

    protected Hashtable getStringTable() {
        return stringTable ;
    }

}