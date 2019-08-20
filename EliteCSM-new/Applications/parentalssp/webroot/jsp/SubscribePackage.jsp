<%@page import="com.elitecore.netvertexsm.ws.xsd.SubscriberProfileData"%>
<%@page import="com.elitecore.ssp.web.bod.forms.SubscribePackageForm"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.BoDPackageData"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.BodSubscriptionData"%>
<%@ page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="nested" %>
<% String basePath=request.getContextPath();%>	
<script language="javascript" src="<%=basePath%>/js/validation.js"></script>
<head>
   	<title>Subscribe Package Information</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<%
 	SubscriberProfileData childObj=(SubscriberProfileData)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT); 	
 	SubscribePackageForm subscribePackageForm = (SubscribePackageForm)request.getAttribute(RequestAttributeKeyConstant.SUBSCRIBE_PACKAGE_FORM);	
%>

<script type="text/javascript">
		
		
if(typeof String.prototype.trim !== 'function') {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, '');
	}
}

function validateStartTime() {
	//This function will validte the datetimes of this forma dd-mm-yyyy hh:mm:ss
	var date 		= document.getElementById("startTime").value.trim();
    var valid 		= true;
    var spaceIndex 	= date.indexOf(" ");
	var onlyDate 	= date.substring(0,spaceIndex);
	var dateData 	= onlyDate.split("-");		
	var onlyTime 	= date.substring(spaceIndex);		
	var timeData 	= onlyTime.split(":");				
    var day 		= (dateData[0]);	    
    var month 		= (dateData[1]);	   
    var year  		= (dateData[2]);	   
    var hour  		= (timeData[0]);	   
    var min 		= (timeData[1]);	   
    var sec 		= (timeData[2]);	   	   
 	var regForDate = new RegExp("\\d{1,2}-\\d{1,2}-\\d{4}$");
 	var regForTime = new RegExp("\\d{1,2}:\\d{1,2}:\\d{1,2}$");
 	
	if(!regForDate.test(onlyDate)) valid =false;
	else if(!regForTime.test(onlyTime)) valid =false;
	else if((month < 1) || (month > 12)) valid = false;
    else if((day < 1) || (day > 31)) valid = false;
    else if(((month == 4) || (month == 6) || (month == 9) || (month == 11)) && (day > 30)) valid = false;
    else if((month == 2) && (((year % 400) == 0) || ((year % 4) == 0)) && ((year % 100) != 0) && (day > 29)) valid = false;
    else if((month == 2) && ((year % 100) == 0) && (day > 29)) valid = false;
    else if((hour < 0) || (hour > 24)) valid = false;
    else if((min < 0) || (min > 59)) valid = false;
    else if((sec < 0) || (sec > 59)) valid = false;  		    		
	
	return valid;
}
	function validateDuration(){
		var durationVal=document.getElementById("duration").value.trim();
		if(durationVal.length<1 || durationVal<=0){
			alert("Duration must be an Integer greater than zero.");
			document.getElementById("duration").focus();					
		}else if(!isInteger(durationVal)){
			alert("Duration must be an Integer greater than zero.");
			document.getElementById("duration").focus();
		}else if(durationVal>=<%=Long.MAX_VALUE%>){
			alert("Duration must is too much long ! please provide appropriate duration.");
			document.getElementById("duration").focus();						
		}else{
			return true;
		}
	}
	function subscribePromotionalOffer(bodPackageID){
		if(!validateStartTime()){
			alert("StartTime is Invalid.");
			document.getElementById("startTime").focus();
		}else if(validateDuration()){
			var msg = '<bean:message key="parentalcontrol.bod.subscribe.confirm.message"/>';
			var agree = confirm(msg);
			if(agree){
				document.forms[0].bodPackageId.value=bodPackageID;
		    	document.forms[0].submit();	
			}	    		
		}		
	}
	
</script>

<div class="border" >
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
		<html:form action="/subscribeBod" method="post">
		<html:hidden property="bodPackageId" value=""/>
		<html:hidden property="SELECTED_LINK" value="serviceactivation" styleId="SELECTED_LINK"/>
		<bean:define id="bodPackageObj" name="subscribePackageForm" property="bodPackageData"  type="com.elitecore.netvertexsm.ws.xsd.BoDPackageData"/>				
		<tr>   		
   			<td colspan="3" >
   				<table width="100%" cellpadding="0" cellspacing="0" border="0">   							
					<tr height="20px">
						<td class="table-org-column" width="140px">
							<bean:message key="parentalcontrol.bod.name"/>
						</td>
						<td class="table-org-column" style="text-align: left;" width="120px">
							<bean:message key="parentalcontrol.bod.description"/>
						</td>
						<td class="table-org-column" style="text-align: center;" width="120px">
							<!-- <bean:message key="parentalcontrol.bod.pricerate"/>  -->
						</td>						
						<td class="table-org-column" style="text-align: center;" width="120px">
							&nbsp;
						</td>
					</tr>
					<%if(subscribePackageForm != null && subscribePackageForm.getBodPackageData()!=null){%>
					<tr class="table-gray" height="20px">
						<td class="table-column" width="140px">
							<bean:write name="bodPackageObj" property="name" />
						</td>
						<td class="table-column" style="text-align: left;" width="120px">
							<bean:write name="bodPackageObj" property="description" />
						</td>
						<td class="table-column" style="text-align: center;" width="120px">
							<!-- <bean:write name="bodPackageObj" property="priceRate" /> -->
						</td>						
						<td class="table-column" style="text-align: center;" width="120px">
							&nbsp;
						</td>
					</tr>											
					<tr>   		 
  						<td colspan="4" >&nbsp;</td>   		
					</tr>
					<tr class="table-white">				
						<td class="table-column"><bean:message key="bod.starttime"/></td>
						<td align="left">								
							<html:text property="startTime"  styleId="startTime" value="<%=com.elitecore.ssp.util.constants.BaseConstant.DATETIME_FORMATTER.format(new Date())%>" maxlength="19" onblur="" />							
						</td>
						<td colspan="2">&nbsp;<bean:message key="bod.timeformat"/></td>						
					</tr>
					<tr class="table-gray">
						<td>&nbsp;</td>
						<td colspan="2" ></td>
						<td>&nbsp;</td>
					</tr>
					<tr class="table-white">
						<td class="table-column"><bean:message key="bod.duration"/></td>
						<td><html:text property="duration" styleClass="" value="1"  styleId="duration" onblur="" maxlength="19" /></td>
						<td>
							<bean:message key="bod.durationunit"/>
						</td>
						<td>
							<html:select property="durationUnit" >
								<html:option value="HOUR">Hour</html:option>
								<html:option value="MINUTE">Minute</html:option>
							</html:select>	
						</td>			
					</tr> 
					<tr>   		 
  						<td colspan="4" >&nbsp;</td>   		
					</tr>					
					<tr class="table-gray" align="center">
						<td colspan="4">
							<input type="button" name="Subscribe" class="orange-btn" value="  <bean:message key="parentalcontrol.bod.subscribe.button"/>  " onclick="subscribePromotionalOffer('<bean:write name="bodPackageObj" property="bodPackageId" />');">
						</td>
					</tr>
				 <%}else{%>
					<tr class="table-white">
						<td align="center" class="tblrows" colspan="4"><bean:message key="general.norecordsfound"/></td>				
					</tr>
				 <%}%>
  				</table>
   			</td>   		
		</tr>
		</html:form>				   			   	
	 </table>	
	</div>
	</div>
</body>