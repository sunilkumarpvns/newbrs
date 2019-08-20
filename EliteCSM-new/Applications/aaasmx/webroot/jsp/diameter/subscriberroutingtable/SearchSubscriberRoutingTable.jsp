<%@page import="com.elitecore.elitesm.web.diameter.subscriberroutingtable.MSISDNMappingData"%>
<%@page import="com.elitecore.elitesm.web.diameter.subscriberroutingtable.IMSIMappingData"%>
<%@page import="com.elitecore.elitesm.web.diameter.subscriberroutingtable.form.SubscriberRoutingTableForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.certificate.forms.ServerCertificateForm"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.*"%>

<%	String basePath = request.getContextPath();
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	int iIndexImsi =0;
	SubscriberRoutingTableForm subscriberRoutingTableForm = (SubscriberRoutingTableForm)request.getAttribute("subscriberRoutingTableForm");
		
	boolean searchRange = subscriberRoutingTableForm.isSearchRange();
	boolean searchImsi = subscriberRoutingTableForm.isSearchImsi();
	boolean searchMsisdn = subscriberRoutingTableForm.isSearchMsisdn();
	
	/* IMSI Based Routing Table */
	int imsiCount=1;
	List lstImsiBasedRoutingTable = subscriberRoutingTableForm.getListIMSIBasedRoutingTable();

	long imsiPageNo = subscriberRoutingTableForm.getImsiPageNumber();
	long imsiTotalPages = subscriberRoutingTableForm.getImsiTotalPages();
	long imsiTotalRecord = subscriberRoutingTableForm.getImsiTotalRecords();
	
	String strImsiPageNumber = String.valueOf(imsiPageNo);     
	String strImsiTotalPages = String.valueOf(imsiTotalPages);
	String strImsiTotalRecords = String.valueOf(imsiTotalRecord);
	
	/* MSISDN Based Routing Table */
	List lstMSISDNBasedRoutingTable = subscriberRoutingTableForm.getListMSISDNBasedRoutingTable();
    
    long msisdnPageNo = subscriberRoutingTableForm.getMsisdnPageNumber();
	long msisdnTotalPages = subscriberRoutingTableForm.getMsisdnTotalPages();
	long msisdnTotalRecord = subscriberRoutingTableForm.getMsisdnTotalRecords();
	String strMsisdnPageNumber = String.valueOf(msisdnPageNo);     
	String strMsisdnTotalPages = String.valueOf(msisdnTotalPages);
	String strMsisdnTotalRecords = String.valueOf(msisdnTotalRecord);
	int msisdnCount=1;
	int iIndexMsisdn =0;
	
	/*Search IMSI Mapping */
	long imsiMappingPageNo = subscriberRoutingTableForm.getImsiMappingPageNumber();
    long imsiMappingTotalPages = subscriberRoutingTableForm.getImsiMappingTotalPages();
    long imsiMappingTotalRecords = subscriberRoutingTableForm.getImsiMappingTotalRecords();
	int mappingCount=1;
	
	String strImsiMappingPageNumber = String.valueOf(imsiMappingPageNo);     
    String strImsiMappingTotalPages = String.valueOf(imsiMappingTotalPages);
    String strImsiMappingTotalRecords = String.valueOf(imsiMappingTotalRecords);
    
    List<IMSIMappingData> imsiMappingData = subscriberRoutingTableForm.getImsiMappingDataList();
   
    String strSubscriberDetails=subscriberRoutingTableForm.getSubscriberDetails() != null ? subscriberRoutingTableForm.getSubscriberDetails() : "";
    String pageNavigationLink="searchSubscriber.do?searchMode=IMSI&subscriber="+strSubscriberDetails+"&action=list&pageNo=";
    
    /* Search MSISDN Mapping*/
    long msisdnMappingPageNo = subscriberRoutingTableForm.getMsisdnMappingPageNumber();
    long msisdnMappingTotalPages = subscriberRoutingTableForm.getMsisdnMappingTotalPages();
    long msisdnMappingTotalRecords = subscriberRoutingTableForm.getMsisdnMappingTotalRecords();
	int msisdnMappingCount=1;
	
	String stMsisdnMappingPageNumber = String.valueOf(msisdnMappingPageNo);     
    String strMsisdnMappingTotalPages = String.valueOf(msisdnMappingTotalPages);
    String strMsisdnMappingTotalRecords = String.valueOf(msisdnMappingTotalRecords);
    
    List<MSISDNMappingData> msisdnMappingData = subscriberRoutingTableForm.getMsisdnMappingDataList();
    String msisdnPageNavigationLink="searchSubscriber.do?searchMode=MSISDN&subscriber="+strSubscriberDetails+"&action=list&pageNo=";
  
%>

<script>
function removeMsisdnData(){
	var selectArray = document.getElementsByName('msisdnselect');
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
				document.forms[0].action='<%=basePath%>/searchSubscriberRoutingTable.do?method=delete&deleteParam=MSISDN';
				document.forms[0].submit();
	  		}
		}
	}else{
		alert("No Records Found For Delete Operation! ");
	}
}	

function removeImsiData(){
	var selectArray = document.getElementsByName('imsiselect');
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
				document.forms[0].action='<%=basePath%>/searchSubscriberRoutingTable.do?method=delete&deleteParam=IMSI';
				document.forms[0].submit();
	  		}
		}
	}else{
		alert("No Records Found For Delete Operation! ");
	}
}	

function  checkImsiAll(){
	var arrayCheck = document.getElementsByName('imsiselect');
	if( document.forms[0].toggleImsiAll.checked == true) {
		for (i = 0; i < arrayCheck.length;i++)
			arrayCheck[i].checked = true ;
	} else if (document.forms[0].toggleImsiAll.checked == false){
		for (i = 0; i < arrayCheck.length; i++)
			arrayCheck[i].checked = false ;
	}
}

function  checkMsisdnAll(){
	var arrayCheck = document.getElementsByName('msisdnselect');
	if( document.forms[0].msisdnToggleAll.checked == true) {
		for (i = 0; i < arrayCheck.length;i++)
			arrayCheck[i].checked = true ;
	} else if (document.forms[0].msisdnToggleAll.checked == false){
		for (i = 0; i < arrayCheck.length; i++)
			arrayCheck[i].checked = false ;
	}
}

function navigateMsisdn(pageNo){
	 document.msisdnBasedRoutingTableForm.action="searchMSISDNBasedRoutingTable.do?pageNo="+pageNo;
	 document.msisdnBasedRoutingTableForm.submit();  
}
 
function navigateImsi(pageNo){
	 document.imsiBasedRoutingTableForm.action="initSearchIMSIBasedRoutingTable.do?pageNo="+pageNo;
	 document.imsiBasedRoutingTableForm.submit();  
}
 
function validateSearchIMSI(){
	var name= $('#subscriberDetails').val();
	if(isEmpty(name)){
		alert('Please enter subscriber');
		return false;
	}else{
		 location.href='<%=basePath%>/searchSubscriber.do?searchMode=IMSI&subscriber='+name;
	}
}

function validateSearchMSISDN(){
	var name= $('#subscriberDetails').val();
	if(isEmpty(name)){
		alert('Please enter subscriber');
		return false;
	}else{
		 location.href='<%=basePath%>/searchSubscriber.do?searchMode=MSISDN&subscriber='+name;
	}
}

function resetSearch(){
	document.forms[0].action='<%=basePath%>/searchSubscriberRoutingTable.do?method=initSearch';
	document.forms[0].submit();
}

function createImsiBasedRoutingTable(){
	document.forms[0].action='<%=basePath%>/searchSubscriberRoutingTable.do?method=initCreate&subscriberMode=IMSI';
	document.forms[0].submit();
}

function createMsisdnBasedRoutingTable(){
	document.forms[0].action='<%=basePath%>/searchSubscriberRoutingTable.do?method=initCreate&subscriberMode=MSISDN';
	document.forms[0].submit();
}

function customValidate(form){
	var name= $('#subscriberDetails').val();
	if(isEmpty(name)){
		alert('Please enter subscriber');
		return false;
	}else{
		 location.href='<%=basePath%>/searchSubscriber.do?searchMode=IMSI&subscriber='+name;
		 return false;
	}
}
</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
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
									<html:form action="/searchSubscriberRoutingTable.do?method=initSearch" onsubmit="return customValidate(this);">
										<html:hidden property="action" styleId="action" />
										<table width="100%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="left" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%" border="0">
														<tr>
															<td class="table-header" colspan="100%">
																<bean:message bundle="diameterResources" key="subscriberroutingtable.title" />
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														<tr>
															<td align="left" class="btns-td" valign="top" width="20%">
																<bean:message bundle="diameterResources" key="subscriberroutingtable.searchsubscriber" /> 
																<ec:elitehelp headerBundle="diameterResources" text="subscriberroutingtable.searchsubscriber" header="subscriberroutingtable.searchsubscriber"/>
															</td>
															<td align="left" class="labeltext" valign="top" width="80%">
																<html:text styleId="subscriberDetails" name="subscriberRoutingTableForm" property="subscriberDetails" tabindex="1" size="30" style="width:250px" />
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														<tr>
															<td align="left" class="btns-td" valign="top" width="20%">
															</td>
															<td align="left" class="labeltext" valign="top" width="80%">
																<input type="button" name="SearchIMSI" width="5%"  onclick="validateSearchIMSI()" value=" Search IMSI " class="light-btn" tabindex="5" /> &nbsp;
																<input type="button" name="SearchMSISDN" width="5%"  onclick="validateSearchMSISDN()" value=" Search MSISDN " class="light-btn" tabindex="5" /> &nbsp;
																<input type="button" name="reset" width="5%"  onclick="resetSearch()" value=" Reset " class="light-btn" tabindex="5" /> 
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														<% if( searchRange == false) { %>
														<tr>
															<td align="left" class="labeltext" colspan="5" valign="top">
																<table cellSpacing="0" cellPadding="0" width="100%" border="0">
																	<tr>
																		<td class="table-header" width="50%">
																			<bean:message bundle="diameterResources" key="imsibasedroutingtable.list" />
																		</td>
																		<td align="right" class="blue-text" valign="middle" width="50%">
																			&nbsp;
																		</td>
																	</tr>
																	<tr>
																		<td class="btns-td" valign="middle" style="padding-top: 5px;">
																			<html:button property="c_btnDelete" onclick="removeImsiData()" tabindex="5" value="   Delete   " styleClass="light-btn" /> 
																			<html:button property="c_btnDelete" onclick="createImsiBasedRoutingTable()" tabindex="5" value="   Create   " styleClass="light-btn" /> 
																		</td>
																		<td class="btns-td" align="right">
																			&nbsp;
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
																						<input type="checkbox" name="toggleImsiAll" value="checkbox" onclick="checkImsiAll()" />
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
																				<logic:iterate id="imsiRoutingTableBean" name="subscriberRoutingTableForm" property="listIMSIBasedRoutingTable" type="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData">
																					<tr>
																						<td align="center" class="tblfirstcol">
																							<input type="checkbox" name="imsiselect" value="<bean:write name="imsiRoutingTableBean" property="routingTableId"/>" />
																						</td>
																						<td align="center" class="tblrows">
																							<%=imsiCount%>
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
																					<% imsiCount=imsiCount+1; %>
																					<% iIndexImsi += 1; %>
																				</logic:iterate>

																	<%	}else{
																		%>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="8">
																			<bean:message bundle="diameterResources" key="diameterpeer.norecordsmsg" />
																		</td>
																	</tr>
																	<%	}%>
																</table>
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" style="padding-top: 3px;">
																<html:button property="c_btnDelete" onclick="removeImsiData()" value="   Delete   " styleClass="light-btn" /> 
																<html:button property="c_btnDelete" onclick="createImsiBasedRoutingTable()" tabindex="5" value="   Create   " styleClass="light-btn" /> 
															</td>
															<td class="btns-td" align="right">
																&nbsp;
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="100%" border="0">
														<tr>
															<td class="table-header" width="50%">
																<bean:message bundle="diameterResources" key="msisdnbasedroutingtable.list" />
															</td>
															<td align="right" class="blue-text" valign="middle" width="50%">
																		&nbsp;
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" style="padding-top: 5px;">
																<html:button property="c_btnDelete" onclick="removeMsisdnData()" tabindex="5" value="   Delete   " styleClass="light-btn" /> 
																<html:button property="c_btnDelete" onclick="createMsisdnBasedRoutingTable()" tabindex="5" value="   Create   " styleClass="light-btn" /> 
															</td>
															<td class="btns-td" align="right">
																&nbsp;
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
																			<input type="checkbox" name="msisdnToggleAll" value="checkbox" onclick="checkMsisdnAll()" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="3%">
																			<bean:message bundle="diameterResources" key="diameterpeer.serialnumber" />
																		</td>
																		<td align="left" class="tblheader" valign="top" width="88%">
																			<bean:message bundle="diameterResources" key="msisdnbasedroutingtable.tablename" />
																		</td>
																		<td align="center" class="tblheader" valign="top" width="7%">
																			<bean:message bundle="diameterResources" key="diameterpeer.edit" />
																		</td>
																	</tr>
																	<%	if(lstMSISDNBasedRoutingTable!=null && lstMSISDNBasedRoutingTable.size()>0){%>
																		<logic:iterate id="msisdnRoutingTableBean" name="subscriberRoutingTableForm" property="listMSISDNBasedRoutingTable" type="com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData">
																			<tr>
																				<td align="center" class="tblfirstcol">
																					<input type="checkbox" name="msisdnselect" value="<bean:write name="msisdnRoutingTableBean" property="routingTableId"/>" />
																				</td>
																				<td align="center" class="tblrows">
																					<%=msisdnCount%>
																				</td>
																				<td align="left" class="tblrows">
																					<a href="<%=basePath%>/viewMSISDNBasedRouting.do?routingTableId=<bean:write name="msisdnRoutingTableBean" property="routingTableId"/>">
																						<bean:write name="msisdnRoutingTableBean" property="routingTableName" />
																					</a>
																				</td>
																				<td align="center" class="tblrows">
																					<a href="<%=basePath%>/updateMSISDNBasedRouting.do?routingTableId=<bean:write name="msisdnRoutingTableBean" property="routingTableId"/>">
																						<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
																					</a>
																				</td>
																			</tr>
																			<% msisdnCount=msisdnCount+1; %>
																			<% iIndexMsisdn += 1; %>
																		</logic:iterate>
																	<%	}else{ %>
																	<tr>
																		<td align="center" class="tblfirstcol" colspan="8">
																			<bean:message bundle="diameterResources" key="diameterpeer.norecordsmsg" />
																		</td>
																	</tr>
																	<%	}%>
																</table>
															</td>
														</tr>
														<tr>
															<td class="btns-td" valign="middle" style="padding-top: 3px;">
																<html:button property="c_btnDelete" onclick="removeMsisdnData()" value="   Delete   " styleClass="light-btn" /> 
																<html:button property="c_btnDelete" onclick="createMsisdnBasedRoutingTable()" tabindex="5" value="   Create   " styleClass="light-btn" /> 
															</td>
															<td class="btns-td" align="right">
																&nbsp;
															</td>
														</tr>
														<tr height="2">
															<td></td>
														</tr>
													</table>
												</td>
											</tr>
											<%} %>
											<% if( searchImsi ){ %>
											<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%" border="0">
														<tr>
															<td class="table-header" width="50%">
																<bean:message bundle="diameterResources" key="imsibasedroutingtable.imsibasedsubscriberdetails" />
															</td>
															<td align="right" class="blue-text" valign="middle" width="50%">
																<% if(imsiMappingTotalRecords == 0) { %> <% }else if(imsiMappingPageNo == imsiMappingTotalPages+1) { %>
																[<%=((imsiMappingPageNo-1)*pageSize)+1%>-<%=imsiMappingTotalRecords%>] of <%= imsiMappingTotalRecords %>
																<% } else if(imsiMappingPageNo == 1) { %> [<%=(imsiMappingPageNo-1)*pageSize+1%>-<%=(imsiMappingPageNo-1)*pageSize+pageSize%>]
																of <%= imsiMappingTotalRecords %> <% } else { %> [<%=((imsiMappingPageNo-1)*pageSize)+1%>-<%=((imsiMappingPageNo-1)*pageSize)+pageSize%>]
																of <%= imsiMappingTotalRecords %> <% } %>
															</td>
														</tr>
														<tr>
															<td>
																&nbsp;
															</td>
															<td align="right">
																<% if(imsiMappingTotalPages >= 1) { %> <% if(imsiMappingPageNo == 1){ %> <a
																href="<%=pageNavigationLink+(imsiMappingPageNo+1)%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingTotalPages+1)%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(imsiMappingPageNo>1 && imsiMappingPageNo!=imsiMappingTotalPages+1) {%> <a
																href="<%=pageNavigationLink+(1L)%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingPageNo-1)%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingPageNo+1)%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingTotalPages+1)%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
			
																<% } %> <% if(imsiMappingPageNo == imsiMappingTotalPages+1) { %> <a
																href="<%=pageNavigationLink+(1L)%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingPageNo-1)%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
																<% } %> <% } %>
															</td>
														</tr>
														<tr>
															 <td class="btns-td" valign="top" colspan="2">
																	<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable">
																		<tr>
																			<td align="center" class="tblheader" valign="top" width="5%">Sr. No.</td>
																			<td align="left" class="tblheader" valign="top" width="19%">IMSI Range</td>
																			<td align="left" class="tblheader" valign="top" width="19%">Primary Peer</td>
																			<td align="left" class="tblheader" valign="top" width="19%">Secondary Peer</td>
																			<td align="left" class="tblheader" valign="top" width="19%">Tag</td>
																			<td align="left" class="tblheader" valign="top" width="19%">Routing Table</td>
																		</tr>
				
																		<%if(imsiMappingData != null && imsiMappingData.size() >0){ %>
				
																		<logic:iterate id="obj" name="subscriberRoutingTableForm" property="imsiMappingDataList" type="com.elitecore.elitesm.web.diameter.subscriberroutingtable.IMSIMappingData">
																			<tr>
																				<td align="center" class="tblfirstcol"><%=((imsiMappingPageNo-1)*pageSize)+mappingCount%></td>
																				<td align="left" class="tblrows">
																					<bean:write name="obj" property="imsiRange" />&nbsp;
																				</td>
																				<td align="left" class="tblrows">
																					<bean:write name="obj" property="primaryPeerName" />&nbsp;
																				</td>
																				<td align="left" class="tblrows">
																					<bean:write name="obj" property="secondaryPeerName" />&nbsp;
																				</td>
																				<td align="left" class="tblrows">
																					<bean:write name="obj" property="tag" />&nbsp;
																				</td>
																				<td align="left" class="tblrows">
																					<a href="javascript:void(0)" onclick="viewImsi(<bean:write name="obj" property="routingTableId" />);">
																						<bean:write name="obj" property="routingTableName" />
																					</a>
																					&nbsp;
																				</td>
																			</tr>
																			<%mappingCount ++ ;%>
																		</logic:iterate>
																		<%}else{ %>
																		<tr>
																			<td align="center" class="tblfirstcol" colspan="8">
																				No Records Found.
																			</td>
																		</tr>
																		<%	}%>
																	</table>
																</td>
															</tr> 
															<tr>
															<td>
																&nbsp;
															</td>
															<td align="right">
																<% if(imsiMappingTotalPages >= 1) { %> <% if(imsiMappingPageNo == 1){ %> <a
																href="<%=pageNavigationLink+(imsiMappingPageNo+1)%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingTotalPages+1)%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(imsiMappingPageNo>1 && imsiMappingPageNo!=imsiMappingTotalPages+1) {%> <a
																href="<%=pageNavigationLink+(1L)%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingPageNo-1)%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingPageNo+1)%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingTotalPages+1)%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
			
																<% } %> <% if(imsiMappingPageNo == imsiMappingTotalPages+1) { %> <a
																href="<%=pageNavigationLink+(1L)%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="<%=pageNavigationLink+(imsiMappingPageNo-1)%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
																<% } %> <% } %>
															</td>
														</tr>
														</table>
												</td>
											</tr>
											<%} else if( searchMsisdn ){%>
												<tr>
												<td align="left" class="labeltext" colspan="5" valign="top">
													<table cellSpacing="0" cellPadding="0" width="99%" border="0">
														<tr>
															<td class="table-header" width="50%">
																<bean:message bundle="diameterResources" key="msisdnbasedroutingtable.msisdnbasedsubscriberdetails" />
															</td>
															<td align="right" class="blue-text" valign="middle" width="50%">
																<% if(msisdnMappingTotalRecords == 0) { %> <% }else if(msisdnMappingPageNo == msisdnMappingTotalPages+1) { %>
																[<%=((msisdnMappingPageNo-1)*pageSize)+1%>-<%=msisdnMappingTotalRecords%>] of <%= msisdnMappingTotalRecords %>
																<% } else if(msisdnMappingPageNo == 1) { %> [<%=(msisdnMappingPageNo-1)*pageSize+1%>-<%=(msisdnMappingPageNo-1)*pageSize+pageSize%>]
																of <%= msisdnMappingTotalRecords %> <% } else { %> [<%=((msisdnMappingPageNo-1)*pageSize)+1%>-<%=((msisdnMappingPageNo-1)*pageSize)+pageSize%>]
																of <%= msisdnMappingTotalRecords %> <% } %>
															</td>
														</tr>
														<tr>
															<td>
																&nbsp;
															</td>
															<td align="right">
																<% if(msisdnMappingTotalPages >= 1) { %> <% if(msisdnMappingPageNo == 1){ %> <a
																href="<%=msisdnPageNavigationLink+(msisdnMappingPageNo+1)%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingTotalPages+1)%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(msisdnMappingPageNo>1 && msisdnMappingPageNo!=msisdnMappingTotalPages+1) {%> <a
																href="<%=msisdnPageNavigationLink+(1L)%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingPageNo-1)%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingPageNo+1)%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingTotalPages+1)%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
			
																<% } %> <% if(msisdnMappingPageNo == msisdnMappingTotalPages+1) { %> <a
																href="<%=msisdnPageNavigationLink+(1L)%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingPageNo-1)%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
																<% } %> <% } %>
															</td>
														</tr>
														<tr>
															 <td class="btns-td" valign="top" colspan="2">
																	<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable">
																		<tr>
																			<td align="center" class="tblheader" valign="top" width="5%">Sr. No.</td>
																			<td align="left" class="tblheader" valign="top" width="19%">MSISDN Range</td>
																			<td align="left" class="tblheader" valign="top" width="19%">Primary Peer</td>
																			<td align="left" class="tblheader" valign="top" width="19%">Secondary Peer</td>
																			<td align="left" class="tblheader" valign="top" width="19%">Tag</td>
																			<td align="left" class="tblheader" valign="top" width="19%">Routing Table</td>
																		</tr>
				
																		<%if(msisdnMappingData != null && msisdnMappingData.size() >0){ %>
				
																		<logic:iterate id="obj" name="subscriberRoutingTableForm" property="msisdnMappingDataList" type="com.elitecore.elitesm.web.diameter.subscriberroutingtable.MSISDNMappingData">
																			<tr>
																				<td align="center" class="tblfirstcol"><%=((msisdnMappingPageNo-1)*pageSize)+msisdnMappingCount%></td>
																				<td align="left" class="tblrows">
																					<bean:write name="obj" property="msisdnRange" />&nbsp;
																				</td>
																				<td align="left" class="tblrows">
																					<bean:write name="obj" property="primaryPeerName" />&nbsp;
																				</td>
																				<td align="left" class="tblrows">
																					<bean:write name="obj" property="secondaryPeerName" />&nbsp;
																				</td>
																				<td align="left" class="tblrows">
																					<bean:write name="obj" property="tag" />&nbsp;
																				</td>
																				<td align="left" class="tblrows">
																					<a href="javascript:void(0)" onclick="viewMsisdn(<bean:write name="obj" property="routingTableId" />);">
																						<bean:write name="obj" property="routingTableName" />
																					</a>
																					&nbsp;
																				</td>
																			</tr>
																			<%msisdnMappingCount++ ;%>
																		</logic:iterate>
																		<%}else{ %>
																		<tr>
																			<td align="center" class="tblfirstcol" colspan="8">
																				No Records Found.
																			</td>
																		</tr>
																		<%	}%>
																	</table>
																</td>
															</tr> 
															<tr>
															<td>
																&nbsp;
															</td>
															<td align="right">
																<% if(msisdnMappingTotalPages >= 1) { %> <% if(msisdnMappingPageNo == 1){ %> <a
																href="<%=msisdnPageNavigationLink+(msisdnMappingPageNo+1)%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingTotalPages+1)%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
																<% } %> <% if(msisdnMappingPageNo>1 && msisdnMappingPageNo!=msisdnMappingTotalPages+1) {%> <a
																href="<%=msisdnPageNavigationLink+(1L)%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingPageNo-1)%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingPageNo+1)%>"><img
																	src="<%=basePath%>/images/next.jpg" name="Image61"
																	onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Next" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingTotalPages+1)%>"><img
																	src="<%=basePath%>/images/last.jpg" name="Image612"
																	onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Last" border="0"></a>
			
																<% } %> <% if(msisdnMappingPageNo == msisdnMappingTotalPages+1) { %> <a
																href="<%=msisdnPageNavigationLink+(1L)%>"><img
																	src="<%=basePath%>/images/first.jpg" name="Image511"
																	onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="First" border="0"></a>
																<a href="<%=msisdnPageNavigationLink+(msisdnMappingPageNo-1)%>"><img
																	src="<%=basePath%>/images/previous.jpg" name="Image5"
																	onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
																	onmouseout="MM_swapImgRestore()" alt="Previous" border="0"></a>
																<% } %> <% } %>
															</td>
														</tr>
														</table>
												</td>
											</tr>
											<%} %>
													</table>
												</td>
											</tr>
										</table>
									</html:form>
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

<script>
	setTitle('<bean:message bundle="diameterResources" key="subscriberroutingtable.title"/>');
</script>
