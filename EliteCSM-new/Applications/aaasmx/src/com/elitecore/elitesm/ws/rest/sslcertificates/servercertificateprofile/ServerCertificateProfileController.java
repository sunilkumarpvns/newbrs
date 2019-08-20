package com.elitecore.elitesm.ws.rest.sslcertificates.servercertificateprofile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
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
import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;
import com.elitecore.elitesm.blmanager.servermgr.certificate.ServerCertificateBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateDuplicateData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.util.exception.CertificateValidationException;
import com.elitecore.elitesm.util.exception.InvalidPrivateKeyException;
import com.elitecore.elitesm.web.servermgr.certificate.ServerAllCertificateAction;
import com.elitecore.elitesm.ws.rest.constant.RestHelpConstant;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.util.ListWrapper;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;
import com.elitecore.elitesm.ws.rest.utility.URLInfo;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class ServerCertificateProfileController {
	
	private static final String SERVER_CERTIFICATE = "Server Certificate";
	
	@GET
	public Response getByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverCertificateName) throws DataManagerException {

		ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();
		ServerCertificateData serverCertificate = serverCertificateBLManager.getServerCertificateByName(serverCertificateName);
		
		ServerAllCertificateAction  serverAllCertificateAction = new ServerAllCertificateAction();
		
		try {
			if (serverCertificate.getCertificate() != null) {
				serverAllCertificateAction.setServerCertificateDetail(serverCertificate);
			}
		} catch (InvalidPrivateKeyException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Invalid Server Certificate for " + serverCertificateName, ResultCode.INTERNAL_ERROR)).build();
		}
		
		try {
			if (serverCertificate.getPrivateKey() != null) {
				serverAllCertificateAction.setPrivateKeyDetail(serverCertificate);
			}
		} catch (InvalidPrivateKeyException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Invalid Private Key for " + serverCertificateName, ResultCode.INTERNAL_ERROR)).build();
		}
		
		serverCertificate.setPrivateKeyPassword(null);
		serverCertificate.setPrivateKeyAlgorithm(null);
		
		return Response.ok(serverCertificate).build();
		
	}

	@GET
	@Path("/{name}")
	public Response getByNameFromPath(@PathParam(value = "name") String serverCertificateName) throws DataManagerException {
		return getByNameFromQuery(serverCertificateName);
	}
	
	@GET
	@Path("/download")
	public Response getDownloadServerCertificateByNameFromQuery(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverCertificateName) throws DataManagerException {

		ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();
		ServerCertificateData serverCertificate = serverCertificateBLManager.getServerCertificateByName(serverCertificateName);
		
		if (serverCertificate.getCertificate() == null) {
			return Response.ok(RestUtitlity.getResponse("Server Certificate for " + serverCertificateName + " is not Found", ResultCode.NOT_FOUND)).build();
		}
		
		try {
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byteArrayOutputStream.write(serverCertificate.getCertificate());
			
			ResponseBuilder responseBuilder = Response.ok((Object) byteArrayOutputStream.toString());
			responseBuilder.header("Content-Disposition", "attachment; filename=\""+serverCertificate.getCertificateFileName()+"\"");
			
			return responseBuilder.build();
			
		} catch (IOException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Unable to Download Server Certificate for " + serverCertificateName, ResultCode.INTERNAL_ERROR)).build();
		}
		
	}

	@GET
	@Path("/download/{name}")
	public Response getDownloadServerCertificateByNameFromPath(@PathParam(value = "name") String serverCertificateName) throws DataManagerException {
		return getDownloadServerCertificateByNameFromQuery(serverCertificateName);
	}
	
	@POST
	public Response createServerCertificate(@Valid ServerCertificateData serverCertificate) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();
		
		serverCertificate.setCreatedByStaffId(staffData.getStaffId());
		serverCertificate.setModifiedDate(new Timestamp(new Date().getTime()));
		serverCertificate.setModifiedByStaffId(staffData.getStaffId());
		
		try {
			String encryptedPassword = PasswordEncryption.getInstance().crypt(serverCertificate.getPrivateKeyPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			serverCertificate.setPrivateKeyPassword(encryptedPassword);
		} catch (NoSuchEncryptionException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Private Key Password Encryption is Failed", ResultCode.INTERNAL_ERROR)).build();
		} catch (EncryptionFailedException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Private Key Password Encryption is Failed", ResultCode.INTERNAL_ERROR)).build();
		}
		
		serverCertificateBLManager.createServerCertificate(serverCertificate, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Server Certificate created successfully")).build();

	}
	
	@POST
	@Path("/bulk")
	public Response createServerCertificates(@Valid ListWrapper<ServerCertificateData> serverCertificateDatas,@Context UriInfo uri) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();
		
		List<ServerCertificateData> serverCertificates = serverCertificateDatas.getList();
		
		for (ServerCertificateData serverCertificate : serverCertificates) {
			
			serverCertificate.setCreatedByStaffId(staffData.getStaffId());
			serverCertificate.setModifiedDate(new Timestamp(new Date().getTime()));
			serverCertificate.setModifiedByStaffId(staffData.getStaffId());
			
			try {
				String encryptedPassword = PasswordEncryption.getInstance().crypt(serverCertificate.getPrivateKeyPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
				serverCertificate.setPrivateKeyPassword(encryptedPassword);
			} catch (NoSuchEncryptionException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Private Key Password Encryption is Failed", ResultCode.INTERNAL_ERROR)).build();
			} catch (EncryptionFailedException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Private Key Password Encryption is Failed", ResultCode.INTERNAL_ERROR)).build();
			}
		}
		Map<String, List<Status>> responseMap = serverCertificateBLManager.createServerCertificate(serverCertificates, staffData, URLInfo.isPartialSuccess(uri));
		
		return Response.ok(RestUtitlity.getResponse(SERVER_CERTIFICATE,"(s) created successfully", responseMap,
				URLInfo.isPartialSuccess(uri), URLInfo.isStatusRecord(uri))).build();
	}
	
	@POST
	@Path("/duplicate")
	public Response duplicateServerCertificateByNameFromQuery(@Valid ServerCertificateDuplicateData serverCertificateDuplicateData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverCertificateName) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();
		
		ServerCertificateData serverCertificate = serverCertificateBLManager.getServerCertificateByName(serverCertificateName);
		serverCertificate.setServerCertificateName(serverCertificateDuplicateData.getServerCertificateName());
		serverCertificate.setServerCertificateId(null);
		
		serverCertificateBLManager.createServerCertificate(serverCertificate, staffData);
		
		return Response.ok(RestUtitlity.getResponse("Server Certificate duplicated successfully")).build();

	}
	
	@POST
	@Path("/duplicate/{name}")
	public Response duplicateServerCertificateDuplicateByNameFromPath(@Valid ServerCertificateDuplicateData serverCertificateDuplicateData, @NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @PathParam(value = "name") String serverCertificateName) throws DataManagerException {
		return duplicateServerCertificateByNameFromQuery(serverCertificateDuplicateData, serverCertificateName);
	}
	
	@PUT
	@Path("/upload/publiccertificate")
	@Consumes("multipart/form-data")
	public Response updateUploadServerCertificateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverCertificateName, List<Attachment> attachments) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();
		
		ServerCertificateData serverCertificateData = serverCertificateBLManager.getServerCertificateByName(serverCertificateName);
		
		if (serverCertificateData.getPrivateKey() == null) {
			return Response.ok(RestUtitlity.getResponse("Upload Private Key before Uploading Public Certificate for " + serverCertificateName, ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		ServerCertificateData serverCertificate = new ServerCertificateData();
	
		serverCertificate.setModifiedDate(new Timestamp(new Date().getTime()));
		serverCertificate.setModifiedByStaffId(staffData.getStaffId());
		
		String certificateType = "publicCertificate";
		
		String fileName = null;
		byte[] byteArray = null;
		
		for (Attachment attachment : attachments) {
			
			DataHandler dataHandler = attachment.getDataHandler();
		
			MultivaluedMap<String, String> multivaluedMap = attachment.getHeaders();
			
			if (multivaluedMap.getFirst("Content-Disposition") == null) {
				return Response.ok(RestUtitlity.getResponse("Public Certificate to Upload for " + serverCertificateName + " is not attached", ResultCode.INPUT_PARAMETER_MISSING)).build();
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
				return Response.ok(RestUtitlity.getResponse("Public Certificate to Upload for " + serverCertificateName + " is not attached", ResultCode.INPUT_PARAMETER_MISSING)).build();
			}
			
			serverCertificate.setCertificateFileName(fileName);
			
			try {
				InputStream inputStream = dataHandler.getInputStream();
				byteArray = IOUtils.toByteArray(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Unable to Upload Public Certificate for " + serverCertificateName, ResultCode.INTERNAL_ERROR)).build();
			}
		
			serverCertificate.setCertificate(byteArray);
			
		}
		
		ServerAllCertificateAction  serverAllCertificateAction = new ServerAllCertificateAction();
		
		Collection<? extends Certificate> certs = null;
		try {
			certs = serverAllCertificateAction.generateCertificate(new ByteArrayInputStream(serverCertificate.getCertificate()));
		} catch (CertificateException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Invalid Public Certificate", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		if (serverCertificateData.getPrivateKey() != null) {
			
			String decryptedPassword = null;
			try {
				decryptedPassword = PasswordEncryption.getInstance().decrypt(serverCertificateData.getPrivateKeyPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
			} catch (NoSuchEncryptionException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Private Key Password Decryption is Failed", ResultCode.INTERNAL_ERROR)).build();
			} catch (DecryptionNotSupportedException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Private Key Password Decryption is Failed", ResultCode.INTERNAL_ERROR)).build();
			} catch (DecryptionFailedException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Private Key Password Decryption is Failed", ResultCode.INTERNAL_ERROR)).build();
			}

			PrivateKey privateKey = null;
			try {
				privateKey = serverAllCertificateAction.generatePrivateKey(new ByteArrayInputStream(serverCertificateData.getPrivateKey()), PrivateKeyAlgo.fromAlgoName(serverCertificateData.getPrivateKeyAlgorithm()), decryptedPassword);
			} catch (InvalidPrivateKeyException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Invalid Private Key", ResultCode.INTERNAL_ERROR)).build();
			}
			
			Certificate publicCert=null;
			for(Certificate cert:certs){
				publicCert=cert;
				break;
			}
			
			try {
				serverAllCertificateAction.checkForPrivateKeyAndPublicKey(publicCert,privateKey,PrivateKeyAlgo.fromAlgoName(serverCertificateData.getPrivateKeyAlgorithm()));
			} catch (CertificateValidationException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Public Certificate and Private Key are mismatch", ResultCode.INVALID_INPUT_PARAMETER)).build();
			}
			
		}
		
		serverCertificateBLManager.updateServerCertificateByName(serverCertificate, staffData, serverCertificateName, certificateType);
		
		return Response.ok(RestUtitlity.getResponse("Public Certificate uploaded successfully")).build();

	}
	
	@PUT
	@Path("/upload/publiccertificate/{name}")
	@Consumes("multipart/form-data")
	public Response updateUploadServerCertificateByPathParam(@PathParam(value = "name") String serverCertificateName, List<Attachment> attachments) throws DataManagerException {
		return updateUploadServerCertificateByQueryParam(serverCertificateName, attachments);
	}

	@PUT
	@Path("/upload/privatekey")
	@Consumes("multipart/form-data")
	public Response updateUploadPrivateKeyByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverCertificateName, List<Attachment> attachments) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();
		
		ServerCertificateData serverCertificate = new ServerCertificateData();
	
		serverCertificate.setModifiedDate(new Timestamp(new Date().getTime()));
		serverCertificate.setModifiedByStaffId(staffData.getStaffId());
		
		String certificateType = "privateKey";
		
		String fileName = null;
		byte[] byteArray = null;
		
		for (Attachment attachment : attachments) {
			
			DataHandler dataHandler = attachment.getDataHandler();
		
			MultivaluedMap<String, String> multivaluedMap = attachment.getHeaders();
			
			if (multivaluedMap.getFirst("Content-Disposition") == null) {
				return Response.ok(RestUtitlity.getResponse("Private Key to Upload for " + serverCertificateName + " is not attached", ResultCode.INPUT_PARAMETER_MISSING)).build();
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
				return Response.ok(RestUtitlity.getResponse("Private Key to Upload for " + serverCertificateName + " is not attached", ResultCode.INPUT_PARAMETER_MISSING)).build();
			}
			
			serverCertificate.setPrivateKeyFileName(fileName);
			
			try {
				InputStream inputStream = dataHandler.getInputStream();
				byteArray = IOUtils.toByteArray(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Unable to Upload Private Key for " + serverCertificateName, ResultCode.INTERNAL_ERROR)).build();
			}
		
			serverCertificate.setPrivateKey(byteArray);
			
		}

		ServerAllCertificateAction  serverAllCertificateAction = new ServerAllCertificateAction();
		
		ServerCertificateData serverCertificateData = serverCertificateBLManager.getServerCertificateByName(serverCertificateName);
		
		String decryptedPassword = null;
		try {
			decryptedPassword = PasswordEncryption.getInstance().decrypt(serverCertificateData.getPrivateKeyPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT);
		} catch (NoSuchEncryptionException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Private Key Password Decryption is Failed", ResultCode.INTERNAL_ERROR)).build();
		} catch (DecryptionNotSupportedException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Private Key Password Decryption is Failed", ResultCode.INTERNAL_ERROR)).build();
		} catch (DecryptionFailedException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Private Key Password Decryption is Failed", ResultCode.INTERNAL_ERROR)).build();
		}

		PrivateKey privateKey = null;
		try {
			privateKey = serverAllCertificateAction.generatePrivateKey(new ByteArrayInputStream(serverCertificate.getPrivateKey()), PrivateKeyAlgo.fromAlgoName(serverCertificateData.getPrivateKeyAlgorithm()), decryptedPassword);
		} catch (InvalidPrivateKeyException e) {
			e.printStackTrace();
			return Response.ok(RestUtitlity.getResponse("Invalid Private Key", ResultCode.INVALID_INPUT_PARAMETER)).build();
		}
		
		if (serverCertificateData.getCertificate() != null) {
			
			Collection<? extends Certificate> certs = null;
			try {
				certs = serverAllCertificateAction.generateCertificate(new ByteArrayInputStream(serverCertificateData.getCertificate()));
			} catch (CertificateException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Invalid Public Certificate", ResultCode.INTERNAL_ERROR)).build();
			}
			
			Certificate publicCert=null;
			for(Certificate cert:certs){
				publicCert=cert;
				break;
			}
			
			try {
				serverAllCertificateAction.checkForPrivateKeyAndPublicKey(publicCert,privateKey,PrivateKeyAlgo.fromAlgoName(serverCertificateData.getPrivateKeyAlgorithm()));
			} catch (CertificateValidationException e) {
				e.printStackTrace();
				return Response.ok(RestUtitlity.getResponse("Public Certificate and Private Key are mismatch", ResultCode.INVALID_INPUT_PARAMETER)).build();
			}
			
		}

		serverCertificateBLManager.updateServerCertificateByName(serverCertificate, staffData, serverCertificateName, certificateType);

		return Response.ok(RestUtitlity.getResponse("Private Key uploaded successfully")).build();

	}
	
	@PUT
	@Path("/upload/privatekey/{name}")
	@Consumes("multipart/form-data")
	public Response updateUploadPrivateKeyByPathParam(@PathParam(value = "name") String serverCertificateName, List<Attachment> attachments) throws DataManagerException {
		return updateUploadPrivateKeyByQueryParam(serverCertificateName, attachments);
	}
	
	@DELETE
	public Response deleteServerCertificateByQueryParam(@NotEmpty(message = RestValidationMessages.NAME_NOT_FOUND_QUERY_PARAMETER) @QueryParam(value = "name") String serverCertificateDataNames) throws DataManagerException {

		StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ServerCertificateBLManager serverCertificateBLManager = new ServerCertificateBLManager();

		List<String> serverCertificateNames = Arrays.asList(serverCertificateDataNames.split(","));

		serverCertificateBLManager.deleteServerCertificateByName(serverCertificateNames, staffData);

		return Response.ok(RestUtitlity.getResponse("Server Certificate(s) deleted successfully")).build();

	}

	@DELETE
	@Path("/{name}")
	public Response deleteServerCertificateByPathParam(@PathParam(value = "name") String serverCertificateNames) throws DataManagerException {
		return deleteServerCertificateByQueryParam(serverCertificateNames);
	}
	
	@GET
	@Path("/help")
	public Response getServerCertificateProfileHelp() throws DataManagerException, FileNotFoundException, IllegalArgumentException, IOException {
		return RestUtitlity.getHelp(RestHelpConstant.SERVER_CERTIFICATE_PROFILE);
	}

}
