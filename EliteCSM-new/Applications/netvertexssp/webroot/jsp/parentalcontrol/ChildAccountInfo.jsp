<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.SubscriberProfileData"%>
<%@	page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountInfoForm"%>
<%@	page import="com.elitecore.netvertexsm.ws.xsd.QuotaUsageData"%>
<%@ page import="com.elitecore.ssp.util.EliteUtility"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<head>
   <title>Child Account Information</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
</head>
<%	
	SubscriberProfileData childObj=(SubscriberProfileData)request.getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);						
	ChildAccountInfoForm childAccountInfoForm = (ChildAccountInfoForm) request.getSession().getAttribute(RequestAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM);
	long[] allowedUsageInfo= null;		
	if(childAccountInfoForm!=null){		
		allowedUsageInfo =childAccountInfoForm.getAllowedUsageInfo();				
	}				
 %>  		
<table width="70%"  cellpadding="0" cellspacing="0" border="0" >
	<% if(allowedUsageInfo!=null && allowedUsageInfo!=null && childObj!=null){%>
	 <tr>   		
   		<td  class="titlebold" colspan="2" >&nbsp;Child Account Information : <%=childObj.getUserName()%> </td>   		
	</tr>
	<tr>
		<td width="50%"  align="left"class="tblfirstcol">Account Status</td>
		<td width="50%" align="left" class="tblrows"><%=(childObj.getCustomerStatus()!=null?childObj.getCustomerStatus():"")%></td>		
	</tr>
	<tr>
		<td width="50%" align="left" class="tblfirstcol"><bean:message key="home.usedquotatotal"/>&nbsp;&nbsp;(Used / Total)</td>
		<td width="50%" align="left" class="tblrows">
			<div style="width:200px;background-color:black;float: left;color:white;height: 20px">
				<div style="background-color:orange;width:<%=(((allowedUsageInfo[5]*100)/allowedUsageInfo[2])*2)%>px;float: left;text-align: center;height: 20px;"></div>
				<div style="position:absolute;;width:200px;text-align:center; color:white;height: 20px"><%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfo[5])%> / <%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfo[2]) %></div>				
			</div>			
		</td>												
	</tr>
	<tr>
		<td width="50%" align="left" class="tblfirstcol" ><bean:message key="home.usedsessiontime"/></td>
		<td width="50%" align="left" class="tblrows"><%=EliteUtility.convertToHourMinuteSecond(allowedUsageInfo[4])%> Hrs</td>		
	</tr>				
	<tr>
		<td width="50%" align="left" class="tblfirstcol" >Current Plan</td>
		<td width="50%" align="left" class="tblrows"><%=childObj.getSubscriberPackage()%></td>		
	</tr>		
	<%}else{%>
	<tr>   		
   		<td  class="titlebold" colspan="2" >&nbsp;Child Account Information</td>   		
	</tr>
	<tr>
		<td align="center" class="tblrows" colspan="2">No Records Found.</td>				
	</tr>
	<%}%>
</table>	
