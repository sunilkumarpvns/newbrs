<%@ page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@	page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountInfoForm"%>
<%@ page import="com.elitecore.ssp.util.EliteUtility"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" 	prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" 	prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" 	prefix="logic" %>

<head>
   <title><bean:message key="childaccount.info"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
 
<body>
<html:form action="/deptWiseUsageInfo" method="post" styleId="childForm">

<div class="border" >
<div>
<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >	
	<tr>   		
   			<td colspan="2" >&nbsp;</td>   		
	</tr>
	<tr> 		  		   	
   		<td colspan="2" align="left" valign="top">   			
   			<table  cellpadding="0" cellspacing="0" border="0" >
   				<tr><td class="img-padding"><img class="large-img" src="<%=request.getContextPath()%>/images/noimage.jpg" /></td><td valign="bottom" class="name" style=""><%=request.getParameter("departmentName")%></td></tr>
   				<tr><td colspan="2" class="black-bg" height="10"></td></tr>
   			</table>
   		</td>   		
	</tr>
	<tr>   		
   			<td colspan="2" >&nbsp;</td>   		
	</tr>
	<tr class="table-org" >   		
   		<td class="table-org-column" align="left" colspan="2"><%=request.getParameter("departmentName")%> department total Usage Information</td>    		
	</tr>	
	<tr class="table-gray">
		<td class="table-column" align="left" width="200px">Total Subscribers</td>
		<td class="table-column" align="left"> <%=request.getAttribute(RequestAttributeKeyConstant.DEPARTMENT_TOTAL_SUBSCRIBER)%></td>		
	</tr>	
	<tr class="table-white">
		<td class="table-column" align="left" width="200px">Total Usage</td>
		<td class="table-column" align="left"><%=EliteUtility.convertBytesToSuitableUnit((Long)request.getAttribute(RequestAttributeKeyConstant.DEPARTMENT_TOTAL_USAGE))%></td>		
	</tr>	 	 
</table>			
</div>	
</div>
</html:form>
</body>
