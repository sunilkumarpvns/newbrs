<%@ page import="java.util.List"%>





<% 
	String localBasePath = request.getContextPath();
%>
<script>

	var dFormat;
	dFormat = 'dd-MMM-yyyy';	
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		//Get format from system parameter document.form[0].
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 

	}

function validateUpdate()
{
	document.forms[0].action.value = 'update';
	if(document.forms[0].reason.value == ''){
		alert('Reason must be specified');
	}else{
		document.forms[0].submit();
	}
}
</script>

<html:form action="/changeStaffStatus">
	<html:hidden name="changeStaffStatusForm" styleId="action"
		property="action" />
	<html:hidden name="changeStaffStatusForm" styleId="staffId"
		property="staffId" />
	<html:hidden name="changeStaffStatusForm" styleId="status"
		property="status" />
	<table width="97%" border="0" cellspacing="0" cellpadding="0"
		height="15%" align="right">
		<tr>
			<td class="tblheader-bold" colspan="3"><bean:message
					key="staff.statusinformation" /></td>
		</tr>
		<tr>
			<logic:equal name="changeStaffStatusForm" property="status"
				value="CST01">
				<td width="30%" height="20%" class="labeltext" valign="top"><bean:message
						key="staff.active" /></td>
			</logic:equal>
			<logic:notEqual name="changeStaffStatusForm" property="status"
				value="CST01">
				<td width="30%" height="20%" class="labeltext" valign="top"><bean:message
						key="staff.inactive" /></td>
			</logic:notEqual>
			<td width="70%" height="20%" class="labeltext" valign="top">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle">&nbsp;</td>
			<td class="btns-td" valign="middle"><input type="button"
				name="c_btnCreate" onclick="validateUpdate()" value="Update"
				class="light-btn"> <!-- 				           	<input type="button" name="c_btnReset"  onclick="validateReset()"   value="Reset"  class="light-btn">                   -->
				<input type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=localBasePath%>/initSearchStaff.do?/>'"
				value="Cancel" class="light-btn"></td>
		</tr>
		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
	</table>
</html:form>
