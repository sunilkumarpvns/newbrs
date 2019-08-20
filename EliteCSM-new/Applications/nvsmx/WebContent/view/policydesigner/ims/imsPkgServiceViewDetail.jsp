<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<s:if test="imsPkgServiceData.expression != null">
	<div class="col-sm-12">
		<div class="row">
			<div class="col-xs-3 col-sm-2" style="font-weight:700; margin-bottom: 10px;margin-top: 10px"><s:text name="ims.pkg.pcc.expression" /> </div>
			<div class="col-xs-9 col-sm-10" style="margin-bottom: 10px;margin-top: 10px"><s:property value="imsPkgServiceData.expression"/>
			</div> 
		</div>
	</div>
	<div class="col-xs-12">
</s:if>
<s:else>
<div class="col-xs-12" style="padding-top: 20px">
</s:else>
	<table class="table table-blue-stripped table-condensed table-bordered">
		<caption class="caption-header">
			<s:text name="ims.pkg.pcc.attributes.restriction" />
		</caption>
		<s:if test="imsPkgServiceData.imsPkgPCCAttributeDatas.size != 0">
				<tr>
					<th><s:text name="ims.pkg.pcc.attributes" /></th>
					<th><s:text name="ims.pkg.pcc.expression" /></th>
					<th><s:text name="ims.pkg.pcc.action" /></th>
					<th><s:text name="ims.pkg.pcc.value" /></th>
				</tr>
				<s:iterator value="imsPkgServiceData.imsPkgPCCAttributeDatas" var="imsPkgPCCAttributeData">
				<tr>
					<td><s:property value="%{#imsPkgPCCAttributeData.attribute.displayValue}"/></td>
					<td><s:property value="%{#imsPkgPCCAttributeData.expression}"/></td>
					<td><s:property value="%{#imsPkgPCCAttributeData.action.val}"/></td>
					<td><s:property value="%{#imsPkgPCCAttributeData.value}"/></td>
				</tr>
				</s:iterator>
			</tbody>
		</s:if>
		<s:else>
			<tr>
				<td style="text-align: center;" colspan="4">
					<s:text name="no.ims.pkg.pcc.attribute" />
				</td>
			</tr>
		</s:else>
	</table>
</div>