package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;

import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class AddRadiusPolicyRejectItemForm extends BaseWebForm{

	private String strAttributeId;
	private String strOperatorId;
	private String strOperatorName;	
	private String strParameterValue;
    private String strParameterDisplayValue;
	private String strStatus;
	private String strAction;
	private int intItemIndex;	
	private String rejectItem;
	private String strOperator;
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
	
	public String getOperatorId(){
		return strOperatorId;
	}
	public void setOperatorId(String strOperatorId){
		this.strOperatorId=strOperatorId;
	}

	public String getOperatorName(){
		return strOperatorName;
	}
	public void setOperatorName(String strOperatorName){
		this.strOperatorName=strOperatorName;
	}

	public String getParameterValue(){
		return strParameterValue;
	}
	public void setParameterValue(String strParameterValue){
		this.strParameterValue=strParameterValue;
	}

	public String getStatus(){
		return strStatus;
	}
	public void setStatus(String strStatus){
		this.strStatus=strStatus;
	}

	public String getRejectAction(){
		return strAction;
	}
	public void setRejectAction(String strAction){
		this.strAction=strAction;
	}

	public int getItemIndex(){
		return intItemIndex;
	}
	public void setItemIndex(int intItemIndex){
		this.intItemIndex=intItemIndex;
	}

	public IDictionaryParameterDetailData getDictionaryParameterDetailData(){
		return dictionaryParameterDetailData;
	}
	public void setDictionaryParameterDetailData(IDictionaryParameterDetailData dictionaryParameterDetailData){
		this.dictionaryParameterDetailData=dictionaryParameterDetailData;
	}

	public String getRejectItem() {
		return rejectItem;
	}

	public void setRejectItem(String rejectItem) {
		this.rejectItem = rejectItem;
	}

	public String getOperator() {
		return strOperator;
	}

	public void setOperator(String operator) {
		this.strOperator = operator;
	}

}
