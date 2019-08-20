package com.elitecore.netvertex.pm.quota;

public class LongRange {
    private long start;
    private long end;

    public LongRange(long start, long end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Represents closed range [start, end], which means start <= value <= end.
     *
     * @param start
     * @param end
     * @return
     */
    public static LongRange closed(long start, long end) {
        return new LongRange(start, end);
    }

    public long restrict(long value) {
        long result = value;
        if (result > end) {
            result = end;
        }
        if (result < start) {
            result = start;
        }
        return result;
    }
}