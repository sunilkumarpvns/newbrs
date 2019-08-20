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
            <s:property value="%{name}"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/mccmncgroup/mcc-mnc-group/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group/${id}?_method=DELETE">
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
                            <%-- <s:label key="mccmncgroup.name" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" /> --%>
                            <s:label key="mccmncgroup.brand" value="%{brandData.name}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
                            <s:label key="mccmncgroup.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
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

	        <table name="networkDatas" cellpadding="0" cellspacing="0" border="0" id="networkMccMncListingTable" class="table table-blue">
			<thead>
				<tr>
					<th><s:text name="mccmncgroup.operator"/></th>
					<th><s:text name="mccmncgroup.network"/></th>
					<th><s:text name="mccmncgroup.mcc"/></th>
					<th><s:text name="mccmncgroup.mnc"/></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="networkDatas" status="count" var="data">
		            <tr>
			            <td><s:hidden name="networkDatas[%{#network.count - 1}].operatorData.name"/><s:property value="operatorData.name"/></td>
			            <td><s:hidden name="networkDatas[%{#network.count - 1}].name"/><a href="${pageContext.request.contextPath}/sm/network/network/<s:property value="id"/>"><s:property value="name"/></a></td>
						<td><s:hidden name="networkDatas[%{#network.count - 1}].mcc"/><s:property value="mcc"/></td>
						<td><s:hidden name="networkDatas[%{#network.count - 1}].mnc"/><s:property value="mnc"/></td>
		            </tr>
		        </s:iterator>
			</tbody>
		</table>

        <div class="row">
            <div class="col-xs-12" align="center">
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="5"
                        style="margin-right:10px;"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/mccmncgroup/mcc-mnc-group'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                </button>
            </div>
        </div>

    </div>

</div>
