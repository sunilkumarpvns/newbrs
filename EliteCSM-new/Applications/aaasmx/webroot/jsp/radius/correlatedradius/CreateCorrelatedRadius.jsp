<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>

<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.web.radius.correlatedradius.form.CorrelatedRadiusForm"%>

<%
    CorrelatedRadiusForm correlatedRadiusForm = (CorrelatedRadiusForm)request.getAttribute("correlatedRadiusForm");
    String basePath = request.getContextPath();
    String statusVal=(String)request.getParameter("status");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
    function verifyName() {
        var searchName = document.getElementById("name").value;
        isValidName = verifyInstanceName('<%=InstanceTypeConstants.CORRELATED_RADIUS%>',searchName,'create','','verifyNameDiv');
    }

    function validateForm(){

        if(isEmpty($('#name').val())){
            alert('Name must be specified');
            return;
        }else if(!isValidName) {
            alert('Enter Valid Correlated Radius Name');
            $('#name').focus();
            return;
        }
        else if($("#authEsiName option:selected" ).val() == "0"){
            alert("Auth ESI must be specified");
            $('#authEsiName').focus();
            return;
        }
        else if($("#acctEsiName option:selected" ).val() == "0"){
            alert("Acct ESI must be specified");
            $('#acctEsiName').focus();
            return;
        }else {
            document.forms[0].submit();
        }
    }
    setTitle('<bean:message bundle="radiusResources" key="correlated.radius.title"/>');
</script>
<html>
<head>

    <html:form action="/createCorrelatedRadius">
        <html:hidden name="correlatedRadiusForm" styleId="action" property="action" value="create" />
        <table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
            <tr>
                <td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
                <td>
                    <table cellpadding="0" cellspacing="0" border="0" width="100%">
                        <tr>
                            <td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
                                <table cellpadding="0" cellspacing="0" border="0" width="100%">
                                    <tr>
                                        <td class="table-header">
                                            <bean:message bundle="radiusResources" key="correlated.radius.create" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td colspan="3">
                                            <table width="100%" name="correlatedRadiusList" id="correlatedRadiusList" align="right" cellSpacing="0" cellPadding="0" border="0">
                                                <tr>
                                                    <td align="left" class="captiontext" valign="top">
                                                        <bean:message bundle="radiusResources"  key="correlated.radius.name" />
                                                        <ec:elitehelp headerBundle="radiusResources" text="correlated.radius.name" header="correlated.radius.name"/>
                                                    </td>
                                                    <td align="left" class="labeltext" valign="top" width="50%">
                                                        <html:text styleId="name" tabindex="1" name="correlatedRadiusForm" property="name" onblur="verifyName();" size="30" styleClass="flatfields" style="font-family: Verdana; width:250px " maxlength="30"  />
                                                        </div><font color="#FF0000"> *</font>
                                                        <div id="verifyNameDiv" class="labeltext">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" class="captiontext" valign="top" width="12%">
                                                        <bean:message bundle="radiusResources" key="correlated.radius.description" />
                                                        <ec:elitehelp headerBundle="radiusResources"
                                                                      text="correlated.radius.description" header="correlated.radius.description"/>
                                                    </td>
                                                    <td align="left" class="labeltext" valign="top" colspan="2">
                                                        <table width="100%" border="0" cellpadding="0"
                                                               cellspacing="0">
                                                            <tr>
                                                                <td><html:textarea styleId="description"
                                                                                   tabindex="3" property="description" cols="30" rows="4"
                                                                                   style="width:250px" name="correlatedRadiusForm" />
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" class="captiontext" valign="top">
                                                        <bean:message bundle="radiusResources" key="correlated.radius.authesi" />
                                                        <ec:elitehelp headerBundle="radiusResources" text="correlated.radius.authesi" header="correlated.radius.authesi"/>
                                                    </td>
                                                    <td align="left" class="labeltext" valign="top" nowrap="nowrap">
                                                        <html:select property="authEsiName" styleId="authEsiName" name="correlatedRadiusForm" style="width:250px" tabindex="3" >
                                                            <html:option value="0">--Select--</html:option>
                                                            <logic:iterate id="esiInstance" name="correlatedRadiusForm"	property="authExternalSystemInterfaceInstanceData" type="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData">
                                                                <option data-id="<%=esiInstance.getEsiTypeId() %>" value="<%=esiInstance.getEsiInstanceId()%>"><%=esiInstance.getName() +"&nbsp;&nbsp;"+"("+ esiInstance.getAddress()+")"%></option>
                                                            </logic:iterate>
                                                        </html:select>
                                                        <font color="#FF0000"> *</font>
                                                    </td>

                                                </tr>
                                                <tr>
                                                    <td align="left" class="captiontext" valign="top">
                                                        <bean:message bundle="radiusResources" key="correlated.radius.acctesi" />
                                                        <ec:elitehelp headerBundle="radiusResources" text="correlated.radius.acctesi" header="correlated.radius.acctesi"/>
                                                    </td>
                                                    <td align="left" class="labeltext" valign="top" nowrap="nowrap">
                                                        <html:select property="acctEsiName" styleId="acctEsiName" name="correlatedRadiusForm" style="width:250px" tabindex="3" >
                                                            <html:option value="0">--Select--</html:option>
                                                            <logic:iterate id="esiInstance" name="correlatedRadiusForm"	property="acctExternalSystemInterfaceInstanceData" type="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData">
                                                                <option data-id="<%=esiInstance.getEsiTypeId() %>" value="<%=esiInstance.getEsiInstanceId()%>"><%=esiInstance.getName() +"&nbsp;&nbsp;"+"("+ esiInstance.getAddress()+")"%></option>
                                                            </logic:iterate>
                                                        </html:select>
                                                        <font color="#FF0000"> *</font>
                                                    </td>

                                                </tr>
                                                <tr>
                                                    <td colspan="3">&nbsp;</td>
                                                </tr>
                                                <tr>
                                                    <td class="btns-td" valign="middle">&nbsp;</td>
                                                    <td class="btns-td" valign="middle" colspan="2">
                                                        <input type="button" class="light-btn" value="  Create  " onclick="validateForm();" tabindex="5"/>
                                                        <input type="button" name="c_btnCancel" tabindex="6" onclick="javascript:location.href='searchCorrelatedRadius.do'" value="  Cancel  " class="light-btn" />
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <%@ include file="/jsp/core/includes/common/Footer.jsp"%>
                    </table>
                </td>
            </tr>
        </table>
    </html:form>
</head>
</html>