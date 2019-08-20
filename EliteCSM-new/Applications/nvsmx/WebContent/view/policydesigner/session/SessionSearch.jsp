<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form namespace="/" action="policydesigner/session/Session/search" id="sessionSearch" method="post" cssClass="form-vertical" validate="true" >
	<h4>
		<s:text name="search.criteria"/>
	</h4>
	<s:select  		id="sessionAttribute"	 name="sessionSearchField.sessionAttribute"	key="session.attrubutes" cssClass="form-control" list="@com.elitecore.nvsmx.policydesigner.controller.session.SessionSearchAttributes@values()" listValue="getValue()" />
	<s:textfield 	id="attributeValue" 	name="sessionSearchField.attributeValue" 		key="session.attribute.value"	cssClass="form-control"/>
	<s:submit  cssClass="btn btn-primary"	value="Search"></s:submit>
</s:form>

