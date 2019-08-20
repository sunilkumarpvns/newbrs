/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 15, 2008
 *
 */
package com.elitecore.classicrating.api;

import java.text.ParseException;

import com.elitecore.classicrating.base.IBaseConstant;
import com.elitecore.classicrating.base.IRatingAppContext;
import com.elitecore.classicrating.blmanager.base.AccountingBLManager;
import com.elitecore.classicrating.blmanager.base.AuthorizationBLManager;
import com.elitecore.classicrating.blmanager.base.MandatoryParameterMissingException;
import com.elitecore.classicrating.blmanager.customer.CustomerNotFoundException;
import com.elitecore.classicrating.blmanager.customer.InsufficientBalanceException;
import com.elitecore.classicrating.commons.request.RequestParameters;
import com.elitecore.classicrating.commons.response.ResponseObject;
import com.elitecore.classicrating.commons.util.logger.Log4jLogger;
import com.elitecore.classicrating.datamanager.rating.RateNotFoundException;

/**
 * 
 * @author 
 *
 */
public class EliteClassicRatingEngine implements IEliteClassicRatingEngine {

    private static final String MODULE = "RATING ENGINE BL MANAGER";
    
    private IRatingAppContext ratingAppContext = null;
    private Log4jLogger Logger;

    public EliteClassicRatingEngine() {
    }

    public EliteClassicRatingEngine(IRatingAppContext ratingAppContext) {

        this.ratingAppContext = ratingAppContext;
        //TODO - Change to our standard approach to keep all implementations in sync.
        Logger = Log4jLogger.getInstance();
    }

    /**
     * 
     */
    public IResponseObject authorizationRequest(IRequestParameters requestParameters) {

        Logger.trace(MODULE, "Received Authorization Request. ");
        Logger.debug(MODULE, "Request Parameters : " + requestParameters.toString());

        long startTime = System.currentTimeMillis();

        IResponseObject responseObject = new ResponseObject();
        if(ratingAppContext == null){
            responseObject.setResponseCode(IBaseConstant.APP_CONTEXT_NOT_DEFINED_ERROR);
            responseObject.setResponseMessage("Rating Application Context not defined");
            return responseObject;
        }

        RequestParameters requestParams = (RequestParameters) requestParameters;

        try {
        
        	AuthorizationBLManager authorisationBLManager = new AuthorizationBLManager(ratingAppContext);
            responseObject =  authorisationBLManager.doAuthorization(requestParams);
            
            //TODO Handle null response without any exception
            if (responseObject == null){
            	responseObject = new ResponseObject();
            	responseObject.setResponseCode(IBaseConstant.RATING_ERROR);
                responseObject.setResponseMessage(IBaseConstant.RATING_ERROR_MSG);
            }
            
        } catch (MandatoryParameterMissingException e) {
            responseObject.setResponseCode(IBaseConstant.MANDATORY_PARAMETER_MISSING_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        } catch (CustomerNotFoundException e) {
            responseObject.setResponseCode(IBaseConstant.CUSTOMER_NOT_FOUND_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        } catch (InsufficientBalanceException e) {
            responseObject.setResponseCode(IBaseConstant.INSUFFICIENT_BALANCE_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        } catch (ParseException e) {
            responseObject.setResponseCode(IBaseConstant.INVALID_INPUT_DATA_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        } catch (RateNotFoundException e) {
            responseObject.setResponseCode(IBaseConstant.NO_RATE_DEFINED_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            responseObject.setResponseCode(IBaseConstant.GENERAL_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        Logger.debug(MODULE, "Processed Authorization Request in " + (endTime - startTime) + " ms");
        Logger.debug(MODULE, responseObject.toString());

        return responseObject;
    }

    public IResponseObject accountingRequest(IRequestParameters requestParameters) {

        Logger.debug(MODULE, "Received Accounting Request. ");
        Logger.debug(MODULE, "Request Parameters : " + requestParameters.toString());

        long startTime = System.currentTimeMillis();

        IResponseObject responseObject = new ResponseObject();
     
        if (ratingAppContext == null) {
            responseObject.setResponseCode(IBaseConstant.APP_CONTEXT_NOT_DEFINED_ERROR);
            responseObject.setResponseMessage("Rating Application Context not defined");
            return responseObject;
        }
        
        RequestParameters requestParams = (RequestParameters) requestParameters;
        
        try {
        	AccountingBLManager accountingBLManager = new AccountingBLManager(ratingAppContext);
            responseObject = accountingBLManager.doAccounting(requestParams);
            
            if (responseObject == null){
            	responseObject = new ResponseObject();
            	responseObject.setResponseCode(IBaseConstant.RATING_ERROR);
                responseObject.setResponseMessage(IBaseConstant.RATING_ERROR_MSG);
            }
            
        } catch (MandatoryParameterMissingException e) {
            responseObject.setResponseCode(IBaseConstant.MANDATORY_PARAMETER_MISSING_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        } catch (ParseException e) {
            responseObject.setResponseCode(IBaseConstant.INVALID_INPUT_DATA_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        } catch (RateNotFoundException e) {
            responseObject.setResponseCode(IBaseConstant.NO_RATE_DEFINED_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            responseObject.setResponseCode(IBaseConstant.GENERAL_ERROR);
            responseObject.setResponseMessage(e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        
        Logger.debug(MODULE, "Processed Accounting Request in " + (endTime - startTime) + " ms");
        Logger.debug(MODULE, responseObject.toString());

        return responseObject;
    }
}
