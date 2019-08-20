package com.elitecore.aaa.diameter.util;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;

/**
 * A helper class that provides methods for controlling the diameter flow.
 * While doing asynchronous communication we need to remember which flags should be 
 * set, which is error prone, so this class takes care of it. Helpers for dropping, rejecting
 * the request are provided for use.
 *  
 * @author narendra.pathai
 *
 */
public class DiameterProcessHelper {

	/**
	 * Adds or updates the Result Code in {@code response} to the {@code resultCodeValue}.
	 * Also adds or updates the Error Message in {@code response} to {@code errorMessage}.
	 *  
	 * No further processing is carried out.
	 * 
	 * @param response response to be rejected
	 * @param resultCodeValue a non-null result code value to be added in response.
	 * @param errorMessage a non-null error message to be added in response.
	 */
	public static void rejectResponse(@Nonnull ApplicationResponse response, @Nonnull ResultCode resultCodeValue , 
			@Nonnull String errorMessage) {

		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, response.getDiameterAnswer(), String.valueOf(resultCodeValue.code));

		upgradeErrorMessage(response, errorMessage);
		
		updateErrorBit(response, resultCodeValue);

		response.setFurtherProcessingRequired(false);
		response.setProcessingCompleted(true);
	}

	private static void updateErrorBit(ApplicationResponse response, ResultCode resultCodeValue) {
		if(resultCodeValue.category == ResultCodeCategory.RC3XXX) {
			response.getDiameterAnswer().setErrorBit();
		} else {
			response.getDiameterAnswer().resetErrorBit();
		}
	}

	/**
	 * Stops the execution of the present request for asynchronous communication. No more 
	 * handlers are applied and response is <b>NOT</b> sent. The processing resumes 
	 * when the request is submitted in queue on async response arrival.
	 * 
	 * @param request request that is being sent asynchronously
	 * @param response the original response
	 */
	public static void onExternalCommunication(@Nonnull ApplicationRequest request, 
			@Nonnull ApplicationResponse response) {
		response.setFurtherProcessingRequired(false);
		response.setProcessingCompleted(false);
	}

	/**
	 * Sets various flags required to drop the response. No further processing is carried out.
	 * 
	 * @param response response to be dropped
	 */
	public static void dropResponse(@Nonnull ApplicationResponse response) {
		response.markForDropRequest();
		response.setFurtherProcessingRequired(false);
		response.setProcessingCompleted(true);
	}

	//As per RFC-6733, ERROR_MESSAGE must be present 0 or 1 times.
	private static void upgradeErrorMessage(ApplicationResponse response,
			String errorMessage) {
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, response.getDiameterAnswer(),errorMessage);
	}
	
	public static void toSuccess(ApplicationResponse response) {
		DiameterAnswer answer = response.getDiameterAnswer();
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE,answer, ResultCode.DIAMETER_SUCCESS.code + "");
		ArrayList<IDiameterAVP> errorMessageAVPs = answer.getAVPList(DiameterAVPConstants.ERROR_MESSAGE);
		if (Collectionz.isNullOrEmpty(errorMessageAVPs) == false) {
			answer.removeAllAVPs(errorMessageAVPs, true);
		}
		answer.resetErrorBit();
		answer.refreshPacketHeader();
	}
}
