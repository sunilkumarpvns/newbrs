/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.elitecore.classicrating.api.ejb.interfaces;

import javax.ejb.Remote;

import com.elitecore.classicrating.api.IRequestParameters;
import com.elitecore.classicrating.api.IResponseObject;
/**
 *
 * @author sheetalsoni
 */
@Remote
public interface IEliteClassicRatingRemote {
  
    public IResponseObject authorizationRequest(IRequestParameters requestParameters);
    
    public IResponseObject accountingRequest (IRequestParameters requestParameters);
}  
