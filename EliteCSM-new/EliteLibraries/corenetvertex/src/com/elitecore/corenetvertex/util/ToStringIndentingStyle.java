package com.elitecore.corenetvertex.util;

import com.elitecore.commons.base.Strings;

import java.util.concurrent.atomic.AtomicInteger;

public class ToStringIndentingStyle extends ToStringCustomStyle {

    private AtomicInteger indentationCount;

    public ToStringIndentingStyle(AtomicInteger level) {
        super();
        this.indentationCount = level;
    }

    private void appendTabs(StringBuffer buffer) {
        super.appendDetail(buffer, null, Strings.repeat("\t", indentationCount.get()));
    }

    @Override
    public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
        appendTabs(buffer);
        super.append(buffer, fieldName, value, fullDetail);
    }
}
