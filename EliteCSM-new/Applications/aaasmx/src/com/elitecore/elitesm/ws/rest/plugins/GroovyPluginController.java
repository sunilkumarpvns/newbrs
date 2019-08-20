package com.elitecore.elitesm.ws.rest.plugins;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginData;
import com.elitecore.elitesm.datamanager.servermgr.plugins.groovyplugin.data.GroovyPluginFile;
import com.elitecore.elitesm.util.constants.PluginTypesConstants;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.plugins.CreatePluginConfig;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class GroovyPluginController {

	private static final String DIAMETER_GROOVY_PLUGINS = "Diameter Groovy Plugins";
	private static final String RADIUS_GROOVY_PLUGINS = "Radius Groovy Plugins";

	//Radius Groovy Plugin
	
	@GET
	@Path("/radius")
	public Response getRadiusGroovyPluignDataByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name) throws DataManagerException, IOException {
		return Response.ok(getGroovyPluginData(name)).build();
	}

	@GET
	@Path("/radius/{name}")
	public Response getGroovyPluignDataByPathParam(@PathParam(value="name") String name) throws DataManagerException, IOException {
		return getRadiusGroovyPluignDataByQueryParam(name);
	}

	@GET
	@Path("/radius/download")
	@Consumes("text/plain")
	public Response downloadRadiusGroovyPluginFileByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name
			,@NotEmpty(message = RestValidationMessages.FILE_NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("filename") String filename) throws DataManagerException, IOException {
		return downloadGroovyPluginFile(name, filename);
	}

	@GET
	@Path("/radius/download/{name}/{filename}")
	public Response downloadRadiusGroovyPluginFileByPathParam(@PathParam(value="name") String name,@PathParam("filename") String filename) throws DataManagerException, IOException {
		return downloadRadiusGroovyPluginFileByQueryParam(name,filename);
	}

	@POST
	@Path("/radius")
	public Response createRadiusGroovyPlugin(@Valid GroovyPluginData pluginData) throws DataManagerException {
		List<GroovyPluginData> groovyPluginDataList = new ArrayList<GroovyPluginData>();
		groovyPluginDataList.add(pluginData);
		createGroovyPluginData(groovyPluginDataList, PluginTypesConstants.RADIUS_GROOVY_PLUGIN,null);
		return Response.ok(RestUtitlity.getResponse("Radius Groovy Plugin Created successfully")).build();
	}
	
	@POST
	@Path("/radius/bulk")
	public Response createRadiusGroovyPlugin(@Valid ListWrapper<GroovyPluginData> pluginData, @Context UriInfo uri) throws DataManagerException {
		Map<String, List<Status>> responseMap = createGroovyPluginData(pluginData.getList(), PluginTypesConstants.RADIUS_GROOVY_PLUGIN, uri);
		
		return Response.ok(RestUtitlity.getResponse(RADIUS_GROOVY_PLUGINS, " Created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}

	@POST
	@Path("/radius/upload")
	@Consumes("multipart/form-data")
	public Response uploadRadiusGroovyPluginFileByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name,List<Attachment> attachments) throws DataManagerException, IOException, ParseException {
		uploadGroovyPluginFile(name,attachments,PluginTypesConstants.RADIUS_GROOVY_PLUGIN);
		return Response.ok(RestUtitlity.getResponse("Radius Groovy Plugin File uploaded successfully")).build();
	}

	@POST
	@Path("/radius/upload/{name}")
	@Consumes("multipart/form-data")
	public void uploadRadiusGroovyPluginFileByPathParam(@PathParam(value="name") String name,List<Attachment> attachments) throws DataManagerException, IOException, ParseException {
		uploadRadiusGroovyPluginFileByQueryParam(name,attachments);
	}
	
	@PUT
	@Path("/radius")
	public Response updateRadiusGroovyPluginByQueryParam(
			@Valid GroovyPluginData pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException, JAXBException {
		updateGroovyPlugin(pluginData , name ,PluginTypesConstants.RADIUS_GROOVY_PLUGIN);
		return Response.ok(RestUtitlity.getResponse("Radius Groovy Plugin updated successfully")).build();
	}
	
	@PUT
	@Path("/radius/{name}")
	public Response updateRadiusGroovyPluginByPathParam(@Valid GroovyPluginData pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value="name") String name)
					throws DataManagerException, JAXBException {
		return updateRadiusGroovyPluginByQueryParam(pluginData, name);
	}
	
	@DELETE
	@Path("/radius")
	public Response deleteRadiusGroovyPluginByQueryParam(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		deleteGroovyPlugin(name);
		return Response.ok(RestUtitlity.getResponse("Radius Groovy Plugins deleted successfully")).build();
	}
	
	@DELETE
	@Path("/radius/{name}")
	public Response deleteRadiusGroovyPluginByPathParam(@PathParam(value="name") String name) throws DataManagerException {
		return deleteRadiusGroovyPluginByQueryParam(name);
	}

	@GET
	@Path("/help/radius")
	public Response getRadiusGroovyPluginHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.RADIUS_GROOVY_PLUGIN);
	}
	//Diameter Groovy Plugin
	
	@GET
	@Path("/diameter")
	public Response getDiameterGroovyPluignDataByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name) throws DataManagerException, IOException {
		return Response.ok(getGroovyPluginData(name)).build();
	}

	@GET
	@Path("/diameter/{name}")
	public Response getDiameterGroovyPluignDataByPathParam(@PathParam(value="name") String name) throws DataManagerException, IOException {
		return getDiameterGroovyPluignDataByQueryParam(name);
	}
	
	@GET
	@Path("/diameter/download")
	@Consumes("text/plain")
	public Response downloadDiameterGroovyPluginFileByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name
			,@NotEmpty(message = RestValidationMessages.FILE_NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("filename") String filename) throws DataManagerException, IOException {
		return downloadGroovyPluginFile(name,filename);
	}

	@GET
	@Path("/diameter/download/{name}/{filename}")
	public Response downloadDiameterGroovyPluginFileByPathParam(@PathParam(value="name") String name,@PathParam("filename") String filename) throws DataManagerException, IOException {
		return downloadDiameterGroovyPluginFileByQueryParam(name,filename);
	}

	@POST
	@Path("/diameter")
	public Response createDiameterGroovyPlugin(@Valid GroovyPluginData pluginData) throws DataManagerException {
		List<GroovyPluginData> groovyPluginDataList = new ArrayList<GroovyPluginData>();
		groovyPluginDataList.add(pluginData);
		createGroovyPluginData(groovyPluginDataList, PluginTypesConstants.DIAMETER_GROOVY_PLUGIN,null);
		return Response.ok(RestUtitlity.getResponse("Diameter Groovy Plugin Created successfully")).build();
	}
	
	@POST
	@Path("/diameter/bulk")
	public Response createDiameterGroovyPlugin(@Valid ListWrapper<GroovyPluginData> pluginData,@Context UriInfo uri) throws DataManagerException {
		Map<String, List<Status>> responseMap = createGroovyPluginData(pluginData.getList(), PluginTypesConstants.DIAMETER_GROOVY_PLUGIN,uri);
		
		return Response.ok(RestUtitlity.getResponse(DIAMETER_GROOVY_PLUGINS, " Created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}

	@POST
	@Path("/diameter/upload")
	@Consumes("multipart/form-data")
	public Response uploadDiameterGroovyPluginFileByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name,List<Attachment> attachments) throws DataManagerException, IOException, ParseException {
		uploadGroovyPluginFile(name,attachments,PluginTypesConstants.DIAMETER_GROOVY_PLUGIN);
		return Response.ok(RestUtitlity.getResponse("Diameter Groovy Plugin File uploaded successfully")).build();
	}

	@POST
	@Path("/diameter/upload/{name}")
	@Consumes("multipart/form-data")
	public void uploadDiameterGroovyPluginFileByPathParam(@PathParam(value="name") String name,List<Attachment> attachments) throws DataManagerException, IOException, ParseException {
		uploadDiameterGroovyPluginFileByQueryParam(name,attachments);
	}
	
	@PUT
	@Path("/diameter")
	public Response updateDiameterGroovyPluginByQueryParam(
			@Valid GroovyPluginData pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException, JAXBException {
		updateGroovyPlugin(pluginData , name ,PluginTypesConstants.DIAMETER_GROOVY_PLUGIN);
		return Response.ok(RestUtitlity.getResponse("Diameter Groovy Plugin updated successfully")).build();
	}
	
	@PUT
	@Path("/diameter/{name}")
	public Response updateDiameterGroovyPluginByPathParam(@Valid GroovyPluginData pluginData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value="name") String name)
					throws DataManagerException, JAXBException {
		return updateDiameterGroovyPluginByQueryParam(pluginData, name);
	}
	
	@DELETE
	@Path("/diameter")
	public Response deleteDiameterGroovyPluginByQueryParam(
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException {
		deleteGroovyPlugin(name);
		return Response.ok(RestUtitlity.getResponse("Diameter Groovy Plugins deleted successfully")).build();
	}
	
	@DELETE
	@Path("/diameter/{name}")
	public Response deleteDiameterGroovyPluginByPathParam(@PathParam(value="name") String name) throws DataManagerException {
		return deleteDiameterGroovyPluginByQueryParam(name);
	}
	
	@GET
	@Path("/help/diameter")
	public Response getDiameterGroovyPluginHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.DIAMETER_GROOVY_PLUGIN);
	}

	private GroovyPluginData getGroovyPluginData(String name) throws DataManagerException{
		PluginBLManager pluginBLManager = new PluginBLManager();

		GroovyPluginData groovyPluginData = pluginBLManager.getGroovyPluginDataByName(name);
		PluginInstData pluginInstData = pluginBLManager.getPluginInstanceData(groovyPluginData.getPluginInstanceId());

		groovyPluginData.setPluginName(pluginInstData.getName());
		groovyPluginData.setPluginDescription(pluginInstData.getDescription());
		groovyPluginData.setPluginStatus(pluginInstData.getStatus());
		
		if(Collectionz.isNullOrEmpty(groovyPluginData.getGroovyPluginFileSet()) == false){
			List<String> fileNames = new ArrayList<String>();
			for (GroovyPluginFile  fileName: groovyPluginData.getGroovyPluginFileSet()) {
				fileNames.add(fileName.getGroovyFileName());
			}
			groovyPluginData.setGroovyFiles(fileNames);
		}
		
		return groovyPluginData;
	}

	private Response downloadGroovyPluginFile(String name, String filename) throws DataManagerException, IOException{
		ResponseBuilder responseBuilder = null;
		GroovyPluginData groovyPluginData = getGroovyPluginData(name);
		Set<String> files = new HashSet<String>();

		Set<GroovyPluginFile> groovyPluginFiles = groovyPluginData.getGroovyPluginFileSet();
		for (GroovyPluginFile groovyPluginFile : groovyPluginFiles) {
			files.add(groovyPluginFile.getGroovyFileName());
		}
		
		for (GroovyPluginFile groovyPluginFile : groovyPluginFiles) {
			if(files.contains(filename)){
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				byteArrayOutputStream.write(groovyPluginFile.getGroovyFile());
				responseBuilder = Response.ok((Object) byteArrayOutputStream.toString());
				responseBuilder.header("Content-Disposition", "attachment; filename=\""+filename+"\"");
				break;
			} else {
				return Response.ok(
						RestUtitlity.getResponse(
								"Groovy Plugin file name must be valid",
								ResultCode.INVALID_INPUT_PARAMETER)).build();
			}
		}
		return responseBuilder.build();
	}

	private Map<String, List<Status>> createGroovyPluginData(List<GroovyPluginData> groovyPluginDataList, String pluginType, UriInfo uri) throws DataManagerException{
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		PluginBLManager pluginBLManager = new PluginBLManager();
		List<CreatePluginConfig> createPluginConfigs = new ArrayList<CreatePluginConfig>();
		for (GroovyPluginData groovyPluginData : groovyPluginDataList) {
			groovyPluginData.setGroovyPluginFileSet(null);
			PluginInstData pluginInstData = new PluginInstData();
			pluginInstData.setName(groovyPluginData.getPluginName()); 
			pluginInstData.setDescription(groovyPluginData.getPluginDescription());
			pluginInstData.setStatus(groovyPluginData.getPluginStatus());
			pluginInstData.setPluginTypeId(pluginType);

			CreatePluginConfig pluginConfig = new CreatePluginConfig();
			pluginConfig.setPluginInstData(pluginInstData);
			pluginConfig.setGroovyPluginData(groovyPluginData);
			createPluginConfigs.add(pluginConfig);
		}
		return pluginBLManager.createGroovyPluginByName(createPluginConfigs,staffData,URLInfo.isPartialSuccess(uri));
	}


	private void uploadGroovyPluginFile(String name, List<Attachment> attachments, String pluginType) throws DataManagerException, IOException, ParseException{
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		PluginBLManager pluginBLManager = new PluginBLManager();
		GroovyPluginData groovyPluginData = getGroovyPluginData(name);
		PluginInstData oldPluginInstData = pluginBLManager.getPluginInstanceData(groovyPluginData.getPluginInstanceId());

		PluginInstData pluginInstData = new PluginInstData();
		pluginInstData.setName(oldPluginInstData.getName()); 
		pluginInstData.setDescription(oldPluginInstData.getDescription());
		pluginInstData.setStatus(oldPluginInstData.getStatus());
		pluginInstData.setPluginInstanceId(oldPluginInstData.getPluginInstanceId());
		pluginInstData.setPluginTypeId(pluginType);

		Set<GroovyPluginFile> oldGroovyPluginFiles = groovyPluginData.getGroovyPluginFileSet();
		Set<GroovyPluginFile> newGroovyPluginFiles = new HashSet<GroovyPluginFile>();
		Set<String> pluginFileNames = new HashSet<String>();

		String fileName = null;
		byte[] byteArray;

		for (Attachment attachment : attachments) {
			DataHandler dataHandler = attachment.getDataHandler();
			// get filename to be uploaded
			MultivaluedMap<String, String> multivaluedMap = attachment.getHeaders();
			if(multivaluedMap.getFirst("Content-Disposition") == null){
				throw new InvalidValueException("Attachment is missing");
			}
			fileName = getFileName(multivaluedMap);

			InputStream inputStream = dataHandler.getInputStream();
			byteArray = IOUtils.toByteArray(inputStream);
			Date dateobj = new Date();
			
			
			for (GroovyPluginFile oldGroovyPluginFile : oldGroovyPluginFiles) {
				if(oldGroovyPluginFile.getGroovyFileName().equalsIgnoreCase(fileName)){
					oldGroovyPluginFile.setGroovyFile(byteArray);
					oldGroovyPluginFile.setDate(dateobj);
					oldGroovyPluginFile.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
					pluginFileNames.add(oldGroovyPluginFile.getGroovyFileName());
					newGroovyPluginFiles.add(oldGroovyPluginFile);
				} else {
					pluginFileNames.add(oldGroovyPluginFile.getGroovyFileName());
					newGroovyPluginFiles.add(oldGroovyPluginFile);
				}
			}
			
			if(pluginFileNames.contains(fileName) == false){
					GroovyPluginFile groovyPluginFile = new GroovyPluginFile();
					groovyPluginFile.setDate(staffData.getLastModifiedDate());
					groovyPluginFile.setGroovyFileName(fileName);
					groovyPluginFile.setGroovyFile(byteArray);
					groovyPluginFile.setDate(dateobj);
					groovyPluginFile.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
					newGroovyPluginFiles.add(groovyPluginFile);
			}
		}
		
		groovyPluginData.setGroovyPluginFileSet(newGroovyPluginFiles);
		pluginBLManager.updateGroovyPluginByName(pluginInstData, groovyPluginData, staffData,name);
	}
	
	private void updateGroovyPlugin(GroovyPluginData pluginData, String name,String pluginType) throws DataManagerException {
		IStaffData staffData = (IStaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		PluginBLManager pluginBLManager = new PluginBLManager();
		
		Set<GroovyPluginData> groovyPluginDataSet = new LinkedHashSet<GroovyPluginData>();
		pluginData.setGroovyPluginFileSet(null);
		
		groovyPluginDataSet.add(pluginData);
		PluginInstData pluginInstDatas = new PluginInstData();
		pluginInstDatas.setName(pluginData.getPluginName()); 
		pluginInstDatas.setDescription(pluginData.getPluginDescription());
		pluginInstDatas.setStatus(pluginData.getPluginStatus());
		pluginInstDatas.setPluginTypeId(pluginType);
		pluginInstDatas.setGroovyPluginDataSet(groovyPluginDataSet);
		pluginBLManager.updateGroovyPluginByName(pluginInstDatas,pluginData,staffData,name);
	}
	
	private void deleteGroovyPlugin(String name) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		PluginBLManager pluginBLManager = new PluginBLManager();
		pluginBLManager.deletePluginByName(Arrays.asList(name.split(",")),staffData);
	}

	private String getFileName(MultivaluedMap<String, String> multivaluedMap) {

		String[] contentDisposition = multivaluedMap.getFirst("Content-Disposition").split(";");
		for (String filename : contentDisposition) {

			if ((filename.trim().startsWith("filename"))) {
				String[] name = filename.split("=");
				String exactFileName = name[1].trim().replaceAll("\"", "");
				return exactFileName;
			}
		}
		return "unknownFile";
	}
}
