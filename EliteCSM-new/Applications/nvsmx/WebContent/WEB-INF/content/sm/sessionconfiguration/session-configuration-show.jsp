<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 31/8/17
  Time: 4:34 PM
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
            <s:text name="session.conf" />
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/sessionconfiguration/session-configuration/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
                <s:if test="%{id} == null">
                    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/sessionconfiguration/session-configuration/*/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
                </s:if>
                <s:else>
                    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/sessionconfiguration/session-configuration/${id}/edit'">
                        <span class="glyphicon glyphicon-pencil"></span>
                    </button>
                </s:else>
			</span>

        </div>
    </div>
    <div class="panel-body" >

        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:hrefLabel key="session.conf.database" value="%{databaseData.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"
                                    url="/sm/database/database/%{databaseData.id}"/>
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:hrefLabel key="getText('modifiedby')" value="%{modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"
                                url="/sm/staff/staff/%{modifiedByStaff.id}"/>
                            <s:if test="%{modifiedDate != null}">
                                <s:set var="modifiedByDate">
                                    <s:date name="%{modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            </s:else>
                        </div>
                    </div>
                </div>
            </fieldset>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="session.conf.batch.operation"/></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="session.conf.batchmode" value="%{@com.elitecore.corenetvertex.constants.BatchUpdateMode@fromValue(batchMode).displayValue}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="session.conf.batchsize" value="%{batchSize}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <s:label key="session.conf.querytimeout" value="%{queryTimeout}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                </div>
            </fieldset>
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="session.conf.field.mapping"/></legend>
                <div class="row">
                    <div class="col-sm-12">
                        <nv:dataTable
                                id="sessionFieldMappingData"
                                list="${sessionFieldMappingAsJson}"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="FieldName" beanProperty="Field Name" tdCssClass="text-left text-middle" tdStyle="width:30%"/>
                            <nv:dataTableColumn title="Referring Attribute" beanProperty="Referring Attribute" tdCssClass="text-left text-middle" tdStyle="width:40%"/>
                            <nv:dataTableColumn title="Data Type" beanProperty="Data Type" tdCssClass="text-left text-middle" tdStyle="width:30%"/>
                        </nv:dataTable>
                    </div>
                </div>
            </fieldset>
        </div>
    </div>
</div>
