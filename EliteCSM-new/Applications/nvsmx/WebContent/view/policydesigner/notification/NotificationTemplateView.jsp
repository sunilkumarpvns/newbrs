<%@page import="com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%
	NotificationTemplateData notificationTemplateData = (NotificationTemplateData)request.getAttribute("notificationTemplateData");
	String templateData = notificationTemplateData.getTemplateData();
	if(templateData == null && templateData.trim().length()==0){
		templateData = "";
	}
%>

<style type="text/css">
.form-group {
	width: 100%;
	display: table;
	margin-bottom: 2px;
}
.with-border {
	border-style: inset;
	width: 50%;
}
</style>
<div class="panel panel-primary">
<div class="panel-heading" style="padding: 8px 15px">
<h3 class="panel-title" style="display: inline;">
<s:property value="notificationTemplateData.name"/>
</h3>
<div class="nv-btn-group" align="right">
	        <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"                                                                                                                 
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${notificationTemplateData.id}&auditableId=${notificationTemplateData.id}&auditPageHeadingName=${notificationTemplateData.name}&refererUrl=/policydesigner/notification/NotificationTemplate/view?notificationTemplateId=${notificationTemplateData.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn"
					onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/init?notificationTemplateId=${notificationTemplateData.id}&groupIds=${notificationTemplateData.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span> 
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/delete?ids=${notificationTemplateData.id}&fromViewPage=true&${notificationTemplateData.id}=${notificationTemplateData.groups}&notificationTemplateId=${notificationTemplateData.id}">
				<button type="button" class="btn btn-default header-btn">
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
</div>
</div>
<div class="panel-body">


<div class="row">
	<fieldset class="fieldSet-line">
		<legend align="top"> 
			<s:text name="Basic Details"></s:text>
		</legend>
		<div class="row">
			<div class="col-sm-4">
				<div class="row">
					<s:label key="notification.type" value="%{notificationTemplateData.templateType}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
					<s:label key="notification.subject" value="%{notificationTemplateData.subject}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
					<s:label key="entity.groups" value="%{notificationTemplateData.groupNames}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
				</div>
			</div>
			<div class="col-sm-4 leftVerticalLine"> 
				<div class="row">
							
						<s:if test="%{notificationTemplateData.createdByStaff !=null}">
							<s:hrefLabel key="getText('createdby')" value="%{notificationTemplateData.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
									 url="/sm/staff/staff/%{notificationTemplateData.createdByStaff.id}"/>
						</s:if>
						<s:else>
							<s:label key="getText('createdby')" value="-" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
						</s:else>
						<s:if test="%{notificationTemplateData.createdDate != null}">
							<s:set var="createdByDate">
								<s:date name="%{notificationTemplateData.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
							</s:set>
							<s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
						</s:if>
						<s:else>
							<s:label key="getText('createdon')" value="-" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
						</s:else>
						<s:if test="%{notificationTemplateData.modifiedByStaff !=null}">
							<s:hrefLabel key="getText('modifiedby')" value="%{notificationTemplateData.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
									 url="/sm/staff/staff/%{notificationTemplateData.modifiedByStaff.id}"/>
						</s:if>
						<s:else>
							<s:label key="getText('modifiedby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
						</s:else>
						<s:if test="%{notificationTemplateData.modifiedDate != null}">
							<s:set var="modifiedByDate">
								<s:date name="%{notificationTemplateData.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
							</s:set>
							<s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
						</s:if>
						<s:else>
							<s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
						</s:else>
				</div>
			</div>
		</div>
	</fieldset>
</div>

<div class="row">
<fieldset class="fieldSet-line">
<legend align="top"> 
	<s:text name="Template Data"></s:text>
</legend>
<div class="col-xs-12">
<%=templateData %>
</div>
</fieldset>
</div>

</div>