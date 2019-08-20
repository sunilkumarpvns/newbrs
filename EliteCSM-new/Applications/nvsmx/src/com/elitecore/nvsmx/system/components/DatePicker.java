package com.elitecore.nvsmx.system.components;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DatePicker extends com.jgeppert.struts2.jquery.components.DatePicker {

    public DatePicker(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    @Override
    public String getTheme() {
        return "bootstrap";
    }


}
