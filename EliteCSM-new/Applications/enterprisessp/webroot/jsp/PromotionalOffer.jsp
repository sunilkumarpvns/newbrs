

<%@page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnSubRequestData"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="java.util.Date"%>
<%@page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage"%>
<%@page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@	page import="com.elitecore.ssp.web.promotional.forms.PromotionalForm"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
 
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.elitecore.ssp.web.promotional.constants.PromotionalOfferConstants"%>

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

 	SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
	SubscriberProfile childObj = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
 	PromotionalForm promotionalOfferForm = (PromotionalForm)request.getAttribute(RequestAttributeKeyConstant.PROMOTIONAL_OFFER_FORM);
 	
%>

<script type="text/javascript">
	function subscribePromotionalOffer(addOnPackageID){
		var msg = '<bean:message key="parentalcontrol.prmoffer.subscribe.confirm.message"/>';
		var agree = confirm(msg);
		if(agree){
			document.getElementById("subscribepromotionform").addOnPackageId.value=addOnPackageID;
			$("#subscribepromotionform").submit();
		}	    		   			
	}
	
	function unSubscribePromotionalOffer(addOnPackageID){
		var msg = '<bean:message key="parentalcontrol.prmoffer.unsubscribe.confirm.message"/>';
		var agree = confirm(msg);
		if(agree){
			document.getElementById("unsubscribepromotionform").addOnPackageId.value=addOnPackageID;
			$("#unsubscribepromotionform").submit();
		}	    	
	}
	
	function onStatusSelection(forwardURL , selectedStatus){
		if(selectedStatus == "Select"){
			return;
		}
		window.location = forwardURL + "&SelectedStatus=" + selectedStatus ;
	}
	
</script>

<div class="border" >
	<div>
	<table width="97%" cellpadding="0" cellspacing="0" border="0" style="margin-right:10px;margin-left: 10px;margin-top: 10px;margin-bottom: 10px" >
	<jsp:useBean id="dateValue" class="java.util.Date" />
		<tr>   		
   			<td colspan="3" >&nbsp;</td>   		
		</tr>		
		<tr>   		
			<td align="left" valign="top" colspan="2">   			
	   			<table  cellpadding="0" cellspacing="0" border="0" >
	   				<tr>
	   					<td class="img-padding"><img class="large-img" src="<%=request.getContextPath()%>/images/noimage.jpg" /></td><td valign="bottom" class="name"><%=childObj.getUserName()%> </td>
	   				</tr>
	   				<tr><td colspan="2" class="black-bg" height="10"></td></tr>
	   			</table>
	   		</td>
	   		<%if(promotionalOfferForm.getPendingPromotionalReqData() != null && promotionalOfferForm.getPendingPromotionalReqData().size() > 0 && loggedInUser.getParentID() == null){%> 
		   		<td align="right" valign="top">
		   			<b><a style="color: #0074C5; font-size: 14px" href="<%=request.getContextPath()%>/initPendingPromotional.do?<%=SessionAttributeKeyConstant.SELECTED_LINK%>=prmoffer"><bean:message key="parentalcontrol.prmoffer.pendingpromorequest"/></a></b>
		   		</td>
	   		<%}%>	   		 	
		</tr>
	   	<tr>   		
   			<td colspan="3" >&nbsp;</td>   		
		</tr>
		<%if(childObj.getParentID()!=null){%>
		<tr class="table-org" height="30px"  >   		
   			<td class="table-org-column" colspan="3"><bean:message key="parentalcontrol.prmoffer.pendingpromorequest"/></td>    		
		</tr>
		
		<tr>   		
   			<td colspan="3" class="smallgap">&nbsp;</td>   		
		</tr>
		
		<tr>   		
   			<td colspan="3" >
   				<table width="100%" cellpadding="0" cellspacing="0" border="0">
   				<%if(promotionalOfferForm.getPendingPromotionalReqData() != null && promotionalOfferForm.getPendingPromotionalReqData().size()>0){%>	
				<%int i=0;%>									
					<tr height="20px">
						<td class="table-org-column" width="150px">
							<bean:message key="parentalcontrol.prmoffer.addonname"/>
						</td>						
						<td class="table-org-column" style="text-align: center;" >
							<bean:message key="parentalcontrol.prmoffer.submitdate"/>
						</td>						
						<td class="table-org-column" style="text-align: center;" >
							<bean:message key="parentalcontrol.prmoffer.price"/>
						</td>
						<td class="table-org-column" style="text-align: center;" >
							<bean:message key="parentalcontrol.prmoffer.validity"/>
						</td>
						<td class="table-org-column" style="text-align: center;" >
							<bean:message key="parentalcontrol.prmoffer.offerenddate"/>
						</td>
					</tr>
						
						<logic:iterate id="requestAddonData" name="promotionalForm" property="pendingPromotionalReqData" type="AddOnSubRequestData" >
						<% AddOnPackage  addOn=promotionalOfferForm.getAddOnPackageById(requestAddonData.getAddOnID());%>
			   				<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
								<td valign="center" style="text-align: left: ;" class="table-column">
								<%=addOn.getAddOnPackageName()%>
									<%-- <bean:write name="requestAddonData" property="addOnPackageName" /> --%>&nbsp;
								</td>										   				
								<td valign="center" class="table-column" style="text-align: center;">
								<c:if test="${not empty requestAddonData.createTime and requestAddonData.createTime ne 0}">
								 <jsp:setProperty name="dateValue" property="time" value="${requestAddonData.createTime}"/>
									<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss"/>
								</c:if>
								</td>								
								<td valign="center" class="table-column" style="text-align: center;">
								   <%=addOn.getPrice()%>
								</td>			   				
								<td valign="center" class="table-column" style="text-align: center;">
									<%=addOn.getValidity()%>&nbsp;<%=addOn.getValidityPeriodUnit()%>
								</td>			   				
								<td valign="center" class="table-column" style="text-align: center;">
							   <%if(addOn.getOfferEndDate()!=null && addOn.getOfferEndDate()!=0){
							    Date date=new Date(addOn.getOfferEndDate());
							    out.print(EliteUtility.dateToString(date, "dd/MM/yyyy HH:mm:ss"));
								}else{
									out.print("-");	
								}%> 
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
		
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;<hr/></td>   		
		</tr>
		
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>
		
		<%}%>						
	  <tr class="table-org" height="30px">   		
   			<td class="table-org-column" align="left" colspan="3"><bean:message key="parentalcontrol.prmoffer.availablepromo"/></td>    		
		</tr>
		
		<tr>   		
   			<td colspan="6" class="smallgap" >&nbsp;</td>   		
		</tr>
		
		<html:form action="/subscribePromotion" method="post" styleId="subscribepromotionform">
		<html:hidden property="addOnPackageId" value=""/>
		<html:hidden property="selectedLink" value="prmoffer"/>
		<html:hidden property="selectedPortal" value="enterprise"/>
		<tr>   		
   			<td colspan="6" >
   				<table width="100%" cellpadding="0" cellspacing="0" border="0">
   				<%if(promotionalOfferForm.getAvailableAddons() != null && promotionalOfferForm.getAvailableAddons().size()>0){%>	
				<%int i=0;%>									
						<%int count=0;%>
						<tr height="20px">
									<td class="table-org-column" align="left" width="170px">
										<bean:message key="parentalcontrol.prmoffer.addonname"/>
									</td>
									<td class="table-org-column" style="text-align: center;" width="80px">
										<bean:message key="parentalcontrol.prmoffer.price"/>
									</td>
									<td class="table-org-column" style="text-align: center;" width="90px">
										<bean:message key="parentalcontrol.prmoffer.validity"/>
									</td>
									<td class="table-org-column" style="text-align: center;" width="190px">
										<bean:message key="parentalcontrol.prmoffer.offerenddate"/>
									</td>
									<td class="table-org-column" style="text-align: center;" width="120px">
										&nbsp;
									</td>
								</tr>
						<logic:iterate id="addonData" name="promotionalForm" property="availableAddons" type="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.AddOnPackage">
			   				<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
									<td valign="center" style="text-align: left;" class="table-column">
										<bean:write name="addonData" property="addOnPackageName" />&nbsp;
									</td>
									<td valign="center" class="table-column" style="text-align: center;">
										<bean:write name="addonData" property="price" />&nbsp; 
									</td>			   				
									<td valign="center" class="table-column" style="text-align: center;">
										<bean:write name="addonData" property="validityPeriod" />&nbsp;<bean:write name="addonData" property="validityPeriodUnit" /> 
									</td>			   				
									<td valign="center" class="table-column" style="text-align: center;">
									<c:choose>
									<c:when test="${not empty addonData.offerEndDate and addonData.offerEndDate ne 0}">
								 		<jsp:setProperty name="dateValue" property="time" value="${addonData.offerEndDate}"/>
										<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss"/>
									</c:when>
									<c:otherwise>-</c:otherwise>
									</c:choose>
									</td>
									<td valign="center" class="table-column" style="text-align: center;">
										<input type="button" name="Subscribe" class="orange-btn" value="  Subscribe  " onclick="subscribePromotionalOffer('<bean:write name="addonData" property="addOnPackageID" />');">&nbsp;&nbsp;
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
						
						
		<tr><td colspan="6" class="smallgap" >&nbsp;<hr/></td></tr>
		
		<tr><td colspan="6" class="smallgap" >&nbsp;</td></tr>
		
		<tr>  
			<td colspan="6">
			<html:form action="/unsubscribePromotion" method="post" styleId="unsubscribepromotionform" >
			<html:hidden property="addOnPackageId" value=""/>
			<html:hidden property="selectedLink" value="prmoffer"/>
			<html:hidden property="selectedPortal" value="enterprise"/>
				<table width="100%" cellpadding="0" cellspacing="0" border="0" >
					<tr class="table-org" height="30px" >   		
	   					<td class="table-org-column" align="left" colspan="4"><bean:message key="parentalcontrol.prmoffer.promosubscription"/></td>
						<td colspan="1"  class="table-org-column" align="right" >View :</td>
						<td colspan="1"  class="table-org-column" align="right" >
	   						<select id="historyStatusSelect" onchange="onStatusSelection('<%= request.getContextPath()%>/promotional.do?action=search&method=post&SELECTED_LINK=prmoffer' , this.value)" >
	   						  <option selected="selected">Select</option>
							  <option><%=PromotionalOfferConstants.ACTIVE%></option>
							  <option><%=PromotionalOfferConstants.UNSUBSCRIBED%></option>
							  <option><%=PromotionalOfferConstants.EXPIRED%></option>
							  <option><%=PromotionalOfferConstants.APPROVAL_PENDING%></option>
							  <option><%=PromotionalOfferConstants.REJECTED%></option>
							  <option><%=PromotionalOfferConstants.APPROVED%></option>
							  <option><%=PromotionalOfferConstants.ALL%></option>
							</select>
							&nbsp;
	   					</td>
					</tr>
					<tr><td colspan="6" class="smallgap" >&nbsp;</td></tr>
							
						<%	if(promotionalOfferForm.getSubscriptionHistoriesList() != null && promotionalOfferForm.getSubscriptionHistoriesList().size()>0){
						%>
						
						<tr height="20px">
							<td class="table-org-column" align="left">Promotional Offer</td>
							<td class="table-org-column" align="center">Subscription Time</td> 
							<td class="table-org-column" align="center">Offer Start Date</td>
							<td class="table-org-column" align="center">Offer End Date</td>
							<td class="table-org-column" align="center">Status</th>
							<td class="table-org-column" align="center"></td>
						</tr>
						<tr>
						<td>
							<%
								int i = 1;
													Integer currentPage = (Integer)request.getAttribute(RequestAttributeKeyConstant.CURRENT_PAGE);
													Integer totalRecords = promotionalOfferForm.getSubscriptionHistoriesList().size();
													int pageSize = 5;
													int likSize = 5;
													// for For each tag value for begin and end is one less then the index of list
													// Eg. to display (1 to 5) recored begin = 0 and end = 4 
													// Eg. to display (5 to 8) recored begin = 4 and end = 7
													int begin = (currentPage - 1) * pageSize;
													int remainingRecords = totalRecords - (currentPage * pageSize );
													int end = remainingRecords > 0 ? (begin + pageSize) -1 : ((begin + ( pageSize + remainingRecords)) - 1);
													
							%>
							<c:set var="subscribedStatus" value="<%=PromotionalOfferConstants.SUBSCRIBED%>"></c:set>
							<c:set var="activeStatus" value="<%=PromotionalOfferConstants.ACTIVE%>"></c:set>

				    		<c:forEach var="subscriptionHistoryData" begin="<%=begin%>" end="<%=end%>" items="<%=promotionalOfferForm.getSubscriptionHistoriesList()%>">
				    			<tr class="<%=(i%2==0)?"table-white":"table-gray"%>">
				    				<td style="text-align: left;" class="table-column">${subscriptionHistoryData.addOnPackageName}</td>
				    				<td style="text-align: center;" class="table-column">&nbsp;
				    				<c:choose>
				    			 	<c:when test="${not empty subscriptionHistoryData.subscriptionTime and subscriptionHistoryData.subscriptionTime ne 0}">
				    				<jsp:setProperty name="dateValue" property="time" value="${subscriptionHistoryData.subscriptionTime}" /> 
				    					 <fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />   
				    				</c:when>
				    				<c:otherwise>-</c:otherwise>
				    				</c:choose>
				    				</td>
				    				<td style="text-align: center;" class="table-column">
				    				<c:choose>
				    			 	<c:when test="${not empty subscriptionHistoryData.subscriptionStartTime and subscriptionHistoryData.subscriptionStartTime ne 0}">
				    				<jsp:setProperty name="dateValue" property="time" value="${subscriptionHistoryData.subscriptionStartTime}" /> 
				    					 <fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />   
				    				</c:when>
				    				<c:otherwise>-</c:otherwise>
				    				</c:choose>
				    				</td>
				    				<td style="text-align: center;" class="table-column">
				    				<c:if test="${not empty subscriptionHistoryData.subscriptionEndTime and subscriptionHistoryData.subscriptionEndTime ne 0}">				    					
			    				 	 <jsp:setProperty name="dateValue" property="time" value="${subscriptionHistoryData.subscriptionEndTime}" />
				    					<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm" />  
				    				</c:if>
				    				</td>
				    				<td style="text-align: center;" class="table-column">${subscriptionHistoryData.subscriptionStatusName}</td>
				    				
				    				<c:choose>
				    					<c:when test="${subscriptionHistoryData.subscriptionStatusName == subscribedStatus}">
      										<td style="text-align: center;width:120px;" class="table-column">
				    							<input type="button" width="100%"  value='Unsubscribe'  class="orange-btn"  onclick="unSubscribePromotionalOffer(${subscriptionHistoryData.addOnSubscriptionID})"/>
				    						</td>
      									</c:when>
      									<c:when test="${subscriptionHistoryData.subscriptionStatusName == activeStatus}">
      										<td style="text-align: center;width:120px;" class="table-column">
				    							<input type="button" width="100%"  value='Unsubscribe'  class="orange-btn"  onclick="unSubscribePromotionalOffer(${subscriptionHistoryData.addOnSubscriptionID})"/>
				    						</td>
      									</c:when>
      									<c:otherwise>
      										<td style="text-align: center;width:120px;" class="table-column"></td>
      									</c:otherwise>
				    				</c:choose>

				    			</tr>
				    			<% i++; %>
							</c:forEach>
						</td>
					</tr>
					
					<tr class="table-column">
						<td align="left" class="table-org-column" colspan="3">
							Displaying  <%=begin + 1%> to <%=end + 1%> of <%=totalRecords%> Subscriptions 
						</td>
						<td align="right" class="table-org-column" colspan="3">
							
							<%  
								double possiblePages = (double)totalRecords / (double)pageSize;

								// Rounding to upper level to possiblePages
								// Eg. 5.2 to 6
								possiblePages = (double)Math.ceil((double)possiblePages);
								
								int startLink = (int)Math.ceil((double)currentPage - (double)(((double)likSize-1D)/2D));
								int endLink = (int)Math.ceil((double)currentPage + (double)(likSize/2D));
								
								// while loading First Page
								if(currentPage == 1 ){
									startLink = 1;
									endLink = likSize;
								}
								
								
								// while loading last Page
								if(endLink > (int)possiblePages){
									endLink = (int)possiblePages;
								}
								
								
								// while loading last Page set Links for (startLink to LastPage)
								if(endLink == (int)possiblePages){
									startLink = (int)possiblePages - (int)likSize + 1;
								}
								
								// while loading first page
								if(startLink < 1 ){
									startLink =  1;
								}
								
								if(endLink - startLink == likSize){
									endLink = endLink -1 ;
								}
								
								// while loading first page
								if(startLink == 1){
									endLink = likSize;
								}
								
							%>
							
							<%if(currentPage != 1){%>
								<b><a style="text-decoration: none;" href="<%=request.getContextPath()%>/promotional.do?<%=RequestAttributeKeyConstant.REQUESTED_PAGE%>=<%=1%>&action=search&method=post&SELECTED_LINK=prmoffer">&lt;&lt;
								</a></b>
							<%}else{%>
								<span style="color: black;">&lt;&lt;</span>						
							<%}%>
							&nbsp;
							<%if(currentPage != 1){%>
								<b><a style="text-decoration: none;" href="<%=request.getContextPath()%>/promotional.do?<%=RequestAttributeKeyConstant.REQUESTED_PAGE%>=<%=currentPage-1%>&action=search&method=post&SELECTED_LINK=prmoffer">&lt;</a></b>
							<%}else{%>
								<span style="color: black;">&lt;</span>								
							<%}%>
							&nbsp;
							<% for(double pageNo = startLink ; pageNo <= endLink && pageNo <= possiblePages; pageNo++){
								if(pageNo != currentPage){%>
									<a   href="<%=request.getContextPath()%>/promotional.do?<%=RequestAttributeKeyConstant.REQUESTED_PAGE%>=<%=(int)pageNo%>&action=search&method=post&SELECTED_LINK=prmoffer"><%=(int)pageNo%></a>
								<%}else{%>
									<span style="color: black;"><%=currentPage%></span>
								<%}%>
								
								<%if(pageNo != endLink && pageNo!=possiblePages){%>
									<span style="color: black;">,</span>
								<%}%>		
										
							<%}%>
							&nbsp;
							<%if(remainingRecords > 0){%>
								<b><a style="text-decoration: none;" href="<%=request.getContextPath()%>/promotional.do?<%=RequestAttributeKeyConstant.REQUESTED_PAGE%>=<%=currentPage+1%>&action=search&method=post&SELECTED_LINK=prmoffer">&gt;</a></b>
							<%}else{%>
								<span style="color: black;">&gt;</span>						
							<%}%>
							&nbsp;
							<%if(currentPage != possiblePages){%>
								<b><a style="text-decoration: none;" href="<%=request.getContextPath()%>/promotional.do?<%=RequestAttributeKeyConstant.REQUESTED_PAGE%>=<%=(int)possiblePages%>&action=search&method=post&SELECTED_LINK=prmoffer">&gt;&gt;</a></b>
							<%}else{%>
								<span style="color: black;">&gt;&gt;</span>
							<%}%>
							
						</td>
					</tr>
					<%}else{%>
						<tr class="table-white">
							<td align="center" class="tblrows" colspan="6"><bean:message key="parentalcontrol.prmoffer.noSubscriptionFound"/></td>			
						</tr>
					<%}%>
				</table>
			</html:form>
			</td>
		</tr>
		<tr><td colspan="6" class="smallgap" >&nbsp;</td></tr>
	 </table>	
	</div>
	</div>
</body>