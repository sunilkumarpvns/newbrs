<%@ include file="/jsp/core/includes/common/Header.jsp"%> 

<jsp:directive.page import="com.elitecore.netvertexsm.util.constants.MBeanNameConstant"/>
<jsp:directive.page import="java.util.Date"/>
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


<html:form action="/viewBatchInfo">
<html:hidden styleId="serverId"  property="serverId"/>
<table cellpadding="0" cellspacing="0" border="0" width="828">	
			<tr> 
			    <td width="10">&nbsp;</td>
			    <td colspan="2"> 
			      <table width="100%" border="0" cellspacing="0" cellpadding="0">
			        <tr> 
			          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td>
			          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">Batch Detail</td>
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
													HashMap batchInfoMap = (HashMap)request.getAttribute("batchInfoMap");
													
													String batchId = (String)batchInfoMap.get(MBeanNameConstant.BATCH_ID);
													String status = (String)batchInfoMap.get(MBeanNameConstant.STATUS);
													String dateAndTime = ((Date)batchInfoMap.get(MBeanNameConstant.DATE_AND_TIME)).toString();
													String reason = (String)batchInfoMap.get(MBeanNameConstant.REASON);
													String userName = (String)batchInfoMap.get(MBeanNameConstant.USER_NAME);
													String noOfcdr = (String)batchInfoMap.get(MBeanNameConstant.NO_OF_CDR);

											
											 %>
											<table width="65%" border="0" cellspacing="0" cellpadding="0" align="left">
												<tr>
													<td class="tblheader" colspan="2">
														<strong>Basic Detail</strong>
													</td>
												</tr>
					
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Batch Id
													</td>
													<td align="left" class="tblrows" valign="top"  width="30%">
														<%=batchId%>
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
														Checked Out Date
													</td>
													<td align="left" class="tblrows" valign="top">
														<%=dateAndTime%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Reason
													</td>
													<td align="left" class="tblrows" valign="top">
														<%=reason%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														User Name
													</td>
													<td align="left" class="tblrows" valign="top">
														<%=userName%>
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Number Of CDR
													</td>
													<td align="left" class="tblrows" valign="top"  width="30%">
														<%=noOfcdr%>
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
														&nbsp;-&nbsp;<a href="">Undo Checkout</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">Check In</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">Re-process Updated CDR</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">Cancel Update</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">Download Batch Again</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">Batch History</a>
													</td>
												</tr>
												<tr class="grey-bkgd">
													<td class="sublinks">
														&nbsp;-&nbsp;<a href="">CDRs Detail</a>
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
														<strong>Batch History</strong>
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
														<strong>CDRs Detail</strong>
													</td>
												</tr>
					
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Distribution Status
													</td>
													<td align="left" class="tblrows" valign="top" width="30%">
														&nbsp;
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Checked Out
													</td>
													<td align="left" class="tblrows" valign="top">
														&nbsp;
													</td>
												</tr>
												<tr>
													<td align="left" class="tblfirstcol" valign="top" width="30%">
														Batch Id
													</td>
													<td align="left" class="tblrows" valign="top">
														&nbsp;
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
