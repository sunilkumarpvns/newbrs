package com.elitecore.netvertex.pm.quota;

public class Slice {

    private long value;
    private double reservedMonetaryBalance;
    private boolean finalSlice;
    private boolean isMonetaryFinalSlice;

    public Slice(long value, double reservedMonetaryBalance, boolean finalSlice) {
        this(value, reservedMonetaryBalance, finalSlice, false);
    }

    public Slice(long value, double reservedMonetaryBalance, boolean finalSlice, boolean isMonetaryFinalSlice) {
        this.value = value;
        this.reservedMonetaryBalance = reservedMonetaryBalance;
        this.finalSlice = finalSlice;
        this.isMonetaryFinalSlice = isMonetaryFinalSlice;
    }

    public long getValue() {
        return value;
    }

    public double getReservedMonetaryBalance() {
        return reservedMonetaryBalance;
    }

    public boolean isFinalSlice() {
        return finalSlice;
    }

    public boolean isMonetaryFinalSlice() {
        return isMonetaryFinalSlice;
    }
}