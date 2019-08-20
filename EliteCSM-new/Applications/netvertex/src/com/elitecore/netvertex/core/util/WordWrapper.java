package com.elitecore.netvertex.core.util;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.core.util.string.BoundedStringBuilder;

/**
 * WordWrapper wraps content by defined length and left margin.
 * 
 * 
 */
public class WordWrapper {

    private int length;
    private int leftMargin;
    private boolean firstLineMarginEnable;
    private StringBuilder finalOutputBuilder;
    private BoundedStringBuilder boundedStringBuilder;

    public WordWrapper() {
	this(10, 0, true);
    }

    public WordWrapper(int width) {
	this(width, 0, true);
    }

    public WordWrapper(int width, int leftMargin) {
	this(width, leftMargin, true);
    }

    /**
     * 
     * @param lineLength
     *            , defines line length for wrapping text
     * @param leftMargin
     *            , left margin of wrapped text. its a number of spaces
     * @param firstLineMarginEnable
     *            , flag for enable/disable first line margin. Default: true
     */
    public WordWrapper(int lineLength, int leftMargin, boolean firstLineMarginEnable) {
	this.length = lineLength;
	this.leftMargin = leftMargin;
	this.firstLineMarginEnable = firstLineMarginEnable;
	this.boundedStringBuilder = new BoundedStringBuilder(lineLength);
	this.finalOutputBuilder = new StringBuilder();
    }

    /**
     * wraps provided String to defined line length and left margin. it works
     * continuous for multiple inputs.<br/>
     * 
     * To get final content use {@link #getFormattedValue()}
     * 
     * @param str
     *            , String value to be wrapped
     */
    public WordWrapper append(String str) {
	String[] stringArray = Splitter.on(' ').splitToArray(str);

	for (String data : stringArray) {

	    if (data.equals(System.getProperty("line.separator"))) {
		wrap(boundedStringBuilder.toString());
		newLine();
		appendMargin();
		boundedStringBuilder = new BoundedStringBuilder(length);
		continue;
	    }

	    if (boundedStringBuilder.isAccomodable(data)) {
		if(boundedStringBuilder.length() != 0) {
		    boundedStringBuilder.append(" ");
		}
		boundedStringBuilder.append(data);
	    } else {
		wrap(boundedStringBuilder.toString());
		boundedStringBuilder = new BoundedStringBuilder(length);
		boundedStringBuilder.append(data);
	    }
	}
	wrap(boundedStringBuilder.toString());
	return this;
    }

    private void wrap(String string) {

	if (finalOutputBuilder.length() == 0) {
	    if (firstLineMarginEnable) {
		appendMargin();
	    }
	    finalOutputBuilder.append(string);
	    return;
	}

	newLine();
	appendMargin();
	finalOutputBuilder.append(string);
    }

    private void newLine() {
	finalOutputBuilder.append(System.getProperty("line.separator"));
    }

    private void appendMargin() {
	finalOutputBuilder.append(Strings.repeat(" ", leftMargin));
    }

    @Override
    public String toString() {
	return finalOutputBuilder.toString();
    }
}
