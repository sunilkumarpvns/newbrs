<%@ page import="com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm"%>

<%                                         														   
     IPPoolForm ipPoolForm = (IPPoolForm)request.getAttribute("ipPoolForm");
    
     long pageNo = ipPoolForm.getPageNumber();
     long totalPages = ipPoolForm.getTotalPages();
     long totalRecord = ipPoolForm.getTotalRecords();
	 Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	 long  srNo   = (pageNo == 1)? 0 : ((pageNo-1)*pageSize);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     String strPageNumber = String.valueOf(pageNo); 
%>

<script>

function navigate(direction, pageNumber ){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].submit();
}

</script>


<table width="100%" border="0" cellspacing="0" cellpadding="0"  >
    <tr> 
      <td valign="top" align="right" height="15%" >
      	<html:form action="/viewIPPool"> 
      	<html:hidden name="ipPoolForm" property="ipPoolId" />
      	<html:hidden name="ipPoolForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNumber%>" />
		<html:hidden name="ipPoolForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
		<html:hidden name="ipPoolForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
											
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr>
          	<td colspan="2" >
          		<table width="100%">
          			<tr>
						<td class="tblheader-bold" width="100%" colspan="8">
							<bean:message bundle="ippoolResources" key="ippool.ippooldetails" />
						</td>
					</tr>	
					
					<tr>
						<td  class="blue-txt" valign="middle" width="50%">
							<%if(totalRecord == 0){ 
							  }else if(pageNo == totalPages+1) { %>
							  	[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
							<% } else if(pageNo == 1) { %> 
								[<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %> 
							<% } else { %> 
								[<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %> 
							<% } %>
						</td>
						<td class="btns-td" align="right" colspan="2">
							<% if(totalPages >= 1) { %> 
								<% if(pageNo == 1){ %> 
									<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
									<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0"/> 
								<% } %> 
								<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
									<%  if(pageNo-1 == 1){ %>
									<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
									<img src="<%=basePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
									<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
									<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
									<% } else if(pageNo == totalPages){ %> 
									<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
									<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
									<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
									<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
									<% } else { %> 
									<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
									<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
									<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
									<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
									<% } %> 
								<% } %> 
								<% if(pageNo == totalPages+1) { %> 
									<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%=1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
									<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
								<% } %> 
							<% } %>
						</td>
					</tr>
          		</table>
          	</td>	
          </tr>
          
          <tr>
			<td width="100%">
				<table width="100%" border="0" cellpadding="0" cellspacing="0" id="listTable">
					<tr>
						<td align="center" class="tblheader" valign="top" width="5%">
							<bean:message bundle="ippoolResources" key="ippool.serialnumber" />
						</td>
						<td align="left" class="tblheader" valign="top" width="15%">
							<bean:message bundle="ippoolResources" key="ippool.ipaddress" />
						</td>
						<td align="left" class="tblheader" valign="top" width="7%">
							<bean:message bundle="ippoolResources" key="ippool.assigned" />
						</td>
						<td align="left" class="tblheader" valign="top" width="7%">
							<bean:message bundle="ippoolResources" key="ippool.reserved" />
						</td>
						<td align="left" class="tblheader" valign="top" width="15%">
							<bean:message bundle="ippoolResources" key="ippool.nas.ipaddress" />
						</td>
						<td align="left" class="tblheader" valign="top" width="15%">
							<bean:message bundle="ippoolResources" key="ippool.callingstationid" />
						</td>
						<td align="left" class="tblheader" valign="top" width="15%">
							<bean:message bundle="ippoolResources" key="ippool.useridentity" />
						</td>
						<td align="left" class="tblheader" valign="top" width="15%">
							<bean:message bundle="ippoolResources" key="ippool.lastupdate" />
						</td>
					</tr>
			        <logic:notEmpty name="lstIPPoolDetail">
			          	<logic:iterate id="ipPool" name="lstIPPoolDetail" type="com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData" >
			          		<tr>
			          			<td align="center" class="tblfirstcol"><%=++srNo %>&nbsp;</td>
			          			<td align="left" class="tblrows"><bean:write name="ipPool" property="ipAddress" />&nbsp;</td>
			          			<td align="left" class="tblrows"><bean:write name="ipPool" property="assigned" />&nbsp;</td>
			          			<td align="left" class="tblrows"><bean:write name="ipPool" property="reserved" />&nbsp;</td>
			          			<td align="left" class="tblrows"><bean:write name="ipPool" property="nasIPAddress" />&nbsp;</td>
			          			<td align="left" class="tblrows"><bean:write name="ipPool" property="callingStationId" />&nbsp;</td>
			          			<td align="left" class="tblrows"><bean:write name="ipPool" property="userIdentity" />&nbsp;</td>
			          			<td align="left" class="tblrows"><%=EliteUtility.dateToString(ipPool.getLastUpdatedTime(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%>&nbsp;</td>
			          		</tr>
			          	</logic:iterate>
			          </logic:notEmpty>
			          <logic:empty name="lstIPPoolDetail">
			          	<tr>
					        <td colspan="8" class="tblfirstcol" align="center">No Record Found</td>
					    </tr>
			          </logic:empty>
				</table>	
			</td>          
          </tr>
           <tr>
          	<td colspan="2" height="20%">
          		<table width="100%">
          			<tr>
						<td class="btns-td" align="right" colspan="2">
							<% if(totalPages >= 1) { %> 
								<% if(pageNo == 1){ %> 
									<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0" />
									<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0"/> 
								<% } %> 
								<% if(pageNo>1 && pageNo!=totalPages+1) {%> 
									<%  if(pageNo-1 == 1){ %>
									<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
									<img src="<%=basePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
									<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
									<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
									<% } else if(pageNo == totalPages){ %> 
									<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
									<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
									<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
									<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
									<% } else { %> 
									<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
									<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
									<img src="<%=basePath%>/images/next.jpg" name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Next" border="0">
									<img src="<%=basePath%>/images/last.jpg" name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Last" border="0">
									<% } %> 
								<% } %> 
								<% if(pageNo == totalPages+1) { %> 
									<img src="<%=basePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%=1%>)" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="First" border="0">
									<img src="<%=basePath%>/images/previous.jpg" name="Image5" onclick="navigate('previous',<%=pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)" onmouseout="MM_swapImgRestore()" alt="Previous" border="0"> 
								<% } %> 
							<% } %>
						</td>
					</tr>
          		</table>
          	</td>
          </tr>		
          
         </table>
         </html:form>
       </td>
   </tr>
</table>
          











<%-- <%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.web.rm.ippool.forms.CreateIPPoolForm" %>





<% 
	String localBasePath = request.getContextPath();
%>

<script>
function remove(){
	document.forms[0].action.value = 'delete';
	document.forms[0].submit();
}
</script>
<%
	int count = 1;
	double recordsPerColumn = 0;
	double totalRecord = 0;
	List listIPPoolDetail = (List)request.getAttribute("listIPPoolDetail");
%>
<html:form action="/viewIPAddressList">
<html:hidden name="viewIPPoolAddressListForm" styleId="action" property="action"/>
<html:hidden name="viewIPPoolAddressListForm" styleId="ipPoolId" property="ipPoolId"/>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
	<bean:define id="IPPoolBean" name="ipPoolData" scope="request" type="com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolData"/> 
	<tr>
		<td align="right" class="labeltext" valign="top" class="box" > 
			<table cellpadding="0" cellspacing="0" border="0" width="100%" >
				<tr> 
		            <td class="tblheader-bold" colspan="8" height="20%"><bean:message bundle="ippoolResources" key="ippool.ippooldetails"/></td>
	            </tr>
				
					<% 
								totalRecord = listIPPoolDetail.size();
								recordsPerColumn = Math.ceil(totalRecord/5);	
					%>
					<% if(totalRecord<=20){ 
						int index=1;
					%>							
 							 <tr>
 								 <td valign="top" align="right" colspan="8">
	 								<table width="100%" cellpadding="0" cellspacing="0" border="0" align="right"> 
											<logic:iterate id="IPPoolDetailBean" name="listIPPoolDetail" scope="request" type="com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData">  
		 										<tr>
		 											<td align="left" class="tblfirstcol" width="5%" ><b><%=index%>.</b></td>
													<td align="left" class="tblrows" width="100%" >&nbsp;&nbsp;<bean:write name="IPPoolDetailBean" property="ipAddress"/></td>   
												</tr>
												<%index++;%>
											</logic:iterate>  
	 								</table>
 								</td>
 							</tr>
					<%} else {
						int index=1;%>
 							<tr>
 								<td valign="top">
		 							<table width="100%" cellpadding="0" cellspacing="0" border="0" align="left">
			
 										<logic:iterate id="IPPoolDetailBean" name="listIPPoolDetail" scope="request" type="com.elitecore.elitesm.datamanager.rm.ippool.data.IIPPoolDetailData">  
											<tr>
												<td align="left" class="tblfirstcol" ><b><%=index%>.</b></td> 
												<td align="left" class="tblrows" >
                                                <bean:write name="IPPoolDetailBean" property="ipAddress"/></td>   
											</tr>
											<% if ((count%recordsPerColumn)==0) { %>
												</table>
											</td>
				 							<td valign="top" >
					 							<table width="100%" cellpadding="0" cellspacing="0" border="0"> 
				
											<% } %>
											<% count=count+1; index++; %>
																			
 										</logic:iterate>  
									</table>
								</td>
 							</tr>
 					<% } %> 
 				<tr > 	
					<td class="labeltext-right" valign="top" colspan="8">&nbsp;
						 <CENTER><input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/viewIPPool.do?ippoolid=<bean:write name="viewIPPoolAddressListForm" property="ipPoolId"/>'"   value="Cancel" class="light-btn"></CENTER>
					</td>
        		</tr> 
			</table>
		</td>
	</tr>
</table>
		</td>
    </tr>
</table>
</html:form>           --%>