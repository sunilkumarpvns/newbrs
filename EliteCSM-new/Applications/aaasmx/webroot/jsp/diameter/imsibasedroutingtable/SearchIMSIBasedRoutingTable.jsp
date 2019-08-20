<%@ page import="com.elitecore.elitesm.util.constants.BaseConstant"%>
<%@ page import="com.elitecore.elitesm.util.constants.DiameterConstant"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.util.logger.Logger"%>
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
<%@ page import="com.elitecore.elitesm.web.diameter.routingconfig.forms.SearchDiameterRoutingConfForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData"%>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.ResultCode" %>
<%@ page import="com.elitecore.diameterapi.core.stack.constant.OverloadAction" %>
<%@ page import="com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm" %>

<%
	String basePath = request.getContextPath();
    Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
    int iIndex =0;
    IMSIBasedRoutingTableForm imsiBasedRoutingTableForm = (IMSIBasedRoutingTableForm)request.getAttribute("imsiBasedRoutingTableForm");
  	
    List lstImsiBasedRoutingTable = imsiBasedRoutingTableForm.getListIMSIBasedRoutingTable();
    
    long pageNo = imsiBasedRoutingTableForm.getPageNumber();
	long totalPages = imsiBasedRoutingTableForm.getTotalPages();
	long totalRecord = imsiBasedRoutingTableForm.getTotalRecords();
	String strPageNumber = String.valueOf(pageNo);     
	String strTotalPages = String.valueOf(totalPages);
	String strTotalRecords = String.valueOf(totalRecord);
	String strAction="";
	
	if(imsiBasedRoutingTableForm.getAction()!=null){
		 strAction=imsiBasedRoutingTableForm.getAction();
	}
	
	 int count=1;
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
var isValidName;

function prepareUrl(image,value,sortOrderValue){
	var name = '';
	image.href = image.href + escape(name);
	makeUrl(image,value,sortOrderValue);
}

function removeData(){
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
			alert("Selection Required To Perform Delete Operation.");
		}else{
			var r=confirm("This will delete the selected items. Do you want to continue ?");
	
			if (r==true){
				document.forms[0].action.value="deleteRoutingTable";
				document.forms[0].submit();
	  		}
		}
	}else{
		alert("No Records Found For Delete Operation! ");
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
	var path = '<%=basePath%>/miscDiameterRoutingConf.do?action=showall';
	window.open(path,'DiameterRoutingTable','resizable=yes,scrollbars=yes');
}

function navigatePageWithType(action,appendAttrbId) {
	createNewForm("newFormData",action);
	var name = $("#"+appendAttrbId).attr("imsiBasedRoutingTableName");
	var val = $("#"+appendAttrbId).val();
	$("#newFormData").append("<input id='hiddenName' type='hidden' name='"+name+"' value='"+val+"'>").submit();
}

function validateCreate(){
	var name= $('#imsiBasedRoutingTableName').val();
	if(!isValidName){
		alert('Enter Valid Routing Table Name');
		$('#imsiBasedRoutingTableName').focus()
		return false;
	}else if(name){
		 location.href='<%=basePath%>/createIMSIBasedRoutingTable.do?name=' + name;
	}else{
		alert('Please enter Routing Table Name');
		return false;
	}
}

function validateSearch(){
	document.forms[0].pageNumber.value = 1;
	document.forms[0].submit(); 
}

function verifyName() {
	var searchName = document.getElementById("imsiBasedRoutingTableName").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.IMSI_BASED_ROUTING_TABLE%>',searchName,'create','','verifyNameDiv');
	if(isValidName){
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.MSISDN_BASED_ROUTING_TABLE%>',searchName,'create','','verifyNameDiv');
	}
}

setTitle('<bean:message bundle="diameterResources" key="imsibasedroutingtable.title"/>');
</script>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td colspan="3">
									<html:form action="/initSearchIMSIBasedRoutingTable" styleId="imsiBasedRoutingTableId">
										<html:hidden name="imsiBasedRoutingTableForm" styleId="pageNumber" property="pageNumber" />
										<html:hidden name="imsiBasedRoutingTableForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
										<html:hidden name="imsiBasedRoutingTableForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
										<html:hidden name="imsiBasedRoutingTableForm" styleId="action" property="action" />
										<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="left" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%" border="0">
														<tr>
															<td class="table-header" colspan="100%">
																<bean:message bundle="diameterResources" key="imsibasedroutingtable.title" />
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														<tr>
															<td align="left" class="btns-td" valign="top" width="20%">
																<bean:message bundle="diameterResources" key="routingconf.tablename" /> 
																<ec:elitehelp headerBundle="diameterResources" text="routingconf.table.name" header="routingconf.tablename"/>
															</td>
															<td align="left" class="labeltext" valign="top" width="80%">
																<html:text styleId="imsiBasedRoutingTableName" property="imsiBasedRoutingTableName" tabindex="1" size="30" maxlength="128" onkeyup="verifyName();" style="width:250px" />
															    <font color="#FF0000"> *</font>
																<div id="verifyNameDiv" class="labeltext"></div> 
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
															<td style="padding-left: 5px;">
																<input type="button" name="Search" width="5%"  onclick="validateSearch()" value="   Search   " class="light-btn" tabindex="5" /> <!-- <input type="button" name="Reset" onclick="reset();" value="   Reset    " class="light-btn" > -->
																<input type="button" name="Create" onclick="validateCreate();" value="   Create   " class="light-btn" tabindex="6">
															</td>
														</tr>
														<tr>
															<td align="left" class="labeltext" colspan="5" valign="top">
																<table cellSpacing="0" cellPadding="0" width="100%" border="0">
																	<tr>
																		<td class="table-header" width="50%">
																			<bean:message bundle="diameterResources" key="imsibasedroutingtable.list" />
																		</td>
																		<td align="right" class="blue-text" valign="middle" width="50%">
																			<% if(totalRecord == 0) { %> <% }else if(pageNo == totalPages+1) { %>
																			[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
																			<% } else if(pageNo == 1) { %> [<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>]
																			of <%= totalRecord %> <% } else { %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>]
																			of <%= totalRecord %> <% } %>
																		</td>
																	</tr>
																	<tr>
																		<td class="btns-td" valign="middle" style="padding-top: 5px;">
																			<html:button property="c_btnDelete" onclick="removeData()" tabindex="5" value="   Delete   " styleClass="light-btn" /> 
																		</td>
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
																				border="0"></a> 
																				<% } %> <% } %>
																			</td>
																		</tr>
																		<tr height="2">
																			<td></td>
																		</tr>
																		<tr>
																			<td class="btns-td"  colspan="2">
																				<table width="99%" border="0" cellpadding="0" cellspacing="0" id="listTable">
																					<tr>
																					<td align="center" class="tblheader" valign="top" width="2%">
																						<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAll()" />
																					</td>
																					<td align="center" class="tblheader" valign="top" width="3%">
																						<bean:message bundle="diameterResources" key="diameterpeer.serialnumber" />
																					</td>
																					<td align="left" class="tblheader" valign="top" width="88%">
																						<bean:message bundle="diameterResources" key="imsibasedroutingtable.routingtablename" />
																					</td>
																					<td align="center" class="tblheader" valign="top" width="7%">
																						<bean:message bundle="diameterResources" key="diameterpeer.edit" />
																					</td>
																				</tr>
																				<%	if(lstImsiBasedRoutingTable!=null && lstImsiBasedRoutingTable.size()>0){%>
																				<logic:iterate id="imsiRoutingTableBean" name="imsiBasedRoutingTableForm" property="listIMSIBasedRoutingTable" type="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData">
																					<tr>
																						<td align="center" class="tblfirstcol">
																							<input type="checkbox" name="select" value="<bean:write name="imsiRoutingTableBean" property="routingTableId"/>" />
																						</td>
																						<td align="center" class="tblrows">
																							<%=((pageNo-1)*pageSize)+count%>
																						</td>
																						<td align="left" class="tblrows">
																							<a href="<%=basePath%>/initViewIMSIBasedRouting.do?routingTableId=<bean:write name="imsiRoutingTableBean" property="routingTableId"/>">
																								<bean:write name="imsiRoutingTableBean" property="routingTableName" />
																							</a>
																						</td>
																						<td align="center" class="tblrows">
																							<a href="<%=basePath%>/initUpdateIMSIBasedRouting.do?routingTableId=<bean:write name="imsiRoutingTableBean" property="routingTableId"/>">
																								<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
																							</a>
																						</td>
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
															<td class="btns-td" valign="middle" style="padding-top: 3px;"><html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /> </td>
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
										</table>
									</td>
								</tr>
							</table>
						</html:form>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
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

<script language="javascript">
  
function navigate(pageNo){
	 document.imsiBasedRoutingTableForm.action="initSearchIMSIBasedRoutingTable.do?pageNo="+pageNo;
	 document.imsiBasedRoutingTableForm.submit();  
}
  
</script>