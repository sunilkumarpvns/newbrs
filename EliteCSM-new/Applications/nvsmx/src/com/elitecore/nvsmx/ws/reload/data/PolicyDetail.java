package com.elitecore.nvsmx.ws.reload.data;


import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class PolicyDetail implements Serializable {

    private  String name;
    private  PolicyStatus status;
    private  String type;
    private  String subType;
    private  String remark;
    private  PkgMode packageMode;

    public PolicyDetail(String name, PolicyStatus status, String packageType, String subType, PkgMode packageMode, @Nullable String remark) {
        this.name = name;
        this.status = status;
        this.type = packageType;
        this.subType = subType;
        this.packageMode = packageMode;
        this.remark = remark;
    }

    public PolicyDetail() {
    }

    private static final long serialVersionUID = 1L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public PkgMode getPackageMode() {
        return packageMode;
    }

    public void setPackageMode(PkgMode packageMode) {
        this.packageMode = packageMode;
    }





    @Override
    public String toString() {
        return "PolicyDetail [name=" + name
                + ", status=" + status
                + ", type=" + type
                + ", sub-type="+subType
                + ", package-mode="+packageMode
                + ", remark=" + remark + "]";
    }

    //FIXME -- only sub type will be there
     public static PolicyDetail from(com.elitecore.corenetvertex.data.PolicyDetail policyDetail){
         return new PolicyDetail(policyDetail.getName(),policyDetail.getStatus(),
                 policyDetail.getPackageType() == null ? "":policyDetail.getPackageType(),
                 policyDetail.getPackageType() == null ? "":policyDetail.getPackageType(),
                 policyDetail.getPackageMode(),policyDetail.getRemark());
     }
}
