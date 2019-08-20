<%@ taglib prefix="s" uri="/struts-tags/ec" %>
<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 8/11/17
  Time: 12:03 PM
  To change this template use File | Settings | File Templates.
--%>
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
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/area/area/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/area/area/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/area/area/${id}?_method=DELETE">
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
                    <div class="col-sm-4">
                        <div class="row">
                            <s:label key="area.groups" value="%{groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8" />
                            <s:label key="area.country" value="%{cityData.regionData.countryData.name}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8" />
                            <s:hrefLabel key="area.region" value="%{cityData.regionData.name}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"
                                     url="/sm/region/region/%{cityData.regionData.id}"/>
                            <s:hrefLabel key="area.city" value="%{cityData.name}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8"
                                     url="/sm/city/city/%{cityData.id}"/>
                        </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <s:label key="area.network" value="%{networkData.name}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8" />
                            <s:label key="area.param1" value="%{param1}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8 word-break" />
                            <s:label key="area.param2" value="%{param2}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8 word-break" />
                            <s:label key="area.param3" value="%{param3}" cssClass="control-label light-text" labelCssClass="col-xs-4" elementCssClass="col-xs-8 word-break" />
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
                <legend> <s:text name="area.wifi.information"/> </legend>
                <div class="row">
                    <div class="col-xs-12 col-sm-6">
                        <div class="row">
                            <s:label key="area.called.stations" value="%{calledStations}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7 word-break" />
                            <s:label key="area.ssids" value="%{ssids}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7 word-break" />
                        </div>
                    </div>
                </div>
            </fieldset>

            <fieldset class="fieldSet-line">
                <legend> <s:text name="area.lac.information"/> </legend>
                <div class="row">
                   <div class="col-sm-12">
                        <nv:dataTable
                                id="lacInformationDataAsJson"
                                list="${lacInformationDataAsJson}"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="LAC" beanProperty="lac" tdCssClass="text-left text-middle" />
                            <nv:dataTableColumn title="CIs" beanProperty="ciList" tdCssClass="text-left text-middle word-break" />
                            <nv:dataTableColumn title="SACs" beanProperty="sacList" tdCssClass="text-left text-middle word-break" />
                            <nv:dataTableColumn title="RACs" beanProperty="racList" tdCssClass="text-left text-middle word-break" />
                        </nv:dataTable>
                    </div>
                </div>
            </fieldset>
        </div>
    </div>
</div>


