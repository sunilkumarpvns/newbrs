<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.web.core.system.staff.forms.StaffAuditForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>


<%
    String basePath = request.getContextPath();
%>
<%
	StaffAuditForm staffAuditForm = (StaffAuditForm) request.getAttribute("staffAuditForm");
	List actionListInCombo = staffAuditForm.getActionListInCombo();
	List usersListInCombo = staffAuditForm.getUsersListInCombo();

	List auditDetailList = staffAuditForm.getAuditDetailList();
    
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	long pageNo = staffAuditForm.getPageNumber();
	long totalPages = staffAuditForm.getTotalPages();
	long totalRecord = staffAuditForm.getTotalRecords();
	int count = 1;

	String strTotalPages = String.valueOf(totalPages);
	String strTotalRecords = String.valueOf(totalRecord);
	String strPageNumber = String.valueOf(pageNo);

	String actionName = staffAuditForm.getActionName();
	String strAuditDate=staffAuditForm.getStrAuditDate();
	String userId=staffAuditForm.getUserId();
	String actionId=staffAuditForm.getActionId();
	String name=staffAuditForm.getName();
	String strAuditDateFrom=staffAuditForm.getStrAuditDateFrom();
	String strActionName="";
	if(userId == null || actionId == null || name == null){
		userId="";
		actionId="";
		name="";
	}
    String paramString="actionId="+actionId+"&strAuditDate="+strAuditDate+"&userId="+userId+"&name="+name+"&strAuditDateFrom="+strAuditDateFrom;
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/js/calender/jquery.ui.datepicker.css" /> 
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/jquery-ui-timepicker-addon.css">
		
<script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.min.js" ></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-ui.min.js" ></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui-timepicker-addon.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/popcalendar.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui-sliderAccess.js"></script> 
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/auditcss.css" /> 
<script type="text/javascript" src="<%=request.getContextPath()%>/js/audit/audit.js"></script>
<script>
$(function() {
	$('#strAuditDate').datetimepicker({
		timeFormat: 'HH:mm:ss',
		stepHour: 1,
		stepMinute: 1
	});
	
	
	$('#strAuditDateFrom').datetimepicker({
		timeFormat: 'HH:mm:ss',
		stepHour: 1,
		stepMinute: 1
	});

	$( "#actionId" ).combobox();
	$( "#toggle" ).click(function() {
		$( "#actionId" ).toggle();
	});
});
</script>
<script>
	var dFormat;
	dFormat = 'MM-dd-yyyy';
	
	var actionList = [];
	
	<% Iterator iterate = actionListInCombo.iterator();
		while (iterate.hasNext()) {
		IActionData actionData = (ActionData) iterate.next();
		if(actionData.getActionId().equals(actionId)){
			strActionName=actionData.getName();
		}
		%>
			actionList.push({'value':'<%=actionData.getName()%>','label':'<%=actionData.getName()%>','actionid':'<%=actionData.getActionId()%>'});
	<%}%>
	
	$(document).ready(function() {
		$('input:text, textarea').keypress(function (e) {
		    if (e.which == 13) {
		    	searchSubmit();
		    }
		}); 		
	});
	
	function setSuggestionForActionName(actionList) {
		 
		 $(".actionClass").autocomplete({
			   source:actionList,
			   focus: function( event, ui ) {
					return false;
				},
			   select: function (event, ui) {
			      this.value = ui.item.label;
			      $('#actionId').val(ui.item.actionid);
			      return false;
			   }
		});
		 
	}
	
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		//Get format from system parameter document.form[0].
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 

	}
	
	function searchSubmit(){
		document.forms[0].pageNumber.value=1;
		document.forms[0].submit();
	}
	
	function navigate(direction, pageNumber ){
		document.forms[0].pageNumber.value = pageNumber;
		document.forms[0].submit();
	}
	function popupViewInfo(index) {	
		$.fx.speeds._default = 1000;
		document.getElementById("auditDetailDiv"+index).style.visibility = "visible";	
		$( "#auditDetailDiv"+index).dialog({
			modal: true,
			autoOpen: false,		
			height: "auto",
			width: 600,		
			buttons:{					
	 		    Cancel: function() {
	            	$(this).dialog('close');
	        	}
			},
			open: function() {
	        	
	    	},
	    	close: function() {
	    		
	    	}	
		
		});
		$( "#auditDetailDiv"+index ).dialog("open");
	}
	setTitle('<bean:message bundle="StaffResources" key="staff.staff"/>');
</script>

<html:form action="/auditDetails">
	<html:hidden name="staffAuditForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>" />
	<html:hidden name="staffAuditForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="searchASMForm" styleId="actionName" property="actionName" value="search" />

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="4">
										<bean:message bundle="StaffResources" key="staff.audit.label" />
									</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="10%">
										<bean:message  bundle="StaffResources" 
											key="staff.audit.auditdate.Name" /> 
												<ec:elitehelp headerBundle="StaffResources" 
													text="staff.audit.auditdate.name"
														header="staff.audit.auditdate.Name" />
									</td>
									<td class="labeltext" valign="top" align="left" colspan="3">
										<html:text styleId="name" property="name" style="width:250px" tabindex="2" /> 
									</td>
								</tr>
								
								<tr>
									<td align="left" class="captiontext" valign="top" width="10%">
										<bean:message bundle="StaffResources" 
											key="staff.audit.actionname.label" /> 
												<ec:elitehelp headerBundle="StaffResources" 
													text="staff.audit.actionname.label" 
														header="staff.audit.actionname.label"/> 
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
									<%-- 	<html:hidden styleId="actionId" property="actionId"></html:hidden>
										<input type="text" name="actionName" id="actionName" style="width: 250px;" tabindex="2" class="actionClass"/>
								 --%>	
								 	<div class="ui-widget">
								 	<%
										if (actionListInCombo != null && !actionListInCombo.isEmpty()) {
									%> <html:select styleId="actionId" property="actionId"
														style="width:250px" tabindex="1">
														<html:option value="0">-All-</html:option>
														<%
											Iterator itr = actionListInCombo.iterator();
											while (itr.hasNext()) {
												IActionData actionData = (ActionData) itr.next();
										%>
														<html:option value="<%=actionData.getActionId()%>"><%=actionData.getName()%></html:option>
														<%
											}
										%>
													</html:select> <%
											}
										%>
										</div>
								 </td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="10%">
										<bean:message bundle="StaffResources" 
											key="staff.audit.auditdate.label" /> 
												<ec:elitehelp headerBundle="StaffResources" 
													text="staff.audit.auditdate.label" 
														header="staff.audit.auditdate.label"/>
									</td>
									<td class="labeltext" valign="top" align="left" colspan="3">
										<html:text styleId="strAuditDate" property="strAuditDate" style="width:190px" tabindex="2" /> 
										 <bean:message bundle="StaffResources" key="staff.audit.auditdate.label.to" /> 
										 <html:text styleId="strAuditDateFrom" property="strAuditDateFrom" style="width:190px" tabindex="2" /> 
										<%-- <a href="javascript:void(0)" onclick="popUpCalendar(this, document.forms[0].strAuditDate)">
											<img src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
										</a> --%>
									</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="10%">
										<bean:message bundle="StaffResources" 
											key="staff.audit.user.label" /> 
												<ec:elitehelp headerBundle="StaffResources" 
													text="staff.audit.user.label" 
														header="staff.audit.user.label"/>
									</td>
									<td align="left" class="labeltext" valign="top" width="32%"
										colspan="3">
										<%
								if (usersListInCombo != null && !usersListInCombo.isEmpty()) {
							%> <html:select styleId="staffAuditForm" tabindex="3"
											name="staffAuditForm" property="userId" style="width:130px">
											<%
										Iterator itr = usersListInCombo.iterator();
										while (itr.hasNext()) {
											IStaffData staffData = (StaffData) itr.next();
										%>
											<html:option
												value="<%=staffData.getStaffId()%>"><%=staffData.getUsername()%></html:option>
											<%
										}
										%>
										</html:select> <%
								}
								%>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle">&nbsp;</td>
									<td align="left" class="labeltext" valign="top" width="5%"
										colspan="4"><input type="button" tabindex="4"
										name="Search" width="5%" value="  Search   "
										onclick="searchSubmit();" class="light-btn" /> <input
										type="button" tabindex="5" name="Cancel" onclick="reset();"
										value="   Cancel    " class="light-btn"></td>
								</tr>
								<%
								if (auditDetailList != null && auditDetailList.size() > 0) {
						%>
								<tr>
									<td align="left" class="labeltext" colspan="4" valign="top">
										<table cellSpacing="0" cellPadding="0" width="100%" border="0">
											<tr>
												<td class="table-header" width="50%">
													<bean:message bundle="StaffResources" 
														key="staff.audit.details.label" />
												</td>
												<td align="right" class="blue-text" valign="middle"
													width="50%">
													<% if(totalRecord == 0) { %> <% }else if(pageNo == totalPages+1) { %>
													[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
													<% } else if(pageNo == 1) { %> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
													of <%= totalRecord %> <% } else { %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
													of <%= totalRecord %> <% } %>
												</td>

											</tr>
											<tr>
												<td></td>
											</tr>
											<tr>
												<td></td>
												<td class="btns-td" align="right">
													<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else if(pageNo == totalPages){ %> <a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } else { %> <a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img
														src="<%=basePath%>/images/last.jpg" name="Image612"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
													<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
													<a
													href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img
														src="<%=basePath%>/images/previous.jpg" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
													<% } %> <% } %>
												</td>
											</tr>

											<tr>
												<td valign="middle" colspan="2" align="center">
													<table width="99%" border="0" cellpadding="0" cellspacing="0" id="listTable">
														<tr>

															<td align="center" class="tblheader" valign="top" width="40px">
																<bean:message key="general.serialnumber" />
															</td>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="StaffResources" key="staff.audit.auditdate.Name" />
															</td>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="StaffResources" key="staff.audit.actionname.label" />
															</td>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="StaffResources" key="staff.audit.user.label" />
															</td>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="StaffResources" key="staff.audit.audittime.label" />
															</td>
															<td align="left" class="tblheader" valign="top" width="20%">
																<bean:message bundle="StaffResources" key="staff.audit.clientip.label" />
															</td>
														</tr>

														<logic:iterate id="auditList" name="staffAuditForm" property="auditDetailList" type="com.elitecore.elitesm.datamanager.systemaudit.Data.ISystemAuditData">
															<tr>
																<td align="center" class="tblrows tblfirstcol" valign="top"><%=((pageNo-1)*pageSize)+count%></td>
																<td align="left" class="tblrows">
																	<logic:notEmpty name="auditList" property="auditName" >
																		<bean:define id="auditNameString" name="auditList" property="actionData.name" type="java.lang.String"/>
																			<%String auditName=auditNameString;
																			if(auditName != null && (auditName.toLowerCase().indexOf("update") >= 0) ){%> 
																				<a href="viewHistoryDetails.do?auditUid=<bean:write name="auditList" property="auditId" />&name=<bean:write name="auditList" property="auditName" />&systemAuditId=<bean:write name="auditList" property="systemAuditId" />&actionName=<%=auditList.getActionData().getName()%>">
																					<bean:write name="auditList" property="auditName" />
																				</a>
																			<%}else{ %>
																				<bean:write name="auditList" property="auditName" />
																			<%} %>
																	</logic:notEmpty>
																	<logic:empty name="auditList" property="auditName">
																		-
																	</logic:empty>
																</td>
																<td align="left" class="tblrows"><%=auditList.getActionData().getName()%>&nbsp;
																</td>
																<td align="left" class="tblrows">
																	<bean:write name="auditList" property="systemUserName" />&nbsp;
																</td>
																<td align="left" class="tblrows">
																	<%=EliteUtility.dateToString(auditList.getAuditDate(),ConfigManager.get(ConfigConstant.DATE_FORMAT))%>
																	&nbsp;
																</td>
																<td align="left" class="tblrows">
																	<bean:write name="auditList" property="clientIP" /> &nbsp;
																</td>
															</tr>
															<%
													count++;
													%>
														</logic:iterate>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<%
							} else {
						%>
								<tr>
									<td align="left" class="labeltext" colspan="4" valign="top">
										<table cellSpacing="0" cellPadding="0" width="100%" border="0">
											<tr>
												<td class="table-header"><bean:message
														bundle="StaffResources" 
															key="staff.audit.details.label" />
												</td>
											</tr>
											<tr>
												<td>
													<table cellSpacing="0" cellPadding="0" width="98%"
														border="0" class="captiontext">
														<tr>

															<td align="left" class="tblheader" valign="top"
																width="2%"><bean:message
																	bundle="StaffResources"
																		key="staff.audit.serial.label" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="23%"><bean:message
																	bundle="StaffResources"
																		key="staff.audit.actionname.label" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="25%"><bean:message
																	bundle="StaffResources"
																		key="staff.audit.user.label" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="25%"><bean:message
																	bundle="StaffResources"
																		key="staff.audit.audittime.label" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="25%"><bean:message
																	bundle="StaffResources"
																		key="staff.audit.clientip.label" />
															</td>

														</tr>

														<tr>
															<td align="center" colspan="5" class="tblfirstcol">
																No Records Found.</td>

														</tr>

														<tr height="4">
															<td></td>
														</tr>
													</table>
												</td>
											</tr>

										</table>
									</td>
								</tr>
								<%}
							%>



							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>