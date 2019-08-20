<jsp:directive.page
	import="com.elitecore.elitesm.datamanager.radius.radtest.data.RadiusTestParamData" />
<jsp:directive.page import="java.util.Set" />
<jsp:directive.page import="java.util.List" />


<%@ include file="/jsp/core/includes/common/Header.jsp"%>






<% 
	String basePath = request.getContextPath(); 
	Set radParamList = (Set)request.getAttribute("radParamList");
%>

<html:form action="/sendRadiusPacket">

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td colspan="2">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="*" background="<%=basePath%>/images/popup-bkgd.jpg"
							valign="top">&nbsp;</td>
						<td width="25"><img
							src="<%=basePath%>/images/popup-curve.jpg"></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="30"><a href="#"><img
								src="<%=basePath%>/images/refresh.jpg" name="Image1"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/refresh-hover.jpg',1)"
								border="0"></a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="30"><a href="#" onclick="window.print()"><img
								src="<%=basePath%>/images/print.jpg" name="Image2"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/print-hover.jpg',1)"
								border="0"> </a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="30"><a href="#"><img
								src="<%=basePath%>/images/aboutus.jpg" name="Image3"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/aboutus-hover.jpg',1)"
								border="0"></a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg"
							width="30"><a href="#"><img
								src="<%=basePath%>/images/help.jpg" name="Image4"
								onMouseOut="MM_swapImgRestore()"
								onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/help-hover.jpg',1)"
								border="0"></a></td>
						<td background="<%=basePath%>/images/popup-btn-bkgd.jpg" width="5">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td colspan="2" class="small-gap">&nbsp;</td>
		</tr>
	</table>
	<table name="MainTable" id="MainTable" cellSpacing="0" cellPadding="0"
		width="100%" border="0">
		<tr>
			<td width="10">&nbsp;</td>
			<td class="box" width="100%">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="table-header" colspan="5">SEND RADIUS PACKET</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td valign="middle" colspan="3">
							<table cellpadding="0" cellspacing="0" border="0" width="100%"
								height="30%">
								<tr>
									<td align="left" class="labeltext" valign="top" width="25%"><strong>Packet
											Name</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<bean:write name="viewRadiusTestForm" property="name" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%"><strong>Radius
											Server Host</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<bean:write name="viewRadiusTestForm" property="adminHost" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%"><strong>Radius
											Server Port</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<bean:write name="viewRadiusTestForm" property="adminPort" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%"><strong>Reply
											Timeout (sec)</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<bean:write name="viewRadiusTestForm" property="reTimeOut" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%"><strong>Retries</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<bean:write name="viewRadiusTestForm" property="retries" />
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" width="18%"><strong>Radius
											Secret Key</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<bean:write name="viewRadiusTestForm" property="scecretKey" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%"><strong>Chap
											Password</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<logic:equal name="viewRadiusTestForm" property="isChap"
											value="Y">
											<img src="<%=basePath%>/images/tick.jpg" alt="View"
												border="0">
										</logic:equal> <logic:equal name="viewRadiusTestForm" property="isChap"
											value="N">
											<img src="<%=basePath%>/images/cross.jpg" alt="View"
												border="0">
										</logic:equal>

									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%"><strong>User
											Name</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<bean:write name="viewRadiusTestForm" property="userName" />
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%"><strong>Request
											Type</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<bean:write name="viewRadiusTestForm" property="serviceType" />
										<logic:equal name="viewRadiusTestForm" property="serviceType"
											value="User Define">
																			 	[<strong><bean:write
													name="viewRadiusTestForm" property="requestType" /></strong>]
																			 </logic:equal>
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" width="18%"><strong>Client
											Interface</strong></td>
									<td align="left" class="labeltext" valign="top" colspan="2">
										<bean:write name="viewRadiusTestForm" property="hostAddress" />
									</td>
								</tr>



								<tr>
									<td align="left" class="labeltext" colspan="3" valign="top">
										&nbsp;</td>
								</tr>
								<% if(radParamList != null && radParamList.size() > 0) { %>
								<tr>
									<td align="center" colspan="3" valign="top">
										<table width="98%" cellpadding="0" cellspacing="0" border="0">
											<tr>
												<td align="left" class="tblheader" valign="top" width="10%">Sr.
													No.</td>
												<td align="left" class="tblheader" valign="top" width="45%">Parameter
													Name</td>
												<td align="left" class="tblheader" valign="top" width="45%">Parameter
													Value</td>
											</tr>
											<%	int index = 0; %>
											<logic:iterate id="objRadParamData" name="radParamList"
												type="RadiusTestParamData">
												<tr>
													<td align="left" class="tblfirstcol"><%=(index+1)%></td>
													<td align="left" class="tblrows"><bean:write
															name="objRadParamData" property="name" /></td>
													<td align="left" class="tblrows"><bean:write
															name="objRadParamData" property="value" /></td>
												</tr>
												<% index++; %>
											</logic:iterate>
										</table>
									</td>
								</tr>
								<% } %>
								<tr>
									<td align="left" class="labeltext" colspan="3" valign="top">
										&nbsp;</td>
								</tr>

								<tr>
									<td align="center" class="labeltext" colspan="3" valign="top">
										<% 
                                        								String responseString = (String)request.getAttribute("responseString"); 
                                        								if(responseString == null || responseString.equalsIgnoreCase("")) {
                                        									responseString = "Response Sending Failed From Server...";
                                        								}
                                        							%> <textarea rows="10"
											cols="80" readonly="readonly" style="width: 98%"><%=responseString%></textarea>
									</td>
								</tr>
							</table>
						</td>
					</tr>

				</table>
			</td>
		</tr>
	</table>

	<table cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td colspan="2" class="small-gap">&nbsp;</td>
		</tr>

		<tr>
			<td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
			<td class="small-gap" width="1%"><img
				src="<%=basePath%>/images/pbtm-line-end.jpg"></td>
		</tr>
	</table>

</html:form>
