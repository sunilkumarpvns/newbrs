package com.elitecore.corenetvertex.pm.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

public class Time implements Comparable<Time> {

    private int hour;
    private int min;
    private int second;

    public Time(int hour, int min, int second) {
        this.hour = hour;
        this.min = min;
        this.second = second;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return min;
    }

    public int getSecond() {
        return second;
    }

    @Override
    public int compareTo(Time time) {
        int ans = hour - time.getHour();
        if (ans != 0) {
            return ans;
        }

        ans = min - time.getMinute();
        if (ans != 0) {
            return ans;
        }

        return second - time.getSecond();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Time time = (Time) o;
        return hour == time.hour &&
                min == time.min &&
                second == time.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, min, second);
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        out.print(hour + ":" + min + ":" + second);
        out.close();
        return stringWriter.toString();
    }
}
