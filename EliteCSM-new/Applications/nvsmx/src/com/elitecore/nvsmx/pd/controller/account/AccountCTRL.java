
package com.elitecore.nvsmx.pd.controller.account;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

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
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pd.partner.PartnerData;
import com.elitecore.corenetvertex.pd.partnergroup.PartnerGroupData;
import com.elitecore.corenetvertex.pd.prefixes.PrefixesData;
import com.elitecore.corenetvertex.pd.productoffer.ProductSpecData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;

@ParentPackage(value = "pd")
@Namespace("/pd/account")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "account" }), })

public class AccountCTRL extends RestGenericCTRL<AccountData> {

	private static final long serialVersionUID = 4168434638718855481L;

	private List<LobData> lobDataList = Collectionz.newArrayList();
	private List<PartnerGroupData> partnerGroupDataList = Collectionz.newArrayList();
	private List<ProductSpecData> productSpecDataList = Collectionz.newArrayList();

	private String prefixesDataAsJson;

	private String partnerIds;

	@Override
	public ACLModules getModule() {
		return ACLModules.ACCOUNT;
	}

	@Override
	public AccountData createModel() {
		return new AccountData();
	}

	@SkipValidation
	@Override
	public HttpHeaders index() { // list or search
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "Method called index()");
		}
		try {

			List<AccountData> list = CRUDOperationUtil.searchByCriteria(AccountData.class, getAdditionalCriteria());
			setList(list);
			setActionChainUrl(getRedirectURL(METHOD_INDEX));
			return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
		} catch (Exception e) {
			getLogger().error(getLogModule(), "Error while fetching " + getModule().getDisplayLabel()
					+ " information. Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
			addActionError("Fail to perform Search Operation");
		}
		return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
	}

	@Override
	public HttpHeaders create() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called create()");
		}
		AccountData model = (AccountData) getModel();
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(),
					"Creating " + getModule().name() + " with name: " + model.getResourceName());
		}
		try {
			String result = authorize();
			if (result.equals(SUCCESS) == false) {
				setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
				return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL)
						.withStatus(ResultCode.INTERNAL_ERROR.code);
			}
			if (model != null) {
				StaffData staffData = getStaffData();
				model.setCreatedDateAndStaff(staffData);

				if (Strings.isNullOrEmpty(model.getPartnerId())) {
					PartnerData partnerData = new PartnerData();
					partnerData.setId(partnerIds);
					model.setPartnerData(partnerData);
				}

				CRUDOperationUtil.save(model);
				String message = getModule().getDisplayLabel() + " <b><i>" + model.getResourceName() + "</i></b> "
						+ "Created";
				CRUDOperationUtil.audit(model, model.getResourceName(), AuditActions.CREATE, getStaffData(),
						getRequest().getRemoteAddr(), model.getHierarchy(), message);
			}

			addActionMessage(getModule().getDisplayLabel() + " created successfully");
			setActionChainUrl(
					CommonConstants.FORWARD_SLASH + getRedirectURL(model.getId()) + "?partnerId=" + partnerIds);
			return new DefaultHttpHeaders(REDIRECT_ACTION).setLocationId(model.getId());
		} catch (Exception e) {
			getLogger().error(getLogModule(), "Error while creating " + getModule().getDisplayLabel()
					+ " information. Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
			addActionError("Fail to perform create Operation");
		}
		return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
	}

	@Override
	public HttpHeaders update() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called update()");
		}
		AccountData model = (AccountData) getModel();
		if (Strings.isNullOrEmpty(model.getPartnerId())) {
			PartnerData partnerData = new PartnerData();
			partnerData.setId(partnerIds);
			model.setPartnerData(partnerData);
		}
		setModel(model);
		return super.update();
	}

	@Override
	public HttpHeaders show() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called show()");
		}

		setPartnerIds(getRequest().getParameter("partnerId"));
		AccountData accountData = (AccountData) getModel();
		AccountData resourceInDB = CRUDOperationUtil.get((Class<AccountData>) AccountData.class, accountData.getId(),
				getAdditionalCriteria());

		if (resourceInDB.getAccountPrefixMasterRelation() != null) {
			setPrefixesDataWrappers(
					resourceInDB.getAccountPrefixMasterRelation().getPrefixListMasterData().getPrefixesList());
		}

		return super.show();
	}

	@Override
	public HttpHeaders destroy() {// delete
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called delete()");
		}
		super.destroy();

		setActionChainUrl(CommonConstants.FORWARD_SLASH + "pd/partner/partner/"
				+ (String.valueOf(getRequest().getAttribute(NVSMXCommonConstants.PARTNER_ID))));
		return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
	}

	private void setPrefixesDataWrappers(List<PrefixesData> prefixList) {
		Gson gson = GsonFactory.defaultInstance();
		setPrefixesDataAsJson(gson.toJsonTree(prefixList).getAsJsonArray().toString());
	}

	@SkipValidation
	@Override
	public void prepareValuesForSubClass() throws Exception {
		setLobDataList(CRUDOperationUtil.findAll(LobData.class));
		setPartnerGroupDataList(CRUDOperationUtil.findAll(PartnerGroupData.class));
		setProductSpecDataList(CRUDOperationUtil.findAll(ProductSpecData.class));

		String partner = getRequest().getParameter(NVSMXCommonConstants.PARTNER_ID);
		if (Strings.isNullOrEmpty(partner) == false) {
			setPartnerIds(partner);
		}

	}

	public List<LobData> getLobDataList() {
		return lobDataList;
	}

	public void setLobDataList(List<LobData> lobDataList) {
		this.lobDataList = lobDataList;
	}

	public List<PartnerGroupData> getPartnerGroupDataList() {
		return partnerGroupDataList;
	}

	public void setPartnerGroupDataList(List<PartnerGroupData> partnerGroupDataList) {
		this.partnerGroupDataList = partnerGroupDataList;
	}

	public List<ProductSpecData> getProductSpecDataList() {
		return productSpecDataList;
	}

	public void setProductSpecDataList(List<ProductSpecData> productSpecDataList) {
		this.productSpecDataList = productSpecDataList;
	}

	public String getPartnerIds() {
		return partnerIds;
	}

	public void setPartnerIds(String partnerIds) {
		this.partnerIds = partnerIds;
	}

	public String getPrefixesDataAsJson() {
		return prefixesDataAsJson;
	}

	public void setPrefixesDataAsJson(String prefixesDataAsJson) {
		this.prefixesDataAsJson = prefixesDataAsJson;
	}

	@Override
	public void validate() {
		AccountData accountData = (AccountData) getModel();

		LobData lobData = CRUDOperationUtil.get(LobData.class, accountData.getLobId());
		if (lobData == null) {
			addFieldError("lobId", getText("account.lob.invalid"));
		}

		PartnerGroupData partnerGroupData = CRUDOperationUtil.get(PartnerGroupData.class,
				accountData.getPartnerGroupId());
		if (partnerGroupData == null) {
			addFieldError("partnerGroupId", getText("account.partnergroup.invalid"));
		}

		ProductSpecData productOfferData = CRUDOperationUtil.get(ProductSpecData.class,
				accountData.getProductSpecificationId());
		if (productOfferData == null) {
			addFieldError("productSpecificationId", getText("account.productspecification.invalid"));
		}

		super.validate();
	}
}