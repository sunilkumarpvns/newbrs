<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form namespace="/" action="policydesigner/ims/IMSPkg/setSearchCriteria" id="imsPkgSearch" method="post" cssClass="form-vertical" validate="true" onsubmit="return validateForm();">
	<h4>
		<s:text name="search.criteria"/>
	</h4>
	<s:textfield name="imsPkgData.name"  		id="imsPkgName"  	key="getText('ims.pkg.name')" 	cssClass="form-control"/>
    <s:set var="priceTag">
        <s:property value="getText('ims.pkg.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
    </s:set>
	<s:textfield name="imsPkgData.price" 		id="imsPrice"  	key="priceTag" 	cssClass="form-control"/>
	<s:select 	 name="imsPkgData.status"   	id="imsPkgStatus"	key="getText('ims.pkg.status')" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" headerValue="-- Select Status --" headerKey=""/>
	<s:select    name="imsPkgData.packageMode"  id="imsPkgMode" 	key="getText('ims.pkg.mode')" 	cssClass="form-control"	list="@com.elitecore.corenetvertex.pkg.PkgMode@values()" headerValue="-- Select Mode --" headerKey="" />
	<s:submit  cssClass="btn btn-primary" value="Search"></s:submit>
</s:form>
<style>
<!--
.form-control-feedback{
	line-height : 80px;
}
-->
</style>
<script type="text/javascript">
function validateForm(){
	var price = $("#imsPrice").val();
	return isPositiveDecimalNumber("imsPrice",price);
}

</script>
 	

