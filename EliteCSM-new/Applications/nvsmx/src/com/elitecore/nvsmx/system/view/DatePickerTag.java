package com.elitecore.nvsmx.system.view;

import com.elitecore.nvsmx.system.components.DatePicker;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DatePickerTag extends com.jgeppert.struts2.jquery.views.jsp.ui.DatePickerTag {

    public Component getBean(ValueStack stack, HttpServletRequest req,
                             HttpServletResponse res) {
        return new DatePicker(stack, req, res);
    }
    @Override
    protected void populateParams() {
        super.populateParams();
        DatePicker form = ((DatePicker)component);
        form.setTheme("bootstrap");
    }
}
