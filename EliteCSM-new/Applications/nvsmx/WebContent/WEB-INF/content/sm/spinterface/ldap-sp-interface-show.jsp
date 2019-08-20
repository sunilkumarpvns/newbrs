<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 14/9/17
  Time: 11:14 AM
  To change this template use File | Settings | File Templates.
--%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/spinterface/ldap-sp-interface/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/spinterface/ldap-sp-interface/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/spinterface/ldap-sp-interface/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body" >
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-8">
                        <div class="row">
                            <s:label key="sp.interface.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:hrefLabel key="sp.interface.ldap.database" value="%{ldapSpInterfaceData.ldapData.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"
                                    url="/sm/ldap/ldap/%{ldapSpInterfaceData.ldapData.id}"/>
                            <s:label key="sp.interface.ldap.expirydatepattern" value="%{ldapSpInterfaceData.expiryDatePattern}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="sp.interface.ldap.passworddecrypttype" value="%{ldapSpInterfaceData.passwordDecryptType}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="sp.interface.ldap.maxquerytimeoutcount" value="%{ldapSpInterfaceData.maxQueryTimeoutCount}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>
            </fieldset>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="sp.interface.field.mapping" /></legend>
                <div class="row">
                    <div class="col-sm-12">
                        <nv:dataTable
                                id="spInterfacefieldMapping"
                                list="${spInterfaceFieldMappingAsJson}"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="Logical Name" beanProperty="logicalNameDisplayValue" tdCssClass="text-left text-middle" tdStyle="width:50%"/>
                            <nv:dataTableColumn title="Field Name" beanProperty="fieldName" tdCssClass="text-left text-middle" tdStyle="width:50%"/>
                        </nv:dataTable>
                    </div>
                </div>
            </fieldset>
        </div>
        <div class="row">
            <div class="col-xs-12" align="center">
                <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/spinterface/ldap-sp-interface'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
            </div>
        </div>
    </div>
</div>
