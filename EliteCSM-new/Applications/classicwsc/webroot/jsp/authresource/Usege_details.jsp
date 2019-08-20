<%@ taglib prefix="s" uri="/struts-tags" %>
<%@page import="com.elite.user.*"%>
<%
 response.setDateHeader ("Expires", 0);
    response.setHeader ("Pragma", "no-cache");
    if (request.getProtocol().equals ("HTTP/1.1")) {
       response.setHeader ("Cache-Control", "no-cache");
    }
Userbean user = (Userbean)request.getSession().getAttribute("user");
%>
<SCRIPT language=JavaScript src="<%=request.getContextPath()%>/js/menuscript.js" type=text/javascript></SCRIPT>
<SCRIPT language=javascript src="<%=request.getContextPath()%>/js/validation.js"></SCRIPT>
<script language="javascript" src="<%=request.getContextPath()%>/js/popcalendar.js"></script>
<LINK href="<%=request.getContextPath()%>/css/portal_style.css" rel=stylesheet>
<LINK href="<%=request.getContextPath()%>/css/menu.css" rel=stylesheet>
<link href="<%=request.getContextPath()%>/css/popcalendar.css" type="text/css" rel="stylesheet">
<script>
function validateDate(){
	//document.frmCallHistory.action = "/servlet/Accounting";
	document.getElementById("c_strActionMode").value="3215134";
	fromDate = document.getElementById("c_dateCreateFrom").value;
	toDate = document.getElementById("c_dateCreateTo").value;
	date1 = new Date(fromDate);
	fromDay = date1.getDate();
	fromMonth = date1.getMonth();
	allowMonth = fromMonth + 3;
	fromYear = date1.getYear();
	dateAllowed = new Date(fromYear,allowMonth,fromDay+1);
	date2 = new Date(toDate);
	if(date1 > date2){
		alert('From date should not be greater than To date. ');
		return false;
	}
	if(date2 > dateAllowed){
		alert('Difference between From Date and To Date should not be greater than 3 months. ');
		return false;
	}
}
</script>
<table class="Box" cellSpacing="0" cellPadding="0" width="90%"	align="center" border="0">
	<tbody>
		<tr>
			<td class="MenuText">
				<!-- For Menu Configuration -->
				<SCRIPT language=JavaScript src="<%=request.getContextPath()%>/js/menu.js" type=text/javascript></SCRIPT>
			</td>
		</tr>
		<tr>
			<td class="BODY" vAlign="top" align="left" width="90%">
					<table cellSpacing="0" cellPadding="0" width="96%" align="center" border="0">
					<s:form id="frmCallHistory" name="frmCallHistory" action="GoUsege_details_page" method="post" theme="simple">
					<tbody>
						<tr>
							<td colSpan="3">
								<table cellSpacing="0" cellPadding="0" width="96%"
									align="center" border="0">
									<tbody>
										<tr>
											<td class="PageHeader">
												Usage History
											</td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
						<tr>
							<td colSpan="2">
								&nbsp;
								<input id="c_strActionMode" type="hidden" value="3215134" name="c_strActionMode">
								<input id="c_strAccountId" type="hidden" value="ACC000000421" name="c_strAccountId">
								<input id="c_strServiceId" type="hidden" value="SRV00003" name="c_strServiceId">
								<input id="c_strDateTimeFormat" type="hidden" value="dd MMM, yyyy" name="c_strDateTimeFormat">
							</td>
						</tr>
						<tr>
							<td>
								<table cellSpacing="0" cellPadding="0" width="96%" align="right"
									border="0">
									<tbody>
										<tr>
											<td class="TextLabelsBold" width="25%">
												Account Name
											</td>
											<td class="TextLabels" colSpan="2">
												<%= user.getUserotherdetail().getCustomername() %>
											</td>
										</tr>
										<tr>
											<td class="TextLabelsBold" width="25%">
												Account Number
											</td>
											<td class="TextLabels" colSpan="2">
												<%= user.getUsername() %>
											</td>
										</tr>
										<tr>
											<td class="SmallGap" colSpan="3">
												&nbsp;
											</td>
										</tr>
										<tr>
											<td class="TextLabelsBold">
												Date Range
											</td>
											<td class="TextLabels">
												From
											</td>
											<td class="TextLabels">
												<s:textfield cssClass="TextLabels" id="c_dateCreateFrom" readonly="readonly" name="c_dateCreateFrom"/>
												<a
													onclick="popUpCalendar(this, frmCallHistory.c_dateCreateFrom,'')"
													tabIndex="1" href="javascript:void(0)"><img height="17"
														alt="Calendar" src="<%=request.getContextPath()%>/images/calendar.jpg"
														width="17" border="0"> </a>
											</td>
										</tr>
										<tr>
											<td class="TextLabels">
												&nbsp;
											</td>
											<td class="TextLabels">
												To
											</td>
											<td class="TextLabels">
												<s:textfield cssClass="TextLabels" id="c_dateCreateTo" readonly="readonly"  name="c_dateCreateTo"/>
												<a
													onclick="popUpCalendar(this,frmCallHistory.c_dateCreateTo,'')"
													tabIndex="1" href="javascript:void(0)"><img height="17"
														alt="Calendar" src="<%=request.getContextPath()%>/images/calendar.jpg"
														width="17" border="0"> </a>
											</td>
										</tr>
										<tr></tr>
										<tr></tr>
									</tbody>
								</table>
							</td>
							<td>
								<table class="Box" cellSpacing="2" cellPadding="0" width="96%"
									align="left" border="0">
									<tbody>
										<tr>
											<td class="TextLabelsBold" width="60%">
												Usage Limit
											</td>
											<td class="TextLabels" colSpan="40">
												<s:property value="usagelimit"/>
											</td>
										</tr>
										<tr>
											<td class="TextLabelsBold" width="60%">
												Total Usage(KB/Sec.)
											</td>
											<td class="TextLabels" colSpan="40">
												<s:property value="totalusage"/>
											</td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td class="SmallGap" colSpan="6">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td align="center" colSpan="6">
								<s:submit cssClass="Buttons" onclick="return validateDate()" value="Search" name="c_btnSumbit"/>
							</td>
						</tr>
						<tr>
							<td class="SmallGap" colSpan="6">
								&nbsp;
							</td>
						</tr>
						<tr>
							<td colSpan="6">
								<table cellSpacing="0" cellPadding="0" width="96%"
									align="center" border="0">


									<tbody></tbody>
								</table>
							</td>
						</tr>
						</s:form>
					</tbody>
				</table>
				<%
				Object obj = request.getAttribute("pagestr");
				
				if(obj != null)
				{	
					System.out.println(obj);
				%>
					<jsp:include page="Usegeinterim.jsp">
					   <jsp:param name="param1" value="value1"/>
					</jsp:include>
				<%
				}
				%>
			</td>
		</tr>
	</tbody>
</table>
