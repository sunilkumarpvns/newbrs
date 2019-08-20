<%@page import="com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup"%>
<%@page import="com.elitecore.elitesm.web.radius.radiuspolicygroup.forms.RadiusPolicyGroupForm"%>
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

<%
	String basePath = request.getContextPath();
    String strDatePattern = "dd MMM,yyyy hh:mm:ss";
    SimpleDateFormat dateForm = new SimpleDateFormat(strDatePattern);
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
	document.radiusPolicyGroupForm.pageNumber.value = 1;
	document.radiusPolicyGroupForm.submit();
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}
function navigate(direction, pageNumber ){
	document.radiusPolicyGroupForm.pageNumber.value = pageNumber;
	document.radiusPolicyGroupForm.submit();
}
function navigatePageWithStatus(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("name");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>").submit();
}

function removeData(){
	
	document.radiusPolicyGroupForm.action.value = 'delete';
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
	  				document.forms[0].submit();
	  			}
		}
	}else{
		alert("No Records Found For Delete Operation! ");
	}
}	
function  checkAll(checkbox){

	if($(checkbox).attr('checked')){
		$('input:checkbox[name="select"]').each(function(){
			$(this).attr('checked',true);
		});
	}else{
		$('input:checkbox[name="select"]').each(function(){
			$(this).attr('checked',false);
		});
	}
	
}
setTitle('<bean:message bundle="radiusResources" key="radiuspolicy.radiuspolicygroup"/>');

</script>
<%
	RadiusPolicyGroupForm radiusPolicyGroupForm = (RadiusPolicyGroupForm)request.getAttribute("radiusPolicyGroupForm");
     List listRadiusPolicyGroup = radiusPolicyGroupForm.getListRadiusPolicyGroup();
     String strName = radiusPolicyGroupForm.getPolicyname();
     
     long pageNo = radiusPolicyGroupForm.getPageNumber();
     long totalPages = radiusPolicyGroupForm.getTotalPages();
     long totalRecord = radiusPolicyGroupForm.getTotalRecords();
	 int count=1;
     
     String strPageNumber = String.valueOf(pageNo);     
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
%>

<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH)%>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td cellpadding="0" cellspacing="0" border="0" width="100%"
						class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">

							<tr>
								<td class="table-header" colspan="5">
									<bean:message bundle="radiusResources" key="radiuspolicy.radiuspolicygroup.search"/>
								</td>
							</tr>
							<tr>
								<td class="small-gap" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="3">
									<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0">
										<html:form action="/searchRadiusPolicyGroup">
											<html:hidden name="radiusPolicyGroupForm" styleId="action" property="action" />
											<html:hidden name="radiusPolicyGroupForm" styleId="pageNumber" property="pageNumber" />
											<html:hidden name="radiusPolicyGroupForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="radiusPolicyGroupForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
											<tr>
												<td align="left" class="captiontext" valign="top" width="8%">
													<bean:message bundle="radiusResources" key="radiuspolicy.radiuspolicygroup.name" />
													<ec:elitehelp headerBundle="radiusResources" text="radiuspolicy.radiuspolicygroup.name" header="radiuspolicy.radiuspolicygroup.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="policyname" property="policyname" size="30" maxlength="30" tabindex="1" style="width:250px;"/>
												</td>
											</tr>
											<tr>
												<td colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" name="Search" width="5%" name="RadiusPolicyName" tabindex="5" onclick="validateSearch()" value="   Search   " class="light-btn" /> 
													<input type="button" name="Create" tabindex="6" value="   Create   " 	onclick="javascript:navigatePageWithStatus('createRadiusPolicyGroup.do','policyname');" class="light-btn">
												</td>
											</tr>

										<%
											if(radiusPolicyGroupForm.getAction()!=null && radiusPolicyGroupForm.getAction().equalsIgnoreCase(RadiusPolicyConstant.LISTACTION)){
										%>
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%"
														border="0">
														<tr>
															<td class="table-header" width="50%">RADIUS POLICY GROUP LIST</td>
															<td align="right" class="blue-text" valign="middle"
																width="50%">
																<%
																	if(totalRecord == 0) {
																%> <%
 	}else if(pageNo == totalPages+1) {
 %>
																[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%=totalRecord%>
																<%
																	} else if(pageNo == 1) {
																%> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
																of <%=totalRecord%> <%
 	} else {
 %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
																of <%=totalRecord%> <%
 	}
 %>
															</td>
														</tr>
														<tr>
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" style="padding-top: 5px;padding-bottom: 3px;">
																<html:button property="c_btnDelete" onclick="removeData()" value="   Delete   " styleClass="light-btn" />
															</td>
															<td class="btns-td" align="right">
																<%
																	if(totalPages >= 1) {
																%> <%
 	if(pageNo == 1){
 %> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<%
																	}
																%> <%
 	if(pageNo>1 && pageNo!=totalPages+1) {
 %> <%
 	if(pageNo-1 == 1){
 %>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<%
																	} else if(pageNo == totalPages){
																%> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<%
																	} else {
																%> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<%
																	}
																%> <%
 	}
 %> <%
 	if(pageNo == totalPages+1) {
 %> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <%
 	}
 %> <%
 	}
 %>
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" colspan="2">
																<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable">
																	<tr>
																		<td align="center" class="tblheader" valign="top" width="1%">
																			<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll(this);" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="40px">
																			<bean:message bundle="radiusResources" key="radiuspolicy.serialnumber" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="*">
																			<bean:message bundle="radiusResources" key="radiuspolicy.name" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="40px">
																			Edit
																		</td>
																	</tr>
																	<%
																		if(listRadiusPolicyGroup!=null && listRadiusPolicyGroup.size()>0){
																	%>
																	<logic:iterate id="radiusPolicyGroupBean" name="radiusPolicyGroupForm" property="listRadiusPolicyGroup" type="com.elitecore.elitesm.datamanager.radius.radiuspolicygroup.data.RadiusPolicyGroup">
																		<%
																			RadiusPolicyGroup sData = (RadiusPolicyGroup)listRadiusPolicyGroup.get(iIndex);
																		%>
																		<tr>
																			<td align="center" class="tblfirstcol">
																				<input type="checkbox" name="select" value="<bean:write name="radiusPolicyGroupBean" property="policyId"/>" />
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows">
																				<a href="<%=basePath%>/viewRadiusPolicyGroup.do?policyId=<bean:write name="radiusPolicyGroupBean" property="policyId"/>"><bean:write name="radiusPolicyGroupBean" property="policyName" /></a>
																			</td>
																			<td align="center" class="tblrows">
																				<a href="<%=request.getContextPath()%>/updateRadiusPolicyGroup.do?policyId=<bean:write name="radiusPolicyGroupBean" property="policyId"/>">
																					<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
																				</a>
																			</td>
																		</tr>
																		<% count=count+1; %>
																		<% iIndex += 1; %>
																	</logic:iterate>
																	<%	}else{	%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="6">
																			No Records Found.
																		</td>
																	</tr>
																	<%	}%>
																</table>
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" style="padding-top: 5px;">
																<html:button property="c_btnDelete" onclick="removeData()" value="   Delete   " styleClass="light-btn" />
															</td>
															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> <% if(pageNo == 1){ %> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(pageNo>1 && pageNo!=totalPages+1) {%> <%  if(pageNo-1 == 1){ %>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else if(pageNo == totalPages){ %> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } else { %> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= 1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= pageNo-1%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous"
																	border="0"></a> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= pageNo+1%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= totalPages+1%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% } %> <% if(pageNo == totalPages+1) { %> <a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%=1%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a
																href="searchRadiusPolicyGroup.do?action=list&pageNo=<%= pageNo-1%>"><img
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
