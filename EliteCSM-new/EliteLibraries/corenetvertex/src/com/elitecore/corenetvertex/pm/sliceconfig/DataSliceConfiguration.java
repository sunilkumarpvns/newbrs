package com.elitecore.corenetvertex.pm.sliceconfig;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class DataSliceConfiguration implements ToStringable{
    private static final long MIN_VALIDITY_TIME = 10;

    private long monetaryReservation;

    private int volumeSlicePercentage;
    private int volumeSliceThreshold;
    private long volumeMinimumSlice;
    private long volumeMaximumSlice;
    private DataUnit volumeMinimumSliceUnit;
    private DataUnit volumeMaximumSliceUnit;

    private long volumeMinimumSliceUnitInByte;
    private long volumeMaximumSliceUnitInByte;

    private int timeSlicePercentage;
    private int timeSliceThreshold;
    private long timeMinimumSlice;
    private long timeMaximumSlice;
    private TimeUnit timeMinimumSliceUnit;
    private TimeUnit timeMaximumSliceUnit;

    private long timeMinimumSliceUnitInSeconds;
    private long timeMaximumSliceUnitInSeconds;

    private boolean dynamicSlicing;

    public void setMonetaryReservation(long monetaryReservation) {
        this.monetaryReservation = monetaryReservation;
    }

    public void setVolumeSlicePercentage(int volumeSlicePercentage) {
        this.volumeSlicePercentage = volumeSlicePercentage;
    }

    public void setVolumeSliceThreshold(int volumeSliceThreshold) {
        this.volumeSliceThreshold = volumeSliceThreshold;
    }

    public void setVolumeMinimumSlice(long volumeMinimumSlice) {
        this.volumeMinimumSlice = volumeMinimumSlice;
    }

    public void setVolumeMaximumSlice(long volumeMaximumSlice) {
        this.volumeMaximumSlice = volumeMaximumSlice;
    }

    public void setVolumeMinimumSliceUnit(DataUnit volumeMinimumSliceUnit) {
        this.volumeMinimumSliceUnit = volumeMinimumSliceUnit;

        switch (volumeMinimumSliceUnit) {
            case MB:
                volumeMinimumSliceUnitInByte = DataUnit.MB.toBytes(volumeMinimumSlice);
                break;
            case KB:
                volumeMinimumSliceUnitInByte = DataUnit.KB.toBytes(volumeMinimumSlice);
                break;
            case GB:
                volumeMinimumSliceUnitInByte = DataUnit.GB.toBytes(volumeMinimumSlice);
                break;
            case BYTE:
                volumeMinimumSliceUnitInByte = volumeMinimumSlice;
                break;
        }
    }

    public void setVolumeMaximumSliceUnit(DataUnit volumeMaximumSliceUnit) {
        this.volumeMaximumSliceUnit = volumeMaximumSliceUnit;

        switch (volumeMaximumSliceUnit) {
            case MB:
                volumeMaximumSliceUnitInByte = DataUnit.MB.toBytes(volumeMaximumSlice);
                break;
            case KB:
                volumeMaximumSliceUnitInByte = DataUnit.KB.toBytes(volumeMaximumSlice);
                break;
            case GB:
                volumeMaximumSliceUnitInByte = DataUnit.GB.toBytes(volumeMaximumSlice);
                break;
            case BYTE:
                volumeMaximumSliceUnitInByte = volumeMaximumSlice;
                break;
        }
    }

    public void setTimeSlicePercentage(int timeSlicePercentage) {
        this.timeSlicePercentage = timeSlicePercentage;
    }

    public void setTimeSliceThreshold(int timeSliceThreshold) {
        this.timeSliceThreshold = timeSliceThreshold;
    }

    public void setTimeMinimumSlice(long timeMinimumSlice) {
        this.timeMinimumSlice = timeMinimumSlice;
    }

    public void setTimeMaximumSlice(long timeMaximumSlice) {
        this.timeMaximumSlice = timeMaximumSlice;
    }

    public void setTimeMinimumSliceUnit(TimeUnit timeMinimumSliceUnit) {
        this.timeMinimumSliceUnit = timeMinimumSliceUnit;

        switch (timeMinimumSliceUnit) {
            case SECOND:
                timeMinimumSliceUnitInSeconds = timeMinimumSlice;
                break;
            case MINUTE:
                timeMinimumSliceUnitInSeconds = TimeUnit.MINUTE.toSeconds(timeMinimumSlice);
                break;
            case HOUR:
                timeMinimumSliceUnitInSeconds = TimeUnit.HOUR.toSeconds(timeMinimumSlice);
                break;
        }
    }

    public void setTimeMaximumSliceUnit(TimeUnit timeMaximumSliceUnit) {
        this.timeMaximumSliceUnit = timeMaximumSliceUnit;

        switch (timeMaximumSliceUnit) {
            case SECOND:
                timeMaximumSliceUnitInSeconds = timeMaximumSlice;
                break;
            case MINUTE:
                timeMaximumSliceUnitInSeconds = TimeUnit.MINUTE.toSeconds(timeMaximumSlice);
                break;
            case HOUR:
                timeMaximumSliceUnitInSeconds = TimeUnit.HOUR.toSeconds(timeMaximumSlice);
                break;
        }
    }

    public void setDynamicSlicing(boolean dynamicSlicing) {
        this.dynamicSlicing = dynamicSlicing;
    }

    public long getMonetaryReservation() {
        return monetaryReservation;
    }

    public int getVolumeSlicePercentage() {
        return volumeSlicePercentage;
    }

    public int getVolumeSliceThreshold() {
        return volumeSliceThreshold;
    }

    public long getVolumeMinimumSlice() {
        return volumeMinimumSliceUnitInByte;
    }

    public long getVolumeMaximumSlice() {
        return volumeMaximumSliceUnitInByte;
    }

    public DataUnit getVolumeMinimumSliceUnit() {
        return volumeMinimumSliceUnit;
    }

    public DataUnit getVolumeMaximumSliceUnit() {
        return volumeMaximumSliceUnit;
    }

    public int getTimeSlicePercentage() {
        return timeSlicePercentage;
    }

    public int getTimeSliceThreshold() {
        return timeSliceThreshold;
    }

    public long getTimeMinimumSlice() {
        return timeMinimumSliceUnitInSeconds;
    }

    public long getTimeMaximumSlice() {
        return timeMaximumSliceUnitInSeconds;
    }

    public TimeUnit getTimeMinimumSliceUnit() {
        return timeMinimumSliceUnit;
    }

    public TimeUnit getTimeMaximumSliceUnit() {
        return timeMaximumSliceUnit;
    }

    public long getMinValidityTime() {
        return MIN_VALIDITY_TIME;
    }

    public boolean getDynamicSlicing() {
        return dynamicSlicing;
    }

    @Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- Slice Configuration -- ");
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.incrementIndentation();
        builder.append("Monetary Reservation", monetaryReservation);
        builder.append("Volume Slice Percentage" + CommonConstants.OPENING_PARENTHESES + CommonConstants.PERECNTILE + CommonConstants.CLOSING_PARENTHESES, volumeSlicePercentage);
        builder.append("Volume Slice Threshold" + CommonConstants.OPENING_PARENTHESES + CommonConstants.PERECNTILE + CommonConstants.CLOSING_PARENTHESES, volumeSliceThreshold);
        builder.append("Volume Minimum Slice", volumeMinimumSlice + " " + volumeMinimumSliceUnit);
        builder.append("Volume Maximum Slice", volumeMaximumSlice + " " + volumeMinimumSliceUnit);
        builder.append("Time Slice Percentage" + CommonConstants.OPENING_PARENTHESES + CommonConstants.PERECNTILE + CommonConstants.CLOSING_PARENTHESES, timeSlicePercentage);
        builder.append("Time Slice Threshold" + CommonConstants.OPENING_PARENTHESES + CommonConstants.PERECNTILE + CommonConstants.CLOSING_PARENTHESES, timeSliceThreshold);
        builder.append("Time Minimum Slice", timeMinimumSlice + " " + timeMinimumSliceUnit);
        builder.append("Time Maximum Slice", timeMaximumSlice + " " + timeMaximumSliceUnit);
        builder.append("Dynamic Slicing", dynamicSlicing);
        builder.decrementIndentation();
    }
}
