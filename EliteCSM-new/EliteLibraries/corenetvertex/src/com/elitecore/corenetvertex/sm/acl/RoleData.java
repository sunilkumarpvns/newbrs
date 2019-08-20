package com.elitecore.corenetvertex.sm.acl;

import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 8/22/17.
 */
@Entity(name="com.elitecore.corenetvertex.sm.acl.RoleData")
@Table(name="TBLM_ROLE")
public class RoleData extends ResourceData implements Serializable{

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String roleType;
    private String systemGenerated;
    private List<RoleModuleActionData> roleModuleActionData;

    public RoleData() {
        this.roleModuleActionData = new ArrayList<>();
    }

    @Column(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="ROLE_TYPE")
    @JsonIgnore
    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    @Column(name="NAME")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name="SYSTEM_GENERATED")
    @JsonIgnore
    public String getSystemGenerated() {
        return systemGenerated;
    }
    public void setSystemGenerated(String systemGenerated) {
        this.systemGenerated = systemGenerated;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "roleData", orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    public List<RoleModuleActionData> getRoleModuleActionData() {
        return roleModuleActionData;
    }
    public void setRoleModuleActionData(List<RoleModuleActionData> roleModuleActionData) {
        this.roleModuleActionData = roleModuleActionData;
    }
    @Override
    @Column(name = "STATUS")
    @XmlElement(name="status")
    public String getStatus() {
        return super.getStatus();
    }
    @Override
    public void setStatus(String status) {
        super.setStatus(status);
    }

    @Transient
    @Override
    public String getResourceName() {
        return getName();
    }



    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", name)
                .append("Description", description);


        return toStringBuilder.toString();
    }


    @Override
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Name", name);
        jsonObject.addProperty("Description", description);
        if(roleModuleActionData != null){
            JsonObject configuredRoleModuleActions = new JsonObject();
            for(RoleModuleActionData roleModuleActions: roleModuleActionData){
                JsonObject configuredRoles = new JsonObject();
                configuredRoles.addProperty("Actions",roleModuleActions.getActions());
                configuredRoleModuleActions.add(roleModuleActions.getModuleName(),configuredRoles);
            }
            jsonObject.add("Role Module Actions", configuredRoleModuleActions);
        }
        return jsonObject;
    }



}
