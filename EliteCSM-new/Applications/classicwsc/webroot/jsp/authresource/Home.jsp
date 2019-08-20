
<%@page import="com.elite.user.*"%>
<%
Userbean user = (Userbean)request.getSession().getAttribute("user");
%>
<SCRIPT language=JavaScript src="<%=request.getContextPath()%>/js/menuscript.js" type=text/javascript></SCRIPT>
<LINK href="<%=request.getContextPath()%>/css/portal_style.css" rel=stylesheet>
<LINK href="<%=request.getContextPath()%>/css/menu.css" rel=stylesheet>
<TABLE class=Box cellSpacing=0 cellPadding=0 width="90%" align=center
	border=0>
	<TBODY>
		<TR>
			<TD class=MenuText>
				<!-- For Menu Configuration -->
				<SCRIPT language=JavaScript src="<%=request.getContextPath()%>/js/menu.js" type=text/javascript></SCRIPT>
			</TD>
		</TR>
		<TR>
			<TD class=BODY vAlign=top align=left width="90%">
		<TR>
			<TD>
				<TABLE cellSpacing=0 cellPadding=0 width="96%" align=center border=0>
					<FORM id=frmCustomerHome name=frmCustomerHome method=post>
					<TBODY>
						<TR>
							<TD class=PageHeader>
								My Information
							</TD>
							<TD class=PageHeader align=right>
								&nbsp;
							</TD>
						</TR>
						<TR>
							<TD class=SmallGap colSpan=2>
								&nbsp;
							</TD>
						</TR>
						<TR>
							<TD colSpan=2>
								&nbsp;
							</TD>
						</TR>
						<TR>
							<TD class=Notes vAlign=top width="38%">
								<TABLE class=Box cellSpacing=2 cellPadding=0 width="98%"
									border=0>
									<TBODY>
										<TR>
											<TD class=TextLabels colSpan=2>
												Welcome&nbsp;&nbsp;<%=user.getUserotherdetail().getCustomername() %>
											</TD>
										</TR>
										<TR>
											<TD class=TextLabels colSpan=2>
												<%=user.getLogin_time() %>
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold>
												&nbsp;
											</TD>
											<TD class=TextLabels>
												&nbsp;
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold width="50%">
												Account Number
											</TD>
											<TD class=TextLabels width="50%">
												<%=user.getUsername() %>
											</TD>
										</TR>
										<TR>
											<TD class=SmallGap colSpan=2>
												&nbsp;
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold width="50%">
												Account Create Date
											</TD>
											<TD class=TextLabels width="50%">
												<%=user.getUserotherdetail().getCreateddate() %>
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold width="50%">
												Account Status
											</TD>
											<TD class=TextLabels width="50%">
												<%=user.getUserotherdetail().getAccountstatus() %>
											</TD>
										</TR>
										<TR>
											<TD class=SmallGap colSpan=2>
												&nbsp;
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold> 
												Customer Type
											</TD>
											<TD class=TextLabels>
												<%=user.getUserotherdetail().getCustomertype() %>
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold width="50%">
												Customer Sub Type
											</TD>
											<TD class=TextLabels width="50%">
												<%=user.getUserotherdetail().getCustomersubtype() %>
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold width="50%">
												Package Name
											</TD>
											<TD class=TextLabels width="50%">
												<%=user.getUserotherdetail().getPackaage() %>
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold width="50%">
												Home Phone Number
											</TD>
											<TD class=TextLabels width="50%">
												<%=user.getUserotherdetail().getHomephone() %>
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold width="50%">
												Mobile Number
											</TD>
											<TD class=TextLabels width="50%">
												<%=user.getUserotherdetail().getMobilephone() %>
											</TD>
										</TR>
										<TR>
											<TD class=TextLabelsBold width="50%">
												Service Type
											</TD>
											<TD class=TextLabels width="50%">
												<%=user.getUserotherdetail().getServicetype() %>
											</TD>
										</TR>
									</TBODY>
								</TABLE>
								<BR>
							</TD>
							<TD class=Notes vAlign=top width="62%">
								<TABLE cellSpacing=0 cellPadding=0 width="90%" align=center
									border=0>
									<TBODY>
										<TR>
											<TD align=right>
												<A href="javascript:PromoPopup('http://www.google.com')"><IMG
														src="<%=request.getContextPath()%>/images/promotions.jpg" border=0 name=Image515>
												</A>
											</TD>
										</TR>
									</TBODY>
								</TABLE>
							</TD>
						</TR>
						<TR>
							<TD vAlign=top colSpan=2>
								&nbsp;
							</TD>
						</TR>
						<TR>
							<TD vAlign=top colSpan=2>
								&nbsp;
							</TD>
						</TR>
						<TR>
							<TD vAlign=top colSpan=2>
								&nbsp;
							</TD>
						</TR>
						</FORM>
					</TBODY>
				</TABLE>
			</TD>
		</TR>
		<SCRIPT language=javascript>
   function PromoPopup(promoLink){

      var target=promoLink;
         winopen=window.open(target,"hints","top=-1,left=-1,toolbars=1,maximize=1,resizable=1, status=1, width=600,height=600,location=1,directories=1,scrollbars=yes");

   }

</SCRIPT>
		</TD>
		</TR>
	</TBODY>
</TABLE>
