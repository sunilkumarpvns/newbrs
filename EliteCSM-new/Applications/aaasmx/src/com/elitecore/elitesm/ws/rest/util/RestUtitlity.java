package com.elitecore.elitesm.ws.rest.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.ConstraintValidatorContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Files;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InputParameterMissingException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.rest.constant.ResultCode;
import com.elitecore.elitesm.ws.rest.data.Status;
import com.elitecore.elitesm.ws.rest.exception.EliteResponseCode;
import com.elitecore.elitesm.ws.rest.exception.EliteResponseCode.EliteResponse;

public class RestUtitlity {
	
	private static final String BULK_OPERATION_MESSAGE = " Bulk Operation was partially successful";
	private static final String DUPLICATE_INSTANCE_FOUND = "unique";
	private static final String FAILURE = "FAILURE";
	private static final String SUCCESS = "SUCCESS";
	
	private RestUtitlity(){
	}

	public static Response getHelp(String fileName) throws FileNotFoundException, IllegalArgumentException, IOException {
		String path = EliteUtility.getSMHome() + File.separator + "WEB-INF" + File.separator
				+ "ws" + File.separator + "rest"+ File.separator + "help" + File.separator + fileName + ".txt";

		File helpFile = new File(path);
		byte[] fileBytes = Files.readFully(helpFile);
		String help = new String(fileBytes);
		return Response.ok(help).type(MediaType.TEXT_HTML).build();
	}

	public static String getXML(String fileName) throws FileNotFoundException, IllegalArgumentException, IOException {
		String path = EliteUtility.getSMHome() + File.separator + "startupdata" + File.separator + "xmldata"+ File.separator + fileName + ".xml";

		File xmlFile = new File(path);
		byte[] fileBytes = Files.readFully(xmlFile);
		String xml = new String(fileBytes);
		return xml;
	}

	// method set custom message for custom validator
	public static void setValidationMessage(ConstraintValidatorContext context, String message) {
		
		HibernateConstraintValidatorContext hibernateContext = context.unwrap( HibernateConstraintValidatorContext.class );
		hibernateContext.disableDefaultConstraintViolation();
		hibernateContext.buildConstraintViolationWithTemplate( message ).addConstraintViolation();
		
	}
	
	public static String getDefaultDescription() {
		
		if( SecurityContextHolder.getContext().getAuthentication() != null ) {

			StaffData staffData = (StaffData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			
			String userName = staffData.getUsername();
			
			SimpleDateFormat formatter = new SimpleDateFormat(ConfigManager.get(ConfigConstant.DATE_FORMAT));
			
			return ("Created by " + userName + " on " + formatter.format(new Date()));
		}
		
		return "";
	}
	
	public static EliteResponseCode getResponse(String message){
		return response(message, ResultCode.SUCCESS);
	}
	
	public static EliteResponseCode getResponseTime(String message, String responseTime){
		return responseTime(message, ResultCode.SUCCESS, responseTime);
	}
	
	public static EliteResponseCode getResponse(String message, ResultCode resultCode){
		return response(message, resultCode);
	}
	
	public static EliteResponseCode getResponse(String moduleName, String massage, Map<String, List<Status>> moduleStatus, String isPartial, String isStatusRecord){
		return responseWithBody(moduleName, massage,moduleStatus,isPartial,isStatusRecord);
	}

	private static EliteResponseCode response(String message, ResultCode resultCode) {
		EliteResponseCode eliteResponseCode = new  EliteResponseCode();
		List<EliteResponse> eliteResponseCodeList = new ArrayList<EliteResponse>();
		
		eliteResponseCodeList.add(new EliteResponse(message, resultCode.responseCode));
		eliteResponseCode.setResponseCodes(eliteResponseCodeList);
		
		return eliteResponseCode;
	}
	
	private static EliteResponseCode responseTime(String message, ResultCode resultCode, String responseTime) {

		EliteResponseCode eliteResponseCode = new EliteResponseCode();
		List<EliteResponse> eliteResponseCodeList = new ArrayList<EliteResponse>();

		eliteResponseCodeList.add(new EliteResponse(message, resultCode.responseCode, responseTime));
		eliteResponseCode.setResponseCodes(eliteResponseCodeList);

		return eliteResponseCode;
	}
	
	private static EliteResponseCode responseWithBody(String moduleName, String massage, Map<String, List<Status>> moduleStatus, String isPartial, String isStatusRecord){
		
		EliteResponseCode eliteResponseCode = new EliteResponseCode();
		List<EliteResponse> eliteResponseCodeList = new ArrayList<EliteResponse>();
		
		List<Status> failureRecords;
		List<Status> successRecords;
		List<Status> failureAndSuccessRecords;

		if(isPartial.equalsIgnoreCase("true") && isStatusRecord.equalsIgnoreCase("true")){
			
			successRecords = getRecordsList(moduleStatus, SUCCESS);
			failureAndSuccessRecords = getRecordsList(moduleStatus, FAILURE);
			int faliedRecord = failureAndSuccessRecords.size();
			failureAndSuccessRecords.addAll(successRecords);
			
			eliteResponseCodeList.add(new EliteResponse(moduleName + BULK_OPERATION_MESSAGE,
					ResultCode.PARTIAL_SUCCESS.responseCode, failureAndSuccessRecords,
					String.valueOf(successRecords.size()), String.valueOf(faliedRecord)));

		} else if(isPartial.equalsIgnoreCase("true") && isStatusRecord.equalsIgnoreCase("false")){
		
			successRecords = getRecordsList(moduleStatus, SUCCESS);
			failureRecords = getRecordsList(moduleStatus,FAILURE);
			
			eliteResponseCodeList.add(new EliteResponse(moduleName + BULK_OPERATION_MESSAGE,
					ResultCode.PARTIAL_SUCCESS.responseCode, failureRecords, String.valueOf(successRecords.size()),
					String.valueOf(failureRecords.size())));

		}else if(isPartial.equalsIgnoreCase("true")){
		
			successRecords = getRecordsList(moduleStatus, SUCCESS);
			failureRecords = getRecordsList(moduleStatus, FAILURE);
			
			eliteResponseCodeList.add(new EliteResponse(moduleName + BULK_OPERATION_MESSAGE,
					ResultCode.PARTIAL_SUCCESS.responseCode, String.valueOf(successRecords.size()),
					String.valueOf(failureRecords.size())));

		}else {
		
			eliteResponseCodeList.add(new EliteResponse(moduleName + massage,ResultCode.SUCCESS.responseCode));
		}
		
		eliteResponseCode.setResponseCodes(eliteResponseCodeList);
		return eliteResponseCode;
	}
	
	private static List<Status> getRecordsList(Map<String, List<Status>> moduleStatus, String recordType){
		
		List<Status> result = new ArrayList<Status>();
		Iterator<Entry<String, List<Status>>> iterator = moduleStatus.entrySet().iterator();
		
		while(iterator.hasNext()) {
			Entry<String, List<Status>> nextEntry = iterator.next();
			if(nextEntry.getKey().equals(recordType)) {
				result = nextEntry.getValue();
				break;
			}
		}
		return result;
	}
	
	public static String getResultCode(DataManagerException dataManagerException) {
		
		String resultCode = ResultCode.INTERNAL_ERROR.responseCode;
		
		if (dataManagerException.getCause() instanceof InvalidValueException) {
			resultCode = ResultCode.NOT_FOUND.responseCode;
		} else if (dataManagerException.getCause() instanceof DataValidationException) {
			resultCode = ResultCode.INVALID_INPUT_PARAMETER.responseCode;
		} else if (dataManagerException.getCause() instanceof InputParameterMissingException) {
			resultCode = ResultCode.INPUT_PARAMETER_MISSING.responseCode;
		} else if (dataManagerException.getCause() instanceof ConstraintViolationException) {
			ConstraintViolationException constraintViolationException = (ConstraintViolationException) dataManagerException.getCause();
			if( Strings.isNullOrBlank(constraintViolationException.getSQLException().getMessage()) == false ){
				if (constraintViolationException.getSQLException().getMessage().toLowerCase().contains(DUPLICATE_INSTANCE_FOUND)) {
					resultCode = ResultCode.ALREADY_EXIST.responseCode;
				}
			}
		}
		
		return resultCode;
	}
	
	public static String getStatus(String status) {
		String newStatus = "";
		if (Strings.isNullOrEmpty(status) == false) {
			if (BaseConstant.SHOW_STATUS_ID.equalsIgnoreCase(status)) {
				newStatus = RestValidationMessages.ACTIVE;
			} else if (BaseConstant.HIDE_STATUS_ID.equalsIgnoreCase(status)) {
				newStatus = RestValidationMessages.INACTIVE;
			} 
		}
		return newStatus;

	}
	
}
