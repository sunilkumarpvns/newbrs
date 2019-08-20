<%@ include file="/jsp/core/includes/common/Header.jsp"%>






<%
    String localBasePath = request.getContextPath();
%>
<script>
	var dFormat;
	dFormat = 'dd-MMM-yyyy';	
function popUpCalendar(ctl,	ctl2, datestyle)
{
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 
}
function validateUpdate(){
		
		if(isNull(document.forms[0].newPassword.value)){
			alert('Password must be specified');
		}else if(isNull(document.forms[0].newConfirmPassword.value)){
			alert('ConfirmPassword must be specified');
		}else if(document.forms[0].newPassword.value == document.forms[0].newConfirmPassword.value){
			document.forms[0].action.value='update';
			return true;
		}else{
			alert('Password not match');
		}
		return false;
}

</script>
<script language="javascript1.2"
	src="<%=localBasePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript1.2">
    function checkPasswordStrength(password) {
   	  
   		var score = chkPass(password);
   		
   		if(score == '0')
   		{
    	 document.getElementById("newPassword").style.backgroundColor= "#ffffff";
		 document.getElementById("passwordStrengthStatus").innerHTML = "";
		}
		if(score == '1')
   		{
   		
   		document.getElementById("newPassword").style.backgroundColor= "#ff5555";
   		document.getElementById("passwordStrengthStatus").innerHTML = "Weak";
     	
		}
		if(score == '2')
   		{
     	document.getElementById("newPassword").style.backgroundColor= "#ffaa55";
		document.getElementById("passwordStrengthStatus").innerHTML = "Medium";
		}
		if(score == '3')
   		{
     	document.getElementById("newPassword").style.backgroundColor= "#d5ffaa";
		document.getElementById("passwordStrengthStatus").innerHTML = "Strong";
		}
		
		
		
  }
</script>
<html:form action="/changeStaffPassword"
	onsubmit="return validateUpdate();">
	<html:hidden name="changeStaffPasswordForm" styleId="action"
		property="action" />
	<html:hidden name="changeStaffPasswordForm" styleId="password"
		property="password" />
	<html:hidden name="changeStaffPasswordForm" styleId="staffId"
		property="staffId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="StaffResources"	key="staff.newinformation" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" width="30%">
							<bean:message bundle="StaffResources"	key="staff.password" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.password" header="staff.password"/>
						</td>
						<!-- 						              <td align="left" class="labeltext" valign="top" width="82%" colspan="2"> -->
						<td align="left" align="left" colspan="2">
							<html:password styleId="newPassword" tabindex="1" property="newPassword" size="20" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources"	key="staff.confirmpassword" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.confirmpassword" header="staff.confirmpassword"/>
						</td>
						<!-- 						              <td align="left" align="top" width="82%" >   -->
						<td align="left" align="left" colspan="2"><html:password
								styleId="newConfirmPassword" tabindex="2"
								property="newConfirmPassword" size="20" style="width:250px" /><font
							color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle"><input type="submit"
							name="c_btnCreate" tabindex="3" value="Update" class="light-btn">
							<!--			                      	<input type="button" name="c_btnReset"  onclick="validateReset()"   value="Reset"  class="light-btn">                   -->
							<input type="reset" name="c_btnDeletePolicy" tabindex="4"
							onclick="javascript:location.href='<%=localBasePath%>/initSearchStaff.do?/>'"
							value="Cancel" class="light-btn"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>