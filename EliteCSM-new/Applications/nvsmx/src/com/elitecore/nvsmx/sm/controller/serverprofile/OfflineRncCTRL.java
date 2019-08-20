package com.elitecore.nvsmx.sm.controller.serverprofile;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.ServerGroups;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.serverprofile.OfflineRncServerProfileData;
import com.elitecore.nvsmx.sm.controller.CreateNotSupportedCTRL;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.util.List;

import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = NVSMXCommonConstants.REST_PARENT_PKG_SM)
@Namespace("/sm/serverprofile")
@Results({ @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = { NVSMXCommonConstants.ACTION_NAME,
		"offline-rnc" }) })
public class OfflineRncCTRL extends CreateNotSupportedCTRL<OfflineRncServerProfileData> {

	private static final long serialVersionUID = 8585433417738751157L;

	@Override
	public ACLModules getModule() {
		return ACLModules.OFFLINE_RNC;
	}

	@Override
	public OfflineRncServerProfileData createModel() {
		return new OfflineRncServerProfileData();
	}

	@SuppressWarnings("unchecked")
	@Override
	public HttpHeaders show() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called Offline Rnc show()");
		}
		
		getRequest().getSession().setAttribute(Attributes.SERVER_PROFILE_TAB_TYPE, ServerGroups.OFFLINE_RNC.getValue());
		OfflineRncServerProfileData model = (OfflineRncServerProfileData) getModel();

		if (model.getId() != null && !("*").equals(model.getId())) {
			model = CRUDOperationUtil.get((Class<OfflineRncServerProfileData>) model.getClass(), model.getId());
			setModel(model);
		}
		else{
			List<OfflineRncServerProfileData> offlineRncList = (List<OfflineRncServerProfileData>) CRUDOperationUtil
					.findAll(getModel().getClass());
			if (Collectionz.isNullOrEmpty(offlineRncList) == false) {
				OfflineRncServerProfileData offlineRncServerProfileData = offlineRncList.get(0);
				setModel(offlineRncServerProfileData);
			}
		}
		
		setActionChainUrl("sm/serverprofile/serverpno-profile/index");
		return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
	}

	@Override
	public void validate() {
		// This method is used to override default feature of validate method
	}

}
