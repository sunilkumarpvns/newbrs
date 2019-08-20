<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 27/11/17
  Time: 7:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:text name="ldap.auth.conf" />
        </h3>
    </div>
    <div class="panel-body" >
        <s:form namespace="/sm/ldapauthconf" action="ldap-auth-configuration" id="ldapauthconf" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-10 col-lg-8">
                    <s:hidden name="id" value="%{id}" />
                    <s:select name="ldapDataId" key="ldap.auth.conf.data.source" cssClass="form-control focusElement" list="ldapDataList" listKey="getId()" listValue="getName()" headerValue="SELECT" headerKey="" />
                    <s:textfield name="datePattern" cssClass="form-control" key="ldap.auth.conf.date.pattern" />
                </div>
            </div>
            <div id="fieldMappingDiv" class="col-xs-12">
                <table id='fieldMappingTable'  class="table table-blue table-bordered">
                    <caption class="caption-header"><s:text name="ldap.auth.conf.field.mapping" />
                        <div align="right" class="display-btn">
                            <span class="btn btn-group btn-group-xs defaultBtn" onclick="addFieldMapping();" id="addRow"> <span class="glyphicon glyphicon-plus"></span></span>
                        </div>
                    </caption>
                    <thead>
                        <th><s:text name="ldap.auth.conf.field.map.staff.attribute"/></th>
                        <th><s:text name="ldap.auth.conf.field.map.ldap.attribute"/></th>
                        <th style="width:35px;">&nbsp;</th>
                    </thead>
                    <tbody>
                    <s:iterator value="ldapAuthConfigurationFieldMappingDataList" status="i" var="ldapAuthConfigurationFieldMapping">
                        <tr name='fieldMappingRow'>
                            <td><s:select list="@com.elitecore.nvsmx.staff.StaffFields@values()" listValue="getDisplayName()" value="%{#ldapAuthConfigurationFieldMapping.staffAttribute}" name="ldapAuthConfigurationFieldMappingDataList[%{#i.count - 1}].staffAttribute" cssClass="form-control"  elementCssClass="col-xs-12"></s:select></td>
                            <td><s:textfield value="%{#ldapAuthConfigurationFieldMapping.ldapAttribute}"	name="ldapAuthConfigurationFieldMappingDataList[%{#i.count - 1}].ldapAttribute" cssClass="form-control"  elementCssClass="col-xs-12" /></td>
                            <td style="width:35px;"><span class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>
                        </tr>
                    </s:iterator>
                    </tbody>
                </table>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <button type="submit" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/sm/ldapauthconf/ldap-auth-configuration/${id}" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/ldapauthconf/ldap-auth-configuration/*'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                </div>
            </div>
        </s:form>
    </div>
</div>

<table id="tempFieldMapping" style="display: none;">
    <tr>
        <td><s:select list="@com.elitecore.nvsmx.staff.StaffFields@values()" listValue="getDisplayName()" cssClass="form-control" elementCssClass="col-xs-12"></s:select></td>
        <td><s:textfield cssClass="form-control" elementCssClass="col-xs-12" /></td>
        <td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
    </tr>
</table>


<script>

    var i = document.getElementsByName("fieldMappingRow").length;

    if(isNullOrEmpty(i)) {
        i = 0;
    }

    function addFieldMapping() {
        $("#fieldMappingTable tbody").append("<tr>" + $("#tempFieldMapping").find("tr").html() + "</tr>");

        $("#fieldMappingTable").find("tr:last td:nth-child(1)").find("select").focus();
        var NAME = "name";
        $("#fieldMappingTable").find("tr:last td:nth-child(1)").find("select").attr(NAME,'ldapAuthConfigurationFieldMappingDataList['+i+'].staffAttribute');
        $("#fieldMappingTable").find("tr:last td:nth-child(2)").find("input").attr(NAME,'ldapAuthConfigurationFieldMappingDataList['+i+'].ldapAttribute');
        i++;
    }
    
</script>

