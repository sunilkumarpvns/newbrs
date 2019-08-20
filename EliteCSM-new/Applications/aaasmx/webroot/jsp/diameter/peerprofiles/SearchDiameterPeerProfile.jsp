
<%@page import="com.elitecore.elitesm.util.logger.Logger"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>


<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.util.constants.RadiusPolicyConstant"%>
<%@ page import="com.elitecore.elitesm.web.diameter.peerprofiles.forms.SearchDiameterPeerProfileForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData"%>
<%
    String basePath = request.getContextPath();
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<style>
.light-btn {
	border: medium none;
	font-family: Arial;
	font-size: 12px;
	color: #FFFFFF;
	background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');
	font-weight: bold
}
</style>
<script>

function validateSearch(){
	
	document.searchDiameterPeerProfileForm.pageNumber.value = 1;
	document.searchDiameterPeerProfileForm.submit();
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}

function removeData(){
	document.searchDiameterPeerProfileForm.actionType.value = 'delete';
	var selectArray = document.getElementsByName('select');
	
	if(selectArray.length>0){
		var b = true;
		for (i=0; i<selectArray.length; i++){

	 		 if (selectArray[i].checked == false){  			
	 		 	b=false;
	 		 }
	 		 else{
	 		 	
				b=true;
				break;
			}
		}
		
		if(b==false){
		alert("Selection Required To Perform Delete Operation.")
		}else{
			var r=confirm("This will delete the selected items. Do you want to continue ?");
				if (r==true)
	  			{
					document.forms[0].action='<%=basePath%>/miscDiameterPeerProfile.do';
					document.forms[0].submit();
	  			}
		}
	}
	else{
		alert("No Records Found For Delete Operation! ")
	}
}	

function  checkAll(){
var arrayCheck = document.getElementsByName('select');
	if( document.forms[0].toggleAll.checked == true) {
		for (i = 0; i < arrayCheck.length;i++)
			arrayCheck[i].checked = true ;
	} else if (document.forms[0].toggleAll.checked == false){
		for (i = 0; i < arrayCheck.length; i++)
			arrayCheck[i].checked = false ;
	}
}

function showall(){  
	var path = '<%=basePath%>/miscDiameterPeerProfile.do?actionType=showall'; 
	window.open(path,'DiameterPeerProfile','resizable=yes,scrollbars=yes');
}

setTitle('<bean:message bundle="diameterResources" key="diameterpeerprofile.diameterpeerprofile"/>');
</script>
<%
     SearchDiameterPeerProfileForm searchDiameterPeerProfileForm = (SearchDiameterPeerProfileForm)request.getAttribute("searchDiameterPeerProfileForm");
     List lstDiameterPeerProfile = searchDiameterPeerProfileForm.getListDiameterPeerProfile();
     String strProfileName = searchDiameterPeerProfileForm.getProfileName();
     
     long pageNo = searchDiameterPeerProfileForm.getPageNumber();
     long totalPages = searchDiameterPeerProfileForm.getTotalPages();
     long totalRecord = searchDiameterPeerProfileForm.getTotalRecords();
	 int count=1;
     
     String strPageNumber = String.valueOf(pageNo);     
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
%>

<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header" colspan="5"><bean:message
										bundle="diameterResources"
										key="diameterpeerprofile.searchdiameterpeerprofile" /></td>
							</tr>
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" cellpadding="0" cellspacing="0">
										<html:form action="/searchDiameterPeerProfiles">
											<html:hidden name="searchDiameterPeerProfileForm" styleId="pageNumber" property="pageNumber" />
											<html:hidden name="searchDiameterPeerProfileForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="searchDiameterPeerProfileForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
											<html:hidden name="searchDiameterPeerProfileForm" styleId="actionType" property="actionType" />
											<tr>
												<td align="left" class="btns-td" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%" border="0">
														<tr>
															<td colspan='100%'>&nbsp;</td>
														</tr>
														<tr>
															<td align="left" class="labeltext" valign="top" width="20%">
																<bean:message bundle="diameterResources" key="diameterpeerprofile.name" /> 
																<ec:elitehelp headerBundle="diameterResources"  text="diameterpeerprofile.name" header="diameterpeerprofile.name"/>
															</td>
															<td align="left" class="labeltext" valign="top" width="80%">
																<html:text styleId="profileName" property="profileName" size="30" maxlength="64" style="width:250px" tabindex="1" />
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle">&nbsp;</td>
															<td align="left" class="labeltext" valign="top" width="5%">
																<input type="button" name="Search" width="5%" name="DiameterPeerProfileName" tabindex="2" Onclick="validateSearch()" value="  Search  " class="light-btn" /> 
																<input type="button" name="Create" 	value="   Create   " tabindex="3" onclick="javascript:location.href='<%=basePath%>/initCreateDiameterPeerProfile.do?profileName='+document.searchDiameterPeerProfileForm.profileName.value" class="light-btn"/>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<%
												 	if(searchDiameterPeerProfileForm.getActionType()!=null && searchDiameterPeerProfileForm.getActionType().equalsIgnoreCase(RadiusPolicyConstant.LISTACTION)){
														       	%>
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%"
														border="0">
														<tr>
															<td class="table-header" width="50%"><bean:message
																	bundle="diameterResources"
																	key="diameterpeerprofile.peerprofileslist" /></td>

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
															<td class="btns-td" valign="middle"><html:button
																	property="c_btnDelete" onclick="removeData()" tabindex="4"
																	value="   Delete   " styleClass="light-btn" /> <html:button
																	property="c_btnshowall" onclick="showall()"
																	tabindex="5" value="   Show All   "
																	styleClass="light-btn" /></td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="javascript:void(0)"
																onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a href="javascript:void(0)"
																onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);""><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" colspan="2">
																<table width="100%" border="0" cellpadding="0"
																	cellspacing="0" id="listTable">
																	<tr>
																		<td align="center" class="tblheader" valign="top"
																			width="1%"><input type="checkbox"
																			name="toggleAll" value="checkbox"
																			onclick="checkAll()" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="2%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeerprofile.serialnumber" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="13%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeerprofile.name" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="13%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeerprofile.dwrduration" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="13%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeerprofile.initconnection" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="13%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeerprofile.retrycount" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="13%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeerprofile.securitystandard" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="3%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeerprofile.edit" /></td>
																	</tr>
																	<%	if(lstDiameterPeerProfile!=null && lstDiameterPeerProfile.size()>0){%>
																	<logic:iterate id="diameterPeerProfileBean"
																		name="searchDiameterPeerProfileForm"
																		property="listDiameterPeerProfile"
																		type="com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData">
																		<%	DiameterPeerProfileData sData = (DiameterPeerProfileData)lstDiameterPeerProfile.get(iIndex);
																						Timestamp dtLastUpdate = sData.getLastModifiedDate();
																					%>
																		<tr>
																			<td align="center" class="tblfirstcol"><input
																				type="checkbox" name="select"
																				value="<bean:write name="diameterPeerProfileBean" property="peerProfileId"/>" />
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows"><a
																				href="<%=basePath%>/viewDiameterPeerProfile.do?peerProfileId=<bean:write name="diameterPeerProfileBean" property="peerProfileId"/>"><bean:write
																						name="diameterPeerProfileBean"
																						property="profileName" /></a></td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerProfileBean"
																					property="dwrDuration" /> &nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerProfileBean"
																					property="initConnectionDuration" />&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerProfileBean"
																					property="retryCount" /> &nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerProfileBean"
																					property="securityStandard" /> &nbsp;</td>
																			<td align="center" class="tblrows"><a
																				href="<%=basePath%>/initUpdateDiameterPeerProfile.do?peerProfileId=<bean:write name="diameterPeerProfileBean" property="peerProfileId"/>">
																					<img src="<%=basePath%>/images/edit.jpg" alt="Edit"
																					border="0">
																			</a></td>
																		</tr>
																		<% count=count+1; %>
																		<% iIndex += 1; %>
																	</logic:iterate>

																	<%	}else{
																		%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="8">
																			<bean:message bundle="diameterResources"
																				key="diameterpeerprofile.norecordsmsg" />
																		</td>
																	</tr>
																	<%	}%>
																</table>
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle"><html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /> <html:button
																	property="c_btnshowall" onclick="showall()"
																	value="   Show All   " styleClass="light-btn" /></td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="javascript:void(0)"
																onclick="navigate(<%=pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%=totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a href="javascript:void(0)"
																onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a href="javascript:void(0)"
																onclick="navigate(<%= pageNo+1%>);"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%= totalPages+1%>);""><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="javascript:void(0)" onclick="navigate(<%=1%>);"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="javascript:void(0)"
																onclick="navigate(<%=pageNo-1%>);"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <% } %> <% } %>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
													</table>
												</td>
											</tr>
											<%}%>
										</html:form>
									</table>
								</td>
							</tr>

						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>

<script language="javascript">
  document.forms[0].profileName.focus();
  function navigate(pageNo){
	 document.searchDiameterPeerProfileForm.action="searchDiameterPeerProfiles.do?pageNo="+pageNo;
	 document.searchDiameterPeerProfileForm.submit();  
  }
  
</script>
