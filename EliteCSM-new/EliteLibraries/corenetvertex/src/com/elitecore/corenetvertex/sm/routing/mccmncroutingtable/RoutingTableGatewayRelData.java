package com.elitecore.corenetvertex.sm.routing.mccmncroutingtable;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
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
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author jaidiptrivedi
 */

@Entity(name = "com.elitecore.corenetvertex.sm.routing.mccmncroutingtable.RoutingTableGatewayRelData")
@Table(name = "TBLM_ROUTING_ENTRY_GATEWAY_REL")
@XmlRootElement
public class RoutingTableGatewayRelData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private RoutingTableData routingTableData;
    private DiameterGatewayData diameterGatewayData;
    private Integer weightage;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROUTING_ID")
    @JsonIgnore
    public RoutingTableData getRoutingTableData() {
        return routingTableData;
    }

    public void setRoutingTableData(RoutingTableData routingTableData) {
        this.routingTableData = routingTableData;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GATEWAY_ID")
    @JsonIgnore
    public DiameterGatewayData getDiameterGatewayData() {
        return diameterGatewayData;
    }

    public void setDiameterGatewayData(DiameterGatewayData diameterGatewayData) {
        this.diameterGatewayData = diameterGatewayData;
    }

    @Transient
    public String getGatewayId() {
        if (this.getDiameterGatewayData() != null) {
            return getDiameterGatewayData().getId();
        }
        return null;
    }

    public void setGatewayId(String gatewayId) {
        if (Strings.isNullOrBlank(gatewayId) == false) {
            DiameterGatewayData diameterGatewayData = new DiameterGatewayData();
            diameterGatewayData.setId(gatewayId);
            this.diameterGatewayData = diameterGatewayData;
        }
    }


    @Column(name = "WEIGHTAGE")
    public Integer getWeightage() {
        return weightage;
    }

    public void setWeightage(Integer weightage) {
        this.weightage = weightage;
    }
}
