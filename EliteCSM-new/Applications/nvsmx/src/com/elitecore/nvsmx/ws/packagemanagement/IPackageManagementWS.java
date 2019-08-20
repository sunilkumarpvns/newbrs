package com.elitecore.nvsmx.ws.packagemanagement;

import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupDataExt;
import com.elitecore.nvsmx.ws.packagemanagement.response.ChargingRuleManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.PCCRuleManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.PackageManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.RatingGroupManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.DataServiceTypeManagementResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(name="PackageManagementWS")
public interface IPackageManagementWS {
	public static final String WS_IMPORT_PACKAGE 		= "wsImportPackage";
	public static final String WS_IMPORT_DATA_SERVICE_TYPE = "wsImportDataServiceType";
	public static final String WS_IMPORT_DATA_SERVICE_TYPES = "wsImportDataServiceTypes";
	public static final String WS_IMPORT_RATING_GROUP 	= "wsImportRatingGroup";
	public static final String WS_IMPORT_RATING_GROUPS 	= "wsImportRatingGroups";
	public static final String WS_IMPORT_PACKAGES 		= "wsImportPackages";
	public static final String WS_IMPORT_IMS_PACKAGE 	= "wsImportIMSPackage";
	public static final String WS_IMPORT_IMS_PACKAGES 	= "wsImportIMSPackages";
	public static final String WS_IMPORT_EMERGENCY_PACKAGE 	= "wsImportEmergencyPackage";
	public static final String WS_IMPORT_EMERGENCY_PACKAGES = "wsImportEmergencyPackages";
	public static final String WS_IMPORT_PROMOTIONAL_PACKAGE 	= "wsImportPromotionalPackage";
	public static final String WS_IMPORT_PROMOTIONAL_PACKAGES = "wsImportPromotionalPackages";
	public static final String WS_IMPORT_GLOBAL_PCC_RULE 	= "wsImportGlobalPCCRule";
	public static final String WS_IMPORT_GLOBAL_PCC_RULES 	= "wsImportGlobalPCCRules";

	public static final String WS_IMPORT_CHARGING_RULE_BASE_NAMES = "wsImportChargingRuleBaseNames";
	public static final String WS_IMPORT_CHARGING_RULE_BASE_NAME = "wsImportChargingRuleBaseName";

	@WebMethod(operationName = WS_IMPORT_PACKAGE)
	public PackageManagementResponse wsImportPackage(
			@WebParam(name = "action") String action,
			@WebParam(name = "packageData") PkgData packageData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);


	@WebMethod(operationName = WS_IMPORT_DATA_SERVICE_TYPE)
	public DataServiceTypeManagementResponse wsImportDataServiceType(
			@WebParam(name = "action") String action,
			@WebParam(name = "dataServiceTypeData") DataServiceTypeDataExt serviceTypeData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_RATING_GROUP)
	public RatingGroupManagementResponse wsImportRatingGroup(
			@WebParam(name = "action") String action,
			@WebParam(name = "ratingGroupData") RatingGroupDataExt ratingGroup,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_RATING_GROUPS)
	public RatingGroupManagementResponse wsImportRatingGroups(
			@WebParam(name = "action") String action,
			@WebParam(name = "ratingGroupData") List<RatingGroupDataExt> ratingGroup,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_DATA_SERVICE_TYPES)
	public DataServiceTypeManagementResponse wsImportDataServiceTypes(
			@WebParam(name = "action") String action,
			@WebParam(name = "dataServiceTypeData") List<DataServiceTypeDataExt> serviceType,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_PACKAGES)
	public PackageManagementResponse wsImportPackages(
			@WebParam(name = "action") String action,
			@WebParam(name = "packageData") List<PkgData> pkgData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_IMS_PACKAGE)
	public PackageManagementResponse wsImportIMSPackage(
			@WebParam(name = "action") String action,
			@WebParam(name = "imsPackageData") IMSPkgData imsPkgData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_IMS_PACKAGES)
	public PackageManagementResponse wsImportIMSPackages(
			@WebParam(name = "action") String action,
			@WebParam(name = "imsPackageData") List<IMSPkgData> imsPkgData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_EMERGENCY_PACKAGE)
	public PackageManagementResponse wsImportEmergencyPackage(
			@WebParam(name = "action") String action,
			@WebParam(name = "emergencyPackage") EmergencyPkgDataExt emergencyPkgData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_EMERGENCY_PACKAGES)
	public PackageManagementResponse wsImportEmergencyPackages(
			@WebParam(name = "action") String action,
			@WebParam(name = "emergencyPackage") List<EmergencyPkgDataExt> emergencyPkgData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_GLOBAL_PCC_RULE)
	public PCCRuleManagementResponse wsImportGlobalPCCRule(
			@WebParam(name = "action") String action,
			@WebParam(name = "globalPCCRuleData") PCCRuleData pccRuleData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_GLOBAL_PCC_RULES)
	public PCCRuleManagementResponse wsImportGlobalPCCRules(
			@WebParam(name = "action") String action,
			@WebParam(name = "globalPccRuleDatas") List<PCCRuleData> pccRuleDatas,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_CHARGING_RULE_BASE_NAMES)
	public ChargingRuleManagementResponse wsImportChargingRuleBaseNames(
			@WebParam(name = "action") String action,
			@WebParam(name = "chargingRuleBaseNames") List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_CHARGING_RULE_BASE_NAME)
	public ChargingRuleManagementResponse wsImportChargingRuleBaseName(
			@WebParam(name = "action") String action,
			@WebParam(name = "chargingRuleBaseName") ChargingRuleBaseNameData chargingRuleBaseNameData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_PROMOTIONAL_PACKAGE)
	public PackageManagementResponse wsImportPromotionalPackage(
			@WebParam(name = "action") String action,
			@WebParam(name = "packageData") PkgData packageData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

	@WebMethod(operationName = WS_IMPORT_PROMOTIONAL_PACKAGES)
	public PackageManagementResponse wsImportPromotionalPackages(
			@WebParam(name = "action") String action,
			@WebParam(name = "packageData") List<PkgData> pkgData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2);

}
