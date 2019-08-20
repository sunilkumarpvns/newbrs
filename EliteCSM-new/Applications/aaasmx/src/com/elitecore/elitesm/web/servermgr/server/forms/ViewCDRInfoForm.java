package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class ViewCDRInfoForm extends BaseWebForm{
    
    private static final long serialVersionUID = 1L;
    
    private String checkAction;
    private String cdrId;
    private String serverId;
        
    
    
    public String getServerId( ) {
        return serverId;
    }



    
    public void setServerId( String serverId ) {
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
