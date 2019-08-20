<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv"%>
<style type="text/css">
.attribute-name {
	font-weight: bold;
}
.tableFormate {
	clear: both; 
	margin-bottom: 6px;
	max-width: none;
}
</style>
<div class="panel-body">
	<table class="table table-blue no-footer tableFormate">
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
			<s:iterator var="fileSystemParameter" value="fileSystemParameters"
				status="stat">
				<tr>
					<td class="attribute-name"><s:text
							name="%{#fileSystemParameter.name}" /></td>
					<td><s:text name="%{#fileSystemParameter.value}" /></td>
					<td><s:text name="%{#fileSystemParameter.description}" /></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>

	<table class="table table-blue no-footer tableFormate">
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
					<td class="attribute-name"><s:text
							name="#ratingSystemParameter.name" /></td>
					<td><s:text name="#ratingSystemParameter.value" /></td>  
					<td><s:text name="#ratingSystemParameter.description" /></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</div>

