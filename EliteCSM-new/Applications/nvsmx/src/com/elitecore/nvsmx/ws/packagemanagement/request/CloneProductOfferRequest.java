package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;

public class CloneProductOfferRequest extends BaseWebServiceRequest {

    private String name;
    private String newName;
    private String groups;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    public CloneProductOfferRequest(String name, String newName, String groups, String parameter1, String parameter2, String webServiceName, String webServiceMethodName) {
        this.name = name;
        this.newName = newName;
        this.groups = groups;
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

    public String getGroups() {
        return groups;
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
        builder.append("Parameter 1 ", getParameter1());
        builder.append("Parameter 2 ", getParameter2());

        return builder.toString();
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }
}
