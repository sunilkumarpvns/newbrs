package com.elitecore.corenetvertex.pkg;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.core.validator.PkgValidator;
import com.elitecore.corenetvertex.pkg.importpkg.PkgImportOperation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="pkg-container")
public class PkgContainer {
	
	private List<PkgData> pkgData;


	public PkgContainer(){
		this.pkgData = Collectionz.newArrayList();
	}


	@XmlElementWrapper(name="pkgDatas")
	@XmlElement(name="pkgData")
	@Import(required = true, validatorClass = PkgValidator.class, importClass = PkgImportOperation.class)
	public List<PkgData> getPkgData() {
		return pkgData;
	}

	public void setPkgData(List<PkgData> pkgData) {
		this.pkgData = pkgData;
	}
	

}
