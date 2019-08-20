<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form namespace="/" action="policydesigner/subscriber/Subscriber/searchCriteria" id="subscriberSearch" method="post" cssClass="form-vertical">
	<h4>
		<s:text name="search.criteria"/>
	</h4>
	<s:textfield name="subscriber.subscriberIdentity"  		id="subscriberIdentity"  	key="getText('subscriber.subscriberIdentity')" 	cssClass="form-control"/>
	<s:textfield name="alternateIdField"  		id="alternateIdField"  	    key="getText('subscriber.alternateIdField')" 	cssClass="form-control"/>
	<s:submit  cssClass="btn btn-primary" value="Search"></s:submit>
</s:form>
 	

