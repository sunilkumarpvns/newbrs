package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class SearchFileSummaryForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    
    private String groupField;
    private String checkAction;

    
    public String getCheckAction( ) {
        return checkAction;
    }

    
    public void setCheckAction( String checkAction ) {
        this.checkAction = checkAction;
    }

    public String getGroupField( ) {
        return groupField;
    }
    
    public void setGroupField( String groupField ) {
        this.groupField = groupField;
    }

}