<%@ include file="/jsp/core/includes/common/Header.jsp"%>


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
	alert('update button pressed')
	document.forms[0].action.value='update';
	document.forms[0].submit();
}
</script>
<html:form action="/editStaff">
	<html:hidden name="editStaffForm" styleId="action" property="action" />
	<html:hidden name="editStaffForm" styleId="staffId" property="staffId" />
	<table cellpadding="0" cellspacing="0" border="0" width="828">
		<tr>
			<td width="10">&nbsp;</td>
			<td colspan="2">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="26" rowspan="2" valign="top"><img
						src="<%=basePath%>/images/left-curve.jpg" border="0"></td>
					<td width="133" align="center" class="page-header"
						background="<%=basePath%>/images/header-gradient.jpg" rowspan="2"><bean:message
						key="accesspolicy.accesspolicy" /></td>
					<td width="32" rowspan="2"><img
						src="<%=basePath%>/images/right-curve.jpg" border="0"></td>
					<td width="633"><a href="#"><img
						src="<%=basePath%>/images/csv.jpg" name="Image1"
						onmouseover="MM_swapImage('Image1','','<%=basePath%>/images/csv-hover.jpg',1)"
						onmouseout="MM_swapImgRestore()" alt="Save as CSV" border="0"></a><a
						href="#"><img src="<%=basePath%>/images/pdf.jpg"
						name="Image2"
						onmouseover="MM_swapImage('Image2','','<%=basePath%>/images/pdf-hover.jpg',1)"
						onmouseout="MM_swapImgRestore()" alt="Save as PDF" border="0"></a><a
						href="#"><img src="<%=basePath%>/images/html.jpg"
						name="Image3"
						onmouseover="MM_swapImage('Image3','','<%=basePath%>/images/html-hover.jpg',1)"
						onmouseout="MM_swapImgRestore()" alt="Save as HTML" border="0"></a><a
						href="#"><img src="<%=basePath%>/images/filter.jpg"
						name="Image4"
						onmouseover="MM_swapImage('Image4','','<%=basePath%>/images/filter-hover.jpg',1)"
						onmouseout="MM_swapImgRestore()" alt="Apply Filter" border="0"></a></td>
				</tr>
				<tr>
					<td width="633" valign="bottom"><img
						src="<%=basePath%>/images/line.jpg" border="0"></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" width="7">&nbsp;</td>
		</tr>
		<tr>
			<td width="10">&nbsp;</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0"
				width="100%">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="table-header" colspan="5"><bean:message
						key="staff.updatestaff" /></td>
				</tr>
				<tr>
					<td class="small-gap" colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td class="btns-td" valign="middle" colspan="3">
					<table cellpadding="0" cellspacing="0" border="0" width="100%"
						height="30%">
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="3"><bean:message
								key="staff.logindetails" /></td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top" width="18%"><bean:message
								key="staff.name" /></td>
							<td align="left" class="tblcol" valign="top" width="30%">Abcd</td>
							<td align="left" class="tblcol" valign="top" width="20%"><img
								src="<%=basePath%>/images/active.jpg" alt="Active">
							&nbsp;&nbsp;&nbsp;&nbsp;Active</td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top" width="18%"><bean:message
								key="staff.username" /></td>
							<td align="left" class="tblcol" valign="top" width="30%">abcd&nbsp;&nbsp;
							<a href="/jsp/cascustomer/ChangeUserNamePopup.jsp"
								onclick="MM_openBrWindow('','wndChangeUname','width=600,height=250')"
								target="wndChangeUname"></a></td>
							<td align="left" class="tblcol" valign="top" width="20%"><a
								href="/servlet/staff?c_strActionMode=102211014&strSystemUserId=STU000003845"
								onclick="MM_openBrWindow('','wndAllRights','scrollbars=yes,width=1015,height=700')"
								target="wndAllRights"> <img src="<%=basePath%>/images/view.jpg"
								name="viewAccRight"
								onmouseover="MM_swapImage('viewAccRight','','<%=basePath%>/images/view-hover.jpg',1)"
								onmouseout="MM_swapImgRestore()" border="0"></a>
							&nbsp;&nbsp;&nbsp;&nbsp;Access Rights</td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top" width="18%%"><bean:message
								key="staff.birthdate" /></td>
							<td align="left" class="tblcol" valign="top" colspan="2">
							01/Oct/05</td>
						</tr>
						<tr>
							<td align="left" class="tblfirstcol" valign="top" width="18%"><bean:message
								key="staff.emailaddress" /></td>
							<td align="left" class="tblcol" valign="top" colspan="2">test@elitecore.com</td>
						</tr>
					</table>
					<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="3"><bean:message
								key="staff.logindetails" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.name" /></td>
							<td align="left" class="labeltext" valign="top" width="82%">
							<html:text styleId="name" property="name" size="40"
								maxlength="30" /><font color="#FF0000"> *</font></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.username" /></td>
							<td align="left" class="labeltext" valign="top" width="30%">abcd&nbsp;&nbsp;
							</td>

						</tr>
					</table>
					<table cellpadding="0" cellspacing="0" border="0" width="100%" class="box" >
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="2"><bean:message
								key="staff.personaldetails" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%%"><bean:message
								key="staff.birthdate" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="strStatusChangeDate"
								property="strStatusChangeDate" size="10" maxlength="15" /><font
								color="#FF0000"> *</font> <a href="javascript:void(0)"
								onclick="popUpCalendar(this, document.forms[0].strStatusChangeDate)">
							<img src="<%=basePath%>/images/calendar.jpg" border="0"
								tabindex="6"> </a></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.address1" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="address1" property="address1" size="50"
								maxlength="30" /><font color="#FF0000"> *</font></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.address2" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="address2" property="address2" size="50"
								maxlength="30" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.city" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2"
								width="82%"><html:text styleId="city" property="city"
								size="30" maxlength="30" /><font color="#FF0000"> *</font></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.state" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2"
								width="82%"><html:text styleId="state" property="state"
								size="30" maxlength="30" /><font color="#FF0000"> *</font></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.country" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2"
								width="82%"><html:text styleId="country"
								property="country" size="30" maxlength="30" /><font
								color="#FF0000"> *</font></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.pincode" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="zip" property="zip" size="6" maxlength="30" /><font
								color="#FF0000"> *</font></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.emailaddress" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="emailAddress" property="emailAddress"
								size="40" maxlength="30" /><font color="#FF0000"> *</font></td>
						</tr>
					</table>
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td align="left" class="tblheader-bold" valign="top" colspan="2"><bean:message
								key="staff.contactdetails" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.phonenumber" /></td>
							<td align="left" class="labeltext" valign="top" width="82%">
							<html:text styleId="phone" property="phone" size="20"
								maxlength="30" /></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%"><bean:message
								key="staff.mobilenumber" /></td>
							<td align="left" class="labeltext" valign="top" width="82%">
							<html:text styleId="phone" property="phone" size="20"
								maxlength="30" /></td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle">&nbsp;</td>
							<td class="btns-td" valign="middle" colspan="2"><input
								type="button" name="c_btnCreate" onclick="validateUpdate()"
								value="Update" class="light-btn"> <input type="button"
								name="c_btnReset" onclick="validateReset()" value="Reset"
								class="light-btn"> <input type="reset"
								name="c_btnDeletePolicy"
								onclick="javascript:location.href='/servlet/AccessPolicy?c_strActionMode=102231004'"
								value="Cancel" class="light-btn"></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<table>
		<tr>
			<td width="10">&nbsp;</td>
			<td colspan="2" valign="top" align="right">
			<table width="99%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="82%" valign="top"><img
						src="<%=basePath%>/images/btm-line.jpg"></td>
					<td align="right" rowspan="2" valign="top"><img
						src="<%=basePath%>/images/btm-gradient.jpg" width="140"
						height="23"></td>
				</tr>
				<tr>
					<td width="82%" valign="top" align="right" class="small-text-grey">Copyright&copy;
					<a href="http://www.elitecore.com" target="_blank">Elitecore
					Technologies Pvt. Ltd.</a></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</html:form>
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
