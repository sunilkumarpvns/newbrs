package com.elitecore.netvertexsm.web.servermgr.service.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;


public class ViewLiveServiceRequiredDetailForm extends BaseWebForm{
    private static final long serialVersionUID = 1L;
    private Long serviceId;
    private String startDate;
    private int startHour;
    private int startMinute;
    private String endDate;
    private int endHour;
    private int endMinute;
    private String checkAction;
    private long pageNumber;
    private long totalPages;
    private long totalRecords;
    
    public String getCheckAction( ) {
        return checkAction;
    }

    
    public void setCheckAction( String checkAction ) {
        this.checkAction = checkAction;
    }

    public String getEndDate( ) {
        return endDate;
    }
    
    public void setEndDate( String endDate ) {
        this.endDate = endDate;
    }
    
    public int getEndHour( ) {
        return endHour;
    }
    
    public void setEndHour( int endHour ) {
        this.endHour = endHour;
    }
    
    public int getEndMinute( ) {
        return endMinute;
    }
    
    public void setEndMinute( int endMinute ) {
        this.endMinute = endMinute;
    }
    
    public String getStartDate( ) {
        return startDate;
    }
    
    public void setStartDate( String startDate ) {
        this.startDate = startDate;
    }
    
    public int getStartHour( ) {
        return startHour;
    }
    
    public void setStartHour( int startHour ) {
        this.startHour = startHour;
    }
    
    public int getStartMinute( ) {
        return startMinute;
    }
    
    public void setStartMinute( int startMinute ) {
        this.startMinute = startMinute;
    }


    
    public Long getServiceId( ) {
        return serviceId;
    }


    
    public void setServiceId( Long serviceId ) {
        this.serviceId = serviceId;
    }


    
    public long getPageNumber( ) {
        return pageNumber;
    }


    
    public void setPageNumber( long pageNumber ) {
        this.pageNumber = pageNumber;
    }


    
    public long getTotalPages( ) {
        return totalPages;
    }


    
    public void setTotalPages( long totalPages ) {
        this.totalPages = totalPages;
    }


    
    public long getTotalRecords( ) {
        return totalRecords;
    }


    
    public void setTotalRecords( long totalRecords ) {
        this.totalRecords = totalRecords;
    }
    

}
