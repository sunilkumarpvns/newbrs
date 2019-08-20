package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class CloneProductOfferRestRequest {

    private String name;
    private String newName;
    private String groups;

    @JsonProperty("name")
    @ApiModelProperty(required = true, dataType = "String", allowEmptyValue = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("newName")
    @ApiModelProperty(required = true, dataType = "String", allowEmptyValue = false)
    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @JsonProperty("groups")
    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "CloneProductOfferRestRequest{" +
                "name='" + name + '\'' +
                ", newName='" + newName + '\'' +
                ", groups='" + groups + '\'' +
                '}';
    }
}
