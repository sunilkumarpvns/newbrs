<script language="javascript" src="<%=basePath%>/js/commonfunctions.js"></script>
<%
	pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	TrustedCertificateForm trustedCertificateForm = (TrustedCertificateForm) request.getAttribute("trustedCertificateForm");

	pageNo		= trustedCertificateForm.getPageNumber();
	totalPages	= trustedCertificateForm.getTotalPages();
	totalRecord	= trustedCertificateForm.getTotalRecords();
	
	count	= 1;
    iIndex 	= 0;
	
	strPageNumber	= String.valueOf(pageNo);
	strTotalPages	= String.valueOf(totalPages);
	strTotalRecords	= String.valueOf(totalRecord); 
	
	List listTrustedCertificate = trustedCertificateForm.getListTrustedCertificate();
%>
<script type="text/javascript">
function showallTrusted(){	
	var path = '<%=basePath%>/trustedCertificate.do?method=showAll'; 
	window.open(path,'TrustedCertificate','resizable=yes,scrollbars=yes'); 
}  
function navigateTrusted(page){
	document.forms[0].action="trustedCertificate.do?method=search&pageNo="+page;
	document.trustedCertificateForm.submit();  
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
		alert("Select at least one Trusted Certificate for delete Operation")
		}else{
			var r=confirm("This will delete the selected Trusted Certificate(s). Do you want to continue ?");
				if (r==true)
	  			{
					document.forms[2].action='<%=basePath%>/trustedCertificate.do?method=deleteCertificate';
					document.forms[2].submit();
	  			}
		}
	}
	else{
		alert("No record Found for delete Operation ! ");
	}
}	


function editTrusted(trustedCertificateId){	  
	  location.href='<%=basePath%>/trustedCertificate.do?method=initUpdate&trustedCertificateId='+trustedCertificateId;
}
function downloadTrustedCertificate(trustedCertificateId){	  
	  location.href='<%=basePath%>/trustedCertificate.do?method=downloadFile&trustedCertificateId='+trustedCertificateId;
}
function viewTrusted(trustedCertificateId){
	  location.href='<%=basePath%>/trustedCertificate.do?method=view&trustedCertificateId='+trustedCertificateId; 
} 
function  checkAllTrusted(parentCheckBox){
	var dataRows = document.getElementsByName('dataRowTrusted');
	try{
	 	if( parentCheckBox.checked == true) {
	 		var selectVars = document.getElementsByName('selectTrusted');
		 	for (var i = 0; i < selectVars.length;i++){
				selectVars[i].checked = true ;
				dataRows[i].className='onHighlightRow';
		 	}
	    } else if (parentCheckBox.checked == false){
	 		var selectVars = document.getElementsByName('selectTrusted');	    
			for (var i = 0; i < selectVars.length; i++){
				selectVars[i].checked = false ;
				dataRows[i].className='offHighlightRow';
			}
		}
	}catch(e){
		alert("Error : "+e);
	}
}

function onOffHighlightedRowTrusted(i,checkBox){
	var dataRows = document.getElementsByName('dataRowTrusted');
	if(checkBox.checked==true){
		dataRows[i].className='onHighlightRow';
	}else{
		dataRows[i].className='offHighlightRow';
	}
}

function validateTrustedSearch(){	
	document.trustedCertificateForm.submit(); 
}


</script>
			
				 <html:form action="/trustedCertificate.do?method=search" styleId="trustedCertificateFormId">
				    <html:hidden name="trustedCertificateForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>"/>
				 	<html:hidden name="trustedCertificateForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages %>"/>
				 	<html:hidden name="trustedCertificateForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords %>" /> 
			
				
					<tr>
 						<td align="left" class="labeltext" colspan="5" valign="top">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0">
								<tr>
								    <td class="table-header" style="border: none;" width="50%">
										<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.list" />
									</td>
									<td align="right" class="blue-text" style="border: none;" valign="middle" width="50%">
										<% if(totalRecord == 0) { %> 
										<% }else if(pageNo == totalPages+1) { %>
											[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
										<% } else if(pageNo == 1) { %> 
											[<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %> 
										<% } else { %> [<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %> 
										<% } %>
									</td> 
								</tr>
								<tr >
									<td  class="topHLine" style="font-size: 1px" colspan="2" >&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle">
										<input type="button" name="Create" value="   Create   " tabindex="3" onclick="javascript:location.href='<%=basePath%>/trustedCertificate.do?method=initCreate&name='+document.serverCertificateForm.serverCertificateName.value" class="light-btn"/>									
										<html:button property="c_btnDelete"  tabindex="4" value="   Delete   " styleClass="light-btn" onclick="deleteTrustedCertificate();" /> 
										<html:button property="c_btnshowall" onclick="showallTrusted()" tabindex="5" value="   Show All   " styleClass="light-btn" />
									</td> 
 									<td class="btns-td" align="right">
										<% if(totalPages >= 1) { %> 
											<% if(pageNo == 1){ %> 
												<a href="javascript:void(0)" onclick="navigateTrusted(<%=pageNo+1%>);">
													<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateTrusted(<%=totalPages+1%>);">
													<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
												</a>
											<% } %> 
											<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
												<%  if(pageNo-1 == 1){ %>
													<a href="javascript:void(0)" onclick="navigateTrusted(<%=1%>);">
														<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo-1%>);">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
													</a> 
													<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo+1%>);">
														<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateTrusted(<%= totalPages+1%>);">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
													</a>
												<% } else if(pageNo == totalPages){ %> 
													<a href="javascript:void(0)" onclick="navigateTrusted(<%=1%>);">
														<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo-1%>);">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
													</a> 
													<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo+1%>);">
														<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateTrusted(<%= totalPages+1%>);">
														<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
													</a>
												<% } else { %> 
													<a href="javascript:void(0)" onclick="navigateTrusted(<%=1%>);">
														<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo-1%>);">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
													</a> 
													<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo+1%>);">
														<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateTrusted(<%= totalPages+1%>);"">
														<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
													</a>
												<% } %> 
											<% } %> 
											<% if(pageNo == totalPages+1) { %> 
												<a href="javascript:void(0)" onclick="navigateTrusted(<%=1%>);">
													<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateTrusted(<%=pageNo-1%>);">
													<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
												</a> 
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
												<td align="center" class="tblheaderfirstcol" valign="top" width="3%">
													<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAllTrusted(this)" />
												</td>
												<td align="center" class="tblheader" valign="top" width="3%">
													<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
												</td>
												<td align="left" class="tblheader" valign="top" width="15%">
													<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.name" />
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.subject" />
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.issuer" />
												</td>
												<td align="left" class="tblheader" valign="top" width="16%">
													<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.expireddate" />
												</td>
												<td align="center" class="tblheader" valign="top" width="5%">
													<bean:message bundle="servermgrResources" key="servermgr.trustedcertificate.downloadcertificate" />
												</td> 
												<td align="center" class="tblheaderlastcol" valign="top" width="5%">
													<bean:message bundle="servermgrResources" key="servermgr.edit" />
												</td>												
											</tr>
							
								 <%	if(listTrustedCertificate!=null && listTrustedCertificate.size()>0){
									int i=0; 	
								 %>
									<logic:iterate id="trustedCertificateIdBean" name="trustedCertificateForm" property="listTrustedCertificate" type="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData">																																					   
									<%TrustedCertificateData trustedCertData= trustedCertificateIdBean;%>
										 <tr id="dataRowTrusted" name="dataRowTrusted" >
											<td align="center" class="tblfirstcol">
												<input type="checkbox" name="selectTrusted" value="<bean:write name="trustedCertificateIdBean" property="trustedCertificateId"/>" onclick="onOffHighlightedRowTrusted(<%=i++%>,this)"  />
											</td>
											<td align="center" class="tblrows">
												<%=((pageNo-1)*pageSize)+count%>
											</td>
											<td align="left" class="tblrows">
												<div style=" overflow: auto; word-wrap: break-word;">
													<a href="javascript:void(0)" onclick="viewTrusted(<bean:write name="trustedCertificateIdBean" property="trustedCertificateId"/>);">
														<bean:write name="trustedCertificateIdBean" property="trustedCertificateName" />
													</a>
												</div>
											</td>
											<td align="left" class="tblrows">
												<div style=" overflow: auto; word-wrap: break-word;">
												 	<% if (trustedCertData.getSubject() != null) { %> 
														<bean:define id="subjectData" name="trustedCertificateIdBean" property="subject" type="java.lang.String" />
														<% String[] subjectDetail = subjectData.split(",");
														for (String str : subjectDetail) {
															if (str.contains("CN=")) { %>
																<%=str.split("CN=")[1]%>
															<% } %>
														<% } %>
													<% } %> 
												</div>
											</td>
											<td align="left" class="tblrows">
												<div style=" overflow: auto; word-wrap: break-word;">
													<% if (trustedCertData.getIssuer() != null) { %> 
														<bean:define id="issuerData" name="trustedCertificateIdBean" property="issuer" type="java.lang.String" />
														<% String[] issuerDetail = issuerData.split(",");
														for (String str : issuerDetail) {
															if (str.contains("CN=")) { %>
																<%=str.split("CN=")[1]%>
															<% } %>
														<% } %>
													<% } %> 
												</div>
											</td>
											<td align="left" class="tblrows">
												<div style=" overflow: auto; word-wrap: break-word;">
													<bean:write name="trustedCertificateIdBean" property="validTo" />&nbsp;
												</div>
											</td>
											<td align="center" class="tblrows">
												<a href="javascript:void(0)" onclick="downloadTrustedCertificate(<bean:write name="trustedCertificateIdBean" property="trustedCertificateId"/>);">
													<img src="<%=basePath%>/images/download.jpg" alt="Download" border="0" height="20" width="20">
												</a>
											</td>			
											<td align="center" class="tblrows">
												<a href="javascript:void(0)" onclick="editTrusted(<bean:write name="trustedCertificateIdBean" property="trustedCertificateId"/>);">
													<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
												</a>
											</td>
										</tr> 
										<% count=count+1; %>
										<% iIndex += 1; %>
									</logic:iterate>

									<%	}else{%>
										<tr>
											<td align="center" class="tblfirstcol" colspan="8">
												<bean:message bundle="servermgrResources" key="servermgr.norecordsfound" />
											</td>
										</tr>
									<%	}%> 
									</table>
								</td>  
							</tr>
							<tr>
								<td class="btns-td" valign="middle">
									<input type="button" name="Create" value="   Create   " tabindex="3" onclick="javascript:location.href='<%=basePath%>/trustedCertificate.do?method=initCreate&name='+document.serverCertificateForm.serverCertificateName.value" class="light-btn"/>
									<html:button property="c_btnDelete" onclick="deleteTrustedCertificate()" value="   Delete   " styleClass="light-btn" /> 
									<html:button property="c_btnshowall" onclick="showall()" value="   Show All   " styleClass="light-btn" />
								</td>
								<td class="btns-td" align="right">
									<% if(totalPages >= 1) { %> 
										<% if(pageNo == 1){ %> 
											<a href="javascript:void(0)" onclick="navigateTrusted(<%=pageNo+1%>);">
												<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
											</a>
											<a href="javascript:void(0)" onclick="navigateTrusted(<%=totalPages+1%>);">
												<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
											</a>
										<% } %> 
										<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
											<%  if(pageNo-1 == 1){ %>
												<a href="javascript:void(0)" onclick="navigateTrusted(<%=1%>);">
													<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo-1%>);">
													<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
												</a> 
												<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo+1%>);">
													<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateTrusted(<%= totalPages+1%>);">
													<img src="<%=basePath%>/images/last.jpg" name="Image612"onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
												</a>
											<% } else if(pageNo == totalPages){ %> 
												<a href="javascript:void(0)" onclick="navigateTrusted(<%=1%>);">
													<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo-1%>);">
													<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
												</a> 
												<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo+1%>);">
													<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateTrusted(<%= totalPages+1%>);">
													<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
												</a>
											<% } else { %> 
												<a href="javascript:void(0)" onclick="navigateTrusted(<%=1%>);">
													<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo-1%>);">
													<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
												</a> 
												<a href="javascript:void(0)" onclick="navigateTrusted(<%= pageNo+1%>);">
													<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateTrusted(<%= totalPages+1%>);"">
													<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
												</a>
											<% } %> 
										<% } %> 
										<% if(pageNo == totalPages+1) { %> 
											<a href="javascript:void(0)" onclick="navigateTrusted(<%=1%>);">
												<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											</a>
											<a href="javascript:void(0)" onclick="navigateTrusted(<%=pageNo-1%>);">
												<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
											</a> 
										<% } %> 
									<% } %>
								</td>
							</tr> 	 
						</table>						
					</td> 				 
				</tr> 
			 </html:form>
 
