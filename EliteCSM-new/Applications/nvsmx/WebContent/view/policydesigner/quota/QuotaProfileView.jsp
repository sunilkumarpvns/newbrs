<%@taglib uri="/struts-tags/ec" prefix="s"%>
<table class="table table-bordered" style="border: 0px; border-spacing: 0 !important; margin-bottom: 0px;">
	<s:if test="%{fupLevelDetailMap.isEmpty()}">
		<tr role="row" style="background: #ecf1f8 !important;">
			<td style="text-align: center;" colspan="7">
			<s:text name="quotaprofile.no.fup.level"/></td>
		</tr>
	</s:if>
	<s:hidden name="groupIds" value="%{quotaProfile.pkgData.groups}" />
	<s:iterator value="fupLevelDetailMap" var="fupLevelMap">
		<tr role="row" style="background: #ecf1f8 !important;">
			<td style="text-align: left; word-wrap: break-word;"><s:text name="quotaprofile.fup"/> <s:property value="#fupLevelMap.key"/></td>

			<s:set var="aggregationKeyMap" value="#fupLevelMap.value"/>
			<td style="text-align: right; width: 100px;">

				<s:set var="billingCycle" value="%{#aggregationKeyMap[@com.elitecore.corenetvertex.constants.AggregationKey@BILLING_CYCLE.name()]}"/>
				<s:if test="#billingCycle != null">
					<s:if test="#billingCycle.total == null && #billingCycle.download == null && #billingCycle.upload == null">
						<s:property value="%{@com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@UNLIMITED_QUOTA_STRING}"/>
					</s:if>
					<s:else>
						<s:if test="#billingCycle.total != null"><div><s:property value="#billingCycle.total"/>&nbsp;<s:property value="#billingCycle.totalUnit"/><span style="padding-right:10px"/></div></s:if>
						<s:if test="#billingCycle.download != null"><div><s:property value="#billingCycle.download"/>&nbsp;<s:property value="#billingCycle.downloadUnit"/><span class='glyphicon glyphicon-arrow-down small-glyphicons' style='color:#3f6caa;float:right;width:10px'/></div></s:if>
						<s:if test="#billingCycle.upload != null"><div><s:property value="#billingCycle.upload"/>&nbsp;<s:property value="#billingCycle.uploadUnit"/><span class='glyphicon glyphicon-arrow-up small-glyphicons' style='color:#3f6caa;float:right;width:10px'/> </div></s:if>
					</s:else>
				</s:if>

			</td>

			<td style="text-align: right; width: 100px;">

				<s:set var="daily" value="%{#aggregationKeyMap[@com.elitecore.corenetvertex.constants.AggregationKey@DAILY.name()]}"/>
				<s:if test="#daily != null">
					<s:if test="#daily.total == null && #daily.download == null && #daily.upload == null">
						<s:property value="%{@com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@UNLIMITED_QUOTA_STRING}"/>
					</s:if>
					<s:else>
                        <s:if test="#daily.total != null"><div><s:property value="#daily.total"/>&nbsp; <s:property value="#daily.totalUnit"/><span style="padding-right:10px"/></div></s:if>
                        <s:if test="#daily.download != null"><div><s:property value="#daily.download"/>&nbsp;<s:property value="#daily.downloadUnit"/><span class='glyphicon glyphicon-arrow-down small-glyphicons' style='color:#3f6caa;float:right;width:10px'/></div></s:if>
                        <s:if test="#daily.upload != null"><div><s:property value="#daily.upload"/>&nbsp;<s:property value="#daily.uploadUnit"/><span class='glyphicon glyphicon-arrow-up small-glyphicons' style='color:#3f6caa;float:right;width:10px'/> </div></s:if>
					</s:else>
				</s:if>

			</td>

			<td style="text-align: right; width: 100px;">

				<s:set var="weekly" value="%{#aggregationKeyMap[@com.elitecore.corenetvertex.constants.AggregationKey@WEEKLY.name()]}"/>
				<s:if test="#weekly != null">
                    <s:if test="#weekly.total == null && #weekly.download == null && #weekly.upload == null">
                        <s:property value="%{@com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@UNLIMITED_QUOTA_STRING}"/>
                    </s:if>
                    <s:else>
                        <s:if test="#weekly.total != null"><div><s:property value="#weekly.total"/>&nbsp;<s:property value="#weekly.totalUnit"/><span style="padding-right:10px"/></div></s:if>
                        <s:if test="#weekly.download != null"><div><s:property value="#weekly.download"/>&nbsp;<s:property value="#weekly.downloadUnit"/><span class='glyphicon glyphicon-arrow-down small-glyphicons' style='color:#3f6caa;float:right;width:10px'/></div></s:if>
                        <s:if test="#weekly.upload != null"><div><s:property value="#weekly.upload"/>&nbsp;<s:property value="#weekly.uploadUnit"/><span class='glyphicon glyphicon-arrow-up small-glyphicons' style='color:#3f6caa;float:right;width:10px'/> </div></s:if>
                    </s:else>
				</s:if>

			</td>

			<td style="text-align: right; width: 100px;">

				<s:set var="custom" value="%{#aggregationKeyMap[@com.elitecore.corenetvertex.constants.AggregationKey@CUSTOM.name()]}"/>
				<s:if test="#custom != null">
                    <s:if test="#custom.total == null && #custom.download == null && #custom.upload == null">
                        <s:property value="%{@com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@UNLIMITED_QUOTA_STRING}"/>
                    </s:if>
                    <s:else>
                        <s:if test="#custom.total != null"><div><s:property value="#custom.total"/>&nbsp;<s:property value="#custom.totalUnit"/><span style="padding-right:10px"/></div></s:if>
                        <s:if test="#custom.download != null"><div><s:property value="#custom.download"/>&nbsp;<s:property value="#custom.downloadUnit"/><span class='glyphicon glyphicon-arrow-down small-glyphicons' style='color:#3f6caa;float:right;width:10px'/></div></s:if>
                        <s:if test="#custom.upload != null"><div><s:property value="#custom.upload"/>&nbsp;<s:property value="#custom.uploadUnit"/><span class='glyphicon glyphicon-arrow-up small-glyphicons' style='color:#3f6caa;float:right;width:10px'/> </div></s:if>
                    </s:else>
				</s:if>

			</td>
			<td style="width: 20px;"></td>
			<td style="width: 20px;"></td>


		</tr>
	</s:iterator>
</table>

