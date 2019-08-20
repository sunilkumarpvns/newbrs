package com.elitecore.elitesm.datamanager.core.util;

import java.util.Collection;

import java.util.List;

public class PageList {
    
    private long currentPage;
    private long totalPages;
    private long totalItems;
    private Collection colListData;
    private List lstListData;
    
    public PageList( Collection listData, long currentPage, long totalPages, long totalItems ) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
        this.colListData = listData;
    }
    public PageList(List listData, long curreentPage, long totalPages, long totalItems){
    	this.currentPage = curreentPage;
    	this.totalPages = totalPages;
    	this.totalItems = totalItems;
    	this.lstListData = listData;
    }
    
    public long getCurrentPage() {
        return currentPage;
    }
    
    public Collection getCollectionData() {
        return colListData;
    }
    public List getListData(){
    	return lstListData;
    }
    
    public long getTotalItems() {
        return totalItems;
    }
    
    public long getTotalPages() {
        return totalPages;
    }
    
}
