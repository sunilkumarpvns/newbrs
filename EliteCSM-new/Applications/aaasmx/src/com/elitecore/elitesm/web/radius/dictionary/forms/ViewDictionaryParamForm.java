package com.elitecore.elitesm.web.radius.dictionary.forms;

import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryData;
import com.elitecore.elitesm.datamanager.radius.dictionary.data.IDictionaryParameterDetailData;
import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class ViewDictionaryParamForm extends BaseDictionaryForm {

	 
    private IDictionaryData dictionaryData;	
    private IDictionaryParameterDetailData dictionaryParamDetailData;
    
    public IDictionaryData getDictionaryData() {
        return dictionaryData;
    }
    public void setDictionaryData(IDictionaryData dictionaryData) {
        this.dictionaryData = dictionaryData;
    }

    public IDictionaryParameterDetailData getDictionaryParamDetailData() {
        return dictionaryParamDetailData;
    }
    public void setDictionaryParamDetailData(IDictionaryParameterDetailData dictionaryParamDetailData) {
        this.dictionaryParamDetailData = dictionaryParamDetailData;
    }

}
