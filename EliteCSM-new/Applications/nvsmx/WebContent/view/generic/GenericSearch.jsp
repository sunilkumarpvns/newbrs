<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form action="policydesigner/util/Generic/setSearchCriteria" method="post" cssClass="form-vertical">
	<h4>
		<s:text name="generic.search" />
	</h4>
	<s:textfield name="genericSearchData.name"  				  	id="name"  	key="globalsearchdata.name" 	cssClass="form-control"/>
	<s:select 	 name="genericSearchData.status"   					id="status"	key="globalsearchdata.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" headerValue="-- Select Status --" headerKey="" labelCssClass="hide" elementCssClass="hide" />
	<s:select 	 name="genericSearchData.policyDesignerModules"   	id="module"	key="globalsearchdata.policydesignermodules" cssClass="form-control" list="@com.elitecore.nvsmx.policydesigner.PolicyDesignerModules@values()" listValue="getVal()" />
	<s:submit  	 cssClass="btn btn-primary" value="Search"></s:submit>
</s:form>
</body>
</html>