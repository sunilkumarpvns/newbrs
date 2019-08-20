<%@ page import="java.util.List"%>






<script>

	var dFormat;
	dFormat = 'dd-MMM-yyyy';	
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		//Get format from system parameter document.form[0].
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 

	}

function customValidate()
{
/*
	alert(document.viewRadiusPolicyForm.lastUpdated.value);
		alert(document.viewRadiusPolicyForm.createDate.value);
			alert(document.viewRadiusPolicyForm.statusChangeDate.value);
*/			
	document.updateRadiusPolicyStatusForm.action.value = 'update';
	var retVar;
	retVar = true;
	return retVar;
}
</script>

<html:form action="/updateRadiusPolicyStatus">
	<html:hidden name="updateRadiusPolicyStatusForm" styleId="action"
		property="action" />
	<html:hidden name="updateRadiusPolicyStatusForm"
		styleId="radiusPolicyId" property="radiusPolicyId" />
	<html:hidden name="updateRadiusPolicyStatusForm" styleId="status"
		property="status" />


	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		height="15%">
		<tr>
			<td class="tblheader-bold" colspan="3"><bean:message
					bundle="radiusResources" key="radiuspolicy.currentstatus" /></td>
		</tr>

		<tr>
			<logic:equal name="updateRadiusPolicyStatusForm" property="status"
				value="CST01">
				<td width="30%" height="20%" class="labeltext" valign="top"><bean:message
						bundle="radiusResources" key="radiuspolicy.radiuspolicy.isactive" /></td>
			</logic:equal>
			<logic:notEqual name="updateRadiusPolicyStatusForm" property="status"
				value="CST01">
				<td width="30%" height="20%" class="labeltext" valign="top"><bean:message
						bundle="radiusResources"
						key="radiuspolicy.radiuspolicy.isinactive" /></td>
			</logic:notEqual>

			<td width="80%" height="20%" class="labeltext" valign="top">&nbsp;</td>
		</tr>

		<tr>
			<td width="20%" height="20%" class="labeltext" valign="top">&nbsp;</td>
			<td width="60%" height="20%" class="labeltext" valign="top">&nbsp;</td>
		</tr>

		<tr>
			<td class="tblheader-bold" colspan="3"><bean:message
					bundle="radiusResources" key="radiuspolicy.change.information" /></td>
		</tr>


		<tr>
			<td height="20%" class="captiontext" valign="top"><bean:message
					bundle="radiusResources" key="radiuspolicy.changereason" /></td>
			<td height="20%" class="labeltext" valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td><html:textarea name="updateRadiusPolicyStatusForm"
								styleId="reason" property="reason" cols="30" rows="4"
								style="width:250px" /></td>
					</tr>
				</table>

			</td>
		</tr>

		<tr>
			<td height="20%" class="labeltext">&nbsp;</td>
			<td height="20%" class="labeltext"><logic:equal
					name="updateRadiusPolicyStatusForm" property="status" value="CST01">
					<input type="submit" name="c_btnCreate"
						onclick="return customValidate();" value="   DeActivate   "
						class="light-btn" />&nbsp;&nbsp;
	            </logic:equal> <logic:notEqual name="updateRadiusPolicyStatusForm"
					property="status" value="CST01">
					<input type="submit" name="c_btnCreate"
						onclick="return customValidate();" value="   Activate   "
						class="light-btn" />&nbsp;&nbsp;	            
	            </logic:notEqual> <input type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=basePath%>/viewRadiusPolicy.do?radiusPolicyId=<bean:write name="radiusPolicyBean" property="radiusPolicyId"/>'"
				value=" Cancel " class="light-btn"></td>
		</tr>

	</table>
</html:form>

