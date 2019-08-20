package com.elitecore.elitesm.web.rm.ippool;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm;

/**
 * This Class Contains common implementation methods for create, update and Check IP pool actions.
 *
 */
public class IPPoolBaseAction extends BaseWebAction {
	protected static final String MODULE = "IPPool";
	
	protected IIPPoolData convertFormToBean(IPPoolForm ipPoolForm,IIPPoolData ipPoolData){
		ipPoolData.setName(ipPoolForm.getName());
		ipPoolData.setDescription(ipPoolForm.getDescription());
		ipPoolData.setNasIPAddress(ipPoolForm.getNasIPAddress());
		ipPoolData.setAdditionalAttributes(ipPoolForm.getAdditionalAttributes());
		ipPoolData.setCommonStatusId(ipPoolForm.getActiveStatus()!=null?BaseConstant.SHOW_STATUS_ID:BaseConstant.HIDE_STATUS_ID);
		ipPoolData.setAuditUId(ipPoolForm.getAuditUId());
		return ipPoolData;
	}

	protected boolean isFileUpload(FormFile formFile) {
		return (formFile != null && !"".equals(formFile.getFileName()));
	}
}
