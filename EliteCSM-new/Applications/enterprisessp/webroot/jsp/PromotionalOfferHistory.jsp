<%@ page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@	page import="com.elitecore.ssp.web.promotional.forms.PromotionalForm"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@ page import="com.elitecore.ssp.web.promotional.PromoSubscriptionData"%>
<%@ page import="com.elitecore.ssp.web.promotional.constants.PromotionalOfferConstants"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="nested" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<head>
   	<title></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<%
	SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT); 	
 	PromotionalForm promotionalOfferForm = (PromotionalForm)request.getAttribute(RequestAttributeKeyConstant.PROMOTIONAL_OFFER_FORM);
%>

<script type="text/javascript">
	
	function unSubscribePromotionalOffer(addOnPackageID){
		var msg = '<bean:message key="parentalcontrol.prmoffer.unsubscribe.confirm.message"/>';
		var agree = confirm(msg);
		if(agree){
			document.getElementById('unsubscribedFourmId').addOnPackageId.value=addOnPackageID;
			document.getElementById('unsubscribedFourmId').submit();	   			
		}	    	
	}
	
	function onStatusSelection(forwardURL,selectedStatus){
					
			if(selectedStatus == "Select"){
				return;
			}
			 document.getElementById('unsubscribedFourmId').selectedAddonStatus.value=selectedStatus;
			window.location = forwardURL + "&SelectedStatus=" + selectedStatus ;
	}
	
	$(document).ready(function () {onloadFunction();});
	
	
	function onloadFunction(){
		 var selection = '${SelectedStatus}';
		 var dd = document.getElementById('historyStatusSelect');
		 for (var i = 0; i < dd.options.length; i++) {
		     if (dd.options[i].text === selection) {
		         dd.selectedIndex = i;
		         break;
		     }
		 }
		 document.getElementById('unsubscribedFourmId').selectedAddonStatus.value = selection;
	};
		
	
</script>

<div class="border">
	<div>
	
	<table width="97%" cellpadding="0" cellspacing="0" border="0" style="margin-right:10px;margin-left: 10px;margin-top: 10px;margin-bottom: 10px" >
		<tr>   		
   			<td colspan="7" >&nbsp;</td>   		
		</tr>		
		<tr>   		
			<td align="left" valign="top" colspan="3">   			
	   			<table  cellpadding="0" cellspacing="0" border="0" >
	   				<tr><td class="img-padding"><img class="large-img" src="<%=request.getContextPath()%>/images/noimage.jpg" /></td><td valign="bottom" class="name"><%=childObj.getUserName()%> </td></tr>
	   				<tr><td colspan="2" class="black-bg" height="10"></td></tr>
	   			</table>
	   		</td>   		
		</tr>
		<tr><td colspan="7" class="smallgap" >&nbsp;</td></tr>
		<tr class="table-org" height="30px" >   		
	   		<td class="table-org-column" colspan="5"><bean:message key="parentalcontrol.prmoffer.promosubscription"/></td>
			<td class="table-org-column" style="height: 80%;" align="right" colspan="2">
			View :
			  <select id="historyStatusSelect" onchange="onStatusSelection('<%=request.getContextPath()%>/promotional.do?action=search&method=post&SELECTED_LINK=prmoffer' , this.value);" >
   				     <option>Select</option>
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
		<tr>
			<td colspan="7" class="smallgap" >&nbsp;</td>
		</tr>
		<tr>  
		
			<td colspan="7">
			<html:form action="/unsubscribePromotion" method="post" styleId="unsubscribedFourmId">
			<html:hidden property="addOnPackageId" value=""/>
			<html:hidden property="selectedAddonStatus" value=""/>
			<html:hidden property="selectedLink" value="prmoffer"/>
			<html:hidden property="selectedPortal" value="enterprise"/>
					
					<table width="100%" cellpadding="0" cellspacing="0" border="0" >
	
						<%
								if(promotionalOfferForm.getSubscriptionHistoriesList() != null && promotionalOfferForm.getSubscriptionHistoriesList().size()>0){
							%>
							
						<tr height="20px">
								<td style="text-align: left; width: 20%;" class="table-org-column" >Promotional Offer</td>
								<td style="text-align: center; width: 10%; padding-left: 0px;" class="table-org-column" >Subscription Time</td>
								<td style="text-align: center; width: 20%; padding-left: 0px;" class="table-org-column">Offer Start Date</td>
								<td style="text-align: center; width: 20%; padding-left: 0px; " class="table-org-column">Offer End Date</td>
								<td style="text-align: center; width: 15%; padding-left: 0px;" class="table-org-column">Status</td>
								<td style="text-align: center; width: 15%; padding-left: 0px;" class="table-org-column"></td>
						</tr>
						<tr>
							<td colspan="7">
							<div style="width: 100%; height: 100%;" width="100%" cellpadding="0" cellspacing="0" border="0">
							<table width="100%" cellpadding="0" cellspacing="0" border="0" >
							<c:set var="subscribedStatus" value="<%=PromotionalOfferConstants.SUBSCRIBED%>"></c:set>
							<c:set var="activeStatus" value="<%=PromotionalOfferConstants.ACTIVE%>"></c:set>
							<%int i = 1; %>
				    		<c:forEach var="subscriptionHistoryData" begin="0" end="<%=promotionalOfferForm.getSubscriptionHistoriesList().size()%>" items="<%=promotionalOfferForm.getSubscriptionHistoriesList()%>">
				    		<jsp:useBean id="dateValue" class="java.util.Date" />
				    			<tr class="<%=(i%2==0)?"table-white":"table-gray"%>">
				    				<td style="text-align: left; width: 20%; " class="table-column" >${subscriptionHistoryData.addOnPackageName}</td>
				    				<td style="text-align: center; width: 20%; padding-left: 0px;" class="table-column">
				    				<c:choose>
				    				<c:when test="${not empty subscriptionHistoryData.subscriptionTime and subscriptionHistoryData.subscriptionTime ne 0}">
				    					<jsp:setProperty name="dateValue" property="time" value="${subscriptionHistoryData.subscriptionTime}" />
				    					<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm" />
				    				</c:when>
				    				<c:otherwise>-</c:otherwise>	
				    				</c:choose>
				    				</td>
				    				<td style="text-align: center; width: 20%; padding-left: 0px;" class="table-column">
				    				<c:choose>
				    				<c:when test="${not empty subscriptionHistoryData.subscriptionStartTime and subscriptionHistoryData.subscriptionStartTime ne 0}">
				    					<jsp:setProperty name="dateValue" property="time" value="${subscriptionHistoryData.subscriptionStartTime}" />
				    					<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm" />
				    				</c:when>
				    				<c:otherwise>-</c:otherwise>	
				    				</c:choose>
				    				</td>
				    				<td style="text-align: center; width: 20%; padding-left: 0px;" class="table-column">
				    					<jsp:setProperty name="dateValue" property="time" value="${subscriptionHistoryData.subscriptionEndTime}" />
				    					<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm" />
				    				</td>
				    				<td style="text-align: center; width: 15%; padding-left: 0px;" class="table-column">${subscriptionHistoryData.subscriptionStatusName}</td>
				    				<c:choose>
				    					<c:when test="${subscriptionHistoryData.subscriptionStatusName == subscribedStatus}">
      										<td style="text-align: center;width:15%; padding-left: 0px;" class="table-column" colspan="1">
				    							<input type="button" width="100%"  value='Unsubscribe'  class="orange-btn"  onclick="unSubscribePromotionalOffer(${subscriptionHistoryData.addOnSubscriptionID})"/>
				    						&nbsp;
				    						</td>
      									</c:when>
      									<c:when test="${subscriptionHistoryData.subscriptionStatusName == activeStatus}" >
      										<td style="text-align: center;width:15%; padding-left: 0px;" class="table-column" colspan="1">
				    							<input type="button" width="100%"  value='Unsubscribe'  class="orange-btn"  onclick="unSubscribePromotionalOffer(${subscriptionHistoryData.addOnSubscriptionID})"/>
				    						</td>&nbsp;
      									</c:when>
      									<c:otherwise>
      										<td style="width:15%; padding-left: 0px;" class="table-column" colspan="1">&nbsp;</td>
      									</c:otherwise>
				    				</c:choose>
				    			</tr>
				    			<% i++; %>
							</c:forEach>
					</table>
					</div>
					</td>
					</tr>
					
					<%}else{%>
							<tr class="table-white">
								<td align="center" class="tblrows" colspan="7"><bean:message key="parentalcontrol.prmoffer.noSubscriptionFound"/></td>				
							</tr>
					<%}%>
				</table>
			</html:form>
			</td>
		</tr>
		<tr>
			<td colspan="7" class="smallgap" >&nbsp;</td>
		</tr>
		<tr class="table-org-column">
				
				<td align="left" class="tblrows" colspan="7" >
					<b><a style="color: #0074C5; padding-left: 10px;" href="<%=request.getContextPath()%>/promotional.do?action=search&method=post&SELECTED_LINK=prmoffer">
					Back to Promotional Offer
					</a></b>
				</td>				
		</tr>
   		</table>
	</div>
	</div>
</body>