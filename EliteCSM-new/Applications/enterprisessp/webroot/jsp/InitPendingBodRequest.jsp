<%@page import="java.util.Map"%>
<%@ page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@page import="com.elitecore.ssp.web.bod.forms.BodForm"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage"%>
<%@ page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData"%>
<%@ page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="nested" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<head>
   	<title>Child Account Information</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%=request.getContextPath()%>/webroot/css/common.css" rel="stylesheet" type="text/css" />
</head>

<%
 	SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT); 	
 	BodForm bodPackageForm = (BodForm)request.getAttribute(RequestAttributeKeyConstant.BOD_FORM);
 	Map<String,SubscriberProfile> subscriberProfileDataMap = (Map<String,SubscriberProfile>)request.getSession().getAttribute(SessionAttributeKeyConstant.SUBSCRIBER_PROFILE_MAP);
%>

<script type="text/javascript"> </script>

<div class="border" >
<div>
	<jsp:useBean id="dateValue" class="java.util.Date" />
	<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >
		<tr>   		
   			<td colspan="3" style="font-size:2px" >&nbsp;</td>   		
		</tr>
				
		<tr class="table-org" height="22px">   		
   			<td class="table-org-column" colspan="3"><bean:message key="enterprise.bod.pendingbodrequest"/>
   			</td>    		
		</tr>
	   	<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>		
		<tr>   		
   			<td colspan="3" >
   				<table width="100%" cellpadding="0" cellspacing="0" border="0">
   				<%if(bodPackageForm.getPendingBodReqData() != null && bodPackageForm.getPendingBodReqData().size()>0){%>	
				<%int i=0;%>									
					<tr height="18px">
						<td class="table-org-column" width="80px">
							<bean:message key="parentalcontrol.prmoffer.childname"/>
						</td>						
						<td class="table-org-column" style="text-align: center;">
							<bean:message key="parentalcontrol.prmoffer.submitdate"/>
						</td>						
						<td class="table-org-column" style="text-align: center;" >							
						</td>
						<td class="table-org-column" style="text-align: center;" >
							<bean:message key="bod.startime"/>
						</td>
						<td class="table-org-column" style="text-align: center;" >
							<bean:message key="bod.endtime"/>
						</td>
					</tr>
						<logic:iterate id="requestBodData" name="bodForm" property="pendingBodReqData" type="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData">
			   				<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
								<td valign="center" style="text-align: left: ;" class="table-column">									
									<a href="<%=request.getContextPath()%>/viewPendingBod.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=serviceactivation&bodID=<bean:write name="requestBodData" property="bodSubscriptionID" />&bodStartTime=<bean:write name="requestBodData" property="bodStartTime" />&subscriberID=<%=childObj.getSubscriberID()%>">																				
										<%-- <%
											if(subscriberProfileDataMap != null && !subscriberProfileDataMap.isEmpty()){
												SubscriberProfile subscriberProfileData = subscriberProfileDataMap.get(requestBodData.getBodPackageID());
												if(subscriberProfileData != null && subscriberProfileData.getUserName() != null && subscriberProfileData.getUserName().length() > 0){
													out.write(subscriberProfileData.getUserName());
												}else{
										%>
													<bean:write name="requestBodData" property="bodSubscriptionID" />&nbsp;
										<%
												}
											}else{
										%> --%>
										<%=childObj.getUserName()%>
												<%-- <bean:write name="requestBodData" property="bodSubscriptionID" />&nbsp; --%>
										<%-- <%}%>&nbsp; --%>
									</a>								
								</td>										   				
								<td valign="center" class="table-column" style="text-align: center;">
									<%-- &nbsp;<bean:write name="requestBodData" property="createTime" /> --%>
									<jsp:setProperty name="dateValue" property="time" value="${requestBodData.bodSubscriptionTime}" />
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />									
								</td>								
								<td valign="center" class="table-column" style="text-align: center;">									
								</td>			   				
								<td valign="center" class="table-column" style="text-align: center;">
									&nbsp;
									<jsp:setProperty name="dateValue" property="time" value="${requestBodData.bodStartTime}" />
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />	
									<%-- <%
										java.sql.Timestamp startTimeTS = new java.sql.Timestamp(requestBodData.getBodStartTime());
										out.println(""+startTimeTS);
									%> --%>								
									<%-- <bean:write name="requestBodData" property="bodStartTime" />&nbsp; --%>
								</td>			   				
								<td valign="center" class="table-column" style="text-align: center;">
									<%-- <%
										java.sql.Timestamp endTimeTS = new java.sql.Timestamp(requestBodData.getBodEndTime());
										out.println(""+endTimeTS);
									%> --%>									
									<jsp:setProperty name="dateValue" property="time" value="${requestBodData.bodEndTime}" />
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />								
									
									&nbsp;<%-- <bean:write name="requestBodData" property="bodEndTime" />&nbsp; --%>
								</td>		   				
							</tr>
						</logic:iterate>  
					<%}else{%>
						<tr class="table-white">
							<td align="center" class="tblrows" colspan="4"><bean:message key="general.norecordsfound"/></td>				
						</tr>
					<%}%>
   				</table>
   			</td>   		
		</tr>													   			   		
	 </table>	
	</div>
	</div>
</body>