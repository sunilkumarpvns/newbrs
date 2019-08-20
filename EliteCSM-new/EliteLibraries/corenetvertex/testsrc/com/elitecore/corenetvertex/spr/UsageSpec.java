package com.elitecore.corenetvertex.spr;

import java.util.Date;

public class UsageSpec {
    private long volume;
    private long reserveVolume;
    private long time;
    private long reserveTime;
    private long resetTime;

    public long volume() {
        return volume;
    }

    public long reserveVolume() {
        return reserveVolume;
    }

    public long time() {
        return time;
    }

    public long reserveTime() {
        return reserveTime;
    }

    public long resetTime() {
        return resetTime;
    }

    public UsageSpec volume(long volume) {
        this.volume = volume;
        return this;
    }

    public UsageSpec reserveVolume(long reserveVolume) {
        this.reserveVolume = reserveVolume;
        return this;
    }

    public UsageSpec time(long time) {
        this.time = time;
        return this;
    }

    public UsageSpec reserveTime(long reserveTime) {
        this.reserveTime = reserveTime;
        return this;
    }

    public UsageSpec resetOn(long time) {
        this.resetTime = time;
        return this;
    }

    public UsageSpec resetOn(Date time) {
        this.resetTime = time.getTime();
        return this;
    }

    public UsageSpec and() {
        return this;
    }
}
