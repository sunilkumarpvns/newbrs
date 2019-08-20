<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<style>
    .failure-count{
        color: #d9534f;
        font-weight: bold;
    }
</style>

<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:text name="slice.conf" />
        </h3>
    </div>
    <div class="panel-body">

        <s:label id="reloadStatus"  key="reload.policy.status1" value="%{reloadDataSliceConfigurationResponse.response}" cssClass="control-label light-text word-break" labelCssClass="col-sm-3 col-xs-4" elementCssClass="col-sm-9 col-xs-8"  />
        <s:label id="unreachabelInstances"  key="reload.policy.unreachable.instance" value="%{unreachableInstances}" cssClass="control-label light-text failure-count word-break" labelCssClass="col-sm-3 col-xs-4" elementCssClass="col-sm-9 col-xs-8 "  />
        <br/>

        <div class="row">
            <div class="col-xs-12" align="center">
                <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/sliceconfig/slice-config/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
            </div>
        </div>
    </div>
</div>
