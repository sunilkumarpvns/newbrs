<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@page isErrorPage="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>EliteCSM - Server Manager</title>

<link rel="stylesheet" href="<%=request.getContextPath()%>/js/calender/jquery-ui.css" />
<link REL="SHORTCUT ICON" HREF="<%=request.getContextPath()%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/calender/jquery.ui.datepicker.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/js/calender/jquery-ui-1.8.24.custom.css" />

<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js"></script>
<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.min.js" ></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-ui.min.js" ></script>

<style type="text/css">
.no-close .ui-dialog-titlebar-close {display: none }
.ui-dialog-titlebar .ui-dialog {padding: 0.2em,1em } 
.ui-widget {font-size: 0.8em } 
 .li{
 display: inline;
 }
</style>

<script  type="text/javascript">

$(function() {
	$( "#dialog-modal" ).dialog({
		open: function(event, ui) { $(".ui-dialog-titlebar-close").hide(); },
		height: 170,
		width:325,
		modal: true
	});
});
function breakout_of_frame()
{
  if (top.location != location) 
  {
    top.location.href = document.location.href ;
  }
}
window.history.forward();
function noBack() {
	window.history.forward(); 
}
window.location.hash="no-back-button";
window.location.hash="Again-no-back-button";
window.onhashchange=function(){window.location.hash="no-back-button";}

</script>
<link REL="SHORTCUT ICON" HREF="<%=request.getContextPath()%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
</head>
<body onload="breakout_of_frame();noBack();" onpageshow="if (event.persisted) noBack();" onunload="" background="#EEEEEE" >
<table width="100%" align="center">
<tr >
	<td width="30%"></td>
	<td width="40%" style="padding-top: 100px;">
			<div align="center" style="padding:20px;vertical-align: middle;border-color:#8A0808;border-style: solid;border-width: 1px;border-radius: 3px;text-align: center;background-color: white;" >
				<table align="center">
					<tr>
						<td align="left" style="vertical-align: top;padding-top: 5px;" ><img src="<%=request.getContextPath()%>/images/Block_Icon.png" width="25px" height="25px"/></td>
						<td>
							<table>
								<tr>
									<td align="left" style="font-size:medium;font-weight: bold;border-bottom-color: #BDBDBD;border-bottom-style: solid;border-bottom-width: 1px;" colspan="2">Malformed Request Detected.</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td colspan="2" align="left" style="font-size: 14px">
											Kindly ensure your computer is not affected from some virus or malware.
									</td>
								</tr>
								<tr>
									<td colspan="2" align="left" style="font-size: 14px">
									Malformed request detected at : 	<span style="font-weight: bold;"> <%=ConfigManager.requestHeader %> </span>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td colspan="2" align="left" style="font-size: 14px">
										<font>
											<a style="color:gray;text-decoration: underline;" href="<%=ConfigManager.getContactMailURIForMalformedUser(request)%>" >Contact your administrator for further assistance
											</a>
										</font>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
								
									<td style="text-align:  left;"><input type="button" value="Get me out of here !" onclick="javascript:location.href='<%=request.getContextPath()%>/logout.do?isRequestFromBacktoHome=true'"></td>
									<td>&nbsp;</td>
								</tr> 
							</table>
						</td>
					</tr>
				</table>
			</div>
	</td>
	<td width="30%"></td>
</tr>

<!-- <div id="dialog-modal" title="Malformed Request Detected !!" class="selector">
		<p><b><font style="color: red;">Malformed Request Detected and Blocked.</font></b></p>
		<p>Kindly ensure your computer is not affected from some virus or malware.</p>
		<p><font style="color:green;">Contact your administrator for further assistance</font></p>
	</div> -->

</table>
</body>
</html>