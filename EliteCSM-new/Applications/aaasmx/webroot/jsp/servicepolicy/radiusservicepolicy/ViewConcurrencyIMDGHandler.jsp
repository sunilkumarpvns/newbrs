<%--
  Created by IntelliJ IDEA.
  User: vijayrajsinh
  Date: 4/26/18
  Time: 4:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/popcalendar.css" >
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@taglib prefix="ec" uri="/elitetags" %>
<html>
<head>
    <%
        String widgetId=request.getParameter("widgetId");
        String isAdditional=request.getParameter("isAdditional");
        String orderNumber=request.getParameter("orderNumber");
        String jsonData=request.getParameter("jsonData");

        if(isAdditional.equalsIgnoreCase("true")){
            widgetId=widgetId+"_additional";
        }else{
            widgetId=widgetId+"_authentication_imdg";
        }
    %>
    <script type="text/javascript">
        var formData = <%=jsonData%>;
        $.each(formData, function(key,value){
            var element = $('#'+key+'<%=widgetId%>');
            if($(element).length > 0 && $(element).is("select")) {
                $(element).val(value);
            }
            if($(element).length > 0 && $(element).attr('class') === 'ruleset'){
                $(element).val(filterRuleset(value));
            }

            if(key == 'isHandlerEnabled'){
                if( value == "true" ){
                    $('#toggle-concurrencyimdghandler_<%=widgetId%>').attr('checked', true);
                    $('#toggle-concurrencyimdghandler_<%=widgetId%>').val('true');
                }else{
                    $('#toggle-concurrencyimdghandler_<%=widgetId%>').attr('checked', false);
                    $('#toggle-concurrencyimdghandler_<%=widgetId%>').val('false');
                    var handlerObject=$('#toggle-concurrencyimdghandler_<%=widgetId%>').closest('table[class^="handler-class"]');
                    $(handlerObject).find('tr').each(function(){
                        $(this).addClass('disable-toggle-class');
                    });
                }
            }

            if( key == 'handlerName'){
                if( value.length > 0 ){
                    $('#concurrencyImdgHandlerName_'+'<%=widgetId%>').attr('size', value.length);
                    $('#concurrencyImdgHandlerName_'+'<%=widgetId%>').val(value);
                    $('#concurrencyIMDGHiddenHandlerName_'+'<%=widgetId%>').val(value);
                }
            }

        });
    </script>
</head>
<body>
<form id="concurrency_imdg_from_<%=widgetId%>" name="concurrency_imdg_from_<%=widgetId%>"  class="form_concurrency_imdg">
    <input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
    <input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>
    <table name="tblmConcurrencyIMDG" width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
        <tr style="cursor: pointer;">
            <td class="sortableClass">
                <table name="tblmConcurrencyIMDG" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
                    <tr>
                        <td align="left" class="tbl-header-bold" valign="top" colspan="2">
                            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                                <tr>
                                    <td width="96%" align="left" class="tbl-header-bold" valign="top">
                                        <input type="text" id="concurrencyImdgHandlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="35" value="<bean:message bundle="servicePolicyProperties" key="radius.imdg.concurrency.handler" />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled"/>
                                        <input type="hidden" id="concurrencyIMDGHiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle="servicePolicyProperties" key="radius.imdg.concurrency.handler" />" />
                                        <input type="hidden" id="concurrencyIMDGHandlerType_<%=widgetId%>" name="handlerType" class="handlerType" value="ConcurrencyHandler" />
                                    </td>
                                    <td width="1%" align="left" class="tbl-header-bold" valign="middle" style="padding-right: 2px;line-height: 9px;">
                                        <div class="switch">
                                            <input id="toggle-concurrencyimdghandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" disabled="disabled"/>
                                            <label for="toggle-concurrencyimdghandler_<%=widgetId%>"></label>
                                        </div>
                                    </td>
                                    <td width="2%" valign="middle" class="tbl-header-bold" align="left" style="padding-right: 10px;" onclick="expandCollapse(this);">
                                        <img alt="Expand" class="expand_class" title="Expand" id="sessionManagerImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <div id="imdgRadiusDiv" class="toggleDivs">
                    <table name="tblmConcurrencyIMDG" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0" class="tblmConcurrencyIMDG">
                        <tr>
                            <td align="left" class="captiontext left-border bottom-border right-border">
                                <table cellspacing="0" cellpadding="0" border="0" width="60%" style="padding-top: 10px;">
                                    <tr>
                                        <td class="tblheader-policy" width="50%">
                                            <bean:message bundle="servicePolicyProperties" key="radius.concurrency.imdg.ruleset" />
                                            <ec:elitehelp  header="radius.concurrency.imdg.ruleset" headerBundle="servicePolicyProperties" text="radius.concurrency.imdg.ruleset" ></ec:elitehelp>
                                        </td>
                                        <td class="tblheader-policy" width="50%">
                                            <bean:message bundle="servicePolicyProperties" key="radius.concurrency.imdg" />
                                            <ec:elitehelp header="radius.concurrency.imdg" headerBundle="servicePolicyProperties" text="radius.concurrency.imdg.handler" ></ec:elitehelp>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="proxy-table-firstcol" width="50%" style="border-right: 1px solid #CCC;">
                                            <input class="ruleset" type="text" name="ruleset" id="ruleset<%=widgetId%>" maxlength="2000" style="width: 99%;" disabled="disabled"/>
                                        </td>
                                        <td class="labeltext right-border tblrows" width="50%">
                                            <select id="imdgFieldName<%=widgetId%>" name="imdgFieldName" style="width:200px;" class="imdgRadiusConClass" disabled="disabled">
                                                <option value="0">--Select--</option>
                                                <logic:iterate id="imdgRadiusField" property="imdgFieldNames" name="updateRadiusServicePolicyForm" type="java.lang.String">
                                                    <option value="<%=imdgRadiusField%>"><%=imdgRadiusField%></option>
                                                </logic:iterate>
                                            </select>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
    </table>
</form>

</body>
</html>
