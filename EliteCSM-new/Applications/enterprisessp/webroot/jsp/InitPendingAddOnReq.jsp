<%@page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage"%>
<%@page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubRequestData"%>
<%@ page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubscriptionData"%>
<%@page import="java.util.Map"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@	page import="com.elitecore.ssp.web.promotional.forms.PromotionalForm"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>


<%@ page import="java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="nested" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<head>
   	<title>Child Account Information</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<%

 	SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT); 	
 	PromotionalForm promotionalOfferForm = (PromotionalForm)request.getAttribute(RequestAttributeKeyConstant.PROMOTIONAL_OFFER_FORM);
 	Map<String,SubscriberProfile> subscriberProfileDataMap = (Map<String,SubscriberProfile>)request.getSession().getAttribute(SessionAttributeKeyConstant.SUBSCRIBER_PROFILE_MAP);
%>




<div class="border" >
	<div>
	<table width="97%" cellpadding="0" cellspacing="0" border="0" style="margin-right:10px;margin-left: 10px;margin-top: 10px;margin-bottom: 10px" >
		<tr>   		
   			<td colspan="3" >&nbsp;</td>   		
		</tr>
		
		<tr class="table-org" >   		
   			<td class="table-org-column" colspan="3"><bean:message key="parentalcontrol.prmoffer.pendingpromorequest"/></td>    		
		</tr>
		
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>
		
		<html:form action="/subscribePromotion" method="post" styleId="subscribepromotionform">
		<html:hidden property="addOnPackageId" value=""/>
		<html:hidden property="selectedLink" value="prmoffer"/>
		<jsp:useBean id="dateValue" class="java.util.Date" />
		<tr>   		
   			<td colspan="3" >
   				<table width="100%" cellpadding="0" cellspacing="0" border="0">
   				<%if(promotionalOfferForm.getPendingPromotionalReqData() != null && promotionalOfferForm.getPendingPromotionalReqData().size()>0){%>	
				<%int i=0;%>									
					<tr height="20px">
						<td class="table-org-column" style="text-align: left;" width="120px">
							<bean:message key="parentalcontrol.prmoffer.childname"/>
						</td>
						<td class="table-org-column" width="120px">
							<bean:message key="parentalcontrol.prmoffer.addonname"/>
						</td>						
						<td class="table-org-column" style="text-align: center;" width="180px">
							<bean:message key="parentalcontrol.prmoffer.createdate"/>
						</td>						
					</tr> 
						<logic:iterate id="requestAddonData" name="promotionalForm" property="pendingPromotionalReqData" type="AddOnSubRequestData">
			   				<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
								<td valign="center" class="table-column" style="text-align: left;">
								
										 <%
											if(subscriberProfileDataMap != null && !subscriberProfileDataMap.isEmpty()){
												SubscriberProfile subscriberProfileData = subscriberProfileDataMap.get(requestAddonData.getSubscriberIdentity());
												if(subscriberProfileData != null && subscriberProfileData.getUserName() != null && subscriberProfileData.getUserName().length() > 0){
													out.write(subscriberProfileData.getUserName());		
											     }else{
										%>
													<bean:write name="requestAddonData" property="subscriberID" />&nbsp;
										<%
												}
											}else{
										%>
												<bean:write name="requestAddonData" property="subscriberID" />&nbsp;
										<%}%>&nbsp;
								</td>											   				
								<td valign="center" style="text-align: left: ;" class="table-column">
								<a href="<%=request.getContextPath()%>/viewPendingPromotional.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=prmoffer&prmOfferID=<bean:write name="requestAddonData" property="addOnSubReqID" />">
								    ${ADDONMAP[requestAddonData.addOnID].addOnPackageName}
								</a>		
								</td>
								<td valign="center" class="table-column" style="text-align: center;">
								<c:if test="${not empty requestAddonData.createTime and requestAddonData.createTime ne 0}">
								 <jsp:setProperty name="dateValue" property="time" value="${requestAddonData.createTime}"/>
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss"/>
								</c:if>
									&nbsp;
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
		</html:form>	
						
		<tr>   		
   			<td colspan="3" >&nbsp;</td>   		
		</tr>
													   			   						
	 </table>	
	</div>
	</div>
</body>