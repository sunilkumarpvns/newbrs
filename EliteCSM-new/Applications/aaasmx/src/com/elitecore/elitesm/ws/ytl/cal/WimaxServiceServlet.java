package com.elitecore.elitesm.ws.ytl.cal;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.Closeables;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.exception.SubscriberProfileWebServiceException;
import com.elitecore.elitesm.ws.subscriber.SubscriberProfileWebServiceBLManager;
import com.elitecore.elitesm.ws.ytl.cal.aaa.asr.EliteDiameterWSServiceLocator;
import com.elitecore.elitesm.ws.ytl.cal.aaa.dm.EliteGenericRadiusServiceLocator;
import com.elitecore.elitesm.ws.ytl.cal.data.AAAData;
import com.elitecore.elitesm.ws.ytl.cal.data.CALAAAConfig;
import com.elitecore.elitesm.ws.ytl.cal.data.ErrorMessage;
import com.elitecore.elitesm.ws.ytl.cal.data.FANode;
import com.elitecore.elitesm.ws.ytl.cal.data.LogicalMapping;
import com.elitecore.elitesm.ws.ytl.cal.data.Profile;
import com.elitecore.elitesm.ws.ytl.cal.data.RequestData;
import com.elitecore.elitesm.ws.ytl.cal.data.Response;
import com.elitecore.elitesm.ws.ytl.cal.data.ResponseTarget;
import com.elitecore.elitesm.ws.ytl.cal.data.ResponseUser;
import com.elitecore.elitesm.ws.ytl.cal.data.Status;
import com.elitecore.elitesm.ws.ytl.cal.data.SubscriberValidationDetail;
import com.elitecore.elitesm.ws.ytl.cal.data.SuccessResult;
import com.elitecore.elitesm.ws.ytl.cal.data.TargetData;
import com.elitecore.elitesm.ws.ytl.cal.data.UserData;
import com.elitecore.elitesm.ws.ytl.cal.data.ValidationData;
import com.elitecore.elitesm.ws.ytl.cal.data.Value;
import com.elitecore.elitesm.ws.ytl.cal.data.WimaxHotlining;
import com.elitecore.passwordutil.base32.Base32CryptEncryption;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class WimaxServiceServlet extends HttpServlet {

	private static final String MODULE = "WIMAX-SERVICE-SERVLET";
	private static final long serialVersionUID = 1L;
	private static final String UTF_8 = "UTF-8";

	private static final String CREATE_OPERATION = "createUser";
	private static final String UPDATE_OPERATION = "updateUser";
	private static final String DELETE_OPERATION = "deleteUser";
	private static final String SEND_DISCONNECT_OPERATION = "sendDisconnect";

	private static final String CHAR_FOR_SUBSCRIBER_ID = "@";

	private static final int EOS = -1;
	private static final int DEFAULT_BUFFER_SIZE = 1024;

	private JAXBContext requestContext;
	private JAXBContext responseContext;


	private Map<String, EliteGenericRadiusServiceLocator> aaaIDToServiceLocatorForDM;
	private Map<String, EliteDiameterWSServiceLocator> aaaIDToServiceLocatorForASR;

	private SubscriberProfileWebServiceBLManager subscriberProfileWebServiceBLManager;
	private Map<String, String> logicalNameToColumn;
	private Map<String, String> orgToRealmMap;
	private Map<String, List<String>> orgToProfileSetMap;
	private Pattern passwordValuePatternForCreate;
	private Pattern passwordValuePatternForUpdate;

	public WimaxServiceServlet() {
		aaaIDToServiceLocatorForDM = new HashMap<String, EliteGenericRadiusServiceLocator>();
		aaaIDToServiceLocatorForASR = new HashMap<String, EliteDiameterWSServiceLocator>();
		logicalNameToColumn = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		subscriberProfileWebServiceBLManager = new SubscriberProfileWebServiceBLManager();
	}

	@Override
	public void init() throws ServletException {
		Logger.logInfo(MODULE, "Initailizing Wimax-service related parameters.");

		readYTLAAAConfiguration(); 

		readYTLSubscriberValidationData();

		try {
			requestContext = JAXBContext.newInstance(RequestData.class);
		} catch (JAXBException e) {
			throw new ServletException("Exception while creating JAXB Context for wimax-service request", e);
		}

		try {
			responseContext = JAXBContext.newInstance(Response.class);
		} catch (JAXBException e) {
			throw new ServletException("Exception while creating JAXB Context for wimax-service response.", e);
		}

		compilePattern();
		Logger.logInfo(MODULE, "Successfully initailizing Wimax-service related parameters.");
	}

	private void compilePattern() {
		passwordValuePatternForCreate = Pattern.compile("<password>[\\s]*<type>.*</type>[\\s]*<value>.*</value>[\\s]*</password>");
		passwordValuePatternForUpdate = Pattern.compile("<password>[\\s]*.*<value>.*</value>[\\s]*.*</password>");
	}

	private void readYTLSubscriberValidationData() {
		String smHome = EliteUtility.getSMHome();
		String validatorFileLocation = smHome + File.separator + "WEB-INF" + File.separator + "ytl-subscriber-validation.xml";
		try {
			SubscriberValidationDetail data = ConfigUtil.deserialize(new File(validatorFileLocation), SubscriberValidationDetail.class);
			createValidatorDataEntryInMapFrom(data.getValidationData());
		} catch (FileNotFoundException ex) {
			Logger.logError(MODULE, "Configuration file for validation of subscriber provisioning not found in classpath.");
		} catch (JAXBException ex) {
			Logger.logError(MODULE, "Error while reading validation file of subscriber provisioning, Reason: " + ex.getMessage());
		}
	}

	private void readYTLAAAConfiguration() {

		String smHome = EliteUtility.getSMHome();
		String configFileLocation = smHome + File.separator + "WEB-INF" + File.separator + "ytl-aaa-config.xml";

		try {
			CALAAAConfig config = ConfigUtil.deserialize(new File(configFileLocation), CALAAAConfig.class);
			createAAAEntryInMapFrom(config);
			createLogicalNameToColumnMapFrom(config);
		} catch (FileNotFoundException ex) {
			Logger.logError(MODULE, "Configuration file for AAA DM and ASR url configuration not found in classpath.");
		} catch (JAXBException ex) {
			Logger.logError(MODULE, "Error while reading AAA DM and ASR url configuration, Reason: " + ex.getMessage());
		}
	}

	private void createValidatorDataEntryInMapFrom(List<ValidationData> validationData) {

		Map<String, String> orgToRealmMap = new HashMap<String, String>();
		Map<String, List<String>> orgToProfileSetMap = new HashMap<String, List<String>>();

		for (ValidationData data : validationData) {
			orgToRealmMap.put(data.getOrganizationName(), data.getRealmName());
			orgToProfileSetMap.put(data.getOrganizationName(), data.getProfileSets());
			System.out.println(data);
		}

		this.orgToRealmMap = orgToRealmMap;
		this.orgToProfileSetMap = orgToProfileSetMap;
	}

	private void createLogicalNameToColumnMapFrom(CALAAAConfig config) {
		for (LogicalMapping logicalMapping : config.getLogicalMappings()) {
			logicalNameToColumn.put(logicalMapping.getLogicalName(), logicalMapping.getColumnName());
		}
	}

	private void createAAAEntryInMapFrom(CALAAAConfig config) {
		List<AAAData> listOfAAAForDM = config.getInstanceDetails().getListOfAAA();
		for (AAAData aaaDetail : listOfAAAForDM) {

			EliteGenericRadiusServiceLocator dmLocator = new EliteGenericRadiusServiceLocator();
			dmLocator.setEliteGenericRadiusWSEndpointAddress(aaaDetail.getDmUrl());
			aaaIDToServiceLocatorForDM.put(aaaDetail.getId(), dmLocator);

			EliteDiameterWSServiceLocator asrLocator = new EliteDiameterWSServiceLocator();
			asrLocator.setEliteDiameterWSEndpointAddress(aaaDetail.getAsrUrl());
			aaaIDToServiceLocatorForASR.put(aaaDetail.getId(), asrLocator);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> queryString = request.getParameterMap();
		if (queryString.isEmpty() == false) {
			String[] parameterValues = queryString.get("method");
			if (parameterValues.length > 0) {
				if("reload".equalsIgnoreCase(parameterValues[0])) {
					reloadConfigurations();
					writeResponseMessage(response, "Configuaration reloaded succesfully.");
				} else {
					Logger.logInfo(MODULE, "Invalid operation: '" + parameterValues[0] + "' recieved, " +
							"possible value(s) are 'reload/RELOAD'.");
					writeResponseMessage(response, "Invalid operation: '" + parameterValues[0] + "' recieved, " +
							"possible value(s) are 'reload/RELOAD'.");
				}
				return;
			}
		}
		doPost(request,response);
	}

	private void writeResponseMessage(HttpServletResponse response, String reloadStatusMessage) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			out.println("<!DOCTYPE html>");
			out.println("<html><head>");
			out.println("<title>Wimax-Service Reload Status</title></head>");
			out.println("<body>");
			out.println("<p>Reload Status: " + reloadStatusMessage + "  </p>");
			out.println("</body>");
			out.println("</html>");
		} finally {		
			Closeables.closeQuietly(out);
		}
	}

	private void reloadConfigurations() throws ServletException {
		Logger.logInfo(MODULE, "Reloading Wimax-service related parameters.");
		readYTLAAAConfiguration();
		readYTLSubscriberValidationData();
		Logger.logInfo(MODULE, "Wimax-service related parameters reloaded successfully.");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Stopwatch stopWatch = new Stopwatch().start();

		String requestXml = readRequestXmlFrom(request);

		String formatedXml = "";
		try {

			formatedXml = format(requestXml);

			printXmlRequest(formatedXml);

			processRequest(getInputStreamFromRequest(formatedXml), response);

		} catch (JAXBException e) {
			errorResponse(response, "COM-00005", "", "Request failed: invalid request. Insufficient Data received from client");
			Logger.logError(MODULE, "Invalid xml request received: "+ formatedXml);
		} catch (SAXException e) {
			errorResponse(response, "COM-00005", "", "Request failed: invalid request. Insufficient Data received from client");
			Logger.logError(MODULE, "Invalid xml request received: "+ requestXml + ", \n Reason: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			errorResponse(response, "COM-00005", "", "Request failed: invalid request. Insufficient Data received from client");
			Logger.logError(MODULE, "Invalid xml request received: "+ requestXml + ", \n Reason: " + e.getMessage());
		} finally {
			Logger.logInfo(MODULE,"Time taken for processing request: " + stopWatch.stop());
		}
	}

	private void printXmlRequest(String formatedXml) {

		
		String xmlWithoutPassword;
		int indexOf = formatedXml.indexOf("</password>");
		
		if (indexOf != -1) {

			int indexOfType = formatedXml.indexOf("</type>");
			
			if (indexOfType == -1) {
				String newPasswordValue = "<password><value>*****</value></password>";
				Matcher matcher = passwordValuePatternForUpdate.matcher(formatedXml);
				xmlWithoutPassword = matcher.replaceAll(newPasswordValue);
			} else {
				int firstIndexOfType = formatedXml.indexOf("<type>");
				int firstIndexOfValue = formatedXml.indexOf("<value>");
				
				String typeValue = formatedXml.substring(firstIndexOfType, firstIndexOfValue).trim();
				String newPasswordValue = "<password>"+typeValue+"<value>*****</value></password>";
				
				Matcher matcher = passwordValuePatternForCreate.matcher(formatedXml);
				xmlWithoutPassword = matcher.replaceAll(newPasswordValue);
			}
			
			Logger.logInfo(MODULE, "Receive xml request: " + xmlWithoutPassword);
		} else {
			Logger.logInfo(MODULE, "Receive xml request: " + formatedXml);
		}
	}

	private ByteArrayInputStream getInputStreamFromRequest(String xmlRequest) {
		return new ByteArrayInputStream(xmlRequest.getBytes());
	}

	private String readRequestXmlFrom(HttpServletRequest request) throws IOException {

		StringWriter writer = new StringWriter();
		InputStreamReader stream = null; 
		BufferedInputStream bReader = null;

		try {
			bReader =  new BufferedInputStream(request.getInputStream());
			stream = new InputStreamReader(bReader);

			char[] charArray = new char[DEFAULT_BUFFER_SIZE];

			int currentChar;
			while((currentChar = stream.read(charArray)) != EOS) {
				writer.write(charArray, 0, currentChar);
			}

			return writer.toString();

		} catch (IOException ioe) {
			throw new IOException("Error while reading the recieved xml from request", ioe);
		} finally {
			Closeables.closeQuietly(bReader);
			Closeables.closeQuietly(stream);
		}
	}

	private void successResponseForCreate(HttpServletResponse response, String id, String subscriberId) {
		Response userResponse = new Response();
		userResponse.setVersion("2.0");

		ResponseTarget target = new ResponseTarget();
		target.setName("UserAPI");
		target.setOperation(CREATE_OPERATION);

		SuccessResult result = new SuccessResult();
		ResponseUser user = new ResponseUser();
		user.setId(id);
		result.setRespUser(user);

		target.setResult(result);

		userResponse.setRespUser(target);
		try {
			marshal(userResponse,response);
			response.getOutputStream().flush();
		} catch (JAXBException e) {
			Logger.logError(MODULE, "Error while marshaling the response of create operation for subscriber" 
					+ subscriberId + ", Reason: " + e.getMessage());
		} catch (IOException ioe) {
			Logger.logError(MODULE, "Error while marshaling the response of create operation for subscriber" 
					+ subscriberId + ", Reason: " + ioe.getMessage());
		}
	}

	private void successResponseForUpdate(HttpServletResponse response, String subscriberId) {
		Response userResponse = new Response();
		userResponse.setVersion("2.0");

		ResponseTarget target = new ResponseTarget();
		target.setName("UserAPI");
		target.setOperation(UPDATE_OPERATION);

		SuccessResult result = new SuccessResult();

		target.setResult(result);

		userResponse.setRespUser(target);

		try {
			marshal(userResponse,response);
			response.getOutputStream().flush();
		} catch (JAXBException e) {
			Logger.logError(MODULE, "Error while marshaling the response of update operation for subscriber" 
					+ subscriberId + ", Reason: " + e.getMessage());
		} catch (IOException ioe) {
			Logger.logError(MODULE, "Error while marshaling the response of update operation for subscriber" 
					+ subscriberId + ", Reason: " + ioe.getMessage());
		}
	}

	private void successResponseForDelete(HttpServletResponse response, String subscriberId) {
		Response userResponse = new Response();
		userResponse.setVersion("2.0");

		ResponseTarget target = new ResponseTarget();
		target.setName("UserAPI");
		target.setOperation(DELETE_OPERATION);

		SuccessResult result = new SuccessResult();

		target.setResult(result);
		userResponse.setRespUser(target);

		try {
			marshal(userResponse,response);
			response.getOutputStream().flush();
		} catch (JAXBException e) {
			Logger.logError(MODULE, "Error while marshaling the response of delete operation for subscriber" 
					+ subscriberId + ", Reason: " + e.getMessage());
		} catch (IOException ioe) {
			Logger.logError(MODULE, "Error while marshaling the response of delete operation for subscriber" 
					+ subscriberId + ", Reason: " + ioe.getMessage());
		}
	}

	private void successResponseForDMandASR(HttpServletResponse response, WimaxHotlining wimaxData, List<FANode> faNodes, String operation) {
		Response userResponse = new Response();
		userResponse.setVersion("2.0");

		ResponseTarget target = new ResponseTarget();
		target.setName("UserAPI");
		target.setOperation(operation);

		SuccessResult result = new SuccessResult();

		WimaxHotlining wimaxResponse = new WimaxHotlining();
		wimaxResponse.setSendFAInfo(wimaxData.getSendFAInfo());
		wimaxResponse.setSendHAInfo(wimaxData.getSendHAInfo());

		wimaxResponse.getFaNode().addAll(faNodes);
		result.setWimax(wimaxResponse);

		target.setResult(result);

		userResponse.setRespUser(target);
		try {
			marshal(userResponse,response);
			response.getOutputStream().flush();
		} catch (JAXBException e) {
			Logger.logError(MODULE, "Error while marshaling the response for disconnect, Reason: " + e.getMessage());
		} catch (IOException ioe) {
			Logger.logError(MODULE, "Error while marshaling the response for disconnect, Reason: " + ioe.getMessage());
		}
	}

	private void errorResponse(HttpServletResponse response, String code, String operation, String messsage) {
		Response userResponse = new Response();
		userResponse.setVersion("2.0");

		ResponseTarget target = new ResponseTarget();
		target.setName("UserAPI");
		target.setOperation(operation);

		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setCode(code);
		errorMessage.setMessage(messsage);

		target.setError(errorMessage);

		userResponse.setRespUser(target);
		try {
			marshal(userResponse,response);
			response.getOutputStream().flush();
		} catch (JAXBException e) {
			Logger.logError(MODULE, "Error while marshaling the response, Reason: " + e.getMessage());
		} catch (IOException ioe) {
			Logger.logError(MODULE, "Error while marshaling the response, Reason: " + ioe.getMessage());
		}
	}

	private void processRequest(InputStream requestAsInputStream, HttpServletResponse response) throws IOException, JAXBException, SAXException {

		try {

			Stopwatch stopWatch = new Stopwatch().start();

			RequestData requestData = unmarshal(requestAsInputStream);

			Logger.logDebug(MODULE, "Time taken for unmarshalling request: " + stopWatch.stop());

			doOperation(requestData, response);

		} catch (JAXBException e) {
			throw e;
		} catch (SAXException e) {
			throw e;
		}
	}

	private void doOperation(RequestData requestData, HttpServletResponse response) {

		TargetData targetData = requestData.getTarget();
		UserData userData = targetData.getParameter().getUser();

		String operationReceivedInRequest = targetData.getOperation();

		StringBuilder operationStatusMessage = new StringBuilder();

		if (DELETE_OPERATION.equalsIgnoreCase(operationReceivedInRequest)) {
			operationStatusMessage.append("OPERATION=DELETE ");
			deleteOperation(response, userData,operationStatusMessage);
		} else if (CREATE_OPERATION.equalsIgnoreCase(operationReceivedInRequest)) {
			operationStatusMessage.append("OPERATION=CREATE ");
			createOperation(response, userData,operationStatusMessage);
		} else if(UPDATE_OPERATION.equalsIgnoreCase(operationReceivedInRequest)) {
			operationStatusMessage.append("OPERATION=UPDATE ");
			updateOperation(response, userData,operationStatusMessage);
		} else if (SEND_DISCONNECT_OPERATION.equalsIgnoreCase(operationReceivedInRequest)) {
			operationStatusMessage.append("OPERATION=SOA(SEND-DISCONNECT) ");
			disconnectOperation(response, targetData,operationStatusMessage);
		}

		Logger.logInfo(MODULE, operationStatusMessage.toString());
	}

	private void disconnectOperation(HttpServletResponse response,
			TargetData targetData,StringBuilder operationStatusMessage) {
		WimaxHotlining wimaxData = targetData.getParameter().getWimax();
		String subscriberName = wimaxData.getSubscriberName();

		try {
			List<FANode> faNodes = sendRequest(response, SEND_DISCONNECT_OPERATION, targetData);
			successResponseForDMandASR(response, wimaxData, faNodes ,SEND_DISCONNECT_OPERATION);
			operationStatusMessage.append("MESSAGE=Success SUBSCRIBER_NAME="+ subscriberName );
		} catch (DataSourceException e) {
			Logger.logDebug(MODULE, "DAE-00403- Could not find the `RADIUS` RMS session for subscriber '" + subscriberName + "', Reason: "+e.getMessage());
			errorResponse(response, "DAE-00403", SEND_DISCONNECT_OPERATION, "Could not find the `RADIUS` RMS session for subscriber '" + subscriberName + "'");
			operationStatusMessage.append("MESSAGE=Could not find the `RADIUS` RMS session for subscriber '" + subscriberName +"' ERROR_CODE=DAE-00403 SUBSCRIBER_NAME="+ subscriberName );
		} catch (RemoteException e) {
			Logger.logDebug(MODULE, "DAE-00403- Could not find the `RADIUS` RMS session for subscriber '" + subscriberName + "', Reason: "+e.getMessage());
			errorResponse(response, "DAE-00403", SEND_DISCONNECT_OPERATION, "Could not find the `RADIUS` RMS session for subscriber '" + subscriberName + "'");
			operationStatusMessage.append("MESSAGE=Could not find the `RADIUS` RMS session for subscriber '" + subscriberName +"' ERROR_CODE=DAE-00403 SUBSCRIBER_NAME="+ subscriberName );
		} catch (SQLException e) {
			Logger.logDebug(MODULE, "DAE-00403- Could not find the `RADIUS` RMS session for subscriber '" + subscriberName + "', Reason: "+e.getMessage());
			errorResponse(response, "DAE-00403", SEND_DISCONNECT_OPERATION, "Could not find the `RADIUS` RMS session for subscriber '" + subscriberName + "'");
			operationStatusMessage.append("MESSAGE=Could not find the `RADIUS` RMS session for subscriber '" + subscriberName +"' ERROR_CODE=DAE-00403 SUBSCRIBER_NAME="+ subscriberName );
		} catch (ServiceException e) {
			Logger.logDebug(MODULE, "DAE-00403- Could not find the `RADIUS` RMS session for subscriber '" + subscriberName + "', Reason: "+e.getMessage());
			errorResponse(response, "DAE-00403", SEND_DISCONNECT_OPERATION, "Could not find the `RADIUS` RMS session for subscriber '" + subscriberName + "'");
			operationStatusMessage.append("MESSAGE=Could not find the `RADIUS` RMS session for subscriber '" + subscriberName +"' ERROR_CODE=DAE-00403 SUBSCRIBER_NAME="+ subscriberName );
		}
	}

	private void updateOperation(HttpServletResponse response,
			UserData userData,StringBuilder operationStatusMessage) {

		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;

		String framedIpAddress = "N/A";
		try {
			String subscriberId = getSubscriberIDFromData(userData);
			Map<String, Object> data = prepareDataForUpdateSubscriber(userData);

			if (userData.getProfileSet() != null) {
				Profile profile = userData.getProfileSet().getProfile();
				if (profile != null && profile.getAttribute() != null) {
					Value valueTagForFramedAddress = profile.getAttribute().getValue();
					framedIpAddress = valueTagForFramedAddress.getValue();
				}
			}
			
			if (data.isEmpty()) {
				errorResponse(response, "PRF-00019", UPDATE_OPERATION, "Entity was not assigned a profile set");
				operationStatusMessage.append("MESSAGE=Entity was not assigned a profile set ERROR_CODE=PRF-00019 SUBSCRIBER_NAME="+ (userData.getName() != null ? userData.getName() : "N/A") +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getDomain().getName() +" ORGANIZATION="+ ( userData.getOrganization() != null ?userData.getOrganization().getQualifiedName(): "N/A" ) +" PROFILE-SET="+( userData.getProfileSet() != null ? userData.getProfileSet().getQualifiedName(): "N/A")  +" STATUS=" + (userData.getStatus() != null ? userData.getStatus().getValue() : "N/A") + " FRAMED-IP-ADDRESS=" + framedIpAddress);
				return;
			}

			int updateCount = subscriberProfileWebServiceBLManager.updateSubscriber(data, subscriberId);

			if (updateCount == 0) {
				errorResponse(response, "USR-00001", UPDATE_OPERATION, "User '" + subscriberId +"' not found");
				operationStatusMessage.append("MESSAGE=User '" + subscriberId +"' not found ERROR_CODE=USR-00001 SUBSCRIBER_NAME="+ (userData.getName() != null ? userData.getName() : "N/A") +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getDomain().getName() +" ORGANIZATION="+ ( userData.getOrganization() != null ?userData.getOrganization().getQualifiedName(): "N/A" ) +" PROFILE-SET="+( userData.getProfileSet() != null ? userData.getProfileSet().getQualifiedName(): "N/A")  +" STATUS=" + (userData.getStatus() != null ? userData.getStatus().getValue() : "N/A") + " FRAMED-IP-ADDRESS=" + framedIpAddress);
			} else {
				successResponseForUpdate(response, subscriberId);
				operationStatusMessage.append("MESSAGE=SUCCESS SUBSCRIBER_NAME="+ (userData.getName() != null ? userData.getName() : "N/A") +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getDomain().getName() +" ORGANIZATION="+ ( userData.getOrganization() != null ?userData.getOrganization().getQualifiedName(): "N/A" ) +" PROFILE-SET="+( userData.getProfileSet() != null ? userData.getProfileSet().getQualifiedName(): "N/A")  +" STATUS=" + (userData.getStatus() != null ? userData.getStatus().getValue() : "N/A") + " FRAMED-IP-ADDRESS=" + framedIpAddress);
			}
		} catch (SubscriberProfileWebServiceException e) {
			Logger.logInfo(MODULE, "CLU-00005- No response from server, Reason: "+e.getMessage());
			errorResponse(response, "CLU-00005", UPDATE_OPERATION, "No response from server");
			operationStatusMessage.append("MESSAGE=No response from server ERROR_CODE=CLU-00005 SUBSCRIBER_NAME="+ (userData.getName() != null ? userData.getName() : "N/A") +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getDomain().getName() +" ORGANIZATION="+ ( userData.getOrganization() != null ?userData.getOrganization().getQualifiedName(): "N/A" ) +" PROFILE-SET="+( userData.getProfileSet() != null ? userData.getProfileSet().getQualifiedName(): "N/A")  +" STATUS=" + (userData.getStatus() != null ? userData.getStatus().getValue() : "N/A") + " FRAMED-IP-ADDRESS=" + framedIpAddress);
		} catch (SQLException e) {
			Logger.logInfo(MODULE, "CLU-00005- No response from server, Reason: "+e.getMessage());
			errorResponse(response, "CLU-00005", UPDATE_OPERATION, "No response from server");
			operationStatusMessage.append("MESSAGE=No response from server ERROR_CODE=CLU-00005 SUBSCRIBER_NAME="+ (userData.getName() != null ? userData.getName() : "N/A") +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getDomain().getName() +" ORGANIZATION="+ ( userData.getOrganization() != null ?userData.getOrganization().getQualifiedName(): "N/A" ) +" PROFILE-SET="+( userData.getProfileSet() != null ? userData.getProfileSet().getQualifiedName(): "N/A")  +" STATUS=" + (userData.getStatus() != null ? userData.getStatus().getValue() : "N/A") + " FRAMED-IP-ADDRESS=" + framedIpAddress);			
		} catch (DatabaseConnectionException e) {
			Logger.logInfo(MODULE, "CLU-00005- No response from server, Reason: "+e.getMessage());
			errorResponse(response, "CLU-00005", UPDATE_OPERATION, "No response from server");
			operationStatusMessage.append("MESSAGE=No response from server ERROR_CODE=CLU-00005 SUBSCRIBER_NAME="+ (userData.getName() != null ? userData.getName() : "N/A") +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getDomain().getName() +" ORGANIZATION="+ ( userData.getOrganization() != null ?userData.getOrganization().getQualifiedName(): "N/A" ) +" PROFILE-SET="+( userData.getProfileSet() != null ? userData.getProfileSet().getQualifiedName(): "N/A")  +" STATUS=" + (userData.getStatus() != null ? userData.getStatus().getValue() : "N/A") + " FRAMED-IP-ADDRESS=" + framedIpAddress);			
		} catch (IllegalArgumentException iae) {
			Logger.logInfo(MODULE, "SSB-00008- "+iae.getMessage());
			errorResponse(response, "SSB-00008", UPDATE_OPERATION, iae.getMessage());
			operationStatusMessage.append("MESSAGE="+iae.getMessage()+" ERROR_CODE=SSB-00008 SUBSCRIBER_NAME="+ (userData.getName() != null ? userData.getName() : "N/A") +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getDomain().getName() +" ORGANIZATION="+ ( userData.getOrganization() != null ?userData.getOrganization().getQualifiedName(): "N/A" ) +" PROFILE-SET="+( userData.getProfileSet() != null ? userData.getProfileSet().getQualifiedName(): "N/A")  +" STATUS=" + (userData.getStatus() != null ? userData.getStatus().getValue() : "N/A") + " FRAMED-IP-ADDRESS=" + framedIpAddress);			
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	private void createOperation(HttpServletResponse response, UserData userData, StringBuilder operationStatusMessage) {

		boolean isValidSubscriberDetail = validateSubscriberDetail(response, userData, operationStatusMessage);

		if (isValidSubscriberDetail == false) {
			return;
		}

		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		String framedIpAddress = "0.0.0.0";
		
		try {
			Map<String, Object> data = prepareDataForCreateSubscriber(userData);

			Profile profile = userData.getProfileSet().getProfile();
			if (profile != null && profile.getAttribute() != null) {
				Value valueTagForFramedAddress = profile.getAttribute().getValue();
				framedIpAddress = valueTagForFramedAddress.getValue();
			}
			
			if (data.isEmpty()) {
				errorResponse(response, "PRF-00019", CREATE_OPERATION, "Entity was not assigned a profile set");
				operationStatusMessage.append("MESSAGE=Entity was not assigned a profile set ERROR_CODE=PRF-00019 SUBSCRIBER_NAME="+ userData.getName() +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getOrganization().getDomain().getName() +" ORGANIZATION="+userData.getOrganization().getQualifiedName()+" PROFILE-SET="+userData.getProfileSet().getQualifiedName() +" STATUS=" + userData.getStatus().getValue() + 
						" FRAMED-IP-ADDRESS=" + framedIpAddress);
				return;
			}

			connection = subscriberProfileWebServiceBLManager.getDbConfiguration().getDbConnectionManager().getConnection();
			prepareStatement = connection.prepareStatement("select SEQ_RADIUSCUSTOMER.NEXTVAL as id from dual");
			resultSet = prepareStatement.executeQuery();
			String id = "";
			if (resultSet.next()) {
				id = resultSet.getString("id");
				data.put("ID", id);
			}
			subscriberProfileWebServiceBLManager.addSubscriber(data);
			successResponseForCreate(response, id, userData.getLoginName());
			operationStatusMessage.append("MESSAGE=SUCCESS SUBSCRIBER_NAME="+ userData.getName() +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getOrganization().getDomain().getName() +" ORGANIZATION="+userData.getOrganization().getQualifiedName()+" PROFILE-SET="+userData.getProfileSet().getQualifiedName() +" STATUS=" + userData.getStatus().getValue() + " FRAMED-IP-ADDRESS=" + framedIpAddress);
		} catch (SubscriberProfileWebServiceException e) {
			Logger.logInfo(MODULE, "CLU-00005- No response from server, Reason: "+e.getMessage());				
			errorResponse(response, "CLU-00005", CREATE_OPERATION, "No response from server");
			operationStatusMessage.append("MESSAGE=No response from server ERROR_CODE=CLU-00005 SUBSCRIBER_NAME="+ userData.getName() +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getOrganization().getDomain().getName() +" ORGANIZATION="+userData.getOrganization().getQualifiedName()+" PROFILE-SET="+userData.getProfileSet().getQualifiedName() +" STATUS=" + userData.getStatus().getValue() + " FRAMED-IP-ADDRESS=" + framedIpAddress);
		} catch (SQLException e) {
			if (e.getErrorCode() == 00001) {
				Logger.logInfo(MODULE, "USR-00017- A user with login name `"+userData.getLoginName()+"` already exists, Reason: "+e.getMessage());
				errorResponse(response, "USR-00017", CREATE_OPERATION, "A user with login name `"+userData.getLoginName()+"` already exists");
				operationStatusMessage.append("MESSAGE=A user with login name `"+userData.getLoginName()+"` already exists ERROR_CODE=USR-00017 SUBSCRIBER_NAME="+ userData.getName() +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getOrganization().getDomain().getName() +" ORGANIZATION="+userData.getOrganization().getQualifiedName()+" PROFILE-SET="+userData.getProfileSet().getQualifiedName() +" STATUS=" + userData.getStatus().getValue() + " FRAMED-IP-ADDRESS=" + framedIpAddress);
				return;
			}
			errorResponse(response, "CLU-00005", CREATE_OPERATION, "No response from server");
			operationStatusMessage.append("MESSAGE=No response from server ERROR_CODE=CLU-00005 SUBSCRIBER_NAME="+ userData.getName() +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getOrganization().getDomain().getName() +" ORGANIZATION="+userData.getOrganization().getQualifiedName()+" PROFILE-SET="+userData.getProfileSet().getQualifiedName() +" STATUS=" + userData.getStatus().getValue() + " FRAMED-IP-ADDRESS=" + framedIpAddress);			
		} catch (DatabaseConnectionException e) {
			Logger.logInfo(MODULE, "CLU-00005- No response from server, Reason: "+e.getMessage());
			errorResponse(response, "CLU-00005", CREATE_OPERATION, "No response from server");
			operationStatusMessage.append("MESSAGE=No response from server ERROR_CODE=CLU-00005 SUBSCRIBER_NAME="+ userData.getName() +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getOrganization().getDomain().getName() +" ORGANIZATION="+userData.getOrganization().getQualifiedName()+" PROFILE-SET="+userData.getProfileSet().getQualifiedName() +" STATUS=" + userData.getStatus().getValue() + " FRAMED-IP-ADDRESS=" + framedIpAddress);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	private boolean validateSubscriberDetail(HttpServletResponse response, UserData userData, StringBuilder operationStatusMessage) {

		if (userData.getOrganization() == null) {
			Logger.logDebug(MODULE, "USR-00012- User Cannot be Created. Organization not found");
			errorResponse(response, "USR-00012", CREATE_OPERATION, "User Cannot be Created. Organization not found");
			return false;
		}

		String orgName = userData.getOrganization().getQualifiedName();
		String org = orgName.substring(1);

		if (this.orgToRealmMap.containsKey(org) == false) {
			errorResponse(response, "USR-00012", CREATE_OPERATION, "User cannot be created in organization `" + orgName + "`. Organization not found");
			operationStatusMessage.append("MESSAGE=User cannot be created in organization `" + orgName + "`. Organization not found ERROR_CODE=USR-00012 SUBSCRIBER_NAME="+ userData.getName() +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getOrganization().getDomain().getName() +" ORGANIZATION="+userData.getOrganization().getQualifiedName()+" PROFILE-SET="+userData.getProfileSet().getQualifiedName() +" STATUS=" + userData.getStatus().getValue() );
			Logger.logDebug(MODULE, "User cannot be created in organization `" + orgName + "`. Organization not found");
			return false;
		}

		String realmName = this.orgToRealmMap.get(org);

		if (Strings.isNullOrEmpty(realmName) == false) {
			userData.getOrganization().getDomain().setName(realmName);
		}

		List<String> profileSetList = this.orgToProfileSetMap.get(org);
		String profileName = userData.getProfileSet().getQualifiedName();
		String profileWithoutOrgName = profileName.substring(profileName.indexOf("/", 1)+1);

		if (profileSetList.contains(profileWithoutOrgName) == false) {
			errorResponse(response, "PRF-00011", CREATE_OPERATION, "Profile Set `" +profileName+ "` not found");
			operationStatusMessage.append("MESSAGE=Profile Set `" +profileName+ "` not found ERROR_CODE=PRF-00011 SUBSCRIBER_NAME="+ userData.getName() +"  LOGIN_NAME="+userData.getLoginName()+" D0MAIN="+userData.getOrganization().getDomain().getName() +" ORGANIZATION="+userData.getOrganization().getQualifiedName()+" PROFILE-SET="+userData.getProfileSet().getQualifiedName() +" STATUS=" + userData.getStatus().getValue() );
			Logger.logDebug(MODULE, "User cannot be created in organization. Reason: Profile Set `" +profileName+ "` not found");
			return false;
		}

		return true;
	}

	private void deleteOperation(HttpServletResponse response,
			UserData userData, StringBuilder operationStatusMessage) {
		String subscriberId = getSubscriberIDFromData(userData);
		try {
			int deleteCount = subscriberProfileWebServiceBLManager.delSubscriber(subscriberId);

			if (deleteCount == 0) {
				errorResponse(response, "USR-00001", DELETE_OPERATION, "User not found");
				operationStatusMessage.append("MESSAGE=User not found ERROR_CODE=USR-00001 SUBSCRIBER_NAME="+ userData.getLoginName() + " D0MAIN="+ userData.getDomain().getName());
			} else {
				operationStatusMessage.append("MESSAGE=SUCCESS SUBSCRIBER_NAME="+ userData.getLoginName() + " D0MAIN="+ userData.getDomain().getName());
				successResponseForDelete(response, subscriberId);
			}
		} catch (SubscriberProfileWebServiceException e) {
			Logger.logInfo(MODULE, "CLU-00005 - No response from server, Reason: "+e.getMessage());
			errorResponse(response, "CLU-00005", DELETE_OPERATION, "No response from server");
			operationStatusMessage.append("MESSAGE=No response from server ERROR_CODE=CLU-00005 SUBSCRIBER_NAME="+ userData.getLoginName() + " D0MAIN="+ userData.getDomain().getName());
		} catch (SQLException e) {
			Logger.logInfo(MODULE, "CLU-00005- No response from server, Reason: "+e.getMessage());
			errorResponse(response, "CLU-00005", DELETE_OPERATION, "No response from server");
			operationStatusMessage.append("MESSAGE=No response from server ERROR_CODE=CLU-00005 SUBSCRIBER_NAME="+ userData.getLoginName() + " D0MAIN="+ userData.getDomain().getName());
		} catch (DatabaseConnectionException e) {
			Logger.logInfo(MODULE, "CLU-00005- No response from server, Reason: "+e.getMessage());
			errorResponse(response, "CLU-00005", DELETE_OPERATION, "No response from server");
			operationStatusMessage.append("MESSAGE=No response from server ERROR_CODE=CLU-00005 SUBSCRIBER_NAME="+ userData.getLoginName() + " D0MAIN="+ userData.getDomain().getName());
		}
	}

	private List<FANode> sendRequest(HttpServletResponse response,
			String operationReceivedInRequest,
			TargetData targetData)  throws DataSourceException, SQLException, RemoteException, ServiceException {

		String subscriberName = targetData.getParameter().getWimax().getSubscriberName();

		Connection connection = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		List<FANode> faNodes = new ArrayList<FANode>();

		try {
			connection = subscriberProfileWebServiceBLManager.getDbConfiguration().getDbConnectionManager().getConnection();

			String sessionLookupColumn = logicalNameToColumn.get("SessionLookUpColumn");

			String sessionQuery = "SELECT * FROM TBLMCONCURRENTUSERS WHERE " + sessionLookupColumn + " = '" + subscriberName + "'";
			Logger.logInfo(MODULE, "Session Query: " + sessionQuery);

			prepareStatement = connection.prepareStatement(sessionQuery);
			resultSet = prepareStatement.executeQuery();


			HashMap<String, String[]> attributeMap = new HashMap<String, String[]>();
			int numberOfRecordFound = 0;

			while (resultSet.next()) {

				numberOfRecordFound++;

				String aaaID = resultSet.getString(logicalNameToColumn.get("AAA_Identity"));
				String sessionType = resultSet.getString(logicalNameToColumn.get("SessionType"));

				String pseudoIdentifier = null;
				if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("PseudoIdentity"))) {
					pseudoIdentifier = resultSet.getString(logicalNameToColumn.get("PseudoIdentity"));
				}

				if ("radius".equalsIgnoreCase(sessionType)) {

					attributeMap = createDMParameters(resultSet);

					EliteGenericRadiusServiceLocator dmWsEndpoint = aaaIDToServiceLocatorForDM.get(aaaID);
					if (dmWsEndpoint != null) {

						Logger.logInfo(MODULE, "Sending disconnect request to AAA with id: " + aaaID + " using " +
								"disconnnect url: " + dmWsEndpoint.getEliteGenericRadiusWSAddress());

						@SuppressWarnings("rawtypes")
						HashMap responseAttributes = dmWsEndpoint.getEliteGenericRadiusWS()
						.requestGenericRadiusWS(RadiusConstants.DISCONNECTION_REQUEST_MESSAGE, 1, attributeMap);

						FANode faNode = new FANode();
						faNode.setSent("true");
						faNode.setRmsClusterId("0");
						faNode.setPseudoIdentifier(pseudoIdentifier);

						String[] packetType = (String[]) responseAttributes.get("21067:127");

						if (String.valueOf(RadiusConstants.DISCONNECTION_ACK_MESSAGE).equals(packetType[0])) {
							ErrorMessage message = new ErrorMessage();
							message.setMessage("Disconnect Request: Successfull");							
							faNode.setError(message);
						} else if (String.valueOf(RadiusConstants.DISCONNECTION_NAK_MESSAGE).equals(packetType[0])) {

							String[] errorCause = (String[])responseAttributes.get("0:101");

							if (errorCause != null && errorCause.length > 0) {
								ErrorMessage message = new ErrorMessage();								
								message.setMessage("Error cause:" + errorCause[0]);								
								faNode.setError(message);								
							} else {
								ErrorMessage message = new ErrorMessage();
								message.setMessage("Unknown Error cause");
								faNode.setError(message);
								Logger.logDebug(MODULE, "Error cause does not recived in Disconnect NAK.");
							}
						} else {
							Logger.logDebug(MODULE, "Packet Type 0 recived in response, Reason: as request was timeout.");
							ErrorMessage message = new ErrorMessage();
							message.setMessage("Timeout occured waiting for reponse");
							faNode.setError(message);
						}
						faNodes.add(faNode);
					} else {
						Logger.logWarn(MODULE, "Disconnect Service locator not found for AAA Id: "+ aaaID);
					}

				} else if ("diameter".equalsIgnoreCase(sessionType)) {

					HashMap<String, String> attributeMap1 = createASRParameters(resultSet);
					EliteDiameterWSServiceLocator asrWsEndpoint = aaaIDToServiceLocatorForASR.get(aaaID);
					if (asrWsEndpoint != null) {

						Logger.logInfo(MODULE, "sending abort session request to aaa with id: " + aaaID + " using " +
								"abort-session url: " + asrWsEndpoint.getEliteDiameterWSAddress());

						int responseCode = asrWsEndpoint.getEliteDiameterWS().wsDiameterAbortSessionRequest(attributeMap1);

						FANode faNode = new FANode();
						faNode.setSent("true");
						faNode.setPseudoIdentifier(pseudoIdentifier);
						faNode.setRmsClusterId("0");

						if (responseCode == -1) {
							Logger.logDebug(MODULE, "Error occured while processing ASR request by AAA: "+ aaaID);
							ErrorMessage message = new ErrorMessage();
							message.setMessage("Internal Error: " + responseCode);
							faNode.setError(message);
						} else if (responseCode == ResultCode.DIAMETER_UNABLE_TO_COMPLY.code) {

							Logger.logDebug(MODULE, "Recieved result code: " +responseCode+ ", as request was timeout.");

							ErrorMessage message = new ErrorMessage();
							message.setMessage("Timeout occured waiting for reponse");
							faNode.setError(message);
						} else {
							ErrorMessage message = new ErrorMessage();
							message.setMessage("Abort Session Request: Successfull");
							faNode.setError(message);
						}
						faNodes.add(faNode);
					} else {
						Logger.logWarn(MODULE, "ASR Service locator not found for AAA Id: "+ aaaID);
					}
				} else {
					Logger.logWarn(MODULE, "Disconnect or ASR will not be generated, as invalid value: " + sessionType + " found for sessoin type identification," +
							" possible value can be radius or diameter.");
				}
			}

			if (numberOfRecordFound == 0) {
				errorResponse(response, "DAE-00403", operationReceivedInRequest, "Could not find the `RADIUS` RMS session for subscriber '" + subscriberName + "'");	
			}

			return faNodes;
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(connection);
		}
	}

	private HashMap<String, String> createASRParameters(ResultSet resultSet) throws SQLException {
		HashMap<String, String> attributeMap = new HashMap<String, String>();


		if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("SessionUsername"))) {
			String userName = resultSet.getString(logicalNameToColumn.get("SessionUsername"));
			attributeMap.put("0:1", userName);
		}

		if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("AcctSessionID"))) {
			String sessionId = resultSet.getString(logicalNameToColumn.get("AcctSessionID"));
			attributeMap.put("0:263", sessionId);
		}

		return attributeMap;
	}

	private HashMap<String, String[]> createDMParameters(ResultSet resultSet) throws SQLException {

		HashMap<String, String[]> attributeMap = new HashMap<String, String[]>();

		if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("SessionUsername"))) {
			String userName = resultSet.getString(logicalNameToColumn.get("SessionUsername"));
			attributeMap.put("0:1", new String[]{userName});
		}

		if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("NasIPAddress"))) {
			String nasIpAddress = resultSet.getString(logicalNameToColumn.get("NasIPAddress"));
			attributeMap.put("0:4", new String[]{nasIpAddress});
		}

		if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("FramedIPAddress"))) {
			String nasPort = resultSet.getString(logicalNameToColumn.get("FramedIPAddress"));
			attributeMap.put("0:8", new String[]{nasPort});
		}

		if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("CallingStationID"))) {
			String callingStationID = resultSet.getString(logicalNameToColumn.get("CallingStationID"));
			attributeMap.put("0:31", new String[]{callingStationID});
		}

		if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("NasIdentifier"))) {
			String nasID = resultSet.getString(logicalNameToColumn.get("NasIdentifier"));
			attributeMap.put("0:32", new String[]{nasID});
		}

		if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("NasPort"))) {
			String nasPort = resultSet.getString(logicalNameToColumn.get("NasPort"));
			attributeMap.put("0:5", new String[]{nasPort});
		}

		if (DBUtility.isValueAvailable(resultSet, logicalNameToColumn.get("AAASessionID"))) {
			String wimaxAAASession = resultSet.getString(logicalNameToColumn.get("AAASessionID"));
			attributeMap.put("24757:4", new String[]{wimaxAAASession});
		}
		return attributeMap;
	}

	private String getSubscriberIDFromData(UserData userData) {
		return userData.getLoginName() + CHAR_FOR_SUBSCRIBER_ID + userData.getDomain().getName();
	}

	private Map<String, Object> prepareDataForCreateSubscriber(UserData data) {

		Map<String,Object> dataForOperation = new HashMap<String, Object>();

		String policy = data.getProfileSet().getQualifiedName();
		policy = policy.substring(policy.indexOf("/", 1)+1);

		boolean isPolicyAvailable = validateTheProfileSet(policy, "TBLMDIAMETERPOLICY");

		if (isPolicyAvailable == false) {
			Logger.logDebug(MODULE, "Diameter policy with name: " + policy + " doesn't exist. So, will " +
					"check for radius policy");
			isPolicyAvailable = validateTheProfileSet(policy, "TBLMRADIUSPOLICY");
		}

		if (isPolicyAvailable == false) {
			Logger.logDebug(MODULE, "Radius policy with name: " + policy + " also doesn't exist.");
			Logger.logDebug(MODULE, "PRF-00019 - Entity was not assigned a profile set.");
			return dataForOperation;
		}

		dataForOperation.put(logicalNameToColumn.get("RadiusPolicy"), policy);
		dataForOperation.put(logicalNameToColumn.get("DiameterPolicy"), policy);

		String customerReplyItem = logicalNameToColumn.get("CustomerReplyItem");
		if (customerReplyItem != null) {
			dataForOperation.put(customerReplyItem, "0:25=\""+policy+"\"");
		}

		dataForOperation.put(logicalNameToColumn.get("ConcurrentLoginPolicy"), policy);
		
		String haProfileName = "HA-" + policy;
		dataForOperation.put(logicalNameToColumn.get("HAProfile"), haProfileName);

		String loginName = data.getLoginName();

		String columnForLoginName = logicalNameToColumn.get("LoginName");
		if (Strings.isNullOrEmpty(columnForLoginName) == false) {
			dataForOperation.put(columnForLoginName, loginName);
		}

		String realm = "";
		if (data.getOrganization() != null && data.getOrganization().getDomain() != null) {
			realm = data.getOrganization().getDomain().getName();
		} else if (data.getDomain() != null) {
			realm = data.getDomain().getName();
		}

		String columnForRealm = logicalNameToColumn.get("Realm");
		if (Strings.isNullOrEmpty(columnForRealm) == false) {
			dataForOperation.put(columnForRealm, realm);
		}

		String userIdentity = loginName + CHAR_FOR_SUBSCRIBER_ID + realm;

		dataForOperation.put(logicalNameToColumn.get("Username"), userIdentity);
		dataForOperation.put(logicalNameToColumn.get("UserIdentity"), userIdentity);
		dataForOperation.put(logicalNameToColumn.get("CUI"), userIdentity);

		String password = data.getPassword().getValue();

		String columnForPlainTextPassword = logicalNameToColumn.get("PlaintextPassword");

		if (Strings.isNullOrEmpty(columnForPlainTextPassword) == false) {
			dataForOperation.put(columnForPlainTextPassword, password);
		}

		String passWordColumn = logicalNameToColumn.get("Password");

		if(Strings.isNullOrEmpty(passWordColumn) == false) {
			dataForOperation.put(passWordColumn, new Base32CryptEncryption().crypt(password));
		}

		dataForOperation.put(logicalNameToColumn.get("EncryptionType"), "32");

		String framedIpAddress = "0.0.0.0";
		Profile profile = data.getProfileSet().getProfile();
		if (profile != null && profile.getAttribute() != null) {
			Value valueTagForFramedAddress = profile.getAttribute().getValue();
			framedIpAddress = valueTagForFramedAddress.getValue();
		}

		dataForOperation.put(logicalNameToColumn.get("FramedAddress"), framedIpAddress);
		dataForOperation.put(logicalNameToColumn.get("CreditLimit"), "2500");

		if (data.getStatus() != null) {
			dataForOperation.put(logicalNameToColumn.get("CustomerStatus"), data.getStatus().getValue());
		}

		return dataForOperation;
	}

	private Map<String, Object> prepareDataForUpdateSubscriber(UserData data) throws IllegalArgumentException{

		Map<String,Object> dataForOperation = new HashMap<String, Object>();
		Status status = data.getStatus();
		if (status != null) {
			prepareDataForUpdateStatus(dataForOperation, status);
		} else if (Strings.isNullOrBlank(data.getNewLoginName()) == false) {
			prepareDataForNewLoginName(data, dataForOperation);
		} else if(data.getProfileSet() != null && Strings.isNullOrBlank(data.getProfileSet().getQualifiedName()) == false) {
			prepareDataForProfileSet(data, dataForOperation);
		} else if(data.getPassword() != null) {
			prepareDataForPasswordChange(data, dataForOperation);
		}

		return dataForOperation;
	}

	private void prepareDataForPasswordChange(UserData data,
			Map<String, Object> dataForOperation) {
		dataForOperation.put(logicalNameToColumn.get("Password"), new Base32CryptEncryption().crypt(data.getPassword().getValue()));

		String columnForPlainTextPassword = logicalNameToColumn.get("PlaintextPassword");

		if (Strings.isNullOrEmpty(columnForPlainTextPassword) == false) {
			dataForOperation.put(columnForPlainTextPassword, data.getPassword().getValue());
		}
	}

	private void prepareDataForProfileSet(UserData data,
			Map<String, Object> dataForOperation) {
		String policy = data.getProfileSet().getQualifiedName();
		policy = policy.substring(policy.indexOf("/", 1)+1);

		boolean isPolicyAvailable = validateTheProfileSet(policy, "TBLMDIAMETERPOLICY");

		if (isPolicyAvailable == false) {
			Logger.logDebug(MODULE, "Diameter policy with name: " + policy + " doesn't exist. So, will " +
					"check for radius policy");
			isPolicyAvailable = validateTheProfileSet(policy, "TBLMRADIUSPOLICY");
		}

		if (isPolicyAvailable == false) {
			Logger.logDebug(MODULE, "Radius policy with name: " + policy + " also doesn't exist.");
			Logger.logDebug(MODULE, "PRF-00019 - Entity was not assigned a profile set.");
			return;
		}

		dataForOperation.put(logicalNameToColumn.get("RadiusPolicy"), policy);
		dataForOperation.put(logicalNameToColumn.get("DiameterPolicy"), policy);

		String customerReplyItem = logicalNameToColumn.get("CustomerReplyItem");
		if (customerReplyItem != null) {
			dataForOperation.put(customerReplyItem, "0:25=\""+policy+"\"");
		}

		dataForOperation.put(logicalNameToColumn.get("ConcurrentLoginPolicy"), policy);
		
		String haProfileName = "HA-" + policy;
		dataForOperation.put(logicalNameToColumn.get("HAProfile"), haProfileName);

		if (data.getProfileSet().getProfile() != null && data.getProfileSet().getProfile().getAttribute() != null) {
			dataForOperation.put(logicalNameToColumn.get("FramedAddress"), data.getProfileSet().getProfile().getAttribute().getValue().getValue());
		}
	}

	private boolean validateTheProfileSet(String policy, String tableForLookup) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null; 
		boolean isPolicyExist = false;
		try {
			connection = subscriberProfileWebServiceBLManager.getDbConfiguration().
					getDbConnectionManager().getConnection();

			String queryForPolicy = getPolicyDataQueryForTable(policy, tableForLookup);

			Logger.logDebug(MODULE, "Query for policy validation: " + queryForPolicy);

			statement = connection.prepareStatement(queryForPolicy);
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				isPolicyExist = true;
			}
		} catch (DataSourceException e) {
			Logger.logError(MODULE, "Error while validating profile-set, Reason: "+e.getMessage());
			Logger.logTrace(MODULE,e);
		} catch (SQLException e) {
			Logger.logError(MODULE, "Error while validating profile-set, Reason: "+e.getMessage());
			Logger.logTrace(MODULE,e);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
		}
		return isPolicyExist;
	}

	private String getPolicyDataQueryForTable(String policyName, String tableName) {
		return "SELECT NAME FROM " + tableName + " WHERE NAME = '" + policyName + "'";
	}

	private void prepareDataForNewLoginName(UserData data,
			Map<String, Object> dataForOperation) {
		String subscriberId = "";
		if (data.getDomain() != null) {
			subscriberId = data.getNewLoginName() + CHAR_FOR_SUBSCRIBER_ID + data.getDomain().getName();
		} else if (data.getOrganization() != null && data.getOrganization().getDomain() != null) {
			subscriberId = data.getNewLoginName() + CHAR_FOR_SUBSCRIBER_ID + data.getOrganization().getDomain().getName();
		}

		dataForOperation.put(logicalNameToColumn.get("Username"), subscriberId);
		dataForOperation.put(logicalNameToColumn.get("UserIdentity"), subscriberId);
		dataForOperation.put(logicalNameToColumn.get("CUI"), subscriberId);
	}

	private void prepareDataForUpdateStatus(
			Map<String, Object> dataForOperation, Status status) {
		String value = status.getValue();
		if ("suspended".equalsIgnoreCase(value)) {
			if (Strings.isNullOrBlank(status.getReason()) == false) {
				value = "INACTIVE";
			} else {
				throw new IllegalArgumentException("Suspension reason required when suspending an entity");
			}
		} else if ("active".equalsIgnoreCase(value)) {
			value = "ACTIVE";
		}
		dataForOperation.put(logicalNameToColumn.get("CustomerStatus"), value);
	}


	private RequestData unmarshal(InputStream requestAsInputStream) throws JAXBException, SAXException {
		Unmarshaller unmarshaller = requestContext.createUnmarshaller();
		RequestData requestData = (RequestData) unmarshaller.unmarshal(requestAsInputStream);
		return requestData;
	}

	private void marshal(Response userResponse, HttpServletResponse response) throws IOException, JAXBException {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		Marshaller marshaller = createMarshaller();
		marshaller.marshal(userResponse, stream);

		response.getOutputStream().write(stream.toByteArray());
		response.setContentType("text/xml");
		response.setCharacterEncoding(UTF_8);
		response.setContentLength(stream.size());        
	}

	private Marshaller createMarshaller() throws JAXBException {
		Marshaller marshaller = responseContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		return marshaller;
	}

	public String format(String unformattedXml) throws SAXException, ParserConfigurationException, IOException {
		Document document = parseXmlFile(unformattedXml);

		OutputFormat format = new OutputFormat(document);
		format.setLineWidth(150);
		format.setIndenting(true);
		format.setIndent(2);
		Writer out = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(out, format);
		serializer.serialize(document);

		return out.toString();
	}

	private Document parseXmlFile(String in) throws SAXException, ParserConfigurationException, IOException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			throw e;
		} catch (SAXException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
	}
}