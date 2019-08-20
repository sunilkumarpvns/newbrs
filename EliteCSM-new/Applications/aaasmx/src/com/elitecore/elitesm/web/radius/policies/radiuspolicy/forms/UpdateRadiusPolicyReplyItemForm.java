package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class UpdateRadiusPolicyReplyItemForm extends BaseWebForm {
    
	private String 						   strAttributeId;
    private String                         strParameterValue;
    private String                           radiusPolicyId;
    private String                         action;
    private String						   replyItem;
    private String                         strOperator;
    private String                         strReqRes;
    private IDictionaryParameterDetailData dictionaryParameterDetailData;
   
    public String getStrAttributeId() {
		return strAttributeId;
	}

	public void setStrAttributeId(String strAttributeId) {
		this.strAttributeId = strAttributeId;
	}

	public String getOperator() {
		return strOperator;
	}

	public void setOperator(String operator) {
		this.strOperator = operator;
	}

	public String getReqRes() {
		return strReqRes;
	}

	public void setReqRes(String reqRes) {
		this.strReqRes = reqRes;
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

	public String getReplyItem() {
		return replyItem;
	}

	public void setReplyItem(String replyItem) {
		this.replyItem = replyItem;
	}
    
}
