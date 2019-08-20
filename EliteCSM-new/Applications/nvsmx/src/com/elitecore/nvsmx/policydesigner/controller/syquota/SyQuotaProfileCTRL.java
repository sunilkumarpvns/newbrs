package com.elitecore.nvsmx.policydesigner.controller.syquota;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileDetailData;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

/**
 * Handle all the operations related to sy counter based quota profile
 * @author Dhyani.Raval
 *
 */
public class SyQuotaProfileCTRL extends EliteGenericCTRL<SyQuotaProfileData>{

	private static final long serialVersionUID = 1L;
	private static final String MODULE = SyQuotaProfileData.class.getSimpleName();
	public String pkgId;
	private SyQuotaProfileData syQuotaProfileData = new SyQuotaProfileData();
	private static final String ACTION_PKG_VIEW ="policydesigner/pkg/Pkg/view";
	private List<DataServiceTypeData> dataServiceTypeData = Collectionz.newArrayList();
	private String actionChainUrl;
	Object [] messageParameter = {Discriminators.QUOTA_PROFILE};
	public static final String SY_QUOTA_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.description,dataList\\[\\d+\\]\\.pkgData,dataList\\[\\d+\\]\\.pkgData.name,dataList\\[\\d+\\]\\.pkgData.id";

	public String init() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called init()");
		}
		String quotaProfileId=getQuotaprofileId();
		if(Strings.isNullOrBlank(quotaProfileId)){
			pkgId = getPkgId();
		PkgData pkgData = CRUDOperationUtil.get(PkgData.class, pkgId);
		syQuotaProfileData.setDescription(NVSMXUtil.getDefaultDescription(request));
		syQuotaProfileData.setPkgData(pkgData);
		setDataServiceTypeData(getServiceTypes());
			request.setAttribute(Attributes.PKG_ID, pkgId);

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Creating Sy Quota profile for the package id : " + pkgId);
			}

		return Results.CREATE.getValue();
		}
		request.setAttribute(Attributes.ACTION, ACTION_PKG_VIEW);
		setActionChainUrl(ACTION_PKG_VIEW);
		try {
			setSyQuotaProfileData(CRUDOperationUtil.get(SyQuotaProfileData.class, quotaProfileId));
			setDataServiceTypeData(getServiceTypes());
			return Results.UPDATE.getValue();
		} catch (Exception e) {
			getLogger().error(MODULE, "Failed to fetch SyQuotaProfile. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.QUOTA_PROFILE + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}
	}
		
	private String getQuotaprofileId() {

		String quotaProfileId = request.getParameter(Attributes.QUOTA_PROFILE_ID);
		if (Strings.isNullOrBlank(quotaProfileId)) {
			quotaProfileId = (String) request.getAttribute(Attributes.QUOTA_PROFILE_ID);
			if (Strings.isNullOrBlank(quotaProfileId)) {
				quotaProfileId = request.getParameter("quotoProfile.id");
	}
		}
		return quotaProfileId;
	}
	
	private String getPkgId() {

		String pkgId = request.getParameter(Attributes.PKG_ID);
		if (Strings.isNullOrBlank(pkgId)) {
			pkgId = (String) request.getAttribute(Attributes.PKG_ID);
			if (Strings.isNullOrBlank(pkgId)) {
				pkgId = request.getParameter("quotaProfile.pkgData.id");
			}
		}
		return pkgId;
	}

	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called create()");
		}
		request.setAttribute(Attributes.ACTION, ACTION_PKG_VIEW);
		setActionChainUrl(ACTION_PKG_VIEW);
		try{
			String pkgId = getPkgId();
			PkgData pkgData = CRUDOperationUtil.get(PkgData.class,pkgId);
			syQuotaProfileData.setPkgData(pkgData);
			syQuotaProfileData.setCreatedDateAndStaff(getStaffData());
			setServiceTypes();
			setSyQuotaProfileDetails(syQuotaProfileData);
			CRUDOperationUtil.save(syQuotaProfileData);
			String message = Discriminators.QUOTA_PROFILE + " <b><i>" + syQuotaProfileData.getName() + "</i></b> " + "Created";
			CRUDOperationUtil.audit(syQuotaProfileData,syQuotaProfileData.getPkgData().getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(),syQuotaProfileData.getHierarchy(), message);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(pkgId);
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.REDIRECT_TO_PARENT.getValue();
 		}catch (Exception e) {
			getLogger().error(MODULE,"Failed to Create SyQuotaProfile. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			addActionError(Discriminators.QUOTA_PROFILE + " " +getText(ActionMessageKeys.CREATE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}
	}
	
	private void setServiceTypes() {
		List<SyQuotaProfileDetailData> syQuotaProfileDetailDatas = Collectionz.newArrayList();
		for(SyQuotaProfileDetailData syQuotaProfileDetailData : syQuotaProfileData.getSyQuotaProfileDetailDatas()){
			DataServiceTypeData dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class,syQuotaProfileDetailData.getDataServiceTypeData().getId());
			syQuotaProfileDetailData.setDataServiceTypeData(dataServiceTypeData);
			syQuotaProfileDetailDatas.add(syQuotaProfileDetailData);
		}
		syQuotaProfileData.setSyQuotaProfileDetailDatas(syQuotaProfileDetailDatas);
		
	}

	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Method called update()");
		}
		request.setAttribute(Attributes.ACTION, ACTION_PKG_VIEW);
		setActionChainUrl(ACTION_PKG_VIEW);
		try{
			SyQuotaProfileData syQuotaProfileDataInDB = CRUDOperationUtil.get(SyQuotaProfileData.class, syQuotaProfileData.getId());
			JsonObject oldJsonObject = syQuotaProfileDataInDB.toJson();
			syQuotaProfileData.setPkgData(syQuotaProfileDataInDB.getPkgData());
			syQuotaProfileData.setModifiedDateAndStaff(getStaffData());
			setSyQuotaProfileDetails(syQuotaProfileData);
			setServiceTypes();
			syQuotaProfileData.setQosProfileDatas(syQuotaProfileDataInDB.getQosProfileDatas());
			CRUDOperationUtil.merge(syQuotaProfileData);
			JsonObject newJsonObject= syQuotaProfileData.toJson();
			JsonArray difference = ObjectDiffer.diff(oldJsonObject,newJsonObject);
			String message = Discriminators.QUOTA_PROFILE + " <b><i>" + syQuotaProfileData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(syQuotaProfileData,syQuotaProfileData.getPkgData().getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference ,syQuotaProfileData.getHierarchy(), message);
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(syQuotaProfileData.getPkgData().getId());
			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.REDIRECT_TO_PARENT.getValue();
 		}catch (Exception e) {
			getLogger().error(MODULE,"Failed to Update SyQuotaProfile. Reason: " + e.getMessage());
			getLogger().trace(MODULE,e);
			addActionError(Discriminators.QUOTA_PROFILE + " " +getText(ActionMessageKeys.UPDATE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}
	}

	public String delete(){
		if (getLogger().isDebugLogLevel()) { 
			getLogger().debug(MODULE, "Method called delete()");
		}
		String quotaProfileId = request.getParameter(Attributes.QUOTA_PROFILE_ID);
		request.setAttribute(Attributes.ACTION, ACTION_PKG_VIEW);
		setActionChainUrl(ACTION_PKG_VIEW);
		try {
			if(Strings.isNullOrEmpty(quotaProfileId) == false){
				syQuotaProfileData = CRUDOperationUtil.get(SyQuotaProfileData.class, quotaProfileId);
				if(Collectionz.isNullOrEmpty(syQuotaProfileData.getQosProfileDatas()) == false){
					for(QosProfileData qosProfileData : syQuotaProfileData.getQosProfileDatas()){
						if(qosProfileData.getStatus().equals(CommonConstants.STATUS_DELETED) == false){
							addActionError(Discriminators.QUOTA_PROFILE + " " + getText(ActionMessageKeys.DELETE_FAILURE.key));
							addActionError("QosProfile is Configured with '"+ syQuotaProfileData.getName() + "' " +Discriminators.QUOTA_PROFILE);
							return Results.REDIRECT_ERROR.getValue();
						}
					}
				}
				syQuotaProfileData.setStatus(CommonConstants.STATUS_DELETED);
				CRUDOperationUtil.update(syQuotaProfileData);
				String message = Discriminators.QUOTA_PROFILE + " <b><i>" + syQuotaProfileData.getName() + "</i></b> " + "Deleted";
				CRUDOperationUtil.audit(syQuotaProfileData,syQuotaProfileData.getPkgData().getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),syQuotaProfileData.getHierarchy(), message);
			}else {
				addActionError(Discriminators.QUOTA_PROFILE + " " +getText(ActionMessageKeys.DATA_NOT_FOUND.key));
				addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
				return Results.REDIRECT_ERROR.getValue();
			}
			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(syQuotaProfileData.getPkgData().getId());
			MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
			addActionMessage(messageFormat.format(messageParameter));
			
			return Results.REDIRECT_TO_PARENT.getValue();
 		} catch (Exception e) {			
			getLogger().error(MODULE,"Error while fetching SyQuotaProfile data for delete operation. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			addActionError(Discriminators.QUOTA_PROFILE + " " +getText(ActionMessageKeys.DELETE_FAILURE.key));
			addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
			return Results.REDIRECT_ERROR.getValue();
		}			
	}
	
	private void setSyQuotaProfileDetails(SyQuotaProfileData syQuotaProfileData) {
		filterEmptySyQuotaProfileDetails(syQuotaProfileData.getSyQuotaProfileDetailDatas());
		List<SyQuotaProfileDetailData> syQuotaProfileDetailDatas = Collectionz.newArrayList();
		for(SyQuotaProfileDetailData syQuotaProfileDetailData : syQuotaProfileData.getSyQuotaProfileDetailDatas()){
			syQuotaProfileDetailData.setSyQuotaProfileData(syQuotaProfileData);
			syQuotaProfileDetailDatas.add(syQuotaProfileDetailData);
		}
		syQuotaProfileData.setSyQuotaProfileDetailDatas(syQuotaProfileDetailDatas);
	}
	
	private void filterEmptySyQuotaProfileDetails(List<SyQuotaProfileDetailData> syQuotaProfileDetailDatas){
		Collectionz.filter(syQuotaProfileDetailDatas,new Predicate<SyQuotaProfileDetailData>() {
			@Override
			public boolean apply(SyQuotaProfileDetailData syQuotaProfileDetailData) {
				if(syQuotaProfileDetailData == null){
					return false;
				}
				if(Strings.isNullOrBlank(syQuotaProfileDetailData.getDataServiceTypeData().getId())
						&& Strings.isNullOrBlank(syQuotaProfileDetailData.getCounterName())
						&& Strings.isNullOrBlank(syQuotaProfileDetailData.getHsqValue())
						&& Strings.isNullOrBlank(syQuotaProfileDetailData.getFup1Value())
						&& Strings.isNullOrBlank(syQuotaProfileDetailData.getFup2Value())
						&& syQuotaProfileDetailData.getCounterPresent() == null){
					return false;
				}
				return true;
			}});
	}

	private List<DataServiceTypeData> getServiceTypes() {
		List<DataServiceTypeData> dataServiceTypeData = CRUDOperationUtil.findAllWhichIsNotDeleted(DataServiceTypeData.class);
		Collections.sort(dataServiceTypeData, new Comparator<DataServiceTypeData>() {
			@Override
			public int compare(DataServiceTypeData dataServiceTypeData1, DataServiceTypeData dataServiceTypeData2) {
				return dataServiceTypeData1.getName().compareTo(dataServiceTypeData2.getName());
			}
		});
		return dataServiceTypeData;
	}
	
	public List<DataServiceTypeData> getDataServiceTypeData() {
		return dataServiceTypeData;
	}

	public void setDataServiceTypeData(List<DataServiceTypeData> dataServiceTypeData) {
		this.dataServiceTypeData = dataServiceTypeData;
	}

	public String getActionChainUrl() {
		return actionChainUrl;
	}

	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}

	public SyQuotaProfileData getSyQuotaProfileData() {
		return syQuotaProfileData;
	}

	public void setSyQuotaProfileData(SyQuotaProfileData syQuotaProfileData) {
		this.syQuotaProfileData = syQuotaProfileData;
	}

	@Override
	public SyQuotaProfileData getModel() {
		return syQuotaProfileData;
	}
	
	

	@Override
	protected List<SyQuotaProfileData> getSearchResult(String criteriaJson,Class<SyQuotaProfileData> beanType, int startIndex, int maxRecords, String sortColumnName, String sortColumnOrder, String staffBelongingGroups) throws Exception {
		return CRUDOperationUtil.searchByNameAndStatus(beanType, criteriaJson, startIndex, maxRecords, sortColumnName, sortColumnOrder, staffBelongingGroups);
	}

	@Override
	public String getIncludeProperties(){
		return SY_QUOTA_INCLUDE_PARAMETERS;
	}

}
