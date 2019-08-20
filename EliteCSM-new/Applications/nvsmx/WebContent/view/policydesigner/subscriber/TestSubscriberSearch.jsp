<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form namespace="/" action="policydesigner/subscriber/Subscriber/testSubscriberSearchCriteria" id="subscriberSearch" method="post" cssClass="form-vertical">
	<h4>
		<s:text name="subscriber.search.criteria"/>
	</h4>
		<s:textfield name="subscriber.subscriberIdentity"  id="subscriberIdentity" key="getText('subscriber.subscriberIdentity')" cssClass="form-control"/>
	<s:submit  cssClass="btn btn-primary" value="Search"></s:submit>
</s:form>
