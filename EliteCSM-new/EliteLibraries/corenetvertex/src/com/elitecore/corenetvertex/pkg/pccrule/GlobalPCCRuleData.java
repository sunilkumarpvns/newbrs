package com.elitecore.corenetvertex.pkg.pccrule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * This class is introduced in order to provide bifurcation of global pcc rules while performing import/export
 * Created by Ishani on 4/8/16.
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class GlobalPCCRuleData {

    private String id;
    private String name;


    public GlobalPCCRuleData(){

    }
    public GlobalPCCRuleData(String id,String name,PCCRuleScope scope){
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
