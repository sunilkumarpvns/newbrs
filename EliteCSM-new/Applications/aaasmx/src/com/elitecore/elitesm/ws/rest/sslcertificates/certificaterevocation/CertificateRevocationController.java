package com.elitecore.elitesm.ws.rest.sslcertificates.certificaterevocation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509CRLEntry;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.servermgr.certificate.CrlCertificateBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.CrlCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.X509CRLEntryData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.servermgr.certificate.ServerAllCertificateAction;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;

public class CertificateRevocationController {
	
	private static final String CERTIFICATE_REVOCATION = "Certificate Revocation";

	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String certificateRevocationName) throws DataManagerException {

		CrlCertificateBLManager certificateRevocationBLManager = new CrlCertificateBLManager();
		CrlCertificateData certificateRevocation = certificateRevocationBLManager.getCertificateRevocationByName(certificateRevocationName);
		
		ServerAllCertificateAction  serverAllCertificateAction = new ServerAllCertificateAction();
		
		try {
			if (certificateRevocation.getCrlCertificate() != null) {
				serverAllCertificateAction.setCrlCertificateDetail(certificateRevocation);
				if (Collectionz.isNullOrEmpty(certificateRevocation.getRevokedList()) == false) {
					Set<X509CRLEntryData> revokedDataList = new LinkedHashSet<X509CRLEntryData>();
					Set<X509CRLEntry> revokedList = certificateRevocation.getRevokedList();
					for (X509CRLEntry revoked : revokedList) {
						X509CRLEntryData revokedData = new X509CRLEntryData();
						SimpleDateFormat simpleDate=new SimpleDateFormat("dd MMM yyyy kk:mm:ss ");
						revokedData.setSerialNumber(revoked.getSerialNumber());
						revokedData.setRevocationDate(simpleDate.format(revoked.getRevocationDate()));
						revokedDataList.add(revokedData);
					}
					certificateRevocation.setRevokedDataList(revokedDataList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Invalid Certificate Revocation for " + certificateRevocationName, ResultCode.INTERNAL_ERROR)).build();
		}
		
		return Response.ok(certificateRevocation).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String certificateRevocationName) throws DataManagerException {
		return getByNameFromQuery(certificateRevocationName);
	}

	@GET
	@Path("/download")
	public Response getDownloadCertificateRevocationByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String certificateRevocationName) throws DataManagerException {

		CrlCertificateBLManager certificateRevocationBLManager = new CrlCertificateBLManager();
		CrlCertificateData certificateRevocation = certificateRevocationBLManager.getCertificateRevocationByName(certificateRevocationName);
		
		if (certificateRevocation.getCrlCertificate() == null) {
			return Response.ok(RestUtitlity.getResponse("Certificate Revocation for " + certificateRevocationName + " is not Found", ResultCode.NOT_FOUND)).build();
		}
		
		try {
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byteArrayOutputStream.write(certificateRevocation.getCrlCertificate());
			
			ResponseBuilder responseBuilder = Response.ok((Object) byteArrayOutputStream.toString());
			responseBuilder.header("Content-Disposition", "attachment; filename=\""+certificateRevocation.getCertificateFileName()+"\"");
			
			return responseBuilder.build();
			
		} catch (IOException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Unable to Download Certificate Revocation for " + certificateRevocationName, ResultCode.INTERNAL_ERROR)).build();
		}
		
	}

	@GET
	@Path("/download/{name}")
	public Response getDownloadCertificateRevocationByNameFromPath(@PathParam(value = "name") String certificateRevocationName) throws DataManagerException {
		return getDownloadCertificateRevocationByNameFromQuery(certificateRevocationName);
	}
	
	@POST
	public Response createCertificateRevocation(@Valid CrlCertificateData certificateRevocation) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		CrlCertificateBLManager certificateRevocationBLManager = new CrlCertificateBLManager();
		
		certificateRevocation.setCreatedByStaffId(staffData.getStaffId());
		certificateRevocation.setModifiedDate(new Timestamp(new Date().getTime()));
		certificateRevocation.setModifiedByStaffId(staffData.getStaffId());
		
		certificateRevocationBLManager.createCertificateRevocation(certificateRevocation, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Certificate Revocation created successfully")).build();

	}
	
	@POST
	@Path("/bulk")
	public Response createCertificateRevocations(@Valid ListWrapper<CrlCertificateData> certificateRevocationDatas,@Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		CrlCertificateBLManager certificateRevocationBLManager = new CrlCertificateBLManager();
		
		List<CrlCertificateData> certificateRevocations = certificateRevocationDatas.getList();
		
		for (CrlCertificateData certificateRevocation : certificateRevocations) {
			
			certificateRevocation.setCreatedByStaffId(staffData.getStaffId());
			certificateRevocation.setModifiedDate(new Timestamp(new Date().getTime()));
			certificateRevocation.setModifiedByStaffId(staffData.getStaffId());
			
		}
		
		Map<String, List<Status>> responseMap = certificateRevocationBLManager.createCertificateRevocation(certificateRevocations, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(CERTIFICATE_REVOCATION ,"(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	@Path("/duplicate")
	public Response duplicateCertificateRevocationByNameFromQuery(@Valid CrlCertificateData certificateRevocationData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String certificateRevocationName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		CrlCertificateBLManager certificateRevocationBLManager = new CrlCertificateBLManager();
		
		CrlCertificateData certificateRevocation = certificateRevocationBLManager.getCertificateRevocationByName(certificateRevocationName);
		certificateRevocation.setCrlCertificateName(certificateRevocationData.getCrlCertificateName());
		certificateRevocation.setCrlCertificateId(null);
		
		certificateRevocationBLManager.createCertificateRevocation(certificateRevocation, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Certificate Revocation duplicated successfully")).build();

	}
	
	@POST
	@Path("/duplicate/{name}")
	public Response duplicateCertificateRevocationDuplicateByNameFromPath(@Valid CrlCertificateData certificateRevocationData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value = "name") String certificateRevocationName) throws DataManagerException {
		return duplicateCertificateRevocationByNameFromQuery(certificateRevocationData, certificateRevocationName);
	}

	@PUT
	@Path("/upload")
	@Consumes("multipart/form-data")
	public Response updateUploadCertificateRevocationByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String certificateRevocationName, List<Attachment> attachments) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		CrlCertificateBLManager certificateRevocationBLManager = new CrlCertificateBLManager();
		
		CrlCertificateData certificateRevocation = new CrlCertificateData();
	
		certificateRevocation.setModifiedDate(new Timestamp(new Date().getTime()));
		certificateRevocation.setModifiedByStaffId(staffData.getStaffId());
		
		String fileName = null;
		byte[] byteArray = null;
		
		for (Attachment attachment : attachments) {
			
			DataHandler dataHandler = attachment.getDataHandler();
		
			MultivaluedMap<String, String> multivaluedMap = attachment.getHeaders();
			
			if (multivaluedMap.getFirst("Content-Disposition") == null) {
				return Response.ok(RestUtitlity.getResponse("Certificate Revocation to Upload for " + certificateRevocationName + " is not attached", ResultCode.INPUT_PARAMETER_MISSING)).build();
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
				return Response.ok(RestUtitlity.getResponse("Certificate Revocation to Upload for " + certificateRevocationName + " is not attached", ResultCode.INPUT_PARAMETER_MISSING)).build();
			}
			
			certificateRevocation.setCertificateFileName(fileName);
			
			try {
				InputStream inputStream = dataHandler.getInputStream();
				byteArray = IOUtils.toByteArray(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Unable to Upload Certificate Revocation for " + certificateRevocationName, ResultCode.INTERNAL_ERROR)).build();
			}
		
			certificateRevocation.setCrlCertificate(byteArray);
			
		}

		ServerAllCertificateAction  serverAllCertificateAction = new ServerAllCertificateAction();
		
		try {
			serverAllCertificateAction.generateCrlCertificate(new ByteArrayInputStream(certificateRevocation.getCrlCertificate()));
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Invalid Certificate Revocation", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		certificateRevocationBLManager.updateCertificateRevocationByName(certificateRevocation, staffData, certificateRevocationName);

		return Response.ok(RestUtitlity.getResponse("Certificate Revocation uploaded successfully")).build();

	}
	
	@PUT
	@Path("/upload/{name}")
	@Consumes("multipart/form-data")
	public Response updateUploadCertificateRevocationByPathParam(@PathParam(value = "name") String certificateRevocationName, List<Attachment> attachments) throws DataManagerException {
		return updateUploadCertificateRevocationByQueryParam(certificateRevocationName, attachments);
	}
	
	@DELETE
	public Response deleteCertificateRevocationByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String certificateRevocationDataNames) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		CrlCertificateBLManager certificateRevocationBLManager = new CrlCertificateBLManager();

		List<String> certificateRevocationNames = Arrays.asList(certificateRevocationDataNames.split(","));

		certificateRevocationBLManager.deleteCertificateRevocationByName(certificateRevocationNames, staffData);

		return Response.ok(RestUtitlity.getResponse("Certificate Revocation(s) deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteCertificateRevocationByPathParam(@PathParam(value = "name") String certificateRevocationNames) throws DataManagerException {
		return deleteCertificateRevocationByQueryParam(certificateRevocationNames);
	}
	
	@GET
	@Path("/help")
	public Response getCertificateRevocationHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.CERTIFICATE_REVOCATION);
	}

}
