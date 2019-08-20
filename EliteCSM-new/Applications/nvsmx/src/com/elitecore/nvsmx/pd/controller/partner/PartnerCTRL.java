package com.elitecore.nvsmx.pd.controller.partner;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pd.partner.PartnerData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

@ParentPackage(value = "pd")
@Namespace("/pd/partner")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "partner" }),

})
public class PartnerCTRL extends RestGenericCTRL<PartnerData> {

	private static final long serialVersionUID = -5141302977149145728L;

	private List<CountryData> countryDataList = Collectionz.newArrayList();
	private List<RegionData> regionDataList = Collectionz.newArrayList();
	private List<CityData> cityDataList = Collectionz.newArrayList();

	private String accountDataAsJson;

	@Override
	public ACLModules getModule() {
		return ACLModules.PARTNER;
	}

	@Override
	public PartnerData createModel() {
		return new PartnerData();
	}

	@SkipValidation
	@Override
	public void prepareValuesForSubClass() throws Exception {
		setCountryDataList(CRUDOperationUtil.findAll(CountryData.class));
		setRegionDataList(CRUDOperationUtil.findAll(RegionData.class));
		setCityDataList(CRUDOperationUtil.findAll(CityData.class));
	}

	@SuppressWarnings("unchecked")
	@Override
	@SkipValidation
	public HttpHeaders show() { // View
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called show()");
		}
		PartnerData model = (PartnerData) getModel();

		try {
			PartnerData partnerData = CRUDOperationUtil.get((Class<PartnerData>) model.getClass(), model.getId(),
					getAdditionalCriteria());

			if (partnerData == null) {
				addActionError(getModule().getDisplayLabel() + " Not Found with id " + model.getId());
				return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
			}
			if (partnerData.getGroups() != null) {
				String belongingsGroups = GroupDAO
						.getGroupNames(CommonConstants.COMMA_SPLITTER.split(model.getGroups()));
				partnerData.setGroupNames(belongingsGroups);
			}

			List<AccountData> listAccount = CRUDOperationUtil.findAll((Class<AccountData>) AccountData.class);
			List<AccountData> tempAccountList = new ArrayList<>();
			for (AccountData account : listAccount) {
				if (account.getPartnerData() != null && account.getPartnerData().getId().equals(partnerData.getId())) {
					tempAccountList.add(account);
				}
			}
			setAccountDataWrappers(tempAccountList);
			partnerData.setAccountData(tempAccountList);
			setModel(partnerData);

		} catch (Exception e) {
			addActionError("Fail to view " + getModule().getDisplayLabel() + " for id ");
			getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + " for id "
					+ model.getId() + " .Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
		}
		setActionChainUrl(getRedirectURL(METHOD_SHOW));
		return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);

	}

	private void setAccountDataWrappers(List<AccountData> accountList) {
		Gson gson = GsonFactory.defaultInstance();
		setAccountDataAsJson(gson.toJsonTree(accountList).getAsJsonArray().toString());
	}

	public String getCountryDataList() {
		Gson gson = GsonFactory.defaultInstance();
		JsonArray modelJson = gson.toJsonTree(countryDataList, new TypeToken<List<CountryData>>() {
		}.getType()).getAsJsonArray();
		return modelJson.toString();
	}

	public void setCountryDataList(List<CountryData> countryDataList) {
		this.countryDataList = countryDataList;
	}

	public String getRegionDataList() {
		Gson gson = GsonFactory.defaultInstance();
		JsonArray modelJson = gson.toJsonTree(regionDataList, new TypeToken<List<RegionData>>() {
		}.getType()).getAsJsonArray();
		return modelJson.toString();
	}

	public void setRegionDataList(List<RegionData> regionDataList) {
		this.regionDataList = regionDataList;
	}

	public String getCityDataList() {
		Gson gson = GsonFactory.defaultInstance();
		JsonArray modelJson = gson.toJsonTree(cityDataList, new TypeToken<List<CityData>>() {
		}.getType()).getAsJsonArray();
		return modelJson.toString();
	}

	public void setCityDataList(List<CityData> cityDataList) {
		this.cityDataList = cityDataList;
	}

	public String getAccountDataAsJson() {
		return accountDataAsJson;
	}

	public void setAccountDataAsJson(String accountDataAsJson) {
		this.accountDataAsJson = accountDataAsJson;
	}

	@Override
	public void validate() {
		PartnerData partnerData = (PartnerData) getModel();

		CountryData countryData = CRUDOperationUtil.get(CountryData.class, partnerData.getCountryId());
		if (countryData == null) {
			addFieldError("countryId", getText("partner.country.invalid"));
		}

		RegionData regionData = CRUDOperationUtil.get(RegionData.class, partnerData.getRegionId());

		if (regionData == null) {
			addFieldError("regionId", getText("partner.region.invalid"));
		}

		CityData cityData = CRUDOperationUtil.get(CityData.class, partnerData.getCityId());
		if (cityData == null) {
			addFieldError("productSpecificationId", getText("partner.city.invalid"));
		}

		if (partnerData.getIsUnsignedPartner() != null) {
			String booleanRegex = "[Tt]rue|[Ff]alse";
			boolean isMatched = partnerData.getIsUnsignedPartner().toString().matches(booleanRegex);
			if (isMatched == false) {
				addFieldError("isUnsignedPartner", getText("partner.isunsignedpartner.invalid"));
			}
		}
		super.validate();
	}
}
