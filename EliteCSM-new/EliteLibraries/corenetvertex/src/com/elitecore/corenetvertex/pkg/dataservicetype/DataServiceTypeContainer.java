package com.elitecore.corenetvertex.pkg.dataservicetype;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.core.validator.DataServiceTypeValidatorExt;
import com.elitecore.corenetvertex.pkg.importpkg.DataServiceTypeImportOperation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * A class that is used for import-export operation that contains list of services
 * Created by Ishani on 14/9/16.
 */

@XmlRootElement(name="data-service-type-container")
public class DataServiceTypeContainer {

    private List<DataServiceTypeDataExt> dataServiceType;


    public DataServiceTypeContainer(){
        this.dataServiceType = Collectionz.newArrayList();
    }


    @XmlElementWrapper(name="dataServiceTypes")
    @XmlElement(name="dataServiceType")
    @Import(required = true, validatorClass = DataServiceTypeValidatorExt.class, importClass = DataServiceTypeImportOperation.class)
    public List<DataServiceTypeDataExt> getserviceType(
    ) {
        return dataServiceType;
    }

    public void setserviceType(List<DataServiceTypeDataExt> dataServiceType) {
        this.dataServiceType = dataServiceType;
    }


}

