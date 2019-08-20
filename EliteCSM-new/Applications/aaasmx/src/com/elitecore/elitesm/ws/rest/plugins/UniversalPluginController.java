package com.elitecore.elitesm.ws.rest.plugins;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.PacketType;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.ParameterUsage;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.PluginAction;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.UniversalPluginData;
import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.plugins.CreatePluginConfig;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.AAAUniversalPluginPolicyDetail;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.RadiusParamDetails;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.UniversalPluginConfigurationImpl;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class UniversalPluginController {

	private static final String UNIVERSAL_ACCT_PLUGINS = "Universal Acct Plugins";
	private static final String UNIVERSAL_AUTH_PLUGINS = "Universal Auth Plugins";
	private static final String PACKET_TYPE_ID = "id";
	private static final String PLUGIN_ACTION_ID = "id";
	private static final String PARAMETER_USAGE_NAME = "name";
	private static final String PARAMETER_USAGE = "parameterUsage";
	private static final String ID = "id";
	private static final String NAME = "name";		

	// Universal Auth Plugin
	@GET
	@Path("/auth")
	public Response getAuthPluginData(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		return Response.ok(getPluginData(name)).build();
	}

	@GET
	@Path("/auth/{name}")
	public Response getAuthPluginDataByPathParam(@PathParam(value="name") String name) throws DataManagerException {
		return getAuthPluginData(name);
	}
	
	@POST
	@Path("/auth")
	public Response createAuthUniversalPlugin(@Valid UniversalPluginConfigurationImpl policyData) throws DataManagerException {
		List<UniversalPluginConfigurationImpl> uniPluginDataList = new ArrayList<UniversalPluginConfigurationImpl>();
		uniPluginDataList.add(policyData);
		createUniversalPlugin(uniPluginDataList,PluginTypesConstants.UNIVERSAL_AUTH_PLUGIN,null);
		return Response.ok(RestUtitlity.getResponse("Universal Auth Plugin created successfully")).build();
	}
	
	@POST
	@Path("/auth/bulk")
	public Response createAuthUniversalPlugin(@Valid ListWrapper<UniversalPluginConfigurationImpl> policyData, @Context UriInfo uri) throws DataManagerException {
		Map<String, List<Status>> responseMap = createUniversalPlugin(policyData.getList(),PluginTypesConstants.UNIVERSAL_AUTH_PLUGIN, uri);
		
		return Response.ok(RestUtitlity.getResponse(UNIVERSAL_AUTH_PLUGINS, " created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	@Path("/auth")
	public Response updateUniversalAuthPlugin(
			@Valid UniversalPluginConfigurationImpl pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException, JAXBException {
		updateUniversalPlugin(pluginData , name ,PluginTypesConstants.UNIVERSAL_AUTH_PLUGIN);
		return Response.ok(RestUtitlity.getResponse("Universal Auth Plugin updated successfully")).build();
	}

	@PUT
	@Path("/auth/{name}")
	public Response updateUniversalAuthPluginByPathParam(@Valid UniversalPluginConfigurationImpl pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException, JAXBException {
		return updateUniversalAuthPlugin(pluginData, name);
	}
	
	@DELETE
	@Path("/auth")
	public Response deleteUniversalAuthPlugin(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		deleteUniversalPlugin(name);
		return Response.ok(RestUtitlity.getResponse("Universal Auth Plugins deleted successfully")).build();
	}

	@DELETE
	@Path("/auth/{name}")
	public Response deleteUniversalAuthPluginByPathParam(@PathParam(value="name") String name) throws DataManagerException {
		return deleteUniversalAuthPlugin(name);
	}
	
	@GET
	@Path("/acct/help")
	public Response getUniversalAuthPluginHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.UNIVERSAL_AUTH_PLUGIN);
	}
	
	
	// Universal Acct Plugin
	@GET
	@Path("/acct")
	public Response getAcctPluginData(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		return Response.ok(getPluginData(name)).build();
	}

	@GET
	@Path("/acct/{name}")
	public Response getAcctPluginDataByPathParam(@PathParam(value="name") String name) throws DataManagerException {
		return getAcctPluginData(name);
	}
	
	@POST
	@Path("/acct")
	public Response createAcctUniversalPlugin(@Valid UniversalPluginConfigurationImpl policyData) throws DataManagerException {
		List<UniversalPluginConfigurationImpl> uniPluginDataList = new ArrayList<UniversalPluginConfigurationImpl>();
		uniPluginDataList.add(policyData);
		createUniversalPlugin(uniPluginDataList,PluginTypesConstants.UNIVERSAL_ACCT_PLUGIN,null);
		return Response.ok(RestUtitlity.getResponse("Universal Acct Plugin created successfully")).build();
	}
	
	@POST
	@Path("/acct/bulk")
	public Response createAcctUniversalPlugin(@Valid ListWrapper<UniversalPluginConfigurationImpl> policyData, @Context UriInfo uri) throws DataManagerException {
		Map<String, List<Status>> responseMap = createUniversalPlugin(policyData.getList(),PluginTypesConstants.UNIVERSAL_ACCT_PLUGIN,uri);
		
		return Response.ok(RestUtitlity.getResponse(UNIVERSAL_ACCT_PLUGINS, " created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@PUT
	@Path("/acct")
	public Response updateUniversalAcctPlugin(
			@Valid UniversalPluginConfigurationImpl pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException, JAXBException {
		updateUniversalPlugin(pluginData,name,PluginTypesConstants.UNIVERSAL_ACCT_PLUGIN);
		return Response.ok(RestUtitlity.getResponse("Universal Acct Plugin updated successfully")).build();
	}
	
	@PUT
	@Path("/acct/{name}")
	public Response updateUniversalAcctPluginByPathParam(@Valid UniversalPluginConfigurationImpl pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value="name") String name)
					throws DataManagerException, JAXBException {
		return updateUniversalAcctPlugin(pluginData, name);
	}
	
	@DELETE
	@Path("/acct")
	public Response deleteUniversalAcctPlugin(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		deleteUniversalPlugin(name);
		return Response.ok(RestUtitlity.getResponse("Universal Acct Plugins deleted successfully")).build();
	}

	@DELETE
	@Path("/acct/{name}")
	public Response deleteUniversalAcctPluginByPathParam(@PathParam(value="name") String name) throws DataManagerException {
		return deleteUniversalAcctPlugin(name);
	}
	
	@GET
	@Path("/acct/help")
	public Response getUniversalAcctPluginHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.UNIVERSAL_ACCT_PLUGIN);
	}
	
	//common method for crud operation
	private UniversalPluginConfigurationImpl getPluginData(String name) throws DataManagerException{
		PluginBLManager pluginBLManager = new PluginBLManager();

		UniversalPluginData pluginData = pluginBLManager.getUniversalAuthPluginDataByPluginInstanceName(name);

		PluginInstData pluginInstData = pluginBLManager.getPluginInstanceData(pluginData.getPluginInstanceId());

		UniversalPluginConfigurationImpl policyData;
		try {
			policyData = (UniversalPluginConfigurationImpl)ConfigUtil.deserialize(new String(pluginData.getPluginData()),UniversalPluginConfigurationImpl.class);
			policyData.setStatus(pluginInstData.getStatus());
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		}
		
		doConversionOfDataAndXml(policyData.getPrePolicyLists(),ID,PARAMETER_USAGE_NAME);
		doConversionOfDataAndXml(policyData.getPostPolicyLists(),ID,PARAMETER_USAGE_NAME);
		return policyData;
	}
	
	private Map<String, List<Status>> createUniversalPlugin(List<UniversalPluginConfigurationImpl> uniPluginDataList, String pluginType, UriInfo uri) throws DataManagerException{
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		PluginBLManager pluginBLManager = new PluginBLManager();
		List<CreatePluginConfig> createPluginConfigs = new ArrayList<CreatePluginConfig>();
		for (UniversalPluginConfigurationImpl universalPluginData : uniPluginDataList) {
			doConversionOfDataAndXml(universalPluginData.getPrePolicyLists(),NAME,PARAMETER_USAGE);
			doConversionOfDataAndXml(universalPluginData.getPostPolicyLists(),NAME,PARAMETER_USAGE);
			UniversalPluginData data = convertToUniversalPluginDataBeanFrom(universalPluginData);

			PluginInstData pluginInstData = new PluginInstData();
			pluginInstData.setName(universalPluginData.getName()); 
			pluginInstData.setDescription(universalPluginData.getDescription());
			pluginInstData.setStatus(universalPluginData.getStatus());
			pluginInstData.setPluginTypeId(pluginType);

			CreatePluginConfig pluginConfig = new CreatePluginConfig();
			pluginConfig.setPluginInstData(pluginInstData);
			pluginConfig.setUniversalPluginData(data);
			createPluginConfigs.add(pluginConfig);
		}
		return pluginBLManager.createUniversalPlugin(createPluginConfigs,staffData,URLInfo.isPartialSuccess(uri));
	}
	
	private void updateUniversalPlugin(UniversalPluginConfigurationImpl pluginData, String name, String pluginType) throws DataManagerException, JAXBException{
		IStaffData staffData = (IStaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		PluginBLManager pluginBLManager = new PluginBLManager();
		UniversalPluginData universalPluginData = pluginBLManager.getUniversalAuthPluginDataByPluginInstanceName(name);

		UniversalPluginConfigurationImpl oldPluginData;
		oldPluginData = (UniversalPluginConfigurationImpl)ConfigUtil.deserialize(new String(universalPluginData.getPluginData()),UniversalPluginConfigurationImpl.class);

		doConversionOfDataAndXml(pluginData.getPrePolicyLists(),NAME,PARAMETER_USAGE);
		doConversionOfDataAndXml(pluginData.getPostPolicyLists(),NAME,PARAMETER_USAGE);

		String pluginXml = ConfigUtil.serialize(UniversalPluginConfigurationImpl.class, pluginData);
		universalPluginData.setPluginData(pluginXml.getBytes());
		
		PluginInstData pluginInstDatas = new PluginInstData();
		pluginInstDatas.setName(pluginData.getName()); 
		pluginInstDatas.setDescription(pluginData.getDescription());
		pluginInstDatas.setStatus(pluginData.getStatus());
		pluginInstDatas.setPluginTypeId(pluginType);

		pluginBLManager.updateRadiusUniversalPlugin(pluginInstDatas, universalPluginData, staffData, oldPluginData, pluginData);
	}
	
	private void deleteUniversalPlugin(String name) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		PluginBLManager pluginBLManager = new PluginBLManager();
		pluginBLManager.deletePluginByName(Arrays.asList(name.split(",")),staffData);
	}

	//Convert Xml To Byte
	private UniversalPluginData convertToUniversalPluginDataBeanFrom(UniversalPluginConfigurationImpl pluginData) throws DataManagerException {
		UniversalPluginData data = new UniversalPluginData();
		try {
			byte[] convertToXml = convertToXml(pluginData);
			data.setPluginData(convertToXml);
		} catch(JAXBException e) {
			throw new DataManagerException(e.getMessage(), e);
		}
		return data;
	}
	
	//Convert Byte to String
	private byte[] convertToXml(UniversalPluginConfigurationImpl pluginData) throws JAXBException {
		String serialize = ConfigUtil.serialize(UniversalPluginConfigurationImpl.class, pluginData);
		return serialize.getBytes();
	}

	//Convert Packet Type ID to String value
	private void doConversionOfDataAndXml(List<AAAUniversalPluginPolicyDetail> list, String idOrName, String parameterUsageVal){
		String pluginActionId = null;
		String pluginActionName = null;

		String packetTyeValue = null;
		String packetTypeName = null;

		String parameterUsageNameValue = null;
		String parameterUsageValue = null;

		if(Collectionz.isNullOrEmpty(list) == false){
			for (AAAUniversalPluginPolicyDetail aaaUniversalPluginPolicyDetail : list) {

				for (PluginAction pluginAction : PluginAction.values()) {
					if(idOrName.equalsIgnoreCase(PLUGIN_ACTION_ID)){
						pluginActionId = String.valueOf(pluginAction.id);
						pluginActionName = pluginAction.name;
					} else {
						pluginActionName	= String.valueOf(pluginAction.id);
						pluginActionId = pluginAction.name;
					}
					if(aaaUniversalPluginPolicyDetail.getAction().equals(pluginActionId)){
						aaaUniversalPluginPolicyDetail.setAction(pluginActionName);
					}
				}

				List<RadiusParamDetails> pluginInfoParams = aaaUniversalPluginPolicyDetail.getParameterDetailsForPlugin();
				for (RadiusParamDetails pluginParameterDetail : pluginInfoParams) {

					// change packet type value
					for (PacketType packetTypes : PacketType.values()) {
						if(idOrName.equalsIgnoreCase(PACKET_TYPE_ID)){
							packetTyeValue = String.valueOf(packetTypes.id);
							packetTypeName = packetTypes.name;
						} else {
							packetTypeName	= String.valueOf(packetTypes.id);
							packetTyeValue = packetTypes.name;
						}
						if(pluginParameterDetail.getPacket_type().equalsIgnoreCase(packetTyeValue)){
							pluginParameterDetail.setPacket_type(packetTypeName);
						}
					}

					// change parameter usage type value
					String paramUsageVal = null;
					for (ParameterUsage parameterUsage : ParameterUsage.values()) {
						if(parameterUsageVal.equalsIgnoreCase(PARAMETER_USAGE_NAME)){
							paramUsageVal = "name";
						} else {
							paramUsageVal = "parameterUsage";
						}
						if(paramUsageVal.equalsIgnoreCase(PARAMETER_USAGE_NAME)){
							parameterUsageNameValue = parameterUsage.name;
							parameterUsageValue = parameterUsage.parameterUsage;

						} else {
							parameterUsageValue = parameterUsage.name;
							parameterUsageNameValue = parameterUsage.parameterUsage;
						}
						if(pluginParameterDetail.getParameter_usage().equalsIgnoreCase(parameterUsageNameValue)){
							pluginParameterDetail.setParameter_usage(parameterUsageValue);
						}
					}
				}
			}
		}
	}
}
