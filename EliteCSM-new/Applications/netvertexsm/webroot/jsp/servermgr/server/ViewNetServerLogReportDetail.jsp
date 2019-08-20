<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Iterator"%>
<%
String localBasePath = request.getContextPath(); 
%>







<script language="javascript" src="<%=request.getContextPath()%>/js/validation.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/cookie.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/popcalendar.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/openhelp.js"></script>

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
				   <tr><td>&nbsp;&nbsp;&nbsp;</td></tr>
    			    <tr align="left"> 
		 				  <td colspan="8" > 
					<table width="100%" border="0" cellspacing="0" cellpadding="0" >
    					 <logic:equal name="errorCode" scope="request"  value="0">
      						<tr>
        					    <td colspan="2">
        							<table width="97%" align="right" border="0" cellspacing=0 cellpadding=0>
        							<tr>
										<td class="tblheader-bold" colspan="4">
											<bean:message bundle="servermgrResources" key="servermgr.server.viewLogReport.tribandnumber.detail.title" />
										</td>
									</tr>
        			<%
			           Map containerMap = (HashMap) request.getAttribute("resultMap");
			           if(containerMap.size() !=0)
			           { 
			            Iterator itUserSet = containerMap.keySet().iterator();
			            List containerList = null;
			            String strTriBandNumber = null;
			            Iterator itLineSet = null;
			            while (itUserSet.hasNext()) {
			            strTriBandNumber = (String) itUserSet.next();
					%>
			<tr>
				<td colspan="4" class="tblheader-bold">
					&nbsp;Login Attempts done by &nbsp;&nbsp;'<%=strTriBandNumber%></td>
				<td>
			</tr>
			<%
			                containerList = (ArrayList) containerMap.get(strTriBandNumber);
			                itLineSet = containerList.iterator();
			                while (itLineSet.hasNext()) {
			%>
			<tr>
			  <td colspan="4" class="tblcol"><%=itLineSet.next()%></td>
			</tr>
			<%	}	%>
		 	<%  }
		 	  	} else{
			%>
			<tr>
				<td colspan="4" class="tblcol">
						No Records Found... 
				</td>
			</tr>
			<% }%>
			<tr>
					</table>
          						</td>
      					   	</tr>
      				   	</logic:equal>			
      				   		
						<logic:equal name="errorCode" scope="request"  value="-1">				    
   						<tr>
							<td  colspan="5">&nbsp;</td>
						</tr>
						
						<tr>
							<td>
								<table align="right" width="97%" align="right" border="0" cellspacing=0 cellpadding=0 >
						
   						<tr>
							<td class="blue-text-bold" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.connectionfailure" />
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
	        <td colspan="8" class="small-gap">&nbsp;</td>
	    </tr>
     </table>	
	 </td>
	</tr>
	<tr> 
   	    <td bgcolor="#00477F" class="small-gap" width="99%">&nbsp;</td>
     	<td class="small-gap" width="1%"><img src="<%=request.getContextPath()%>/images/pbtm-line-end.jpg"></td>
	</tr>
</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>