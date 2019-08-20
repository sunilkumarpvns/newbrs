<%@ taglib prefix="s" uri="/struts-tags" %>
<%@page import="com.elite.user.*"%>
<%
Userbean user = (Userbean)request.getSession().getAttribute("user");
%>
<SCRIPT language=JavaScript src="<%=request.getContextPath()%>/js/menuscript.js" type=text/javascript></SCRIPT>
<SCRIPT language=javascript src="<%=request.getContextPath()%>/js/validation.js"></SCRIPT>
<LINK href="<%=request.getContextPath()%>/css/portal_style.css" rel=stylesheet>
<LINK href="<%=request.getContextPath()%>/css/menu.css" rel=stylesheet>
<SCRIPT language=JavaScript>
	setForm('chngpswdfrm');
</SCRIPT>
<SCRIPT language=javascript>

	function validatePasswordForm(){
		
		var formObj = document.chngpswdfrm;
		if ("Enable" == "Disable"){
			formObj.action = "https://ngt.jboss.datawsc.co.in:";
		}
		var bRetValue = true;	
		// Edited By Chetan to Customize Regular Expression Start	
		if(!validate('RNull','is Null.Please, Enter old Password !','oldpassword','Old Password')){
			formObj.oldpassword.value="";
			formObj.oldpassword.focus();
			return false;		
		}
		if(!validate('RPassword','is invalid.Please, Enter proper old Password !','oldpassword','Old Password')){
			formObj.oldpassword.value="";
			formObj.oldpassword.focus();
			return false;		
		}
		if(!validate('RNull','is Null.Please, Enter New Password !','newpassword','New Password')){
			formObj.newpassword.value="";
			formObj.newpassword.focus();
			return false;		
		}
		if(!validate('RPassword','is invalid.Please, Enter proper New Password !','newpassword','New Password')){
			formObj.newpassword.value="";
			formObj.newpassword.focus();
			return false;		
		}
		if(!validate('RNull','is Null.Please, Enter Confirm Password !','confirmpassword','Confirm Password')){
			formObj.confirmpassword.value="";
			formObj.confirmpassword.focus();
			return false;		
		}
		
		if(!validate('RPassword','is invalid.Please, Enter proper Confirm Password !','confirmpassword','Confirm Password')){
			formObj.confirmpassword.value="";
			formObj.confirmpassword.focus();
			return false;		
		}
		
		if(formObj.newpassword.value != formObj.confirmpassword.value){
			alert("Please, check New and Confirm Passwords !");	
			return false;
		}
	
		var pLen = formObj.newpassword.value.length;
		var pRange1 = parseInt('3');
		var pRange2 = parseInt('15');
		if(pRange1 == pRange2 && pLen != pRange1){
			alert("Password length must be of "+pRange1+" characters long");
			return false;
		}else if(pRange1 < pRange2 && (pLen < pRange1 || pLen > pRange2)){
			alert("Password length must be in range of "+pRange1 +" to "+pRange2);
			return false;
		}else if(pRange1 > pRange2 && (pLen < pRange2 || pLen > pRange1)){
			alert("Password length must be in range of "+pRange2 +" to "+pRange1);
			return false;
		}
		return bRetValue;					
	}
</SCRIPT>
<TABLE class=Box cellSpacing=0 cellPadding=0 width="90%" align=center border=0>
	<TBODY>
		<TR>
			<TD class=MenuText><!-- For Menu Configuration -->
				<SCRIPT language=JavaScript src="<%=request.getContextPath()%>/js/menu.js" type=text/javascript></SCRIPT>
			</TD>
		</TR>
		<TR>
			<TD class=BODY vAlign=top align=left width="90%">
				<s:form id="chngpswdfrm" name="chngpswdfrm" action="GoChangePass" method="post" theme="simple">
					<TABLE cellSpacing=0 cellPadding=0 width=780 align=center>
						<TBODY>
							<TR>
								<TD height=105>
									<TABLE cellSpacing=0 cellPadding=0 width="96%" align=center
										border=0>
										<TBODY>
											<TR>
												<TD class=PageHeader>
													Change Password
												</TD>
											</TR>
											<TR>
												<TD class=SmallGap>
													&nbsp;
												</TD>
											</TR>
											<TR>
												<TD>
													&nbsp;
												</TD>
											</TR>
											<TR>
												<TD colSpan=6>
													<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
														<TBODY>
															<tr>
																<td class="TextLabelsBold" width="25%">
																	Account Name
																</td>
																<td class="TextLabels" colSpan="1">
																	<%= user.getUserotherdetail().getCustomername() %>
																</td>
															</tr>
															<tr>
																<td class="TextLabelsBold" width="25%">
																	Account Number
																</td>
																<td class="TextLabels" colSpan="1">
																	<%= user.getUsername() %>
																</td>
															</tr>
														</TBODY>
													</TABLE>
												</TD>
											</TR>
											<TR>
												<TD>
													&nbsp;
												</TD>
											</TR>
											<TR>
												<TD>
													<TABLE class=Box cellSpacing=6 cellPadding=0 width="80%"
														align=center border=0>
														<TBODY>
															<TR>
																<TD class=TextLabelsBold align=right>
																	Old Password
																</TD>
																<TD>
																	<s:password id="oldpassword" name="oldpassword"/>
																</TD>
															</TR>
															<TR>
																<TD class=TextLabelsBold align=right>
																	New Password
																</TD>
																<TD>
																	<s:password id="newpassword" name="newpassword"/>
																</TD>
															</TR>
															<TR>
																<TD class=TextLabelsBold align=right>
																	Confirm New Password
																</TD>
																<TD>
																	<s:password id="confirmpassword" name="confirmpassword"/>
																</TD>
															</TR>
															<TR>
																<TD align=right>
																	<s:submit cssClass="Buttons" id="submit" onclick="return validatePasswordForm();" 
																		value="Submit" name="submit"/>
																</TD>
																<TD align=left>
																	<s:reset cssClass="Buttons" id="cancel" 
																		value="Cancel" name="cancel"/>
																</TD>
															</TR>
															<tr>
																<td width="23%" align="center" colspan="9">
																	<FONT color=#cc0000 size=2><s:actionmessage/></FONT>
																</td>
															</tr>
														</TBODY>
													</TABLE>
												</TD>
											</TR>
										</TBODY>
									</TABLE>
								</TD>
							</TR>
							<TR>
								<TD>
									&nbsp;
								</TD>
							</TR>
							<TR>
								<TD>
									<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
										<TBODY></TBODY>
									</TABLE>
								</TD>
							</TR>
						</TBODY>
					</TABLE>
				</s:form>
			</TD>
		</TR>
	</TBODY>
</TABLE>
