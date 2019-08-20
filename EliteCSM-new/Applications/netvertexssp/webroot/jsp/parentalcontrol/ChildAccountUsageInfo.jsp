<%@	page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountManageForm"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.UsageMeteringInfo"%>
<%@ page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>

<head>
   	<title>Child Account Information</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
</head>

 <%
 	String serviceType=(String)request.getAttribute("serviceType");
 	String message=""; 
 	String subscriberName=((String)request.getSession().getAttribute("subscriberName"));
 			if(subscriberName==null)
 				 subscriberName="";
 %>
 <html:form action="/childAccountUsageInfo">
	<html:hidden property="viewUsagePage" value="false" styleId="viewUsagePage"/>
	<table width="70%"  cellpadding="0" cellspacing="0" border="0" >
	   	<tr>
	   		<td  class="tableheader"  colspan="2" >&nbsp;Child Account Usage Information : <%=subscriberName%> </td>   		
		</tr>
		<tr>
			<td width="50%"  align="left" class="tblfirstcol">Usage Information </td>
			<td width="50%" align="left" class="tblrows">
				<select name="usageInfoOf" id="usageInfoOf" onchange="this.form.submit()">
						<option value="">--Select--</option>
					<% if(serviceType!=null && serviceType.equalsIgnoreCase("DAILY")){ message="Last 24Hrs"; %>
						<option value="DAILY" selected >Last 24Hrs</option>
						<option value="WEEKLY" >Last Week</option>
						<option value="MONTHLY">Last Month</option>					
					<%}else if(serviceType!=null && serviceType.equalsIgnoreCase("MONTHLY")){ message="Last Month";%>
						<option value="DAILY">Last 24Hrs</option>
						<option value="WEEKLY" >Last Week</option>
						<option value="MONTHLY" selected >Last Month</option>
					<%}else if(serviceType!=null && serviceType.equalsIgnoreCase("WEEKLY")){ message="Last Week";%>
						<option value="DAILY" >Last 24Hrs</option>
						<option value="WEEKLY" selected >Last Week</option>
						<option value="MONTHLY">Last Month</option>					
					<%}else{%>
						<option value="DAILY" >Last 24Hrs</option>
						<option value="WEEKLY" >Last Week</option>
						<option value="MONTHLY">Last Month</option>										
					<%}%>					
				</select>
			</td>		
		</tr>	

	</table>
 
</html:form>
	<table width="70%"  cellpadding="0" cellspacing="0" border="0" >
		<%						 	
			ArrayList<String> serviceList=(ArrayList<String>) request.getAttribute("serviceList");
			if(serviceList!=null && serviceList.size()>0){
		%>			
	   			<tr>
	   				<td  class="titlebold" colspan="2" >&nbsp;<%=(message!=null?message:"")%></td>   		
				</tr>
				<tr class="tableheader">
						<td width="35%" align="left" >&nbsp;Service</td>
						<td width="35%" align="left" >&nbsp;Total Octets</td>		
				</tr>				
				<%
					for(String str:serviceList){
						String ar[]=str.split("-");
				%>
				<tr >
						<td width="35%" align="left" class="tblfirstcol"><%=ar[0]%></td>
						<td width="35%" align="left" class="tblrows"><%=ar[1]%></td>		
				</tr>
				<%}%>			
			<%	
		}else{
			%>
				<tr >
					<td align="center" class="tblfirstcol" colspan="2">No Records Found.</td>				
				</tr>
			<%			
		}%>
	</table>	
</body>
