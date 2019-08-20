<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@ taglib prefix="s" uri="/struts-tags/ec"%>
<center>
	<div class="panel panel-danger success-failure-message" >
		<div class="panel-heading" ><span class="glyphicon glyphicon-alert"></span>&nbsp;&nbsp;&nbsp;&nbsp;<s:property value="getText('auth.error.label')" /></div>
		<div class="panel-body">
		<%
			String LAST_URI = (String)request.getSession().getAttribute(Attributes.LAST_URI);			
			if(LAST_URI !=null && LAST_URI.contains("initCreate")){
		%>
		
			<s:form namespace="/" action="%{#request.action}">
				<b>You have tried to perform Unauthorized operation</b><br>
				<b>${requestScope.message}</b><br>
				<br><s:label name="getText('note.refer.logs')" />
				<s:submit cssClass="btn btn-danger form-control" value="Ok" name="Ok" cssStyle="width:10%;margin-left:45%;"/>
			</s:form>
			
		<%}else{%>
		
			<b>You have tried to perform Unauthorized operation</b><br>
			<b>${requestScope.message}</b><br>
			<br><s:label name="getText('note.refer.logs')" />							
			<input type="button" class="btn btn-danger form-control" value="Ok" name="Ok" style="width:10%;margin-left:45%;"  onclick="history.back()" />
			
		<%}%>
		</div>
	</div>
</center>
 