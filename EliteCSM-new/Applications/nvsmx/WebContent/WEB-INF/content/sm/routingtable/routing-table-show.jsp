<%@taglib uri="/struts-tags/ec" prefix="s" %>
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
        <h3 class="panel-title">
            <s:text name="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="<s:text name="audit.history"/>"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/routingtable/routing-table/${id}'">
                    <span class="glyphicon glyphicon-eye-open"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/routingtable/routing-table/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()"
                  data-href="${pageContext.request.contextPath}/sm/routingtable/routing-table/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body">
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail"/></legend>
                <div class="row">
                    <div class="col-sm-8">
                        <div class="row">
                            <s:label key="routingtable.name" value="%{name}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                            <s:label key="routingtable.type" value="%{type}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                            <s:hrefLabel key="routingtable.mccmncgroup" value="%{mccMncGroupData.name}"
                                     url="/sm/mccmncgroup/mcc-mnc-group/%{mccMncGroupData.id}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                            <s:label key="routingtable.realmCondition" value="%{realmCondition}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                            <s:label key="routingtable.action" value="%{action}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
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
                <legend><s:text name="routingtable.gateways"/></legend>
                <table class="table table-blue">
                    <thead>
                    <th><s:text name="gateway.name"/></th>
                    <th><s:text name="gateway.weightage"/></th>
                    </thead>
                    <tbody>
                    <s:iterator value="routingTableGatewayRelDataList">
                        <tr>
                            <td><a href="${pageContext.request.contextPath}/sm/gateway/diameter-gateway/${diameterGatewayData.id}"><s:property value="diameterGatewayData.name"/></a></td>
                            <td><s:property value="weightage"/></td>
                        </tr>
                    </s:iterator>
                    </tbody>
                </table>
            </fieldset>
        </div>

        <div class="row">
            <div class="col-xs-12" align="center">
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="5"
                        style="margin-right:10px;"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/routingtable/routing-table'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                </button>
            </div>
        </div>
    </div>
</div>
