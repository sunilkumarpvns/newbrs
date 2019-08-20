<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.staff.forms.StaffAuditForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ActionData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>

<%
	StaffAuditForm staffAuditForm = (StaffAuditForm) request
			.getAttribute("staffAuditForm");
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
	if(userId==null){
		userId="";
	}
    String paramString="actionId="+actionId+"&strAuditDate="+strAuditDate+"&userId="+userId;
    
    
%>

<script>
	var dFormat;
	dFormat = 'MM-dd-yyyy';
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		//Get format from system parameter document.form[0].
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 

	}
	
	function searchSubmit(){
			document.forms[0].pageNumber.value = 1;
			document.forms[0].submit();
	}
	
	function navigate(direction, pageNumber ){
		document.forms[0].pageNumber.value = pageNumber;
		document.forms[0].submit();
	}
	$(document).ready(function(){
		setTitle('<bean:message key="staff.staff" />');
	});
</script>
<table name="MainTable" id="MainTable" width="100%" cellspacing="0" border="0" cellpadding="0">
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/auditDetails">
	<html:hidden name="staffAuditForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>" />
	<html:hidden name="staffAuditForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="staffAuditForm" styleId="actionName" property="actionName" value="search" />
		<tr>
			<td width="10">
				&nbsp; 
			</td>
			<td width="100%" colspan="2" valign="top" class="box">
				<table cellSpacing="0" cellPadding="0" width="100%" border="0">
					<tr>
						<td class="table-header" colspan="5">
							<bean:message key="staff.audit.label" />
						</td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="3">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td colspan="3">
							<table width="97%" name="c_tblCrossProductList"
								id="c_tblCrossProductList" align="right" border="0">
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%">
										<bean:message key="staff.audit.actionname.label" />
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<%
											if (actionListInCombo != null && !actionListInCombo.isEmpty()) {
										%>
										<html:select styleId="actionId" property="actionId">
											<html:option value="">----Select----</html:option>
											<%
												Iterator itr = actionListInCombo.iterator();
															while (itr.hasNext()) {
																IActionData actionData = (ActionData) itr.next();
											%>
											<html:option value="<%=actionData.getActionId()%>"><%=actionData.getName()%></html:option>
											<%
												}
											%>
										</html:select>
										<%
											}
										%>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%">
										<bean:message key="staff.audit.auditdate.label" />
									</td>

									<td class="labeltext" valign="top" align="left">
										<html:text styleId="strAuditDate" property="strAuditDate" />
										<a href="javascript:void(0)" onclick="popUpCalendar(this, document.forms[0].strAuditDate)">
											<img src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6"> </a>
									</td>

								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" width="10%">
										<bean:message key="staff.audit.user.label" />
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<%
											if (usersListInCombo != null && !usersListInCombo.isEmpty()) {
										%>
										<html:select styleId="staffAuditForm" name="staffAuditForm" property="userId">
											<html:option value="0">----Select----</html:option>
											<%
												Iterator itr = usersListInCombo.iterator();
												while (itr.hasNext()) {
													IStaffData staffData = (StaffData) itr.next();
											%>
											<html:option value="<%=Long.toString(staffData.getStaffId())%>"><%=staffData.getUserName()%></html:option>
											<%
												}
											%>
										</html:select>
										<%
											}
										%>
									</td>
								</tr>
								<tr>

									<td align="left" class="labeltext" valign="top" width="5%">
										<input type="button" name="Search" width="5%" value="  Search   " onclick="searchSubmit();" class="light-btn" />
										<input type="button" name="Cancel" onclick="reset();" value="   Cancel    " class="light-btn">
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%
						if (actionName != null && actionName.equalsIgnoreCase("search")) {

								if (auditDetailList != null && auditDetailList.size() > 0) {
					%>

					<tr>
						<td>
							<table cellSpacing="0" cellPadding="0" width="100%" border="0">
								<tr>
									<td width="50%" colspan="2" class="small-gap">
										&nbsp;
									</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">
										&nbsp;
									</td>
								</tr>

								<tr>
									<td width="50%" colspan="2" class="small-gap">
										&nbsp;
									</td>
								</tr>
								<tr>
									<td class="table-header" valign="bottom" colspan="3" align="left">
										<bean:message key="staff.audit.details.label" />
									</td>
									<td align="right" class="blue-text" valign="middle" width="14%" colspan="4">
									    <% if(totalRecord == 0) { %>
										<% }else if(pageNo == totalPages+1) { %>
										    [<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
										<% } else if(pageNo == 1) { %>
										    [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %>
										<% } else { %>
										    [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %>
										<% } %>
									</td>

								</tr>

								<tr>
									<td class="btns-td" align="right" colspan="8">
										<% if(totalPages >= 1) { %>
											  	<% if(pageNo == 1){ %>
													<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
													<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
											  	<% } %>
												<% if(pageNo>1 && pageNo!=totalPages+1) {%>
													<%  if(pageNo-1 == 1){ %>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
													<% } else if(pageNo == totalPages){ %>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
													<% } else { %>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= 1%>"><img  src="<%=basePath%>/images/first.jpg"  name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg"  name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo+1%>"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
														<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= totalPages+1%>"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
													<% } %>
												<% } %>
											<% if(pageNo == totalPages+1) { %>
												<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%=1%>"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
												<a  href="auditDetails.do?<%=paramString%>&action=list&pageNo=<%= pageNo-1%>"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
											<% } %>
									  <% } %>
									</td>
								</tr>

								<tr>
									<td colspan="7">
										<table cellSpacing="0" cellPadding="0" width="97%" border="0"
											align="right">
											<tr height="2">
												<td></td>
											</tr>
											<tr>
												<td width="50%" colspan="2" class="small-gap">
													&nbsp;
												</td>
											</tr>

											<tr>
												<td width="50%" colspan="2" class="small-gap">
													&nbsp;
												</td>
											</tr>

											<tr>
												<td width="50%" colspan="2" class="small-gap">
													&nbsp;
												</td>
											</tr>
											<tr>

												<td align="left" class="tblheader" valign="top" width="2%">
													<bean:message key="staff.audit.serial.label" />
												</td>
												<td align="left" class="tblheader" valign="top" width="23%">
													<bean:message key="staff.audit.actionname.label" />
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message key="staff.audit.user.label" />
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message key="staff.audit.audittime.label" />
										
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message key="staff.audit.clientip.label" />
												</td>
											</tr>

											<logic:iterate id="auditList" name="staffAuditForm"
												property="auditDetailList"
												type="com.elitecore.netvertexsm.datamanager.systemaudit.Data.ISystemAuditData">

													<tr>
														<td align="left" class="tblfirstcol" valign="top"><%=((pageNo-1)*pageSize)+count%></td>
														<td align="left" class="tblrows"><%=auditList.getActionData().getName()%></a>&nbsp;
														</td>
														<td align="left" class="tblrows">
															<bean:write name="auditList" property="systemUserName" />
															</a>&nbsp;
														</td>
														<td align="left" class="tblrows"><%=EliteUtility.dateToString(auditList.getAuditDate(),ConfigManager.get(ConfigConstant.DATE_FORMAT))%>
															&nbsp;
														</td>
														<td align="left" class="tblrows">
															<bean:write name="auditList" property="clientIP" />
															</a>&nbsp;
														</td>
													</tr>
													<%
													count++;
													%>
											</logic:iterate>

											<tr>
												<td width="50%" colspan="2" class="small-gap">
													&nbsp;
												</td>
											</tr>

											<tr>
												<td width="50%" colspan="2" class="small-gap">
													&nbsp;
												</td>
											</tr>

											<tr>
												<td width="50%" colspan="2" class="small-gap">
													&nbsp;
												</td>
											</tr>
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
									<td>
										<table cellSpacing="0" cellPadding="0" width="100%" border="0">

											<tr>
												<td colspan="7">
													<table cellSpacing="0" cellPadding="0" width="97%"
														border="0" align="right">
														<tr>

															<td align="left" class="tblheaderfirstcol" valign="top"
																width="2%">
																<bean:message key="staff.audit.serial.label" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="23%">
																<bean:message key="staff.audit.actionname.label" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="25%">
																<bean:message key="staff.audit.user.label" />
															</td>
															<td align="left" class="tblheader" valign="top"
																width="25%">
																<bean:message key="staff.audit.audittime.label" />
															</td>
															<td align="left" class="tblheaderlastcol" valign="top"
																width="25%">
																<bean:message key="staff.audit.transactionid.label" />
															</td>

														</tr>

														<tr>
															<td align="center" colspan="5" class="tblfirstcol">
																No Records Found.
															</td>

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
								
							
								<%
									}%>
										<% }
								%>
								
				</table>
			</td>
		</tr>
		<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</html:form>
</table>
