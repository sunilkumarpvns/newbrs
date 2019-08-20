<%--
  Created by IntelliJ IDEA.
  User: vijayrajsinh
  Date: 7/27/18
  Time: 1:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/popcalendar.css" >
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@ taglib prefix="ec" uri="/elitetags" %>

<html>
<head>
    <%
        String jsonData=request.getParameter("jsonData");
        String widgetId=request.getParameter("widgetId");
        String isAdditional=request.getParameter("isAdditional");
        String orderNumber=request.getParameter("orderNumber");

        if(isAdditional.equalsIgnoreCase("true")){
            widgetId=widgetId+"_stateful_broadcast_proxy_additional";
        }else{
            widgetId=widgetId+"_stateful_broadcast_proxy_authentication";
        }
    %>
    <script>
        var formData =<%=jsonData%>;
        $.each(formData, function(key,value){
            if(key == 'statefulProxyBroadcastCommunicationList') {
                $.each(value, function (jsonKey, jsonValue) {
                    addStatefulProxyPolicyRow('statefulProxyBroadcastCommunicationTemplate_<%=widgetId%>', 'statefulProxyBroadcastCommunicationTbl_<%=widgetId%>');
                    var findTableObj = $("#statefulProxyBroadcastCommunicationTbl_<%=widgetId%>" + " tr:last");
                    //set ruleSet
                    var rulesetrow = $(findTableObj).find("input[name='ruleset']");
                    $(rulesetrow).val(jsonValue.ruleset);

                    //set serverGroupName
                    var serverGroupNameRow = $(findTableObj).find("select[name='serverGroupName']");
                    $(serverGroupNameRow).val(jsonValue.serverGroupName);

                    //set translationMappingName
                    var translationMappingNamerow = $(findTableObj).find("select[name='translationMappingName']");
                    $(translationMappingNamerow).val(jsonValue.translationMappingName);

                    //set Script
                    var scriptrow = $(findTableObj).find("input[name='script']");
                    $(scriptrow).val(jsonValue.script);

                    var acceptOnTimeoutrow = $(findTableObj).find("input:checkbox[name='acceptOnTimeout']");
                    if (jsonValue.acceptOnTimeout === "true") {
                        $(acceptOnTimeoutrow).attr('checked', true);
                        $(acceptOnTimeoutrow).val(jsonValue.acceptOnTimeout);
                    } else {
                        $(acceptOnTimeoutrow).attr('checked', false);
                        $(acceptOnTimeoutrow).val(jsonValue.acceptOnTimeout);
                    }

                    var waitForResponse = $(findTableObj).find("input:checkbox[name='waitForResponse']");
                    if (jsonValue.waitForResponse === "true") {
                        $(waitForResponse).attr('checked', true);
                        $(waitForResponse).val(jsonValue.waitForResponse);
                    } else {
                        $(waitForResponse).attr('checked', false);
                        $(waitForResponse).val(jsonValue.waitForResponse);
                    }
                });
            }else if(key == 'isHandlerEnabled'){
                if( value == "true" ){
                    $('#toggle-statefulproxybroadcasthandler_<%=widgetId%>').attr('checked', true);
                    $('#toggle-statefulproxybroadcasthandler_<%=widgetId%>').val('true');
                }else{
                    $('#toggle-statefulproxybroadcasthandler_<%=widgetId%>').attr('checked', false);
                    $('#toggle-statefulproxybroadcasthandler_<%=widgetId%>').val('false');
                    var handlerObject=$('#toggle-statefulproxybroadcasthandler_<%=widgetId%>').closest('table[class^="handler-class"]');
                    $(handlerObject).find('tr').each(function(){
                        $(this).addClass('disable-toggle-class');
                    });
                }
            }else if( key == 'handlerName'){
                if( value.length > 0 ){
                    $('#statefulProxyBroadcasthandlerName'+'<%=widgetId%>').attr('size', value.length);
                    $('#statefulProxyBroadcasthandlerName'+'<%=widgetId%>').val(value);
                    $('#statefulProxyCommucnicationHiddenHandlerName_<%=widgetId%>').val(value);
                }
            }
        });

        function changeValuesAcceptOnTimeout(checkbox){
            if($(checkbox).attr('checked')){
                $(checkbox).val('true');
            }else{
                $(checkbox).val('false');
            }
        }

        function removeEntry(obj) {
            var row = obj.parentNode.parentNode;
            row.parentNode.removeChild(row);
        }
    </script>
</head>
<body>

<form id="form_stateful_broadcast_proxycomm_<%=widgetId%>" name="form_stateful_broadcast_proxycomm_<%=widgetId%>" class="form_statefulproxycommunication">
    <input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
    <input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>

    <table name="tblmStatefulProxyBroadcastCommunication" width="100%" border="0" style="background-color: white;" class="handler-class tblmStatefulProxyBroadcastCommunication" cellspacing="0" cellpadding="0">
        <tr style="cursor: pointer;">
            <td align="left" class="tbl-header-bold sortableClass" valign="top" colspan="3">
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td width="96%" lign="left" class="tbl-header-bold" valign="top">
                            <input type="text" id="statefulProxyBroadcasthandlerName<%=widgetId%>" name="handlerName" class="handler-name-txt" size="32" value="<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxy.handler" />" onkeyup="expand(this);" onload="expand(this);" disabled="disabled"/>
                            <input type="hidden" id="statefulProxyCommucnicationHiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxy.handler" />" />
                            <input type="hidden" id="statefulProxyBroadcastCommunicationHandlerType_<%=widgetId%>" name="handlerType" class="handlerType" value="StatefulProxyCommunication" />
                        </td>
                        <td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
                            <div class="switch">
                                <input id="toggle-statefulproxybroadcasthandler_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" disabled="disabled"/>
                                <label for="toggle-statefulproxybroadcasthandler_<%=widgetId%>"></label>
                            </div>
                        </td>
                        <td width="2%" style="padding-right: 10px" class="tbl-header-bold" valign="middle" onclick="expandCollapse(this);">
                            <img alt="Expand" class="expand_class"  title="Expand" id="proxyCommunicationImg"   src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"/>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <div id="statefulProxyBroadcastCommunicationDiv" class="toggleDivs">
                    <table name="tblmStatefulProxyBroadcastCommunication" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
                        <tr>
                            <td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
                                <input type="button" onclick="addStatefulProxyPolicyRow('statefulProxyBroadcastCommunicationTemplate_<%=widgetId%>','statefulProxyBroadcastCommunicationTbl_<%=widgetId%>');" value=" Add Stateful Proxy Communication " class="light-btn proxy-com-btn" style="size: 140px" tabindex="3" disabled="disabled">
                            </td>
                        </tr>
                        <tr>
                            <td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
                                <table cellSpacing="0" cellPadding="0" width="100%" border="0" id="statefulProxyBroadcastCommunicationTbl_<%=widgetId%>" class="statefulProxyBroadcastCommunicationTbl">
                                    <tr>
                                        <td align="left" class="tblheader-policy" valign="top" width="16%" id="tbl_attrid">
                                            <bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxycommunication.ruleset" />
                                            <ec:elitehelp  header="radiusservicepolicy.stateful.proxycommunication.ruleset" headerBundle="servicePolicyProperties" text="radiusservicepolicy.stateful.proxycommunication.ruleset" ></ec:elitehelp>
                                        </td>
                                        <td align="left" class="tblheader-policy" valign="top" width="16%">
                                            <bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxycommunication.servergroup" />
                                            <ec:elitehelp header="radiusservicepolicy.stateful.proxycommunication.servergroup" headerBundle="servicePolicyProperties" text="radiusservicepolicy.stateful.proxycommunication.servergroup" ></ec:elitehelp>
                                        </td>
                                        <td align="left" class="tblheader-policy" valign="top" width="16%">
                                            <bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxycommunication.translationmapping" />
                                            <ec:elitehelp header="radiusservicepolicy.stateful.proxycommunication.translationmapping" headerBundle="servicePolicyProperties" text="radiusservicepolicy.stateful.proxycommunication.translationmapping" ></ec:elitehelp>
                                        </td>
                                        <td align="left" class="tblheader-policy" valign="top" width="16">
                                            <bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxycommunication.script" />
                                            <ec:elitehelp header="radiusservicepolicy.stateful.proxycommunication.script" headerBundle="servicePolicyProperties" text="radiusservicepolicy.stateful.proxycommunication.script" ></ec:elitehelp>
                                        </td>
                                        <td align="left" class="tblheader-policy" valign="top" width="16%">
                                            <bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxycommunication.acceptontimeout" />
                                            <ec:elitehelp header="radiusservicepolicy.stateful.proxycommunication.acceptontimeout" headerBundle="servicePolicyProperties" text="radiusservicepolicy.stateful.proxycommunication.acceptontimeout" ></ec:elitehelp>
                                        </td>
                                        <td align="left" class="tblheader-policy" valign="top" width="16%">
                                            <bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxycommunication.waitforresponse" />
                                            <ec:elitehelp header="radiusservicepolicy.stateful.proxycommunication.waitforresponse" headerBundle="servicePolicyProperties" text="radiusservicepolicy.stateful.proxycommunication.waitforresponse" ></ec:elitehelp>
                                        </td>
                                        <td align="left" class="tblheader-policy" valign="top" width="4%">
                                            <bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxycommunication.remove" />
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


<table id="statefulProxyBroadcastCommunicationTemplate_<%=widgetId%>" style="display: none">
    <tr>
        <td class='proxy-table-firstcol tblrows' width="16%" valign="middle">
            <input class="ruleset" type="text" name="ruleset" id="ruleset_<%=widgetId%>" maxlength="2000" style="width: 100%;" disabled="disabled"/>
        </td>
        <td align="left" class="tblrows" valign="top" width="16%">
            <select id="serverGroupName" name="serverGroupName" style="width:100%;" class="corRadEsiClass" disabled="disabled">
                <option value="0">--Select--</option>
                <logic:iterate id="names" property="radiusEsiGroupNames" name="updateRadiusServicePolicyForm" type="java.lang.String">
                    <option value="<%=names%>"><%=names%></option>
                </logic:iterate>
            </select>
            <input type="hidden" name="oNumber" value="1"/>
        </td>
        <td class="tblrows" width="16%" valign="middle"><select
                name="translationMappingName"
                id="translationMappingName_<%=widgetId%>" style="width: 100%;" disabled="disabled">
            <option value="">--Select--</option>
            <optgroup label="Translation Mapping" class="labeltext">
                <logic:iterate id="translationMapping"
                               name="updateRadiusServicePolicyForm"
                               property="translationMappingConfDataList"
                               type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
                    <option
                            value="<%=translationMapping.getName()%>"
                            class="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></option>
                </logic:iterate>

            </optgroup>

            <optgroup label="Copy Packet Mapping" class="labeltext">
                <logic:iterate id="copyPacketMapping"
                               name="updateRadiusServicePolicyForm"
                               property="copyPacketMappingConfDataList"
                               type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData">
                    <option
                            value="<%=copyPacketMapping.getName()%>"
                            styleClass="<%=ConfigConstant.COPY_PACKET_MAPPING%>"><%=copyPacketMapping.getName()%></option>
                </logic:iterate>
            </optgroup>
        </select>
        </td>

        <td class="tblrows" width="16%" valign="middle">
            <input class="esiScriptAutocomplete" id="script_<%=widgetId%>" type="text" name="script" style="width: 100%;" maxlength="2000" disabled="disabled"/>
        </td>
        <td class="tblrows" width="16%" valign="middle" align="center">
            <input type="checkbox" id="acceptOnTimeout_<%=widgetId%>" value="false" name="acceptOnTimeout" class="noborder acceptOnTimeout" onclick="changeValuesAcceptOnTimeout(this);" disabled="disabled"/>
        </td>
        <td class="tblrows" width="16%" valign="middle" align="center">
            <input type="checkbox" id="waitForResponse_<%=widgetId%>" value="false" name="waitForResponse" class="noborder waitForResponse" onclick="changeValuesAcceptOnTimeout(this);" disabled="disabled"/>
        </td>
        <td class='tblrows' align="center" valign="middle" width="4%">
            <img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' />&nbsp;
        </td>
    </tr>
</table>
</html>

