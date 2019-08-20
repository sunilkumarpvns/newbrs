package com.elitecore.corenetvertex.util;

import com.elitecore.commons.base.TimeSource;

public class TimeSourceChain extends TimeSource {

    private int noOfLinksInChain=1;
    private long advanceLap;
    private long initialValues=0;
    private long[] chainLinksValues;
    private int counter = 0;



    public TimeSourceChain(int noOfLinksInChain,long initialValues, long lapValue) {
        super();
        this.noOfLinksInChain = noOfLinksInChain;
        this.advanceLap = lapValue;
        this.initialValues = initialValues;
        chainLinksValues = new long[noOfLinksInChain];
        createTimeSourceChain();
    }

    private void createTimeSourceChain() {
        for (int i = 0; i <= chainLinksValues.length-1 ; i++) {
            chainLinksValues[i] = initialValues + (advanceLap * i);
        }
    }

    @Override
    public long currentTimeInMillis() {
        counter++;
        if (counter > noOfLinksInChain) {
            throw new IllegalStateException("no of steps in time chain increases");
        }
        return chainLinksValues[counter-1];
    }
}
