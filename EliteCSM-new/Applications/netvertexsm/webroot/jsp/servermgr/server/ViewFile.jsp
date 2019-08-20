<%@ include file="/jsp/core/includes/common/Header.jsp"%> 

<jsp:directive.page import="java.util.HashMap"/>





<script>
	function closeWindow() {
		window.close();
	}
</script>
<html:form action="/viewFile">
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
						      		<td valign="top" align="right"> 
						      		
						        		<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
								  					<tr>
														<td align="left" class="labeltext" valign="top" width="10%"><strong>File Id</strong></td>
														<td align="left" class="labeltext" valign="top" width="18%" ><bean:write property="fileId" name="viewFileForm"/></td>
												  	</tr>
												  	<tr>
														<td align="left" class="labeltext" valign="top" width="10%"><strong>File Name</strong></td>
														<td align="left" class="labeltext" valign="top" width="32%" ><bean:write property="fileName" name="viewFileForm"/></td>
												  	</tr>
												  	<tr>
														<td align="left" class="labeltext" valign="top" width="10%"><strong>Device Id</strong></td>
														<td align="left" class="labeltext" valign="top" width="32%" ><bean:write property="deviceId" name="viewFileForm" /></td>
												  	</tr>
												  	<tr>
														<td align="left" class="labeltext" valign="top" width="10%"><strong>Date and Time</strong></td>
														<td align="left" class="labeltext" valign="top" width="32%" ><bean:write property="dateAndTime" name="viewFileForm"/></td>
												  	</tr>
												  	<tr>
														<td align="left" class="labeltext" valign="top" width="10%"><strong>Status</strong></td>
														<td align="left" class="labeltext" valign="top" width="32%" ><bean:write property="status" name="viewFileForm"/></td>
												  	</tr>
												  	<logic:notEmpty property="reason" name="viewFileForm">
												  	<tr>
														<td align="left" class="labeltext" valign="top" width="10%"><strong>Reason</strong></td>
														<td align="left" class="labeltext" valign="top" width="32%" ><bean:write property="reason" name="viewFileForm"/></td>
												  	</tr>
												  	</logic:notEmpty>
												  	<tr>
														<td align="left" class="labeltext" valign="top" width="10%"><strong>Location</strong></td>
														<td align="left" class="labeltext" valign="top" width="32%" ><bean:write property="location" name="viewFileForm"/></td>
												  	</tr>
												  	<tr>
														<td align="left" class="labeltext" valign="top" width="10%">&nbsp;</td>
														<td align="left" class="labeltext" valign="top" width="32%" ><input type="button" name="close" value=" Close " onclick="closeWindow()"/></td>
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
