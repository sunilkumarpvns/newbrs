package com.elitecore.corenetvertex.sm.alerts;

import com.elitecore.corenetvertex.core.alerts.Alerts;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity(name = "com.elitecore.corenetvertex.sm.alerts.AlertListenerRelData")
@Table(name = "TBLM_ALERT_LISTENER_REL")
public class AlertListenerRelData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String type;
    private boolean floodControl;


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


    @Column(name = "ALERT_TYPE")
    public String getType() {
        return type;
    }

    public void setType(String typeId) {
        this.type = typeId;
    }

    @Column(name = "FLOOD_CONTROL")
    public boolean isFloodControl() {
        return floodControl;
    }

    public void setFloodControl(boolean floodControl) {
        this.floodControl = floodControl;
    }

    @Transient
    public Alerts from(){
          return Alerts.valueOf(type);
    }
}
