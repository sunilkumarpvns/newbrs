package com.elitecore.nvsmx.policydesigner.controller.syquota;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.nvsmx.system.constants.Results;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.policydesigner.model.pkg.syquota.SyQuotaProfileWrapper;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Perform search operation for the SyQuotaProfile
 * @author Dhyani.Raval
 *
 */
public class SyQuotaProfileSearchCTRL extends ActionSupport implements ServletRequestAware {
	
	private static final long serialVersionUID = 1L;
	private static final String MODULE = SyQuotaProfileSearchCTRL.class.getSimpleName();
	private HttpServletRequest request;
	private List<SyQuotaProfileWrapper> dataList;
	private SyQuotaProfileData syQuotaProfileData = new SyQuotaProfileData();
	
	public String syQuotaProfileView(){
		if(LogManager.getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called syQuotaProfileView()");
		}
		try{
			String tableId = request.getParameter(Attributes.TABLE_ID);
			String rowData = request.getParameter(Attributes.ROW_DATA+tableId);
			Gson gson = GsonFactory.defaultInstance();
			SyQuotaProfileWrapper syQuotaProfileWrapper = gson.fromJson(rowData, SyQuotaProfileWrapper.class);
			setSyQuotaProfileData(CRUDOperationUtil.get(SyQuotaProfileData.class, syQuotaProfileWrapper.getId()));
		}catch(Exception e){
			getLogger().error(MODULE,"Failed to get Sy Quota Profile Detail Data. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
		}
		return Results.SUBTABLE_SUCCESS.getValue();
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<SyQuotaProfileWrapper> getDataList() {
		return dataList;
	}

	public void setDataList(
			List<SyQuotaProfileWrapper> syQuotaProfileWrappers) {
		this.dataList = syQuotaProfileWrappers;
	}

	public SyQuotaProfileData getSyQuotaProfileData() {
		return syQuotaProfileData;
	}

	public void setSyQuotaProfileData(SyQuotaProfileData syQuotaProfileData) {
		this.syQuotaProfileData = syQuotaProfileData;
	}

}
