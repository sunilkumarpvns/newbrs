 

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
function validateUpdate(){
	document.forms[0].action.value='update';
	if(document.forms[0].userName.value == ''){
		alert('Staff UserName must be specified');
	}else{
		document.forms[0].submit();
	}
}
</script>          
<html:form action="/changeUserName">
<html:hidden name="changeUserNameForm" styleId="action" 	property="action" />
<html:hidden name="changeUserNameForm" styleId="staffId" 	property="staffId"/>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
			        <tr>
						<td valign="top" align="right"> 
				 		 	<table cellpadding="0" cellspacing="0" border="0" width="97%" height="15%" > 
								 <tr > 
						              <td align="left" class="tblheader-bold" valign="top" colspan="3" ><bean:message key="staff.newinformation"/></td>
					             </tr>
					             <tr>
					              	<td>&nbsp;</td>
					              </tr>
					             <tr > 
						              <td align="left" class="labeltext"  width="30%" ><bean:message key="staff.username"/></td>
						              <td align="left" align="left" colspan="2"> 
							              <html:text styleId="userName" property="userName" size="20" maxlength="15"/><font color="#FF0000"> *</font>
				  	                  </td>
				 				 </tr>
				 				 <tr>
					              	<td>&nbsp;</td>
					              </tr>
					              <tr>
					              	<td>&nbsp;</td>
					              </tr>
			  	                 <tr > 
					                <td class="btns-td" valign="middle" >&nbsp;</td>
				                    <td class="btns-td" valign="left" >
				                      	<input type="button" name="c_btnCreate"  onclick="validateUpdate()"  value="Update"  class="light-btn">                   
<!--			                      	<input type="button" name="c_btnReset"  onclick="validateReset()"   value="Reset"  class="light-btn">                   -->
				                      	<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/initSearchStaff.do?/>'"   value="Cancel" class="light-btn"> 
					                </td>
                				</tr>
				             </table>
 			             </td>  
			          </tr>  
			      </table>
</html:form>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
