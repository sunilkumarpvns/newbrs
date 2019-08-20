<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page
	import="com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData"%>

<%
    String localBasePath = request.getContextPath();
    String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
    StaffData staffInstanceData = (StaffData)request.getAttribute("staffData");
%>

<script>
	var dFormat;
	dFormat = '<%=dateFormat%>';	
function popUpCalendar(ctl,	ctl2, datestyle)
{
		//Get format from system parameter document.form[0].
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 

}

   function echeck(str) {

		var at="@"
		var dot="."
		var lat=str.indexOf(at)
		var lstr=str.length
		var ldot=str.indexOf(dot)
		if (str.indexOf(at)==-1){
		   alert("Invalid E-mail ID")
		   return false
		}

		if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr){
		   alert("Invalid E-mail ID")
		   return false
		}

		if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
		    alert("Invalid E-mail ID")
		    return false
		}

		 if (str.indexOf(at,(lat+1))!=-1){
		    alert("Invalid E-mail ID")
		    return false
		 }

		 if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
		    alert("Invalid E-mail ID")
		    return false
		 }

		 if (str.indexOf(dot,(lat+2))==-1){
		    alert("Invalid E-mail ID")
		    return false
		 }
		
		 if (str.indexOf(" ")!=-1){
		    alert("Invalid E-mail ID")
		    return false
		 }

 		 return true					
	}


   function validateUpdate(){
	   document.forms[0].action.value='update';
	   	if(isNull(document.forms[0].name.value)){
	   		alert('Name is a compulsory field Please enter required data in the field');
	   	}else if(isNull(document.forms[0].address1.value)){
	   		alert('Address1 is a compulsory field Please enter required data in the field');
	   	}else if(isNull(document.forms[0].city.value)){
	   		alert('City is a compulsory field Please enter required data in the field');
	   	}else if(isNull(document.forms[0].state.value)){
	   		alert('State is a compulsory field Please enter required data in the field');
	   	}else if(isNull(document.forms[0].country.value)){
	   		alert('Country is a compulsory field Please enter required data in the field');
	       }else if(isNull(document.forms[0].zip.value)){
	           alert('Zip is a compulsory field Please enter required data in the field');
	       }else if(isNull(document.forms[0].emailAddress.value)){
	           alert('EMailAddress is a compulsory field Please enter required data in the field');
	   	}else if(!validatePhoneNumber(document.forms[0].phone)){
	   		alert('Enter valid Phone Number.');
	   	}else if(document.forms[0].mobile.value.length > 0 && !validateMobileNumber(document.forms[0].mobile.value)){
	   		alert('Enter valid Mobile Number.');
	   	}else if(document.forms[0].zip.value.length > 0 &&  !IsNumeric(document.forms[0].zip.value)){
	   		alert('Enter valid Pin Code.');
	   	}else{
	   		document.forms[0].submit();
	   	}
	   }	
	   // mobile number validation

	   function validateMobileNumber(val){
	   	
	   	nre=/^\+?(\d)+$/;
	   	var regexp = new RegExp(nre);
	   	if(!regexp.test(val))
	   	{
	   		return false;
	   		
	   	}
	   	return true;
	   }
	   
</script>
<html:form action="/updateStaff">
	<html:hidden name="updateStaffBasicDetailForm" styleId="action"
		property="action" />
	<html:hidden name="updateStaffBasicDetailForm" styleId="staffId"
		property="staffId" />
	<html:hidden name="updateStaffBasicDetailForm" styleId="auditUId"
		property="auditUId" />
	<html:hidden name="updateStaffBasicDetailForm" styleId="auditName"
		property="auditName" />
	<html:hidden name="updateStaffBasicDetailForm" styleId="userName"
		property="username" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3"><bean:message
								bundle="StaffResources" key="staff.logindetails" /></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources" key="staff.name" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.name" header="staff.name"/>
						</td>
						<td align="left" align="top" width="82%">
							<html:text styleId="name" tabindex="1" property="name" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources" key="staff.username" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.username" header="staff.username"/>
						</td>
						<td align="left" class="labeltext" tabindex="2" valign="top" width="30%">
							<bean:write name="updateStaffBasicDetailForm" property="username" />&nbsp;
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="StaffResources" key="staff.personaldetails" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%%">
							<bean:message bundle="StaffResources" key="staff.birthdate" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.birthdate" header="staff.birthdate"/>
						</td>
						<td align="left" valign="top">
							<html:text styleId="strStatusChangeDate" tabindex="3" property="strStatusChangeDate" size="10" maxlength="15" style="width:250px" />
							<font color="#FF0000"> *</font> 
							<a href="javascript:void(0)" onclick="popUpCalendar(this, document.forms[0].strStatusChangeDate)">
								<img src="<%=localBasePath%>/images/calendar.jpg" border="0" tabindex="6">
							</a>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources" key="staff.address1" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.address1" header="staff.address1"/>
						</td>
						<td align="left" valign="top">
							<html:text styleId="address1" tabindex="4" property="address1" size="50" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources" key="staff.address2" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.address2" header="staff.address2"/>
						</td>
						<td align="left" valign="top">
							<html:text styleId="address2" tabindex="5" property="address2" size="50" maxlength="30" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources" key="staff.city" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.city" header="staff.city"/>
						</td>
						<td align="left" valign="top" width="82%">
							<html:text styleId="city" property="city" tabindex="6" size="30" maxlength="30" style="width:250px" />
							<font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources" key="staff.state" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.state" header="staff.state"/>
						</td>
						<td align="left" valign="top" width="82%">
							<html:text styleId="state" property="state" tabindex="7" size="30" maxlength="30" style="width:250px" />
							<font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources" key="staff.country" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.country" header="staff.country"/>
						</td>
						<td align="left" valign="top" width="82%">
							<html:text styleId="country" property="country" tabindex="8" size="30" maxlength="30" style="width:250px" />
							<font color="#FF0000">*</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message  bundle="StaffResources" key="staff.pincode" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.pincode" header="staff.pincode"/>
						</td>
						<td align="left" valign="top">
							<html:text styleId="zip" property="zip" size="6" tabindex="9" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources" key="staff.emailaddress" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.emailaddress" header="staff.emailaddress"/>
						</td>
						<td align="left" valign="top">
							<html:text styleId="emailAddress" property="emailAddress" tabindex="10" size="40" maxlength="30" style="width:250px" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="2">
							<bean:message bundle="StaffResources" key="staff.contactdetails" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message  bundle="StaffResources" key="staff.phonenumber" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.phonenumber" header="staff.phonenumber"/>
						</td>
						<td align="left" valign="top" width="82%">
							<html:text styleId="phone" property="phone" tabindex="11" size="20" maxlength="30" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="StaffResources" key="staff.mobilenumber" />
							<ec:elitehelp headerBundle="StaffResources" text="staff.mobilenumber" header="staff.mobilenumber"/>
						</td>
						<td align="left" valign="top" width="82%">
							<html:text styleId="mobile" property="mobile" tabindex="12" size="20" maxlength="30" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle"><input type="button"
							name="c_btnCreate" tabindex="13" onclick="validateUpdate()"
							value="Update" class="light-btn"> <!-- 				                      	<input type="button" name="c_btnReset"  onclick="validateReset()"   value="Reset"  class="light-btn">                   -->
							<input type="reset" name="c_btnDeletePolicy" tabindex="14"
							onclick="javascript:location.href='<%=localBasePath%>/initSearchStaff.do?/>'"
							value="Cancel" class="light-btn"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
