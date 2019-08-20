<%@page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.TrustedCertificateData"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.certificate.forms.ServerCertificateForm"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%
	basePath = request.getContextPath();
	pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	serverCertificateForm = (ServerCertificateForm) request.getAttribute("serverCertificateForm");

	pageNo=serverCertificateForm.getTrustedPageNumber();
	totalPages=serverCertificateForm.getTrustedTotalPages();
	totalRecord=serverCertificateForm.getTrustedtotalRecords();
	int countTrusted=1;
    int iTIndex =0;
	
	strPageNumber=String.valueOf(pageNo);
	strTotalPages=String.valueOf(totalPages);
	strTotalRecords=String.valueOf(totalRecord); 
	
	List listTrustedCertificate = serverCertificateForm.getListTrustedCertificate();
%>
<script type="text/javascript">
var trustedScroll;
$(document).ready(function() {
	 $("#trustedcertificateToggleImageElement").click(function(){
		  var imgElement = document.getElementById("trustedcertificateToggleImageElement");
		  if ($("#trustedcertificateToggleDivElement").is(':hidden')) {
	            imgElement.src="<%=basePath%>/images/top-level.jpg";
	       } else {
	            imgElement.src="<%=basePath%>/images/bottom-level.jpg";
	       }
	      $("#trustedcertificateToggleDivElement").slideToggle("normal");
	  });
});

var isValidTrustedName=false;
var trustedDialogHeight="110";
$(document).ready(function() {
	if ( $.browser.msie ) {
		trustedDialogHeight="180";
	}
});
function verifyTrustedName(){
	var searchName=document.getElementById("trustedDuplicateName").value;
	isValidTrustedName=verifyInstanceName('<%=InstanceTypeConstants.TRUSTED_CERTIFICATE%>',searchName,'create','','verifyTrustedNameDiv');
}
$(function() {
	$( "#dialog-trusted-form" ).dialog({
	    autoOpen: false,
	    height: trustedDialogHeight,
	    width: 390,
	    modal: true
	});
	 $.fn.hasScrollBar = function() {
	        return this.get(0).scrollHeight > this.height();
	 }
	 trustedScroll=$('#divTrustedId').hasScrollBar();
	 if(trustedScroll == true){
		 $('#downTrustedTd').width('6%');
		 $('#dupTrustedTd').width('6%');
	 }else{
		 $('#downTrustedTd').width('7%');
		 $('#dupTrustedTd').width('7%');
	 }
});
var trustedCertificateId="";
function openTrustedCertificate(trustedId){
	$('#trustedDuplicateName').val("");
	$( "#dialog-trusted-form" ).dialog( "open" );
	$('#trustedDuplicateName').focus();
	$( "#trustedDuplicateName" ).blur();
	trustedCertificateId=trustedId;
}
function submitTrustedData(){
	if(isEmpty($("#trustedDuplicateName").val())){
		alert("Please Enter Name");
		return false;
	}else{
		if(!isValidTrustedName){
			alert('Enter Valid Trusted Name');
			$('#trustedDuplicateName').focus();
			return false;
		}else{
			var trustedName=$("#trustedDuplicateName").val().trim();
			location.href='<%=basePath%>/serverAllCertificates.do?method=duplicateTrustedCertificate&trustedCertificateId='+trustedCertificateId+'&trustedCertName='+trustedName;
			$("#dialog-trusted-form").dialog( "close" );
			$("#trustedDuplicateName").val("");
		}
	}
}
function cancelTrustedData(){
	$("#dialog-trusted-form").dialog( "close" );
	$("#trustedDuplicateName").val("");
}
function showAllTrusted(){	
	var path = '<%=basePath%>/serverAllCertificates.do?method=showAllTrusted'; 
	window.open(path,'TrustedCertificate','resizable=yes,scrollbars=yes'); 
}  
function navigateTrusted(page){
	document.forms[0].action="serverAllCertificates.do?method=search&pageNo="+page;
	document.serverCertificateForm.submit();  
}
function deleteTrustedCertificate(){
	var selectArray = document.getElementsByName('selectTrusted');
	
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
				if (r==true)
	  			{
					document.forms[0].action='<%=basePath%>/serverAllCertificates.do?method=deleteTrustedCertificate';
					document.forms[0].submit();
	  			}
		}
	}
	else{
		alert("No Records Found For Delete Operation! ");
	}
}	
function downloadTrustedCertificate(trustedCertificateId){	  
	  location.href='<%=basePath%>/serverAllCertificates.do?method=downloadTrustedFile&trustedCertificateId='+trustedCertificateId;
}
function duplicateTrustedCertificate(trustedCertificateId){
	 location.href='<%=basePath%>/serverAllCertificates.do?method=duplicateTrustedCertificate&trustedCertificateId='+trustedCertificateId;
}
function viewTrustedCertificate(trustedCertificateId){
	  location.href='<%=basePath%>/serverAllCertificates.do?method=viewTrustedCertificate&trustedCertificateId='+trustedCertificateId; 
} 
function  checkTrustedAll(){
 	if(document.getElementById('toggleAllTrusted').checked == true) {
 		var selectVars = document.getElementsByName('selectTrusted');
	 	for (i = 0; i < selectVars.length;i++)
			selectVars[i].checked = true ;
    } else if (document.getElementById('toggleAllTrusted').checked == false){
 		var selectVars = document.getElementsByName('selectTrusted');	    
		for (i = 0; i < selectVars.length; i++)
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
				    <html:hidden name="serverCertificateForm" styleId="trustedPageNumber" property="trustedPageNumber" value="<%=strPageNumber%>"/>
				 	<html:hidden name="serverCertificateForm" styleId="trustedTotalPages" property="trustedTotalPages" value="<%=strTotalPages %>"/>
				 	<html:hidden name="serverCertificateForm" styleId="trustedtotalRecords" property="trustedtotalRecords" value="<%=strTotalRecords %>" /> 
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
													<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.list" />
												</td>
												<td align="right" class="blue-text" style="padding-right: 8px;" >
													<img alt="bottom" id="trustedcertificateToggleImageElement" tabindex="9" src="<%=basePath%>/images/top-level.jpg" />
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
										<div id="trustedcertificateToggleDivElement" >
										<table width="100%" border="0"  cellpadding="0" cellspacing="0" id="allButtonTables" style="padding-left: 5px;padding-right: 5px;padding-bottom: 5px;padding-top: 2px">
											<tr>
												<td>
													<html:button property="c_btnDelete"  tabindex="10" value="   Delete   " styleClass="light-btn" onclick="deleteTrustedCertificate();" /> 
													<html:button property="c_btnshowall" onclick="showAllTrusted()" tabindex="11" value="   Show All   " styleClass="light-btn" />
													<input type="button" name="Create" value="   Create   " tabindex="12" onclick="javascript:location.href='<%=basePath%>/serverAllCertificates.do?method=initCreateTrusted'" class="light-btn">
												</td>
											</tr>
										</table>
										<div style="height: 130px;">
										<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable" style="padding-left: 5px;padding-right: 5px;"> 
												<thead>
														<tr>
															<th align="center" class="tblheader" valign="top" id="truChk" style="border-color: #DBE6FF;width: 3%;">
																<input type="checkbox" id="toggleAllTrusted" name="toggleAllTrusted" value="checkbox" tabindex="13" onclick="checkTrustedAll()" />
															</th>
															<th align="center" class="tblheader" valign="top"  style="border-color: #DBE6FF;width: 4%;">
																<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
															</th>
															<th align="left" class="tblheader" valign="top" id="truName" style="border-color: #DBE6FF;width: 14%;">
																<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.name" />
															</th>
															<th align="left" class="tblheader" valign="top" id="truSubject" style="border-color: #DBE6FF;width: 24%;">
																<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.subject" />
															</th>
															<th align="left" class="tblheader" valign="top" id="truIssuer" style="border-color: #DBE6FF;width: 24%;">
																<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.issuer" />
															</th>
															<th align="left" class="tblheader" valign="top" id="truExpireDate" style="border-color: #DBE6FF;width: 14%;">
																<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.expireddate" />
															</th>
															<th align="left" class="tblheader" valign="top" style="border-color: #DBE6FF;width: 7%;">
																<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.downloadcertificate" />
															</th> 
															<th align="left" class="tblheader" valign="top" id="certDownload" style="border-color: #DBE6FF;width: 7%;">
																<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.duplicatecertificate" />
															</th>
														</tr>
												</thead>
												<tbody>
													<tr>
														<td colspan="9" width="100%">
															<div style="overflow: auto; height: 110px;" id="divTrustedId">
																	<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTrustedTable">
																	 <%	if(listTrustedCertificate!=null && listTrustedCertificate.size()>0){%>
																			<logic:iterate id="trustedCertificateIdBean" name="serverCertificateForm" property="listTrustedCertificate" type="com.elitecore.elitesm.datamanager.servermgr.certificate.data.TrustedCertificateData">
																			<%
																			String colorCode="#000000",title="";
																			TrustedCertificateData trustedCertData= trustedCertificateIdBean;%>
																				 <tr>
																					<td align="center" class="tblfirstcol" width="3%">
																						<input type="checkbox"  id="selectTrusted" tabindex="14" name="selectTrusted" value="<bean:write name="trustedCertificateIdBean" property="trustedCertificateId"/>" />
																					</td>
																					<td align="center" class="tblrows" width="4%">
																						<%=((pageNo-1)*pageSize)+countTrusted%>
																					</td>
																					<td align="left" class="tblrows" width="14%">
																						<a href="javascript:void(0)"  title="<%=title %>" tabindex="15" onclick="viewTrustedCertificate('<bean:write name="trustedCertificateIdBean" property="trustedCertificateId"/>');">
																								<bean:write name="trustedCertificateIdBean" property="trustedCertificateName" />
																						</a>
																					</td>
																					<td align="left" class="tblrows" width="24%">
																						 	<bean:write name="trustedCertificateIdBean" property="strSubjectName"/>&nbsp;
																					</td>
																					<td align="left" class="tblrows" width="24%">
																							<bean:write name="trustedCertificateIdBean" property="strIssuerName"/>&nbsp;
																					</td>
																					<%	
																					if(trustedCertData.getValidTo() != null){	
																						if(trustedCertData.getCheckExpiryTime() <= 0 ){ 
																							colorCode="#DF0101";
																							title="This certificate has expired";
																						}else if(trustedCertData.getCheckExpiryTime() >= 1 && trustedCertData.getCheckExpiryTime() <= 31){ 
																							colorCode="#DF7401";
																							title="This certificate will expire soon";
																						}
																					}
																					%>
																					<td align="left" class="tblrows" width="14%" style="color:<%=colorCode%>;" title="<%=title%>">
																							<bean:write name="trustedCertificateIdBean" property="validTo" />&nbsp;
																					</td>
																					<td align="center" class="tblrows padding-in-td" id="downTrustedTd">
																						<a href="javascript:void(0)" tabindex="16" onclick="downloadTrustedCertificate('<bean:write name="trustedCertificateIdBean" property="trustedCertificateId"/>');">
																							<img src="<%=basePath%>/images/download_certificate.png" alt="Download" border="0" />
																						</a>
																					</td>
																					<td align="center" class="tblrows padding-in-td" id="dupTrustedTd">
																						<a href="javascript:void(0)" tabindex="17" title="Duplicate Trusted certificate" onclick="openTrustedCertificate('<bean:write name="trustedCertificateIdBean" property="trustedCertificateId"/>');">
																							<img src="<%=basePath%>/images/duplicate_cert.png" alt="Duplicate" border="0"/>
																						</a>
																					</td>			
																				</tr> 
																				<% countTrusted=countTrusted+1; %>
																				<% iTIndex += 1; %>
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
<div id="dialog-trusted-form" title="Duplicate Trusted Certificate" style="display: none;">
<table>
<tr>
	<td align="left" class="captiontext" valign="top"><bean:message bundle="servermgrResources" key="servermgr.certificate.name" /></td>
	<td align="left" class="labeltext" valign="top">
		<input type="text" id="trustedDuplicateName" name="trustedDuplicateName" maxlength="255" size="30" onblur="verifyTrustedName();" style="width:250px" placeholder="Enter Name"/>
		<font color="#FF0000"> *</font>
		<div id="verifyTrustedNameDiv" class="labeltext"></div>
	</td>
</tr>
<tr>
	<td colspan="2" align="center" class="labeltext" valign="top"> 
		<input type="button" name="Create" value=" Create Duplicate "  onclick="submitTrustedData();" class="light-btn"/>
		<input type="button" name="Cancel" value=" Cancel "  onclick="cancelTrustedData();" class="light-btn"/>
	</td>
</tr>
</table>
</div>
