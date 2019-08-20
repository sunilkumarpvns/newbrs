package com.elitecore.nvsmx.system.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.UIBean;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;

public class DataTableColumn extends UIBean {

    private static final String TEMPLATE = "datatablecolumn";

    private String cssClass;
    private String style;
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
    
    public DataTableColumn(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
	super(stack, request, response);
    }

    @Override
    protected void evaluateExtraParams() {
		super.evaluateExtraParams();

		if (Strings.isNullOrBlank(title) == false) {
			addParameter("title", findValue(title, String.class));
		}

		if (Strings.isNullOrBlank(cssClass) == false) {
			addParameter("cssClass", findValue(cssClass, String.class));
		}

		if (Strings.isNullOrBlank(style) == false) {
			addParameter("style", findValue(style, String.class));
		}

		if (Strings.isNullOrBlank(id) == false) {
			addParameter("id", findValue(id, String.class));
		}

		if (Strings.isNullOrBlank(width) == false) {
			addParameter("width", findValue(width, String.class));
		}

		if (Strings.isNullOrBlank(beanProperty) == false) {
			addParameter("beanProperty", findValue(beanProperty, String.class));
		}

		if (Strings.isNullOrBlank(hrefurl) == false) {
			addParameter("hrefurl", findValue(hrefurl, String.class));
		}

		if (Strings.isNullOrBlank(sortable) == false) {
			addParameter("sortable", findValue(sortable, String.class));
		}

		if (Strings.isNullOrBlank(icon) == false) {
			addParameter("icon", findValue(icon, String.class));
		}

		if (Strings.isNullOrBlank(tdStyle) == false) {
			addParameter("tdStyle", findValue(tdStyle, String.class));
		}

		if (Strings.isNullOrBlank(tdCssClass) == false) {
			addParameter("tdCssClass", findValue(tdCssClass, String.class));
		}

		if (Strings.isNullOrBlank(hiddenElement) == false) {
			addParameter("hiddenElement", findValue(hiddenElement, String.class));
		}

		if (Strings.isNullOrBlank(type) == false) {
			addParameter("type", findValue(type, String.class));
		}

		if (Strings.isNullOrBlank(format) == false) {
			addParameter("format", findValue(format, String.class));
		}

		if (Strings.isNullOrBlank(disableWhen) == false) {
			addParameter("disableWhen", findValue(disableWhen, String.class));
		}
		if (Strings.isNullOrBlank(renderFunction) == false) {
			addParameter("renderFunction", findValue(renderFunction, String.class));

		}
	}

    @Override
    protected String getDefaultTemplate() {
	return TEMPLATE;
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

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

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
