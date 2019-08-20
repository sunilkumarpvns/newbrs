<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.web.reports.userstat.forms.SearchUserStatisticsForm"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
function  validateSearch(){
	document.forms[0].pageNumber.value = 0;
	document.forms[0].submit();
}
function navigate(direction, pageNumber ){
	document.forms[1].pageNumber.value = pageNumber;
	document.forms[1].submit();
}

setTitle('<bean:message bundle="reportResources" key="userstatistics.userstatistics"/>');
</script>
<%
  SearchUserStatisticsForm searchUserStatisticsForm = (SearchUserStatisticsForm) request.getAttribute("searchUserStatisticsForm");
  List lstUserStatistics = searchUserStatisticsForm.getUserStatisticsList();
  long pageNo = searchUserStatisticsForm.getPageNumber();
  long totalPages= searchUserStatisticsForm.getTotalPages();
  long totalRecord= searchUserStatisticsForm.getTotalRecords();
  String strTotalPages= String.valueOf(totalPages);
  String strPageNumber= String.valueOf(pageNo);
  String actionCheck  = searchUserStatisticsForm.getAction();
  Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));  
  long iIndex = ((pageNo-1)*pageSize);
  int count=1;
  
 %>

<html:form action="/searchUserStatistics">
	<html:hidden name="searchUserStatisticsForm" property="totalPages"
		styleId="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="searchUserStatisticsForm" property="pageNumber"
		styleId="pageNumber" value="<%=strPageNumber%>" />
	<html:hidden name="searchUserStatisticsForm" property="action"
		styleId="action" value="search" />

	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>

			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header" colspan="3"><bean:message
											bundle="reportResources" key="userstatistics.search" /></td>
								</tr>

								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top" width="10%">
										<bean:message bundle="reportResources"
											key="userstatistics.useridentity" /> 
											<ec:elitehelp headerBundle="reportResources" text="userstatistics.useridentity" 
											header="userstatistics.useridentity" />
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:text styleId="userIdentity" tabindex="1"
											property="userIdentity" size="50" maxlength="100" />
									</td>
								</tr>

								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>

								<tr>
									<td class="btns-td" valign="middle">&nbsp;</td>
									<td align="left" class="labeltext" valign="top" width="5%">
										<input type="button" tabindex="2" name="Search" width="5%"
										name="search" Onclick="validateSearch()" value="   Search   "
										class="light-btn" /> <input type="button" tabindex="3"
										name="Reset" onclick="reset();" value="   Reset    "
										class="light-btn">
									</td>
								</tr>
								<tr>
									<td colspan="3">&nbsp;</td>
								</tr>
								<% 
    	if(actionCheck!=null){
   	%>
								<html:form action="/searchUserStatistics">
									<html:hidden name="searchUserStatisticsForm" styleId="action"
										property="action" value="closeSelected" />

									<html:hidden name="searchUserStatisticsForm"
										styleId="userIdentity" property="userIdentity" />
									<html:hidden name="searchUserStatisticsForm"
										styleId="pageNumber" property="pageNumber"
										value="<%=strPageNumber%>" />
									<html:hidden name="searchUserStatisticsForm"
										styleId="totalPages" property="totalPages"
										value="<%=strTotalPages%>" />

									<tr>
										<td align="left" class="labeltext" colspan="5" valign="top">
											<table cellSpacing="0" cellPadding="0" width="100%"
												border="0">
												<tr>
													<td class="table-header" width="50%"><bean:message
															bundle="reportResources" key="userstatistics.list.header" />
													</td>
													<td align="right" class="blue-text" valign="middle"
														width="50%">
														<%if(totalRecord == 0){ %> <% } else if(pageNo == totalPages+1) { %>
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
													<td class="btns-td" align="right">
														<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onclick="navigate('next',<%=pageNo+1%>)"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onclick="navigate('last',<%=totalPages+1%>)"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
														<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
														<img src="<%=basePath%>/images/first.jpg" name="Image511"
														onclick="navigate('first',<%= 1%>)"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
														<img src="<%=basePath%>/images/previous.jpg"
														onclick="navigate('next',<%= pageNo-1%>)" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
														<img src="<%=basePath%>/images/next.jpg" name="Image61"
														onclick="navigate('next',<%= pageNo+1%>)"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onclick="navigate('last',<%= totalPages+1%>)"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
														<% } else if(pageNo == totalPages){ %> <img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onclick="navigate('previous',<%= 1%>)"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5"
														onclick="navigate('previous',<%= pageNo-1%>)"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
														<img src="<%=basePath%>/images/next.jpg" name="Image61"
														onclick="navigate('next',<%= pageNo+1%>)"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onclick="navigate('last',<%= totalPages+1%>)"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
														<% } else { %> <img src="<%=basePath%>/images/first.jpg"
														name="Image511" onclick="navigate('previous',<%= 1%>)"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5"
														onclick="navigate('previous',<%=pageNo-1%>)"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
														<img src="<%=basePath%>/images/next.jpg" name="Image61"
														onclick="navigate('next',<%=pageNo+1%>)"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onclick="navigate('last',<%=totalPages+1%>)"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
														<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onclick="navigate('first',<%=1%>)"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5"
														onclick="navigate('previous',<%=pageNo-1%>)"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
														<% } %> <% } %>
													</td>
												</tr>

												<tr height="2">
													<td></td>
												</tr>

												<tr>
													<td class="btns-td" valign="middle" colspan="2">
														<table width="99%" border="0" cellpadding="0"
															cellspacing="0" id="listTable">
															<%
	  							if(lstUserStatistics!=null && lstUserStatistics.size()>0){
							%>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td align="center" class="tblheader" valign="top"
																	width="4%"><bean:message
																		key="general.serialnumber" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="12%"><bean:message bundle="reportResources"
																		key="userstatistics.useridentity" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="20%"><bean:message bundle="reportResources"
																		key="userstatistics.replymessage" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="10%"><bean:message bundle="reportResources"
																		key="userstatistics.callingstationid" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="10%"><bean:message bundle="reportResources"
																		key="userstatistics.param0" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="10%"><bean:message bundle="reportResources"
																		key="userstatistics.param1" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="16%"><bean:message bundle="reportResources"
																		key="userstatistics.eventdate" /></td>
															</tr>

															<logic:iterate id="userStatisticsBean"
																name="searchUserStatisticsForm"
																property="userStatisticsList"
																type="com.elitecore.elitesm.datamanager.reports.userstat.data.IUserStatisticsData">
																<tr>
																	<td align="center" class="tblfirstcol" valign="top"><%=(iIndex+1) %></td>
																	<td align="left" class="tblrows"><bean:write
																			name="userStatisticsBean" property="userIdentity" />&nbsp;</td>
																	<td align="left" class="tblrows"><bean:write
																			name="userStatisticsBean" property="replyMessage" />&nbsp;</td>
																	<td align="left" class="tblrows"><bean:write
																			name="userStatisticsBean" property="callingStationId" />&nbsp;</td>
																	<td align="left" class="tblrows"><bean:write
																			name="userStatisticsBean" property="paramStr0" />&nbsp;</td>
																	<td align="left" class="tblrows"><bean:write
																			name="userStatisticsBean" property="paramStr1" />&nbsp;</td>
																	<td align="left" class="tblrows"><bean:write
																			name="userStatisticsBean" property="strCreateDate" />&nbsp;</td>
																</tr>
																<% iIndex += 1; %>
															</logic:iterate>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr height="4">
																<td></td>
															</tr>
															<%
							}else{
							%>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td align="center" class="tblheader" valign="top"
																	width="4%"><bean:message
																		key="general.serialnumber" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="10%"><bean:message bundle="reportResources"
																		key="userstatistics.useridentity" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="12%"><bean:message bundle="reportResources"
																		key="userstatistics.replymessage" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="17%"><bean:message bundle="reportResources"
																		key="userstatistics.callingstationid" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="16%"><bean:message bundle="reportResources"
																		key="userstatistics.param0" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="16%"><bean:message bundle="reportResources"
																		key="userstatistics.param1" /></td>
																<td align="left" class="tblheader" valign="top"
																	width="16%"><bean:message bundle="reportResources"
																		key="userstatistics.eventdate" /></td>
															</tr>

															<tr>
																<td align="center" colspan="8" class="tblfirstcol">
																	No Records Found.</td>
															</tr>

															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr>
																<td width="50%" colspan="2" class="small-gap">&nbsp;</td>
															</tr>
															<tr height="4">
																<td></td>
															</tr>
															<%
							}
						%>

														</table>
													</td>
												</tr>

												<tr>
													<td class="btns-td" align="right" colspan="8">
														<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <img
														src="<%=basePath%>/images/next.jpg" name="Image61"
														onclick="navigate('next',<%=pageNo+1%>)"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onclick="navigate('last',<%=totalPages+1%>)"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
														<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
														<img src="<%=basePath%>/images/first.jpg" name="Image511"
														onclick="navigate('first',<%= 1%>)"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
														<img src="<%=basePath%>/images/previous.jpg"
														onclick="navigate('next',<%= pageNo-1%>)" name="Image5"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
														<img src="<%=basePath%>/images/next.jpg" name="Image61"
														onclick="navigate('next',<%= pageNo+1%>)"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onclick="navigate('last',<%= totalPages+1%>)"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
														<% } else if(pageNo == totalPages){ %> <img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onclick="navigate('previous',<%= 1%>)"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5"
														onclick="navigate('previous',<%= pageNo-1%>)"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
														<img src="<%=basePath%>/images/next.jpg" name="Image61"
														onclick="navigate('next',<%= pageNo+1%>)"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onclick="navigate('last',<%= totalPages+1%>)"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
														<% } else { %> <img src="<%=basePath%>/images/first.jpg"
														name="Image511" onclick="navigate('previous',<%= 1%>)"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5"
														onclick="navigate('previous',<%=pageNo-1%>)"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
														<img src="<%=basePath%>/images/next.jpg" name="Image61"
														onclick="navigate('next',<%=pageNo+1%>)"
														onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Next" border="0">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"
														onclick="navigate('last',<%=totalPages+1%>)"
														onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Last" border="0">
														<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <img
														src="<%=basePath%>/images/first.jpg" name="Image511"
														onclick="navigate('first',<%=1%>)"
														onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="First" border="0">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5"
														onclick="navigate('previous',<%=pageNo-1%>)"
														onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
														onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
														<% } %> <% } %>
													</td>
												</tr>
											</table>
										</td>
									</tr>
								</html:form>
								<%
			}
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