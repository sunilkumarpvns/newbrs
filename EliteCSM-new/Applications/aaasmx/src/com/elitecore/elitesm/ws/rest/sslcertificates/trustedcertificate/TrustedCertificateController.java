package com.elitecore.elitesm.ws.rest.sslcertificates.trustedcertificate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.certificate.TrustedCertificateBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.TrustedCertificateData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.servermgr.certificate.ServerAllCertificateAction;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class TrustedCertificateController {
	
	private static final String TRUSTED_CERTIFICATE = "Trusted Certificate";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String trustedCertificateName) throws DataManagerException {

		TrustedCertificateBLManager trustedCertificateBLManager = new TrustedCertificateBLManager();
		TrustedCertificateData trustedCertificate = trustedCertificateBLManager.getTrustedCertificateByName(trustedCertificateName);
		
		ServerAllCertificateAction  serverAllCertificateAction = new ServerAllCertificateAction();
		
		try {
			if (trustedCertificate.getTrustedCertificate() != null) {
				serverAllCertificateAction.setTrustedCertificateDetail(trustedCertificate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Invalid Trusted Certificate for " + trustedCertificateName, ResultCode.INTERNAL_ERROR)).build();
		}
		
		return Response.ok(trustedCertificate).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String trustedCertificateName) throws DataManagerException {
		return getByNameFromQuery(trustedCertificateName);
	}

	@GET
	@Path("/download")
	@Produces("text/plain")
	public Response getDownloadTrustedCertificateByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String trustedCertificateName) throws DataManagerException {

		TrustedCertificateBLManager trustedCertificateBLManager = new TrustedCertificateBLManager();
		TrustedCertificateData trustedCertificate = trustedCertificateBLManager.getTrustedCertificateByName(trustedCertificateName);
		
		if (trustedCertificate.getTrustedCertificate() == null) {
			return Response.ok(RestUtitlity.getResponse("Trusted Certificate for " + trustedCertificateName + " is not Found", ResultCode.NOT_FOUND)).build();
		}
		
		try {
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byteArrayOutputStream.write(trustedCertificate.getTrustedCertificate());
			
			ResponseBuilder responseBuilder = Response.ok((Object) byteArrayOutputStream.toString());
			responseBuilder.header("Content-Disposition", "attachment; filename=\""+trustedCertificate.getCertificateFileName()+"\"");
			
			return responseBuilder.build();
			
		} catch (IOException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Unable to Download Trusted Certificate for " + trustedCertificateName, ResultCode.INTERNAL_ERROR)).build();
		}
		
	}

	@GET
	@Path("/download/{name}")
	@Produces("text/plain")
	public Response getDownloadTrustedCertificateByNameFromPath(@PathParam(value = "name") String trustedCertificateName) throws DataManagerException {
		return getDownloadTrustedCertificateByNameFromQuery(trustedCertificateName);
	}
	
	@POST
	public Response createTrustedCertificate(@Valid TrustedCertificateData trustedCertificate) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		TrustedCertificateBLManager trustedCertificateBLManager = new TrustedCertificateBLManager();
		
		trustedCertificate.setCreatedByStaffId(staffData.getStaffId());
		trustedCertificate.setModifiedDate(new Timestamp(new Date().getTime()));
		trustedCertificate.setModifiedByStaffId(staffData.getStaffId());
		
		trustedCertificateBLManager.createTrustedCertificate(trustedCertificate, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Trusted Certificate created successfully")).build();

	}
	
	@POST
	@Path("/bulk")
	public Response createTrustedCertificates(@Valid ListWrapper<TrustedCertificateData> trustedCertificateDatas,@Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		TrustedCertificateBLManager trustedCertificateBLManager = new TrustedCertificateBLManager();
		
		List<TrustedCertificateData> trustedCertificates = trustedCertificateDatas.getList();
		
		for (TrustedCertificateData trustedCertificate : trustedCertificates) {
			
			trustedCertificate.setCreatedByStaffId(staffData.getStaffId());
			trustedCertificate.setModifiedDate(new Timestamp(new Date().getTime()));
			trustedCertificate.setModifiedByStaffId(staffData.getStaffId());
			
		}
		
		Map<String, List<Status>> responseMap = trustedCertificateBLManager.createTrustedCertificate(trustedCertificates, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(TRUSTED_CERTIFICATE,"(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	@Path("/duplicate")
	public Response duplicateTrustedCertificateByNameFromQuery(@Valid TrustedCertificateData trustedCertificateData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String trustedCertificateName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		TrustedCertificateBLManager trustedCertificateBLManager = new TrustedCertificateBLManager();
		
		TrustedCertificateData trustedCertificate = trustedCertificateBLManager.getTrustedCertificateByName(trustedCertificateName);
		trustedCertificate.setTrustedCertificateName(trustedCertificateData.getTrustedCertificateName());
		trustedCertificate.setTrustedCertificateId(null);
		
		trustedCertificateBLManager.createTrustedCertificate(trustedCertificate, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Trusted Certificate duplicated successfully")).build();

	}
	
	@POST
	@Path("/duplicate/{name}")
	public Response duplicateTrustedCertificateDuplicateByNameFromPath(@Valid TrustedCertificateData trustedCertificateData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value = "name") String trustedCertificateName) throws DataManagerException {
		return duplicateTrustedCertificateByNameFromQuery(trustedCertificateData, trustedCertificateName);
	}

	@PUT
	@Path("/upload")
	@Consumes("multipart/form-data")
	public Response updateUploadTrustedCertificateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String trustedCertificateName, List<Attachment> attachments) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		TrustedCertificateBLManager trustedCertificateBLManager = new TrustedCertificateBLManager();
		
		TrustedCertificateData trustedCertificate = new TrustedCertificateData();
	
		trustedCertificate.setModifiedDate(new Timestamp(new Date().getTime()));
		trustedCertificate.setModifiedByStaffId(staffData.getStaffId());
		
		String fileName = null;
		byte[] byteArray = null;
		
		for (Attachment attachment : attachments) {
			
			DataHandler dataHandler = attachment.getDataHandler();
		
			MultivaluedMap<String, String> multivaluedMap = attachment.getHeaders();
			
			if (multivaluedMap.getFirst("Content-Disposition") == null) {
				return Response.ok(RestUtitlity.getResponse("Trusted Certificate to Upload for " + trustedCertificateName + " is not attached", ResultCode.INPUT_PARAMETER_MISSING)).build();
			}
			
			String[] contentDisposition = multivaluedMap.getFirst("Content-Disposition").split(";");
			for (String filename : contentDisposition) {
				if ((filename.trim().startsWith("filename"))) {
					String[] name = filename.split("=");
					String exactFileName = name[1].trim().replaceAll("\"", "");
					fileName = exactFileName;
				}
			}
			
			if (Strings.isNullOrBlank(fileName) == true) {
				return Response.ok(RestUtitlity.getResponse("Trusted Certificate to Upload for " + trustedCertificateName + " is not attached", ResultCode.INPUT_PARAMETER_MISSING)).build();
			}
			
			trustedCertificate.setCertificateFileName(fileName);
			
			try {
				InputStream inputStream = dataHandler.getInputStream();
				byteArray = IOUtils.toByteArray(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Unable to Upload Trusted Certificate for " + trustedCertificateName, ResultCode.INTERNAL_ERROR)).build();
			}
		
			trustedCertificate.setTrustedCertificate(byteArray);
			
		}
		
		ServerAllCertificateAction  serverAllCertificateAction = new ServerAllCertificateAction();
		
		try {
			Collection<? extends Certificate> certs = serverAllCertificateAction.generateTrustedCertificate(new ByteArrayInputStream(trustedCertificate.getTrustedCertificate()));
		} catch (CertificateException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Invalid Trusted Certificate", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}

		trustedCertificateBLManager.updateTrustedCertificateByName(trustedCertificate, staffData, trustedCertificateName);

		return Response.ok(RestUtitlity.getResponse("Trusted Certificate uploaded successfully")).build();

	}
	
	@PUT
	@Path("/upload/{name}")
	@Consumes("multipart/form-data")
	public Response updateUploadTrustedCertificateByPathParam(@PathParam(value = "name") String trustedCertificateName, List<Attachment> attachments) throws DataManagerException {
		return updateUploadTrustedCertificateByQueryParam(trustedCertificateName, attachments);
	}
	
	@DELETE
	public Response deleteTrustedCertificateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String trustedCertificateDataNames) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		TrustedCertificateBLManager trustedCertificateBLManager = new TrustedCertificateBLManager();

		List<String> trustedCertificateNames = Arrays.asList(trustedCertificateDataNames.split(","));

		trustedCertificateBLManager.deleteTrustedCertificateByName(trustedCertificateNames, staffData);

		return Response.ok(RestUtitlity.getResponse("Trusted Certificate(s) deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteTrustedCertificateByPathParam(@PathParam(value = "name") String trustedCertificateNames) throws DataManagerException {
		return deleteTrustedCertificateByQueryParam(trustedCertificateNames);
	}
	
	@GET
	@Path("/help")
	public Response getTrustedCertificateHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.TRUSTED_CERTIFICATE);
	}
}
