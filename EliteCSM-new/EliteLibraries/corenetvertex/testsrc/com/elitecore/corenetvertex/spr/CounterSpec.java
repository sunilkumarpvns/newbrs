package com.elitecore.corenetvertex.spr;

import java.util.Date;

public class CounterSpec {

    private long volume;
    private long time;
    private long resetTime;


    public CounterSpec volume(long upload) {
        this.volume = upload;
        return this;
    }

    public CounterSpec time(long time) {
        this.time = time;
        return this;
    }

    public CounterSpec resetOn(long time) {
        this.resetTime = time;
        return this;
    }

    public CounterSpec resetOn(Date time) {
        this.resetTime = time.getTime();
        return this;
    }

    public CounterSpec and() {
        return this;
    }

    public long volume() {
        return volume;
    }

    public long time() {
        return time;
    }

    public long resetTime() {
        return resetTime;
    }
}
