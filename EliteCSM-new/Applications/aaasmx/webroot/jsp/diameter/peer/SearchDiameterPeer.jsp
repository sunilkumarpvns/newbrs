
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
<%@ page import="com.elitecore.elitesm.web.diameter.peer.forms.SearchDiameterPeerForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData"%>
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
	
	document.searchDiameterPeerForm.pageNumber.value = 1;
	document.searchDiameterPeerForm.submit(); 
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}

function removeData(){
	document.searchDiameterPeerForm.actionType.value = 'delete';
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
					document.searchDiameterPeerForm.action='<%=basePath%>/miscDiameterPeer.do';
	  				document.searchDiameterPeerForm.submit();
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
	var path = '<%=basePath%>/miscDiameterPeer.do?actionType=showall'; 
	window.open(path,'DiameterPeer','resizable=yes,scrollbars=yes');
}

setTitle('<bean:message bundle="diameterResources" key="diameterpeer.diameterpeer"/>');
</script>
<%
     SearchDiameterPeerForm searchDiameterPeerForm = (SearchDiameterPeerForm)request.getAttribute("searchDiameterPeerForm");
     List lstDiameterPeer = searchDiameterPeerForm.getListDiameterPeer();
    
     long pageNo = searchDiameterPeerForm.getPageNumber();
     long totalPages = searchDiameterPeerForm.getTotalPages();
     long totalRecord = searchDiameterPeerForm.getTotalRecords();
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
										key="diameterpeer.searchdiameterpeer" /></td>
							</tr>
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblDiameterPeer"
										id="c_tblDiameterPeer" align="right" border="0"
										cellpadding="0" cellspacing="0">
										<html:form action="/searchDiameterPeer">
											<html:hidden name="searchDiameterPeerForm"
												styleId="pageNumber" property="pageNumber" />
											<html:hidden name="searchDiameterPeerForm"
												styleId="totalPages" property="totalPages"
												value="<%=strTotalPages%>" />
											<html:hidden name="searchDiameterPeerForm"
												styleId="totalRecords" property="totalRecords"
												value="<%=strTotalRecords%>" />
											<html:hidden name="searchDiameterPeerForm"
												styleId="actionType" property="actionType" />
											<tr>
												<td align="left" class="btns-td" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%"
														border="0">
														<tr>
															<td colspan='100%'>&nbsp;</td>
														</tr>
														<tr>
															<td align="left" class="labeltext" valign="top"
																width="8%"><bean:message bundle="diameterResources"
																	key="diameterpeer.name" /> 
																	<ec:elitehelp headerBundle="diameterResources" text="diameterpeer.peername" 
																	header="diameterpeer.name"/>
															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><html:text styleId="name"
																	property="name" size="30" maxlength="256"
																	style="width:250px" tabindex="1" /></td>
														</tr>
														<tr>
															<td align="left" class="labeltext" valign="top"
																width="8%"><bean:message bundle="diameterResources"
																	key="diameterpeer.hostidentity_remoteaddress" /> 
																	<ec:elitehelp headerBundle="diameterResources" text="diameterpeer.hostidentity_remoteaddress"
																	header="diameterpeer.hostidentity_remoteaddress"/>
															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><html:text styleId="hostIdentity"
																	property="hostIdentity" size="30" maxlength="256"
																	style="width:250px" tabindex="1" /></td>
														</tr>
														<tr>
															<td align="left" class="labeltext" valign="top"
																width="8%"><bean:message bundle="diameterResources"
																	key="diameterpeer.profilename" /> 
																	<ec:elitehelp headerBundle="diameterResources" text="diameterpeer.name" 
																	header="diameterpeer.profilename"/>
															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><bean:define id="profileList"
																	name="searchDiameterPeerForm"
																	property="peerProfileList"></bean:define> <html:select
																	name="searchDiameterPeerForm" tabindex="2"
																	styleId="peerProfileId" property="peerProfileId"
																	size="1">
																	<html:option value="0">-----Select-----</html:option>
																	<html:options collection="profileList"
																		property="peerProfileId" labelProperty="profileName" />
																</html:select></td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle">&nbsp;</td>
															<td align="left" class="labeltext" valign="top"
																width="5%"><input type="button" tabindex="3"
																name="Search" width="5%" Onclick="validateSearch()"
																value="  Search  " class="light-btn" /> <input
																type="button" tabindex="4" name="Create"
																value="   Create   " onclick="javascript:create();"
																class="light-btn"></td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%"
														border="0">
														<tr>
															<td class="table-header" width="50%"><bean:message
																	bundle="diameterResources" key="diameterpeer.peerlist" />
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
															<td class="btns-td" valign="middle"><html:button
																	property="c_btnDelete" onclick="removeData()" tabindex="5"
																	value="   Delete   " styleClass="light-btn" /> <html:button
																	property="c_btnshowall" onclick="showall()"
																	tabindex="6" value="   Show All   "
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
																<table width="99%" border="0" cellpadding="0"
																	cellspacing="0" id="listTable">
																	<tr>
																		<td align="center" class="tblheader" valign="top" width="2%">
																			<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="2%">
																			<bean:message bundle="diameterResources" key="diameterpeer.serialnumber" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="15%">
																			<bean:message bundle="diameterResources" key="diameterpeer.name" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="15%">
																			<bean:message bundle="diameterResources" key="diameterpeer.hostidentity" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="15%">
																			<bean:message bundle="diameterResources" key="diameterpeer.realmname" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="15%">
																			<bean:message bundle="diameterResources" key="diameterpeer.remoteaddress" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="15%">
																			<bean:message bundle="diameterResources" key="diameterpeer.uriformat" />
																		</td>
																		<%-- <td align="left" class="tblheader" valign="top" width="15%">
																			<bean:message bundle="diameterResources" key="diameterpeer.localaddress" />
																		</td> --%>
																		<td align="left" class="tblheader" valign="top" width="15%">
																			<bean:message bundle="diameterResources" key="diameterpeer.profilename" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="6%">
																			<bean:message bundle="diameterResources" key="diameterpeer.edit" />
																		</td>
																	</tr>
																	<%	if(lstDiameterPeer!=null && lstDiameterPeer.size()>0){%>
																	<logic:iterate id="diameterPeerBean"
																		name="searchDiameterPeerForm"
																		property="listDiameterPeer"
																		type="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData">
																		<tr>
																			<td align="center" class="tblfirstcol"><input
																				type="checkbox" name="select"
																				value="<bean:write name="diameterPeerBean" property="peerUUID"/>" />&nbsp;
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows"><a
																				href="<%=basePath%>/viewDiameterPeer.do?peerUUID=<bean:write name="diameterPeerBean" property="peerUUID"/>"><bean:write
																						name="diameterPeerBean" property="name" /></a></td>
																				
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean" property="hostIdentity" />
																				&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean" property="realmName" />
																				&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean" property="remoteAddress" />
																				&nbsp;</td>
																				<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean" property="diameterURIFormat" />
																			</td>
																			<%-- <td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean" property="localAddress" />
																				&nbsp;</td> --%>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterPeerBean"
																					property="diameterPeerProfileData.profileName" />&nbsp;</td>
																			<td align="center" class="tblrows">
																			<a href='<%=basePath%>/initUpdateDiameterPeer.do?peerUUID=<bean:write name="diameterPeerBean" property="peerUUID"/>'>
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
																				key="diameterpeer.norecordsmsg" />
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
																<a href="javascript:void(0)" onclick="navigate(<%=pageNo-1%>);"><img
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

  function navigate(pageNo){
	 document.searchDiameterPeerForm.action="searchDiameterPeer.do?pageNo="+pageNo;
	 document.searchDiameterPeerForm.submit();  
  }
  
  function create(){
     location.href='<%=basePath%>/initCreateDiameterPeer.do?hostIdentity='+document.searchDiameterPeerForm.hostIdentity.value+'&peerProfileId='+document.searchDiameterPeerForm.peerProfileId.value+'&name='+document.searchDiameterPeerForm.name.value;
  }
</script>
