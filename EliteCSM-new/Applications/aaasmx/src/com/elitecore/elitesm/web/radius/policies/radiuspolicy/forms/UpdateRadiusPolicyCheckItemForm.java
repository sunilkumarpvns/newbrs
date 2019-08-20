package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

//import com.elitecore.radius.manager.policies.radiuspolicy.data.*;
//import com.elitecore.radius.manager.system.standardmaster.data.RadiusStandardMasterBean;
//import com.elitecore.radius.manager.system.user.data.RadiusStaffBean;
//import com.elitecore.radius.web.base.forms.BaseWebForm;

public class UpdateRadiusPolicyCheckItemForm extends BaseWebForm {
    
	private String 						   strAttributeId;
    private String                         strParameterValue;
    private String                         strParameterDisplayValue;
    private String                           radiusPolicyId;
    private String                         action;
    private String 						   checkItem;
    private String 						   strOperator;
    private IDictionaryParameterDetailData dictionaryParameterDetailData;
    
    public String getStrAttributeId() {
		return strAttributeId;
	}

	public void setStrAttributeId(String strAttributeId) {
		this.strAttributeId = strAttributeId;
	}

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
    
    public String getParameterValue( ) {
        return strParameterValue;
    }
    
    public void setParameterValue( String strParameterValue ) {
        this.strParameterValue = strParameterValue;
    }

    public IDictionaryParameterDetailData getDictionaryParameterDetailData( ) {
        return dictionaryParameterDetailData;
    }
    
    public void setDictionaryParameterDetailData( IDictionaryParameterDetailData dictionaryParameterDetailData ) {
        this.dictionaryParameterDetailData = dictionaryParameterDetailData;
    }

	public String getCheckItem() {
		return checkItem;
	}

	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}

	public String getOperator() {
		return strOperator;
	}

	public void setOperator(String strOperator) {
		this.strOperator = strOperator;
	}
    
}
