<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ViewCDRRecordsForm" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%> 






<%
    String basePath = request.getContextPath();
   	ViewCDRRecordsForm viewCDRRecordsForm = (ViewCDRRecordsForm)request.getAttribute("viewCDRRecordsForm");
    List lstCDRRecordsList = (List)request.getAttribute("lstCDRRecordsList");
    List lstCDRColumns = (List)request.getAttribute("lstCDRColumns");
%>  

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
		<td valign="top" class="box" colspan="8">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td colspan="8">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="38%" class="blue-text-bold" valign="bottom"></td>
								<td class="blue-text-bold" width="43%"><a href="#"><img src="<%=basePath%>/images/pdf.jpg" name="Image21" onMouseOver="MM_swapImage('Image21','','<%=basePath%>/images/pdf-hover.jpg',1)" border="0" alt="Save as PDF"></a><a href="#"><img src="<%=basePath%>/images/html.jpg" name="Image31" onMouseOver="MM_swapImage('Image31','','<%=basePath%>/images/html-hover.jpg',1)" border="0" alt="Save as HTML"></a></td>
							    <td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="3%" class="blue-text-bold">&nbsp;</td>
								<td width="4%" class="blue-text-bold">&nbsp;</td>
							</tr>
							<tr>
							    <td class="large-gap" height="2">&nbsp;</td>
							</tr>
							<tr>
								<td width="3%" class="labeltext">&nbsp;<b></td>
								<td width="3%" class="labeltext"></td>
								<td width="3%" class="labeltext">&nbsp;</td>
								<td width="3%" class="labeltext">&nbsp;</td>
								<td width="3%" class="labeltext">&nbsp;</td>
								<td width="3%" class="labeltext">&nbsp;</td>
								<td width="3%" class="labeltext">&nbsp;</td>
								<td width="3%" class="labeltext">&nbsp;</td>
				 			 </tr>
						</table>
					</td>
				</tr>		
				<tr>
		    		<td class="small-gap" height="2">&nbsp;</td>
		  		</tr>
		  		<tr>
		    		<td class="small-gap" height="2">&nbsp;</td>
		  		</tr>	
		  		<tr>
      				 <td  valign="middle" colspan="3" align="right">
				         <table cellpadding="0" cellspacing="0" border="0" width="97%" >
				           <tr>
				             <td class="small-gap" colspan="2">&nbsp;</td>
				           </tr>
				            <tr>
							  <% 
							  for(int i=0;i<lstCDRColumns.size();i++){ %>
							  	<td align="center" valign="top" width="5%" 	class="tblheader">																	
							  		<%=(String)lstCDRColumns.get(i)%>
								</td>
							  <% } %>  
						   </tr>
						<%
						  	for(int j=1;j<lstCDRRecordsList.size()  && j<100;j++) {
						  		List lstCDR = (List)lstCDRRecordsList.get(j);
						%>
						   <tr>
						       <% 
						       for(int k=0;k<lstCDRColumns.size();k++){ %>
						       <%if(k == 0){ %>
						       			<td align="center" valign="top" width="5%" 	class="tblfirstcol">																	
									  		<%=(String)lstCDR.get(k)%>
										</td>
						       
						       <% } else { %>
										<td align="center" valign="top" width="5%" 	class="tblrows">																	
									  		<%=(String)lstCDR.get(k)%>
										</td>
							   <%  
							   		}  
							   	  }    
							   %> 
					   </tr>      
					  <%
						}
					  %>
			           </table>
			         </td>
			     </tr>    
		  		<tr>
				    <td class="large-gap" height="2">&nbsp;</td>
			    </tr>
				<tr>
					<td valign="top">
						<table cellspacing="0" cellpadding="0" border="0" width="100%">
							<tr>
								 <td align="left" class="labeltext" valign="top" colspan="80" ></td>
								 <td align="left" class="labeltext" valign="top">
									  <input type="button" name="c_btnClose"  onclick="window.close()"  value="   Close   " class="light-btn" >
								 </td>
							</tr>
						</table>
					</td>
				</tr>
			    <tr> 
		            <td colspan="8" class="small-gap">&nbsp;</td>
		        </tr>
		     </table>
		</td>
	</tr>
</table>	