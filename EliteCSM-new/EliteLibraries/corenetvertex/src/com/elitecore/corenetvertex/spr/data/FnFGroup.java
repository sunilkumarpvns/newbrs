package com.elitecore.corenetvertex.spr.data;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class FnFGroup {
    private String name;
    private List<String> members = new ArrayList<>();

    public FnFGroup(){

    }

    public FnFGroup(String name, List<String> members) {
        this.name = name;
        if(members!=null){
            this.members = members;
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }

    public boolean isMemberExist(String member){
        return members.contains(member);
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
        if(name!=null){
            indentingWriter.println("Name = "+ name);
            indentingWriter.println("Members = "+ members);
        }
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
