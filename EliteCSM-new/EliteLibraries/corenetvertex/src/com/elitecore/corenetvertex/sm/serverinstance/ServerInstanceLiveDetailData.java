package com.elitecore.corenetvertex.sm.serverinstance;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.DataSourceInfo;
import com.elitecore.corenetvertex.GatewayStatusInfo;
import com.elitecore.corenetvertex.GlobalListenersInfo;
import com.elitecore.corenetvertex.ServerInfo;
import com.elitecore.corenetvertex.ServiceInfo;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.sm.ResourceData;

import java.util.List;

public class ServerInstanceLiveDetailData extends ResourceData {

    private static final long serialVersionUID = 1L;

    private transient ServerInfo serverInfo;
    private transient List<ServiceInfo> serviceInfoList =  Collectionz.newArrayList();
    private transient List<GatewayStatusInfo> gatewayStatusInfoList = Collectionz.newArrayList();
    private transient List<GlobalListenersInfo> globalListenersInfoList = Collectionz.newArrayList();
    private transient List<DataSourceInfo> dataSourceInfoList =  Collectionz.newArrayList();
    private transient List<PolicyDetail> policyDetailList = Collectionz.newArrayList();

    public List<ServiceInfo> getServiceInfoList() {
        return serviceInfoList;
    }

    public void setServiceInfoList(List<ServiceInfo> serviceInfoList) {
        this.serviceInfoList = serviceInfoList;
    }

    public List<GatewayStatusInfo> getGatewayStatusInfoList() {
        return gatewayStatusInfoList;
    }

    public void setGatewayStatusInfoList(List<GatewayStatusInfo> gatewayStatusInfoList) {
        this.gatewayStatusInfoList = gatewayStatusInfoList;
    }

    public List<GlobalListenersInfo> getGlobalListenersInfoList() {
        return globalListenersInfoList;
    }

    public void setGlobalListenersInfoList(List<GlobalListenersInfo> globalListenersInfoList) {
        this.globalListenersInfoList = globalListenersInfoList;
    }

    public List<DataSourceInfo> getDataSourceInfoList() {
        return dataSourceInfoList;
    }

    public void setDataSourceInfoList(List<DataSourceInfo> dataSourceInfoList) {
        this.dataSourceInfoList = dataSourceInfoList;
    }

    public List<PolicyDetail> getPolicyDetailList() {
        return policyDetailList;
    }

    public void setPolicyDetailList(List<PolicyDetail> policyDetailList) {
        this.policyDetailList = policyDetailList;
    }

    @Override
    public String getResourceName() {
        return null;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Override
    public String toString() {
        return "ServerInstanceLiveDetailData{" +
                "serverInfo=" + serverInfo +
                ", serviceInfoList=" + serviceInfoList +
                ", gatewayStatusInfoList=" + gatewayStatusInfoList +
                ", globalListenersInfoList=" + globalListenersInfoList +
                ", dataSourceInfoList=" + dataSourceInfoList +
                ", policyDetailList=" + policyDetailList +
                '}';
    }

}