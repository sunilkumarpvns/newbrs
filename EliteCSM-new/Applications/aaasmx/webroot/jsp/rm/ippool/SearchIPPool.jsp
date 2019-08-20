<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.elitesm.util.constants.IPPoolConstant"%>

<%
    String basePath = request.getContextPath();
    int iIndex =0;
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>

function validateSearch(){
	document.forms[0].pageNumber.value = 1;
	document.forms[0].submit();
}
function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}
function navigate(direction, pageNumber ){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].submit();
}
function navigatePageWithInfo(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("name");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+val+"'>")
	
	var name = $("#status").attr("name");
	var val=$('input[name=status]:radio:checked').val();
	
	$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
	
	var name = $("#nasIPAddress").attr("name");
	var val=$('#nasIPAddress').val();
	
	$("#newFormData").append("<input type='hidden' name='"+name+"' id='"+name+"' value='"+val+"'>")
					 .submit();
}
function show(){
	var selectArray = document.getElementsByName('select');
	if(selectArray.length>0){
			var b = true;
			for (var i=0; i<selectArray.length; i++){
		
			 		 if (selectArray[i].checked == false){  			
			 		 	b=false;
			 		 }
			 		 else{
			 		 	
						b=true;
						break;
					}
				}
			if(b==false){
				alert("Please select atleast one IPPool to Show.");
			}else{
				$("#miscActionId").val('show');
	        	$("#miscIPPoolForm").submit();
			}
	}
	else{
		alert("No Records Found For Show Operation! ");
	}


}
function hide(){
	var selectArray = document.getElementsByName('select');
	if(selectArray.length>0){
			var b = true;
			for (var i=0; i<selectArray.length; i++){
		
			 		 if (selectArray[i].checked == false){  			
			 		 	b=false;
			 		 }
			 		 else{
			 		 	
						b=true;
						break;
					}
				}
			if(b==false){
				alert("Please select atleast one IPPool to Hide.")
			}else{
				$("#miscActionId").val('hide');
	        	$("#miscIPPoolForm").submit();
			}
	}
	else{
		alert("No Records Found For Hide Operation! ")
	}

}

function removeData(){
    var selectVar = false;
    
    for (var i=0; i < document.forms[1].elements.length; i++){
        if(document.forms[1].elements[i].name.substr(0,6) == 'select'){
            if(document.forms[1].elements[i].checked == true)
                selectVar = true;
        }
    }
    if(selectVar == false){
        alert('At least select one IP Pool for remove process');
    }else{
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.searchippooljsp.delete.query"/>';        
        //msg = "All the selected IP Pool would be deleted. Would you like to continue? "
        var agree = confirm(msg);
        if(agree){
        	$("#miscActionId").val('delete');
        	$("#miscIPPoolForm").submit();
        }
    }
}
function  checkAll(){
var arrayCheck = document.getElementsByName('select');
	 	if( document.forms[1].toggleAll.checked == true) {
		 	for (var i = 0; i < arrayCheck.length;i++)
				arrayCheck[i].checked = true ;
	    } else if (document.forms[1].toggleAll.checked == false){
			for (var i = 0; i < arrayCheck.length; i++)
				arrayCheck[i].checked = false ;
		}
}
setTitle('<bean:message bundle="ippoolResources" key="ippool.ippool"/>');
</script>


<%                                         														   
     IPPoolForm ipPoolForm = (IPPoolForm)request.getAttribute("ipPoolForm");
     List lstIPPool = ipPoolForm.getIpPoolList();
     
     long pageNo = ipPoolForm.getPageNumber();
     long totalPages = ipPoolForm.getTotalPages();
     long totalRecord = ipPoolForm.getTotalRecords();
	 int count=1;
	 Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     String strPageNumber = String.valueOf(pageNo);
%>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header" colspan="5">
									<bean:message bundle="ippoolResources" key="ippool.searchippool" />
								</td>
							</tr>
							<tr>
								<td class="small-gap" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td class="small-gap" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td>
									<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="right" border="0" cellpadding="0" cellspacing="0">
										<html:form action="/searchIPPool">
											<html:hidden name="ipPoolForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>" />
											<html:hidden name="ipPoolForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
											<html:hidden name="ipPoolForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="ippoolResources" key="ippool.name" /> 
													<ec:elitehelp headerBundle="ippoolResources" text="ippool.name" header="ippool.name"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="name" tabindex="1" property="name" size="30" maxlength="30" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="ippoolResources" key="ippool.nas.ipaddress" />
													<ec:elitehelp headerBundle="ippoolResources" text="ippool.nasipaddress" 
													header="ippool.nas.ipaddress"/> 
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="nasIPAddress" tabindex="2" property="nasIPAddress" size="30" maxlength="30" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="10%">
													<bean:message bundle="ippoolResources" key="ippool.status" />
													<ec:elitehelp headerBundle="ippoolResources" text="ippool.status" header="ippool.status"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<html:radio name="ipPoolForm" styleId="status" property="status" value="Show" tabindex="3" />Show 
													<html:radio name="ipPoolForm" styleId="status" property="status" value="Hide" tabindex="4" />Hide 
													<html:radio name="ipPoolForm" styleId="status" property="status" value="All" tabindex="5" />All
												</td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" width="5%">
													<input type="button" tabindex="6" name="Search" width="5%" name="IPPoolName" Onclick="validateSearch(document.searchRadiusForm,'list')" value="   Search   " class="light-btn" /> 
													<input type="button" tabindex="7" name="Create" onclick="javascript:navigatePageWithInfo('initCreateIPPool.do','name')" value="   Create   " class="light-btn">
												</td>
											</tr>
										</html:form>
										<%
											if(ipPoolForm.getAction()!=null && ipPoolForm.getAction().equalsIgnoreCase(IPPoolConstant.LISTACTION)) {
		         						%>
										<html:form action="/miscIPPool" styleId="miscIPPoolForm">
											<html:hidden name="ipPoolForm" property="name" />
											<html:hidden name="ipPoolForm" property="ruleSet" />
											<html:hidden property="action" styleId="miscActionId"/>
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%" border="0">
														<tr>
															<td class="table-header" width="50%">
																<bean:message bundle="ippoolResources" key="ippool.ippoollist" />
															</td>

															<td align="right" class="blue-text" valign="middle" width="50%">
																<%if(totalRecord == 0){ 
																  }else if(pageNo == totalPages+1) { %>
																  	[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
																<% } else if(pageNo == 1) { %> 
																	[<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %> 
																<% } else { %> 
																	[<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %> 
																<% } %>
															</td>
														</tr>
														<tr>
															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle">
																<input type="button" name="Show" Onclick="show()" value="   Show   " class="light-btn"> 
																<input type="button" name="Hide" Onclick="hide()" value="   Hide   " class="light-btn"> 
																<input type="button" name="Delete" Onclick="removeData()" value="   Delete   " class="light-btn">
															</td>

															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> 
																	<% if(pageNo == 1){ %> 
																		<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
																		<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0"/> 
																	<% } %> 
																	<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
																		<%  if(pageNo-1 == 1){ %>
																		<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
																		<img src="<%=basePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
																		<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
																		<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
																		<% } else if(pageNo == totalPages){ %> 
																		<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
																		<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
																		<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
																		<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
																		<% } else { %> 
																		<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
																		<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
																		<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
																		<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
																		<% } %> 
																	<% } %> 
																	<% if(pageNo == totalPages+1) { %> 
																		<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%=1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
																		<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
																	<% } %> 
																<% } %>
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
																			<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="40px">
																			<bean:message bundle="ippoolResources" key="ippool.serialnumber" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="25%">
																			<bean:message bundle="ippoolResources" key="ippool.name" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="*">
																			<bean:message bundle="ippoolResources" key="ippool.description" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="18%">
																			<bean:message bundle="ippoolResources" key="ippool.lastupdate" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="8%">
																			<bean:message bundle="ippoolResources" key="ippool.status" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="40px">
																			<bean:message bundle="ippoolResources" key="ippool.edit" />
																		</td>
																	</tr>
																	<% if(lstIPPool!=null && lstIPPool.size()>0){ %>
																	<logic:iterate id="ipPoolBean" name="ipPoolForm" property="ipPoolList" type="com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData">
																		<% Timestamp dtLastModifiedDate = (java.sql.Timestamp)org.apache.struts.util.RequestUtils.lookup(pageContext, "ipPoolBean", "lastModifiedDate", null); %>
																		<tr>
																			<td align="center" class="tblfirstcol">
																				<input type="checkbox" name="select" value="<bean:write name="ipPoolBean" property="ipPoolId"/>" />
																			</td>
																			<td align="center" class="tblrows"><%=((pageNo-1)*pageSize)+count%></td>
																			<td align="left" class="tblrows">
																				<a href="viewIPPool.do?ipPoolId=<bean:write name="ipPoolBean" property="ipPoolId"/>"><bean:write name="ipPoolBean" property="name" /></a>
																			</td>
																			<td align="left" class="tblrows"><%=EliteUtility.formatDescription(ipPoolBean.getDescription()) %>&nbsp;</td>
																			<td align="center" class="tblrows"><%=EliteUtility.dateToString(dtLastModifiedDate, ConfigManager.get(ConfigConstant.DATE_FORMAT))%></td>
																			<td align="center" class="tblrows">
																				<% String CommonStatusValue = (String)org.apache.struts.util.RequestUtils.lookup(pageContext, "ipPoolBean", "commonStatusId", null);
																					if(CommonStatusValue.equals(IPPoolConstant.HIDE_STATUS_ID) || CommonStatusValue.equals(IPPoolConstant.DEFAULT_STATUS_ID)){ 
																				%> 
																					<img src="<%=basePath%>/images/hide.jpg" border="0">
																				<% }else if(CommonStatusValue.equals(IPPoolConstant.SHOW_STATUS_ID)){ %>
																					<img src="<%=basePath%>/images/show.jpg" border="0">
																				<% } %>
																			</td>
																			<td align="center" class="tblrows">
																				<a href="initUpdateIPPool.do?ipPoolId=<bean:write name="ipPoolBean" property="ipPoolId"/>&action=initUpdate">
																					<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
																				</a>
																			</td>
																		</tr>
																		<% count=count+1; %>
																		<% iIndex += 1; %>
																	</logic:iterate>
																	<% }else{ %>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="7">No Records Found.</td>
																	</tr>
																	<% } %>
																</table>
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle">
																<input type="button" name="Show" Onclick="show()" value="   Show   " class="light-btn"> 
																<input type="button" name="Hide" Onclick="hide()" value="   Hide   " class="light-btn"> 
																<input type="button" name="Delete" Onclick="removeData()" value="   Delete   " class="light-btn">
															</td>

															<td class="btns-td" align="right">
																<% if(totalPages >= 1) { %> 
																	<% if(pageNo == 1){ %> 
																		<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
																		<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0"/> 
																	<% } %> 
																	<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
																		<%  if(pageNo-1 == 1){ %>
																		<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
																		<img src="<%=basePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
																		<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
																		<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
																		<% } else if(pageNo == totalPages){ %> 
																		<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
																		<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
																		<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
																		<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
																		<% } else { %> 
																		<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
																		<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
																		<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
																		<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
																		<% } %> 
																	<% } %> 
																	<% if(pageNo == totalPages+1) { %> 
																		<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%=1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
																		<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
																	<% } %> 
																<% } %>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</html:form>
										<% } %>
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