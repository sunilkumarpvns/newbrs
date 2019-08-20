package com.elitecore.elitesm.ws.rest.script;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
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
import com.elitecore.elitesm.blmanager.servermgr.script.ScriptBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class ScriptController {

	private static final String SCRIPT_S = "Script(s) ";

	@GET
	public Response getScriptDataByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name) throws DataManagerException, IOException {
		return Response.ok(getScriptInstData(name)).build();
	}

	@GET
	@Path("/{name}")
	public Response getScriptDataByPathParam(@PathParam(value="name") String name) throws DataManagerException, IOException {
		return getScriptDataByQueryParam(name);
	}
	
	@POST
	public Response create(@Valid ScriptInstanceData scriptInstanceData) throws DataManagerException {
		IStaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ScriptBLManager blManager = new ScriptBLManager();
		
		scriptInstanceData.setCreatedByStaffId(staffData.getStaffId());
		scriptInstanceData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		
		blManager.createScriptInstance(scriptInstanceData, staffData);
		return Response.ok(RestUtitlity.getResponse("Script created successfully")).build();
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<ScriptInstanceData> scriptInstanceDataList, @Context UriInfo uri) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ScriptBLManager blManager = new ScriptBLManager();
		
		for (ScriptInstanceData scriptInstanceData : scriptInstanceDataList.getList()) {
			scriptInstanceData.setCreatedByStaffId(staffData.getStaffId());
			scriptInstanceData.setCreateDate(new Timestamp(System.currentTimeMillis()));
		}
		
		Map<String, List<Status>> responseMap = blManager.createScriptInstance(scriptInstanceDataList.getList(), staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(SCRIPT_S, "created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	public Response uploadScriptFileByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name,List<Attachment> attachments) throws DataManagerException, IOException, ParseException {
		uploadScriptFile(name, attachments);
		return Response.ok(RestUtitlity.getResponse("Script File uploaded successfully")).build();
	}
	
	@POST
	@Path("/upload/{name}")
	@Consumes("multipart/form-data")
	public Response uploadScriptFileByPathParam(@PathParam(value="name") String name, List<Attachment> attachments) throws DataManagerException, IOException, ParseException {
		uploadScriptFile(name, attachments);
		return Response.ok(RestUtitlity.getResponse("Script File uploaded successfully")).build();
	}
	
	@GET
	@Path("/download")
	@Consumes("text/plain")
	public Response downloadScriptFileByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name, 
			@NotEmpty(message = RestValidationMessages.FILE_NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("filename") String filename) throws DataManagerException, IOException {
		return downloadScriptFile(name,filename);
	}

	@GET
	@Path("/download/{name}/{filename}")
	public Response downloadScriptFileByPathParam(@PathParam(value="name") String name,@PathParam("filename") String filename) throws DataManagerException, IOException {
		return downloadScriptFile(name,filename);
	}

	@PUT
	public Response updateScriptInstanceByQueryParam(
			@Valid ScriptInstanceData scriptInstanceData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name)
					throws DataManagerException, JAXBException {
		updateScriptInstanceData(scriptInstanceData, name);
		return Response.ok(RestUtitlity.getResponse("Script updated successfully")).build();
	}
	
	@PUT
	@Path("{name}")
	public Response updateScriptByPathParam(@Valid ScriptInstanceData scriptInstanceData,
			@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value="name") String name)
					throws DataManagerException, JAXBException {
		return updateScriptInstanceByQueryParam(scriptInstanceData, name);
	}
	
	@DELETE
	public Response deleteScriptByQueryParam( @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam("name") String name) throws DataManagerException {
		deleteScript(name);
		return Response.ok(RestUtitlity.getResponse("Script deleted successfully")).build();
	}
	
	private void deleteScript(String name) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ScriptBLManager scriptBLManager = new ScriptBLManager();
		scriptBLManager.deleteScriptByName(Arrays.asList(name.split(",")),staffData);
	}

	@DELETE
	@Path("/{name}")
	public Response deleteScriptByPathParam(@PathParam(value="name") String name) throws DataManagerException {
		return deleteScriptByQueryParam(name);
	}
	
	private void updateScriptInstanceData(ScriptInstanceData scriptInstanceData, String name) throws DataManagerException {
		IStaffData staffData = (IStaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ScriptBLManager scriptBLManager = new ScriptBLManager();
		scriptInstanceData.setLastModifiedDate(new Timestamp(new Date().getTime()));
		scriptInstanceData.setLastModifiedByStaffId(staffData.getUsername());
		
		scriptBLManager.updateScriptBasicDetails(scriptInstanceData, staffData, name);
	}
	
	private Response downloadScriptFile(String name, String filename) throws DataManagerException, IOException{
		ResponseBuilder responseBuilder = null;
		ScriptInstanceData scriptInstanceData = getScriptInstData(name);
		Set<String> files = new HashSet<String>();

		List<ScriptData> scriptDataList = scriptInstanceData.getScriptDataList();
		for (ScriptData scriptData : scriptDataList) {
			files.add(scriptData.getScriptFileName());
		}
		
		for (ScriptData scriptData : scriptDataList) {
			if(files.contains(filename)){
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				byteArrayOutputStream.write(scriptData.getScriptFile());
				responseBuilder = Response.ok((Object) byteArrayOutputStream.toString());
				responseBuilder.header("Content-Disposition", "attachment; filename=\""+filename+"\"");
				break;
			} else {
				return Response.ok(
						RestUtitlity.getResponse("Script file name must be valid", ResultCode.INVALID_INPUT_PARAMETER)).build();
			}
		}
		return responseBuilder.build();
	}
	
	private void uploadScriptFile(String name, List<Attachment> attachments) throws DataManagerException, IOException, ParseException{
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ScriptBLManager scriptBLManager = new ScriptBLManager();

		ScriptInstanceData scriptInstanceData = getScriptInstData(name);

		List<ScriptData> oldScriptDataList = scriptInstanceData.getScriptDataList();
		List<ScriptData> newScriptDataList = new ArrayList<ScriptData>();
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
			
			
			for (ScriptData scriptData : oldScriptDataList) {
				if(scriptData.getScriptFileName().equalsIgnoreCase(fileName)){
					scriptData.setScriptFile(byteArray);
					scriptData.setDate(dateobj.getTime());
					scriptData.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
					pluginFileNames.add(scriptData.getScriptFileName());
					newScriptDataList.add(scriptData);
				} else {
					pluginFileNames.add(scriptData.getScriptFileName());
					newScriptDataList.add(scriptData);
				}
			}
			
			if(pluginFileNames.contains(fileName) == false){
				ScriptData scriptFileData = new ScriptData();
				scriptFileData.setScriptFileName(fileName);
				scriptFileData.setScriptFile(byteArray);
				scriptFileData.setDate(dateobj.getTime());
				scriptFileData.setLastUpdatedTime(new Timestamp(System.currentTimeMillis()));
				newScriptDataList.add(scriptFileData);
			}
		}
		
		scriptInstanceData.setScriptDataList(newScriptDataList);
		scriptBLManager.updateScriptByName(scriptInstanceData, staffData,name);
	}
	
	private ScriptInstanceData getScriptInstData(String name) throws DataManagerException {
		ScriptBLManager scriptBLManager = new ScriptBLManager();
		
		ScriptInstanceData scriptInstanceData = scriptBLManager.getScriptInstanceByName(name);
		
		if( Collectionz.isNullOrEmpty(scriptInstanceData.getScriptDataList()) == false){
			List<String> fileNames = new ArrayList<String>();
			for (ScriptData fileName: scriptInstanceData.getScriptDataList()) {
				fileNames.add(fileName.getScriptFileName());
			}
			scriptInstanceData.setScriptFiles(fileNames);
		}
		return scriptInstanceData;
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
	
	@GET
	@Path("/help")
	public Response getScriptHelp() throws IOException {
		return RestUtitlity.getHelp(RestHelpConstant.SCRIPT);
	}
	
}
