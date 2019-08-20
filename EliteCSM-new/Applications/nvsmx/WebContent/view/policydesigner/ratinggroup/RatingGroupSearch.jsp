<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form namespace="/" action="policydesigner/ratinggroup/RatingGroup/setSearchCriteria" method="post" cssClass="form-vertical" >
	<h4>
		<s:text name="search.criteria"/>
	</h4>
	<s:textfield name="ratingGroupData.name"  				id="ratingGroupName"  	key="getText('ratinggroup.name')" 				cssClass="form-control"/>
	<s:textfield name="ratingGroupData.identifier"  	id="identifier"  key="getText('ratinggroup.identifier')" 	cssClass="form-control" type="text" onkeypress="return isNaturalInteger(event);" maxLength="10"/>
	<s:submit  cssClass="btn btn-primary" value="Search"/>
</s:form>

<script type="text/javascript">

</script>
