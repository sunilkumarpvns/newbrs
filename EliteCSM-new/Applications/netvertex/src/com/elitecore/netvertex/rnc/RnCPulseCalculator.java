package com.elitecore.netvertex.rnc;

public final class RnCPulseCalculator {

    public static long floor(long unit, double pulse) {
        return (long) Math.floor(unit / pulse);
    }

    public static long ceil(long unit, double pulse) {
        return (long) Math.ceil(unit / pulse);
    }

    public static long multiply(long unit, long pulse) {
        return unit * pulse;
    }

}
