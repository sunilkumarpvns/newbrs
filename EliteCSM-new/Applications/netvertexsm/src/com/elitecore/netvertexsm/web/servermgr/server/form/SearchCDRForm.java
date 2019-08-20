package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class SearchCDRForm extends BaseWebForm{
private static final long serialVersionUID = 1L;
    
    private Long serverId;
    private String searchCriteria;
    private String reason;
    private String deviceId;
    private String fileName;
    private String status;
    private String distributionStatus;
    private String dateFrom;
    private String dateTo;
    private String hideCheckedOut;
    private String searchName;
    
    private String fieldName;
    private String operator;
    private String value;
    private String logicalConnector;
    private String checkAction;
    private int itemIndex;
    private List cdrDataList;
    
    
    
    
    public List getCdrDataList( ) {
        return cdrDataList;
    }



    
    public void setCdrDataList( List cdrDataList ) {
        this.cdrDataList = cdrDataList;
    }



    public int getItemIndex( ) {
        return itemIndex;
    }


    
    public void setItemIndex( int itemIndex ) {
        this.itemIndex = itemIndex;
    }


    public String getDeviceId( ) {
        return deviceId;
    }
    
    
    public String getCheckAction( ) {
        return checkAction;
    }

    
    public void setCheckAction( String checkAction ) {
        this.checkAction = checkAction;
    }

    public void setDeviceId( String deviceId ) {
        this.deviceId = deviceId;
    }
    
    public String getFieldName( ) {
        return fieldName;
    }
    
    public void setFieldName( String fieldName ) {
        this.fieldName = fieldName;
    }
    
    
    public String getDistributionStatus( ) {
        return distributionStatus;
    }

    
    public void setDistributionStatus( String distributionStatus ) {
        this.distributionStatus = distributionStatus;
    }

    
    public String getFileName( ) {
        return fileName;
    }

    
    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }
    
    public String getLogicalConnector( ) {
        return logicalConnector;
    }

    
    public void setLogicalConnector( String logicalConnector ) {
        this.logicalConnector = logicalConnector;
    }

    
    public String getOperator( ) {
        return operator;
    }

    
    public void setOperator( String operator ) {
        this.operator = operator;
    }

    
    public String getSearchCriteria( ) {
        return searchCriteria;
    }

    
    public void setSearchCriteria( String searchCriteria ) {
        this.searchCriteria = searchCriteria;
    }

    
    public String getSearchName( ) {
        return searchName;
    }

    
    public void setSearchName( String searchName ) {
        this.searchName = searchName;
    }

    
    public String getValue( ) {
        return value;
    }

    
    public void setValue( String value ) {
        this.value = value;
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

    
    public String getHideCheckedOut( ) {
        return hideCheckedOut;
    }

    
    public void setHideCheckedOut( String hideCheckedOut ) {
        this.hideCheckedOut = hideCheckedOut;
    }




    
    public Long getServerId( ) {
        return serverId;
    }




    
    public void setServerId( Long serverId ) {
        this.serverId = serverId;
    }

}
