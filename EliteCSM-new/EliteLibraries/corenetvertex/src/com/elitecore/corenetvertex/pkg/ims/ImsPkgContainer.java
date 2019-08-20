package com.elitecore.corenetvertex.pkg.ims;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.core.validator.IMSPkgValidator;
import com.elitecore.corenetvertex.pkg.importimspkg.IMSPkgImportOperation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * A container class that contains list of ims packages
 * Created by Ishani on 17/10/16.
 */
@XmlRootElement(name="ims-pkg-container")
public class ImsPkgContainer {


        private List<IMSPkgData> imsPkgData;


        public ImsPkgContainer(){
            this.imsPkgData = Collectionz.newArrayList();
        }


        @XmlElementWrapper(name="imsPkgDatas")
        @XmlElement(name="imsPkgData")
        @Import(required = true, validatorClass = IMSPkgValidator.class, importClass = IMSPkgImportOperation.class)
        public List<IMSPkgData> getImsPkgData() {
            return imsPkgData;
        }

        public void setImsPkgData(List<IMSPkgData> imsPkgData) {
            this.imsPkgData = imsPkgData;
        }


}
