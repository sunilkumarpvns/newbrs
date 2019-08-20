<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@ page import="java.util.List"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData"%>
<%@ page
	import="com.elitecore.elitesm.web.servermgr.server.forms.ListNetServerInstanceForm"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
    String basePath = request.getContextPath();
    List<NetServerInstanceData> netServerInstanceList = (List<NetServerInstanceData>)request.getAttribute("netServerListForLicense");
    List<NetServerTypeData> netServerTypeList = (List<NetServerTypeData>)request.getAttribute("netServerTypeList");
    List lstNetServerInstanceData = ((ListNetServerInstanceForm) request.getAttribute("listNetServerInstanceForm")).getListServer();
	int iIndex = 0;
%>

<table width="100%" cellpadding="0" cellspacing="0" border="0"
	class="box">

	<% for(NetServerInstanceData netServerInstanceData : netServerInstanceList) {%>
	<tr>
		<td width="20px"></td>
		<td>
			<table width="100%" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td colspan="3" style="color: red;" class="labeltext">
						<%if(netServerInstanceData.getLicenseExpiryDays() == 0) {%> <font
						color="red"> <b> License will be expire today</b></font> <%}else if(netServerInstanceData.getLicenseExpiryDays() > 0){%>
						<font color="red"> <b> License will be expire after <%=netServerInstanceData.getLicenseExpiryDays()%>
								&nbsp; days
						</b></font> <%}else{%> <font color="red"> <b> License expire</b></font> <%}%>
					</td>
				</tr>

				<tr>
					<td width="30"></td>
					<td width="40%" class="labeltext"><bean:message
							bundle="servermgrResources" key="servermgr.servername" />:</td>
					<td class="labeltext"><%=netServerInstanceData.getName()%></td>
				</tr>
				<tr>
					<td></td>
					<td class="labeltext"><bean:message
							bundle="servermgrResources" key="servermgr.address" /> :</td>
					<td class="labeltext"><%=netServerInstanceData.getAdminHost()%>:<%=netServerInstanceData.getAdminPort()%>
					</td>

				</tr>

				<tr>
					<td></td>
					<td class="labeltext"><bean:message
							bundle="servermgrResources" key="servermgr.servertype" /> :</td>
					<td class="labeltext"><logic:iterate id="netServerTypeData"
							name="netServerTypeList"
							type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData">
							<logic:equal name="netServerTypeData" property="netServerTypeId"
								value="<%=netServerInstanceData.getNetServerTypeId()%>">
								<bean:write name="netServerTypeData" property="name" />
							</logic:equal>
						</logic:iterate></td>
				</tr>
				<tr>
					<td></td>
				</tr>
			</table>
		</td>
	</tr>
	<%}%>
</table>





