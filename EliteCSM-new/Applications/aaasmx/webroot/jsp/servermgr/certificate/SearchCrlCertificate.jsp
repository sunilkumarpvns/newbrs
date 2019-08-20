<%@page import="com.elitecore.elitesm.datamanager.servermgr.certificate.data.CrlCertificateData"%>
<%@ page import="com.elitecore.elitesm.web.servermgr.certificate.forms.ServerCertificateForm"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%
	basePath = request.getContextPath();
    serverCertificateForm = (ServerCertificateForm) request.getAttribute("serverCertificateForm");
	pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));

	pageNo=serverCertificateForm.getCrlPageNumber();
	totalPages=serverCertificateForm.getCrlTotalPages();
	totalRecord=serverCertificateForm.getCrlTotalRecords();
	count=1;
    iIndex =0;
	
	strPageNumber=String.valueOf(pageNo);
	strTotalPages=String.valueOf(totalPages);
	strTotalRecords=String.valueOf(totalRecord); 
	
	List listCrlCertificate = serverCertificateForm.getListCrlCertificate(); 
%>

<script type="text/javascript">
var crlScroll;
$(document).ready(function() {
	$("#crlToggleImageElement").click(function(){
		  var imgElement = document.getElementById("crlToggleImageElement");
		  if ($("#crlToggleDivElement").is(':hidden')) {
	          imgElement.src="<%=basePath%>/images/top-level.jpg";
	     } else {
	          imgElement.src="<%=basePath%>/images/bottom-level.jpg";
	     }
	    $("#crlToggleDivElement").slideToggle("normal");
	});
});
var isValidName=false;
function verifyName(){
	var searchName=document.getElementById("crlDuplicateName").value;
	isValidName=verifyInstanceName('<%=InstanceTypeConstants.CRL_CERTIFICATE%>',searchName,'create','','verifyNameDiv');
}
var crlDialogHeight="110";
$(document).ready(function() {
	if ( $.browser.msie ) {
		crlDialogHeight="180";
	}
});
$(function() {
	$( "#dialog-form" ).dialog({
	    autoOpen: false,
	    height: crlDialogHeight,
	    width: 390,
	    modal: true
	});
	 $.fn.hasScrollBar = function() {
	        return this.get(0).scrollHeight > this.height();
	 }
	 crlScroll=$('#divCrlId').hasScrollBar();
	 if(crlScroll == true){
		 $('#downCrlTd').width('6%');
		 $('#dupCrlTd').width('6%');
	 }else{
		 $('#downCrlTd').width('7%');
		 $('#dupCrlTd').width('7%');
	 }
});
var crlCertificateId="";
function openDuplicateCRL(crlId){
	$('#crlDuplicateName').val("");
	$( "#dialog-form" ).dialog( "open" );
	$('#crlDuplicateName').focus();
	$("#crlDuplicateName").blur();
	crlCertificateId=crlId;
}
function showAllCrl(){
	var path = '<%=basePath%>/serverAllCertificates.do?method=showAllCrl'; 
	window.open(path,'CrlCertificate','resizable=yes,scrollbars=yes'); 
}  
function submitCRLData(){
	if(isEmpty($("#crlDuplicateName").val())){
		alert("Please Enter Name");
		return false;
	}else{
		if(!isValidName){
			alert('Enter Valid CRL Name');
			$('#crlDuplicateName').focus();
			return false;
		}else{
			var crlName=$("#crlDuplicateName").val().trim();
			location.href='<%=basePath%>/serverAllCertificates.do?method=duplicateCRL&crlCertificateId='+crlCertificateId+'&crlName='+crlName;
			$("#dialog-form").dialog( "close" );
			$("#crlDuplicateName").val('');
		}
	}
}
function cancelCRLData(){
	$("#dialog-form").dialog( "close" );
	$("#crlDuplicateName").val('');
}
function navigateCrl(page){
 	document.forms[0].action="serverAllCertificates.do?method=search&pageNo="+page;
	document.serverCertificateForm.submit();   
}
function deleteCertificate(){
	var selectArray = document.getElementsByName('selectCRL');
	
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
					document.forms[0].action='<%=basePath%>/serverAllCertificates.do?method=deleteCrlCertificate';
					document.forms[0].submit();
	  			}
		}
	}
	else{
		alert("No Records Found For Delete Operation! ");
	} 
}	

function editCrl(crlCertificateId){
	location.href='<%=basePath%>/serverAllCertificates.do?method=initUpdateCrl&crlCertificateId='+crlCertificateId; 
}
function downloadCrlCertificate(crlCertificateId){	  
	location.href='<%=basePath%>/serverAllCertificates.do?method=downloadCrlFile&crlCertificateId='+crlCertificateId;
}
function duplicateCRL(crlCertificateId){
	location.href='<%=basePath%>/serverAllCertificates.do?method=duplicateCRL&crlCertificateId='+crlCertificateId;
}
function viewCrl(crlCertificateId){
    location.href='<%=basePath%>/serverAllCertificates.do?method=viewCrl&crlCertificateId='+crlCertificateId; 
} 

function  checkAll(){
 	if( document.serverCertificateForm.toggleAllCRL.checked == true) {
 		var selectVars = document.getElementsByName('selectCRL');
	 	for (i = 0; i < selectVars.length;i++)
			selectVars[i].checked = true ;
    } else if (document.serverCertificateForm.toggleAllCRL.checked == false){
 		var selectVars = document.getElementsByName('selectCRL');	    
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
			<table width="100%" name="c_tblSearchCrlCertificate" id="c_tblSearchCrlCertificate" align="right" border="0" cellpadding="0" cellspacing="0">
				<%--  <html:form action="/crlCertificate.do?method=search" styleId="serverCertificateFormId"> --%>
				    <html:hidden name="serverCertificateForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>"/>
				 	<html:hidden name="serverCertificateForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages %>"/>
				 	<html:hidden name="serverCertificateForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords %>" />  
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
													<bean:message bundle="servermgrResources" key="servermgr.crlcertificate" />
												</td>
												<td align="right" class="blue-text" style="padding-right: 8px;" >
														<img alt="bottom" id="crlToggleImageElement" tabindex="18" src="<%=basePath%>/images/top-level.jpg" />
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
									<div id="crlToggleDivElement" >
										<table width="100%" border="0"  cellpadding="0" cellspacing="0" id="crlButtonTables" style="padding-left: 5px;padding-right: 5px;padding-bottom: 5px;padding-top: 2px">
											<tr>
												<td>
													<html:button property="c_btnDelete"  tabindex="19" value="   Delete   " styleClass="light-btn" onclick="deleteCertificate();" /> 
													<html:button property="c_btnshowall" onclick="showAllCrl()" tabindex="20" value="   Show All   " styleClass="light-btn" />
													<input type="button" name="Create" value="   Create   " tabindex="21" onclick="javascript:location.href='<%=basePath%>/serverAllCertificates.do?method=initCreateCrl'" class="light-btn">
												</td>
											</tr>
										</table>
										<div style="height: 130px;">
										<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable" style="padding-left: 5px;padding-right: 5px;"> 
											<thead>
												<tr>
													<th align="center" class="tblheader" valign="top" style="border-color: #DBE6FF;width: 3%;">
														<input type="checkbox" name="toggleAllCRL" tabindex="22" value="checkbox" onclick="checkAll()" />
													</th>
													<th align="center" class="tblheader" valign="top" style="border-color: #DBE6FF;width: 4%;">
														<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
													</th>
													<th align="left" class="tblheader" valign="top" id="crlName" style="border-color: #DBE6FF;width: 14%;">
														<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.name" />
													</th>
													<th align="left" class="tblheader" valign="top" id="crlIssuer" style="border-color: #DBE6FF;width: 24%;">
														<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.issuer" />
													</th>
													<th align="left" class="tblheader" valign="top" id="crlSerialNo" style="border-color: #DBE6FF;width: 24%;">
														<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.serialno" />
													</th>																				
													<th align="left" class="tblheader" valign="top" id="crlLastUpdate" style="border-color: #DBE6FF;width: 14%;">
														<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.lastupdate" />
													</th>													
													<th align="left" class="tblheader" valign="top" style="border-color: #DBE6FF;width: 7%;">
														<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.downloadcertificate" />
													</th>		
													<th align="left" class="tblheader" valign="top" id="certDownload" style="border-color: #DBE6FF;width: 7%;">
														<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.duplicatecertificate" />
													</th>										
												</tr>
											</thead>
											<tbody>
													<tr>
														<td colspan="9" width="100%">
															<div style="overflow: auto; height: 110px;" id="divCrlId">
																<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listCrlTable">
																 <%	if(listCrlCertificate!=null && listCrlCertificate.size()>0){%>
																	<logic:iterate id="crlCertificateIdBean" name="serverCertificateForm" property="listCrlCertificate" type="com.elitecore.elitesm.datamanager.servermgr.certificate.data.CrlCertificateData">
																	<%CrlCertificateData crlCertData= crlCertificateIdBean;%>
																		 <tr>
																			<td align="center" class="tblfirstcol" width="3%">
																				<input type="checkbox" name="selectCRL" tabindex="23" value="<bean:write name="crlCertificateIdBean" property="crlCertificateId"/>" />
																			</td>
																			<td align="center" class="tblrows" width="4%">
																				<%=((pageNo-1)*pageSize)+count%>
																			</td>
																			<td align="left" class="tblrows" width="14%">
																				<div style="overflow: auto; word-wrap: break-word;">
																					<a href="javascript:void(0)" tabindex="24" onclick="viewCrl('<bean:write name="crlCertificateIdBean" property="crlCertificateId"/>');">
																						<bean:write name="crlCertificateIdBean" property="crlCertificateName" />
																					</a>
																				</div>
																			</td>	
																			<td align="left" class="tblrows" width="24%">
																				<div style=" overflow: auto; word-wrap: break-word;">
																					<bean:write name="crlCertificateIdBean" property="strIssuerName"/> &nbsp;
																				</div>
																			</td>			
																			<td align="left" class="tblrows" width="24%">
																				<div style=" overflow: auto; word-wrap: break-word;">
																					<bean:write name="crlCertificateIdBean" property="serialNo" />&nbsp;
																				</div>
																			</td>
																			<td align="left" class="tblrows" width="14%">
																				<div style=" overflow: auto; word-wrap: break-word;">
																					<bean:write name="crlCertificateIdBean" property="lastUpdate" />&nbsp;
																				</div>
																			</td>
																			<td align="center" class="tblrows padding-in-td" style="width: 6%" id="downCrlTd">
																				<a href="javascript:void(0)" tabindex="25" onclick="downloadCrlCertificate('<bean:write name="crlCertificateIdBean" property="crlCertificateId"/>');">
																						<img src="<%=basePath%>/images/download_certificate.png" alt="Download" border="0" />
																				</a>
																			</td>
																			<td align="center" class="tblrows padding-in-td" style="width: 6%" id="dupCrlTd">
																				<a href="javascript:void(0)" tabindex="26" id="duplicateCRLTag" title="Duplicate server certificate" onclick="openDuplicateCRL('<bean:write name="crlCertificateIdBean" property="crlCertificateId"/>');">
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
<div id="dialog-form" title="Duplicate CRL" style="display: none;">
<table>
<tr>
	<td align="left" class="captiontext" valign="top"><bean:message bundle="servermgrResources" key="servermgr.certificate.name" /></td>
	<td align="left" class="labeltext" valign="top">
		<input type="text" id="crlDuplicateName" name="crlDuplicateName" maxlength="255" size="30" onblur="verifyName();" style="width:250px" placeholder="Enter Name"/>
		<font color="#FF0000"> *</font>
		<div id="verifyNameDiv" class="labeltext"></div>
	</td>
</tr>
<tr>
	<td colspan="2" align="center" class="captiontext" valign="top"> 
		<input type="button" name="Create" value=" Create Duplicate "  onclick="submitCRLData();" class="light-btn"/>
		<input type="button" name="Cancel" value=" Cancel "  onclick="cancelCRLData();" class="light-btn"/>
	</td>
</tr>
</table>
</div>
