package com.elitecore.netvertex.pm.quota;

public class VoiceSliceConfiguration {

    public int getTimeReservationSlicePercentage() {
        return 10;
    }

    public long getMaximumTimeSlice() {
        return 3600;
    }

    public long getMinimumTimeSlice() {
        return 60;
    }

    public long getSlicePulse() {
        return 10;
    }
}