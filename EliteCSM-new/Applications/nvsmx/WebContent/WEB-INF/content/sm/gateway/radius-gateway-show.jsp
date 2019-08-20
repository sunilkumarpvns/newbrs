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
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/gateway/radius-gateway/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gateway/radius-gateway/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/gateway/radius-gateway/${id}?_method=DELETE">
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
                    <div class="col-sm-7">
                        <div class="row">
                            <s:label key="radius.gateway.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.gateway.groups" value="%{groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.gateway.status" value="%{status}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.gateway.policyEnforcementMethod" value="%{@com.elitecore.corenetvertex.constants.PolicyEnforcementMethod@valueOf(policyEnforcementMethod).getVal()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:hrefLabel key="radius.gateway.radiusprofile" value="%{radiusGatewayProfileData.name}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6"
                                    url="/sm/gatewayprofile/radius-gateway-profile/%{radiusGatewayProfileData.id}"/>
                            <s:label key="radius.gateway.connectionURL" value="%{connectionURL}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.gateway.sharedSecret" value="%{sharedSecret}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />
                            <s:label key="radius.gateway.minLocalPort" value="%{minLocalPort}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6" elementCssClass="col-xs-8 col-sm-6" />

                        </div>
                    </div>
                    <div class="col-sm-5 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>
            </fieldset>
            <div class="row">
                <div class="col-xs-12" align="center">

                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gateway/radius-gateway'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>

        </div>
    </div>
</div>
