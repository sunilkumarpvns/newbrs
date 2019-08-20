<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form namespace="/" action="policydesigner/chargingrulebasename/ChargingRuleBaseName/setSearchCriteria" method="post" cssClass="form-vertical" >

	<h4>
		<s:text name="search.criteria"/>
	</h4>

	<s:textfield
		name="chargingRuleBaseNameData.name"
		id="chargingRuleBaseName"
		key="getText('chargingrulebasename.name')"
		cssClass="form-control"/>

	<s:submit cssClass="btn btn-primary" value="Search"	/>

</s:form>

<script type="text/javascript">

</script>
