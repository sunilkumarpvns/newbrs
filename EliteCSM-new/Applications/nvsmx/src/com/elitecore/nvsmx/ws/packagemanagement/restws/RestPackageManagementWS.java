package com.elitecore.nvsmx.ws.packagemanagement.restws;

import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.pd.prefix.PrefixDataExt;
import com.elitecore.corenetvertex.pkg.EmergencyPkgContainer;
import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.PkgContainer;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeContainer;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.ImsPkgContainer;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupContainer;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupDataExt;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.util.StringUtil;
import com.elitecore.nvsmx.pd.importexportutility.prefix.PrefixImportExportUtility;
import com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename.ChargingRuleBaseNameContainer;
import com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PCCRuleContainer;
import com.elitecore.nvsmx.sm.controller.prefix.PrefixCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import com.elitecore.nvsmx.ws.packagemanagement.blmanager.PackageImportWSBLManager;
import com.elitecore.nvsmx.ws.packagemanagement.request.ChargingRuleBaseNameListRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.ChargingRuleBaseNameRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.ClonePackageRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.ClonePackageRestRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.CloneProductOfferRequest;
import com.elitecore.nvsmx.ws.packagemanagement.request.CloneProductOfferRestRequest;
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
import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
@Path("/")
@Api(value="/restful/import/import" , description = "Apis for import packages ")
public class RestPackageManagementWS {

	private static final String MODULE = "PKG-MGMT-REST-WS";
	private static final String WEB_SERVICE_NAME = RestPackageManagementWS.class.getSimpleName();
	private static final String CLONE_PACKAGE = "clonePackage";
	private PackageImportWSBLManager packageManagementWSBLManager;

	public RestPackageManagementWS() {
		packageManagementWSBLManager = new PackageImportWSBLManager();
	}

	@POST
	@Path("/pkg")
	public PackageManagementResponse wsImportPackage(
			@QueryParam(value = "action")String action,
			PkgData packageData,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportPackage with Request Parameters: "
				+ " Action: " + action
				+ " Parameter1: " + parameter1
				+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importPackage(new PackageManagementRequest(packageData, action, parameter1, parameter2, ACLModules.DATAPKG));
	}


	@POST
	@Path("/dataServiceType")
	public DataServiceTypeManagementResponse wsImportDataServiceType(
			@QueryParam(value = "action")String action,
			DataServiceTypeDataExt dataServiceType,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportDataServiceType with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importDataServiceType(new ServiceTypeManagementRequest(dataServiceType, action, parameter1, parameter2));
	}

	@POST
	@Path("/ratingGroup")
	public RatingGroupManagementResponse wsImportRatingGroup(
			@QueryParam(value = "action")String action,
			RatingGroupDataExt ratingGroup,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportRatingGroup with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importRatingGroup(new RatingGroupManagementRequest(ratingGroup, action, parameter1, parameter2));
	}


	@POST
	@Path("/globalPCCRule")
	public PCCRuleManagementResponse wsImportGlobalPCCRule(
			@QueryParam(value = "action") String action,
			PCCRuleData pccRuleData,
			@QueryParam(value = "parameter1")String parameter1,
			@QueryParam(value = "parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportGlobalPCCRule with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}

       return packageManagementWSBLManager.importGlobalPCCRule(new PCCRuleRequest(pccRuleData,action,parameter1,parameter2));
	}

	@POST
	@Path("/pkgs")
	public PackageManagementResponse wsImportPackages(
			@QueryParam(value = "action")String action,
			PkgContainer pkgContainer,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportPackages with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importPackages(new PackageManagementListRequest(pkgContainer.getPkgData(), action, parameter1, parameter2, ACLModules.DATAPKG));
	}


	@POST
	@Path("/dataServiceTypes")
	public DataServiceTypeManagementResponse wsImportDataServiceTypes(
			@QueryParam(value = "action")String action,
			DataServiceTypeContainer dataServiceTypeContainer,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportDataServiceTypes with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importDataServiceTypes(new ServiceTypeListManagementRequest(dataServiceTypeContainer.getserviceType(), action, parameter1, parameter2));
	}

	@POST
	@Path("/ratingGroups")
	public RatingGroupManagementResponse wsImportRatingGroups(
			@QueryParam(value = "action")String action,
			RatingGroupContainer ratingGroup,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportRatingGroups with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importRatingGroups(new RatingGroupListManagementRequest(ratingGroup.getRatingGroup(), action, parameter1, parameter2));
	}

	@POST
	@Path("/imsPkg")
	public PackageManagementResponse wsImportIMSPackage(
			@QueryParam(value = "action") String action,
			IMSPkgData imsPackageData,
			@QueryParam(value = "parameter1") String parameter1,
			@QueryParam(value = "parameter2") String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportIMSPackage with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importIMSPackage(new IMSPackageManagementRequest(imsPackageData, action, parameter1, parameter2));
	}

	@POST
	@Path("/imsPkgs")
	public PackageManagementResponse wsImportIMSPackages(
			@QueryParam(value = "action") String action,
			ImsPkgContainer imsPkgContainer,
			@QueryParam(value = "parameter1") String parameter1,
			@QueryParam(value = "parameter2") String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportPackages with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importIMSPackages(new IMSPackageManagementListRequest(imsPkgContainer.getImsPkgData(), action, parameter1, parameter2));
	}

	@POST
	@Path("/globalPCCRules")
	public PCCRuleManagementResponse wsImportGlobalPCCRules(
		@QueryParam(value = "action") String action,
		PCCRuleContainer pccRuleContainer,
		@QueryParam(value = "parameter1") String parameter1,
		@QueryParam(value = "parameter2") String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportGlobalPCCRules with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}

		return packageManagementWSBLManager.importGlobalPCCRules(new PCCRuleListRequest(pccRuleContainer.getPccRules(),action,parameter1,parameter2) );
	}


	@POST
	@Path("/emergencyPkg")
	public PackageManagementResponse wsImportEmergencyPackage(
			@QueryParam(value = "action")String action,
			EmergencyPkgDataExt emergencyPackageData,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportEmergencyPackage with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importEmergencyPackage(new EmergencyPackageManagementRequest(emergencyPackageData, action, parameter1, parameter2));
	}

	@POST
	@Path("/emergencyPkgs")
	public PackageManagementResponse wsImportEmergencyPackages(
			@QueryParam(value = "action")String action,
			EmergencyPkgContainer emergencyPkgContainer,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportEmergencyPackages with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importEmergencyPackages(new EmergencyPackageManagementListRequest(emergencyPkgContainer.getEmergencyPkgData(), action, parameter1, parameter2));
	}

	@POST
	@Path("/chargingRuleBaseName")
	public ChargingRuleManagementResponse wsImportChargingRuleBaseName(
			@QueryParam(value = "action") String action,
			ChargingRuleBaseNameData chargingRuleBaseNameData,
			@QueryParam(value = "parameter1")String parameter1,
			@QueryParam(value = "parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportChargingRuleBaseName with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}

		return packageManagementWSBLManager.importChargingRuleBaseName(new ChargingRuleBaseNameRequest(chargingRuleBaseNameData, action, parameter1, parameter2));
	}

	@POST
	@Path("/chargingRuleBaseNames")
	public ChargingRuleManagementResponse wsImportChargingRuleBaseNames(
			@QueryParam(value = "action") String action,
			ChargingRuleBaseNameContainer chargingRuleBaseNameContainer,
			@QueryParam(value = "parameter1") String parameter1,
			@QueryParam(value = "parameter2") String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportChargingRuleBaseNames with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}

		return packageManagementWSBLManager.importChargingRuleBaseNames(new ChargingRuleBaseNameListRequest(chargingRuleBaseNameContainer.getChargingRuleBaseNameDatas(), action, parameter1, parameter2));
	}

	@POST
	@Path("/promotionalPkg")
	public PackageManagementResponse wsImportPromotionalPackage(
			@QueryParam(value = "action")String action,
			PkgData packageData,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportPromotionalPackage with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importPackage(new PackageManagementRequest(packageData, action, parameter1, parameter2,ACLModules.PROMOTIONALPKG));
	}

	@POST
	@Path("/promotionalPkgs")
	public PackageManagementResponse wsImportPromotionalPackages(
			@QueryParam(value = "action")String action,
			PkgContainer pkgContainer,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){
		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		action = StringUtil.trimParameter(action);
		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Called wsImportPromotionalPackages with Request Parameters: "
					+ " Action: " + action
					+ " Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.importPackages(new PackageManagementListRequest(pkgContainer.getPkgData(), action, parameter1, parameter2, ACLModules.PROMOTIONALPKG));

	}

	@POST
	@Path("/clonePackages")

	@ApiOperation(
			value = "To clone existing Data And Rnc Packages",
			notes = "Cloned package will have the same configuration as that of the existing package",
			response = ClonePackageResponse.class
	)

	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Invalid Input Parameter with one of following reasons;<br/>" +
					"1. Provided group name does not exist <br/>"+
					"2. Global PCC Rule/Charging Rule Base Name is not compatible with provided groups"+
					"3. Cloned Package type does not matched"),
			@ApiResponse(code = 401, message = "One of the following Input Parameter Missing;<br/>" +
					"1. Data/RnC Package name to be cloned <br/>"+
					"2. Cloned Data/RnC Package name"+
					"3. Cloned Package type name not provided"),
			@ApiResponse(code = 404, message = "Data/RnC Package with provided name not found"),
			@ApiResponse(code = 450, message = "Data/RnC Package with provided cloned name already exists"),
			@ApiResponse(code = 500, message = "Internal Error"),
			@ApiResponse(code = 599, message = "Operation not supported for provided Data/RnC package"),
	})

	public ClonePackageResponse clonePackage(
			@ApiParam(required = true) ClonePackageRestRequest request,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){

		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		String name = StringUtil.trimParameter(request.getName());
		String newName = StringUtil.trimParameter(request.getNewName());
		String groups = StringUtil.trimParameter(request.getGroups());
		String type = StringUtil.trimParameter(request.getType());

		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Cloning Package with following request parameters: "
					+ " Name: " + name
					+ ", New Name: " + newName
					+ ", Group: " + groups
					+ ", Type: " + type
					+ ", Parameter1: " + parameter1
					+ ", Parameter2: " + parameter2);
		}
		return packageManagementWSBLManager.clonePackage(new ClonePackageRequest(name, newName, groups, type, parameter1, parameter2, WEB_SERVICE_NAME, CLONE_PACKAGE));

	}

	@POST
	@Path("/cloneProductOffer")
	@ApiOperation(
			value = "To clone existing product offer",
			notes = "Cloned product offer will have the same configuration as that of the existing product offer",
			response = CloneProductOfferResponse.class
	)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Invalid Input Parameter with one of following reasons;<br/>" +
					"1. Provided group name does not exist <br/>"),
			@ApiResponse(code = 401, message = "One of the following Input Parameter Missing;<br/>" +
					"1. Product Offer name to be cloned <br/>"+
					"2. New Product Offer name<br/>"),
			@ApiResponse(code = 404, message = "Product Offer with provided name not found"),
			@ApiResponse(code = 450, message = "Product Offer with provided cloned name already exists"),
	})
	public CloneProductOfferResponse cloneProductOffer(
			@ApiParam(required = true) CloneProductOfferRestRequest request,
			@QueryParam(value="parameter1")String parameter1,
			@QueryParam(value="parameter2")String parameter2){

		parameter1 = StringUtil.trimParameter(parameter1);
		parameter2 = StringUtil.trimParameter(parameter2);
		String name = StringUtil.trimParameter(request.getName());
		String newName = StringUtil.trimParameter(request.getNewName());
		String groups = StringUtil.trimParameter(request.getGroups());

		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(MODULE, "Cloning Product Offer with following request parameters: " + request.toString());
		}
		return packageManagementWSBLManager.cloneProductOffer(new CloneProductOfferRequest(name, newName, groups, parameter1, parameter2, WEB_SERVICE_NAME, CLONE_PACKAGE));
	}

	@POST
	@Path("/prefix")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response wsImportPrefix(@Multipart("file") Attachment uploadedInputStream, @Context HttpServletRequest request) {
		long startTime = System.nanoTime();
		Response.ResponseBuilder response = null;
		if (request.getParameter("user_action") == null) {
			response = Response.status(Response.Status.BAD_REQUEST);
			response = Response.ok("Please enter parameter user_action");
			return response.build();
		}
		String userAction = (request.getParameter("user_action").toUpperCase());
		String fileNameExtention = uploadedInputStream.getDataHandler().getName().substring(uploadedInputStream.getDataHandler().getName().lastIndexOf('.') + 1);
		String fileName = NVSMXCommonConstants.IMPORT_PREFIX_ERROR + PrefixImportExportUtility.getTime() + NVSMXCommonConstants.CSV;
		File outputFile = null;
		String inputFileString = "";
		String line;
		File inputFile = null;
		int length = 0;
		String strCounter = "";
		InputStream inputStreamnew = null;
		String userName = "";


		if (request.getParameter("staff_user_name") == null) {
			userName= NVSMXCommonConstants.ADMIN;
		}
		else {
			userName=request.getParameter("staff_user_name");
		}

		if (!(fileNameExtention.equalsIgnoreCase("txt") || fileNameExtention.equalsIgnoreCase("csv"))) {
			response = Response.status(Response.Status.BAD_REQUEST);
			response = Response.ok("Please select .txt or .csv file");
			return response.build();
		}
		try {
			InputStream inputStreamTemp = uploadedInputStream.getDataHandler().getInputStream();

			try (final Reader reader = new InputStreamReader(inputStreamTemp)) {
				inputFileString = CharStreams.toString(reader);
			}
			if ((inputFileString.length()) == 0) {
				response = Response.status(Response.Status.BAD_REQUEST);
				response = Response.ok("File is empty");
				return response.build();
			}
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error while importing Prefix data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		try {
			inputFile = File.createTempFile("temp", ".txt");
			FileOutputStream fos = new FileOutputStream(inputFile);
			String str = inputFileString;
			byte[] strToBytes = str.getBytes();
			fos.write(strToBytes);
			fos.flush();
			if (fos != null) {
				fos.close();
			}
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error while importing Prefix data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		StringBuilder stringBuilderForInputFile = new StringBuilder();
		BufferedReader bufferedReaderForInputFile = null;
        BufferedReader headerBufferReader = null;
		try {
		    headerBufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile.getAbsoluteFile())));
			String header = headerBufferReader.readLine();
			if (!(header.equals(NVSMXCommonConstants.PREFIX_HEADER) || (header.equals(NVSMXCommonConstants.PREFIX_HEADER_WITH_REMARKS)))) {
				response = Response.status(Response.Status.BAD_REQUEST);
				response = Response.ok("Select file with Proper header. Expected Header (" + NVSMXCommonConstants.PREFIX_HEADER + ")");
				return response.build();
			}


		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while importing Prefix data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		finally {
            Closeables.closeQuietly(headerBufferReader);
        }
		try {
			bufferedReaderForInputFile = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile.getAbsoluteFile())));
			while ((line = bufferedReaderForInputFile.readLine()) != null) {
				if (!(line.equals("")))
				{
					length++;
					stringBuilderForInputFile.append(line);
					stringBuilderForInputFile.append("\n");
				}
			}
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while importing Prefix data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} finally {
			if (bufferedReaderForInputFile != null) {
				try {
					bufferedReaderForInputFile.close();
				} catch (IOException e) {
					LogManager.getLogger().error(MODULE, "Error while importing Prefix data. Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
		}
		for (int i = 0; i < length - 1; i++) {
			if (i == length - 2) {
				strCounter = strCounter + i;
			} else {
				strCounter = strCounter + i + ",";
			}
		}

		Session session = HibernateSessionFactory.getSession();
		Transaction transaction = session.beginTransaction();
		HttpSession sessionForRestApi = request.getSession();
		try {
			inputStreamnew = new FileInputStream(inputFile.getAbsoluteFile());
			List<StaffData> staffData = CRUDOperationUtil.findAll(StaffData.class);
			List<String> staffIDS = new ArrayList<>();
			List<String> staffUserNames = new ArrayList<>();
			for (int i = 0; i < staffData.size(); i++) {
				staffUserNames.add(staffData.get(i).getUserName());
				staffIDS.add(staffData.get(i).getId());
			}
			String staffID = staffIDS.get(staffUserNames.indexOf(userName));

			PrefixImportExportUtility prefixImportExportUtility = new PrefixImportExportUtility();
			List<PrefixDataExt> pd = prefixImportExportUtility.importPrefixExtended(inputStreamnew);

			String[] splitURL = request.getRequestURL().toString().split("/");

			PrefixCTRL prefixCTRL = new PrefixCTRL();
			prefixCTRL.importDataExtended(strCounter, userAction, pd, splitURL[splitURL.length - 1], staffID, request);

			FileOutputStream fos = null;
			String strResult = PrefixImportExportUtility.mergeValuesAndStatus((JsonArray) request.getSession().getAttribute("importDataPrefixEXTS"), (JsonArray) request.getSession().getAttribute("reason"));
			try {
				outputFile = File.createTempFile("temp", ".txt");
				fos = new FileOutputStream(outputFile);
				byte[] strToBytes;
				if (!(strResult.equals(NVSMXCommonConstants.PREFIX_HEADER_WITH_REMARKS + "\n") || strResult.equals(NVSMXCommonConstants.PREFIX_HEADER + "\n"))) {
					strToBytes = strResult.getBytes();
				} else {
					strToBytes = "Every record inserted successfully".getBytes();
				}
				fos.write(strToBytes);
				fos.flush();

			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error while importing Prefix data. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
			HibernateSessionUtil.commitTransaction(transaction);

		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error while importing Prefix data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			transaction.rollback();
		} finally {
			sessionForRestApi.invalidate();
			HibernateSessionUtil.closeSession(session);
		}

		response = Response.ok((Object) outputFile);
		response.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		long endTime = System.nanoTime();
		LogManager.getLogger().debug(MODULE, "Total Execution time : " + (endTime - startTime));
		return response.build();

	}
}
