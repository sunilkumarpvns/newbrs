package com.elitecore.nvsmx.policydesigner.controller.imspkgservice;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyType;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgPCCAttributeData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.policydesigner.controller.util.EliteGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.commons.lang.builder.CompareToBuilder;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * 
 * @author Dhyani.Raval
 *
 */
public class IMSPkgServiceCTRL extends EliteGenericCTRL<IMSPkgServiceData> {

	private static final long serialVersionUID = 1L;
	private IMSPkgServiceData imsPkgServiceData = new IMSPkgServiceData();
	private static final String MODULE = IMSPkgServiceCTRL.class.getSimpleName();
	private String actionChainUrl;
	private List<MediaTypeData> mediaTypeDatas = Collectionz.newArrayList();
	Object [] messageParameter = {Discriminators.IMS_PACKAGE_SERVICES};
	public static final String IMS_SERVICE_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.imsPkgData,dataList\\[\\d+\\]\\.imsPkgData.name,dataList\\[\\d+\\]\\.imsPkgData.id,dataList\\[\\d+\\]\\.mediaTypeData,dataList\\[\\d+\\]\\.mediaTypeData.name,dataList\\[\\d+\\]\\.afApplicationId,dataList\\[\\d+\\]\\.action";

	private String getImsPkgServiceId() {

		String imsServiceId = request.getParameter(Attributes.IMS_PKG_SERVICE_ID);
		if (Strings.isNullOrBlank(imsServiceId)) {
			imsServiceId = (String) request.getAttribute(Attributes.IMS_PKG_SERVICE_ID);
			if (Strings.isNullOrBlank(imsServiceId)) {
				imsServiceId = request.getParameter("imsPkgServiceData.id");
			}
		}
		return imsServiceId;
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

	public String init(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called init()");
		}
		String imsPkgServiceId = getImsPkgServiceId();

		if(Strings.isNullOrBlank(imsPkgServiceId)) {
			String pkgId = getPkgId();
		setMediaTypeDatas((getMediaTypes()));
		IMSPkgData imsPkgData = CRUDOperationUtil.get(IMSPkgData.class, pkgId);
		imsPkgData.setId(pkgId);
		imsPkgServiceData.setImsPkgData(imsPkgData);
		request.setAttribute(Attributes.PKG_ID, pkgId);
		request.setAttribute(Attributes.ADVANCE_CONDITIONS, getAdvanceConditionAutoSuggestions());
		return Results.CREATE.getValue();
	}
	
		setActionChainUrl(NVSMXCommonConstants.ACTION_IMS_PKG_VIEW);
		try{
			if(Strings.isNullOrEmpty(imsPkgServiceId) == false){
				imsPkgServiceData = CRUDOperationUtil.get(IMSPkgServiceData.class, imsPkgServiceId);
				imsPkgServiceData.setMediaTypeId(imsPkgServiceData.getMediaTypeData().getId());
			}else{
				return redirectError(Discriminators.IMS_PACKAGE_SERVICES, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
			}
			setMediaTypeDatas(getMediaTypes());
			request.setAttribute(Attributes.ADVANCE_CONDITIONS, getAdvanceConditionAutoSuggestions());
			return Results.UPDATE.getValue();
		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Failed to update.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	@InputConfig(resultName = InputConfigConstants.CREATE)
	public String create(){	
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called create()");
		}
		String pkgId = getPkgId();
		setActionChainUrl(NVSMXCommonConstants.ACTION_IMS_PKG_VIEW);
		setParentIdKey(Attributes.PKG_ID);
		setParentIdValue(pkgId);
		try{
			IMSPkgData imsPkgData = CRUDOperationUtil.get(IMSPkgData.class,pkgId);
			imsPkgServiceData = getImsPkgServiceData();
			setMediaType();
			imsPkgServiceData.setImsPkgData(imsPkgData);
			imsPkgServiceData.setCreatedDateAndStaff(getStaffData());
			setImsPkgPccRuleAttributesData(imsPkgServiceData);
			CRUDOperationUtil.save(imsPkgServiceData);
			String message = Discriminators.IMS_PACKAGE_SERVICES + " <b><i>" + imsPkgServiceData.getName() + "</i></b> " + "Created";
			CRUDOperationUtil.audit(imsPkgServiceData,imsPkgServiceData.getImsPkgData().getName(),AuditActions.CREATE, getStaffData(), request.getRemoteAddr(),imsPkgServiceData.getHierarchy(), message);
			MessageFormat messageFormat = new MessageFormat(getText("create.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.REDIRECT_TO_PARENT.getValue();
 		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Failed to Create.", ActionMessageKeys.CREATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
		
	}

	@InputConfig(resultName = InputConfigConstants.UPDATE)
	public String update(){	
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called update()");
		}
		setActionChainUrl(NVSMXCommonConstants.ACTION_IMS_PKG_VIEW);
		setParentIdKey(Attributes.PKG_ID);
		setParentIdValue(getPkgId());
		try{
			String imsPkgServiceId = getImsPkgServiceId();
			IMSPkgServiceData imsPkgServiceInDB = CRUDOperationUtil.get(IMSPkgServiceData.class, imsPkgServiceId);
			JsonObject jsonObjectOld = imsPkgServiceInDB.toJson(); 
			imsPkgServiceData.setImsPkgData(imsPkgServiceInDB.getImsPkgData());
			setMediaType();
			setImsPkgPccRuleAttributesData(imsPkgServiceData);
			imsPkgServiceData.setModifiedDateAndStaff(getStaffData());
			
			CRUDOperationUtil.merge(imsPkgServiceData);
			JsonObject  jsonObjectNew = imsPkgServiceData.toJson();
			JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);
			String message = Discriminators.IMS_PACKAGE_SERVICES + " <b><i>" + imsPkgServiceData.getName() + "</i></b> " + "Updated";
			CRUDOperationUtil.audit(imsPkgServiceData,imsPkgServiceData.getImsPkgData().getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference , imsPkgServiceData.getHierarchy(), message);
			request.setAttribute(Attributes.IMS_PKG_SERVICE_ID, imsPkgServiceId);
			
			MessageFormat messageFormat = new MessageFormat(getText("update.success"));
			addActionMessage(messageFormat.format(messageParameter));
			return Results.REDIRECT_TO_PARENT.getValue();
 		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Failed to Update.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
		
	}

	/**
	 * It will set the IMSPkgServiceData for the IMSPkgPCCAttributeData
	 * @param imsPkgServiceData
	 */
	private void setImsPkgPccRuleAttributesData(IMSPkgServiceData imsPkgServiceData){
		filterEmptyImsPkgPccAttributes(imsPkgServiceData.getImsPkgPCCAttributeDatas());
		List<IMSPkgPCCAttributeData> imsPkgPCCAttributeDatas = Collectionz.newArrayList();
		for(IMSPkgPCCAttributeData imsPkgPCCAttributeData : imsPkgServiceData.getImsPkgPCCAttributeDatas()){
			imsPkgPCCAttributeData.setImsPkgServiceData(imsPkgServiceData);
			imsPkgPCCAttributeDatas.add(imsPkgPCCAttributeData);
		}
		imsPkgServiceData.setImsPkgPCCAttributeDatas(imsPkgPCCAttributeDatas);;
	}

	/**
	 * It will filter the null and empty records for list 
	 * @param imsPkgPCCAttributeDatas
	 */
	private void filterEmptyImsPkgPccAttributes(List<IMSPkgPCCAttributeData> imsPkgPCCAttributeDatas){
		Collectionz.filter(imsPkgPCCAttributeDatas,new Predicate<IMSPkgPCCAttributeData>() {
			@Override
			public boolean apply(IMSPkgPCCAttributeData imsPkgPCCAttributeData) {
				if(imsPkgPCCAttributeData == null){
					return false;
				}
				if(Strings.isNullOrBlank(imsPkgPCCAttributeData.getAttribute().displayValue)
						&& Strings.isNullOrBlank(imsPkgPCCAttributeData.getExpression())
						&& Strings.isNullOrBlank(imsPkgPCCAttributeData.getAction().val)
						&& Strings.isNullOrBlank(imsPkgPCCAttributeData.getValue())){
					return false;
				}
				return  true;
			}});
	}

	public String delete(){
		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Method called delete()");
		}
		setActionChainUrl(NVSMXCommonConstants.ACTION_IMS_PKG_VIEW);
		try{
			String imsPkgServiceId = request.getParameter("imsPkgServiceId");
			
			imsPkgServiceData = CRUDOperationUtil.get(IMSPkgServiceData.class,imsPkgServiceId);
			if(imsPkgServiceData !=null){
				imsPkgServiceData.setStatus(CommonConstants.STATUS_DELETED);
				CRUDOperationUtil.update(imsPkgServiceData);
			}

			String message = Discriminators.IMS_PACKAGE_SERVICES + " <b><i>" + imsPkgServiceData.getName() + "</i></b> " + " Deleted";
			CRUDOperationUtil.audit(imsPkgServiceData,imsPkgServiceData.getName(),AuditActions.DELETE, getStaffData(), request.getRemoteAddr(),imsPkgServiceData.getHierarchy(), message);
			MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
			addActionMessage(messageFormat.format(messageParameter));

			setParentIdKey(Attributes.PKG_ID);
			setParentIdValue(imsPkgServiceData.getImsPkgData().getId());
			return Results.REDIRECT_TO_PARENT.getValue();
 		}catch(Exception e){
			return generateErrorLogsAndRedirect(e, "Failed to delete.", ActionMessageKeys.DELETE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
		}
	}

	private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
		return super.generateErrorLogsAndRedirect(Discriminators.PCCRULE, e, message, key, result);
	}

	public IMSPkgServiceData getImsPkgServiceData() {
		return imsPkgServiceData;
	}

	public void setImsPkgServiceData(IMSPkgServiceData imsPkgServiceData) {
		this.imsPkgServiceData = imsPkgServiceData;
	}

	@Override
	public IMSPkgServiceData getModel() {
		return imsPkgServiceData;
	}



	public String getActionChainUrl() {
		return actionChainUrl;
	}
	
	@ActionChain(name = "actionChainUrlMethod")
	public void setActionChainUrl(String actionChainUrl) {
		this.actionChainUrl = actionChainUrl;
	}
	
	public List<MediaTypeData> getMediaTypeDatas() {
		return mediaTypeDatas;
	}

	public void setMediaTypeDatas(List<MediaTypeData> mediaTypeDatas) {
		this.mediaTypeDatas = mediaTypeDatas;
	}

	private List<MediaTypeData> getMediaTypes() {
		List<MediaTypeData> mediaTypeDataList = CRUDOperationUtil.findAll(MediaTypeData.class);
		Collections.sort(mediaTypeDataList, new Comparator<MediaTypeData>() {
			@Override
			public int compare(MediaTypeData serviceTypeData1, MediaTypeData serviceTypeData2) {
				return new CompareToBuilder().append(serviceTypeData1.getName(), serviceTypeData2.getName()).toComparison();
			}
		});
		return mediaTypeDataList;

	}
	/**
	 * get all suggestions for the expression from PCRFKeyConstants
	 * @return json String
	 */
	private String getAdvanceConditionAutoSuggestions() {
		Gson gson = GsonFactory.defaultInstance();
		List<PCRFKeyConstants> pcrfKeyConstants = PCRFKeyConstants.values(PCRFKeyType.IMS_RULE);
		String [] autoSuggestion = new String[pcrfKeyConstants.size()];
		short index= 0;
		for(PCRFKeyConstants keyConstants : pcrfKeyConstants){    	
			autoSuggestion[index] = keyConstants.getVal();
			index++;
		}
		return gson.toJson(autoSuggestion);
	}
	private void setMediaType() {
		if(Strings.isNullOrEmpty(getImsPkgServiceData().getMediaTypeId())==false){
			MediaTypeData mediaTypeData = CRUDOperationUtil.get(MediaTypeData.class, getImsPkgServiceData().getMediaTypeId());
			getImsPkgServiceData().setMediaTypeData((mediaTypeData));
		}

	}

	@Override
	protected List<IMSPkgServiceData> getSearchResult(String criteriaJson, Class<IMSPkgServiceData> beanType, int startIndex, int maxRecords, String sortColumnName, String sortColumnOrder, String staffBelongingGroups) throws Exception {
		return super.getSearchResult(criteriaJson, beanType, startIndex, maxRecords, sortColumnName, sortColumnOrder, staffBelongingGroups);
	}

	@Override
	public String getIncludeProperties(){
		return IMS_SERVICE_INCLUDE_PARAMETERS;
	}

}
