package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class ViewCDRInfoForm extends BaseWebForm{
    
    private static final long serialVersionUID = 1L;
    
    private String checkAction;
    private String cdrId;
    private Long serverId;
        
    
    
    public Long getServerId( ) {
        return serverId;
    }



    
    public void setServerId( Long serverId ) {
        this.serverId = serverId;
    }



    public String getCdrId( ) {
        return cdrId;
    }


    
    public void setCdrId( String cdrId ) {
        this.cdrId = cdrId;
    }


    public String getCheckAction( ) {
        return checkAction;
    }

    
    public void setCheckAction( String checkAction ) {
        this.checkAction = checkAction;
    }
    

}
