<%--<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri= "http://displaytag.sf.net" prefix="display"%>

<s:form namespace="/" action="policydesigner/emergency/EmergencyPkg/searchCriteria" id="pkgSearch" method="post" cssClass="form-vertical" validate="true" onsubmit="return validateForm();">
	<h4>
		<s:text name="search.criteria"/>
	</h4>
	<s:textfield name="pkgData.name"  		id="pkgName"  	key="pkg.name" 	cssClass="form-control"/>
	<s:textfield name="pkgData.price" 		id="pkgPrice"  	key="pkg.price" 	cssClass="form-control"/>
	<s:select 	 name="pkgData.status"   	id="pkgStatus"	key="pkg.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" headerValue="-- Select Status --" headerKey=""/>
	<s:select    name="pkgData.packageMode" id="pkgMode" 	key="pkg.mode" 	cssClass="form-control"	list="@com.elitecore.corenetvertex.pkg.PkgMode@values()" headerValue="-- Select Mode --" headerKey="" />
	<s:select 	 name="pkgData.type" 		id="pkgType"	key="pkg.type"	cssClass="form-control" list="@com.elitecore.corenetvertex.pkg.PkgType@values()" headerValue="-- Select Type --" headerKey=""/>
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
	var price = $("#pkgPrice").val();
	return isPositiveDecimalNumber('pkgPrice',price);
}

</script>
 	

 --%>