package com.elitecore.netvertex.rnc;

public class PulseCalculator {

    private long pulse;
    private double pulseInDouble;

    public PulseCalculator(long pulse) {
        this.pulse = pulse;
        this.pulseInDouble = pulse;
    }

    public long floor(long unit) {
        return (long) Math.floor(unit / pulseInDouble);
    }

    public long ceil(long unit) {
        return (long) Math.ceil(unit / pulseInDouble);
    }

    public long multiply(long unit) {
        return unit * pulse;
    }

}
