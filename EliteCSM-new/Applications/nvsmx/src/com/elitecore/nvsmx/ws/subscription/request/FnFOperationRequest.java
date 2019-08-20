package com.elitecore.nvsmx.ws.subscription.request;

import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.ws.interceptor.BaseWebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextInjector;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

public class FnFOperationRequest extends BaseWebServiceRequest {

    public enum Operation{ ADD, REMOVE }

    private String subscriberId;
    private String alternateId;
    private String groupName;
    private List<String> members;
    private Operation operation;
    private String parameter1;
    private String parameter2;
    private String webServiceName;
    private String webServiceMethodName;

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @ApiModelProperty(hidden=true)
    public Operation getOperation() {
        return operation;
    }

    @JsonIgnore
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    @ApiModelProperty(hidden=true)
    public String getWebServiceName() {
        return this.webServiceName;
    }

    @Override
    @ApiModelProperty(hidden=true)
    public String getWebServiceMethodName() {
        return this.webServiceMethodName;
    }

    @Override
    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    @Override
    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    @Override
    public void visit(DiagnosticContextInjector manager) {
        manager.add(this);
    }

    public void setWebServiceName(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    public void setWebServiceMethodName(String webServiceMethodName) {
        this.webServiceMethodName = webServiceMethodName;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.CUSTOM_TO_STRING_STYLE);

        builder.append("Subscriber Id", subscriberId);
        builder.append("Alternate Id", alternateId);
        builder.append("FnF Group Name", groupName);
        builder.append("Members", members);
        builder.append("Operation", operation==null?"":operation.name());
        builder.append("Parameter 1", getParameter1());
        builder.append("Parameter 2", getParameter2());

        return builder.toString();
    }
}
