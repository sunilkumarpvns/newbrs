package com.elitecore.nvsmx.ws.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.commons.io.IndentingPrintWriter;

/**
 *
 * @author Jay Trivedi
 *
 */
public class Entry {
    private String key;
    private String value;

    public Entry(){ }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter tabbedPrintWriter = new IndentingPrintWriter(new PrintWriter(stringWriter));

        tabbedPrintWriter.println();
        tabbedPrintWriter.print(this.getKey());
        tabbedPrintWriter.print(" : ");
        tabbedPrintWriter.println(this.getValue());

        return stringWriter.toString();
    }
}
