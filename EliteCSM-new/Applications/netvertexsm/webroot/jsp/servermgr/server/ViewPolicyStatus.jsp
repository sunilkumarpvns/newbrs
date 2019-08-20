
<%@page import="com.elitecore.corenetvertex.data.PolicyDetail"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.core.util.mbean.data.live.EliteNetServerDetails"%>
<script src="<%=basePath%>/jquery/libs/datatables/jquery.dataTables.js">
$(document).ready(function() {
    $('#table').dataTable();
} );
</script>

<table width="97%" border="0" cellpadding="0" cellspacing="0"
	height="15%" align="right">

	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>

	<tr>
		<td class="tblheader-bold" colspan="7" height="20%"><bean:message
				bundle="servermgrResources" key="servermgr.policyStatus" /></td>
	</tr>

	<tr>
	
		<td align="left" class="tblheader" valign="top" width="1%"><bean:message
				bundle="servermgrResources" key="servermgr.serialnumber" /></td>
		<td align="left" class="tblheader" valign="top" width="10%"><bean:message
				bundle="servermgrResources" key="servermgr.policyStatus.name" /></td>
		<td align="left" class="tblheader" valign="top" width="5%"><bean:message
				bundle="servermgrResources" key="servermgr.policyStatus.status" /></td>
		<td align="left" class="tblheader" valign="top" width="25%"><bean:message
				bundle="servermgrResources" key="servermgr.policyStatus.remark" /></td>
	
	</tr>

	<logic:notEmpty name="policyDetails">
		<logic:iterate id="policyStatusData" name="policyDetails" type="com.elitecore.corenetvertex.data.PolicyDetail" indexId="iIndex">
		<tr>
			<td align="left" class="tblfirstcol" valign="top"><%=++iIndex%></td>
			<td align="left" class="tblrows" valign="top"><bean:write
					name="policyStatusData" property="name" /></td>
			<td align="left" class="tblrows" valign="top">
			<bean:write name="policyStatusData" property="status.status" /></td>
			<td align="left" class="tblcol" valign="top">
				<logic:notEmpty name="policyStatusData" property="remark">
					<bean:write name="policyStatusData" property="remark"/>&nbsp;
				</logic:notEmpty>
				<logic:empty name="policyStatusData" property="remark">--</logic:empty> 
			</td>
		</tr>
		</logic:iterate>
	</logic:notEmpty>

	<logic:empty name="policyDetails">
		<tr>
			<td align="center" class="tblfirstcol" colspan="8"><bean:message
				bundle="datasourceResources" key="database.datasource.norecordfound" /></td>
		</tr>
	</logic:empty>
	
</table>