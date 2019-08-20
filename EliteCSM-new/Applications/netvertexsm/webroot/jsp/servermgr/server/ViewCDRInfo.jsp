
<jsp:directive.page import="com.elitecore.netvertexsm.util.constants.MBeanNameConstant"/>
<jsp:directive.page import="java.util.Date"/><%@ include file="/jsp/core/includes/common/Header.jsp"%> 

<jsp:directive.page import="java.util.HashMap"/>






<script>
	function closeWindow() {
		window.close();
	}
	
	function submitPage() {
		document.forms[0].checkAction.value = 'SearchSummary';
		document.forms[0].submit();
	}
</script>


<html:form action="/viewCDRInfo">
<html:hidden property="serverId" />
<table cellpadding="0" cellspacing="0" border="0" width="828">	
			<tr> 
			    <td width="10">&nbsp;</td>
			    <td colspan="2"> 
			      <table width="100%" border="0" cellspacing="0" cellpadding="0">
			        <tr> 
			          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td>
			          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">CDR File Information</td>
			          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td>
			          <td width="633"><a href="#"><img src="<%=basePath%>/images/csv.jpg" name="Image1" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/csv-hover.jpg',1)" border="0" alt="Save as CSV"></a><a href="#"><img src="<%=basePath%>/images/pdf.jpg" name="Image2" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/pdf-hover.jpg',1)" border="0" alt="Save as PDF"></a><a href="#"><img src="<%=basePath%>/images/html.jpg" name="Image3" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/html-hover.jpg',1)" border="0" alt="Save as HTML"></a><a href="#"><img src="<%=basePath%>/images/filter.jpg" name="Image4" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/filter-hover.jpg',1)" border="0" alt="Apply Filter"></a></td>
			        </tr>
			        <tr> 
			          <td width="633" valign="bottom"><img src="<%=basePath%>/images/line.jpg"></td>
			        </tr>
			      </table>
			    </td>
		  </tr>
	  
		<tr > 
			 <td class="small-gap" width="7" >&nbsp;</td>
		</tr>
		
		<tr> 
                        <td width="10">&nbsp;</td> 
                        <td class="box" cellpadding="0" cellspacing="0" border="0" width="100%"> 

							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<TD>
											<%
													HashMap cdrInfoMap = (HashMap)request.getAttribute("cdrInfoMap");
													
													String cdrId = (String)cdrInfoMap.get(MBeanNameConstant.CDR_ID);
													String fileName = (String)cdrInfoMap.get(MBeanNameConstant.FILE_NAME);
													String status = (String)cdrInfoMap.get(MBeanNameConstant.STATUS);
													String deviceId = (String)cdrInfoMap.get(MBeanNameConstant.DEVICE_ID);
													String serviceInstance = (String)cdrInfoMap.get(MBeanNameConstant.SERVICE_INSTANCE);
													String reason = (String)cdrInfoMap.get(MBeanNameConstant.REASON);
													String dateAndTime = ((Date)cdrInfoMap.get(MBeanNameConstant.DATE_AND_TIME)).toString();
													
													String distStatus = (String)cdrInfoMap.get(MBeanNameConstant.DISTRIBUTION_STATUS);
													String checkedOut = (String)cdrInfoMap.get(MBeanNameConstant.CHECKEDOUT);
													String batchId = (String)cdrInfoMap.get(MBeanNameConstant.BATCH_ID);

											
											 %>
											<table width="65%" border="0" cellspacing="0" cellpadding="0" align="left">
												<tr>
													<td class="tblheader" colspan="2">
														<strong>Basic Detail</strong>
													</td>
												</tr>
					
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														CDR Id
													</td>
													<td align="left" class="tblrows" valign="top"  width="30%">
														<%=cdrId%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														File Name
													</td>
													<td align="left" class="tblrows" valign="top">
														<%=fileName%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Status
													</td>
													<td align="left" class="tblrows" valign="top">
														<%=status%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Device
													</td>
													<td align="left" class="tblrows" valign="top">
														<%=deviceId%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Service Instance
													</td>
													<td align="left" class="tblrows" valign="top">
														<%=serviceInstance%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Reason
													</td>
													<td align="left" class="tblrows" valign="top"  width="30%">
														<%=reason%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Date And Time
													</td>
													<td align="left" class="tblrows" valign="top"  width="30%">
														<%=dateAndTime%>
													</td>
												</tr>
											</table>
												
											<table width="35%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right">
												<tr class="grey-bkgd">
													<td class="labeltext">
														<strong>Basic Operation</strong>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">Edit</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">Detached From Batch</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">Mark For Reprocess</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">Checked Out</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">View Update History</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">View Batch History</a>
													</td>
												</tr>
												
											</table>		
									</TD>
								</tr>
								
								<tr>
									<td>
										&nbsp;
									</td>
								</tr>
				
								<tr>
									<td>
										<table width="65%" border="0" cellspacing="0" cellpadding="0" height="15%" align="left">
												<tr>
													<td class="tblheader" colspan="2">
														<strong>Unified Information</strong>
													</td>
												</tr>
					
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														A Party MDN
													</td>
													<td align="left" class="tblrows valign="top" width="30%">
														&nbsp;
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														B Party MDN
													</td>
													<td align="left" class="tblrows" valign="top">
														&nbsp;
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Call Duration
													</td>
													<td align="left" class="tblrows" valign="top">
														&nbsp;
													</td>
												</tr>
											</table>
									</td>
								</tr>
								
								<tr>
									<td>
										&nbsp;
									</td>
								</tr>
								
								
								
								<tr>
									<td>
										<table width="65%" border="0" cellspacing="0" cellpadding="0" height="15%" align="left">
												<tr>
													<td class="tblheader" colspan="2">
														<strong>Current Batch Detail</strong>
													</td>
												</tr>
					
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Distribution Status
													</td>
													<td align="left" class="tblrows" valign="top" width="30%">
														<%=distStatus%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Checked Out
													</td>
													<td align="left" class="tblrows" valign="top">
														<%=checkedOut%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Batch Id
													</td>
													<td align="left" class="tblrows" valign="top">
														<%=batchId%>
													</td>
												</tr>
												<tr>
													<td colspan="2" align="center">
														<input type="button" name="close" value=" Close " onclick="closeWindow()"/>
													</td>
												</tr>
					
											</table>
									</td>
								</tr>
						</table>
				</td>
			</tr>
			
	<tr> 
	    <td width="10">&nbsp;</td>
	    <td colspan="2" valign="top" align="right"> 
	      <table width="99%" border="0" cellspacing="0" cellpadding="0">
	        <tr> 
	          <td width="82%" valign="top"><img src="<%=basePath%>/images/btm-line.jpg"></td>
	          <td align="right" rowspan="2" valign="top"><img src="<%=basePath%>/images/btm-gradient.jpg" width="140" height="23"></td>
	        </tr>
	        <tr> 
	          <td width="82%" valign="top" align="right" class="small-text-grey">Copyright 
	            &copy; <a href="http://www.elitecore.com" target="_blank">Elitecore 
	            Technologies Pvt. Ltd.</a> </td>
	        </tr>
	      </table>
	    </td>
  </tr>
</table>
</html:form>
