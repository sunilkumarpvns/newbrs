		<script type="text/javascript">
			function showallCrl(){
				var path = '<%=basePath%>/crlCertificate.do?method=showAll'; 
				window.open(path,'CrlCertificate','resizable=yes,scrollbars=yes'); 
			}  
			function navigateCrl(page){
			 	document.forms[0].action="crlCertificate.do?method=search&pageNo="+page;
				document.crlCertificateForm.submit();   
			}
			function deleteCrlCertificate(){
				var selectArray = document.getElementsByName('selectCrl');
				
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
					alert("Select at least one CRL for delete Operation")
					}else{
						var r=confirm("This will delete the selected CRL(s). Do you want to continue ?");
							if (r==true)
				  			{
								document.forms[1].action='<%=basePath%>/crlCertificate.do?method=deleteCertificate';
								document.forms[1].submit();
				  			}
					}
				}
				else{
					alert("No record Found for delete Operation ! ");
				} 
			}	
			
			function editCrl(crlCertificateId){
				location.href='<%=basePath%>/crlCertificate.do?method=initUpdate&crlCertificateId='+crlCertificateId; 
			}
			function downloadCrlCertificate(crlCertificateId){	  
				  location.href='<%=basePath%>/crlCertificate.do?method=downloadFile&crlCertificateId='+crlCertificateId;
			}
			function viewCrl(crlCertificateId){
			    location.href='<%=basePath%>/crlCertificate.do?method=view&crlCertificateId='+crlCertificateId; 
			} 
			
			function  checkAllCrl(parentCheckBox){
				 
					var dataRows = document.getElementsByName('dataRowCrl');
					try{
					 	if( parentCheckBox.checked == true) {
					 		var selectVars = document.getElementsByName('selectCrl');
						 	for (var i = 0; i < selectVars.length;i++){
								selectVars[i].checked = true ;
								dataRows[i].className='onHighlightRow';
						 	}
					    } else if (parentCheckBox.checked == false){
					 		var selectVars = document.getElementsByName('selectCrl');	    
							for (var i = 0; i < selectVars.length; i++){
								selectVars[i].checked = false ;
								dataRows[i].className='offHighlightRow';
							}
						}
					}catch(e){
						alert("Error : "+e);
					}
			}
			function onOffHighlightedRowCrl(i,checkBox){
				var dataRows = document.getElementsByName('dataRowCrl');
				if(checkBox.checked==true){
					dataRows[i].className='onHighlightRow';
				}else{
					dataRows[i].className='offHighlightRow';
				}
			}
			function validateSearchCrl(){	
				document.crlCertificateForm.submit();  
			}
		
</script>			
			
				<!--  Begin : search section for CRL certificate -->
				<%
						CrlCertificateForm crlCertificateForm = (CrlCertificateForm) request.getAttribute("crlCertificateForm");
						pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
						
						pageNo=crlCertificateForm.getPageNumber();
						totalPages=crlCertificateForm.getTotalPages();
						totalRecord=crlCertificateForm.getTotalRecords();
						count=1;
						iIndex =0;
						
						strPageNumber=String.valueOf(pageNo);
						strTotalPages=String.valueOf(totalPages);
						strTotalRecords=String.valueOf(totalRecord); 
						
						List listCrlCertificate = crlCertificateForm.getListCrlCertificate(); 
				%>				
				
				<html:form action="/crlCertificate.do?method=search" styleId="crlCertificateFormId">
				    <html:hidden name="crlCertificateForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>"/>
				 	<html:hidden name="crlCertificateForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages %>"/>
				 	<html:hidden name="crlCertificateForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords %>" />  
				
					<tr>
  						<td align="left" class="labeltext" colspan="5" valign="top">
							<table cellSpacing="0" cellPadding="0" width="100%" border="0">
								<tr>
								    <td class="table-header" style="border: none;" width="50%">
										<bean:message bundle="servermgrResources" key="servermgr.crlcertificate" />
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
										<input type="button" name="Create" value="   Create   " tabindex="3" onclick="javascript:location.href='<%=basePath%>/crlCertificate.do?method=initCreate&name='+document.serverCertificateForm.serverCertificateName.value" class="light-btn">
										<html:button property="c_btnDelete"  tabindex="4" value="   Delete   " styleClass="light-btn" onclick="deleteCrlCertificate();" /> 
										<html:button property="c_btnshowall" onclick="showallCrl()" tabindex="5" value="   Show All   " styleClass="light-btn" />
									</td> 
 									<td class="btns-td" align="right">
										<% if(totalPages >= 1) { %> 
											<% if(pageNo == 1){ %> 
												<a href="javascript:void(0)" onclick="navigateCrl(<%=pageNo+1%>);">
													<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateCrl(<%=totalPages+1%>);">
													<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
												</a>
											<% } %> 
											<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
												<%  if(pageNo-1 == 1){ %>
													<a href="javascript:void(0)" onclick="navigateCrl(<%=1%>);">
														<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo-1%>);">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
													</a> 
													<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo+1%>);">
														<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateCrl(<%= totalPages+1%>);">
														<img src="<%=basePath%>/images/last.jpg" name="Image612"onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
													</a>
												<% } else if(pageNo == totalPages){ %> 
													<a href="javascript:void(0)" onclick="navigateCrl(<%=1%>);">
														<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo-1%>);">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
													</a> 
													<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo+1%>);">
														<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateCrl(<%= totalPages+1%>);">
														<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
													</a>
												<% } else { %> 
													<a href="javascript:void(0)" onclick="navigateCrl(<%=1%>);">
														<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo-1%>);">
														<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
													</a> 
													<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo+1%>);">
														<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
													</a>
													<a href="javascript:void(0)" onclick="navigateCrl(<%= totalPages+1%>);"">
														<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
													</a>
												<% } %> 
											<% } %> 
											<% if(pageNo == totalPages+1) { %> 
												<a href="javascript:void(0)" onclick="navigateCrl(<%=1%>);">
													<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateCrl(<%=pageNo-1%>);">
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
													<input type="checkbox" name="toggleAll" value="checkbox" onclick="checkAllCrl(this)" />
												</td>
												<td align="center" class="tblheader" valign="top" width="3%">
													<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
												</td>
												<td align="left" class="tblheader" valign="top" width="15%">
													<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.name" />
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.issuer" />
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.serialno" />
												</td>																				
												<td align="left" class="tblheader" valign="top" width="16%">
													<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.lastupdate" />
												</td>													
												<td align="center" class="tblheader" valign="top" width="5%">
													<bean:message bundle="servermgrResources" key="servermgr.crlcertificate.downloadcertificate" />
												</td>												
												<td align="center" class="tblheaderlastcol" valign="top" width="5%">
													<bean:message bundle="servermgrResources" key="servermgr.edit" />
												</td>												
											</tr>
							
								 <%	if(listCrlCertificate!=null && listCrlCertificate.size()>0){
									 int i=0;
								 %>
								 
									<logic:iterate id="crlCertificateIdBean" name="crlCertificateForm" property="listCrlCertificate" type="com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.CrlCertificateData">
									<%CrlCertificateData crlCertData= crlCertificateIdBean;%>
										 <tr id="dataRowCrl" name="dataRowCrl">
											<td align="center" class="tblfirstcol">
												<input type="checkbox" name="selectCrl" value="<bean:write name="crlCertificateIdBean" property="crlCertificateId"/>" onclick="onOffHighlightedRowCrl(<%=i++%>,this)" />
											</td>
											<td align="center" class="tblrows">
												<%=((pageNo-1)*pageSize)+count%>
											</td>
											<td align="left" class="tblrows">
												<div style="width:200px; overflow: auto; word-wrap: break-word;">
													<a href="javascript:void(0)" onclick="viewCrl(<bean:write name="crlCertificateIdBean" property="crlCertificateId"/>);">
														<bean:write name="crlCertificateIdBean" property="crlCertificateName" />
													</a>
												</div>
											</td>	
											<td align="left" class="tblrows">
												<div style=" overflow: auto; word-wrap: break-word;">
													<% if (crlCertData.getIssuer() != null) { %> 
														<bean:define id="issuerData" name="crlCertificateIdBean" property="issuer" type="java.lang.String" />
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
													<bean:write name="crlCertificateIdBean" property="serialNo" />&nbsp;
												</div>
											</td>
											<td align="left" class="tblrows">
												<div style=" overflow: auto; word-wrap: break-word;">
													<bean:write name="crlCertificateIdBean" property="lastUpdate" />&nbsp;
												</div>
											</td>
											<td align="center" class="tblrows">
												<a href="javascript:void(0)" onclick="downloadCertificate(<bean:write name="crlCertificateIdBean" property="crlCertificateId"/>);">
													<img src="<%=basePath%>/images/download.jpg" alt="Download" border="0" height="20" width="20">
												</a>
											</td>		
											<td align="center" class="tblrows">
												<a href="javascript:void(0)" onclick="editCrl(<bean:write name="crlCertificateIdBean" property="crlCertificateId"/>);">
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
									<input type="button" name="Create" value="   Create   " tabindex="3" onclick="javascript:location.href='<%=basePath%>/crlCertificate.do?method=initCreate&name='+document.serverCertificateForm.serverCertificateName.value" class="light-btn">
									<html:button property="c_btnDelete" onclick="deleteCrlCertificate()" value="   Delete   " styleClass="light-btn" /> 
									<html:button property="c_btnshowall" onclick="showallCrl()" value="   Show All   " styleClass="light-btn" />
								</td>
								<td class="btns-td" align="right">
									<% if(totalPages >= 1) { %> 
										<% if(pageNo == 1){ %> 
											<a href="javascript:void(0)" onclick="navigateCrl(<%=pageNo+1%>);">
												<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
											</a>
											<a href="javascript:void(0)" onclick="navigateCrl(<%=totalPages+1%>);">
												<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
											</a>
										<% } %> 
										<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
											<%  if(pageNo-1 == 1){ %>
												<a href="javascript:void(0)" onclick="navigateCrl(<%=1%>);">
													<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo-1%>);">
													<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
												</a> 
												<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo+1%>);">
													<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateCrl(<%= totalPages+1%>);">
													<img src="<%=basePath%>/images/last.jpg" name="Image612"onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
												</a>
											<% } else if(pageNo == totalPages){ %> 
												<a href="javascript:void(0)" onclick="navigateCrl(<%=1%>);">
													<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo-1%>);">
													<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
												</a> 
												<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo+1%>);">
													<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateCrl(<%= totalPages+1%>);">
													<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
												</a>
											<% } else { %> 
												<a href="javascript:void(0)" onclick="navigateCrl(<%=1%>);">
													<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo-1%>);">
													<img src="<%=basePath%>/images/previous.jpg" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0">
												</a> 
												<a href="javascript:void(0)" onclick="navigateCrl(<%= pageNo+1%>);">
													<img src="<%=basePath%>/images/next.jpg" name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
												</a>
												<a href="javascript:void(0)" onclick="navigateCrl(<%= totalPages+1%>);"">
													<img src="<%=basePath%>/images/last.jpg" name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
												</a>
											<% } %> 
										<% } %> 
										<% if(pageNo == totalPages+1) { %> 
											<a href="javascript:void(0)" onclick="navigateCrl(<%=1%>);">
												<img src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
											</a>
											<a href="javascript:void(0)" onclick="navigateCrl(<%=pageNo-1%>);">
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
