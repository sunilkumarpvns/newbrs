<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.externalsystem.data.*"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData"%>
<%@page import="com.elitecore.elitesm.util.constants.ExternalSystemConstants"%>
<%@page import="com.elitecore.core.systemx.esix.udp.StatusCheckMethod" %>



<%
	//String basePath = request.getContextPath();
	ESITypeAndInstanceData esiTypeInstance = (ESITypeAndInstanceData)session.getAttribute("esiTypeInstance");
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
		<td valign="top" align="right">
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			height="15%">
			<tr>
				<td class="tblheader-bold" colspan="4" height="20%">
					<bean:message bundle="externalsystemResources" key="esi.view" />
				</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.name" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="name" /></td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.description" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="description" />&nbsp;</td>
			</tr>
			
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.esitype" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="esiTypeName" />&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.address" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="address" />&nbsp;</td>
			</tr>			
			
			<tr>
				<%if(esiTypeInstance.getEsiTypeId() == ExternalSystemConstants.AUTH_PROXY || esiTypeInstance.getEsiTypeId() == ExternalSystemConstants.ACCT_PROXY){%>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.sharedsecret" /></td>
				<td class="tblcol" width="30%" height="20%"><bean:write name="updateESIInstanceForm" property="sharedSecret" />&nbsp;</td>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.realmnames" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="realmNames" />&nbsp;</td>
				<%} else {%>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.sharedsecret" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="sharedSecret" />&nbsp;</td>
				
				<%} %>
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.timeout" /></td>
				<td class="tblcol" width="30%" height="20%"><bean:write name="updateESIInstanceForm" property="timeout" />&nbsp;</td>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.retrylimit" /></td>
				<td class="tblcol" width="30%" height="20%"><bean:write name="updateESIInstanceForm" property="retryLimit" />&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.statuscheckduration" /></td>
				<td class="tblcol" width="30%" height="20%"><bean:write name="updateESIInstanceForm" property="statusCheckDuration" />&nbsp;</td>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.expiredrequestlimitcount" /></td>
				<td class="tblcol" width="30%" height="20%"><bean:write name="updateESIInstanceForm" property="expiredRequestLimitCount" />&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.statuscheckmethod" /> </td>
				<td class="tblcol" width="30%" height="20%" colspan="3">
					<logic:iterate id="statusCheckMethodInst"  collection="<%=StatusCheckMethod.VALUES %>" >
						<%if(esiTypeInstance.getStatusCheckMethod() == (((StatusCheckMethod)statusCheckMethodInst).id)){ %>
							<%=((StatusCheckMethod)statusCheckMethodInst).name%>
						<%}%>
					</logic:iterate>
				</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.packetbytes" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="packetBytes" />&nbsp;</td>
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.minlocalport" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="minLocalPort" />&nbsp;</td>				
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.supportedattribute" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="supportedAttribute" />&nbsp;</td>				
			</tr>
			<tr>
				<td class="tblfirstcol" width="25%" height="20%"><bean:message bundle="externalsystemResources" key="esi.unsupportedattribute" /></td>
				<td class="tblcol" width="30%" height="20%" colspan="3"><bean:write name="updateESIInstanceForm" property="unSupportedAttribute" />&nbsp;</td>				
			</tr>

		</table>
		</td>
	</tr>
</table>