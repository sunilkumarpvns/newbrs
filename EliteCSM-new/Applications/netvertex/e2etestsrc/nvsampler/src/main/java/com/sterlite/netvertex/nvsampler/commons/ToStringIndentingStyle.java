package com.sterlite.netvertex.nvsampler.commons;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.StringUtils;

public class ToStringIndentingStyle extends ToStringCustomStyle {

    private AtomicInteger indentationCount;

    public ToStringIndentingStyle(AtomicInteger level) {
        super();
        this.indentationCount = level;
    }

    private void appendTabs(StringBuffer buffer) { //NOSONAR as required by apache commons lang for ToStringStyle
        super.appendDetail(buffer, null, StringUtils.repeat("\t", indentationCount.get()));
    }

    @Override
    public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) { //NOSONAR as required by apache commons lang for ToStringStyle
        appendTabs(buffer);
        super.append(buffer, fieldName, value, fullDetail);
    }
}
