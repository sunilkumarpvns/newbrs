<%@ taglib prefix="s" uri="/struts-tags/ec"%>

<center>
	<div class="panel panel-primary success-failure-message" >
		<div class="panel-heading" >
			<span class="glyphicon glyphicon-ok"></span> <s:property value="getText('success.label')" />
		</div>
		<div class="panel-body" >
			<s:form namespace="/" action="%{#request.action}" >
				<s:if test="hasActionMessages()">
					<s:actionmessage />
				</s:if>				
				<s:submit cssClass="btn btn-primary form-control" value="Ok" name="Ok" cssStyle="width:10%;margin-left:45%;" />
			</s:form>
		</div>
	</div>
</center>
