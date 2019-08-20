package com.elitecore.nvsmx.system.components;

import com.elitecore.commons.base.Strings;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/***
 * This class extends the label tag functionality & provide support for url support for href
 */
public class HrefLabel extends Label {


    protected String url;

    public HrefLabel(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if(Strings.isNullOrBlank(url) == false){
            addParameter("url", findValue(url, String.class));
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected String getDefaultTemplate() {
        return "hreflabel";
    }
}
