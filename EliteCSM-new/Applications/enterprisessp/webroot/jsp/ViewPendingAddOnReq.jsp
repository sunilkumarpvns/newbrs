<%@page import="java.util.Date"%>
<%@page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>

<%@page import="com.elitecore.ssp.web.promotional.forms.PromotionalForm"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
   <title><bean:message key="promotional.info"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<%	
	SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
	PromotionalForm promotionalForm = (PromotionalForm) request.getAttribute(RequestAttributeKeyConstant.PROMOTIONAL_OFFER_FORM);

%>

<script type="text/javascript">
	function approvePromotionalOffer(addOnPackageID,childID){
		var msg = '<bean:message key="enterprise.promotion.subscribe.approve.message"/>';
		var agree = confirm(msg);
		if(agree){
			document.getElementById("pendingPrmOfferForm").addOnPackageId.value=addOnPackageID;
			document.getElementById("pendingPrmOfferForm").childID.value=childID;
			$("#pendingPrmOfferForm").submit();
		}	    		   			
	}
	
	function rejectPromotionalOffer(addOnPackageID,childID){
		var rejectReason = prompt("Reject Reason:","");
		var msg = '<bean:message key="enterprise.promotion.reject.confirm.message"/>';
		var agree = confirm(msg);		
		if(agree){
			document.getElementById("pendingPrmOfferForm").addOnPackageId.value=addOnPackageID;
			document.getElementById("pendingPrmOfferForm").rejectReason.value=rejectReason;
			$("#pendingPrmOfferForm").attr("action", '<%=request.getContextPath()%>/rejectAddOnSubReq.do');
			$("#pendingPrmOfferForm").submit();
		}
	}
	
</script>


<div class="border" >
<div>
<html:form action="/subscribePromotion" method="post" styleId="pendingPrmOfferForm">
<html:hidden property="addOnPackageId" value=""/>
<html:hidden property="selectedLink" value="prmoffer"/>
<html:hidden property="selectedPortal" value="enterprise"/>
<html:hidden property="childID" value=""/>
<html:hidden property="rejectReason" value=""/>

<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >	
	<tr>   		
   			<td colspan="2" >&nbsp;</td>   		
	</tr>
	
	<tr class="table-org" >   		
   		<td class="table-org-column" colspan="2"><bean:message key="promotional.info"/></td>    		
	</tr>
	<%if(promotionalForm != null && promotionalForm.getPendingPromotionalSubReqData() != null){%>
		<tr class="table-gray">
			<td class="table-column" align="left" width="200px"><bean:message key="parentalcontrol.prmoffer.childname"/></td>
			<td class="table-column" align="left"  >
				<%=promotionalForm.getPendingPromotionalSubReqData().getSubscriberIdentity()%>
			</td>		
		</tr>	
		<tr class="table-white">
			<td class="table-column" align="left" width="200px"><bean:message key="parentalcontrol.prmoffer.addonname"/></td>
			<td class="table-column" align="left"  >
				<%=promotionalForm.getPromotionalData().getAddOnPackageName()%>
			</td>		
		</tr>
		<tr class="table-white">
			<td class="table-column" align="left" width="200px"><bean:message key="parentalcontrol.prmoffer.description"/></td>
			<td class="table-column" align="left"  >
				  <%=promotionalForm.getPromotionalData().getDescription()%>  
			</td>		
		</tr>
		<tr class="table-gray">
			<td class="table-column" align="left"><bean:message key="parentalcontrol.prmoffer.price"/></td>
			<td class="table-column" align="left" >
				  <%=promotionalForm.getPromotionalData().getPrice()%>  
			</td>												 
		</tr>
		<tr class="table-white">
			<td class="table-column" align="left"><bean:message key="parentalcontrol.prmoffer.validity"/></td>
			<td class="table-column" align="left" >
				 <%=promotionalForm.getPromotionalData().getValidity()%>&nbsp;<%=promotionalForm.getPromotionalData().getValidityPeriodUnit()%>  
			</td>		
		</tr>				
		<tr class="table-gray">
			<td class="table-column" align="left"><bean:message key="parentalcontrol.prmoffer.offerenddate"/></td>
			<td class="table-column" align="left" >
				<%=EliteUtility.dateToString(new Date(promotionalForm.getPendingPromotionalSubReqData().getCreateTime()),"dd/MM/yyyy HH:mm:ss")%>
			</td>												 
		</tr>
		<html:hidden property="addOnSubReqID" value='<%=String.valueOf(promotionalForm.getPendingPromotionalSubReqData().getAddOnSubReqID())%>'/>
		<tr class="table-grey">   		
	   			<td class="table-column" colspan="2" >&nbsp;</td>   		
		</tr>
		<tr>
			<td valign="center" class="table-column" style="text-align: center;" align="center" colspan="5">
				<input type="button" name="Approve" class="orange-btn" value="  Approve  " onclick="approvePromotionalOffer(<%=promotionalForm.getPendingPromotionalSubReqData().getAddOnID()%>,'<%=promotionalForm.getPendingPromotionalSubReqData().getSubscriberIdentity()%>')">
				<input type="button" name="Reject" class="orange-btn" value="  Reject  " onclick="rejectPromotionalOffer(<%=promotionalForm.getPendingPromotionalSubReqData().getAddOnSubReqID()%>,'<%=promotionalForm.getPendingPromotionalSubReqData().getSubscriberIdentity()%>')">
			</td>
		</tr>
	<%}else{%>
		<tr class="table-white">
			<td align="center" class="tblrows" colspan="4"><bean:message key="general.norecordsfound"/></td>				
		</tr>
	<%}%>	
</table>
</html:form>
</div>	
</div>