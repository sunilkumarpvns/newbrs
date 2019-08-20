<%@ page import="java.util.Map"%>
<%@ page import="com.elitecore.elitesm.web.rm.ippool.forms.IPPoolForm"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%                                         														   
     IPPoolForm ipPoolForm = (IPPoolForm)request.getAttribute("ipPoolForm");
	 String basePath = request.getContextPath();
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
	checkIPAddressByRange('<%=basePath%>/checkIPPool.do',pageNumber);
}

</script>

<table cellpadding="0" cellspacing="0" border="0" width="350" id="checkIpAddressTable" align="center">
	<tr>
       <td colspan="2" >
       	<html:form action="/viewIPPool"> 
       		<table width="100%">
       			<tr>
       				<td colspan="8">
       					<table width="100%">
       						<tr>
       							<td align="left" class="blue-txt" valign="middle" width="50%" >
									<%if(totalRecord == 0){ 
									  }else if(pageNo == totalPages+1) { %>
									  	[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
									<% } else if(pageNo == 1) { %> 
										[<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %> 
									<% } else { %> 
										[<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %> 
									<% } %>
								
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
					<td align="left" class="tblheader-bold" valign="top">&nbsp;
						<bean:message bundle="ippoolResources" key="ippool.serialnumber" />
					</td>
					<td align="left" class="tblheader-bold" valign="top" >&nbsp;
						<bean:message bundle="ippoolResources" key="ippool.ipaddress" />
					</td>
					<td align="left" class="tblheader-bold" valign="top" >&nbsp;
						<bean:message bundle="ippoolResources" key="ippool.numberofippool" />
					</td>
				</tr>
				<logic:iterate id="ipPoolCheckAddressMap" name="ipPoolCheckAddressMap">
				<tr>
						<td align="center" class="tblfirstcol">&nbsp;<%=++srNo%></td>
						<td align="left" class="tblrows">&nbsp;<bean:write name="ipPoolCheckAddressMap" property="key" /></td>
						<td align="left" class="tblrows">&nbsp;
							<a href="javascript:void(0);" onclick="viewIPPools('<bean:write name="ipPoolCheckAddressMap" property="key" />')"><bean:write name="ipPoolCheckAddressMap" property="value" /></a>
						</td>
				</tr>
				</logic:iterate>
				
				<tr>
       				<td colspan="8">
       					<table width="100%">
       						<tr>
       							<td align="left" class="blue-txt" valign="middle" width="50%" >
									<%if(totalRecord == 0){ 
									  }else if(pageNo == totalPages+1) { %>
									  	[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
									<% } else if(pageNo == 1) { %> 
										[<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %> 
									<% } else { %> 
										[<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %> 
									<% } %>
								
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


