package com.elitecore.corenetvertex.spr.data;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class SubscriptionMetadata {
    private FnFGroup fnFGroup;

    public FnFGroup getFnFGroup() {
        return fnFGroup;
    }

    public void setFnFGroup(FnFGroup fnFGroup) {
        this.fnFGroup = fnFGroup;
    }

    public static SubscriptionMetadata parse(String metadata) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(metadata, SubscriptionMetadata.class);
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter out = new IndentingPrintWriter(new PrintWriter(stringWriter));
        toString(out);
        out.close();
        return stringWriter.toString();
    }

    public void toString(IndentingWriter indentingWriter) {
        indentingWriter.println("Meta Data ");
        if(fnFGroup!=null){
            indentingWriter.incrementIndentation();
            indentingWriter.println("FnF Group");
            indentingWriter.incrementIndentation();
            fnFGroup.toString(indentingWriter);
            indentingWriter.decrementIndentation();
            indentingWriter.decrementIndentation();
        }
    }
}
