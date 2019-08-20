<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.Date"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData"%>

<%
    String basePath = request.getContextPath();
%>

<script language="javascript">
setTitle('WELCOME');
</script>

<html>
<head>
<title>EliteCSM - Server Manager</title>
<LINK REL="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/mllnstyles.css">
<LINK REL="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/popcalendar.css">
<script language="javascript" src="<%=basePath%>/js/validation.js"></script>
<script language="javascript" src="<%=basePath%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=basePath%>/js/cookie.js"></script>
<script language="javascript" src="<%=basePath%>/js/popcalendar.js"></script>
<script language="javascript" src="<%=basePath%>/js/openhelp.js"></script>

<%
	String bLicenseAlert = (String)request.getSession().getAttribute("bLicenseAlert");
	String bLicenseAlertForPopup = (String)request.getSession().getAttribute("bLicenseAlertForPopup");
	String bLicenseExpireAlert = (String)request.getSession().getAttribute("bLicenseExpireAlert");
%>

<script language="javascript">

$(document).ready(function() {
	var html  = $.ajax({
		   type: "POST",
		   url: "<%=basePath%>/licenseExpireAlert.do?",
		   async:false,
		   data:""
	}).responseText;
		var div = document.getElementById("messageTypeDiv");
		$(div).append(html);		
		if(<%=bLicenseAlertForPopup%> == true) {
			popup();
		}
});

function popup() {	
	$.fx.speeds._default = 1000;
	document.getElementById("licenseExpiryDiv").style.visibility = "visible";		
	$( "#licenseExpiryDiv" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 250,
		width: 500,		
		buttons:{
								
            'Ok': function() {
		             $(this).dialog('close');
            }                
		    
        },
    	open: function() {
        		        	
    	},
    	close: function() {
    		
    	}			
	});
	$( "#licenseExpiryDiv" ).dialog("open");
}	

function storeDOM()
{
	pagecontent=('<pre><html>' + (document.documentElement.innerHTML) + '</html></pre>');
	
}
</script>

</head>


<body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0"
	marginwidth="0" marginheight="0"
	onLoad="MM_preloadImages('images/csv-hover.jpg','images/pdf-hover.jpg','images/html-hover.jpg','images/filter-hover.jpg','images/previous-hover.jpg','images/next-hover.jpg','images/dnarrow-y.jpg','images/sublinks-dnarrow.jpg','images/sublinks-uparrow.jpg'); collapseAllRows();"
	onhelp="openHelpPage();return false;" onBlur="storeDOM()">
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td width="100%" class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="small-gap" width="7">&nbsp;</td>
								</tr>
								<tr>
									<td width="10">&nbsp;</td>
									<td width="100%">
										<table width="100%">
											<tr height=350px>
												<td>&nbsp;</td>
											</tr>

										</table>
									</td>
									<%-- td width="168" class="grey-bkgd" valign="top">&nbsp;
							 
						    </td--%>
								</tr>
								<tr>
									<td colspan="3" class="small-gap">&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>

	<div id="licenseExpiryDiv" style="display: none;"
		title="License Expiry Notification">
		<div id="messageTypeDiv"></div>
	</div>