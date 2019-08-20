
<%@page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@	page import="com.elitecore.ssp.web.quota.forms.QuotaTransferForm"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
 
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.elitecore.ssp.web.promotional.constants.PromotionalOfferConstants"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="nested" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   	<title>Quota Transfer Information</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>NetVertex Enterprise Policy Control</title>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/common.css" />
	<script type="text/javascript">
    function validate(){
    	var quota = document.getElementById('quotaToBeRequested').value;
    	var userId = document.getElementById('userIdToBeRequested').value;
		if(userId =='' || quota == ''){
			alert('Enter user id and quota amount values');
			return false;
		}else if(!isPositiveInteger(quota)){
			alert('Enter quota amount value as positive integer number');
			return false;
		} else {
			return true;
		}
	}
    
	function setDefaultFocus(){
    	document.getElementById('userIdToBeRequested').focus();
    	
	}
	
	function alertMessageAndReset(msg){
		document.getElementById('userIdToBeRequested').value='';
		document.getElementById('quotaToBeRequested').value='';
		alert(msg);
	}
	
	function isNumber(n) {
		  return !isNaN(n) && isFinite(n);
	}
	
	function isPositiveInteger(n) {
	    return 0 === n % (!isNaN(parseFloat(n)) && 0 <= ~~n);
	}
	
	function submitRequestApprovalOrRejection(action, user, quota){
		document.getElementById('actionPerformedForApproval').value=action;
		document.getElementById('requesterUserName').value=user;
		document.getElementById('requestedQuota').value=quota;
		return true;
	}
	
</script>
	
</head>

<%
 	SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
	SubscriberProfile childObj = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
	String messageToPopup = (String) request.getSession().getAttribute("QuotaTransferRequestMessage");
	request.getSession().setAttribute("QuotaTransferRequestMessage", null);
	HashMap<String, String> quotaRequestMap = (HashMap<String, String>) request.getSession().getAttribute("QuotaTransferRequestMap");
	request.getSession().setAttribute("QuotaTransferRequestMap", null);
%>

<body onload="setDefaultFocus();">
<html:form action="/quotatransfer">
<div class="border" >
<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >	
	<tr>   		
   			<td colspan="2" >&nbsp;</td>   		
	</tr>
	<tr> 		  		   	
   		<td colspan="2" align="left" valign="top">   			
   			<table  cellpadding="0" cellspacing="0" border="0" >
   				<tr><td class="img-padding"><img class="large-img" src="<%=request.getContextPath()%>/images/noimage.jpg" /></td><td valign="bottom" class="name" ><%=loggedInUser.getUserName()%> </td></tr>
   				<tr><td colspan="2" class="black-bg" height="10"></td></tr>
   			</table>
   		</td>   		
	</tr>
	<tr>   		
   			<td colspan="2" >&nbsp;</td>   		
	</tr>
	
	<tr class="table-org" height="30px">   		
   		<td class="table-org-column" align="left" colspan="3"><bean:message key="enterprise.subscriber.quotatransfer.requestform"/></td>    		
	</tr>
		
	<tr>
   		<td colspan="3" class="smallgap" >&nbsp;</td>   		
	</tr>
	
	<tr class="table-gray">
		<td class="table-column" align="left" width="50%">Enter the User Id, whom you want to request quota:</td>
		<td align="left" width="50%" ><html:text property="userIdToBeRequested" styleId="userIdToBeRequested"/></td>
	</tr>
	
	<tr class="table-gray">
		<td class="table-column" align="left" width="50%">Enter the quota amount to be requested (in MBs):</td>
		<td align="left" width="50%" ><html:text property="quotaToBeRequested" styleId="quotaToBeRequested"/></td>
	</tr>
	
	<tr>   		
   		<td colspan="3" class="smallgap" >&nbsp;</td>   		
	</tr>

	<tr>
		<td width="50%" class="table-text ">&nbsp;</td>
        <td width="50%" ><input class="button-txt" style="border:1px solid black;height:25px;" value="Send Request" type="submit" onclick="return validate();"/></td>
	</tr>

	<tr>
   		<td colspan="3" class="smallgap" >&nbsp;</td>   		
	</tr>
	<tr>
   		<td colspan="3" class="smallgap" >&nbsp;</td>   		
	</tr>
		
	<%if(messageToPopup!=null) {%>
		<script>alertMessageAndReset('<%=messageToPopup%>');</script>
		
    <%}%>
</table>
</div>
</html:form>

<%if(quotaRequestMap!= null && quotaRequestMap.size() > 0) {%>

<html:form action="/quotaapprove">
<div class="border" >


<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >	

	<html:hidden property="actionPerformedForApproval" styleId="actionPerformedForApproval" value=""/>
	<html:hidden property="requesterUserName" styleId="requesterUserName" value=""/>
	<html:hidden property="requestedQuota" styleId="requestedQuota" value=""/>

	<tr>
   		<td colspan="3" class="smallgap" >&nbsp;<hr/></td>   		
	</tr>	
	<tr>   		
   		<td colspan="3" class="smallgap" >&nbsp;</td>   		
	</tr>
		
	<tr class="table-org" height="30px">   		
   		<td class="table-org-column" align="left" colspan="3"><bean:message key="enterprise.subscriber.quotatransfer.pendingrequests"/></td>    		
	</tr>
		
	<tr>   		
   		<td colspan="3" class="smallgap" >&nbsp;</td>   		
	</tr>
	
	
	<% if(quotaRequestMap!= null && quotaRequestMap.size() > 0 ){
		for(String requester:quotaRequestMap.keySet()){
			if(requester!=null){
	%> 
		<tr class="table-gray">
			<td class="table-column" align="left" width="50%">User '<%=requester%>' has requested quota of <%=quotaRequestMap.get(requester)%> MBs</td>
            <td align="left" width="50%" ><html:text property="quotaToBeApproved" styleId="quotaToBeApproved" value="<%=quotaRequestMap.get(requester)%>"/></td>
		</tr>
		<tr class="table-gray">
			<td width="50%"></td>
			<td ><input class="button-txt" style="border:1px solid black;height:25px;" value="Approve" type="submit" 
							onClick="return submitRequestApprovalOrRejection('Approved','<%=requester%>', '<%=quotaRequestMap.get(requester)%>');"/>
			<input class="button-txt" style="border:1px solid black;height:25px;" value="Reject" type="submit" 
							onClick="return submitRequestApprovalOrRejection('Rejected','<%=requester%>', '<%=quotaRequestMap.get(requester)%>');"/></td>
		</tr>
	<%
			}
		}
	}
	%>	 
</table>
</div>
</html:form>
<%}%>

</body>
</html>

