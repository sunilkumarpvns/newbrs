<%@page import="com.elitecore.netvertexsm.ws.cxfws.ssp.parental.SubscriberProfile"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@page import="com.elitecore.netvertexsm.ws.cxfws.ssp.parental.SubscriberProfile"%>
<%@	page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountInfoForm"%>
<%@	page import="com.elitecore.netvertexsm.ws.xsd.QuotaUsageData"%>
<%@ page import="com.elitecore.ssp.util.EliteUtility"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
   <title><bean:message key="childaccount.info"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<%	
	SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
	SubscriberProfile currentUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
	ChildAccountInfoForm childAccountInfoForm = (ChildAccountInfoForm) request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM);	
	long[] allowedUsageInfo= null;		
	String memberIs="";
	
	if(childAccountInfoForm!=null){		
		allowedUsageInfo =childAccountInfoForm.getAllowedUsageInfo();			
	}else{		
		allowedUsageInfo=(long[])request.getAttribute(RequestAttributeKeyConstant.ALLOWED_USAGE_INFO);
	}
	if(currentUser.equals(childObj)){
		memberIs="Parent ";
	}else{
		memberIs="Child ";
	}
	String statusColor="";
	if(childObj.getStatus()!=null && (childObj.getStatus().equalsIgnoreCase("A") || childObj.getStatus().equalsIgnoreCase("Active"))){
		statusColor = "green";
	}
 %>
<div class="border" >
<div>
<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >	
	<tr>   		
   			<td colspan="2" >&nbsp;</td>   		
	</tr>
	<tr> 		  		   	
   		<td colspan="2" align="left" valign="top">   			
   			<table  cellpadding="0" cellspacing="0" border="0" >
   				<tr><td class="img-padding"><img class="large-img" src="<%=request.getContextPath()%>/images/noimage.jpg" /></td><td valign="bottom" class="name" style=""><%=childObj.getUserName()%> </td></tr>
   				<tr><td colspan="2" class="black-bg" height="10"></td></tr>
   			</table>
   		</td>   		
	</tr>
	<tr>   		
   			<td colspan="2" >&nbsp;</td>   		
	</tr>
	<tr class="table-org" height="30px">   		
   		<td class="table-org-column" colspan="2"><%=memberIs%><bean:message key="childaccount.info"/></td>    		
	</tr>	
	<tr class="table-gray">
		<td class="table-column" align="left" width="200px"><bean:message key="childaccount.status"/></td>
		<%-- <td class="table-column" align="left"  ><%=(childObj.getAccountStatus()!=null?childObj.getAccountStatus():"Active")%></td> --%>
		<td class="table-column" align="left"> <font color="<%=statusColor%>"><b>  <%=(childObj.getStatus()!=null?childObj.getStatus():"-")%></b></font></td>		
	</tr>
	<% if(allowedUsageInfo!=null && allowedUsageInfo!=null && childObj!=null){%>	
	<tr class="table-white">
		<td class="table-column" align="left"><bean:message key="home.usedquotatotal"/>&nbsp;<bean:message key="childaccount.used_total"/></td>
		<td class="table-column" align="left" >
			<%if(allowedUsageInfo.length>5){								
				long divWidth = ((allowedUsageInfo[2]==0?0:(allowedUsageInfo[5]*100)/allowedUsageInfo[2])*2);
				if(divWidth>400){
					divWidth = 400;
				}
			%>
			<div class="quota-usage-control">
				<div style="background-color:orange;width:<%=divWidth%>px;float: left;text-align: center;height: 20px;">
					<div style="width:200px;text-align:center;font-weight: bold"><%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfo[5])%> / <%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfo[2]) %></div>	
				</div>									
			</div>
			<%}else{%>
			<div style="width:200px;float: left;height: 20px;">-</div>
			<%}%>			
		</td>												 
	</tr>
	<tr class="table-gray">
		<td class="table-column" align="left"><bean:message key="home.usedsessiontime"/></td>
		<td class="table-column" align="left" >
			<%
				if(allowedUsageInfo.length>4){
					out.println(EliteUtility.convertToHourMinuteSecond(allowedUsageInfo[4])+" Hrs");
				}else{
				%>-<%	
				}
			%>
		</td>		
	</tr>				
	<%}else{%>
	<tr class="table-white">
		<td align="center" class="tblrows" colspan="2"><bean:message key="parentalcontrol.norecords"/></td>				
	</tr>
	<%}%>
	<tr class="table-white">
		<td class="table-column" align="left"><bean:message key="childaccount.currentplan"/></td>
		<td class="table-column" align="left"><%=childObj.getSubscriberPackage()!=null?childObj.getSubscriberPackage():"-"%></td>					
	</tr>	
	<tr class="table-grey">   		
   			<td class="table-column" colspan="2" >&nbsp;</td>   		
	</tr>		
</table>
</div>	
</div>
