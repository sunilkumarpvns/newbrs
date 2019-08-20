<%@page import="com.elitecore.netvertexsm.ws.xsd.SubscriberProfileData"%>
<%@page import="com.elitecore.ssp.web.bod.forms.BodForm"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.BoDPackageData"%>
<%@ page import="com.elitecore.netvertexsm.ws.xsd.BodSubscriptionData"%>
<%@ page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@ page import="com.elitecore.ssp.util.constants.BaseConstant" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="nested" %>
<head>
   	<title>Child Account Information</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%=request.getContextPath()%>/css/stylesheet.css" rel="stylesheet" type="text/css" />
</head>

<%
 	SubscriberProfileData childObj=(SubscriberProfileData)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT); 	
 	BodForm bodPackageForm = (BodForm)request.getAttribute(RequestAttributeKeyConstant.BOD_FORM);	
%>

<script type="text/javascript">

	function subscribePromotionalOffer(bodPackageID){				 
			document.forms[0].bodPackageId.value=bodPackageID;
	    	document.forms[0].submit();			 		   		
	}	
	function unSubscribePromotionalOffer(bodPackageID){
		var msg = '<bean:message key="parentalcontrol.bod.unsubscribe.confirm.message"/>';
		var agree = confirm(msg);		
		if(agree){
			document.forms[1].bodId.value=bodPackageID;
	    	document.forms[1].submit();	
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
		
		<tr class="table-org" height="30px">   		
   			<td class="table-org-column" colspan="3"><bean:message key="parentalcontrol.bod.availablebodpackages"/></td>    		
		</tr>
		
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>
		
		<html:form action="/subscribePackage" method="post">
		<html:hidden property="bodPackageId" value=""/>		
		<html:hidden property="SELECTED_LINK" value="serviceactivation" styleId="SELECTED_LINK"/>
		<tr>   		
   			<td colspan="3" >
   				<table width="100%" cellpadding="0" cellspacing="0" border="0">
   				<%if(bodPackageForm.getAvailableBodPackages() != null && bodPackageForm.getAvailableBodPackages().length > 0){%>	
				<%int i=0;%>									
					<tr height="20px">
						<td class="table-org-column" width="160px">
							<bean:message key="parentalcontrol.bod.name"/>
						</td>
						<td class="table-org-column" style="text-align: left;" colspan="2" width="120px">
							<bean:message key="parentalcontrol.bod.description"/>
						</td>
 
						 						
						<td class="table-org-column" style="text-align: center;" width="120px">
							&nbsp;
						</td>
					</tr>
					<logic:iterate id="bodData" name="bodForm" property="availableBodPackages" type="com.elitecore.netvertexsm.ws.xsd.BoDPackageData">
						<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
							<td valign="center" style="text-align: left: ;" class="table-column">
								<bean:write name="bodData" property="name" />&nbsp;
							</td>
							<td valign="center" class="table-column" style="text-align: left;width:200px">
								<bean:write name="bodData" property="description" />&nbsp;
							</td>
							<td valign="center" class="table-column" style="text-align: center;">
								<!-- <bean:write name="bodData" property="priceRate" />&nbsp; -->
							</td>
							<td valign="center" class="table-column" style="text-align: right;">
								<input type="button" name="Subscribe" class="orange-btn" value="  <bean:message key="parentalcontrol.bod.subscribe.button"/>  " onclick="subscribePromotionalOffer('<bean:write name="bodData" property="bodPackageId" />');">&nbsp;&nbsp;
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
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>		
		
		<tr class="table-org" height="30px">   		
   			<td class="table-org-column" colspan="3"><bean:message key="parentalcontrol.bod.active.bodpackages"/></td>    		
		</tr>
		
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>
		
		<html:form action="/unsubscribeBoD" method="post">
		<html:hidden property="bodId" value=""/>
		<html:hidden property="selectedLink" value="serviceactivation"/>		
		<tr>   		
   			<td colspan="3" >
   				<table width="100%" cellpadding="0" cellspacing="0" border="0">
   				<%if(bodPackageForm.getActiveBodPackages() != null && bodPackageForm.getActiveBodPackages().length>0){%>	
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
						<logic:iterate id="activeBODData" name="bodForm" property="activeBodPackages" type="com.elitecore.netvertexsm.ws.xsd.BodSubscriptionData">
			   				<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
								<td valign="center" style="text-align: left: ;" class="table-column">
									<bean:write name="activeBODData" property="subscriberPackage" />&nbsp;
								</td>
								<td valign="center" class="table-column" style="text-align: center;">									
									<bean:write name="activeBODData" property="startTime" />&nbsp; 
								</td>			   				
								<td valign="center" class="table-column" style="text-align: center;">									
									<bean:write name="activeBODData" property="endTime" />&nbsp;
								</td>			   												
								<td valign="center" class="table-column" style="text-align: right;">
									<input type="button" name="UnSubscribe" class="orange-btn" value="  <bean:message key="parentalcontrol.bod.unsubscribe.button"/>  " onclick="unSubscribePromotionalOffer('<bean:write name="activeBODData" property="bodId" />');">&nbsp;&nbsp;
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
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>		
		
		<tr class="table-org" height="30px">   		
   			<td class="table-org-column" colspan="3"><bean:message key="parentalcontrol.prmoffer.subscriptionhistory"/></td>    		
		</tr>
		
		<tr>   		
   			<td colspan="3" class="smallgap" >&nbsp;</td>   		
		</tr>
		
		<tr>   		
   			<td colspan="3">   				
   				<table width="100%"  cellpadding="0" cellspacing="0" border="0" >
   				<%if(bodPackageForm.getBodSubscriptionHistories() != null && bodPackageForm.getBodSubscriptionHistories().length>0){%>	
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
					<tr>
						<td colspan="3">
						<div  class="historyscrollbar">   				
	   					<table width="100%"  cellpadding="0" cellspacing="0" border="0" >
						<logic:iterate id="activeBODData" name="bodForm" property="bodSubscriptionHistories" type="com.elitecore.netvertexsm.ws.xsd.BodSubscriptionData">					
				   			<tr class="<%=(++i%2==0)?"table-white":"table-gray"%>">
								<td valign="center" style="text-align: left: ;" class="table-column">
									<bean:write name="activeBODData" property="subscriberPackage" />&nbsp;
								</td>
								<td valign="center" class="table-column" style="text-align: center;">
									<bean:write name="activeBODData" property="startTime" />&nbsp;
								</td>			   				
								<td valign="center" class="table-column" style="text-align: center;">
									<bean:write name="activeBODData" property="endTime" />&nbsp;
								</td>			   																								   															   		
							</tr>
						</logic:iterate>
						</table>
						</div>
						</td>										
					</tr>
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
									   			   		
	 </table>	
	</div>
	</div>
</body>