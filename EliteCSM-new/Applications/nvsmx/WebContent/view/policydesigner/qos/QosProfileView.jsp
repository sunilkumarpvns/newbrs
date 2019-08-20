<%@taglib uri="/struts-tags/ec" prefix="s"%>
<table class="table table-bordered" style="border: 0px; border-spacing: 0 !important; margin-bottom: 0px;">
	<s:if test="%{qosProfile.qosProfileDetailDataList.isEmpty()}">
		<tr role="row" style="background: #ecf1f8 !important;">
			<td style="text-align: center;" colspan="7">
			<s:text name="qosprofile.no.fup.level"></s:text></td>
		</tr>
	</s:if>
	<s:hidden name="groupIds" value="%{qosProfile.pkgData.groups}" />
	<s:iterator value="qosProfile.qosProfileDetailDataList">
		<tr role="row" style="background: #ecf1f8 !important;">
			<td style="text-align: left; word-wrap: break-word;"><s:text name="qosprofile.fup"/>${fupLevel}</td>
			<td style="text-align: right; width: 100px;">
				<s:if test="mbrdl != null"><div>${mbrdl}&nbsp;${mbrdlUnit}<span class='glyphicon glyphicon-arrow-down small-glyphicons' style='color:#3f6caa;float:right;width:10px'/></div></s:if>
				<s:if test="mbrul != null"><div>${mbrul}&nbsp;${mbrulUnit}<span class='glyphicon glyphicon-arrow-up small-glyphicons' style='color:#3f6caa;float:right;width:10px'/> </div></s:if>
			</td>			
			<td style="text-align: right; width: 100px;">
				<s:if test="aambrdl != null"><div>${aambrdl}&nbsp;${aambrdlUnit}<span class='glyphicon glyphicon-arrow-down small-glyphicons' style='color:#3f6caa;float:right;width:10px'/></div></s:if>
				<s:if test="aambrul != null"><div>${aambrul}&nbsp;${aambrulUnit}<span class='glyphicon glyphicon-arrow-up small-glyphicons' style='color:#3f6caa;float:right;width:10px'/></div></s:if>
			</td>
			<td style="text-align: right; width: 100px;">
				<s:property value="qci"/>
			</td>
			<td style="width: 20px;"></td>
			<td style="width: 20px;"></td>
		</tr>
	</s:iterator>
</table>