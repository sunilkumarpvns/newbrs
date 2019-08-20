package com.elitecore.corenetvertex.pkg;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.core.validator.EmergencyPkgValidator;
import com.elitecore.corenetvertex.pkg.importemergencypkg.EmergencyPkgImportOperation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Ishani on 6/12/16.
 */
@XmlRootElement(name="emergency-pkg-container")
public class EmergencyPkgContainer {

    private List<EmergencyPkgDataExt> emergencyPkgData;


    public EmergencyPkgContainer(){
        this.emergencyPkgData = Collectionz.newArrayList();
    }


    @XmlElementWrapper(name="emergencyPkgDatas")
    @XmlElement(name="emergencyPkgData")
    @Import(required = true, validatorClass = EmergencyPkgValidator.class, importClass = EmergencyPkgImportOperation.class)
    public List<EmergencyPkgDataExt> getEmergencyPkgData() {
        return emergencyPkgData;
    }

    public void setEmergencyPkgData(List<EmergencyPkgDataExt> emergencyPkgData) {
        this.emergencyPkgData = emergencyPkgData;
    }
}
