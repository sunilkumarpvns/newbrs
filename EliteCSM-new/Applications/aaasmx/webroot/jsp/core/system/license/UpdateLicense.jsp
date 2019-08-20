<%@page import="com.elitecore.elitesm.web.core.system.license.forms.LicenseForm"%>
<%@page import="com.elitecore.license.base.commons.LicenseConstants"%>
<%@page import="com.elitecore.license.base.commons.LicenseTypeConstants"%>
<%@page import="com.elitecore.license.base.LicenseData" %>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<link rel="stylesheet" href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css">
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
	LicenseForm licenseForm = (LicenseForm)request.getAttribute("licenseForm");
	List<LicenseData> licenseData = licenseForm.getLicenseData();
%>

<link rel="stylesheet" href="<%=basePath%>/css/font/font-awesome.css" />
<link href="<%=request.getContextPath()%>/js/fileupload/filer/font/jquery-filer.css" type="text/css" rel="stylesheet" />
<link href="<%=request.getContextPath()%>/js/fileupload/filer/jquery.filer-dragdropbox-theme.css" type="text/css" rel="stylesheet" />
<LINK REL="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" />
<link rel="stylesheet" href="<%=basePath%>/jquery/development/themes/base/jquery.ui.all.css" />

<script type="text/javascript" src="<%=basePath%>/js/calender/jquery-ui.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/fileupload/filer/jquery.filer.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/plugin/radius-groovy-plugin.js"></script>

<script type="text/javascript">
    function uploadFile() {
        var filename = $("#file").val();
        if (filename.trim() == "") {
            alert("Please select a license file before upload");
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
            // (N.B. this conversion will not effect the value of the extension
            // on the file upload.)
            extension = extension.toLowerCase();
        }

        if (extension != "lic") {
            alert("Uploaded file foramt must have .lic extension")
            return false;
        } else if(confirm("Are you sure want to upload a license file ?")) {
        	$("#upload-form").submit();
        }
        else {
  			$("#file").val("");
        }

    }
    
    function download(obj) {
    	var fileVal = $(obj).closest("tr").find(".groovy-label-text").text().trim();
    	var path = "<%=basePath%>/viewLicenceAction.do?method=downloadLicense&filename="+fileVal;
    	window.location.href = path;
    }
    
    function downloadPubKey() {
    	var fileVal = "pub.key";
    	var path = "<%=basePath%>/viewLicenceAction.do?method=downloadPubKey";
    	window.location.href = path;
    }
    function deleteLicenseFile(obj) {
    	if(confirm("Are you sure want to delete?")) {
	    	var fileName = $(obj).closest("tr").find(".groovy-label-text").text();
	    	var path = "<%=basePath%>/viewLicenceAction.do?method=deleteLicenseFile&fileName="+fileName;
	    	window.location.href = path;
    	}
    }
    setTitle('<bean:message bundle="LicenseResources" key="license.label"/>');
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td class="table-header">
						<bean:message bundle="LicenseResources" key="license.information" />
					</td>
				</tr>
			</table>
		</td>
   </tr>
   <tr>
   		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<html:form styleId="upload-form" action="/viewLicenceAction.do?method=createLicence" method="post" enctype="multipart/form-data">
					<tr>
						<td class="captiontext" width="15%" align="left">Select File
							:</td>
						<td class="labeltext" align="left" style="padding-top: 5px;">
							<html:file styleId="file" property="upload" accept=".lic">
							</html:file>
						</td>
					</tr>
					<tr></tr>
					<tr>
						<td colspan="2" class="captiontext" valign="top" align="left" width="15%" style="padding-top: 20px; padding-left: 100px;">
							<input type="button" class="light-btn" id="upload-button" value="Upload License" onclick="return uploadFile();" /> 
							<input type="button" class="light-btn" value="Download License Key"	onclick="downloadPubKey()"></input>
						</td>
					</tr>
				</html:form>
			</table>
		</td>
   </tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="tblheader-bold">
		<bean:message bundle="LicenseResources" key="license.upload.information" />
		</td>
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
					<td align="center" class="tblheader-bold border-right-download" valign="top" width="15%">Delete</td>
				</tr>
				<html:hidden name="licenseForm" styleId="licenseName" property="licenseName" />
				<logic:notEmpty name="licenseForm" property="lstLicenseFile" >
					<% 	int iIndex = 0; %>
					<logic:iterate id="licFile" property="lstLicenseFile" name="licenseForm">
						<% 	iIndex++; %>
						<tr>
							<td align="center" class="tblfirstcol"><%=iIndex%></td>
							<td align="left" class="tblrows">
								<a	href="viewLicenceAction.do?method=fetchLicense&module=upgradeLicense&fileName=<%=licFile%>">
									<span class="groovy-label-text" onclick="viewLicenseData(this);"><%=licFile%></span>
								</a>
							</td>
							<td align="center" class="tblrows">
								<span class="downloadElement" onclick="download(this);"></span> 
							</td>
							<td align="center" class="tblfirstcol">
								<span> <i class="fa fa-trash-o license_delete" onclick="deleteLicenseFile(this);"></i> </span>
							</td>
						</tr>
					</logic:iterate>
				</logic:notEmpty>
				
				<logic:empty name="licenseForm" property="lstLicenseFile">
					<tr>
						<td align="center" class="tblfirstcol" colspan="4">
							No License file(s) found.
						</td>
					</tr>
				</logic:empty>
			</table>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="captiontext">
			<table align="left" width="97%" cellspacing="0" cellpadding="0">
				<tr>
					<td></td>
				</tr>
			</table>
		</td>
	</tr>
</table>


