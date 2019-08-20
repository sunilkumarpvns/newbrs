package com.elitecore.nvsmx.ws.packagemanagement.blmanager;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardScope;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.pkg.importpkg.PkgImportOperation;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupDataExt;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.Collector;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.policydesigner.controller.util.PkgTypeValidator;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.ClonePackageType;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.exception.InvalidParameterException;
import com.elitecore.nvsmx.system.hibernate.ActiveTransactionSessionProviderImpl;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.SessionProviderImpl;
import com.elitecore.nvsmx.system.util.ResourceDataGroupPredicate;
import com.elitecore.nvsmx.ws.interceptor.WebServiceAuditInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.ChargingRuleBaseNameListRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.ChargingRuleBaseNameRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.ClonePackageRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.CloneProductOfferRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.EmergencyPackageManagementListRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.EmergencyPackageManagementRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.IMSPackageManagementListRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.IMSPackageManagementRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.PCCRuleListRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.PCCRuleRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.PackageManagementListRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.PackageManagementRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.RatingGroupListManagementRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.RatingGroupManagementRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.ServiceTypeListManagementRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.ServiceTypeManagementRequest;
import com.elitecore.nvsmx.ws.packagemanagement.response.ChargingRuleManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.ClonePackageResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.CloneProductOfferResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.DataServiceTypeManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.PCCRuleManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.PackageManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.RatingGroupManagementResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.ENTITY_IMPORT_FAIL;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.closeSession;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.commitTransaction;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.syncSession;

public class PackageImportWSBLManager {
	private static final String  MODULE="PKG-WSBL-MNGR";
    private static final String PCC_RULE = "PCC Rules";
	private static final String GLOBAL_RATE_CARD = "RATE Card";
    private static final String CRBN = "Charging Rule Base Name";
    private StaffData adminStaff;
	private static ImportExportUtil importExportUtil = new ImportExportUtil(new SessionProviderImpl());
	private List<WebServiceInterceptor> interceptors = new ArrayList<>();

	public PackageImportWSBLManager(){
		adminStaff = new StaffData();
		adminStaff.setUserName("admin");
		adminStaff.setId(NVSMXCommonConstants.ADMIN_STAFF_ID);
		interceptors.add(WebServiceAuditInterceptor.getInstance());
	}

	public PackageManagementResponse importPackage(PackageManagementRequest request) {
		PackageManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>();
		try {
			PkgData pkgData = request.getPkgData();
			if (pkgData == null) {
				getLogger().error(MODULE, "Unable to import Package Data. Reason: Package Data Missing");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Package Data", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import Package Data. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import Package Data. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			PkgTypeValidator pkgTypeValidator = new PkgTypeValidator(request.getAclModule());
			importPackageInformation(pkgData,request.getAction(),reasons,pkgTypeValidator);

			return new PackageManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2() );
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing package data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import package due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(request.getPkgData().getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
			response = new PackageManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}


	public PackageManagementResponse importPackages(PackageManagementListRequest request) {
		PackageManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>();
		try {
			List<PkgData> pkgData = request.getPkgData();
			if (pkgData == null) {
				getLogger().error(MODULE, "Unable to import Package List. Reason: Package Data List Missing");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Package Data List", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import Package Data List. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import Package List. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			PkgTypeValidator pkgTypeValidator = new PkgTypeValidator(request.getAclModule());
			for(PkgData pkg : pkgData){
				importPackageInformation(pkg,request.getAction(),reasons, pkgTypeValidator);
			}
			return new PackageManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2() );
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing package data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import package due to internal error. Kindly refer logs for further details";
			response = new PackageManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name +". "+errorMessage , reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}

	public void importPackageInformation(PkgData pkgData, String action, List<Reason> reasons, PkgTypeValidator pkgTypeValidator){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Import Operation started for package: " + pkgData.getName());
		}
		try {
			//Setting staff information to the package
			Reason reason = new Reason(pkgData.getName());
			List<String> failedReasons = pkgTypeValidator.validate(pkgData);
			if(Collectionz.isNullOrEmpty(failedReasons) == false){
				reason.setRemarks(getRemarksFromSubReasons(failedReasons));
				reason.setMessages(ENTITY_IMPORT_FAIL);
				reasons.add(reason);
				return ;
			}
			pkgData.setCreatedByStaff(adminStaff);
			pkgData.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			pkgData.setModifiedByStaff(pkgData.getCreatedByStaff());
			pkgData.setModifiedDate(pkgData.getCreatedDate());
			List<String> groups = new ArrayList<String>();
			boolean isExistGroup = true;
			if(Strings.isNullOrBlank(pkgData.getGroupNames())){
				pkgData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
				groups.add(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				isExistGroup = importExportUtil.getGroupIdsFromName(pkgData, reason, groups);
			}
			if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
				pkgData.setGroups(Strings.join(",", groups));
				importExportUtil.validateAndImportInformation(pkgData, action, reason);
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Import Operation finished for package: " + pkgData.getName());
				}
			}
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}catch (Exception e) {
			getLogger().error(MODULE, "Error while importing package data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import package due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(pkgData.getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}
	}

	public DataServiceTypeManagementResponse importDataServiceType(ServiceTypeManagementRequest request) {
		DataServiceTypeManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>();
		try {
			DataServiceTypeDataExt dataServiceTypeData = request.getDataServiceTypeDataExt();
			if (dataServiceTypeData == null) {
				getLogger().error(MODULE, "Unable to import Data Service Type Data. Reason: Data Service Type Data Missing");
				response = new DataServiceTypeManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Data Service Type Data", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import Data Service Type Data. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new DataServiceTypeManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import Data Service Type Data. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new DataServiceTypeManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}
			importDataServiceInformation(dataServiceTypeData,request.getAction(),reasons);
			return new DataServiceTypeManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2() );
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing data service type data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import data service type due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>(2);
			errors.add(errorMessage);
			Reason reason = new Reason(request.getDataServiceTypeDataExt().getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
			response = new DataServiceTypeManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}



	public DataServiceTypeManagementResponse importDataServiceTypes(ServiceTypeListManagementRequest request) {
		DataServiceTypeManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>();
		try {
			List<DataServiceTypeDataExt> serviceTypes = request.getServiceType();
			if (Collectionz.isNullOrEmpty(serviceTypes)) {
				getLogger().error(MODULE, "Unable to import Data Service Type List. Reason: Data Service Type List Missing");
				response = new DataServiceTypeManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Data Service Type List", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import Data Service Type List. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new DataServiceTypeManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import Data Service Type List. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new DataServiceTypeManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			for(DataServiceTypeDataExt serviceType : serviceTypes){
				importDataServiceInformation(serviceType,request.getAction(), reasons);
			}
			return new DataServiceTypeManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons, request.getParameter1(), request.getParameter2());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing data service type data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import data service type due to internal error. Kindly refer logs for further details";
			response = new DataServiceTypeManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name + ". "+errorMessage, reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}

	public RatingGroupManagementResponse importRatingGroup(RatingGroupManagementRequest request) {
		RatingGroupManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>(2);
		try {
			RatingGroupDataExt ratingGroup = request.getRatingGroup();
			if (ratingGroup == null) {
				getLogger().error(MODULE, "Unable to import Rating Group Data. Reason: Rating Group Data Missing");
				response = new RatingGroupManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Rating Group Data", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import Rating Group Data. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new RatingGroupManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false) {
				getLogger().error(MODULE, "Unable to import Rating Group Data. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new RatingGroupManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			importRatingGroupInformation(ratingGroup,request.getAction(),reasons);
			return new RatingGroupManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons, request.getParameter1(), request.getParameter2());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing rating group data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import rating group data due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>(2);
			errors.add(errorMessage);
			Reason reason = new Reason(request.getRatingGroup().getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
			response = new RatingGroupManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}

	public RatingGroupManagementResponse importRatingGroups(RatingGroupListManagementRequest request) {
		RatingGroupManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>();
		try {
			List<RatingGroupDataExt> ratingGroups = request.getRatingGroup();
			if (Collectionz.isNullOrEmpty(ratingGroups)) {
				getLogger().error(MODULE, "Unable to import Rating Group List. Reason: Rating Group Data Missing");
				response = new RatingGroupManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Rating Group Data", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import Rating Group List. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new RatingGroupManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false) {
				getLogger().error(MODULE, "Unable to import Rating Group List. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new RatingGroupManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			for(RatingGroupDataExt ratingGroup : ratingGroups){
				importRatingGroupInformation(ratingGroup,request.getAction(),reasons);
			}

			return new RatingGroupManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2() );
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing rating group list. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = ResultCode.INTERNAL_ERROR.name + ". Failed to import rating group data due to internal error. Kindly refer logs for further details";
			response = new RatingGroupManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name +". "+errorMessage , reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}


	private void importDataServiceInformation(DataServiceTypeDataExt serviceType, String action, List<Reason> reasons){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Import Operation started for data service type: " + serviceType.getName());
		}
		try {

			serviceType.setCreatedByStaff(adminStaff);
			serviceType.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			serviceType.setModifiedByStaff(serviceType.getCreatedByStaff());
			serviceType.setModifiedDate(serviceType.getCreatedDate());
			Reason reason = new Reason(serviceType.getName());
			List<String> groups = new ArrayList<String>();
			boolean isExistGroup = true;
			if(Strings.isNullOrBlank(serviceType.getGroupNames())){
				serviceType.setGroups(CommonConstants.DEFAULT_GROUP_ID);
				groups.add(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				isExistGroup = importExportUtil.getGroupIdsFromName(serviceType, reason, groups);
			}
			if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
				serviceType.setGroups(Strings.join(",", groups));
				importExportUtil.validateAndImportInformation(serviceType, action, reason);
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Import Operation finished for data service type: " + serviceType.getName());
				}
			}
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing data service type data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import data service type due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>(2);
			errors.add(errorMessage);
			Reason reason = new Reason(serviceType.getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}

	}

	private void importRatingGroupInformation(RatingGroupDataExt ratingGroup, String action, List<Reason> reasons) throws Exception{
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Import Operation started for rating group: " + ratingGroup.getName());
		}
		try {
			//Setting staff information to the package
			ratingGroup.setCreatedByStaff(adminStaff);
			ratingGroup.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			ratingGroup.setModifiedByStaff(ratingGroup.getCreatedByStaff());
			ratingGroup.setModifiedDate(ratingGroup.getCreatedDate());
			Reason reason = new Reason(ratingGroup.getName());
			List<String> groups = new ArrayList<String>();
			boolean isExistGroup = true;
			if(Strings.isNullOrBlank(ratingGroup.getGroupNames())){
				ratingGroup.setGroups(CommonConstants.DEFAULT_GROUP_ID);
				groups.add(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				isExistGroup = importExportUtil.getGroupIdsFromName(ratingGroup, reason, groups);
			}
			if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
				ratingGroup.setGroups(Strings.join(",", groups));
				importExportUtil.validateAndImportInformation(ratingGroup, action, reason);
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Import Operation finished for rating group: " + ratingGroup.getName());
				}
			}
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}catch(Exception e){
			getLogger().error(MODULE, "Error while importing rating group list. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import rating group data due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(ratingGroup.getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);

		}
	}

	public PackageManagementResponse importIMSPackage(IMSPackageManagementRequest request) {
		PackageManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>();
		try {
			IMSPkgData imsPackageData = request.getImsPackageData();
			if (imsPackageData == null) {
				getLogger().error(MODULE, "Unable to import IMS Package Data. Reason: IMS Package Data Missing");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing IMS Package Data", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import IMS Package Data. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import IMS Package Data. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			importImsPackageInformation(imsPackageData, request.getAction(), reasons);

			return new PackageManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2() );
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing ims package data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import ims package due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(request.getImsPackageData().getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
			response = new PackageManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}

	public void importImsPackageInformation(IMSPkgData imsPkgData,String action, List<Reason> reasons){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Import Operation started for ims package: " + imsPkgData.getName());
		}
		try {
			//Setting staff information to the package
			imsPkgData.setCreatedDateAndStaff(adminStaff);
			imsPkgData.setModifiedDateAndStaff(adminStaff);
			Reason reason = new Reason(imsPkgData.getName());
			List<String> groups = new ArrayList<String>();
			boolean isExistGroup = true;
			if(Strings.isNullOrBlank(imsPkgData.getGroupNames())){
				imsPkgData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
				groups.add(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				isExistGroup = importExportUtil.getGroupIdsFromName(imsPkgData, reason, groups);
			}
			if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
				imsPkgData.setGroups(Strings.join(",", groups));
				importExportUtil.validateAndImportInformation(imsPkgData, action, reason);
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Import Operation finished for ims package: " + imsPkgData.getName());
				}
			}
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}catch (Exception e) {
			getLogger().error(MODULE, "Error while importing ims package data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import ims package due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(imsPkgData.getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}
	}

	public PackageManagementResponse importIMSPackages(IMSPackageManagementListRequest request) {
		PackageManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>();
		try {
			List<IMSPkgData> imsPkgData = request.getImsPkgData();
			if (imsPkgData == null) {
				getLogger().error(MODULE, "Unable to import IMS Package List. Reason: IMS Package Data List Missing");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing IMS Package Data List", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import IMS Package Data List. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import IMS Package List. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			for(IMSPkgData imsPkg : imsPkgData){
				importImsPackageInformation(imsPkg, request.getAction(), reasons);
			}
			return new PackageManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2() );
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing ims package list. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import ims package list due to internal error. Kindly refer logs for further details";
			response = new PackageManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name +". "+errorMessage , reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}

	//Emergency Package
	public PackageManagementResponse importEmergencyPackage(EmergencyPackageManagementRequest request) {
		PackageManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>();
		try {
			EmergencyPkgDataExt emergencyPackageData = request.getEmergencyPackageData();
			if (emergencyPackageData == null) {
				getLogger().error(MODULE, "Unable to import Emergency Package Data. Reason: Emergency Package Data Missing");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Emergency Package Data", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import Emergency Package Data. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import Emergency Package Data. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			importEmergencyPackageInformation(emergencyPackageData, request.getAction(), reasons);

			return new PackageManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2() );
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing emergency package data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import emergency package due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(request.getEmergencyPackageData().getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
			response = new PackageManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}

	public void importEmergencyPackageInformation(EmergencyPkgDataExt emergencyPkgData,String action, List<Reason> reasons){
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Import Operation started for emergency package: " + emergencyPkgData.getName());
		}
		try {
			//Setting staff information to the package
			emergencyPkgData.setCreatedDateAndStaff(adminStaff);
			emergencyPkgData.setModifiedDateAndStaff(adminStaff);
			Reason reason = new Reason(emergencyPkgData.getName());
			List<String> groups = new ArrayList<String>();
			boolean isExistGroup = true;
			if(Strings.isNullOrBlank(emergencyPkgData.getGroupNames())){
				emergencyPkgData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
				groups.add(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				isExistGroup = importExportUtil.getGroupIdsFromName(emergencyPkgData, reason, groups);
			}
			if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
				emergencyPkgData.setGroups(Strings.join(",", groups));
				importExportUtil.validateAndImportInformation(emergencyPkgData, action, reason);
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Import Operation finished for emergency package: " + emergencyPkgData.getName());
				}
			}
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}catch (Exception e) {
			getLogger().error(MODULE, "Error while importing emergency package data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import emergency package due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(emergencyPkgData.getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}
	}

	public PackageManagementResponse importEmergencyPackages(EmergencyPackageManagementListRequest request) {
		PackageManagementResponse response = null;
		List<Reason> reasons = new ArrayList<Reason>();
		try {
			List<EmergencyPkgDataExt> emergencyPkgData = request.getEmergencyPkgData();
			if (Collectionz.isNullOrEmpty(emergencyPkgData)) {
				getLogger().error(MODULE, "Unable to import Emergency Package List. Reason: Emergency Package Data List Missing");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Emergency Package Data List", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import Emergency Package Data List. Reason: Action is Missing. It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import Emergency Package List. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				response = new PackageManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
				return response;
			}

			for(EmergencyPkgDataExt imsPkg : emergencyPkgData){
				importEmergencyPackageInformation(imsPkg, request.getAction(), reasons);
			}
			return new PackageManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2() );
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing emergency package list. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import emergency package list due to internal error. Kindly refer logs for further details";
			response = new PackageManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name +". "+errorMessage , reasons, request.getParameter1(), request.getParameter2());
		}

		return response;
	}

	private String getRemarksFromSubReasons(List<String> subReasons){
		StringBuilder sb = new StringBuilder();
		if(Collectionz.isNullOrEmpty(subReasons)){
			sb.append(" ---- ");
		}else {
			for (String str : subReasons) {
				sb.append(str);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public PCCRuleManagementResponse importGlobalPCCRule(PCCRuleRequest request) {

		List<Reason> reasons = new ArrayList<Reason>();
		try {
			PCCRuleData pccRuleData = request.getGlobalPCCRule();

			if (pccRuleData == null) {

				getLogger().error(MODULE, "Unable to import PCC Rule Data. Reason: PCC Rule Missing");
				return new PCCRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ".Reason: PCC Rule Data missing", reasons, request.getParameter1(), request.getParameter2());

			}

			if (Strings.isNullOrBlank(request.getAction())) {

				getLogger().error(MODULE, "Unable to import PCC Rule Data. Reason: Action is Missing. It can be FAIL/REPLACE");
				return new PCCRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ".Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());

			}

			if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false
					&& CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){

				getLogger().error(MODULE, "Unable to import PCC Rule Data. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				return  new PCCRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
			}

			importGlobalPCCRuleInformation(pccRuleData, request.getAction(), reasons);
			return new PCCRuleManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing PCC Rule data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import PCC Rule due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(request.getGlobalPCCRule().getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
			return new PCCRuleManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, reasons, request.getParameter1(), request.getParameter2());
		}
	}


	public PCCRuleManagementResponse importGlobalPCCRules(PCCRuleListRequest request) {

		List<Reason> reasons = new ArrayList<Reason>();
		try {
			List<PCCRuleData> pccRules = request.getGlobalPCCRules();
			if (pccRules == null) {
				getLogger().error(MODULE, "Unable to import PCC Rule List. Reason: PCC Rule List Missing");
				return new PCCRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: PCC Rule List Missing", reasons, request.getParameter1(), request.getParameter2());

			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import PCC Rule List. Reason: Action is Missing. It can be FAIL/REPLACE");
				return new PCCRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());

			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import PCC Rule List. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				return new PCCRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());

			}

			for(PCCRuleData pccRule : pccRules){
				importGlobalPCCRuleInformation(pccRule, request.getAction(), reasons);
			}
			return new PCCRuleManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing PCC Rule List. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import PCC Rule list due to internal error. Kindly refer logs for further details";
			return new PCCRuleManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name +". "+errorMessage , reasons, request.getParameter1(), request.getParameter2());
		}
	}


	private void importGlobalPCCRuleInformation(PCCRuleData pccRuleData, String action, List<Reason> reasons) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Import Operation started for PCC Rule: " + pccRuleData.getName());
		}
		try {
			//Setting staff information to the package
			pccRuleData.setCreatedDateAndStaff(adminStaff);
			pccRuleData.setModifiedDateAndStaff(adminStaff);
			pccRuleData.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			pccRuleData.setModifiedDate(pccRuleData.getCreatedDate());
			Reason reason = new Reason(pccRuleData.getName());
			List<String> groups = new ArrayList<String>();
			boolean isExistGroup = true;
			if(Strings.isNullOrBlank(pccRuleData.getGroupNames())){
				pccRuleData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
				groups.add(CommonConstants.DEFAULT_GROUP_ID);
			}else {
				isExistGroup = importExportUtil.getGroupIdsFromName(pccRuleData, reason, groups);
			}
			if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
				pccRuleData.setGroups(Strings.join(",", groups));
				importExportUtil.validateAndImportInformation(pccRuleData, action, reason);
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Import Operation finished for pcc rule data: " + pccRuleData.getName());
				}
			}
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}catch (Exception e) {
			getLogger().error(MODULE, "Error while importing PCC Rule Data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import PCC Rule due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(pccRuleData.getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
		}
	}



	public ChargingRuleManagementResponse importChargingRuleBaseName(ChargingRuleBaseNameRequest request) {

		List<Reason> reasons = new ArrayList<Reason>();
		try {
			ChargingRuleBaseNameData chargingRuleBaseNameData = request.getChargingRuleBaseNameData();

			if (chargingRuleBaseNameData == null) {

				getLogger().error(MODULE, "Unable to import ChargingRuleBaseName data. Reason: ChargingRuleBaseName Missing");
				return new ChargingRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ".Reason: ChargingRuleBaseName Data missing", reasons, request.getParameter1(), request.getParameter2());

			}

			if (Strings.isNullOrBlank(request.getAction())) {

				getLogger().error(MODULE, "Unable to import ChargingRuleBaseName Data. Reason: Action is Missing. It can be FAIL/REPLACE");
				return new ChargingRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ".Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());

			}

			if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false
					&& CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){

				getLogger().error(MODULE, "Unable to import ChargingRuleBaseName Data. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				return  new ChargingRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());
			}

			importChargingRuleBaseName(chargingRuleBaseNameData, request.getAction(), reasons);
			return new ChargingRuleManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing ChargingRuleBaseName data. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import ChargingRuleBaseName due to internal error. Kindly refer logs for further details";
			List<String> errors = new ArrayList<String>();
			errors.add(errorMessage);
			Reason reason = new Reason(request.getChargingRuleBaseNameData().getName());
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.addFailReason(errors);
			reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
			reasons.add(reason);
			return new ChargingRuleManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name, reasons, request.getParameter1(), request.getParameter2());
		}
	}


	public ChargingRuleManagementResponse importChargingRuleBaseNames(ChargingRuleBaseNameListRequest request) {

		List<Reason> reasons = new ArrayList<Reason>();
		try {
			List<ChargingRuleBaseNameData> chargingRuleBaseNames = request.getChargingRuleBaseNames();
			if (chargingRuleBaseNames == null) {
				getLogger().error(MODULE, "Unable to import ChargingRuleBaseName List. Reason: ChargingRuleBaseName List Missing");
				return new ChargingRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: ChargingRuleBaseName List Missing", reasons, request.getParameter1(), request.getParameter2());

			}

			if (Strings.isNullOrBlank(request.getAction())) {
				getLogger().error(MODULE, "Unable to import ChargingRuleBaseName List. Reason: Action is Missing. It can be FAIL/REPLACE");
				return new ChargingRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Missing Action. It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());

			}else if(CommonConstants.FAIL.equalsIgnoreCase(request.getAction()) == false && CommonConstants.REPLACE.equalsIgnoreCase(request.getAction()) == false){
				getLogger().error(MODULE, "Unable to import ChargingRuleBaseName List. Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE");
				return new ChargingRuleManagementResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name + ". Reason: Invalid Action provided "+request.getAction() +". It can be FAIL/REPLACE", reasons, request.getParameter1(), request.getParameter2());

			}

			for(ChargingRuleBaseNameData chargingRuleBaseNameData : chargingRuleBaseNames){
				importChargingRuleBaseName(chargingRuleBaseNameData, request.getAction(), reasons);
			}
			return new ChargingRuleManagementResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reasons,request.getParameter1(), request.getParameter2());
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while importing ChargingRuleBaseName List. Reason: " + e.getMessage());
			getLogger().trace(MODULE, e);
			String errorMessage = "Failed to import ChargingRuleBaseName list due to internal error. Kindly refer logs for further details";
			return new ChargingRuleManagementResponse(ResultCode.INTERNAL_ERROR.code, ResultCode.INTERNAL_ERROR.name +". "+errorMessage , reasons, request.getParameter1(), request.getParameter2());
		}
	}

	private void importChargingRuleBaseName(ChargingRuleBaseNameData chargingRuleBaseNameData, String action,  List<Reason> reasons) {

			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Import Operation started for ChargingRuleBaseName: " + chargingRuleBaseNameData.getName());
			}

			Reason reason = new Reason(chargingRuleBaseNameData.getName());
			try {
				chargingRuleBaseNameData.setCreatedByStaff(adminStaff);
				chargingRuleBaseNameData.setModifiedByStaff(adminStaff);
				chargingRuleBaseNameData.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				chargingRuleBaseNameData.setModifiedDate(chargingRuleBaseNameData.getCreatedDate());

				boolean isExistGroup = true;
				List<String> groups = new ArrayList<String>();

				if (Strings.isNullOrBlank(chargingRuleBaseNameData.getGroupNames())) {
					chargingRuleBaseNameData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
					groups.add(CommonConstants.DEFAULT_GROUP_ID);
				} else {
					isExistGroup = importExportUtil.getGroupIdsFromName(chargingRuleBaseNameData, reason, groups);
				}

				if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {

					if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
						chargingRuleBaseNameData.setGroups(Strings.join(",", groups));

						importExportUtil.validateAndImportInformation(chargingRuleBaseNameData, action, reason);
						if (getLogger().isDebugLogLevel()) {
							getLogger().debug(MODULE, "Import Operation finished for ChargingRuleBaseName: " + chargingRuleBaseNameData.getName());
						}
					}
				}
				reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
				reasons.add(reason);

			} catch (Exception e) {
				getLogger().error(MODULE, "Error while importing ChargingRuleBaseNames : " + chargingRuleBaseNameData.getName() + " Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				String errorMessage = "Failed to import ChargingRuleBaseName due to internal error. Kindly refer logs for further details";
				List<String> errors = new ArrayList(2);
				errors.add(errorMessage);
				reason.setMessages(ENTITY_IMPORT_FAIL);
				reason.addFailReason(errors);
				reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
				reasons.add(reason);
			}
	}




	public ClonePackageResponse clonePackage(ClonePackageRequest request){
	     applyRequestInterceptors(request);
	     return doClonePackage(request);
	}


	public ClonePackageResponse doClonePackage(ClonePackageRequest clonePackageRequest) {

        SessionProvider activeTransactionSessionProvider = new ActiveTransactionSessionProviderImpl();

        ClonePackageResponse validationResponse = validateRequestParameters(clonePackageRequest);
        if (validationResponse != null) {
            return validationResponse;
        }

		// check type and based on that type clone according
		if (clonePackageRequest.getType().equalsIgnoreCase(ClonePackageType.DATA.getType())) {
			return doCloneDataPackage(clonePackageRequest, activeTransactionSessionProvider);
		} else if (clonePackageRequest.getType().equalsIgnoreCase(ClonePackageType.RNC.getType())) {
			return doCloneRncpackage(clonePackageRequest, activeTransactionSessionProvider);
		} else {
			return new ClonePackageResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name+". Reason: name of type does not match.", clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}

	}

	private ClonePackageResponse doCloneRncpackage(ClonePackageRequest clonePackageRequest, SessionProvider activeTransactionSessionProvider) {

		// validate existing package info
		RncPackageData existingRnCPackage = getPkgRnC(clonePackageRequest.getName(), activeTransactionSessionProvider);
		if(Objects.isNull(existingRnCPackage)) {
			getLogger().error(MODULE, "Error while creating RnC Package with name: " + clonePackageRequest.getNewName() + ". Reason: Package with name "+clonePackageRequest.getName()+" not found.");
			return new ClonePackageResponse(ResultCode.NOT_FOUND.code, ResultCode.NOT_FOUND.name+".Reason: RnC Package with name "+clonePackageRequest.getName()+" not found.", clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}

		// check duplicate package of new name
		RncPackageData newRnCPackage = getPkgRnC(clonePackageRequest.getNewName(), activeTransactionSessionProvider);
		if(Objects.nonNull(newRnCPackage)) {
			getLogger().error(MODULE, "A RnC Package with name: " + clonePackageRequest.getNewName() + "already exists.");
            return new ClonePackageResponse(ResultCode.ALREADY_EXIST.code, ResultCode.ALREADY_EXIST.name+". Reason: RnC Package with name "+clonePackageRequest.getNewName()+" already exists.", clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}

		// validate new groups are exist or not
		String groupIds;
		try {
			groupIds = fromGroupNames(clonePackageRequest.getGroups(), activeTransactionSessionProvider);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while validating Groups(" + clonePackageRequest.getGroups());
			getLogger().trace(MODULE,e);
			return new ClonePackageResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason : " + e.getMessage(), clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}

		// check group with resource entity group
		List<RateCardData> configuredGlobalRateCards = getGlobalRateCards(existingRnCPackage);

		ResourceDataGroupPredicate resourceDataGroupPredicate = ResourceDataGroupPredicate.create(CommonConstants.COMMA_SPLITTER.split(groupIds));

		if(StringUtils.isNotBlank(groupIds) && Objects.nonNull(configuredGlobalRateCards) && !configuredGlobalRateCards.isEmpty()) {
			ClonePackageResponse validatePccRuleGroupResponse = validateGlobalEntityGroups(clonePackageRequest, configuredGlobalRateCards, GLOBAL_RATE_CARD, resourceDataGroupPredicate);
			if (Objects.nonNull(validatePccRuleGroupResponse)) {
				return validatePccRuleGroupResponse;
			}
		}

		RncPackageData newEntity = existingRnCPackage.copyModel(); //clone the new entity
		newEntity.setId(getRandomIdForReplicatedPackage());
		if(StringUtils.isNotBlank(groupIds)){
			newEntity.setGroups(groupIds);
		}
		newEntity.setName(clonePackageRequest.getNewName());
		newEntity.setCreatedDateAndStaff(adminStaff);

		JsonObject newEntityJson = newEntity.toJson();

		ImportExportCRUDOperationUtil.save(newEntity, activeTransactionSessionProvider);

		syncSession(activeTransactionSessionProvider.getSession());

		auditClonedPackage(existingRnCPackage, newEntity, newEntityJson,clonePackageRequest);

		commitTransaction(activeTransactionSessionProvider.getSession().getTransaction());
		closeSession(activeTransactionSessionProvider.getSession());

		return new ClonePackageResponse(ResultCode.SUCCESS.code,ResultCode.SUCCESS.name+". RnC Package successfully cloned. Cloned RnC Package Id : "+newEntity.getId(), clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
	}

	private List<RateCardData> getGlobalRateCards(RncPackageData existingRnCPackage) {
		List<RateCardData> configuredGlobalRateCards;

		if(CollectionUtils.isEmpty(existingRnCPackage.getRateCardGroupData())  ) {
			return Collections.emptyList();
		}

		configuredGlobalRateCards = new ArrayList<>();

		for(RateCardGroupData rateCardGroupData : existingRnCPackage.getRateCardGroupData()) {
			RateCardData peakRateCardData = rateCardGroupData.getPeakRateRateCard();
			if(Objects.nonNull(peakRateCardData) && peakRateCardData.getScope().equals(RateCardScope.GLOBAL.name())) {
				configuredGlobalRateCards.add(peakRateCardData);
			}

			RateCardData offPeakRateCardData = rateCardGroupData.getOffPeakRateRateCard();
			if(Objects.nonNull(offPeakRateCardData) && offPeakRateCardData.getScope().equals(RateCardScope.GLOBAL.name())) {
				configuredGlobalRateCards.add(offPeakRateCardData);
			}
		}

		return configuredGlobalRateCards;
	}

	private ClonePackageResponse doCloneDataPackage(ClonePackageRequest clonePackageRequest, SessionProvider activeTransactionSessionProvider) {

		PkgData pkgData = getPkgData(clonePackageRequest.getName(), activeTransactionSessionProvider);
		if (pkgData == null) {
			getLogger().error(MODULE, "Error while creating Package Data with name: " + clonePackageRequest.getName() + ". Reason: Not found");
			return new ClonePackageResponse(ResultCode.NOT_FOUND.code, ResultCode.NOT_FOUND.name+". Reason: Data Package with name "+clonePackageRequest.getName()+" not found.", clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}

		PkgData newPkgData = getPkgData(clonePackageRequest.getNewName(), activeTransactionSessionProvider);
		if (Objects.nonNull(newPkgData)) {
			getLogger().error(MODULE, "A Data Package with name: " + clonePackageRequest.getName() + "already exists.");
			return new ClonePackageResponse(ResultCode.ALREADY_EXIST.code, ResultCode.ALREADY_EXIST.name+". Reason: Data Package with name "+clonePackageRequest.getNewName()+" already exists.", clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}

		String groupIds;
		try {
			groupIds = fromGroupNames(clonePackageRequest.getGroups(), activeTransactionSessionProvider);
		} catch (Exception e) {
			getLogger().error(MODULE, "Error while validating Groups(" + clonePackageRequest.getGroups());
			getLogger().trace(MODULE,e);
			return new ClonePackageResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name + ". Reason : " + e.getMessage(), clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}

		List<PCCRuleData> pccRules = getConfiguredPCCRules(pkgData);
		List<ChargingRuleBaseNameData> chargingRuleBaseNames = getConfiguredChargingRuleBaseName(pkgData);

		ResourceDataGroupPredicate resourceDataGroupPredicate = ResourceDataGroupPredicate.create(CommonConstants.COMMA_SPLITTER.split(groupIds));

		if(StringUtils.isNotBlank(groupIds)){
			ClonePackageResponse validatePccRuleGroupResponse = validateGlobalEntityGroups(clonePackageRequest, getGlobalPccRules(pccRules), PCC_RULE,resourceDataGroupPredicate);
			if (Objects.nonNull(validatePccRuleGroupResponse)){
				return validatePccRuleGroupResponse;
			}

			ClonePackageResponse validateCRBNGroupResponse = validateGlobalEntityGroups(clonePackageRequest, chargingRuleBaseNames, CRBN,resourceDataGroupPredicate);
			if (Objects.nonNull(validateCRBNGroupResponse)){
				return validateCRBNGroupResponse;
			}
		}

		pkgData.setCreatedDateAndStaff(adminStaff);

		PkgData newEntity = pkgData.copyModel(); //clone the new entity

		setPCCRuleName(getLocalPccRules(getConfiguredPCCRules(newEntity)), clonePackageRequest.getNewName());

		newEntity.setId(getRandomIdForReplicatedPackage());
		if(StringUtils.isNotBlank(groupIds)){
			newEntity.setGroups(groupIds);
		}

		newEntity.setName(clonePackageRequest.getNewName());

		JsonObject newEntityJson = newEntity.toJson();
		PkgImportOperation pkgImportOperation = new PkgImportOperation();

		try {
			pkgImportOperation.importData(newEntity, newEntity, newEntity, activeTransactionSessionProvider);
		} catch (ImportOperationFailedException e) {
			getLogger().error(MODULE, "Package Import failed ");
			getLogger().trace(MODULE,e);
			return new ClonePackageResponse(ResultCode.INTERNAL_ERROR.code,ResultCode.INTERNAL_ERROR.name+". Reason : Package import failed for "+clonePackageRequest.getGroups(), clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}

		syncSession(activeTransactionSessionProvider.getSession());

		auditClonedPackage(pkgData, newEntity, newEntityJson,clonePackageRequest);

		commitTransaction(activeTransactionSessionProvider.getSession().getTransaction());
		closeSession(activeTransactionSessionProvider.getSession());

		return new ClonePackageResponse(ResultCode.SUCCESS.code,ResultCode.SUCCESS.name+". Data Package successfully cloned. Cloned Data Package Id : "+newEntity.getId(), clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
	}

	private ClonePackageResponse validateRequestParameters(ClonePackageRequest clonePackageRequest) {

		if(Strings.isNullOrEmpty(clonePackageRequest.getName())){
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE,"Skipping cloning of package. Reason: name of Package to be cloned is not provided");
            }
            return new ClonePackageResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name+". Reason: The name of Package to be cloned can not be empty.", clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
        }

        if(Strings.isNullOrEmpty(clonePackageRequest.getNewName())){
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE,"Skipping cloning of package. Reason: name of Cloned Package is not provided");
            }
            return new ClonePackageResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name+". Reason: name of Cloned Package can not be empty.", clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
        }

		// check type value is provided or not and it should be data and rnc
		if(Strings.isNullOrEmpty(clonePackageRequest.getType())) {
			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping cloning of package. Reason: name of type is not provided");
			}
			return new ClonePackageResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name+". Reason: name of type can not be empty.", clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}
		if(isNotValidClonePackageType(clonePackageRequest.getType())) {
			if(getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Skipping cloning of package. Reason: name of type does not matched");
			}
			return new ClonePackageResponse(ResultCode.INVALID_INPUT_PARAMETER.code, ResultCode.INVALID_INPUT_PARAMETER.name+". Reason: name of type does not match.", clonePackageRequest.getParameter1(), clonePackageRequest.getParameter2(), clonePackageRequest.getWebServiceMethodName(), clonePackageRequest.getWebServiceName());
		}

		return null;
	}

	private boolean isNotValidClonePackageType(String type) {
		return ClonePackageType.DATA.getType().equalsIgnoreCase(type) == false &&
				ClonePackageType.RNC.name().equalsIgnoreCase(type)  == false;
	}

    private void auditClonedPackage(ResourceData resourceData, ResourceData newEntity, JsonObject newEntityJson, ClonePackageRequest request) {
        String message = Discriminators.PACKAGE + " <b><i>" + newEntity.getResourceName() + "</i></b> " + " Created";
        JsonArray difference = ObjectDiffer.diff(CRUDOperationUtil.EMPTY_JSON_OBJECT, newEntityJson);
        CRUDOperationUtil.audit(newEntity,newEntity.getResourceName(),AuditActions.CREATE,adminStaff,request.getRequestIpAddress(),difference , newEntity.getHierarchy(), message);

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(MODULE, "Data Package "+resourceData.getResourceName()+" successfully replicated. New Data Package ID is: " + newEntity.getId());
        }
    }


    private ClonePackageResponse validateGlobalEntityGroups(ClonePackageRequest cloneDataPackageRequest, List<? extends ResourceData> entities, String entityType, Predicate<ResourceData> predicate) {
        EntityNameCollector entityNameCollector = new EntityNameCollector();
        entityNameCollector.start();

        for (ResourceData chargingRuleBaseNameData : entities){
            if (predicate.test(chargingRuleBaseNameData) == false) {
                entityNameCollector.collect(chargingRuleBaseNameData.getResourceName());
            }
        }

        entityNameCollector.stop();
        String failedEntities = entityNameCollector.get();

        ClonePackageResponse reasonMessage = displayValidationMessagesForFailedGroups(cloneDataPackageRequest, failedEntities, entityType);
        if (Objects.nonNull(reasonMessage)) {
            return reasonMessage;
        }
        return null;
    }

	private ClonePackageResponse displayValidationMessagesForFailedGroups(ClonePackageRequest cloneDataPackageRequest, String failedEntities, String entityType) {
		if(StringUtils.isNotBlank(failedEntities)){
			String reasonMessage = ". Reason: "+entityType+" ("+failedEntities+") is not compatible with groups ("+cloneDataPackageRequest.getGroups()+")";
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE,"Error while cloning "+cloneDataPackageRequest.getName()+reasonMessage);
			}
			return new ClonePackageResponse(ResultCode.INVALID_INPUT_PARAMETER.code,ResultCode.INVALID_INPUT_PARAMETER.name+reasonMessage, cloneDataPackageRequest.getParameter1(), cloneDataPackageRequest.getParameter2(), cloneDataPackageRequest.getWebServiceMethodName(), cloneDataPackageRequest.getWebServiceName());
		}
		return null;
	}

	private List<PCCRuleData> getConfiguredPCCRules(PkgData pkgData) {
		List<PCCRuleData> pccRules = new ArrayList<>();
		pkgData.getQosProfiles()
				.forEach(qosProfileData -> qosProfileData.getQosProfileDetailDataList()
						.forEach(qosProfileDetailData -> qosProfileDetailData.getPccRules()
								.forEach(pccRules::add)));
		return pccRules;
	}

	private List<PCCRuleData> getLocalPccRules(List<PCCRuleData> pccRules){
		return pccRules.stream().filter(pccRuleData -> PCCRuleScope.LOCAL == pccRuleData.getScope()).collect(Collectors.toList());
	}

	private List<PCCRuleData> getGlobalPccRules(List<PCCRuleData> pccRules){
		return pccRules.stream().filter(pccRuleData -> PCCRuleScope.GLOBAL == pccRuleData.getScope()).collect(Collectors.toList());
	}

	private List<ChargingRuleBaseNameData> getConfiguredChargingRuleBaseName(PkgData pkgData) {
		List<ChargingRuleBaseNameData> chargingRuleBaseNames = new ArrayList<>();
		pkgData.getQosProfiles()
				.forEach(qosProfileData -> qosProfileData.getQosProfileDetailDataList()
						.forEach(qosProfileDetailData -> qosProfileDetailData.getChargingRuleBaseNames()
								.forEach(chargingRuleBaseNames::add)));
		return chargingRuleBaseNames;
	}

	private PkgData getPkgData(String pkgName, SessionProvider activeTransactionSessionProvider) {
        List<PkgData> dataPackages = new ArrayList();
        try {
            dataPackages = ImportExportCRUDOperationUtil
                    .getByName(PkgData.class,pkgName,activeTransactionSessionProvider).stream()
                    .filter(dataPkg -> dataPkg.getStatus().equals(PkgStatus.ACTIVE.name())).collect(Collectors.toList());
        } catch (Exception e) {
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE,"Operation failed while trying to fetch Data Package with name : "+pkgName);
            }
            getLogger().trace(MODULE,e);
        }
        if (Collectionz.isNullOrEmpty(dataPackages)==false) {
            return dataPackages.get(0);
        }else{
            return null;
        }
    }

	private RncPackageData getPkgRnC(String pkgName, SessionProvider activeTransactionSessionProvider) {
		try {
			return ImportExportCRUDOperationUtil
					.getByName(RncPackageData.class, pkgName, activeTransactionSessionProvider).stream()
					.filter(rncPkg -> rncPkg.getStatus().equals(PkgStatus.ACTIVE.name())).findFirst().orElse(null);

		} catch (Exception e) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Operation failed while trying to fetch RnC Package with name : " + pkgName);
			}
			getLogger().trace(MODULE, e);
			return null;
		}
	}

    private String getRandomIdForReplicatedPackage() {
		return UUID.randomUUID().toString();
	}

	private String fromGroupNames(String groups, SessionProvider activeTransactionSessionProvider) throws Exception {
		StringBuilder groupIds = new StringBuilder();
		List<String> groupNames = CommonConstants.COMMA_SPLITTER.split(groups);
		if(Collectionz.isNullOrEmpty(groupNames) == false) {
			for (String groupName : groupNames) {
				GroupData groupData = null;
                List<GroupData> groupDataList = ImportExportCRUDOperationUtil.getByName(GroupData.class, groupName.trim(), activeTransactionSessionProvider);
				if (Collectionz.isNullOrEmpty(groupDataList) == false) {
					groupData =  groupDataList.get(0);
				}
                if (Objects.isNull(groupData)) {
                    throw new InvalidParameterException("Group with name: " + groupName + " doesn't exist.");
                }
                groupIds.append(groupData.getId()).append(",");
            }
			groupIds.deleteCharAt(groupIds.lastIndexOf(","));
		}
		return groupIds.toString();
	}

	private void setPCCRuleName(List<PCCRuleData> pccRules, String newName) {
		if(CollectionUtils.isEmpty(pccRules)){
			return;
		}

		for (PCCRuleData pccRule : pccRules) {
			String name = pccRule.getName();
			String monitoringKey = pccRule.getMonitoringKey();

			pccRule.setName(name+CommonConstants.UNDERSCORE+newName);
			pccRule.setMonitoringKey(monitoringKey+CommonConstants.UNDERSCORE+newName);
		}
	}

	public CloneProductOfferResponse cloneProductOffer(CloneProductOfferRequest request) {
		applyRequestInterceptors(request);

		CloneProductOfferResponse validationResponse = validateProductOfferRequestParameters(request);
		if (validationResponse != null) {
			return validationResponse;
		}

		return doCloneProductOffer(request);
	}

	private CloneProductOfferResponse doCloneProductOffer(CloneProductOfferRequest cloneProductOfferRequest) {

		SessionProvider activeTransactionSessionProvider = new ActiveTransactionSessionProviderImpl();

		// validate existing product offer info
		ProductOfferData existingProductOffer = getProductOffer(cloneProductOfferRequest.getName(), activeTransactionSessionProvider);
		if(Objects.isNull(existingProductOffer)) {
			getLogger().error(MODULE, "Error while creating product offer " + cloneProductOfferRequest.getNewName() + ". Reason: Product offer with name "+cloneProductOfferRequest.getName()+" not found");
			return new CloneProductOfferResponse(ResultCode.NOT_FOUND.code, ResultCode.NOT_FOUND.name+". Reason: Product offer with name "+cloneProductOfferRequest.getName()+" not found.", cloneProductOfferRequest.getParameter1(), cloneProductOfferRequest.getParameter2(), cloneProductOfferRequest.getWebServiceMethodName(), cloneProductOfferRequest.getWebServiceName());
		}

		// check duplicate product offer of new name
		ProductOfferData newProductOffer = getProductOffer(cloneProductOfferRequest.getNewName(), activeTransactionSessionProvider);
		if(Objects.nonNull(newProductOffer)) {
			getLogger().error(MODULE, "Product Offer with new name: " + cloneProductOfferRequest.getNewName() + "already exists.");
			return new CloneProductOfferResponse(ResultCode.ALREADY_EXIST.code, ResultCode.ALREADY_EXIST.name+". Reason: Product offer with new name "+cloneProductOfferRequest.getNewName()+" already exists.", cloneProductOfferRequest.getParameter1(), cloneProductOfferRequest.getParameter2(), cloneProductOfferRequest.getWebServiceMethodName(), cloneProductOfferRequest.getWebServiceName());
		}

		ProductOfferData newEntity = existingProductOffer.copyModel(); //clone the new entity
		newEntity.setId(getRandomIdForReplicatedPackage());
		newEntity.setName(cloneProductOfferRequest.getNewName());
		newEntity.setCreatedDateAndStaff(adminStaff);

		JsonObject newEntityJson = newEntity.toJson();

		ImportExportCRUDOperationUtil.save(newEntity, activeTransactionSessionProvider);

		syncSession(activeTransactionSessionProvider.getSession());

		auditClonedProductOffer(existingProductOffer, newEntity, newEntityJson,cloneProductOfferRequest);

		commitTransaction(activeTransactionSessionProvider.getSession().getTransaction());
		closeSession(activeTransactionSessionProvider.getSession());

		return new CloneProductOfferResponse(ResultCode.SUCCESS.code,ResultCode.SUCCESS.name+". Product offer successfully cloned. Cloned Product Offer Id : "+newEntity.getId(), cloneProductOfferRequest.getParameter1(), cloneProductOfferRequest.getParameter2(), cloneProductOfferRequest.getWebServiceMethodName(), cloneProductOfferRequest.getWebServiceName());
	}

	private void auditClonedProductOffer(ResourceData resourceData, ResourceData newEntity, JsonObject newEntityJson, CloneProductOfferRequest request) {
		String message = Discriminators.PRODUCT_OFFER + " <b><i>" + newEntity.getResourceName() + "</i></b> " + " Created";
		JsonArray difference = ObjectDiffer.diff(CRUDOperationUtil.EMPTY_JSON_OBJECT, newEntityJson);
		CRUDOperationUtil.audit(newEntity,newEntity.getResourceName(),AuditActions.CREATE,adminStaff,request.getRequestIpAddress(),difference , newEntity.getHierarchy(), message);

		if(getLogger().isDebugLogLevel()){
			getLogger().debug(MODULE, "Product Offer "+resourceData.getResourceName()+" successfully replicated. New Product Offer ID is: " + newEntity.getId());
		}
	}

	private ProductOfferData getProductOffer(String productOfferName, SessionProvider activeTransactionSessionProvider) {
		try {
			return ImportExportCRUDOperationUtil
					.getByName(ProductOfferData.class, productOfferName, activeTransactionSessionProvider).stream()
					.filter(productOfferData -> productOfferData.getStatus().equals(PkgStatus.ACTIVE.name())).findFirst().orElse(null);

		} catch (Exception e) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Operation failed while trying to fetch Product Offer with name : " + productOfferName);
			}
			getLogger().trace(MODULE, e);
			return null;
		}
	}

	private CloneProductOfferResponse validateProductOfferRequestParameters(CloneProductOfferRequest request) {
		if(Strings.isNullOrEmpty(request.getName())){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE,"Skipping cloning of product offer. Reason: name of product offer to be cloned is not provided");
			}
			return new CloneProductOfferResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name+". Reason: The name of product offer to be cloned can not be empty.", request.getParameter1(), request.getParameter2(), request.getWebServiceMethodName(), request.getWebServiceName());
		}

		if(Strings.isNullOrEmpty(request.getNewName())){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE,"Skipping cloning of product offer. Reason: name of Cloned product offer is not provided");
			}
			return new CloneProductOfferResponse(ResultCode.INPUT_PARAMETER_MISSING.code, ResultCode.INPUT_PARAMETER_MISSING.name+". Reason: name of Cloned product offer can not be empty.", request.getParameter1(), request.getParameter2(), request.getWebServiceMethodName(), request.getWebServiceName());
		}
		return null;
	}

	private static class EntityNameCollector implements Collector<String,String>{
      private StringBuilder pccRuleNames ;

		@Override
		public void start() {
			pccRuleNames = new StringBuilder();
		}

		@Override
		public void stop() {
			if(pccRuleNames.length() > 0 ) {
				pccRuleNames.deleteCharAt(pccRuleNames.length() - 1);
			}
		}

		@Override
		public void collect(String name) {
              pccRuleNames.append(name).append(CommonConstants.COMMA);
		}

		@Override
		public String get() {
			if(pccRuleNames!=null && pccRuleNames.length()>0){
				return pccRuleNames.toString();
			}
			return null;
		}
	}

	private void applyRequestInterceptors(WebServiceRequest request) {
		for(WebServiceInterceptor interceptor : interceptors){
			interceptor.requestReceived(request);
		}
	}
}
