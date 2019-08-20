<%@taglib uri="/struts-tags/ec" prefix="s"%>

<s:form namespace="/" action="globalpccrule/policydesigner/pccrule/PccRule/GlobalPccRule/setSearchCriteria" id="pccRuleSearch" method="post" cssClass="form-vertical">
	<h4>
		<s:text name="search.criteria"/>
	</h4>
	<s:textfield name="pccRule.name"  		id="pccRuleName"  	key="pccrule.name" 	cssClass="form-control"/>
	<s:select 	 name="pccRule.type"   	    id="pccRuleType"  	key="pccrule.type" cssClass="form-control"  list="@com.elitecore.nvsmx.policydesigner.controller.pccrule.PccRuleCTRL$PCCRuleType@values()" listValue="getValue()" listKey="getValue()"  headerValue="-- Select Type --" headerKey=""/>
	<s:select name="pccRule.dataServiceTypeData.id" key="pccrule.servicetype" cssClass="form-control" list="%{serviceTypeDataList}" listKey="id" listValue="%{name}" id="serviceData" headerValue="-- Select --" headerKey="" />
	<s:hidden name="pccRule.scope" value="%{@com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope@GLOBAL.name()}"/>
	<s:submit  cssClass="btn btn-primary" value="Search"></s:submit>
</s:form>
 	

