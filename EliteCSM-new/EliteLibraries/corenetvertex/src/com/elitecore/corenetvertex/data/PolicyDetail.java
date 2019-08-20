package com.elitecore.corenetvertex.data;

import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;

import javax.annotation.Nullable;
import java.io.Serializable;


public class PolicyDetail implements Serializable, Comparable<PolicyDetail> {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String name;
    private final PolicyStatus status;
    private final String packageType;
    private final String entityType;
    private final String remark;
    private final PkgMode packageMode;

    public PolicyDetail(String id, String name, PolicyStatus status, String packageType, String entityType, PkgMode packageMode, @Nullable String remark) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.packageType = packageType;
        this.entityType = entityType;
        this.packageMode = packageMode;
        this.remark = remark;
    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }

    public String getPackageType() {
        return packageType;
    }

    public String getEntityType() {
        return entityType;
    }

    public PkgMode getPackageMode() {
        return packageMode;
    }

    @Override
    public String toString() {
        return "PolicyDetail [name=" + name
                + ", status=" + status
                + ", type=" + packageType
                + ", entity=" + entityType
                + ", package-mode=" + packageMode
                + ", remark=" + remark + "]";
    }

    @Override
    public int compareTo(PolicyDetail policyDetail) {

        if (status == policyDetail.status) {
            return policyDetail.name.compareToIgnoreCase(name);
        }

        if (status == PolicyStatus.FAILURE) {
            return 1;
        }

        if (policyDetail.status == PolicyStatus.FAILURE) {
            return -1;
        }

        if (status == PolicyStatus.PARTIAL_SUCCESS) {
            return 1;
        }

        if (policyDetail.status == PolicyStatus.PARTIAL_SUCCESS) {
            return -1;
        }

        if (status == PolicyStatus.LAST_KNOWN_GOOD) {
            return 1;
        }

        if (policyDetail.status == PolicyStatus.LAST_KNOWN_GOOD) {
            return -1;
        }

        return policyDetail.name.compareToIgnoreCase(name);
    }

}
