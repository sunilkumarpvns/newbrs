package com.elitecore.nvsmx.ws.packagemanagement;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupDataExt;
import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.nvsmx.ws.packagemanagement.blmanager.PackageImportWSBLManager;
import com.elitecore.nvsmx.ws.packagemanagement.request.ChargingRuleBaseNameListRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.ChargingRuleBaseNameRequest;
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
import com.elitecore.nvsmx.ws.packagemanagement.response.PCCRuleManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.PackageManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.RatingGroupManagementResponse;
import com.elitecore.nvsmx.ws.packagemanagement.response.DataServiceTypeManagementResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.List;

public class PackageManagementWS  implements IPackageManagementWS {

	private static final String MODULE = "PKG-MGMT-WS";
	private PackageImportWSBLManager packageManagementWSBLManager;

	public PackageManagementWS() {
		packageManagementWSBLManager = new PackageImportWSBLManager();
	}

	@Override
	@WebMethod(operationName = WS_IMPORT_PACKAGE)
	public PackageManagementResponse wsImportPackage(
			@WebParam(name = "action") String action,
			PkgData packageData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportPackage with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importPackage(new PackageManagementRequest(packageData, action, parameter1, parameter2, ACLModules.DATAPKG));
	}


	@Override
	@WebMethod(operationName = WS_IMPORT_DATA_SERVICE_TYPE)
	public DataServiceTypeManagementResponse wsImportDataServiceType(
			@WebParam(name = "action") String action,
			@WebParam(name = "dataServiceType") DataServiceTypeDataExt dataServiceTypeData,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportDataServiceType with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importDataServiceType(new ServiceTypeManagementRequest(dataServiceTypeData, action, parameter1, parameter2));
	}

	@Override
	@WebMethod(operationName = WS_IMPORT_RATING_GROUP)
	public RatingGroupManagementResponse wsImportRatingGroup(
			@WebParam(name = "action") String action,
			RatingGroupDataExt ratingGroup,
			@WebParam(name = "parameter1") String parameter1,
			@WebParam(name = "parameter2") String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportRatingGroup with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importRatingGroup(new RatingGroupManagementRequest(ratingGroup, action, parameter1, parameter2));
	}

	@Override
	public RatingGroupManagementResponse wsImportRatingGroups(@WebParam(name = "action") String action,
															  @WebParam(name = "ratingGroup") List<RatingGroupDataExt> ratingGroup,
															  @WebParam(name = "parameter1") String parameter1,
															  @WebParam(name = "parameter2") String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportRatingGroups with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importRatingGroups(new RatingGroupListManagementRequest(ratingGroup, action, parameter1, parameter2));
	}

	@Override
	public DataServiceTypeManagementResponse wsImportDataServiceTypes(@WebParam(name = "action") String action,
																	  @WebParam(name = "dataServiceType") List<DataServiceTypeDataExt> serviceType,
																	  @WebParam(name = "parameter1") String parameter1,
																	  @WebParam(name = "parameter2") String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportDataServiceTypes with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importDataServiceTypes(new ServiceTypeListManagementRequest(serviceType, action, parameter1, parameter2));
	}

	@Override
	public PackageManagementResponse wsImportPackages(@WebParam(name = "action") String action,
													  @WebParam(name = "packageData") List<PkgData> pkgData,
													  @WebParam(name = "parameter1") String parameter1,
													  @WebParam(name = "parameter2") String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportPackages with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importPackages(new PackageManagementListRequest(pkgData, action, parameter1, parameter2, ACLModules.DATAPKG));
	}

	@Override
	public PackageManagementResponse wsImportIMSPackage(String action, IMSPkgData imsPkgData, String parameter1, String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportIMSPackage with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importIMSPackage(new IMSPackageManagementRequest(imsPkgData, action, parameter1, parameter2));
	}

	@Override
	public PackageManagementResponse wsImportIMSPackages(String action, List<IMSPkgData> imsPkgData, String parameter1, String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportIMSPackages with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importIMSPackages(new IMSPackageManagementListRequest(imsPkgData, action, parameter1, parameter2));
	}

	@Override
	public PackageManagementResponse wsImportEmergencyPackage(@WebParam(name = "action") String action,
															  @WebParam(name = "emergencyPackage") EmergencyPkgDataExt emergencyPkgData,
															  @WebParam(name = "parameter1") String parameter1,
															  @WebParam(name = "parameter2") String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportEmergencyPackage with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importEmergencyPackage(new EmergencyPackageManagementRequest(emergencyPkgData, action, parameter1, parameter2));
	}

	@Override
	public PackageManagementResponse wsImportEmergencyPackages(@WebParam(name = "action") String action,
															   @WebParam(name = "emergencyPackage") List<EmergencyPkgDataExt> emergencyPkgData,
															   @WebParam(name = "parameter1") String parameter1,
															   @WebParam(name = "parameter2") String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportEmergencyPackages with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importEmergencyPackages(new EmergencyPackageManagementListRequest(emergencyPkgData, action, parameter1, parameter2));
	}

	@Override
	public PCCRuleManagementResponse wsImportGlobalPCCRule(String action, PCCRuleData pccRuleData, String parameter1, String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportGlobalPCCRule with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}

		return packageManagementWSBLManager.importGlobalPCCRule(new PCCRuleRequest(pccRuleData, action, parameter1, parameter2));
	}

	@Override
	public PCCRuleManagementResponse wsImportGlobalPCCRules(String action, List<PCCRuleData> pccRuleDatas, String parameter1, String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportGlobalPCCRules with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}

		return packageManagementWSBLManager.importGlobalPCCRules(new PCCRuleListRequest(pccRuleDatas, action, parameter1, parameter2));
	}

	private ILogger getLogger() {
		return LogManager.getLogger();

	}

	@Override
	public ChargingRuleManagementResponse wsImportChargingRuleBaseName(String action, ChargingRuleBaseNameData chargingRuleBaseNameData, String parameter1, String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportChargingRuleBaseName with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}

		return packageManagementWSBLManager.importChargingRuleBaseName(new ChargingRuleBaseNameRequest(chargingRuleBaseNameData, action, parameter1, parameter2));
	}

	@Override
	public PackageManagementResponse wsImportPromotionalPackage(String action, PkgData packageData, String parameter1, String parameter2) {
			parameter1 = StringUtil.trimParameter(parameter1);
			parameter2 = StringUtil.trimParameter(parameter2);
			action = StringUtil.trimParameter(action);
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Called wsImportPromotionalPackage with Request Parameters: "
						+ " Action: " + action
						+ " Parameter1: " + parameter1
						+ ", Parameter2: " + parameter2);
			}
			return packageManagementWSBLManager.importPackage(new PackageManagementRequest(packageData, action, parameter1, parameter2,ACLModules.PROMOTIONALPKG));

		}

	@Override
	public PackageManagementResponse wsImportPromotionalPackages(@WebParam(name = "action") String action,
																 @WebParam(name = "packageData") List<PkgData> pkgData,
																 @WebParam(name = "parameter1") String parameter1,
																 @WebParam(name = "parameter2") String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportPromotionalPackages with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importPackages(new PackageManagementListRequest(pkgData, action, parameter1, parameter2, ACLModules.PROMOTIONALPKG));
	}

	@Override
	public ChargingRuleManagementResponse wsImportChargingRuleBaseNames(String action, List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas, String parameter1, String parameter2) {
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Called wsImportChargingRuleBaseNames with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}

		return packageManagementWSBLManager.importChargingRuleBaseNames(new ChargingRuleBaseNameListRequest(chargingRuleBaseNameDatas, action, parameter1, parameter2));
	}

}
