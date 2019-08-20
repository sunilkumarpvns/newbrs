package com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms;

import java.util.List;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;


public class SearchDictionaryPopUpForm extends BaseDictionaryForm{
        
        List dictionaryListInCombo;
        List dictionaryListByCriteria;
        String searchByName;
        String dictionaryId;
        String status;
        String action;
        String fieldValue;
        

        public String getFieldValue() {
                return fieldValue;
        }

        public void setFieldValue(String fieldValue) {
                this.fieldValue = fieldValue;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public String getDictionaryId() {
                return dictionaryId;
        }

        public void setDictionaryId(String dictionaryId) {
                this.dictionaryId = dictionaryId;
        }

        public List getDictionaryListInCombo() {
                return dictionaryListInCombo;
        }

        public void setDictionaryListInCombo(List dictionaryListInCombo) {
                this.dictionaryListInCombo = dictionaryListInCombo;
        }

        public String getSearchByName() {
                return searchByName;
        }

        public void setSearchByName(String searchByName) {
                this.searchByName = searchByName;
        }

        public List getDictionaryListByCriteria() {
                return dictionaryListByCriteria;
        }

        public void setDictionaryListByCriteria(List dictionaryListByCriteria) {
                this.dictionaryListByCriteria = dictionaryListByCriteria;
        }

        public String getAction() {
                return action;
        }

        public void setAction(String action) {
                this.action = action;
        }

}

