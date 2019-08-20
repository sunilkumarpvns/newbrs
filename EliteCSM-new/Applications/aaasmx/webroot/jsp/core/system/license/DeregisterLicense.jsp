<%@page import="com.elitecore.license.nfv.PresentableLicenseData"%>
<%@page import="com.elitecore.elitesm.web.core.system.license.forms.LicenseForm"%>
<%@page import="com.elitecore.elitesm.datamanager.core.system.license.data.SMLicenseData"%>
<%@page import="com.elitecore.license.base.commons.LicenseConstants"%>
<%@page import="com.elitecore.license.base.commons.LicenseTypeConstants"%>
<%@page import="com.elitecore.license.base.LicenseData" %>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
	String showData = (String)request.getAttribute("showData");
	LicenseForm uploadForm = (LicenseForm)request.getAttribute("uploadForm");
	if(showData == null) {
		List lstLicenseFiles = (List) request.getAttribute("serverlicenselist");
	}else {
		List<PresentableLicenseData> presentableLicenseDatas = uploadForm.getPresentableLicenseDatas();
	}
%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css">
<LINK REL="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery/development/themes/base/jquery.ui.all.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-ui.js"></script>
<script type="text/javascript">
$(document).ready(function () {
<%
	String hasData = (String)request.getAttribute("showData");
	if(hasData != null){
%>
	$("table[name='serverLicenseInfo']").hide();
	$("table[name='serverLicense']").show();
<%} else {%>
	$("table[name='serverLicenseInfo']").show();
	$("table[name='serverLicense']").hide();
<%	}%>
});
    function deRegister(obj) {
    	var instanceName = $(obj).closest("tr").find("span[name='instanceName']").text().trim();
    	$("#instanceName").val(instanceName);
    	if(confirm("Are you sure want to deregistered the license ?") == false) {
    		return false;
    	}
    }
    
    function viewLicenseData(obj) {
    	var id = $(obj).closest("tr").find(".register").val();
    	var path = "<%=basePath%>/viewServerLicenceAction.do?method=fetchLicense&id="+id;
    	window.location.href = path;
    }
    
    function back() {
    	var path = "<%=basePath%>/viewServerLicenceAction.do?method=getServerLicenceInformation";
    	window.location.href = path;
    }
    
</script>

<html:form styleId="upload-form" action="/viewServerLicenceAction.do?method=doDeregister" method="post" enctype="multipart/form-data">
	<html:hidden name="uploadForm" styleId="instanceName" property="instanceName" />
	<html:hidden name="uploadForm" styleId="id" property="id" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="table-header">Server Instance Information</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>
				<table width="70%" style="padding-left: 50px;display:show"  border="0" cellpadding="0" cellspacing="0" align="left" name="serverLicenseInfo">
					<tr>
						<td align="right" class="tblheader-bold border-right-download" valign="top" width="5%"><bean:message bundle="LicenseResources" key="license.srno" /></td>
						<td align="left" class="tblheader-bold border-right-download" width="50%" valign="top"><bean:message bundle="LicenseResources" key="license.servername" /></td>
						<td align="center" class="tblheader-bold border-right-download" valign="top"><bean:message bundle="LicenseResources" key="license.license" /></td>
						<td align="center" class="tblheader-bold border-right-download" valign="top"><bean:message bundle="LicenseResources" key="license.deregister" /></td>
					</tr>
					<logic:notEmpty name="uploadForm" property="smLicenceData" >
						<% 	int iIndex = 0; %>
						<logic:iterate id="licData" name="uploadForm" property="smLicenceData" type="com.elitecore.elitesm.web.core.system.license.forms.ServerInstanceStatus">
							<tr>
									<% 	iIndex++; %>
								<logic:equal name="licData" property="status" value="REGISTERED">
										<td align="center" class="tblfirstcol"><%=iIndex%></td>
										<td align="left" class="tblrows">
											<html:hidden name="licData" styleClass="register" property="id" />
												<span name="instanceName">
													<bean:write name="licData" property="name" />
												</span>
											</td>
										<td align="center" class="tblrows">
											<span>
												<i class="fa fa-file-text-o license_view" onclick="viewLicenseData(this);"></i>
											</span>
										</td>
										<td align="center" class="tblrows">
											<html:submit styleClass="light-btn" styleId="deregiter" value="Deregister" onclick="return deRegister(this);" />
										</td>
									</logic:equal>
								</tr>
								<tr>
								<logic:notEqual name="licData" property="status" value="REGISTERED"> 
									<td align="center" class="tblfirstcol"><%=iIndex%></td>
									<td align="left" class="tblrows">
										<html:hidden name="licData" styleClass="register" property="id" />
											<span name="instanceName">
													<bean:write name="licData" property="name" />
												</a>
											</span>
										</td>
									<td align="center" class="tblrows">
										<span>
										-
										</span>
									</td>
									<td align="center" class="tblrows">
										<span style="color:#686868"><b>N/A</b></span>
 									</td> 
								</logic:notEqual>
							</tr>
						</logic:iterate>
					</logic:notEmpty>
					<logic:empty name="uploadForm" property="smLicenceData" >
						<tr>
							<td align="center" class="tblfirstcol" colspan="4">
								No Server Instance(s) Found.
							</td>
						</tr>
					 </logic:empty>
				</table>
				<table width="70%" style="padding-left: 50px;display: none;" border="0" cellpadding="0" cellspacing="0" align="left" name="serverLicense">
				
				<tr>
					<td align="center" class="tblheader-bold border-right-download" valign="top" width="5%">
						<bean:message bundle="servermgrResources" key="servermgr.serialnumber" />
					</td>
					<td align="left" class="tblheader-bold border-right-download" valign="top" width="20%">
						<bean:message bundle="servermgrResources" key="servermgr.license.name" />
					</td>
					<td align="center" class="tblheader-bold border-right-download" valign="top" width="12%">
						<bean:message bundle="servermgrResources" key="servermgr.license.version" />
					</td>
					<td align="left" class="tblheader-bold border-right-download" valign="top" width="30%">
						<bean:message bundle="servermgrResources" key="servermgr.license.validity" />
					</td>
				</tr>
				<logic:notEmpty name="uploadForm" property="presentableLicenseDatas" >
					<% int iIndex = 0; %>
					<logic:iterate id="licData" name="uploadForm" property="presentableLicenseDatas" type="com.elitecore.license.nfv.PresentableLicenseData">
							<% iIndex++; %>
							<tr>
								<td align="center" class="tblfirstcol" valign="top"><%=iIndex%>
								</td>
								<td align="left" class="tblrows" valign="top">
									<bean:write name="licData" property="name" />&nbsp;
								</td>
	
								<td align="center" class="tblrows" valign="top">
									<bean:write name="licData" property="version" />&nbsp;
								</td>
	
								<td align="left" class="tblrows" valign="top" valign="top">&nbsp;
									<logic:notEqual value="<%=LicenseTypeConstants.NODE%>" name="licData" property="type">
										<%
	 										String strValue = "-";
	 												if (licData != null) { 
	 													strValue = licData.getValue().toString(); 
	 													if (strValue.contains("-1")) {
	 														strValue = "Unlimited";
	 													}
	 												}
	 								%> 
										<%=strValue%>
									</logic:notEqual>
									 <logic:equal value="<%=LicenseTypeConstants.NODE%>" name="licData" property="type">
										<%
	 										String strValue = "-";
	 											if (licData != null) {
	 												strValue = licData.getValue().toString();
	 												if (strValue.contains("-1")) {
	 													strValue = "Unlimited";
	 												} else {
	 													int ipindex = strValue.indexOf(LicenseConstants.PUBLIC_KEY_SEPRATOR + LicenseConstants.DEFAULT_ADDITIONAL_KEY);
	 													String ipaddress = strValue.substring(0, ipindex);
	 													if (ipaddress != null) {
	 														ipindex = ipaddress.lastIndexOf(LicenseConstants.PUBLIC_KEY_SEPRATOR);
	 														ipaddress = ipaddress.substring(0, ipindex);
	 														if (ipaddress != null) {
	 															ipindex = ipaddress.lastIndexOf(LicenseConstants.PUBLIC_KEY_SEPRATOR);
	 															ipaddress = ipaddress.substring(0, ipindex);
	 															strValue = "IP = " + ipaddress;
	 														}
	 													}
	 												}
	 											}
	 									%> 
										<%=strValue%>
	 								</logic:equal> 		
								</td>
							</tr>
						</logic:iterate>
					</logic:notEmpty>
					<logic:empty name="uploadForm" property="presentableLicenseDatas" >
						<tr>
							<td align="center" class="tblfirstcol" colspan="4">
								No License Data Found
							</td>
						</tr>
					</logic:empty>
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td> </td>
						<td> </td>
						<td align="center">
							<input type="button" class="light-btn" value="Back" onclick="back();" />								
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
