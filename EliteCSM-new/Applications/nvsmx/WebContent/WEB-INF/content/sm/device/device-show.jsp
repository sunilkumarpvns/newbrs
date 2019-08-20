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
            <s:property value="brand"/>&nbsp;<s:property value="deviceModel"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/device/device/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/device/device/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/device/device/${id}?_method=DELETE">
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
                            <s:label key="device.tac" value="%{tac}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            <s:label key="device.brand" value="%{brand}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            <s:label key="device.tacModel" value="%{deviceModel}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            <s:label key="device.hardware" value="%{hardwareType}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            <s:label key="device.os" value="%{os}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            <s:label key="device.release_year" value="%{year}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            <s:label key="device.additional_info" value="%{additionalInformation}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            
                        </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>
            </fieldset>

        </div>
        <div class="row">
            <div class="col-xs-12" align="center">
                <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/device/device'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
            </div>
        </div>
    </div>
</div>
