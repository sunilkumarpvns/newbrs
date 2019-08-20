
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@page import="com.elitecore.netvertexsm.ws.xsd.QuotaUsageDetailData"%>
<%@page import="com.elitecore.netvertexsm.ws.xsd.QuotaUsageData"%>
<%@page import="com.elitecore.ssp.web.home.forms.HomeForm"%>
<%@page import="com.elitecore.netvertexsm.ws.xsd.SubscriberProfileData"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>

<%
  SubscriberProfileData subscriberProfileData =(SubscriberProfileData)session.getAttribute(SessionAttributeKeyConstant.CURRENT_USER);
  HomeForm homeForm = (HomeForm) request.getAttribute(SessionAttributeKeyConstant.HOME_FORM);
  QuotaUsageData[] quotaUsageDatas= null;
  if(homeForm!=null){
	  quotaUsageDatas = homeForm.getQuotaUsageDatas();
	  System.out.println("QuotaUsageDatas "+quotaUsageDatas);
  }
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="">
   <tr>
   		<td  height="20" class="titlebold">
   		  &nbsp;&nbsp;&nbsp;&nbsp; Current Plan Details
   		</td>
	</tr>
	<tr>	
		<td height="35"">
		</td>
	</tr>
	<tr>	
		<td>
		<table width="100%">
			<tr>
				<td width="25%"   align="left" class="blackboldtext">Policy Name:</td>
				<td align="left" class="blacktext"><%=subscriberProfileData!=null?subscriberProfileData.getSubscriberPackage()!=null?subscriberProfileData.getSubscriberPackage() :"-" :"-"%></td>
			</tr>
			
			<tr>
				<td align="left" class="blackboldtext">Billing Cycle:</td>
				<td align="left" class="blacktext"><%=subscriberProfileData!=null?subscriberProfileData.getBillingDate()!=null?subscriberProfileData.getBillingDate() :"-":"-"%></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>	
		<td height="35"">
		</td>
	</tr>
	
	<%if(quotaUsageDatas!=null && quotaUsageDatas.length>0){ %>
	<tr>
   		<td  height="20" class="titlebold">
   		  &nbsp;&nbsp;&nbsp;&nbsp; <bean:message key="home.mainusage"/>
   		</td>
	</tr>
	<tr>	
		<td height="35"">
		</td>
	</tr>
	<tr>	
		<td align="left">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td  align="left" class="tableheader"><bean:message key="general.serialno"/></td>
				<td align="left" class="tableheader"><bean:message key="home.packagename"/></td>
				<td align="left" class="tableheader"><bean:message key="home.maxquota"/></td>
				<td align="left" class="tableheader"><bean:message key="home.maxsessiontime"/></td>
				<td align="left" class="tableheader"><bean:message key="home.maxquotatotal"/></td>
				<td align="left" class="tableheader"><bean:message key="home.usedquota"/></td>
				<td align="left" class="tableheader"><bean:message key="home.usedsessiontime"/></td>
				<td align="left" class="tableheader"><bean:message key="home.usedquotatotal"/></td>
				<td align="left" class="tableheader"><bean:message key="home.starttime"/></td>
				<td align="left" class="tableheader"><bean:message key="home.endtime"/></td>
			</tr>
			<% int quotaUsageIndex=1; %>
			<logic:iterate id="quotaUsageData" name="homeForm" property="quotaUsageDatas" type="QuotaUsageData">
			<tr>
					<td align="left" class="tblfirstcol"><%=quotaUsageIndex%></td>
					<td align="left" class="tblrows"><bean:write name="quotaUsageData" property="packageName" /></td>
					<td align="left" class="tblrows">
					 <%= EliteUtility.convertBytesToSuitableUnit(quotaUsageData.getMaxQuotaUpload()) %>	/
					 <%= EliteUtility.convertBytesToSuitableUnit(quotaUsageData.getMaxQuotaDownload()) %>
					</td>
					<td align="left" class="tblrows"><bean:write name="quotaUsageData" property="maxSessionTime" /></td>
					<td align="left" class="tblrows"><%= EliteUtility.convertBytesToSuitableUnit(quotaUsageData.getMaxQuotaTotal()) %></td>
					<td align="left" class="tblrows">
					<%= EliteUtility.convertBytesToSuitableUnit(quotaUsageData.getUsedQuotaUpload()) %>/
					<%= EliteUtility.convertBytesToSuitableUnit(quotaUsageData.getUsedQuotaDownload()) %>
					</td>
					<td align="left" class="tblrows"><bean:write name="quotaUsageData" property="usedSessionTime" /></td>
					<td align="left" class="tblrows"><%= EliteUtility.convertBytesToSuitableUnit(quotaUsageData.getUsedQuotaTotal()) %></td>
					<td align="left" class="tblrows"><bean:write name="quotaUsageData" property="quotaStartDate" /></td>
					<td align="left" class="tblrows"><bean:write name="quotaUsageData" property="quotaEndDate" /></td>
			</tr>
			<%quotaUsageIndex++; %>
			</logic:iterate>
		</table>
		</td>
	</tr>
	<%} %>
	<tr>	
		<td height="35"">
		</td>
	</tr>
	<%if(quotaUsageDatas!=null && quotaUsageDatas.length>0){ 
	boolean recordExist=false;
	%>
	<tr>
   		<td  height="20" class="titlebold">
   		  &nbsp;&nbsp;&nbsp;&nbsp; <bean:message key="home.servicesusage"/>
   		</td>
	</tr>
	<tr>	
		<td height="35"">
		</td>
	</tr>
	<tr>	
		<td align="left">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td  align="left" class="tableheader"><bean:message key="general.serialno"/></td>
				<td align="left" class="tableheader"><bean:message key="home.servicename"/></td>
				<td align="left" class="tableheader"><bean:message key="home.packagename"/></td>
				<td align="left" class="tableheader"><bean:message key="home.maxquota"/></td>
				<td align="left" class="tableheader"><bean:message key="home.maxsessiontime"/></td>
				<td align="left" class="tableheader"><bean:message key="home.maxquotatotal"/></td>
				<td align="left" class="tableheader"><bean:message key="home.usedquota"/></td>
				<td align="left" class="tableheader"><bean:message key="home.usedsessiontime"/></td>
				<td align="left" class="tableheader"><bean:message key="home.usedquotatotal"/></td>

			</tr>
			<% int index=1; %>
			<logic:iterate id="quotaUsageData" name="homeForm" property="quotaUsageDatas" type="QuotaUsageData">
					
				<%if(quotaUsageData.getQuotaUsageDetails()!=null){ %>
				<logic:iterate id="quotaUsageDetail" name="quotaUsageData" property="quotaUsageDetails" type="QuotaUsageDetailData">
					<%recordExist=true; %>
					<tr>
						<td align="left" class="tblfirstcol"><%=index%></td>
						<td align="left" class="tblrows"><bean:write name="quotaUsageDetail" property="serviceName" /></td>
						<td align="left" class="tblrows"><bean:write name="quotaUsageData" property="packageName" /></td>
						<td align="left" class="tblrows">
						        <%= EliteUtility.convertBytesToSuitableUnit(quotaUsageDetail.getMaxQuotaUpload()) %>/
								<%= EliteUtility.convertBytesToSuitableUnit(quotaUsageDetail.getMaxQuotaDownload()) %>
						</td>
						<td align="left" class="tblrows"><bean:write name="quotaUsageDetail" property="maxSessionTime" /></td>
						<td align="left" class="tblrows"><%= EliteUtility.convertBytesToSuitableUnit(quotaUsageDetail.getMaxQuotaTotal()) %></td>
						<td align="left" class="tblrows">
						<%= EliteUtility.convertBytesToSuitableUnit(quotaUsageDetail.getUsedQuotaUpload()) %>	/
						<%= EliteUtility.convertBytesToSuitableUnit(quotaUsageDetail.getUsedQuotaDownload()) %>
						<td align="left" class="tblrows"><bean:write name="quotaUsageDetail" property="usedSessionTime" /></td>
						<td align="left" class="tblrows"><%= EliteUtility.convertBytesToSuitableUnit(quotaUsageDetail.getUsedQuotaTotal()) %></td>
					</tr>
					<% index++; %>
				</logic:iterate>
				<%}%>
			</logic:iterate>
			<%if(!recordExist){ %>
			<tr><td align="center" class="tblfirstcol" colspan="9">No Records Found.</td></tr>
			<%}%>
		</table>
		</td>
	</tr>
	<%} %>
</table>