package com.elitecore.nvsmx.sm.controller.network;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.routing.mccmncgroup.MccMncGroupData;
import com.elitecore.corenetvertex.sm.routing.network.BrandData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by vikas on 14/9/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/network")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","network"}),

})
public class NetworkCTRL extends RestGenericCTRL<NetworkData> {
	
	private static final long serialVersionUID = 1L;
	private List<CountryData> countryList = new ArrayList<>();
    private List<BrandData> brandList = new ArrayList<>();
    private List<OperatorData> operatorList = new ArrayList<>();

	@Override
    public ACLModules getModule() {
        return ACLModules.NETWORK;
    }

    @Override
    public NetworkData createModel() {
        return new NetworkData();
    }

	@Override
	public void prepareValuesForSubClass() throws Exception {
		setCountryList(CRUDOperationUtil.findAll(CountryData.class));
		setOperatorList(CRUDOperationUtil.findAll(OperatorData.class));
		setBrandList(CRUDOperationUtil.findAll(BrandData.class));
	}

	@Override
	protected boolean prepareAndValidateDestroy(NetworkData networkData) {
		List<MccMncGroupData> mccMncGroupDataList = findAssociatedMCCMNCGroup(networkData);
		if (Collectionz.isNullOrEmpty(mccMncGroupDataList) == false) {
			MccMncGroupData mccMncGroupData = mccMncGroupDataList.get(0);
			addActionError("Network " + networkData.getName() + " is associated with MCC-MNC Group" );
			getLogger().error(getLogModule(), "Error while deleting " + networkData.getName() + " Reason. Network is associated with MCC-MNC group " + mccMncGroupData.getName());
			return false;
		}
		return true;
	}

	@Override
	public void validate() {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called validate()");
		}
		NetworkData networkData = (NetworkData) getModel();

		validateCountryData(networkData);
		validateOperatorData(networkData);
		validateBrandData(networkData);

		super.validate();
	}

	private void validateCountryData(NetworkData networkData) {
		if (Strings.isNullOrBlank(networkData.getCountryId()) == false) {
			CountryData countryData = CRUDOperationUtil.get(CountryData.class, networkData.getCountryId());
			if (countryData == null) {
				addFieldError("countryId", getText("network.countryid.invalid"));
			}
			networkData.setCountryData(countryData);
		} else {
			addFieldError("countryId", getText("network.countryid.invalid"));
		}
	}

	private void validateOperatorData(NetworkData networkData) {
		if (Strings.isNullOrBlank(networkData.getOperatorId()) == false) {
			OperatorData operatorData = CRUDOperationUtil.get(OperatorData.class, networkData.getOperatorId());
			if (operatorData == null) {
				addFieldError("operatorId", getText("network.operatorid.invalid"));
			}
			networkData.setOperatorData(operatorData);
		} else {
			addFieldError("operatorId", getText("network.operatorid.invalid"));
		}
	}

	private void validateBrandData(NetworkData networkData) {
		if (Strings.isNullOrBlank(networkData.getBrandId()) == false) {
			BrandData brandData = CRUDOperationUtil.get(BrandData.class, networkData.getBrandId());
			if (brandData == null) {
				addFieldError("brandId", getText("network.brandid.invalid"));
			}
			networkData.setBrandData(brandData);
		} else {
			addFieldError("brandId", getText("network.brandid.invalid"));
		}
	}


	public List<CountryData> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<CountryData> countryList) {
		this.countryList = countryList;
	}
	
	public List<BrandData> getBrandList() {
		return brandList;
	}

	public void setBrandList(List<BrandData> brandList) {
		this.brandList = brandList;
	}

	public List<OperatorData> getOperatorList() {
		return operatorList;
	}

	public void setOperatorList(List<OperatorData> operatorList) {
		this.operatorList = operatorList;
	}

	public List findAssociatedMCCMNCGroup(NetworkData networkData){
		DetachedCriteria mccmncGroupData = DetachedCriteria.forClass(MccMncGroupData.class);
		mccmncGroupData.createAlias("networkDatas", "network");
		mccmncGroupData.add(Restrictions.eq("network.id",networkData.getId()));
		return CRUDOperationUtil.findAllByDetachedCriteria(mccmncGroupData);
	}
}