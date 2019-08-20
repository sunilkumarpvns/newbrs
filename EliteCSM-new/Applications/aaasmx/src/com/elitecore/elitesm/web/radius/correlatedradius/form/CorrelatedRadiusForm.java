package com.elitecore.elitesm.web.radius.correlatedradius.form;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class CorrelatedRadiusForm extends  BaseWebForm{

    private String id;
    private String Name;
    private String Description;
    private String authEsiName;
    private String acctEsiName;
    private List<ExternalSystemInterfaceInstanceData> authExternalSystemInterfaceInstanceData;
    private List<ExternalSystemInterfaceInstanceData> acctExternalSystemInterfaceInstanceData;

    private String auditUId;
    private String action;
    private long pageNumber;
    private long totalPages;
    private long totalRecords;
    private Collection correlatedEsiData;
    private List correlatedEsiList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAuthEsiName() {
        return authEsiName;
    }

    public void setAuthEsiName(String authEsiName) {
        this.authEsiName = authEsiName;
    }

    public String getAcctEsiName() {
        return acctEsiName;
    }

    public void setAcctEsiName(String acctEsiName) {
        this.acctEsiName = acctEsiName;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public String getAuditUId() {
        return auditUId;
    }

    public void setAuditUId(String auditUId) {
        this.auditUId = auditUId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Collection getCorrelatedEsiData() {
        return correlatedEsiData;
    }

    public void setCorrelatedEsiData(Collection correlatedEsiData) {
        this.correlatedEsiData = correlatedEsiData;
    }

    public List getCorrelatedEsiList() {
        return correlatedEsiList;
    }

    public void setCorrelatedEsiList(List correlatedEsiList) {
        this.correlatedEsiList = correlatedEsiList;
    }

    public List<ExternalSystemInterfaceInstanceData> getAuthExternalSystemInterfaceInstanceData() {
        return authExternalSystemInterfaceInstanceData;
    }

    public void setAuthExternalSystemInterfaceInstanceData(List<ExternalSystemInterfaceInstanceData> authExternalSystemInterfaceInstanceData) {
        this.authExternalSystemInterfaceInstanceData = authExternalSystemInterfaceInstanceData;
    }

    public List<ExternalSystemInterfaceInstanceData> getAcctExternalSystemInterfaceInstanceData() {
        return acctExternalSystemInterfaceInstanceData;
    }

    public void setAcctExternalSystemInterfaceInstanceData(List<ExternalSystemInterfaceInstanceData> acctExternalSystemInterfaceInstanceData) {
        this.acctExternalSystemInterfaceInstanceData = acctExternalSystemInterfaceInstanceData;
    }
}
