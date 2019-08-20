package com.elitecore.corenetvertex.sm.acl;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.google.gson.JsonObject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Set;

@Entity(name = "com.elitecore.corenetvertex.sm.acl.RoleModuleActionData")
@Table(name = "TBLM_ROLE_MODULE_ACTIONS_REL")
public class RoleModuleActionData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String moduleName;
    private String actions;

    private transient RoleData roleData;
    private transient Set<ACLAction> aclActions; //NOSONAR


    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @XmlElement(name = "id")
    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Column(name = "MODULE_NAME")
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Column(name = "ACTIONS")
    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_ID")
    @XmlTransient
    @JsonIgnore
    public RoleData getRoleData() {
        return roleData;
    }

    public void setRoleData(RoleData roleData) {
        this.roleData = roleData;
    }


    @Transient
    @JsonIgnore
    public ACLModules getAclModules() {
        return ACLModules.getModuleByName(moduleName);
    }

    @Transient
    @JsonIgnore
    public Set<ACLAction> getAclActions() {

        if (aclActions == null) {
            aclActions = Collectionz.newHashSet();
            if (Strings.isNullOrBlank(actions) == false) {
                for (String action : actions.split(",")) {
                    aclActions.add(ACLAction.valueOf(action));
                }
            }
        }

        return aclActions;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof RoleModuleActionData)){
            return false;
        }

        RoleModuleActionData that = (RoleModuleActionData) o;

        if (!getModuleName().equals(that.getModuleName())){
            return false;
        }
        return getRoleData().getId().equals(that.getRoleData().getId());
    }

    @Override
    public int hashCode() {
        int result = getModuleName().hashCode();
        result = 31 * result + getRoleData().hashCode();
        return result;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Module Name", moduleName);
        jsonObject.addProperty("Actions", actions);
        return jsonObject;
    }

}
