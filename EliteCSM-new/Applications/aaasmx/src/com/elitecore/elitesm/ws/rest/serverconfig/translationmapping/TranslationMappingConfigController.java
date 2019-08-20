package com.elitecore.elitesm.ws.rest.serverconfig.translationmapping;

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

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.elitesm.blmanager.servermgr.transmapconf.TranslationMappingConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstData;
import com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstDetailData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class TranslationMappingConfigController {

	private static final String TRANSLATION_MAPPING = "Translation Mapping";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
			@QueryParam(value = "name") String translationMappingName) throws DataManagerException {
		
		TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
		TranslationMappingConfData translationMappingData = (TranslationMappingConfData) translationMappingBLManager.getTranslationMappingConfDataByName(translationMappingName);
		
		getDefaultRequestResponseParameters(translationMappingData);
		
		getMappingRequestResponseParameters(translationMappingData);

		return Response.ok(translationMappingData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String translationMappingName) throws DataManagerException {
			return getByNameFromQuery(translationMappingName);
	}
	
	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<TranslationMappingConfData> translationMappingDataList, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
		List<TranslationMappingConfData> list = translationMappingDataList.getList();
		List<TranslationMappingConfData> translationMappingList = new  ArrayList<TranslationMappingConfData>();

		for (TranslationMappingConfData translationMappingData : list) {
			
			setDefaultRequestResponseParameters(translationMappingData);
			setMappingRequestResponseParameters(translationMappingData);
		
			translationMappingList.add(translationMappingData);
		}

		Map<String, List<Status>> responseMap = translationMappingBLManager.create(translationMappingList, staffData, URLInfo.isPartialSuccess(uri));

		return Response.ok(RestUtitlity.getResponse(TRANSLATION_MAPPING, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid TranslationMappingConfData translationMappingData) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();

		setDefaultRequestResponseParameters(translationMappingData);
		setMappingRequestResponseParameters(translationMappingData);

		translationMappingBLManager.create(translationMappingData, staffData);

		return Response.ok(RestUtitlity.getResponse("Translation Mapping created successfully")).build();
	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String translationMappingName, @Valid TranslationMappingConfData translationMappingData) 
		throws DataManagerException {
		
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();
		
		setDefaultRequestResponseParameters(translationMappingData);
		setMappingRequestResponseParameters(translationMappingData);
	
		translationMappingBLManager.updateByName(translationMappingData, staffData, translationMappingName);

		return Response.ok(RestUtitlity.getResponse("Translation mapping updated successfully")).build();
	}
	
	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String translationMappingName, 
		@Valid TranslationMappingConfData translationMappingData) throws DataManagerException {
		return updateByQueryParam(translationMappingName, translationMappingData);
	}
	
	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
		@QueryParam(value = "name") String translationMappingName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		TranslationMappingConfBLManager translationMappingBLManager = new TranslationMappingConfBLManager();

		List<String> translationMappingNameList = Arrays.asList(translationMappingName.split(","));
		translationMappingBLManager.deleteByName(translationMappingNameList, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Translation Mapping(s) deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String translationMappingName) throws DataManagerException {
		return deleteByQueryParam(translationMappingName);
	}

	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.TRANSLATION_MAPPING);
	}
	
	private void setDefaultRequestResponseParameters(TranslationMappingConfData translationMappingData) {
		DefaultMappingParameter defaultMappingParameter = translationMappingData.getDefaultMapping();
		if (defaultMappingParameter != null) {
		List<TranslationMappingInstDetailData> defaultRequestParameter = defaultMappingParameter.getDefaultRequestParameter();
		for (TranslationMappingInstDetailData requestParameter: defaultRequestParameter) {
			requestParameter.setMappingTypeId(TranslationMappingConfigConstants.REQUEST_PARAMETERS);
			requestParameter.setDefaultMapping(TranslationMappingConfigConstants.YES);
			translationMappingData.getDefaultTranslationMappingDetailDataList().add(requestParameter);
		}

		List<TranslationMappingInstDetailData> defaultResponseParameter = defaultMappingParameter.getDefaultResponseParameter();
		for (TranslationMappingInstDetailData responseParameter: defaultResponseParameter) {
			responseParameter.setMappingTypeId(TranslationMappingConfigConstants.RESPONSE_PARAMETERS);
			responseParameter.setDefaultMapping(TranslationMappingConfigConstants.YES);
			translationMappingData.getDefaultTranslationMappingDetailDataList().add(responseParameter);
		}
	}
	}
	
	private void setMappingRequestResponseParameters(TranslationMappingConfData translationMappingData) {
		List<TranslationMappingInstData> mappingList = translationMappingData.getTranslationMappingInstDataList();

		if (Collectionz.isNullOrEmpty(mappingList) == false) {
			for (TranslationMappingInstData mapping : mappingList) {
				List<TranslationMappingInstDetailData> requestParameterList = mapping.getRequestParameter();
				if (Collectionz.isNullOrEmpty(requestParameterList) == false) {
					for (TranslationMappingInstDetailData request : requestParameterList) {
						request.setMappingTypeId(TranslationMappingConfigConstants.REQUEST_PARAMETERS);
						mapping.getTranslationMappingInstDetailDataList().add(request);
					}
				} else {
					requestParameterList = null;
				}

				List<TranslationMappingInstDetailData> responseParameterList = mapping.getResponseParameter();
				if (Collectionz.isNullOrEmpty(responseParameterList) == false) {
					for (TranslationMappingInstDetailData response : responseParameterList) {
						response.setMappingTypeId(TranslationMappingConfigConstants.RESPONSE_PARAMETERS);
						mapping.getTranslationMappingInstDetailDataList().add(response);
					}
				} else {
					responseParameterList = null;
				}

			}
		}
	}
	
	private void getDefaultRequestResponseParameters(TranslationMappingConfData translationMappingData) {
		List<TranslationMappingInstDetailData> defaultTranslationMappingList = translationMappingData.getDefaultTranslationMappingDetailDataList();

		DefaultMappingParameter defaultMapping =  new DefaultMappingParameter();
		if (Collectionz.isNullOrEmpty(defaultTranslationMappingList) == false) {
			for (TranslationMappingInstDetailData data : defaultTranslationMappingList) {
				if (TranslationMappingConfigConstants.REQUEST_PARAMETERS.equals(data.getMappingTypeId())) {

					defaultMapping.getDefaultRequestParameter().add(data);
				} else {
					defaultMapping.getDefaultResponseParameter().add(data);
				}

			}
			translationMappingData.setDefaultMapping(defaultMapping);
		}
	}
	
	private void getMappingRequestResponseParameters(TranslationMappingConfData translationMappingData) {
		List<TranslationMappingInstData> mappingList = translationMappingData.getTranslationMappingInstDataList();
		for (TranslationMappingInstData mapping : mappingList) {
			List<TranslationMappingInstDetailData> requestOrResponse = mapping.getTranslationMappingInstDetailDataList();
			if (Collectionz.isNullOrEmpty(requestOrResponse) == false) {
				for (TranslationMappingInstDetailData data : requestOrResponse) {
					if (TranslationMappingConfigConstants.REQUEST_PARAMETERS.equals(data.getMappingTypeId())) {
						mapping.getRequestParameter().add(data);
					} else if (TranslationMappingConfigConstants.RESPONSE_PARAMETERS.equals(data.getMappingTypeId())) {
						mapping.getResponseParameter().add(data);
					}
				}
			} else  {
				mapping.setRequestParameter(null);
				mapping.setResponseParameter(null);
			}
		}
	}
	
}
