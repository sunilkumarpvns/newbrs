<%@ include file="/jsp/core/includes/common/Header.jsp"%> 

<jsp:directive.page import="com.elitecore.elitesm.util.constants.MBeanNameConstant"/>
<jsp:directive.page import="java.util.Date"/>
<jsp:directive.page import="java.util.HashMap"/>
<% 
	String basePath = request.getContextPath(); 
%>

<script>
	function closeWindow() {
		window.close();
	}
	
	function submitPage() {
		document.forms[0].checkAction.value = 'SearchSummary';
		document.forms[0].submit();
	}
	setTitle('Batch Detail');
</script>


<html:form action="/viewBatchInfo">
<html:hidden styleId="serverId"  property="serverId"/>
<table cellpadding="0" cellspacing="0" border="0" width="100%">	
			<tr> 
			    <td width="7" >&nbsp;</td>
      			<td width="821" colspan="2" >
			      <table width="100%" border="0" cellspacing="0" cellpadding="0">
			    <tr>
     				<td width="7" >&nbsp;</td>
      				<td width="100%" colspan="2" >
      				</td>
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
</table>
</html:form>
