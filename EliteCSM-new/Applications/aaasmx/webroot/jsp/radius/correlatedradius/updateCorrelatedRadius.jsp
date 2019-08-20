<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html" %>

<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.radius.correlatedradius.form.CorrelatedRadiusForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
    CorrelatedRadiusForm correlatedRadiusForm = (CorrelatedRadiusForm)request.getAttribute("correlatedRadiusForm");
%>

<script>
    function verifyName() {
        var searchName = document.getElementById("name").value;
        isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_ESI_GROUP%>',searchName,'update','<%=correlatedRadiusForm.getId() %>','verifyNameDiv');
    }
    $( document ).ready(function() {
        $("#esiGroupName").focus();
        verifyName();
    });
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

<html:form action="/updateCorrelatedRadius">

    <html:hidden name="correlatedRadiusForm" styleId="id" property="id" />
    <html:hidden name="correlatedRadiusForm" styleId="auditUId" property="auditUId" />
    <html:hidden name="correlatedRadiusForm" styleId="action" property="action"  value="update" />
    <table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
        <tr>
            <td class="small-gap" colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td valign="middle" colspan="5">
                <table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
                    <tr>
                        <td align="left" class="tblheader-bold" valign="top" colspan="3">
                            <bean:message bundle="radiusResources" key="correlated.radius.update" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="100%">&nbsp;</td>
                    </tr>

                    <!-- Peer Name -->
                    <tr>
                        <td align="left" class="captiontext" valign="top" width="25%">
                            <bean:message bundle="radiusResources" key="correlated.radius.name" />
                            <ec:elitehelp headerBundle="radiusResources"  text="correlated.radius.esiname" header="correlated.radius.esiname"/>
                        </td>
                        <td align="left" class="labeltext" valign="top" nowrap="nowrap">
                            <html:text name="correlatedRadiusForm" tabindex="1" styleId="name" property="name" size="30"  onblur="verifyName();" maxlength="256" style="width:250px" />
                            <font color="#FF0000">*</font>
                            <div id="verifyNameDiv" class="labeltext" />
                        </td>
                    </tr>
                    <%
                        CorrelatedRadiusForm correlatedRadiusForm11;
                    %>
                    <!-- Description -->
                    <tr>
                        <td align="left" class="captiontext" valign="top" width="25%">
                            <bean:message bundle="radiusResources" key="correlated.radius.description"/>
                            <ec:elitehelp headerBundle="radiusResources" text="correlated.radius.description" header="correlated.radius.description"/>
                        </td>
                        <td align="left" class="labeltext" valign="top" nowrap="nowrap">
                            <html:textarea property="description" styleId="description" name="correlatedRadiusForm" rows="2" cols="40" tabindex="3"></html:textarea>
                        </td>
                    </tr>

                    <tr>
                        <td align="left" class="captiontext" valign="top" width="25%">
                            <bean:message bundle="radiusResources" key="correlated.radius.authesi" />
                            <ec:elitehelp headerBundle="radiusResources" text="correlated.radius.authesi" header="correlated.radius.authesi"/>
                        </td>
                        <td align="left" class="labeltext" valign="top" nowrap="nowrap">
                            <html:select styleId="authEsiName" property="authEsiName" name="correlatedRadiusForm" style="width:250px" tabindex="3">
                                <logic:iterate id="esiInstance" name="correlatedRadiusForm"	property="authExternalSystemInterfaceInstanceData" type="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData">
                                    <%if(correlatedRadiusForm.getAuthEsiName() != null && esiInstance.getEsiInstanceId().equals(correlatedRadiusForm.getAuthEsiName())){ %>
                                    <option  selected="selected" data-id="<%=esiInstance.getEsiTypeId()%>" value="<%=esiInstance.getEsiInstanceId()%>"><%=esiInstance.getName() +"&nbsp;&nbsp;"+"("+ esiInstance.getAddress()+")"%></option>
                                    <%} else {%>
                                    <option  data-id="<%=esiInstance.getEsiTypeId()%>" value="<%=esiInstance.getEsiInstanceId()%>"><%=esiInstance.getName() +"&nbsp;&nbsp;"+"("+ esiInstance.getAddress()+")"%></option>
                                    <%} %>
                                </logic:iterate>
                            </html:select>
                            <font color="#FF0000"> *</font>
                        </td>
                    </tr>

                    <tr>
                        <td align="left" class="captiontext" valign="top" width="25%">
                            <bean:message bundle="radiusResources" key="correlated.radius.acctesi" />
                            <ec:elitehelp headerBundle="radiusResources" text="correlated.radius.acctesi" header="correlated.radius.acctesi"/>
                        </td>
                        <td align="left" class="labeltext" valign="top" nowrap="nowrap">
                            <html:select property="acctEsiName" styleId="acctEsiName" name="correlatedRadiusForm" style="width:250px" tabindex="4">
                                <logic:iterate id="esiInstance" name="correlatedRadiusForm"	property="acctExternalSystemInterfaceInstanceData" type="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData">
                                    <%if(correlatedRadiusForm.getAcctEsiName() != null && esiInstance.getEsiInstanceId().equals(correlatedRadiusForm.getAcctEsiName())){ %>
                                    <option selected="selected" data-id="<%=esiInstance.getEsiTypeId()%>" value="<%=esiInstance.getEsiInstanceId()%>"><%=esiInstance.getName() +"&nbsp;&nbsp;"+"("+ esiInstance.getAddress()+")"%></option>
                                    <%} else {%>
                                    <option  data-id="<%=esiInstance.getEsiTypeId()%>" value="<%=esiInstance.getEsiInstanceId()%>"><%=esiInstance.getName() +"&nbsp;&nbsp;"+"("+ esiInstance.getAddress()+")"%></option>
                                    <%} %>
                                </logic:iterate>
                            </html:select>
                            <font color="#FF0000"> *</font>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">&nbsp;</td>
                    </tr>
                    <tr>
                        <td class="btns-td" valign="middle">&nbsp;</td>
                        <td class="btns-td" valign="middle" colspan="2">
                            <input type="button" class="light-btn" value="  Update  " onclick="validateForm();" tabindex="5" />
                            <input type="button" name="c_btnCancelBtn" tabindex="6" onclick="javascript:location.href='searchCorrelatedRadius.do'" value="  Cancel  " class="light-btn" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">&nbsp;</td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</html:form>