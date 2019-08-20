package com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by RAJ KIRPALSINH
 */
@XmlRootElement(name="global-charging-rule-base-name-container")
public class ChargingRuleBaseNameContainer {

    private List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas;
    public ChargingRuleBaseNameContainer(){
        this.chargingRuleBaseNameDatas = Collectionz.newArrayList();
    }


    @XmlElementWrapper(name="chargingRuleBaseNameDatas")
    @XmlElement(name="chargingRuleBaseNameData")
    public List<ChargingRuleBaseNameData> getChargingRuleBaseNameDatas() {
        return chargingRuleBaseNameDatas;
    }

    public void setChargingRuleBaseNameDatas(List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas) {
        this.chargingRuleBaseNameDatas = chargingRuleBaseNameDatas;
    }

}
