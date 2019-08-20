<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>

<%
Attributes.count = 0;
%>
<s:form namespace="/" action="commons/audit/Audit/searchCriteria" id="pkgSearch" method="post" cssClass="form-vertical">
	<h4>
		<s:text name="search.criteria"/>
	</h4>
	<s:datepicker name="auditCriteriaData.fromDate" key="fromdate" parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss" readonly="true" showAnim="slideDown" id="fromDate" />
	<s:datepicker name="auditCriteriaData.toDate" key="todate" parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss" readonly="true" showAnim="slideDown" id="toDate" />
	<s:textfield id="user" name="auditCriteriaData.staffUserName" key="staff.search" cssClass="form-control"/>
	<s:submit  cssClass="btn btn-primary text-center" value="Search"></s:submit>
</s:form>
<html><head><style>
button.ui-datepicker-current { 
	display: none; 
}
</style></head></html>

