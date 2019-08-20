<%--
  Created by IntelliJ IDEA.
  User: vijayrajsinh
  Date: 5/11/18
  Time: 1:46 PM
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
        String widgetId=request.getParameter("widgetId");
        String isAdditional=request.getParameter("isAdditional");
        String orderNumber=request.getParameter("orderNumber");

        if(isAdditional.equalsIgnoreCase("true")){
            widgetId=widgetId+"_stateful_proxy_additional";
        }else{
            widgetId=widgetId+"_stateful_proxy_authentication";
        }
    %>
    <script>
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

<form id="form_stateful_proxycomm_<%=widgetId%>" name="form_stateful_proxycomm_<%=widgetId%>" class="form_statefulproxycommunication">
    <input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
    <input type="hidden" value="<%=isAdditional%>" name="isAdditional" id="isAdditional"/>

    <table name="tblmStatefulProxyCommunication" width="100%" border="0" style="background-color: white;" class="handler-class tblmStatefulProxyCommunication" cellspacing="0" cellpadding="0">
        <tr style="cursor: pointer;">
            <td align="left" class="tbl-header-bold sortableClass" valign="top" colspan="3">
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td width="96%" lign="left" class="tbl-header-bold" valign="top">
                            <input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="32" value="<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxy.handler" />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled"/>
                            <input type="hidden" id="statefulProxyCommucnicationHiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxy.handler" />" />
                            <input type="hidden" id="statefulProxyCommunicationHandlerType_<%=widgetId%>" name="handlerType" class="handlerType" value="StatefulProxyCommunication" />
                        </td>
                        <td class="editable_icon_bg">
                            <span class="edit_handler_icon" onclick="changeHandlerName(this);" title="Edit Handler Name"></span>
                            <span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;"></span>
                        </td>
                        <td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
                            <div class="switch">
                                <input id="toggle-statefulproxycommunication_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" />
                                <label for="toggle-statefulproxycommunication_<%=widgetId%>"></label>
                            </div>
                        </td>
                        <td width="1%" style="padding-right: 5px" class="tbl-header-bold" valign="middle">
                            <img alt="Delete" class="delele_proxy" title="Delete" src="<%=request.getContextPath()%>/images/delete_proxy.png" onclick="deleteHandler(this);" height="14" width="14" style="cursor: pointer;"/>
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
                <div id="statefulProxyCommunicationDiv" class="toggleDivs">
                    <table name="tblmStatefulProxyCommunication" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
                        <tr>
                            <td align="left" class="captiontext left-border right-border" valign="top" id="button" style="padding-top: 10px;">
                                <input type="button" onclick="addStatefulProxyPolicyRow('statefulProxyCommunicationTemplate_<%=widgetId%>','statefulProxyCommunicationTbl_<%=widgetId%>');" value=" Add Stateful Proxy Communication " class="light-btn proxy-com-btn" style="size: 140px" tabindex="3">
                            </td>
                        </tr>
                        <tr>
                            <td align="left" class="captiontext left-border right-border bottom-border" valign="top" id="button" style="padding-right: 25px;">
                                <table cellSpacing="0" cellPadding="0" width="100%" border="0" id="statefulProxyCommunicationTbl_<%=widgetId%>" class="statefulProxyCommunicationTbl">
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


<table id="statefulProxyCommunicationTemplate_<%=widgetId%>" style="display: none">
    <tr>
        <td class='proxy-table-firstcol tblrows' width="16%" valign="middle">
            <input class="ruleset" type="text" name="ruleset" id="ruleset_<%=widgetId%>" maxlength="2000" style="width: 100%;"/>
        </td>
        <td align="left" class="tblrows" valign="top" width="16%">
            <select id="serverGroupName_<%=widgetId%>" name="serverGroupName" style="width:100%;" class="corRadEsiClass" onchange="setStatefulProxyHandlerDropDown(this);">
                <option value="0">--Select--</option>
                <logic:iterate id="names" property="radiusEsiGroupNames" name="createRadiusServicePolicyForm" type="java.lang.String">
                    <option value="<%=names%>"><%=names%></option>
                </logic:iterate>
            </select>
            <input type="hidden" name="oNumber" value="1"/>
        </td>
        <td class="tblrows" width="16%" valign="middle"><select
                name="translationMappingName"
                id="translationMappingName_<%=widgetId%>" style="width: 100%;">
            <option value="">--Select--</option>
            <optgroup label="Translation Mapping" class="labeltext">
                <logic:iterate id="translationMapping"
                               name="createRadiusServicePolicyForm"
                               property="translationMappingConfDataList"
                               type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
                    <option
                            value="<%=translationMapping.getName()%>"
                            class="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></option>
                </logic:iterate>

            </optgroup>

            <optgroup label="Copy Packet Mapping" class="labeltext">
                <logic:iterate id="copyPacketMapping"
                               name="createRadiusServicePolicyForm"
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
            <input class="esiScriptAutocomplete" id="script_<%=widgetId%>" type="text" name="script" style="width: 100%;" maxlength="2000"/>
        </td>
        <td class="tblrows" width="16%" valign="middle" align="center">
            <input type="checkbox" id="acceptOnTimeout_<%=widgetId%>" value="false" name="acceptOnTimeout" class="noborder acceptOnTimeout" onclick="changeValuesAcceptOnTimeout(this);"/>
        </td>
        <td class='tblrows' align="center" valign="middle" width="4%">
            <img src='<%=request.getContextPath()%>/images/minus.jpg' onclick="removeEntry(this)" height='15' />&nbsp;
        </td>
    </tr>
</table>
</html>
