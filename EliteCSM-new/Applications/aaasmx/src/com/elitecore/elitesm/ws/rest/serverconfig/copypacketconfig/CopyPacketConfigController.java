package com.elitecore.elitesm.ws.rest.serverconfig.copypacketconfig;

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
import com.elitecore.elitesm.blmanager.servermgr.copypacket.CopyPacketTransMapConfBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapData;
import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationMapDetailData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

@Path("/")
public class CopyPacketConfigController {

	private static final String COPY_PACKET_CONFIGURATION = "Copy Packet configuration";
	private static final String TRUE = "true";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String copyPacketConfigName) throws DataManagerException {

		CopyPacketTransMapConfBLManager copyPacketBLManager =  new CopyPacketTransMapConfBLManager();
		CopyPacketTranslationConfData copyPacketConfigData = copyPacketBLManager.getCopyPacketTransMapConfigDetailDataByName(copyPacketConfigName);
		getMappingRequestResponseParameters(copyPacketConfigData);
		return Response.ok(copyPacketConfigData).build();
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String copyPacketConfigName) throws DataManagerException {
		return getByNameFromQuery(copyPacketConfigName);
	}

	@POST
	@Path("/bulk")
	public Response create(@Valid ListWrapper<CopyPacketTranslationConfData> copyPacketConfigDataList, @Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CopyPacketTransMapConfBLManager copyPacketBLManager =  new CopyPacketTransMapConfBLManager();
		List<CopyPacketTranslationConfData> list = copyPacketConfigDataList.getList();
		List<CopyPacketTranslationConfData> copyPacketDataList = new  ArrayList<CopyPacketTranslationConfData>();

		for (CopyPacketTranslationConfData copyPacketData : list) {
			setMappingRequestResponseParameters(copyPacketData);
			setDefaultRequestResponseParameters(copyPacketData);
			copyPacketDataList.add(copyPacketData);
		}
		Map<String, List<Status>> responseMap = copyPacketBLManager.create(copyPacketDataList, staffData, URLInfo.isPartialSuccess(uri));

		return Response.ok(RestUtitlity.getResponse(COPY_PACKET_CONFIGURATION, "(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	public Response create(@Valid CopyPacketTranslationConfData copyPacketConfigData) throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CopyPacketTransMapConfBLManager copyPacketBLManager =  new CopyPacketTransMapConfBLManager();

		setMappingRequestResponseParameters(copyPacketConfigData);

		setDefaultRequestResponseParameters(copyPacketConfigData);
		copyPacketBLManager.create(copyPacketConfigData, staffData);

		return Response.ok(RestUtitlity.getResponse("Copy Packet configuration created successfully")).build();
	}

	@PUT
	public Response updateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String translationMappingName, @Valid CopyPacketTranslationConfData copyPacketData) 
			throws DataManagerException {

		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CopyPacketTransMapConfBLManager copyPacketBLManager =  new CopyPacketTransMapConfBLManager();

		setDefaultRequestResponseParameters(copyPacketData);
		setMappingRequestResponseParameters(copyPacketData);

		copyPacketBLManager.updateByName(copyPacketData, staffData, translationMappingName);

		return Response.ok(RestUtitlity.getResponse("Copy Packet configuration updated successfully")).build();
	}

	@PUT
	@Path("/{name}")
	public Response updateByPathParam(@PathParam(value = "name") String translationMappingName, @Valid CopyPacketTranslationConfData translationMappingData) throws DataManagerException {
		return updateByQueryParam(translationMappingName, translationMappingData);
	}

	@DELETE
	public Response	deleteByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) 
	@QueryParam(value = "name") String copyPacketConfigName) throws DataManagerException {
		StaffData staffData = (StaffData)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		CopyPacketTransMapConfBLManager copyPacketBLManager =  new CopyPacketTransMapConfBLManager();

		List<String> copyPacketConfigNameList = Arrays.asList(copyPacketConfigName.split(","));
		copyPacketBLManager.deleteByName(copyPacketConfigNameList, staffData);

		return Response.ok(RestUtitlity.getResponse("Copy Packet configuration(s) deleted successfully")).build();
	}

	@DELETE
	@Path("/{name}")
	public Response	deleteByPathParam(@PathParam(value = "name") String copyPacketConfigName) throws DataManagerException {
		return deleteByQueryParam(copyPacketConfigName);
	}

	@GET
	@Path("/help")
	public Response getHelp() throws FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.COPYPACKET_MAPPING);
	}


	private void getMappingRequestResponseParameters(CopyPacketTranslationConfData copyPacketData) {
		List<CopyPacketTranslationMapData>  mappingSet = copyPacketData.getCopyPacketTransMapData();
		List<CopyPacketTranslationMapDetailData> defaultParameters = new ArrayList<CopyPacketTranslationMapDetailData>();
		if (Collectionz.isNullOrEmpty(mappingSet) == false) {
			for (CopyPacketTranslationMapData mapping : mappingSet) {
				if (TRUE.equals(mapping.getIsDefaultMapping())) {
					if (Collectionz.isNullOrEmpty(defaultParameters)) {
						defaultParameters = mapping.getCopyPacketTransMapDetail();
						getDefaultRequestResponseParameters(defaultParameters, copyPacketData);
						mapping.setCopyPacketTransMapDetail(null);
						mapping.setRequestParameter(null);
						mapping.setResponseParameter(null);
					} else {
						mapping.setCopyPacketTransMapDetail(null);
						mapping.setRequestParameter(null);
						mapping.setResponseParameter(null);
					}
				} else {
					List<CopyPacketTranslationMapDetailData> requestOrResponse = mapping.getCopyPacketTransMapDetail();
					if (Collectionz.isNullOrEmpty(requestOrResponse) == false) {
						for (CopyPacketTranslationMapDetailData data : requestOrResponse) {
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
	}

	private void getDefaultRequestResponseParameters(List<CopyPacketTranslationMapDetailData> defaultParameters, CopyPacketTranslationConfData copyPacketData) {

		DefaultMappingParameter defaultMapping =  new DefaultMappingParameter();
		if (Collectionz.isNullOrEmpty(defaultParameters) == false) {
			for (CopyPacketTranslationMapDetailData data : defaultParameters) {
				if (TranslationMappingConfigConstants.REQUEST_PARAMETERS.equals(data.getMappingTypeId())) {
					defaultMapping.getDefaultRequestParameter().add(data);
				} else {
					defaultMapping.getDefaultResponseParameter().add(data);
				}

			}
			copyPacketData.setDefaultMapping(defaultMapping);
		}
	}

	private void setMappingRequestResponseParameters(CopyPacketTranslationConfData copyPacketConfigData) {
		List<CopyPacketTranslationMapData> mappingList = copyPacketConfigData.getCopyPacketTransMapData();

		if (Collectionz.isNullOrEmpty(mappingList) == false) {
			for (CopyPacketTranslationMapData mapping : mappingList) {
				List<CopyPacketTranslationMapDetailData> requestParameterList = mapping.getRequestParameter();
				if (Collectionz.isNullOrEmpty(requestParameterList) == false) {
					long orderNumber = 1;
					for (CopyPacketTranslationMapDetailData request : requestParameterList) {
						request.setMappingTypeId(TranslationMappingConfigConstants.REQUEST_PARAMETERS);
						request.setOrderNumber(orderNumber);
						mapping.getCopyPacketTransMapDetail().add(request);
						orderNumber++;
					}
				} else {
					mapping.setRequestParameter(null);
				}

				List<CopyPacketTranslationMapDetailData> responseParameterList = mapping.getResponseParameter();
				if (Collectionz.isNullOrEmpty(responseParameterList) == false) {
					long orderNumber = 1;
					for (CopyPacketTranslationMapDetailData response : responseParameterList) {
						response.setMappingTypeId(TranslationMappingConfigConstants.RESPONSE_PARAMETERS);
						response.setOrderNumber(orderNumber);
						mapping.getCopyPacketTransMapDetail().add(response);
						orderNumber++;
					}
				} else {
					mapping.setResponseParameter(null);
				}

			}
		}
	}

	private void setDefaultRequestResponseParameters(CopyPacketTranslationConfData copyPacketConfigData) {
		DefaultMappingParameter defaultMappingParameter = copyPacketConfigData.getDefaultMapping();
		List<CopyPacketTranslationMapData> mappingList = copyPacketConfigData.getCopyPacketTransMapData();

		if (defaultMappingParameter != null) {
			List<CopyPacketTranslationMapDetailData> defaultRequestParameter = defaultMappingParameter.getDefaultRequestParameter();
			if (Collectionz.isNullOrEmpty(mappingList) == false) {
				for (CopyPacketTranslationMapData copyPacketData : mappingList) {
					long orderNumberForReq = 1;
					long orderNumberForRes = 1;
					if (TRUE.equals(copyPacketData.getIsDefaultMapping())) {
						List<CopyPacketTranslationMapDetailData> defaultValue = new ArrayList<CopyPacketTranslationMapDetailData>();
						int NoOfRequestParam = defaultRequestParameter.size();
						for (int i=0; i< NoOfRequestParam; i++) {
							CopyPacketTranslationMapDetailData requestParameter = new CopyPacketTranslationMapDetailData();
							requestParameter.setCheckExpression(defaultRequestParameter.get(i).getCheckExpression());
							requestParameter.setDefaultValue(defaultRequestParameter.get(i).getDefaultValue());
							requestParameter.setDestinationExpression(defaultRequestParameter.get(i).getDestinationExpression());
							requestParameter.setOperation(defaultRequestParameter.get(i).getOperation());
							requestParameter.setSourceExpression(defaultRequestParameter.get(i).getSourceExpression());
							requestParameter.setValueMapping(defaultRequestParameter.get(i).getValueMapping());
							requestParameter.setMappingTypeId(TranslationMappingConfigConstants.REQUEST_PARAMETERS);
							requestParameter.setOrderNumber(orderNumberForReq);
							orderNumberForReq++;
							defaultValue.add(requestParameter);
						}

						List<CopyPacketTranslationMapDetailData> defaultResponseParameter = defaultMappingParameter.getDefaultResponseParameter();
						int noOfResponseParam = defaultResponseParameter.size();
						for (int i=0; i< noOfResponseParam; i++) {
							CopyPacketTranslationMapDetailData responseParameter = new CopyPacketTranslationMapDetailData();
							responseParameter.setCheckExpression(defaultResponseParameter.get(i).getCheckExpression());
							responseParameter.setDefaultValue(defaultResponseParameter.get(i).getDefaultValue());
							responseParameter.setDestinationExpression(defaultResponseParameter.get(i).getDestinationExpression());
							responseParameter.setOperation(defaultResponseParameter.get(i).getOperation());
							responseParameter.setSourceExpression(defaultResponseParameter.get(i).getSourceExpression());
							responseParameter.setValueMapping(defaultResponseParameter.get(i).getValueMapping());
							responseParameter.setMappingTypeId(TranslationMappingConfigConstants.RESPONSE_PARAMETERS);
							responseParameter.setOrderNumber(orderNumberForRes);
							orderNumberForRes++;
							defaultValue.add(responseParameter);
						}
						copyPacketData.setCopyPacketTransMapDetail(defaultValue);
					}
				}
			}
		}
	}
}
