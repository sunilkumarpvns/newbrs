<%@ page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.certificate.forms.ServerCertificateForm"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%
	basePath = request.getContextPath();
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	ServerCertificateForm serverCertificateForm = (ServerCertificateForm) request.getAttribute("serverCertificateForm");

	long pageNo=serverCertificateForm.getPageNumber();
	long totalPages=serverCertificateForm.getTotalPages();
	long totalRecord=serverCertificateForm.getTotalRecords();
	int count=1;
    int iIndex =0;
	
	String strPageNumber=String.valueOf(pageNo);
	String strTotalPages=String.valueOf(totalPages);
	String strTotalRecords=String.valueOf(totalRecord); 
	
	List listServerCertificate = serverCertificateForm.getListServerCertificate();
	String tdHeight="6%";
	String dialogHeight="110";
%>

<script type="text/javascript">
var isValidServerName=false;
var scrollbar;
var dialogHeight="120";
function verifyServerName(){
	var searchName=document.getElementById("serverDuplicateName").value;
	isValidServerName=verifyInstanceName('<%=InstanceTypeConstants.SERVER_CERTIFICATE%>',searchName,'create','','verifyServerNameDiv');
}
$(document).ready(function() {
	if ( $.browser.msie ) {
		dialogHeight="180";
	}
});

$(function() {
	$( "#dialog-form-server" ).dialog({
	    autoOpen: false,
	    height: dialogHeight,
	    width: 390,
	    modal: true
	});
	 $.fn.hasScrollBar = function() {
	        return this.get(0).scrollHeight > this.height();
	 }
	 scrollbar=$('#divContentId').hasScrollBar();
	 if(scrollbar == true){
		 $('#downTd').width('6%');
		 $('#dupTd').width('6%');
	 }else{
		 $('#downTd').width('7%');
		 $('#dupTd').width('7%');
	 }
});
var serverCertificateId="";
function openDuplicateServerCertificate(serverCertId){
	$("#serverDuplicateName").val("");
	$( "#dialog-form-server" ).dialog( "open" );
	$('#serverDuplicateName').focus();
	$("#serverDuplicateName").blur();
	serverCertificateId=serverCertId;
}
function showAllCertificates(){	
	var path = '<%=basePath%>/serverAllCertificates.do?method=showAll'; 
	window.open(path,'ServerCertificate','resizable=yes,scrollbars=yes'); 
}
function navigateCertificate(page){
	document.forms[0].action="serverAllCertificates.do?method=search&pageNo="+page;
	document.serverCertificateForm.submit();  
}
function deleteServerCertificate(){
	var selectArray = document.getElementsByName('selectCertificate');
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
		alert("Selection Required To Perform Delete Operation.");
		}else{
			var r=confirm("This will delete the selected items. Do you want to continue ?");
				if (r==true)
	  			{
					document.forms[0].action='<%=basePath%>/serverAllCertificates.do?method=deleteCertificate';
					document.forms[0].submit();
	  			}
		}
	}
	else{
		alert("No Records Found For Delete Operation! ");
	}
}
function submitServerCertData(){
	if(isEmpty($("#serverDuplicateName").val())){
		alert("Please Enter Name");
		return false;
	}else{
		if(!isValidServerName){
			alert('Enter Valid Server Name');
			$('#serverDuplicateName').focus();
			return false;
		}else{
			var serverName=$("#serverDuplicateName").val().trim();
			location.href='<%=basePath%>/serverAllCertificates.do?method=duplicateServerCertificate&serverCertificateId='+serverCertificateId+'&serverCertificateName='+serverName;
			$("#dialog-form-server").dialog( "close" );
			$("#serverDuplicateName").val("");
		}
	}
}
function cancelServerCertData(){
	$("#dialog-form-server").dialog( "close" );
	$("#serverDuplicateName").val("");
}
function editServerCertificate(serverCertificateId){	  
	  location.href='<%=basePath%>/serverAllCertificates.do?method=initUpdate&serverCertificateId='+serverCertificateId;
}

function downloadServerCertificate(serverCertificateId){	  
	  location.href='<%=basePath%>/serverAllCertificates.do?method=downloadFile&serverCertificateId='+serverCertificateId;
}
function viewServerCertificate(serverCertificateId){
	  location.href='<%=basePath%>/serverAllCertificates.do?method=view&serverCertificateId='+serverCertificateId; 
} 
function  checkAllCertificate(){
 	if( document.serverCertificateForm.toggleAllCertificate.checked == true) {
 		var selectVars = document.getElementsByName('selectCertificate');
	 	for (var i = 0; i < selectVars.length;i++)
			selectVars[i].checked = true ;
    } else if (document.serverCertificateForm.toggleAllCertificate.checked == false){
 		var selectVars = document.getElementsByName('selectCertificate');	    
		for (var i = 0; i < selectVars.length; i++)
			selectVars[i].checked = false ;
	}
}
function validateSearch(){	
	document.serverCertificateForm.submit(); 
}
</script>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td colspan="3">
			<table width="100%" name="c_tblSearchServerCertificate" id="c_tblSearchServerCertificate" align="right" border="0" cellpadding="0" cellspacing="0">
				 	<html:hidden name="serverCertificateForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>"/>
				 	<html:hidden name="serverCertificateForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages %>"/>
				 	<html:hidden name="serverCertificateForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords %>" /> 
					<html:hidden name="serverCertificateForm" styleId="action" property="action" /> 
 					<html:hidden name="serverCertificateForm" styleId="actionName" property="actionName" /> 
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td align="center" class="labeltext"  valign="top">
							<table cellSpacing="0" cellPadding="0" width="95%" border="0" class="certi-table-header">
								<tr>
									<td class="certi-table-header" style="background-color:rgb(217, 230, 246)" colspan="4">
										<table cellSpacing="0" cellPadding="0" width="100%" border="0">
											<tr>
												<td class="certi-table-header-inner" width="70%">
													<bean:message bundle="servermgrResources" key="servermgr.certificate.list" />
												</td>
												<td align="right" class="blue-text" style="padding-right: 8px;">
													<img alt="bottom" id="servercertificateToggleImageElement" tabindex="1" src="<%=basePath%>/images/top-level.jpg" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td colspan="2">
										<div id="servercertificateToggleDivElement" >
										<table width="100%" border="0"  cellpadding="0" cellspacing="0" id="buttonTables" style="padding-left: 5px;padding-right: 5px;padding-bottom: 5px;padding-top: 2px">
											<tr>
												<td>
														<html:button property="c_btnDelete"  tabindex="2" value="   Delete   " styleClass="light-btn" onclick="deleteServerCertificate();"/> 
														<html:button property="c_btnshowall" onclick="showAllCertificates()" tabindex="3" value="   Show All   " styleClass="light-btn" />
														<input type="button" name="Create" value="   Create   " tabindex="4" onclick="javascript:location.href='<%=basePath%>/serverAllCertificates.do?method=initCreate'" class="light-btn">
												</td>
											</tr>
										</table>
										<div style="height: 130px;" id="innerDiv">
										<table width="100%" border="0"  cellpadding="0" cellspacing="0" id="listTable" style="padding-left: 5px;padding-right: 5px;"> 
												<thead>
													<th align="center" class="tblheader" valign="top" id="certChk" style="border-color: #DBE6FF;width: 3%;padding-left: 2px;">
														<input type="checkbox" name="toggleAllCertificate" value="checkbox" onclick="checkAllCertificate()" tabindex="5"/>
													</th>
													<th align="center" class="tblheader" valign="top" id="certSerialNo"  style="border-color: #DBE6FF;width: 4%;">
														<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
													</th>
													<th align="left" class="tblheader" valign="top" id="certName" style="border-color: #DBE6FF;width: 14%;">
														<bean:message bundle="servermgrResources" key="servermgr.certificate.name" />
													</th>
													<th align="left" class="tblheader" valign="top" id="certSubject"  style="border-color: #DBE6FF;width: 24%;">
														<bean:message bundle="servermgrResources" key="servermgr.certificate.subject" />
													</th>
													<th align="left" class="tblheader" valign="top" id="certIssuer"  style="border-color: #DBE6FF;width: 24%;">
														<bean:message bundle="servermgrResources" key="servermgr.certificate.issuer" />
													</th>
													<th align="left" class="tblheader" valign="top" id="certExpiryDate" style="border-color: #DBE6FF;width: 14%;">
														<bean:message bundle="servermgrResources" key="servermgr.certificate.expireddate" />
													</th>
													<th align="left" class="tblheader" valign="top" id="certDownload" style="border-color: #DBE6FF;width: 7%;">
														<bean:message bundle="servermgrResources" key="servermgr.certificate.downloadcertificate" />
													</th>
													<th align="left" class="tblheader" valign="top" id="certDownload" style="border-color: #DBE6FF;width: 7%;">
														<bean:message bundle="servermgrResources" key="servermgr.certificate.duplicatecertificate" />
													</th> 
												</thead>
												<tbody>
													<tr>
														<td colspan="9" width="100%">
															<div style="overflow: auto; height: 110px;" id="divContentId"> 
															<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listCertificateTable">
								 								<%	if(listServerCertificate!=null && listServerCertificate.size()>0){%>
																<logic:iterate id="serverCertificateIdBean" name="serverCertificateForm" property="listServerCertificate" type="com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData">
																	<%ServerCertificateData serverCertData=serverCertificateIdBean;
																		String colorCode="#000000",title="";
																	%>
																	 <tr>
																		<td align="center" class="tblfirstcol" width="3%">
																			<input type="checkbox" name="selectCertificate" tabindex="6" value="<bean:write name="serverCertificateIdBean" property="serverCertificateId"/>" />
																		</td>
																		<td align="center" class="tblrows" width="4%">
																			<%=((pageNo-1)*pageSize)+count%>
																		</td>
																		<td align="left" class="tblrows" width="14%">
																				<a  href="javascript:void(0)"  tabindex="7" onclick="viewServerCertificate('<bean:write name="serverCertificateIdBean" property="serverCertificateId"/>');">
																					<bean:write name="serverCertificateIdBean" property="serverCertificateName" />
																				</a>
																		</td>
																		<td align="left" class="tblrows" width="24%">
																				 <bean:write name="serverCertificateIdBean"  property="strSubjectName"/> &nbsp; 
																		</td>
																		<td align="left" class="tblrows" width="24%">
																				<bean:write name="serverCertificateIdBean"  property="strIssuerName"/> &nbsp; 
																		</td>
																		<%	
																		if(serverCertData.getValidTo() != null){
																			if(serverCertData.getCheckExpiryTime() <= 0 ){ 
																				colorCode="#DF0101";
																				title="This certificate has expired";
																			}else if(serverCertData.getCheckExpiryTime() >= 1 && serverCertData.getCheckExpiryTime() <= 31){ 
																				colorCode="#DF7401";
																				title="This certificate will expire soon";
																			}
																		}
																		%>
																		<td align="left" class="tblrows" width="14%" style="color:<%=colorCode%>;" title="<%=title%>">
																				<bean:write name="serverCertificateIdBean" property="validTo" />&nbsp;
																		</td>
																		<td align="center" class="tblrows padding-in-td" style="width: 6%" id="downTd">
																			<a href="javascript:void(0)" tabindex="8" title="Download server certificate" onclick="downloadServerCertificate('<bean:write name="serverCertificateIdBean" property="serverCertificateId"/>');">
																				<img src="<%=basePath%>/images/download_certificate.png" alt="Download" border="0" />
																			</a>
																		</td>
																		<td align="center" class="tblrows padding-in-td" style="width: 6%" id="dupTd">
																			<a href="javascript:void(0)" tabindex="8" title="Duplicate server certificate" onclick="openDuplicateServerCertificate('<bean:write name="serverCertificateIdBean" property="serverCertificateId"/>');">
																				<img src="<%=basePath%>/images/duplicate_cert.png" alt="Duplicate" border="0"/>
																			</a>
																		</td>
																	</tr> 
																	<% count=count+1; %>
																	<% iIndex += 1; %>
																</logic:iterate>
							
																<%	}else{%>
																	<tr>
																		<td align="center" class="tblfirstcol no-rec-found" colspan="8">
																			<bean:message bundle="servermgrResources" key="servermgr.norecordsfound" />
																		</td>
																	</tr>
																<%	}%> 
																</table>
															</div>
														</td>
													</tr>
												</tbody>
											</tr>
										</table>
										</div>
										</div>
									</td>
								</tr>
						</table>						
					</td>
				</tr> 
			</table>
		</td>
	</tr>
</table>
<div id="dialog-form-server" title="Duplicate Server Certificate" style="display: none">
<table>
<tr>
	<td align="left" class="captiontext" valign="top"><bean:message bundle="servermgrResources" key="servermgr.certificate.name" /></td>
	<td align="left" class="labeltext" valign="top">
		<input type="text" id="serverDuplicateName" name="serverDuplicateName" maxlength="255" size="30" onblur="verifyServerName();" style="width:250px" placeholder="Enter Name"/>
		<font color="#FF0000"> *</font>
		<div id="verifyServerNameDiv" class="labeltext"></div>
	</td>
</tr>
<tr>
	<td colspan="2" align="center" class="captiontext" valign="top"> 
		<input type="button" name="Create" value=" Create Duplicate "  onclick="submitServerCertData();" class="light-btn"/>
		<input type="button" name="Cancel" value=" Cancel "  onclick="cancelServerCertData();" class="light-btn"/>
	</td>
</tr>
</table>
</div>
		
