package com.elitecore.nvsmx.ws.packagemanagement.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class ClonePackageRestRequest {

    private String name;
    private String newName;
    private String groups;
    private String type;

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

    @JsonProperty("type")
    @ApiModelProperty(required = true, dataType = "String", allowEmptyValue = false, allowableValues = "Data, RnC")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
