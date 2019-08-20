<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@	page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountInfoForm"%>
<%@	page import="com.elitecore.netvertexsm.ws.xsd.QuotaUsageData"%>
<%@ page import="com.elitecore.ssp.util.EliteUtility"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
   <title><bean:message key="childaccount.info"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<%	
	ChildAccountInfoForm childAccountInfoForm = (ChildAccountInfoForm) request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_ACCOUNT_INFO_FORM);	
%>

<div class="border" >
	<div>
		<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >	

			
			<tr class="table-org" >   		
		   		<td class="table-org-column" colspan="4"><bean:message key="childaccount.info"/></td>    		
			</tr>
			
			<tr>   		
		   			<td class="smallgap" colspan="2" >&nbsp;</td>   		
			</tr>
			
			<tr>
				<td colspan="3" align="left" valign="top">
					<table width="100%" cellpadding="0" cellspacing="0" border="0" id="acctInfoTbl">
						<tr height="20px">
							<td class="table-org-column" width="120px" style="text-align: left;">
								<bean:message key="childaccount.username"/>								
							</td>
							<td class="table-org-column"  style="text-align: left;width:70px">Status</td>
							<td class="table-org-column" style="text-align: left: ;">
								<bean:message key="home.usedquotatotal"/>&nbsp;<bean:message key="childaccount.used_total"/>
							</td>
							<td class="table-org-column" style="text-align: left;" width="130px">
								<bean:message key="home.usedsessiontime"/>
							</td>
							<td class="table-org-column" style="text-align: left: ;" >
								<bean:message key="childaccount.currentplan"/>
							</td>
						</tr>
						<%if(childAccountInfoForm.getChildAccountUsageInfoDataList() != null && childAccountInfoForm.getChildAccountUsageInfoDataList().size()>0){%>	
						<%int i=0;%>									
							<logic:iterate id="allowedUsageInfoBean" name="childAccountInfoForm" property="childAccountUsageInfoDataList" type="com.elitecore.ssp.web.parentalcontrol.ChildAccountUsageInfo">						
								<tr height="20px" >								
									<td valign="center" style="text-align: left; padding-left: 10px">
										<%=(allowedUsageInfoBean.getChildObject().getUserName())%>
									</td>
									<td valign="center" style="text-align: left;padding-left: 10px ">
										<%=(allowedUsageInfoBean.getChildObject().getAccountStatus()!=null?allowedUsageInfoBean.getChildObject().getAccountStatus():"Active")%>
									</td>
									<td valign="center" style="text-align: center;">
										<%if(allowedUsageInfoBean.getAllowedUsageInfo().length>5){%>
											<div class="quota-usage-control">
												<div style="background-color:orange;width:<%=((allowedUsageInfoBean.getAllowedUsageInfo()[2]==0?0:(allowedUsageInfoBean.getAllowedUsageInfo()[5]*100)/allowedUsageInfoBean.getAllowedUsageInfo()[2])*2)%>px;float: left;text-align: center;height: 20px;"></div>
												<div class="used-total-quota"><%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfoBean.getAllowedUsageInfo()[5])%> / <%=EliteUtility.convertBytesToSuitableUnit(allowedUsageInfoBean.getAllowedUsageInfo()[2]) %></div>							
											</div>
										<%}else{%>
											<div style="width:200px;float: left;color:white;height: 20px;"></div>
										<%}%>	
									</td>
									<td valign="center" style="text-align: left; padding-left: 10px">
										<%
											if(allowedUsageInfoBean.getAllowedUsageInfo().length>4){
												out.println(EliteUtility.convertToHourMinuteSecond(allowedUsageInfoBean.getAllowedUsageInfo()[4])+" Hrs");
											}
										%>
									</td>
									<td valign="center" style="text-align: left; padding-left: 10px">
										<%=allowedUsageInfoBean.getChildObject().getSubscriberPackage()%>
									</td>								
								</tr>							
							</logic:iterate>
						<%}else{%>
							<tr class="table-white">
								<td align="center" class="tblrows" colspan="7"><bean:message key="general.norecordsfound"/></td>				
							</tr>
						<%}%>					
					</table>				
				</td>			
			</tr>					
			
			<tr class="table-grey">   		
		   			<td class="table-column" colspan="2" >&nbsp;</td>   		
			</tr>		
		</table>
	</div>	
</div>

<script>
	$(document).ready(function(){
		$("#acctInfoTbl tr:even").addClass("table-gray");
		$("#acctInfoTbl tr:odd").addClass("table-white");
	});
</script>