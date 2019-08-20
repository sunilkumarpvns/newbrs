package com.elitecore.nvsmx.policydesigner.model.pkg.pccrule;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by aditya on 9/29/16.
 */
@XmlRootElement(name="global-pcc-rule-container")
public class PCCRuleContainer {

    private List<PCCRuleData> pccRules;
    public PCCRuleContainer(){
        this.pccRules = Collectionz.newArrayList();
    }


    @XmlElementWrapper(name="pccRuleDatas")
    @XmlElement(name="pccRuleData")
    public List<PCCRuleData> getPccRules() {
        return pccRules;
    }

    public void setPccRules(List<PCCRuleData> pccRuleDatas) {
        this.pccRules = pccRuleDatas;
    }

}
