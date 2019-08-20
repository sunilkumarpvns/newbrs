package com.elitecore.netvertexsm.web.servergroup.form;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.servergroup.data.ServerInstanceGroupData;
import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

/**
 * Created by aditya on 11/5/16.
 */
public class ServerInstanceGroupForm  extends BaseWebForm {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
    private String name;
    private String accessGroups;
    private Integer orderNo;
    private List<String> groupNameList;
    private String groupNames;
    private boolean sessionSynchronization;

    private List<ServerInstanceGroupData> serverInstanceGroupDataList;
    
    private long pageNumber;
    private long totalPages;
    private long totalRecords;
    private String actionName;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(String accessGroups) {
        this.accessGroups = accessGroups;
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

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public List<ServerInstanceGroupData> getServerInstanceGroupDataList() {
        return serverInstanceGroupDataList;
    }

    public void setServerInstanceGroupDataList(List<ServerInstanceGroupData> serverInstanceGroupDataList) {
        this.serverInstanceGroupDataList = serverInstanceGroupDataList;
    }

	public List<String> getGroupNameList() {
		return groupNameList;
	}

	public void setGroupNameList(List<String> groupNameList) {
		this.groupNameList = groupNameList;
	}

	public String getGroupNames() {
		return groupNames;
	}

	public void setGroupNames(String groupNames) {
		this.groupNames = groupNames;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

    public boolean getSessionSynchronization() {
        return sessionSynchronization;
    }

    public void setSessionSynchronization(boolean sessionSynchronization) {
        this.sessionSynchronization = sessionSynchronization;
    }
}
