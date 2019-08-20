package com.elitecore.nvsmx.system.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import com.elitecore.nvsmx.system.components.DataTableColumn;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * @author kirpalsinh.raj
 *
 */
public class DataTableColumnTag extends AbstractUITag {
	private static final long serialVersionUID = 1L;
	
	private String style;
	private String cssClass;
	private String id;
	private String title;
	private String width;
	private String beanProperty;
	private String hrefurl;
	private String sortable;
	private String icon;
	private String tdStyle;
	private String tdCssClass;
	private String hiddenElement;
	private String type;
    private String format;
    private String disableWhen;
    private String renderFunction;
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
	    return new DataTableColumn(stack, request, response);
	}
	
	@Override
	protected void populateParams() {
	    super.populateParams();
	    
	    DataTableColumn dataTableColumn = (DataTableColumn) component;
	    dataTableColumn.setCssClass(cssClass);
	    dataTableColumn.setTitle(title);
	    dataTableColumn.setWidth(width);
	    dataTableColumn.setId(id);
	    dataTableColumn.setStyle(style);
	    dataTableColumn.setBeanProperty(beanProperty);
	    dataTableColumn.setHrefurl(hrefurl);
	    dataTableColumn.setSortable(sortable);
	    dataTableColumn.setIcon(icon);
	    dataTableColumn.setTdStyle(tdStyle);
	    dataTableColumn.setTdCssClass(tdCssClass);
	    dataTableColumn.setHiddenElement(hiddenElement);
	    dataTableColumn.setType(type);
	    dataTableColumn.setFormat(format);
	    dataTableColumn.setDisableWhen(disableWhen);
	    dataTableColumn.setRenderFunction(renderFunction);
	}
	
	public String getBeanProperty() {
	    return beanProperty;
	}

	public void setBeanProperty(String beanProperty) {
	    this.beanProperty = beanProperty;
	}

	public String getStyle() {
	    return style;
	}
	
	public void setStyle(String style) {
	    this.style = style;
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

	public String getCssClass() {
	    return cssClass;
	}

	public void setCssClass(String cssClass) {
	    this.cssClass = cssClass;
	}
	
	public String getTitle() {
	    return title;
	}

	public void setTitle(String title) {
	    this.title = title;
	}

	public String getHrefurl() {
	    return hrefurl;
	}

	public void setHrefurl(String hrefurl) {
	    this.hrefurl = hrefurl;
	}

	public String getSortable() {
	    return sortable;
	}

	public void setSortable(String sortable) {
	    this.sortable = sortable;
	}

	public String getIcon() {
	    return icon;
	}

	public void setIcon(String icon) {
	    this.icon = icon;
	}

	public String getTdStyle() {
	    return tdStyle;
	}

	public void setTdStyle(String tdStyle) {
	    this.tdStyle = tdStyle;
	}

	public String getTdCssClass() {
	    return tdCssClass;
	}

	public void setTdCssClass(String tdCssClass) {
	    this.tdCssClass = tdCssClass;
	}

	public String getHiddenElement() {
	    return hiddenElement;
	}

	public void setHiddenElement(String hiddenElement) {
	    this.hiddenElement = hiddenElement;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Format parameter is mandatory for defining date format. 
	 * If the value of date format is not specified, it will take empty string as date format
	 * @return
	 */
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	/*
	 * disableWhen attribute will be use when there is a need to disable particular columns values/element based on the
	 * value of some specific member of the bean class
	 * i.e disable edit/delete button the search screen when the values of packageMode is LIVE2
	 * then the usage of disableWhen will be like disableWhen="packageMode=LIVE2"
	 * */
	public String getDisableWhen() {
		return disableWhen;
	}

	public void setDisableWhen(String disableWhen) {
		this.disableWhen = disableWhen;
	}

	public String getRenderFunction() {
		return renderFunction;
	}

	public void setRenderFunction(String renderFunction) {
		this.renderFunction = renderFunction;
	}
}
