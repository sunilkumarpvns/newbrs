package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class ClonePackageRequest extends BaseWebServiceRequest {

    private String name;
    private String newName;
    private String groups;
    private String type;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    public ClonePackageRequest(String name, String newName, String groups, String type, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.name = name;
        this.newName = newName;
        this.groups = groups;
        this.type = type;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.webServiceName = webServiceName;
        this.webServiceMethodName = webServiceMethodName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String group) {
        this.groups = groups;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getWebServiceName() {
        return webServiceName;
    }

    @Override
    public String getWebServiceMethodName() {
        return webServiceMethodName;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    @Override
    public String toString(){
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

        builder.append("Name ", getName());
        builder.append("New Name ", getNewName());
        builder.append("Group ", getGroups());
        builder.append("Type ", getType());
        builder.append("Parameter 1 ", getParameter1());
        builder.append("Parameter 2 ", getParameter2());

        return builder.toString();
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }
}
