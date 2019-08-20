<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form  method="post" cssClass="form-vertical">
	<h4>
		<s:text name="generic.search" />
	</h4>
	<s:textfield name="partnerRncGenericData.name" id="name"  	key="globalsearchdata.name" 	cssClass="form-control"/>
	<s:select name="partnerRncGenericData.partnerRnCModules" id="module"	key="globalsearchdata.policydesignermodules" cssClass="form-control" list="@com.elitecore.nvsmx.pd.controller.PartnerRnCModules@values()" listValue="getVal()" />
	<button type="submit" class="btn btn-primary btn-sm" role="submit" formaction="${pageContext.request.contextPath}/pd/genericpartnerrnc/partner-rnc-generic-search/*/search"></span> <s:text name="Search"/></button>
</s:form>
</body>
</html>