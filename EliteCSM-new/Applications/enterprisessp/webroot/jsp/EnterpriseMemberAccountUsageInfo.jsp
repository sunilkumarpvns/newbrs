<%@page import="com.elitecore.ssp.web.parentalcontrol.forms.ChildAccountUsageInfoForm"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@ page import="com.elitecore.ssp.util.EliteUtility"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<head>
   <title><bean:message key="childaccount.info"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<%	
	 ChildAccountUsageInfoForm childAccountUsageInfoForm = (ChildAccountUsageInfoForm) request.getAttribute(RequestAttributeKeyConstant.CHILD_ACCOUNT_USAGE_INFO_FORM);	
%>

<div class="border" >
	<div>
		<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >		
			
			<tr class="table-org" >   		
		   		<td class="table-org-column" colspan="8"><bean:message key="parentalcontrol.usageinfo"/></td>    		
			</tr>
			
			<tr>   		
		   			<td class="smallgap" colspan="8" >&nbsp;</td>   		
			</tr>
			
			<tr>
				<td colspan="15" align="left" valign="top">
					<table width="100%" cellpadding="0" cellspacing="0" border="0" id="acctUsageInfoTbl">
						<tr height="15px" style="width: 100%">
							<td class="table-org-column" width="170px" style="text-align: left;" colspan="2">
								<bean:message key="childaccount.username"/>								
							</td>
							<td class="table-org-column" width="170px" style="text-align: left;" colspan="2">
								<bean:message key="parentalcontrol.usageinfo.lastday"/>
							</td>
							<td class="table-org-column" style="text-align: left;" width="170px" colspan="2">
								<bean:message key="parentalcontrol.usageinfo.lastweek"/>
							</td>
							<td class="table-org-column" style="text-align: left;" width="170px" colspan="2">
								<bean:message key="parentalcontrol.usageinfo.lastmonth"/>
							</td>							
						</tr>
						<%if(childAccountUsageInfoForm != null && childAccountUsageInfoForm.getEnterpriseMemberAccountUsageInfoDataList().size()>0){%>	
							<logic:iterate id="enterpriseAccountUsageInfoBean" name="childAccountUsageInfoForm" property="enterpriseMemberAccountUsageInfoDataList" type="com.elitecore.ssp.web.parentalcontrol.EnterpriseMemberAccountUsageInfo">						
								<tr height="20px" >								
									<td valign="center" style="text-align: left; padding-left: 10px">
										<%if(enterpriseAccountUsageInfoBean.getSubscriberProfileData() != null){%>
							   				<%=enterpriseAccountUsageInfoBean.getSubscriberProfileData().getUserName()%>	   			
							   			<%}%>
									</td>
									<td class="table-column" align="right" width="100px">
							   			<%if(enterpriseAccountUsageInfoBean.getLastDayTotalOctets() != null){%>
							   				<%=EliteUtility.convertBytesToSuitableUnit(enterpriseAccountUsageInfoBean.getLastDayTotalOctets())%>	   			
							   			<%}%>
							   		</td>
							   		<td class="table-column" align="left" style="padding-left: 20px">
							   			<%if(enterpriseAccountUsageInfoBean.getLastDayUsageTime() != null){%>
							   				<%=EliteUtility.convertToHourMinuteSecond(enterpriseAccountUsageInfoBean.getLastDayUsageTime())+" Hrs"%>	   			
							   			<%}else{%>
							   				-
							   			<%}%>
							   		</td>
									<td class="table-column" align="right">
							   			<%if(enterpriseAccountUsageInfoBean.getLastWeekTotalOctets() != null){%>
							   				<%=EliteUtility.convertBytesToSuitableUnit(enterpriseAccountUsageInfoBean.getLastWeekTotalOctets())%>
							   			<%}%>
							   		</td>
							   		<td class="table-column" align="left" style="padding-left: 20px">
							   			<%if(enterpriseAccountUsageInfoBean.getLastWeekUsageTime() != null){%>
							   				<%=EliteUtility.convertToHourMinuteSecond(enterpriseAccountUsageInfoBean.getLastWeekUsageTime())+" Hrs"%>	   			
							   			<%}else{%>
							   				-
							   			<%}%>
							   		</td>
							   		<td class="table-column" align="right">
							   			<%if(enterpriseAccountUsageInfoBean.getLastMonthTotalOctets() != null){%>
							   				<%=EliteUtility.convertBytesToSuitableUnit(enterpriseAccountUsageInfoBean.getLastMonthTotalOctets())%>
							   			<%}%>
							   		</td>
							   		<td class="table-column" align="left" style="padding-left: 20px">
							   			<%if(enterpriseAccountUsageInfoBean.getLastMonthUsageTime() != null){%>
							   				<%=EliteUtility.convertToHourMinuteSecond(enterpriseAccountUsageInfoBean.getLastMonthUsageTime())+" Hrs"%>	   			
							   			<%}else{%>
							   				-
							   			<%}%>
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
		$("#acctUsageInfoTbl tr:even").addClass("table-gray");
		$("#acctUsageInfoTbl tr:odd").addClass("table-white");
	});
</script>