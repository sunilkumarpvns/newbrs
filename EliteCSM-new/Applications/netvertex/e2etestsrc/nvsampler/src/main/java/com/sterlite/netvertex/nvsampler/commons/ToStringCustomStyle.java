package com.sterlite.netvertex.nvsampler.commons;

import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ToStringCustomStyle extends ToStringStyle {


    private boolean skipNull;

    public ToStringCustomStyle() {
        super();
        setDefaultFormating();
    }

    private void setDefaultFormating() {
        this.setUseClassName(false);
        this.setUseIdentityHashCode(false);
        this.setNullText("N/A");
        this.setUseFieldNames(true);
        this.setFieldNameValueSeparator(" = ");
        this.setContentStart("");
        this.setFieldSeparator(System.lineSeparator());
        this.setFieldSeparatorAtStart(true);
        this.setContentEnd(System.lineSeparator());
    }

    @Override
    public void setContentStart(String contentStart) {
        super.setContentStart(contentStart);
    }

    @Override
    public void setContentEnd(String contentEnd) {
        super.setContentEnd(contentEnd);
    }

    @Override
    public void setFieldSeparator(String fieldSeparator) {
        super.setFieldSeparator(fieldSeparator);
    }

    @Override
    public void setFieldSeparatorAtEnd(boolean fieldSeparatorAtEnd) {
        super.setFieldSeparatorAtEnd(fieldSeparatorAtEnd);
    }

    @Override
    public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
        if(skipNull == false || Objects.isNull(value) == false) {
            super.append(buffer, fieldName, value, fullDetail);
        }
    }

    @Override
    public void setFieldSeparatorAtStart(boolean fieldSeparatorAtStart) {
        super.setFieldSeparatorAtStart(fieldSeparatorAtStart);
    }

    @Override
    public void setFieldNameValueSeparator(String fieldNameValueSeparator) {
        super.setFieldNameValueSeparator(fieldNameValueSeparator);
    }

    @Override
    public void setUseClassName(boolean useClassName) {
        super.setUseClassName(useClassName);
    }

    @Override
    public void setUseIdentityHashCode(boolean useClassName) {
        super.setUseIdentityHashCode(useClassName);
    }

    @Override
    public void setNullText(String nullText) {
        super.setNullText(nullText);
    }

    @Override
    public void setUseFieldNames(boolean useFieldNames) {
        super.setUseFieldNames(useFieldNames);
    }

    public void skipNull() {
        skipNull = true;
    }
}
