<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form namespace="/" action="policydesigner/dataservicetype/DataServiceType/setSearchCriteria" method="post" cssClass="form-vertical" >
	<h4>
		<s:text name="search.criteria"/>
	</h4>
	<s:textfield name="dataServiceTypeData.name"  				id="serviceTypeName"  	key="getText('dataservicetype.name')" 				cssClass="form-control"/>
	<s:textfield name="dataServiceTypeData.serviceIdentifier"  	id="serviceIdentifier"  key="getText('dataservicetype.serviceidentifier')" 	cssClass="form-control" type="text" onkeypress="return isNaturalInteger(event);" maxLength="10"/>
	<s:submit  cssClass="btn btn-primary" value="Search"/>
</s:form>

<script type="text/javascript">

</script>
