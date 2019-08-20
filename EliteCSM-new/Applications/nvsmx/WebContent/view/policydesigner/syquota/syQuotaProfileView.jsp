<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<s:if test="syQuotaProfileData.syQuotaProfileDetailDatas.size != 0">
	<div class="col-xs-12" style="padding-top: 20px">
		<table class="table table-blue-stripped table-condensed table-bordered">
			<tr>
				<th><s:text name="sy.quotaprofile.detail.servicetype" /></th>
				<th><s:text name="sy.quotaprofile.detail.countername" /></th>
				<th><s:text name="sy.quotaprofile.detail.hsqvalue" /></th>
				<th><s:text name="sy.quotaprofile.detail.fup1value" /></th>
				<th><s:text name="sy.quotaprofile.detail.fup2value" /></th>
				<th style="width: 15%"><s:text name="sy.quotaprofile.detail.counterpresent" /></th>
			</tr>
			<s:iterator value="syQuotaProfileData.syQuotaProfileDetailDatas" var="syQuotaProfileDetailData">
				<tr>
					<td><s:property value="#syQuotaProfileDetailData.dataServiceTypeData.name"/></td>
					<td><s:property value="#syQuotaProfileDetailData.counterName"/></td>
					<td><s:property value="#syQuotaProfileDetailData.hsqValue"/></td>
					<td><s:property value="#syQuotaProfileDetailData.fup1Value"/></td>
					<td><s:property value="#syQuotaProfileDetailData.fup2Value"/></td>
					<td><s:property value="%{@com.elitecore.corenetvertex.constants.CounterPresence@fromValue(#syQuotaProfileDetailData.counterPresent).getDisplayVal()}"/></td>
				</tr>
			</s:iterator>
		</table>
	</div>
</s:if>
<s:else>
	<table class="table table-bordered" style="border: 0px; border-spacing: 0 !important; margin-bottom: 0px;">
		<tr role="row" style="background: #ecf1f8 !important;">
			<td style="text-align: center;" colspan="7"><s:text name="no.configured.service.wise.counter.defination"/></td>
		</tr>
	</table>
</s:else>