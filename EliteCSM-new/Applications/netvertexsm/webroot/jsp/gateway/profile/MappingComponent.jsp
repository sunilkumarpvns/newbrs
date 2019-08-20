<%
	String localPath = request.getContextPath();

	String index = request.getParameter("mappingIndexParam");
	String condionId = "ccCondition" + index;
	String packetType = "packetType" + index;
%>

<table width="100%" cellpadding="0" cellspacing="0" id="table<%=index %>">
	<tr>
		<td width="10" class="small-gap">&nbsp;</td>
		<td class="small-gap" colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="3">
		<table class="box" align="left" cellpadding="0" cellspacing="0" style="width: 100%">

			<!-- 		Request Mapping 		 -->
			<tr>
				<td width="10" class="small-gap">&nbsp;</td>
			</tr>
			<tr>
				<td align="left" class="labeltext" valign="middle" style="padding-left: 2.3em;">Condition</td>
				<td class="labeltext" valign="top" width="78%">
					<input type="text" maxlength="200" size="60" name="<%=condionId %>" />
				</td>
				<td class="labeltext" valign="top" width="3%"><img src="<%=localPath%>/images/minus.jpg" class="removeComp" style="padding-right: 5px; padding-top: 5px;" height="14" onclick="remove('1')"/></td>
			</tr>
			<tr><td width="10" class="small-gap">&nbsp;</td></tr>
			<tr><td width="10" class="small-gap">&nbsp;</td></tr>
			<tr><td width="10" class="small-gap">&nbsp;</td></tr>
			<tr><td width="10" class="small-gap">&nbsp;</td></tr>
			<tr><td width="10" class="small-gap">&nbsp;</td></tr>
			<tr>
				<td align="left" class="labeltext" valign="top" style="padding-left: 2em;" width="100%" colspan="2">
					<input type="radio" tabindex="19" name="<%=packetType %>" value="CC" checked="checked" />Credit Control Mapping
					<input type="radio" tabindex="20" name="<%=packetType %>" value="RAR" />RAR Mapping
				</td>
			</tr>
			<tr>
				<td width="10" class="">&nbsp;</td>
			</tr>
			<tr>
				<td class="" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 2.3em; font-weight: bold">
					<input type="button" id="reqBtn" value="Add Request Mapping" tabindex="2" class="light-btn" onclick="openPopupMap('1<%=index %>');"></td>
			</tr>
			<tr>
				<td width="10" class="small-gap">&nbsp;</td>
				<td class="small-gap" colspan="2">&nbsp;</td>
			</tr>

			<tr>
				<td class="btns-td" valign="middle" colspan="3">
				<table cellpadding="0" id="req<%=index %>" cellspacing="0" border="0" width="97%" class="">
					<tr class='L'>
						<td align="center" class="tblheader" valign="top" width="20%">Diameter Attribute</td>
						<td align="center" class="tblheader" valign="top" width="20%">Policy Key</td>
						<td align="center" class="tblheader" valign="top" width="20%">Default Value</td>
						<td align="center" class="tblheader" valign="top" width="30%">Value Mapping</td>
						<td align="center" class="tblheader" valign="top" width="5%">Remove</td>
					</tr>

				</table>
				</td>
			</tr>

			<tr>
				<td width="10" class="small-gap">&nbsp;</td>
				<td class="small-gap" colspan="2">&nbsp;</td>
			</tr>

			<!-- 		Response Mapping 		 -->

			<tr>
				<td class="" colspan="4" height="20%" style="font-size: 11px; line-height: 20px; padding-left: 2.3em; font-weight: bold">
					<input type="button" id="resBtn" value="Add Response Mapping" class="light-btn" tabindex="3" onclick="openPopupMap('2<%=index %>');">
				</td>
			</tr>
			<tr>
				<td width="10" class="small-gap">&nbsp;</td>
				<td class="small-gap" colspan="2">&nbsp;</td>
			</tr>

			<tr>
				<td class="btns-td" valign="middle" colspan="3">
				<table cellpadding="0" id="res<%=index %>" cellspacing="0" border="0" width="97%">
					<tr class='M'>
						<td align="center" class="tblheader" valign="top" width="20%">Diameter Attribute</td>
						<td align="center" class="tblheader" valign="top" width="20%">Policy Key</td>
						<td align="center" class="tblheader" valign="top" width="20%">Default Value</td>
						<td align="center" class="tblheader" valign="top" width="30%">Value Mapping</td>
						<td align="center" class="tblheader" valign="top" width="5%">Remove</td>
					</tr>

				</table>
				</td>
			</tr>

			<tr>
				<td align="left" class="labeltext" colspan="3" valign="middle">
				&nbsp;</td>
			</tr>

		</table>
		</td>
	</tr>
</table>

