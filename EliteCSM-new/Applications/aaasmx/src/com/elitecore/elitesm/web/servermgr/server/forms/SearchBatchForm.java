package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;


public class SearchBatchForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    private String batchId;
    private String dateFrom;
    private String dateTo;
    private String status;
    private String reason;
    private String userName;
    private String select;
    private String checkAction;
    private String serverId;
    
    
    public String getServerId( ) {
        return serverId;
    }

    
    public void setServerId( String serverId ) {
        this.serverId = serverId;
    }

    public String getBatchId( ) {
        return batchId;
    }
    
    public void setBatchId( String batchId ) {
        this.batchId = batchId;
    }
    
    public String getDateFrom( ) {
        return dateFrom;
    }
    
    public void setDateFrom( String dateFrom ) {
        this.dateFrom = dateFrom;
    }
    
    public String getDateTo( ) {
        return dateTo;
    }
    
    public void setDateTo( String dateTo ) {
        this.dateTo = dateTo;
    }
    
    public String getReason( ) {
        return reason;
    }
    
    public void setReason( String reason ) {
        this.reason = reason;
    }
    
    public String getStatus( ) {
        return status;
    }
    
    public void setStatus( String status ) {
        this.status = status;
    }
    
    public String getUserName( ) {
        return userName;
    }
    
    public void setUserName( String userName ) {
        this.userName = userName;
    }

    
    public String getSelect( ) {
        return select;
    }

    
    public void setSelect( String select ) {
        this.select = select;
    }

    
    public String getCheckAction( ) {
        return checkAction;
    }

    
    public void setCheckAction( String checkAction ) {
        this.checkAction = checkAction;
    }
    
    
}
