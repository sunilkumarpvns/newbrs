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
	setForm('frmUpdateCustomerAccount');
</SCRIPT>
<!-- VALIDATION FOR FIELDS...-->
<SCRIPT language=Javascript>
	function validateform()
	{
		if(document.frmUpdateCustomerAccount.c_strEmailAddress1 != null ){}
		else
		{
				if(!validate('RNull','is compulsary field.Please Enter Email Id.','c_strEmailAddress','Email')){
					return false;
				}
				if(!validate('REmail','is invalid Email address. Please enter valid email address.','c_strEmailAddress','Email')){
					return false;
				}			
		}	
		if(document.frmUpdateCustomerAccount.sltQuestion1 != null){			
		}else
		{			
				if(document.frmUpdateCustomerAccount.sltQuestion.value == null || document.frmUpdateCustomerAccount.sltQuestion.value == ""){			
					alert("Question is compulsary field.please select Question.");
					return false;
				} 
		}
		if(document.frmUpdateCustomerAccount.c_strAnswer1 != null){			
		}else
		{
				if(document.frmUpdateCustomerAccount.c_strAnswer.value == null || document.frmUpdateCustomerAccount.c_strAnswer.value == ""){			
					if(!validate('RNull','is compulsary field.please enter Answer for Question.','c_strAnswer','Answer')){
						//document.frmUpdateCustomerAccount.c_strAnswer.focus();
						return false;
					}
				}
			
		}	
			
		if(document.frmUpdateCustomerAccount.c_strBillAddress11 != null){			
		}else{
				if(document.frmUpdateCustomerAccount.c_strBillAddress1.value == null || document.frmUpdateCustomerAccount.c_strBillAddress1.value == ""){			
					if(!validate('RNull','is compulsary field.please enter Billing Address 1.','c_strBillAddress1','Address1')){
						return false;
					}
				}
		}
		if(document.frmUpdateCustomerAccount.c_strHomePhone1 != null){			
		}else{
		
		//	if(document.frmUpdateCustomerAccount.c_strHomePhone1.value != "hidden"){	
		
				if(document.frmUpdateCustomerAccount.c_strHomePhone.value != null && document.frmUpdateCustomerAccount.c_strHomePhone.value != ""){
					
					var strMobileNo = document.frmUpdateCustomerAccount.c_strHomePhone.value;			
					if(document.frmUpdateCustomerAccount.c_strHomePhone.value != "")
					{
						for (i = 0; i < strMobileNo.length; i++)
						{   
							// Check that current character is number.
							var c = strMobileNo.charAt(i);				
			
							if (((c < "0") || (c > "9")))
							{
								alert("Home phone is integer field. Please enter an Integer value.");
								//document.frmUpdateCustomerAccount.c_strHomePhone.focus();				
								return false;
							}	
						}
					}
				}else{
					alert("Home phone is compulsary field. Please enter home phone number.");
					return false;
				}
			//}
		}			
		
		
		if(document.frmUpdateCustomerAccount.c_strMobileNo1 != null){			
			
		}else{
			// if(document.frmUpdateCustomerAccount.c_strMobileNo1.value != "hidden"){			
					if(document.frmUpdateCustomerAccount.c_strMobileNo.value != null && document.frmUpdateCustomerAccount.c_strMobileNo.value != ""){
						
						var strMobileNo = document.frmUpdateCustomerAccount.c_strMobileNo.value;			
						if(document.frmUpdateCustomerAccount.c_strMobileNo.value != "")
						{
							for (i = 0; i < strMobileNo.length; i++)
							{   
								// Check that current character is number.
								var c = strMobileNo.charAt(i);				
				
								if (((c < "0") || (c > "9")))
								{
									alert("Mobile phone is integer field. Please enter an Integer value.");
									//document.frmUpdateCustomerAccount.c_strHomePhone.focus();				
									return false;
								}	
							}
						}	
							
					}else{
						alert("Mobile phone is compulsary field.please enter mobile number.");
						return false;
					}
				//}
			}		
	
				//var path ="/servlet/DATAService?c_strActionMode=3214106";
				//document.frmUpdateCustomerAccount.action=path;
				//document.frmUpdateCustomerAccount.method='post';
				document.frmUpdateCustomerAccount.submit();	
		
	}	
</SCRIPT>
<TABLE class=Box cellSpacing=0 cellPadding=0 width="90%" align=center border=0>
	<TBODY>
		<TR>
			<TD class=MenuText>
				<SCRIPT language=JavaScript src="<%=request.getContextPath()%>/js/menu.js" type=text/javascript></SCRIPT>
			</TD>
		</TR>
		<TR>
			<TD class=BODY vAlign=top align=left width="90%">
		<TR>
			<TD>
				<TABLE cellSpacing=0 cellPadding=0 width="96%" align=center border=0>
					<s:form id="frmUpdateCustomerAccount" name="frmUpdateCustomerAccount" action="UpdateProfile" method="post" theme="simple">
					<TBODY>
						<TR>
							<TD class=PageHeader colSpan=6>
								My Profile
							</TD>
						</TR>
						<TR>
							<TD>
								&nbsp;
							</TD>
						</TR>
						<TR>
							<TD vAlign=top colSpan=6>
								<TABLE cellSpacing=2 cellPadding=0 width="100%" border=0>
									<TBODY>
										<TR>
											<TD vAlign=top width="52%">
												<TABLE cellSpacing=2 cellPadding=0 width="100%" border=0>
													<TBODY>
														<TR>
															<TD class=TextLabelsBold width="37%">
																Account Name
															</TD>
															<TD class=TextLabels width="63%" colSpan=2>
																<%= user.getUserotherdetail().getCustomername() %>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabelsBold width="37%">
																Account Number
															</TD>
															<TD class=TextLabels width="63%" colSpan=2>
																<%= user.getUsername() %>
															</TD>
														</TR>
														<TR>
															<TD colSpan=6>
																&nbsp;
															</TD>
														</TR>
													</TBODY>
												</TABLE>
											</TD>
											<TD vAlign=top width="3%">
												&nbsp;
											</TD>
											<TD vAlign=top width="45%"></TD>
										</TR>
									</TBODY>
								</TABLE>
							</TD>
						</TR>
						<TR>
							<TD colSpan=6>
								&nbsp;
							</TD>
						</TR>
						<TR>
							<TD colSpan=3>
								<!-- formed new outer table here.. -->
								<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center
									border=0>
									<TBODY>
										<TR>
											<TD>
												<TABLE class=Box cellSpacing=5 cellPadding=0 width="70%"
													align=center border=0>
													<TBODY>
														<TR>
															<TD class=TextLabelsBold align=right>
																Email Address
															</TD>
															<TD>
																<LABEL>
																	<s:textfield id="c_strEmailAddress" maxLength="255" size="30" name="c_strEmailAddress"/>
																	<FONT color=red>*</FONT>
																</LABEL>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabelsBold align=right>
																Question
															</TD>
															<TD>
																<LABEL>
																	<s:select id="sltQuestion" size="1" name="sltQuestion" list="questionlist"/>
																</LABEL>
																<FONT color=red>*</FONT>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabelsBold align=right>
																Answer
															</TD>
															<TD>
																<LABEL>
																	<s:textfield id="c_strAnswer" maxLength="30" size="30" name="c_strAnswer"/>
																	<FONT color=red>*</FONT>
																</LABEL>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabelsBold align=right>
																Mobile Phone
															</TD>
															<TD>
																<LABEL>
																	<s:textfield id="c_strMobileNo" maxLength="15" size="30" name="c_strMobileNo"/>
																	<FONT color=red>*</FONT>
																</LABEL>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabelsBold align=right>
																Address Line 1
															</TD>
															<TD>
																<LABEL>
																	<s:textfield id="c_strBillAddress1" maxLength="100" size="30" name="c_strBillAddress1"/>
																	<FONT color=red>*</FONT>
																</LABEL>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabelsBold align=right>
																Address Line 2
															</TD>
															<TD>
																<LABEL>
																	<s:textfield id="c_strBillAddress2" maxLength="100" size="30" name="c_strBillAddress2"/>
																</LABEL>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabelsBold align=right>
																ZIP/Postal Code
															</TD>
															<TD>
																<LABEL>
																	<s:textfield id="c_strBillZip" maxLength="12" size="30" name="c_strBillZip"/>
																</LABEL>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabelsBold align=right>
																Home Phone
															</TD>
															<TD>
																<LABEL>
																	<s:textfield id="c_strHomePhone" maxLength="12" size="30" name="c_strHomePhone"/>
																	<FONT color=red>*</FONT>
																</LABEL>
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
												<!-- Shifted Address Info here.. from above -->
												<TABLE class=Box cellSpacing=0 cellPadding=0 width="70%"
													align=center border=0>
													<TBODY>
														<TR>
															<TD class=TextLabels>
																<SPAN class=TextLabelsBold>City</SPAN>
															</TD>
															<TD class=TextLabels>
																<%= user.getUserotherdetail().getCity() %>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabels>
																<SPAN class=TextLabelsBold>State/Province</SPAN>
															</TD>
															<TD class=TextLabels>
																<%= user.getUserotherdetail().getState() %>
															</TD>
														</TR>
														<TR>
															<TD class=TextLabels>
																<SPAN class=TextLabelsBold>Country</SPAN>
															</TD>
															<TD class=TextLabels>
																<%= user.getUserotherdetail().getCountry() %>
															</TD>
														</TR>
													</TBODY>
												</TABLE>
											</TD>
										</TR>
									</TBODY>
								</TABLE>
							</TD>
						</TR>
						<TR>
							<TD colSpan=6>
								&nbsp;
							</TD>
						</TR>
						<TR>
							<TD align="center" colSpan=6>
								<INPUT class=Buttons onclick=validateform(); type=button
									value="Update Profile" name=update>
								<INPUT class=Buttons type=reset value=Reset name=Reset>
								<INPUT class=Buttons onclick=window.history.back(); type=button
									value=Cancel name=cancel1>
							</TD>
						</TR>
						<TR>
							<td width="23%" align="center" colspan="6">
								<FONT color=#cc0000 size=2><s:actionmessage/></FONT>
							</td>
						</TR>
					</TBODY>
					</s:form>
				</TABLE>
			</TD>
		</TR>
		</TD>
		</TR>
	</TBODY>
</TABLE>

