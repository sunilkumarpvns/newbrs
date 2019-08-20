package com.elitecore.nvsmx.system.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ClosingUIBean;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;

public class DataTable extends ClosingUIBean {

    public static final String OPEN_TAMPLATE = "datatable";
    public static final String CLOSE_TEMPLATE = "datatable-close";

    private String cssClass;
    private String style;
    private String id;
    private String width;
    private String beanType;
    private String dataListName;
    private int pages;
    private int rows;
    private String showPagination;
    private String actionUrl;
    private String subTableUrl;
    private String showInfo;
    private String list;
    private String caption;
    private String captionStyle;
    private String captionCssClass;
    private String showFilter;
    private String emptyTable;
    
    public DataTable(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
	super(stack, request, response);
    }

    @Override
    protected void evaluateExtraParams() {
	super.evaluateExtraParams();

	if (Strings.isNullOrBlank(cssClass) == false) {
	    addParameter("cssClass", findValue(cssClass, String.class));
	}

	if (Strings.isNullOrBlank(id) == false) {
	    addParameter("id", findValue(id, String.class));
	}

	if (Strings.isNullOrBlank(width) == false) {
	    addParameter("width", findValue(width, String.class));
	}

	if (Strings.isNullOrBlank(style) == false) {
	    addParameter("style", findValue(style, String.class));
	}

	if (Strings.isNullOrBlank(beanType) == false) {
	    addParameter("beanType", findValue(beanType, String.class));
	}
	
	if (Strings.isNullOrBlank(dataListName) == false) {
	    addParameter("dataListName", findValue(dataListName, String.class));
	}
	
	if (Strings.isNullOrBlank(showPagination) == false) {
	    addParameter("showPagination", findValue(showPagination, String.class));
	}

	if (Strings.isNullOrBlank(actionUrl) == false) {
	    addParameter("actionUrl", findValue(actionUrl, String.class));
	}
	
	if (Strings.isNullOrBlank(getSubTableUrl()) == false) {
	    addParameter("subTableUrl", findValue(getSubTableUrl(), String.class));
	}
	
	if(Strings.isNullOrBlank(showInfo) == false) {
	    addParameter("showInfo", findValue(showInfo, String.class));
	}
	
	if(Strings.isNullOrBlank(list) == false) {
	    addParameter("list", findValue(list, String.class));
	}
	
	if(Strings.isNullOrBlank(caption) == false){
	    addParameter("caption", findValue(caption, String.class));
	}
	
	if(Strings.isNullOrBlank(captionStyle) == false){
	    addParameter("captionStyle", findValue(captionStyle, String.class));
	}
		
	if(Strings.isNullOrBlank(captionCssClass) == false){
	    addParameter("captionCssClass", findValue(captionCssClass, String.class));
	}
    if(Strings.isNullOrBlank(showFilter)==false){
       addParameter("showFilter", findValue(showFilter, String.class));
        }

    if(Strings.isNullOrEmpty(emptyTable) == false){
        addParameter("emptyTable", findValue(emptyTable, String.class));
    }
	
	addParameter("pages", pages);
	addParameter("rows", rows);
    }

    @Override
    public String getDefaultOpenTemplate() {
	return OPEN_TAMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
	return CLOSE_TEMPLATE;
    }

    public int getPages() {
	return pages;
    }

    public void setPages(int pages) {
	this.pages = pages;
    }

    public int getRows() {
	return rows;
    }

    public void setRows(int rows) {
	this.rows = rows;
    }

    public String getBeanType() {
	return beanType;
    }

    public void setBeanType(String beanType) {
	this.beanType = beanType;
    }

    public String getStyle() {
	return style;
    }

    public void setStyle(String style) {
	this.style = style;
    }

    public String getCssClass() {
	return cssClass;
    }

    public void setCssClass(String cssClass) {
	this.cssClass = cssClass;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getWidth() {
	return width;
    }

    public void setWidth(String width) {
	this.width = width;
    }

    public String getShowPagination() {
	return showPagination;
    }

    public void setShowPagination(String showPagination) {
	this.showPagination = showPagination;
    }

    public String getActionUrl() {
	return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
	this.actionUrl = actionUrl;
    }

    public String getSubTableUrl() {
	return subTableUrl;
    }

    public void setSubTableUrl(String subTableUrl) {
	this.subTableUrl = subTableUrl;
    }

    public String getShowInfo() {
	return showInfo;
    }

    public void setShowInfo(String showInfo) {
	this.showInfo = showInfo;
    }

    public String getList() {
	return list;
    }

    public void setList(String list) {
	this.list = list;
    }

    public String getCaption() {
	return caption;
    }

    public void setCaption(String caption) {
	this.caption = caption;
    }

    public String getCaptionStyle() {
	return captionStyle;
    }

    public void setCaptionStyle(String captionStyle) {
	this.captionStyle = captionStyle;
    }

    public String getCaptionCssClass() {
	return captionCssClass;
    }

    public void setCaptionCssClass(String captionCssClass) {
	this.captionCssClass = captionCssClass;
    }

	public String getDataListName() {
		return dataListName;
	}

	public void setDataListName(String dataListName) {
		this.dataListName = dataListName;
	}

    public String getShowFilter() {
        return showFilter;
    }

    public void setShowFilter(String showFilter) {
        this.showFilter = showFilter;
    }

    public String getEmptyTable() { return emptyTable; }

    public void setEmptyTable(String emptyTable) { this.emptyTable = emptyTable; }
}
