package com.elitecore.corenetvertex.pkg.chargingrulebasename;

import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * This class is introduced in order to provide bifurcation of ChargingRuleBaseNameData rules while performing import/export
 * Created by RAJ KIRPALSINH
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ChargingRuleData {

    private String id;
    private String name;

    public ChargingRuleData(){ }

    public ChargingRuleData(String id, String name){
        this.id = id;
        this.name = name;
    }

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

}
