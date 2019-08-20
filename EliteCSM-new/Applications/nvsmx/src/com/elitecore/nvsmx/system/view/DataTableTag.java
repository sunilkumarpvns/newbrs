package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import com.elitecore.nvsmx.system.components.DataTable;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * @author kirpalsinh.raj
 * 
 */
public class DataTableTag extends AbstractClosingTag {
    private static final long serialVersionUID = 1L;

    private String style;
    private String cssClass;
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
    
    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
	return new DataTable(stack, request, response);
    }

    @Override
    protected void populateParams() {
	super.populateParams();

	DataTable dataTable = (DataTable) component;
	dataTable.setCssClass(cssClass);
	dataTable.setWidth(width);
	dataTable.setId(id);
	dataTable.setStyle(style);
	dataTable.setBeanType(beanType);
	dataTable.setDataListName(dataListName);
	dataTable.setRows(rows);
	dataTable.setPages(pages);
	dataTable.setShowPagination(showPagination);
	dataTable.setActionUrl(actionUrl);
	dataTable.setSubTableUrl(subTableUrl);
	dataTable.setShowInfo(showInfo);
	dataTable.setList(list);
	dataTable.setCaption(caption);
	dataTable.setCaptionStyle(captionStyle);
	dataTable.setCaptionCssClass(captionCssClass);
    dataTable.setShowFilter(showFilter);
    dataTable.setEmptyTable(emptyTable);
    }

    public int getRows() {
	return rows;
    }

    public void setRows(int rows) {
	this.rows = rows;
    }

    public int getPages() {
	return pages;
    }

    public void setPages(int totalPages) {
	this.pages = totalPages;
    }

    public String getBeanType() {
	return beanType;
    }

    public void setBeanType(String beanType) {
	this.beanType = beanType;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getStyle() {
	return style;
    }

    public void setStyle(String style) {
	this.style = style;
    }

    public String getWidth() {
	return width;
    }

    public void setWidth(String width) {
	this.width = width;
    }

    public String getCssClass() {
	return cssClass;
    }

    public void setCssClass(String cssClass) {
	this.cssClass = cssClass;
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

	public void setDataListName(String beanId) {
		this.dataListName = beanId;
	}

    public String getShowFilter() { return showFilter;  }

    public void setShowFilter(String showFilter) { this.showFilter = showFilter; }

    public String getEmptyTable() { return emptyTable; }

    public void setEmptyTable(String emptyTable) { this.emptyTable = emptyTable; }
}
