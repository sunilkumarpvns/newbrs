package com.elitecore.nvsmx.system.view;

import com.elitecore.nvsmx.system.components.HrefLabel;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


 /***
 * custom tag hreflabel for href support for label tag
 */

public class HrefLabelTag extends LabelTag {

    protected String url ;
    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req,
                             HttpServletResponse res) {
        return new HrefLabel(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        HrefLabel label = (HrefLabel) component;
        label.setUrl(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
