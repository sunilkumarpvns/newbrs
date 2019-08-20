<%@ include file="/jsp/core/includes/common/Header.jsp"%> 

<jsp:directive.page import="java.util.HashMap"/>

<% 
	String basePath = request.getContextPath(); 
%>
<script>
	function closeWindow() {
		window.close();
	}
	setTitle('CDR File Information');	
</script>
<html:form action="/viewFile">
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
</table>			
</html:form>			
