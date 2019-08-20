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
	document.forms[0].action.value = 'update';
	if(document.forms[0].reason.value == ''){
		alert('Reason must be specified');
		return false;
	}else{
		document.forms[0].submit();
	}
}
</script>

<html:form action="/updateConcurrentLoginPolicyStatus">
	<html:hidden name="updateConcurrentLoginPolicyStatusForm"
		styleId="action" property="action" />
	<html:hidden name="updateConcurrentLoginPolicyStatusForm"
		styleId="concurrentLoginId" property="concurrentLoginId" />
	<html:hidden name="updateConcurrentLoginPolicyStatusForm"
		styleId="status" property="status" />


	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		height="15%" align="right">
		<tr>
			<td class="tblheader-bold" colspan="3"><bean:message
					bundle="radiusResources" key="concurrentloginpolicy.currentstatus" /></td>
		</tr>

		<tr>
			<logic:equal name="updateConcurrentLoginPolicyStatusForm"
				property="status" value="CST01">
				<td width="30%" height="20%" class="captiontext" valign="top"><bean:message
						bundle="radiusResources" key="concurrentloginpolicy.isactive" /></td>
			</logic:equal>
			<logic:notEqual name="updateConcurrentLoginPolicyStatusForm"
				property="status" value="CST01">
				<td width="30%" height="20%" class="captiontext" valign="top"><bean:message
						bundle="radiusResources" key="concurrentloginpolicy.isinactive" /></td>
			</logic:notEqual>

			<td width="70%" height="20%" class="labeltext" valign="top">&nbsp;</td>
		</tr>

		<tr>
			<td width="30%" height="20%" class="labeltext" valign="top">&nbsp;</td>
			<td width="70%" height="20%" class="labeltext" valign="top">&nbsp;</td>
		</tr>

		<tr>
			<td class="tblheader-bold" colspan="3"><bean:message
					bundle="radiusResources"
					key="concurrentloginpolicy.change.information" /></td>
		</tr>

		<tr>
			<td width="30%" height="20%" class="captiontext" valign="top"><bean:message
					bundle="radiusResources" key="concurrentloginpolicy.changereason" /></td>
			<td width="70%" height="20%" class="labeltext" valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top"><html:textarea
								name="updateConcurrentLoginPolicyStatusForm" tabindex="1"
								styleId="reason" property="reason" cols="30" rows="4"
								style="width:250px" /> <font color="#FF0000"> *</font></td>
					</tr>
				</table>

			</td>
		</tr>

		<tr>
			<td width="30%" height="20%" class="labeltext">&nbsp;</td>
			<td width="70%" height="20%" class="labeltext"><logic:equal
					name="updateConcurrentLoginPolicyStatusForm" property="status"
					value="CST01">
					<input type="submit" tabindex="2" name="c_btnCreate"
						onclick="return customValidate();" value="   DeActivate   "
						class="light-btn" />&nbsp;&nbsp;
	            </logic:equal> <logic:notEqual
					name="updateConcurrentLoginPolicyStatusForm" property="status"
					value="CST01">
					<input type="submit" name="c_btnCreate"
						onclick="return customValidate();" value="   Activate   "
						class="light-btn" />&nbsp;&nbsp;	            
	            </logic:notEqual> <input type="reset" name="c_btnDeletePolicy" tabindex="3"
				onclick="javascript:location.href='<%=basePath%>/viewConcurrentLoginPolicy.do?concurrentLoginPolicyId=<bean:write name="updateConcurrentLoginPolicyStatusForm" property="concurrentLoginId"/>'"
				value=" Cancel " class="light-btn"></td>
		</tr>

	</table>
</html:form>


