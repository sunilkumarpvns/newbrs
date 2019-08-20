package com.elitecore.corenetvertex.pm;

import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;

import java.io.Serializable;
import java.util.Map;

public class PolicyBackUpData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Map<String, com.elitecore.corenetvertex.pm.pkg.datapackage.Package> packageById;
	private Map<String, IMSPackage> imsPackageById;
	private Map<String, QuotaTopUp> quotatopUpById;
	private DeploymentMode deploymentMode;

	public PolicyBackUpData(Map<String, Package> packageById,
							Map<String, IMSPackage> imsPackageById,
							Map<String, QuotaTopUp> quotatopUpById,
							DeploymentMode deploymentMode) {
		
		this.packageById = packageById;
		this.imsPackageById = imsPackageById;
		this.quotatopUpById = quotatopUpById;
		this.deploymentMode = deploymentMode;
	}

	public Map<String, Package> getPackageById() {
		return packageById;
	}

	public Map<String, IMSPackage> getImsPackageById() {
		return imsPackageById;
	}

	public Map<String, QuotaTopUp> getQuotatopUpById() {
		return quotatopUpById;
	}

	public DeploymentMode getDeploymentMode() {
		return deploymentMode;
	}
}
