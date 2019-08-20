package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;

import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

//import com.elitecore.radius.manager.policies.radiuspolicy.data.*;
//import com.elitecore.radius.manager.system.standardmaster.data.RadiusStandardMasterBean;
//import com.elitecore.radius.manager.system.user.data.RadiusStaffBean;
//import com.elitecore.radius.web.base.forms.BaseWebForm;

public class UpdateRadiusPolicyReplaceItemForm extends BaseWebForm {
    
    private String                         strParameterName;
    private int                            strParameterId;
    private String                         strOperatorId;
    private String                         strOperatorName;
    private String                         strParameterValue;
    private String                         strParameterDisplayValue;
    private String                         strStatus;
    private String                           radiusPolicyId;
    private String                         action;
    private int                            intItemIndex;
    private IDictionaryParameterDetailData dictionaryParameterDetailData;
    
    public String getParameterDisplayValue( ) {
        return strParameterDisplayValue;
    }
    
    public void setParameterDisplayValue( String strParameterDisplayValue ) {
        this.strParameterDisplayValue = strParameterDisplayValue;
    }
    
    public String getRadiusPolicyId( ) {
        return radiusPolicyId;
    }
    
    public void setRadiusPolicyId( String radiusPolicyId ) {
        this.radiusPolicyId = radiusPolicyId;
    }
    
    public ActionErrors validate( ActionMapping mapping ,
                                  HttpServletRequest request ) {
        ActionErrors errors = super.validate(mapping, request);
        return errors;
    }
    
    public String getAction( ) {
        return action;
    }
    
    public void setAction( String action ) {
        this.action = action;
    }
    
    public int getItemIndex( ) {
        return intItemIndex;
    }
    
    public void setItemIndex( int intItemIndex ) {
        this.intItemIndex = intItemIndex;
    }
    
    public String getParameterName( ) {
        return strParameterName;
    }
    
    public void setParameterName( String strParameterName ) {
        this.strParameterName = strParameterName;
    }
    
    public int getParameterId( ) {
        return strParameterId;
    }
    
    public void setParameterId( int strParameterId ) {
        this.strParameterId = strParameterId;
    }
    
    public String getOperatorId( ) {
        return strOperatorId;
    }
    
    public void setOperatorId( String strOperatorId ) {
        this.strOperatorId = strOperatorId;
    }
    
    public String getOperatorName( ) {
        return strOperatorName;
    }
    
    public void setOperatorName( String strOperatorName ) {
        this.strOperatorName = strOperatorName;
    }
    
    public String getParameterValue( ) {
        return strParameterValue;
    }
    
    public void setParameterValue( String strParameterValue ) {
        this.strParameterValue = strParameterValue;
    }
    
    public String getStatus( ) {
        return strStatus;
    }
    
    public void setStatus( String strStatus ) {
        this.strStatus = strStatus;
    }
    
    public IDictionaryParameterDetailData getDictionaryParameterDetailData( ) {
        return dictionaryParameterDetailData;
    }
    
    public void setDictionaryParameterDetailData( IDictionaryParameterDetailData dictionaryParameterDetailData ) {
        this.dictionaryParameterDetailData = dictionaryParameterDetailData;
    }
    
}
