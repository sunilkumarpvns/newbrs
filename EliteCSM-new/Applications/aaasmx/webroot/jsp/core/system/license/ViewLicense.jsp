<%@page import="com.elitecore.elitesm.web.core.system.license.forms.LicenseForm"%>
<%@page import="com.elitecore.license.base.commons.LicenseConstants"%>
<%@page import="com.elitecore.license.base.commons.LicenseTypeConstants"%>
<%@page import="com.elitecore.license.base.LicenseData" %>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
	String showData = (String)request.getAttribute("showData");
	if(showData == null) {
		LicenseForm licenseForm = (LicenseForm)request.getAttribute("licenseForm");
		List<LicenseData> licenseData = licenseForm.getLicenseData();
	}
%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/font/font-awesome.css" />
<link href="<%=request.getContextPath()%>/js/fileupload/filer/font/jquery-filer.css" type="text/css" rel="stylesheet" />
<LINK REL="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/jquery/development/themes/base/jquery.ui.all.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-ui.js"></script>

<script type="text/javascript">
$(document).ready(function () {
	<%
	String data = (String)request.getAttribute("showData");
	if(data != null){
%>
	$("table[name='licenseInfo']").hide();
	$("table[name='licenseData']").show();
	<%} else {%>
	$("table[name='licenseInfo']").show();
	$("table[name='licenseData']").hide();
	<%}%>
});
    function uploadFile() {
        var filename = $("#file").val();
        if (filename.trim() == "") {
            alert("Please upload a file");
            return false;
        }
        // Use a regular expression to trim everything before final dot
        var extension = filename.replace(/^.*\./, '');
        // If there is no dot anywhere in filename, we would have extension == filename,
        // so we account for this possibility now
        if (extension == filename) {
            extension = '';
        } else {
            // if there is an extension, we convert to lower case
            //  this conversion will not effect the value of the extension
            // on the file upload.)
            extension = extension.toLowerCase();
        }

        if (extension != "lic") {
            alert("Upload file only in lic format.")
            return false;
        } else {
            $("#upload-form").submit();
        }
    }
    function download(obj) {
    	var fileVal = $(obj).closest("tr").find(".groovy-label-text").text().trim();
    	var path = "<%=basePath%>/viewLicenceAction.do?method=downloadLicense&filename="+fileVal;
    	window.location.href = path;
    }
    
    function back() {
    	<%  String module = (String)request.getAttribute("module"); %>
    	
    	<% if (module != null) {%>
    	var path = "<%=basePath%>/viewLicenceAction.do?method=getExistedLicenseData";
    	window.location.href = path;
    	
   		<% }else {%>
    	var path = "<%=basePath%>/viewLicenceAction.do?method=getLicenceData";
    	window.location.href = path;
    	<% }%>
    }
    
    setTitle('<bean:message bundle="LicenseResources" key="license.label"/>');
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    
	<tr>
		<td class="tblheader-bold"><bean:message bundle="LicenseResources" key="license.view.information"/></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>
			<table width="70%" style="padding-left: 50px;display:show" border="0" name="licenseInfo" cellpadding="0" cellspacing="0" align="left">
				<tr>
					<td align="right" class="tblheader-bold border-right-download" valign="top" width="5%">Sr.No.</td>
					<td align="left" class="tblheader-bold border-right-download" width="60%" valign="top">License File</td>
					<td align="center" class="tblheader-bold border-right-download" valign="top">Download License</td>
				</tr>
				<logic:notEmpty name="licenseForm" property="lstLicenseFile" >
					<% 	int iIndex = 0; %>
					<logic:iterate id="licFile" property="lstLicenseFile" name="licenseForm">
						<% 	iIndex++; %>
						<tr>
							<td align="center" class="tblfirstcol"><%=iIndex%></td>
							<td align="left" class="tblrows">
								<a	href="viewLicenceAction.do?method=fetchLicense&fileName=<%=licFile%>">
									<span class="groovy-label-text" onclick="viewLicenseData(this);"><%=licFile%></span>
								</a>
							</td>
							<td align="center" class="tblrows">
								<span class="downloadElement" onclick="download(this);"></span> 
							</td>
						</tr>
					</logic:iterate>
				</logic:notEmpty>
				
				<logic:empty name="licenseForm" property="lstLicenseFile">
					<tr>
						<td align="center" class="tblfirstcol" colspan="3">
							No Licnese file(s) found.
						</td>
					</tr>
				</logic:empty>
			</table>
			
			<table width="70%" style="padding-left: 50px;display:none" name="licenseData" border="0" cellpadding="0" cellspacing="0" align="left">
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

					<% int iIndex = 0; %>

					<logic:iterate id="licData" name="licenseForm" property="licenseData" type="com.elitecore.license.base.LicenseData">
						<% iIndex++; %>
					<tr>
						<td align="center" class="tblfirstcol" valign="top"><%=iIndex%>
						</td>
						<td align="left" class="tblrows" valign="top">
							<bean:write name="licData" property="displayName" />&nbsp;
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
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td> &nbsp;</td>
						<td> &nbsp;</td>
						<td align="center">
							<input type="button" class="light-btn" value="Back" onclick="back();" />								
						</td>
					</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>