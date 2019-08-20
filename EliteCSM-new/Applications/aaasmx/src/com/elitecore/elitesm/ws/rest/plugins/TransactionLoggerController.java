package com.elitecore.elitesm.ws.rest.plugins;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data.TransactionLoggerData;
import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.plugins.CreatePluginConfig;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

@Path("/")
public class TransactionLoggerController {

	private static final String DIAMETER = "Diameter";
	private static final String RADIUS = "Radius";
	private static final Pattern NAME_PATTERN = Pattern.compile(RestValidationMessages.NAME_REGEX);

	//Radius Transaction Logger
	@GET
	@Path("/radius")
	public Response getRadiusTransactionLoggerPluginData(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		return Response.ok(getPluginData(name)).build();
	}
	
	@GET
	@Path("/radius/{name}")
	public Response getRadiusTransactionLoggerPluginDataByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return getRadiusTransactionLoggerPluginData(name);
	}
	
	@POST
	@Path("/radius")
	public Response createRadiusTransactionLoggerPlugin(@Valid TransactionLoggerData pluginData) throws DataManagerException {
		List<TransactionLoggerData> transactionLoggerDataList = new ArrayList<TransactionLoggerData>();
		transactionLoggerDataList.add(pluginData);
		ResponseCode responseCode = createTransactionLoggerPlugin(transactionLoggerDataList,PluginTypesConstants.RADIUS_TRANSACTION_LOGGER,RADIUS);
		if(responseCode != null){
			return Response.ok(RestUtitlity.getResponse(responseCode.getMassage(),responseCode.getResultCode())).build();
		} 
		return Response.ok(RestUtitlity.getResponse("Radius Transaction Logger Plugin created successfully")).build();
	}
	
	@PUT
	@Path("/radius")
	public Response updateRadiusTransactionLoggerPlugin(
			@Valid TransactionLoggerData pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		ResponseCode responseCode = updateTransactionLoggerPlugin(pluginData , name ,PluginTypesConstants.RADIUS_TRANSACTION_LOGGER);
		if(responseCode != null){
			return Response.ok(RestUtitlity.getResponse(responseCode.getMassage(),responseCode.getResultCode())).build();
		} 
		return Response.ok(RestUtitlity.getResponse("Radius Transaction Logger Plugin updated successfully")).build();
	}
	
	@PUT
	@Path("/radius/{name}")
	public Response updateRadiusTransactionLoggerPluginByPathParam(@Valid TransactionLoggerData pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value = "name") String name)
					throws DataManagerException {
		return updateRadiusTransactionLoggerPlugin(pluginData, name);
	}
	
	@DELETE
	@Path("/radius")
	public Response deleteRadiusTransactionLogger(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		deleteTransactionLoggerPlugin(name);
		return Response.ok(RestUtitlity.getResponse("Radius Transaction Logger Plugins deleted successfully")).build();
	}
	
	@DELETE
	@Path("/radius/{name}")
	public Response deleteRadiusTransactionLoggerPluginByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return deleteRadiusTransactionLogger(name);
	}
	
	@GET
	@Path("/help/radius")
	public Response getRadiusTransactionLoggerHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RADIUS_TRANSACTION_LOGGER);
	}
	
	//Diameter Transaction Logger
	@GET
	@Path("/diameter")
	public Response getDiameterTransactionLoggerPluginData(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		return Response.ok(getPluginData(name)).build();
	}
	
	@GET
	@Path("/diameter/{name}")
	public Response getDiameterTransactionLoggerPluginDataByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return getDiameterTransactionLoggerPluginData(name);
	}
	
	@POST
	@Path("/diameter")
	public Response createDiameterTransactionLoggerPlugin(@Valid TransactionLoggerData pluginData) throws DataManagerException {
		List<TransactionLoggerData> transactionLoggerDataList = new ArrayList<TransactionLoggerData>();
		transactionLoggerDataList.add(pluginData);
		ResponseCode responseCode = createTransactionLoggerPlugin(transactionLoggerDataList,PluginTypesConstants.DIAMETER_TRANSACTION_LOGGER,DIAMETER);
		if(responseCode != null){
			return Response.ok(RestUtitlity.getResponse(responseCode.getMassage(),responseCode.getResultCode())).build();
		} 
		return Response.ok(RestUtitlity.getResponse("Diameter Transaction Logger Plugin created successfully")).build();
	}
	
	@PUT
	@Path("/diameter")
	public Response updateDiameterTransactionLoggerPlugin(
			@Valid TransactionLoggerData pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		updateTransactionLoggerPlugin(pluginData , name ,PluginTypesConstants.DIAMETER_TRANSACTION_LOGGER);
		return Response.ok(RestUtitlity.getResponse("Diameter Transaction Logger Plugin updated successfully")).build();
	}
	
	@PUT
	@Path("/diameter/{name}")
	public Response updateDiameterTransactionLoggerPluginByPathParam(@Valid TransactionLoggerData pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value = "name") String name)
					throws DataManagerException {
		return updateDiameterTransactionLoggerPlugin(pluginData, name);
	}
	
	@DELETE
	@Path("/diameter")
	public Response deleteDiameterTransactionLogger(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		deleteTransactionLoggerPlugin(name);
		return Response.ok(RestUtitlity.getResponse("Diameter Transaction Logger Plugins deleted successfully")).build();
	}
	
	@DELETE
	@Path("/diameter/{name}")
	public Response deleteDiameterTransactionLoggerPluginByPathParam(@PathParam(value = "name") String name) throws DataManagerException {
		return deleteDiameterTransactionLogger(name);
	}
	
	@GET
	@Path("/help/diameter")
	public Response getDiameterTransactionLoggerHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_TRANSACTION_LOGGER);
	}

	private TransactionLoggerData getPluginData(String name) throws DataManagerException {
		TransactionLoggerData transactionLoggerData = null;
		PluginBLManager pluginBLManager = new PluginBLManager();
		transactionLoggerData = pluginBLManager.getTransactionLoggerPluginDataByName(name);
		
		PluginInstData pluginInstData = pluginBLManager.getPluginInstanceData(transactionLoggerData.getPluginInstanceId());
		
		transactionLoggerData.setPluginName(pluginInstData.getName());
		transactionLoggerData.setPluginDescription(pluginInstData.getDescription());
		transactionLoggerData.setPluginStatus(pluginInstData.getStatus());
		
		return transactionLoggerData;
	}
	
	private ResponseCode createTransactionLoggerPlugin(List<TransactionLoggerData> transactionLoggerDataList, String pluginType, String type) throws DataManagerException {
		ResponseCode responseCode = new ResponseCode();
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		List<CreatePluginConfig> createPluginConfigs = new ArrayList<CreatePluginConfig>();
		
		PluginBLManager pluginBLManager = new PluginBLManager();
		for (TransactionLoggerData transactionLoggerData : transactionLoggerDataList) {
			
			if(Strings.isNullOrEmpty(transactionLoggerData.getPluginName())){
				responseCode.setMassage("Transaction Logger Plugin Name must be specified");
				responseCode.setResultCode(ResultCode.INPUT_PARAMETER_MISSING);
				return responseCode;
			}
			
			if(NAME_PATTERN.matcher(transactionLoggerData.getPluginName()).matches() == false){
				responseCode.setMassage("Transaction Logger Plugin Name must be valid");
				responseCode.setResultCode(ResultCode.INVALID_INPUT_PARAMETER);
				return responseCode;
			}
			
			if(Strings.isNullOrEmpty(transactionLoggerData.getPluginStatus())){
				responseCode.setMassage("Transaction Logger Plugin Status parameter must be specified");
				responseCode.setResultCode(ResultCode.INPUT_PARAMETER_MISSING);
				return responseCode;
			} else if((transactionLoggerData.getPluginStatus().equalsIgnoreCase("CST01") == false) && (transactionLoggerData.getPluginStatus().equalsIgnoreCase("CST02") == false)) {
				responseCode.setMassage("Invalid value of Status parameter. Value could be 'ACTIVE' or 'INACTIVE'");
				responseCode.setResultCode(ResultCode.INVALID_INPUT_PARAMETER);
				return responseCode;
			}
			
			List<PluginInstData> radiusAuthPlugin = pluginBLManager.getAuthPluginList();
			
			if(Collectionz.isNullOrEmpty(radiusAuthPlugin) == false){
				for (PluginInstData pluginInstData : radiusAuthPlugin) {
					if(pluginInstData.getPluginTypeId().equals(pluginType)){
						throw new DataManagerException("System allows you to create only one Radius Transaction Logger.");
					}
				}
			}
			
			List<PluginInstData> radiusAcctPlugin = pluginBLManager.getAcctPluginList();
			if(Collectionz.isNullOrEmpty(radiusAcctPlugin) == false){
				for (PluginInstData pluginInstData : radiusAcctPlugin) {
					if(pluginInstData.getPluginTypeId().equals(pluginType)){
						throw new DataManagerException("System allows you to create only one Radius Transaction Logger.");
					}
				}
			}
			
			List<PluginInstData> diameterPlugin = pluginBLManager.getDiameterPluginList();
			if(Collectionz.isNullOrEmpty(diameterPlugin) == false){
				for (PluginInstData pluginInstData : diameterPlugin) {
					if(pluginInstData.getPluginTypeId().equals(pluginType)){
						throw new DataManagerException("System allows you to create only one Diameter Transaction Logger.");
					}
				}
			}
			
			PluginInstData pluginInstData = new PluginInstData();
			
			pluginInstData.setName(transactionLoggerData.getPluginName()); 
			pluginInstData.setDescription(transactionLoggerData.getPluginDescription());
			pluginInstData.setStatus(transactionLoggerData.getPluginStatus());
			pluginInstData.setPluginTypeId(pluginType);

			CreatePluginConfig pluginConfig = new CreatePluginConfig();
			pluginConfig.setPluginInstData(pluginInstData);
			pluginConfig.setTransactionLoggerData(transactionLoggerData);
			createPluginConfigs.add(pluginConfig);
		}
		pluginBLManager.createTransactionLogger(createPluginConfigs,staffData);
		return null;
	}
	
	private ResponseCode updateTransactionLoggerPlugin(TransactionLoggerData pluginData, String name, String pluginType) throws DataManagerException {
		
		ResponseCode responseCode = new ResponseCode();
		
		IStaffData staffData = (IStaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		PluginBLManager pluginBLManager = new PluginBLManager();
		
		Set<TransactionLoggerData> transactionLoggerDataSet = new LinkedHashSet<TransactionLoggerData>();
		
		if(Strings.isNullOrEmpty(pluginData.getPluginName())){
			responseCode.setMassage("Transaction Logger Plugin Name must be specified");
			responseCode.setResultCode(ResultCode.INPUT_PARAMETER_MISSING);
			return responseCode;
		}
		
		if(NAME_PATTERN.matcher(pluginData.getPluginName()).matches() == false){
			responseCode.setMassage("Transaction Logger Plugin Name must be valid");
			responseCode.setResultCode(ResultCode.INVALID_INPUT_PARAMETER);
			return responseCode;
		}
		
		if(Strings.isNullOrEmpty(pluginData.getPluginStatus())){
			responseCode.setMassage("Transaction Logger Plugin Status parameter must be specified");
			responseCode.setResultCode(ResultCode.INPUT_PARAMETER_MISSING);
			return responseCode;
		} else if((pluginData.getPluginStatus().equalsIgnoreCase("CST01") == false) && (pluginData.getPluginStatus().equalsIgnoreCase("CST02") == false)) {
			responseCode.setMassage("Invalid value of Status parameter. Value could be 'ACTIVE' or 'INACTIVE'");
			responseCode.setResultCode(ResultCode.INVALID_INPUT_PARAMETER);
			return responseCode;
		}
		
		transactionLoggerDataSet.add(pluginData);
		PluginInstData pluginInstDatas = new PluginInstData();
		pluginInstDatas.setName(pluginData.getPluginName()); 
		pluginInstDatas.setDescription(pluginData.getPluginDescription());
		pluginInstDatas.setStatus(pluginData.getPluginStatus());
		pluginInstDatas.setPluginTypeId(pluginType);
		pluginInstDatas.setTransactionLoggerDataSet(transactionLoggerDataSet);
		pluginBLManager.updateTransactionLoggerByName(pluginInstDatas,pluginData,staffData,name);
		return null;
	}
	
	private void deleteTransactionLoggerPlugin(String name) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		PluginBLManager pluginBLManager = new PluginBLManager();
		pluginBLManager.deletePluginByName(Arrays.asList(name.split(",")),staffData);
	}
}
