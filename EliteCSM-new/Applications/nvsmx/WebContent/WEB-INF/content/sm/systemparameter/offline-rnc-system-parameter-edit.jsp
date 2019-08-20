<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<style type="text/css">
.attribute-name {
	font-weight: bold;
	margin: auto;
	vertical-align: middle;
}

.form-group {
	margin-bottom: 0px;
}
</style>
<div class="panel panel-primary">
	<div class="panel-heading" style="padding: 8px 15px">
		<h3 class="panel-title" style="display: inline;">
			<s:text name="systemparameter.update" />
		</h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/sm/systemparameter" action="offline-rnc-system-parameter" id="offlineSystemParameters" method="post" cssClass="form-vertical">

			<s:hidden name="_method" value="put" />

			<div class="row">
				<div class="col-sm-12">
					<table class="table table-blue dataTable no-footer">
						<caption>
							<s:text name="fileparameter.title"></s:text>
						</caption>
						<col width="20%">
						<col width="20%">
						<col width="60%">
						<thead>
							<tr>
								<th><s:text name="header.name" /></th>
								<th><s:text name="header.value" /></th>
								<th><s:text name="header.description" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator var="fileSystemParameter" value="fileSystemParameters" status="stat">
								<tr>
									<s:hidden name="fileSystemParameters[%{#stat.index}].alias" />
									<td class="attribute-name"><s:text name="%{#fileSystemParameter.name}" /></td>
									<td><s:textfield id="%{#fileSystemParameter.alias}" name="fileSystemParameters[%{#stat.index}].value"
											cssClass="form-control" />
									</td>
									<td><s:text name="%{#fileSystemParameter.description}" /></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>

					<table class="table table-blue dataTable no-footer">
						<caption>
							<s:text name="ratingparameter.title"></s:text>
						</caption>
						<col width="20%">
						<col width="20%">
						<col width="60%">
						<thead>
							<tr>
								<th><s:text name="header.name" /></th>
								<th><s:text name="header.value" /></th>
								<th><s:text name="header.description" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator var="ratingSystemParameter"
								value="ratingSystemParameters" status="stat">
								<tr>
									<s:hidden name="ratingSystemParameters[%{#stat.index}].alias" />
									<td class="attribute-name"><s:text name="%{#ratingSystemParameter.name}" /></td>
									<td>
										<s:if test="%{offlineRncSystemParameterValuePoolMap.containsKey(#ratingSystemParameter.alias)}">
											<s:select name="ratingSystemParameters[%{#stat.index}].value"
	                                              list="offlineRncSystemParameterValuePoolMap.get(#ratingSystemParameter.alias)"
	                                              cssClass="form-control" listKey="val" listValue="displayValue"/>		
	                                    </s:if>
	                                    <s:elseif test="%{'SYSTEM_CURRENCY' == (#ratingSystemParameter.alias)}">
	                                    	<s:select name="ratingSystemParameters[%{#stat.index}].value"
	                                    	cssClass="form-control" list="isoCodeList" />
	                                    </s:elseif>
	                                    <s:elseif test="%{'SYSTEM_TIMEZONE' == (#ratingSystemParameter.alias)}">
	                                    	<s:select name="ratingSystemParameters[%{#stat.index}].value"
	                                    	 list="timeZoneList" />
	                                    </s:elseif>
	                                    <s:elseif test="%{'NUMBER_OF_DECIMAL_POINTS_IN_TRANSACTION' == (#ratingSystemParameter.alias)}">
	                                    	<s:select name="ratingSystemParameters[%{#stat.index}].value"
	                                              list="offlineRncSystemParameterValuePoolMap.get(#ratingSystemParameter.alias)"
	                                              cssClass="form-control" listKey="val" listValue="displayValue"/>	
	                                    </s:elseif>
	                                    <s:else>
	                                       	<s:textfield id="%{#ratingSystemParameter.alias}" name="ratingSystemParameters[%{#stat.index}].value"
												cssClass="form-control" />
	                                    </s:else>
									</td>
									<td><s:text name="%{#ratingSystemParameter.description}" /></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>
			</div>

			<div class="row">
				<div class="col-xs-12" align="center">
					<button class="btn btn-sm btn-primary" type="submit" role="button"
						formaction="${pageContext.request.contextPath}/sm/systemparameter/offline-rnc-system-parameter/update">
						<span class="glyphicon glyphicon-floppy-disk"></span>
						<s:text name="button.save" />
					</button>

					<button type="button" id="btnCancel" class="btn btn-primary btn-sm"
						value="Cancel" style="margin-right: 10px;"
						onclick="javascript:location.href='${pageContext.request.contextPath}/sm/systemparameter/offline-rnc-system-parameter/show'">
						<span class="glyphicon glyphicon-backward" title="Back"></span>
						<s:text name="systemparameter.view" />
					</button>
				</div>
			</div>
		</s:form>
	</div>
</div>
