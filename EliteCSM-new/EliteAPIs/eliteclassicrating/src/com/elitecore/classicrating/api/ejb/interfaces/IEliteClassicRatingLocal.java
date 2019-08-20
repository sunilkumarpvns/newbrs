/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.elitecore.classicrating.api.ejb.interfaces;

import javax.ejb.Local;

import com.elitecore.classicrating.api.IRequestParameters;
import com.elitecore.classicrating.api.IResponseObject;

/**
 *
 * @author sheetalsoni
 */
@Local
public interface IEliteClassicRatingLocal {
    
    public IResponseObject authorizationRequest(IRequestParameters requestParameters);
    
    public IResponseObject accountingRequest (IRequestParameters requestParameters);
}
