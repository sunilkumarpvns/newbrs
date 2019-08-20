<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form namespace="/" action="policydesigner/notification/NotificationTemplate/setSearchCriteria" id="notificationTemplateSearch" method="post" cssClass="form-vertical">
<h4>
		<s:text name="search.criteria"/>
</h4>

<s:textfield name="notificationTemplateData.name" id="name" key="Template Name" cssClass="form-control"/>
<s:submit  cssClass="btn btn-primary" value="Search"></s:submit>
</s:form>