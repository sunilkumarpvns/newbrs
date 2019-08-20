<%@ page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@	page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountInfoForm"%>
<%@ page import="com.elitecore.ssp.util.EliteUtility"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" 	prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" 	prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" 	prefix="logic" %>

<head>
   <title><bean:message key="childaccount.info"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<%	
	SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
	SubscriberProfile currentUser=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.TEMP_CURRENT_USER);
	ChildAccountInfoForm childAccountInfoForm = (ChildAccountInfoForm) request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM);	
	long[] allowedUsageInfo= null;		
	String memberIs="";
	String showRolloverOption = "no";
	
	if(childAccountInfoForm!=null){		
		allowedUsageInfo =childAccountInfoForm.getAllowedUsageInfo();
		childAccountInfoForm.setProfile(childObj);
	}else{	
		childAccountInfoForm = new ChildAccountInfoForm();
		childAccountInfoForm.setProfile(childObj);
		allowedUsageInfo=(long[])request.getAttribute(RequestAttributeKeyConstant.ALLOWED_USAGE_INFO);
		request.getSession().setAttribute(SessionAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM,childAccountInfoForm);
	}
	if(currentUser.equals(childObj)){
		memberIs="Admin ";
	}else{
		memberIs="Subscriber ";
	}
	String statusColor="";
	if(childObj.getStatus()!=null && (childObj.getStatus().equalsIgnoreCase("A") || childObj.getStatus().equalsIgnoreCase("Active"))){
		statusColor = "green";
	}
	
	System.out.println("memberIs : "+memberIs);
	System.out.println("PARAM10 : "+currentUser.getParam10());
	
	if(memberIs.equalsIgnoreCase("Admin ") && currentUser.getParam10()!=null && !currentUser.getParam10().trim().isEmpty() && currentUser.getParam10().equalsIgnoreCase("yes")){
		showRolloverOption = "yes";
	}else{
		showRolloverOption = "no";
	}
	
	String errorCode = (String)request.getAttribute("errorCode");
	
	String successCode = (String)request.getAttribute("successCode");
		
 %>
 <script>
 	function blockURL(){
		$("#actionPerformed").val("blockURL");
		$("#childAccountModifyForm").submit();
	}
 	
 	function setQuotaRollover(){
 		$("#actionPerformed").val("quotaRollOver");
		$("#childAccountModifyForm").submit();
 	}
 	
 	function validateAndForwardRequest(){		
		var intRegex = /^\d+$/;
		var enteredNumber = document.getElementById("notificationPercentage").value;
		
		alert('enteredNumber : '+enteredNumber);
		
		if(intRegex.test(enteredNumber)) {
			alert('enteredNumber :'+enteredNumber);
			if(enteredNumber>=0 && enteredNumber<=100){
				alert('true');
				$("#actionPerformed").val("notificationPercentageValue");
				$("#childAccountModifyForm").submit();
			}else{
				alert('false');
				alert('Please Enter only Positive Integer values from 0 - 100');
				return false;
			}
		   
		}else{
			alert('Please Enter only Positive Integer values from 0 - 100');
			return false;
		}
		
		
	}
 	
 </script>
<body>
<html:form action="/childAccountInfo" method="post" styleId="childAccountModifyForm">
<html:hidden property="actionPerformed" styleId="actionPerformed" value=""/>
<div class="border" >
<div>
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
	<tr class="table-org" >   		
   		<td class="table-org-column" align="left" colspan="2"><%=memberIs%><bean:message key="childaccount.info"/></td>    		
	</tr>	
	<tr class="table-gray">
		<td class="table-column" align="left" width="200px"><bean:message key="childaccount.status"/></td>
		<td class="table-column" align="left"> <font color="<%=statusColor%>"><b>  <%=(childObj.getStatus()!=null?childObj.getStatus():"-")%></b></font></td>		
	</tr>
	<% if(allowedUsageInfo!=null && allowedUsageInfo!=null && childObj!=null){%>	
	<tr class="table-white">
		<td class="table-column" align="left"><bean:message key="home.usedquotatotal"/>&nbsp;<bean:message key="childaccount.used_total"/></td>
		<td class="table-column" align="left" >
			<%if(allowedUsageInfo.length>5){
				long divWidth = ((allowedUsageInfo[2]==0?0:(allowedUsageInfo[5]*100)/allowedUsageInfo[2])*2);
				if(divWidth>400){
					divWidth = 400;
				}				
			%>
			<div class="quota-usage-control" >
				<div style="background-color:orange;width:<%=divWidth%>px;float: left;text-align: center;height: 20px;">
					<div style="width:200px;text-align:center;font-weight: bold" ><%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfo[5])%> / <%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfo[2]) %></div>
				</div>
				<%-- <div class="used-total-quota" align="center" style="vertical-align: middle;" ><%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfo[5])%> / <%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfo[2]) %></div>							 --%>
			</div>
			<%}else{%>
			<div style="width:200px;float: left;height: 20px;">-</div>
			<%}%>			
		</td>												 
	</tr>
	<tr class="table-gray">
		<td class="table-column" align="left"><bean:message key="home.usedsessiontime"/></td>
		<td class="table-column" align="left" >
			<%
				if(allowedUsageInfo.length>4){
					out.println(EliteUtility.convertToHourMinuteSecond(allowedUsageInfo[4])+" Hrs");
				}else{
					out.println("-");
				}
			%>
		</td>		
	</tr>				
	<%}else{%>
	<tr class="table-white">
		<td align="center" class="tblrows" colspan="2"><bean:message key="parentalcontrol.norecords"/></td>				
	</tr>
	<%}%>
	
	<tr class="table-white">
		<td class="table-column" align="left"><bean:message key="childaccount.currentplan"/></td>
		<td class="table-column" align="left"><%=childObj.getSubscriberPackage()!=null?childObj.getSubscriberPackage():"-"%></td>		
	</tr>	
	<tr class="table-grey">   		
   			<td class="table-column" colspan="2" >&nbsp;</td>   		
	</tr>
	
	<% if(!memberIs.equalsIgnoreCase("Admin ")){%>
		<tr>
          <td class="table-txt ">Blocked URL</td>
          <td>          
          <table>
	          <tr>
		          <td>
		          	<html:text property="blockedUrlString" styleId="blockedUrlString" value="<%=childObj.getParam1()%>" />
		          </td>
		          <td align="left">
		          	<input class="button-txt" style="border:1px solid black;height:25px;" value="Block URL" type="submit" onclick="blockURL();"/>
		          </td>
	          </tr>          
          </table>
          </td>          	
        </tr>
	<%}%>	
	
	<% if(memberIs.equalsIgnoreCase("Admin ")){%>
		<tr>
          <td class="table-txt ">Quota Notification Percentage</td>
          <td>
	          <table>
		          <tr>
			          <td>
			          	<html:text property="notificationPercentage" styleId="notificationPercentage" value="<%=currentUser.getParam3()%>" />
			          </td>
			          <td>
			          	<input class="button-txt" style="border:1px solid black;height:25px;" value="Submit" type="submit" onclick="return validateAndForwardRequest()"/>
			          </td>
		          </tr>
	          </table>
          </td>          
        </tr>
	<%}%>
	<% if(showRolloverOption.equalsIgnoreCase("yes")){%>
		<tr>
			<td class="table-txt ">Rollover Quota To Next Month : </td>
			<td>
				<table>
					<tr>
						<td align="left" >
							<html:select property="quotaRollOver" >
								<html:option value="yes">YES</html:option>
								<html:option value="no">NO</html:option>
							</html:select>	
						</td>
						<td>
			          		<input class="button-txt" style="border:1px solid black;height:25px;" value="Submit" type="submit" onclick="setQuotaRollover();"/>
			          	</td>	
					</tr>
				</table>			
			</td>		
		</tr>
	<%}%>	
</table>			
</div>	
</div>
</html:form>
</body>
