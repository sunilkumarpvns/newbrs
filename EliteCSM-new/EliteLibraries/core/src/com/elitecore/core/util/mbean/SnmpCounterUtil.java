package com.elitecore.core.util.mbean;


public class SnmpCounterUtil {
	
	/**
	 * Value represents max unsigned32 + 1
	 */
    private static final long SNMP_COUNTER32_ROUNDOFF_VAL = 4294967296l;

    public static long convertToCounter32(long value) {
		return value % SNMP_COUNTER32_ROUNDOFF_VAL;
	}
}
