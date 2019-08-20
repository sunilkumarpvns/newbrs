<%@ taglib prefix="s" uri="/struts-tags/ec"%>
<center>
	<div class="panel panel-danger success-failure-message" >
		<div class="panel-heading" ><span class="glyphicon glyphicon-alert"></span> <s:property value="getText('error.label')" /></div>
		<div class="panel-body" >
			<s:if test="hasActionErrors()">
				<s:actionerror />
				<s:set var="logText" >
					<s:text name="note.refer.logs" />
				</s:set>
				<s:property value="%{#logText}" escapeHtml="false"/>
			</s:if>
			<s:else>
				<s:label name="getText('error.while.operation')" />
				<s:set var="logText" >
					<s:text name="note.refer.logs" />
				</s:set>
				<s:property value="%{#logText}" escapeHtml="false"/>
			</s:else>
			<div class="row">
				<div class="col-xs-12" align="center">
					<button type="button"
						id="btnOk"
						class="btn btn-danger btn-sm"
						value="Ok"
						onclick="javascript:location.href='<%=request.getHeader("referer")%>'">
						<s:text name="OK"/>
					</button>
				</div>
			</button>
		</div>
	</div>
</center>