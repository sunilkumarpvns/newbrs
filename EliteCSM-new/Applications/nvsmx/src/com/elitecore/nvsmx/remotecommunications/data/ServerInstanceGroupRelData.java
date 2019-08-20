package com.elitecore.nvsmx.remotecommunications.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.elitecore.nvsmx.commons.model.sessionmanager.NetServerInstanceData;
import org.apache.commons.lang.SystemUtils;

@Entity
@Table(name = "TBLM_NET_SERVER_GROUP_REL")
public class ServerInstanceGroupRelData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final ServerInstanceGroupDataToString SERVER_INSTANCE_GROUP_DATA_TO_STRING = new ServerInstanceGroupDataToString();
    private String groupId;
    private NetServerInstanceData netServerInstanceData;
    private String serverWeight;

    @Id
    @Column(name = "ID")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Id
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "NETSERVER_INSTANCE_ID")
    public NetServerInstanceData getNetServerInstanceData() {
        return netServerInstanceData;
    }

    public void setNetServerInstanceData(NetServerInstanceData instanceId) {
        this.netServerInstanceData = instanceId;
    }

    @Column(name = "SERVER_WEIGHTAGE")
    public String getServerWeight() {
        return serverWeight;
    }

    public void setServerWeight(String serverWeight) {
        this.serverWeight = serverWeight;
    }


    @Override
    public String toString() {
        return toString(SERVER_INSTANCE_GROUP_DATA_TO_STRING);

    }


    public String toString(ToStringStyle toStringStyle) {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, toStringStyle)
                .append(serverWeight.equalsIgnoreCase(String.valueOf(CommonConstants.PRIMARY_INSTANCE)) ? "Primary:" : "Secondary:").
                        append(netServerInstanceData);
        return toStringBuilder.toString();
    }


    private static final class ServerInstanceGroupDataToString extends ToStringStyle.CustomToStringStyle {

        private static final long serialVersionUID = 1L;

        ServerInstanceGroupDataToString() {
            super();
            this.setFieldSeparator(getSpaces(4) + getTabs(1));
        }
    }

}
