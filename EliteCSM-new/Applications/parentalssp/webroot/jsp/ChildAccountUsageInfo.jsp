<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountUsageInfoForm"%>
<%@	page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountManageForm"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.UsageMeteringInfo"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.SubscriberProfileData"%>
<%@ page import="java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="nested" %>

<head>
   	<title>Child Account Information</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
</head>
 <%
 	SubscriberProfileData childObj=(SubscriberProfileData)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT); 	
 	ChildAccountUsageInfoForm childAccountUsageInfoForm = (ChildAccountUsageInfoForm)request.getAttribute(RequestAttributeKeyConstant.CHILD_ACCOUNT_USAGE_INFO_FORM);	
 	String subscriberName=childObj.getUserName();
 			if(subscriberName==null)
 				 	subscriberName=""; 			 	
 %>
<html:form action="/childAccountUsageInfo">
<html:hidden property="viewUsagePage" value="false" styleId="viewUsagePage"/>
<html:hidden property="selectedLink" value="usageinfo" styleId="selectedLink"/>
<div class="border">
	<div>
	<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >			
		<tr>   		
   			<td colspan="3" >&nbsp;</td>   		
		</tr>		
		<tr>   		
			<td align="left" valign="top" colspan="3">   			
   			<table  cellpadding="0" cellspacing="0" border="0" >
   				<tr><td class="img-padding"><img class="large-img" src="<%=request.getContextPath()%>/images/noimage.jpg" /></td><td valign="bottom" class="name"><%=childObj.getUserName()%></td></tr>
   				<tr><td colspan="2" class="black-bg" height="10"></td></tr>
   			</table>
   		</td>   		
		</tr>
	   	<tr>   		
   			<td colspan="3" >&nbsp;</td>   		
		</tr>
		
		<tr class="table-org" height="30px">   		
   			<td class="table-org-column" colspan="3"><bean:message key="parentalcontrol.usageinfo"/></td>    		
		</tr>
		
		<tr>   		
   			<td colspan="3" >&nbsp;</td>   		
		</tr>
		
		<tr class="table-white">	   				
	   		<td class="table-org-column" align="left" width="30%">
	   			<bean:message key="parentalcontrol.usageinfo.lastday"/>
	   		</td>
	   		<td class="table-column" align="right">
	   			<%if(childAccountUsageInfoForm.getLastDayTotalOctets() != null){%>
	   				<%=EliteUtility.convertBytesToSuitableUnit(childAccountUsageInfoForm.getLastDayTotalOctets())%>	   			
	   			<%}else{%>
	   				-
	   			<%}%>
	   		</td>
	   		<td class="table-column" align="left" style="padding-left: 20px">
	   			<%if(childAccountUsageInfoForm.getLastDayUsageTime() != null){%>
	   				<%=EliteUtility.convertToHourMinuteSecond(childAccountUsageInfoForm.getLastDayUsageTime())+" Hrs"%>	   			
	   			<%}%>
	   		</td>	   					
	   	</tr>
	   	<tr class="table-gray">	   				
	   		<td class="table-org-column" align="left" width="30%">
	   			<bean:message key="parentalcontrol.usageinfo.lastweek"/>
	   		</td>
	   		<td class="table-column" align="right">
	   			<%if(childAccountUsageInfoForm.getLastWeekTotalOctets() != null){%>
	   				<%=EliteUtility.convertBytesToSuitableUnit(childAccountUsageInfoForm.getLastWeekTotalOctets())%>
	   			<%}else{%>
	   				-
	   			<%}%>
	   		</td>
	   		<td class="table-column" align="left" style="padding-left: 20px">
	   			<%if(childAccountUsageInfoForm.getLastWeekUsageTime() != null){%>
	   				<%=EliteUtility.convertToHourMinuteSecond(childAccountUsageInfoForm.getLastWeekUsageTime())+" Hrs"%>	   			
	   			<%}%>
	   		</td>	   					
	   	</tr>
	   	<tr class="table-white">	   				
	   		<td class="table-org-column" align="left" width="30%">
	   			<bean:message key="parentalcontrol.usageinfo.lastmonth"/>
	   		</td>
	   		<td class="table-column" align="right">
	   			<%if(childAccountUsageInfoForm.getLastMonthTotalOctets() != null){%>
	   				<%=EliteUtility.convertBytesToSuitableUnit(childAccountUsageInfoForm.getLastMonthTotalOctets())%>
	   			<%}else{%>
	   				-
	   			<%}%>
	   		</td>
	   		<td class="table-column" align="left" style="padding-left: 20px">
	   			<%if(childAccountUsageInfoForm.getLastMonthUsageTime() != null){%>
	   				<%=EliteUtility.convertToHourMinuteSecond(childAccountUsageInfoForm.getLastMonthUsageTime())+" Hrs"%>	   			
	   			<%}%>
	   		</td>	   					
	   	</tr>
	   	
	   	<tr>   		
   			<td colspan="3" >&nbsp;</td>   		
		</tr>
		
		<tr class="table-org" height="30px">   		
   			<td class="table-org-column" colspan="3"><bean:message key="childaccount.usageinfoperservice"/></td>    		
		</tr>
		
		<tr>   		
   			<td colspan="3" >&nbsp;</td>   		
		</tr>
		
		<tr>
			<td colspan="3" align="left" valign="top">
				<table width="100%" cellpadding="0" cellspacing="0" border="0">
					<tr height="20px">
						<td class="multi-col-table" width="130px">
							<bean:message key="parentalcontrol.serviceperiod"/>
						</td>
						<td class="table-org-column" style="text-align: center;" width="110px" colspan="2">
							<bean:message key="parentalcontrol.usageinfo.lastday"/>
						</td>
						<td class="table-org-column" style="text-align: center;" width="110px" colspan="2">
							<bean:message key="parentalcontrol.usageinfo.lastweek"/>
						</td>
						<td class="table-org-column" style="text-align: center;" width="110px" colspan="2">
							<bean:message key="parentalcontrol.usageinfo.lastmonth"/>
						</td>
					</tr>
					<%if(childAccountUsageInfoForm.getServiceUsageInfoDataList() != null && childAccountUsageInfoForm.getServiceUsageInfoDataList().size()>0){%>	
					<%int i=0;%>									
						<logic:iterate id="serviceUsageInfoData" name="childAccountUsageInfoForm" property="serviceUsageInfoDataList" type="com.elitecore.ssp.web.parentalcontrol.ServiceUsageInfo">						
							<tr height="20px"   class="<%=(i++%2==0)?"table-gray":"table-white"%>" >
								<td valign="center" class="table-org-column">
									<bean:write name="serviceUsageInfoData" property="serviceName" />&nbsp;
								</td>
								<td valign="center" style="text-align: right;padding-left:10px">
									<%=EliteUtility.convertBytesToSuitableUnit(serviceUsageInfoData.getLastDayTotalOctets())%>
								</td>
								<td valign="center" style="text-align: center;">
									<%=EliteUtility.convertToHourMinuteSecond(serviceUsageInfoData.getLastDayUsageTime()) + " Hrs"%>
								</td>
								<td valign="center" style="text-align: right;padding-left:10px">
									<%=EliteUtility.convertBytesToSuitableUnit(serviceUsageInfoData.getLastWeekTotalOctets())%>
								</td>
								<td valign="center" style="text-align: center;">
									<%=EliteUtility.convertToHourMinuteSecond(serviceUsageInfoData.getLastWeekUsageTime()) + " Hrs"%>
								</td>
								<td valign="center" style="text-align: right;padding-left:10px">
									<%=EliteUtility.convertBytesToSuitableUnit(serviceUsageInfoData.getLastMonthTotalOctets())%>
								</td>
								<td valign="center" style="text-align: center;">
									<%=EliteUtility.convertToHourMinuteSecond(serviceUsageInfoData.getLastMonthUsageTime()) + " Hrs"%>
								</td>
							</tr>							
						</logic:iterate>
					<%}else{%>
						<tr class="table-white">
							<td align="center" class="tblrows" colspan="7"><bean:message key="general.norecordsfound"/></td>				
						</tr>
					<%}%>					
				</table>				
			</td>			
		</tr>					   			   							
	 </table>	
	</div>
	</div>
	</html:form>
</body>