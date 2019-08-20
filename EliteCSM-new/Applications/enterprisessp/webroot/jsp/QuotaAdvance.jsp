

<%@page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
 
<%@ page import="java.util.ArrayList"%>
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
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<%
 	SubscriberProfile loggedInUser = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
	SubscriberProfile childObj = (SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
	
	String errorCode = (String)request.getAttribute("errorCode");
	
	String successCode = (String)request.getAttribute("successCode");
%>
 <script>
 	function validateAndForwardRequest(){		
		var intRegex = /^\d+$/;
		var enteredNumber = document.getElementById("noOfMonth").value;
		var enteredQuota = document.getElementById("quotaAmount").value;
		
		if(intRegex.test(enteredNumber) && intRegex.test(enteredQuota)) {
			
			$("#actionPerformed").val("requestQuotaAdvance");
			$("#quotaAdvanceForm").submit();
		   
		}else{
			alert('Please Enter only Positive Integer values');
			return false;
		}
		
		
	}
 	
 	function keyUpEvent(){
 		var intRegex = /^\d+$/;
		var enteredNumber = document.getElementById("noOfMonth").value;
		var enteredQuota = document.getElementById("quotaAmount").value;
		if(intRegex.test(enteredNumber) && intRegex.test(enteredQuota)){
			var  totalAmount = enteredNumber * enteredQuota;
			$("#totalValueRequested").val(totalAmount);
		}else{
			$("#totalValueRequested").val(0);
		}
		
 	}
 	
 </script>

<body>
<html:form action="/quotaAdvance" styleId="quotaAdvanceForm">
<html:hidden property="actionPerformed" styleId="actionPerformed" value=""/>
<div class="border" >
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
   		<td class="table-org-column" align="left" colspan="3">Request Quota Advance</td>    		
	</tr>
		
	<tr>   		
   		<td colspan="3" class="smallgap" >&nbsp;</td>   		
	</tr>
	
	<tr class="table-gray">
		<td class="table-column" align="left" width="50%">No. Of Months from which advance is required :</td>
		<td align="left" width="50%" ><html:text property="noOfMonth" styleId="noOfMonth" onkeyup="keyUpEvent()"/></td>
	</tr>
	
	<tr class="table-gray">
		<td class="table-column" align="left" width="50%">Enter the quota amount to be requested (in MBs):</td>
		<td align="left" width="50%" ><html:text property="quotaAmount" styleId="quotaAmount" value="0" onkeyup="keyUpEvent()"/></td>
	</tr>
	
	<tr class="table-gray">
		<td class="table-column" align="left" width="50%">Total Amount of Advance Quota Requested (in MBs):</td>
		<td align="left" width="50%" ><input type="text" name="totalValueRequested" id="totalValueRequested" disabled="disabled"></td>
	</tr>
	
	<tr>   		
   		<td colspan="3" class="smallgap" >&nbsp;</td>   		
	</tr>

	<tr>
		<td width="50%" class="table-text ">&nbsp;</td>
        <td width="50%" ><input class="button-txt" style="border:1px solid black;height:25px;" value="Request Advance" type="submit" onclick="return validateAndForwardRequest()" /></td>
	</tr>
	
			<%if(errorCode!=null) {%>
			  	<tr>
					<td width="30%" class="whitetext" style="color: red" colspan="2"><%=errorCode%></td>
				</tr>	
			  <%}%>
			  
			  <%if(successCode!=null) {%>
			  	<tr>
					<td width="30%" class="whitetext" style="color: green" colspan="2"><%=successCode%></td>
				</tr>	
			  <%}%>
	
</table>
</div>
</html:form>
</body>
</html>

