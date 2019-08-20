package com.sterlite.netvertex.nvsampler.commons;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IndentingToStringBuilder {
    private AtomicInteger indentationCount;
    private StringBuffer buffer; //NOSONAR as required by apache commons lang for ToStringStyle
    private Deque<ToStringStyle> styles;

    public IndentingToStringBuilder() {
        this.styles  = new ArrayDeque<>(4);
        this.indentationCount = new AtomicInteger(0);
        this.styles.push(new ToStringIndentingStyle(indentationCount));
        this.buffer = new StringBuffer();
    }

    public IndentingToStringBuilder incrementIndentation() {
        indentationCount.incrementAndGet();
        return this;
    }

    public IndentingToStringBuilder decrementIndentation() {
        if (indentationCount.get() == 0) {
            throw new IllegalStateException("indentation cannot be negative");
        }
        indentationCount.decrementAndGet();
        return this;
    }

    public AtomicInteger getCurrentIndentation() {
        return indentationCount;
    }

    public void pushStyle(ToStringStyle style) {
        this.styles.push(style);
    }

    public void popStyle() {
        this.styles.pop();
    }

    @Override
    public String toString() {
        return this.buffer.toString();
    }

    public IndentingToStringBuilder newline() {
        buffer.append("\n");
        return this;
    }

    public IndentingToStringBuilder appendField(String fieldName) {
        styles.peek().append(buffer, fieldName, "", true);
        return this;
    }

    public IndentingToStringBuilder appendValue(final Object obj) {
        styles.peek().append(buffer, null, obj, true);
        return this;
    }

    public IndentingToStringBuilder append(String fieldName, Object obj) {
        styles.peek().append(buffer, fieldName, obj, true);
        return this;
    }

    public IndentingToStringBuilder appendChildObject(String fieldName, @Nullable Collection<? extends ToStringable> childObjects) {
        appendField(fieldName);
        incrementIndentation();
        if (CollectionUtils.isEmpty(childObjects) == false) {
            childObjects.forEach(stringable -> stringable.toString(this));
        } else {
            appendValue(null); //will be printed based on setNullText
        }
        decrementIndentation();
        return this;
    }

    public IndentingToStringBuilder appendChildObject(String fieldName, @Nullable ToStringable childObject) {
        appendField(fieldName);
        incrementIndentation();
        if (childObject == null) {
            appendValue(null);
        } else {
            childObject.toString(this);
        }
        decrementIndentation();
        return this;
    }

    public IndentingToStringBuilder appendHeading(String heading) {
        styles.peek().append(buffer, null, heading, true);
        return this;
    }

    public IndentingToStringBuilder appendChild(String fieldName, Collection<String> values) {
        appendField(fieldName);
        incrementIndentation();
        if ((values == null || values.isEmpty()) == false) {
            values.stream().forEach(this::appendValue);
        } else {
            appendValue(null); // will be printed based on setNullText
        }
        decrementIndentation();
        return this;
    }
}


