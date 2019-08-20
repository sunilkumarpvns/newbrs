<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<style type="text/css">
.usage-rncpackage-th {
	font-weight: bold;
	width: 150px;
	text-align: left;
}

.usage-rncpackage-td {
	text-align: left;
	width: 150px;
	word-break: break-all;
}
</style>
<s:if test="rncPackageDetailsWrapperData.size != 0">
	<div class="col-xs-12" style="padding-top: 20px">
		
			<table
				class="table table-blue-stripped table-condensed table-bordered">
				<tr>
					<caption class="caption-header">
						<s:text name="rncpackage.rcglist" />
						</div>
					</caption>
					<th><s:text name="rncpackage.name"/></th>
					<th class="usage-rncpackage-th"><s:text name="rncpackage.peak" /></th>
					<th class="usage-rncpackage-th"><s:text name="rncpackage.offpeak" /></th>
					<th class="usage-rncpackage-th"><s:text name="rncpackage.weekend" /></th>
					<th class="usage-rncpackage-th"><s:text name="rncpackage.special" /></th>
				</tr>
				<s:iterator value="rncPackageDetailsWrapperData" var="test">
					<tr>
						<td class="usage-rncpackage-td">${name}</td>
						<td class="usage-rncpackage-td">${peakRateCard}</td>
						<td class="usage-rncpackage-td">${offPeakRateCard}</td>
						<td class="usage-rncpackage-td">${weekRateCard}</td>
						<td class="usage-rncpackage-td">${specialRateCard}</td>
					</tr>
				</s:iterator>
			</table>
	</div>
</s:if>
<s:else>
	<table class="table table-bordered"
		style="border: 0px; border-spacing: 0 !important; margin-bottom: 0px;">
		<tr role="row" style="background: #ecf1f8 !important;">
			<td style="text-align: center;" colspan="7"><s:text name="rncpackage.empty" /></td>
		</tr>
	</table>
</s:else>
