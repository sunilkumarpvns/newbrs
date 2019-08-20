<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>







<script language="javascript" src="<%=request.getContextPath()%>/js/validation.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/cookie.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/popcalendar.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/openhelp.js"></script>
<%
String localBasePath = request.getContextPath(); 
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	 <tr> 
    	 <td valign="top" class="box" colspan="8">
     			  <table width="100%" border="0" cellspacing="0" cellpadding="0" >
				   	<tr> 
					      <td width="81%" background="<%=request.getContextPath() %>/images/popup-bkgd.jpg" valign="top">&nbsp; </td>
				          <td width="3%"><img src="<%=request.getContextPath() %>/images/popup-curve.jpg"></td>
					      <td background="<%=request.getContextPath() %>/images/popup-btn-bkgd.jpg" width="3%"><a href="#"><img src="<%=request.getContextPath() %>/images/refresh.jpg" name="Image1" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image1','','<%=localBasePath%>/images/refresh-hover.jpg',1)" border="0"></a></td>
					      <td background="<%=request.getContextPath() %>/images/popup-btn-bkgd.jpg" width="3%"><a href="#" onclick="window.print()"><img src="<%=request.getContextPath() %>/images/print.jpg" name="Image2" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image2','','<%=localBasePath%>/images/print-hover.jpg',1)" border="0" > </a></td>
					      <td background="<%=request.getContextPath() %>/images/popup-btn-bkgd.jpg" width="3%"><a href="#"><img src="<%=request.getContextPath() %>/images/aboutus.jpg" name="Image3" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image3','','<%=localBasePath%>/images/aboutus-hover.jpg',1)" border="0"></a></td>
					      <td background="<%=request.getContextPath() %>/images/popup-btn-bkgd.jpg" width="3%"><a href="#"><img src="<%=request.getContextPath() %>/images/help.jpg" name="Image4" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image4','','<%=localBasePath%>/images/help-hover.jpg',1)" border="0"></a></td>
					      <td background="<%=request.getContextPath() %>/images/popup-btn-bkgd.jpg" width="4%">&nbsp;</td>
      			   </tr>
      			   
      			   
      			    <tr> 
		 				  <td colspan="8"> 
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
			  						 <tr> 
			  
        									<td width="38%" class="blue-text-bold" valign="bottom">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message bundle="servermgrResources" key="servermgr.server.viewLogReport" /></td>
			  								<td class="blue-text-bold" width="43%"><a href="#"><img src="<%=request.getContextPath() %>/images/pdf.jpg" name="Image21" onMouseOver="MM_swapImage('Image21','','<%=localBasePath%>/images/pdf-hover.jpg',1)" border="0" alt="Save as PDF"></a><a href="#"><img src="<%=request.getContextPath() %>/images/html.jpg" name="Image31" onMouseOver="MM_swapImage('Image31','','<%=localBasePath%>/images/html-hover.jpg',1)" border="0" alt="Save as HTML"></a></td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="3%" class="blue-text-bold">&nbsp;</td>
											<td width="4%" class="blue-text-bold">&nbsp;</td>
									 </tr>
							    </table>
		 				  </td>
				   </tr>
      			   <tr><td colspan="8">&nbsp;&nbsp;&nbsp;</td></tr>
      			   
      			   
      			   <logic:equal name="errorCode" scope="request"  value="0">	
      			   
      				    <%
							String date = (String) request.getAttribute("date");
							Map resultMap1 = (HashMap) request.getAttribute("resultMap");
							Map totalMap = (HashMap) resultMap1.get("totalMap");
							Long totalSuccess = (Long) totalMap.get("Success");
							Long totalFail = (Long) totalMap.get("Failure");
							Long totalRequest = new Long(totalSuccess.longValue() + totalFail.longValue());
						%>
    			   <tr>
      				   	<td colspan="8">
							<table width="97%" align="right" border="0" cellspacing="0" cellpadding="0">
			  						 <tr> 
										<td class="tblheader-bold" colspan="4">
											<bean:message bundle="servermgrResources" key="servermgr.server.viewLogReport.tribandnumber.summary.title" />
										</td>
									</tr>
							 </table>
						</td>
				 </tr>
      			   
      			 <tr>
      				   	<td colspan="8">
							<table width="97%%" align="right" border="0" cellspacing="0" cellpadding="0">
			  						 <tr> 
										<td class="tblheader-bold" colspan="6">
											Total Attempts	
										</td>
									</tr>
									<tr>
											<td width="19%" class="tblfirstcol" valign="top">Total Request</td>
											<td width="10%" class="tblrows" valign="top"><%=totalRequest%></td>
											<td width="19%" class="tblrows" valign="top">Total Authenticated</td>
											<td width="10%" class="tblrows" valign="top"><%=totalSuccess%></td>
											<td width="19%" class="tblrows" valign="top">Total Rejected</td>
											<td width="10%" class="tblcol" valign="top"><%=totalFail%></td>
										</tr>
						    </table>
						</td>
				 </tr>  
      			 <tr><td colspan="8">&nbsp;&nbsp;&nbsp;</td></tr>
      			 
      			 
			<%Map containerMap = (HashMap) resultMap1.get("containerMap");
			Iterator itUserSet = containerMap.keySet().iterator();
		while(itUserSet.hasNext())
		{
			 String	strTriBandNumber = (String) itUserSet.next();
      	 %>		   
      			   
      			    <tr>
      				   	<td colspan="8">
							<table width="97%" align="right" border="0" cellspacing="0" cellpadding="0">
			  						 <tr> 
										<td class="tblheader-bold" colspan="6">
											Attempt done by '<%=strTriBandNumber %>'	
										</td>
									 </tr>
									<%
		         						Map resultMap = (HashMap) containerMap.get(strTriBandNumber);
	         							 Long totalRequestByUser = new Long(0);
    									 Long totalFailueByUser = new Long(0);
										 Set set = resultMap.keySet();
										 Iterator it = set.iterator();
										  while (it.hasNext()) {
											String key = (String) it.next();
											Long val = (Long) resultMap.get(key);
											totalRequestByUser = new Long(totalRequestByUser.longValue() + val.longValue());
												if(!key.equals("Success")) 
													totalFailueByUser= new Long(totalFailueByUser.longValue() + val.longValue());
												} 
											if (resultMap.size() > 0) {
									%>
									
									 <tr> 
											<td width="19%" class="tblfirstcol" valign="top">Total Request</td>
											<td width="10%" class="tblrows" valign="top"><%= totalRequestByUser %></td>
											<td width="19%" class="tblrows" valign="top">Total Authenticated</td>
											<td width="10%" class="tblrows" valign="top"><%=resultMap.get("Success")%></td>
											<td width="19%" class="tblrows" valign="top">Total Rejected</td>
											<td width="10%" class="tblcol" valign="top"><%= totalFailueByUser %></td>
										
									 </tr>
									 	<% if( resultMap.size() > 1){ %>
										<tr>
										  <td colspan="3">
												<table width="100%" align="c" border="0" cellspacing=0 cellpadding=0>
													<tr>
														<td class="tblheader-bold" colspan="3">Failure Report</td>
													</tr>
													<tr>
														<td width="10%" class="tblfirstcol" align="center" >No</td>
														<td width="56%" class="tblrows" align="center">Cause</td>
														<td width="20%" class="tblrows" align="center">Attempts</td>
													</tr>
													
													<%
									                String key = null;
									                Long val = null;
						            			    Iterator it1 = resultMap.keySet().iterator();
									                int i = 1;
						           				    while (it1.hasNext()) {
								                    key = (String) it1.next();
								                    if (!key.equals("Success")) {
							                        val = (Long) resultMap.get(key);
										            %>
													<tr>
														<td class="tblfirstcol" align="center">&nbsp;<%=i++%></td>
														<td class="tblrows">&nbsp;&nbsp;&nbsp;<%=key%></td>
														<td class="tblrows" align="center">&nbsp;<%=val%></td>
													</tr>
													<%		}
													  }%>
													<tr>
														<td colspan="2" class="tblfirstcol" align="right" >Total Failure&nbsp;&nbsp;&nbsp;</td>
														<td class="tblrows" align="center"><font color="#FF0000"><%=totalFailueByUser %></font></td>
													</tr>
												</table>
										  </td>
										</tr>  			
									<%} 	%>
												 	
									 	</td>
									 </tr>
									<tr><td>&nbsp;</td></tr>	
									<%}%>
						    </table>
						</td>
				 </tr>  
      			   
      		<%}%>	
      		   
      		<% if(containerMap.size() == 0){ %>						
				<tr>
					<td colspan="8">
						<table width="97%" align="right" border="0" cellspacing=0 cellpadding=0>
								<tr>
									<td class="tblheader-bold">Attempt Report</td>
								</tr>
								<tr>
									<td class="tblfirstcol">No Records Found....</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
						</table>
					</td>							
				</tr>
	    	<%}%>
			</logic:equal>
      			  
         	<logic:equal name="errorCode" scope="request"  value="-1">				    
				<tr>
					<td  colspan="5">&nbsp;</td>
				</tr>
				<tr>
					<td>
						<table align="right" width="97%" align="right" border="0" cellspacing=0 cellpadding=0 >
							<tr>
								<td class="blue-text-bold" colspan="3">
									<bean:message bundle="servermgrResources" key="servermgr.connectionfailure" />
									<br> <bean:message bundle="servermgrResources" key="servermgr.admininterfaceip" />
									:<bean:write name="netServerInstanceData" property="adminHost" />
									<br> <bean:message bundle="servermgrResources" key="servermgr.admininterfaceport" />
									: <bean:write name="netServerInstanceData" property="adminPort" />
									&nbsp;
								</td>
							</tr>
						</table>
					</td>
			</tr>
			<tr><td  colspan="5">&nbsp;</td></tr>
 			</logic:equal>
      </table>
  	</td>
 </tr>
 <tr> 
    <td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
    <td class="small-gap" width="1%"><img src="<%=request.getContextPath()%>/images/pbtm-line-end.jpg"></td>
  </tr>
</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>