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
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.DiameterUniversalPluginAction;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.DiameterUniversalPluginPacketType;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.ParameterUsage;
import com.elitecore.elitesm.datamanager.servermgr.plugins.universalplugin.data.UniversalPluginData;
import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.plugins.CreatePluginConfig;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin.DiameterParamDetail;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin.DiameterUniversalPluginDetails;
import com.elitecore.elitesm.web.servicepolicy.plugin.data.universalplugin.diameteruniversalplugin.DiameterUniversalPluginPolicyDetail;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class DiameterUniversalPluginController {

	private static final String UNIVERSAL_DIAMETER_PLUGINS = "Universal Diameter Plugins";
	private static final String PACKET_TYPE_ID = "id";
	private static final String PLUGIN_ACTION_ID = "id";
	private static final String PARAMETER_USAGE_NAME = "name";
	private static final String PARAMETER_USAGE = "parameterUsage";
	private static final String ID = "id";
	private static final String NAME = "name";

	//Diameter Universal Plugin

	@GET
	public Response getDiameterUniversalPluginData(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {

		PluginBLManager pluginBLManager = new PluginBLManager();

		UniversalPluginData pluginData = pluginBLManager.getUniversalAuthPluginDataByPluginInstanceName(name);

		PluginInstData pluginInstData = pluginBLManager.getPluginInstanceData(pluginData.getPluginInstanceId());

		DiameterUniversalPluginDetails policyData;

		try {
			policyData = (DiameterUniversalPluginDetails)ConfigUtil.deserialize(new String(pluginData.getPluginData()),DiameterUniversalPluginDetails.class);
			policyData.setStatus(pluginInstData.getStatus());
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new DataManagerException(e.getMessage(), e);
		}
		if(Collectionz.isNullOrEmpty(policyData.getInPluginList()) == false){
			doConversionOfDataAndXml(policyData.getInPluginList(),ID,PARAMETER_USAGE_NAME);
		} else {
			policyData.setInPluginList(null);
		}
		
		if(Collectionz.isNullOrEmpty(policyData.getOutPluginList()) == false){
			doConversionOfDataAndXml(policyData.getOutPluginList(),ID,PARAMETER_USAGE_NAME);
		}else {
			policyData.setOutPluginList(null);
		}

		return Response.ok(policyData).build();
	}

	@GET
	@Path("/diameter/{name}")
	public Response getDiameterUniversalDataByPathParam(@PathParam(value="name") String name) throws DataManagerException {
		return getDiameterUniversalPluginData(name);
	}

	@POST
	public Response createDiameterUniversalPlugin(@Valid DiameterUniversalPluginDetails policyData) throws DataManagerException {
		List<DiameterUniversalPluginDetails> diameterUniPluginDataList = new ArrayList<DiameterUniversalPluginDetails>();
		diameterUniPluginDataList.add(policyData);

		createDiaUniPlugin(diameterUniPluginDataList,null);

		return Response.ok(RestUtitlity.getResponse("Universal Diameter Plugin created successfully")).build();
	}

	@POST
	@Path("/bulk")
	public Response createDiameterUniversalPlugin(@Valid ListWrapper<DiameterUniversalPluginDetails> diameterUniPluginDataList,@Context UriInfo uri) throws DataManagerException {

		Map<String, List<Status>> responseMap = createDiaUniPlugin(diameterUniPluginDataList.getList(), uri);

		return Response.ok(RestUtitlity.getResponse(UNIVERSAL_DIAMETER_PLUGINS, " created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}

	private Map<String, List<Status>> createDiaUniPlugin(List<DiameterUniversalPluginDetails> diameterUniPluginDataList, UriInfo uri) throws DataManagerException{

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		PluginBLManager pluginBLManager = new PluginBLManager();
		List<CreatePluginConfig> createPluginConfigs = new ArrayList<CreatePluginConfig>();

		for (DiameterUniversalPluginDetails diameterUniPlugin : diameterUniPluginDataList) {
			doConversionOfDataAndXml(diameterUniPlugin.getInPluginList(),NAME,PARAMETER_USAGE);
			doConversionOfDataAndXml(diameterUniPlugin.getOutPluginList(),NAME,PARAMETER_USAGE);
			UniversalPluginData data = convertToUniversalPluginDataBeanFrom(diameterUniPlugin);

			PluginInstData pluginInstData = new PluginInstData();
			pluginInstData.setName(diameterUniPlugin.getName()); 
			pluginInstData.setDescription(diameterUniPlugin.getDescription());
			pluginInstData.setStatus(diameterUniPlugin.getStatus());
			pluginInstData.setPluginTypeId(PluginTypesConstants.UNIVERSAL_DIAMETER_PLUGIN);

			CreatePluginConfig pluginConfig = new CreatePluginConfig();
			pluginConfig.setPluginInstData(pluginInstData);
			pluginConfig.setUniversalPluginData(data);
			createPluginConfigs.add(pluginConfig);
		}
		return pluginBLManager.createUniversalPlugin(createPluginConfigs,staffData,URLInfo.isPartialSuccess(uri));
	}

	@PUT
	public Response updateDiameterUniversalPlugin(
			@Valid DiameterUniversalPluginDetails pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException, JAXBException {

		IStaffData staffData = (IStaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		PluginBLManager pluginBLManager = new PluginBLManager();
		UniversalPluginData universalPluginData = pluginBLManager.getUniversalAuthPluginDataByPluginInstanceName(name);

		DiameterUniversalPluginDetails oldPluginData;
		oldPluginData = (DiameterUniversalPluginDetails)ConfigUtil.deserialize(new String(universalPluginData.getPluginData()),DiameterUniversalPluginDetails.class);

		doConversionOfDataAndXml(pluginData.getInPluginList(),NAME,PARAMETER_USAGE);
		doConversionOfDataAndXml(pluginData.getOutPluginList(),NAME,PARAMETER_USAGE);

		String pluginXml = ConfigUtil.serialize(DiameterUniversalPluginDetails.class, pluginData);
		universalPluginData.setPluginData(pluginXml.getBytes());

		PluginInstData pluginInstDatas = new PluginInstData();
		pluginInstDatas.setName(pluginData.getName()); 
		pluginInstDatas.setDescription(pluginData.getDescription());
		pluginInstDatas.setStatus(pluginData.getStatus());
		pluginInstDatas.setPluginTypeId(PluginTypesConstants.UNIVERSAL_DIAMETER_PLUGIN);

		pluginBLManager.updateDiameterUniversalPlugin(pluginInstDatas, universalPluginData, staffData, oldPluginData, pluginData);
		return Response.ok(RestUtitlity.getResponse("Universal Diameter Plugin updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateDiameterUniversalPluginByPathParam(@Valid DiameterUniversalPluginDetails pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value="name") String name)
					throws DataManagerException, JAXBException {
		return updateDiameterUniversalPlugin(pluginData, name);
	}

	@DELETE
	public Response deleteDiameterUniversalPlugin(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		PluginBLManager pluginBLManager = new PluginBLManager();
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		pluginBLManager.deletePluginByName(Arrays.asList(name.split(",")),staffData);
		return Response.ok(RestUtitlity.getResponse("Universal Diameter Plugins deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response deleteDiameterUniversalPluginByPathParam(@PathParam(value="name") String name) throws DataManagerException {
		return deleteDiameterUniversalPlugin(name);
	}

	@GET
	@Path("/diameter/help")
	public Response getDiameterUniversalPluginHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.UNIVERSAL_DIAMETER_PLUGIN);
	}

	//Convert Xml To Byte
	private UniversalPluginData convertToUniversalPluginDataBeanFrom(DiameterUniversalPluginDetails pluginData) throws DataManagerException {
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
	private byte[] convertToXml(DiameterUniversalPluginDetails pluginData) throws JAXBException {
		String serialize = ConfigUtil.serialize(DiameterUniversalPluginDetails.class, pluginData);
		return serialize.getBytes();
	}

	//Convert Packet Type ID to String value
	private void doConversionOfDataAndXml(List<DiameterUniversalPluginPolicyDetail> list, String idOrName, String parameterUsageVal){
		String pluginActionId = null;
		String pluginActionName = null;

		String packetTyeValue = null;
		String packetTypeName = null;

		String parameterUsageNameValue = null;
		String parameterUsageValue = null;

		if(Collectionz.isNullOrEmpty(list) == false){
			for (DiameterUniversalPluginPolicyDetail aaaUniversalPluginPolicyDetail : list) {

				for (DiameterUniversalPluginAction pluginAction : DiameterUniversalPluginAction.values()) {
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

				List<DiameterParamDetail> pluginInfoParams = aaaUniversalPluginPolicyDetail.getParameterDetailsForPlugin();
				for (DiameterParamDetail pluginParameterDetail : pluginInfoParams) {

					// change packet type value
					for (DiameterUniversalPluginPacketType packetTypes : DiameterUniversalPluginPacketType.values()) {
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
