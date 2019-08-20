<%
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	ServerCertificateForm serverCertificateForm = (ServerCertificateForm) request.getAttribute("serverCertificateForm");

	long pageNo 	 = serverCertificateForm.getPageNumber();
	long totalPages  = serverCertificateForm.getTotalPages();
	long totalRecord = serverCertificateForm.getTotalRecords();
	int count  = 1;
	int iIndex = 0;

	String strPageNumber 	= String.valueOf(pageNo);
	String strTotalPages 	= String.valueOf(totalPages);
	String strTotalRecords 	= String.valueOf(totalRecord);

	List listServerCertificate = serverCertificateForm.getListServerCertificate();
	String planet ="";
%>
<script type="text/javascript">
var searchName;
$(document).ready(function(){	
	$("#serverCertificateName").focus();
	$("#serverCertificateName").select();
});
    
 
<%-- function callDoSearch(value){
	if($.trim(searchName)!=$.trim(value) && $.trim(value).length>0){
		searchName = value;
		$.ajax({url:'<%=basePath%>/searchAllCertificate.do?',
	      	type:'POST', 
	      	async:false,
	      	data:'method=initSearch&ajaxServerCertificateName='+value,
	     	success: function(transport, textStatus, request){
	     		if(transport!=null){
	     			//alert(0);
	     			//alert(request.getResponseHeader('hey'));
	     			//alert(01);
 					//document.getElementById("serverCertificateName").value=searchName;
	     		}else{
	     			alert('in else');
	     		}
			}
	   	});
	}
};  --%>

function showall(){	
	var path = '<%=basePath%>/serverCertificate.do?method=showAll'; 
	window.open(path,'ServerCertificate','resizable=yes,scrollbars=yes'); 
}
function navigate(page){
	document.forms[0].action="serverCertificate.do?method=search&pageNo="+page;
	document.serverCertificateForm.submit();  
}
function deleteCertificate(){
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
			alert("Select at least one Server Certificate for delete Operation")
		}else{
			var r=confirm("This will delete the selected Server Certificate(s). Do you want to continue ?");
				if (r==true)
	  			{
					document.forms[0].action='<%=basePath%>/serverCertificate.do?method=deleteCertificate';
					document.forms[0].submit();
	  			}
		}
	}
	else{
		alert("No record found for delete Operation! ")
	}
}
function edit(serverCertificateId){	  
	  location.href='<%=basePath%>/serverCertificate.do?method=initUpdate&serverCertificateId='+serverCertificateId;
}
function downloadCertificate(serverCertificateId){	  
	  location.href='<%=basePath%>/serverCertificate.do?method=downloadFile&serverCertificateId='+serverCertificateId;
}
function view(serverCertificateId){
	  location.href='<%=basePath%>/serverCertificate.do?method=view&serverCertificateId='+serverCertificateId; 
} 
 
function validateSearch(){	
	document.serverCertificateForm.submit(); 
}
</script>

<html:form action="/searchAllCertificate.do?method=search"
	styleId="serverCertificateFormId">

	<html:hidden name="serverCertificateForm" styleId="pageNumber"
		property="pageNumber" value="<%=strPageNumber%>" />
	<html:hidden name="serverCertificateForm" styleId="totalPages"
		property="totalPages" value="<%=strTotalPages%>" />
	<html:hidden name="serverCertificateForm" styleId="totalRecords"
		property="totalRecords" value="<%=strTotalRecords%>" />
	<html:hidden name="serverCertificateForm" styleId="action"
		property="action" />
	<html:hidden name="serverCertificateForm" styleId="actionName"
		property="actionName" />
	<tr>
		<td align="left" class="btns-td" colspan="5" valign="top">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<tr>
					<td colspan='100%'>&nbsp;</td>
				</tr>
				<tr>
					<td align="left" class="labeltext" valign="top" width="2%"><bean:message
							bundle="servermgrResources" key="servermgr.certificatename" /></td>
					<td align="left" class="labeltext" valign="top" width="22%">
					<html:text  styleId="serverCertificateName" name="serverCertificateForm" property="serverCertificateName" onkeyup="callDoSearch(this.value);" size="30" maxlength="64" style="width:250px" tabindex="1"></html:text></td> <!-- onkeyup="callDoSearch(this.value);"   -->
				</tr>
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td class="btns-td" valign="middle">&nbsp;</td>
					<td align="left" class="labeltext" valign="top" width="5%"
						style="padding-left: 50px;"><input type="button"
						name="Search" width="5%" tabindex="2" Onclick="validateSearch()" value="  Search  " class="light-btn" /></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td align="left" class="labeltext" colspan="5" valign="top">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0">
				<tr>
					<td class="table-header" style="border: none;" width="50%"><bean:message
							bundle="servermgrResources" key="servermgr.certificate.list" />
					</td>
					<td align="right" class="blue-text" style="border: none;"
						valign="middle" width="50%">
						<%
							if (totalRecord == 0) {
						%> <%
 	} else if (pageNo == totalPages + 1) {
 %> [<%=((pageNo - 1) * pageSize) + 1%>-<%=totalRecord%>] of <%=totalRecord%>
						<%
							} else if (pageNo == 1) {
						%> [<%=(pageNo - 1) * pageSize + 1%>-<%=(pageNo - 1) * pageSize + pageSize%>]
						of <%=totalRecord%> <%
 	} else {
 %> [<%=((pageNo - 1) * pageSize) + 1%>-<%=((pageNo - 1) * pageSize) + pageSize%>]
						of <%=totalRecord%> <%
 	}
 %>
					</td>
				</tr>
				<tr>
					<td class="topHLine" style="font-size: 1px" colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<td class="btns-td" valign="middle"><input type="button"
						name="Create" value="   Create   " tabindex="3"
						onclick="javascript:location.href='<%=basePath%>/serverCertificate.do?method=initCreate&name='+document.serverCertificateForm.serverCertificateName.value"
						class="light-btn"> <html:button property="c_btnDelete"
							tabindex="4" value="   Delete   " styleClass="light-btn"
							onclick="deleteCertificate();" /> <html:button
							property="c_btnshowall" onclick="showall()" tabindex="5"
							value="   Show All   " styleClass="light-btn" /></td>
					<td class="btns-td" align="right">
						<%
							if (totalPages >= 1) {
						%> <%
 	if (pageNo == 1) {
 %> <a href="javascript:void(0)" onclick="navigate(<%=pageNo + 1%>);">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a> <a href="javascript:void(0)"
						onclick="navigate(<%=totalPages + 1%>);"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0">
					</a> <%
 	}
 %> <%
 	if (pageNo > 1 && pageNo != totalPages + 1) {
 %> <%
 	if (pageNo - 1 == 1) {
 %> <a href="javascript:void(0)" onclick="navigate(<%=1%>);"> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo - 1%>);">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo + 1%>);">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a> <a href="javascript:void(0)"
						onclick="navigate(<%=totalPages + 1%>);"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0">
					</a> <%
 	} else if (pageNo == totalPages) {
 %> <a href="javascript:void(0)" onclick="navigate(<%=1%>);"> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo - 1%>);">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo + 1%>);">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a> <a href="javascript:void(0)"
						onclick="navigate(<%=totalPages + 1%>);"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0">
					</a> <%
 	} else {
 %> <a href="javascript:void(0)" onclick="navigate(<%=1%>);"> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo - 1%>);">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo + 1%>);">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a> <a href="javascript:void(0)"
						onclick="navigate(<%=totalPages + 1%>);""> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0">
					</a> <%
 	}
 %> <%
 	}
 %> <%
 	if (pageNo == totalPages + 1) {
 %> <a href="javascript:void(0)" onclick="navigate(<%=1%>);"> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo - 1%>);">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a> <%
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
						<table width="100%" border="0" cellpadding="0" cellspacing="0"
							id="listTable">
							<tr>
								<td align="center" class="tblheaderfirstcol" valign="top" width="3%">
									<input type="checkbox" name="toggleAll" value="checkbox"
									onclick="checkAll(this)" />
								</td>
								<td align="center" class="tblheader" valign="top" width="3%">
									<bean:message bundle="servermgrResources"
										key="servermgr.serialnumber" />
								</td>
								<td align="left" class="tblheader" valign="top" width="15%">
									<bean:message bundle="servermgrResources"
										key="servermgr.certificate.name" />
								</td>
								<td align="left" class="tblheader" valign="top" width="25%">
									<bean:message bundle="servermgrResources"
										key="servermgr.certificate.subject" />
								</td>
								<td align="left" class="tblheader" valign="top" width="25%">
									<bean:message bundle="servermgrResources"
										key="servermgr.certificate.issuer" />
								</td>
								<td align="left" class="tblheader" valign="top" width="16%">
									<bean:message bundle="servermgrResources"
										key="servermgr.certificate.expireddate" />
								</td>
								<td align="center" class="tblheader" valign="top" width="5%">
									<bean:message bundle="servermgrResources"
										key="servermgr.certificate.downloadcertificate" />
								</td>
								<td align="center" class="tblheaderlastcol" valign="top" width="5%">
									<bean:message bundle="servermgrResources" key="servermgr.edit" />
								</td>
							</tr>
							<%
								if (listServerCertificate != null
											&& listServerCertificate.size() > 0) {
									int i=0;
							%>
							<logic:iterate id="serverCertificateIdBean"
								name="serverCertificateForm" property="listServerCertificate"
								type="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData">
								<%
									ServerCertificateData serverCertData = serverCertificateIdBean;
								%>
								<tr id="dataRow" name="dataRow" >
									<td align="center" class="tblfirstcol"><input
										type="checkbox" name="select"
										value="<bean:write name="serverCertificateIdBean" property="serverCertificateId"/>" onclick="onOffHighlightedRow(<%=i++%>,this)" />
									</td>
									<td align="center" class="tblrows"><%=((pageNo - 1) * pageSize) + count%>
									</td>
									<td align="left" class="tblrows">
										<div style="overflow: auto; word-wrap: break-word;">
											<a href="javascript:void(0)"
												onclick="view(<bean:write name="serverCertificateIdBean" property="serverCertificateId"/>);">
												<bean:write name="serverCertificateIdBean"
													property="serverCertificateName" />
											</a>
										</div>
									</td>
									<td align="left" class="tblrows">
										<div style="overflow: auto; word-wrap: break-word;">
											<%
												if (serverCertData.getSubject() != null) {
											%>
											<bean:define id="subjectData" name="serverCertificateIdBean"
												property="subject" type="java.lang.String" />
											<%
												String[] subjectDetail = subjectData.split(",");
																for (String str : subjectDetail) {
																	if (str.contains("CN=")) {
											%>
											<%=str.split("CN=")[1]%>
											<%
												}
											%>
											<%
												}
											%>
											<%
												}
											%>
										</div>
									</td>
									<td align="left" class="tblrows">
										<div style="overflow: auto; word-wrap: break-word;">
											<%
												if (serverCertData.getIssuer() != null) {
											%>
											<bean:define id="issuerData" name="serverCertificateIdBean"
												property="issuer" type="java.lang.String" />
											<%
												String[] issuerDetail = issuerData.split(",");
																for (String str : issuerDetail) {
																	if (str.contains("CN=")) {
											%>
											<%=str.split("CN=")[1]%>
											<%
												}
											%>
											<%
												}
											%>
											<%
												}
											%>
										</div>
									</td>
									<td align="left" class="tblrows">
										<div style="overflow: auto; word-wrap: break-word;">
											<bean:write name="serverCertificateIdBean" property="validTo" />
											&nbsp;
										</div>
									</td>
									<td align="center" class="tblrows"><a
										href="javascript:void(0)"
										onclick="downloadCertificate(<bean:write name="serverCertificateIdBean" property="serverCertificateId"/>);">
											<img src="<%=basePath%>/images/download.jpg" alt="Download"
											border="0" height="20" width="20">
									</a></td>
									<td align="center" class="tblrows"><a
										href="javascript:void(0)"
										onclick="edit(<bean:write name="serverCertificateIdBean" property="serverCertificateId"/>);">
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
								<td align="center" class="tblfirstcol" colspan="8"><bean:message
										bundle="servermgrResources" key="servermgr.norecordsfound" />
								</td>
							</tr>
							<%
								}
							%>
						</table>
					</td>
				</tr>
				<tr>
					<td class="btns-td" valign="middle"><input type="button"
						name="Create" value="   Create   " tabindex="3"
						onclick="javascript:location.href='<%=basePath%>/serverCertificate.do?method=initCreate&name='+document.serverCertificateForm.serverCertificateName.value"
						class="light-btn"> <html:button property="c_btnDelete"
							onclick="deleteCertificate()" value="   Delete   "
							styleClass="light-btn" /> <html:button property="c_btnshowall"
							onclick="showall()" value="   Show All   " styleClass="light-btn" />
					</td>
					<td class="btns-td" align="right">
						<%
							if (totalPages >= 1) {
						%> <%
 	if (pageNo == 1) {
 %> <a href="javascript:void(0)" onclick="navigate(<%=pageNo + 1%>);">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a> <a href="javascript:void(0)"
						onclick="navigate(<%=totalPages + 1%>);"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0">
					</a> <%
 	}
 %> <%
 	if (pageNo > 1 && pageNo != totalPages + 1) {
 %> <%
 	if (pageNo - 1 == 1) {
 %> <a href="javascript:void(0)" onclick="navigate(<%=1%>);"> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo - 1%>);">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo + 1%>);">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a> <a href="javascript:void(0)"
						onclick="navigate(<%=totalPages + 1%>);"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0">
					</a> <%
 	} else if (pageNo == totalPages) {
 %> <a href="javascript:void(0)" onclick="navigate(<%=1%>);"> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo - 1%>);">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo + 1%>);">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a> <a href="javascript:void(0)"
						onclick="navigate(<%=totalPages + 1%>);"> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0">
					</a> <%
 	} else {
 %> <a href="javascript:void(0)" onclick="navigate(<%=1%>);"> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo - 1%>);">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo + 1%>);">
							<img src="<%=basePath%>/images/next.jpg" name="Image61"
							onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Next" border="0">
					</a> <a href="javascript:void(0)"
						onclick="navigate(<%=totalPages + 1%>);""> <img
							src="<%=basePath%>/images/last.jpg" name="Image612"
							onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Last" border="0">
					</a> <%
 	}
 %> <%
 	}
 %> <%
 	if (pageNo == totalPages + 1) {
 %> <a href="javascript:void(0)" onclick="navigate(<%=1%>);"> <img
							src="<%=basePath%>/images/first.jpg" name="Image511"
							onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="First" border="0">
					</a> <a href="javascript:void(0)" onclick="navigate(<%=pageNo - 1%>);">
							<img src="<%=basePath%>/images/previous.jpg" name="Image5"
							onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"
							onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
					</a> <%
 	}
 %> <%
 	}
 %>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</html:form>