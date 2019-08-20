package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.data.GyServiceUnits;

public abstract class RateCalculator {
    private double rate;
    private long rateInLong;

    protected RateCalculator(double rate) {

        this.rate = rate;
        this.rateInLong = (long) rate;
    }

    public abstract long floor(GyServiceUnits gyServiceUnits);

    public abstract long ceil(GyServiceUnits gyServiceUnits);

    public abstract long floor(long volume, long time);

    public abstract long ceil(long volume, long time);

    public abstract double calculate(long volume, long time);

    public long floor(long unit) {
        return unit - unit % rateInLong;
    }

    public long ceil(long unit) {
        return unit + unit % rateInLong;
    }

    public double calculate(long unit) {
        return unit * rate;
    }
    

    public static class TimeBaseRateCalculator extends RateCalculator {

        public TimeBaseRateCalculator(double rate) {
            super(rate);
        }

        @Override
        public long floor(GyServiceUnits gyServiceUnits) {
            return floor(gyServiceUnits.getTime());
        }

        @Override
        public long ceil(GyServiceUnits gyServiceUnits) {
            return ceil(gyServiceUnits.getTime());
        }

        @Override
        public long floor(long volume, long time) {
            return floor(time);
        }

        @Override
        public long ceil(long volume, long time) {
            return ceil(time);
        }

        @Override
        public double calculate(long volume, long time) {
            return calculate(time);
        }


    }

    public static class VolumeBaseRateCalculator extends RateCalculator {

        public VolumeBaseRateCalculator(double rate) {
            super(rate);
        }

        @Override
        public long floor(GyServiceUnits gyServiceUnits) {
            return floor(gyServiceUnits.getVolume());
        }

        @Override
        public long ceil(GyServiceUnits gyServiceUnits) {
            return ceil(gyServiceUnits.getVolume());
        }

        @Override
        public long floor(long volume, long time) {
            return floor(volume);
        }

        @Override
        public long ceil(long volume, long time) {
            return ceil(volume);
        }

        @Override
        public double calculate(long volume, long time) {
            return calculate(volume);
        }
    }
}
