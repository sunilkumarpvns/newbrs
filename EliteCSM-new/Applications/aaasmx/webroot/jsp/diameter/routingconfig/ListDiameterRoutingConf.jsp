<%@page import="com.elitecore.commons.base.Collectionz"%>
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
<%@ page import="com.elitecore.elitesm.web.tableorder.TableOrderData"%>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions"%>
<%@ page import="com.elitecore.diameterapi.diameter.common.util.constant.ResultCode" %>
<%@ page import="com.elitecore.diameterapi.core.stack.constant.OverloadAction" %>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>

<%
String basePath = request.getContextPath();
SearchDiameterRoutingConfForm searchDiameterRoutingConfForm = (SearchDiameterRoutingConfForm) request.getAttribute("searchDiameterRoutingConfForm");
long pageNo = searchDiameterRoutingConfForm.getPageNumber();
long totalPages = searchDiameterRoutingConfForm.getTotalPages();
long totalRecord = searchDiameterRoutingConfForm.getTotalRecords();
String strPageNumber = String.valueOf(pageNo);
String strTotalPages = String.valueOf(totalPages);
String strTotalRecords = String.valueOf(totalRecord);
int iIndex = 0;
List lstDiameterRoutingConf = searchDiameterRoutingConfForm.getListDiameterRoutingConf();
String strRoutingConfName = searchDiameterRoutingConfForm.getRoutingConfName();
String routingTableId = String.valueOf(request.getAttribute("routingTableId"));
int count = 1;
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script src="<%=request.getContextPath()%>/jquery/OrderTable/jquery.tablednd.js"></script>
<LINK REL="stylesheet" TYPE="text/css" href="<%=request.getContextPath()%>/css/tablednd.css">
<script language="javascript">

setTitle('<bean:message bundle="diameterResources" key="routingconf.title"/>');

var resultCodeList = [];
var scriptInstanceList = [];

<%for(ResultCode resultCode:ResultCode.VALUES){%>
	resultCodeList.push({'value':'<%=resultCode.code%>','label':'[<%=resultCode.code%>] <%=resultCode.name()%>'});
<%}%>

<% 
	if( Collectionz.isNullOrEmpty(searchDiameterRoutingConfForm.getScriptDataList()) == false ){
		for( ScriptInstanceData scriptInstData : searchDiameterRoutingConfForm.getScriptDataList()){ %>
			scriptInstanceList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
		<%}
	}
%>

var isValidName;
$(document).ready(function(){
	
	setSuggestionForResultCode(resultCodeList);
	
	setSuggestionForScript(scriptInstanceList, "scriptInstAutocomplete");
	/* Set Drag and Drop functionality on table */
	setTableDragAndDrop("listTable");
	/* Save order of Table */
	$(".saveorder").click(function(){
		saveOrder("routingConfigIdS", "<%=TableOrderData.DIAMETERROUTING%>", "diameterRoutingTable.do?actionType=tablewiserouting&routingTableId="+$("#routingTableId").val());
	});
	
	if($("#ddlOverloadAction").val() == '<%= OverloadAction.REJECT.val %>'){
		document.getElementById("resultCode").disabled = false;
	}else{
		document.getElementById("resultCode").disabled = true;
	}
	$("#ddlOverloadAction").change(function(){
		if($(this).val() == '<%=OverloadAction.DROP.val%>'){
			document.getElementById("resultCode").disabled = true;
		}else{
			document.getElementById("resultCode").disabled = false;
			document.getElementById("resultCode").value = "3004";
			setSuggestionForResultCode(resultCodeList);
			
		}
	});
});

function setSuggestionForResultCode(resultCodeArray) {
	 $( ".autoSuggestCode").autocomplete({	
			source:resultCodeArray
	});
};

function  checkAll(){
	var arrayCheck = document.getElementsByName('select');
		if( document.forms[1].toggleAll.checked == true) {
			for (i = 0; i < arrayCheck.length;i++)
				arrayCheck[i].checked = true ;
		} else if (document.forms[1].toggleAll.checked == false){
			for (i = 0; i < arrayCheck.length; i++)
				arrayCheck[i].checked = false ;
		}
	}

function showall(){  
		var path = '<%=basePath%>/miscDiameterRoutingConf.do?action=showall';
		window.open(path, 'DiameterRoutingTable',
				'resizable=yes,scrollbars=yes');
	}

	function removeData() {
		document.forms[1].action.value = 'delete';
		var selectArray = document.getElementsByName('select');
		if (selectArray.length > 0) {
			var b = true;
			for (i = 0; i < selectArray.length; i++) {
				if (selectArray[i].checked == false) {
					b = false;
				} else {
					b = true;
					break;
				}
			}

			if (b == false) {
				alert("Selection Required To Perform Delete Operation.");
			} else {
				var r = confirm("This will delete the selected items. Do you want to continue ?");

				if (r == true) {
					document.forms[1].submit();
				}
			}
		} else {
			alert("No Records Found For Delete Operation! ");
		}
	}
	
	function validateform(){
		if(validateRoutingTableName() == false){
			return false;				
		}
		
		var overloadAction = $("#ddlOverloadAction").val();
		if(overloadAction == '<%= OverloadAction.REJECT.val %>'){
	  		if(isNull(document.forms[0].resultCode.value)){
	  			alert('Enter Result Code For Overload Action type Reject');
	  			document.forms[0].resultCode.focus();
	  			return false;
	  		}
	  		if(overloadAction == '<%= OverloadAction.REJECT.val %>'){
				if(document.forms[0].resultCode.value < 1000 || document.forms[0].resultCode.value > 5999 ){
	  				alert('Invalid Result Code Configured For Reject Overload Action');
	  				document.forms[0].resultCode.focus();
	  				return false;
				}
	  		}
	  		
	  	}
		return true;
		
	}

	/**
	This method validates the name of Diameter Routing Table. It makes an ajax call and returns true if the name is 
	unique else it will return false.
	*/
	function verifyName() { 
		var searchName = document.getElementById("routingTableName").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_ROUTING_TABLE%>',searchName,'update','<%=routingTableId %>','verifyNameDiv');
	}
	
	/*
	This function validates that the name of Diameter Routing Table is not blank/empty, it contains only specific
	characters in its name and it is unique or not. If everyting is valid it will return true and submit your form
	else false will be returned with proper error message and form will not be submitted.
	*/
	function validateRoutingTableName(){
		var isValid = true;
		if(isEmptyById('routingTableName')){
			alert("Routing Table name must be specified");
			isValid = false;
		} else if(validateName($('#routingTableName').val()) == false){
			alert("Routing Table name should have following characters. A-Z, a-z, 0-9, _ and - ");
			isValid = false;
		} else if (isValidName == false){
			alert('Routing Table name already exists');
			isValid = false;
		}
		
		if(isValid == false){
			$('#routingTableName').focus();
		}
		return isValid;
	}
	
	/*
	This method validates that the name should contain only some specific characters.
	It returns true if name contain those specific chracters else would return false.
	*/
	function validateName(val){
		var test1 = /(^[A-Za-z0-9-_]*$)/;
		var regexp =new RegExp(test1);
		if(regexp.test(val)){
			return true;
		}
		return false;
	}
</script>
<table cellpadding="0" cellspacing="0" border="0"
	width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH)%>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="100%" class="box">
					<html:form action="/updateOverloadConfig" styleId="mainform">
					<html:hidden name="searchDiameterRoutingConfForm"
								styleId="action" property="action" />
					<input type="hidden" name="routingTableId" value='<%=routingTableId %>' >
					<table width="100%" border="0" cellSpacing="0" cellPadding="0">
								<tr>
									<td align="left">
										<table cellSpacing="0" cellPadding="0" width="100%" border="0">
											<tr>
												<td class="table-header" width="50%"><bean:message
														bundle="diameterResources"
														key="routingconf.overloadconfig" /></td></tr>
												<tr><td><table>
												
													<tr>
															<td align="left" class="btns-td" valign="top" width="5%">
																<bean:message bundle="diameterResources"
																	key="routingconf.tablename" /> 
																	<ec:elitehelp headerBundle="diameterResources" text="routingconf.table.name" 
																	header="routingconf.tablename"/>
															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><html:text styleId="routingTableName"
																	property="routingTableName" tabindex="1" size="30"
																	style="width:250px" onkeyup="verifyName();"/> <font color="#FF0000"> *</font>
																<div id="verifyNameDiv" class="labeltext"></div> </td>
															</td>
														</tr>
																										
														<tr>
															<td align="left" class="btns-td" valign="top" width="5%">
																<bean:message bundle="diameterResources"
																	key="routingconf.overloadaction" /> 
																	<ec:elitehelp headerBundle="diameterResources" text="routingconf.overloadaction" 
																	header="routingconf.overloadaction"/>
															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><html:select styleId="ddlOverloadAction"
																	property="overloadAction" tabindex="1" name="searchDiameterRoutingConfForm"
																	style="width:250px" >
																	<% for(OverloadAction overloadAction : OverloadAction.values()) { %>
																	<html:option value="<%=overloadAction.val%>"><%=overloadAction.val%></html:option>
																	<%} %>
																	</html:select> 
																 </td>
														</tr>
														
														<tr id="resultCodeRow">
														
															<td align="left" class="btns-td" valign="top" width="5%">
																<bean:message bundle="diameterResources"
																	key="routingconf.resultcode" /> 
																	<ec:elitehelp headerBundle="diameterResources" text="routingconf.resultcode" 
																	header="routingconf.resultcode"/>
															</td>
															<td align="left" class="labeltext" valign="top"
																width="22%"><html:text name="searchDiameterRoutingConfForm" styleId="resultCode"
																	property="resultCode"  tabindex="1" size="30"
																	maxlength="128" styleClass="autoSuggestCode"
																	style="width:250px" /> <font color="#FF0000"> *</font>
																 </td>
														</tr>
														
														<tr>
															<td align="left" class="btns-td" valign="top" width="5%">
																<bean:message bundle="diameterResources" key="routingconf.routingscript" /> 
																<ec:elitehelp headerBundle="diameterResources" text="routingconf.routingscript" header="routingconf.routingscript"/>
															</td>
															<td align="left" class="labeltext" valign="top" width="22%">
																<html:text styleId="routingScript" name="searchDiameterRoutingConfForm" property="routingScript" styleClass="scriptInstAutocomplete" style="width:250px" />
															</td>
														</tr>
														
														<tr><td></td><td class="btns-td"  valign="middle"><input type="submit" name="Update" value=" Update "  onclick="return validateform();"
														   class="light-btn" /> <input type="button" name="Cancel" value=" Cancel "
																		class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchDiameterRoutingConfig.do'" /></td></tr>
												</table></td></tr></table></td>
											</tr></table>

					
					
					</html:form>
					<html:form action="/miscDiameterRoutingConf" styleId="miscForm">
							<html:hidden name="searchDiameterRoutingConfForm"
								styleId="action" property="action" />
							<html:hidden name="searchDiameterRoutingConfForm"
								styleId="pageNumber" property="pageNumber" />
							<html:hidden name="searchDiameterRoutingConfForm"
								styleId="totalPages" property="totalPages"
								value="<%=strTotalPages%>" />
							<html:hidden name="searchDiameterRoutingConfForm"
								styleId="totalRecords" property="totalRecords"
								value="<%=strTotalRecords%>" />
							<html:hidden name="searchDiameterRoutingConfForm"
								styleId="searchBy" property="searchBy" />
							<html:hidden name="searchDiameterRoutingConfForm"
								styleId="searchValue" property="searchValue" />
							<html:hidden styleId="routingTableId" property="routingTableId"
								value="<%=routingTableId%>" />
							<table width="100%" border="0" cellSpacing="0" cellPadding="0">
											</td>

											</tr>
											<tr>
												<td align="left">
													<table cellSpacing="0" cellPadding="0" width="100%"
														border="0">
														<tr>
															<td class="table-header" width="50%"><bean:message
																	bundle="diameterResources"
																	key="routingconf.routingconflist" /></td>

															<td align="right" class="blue-text" valign="middle"
																width="50%"></td>
														</tr>
														<tr>

															<td></td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle"><html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /> <html:button
																	property="c_btnshowall" onclick="showall()"
																	value="   Show All   " styleClass="light-btn" /> <input
																type="button" name="Create" value="   Create   "
																onclick="javascript:location.href='<%=basePath%>/initCreateDiameterRoutingConfig.do?routingTableId=<%=routingTableId%>'"
																class="light-btn"> <logic:notEmpty
																	name="searchDiameterRoutingConfForm"
																	property="listDiameterRoutingConf">
																	<input type="button" name="Create" value=" Save Order "
																		class="light-btn saveorder">
																</logic:notEmpty></td>
															<td class="btns-td" align="right"></td>
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
																		<td align="left" class="tblheader" valign="top"
																			width="3%"><bean:message
																				bundle="diameterResources"
																				key="routingconf.serialnumber" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="diameterResources" key="routingconf.name" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="diameterResources"
																				key="routingconf.realmname" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="diameterResources"
																				key="routingconf.originhost" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="15%"><bean:message
																				bundle="diameterResources"
																				key="routingconf.originrealm" /></td>
																		<td align="left" class="tblheader" valign="top"
																			width="8%"><bean:message
																				bundle="diameterResources"
																				key="routingconf.routingaction" /></td>
																		<td align="center" class="tblheader" valign="top"
																			width="7%"><bean:message
																				bundle="diameterResources"
																				key="diameterpeerprofile.edit" /></td>
																	</tr>
																	<%
																		if (lstDiameterRoutingConf != null
																					&& lstDiameterRoutingConf.size() > 0) {
																	%>
																	<logic:iterate id="diameterRoutingConfBean"
																		name="searchDiameterRoutingConfForm"
																		property="listDiameterRoutingConf"
																		type="com.elitecore.elitesm.datamanager.diameter.routingconf.data.DiameterRoutingConfData">
																		<%
																			DiameterRoutingConfData sData = (DiameterRoutingConfData) lstDiameterRoutingConf
																								.get(iIndex);
																		%>
																		<tr>
																			<td align="center" class="tblfirstcol"><input
																				type="checkbox" name="select"
																				value="<bean:write name="diameterRoutingConfBean" property="routingConfigId"/>" />
																				<input type="hidden" name="routingConfigIdS"
																				value="<bean:write name='diameterRoutingConfBean' property='routingConfigId'/>"
																				id="routingConfigId" /></td>
																			<td align="center" class="tblrows"><%=count%></td>
																			<td align="left" class="tblrows"><a
																				href="<%=request.getContextPath()%>/viewDiameterRoutingConfBasicDetail.do?routingConfigId=<%=diameterRoutingConfBean.getRoutingConfigId()%>"><bean:write
																						name="diameterRoutingConfBean" property="name" /></a></td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterRoutingConfBean" property="realmName" />
																				&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterRoutingConfBean"
																					property="originHost" />&nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterRoutingConfBean"
																					property="originRealm" /> &nbsp;</td>
																			<td align="left" class="tblrows"><bean:write
																					name="diameterRoutingConfBean"
																					property="routingActionName" /> &nbsp;</td>
																			<td align="center" class="tblrows"><a
																				href="initUpdateDiameterRoutingConfig.do?routingConfigId=<bean:write name="diameterRoutingConfBean" property="routingConfigId"/>">
																					<img src="<%=basePath%>/images/edit.jpg" alt="Edit"
																					border="0">
																			</a></td>
																		</tr>
																		<%
																			count = count + 1;
																		%>
																		<%
																			iIndex += 1;
																		%>
																	</logic:iterate>

																	<%
																		} else {
																	%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="100%">
																			<bean:message bundle="diameterResources"
																				key="diameterpeerprofile.norecordsmsg" />
																		</td>
																	</tr>
																	<%
																		}
																	%>

																</table>
															</td>
														</tr>
														<tr>

														</tr>

														<tr>
															<td class="btns-td" valign="middle"><html:button
																	property="c_btnDelete" onclick="removeData()"
																	value="   Delete   " styleClass="light-btn" /> <html:button
																	property="c_btnshowall" onclick="showall()"
																	value="   Show All   " styleClass="light-btn" /> <input
																type="button" name="Create" value="   Create   "
																onclick="javascript:location.href='<%=basePath%>/initCreateDiameterRoutingConfig.do'"
																class="light-btn"> <logic:notEmpty
																	name="searchDiameterRoutingConfForm"
																	property="listDiameterRoutingConf">
																	<input type="button" name="Create" value=" Save Order "
																		class="light-btn saveorder">
																</logic:notEmpty></td>
															<td class="btns-td" align="right"></td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
													</table>
												</td>
											</tr>
										</table> </html:form>
									</td>
								</tr>
								<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
							</table></td>
				</tr>
			</table>