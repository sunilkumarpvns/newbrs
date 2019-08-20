<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>

<%response.setContentType("text/html;charset=UTF-8");%>


<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Subscriber Service Selection System</title>
<link href="<%= request.getContextPath() %>/css/stylesheet.css" rel="stylesheet" type="text/css" />
<%
	String errorCode = (String)request.getAttribute("errorCode");
 %>
<script type="text/javascript">

    function validate(){
		if(document.forms[0].userName.value=='' || document.forms[0].password.value==''){
			alert('Enter Username and Password');
			return false;
		}else{
			document.forms[0].submit();
			return true;
		}
	}
    
	</script>
</head>
<html>
<body>

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="100%">
		<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="34" background="<%=request.getContextPath()%>/images/marquee_bkgd.jpg">
						<img src="<%=request.getContextPath()%>/images/leftmcurve.jpg" width="33" height="51" />
						</td>

						<td width="147" align="center" background="<%=request.getContextPath()%>/images/marquee_bkgd.jpg">
						<img src="<%=request.getContextPath()%>/images/elitecore.jpg" alt="Elitecore" width="107" height="39" />
						</td>

						<td width="801" align="right" class="toplinks" style="background-image:url(<%=request.getContextPath()%>/images/marquee_bkgd.jpg); background-repeat: repeat-x; border-left: 1px solid rgb(195, 195, 195);">
						<a href="#" target="_blank"> Services </a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
						<a href="#" target="_blank"> New Offers </a>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
						<a href="#" target="_blank"> Contact Us </a> 
						</td>
						
						<td width="18">
						<img src="<%=request.getContextPath()%>/images/topright_curve.jpg" width="18" height="51" />
						</td>
						
					</tr>
				</table>
				</td>
			</tr>

			<tr>
				<td style="background-image:url(<%=request.getContextPath()%>/images/banner_bkgd.jpg); background-repeat:repeat-x;">
				  <table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td height="275" valign="top">
						<jsp:include page="/jsp/core/includes/LoginHeader.jsp"></jsp:include>
						</td>
						
						<td width="50%" align="center" valign="bottom">
						<img src="<%=request.getContextPath()%>/images/home_banner.jpg" width="485" />
						</td>
						
					</tr>
				</table>
				</td>
			</tr>

			<tr>
				<td height="15" bgcolor="#e60000"></td>
			</tr>

			<tr>
				<td align="left" valign="top" bgcolor="#545454" class="bot_con_rep" height="100%">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="18" align="left" valign="bottom" rowspan="3">
						</td>
						<td height="20" colspan="3"></td>
						<td width="18" rowspan="3" align="right" valign="bottom">
						</td>
					</tr>

					<tr>
						<td width="315" height="174" valign="top">
						<table width="88%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td class="bottompaneltitle" width="100%" nowrap="nowrap">Do more label</td>
							</tr>

							<tr>
								<td height="8"></td>
							</tr>

							<tr>
								<td>
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="20" valign="bottom">
										<img src="<%=request.getContextPath()%>/images/bluearrow_btn.png" width="12" height="12" />
										</td>
										
										<td class="whitetext">Watch Movies Without Buffering</td>
									</tr>

									<tr>
										<td valign="bottom">
										<img src="<%=request.getContextPath()%>/images/bluearrow_btn.png" width="12" height="12" />
										</td>
										<td class="whitetext">Upload images/Pictures Instantly</td>
									</tr>

									<tr>
										<td valign="bottom">
										<img src="<%=request.getContextPath()%>/images/bluearrow_btn.png" width="12" height="12" />
										</td>
										
										<td class="whitetext">Enjoy Uninterrupted Online Gaming </td>
									</tr>

									<tr>
										<td valign="bottom">
										<img src="<%=request.getContextPath()%>/images/bluearrow_btn.png" width="12" height="12" />
										</td>
										<td class="whitetext">Watch Video On Demand</td>
									</tr>

									<tr>
										<td valign="bottom">
										<img src="<%=request.getContextPath()%>/images/bluearrow_btn.png" width="12" height="12" />
										</td>
										<td class="whitetext">Enjoy TV on IPTV</td>
									</tr>

									<tr>
										<td height="8"></td>
										<td></td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
						</td>
						<td width="400" valign="top" style="border-right: #FFFFFF dashed 1px; border-left: #FFFFFF dashed 1px;">
						<table width="88%" border="0" align="center" cellpadding="0" cellspacing="0">
						 
							<tr>
								<td class="bottompaneltitle">Login</td>
							</tr>

							<tr>
								<td height="8"></td>
							</tr>

							<tr>
								<td valign="top">
								<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
									<tr>
										<td align="center" class="whitetext">Log on with your Broadband Credentials	 </td>
									</tr>

									<tr>
										<td height="5"></td>
									</tr>

									<tr>
										<td>
										<table width="90%" border="0" align="center" cellpadding="0" cellspacing="0">
						                    <!--  Struts login form -->
						                    
						                    <html:form action="/login">		
						                  <%if(errorCode!=null) {%>
						                    <tr>
												<td width="30%" class="whitetext" colspan="2" align="center"><%=errorCode%></td>
											</tr>	
											<%}%>
											<tr>
												<td width="30%" class="whitetext">Username</td>
												<td>
												   <%--input name="userName" type="text" 	class="blacktext" id="userName" size="30" maxlength="50"/--%>
												   <html:text property="userName" styleId="userName" size="30" maxlength="50" /> 
												</td>
											</tr>

											<tr>
												<td height="5"></td>
												<td></td>
											</tr>

											<tr>
												<td class="whitetext">Password</td>
												<td>
												  <%--input name="password" type="password" class="blacktext" id="password" size="30" maxlength="50" /--%>
												  <html:password property="password" styleId="password" size="30" maxlength="50"/>
												</td>
											</tr>

											<tr>
												<td height="5"></td>
												<td></td>
											</tr>

											<tr>
												<td class="whitetext">&nbsp;</td>
												<td>
												 <input type="image" src="<%=request.getContextPath()%>/images/login_btn.jpg" width="107" height="22" border="0" onclick="return validate();" />
												</td>
											</tr>
											<tr>
												<td height="5"></td>
												<td></td>
											</tr>
                                            </html:form>
										</table>
										</td>
									</tr>
									
								</table>
								</td>
							</tr>

							<tr>
								<td valign="top">&nbsp;</td>
							</tr>

							<tr>
								<td valign="top"></td>
							</tr>
						</table>
						
						
						</td>
						
						<td width="300" valign="top">
							<table width="80%" border="0" align="center" cellpadding="0" cellspacing="0">
								<tr>
									<td class="bottompaneltitle">Service</td>
								</tr>
	
								<tr>
									<td height="8"></td>
								</tr>
	
								<tr>
									<td class="whitetext">This service available only on Broadband Plans 
									  <a href="#" target="_blank">Click here</a> to check or call customer care.
									 </td>
								</tr>

							<tr>
								<td height="15"></td>
							</tr>

							<tr>
							  <td align="center">
								<a href="#" target="_blank"> 
									<img src="<%=request.getContextPath()%>/images/customercare_btn.jpg" width="107" height="22" border="0" />
								</a>
							   </td>
							</tr>
						</table>
						</td>
					</tr>

					<tr>
						<td></td>
						<td></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td valign="bottom" height="10" bgcolor="#343434" align="center">
					<table width="100%" cellspacing="0" cellpadding="0" border="0" align="center">
						<tbody>
							<tr>
								<td width="15" valign="bottom" align="left">
								<img width="15" height="20" src="<%=request.getContextPath() %>/images/bot_cor_left.jpg"></img>
							</td>
							
							<td class="bottom_rep">&nbsp;</td>
							<td width="15" valign="bottom" align="right">
								<img width="18" height="20" src="<%=request.getContextPath() %>/images/bot_cor_right.jpg"></img>
							</td>
						</tr>
					</tbody>
				</table>
				</td>
			</tr>

			<tr>
				<td align="center" valign="bottom"></td>
			</tr>

			<tr>
	        <td height="100%">
    		    <table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
	 <td  height="35" style="border:#172734 solid 1px; background-image:url(<%=request.getContextPath() %>/images/menu_bkgd.jpg);">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            	<td width="30%" height="6" align="left" colspan="4" class="blacktext">
            		<strong>Select Language:</strong>	
  	            	<a href="#" >
		            	<label class="blacktext">English</label>
	            	</a><%--&nbsp;|&nbsp;
	            	 <a href="<%=request.getContextPath() %>/servlet/LoginServlet?actionMode=3&locale=hi" class="blacktext">
	            	Hindi
	            	</a>&nbsp;|&nbsp;
		           	<a href="<%=request.getContextPath() %>/servlet/LoginServlet?actionMode=3&locale=th" class="blacktext">
		           	Thai
		           	</a--%>
	            </td>
    	        <td  nowrap="nowrap" align="right" class="blacktext" valign="middle">
    	        	<table>
    	        		<td>
    	        				<a target="_blank" href="http://www.elitecore.com">
	                 				<img width="25" height="25" border="0" src="<%=request.getContextPath() %>/images/elitecore_logo.jpg"></img>
	               				</a>
    	        		</td>
    	        		<td class="blacktext">
    	        				Powered By Elitecore Technologies Pvt. Ltd.
    	        		</td>
    	        	</table>
	               </td>
	               
            	   
      			</tr>
	   </table>
		</td>
		</tr>
		</table>
				</td>
  			</tr>
		</table>
		</td>
		</tr>
		</table>
</body>
<script>
document.getElementById("userName").focus();
</script>
</html>
