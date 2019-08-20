/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 */
package com.elitecore.classicrating.api.ejb;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.elitecore.classicrating.api.EliteClassicRatingEngine;
import com.elitecore.classicrating.api.IEliteClassicRatingEngine;
import com.elitecore.classicrating.api.IRequestParameters;
import com.elitecore.classicrating.api.IResponseObject;
import com.elitecore.classicrating.api.ejb.interfaces.IEliteClassicRatingRemote;
import com.elitecore.classicrating.base.IRatingAppContext;
import com.elitecore.classicrating.commons.response.ResponseObject;
import com.elitecore.classicrating.datasource.EliteClassicRatingAppContext;


/**
 *
 * @author sheetalsoni
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EliteClassicRatingApiSessionBean extends EliteClassicRatingBase implements IEliteClassicRatingRemote {
    
    // Add business logic below. (Right-click in editor and choose
    // "EJB Methods > Add Business Method" or "Web Service > Add Operation")
 
    private static final String MODULE ="ELITE CLASSIC RATING API SESSION BEAN";
    
    IRatingAppContext ratingApplicationContext = new EliteClassicRatingAppContext( new DataSourceProvider());
    public IEliteClassicRatingEngine eliteClassicRatingEngine = new EliteClassicRatingEngine(ratingApplicationContext);
    

    /**
     * <P> This method performs Accounting Request </P>
     @method accountingRequest
     *@parameters RequestParameters
     * @return responseObject 
     */
    public IResponseObject accountingRequest(IRequestParameters requestParameters){
        //Logger.logInfo("MODULE","In Torres Rating Api Bean ");
        IResponseObject responseObject = null;
        
        try{
            if(requestParameters != null){

            	responseObject = eliteClassicRatingEngine.accountingRequest(requestParameters);
            }
        }catch(Exception e){
/*           Logger.logTrace(MODULE,e);
           Logger.logError(MODULE, "Exception caught while performing Accounting Request : ");
*/           responseObject = new ResponseObject();
	   responseObject.setResponseMessage(e.getMessage());
	   responseObject.setResponseCode(1);
        }
    return responseObject;
    }
    
    /**
     <P> This method performs Authorization Request </P>
     @method AuthorizationRequest
     @parameters RequestParameters
     @return ReponseObject*/
    
    public IResponseObject authorizationRequest(IRequestParameters requestParameters){
        ////Logger.logInfo("MODULE","In Torres Rating Api Bean ");
        IResponseObject responseObject = null;
        try{
            if(requestParameters != null){
                System.out.println("In Auth request : " + MODULE);
            	responseObject = eliteClassicRatingEngine.authorizationRequest(requestParameters);
            	System.out.println("Auth Request Completed : " + MODULE);
            }
         }catch(Exception e){
/*           Logger.logTrace(MODULE,e);
           Logger.logError(MODULE, "Exception caught while performing Authorization Request : ");
*/           responseObject = new ResponseObject();
	   responseObject.setResponseMessage(e.getMessage());
	   responseObject.setResponseCode(1);
        }
    return responseObject;
    }
    
}
