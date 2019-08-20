<%@ taglib prefix="s" uri="/struts-tags/ec"%>
<%@page isErrorPage="true" %>

<center>
	<div class="panel panel-danger success-failure-message" >
		<div class="panel-heading"><span class="glyphicon glyphicon-alert"></span><s:property value="getText('error.label')" /></div>
		<div class="panel-body">			
			<s:label label="Found Unhandled Exception while Performing the Operation"/>			
			<s:label label="Message :" value='%{exception}' />
			<s:set var="logText" >
				<s:text name="note.refer.logs" />
			</s:set>
			<s:property value="%{#logText}" escapeHtml="false"/>
		</div>
	</div>
</center>
 