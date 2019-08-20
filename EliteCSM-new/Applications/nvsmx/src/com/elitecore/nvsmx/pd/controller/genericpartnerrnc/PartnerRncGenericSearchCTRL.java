package com.elitecore.nvsmx.pd.controller.genericpartnerrnc;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

import java.util.List;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.SimpleExpression;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.commons.model.generic.PartnerRncGenericData;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;

/**
 * Generic Controller for Partner RnC Child Entities that will be used for
 * generic search functionality.
 * 
 * @author gaurav.mishra
 */

@ParentPackage(value = "pd")
@Namespace("/pd/genericpartnerrnc")
@org.apache.struts2.convention.annotation.Results({
		@Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = { NVSMXCommonConstants.ACTION_NAME,
				"partner-rnc-generic-search" }), })
public class PartnerRncGenericSearchCTRL extends RestGenericCTRL<PartnerRncGenericData> {
	private static final long serialVersionUID = 4154031061918779097L;
	
	private static final String PATH = "pd/genericpartnerrnc/partner-rnc-generic-search";
	private PartnerRncGenericData partnerRncGenericData = new PartnerRncGenericData();
	
	@Override
	public ACLModules getModule() {
		return null;
	}

	@Override
	public PartnerRncGenericData createModel() {
		return new PartnerRncGenericData();
	}

	@Override
	public HttpHeaders index() {
		try {
			setActionChainUrl(CommonConstants.FORWARD_SLASH+PATH);
			return new DefaultHttpHeaders(NVSMXCommonConstants.GENERIC_PARTNER_RNC_SEARCH).disableCaching();
		} catch (Exception e) {
			getLogger().error(getLogModule(), "Error while fetching " + getModule().getDisplayLabel()
					+ " information. Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
			addActionError("Fail to perform Search Operation");
		}
		return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
	}
	
	@SkipValidation
	public HttpHeaders search() {
		try {
			if (Strings.isNullOrEmpty(partnerRncGenericData.getName())) {
				setList(Collectionz.newArrayList());
			} else {
				List partnerRnCgenericDataList = CRUDOperationUtil.findAll(
						Class.forName(partnerRncGenericData.getPartnerRnCModules().getEntityName()),
						getAdditionalCriteria());
				setList(partnerRnCgenericDataList);
			}
			setActionChainUrl(
					CommonConstants.FORWARD_SLASH + partnerRncGenericData.getPartnerRnCModules().getModulePath());
			setModel(partnerRncGenericData);
			return new DefaultHttpHeaders(NVSMXCommonConstants.GENERIC_PARTNER_RNC_SEARCH).disableCaching();
		} catch (Exception e) {
			getLogger().error(getLogModule(), "Error while fetching " + getModule().getDisplayLabel()
					+ " information. Reason: " + e.getMessage());
			getLogger().trace(getLogModule(), e);
			addActionError("Fail to perform Search Operation");
		}
		return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);

	}
	
	
	@Override
	protected SimpleExpression getAdditionalCriteria() {
		return Expression.like("name", partnerRncGenericData.getName(), MatchMode.ANYWHERE);
	}

	public PartnerRncGenericData getPartnerRncGenericData() {
		return partnerRncGenericData;
	}

	public void setPartnerRncGenericData(PartnerRncGenericData partnerRncGenericData) {
		this.partnerRncGenericData = partnerRncGenericData;
	}
}
