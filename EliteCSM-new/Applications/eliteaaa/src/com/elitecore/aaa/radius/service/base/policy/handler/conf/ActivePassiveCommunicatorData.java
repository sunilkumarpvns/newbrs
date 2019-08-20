package com.elitecore.aaa.radius.service.base.policy.handler.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "active-passive-esi")
@XmlType(propOrder = {"activeEsiName","passiveEsiName","loadFactor"})
public class ActivePassiveCommunicatorData {

    private String activeEsiName;
    private String passiveEsiName;
    private Integer loadFactor;

    public ActivePassiveCommunicatorData() {
		// for JAXB
	}
    
    public ActivePassiveCommunicatorData(String activeEsiName, String passiveEsiName, Integer loadFactor) {
		this.activeEsiName = activeEsiName;
		this.passiveEsiName = passiveEsiName;
		this.loadFactor = loadFactor;
	}

	@XmlElement(name = "active-esi-name")
    public String getActiveEsiName() {
        return activeEsiName;
    }

    public void setActiveEsiName(String activeEsiName) {
        this.activeEsiName = activeEsiName;
    }

    @XmlElement(name = "passive-esi-name")
    public String getPassiveEsiName() {
        return passiveEsiName;
    }

    public void setPassiveEsiName(String passiveEsiName) {
        this.passiveEsiName = passiveEsiName;
    }

    @XmlElement(name = "load-factor")
    public Integer getLoadFactor() {
        return loadFactor;
    }

    public void setLoadFactor(Integer loadFactor) {
        this.loadFactor = loadFactor;
    }

    @Override
    public String toString() {
        return "Active ESI Name = " + this.activeEsiName + ", Active ESI Name = " + this.passiveEsiName + ", Load-Factor = " + this.loadFactor + "";
    }
    
}