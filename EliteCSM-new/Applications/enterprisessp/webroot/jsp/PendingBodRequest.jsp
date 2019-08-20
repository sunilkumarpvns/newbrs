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
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<%
 	SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT); 	
 	BodForm bodPackageForm = (BodForm)request.getAttribute(RequestAttributeKeyConstant.BOD_FORM);	
%>

<script type="text/javascript">

	function subscribeBodPackage(bodPackageID){			
 			document.getElementById("bodPackageId").value=bodPackageID; 			
	    	document.getElementById("subscribeForm").submit(); 	
	}	
	
	function unsubscribeBodPackage(bodPackageID){
		var msg = '<bean:message key="parentalcontrol.bod.unsubscribe.confirm.message"/>';
		var agree = confirm(msg);		
		if(agree){						
 			document.getElementById("bodId").value=bodPackageID; 			
	    	document.getElementById("unsubscribeForm").submit(); 				
			//document.forms[1].bodId.value=bodPackageID;
	    	//document.forms[1].submit();	
		}	    	
	}
	
</script>

<div class="border" >
<div>
<jsp:useBean id="dateValue" class="java.util.Date" />
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
		<tr class="table-org" >   		
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
					<tr height="20px">
						<td class="table-org-column" width="150px">
							<bean:message key="general.name"/>
						</td>						
						<td class="table-org-column" style="text-align: center;">
							<bean:message key="parentalcontrol.prmoffer.submitdate"/>
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
									<bean:write name="requestBodData" property="bodPackageName" />&nbsp;
								</td>										   				
								<td valign="center" class="table-column" style="text-align: center;">
									<jsp:setProperty name="dateValue" property="time" value="${requestBodData.bodSubscriptionTime}" />
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />								
									<%-- <bean:write name="requestBodData" property="bodSubscriptionTime" />&nbsp; --%>
								</td>								
		   				
								<td valign="center" class="table-column" style="text-align: center;">
									<jsp:setProperty name="dateValue" property="time" value="${requestBodData.bodStartTime}" />
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />								
								
<%-- 									<%
										java.sql.Timestamp startTimeTS = new java.sql.Timestamp(requestBodData.getBodStartTime());
										out.println(""+startTimeTS);
									%>
 --%>									<%-- <bean:write name="requestBodData" property="bodStartTime" />&nbsp; --%>
								</td>			   				
								<td valign="center" class="table-column" style="text-align: center;">
								
									<jsp:setProperty name="dateValue" property="time" value="${requestBodData.bodEndTime}" />
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />								
								
									<%-- <%
										java.sql.Timestamp endTimeTS = new java.sql.Timestamp(requestBodData.getBodEndTime());
										out.println(""+endTimeTS);
									%>							 --%>	
									<%-- <bean:write name="requestBodData" property="bodEndTime" />&nbsp; --%>
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
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;<hr/></td>   		
		</tr>		
		
		<tr>   		
   			<td colspan="3" class="smallgap">&nbsp;</td>   		
		</tr>
		<tr class="table-org" >   		
   			<td class="table-org-column" colspan="3"><bean:message key="parentalcontrol.bod.availablebodpackages"/>
   			</td>    		
		</tr>
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>				
		<html:form action="/subscribePackage" method="post" styleId="subscribeForm" >
		<html:hidden property="bodPackageId" styleId="bodPackageId" />		
		<html:hidden property="SELECTED_LINK" value="serviceactivation" styleId="SELECTED_LINK"/>
		<html:hidden property="selectedPortal" styleId="selectedPortal" value="enterprise"/>
		<tr>   		
   			<td colspan="3" >

   				<table width="100%" cellpadding="0" cellspacing="0" border="0">
   				<%if(bodPackageForm.getAvailableBodPackages() != null && bodPackageForm.getAvailableBodPackages().size() > 0){%>	
				<%	int i=0; %>									
					<tr height="20px">
						<td class="table-org-column" width="140px">
							<bean:message key="parentalcontrol.bod.name"/>
						</td>
						<td class="table-org-column" style="text-align: center;" width="120px">
							<bean:message key="parentalcontrol.bod.description"/>
						</td>
						 
						<td class="table-org-column" style="text-align: center;" width="120px">
							<!-- <bean:message key="parentalcontrol.bod.pricerate"/> -->
						</td>
						 						
						<td class="table-org-column" style="text-align: center;" width="120px">
							&nbsp;
						</td>
					</tr>
					<logic:iterate id="bodData" name="bodForm" property="availableBodPackages" type="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage">
					  
						<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
							<td valign="center" style="text-align: left: ;" class="table-column">
								<bean:write name="bodData" property="bodPackageName" />&nbsp;
							</td>
							<td valign="center" class="table-column" style="text-align: center;">
								<bean:write name="bodData" property="description" />&nbsp;
							</td>
							<td valign="center" class="table-column" style="text-align: center;">
								&nbsp;
							</td>
							<td valign="center" class="table-column" style="text-align: center;">
								<input type="button" name="Subscribe" class="orange-btn" value="  Subscribe  " onclick="subscribeBodPackage('<bean:write name="bodData" property="bodPackageID" />');">
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
   			<td colspan="3" class="smallgap" >&nbsp;<hr/></td>   		
		</tr>		
		
		<tr>   		
   			<td colspan="3" class="smallgap">&nbsp;</td>   		
		</tr>	
		<tr class="table-org" >   		
   			<td class="table-org-column" colspan="3"><bean:message key="parentalcontrol.bod.active.bodpackages"/></td>    		
		</tr>
		
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>
		
		<html:form action="/unsubscribeBoD" method="post" styleId="unsubscribeForm" >
		<html:hidden property="bodId"  styleId="bodId"  value=""/>
		<html:hidden property="selectedLink" value="serviceactivation"/>
		<html:hidden property="selectedPortal" styleId="selectedPortal" value="enterprise"/>			
		<tr>   		
   			<td colspan="3" >
   				<table width="100%" cellpadding="0" cellspacing="0" border="0">
   				<%if(bodPackageForm.getActiveBodPackages() != null && bodPackageForm.getActiveBodPackages().size()>0){%>	
				<%int i=0;%>									
					<tr height="20px">
						<td class="table-org-column" width="170px">
							<bean:message key="parentalcontrol.bod.active.subscriberpackage"/>
						</td>
						<td class="table-org-column" style="text-align: center;" width="120px">
							<bean:message key="parentalcontrol.bod.active.starttime"/>
						</td>
						<td class="table-org-column" style="text-align: center;" width="120px">
							<bean:message key="parentalcontrol.bod.active.endtime"/>
						</td>						
						<td class="table-org-column" style="text-align: center;" width="120px">
							&nbsp;
						</td>												
					</tr>
						<logic:iterate id="activeBoDData" name="bodForm" property="activeBodPackages" type="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData">							
			   				<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
								<td valign="center" style="text-align: left: ;" class="table-column">
									<bean:write name="activeBoDData" property="bodPackageName" />&nbsp;
								</td>
								<td valign="center" class="table-column" style="text-align: center;">
<%-- 									<bean:write name="activeBoDData" property="bodStartTime" />&nbsp; --%>
									
									<jsp:setProperty name="dateValue" property="time" value="${activeBoDData.bodStartTime}" />
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />								
									
								</td>			   				
								<td valign="center" class="table-column" style="text-align: center;">
									<%-- <bean:write name="activeBoDData" property="bodEndTime" />&nbsp; --%>
									
									<jsp:setProperty name="dateValue" property="time" value="${activeBoDData.bodEndTime}" />
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />								
									
								</td>			   												
								<td valign="center" class="table-column" style="text-align: center;">
									<input type="button" name="UnSubscribe" class="orange-btn" value="  UnSubscribe  " onclick="unsubscribeBodPackage('<bean:write name="activeBoDData" property="bodSubscriptionID" />');">
								</td>				   															   				
							</tr>
						</logic:iterate>
					<%}else{%>
						<tr class="table-white">
							<td align="center" class="tblrows" colspan="3"><bean:message key="general.norecordsfound"/></td>				
						</tr>
					<%}%>
   				</table>
   			</td>   		
		</tr>
		</html:form>
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;<hr/></td>   		
		</tr>		
		
		<tr>   		
   			<td colspan="3" class="smallgap">&nbsp;</td>   		
		</tr>	
		<tr class="table-org" >   		
   			<td class="table-org-column" colspan="3"><bean:message key="parentalcontrol.prmoffer.subscriptionhistory"/></td>    		
		</tr>		
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>
		
		<tr>   		
   			<td colspan="3" >
   				<table width="100%" cellpadding="0" cellspacing="0" border="0">
   				<%if(bodPackageForm.getBodSubscriptionHistories() != null && bodPackageForm.getBodSubscriptionHistories().size()>0){%>	
				<%int i=0;%>									
					<tr height="20px">
						<td class="table-org-column" width="140px">
							<bean:message key="parentalcontrol.bod.active.subscriberpackage"/>
						</td>
						<td class="table-org-column" style="text-align: center;">
							<bean:message key="parentalcontrol.bod.active.starttime"/>
						</td>
						<td class="table-org-column" style="text-align: center;">
							<bean:message key="parentalcontrol.bod.active.endtime"/>
						</td>																								
					</tr>
					<logic:iterate id="activeBoDData" name="bodForm" property="bodSubscriptionHistories" type="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData">
			   			<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
							<td valign="center" style="text-align: left: ;" class="table-column">
								<bean:write name="activeBoDData" property="bodPackageName" />&nbsp;
							</td>
							<td valign="center" class="table-column" style="text-align: center;">
								<%--<bean:write name="activeBoDData" property="bodStartTime" />&nbsp; --%>

								<jsp:setProperty name="dateValue" property="time" value="${activeBoDData.bodStartTime}" />
								<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />																								
								
							</td>			   				
							<td valign="center" class="table-column" style="text-align: center;">
								<%-- <bean:write name="activeBoDData" property="bodEndTime" />&nbsp; --%>
								
								<jsp:setProperty name="dateValue" property="time" value="${activeBoDData.bodEndTime}" />
								<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />																								
							</td>			   																								   															   		
						</tr>
					</logic:iterate>										
					<%}else{%>
						<tr class="table-white">
							<td align="center" class="tblrows" colspan="3"><bean:message key="general.norecordsfound"/></td>				
						</tr>
					<%}%>
   				</table>
   			</td>   		
		</tr>	
				<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;<hr/></td>   		
		</tr>		
		
		<tr>   		
   			<td colspan="3" class="smallgap">&nbsp;</td>   		
		</tr>							   			   		
	 </table>	
	</div>
	</div>
</body>