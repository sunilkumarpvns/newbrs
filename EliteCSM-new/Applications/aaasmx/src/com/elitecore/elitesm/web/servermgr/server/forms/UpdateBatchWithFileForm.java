package com.elitecore.elitesm.web.servermgr.server.forms;

import org.apache.struts.upload.FormFile;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class UpdateBatchWithFileForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;

    private String checkAction;
    private String batchId;
    private FormFile batchFile;
    private String serverId;
    
    
    public String getServerId( ) {
        return serverId;
    }

    
    public void setServerId( String serverId ) {
        this.serverId = serverId;
    }

    public FormFile getBatchFile( ) {
        return batchFile;
    }

    public void setBatchFile( FormFile batchFile ) {
        this.batchFile = batchFile;
    }

    public String getCheckAction( ) {
        return checkAction;
    }
    
    public void setCheckAction( String checkAction ) {
        this.checkAction = checkAction;
    }

    
    public String getBatchId( ) {
        return batchId;
    }

    
    public void setBatchId( String batchId ) {
        this.batchId = batchId;
    }
}
