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
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/notificationagents/email-agent/${id}'">
                    <span class="glyphicon glyphicon-eye-open"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/notificationagents/email-agent/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()"
                  data-href="${pageContext.request.contextPath}/sm/notificationagents/email-agent/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body">
        <button type="button" class="btn btn-primary btn-sm pull-right" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/notificationagents/email-agent-password/${id}/edit'">
            <s:text name="change.password.label"/>
        </button>
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail"/></legend>
                <div class="row">
                    <div class="col-sm-8">
                        <div class="row">
                            <s:label key="email.agent.hostaddress" value="%{emailHost}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-5"
                                     elementCssClass="col-xs-7"/>
                            <s:label key="email.agent.username" value="%{userName}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-5" elementCssClass="col-xs-7 word-break"/>
                            <s:label key="email.agent.fromaddress" value="%{fromAddress}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-5"
                                     elementCssClass="col-xs-7"/>
                        </div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                    </div>
                </div>
            </fieldset>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/notificationagents/email-agent'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                            name="button.list"/></button>
                </div>
            </div>
        </div>
    </div>
</div>
