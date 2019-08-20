<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<html>
<head>
</head>
<body>
<%--START : Handler Template  --%>
<div id="addPostServiceHandlerPopup" style="display: none;" title="Add Service Handler">
	<table id="addServiceHandler" name="addServiceHandler" cellpadding="0" cellspacing="0" width="100%" >
		<tr>
			<td class="captiontext" valign="top" width="40%" style="padding-top: 3px;padding-right: 5px;" align="right">
				<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.handlerlist"/>
				<ec:elitehelp header="tgppservicepolicy.postservicehandlerlist" headerBundle="servicePolicyProperties" text="tgppservicepolicy.postservicehandlerlist" ></ec:elitehelp>
			</td>
			<td valign="top">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="labeltext">
							<input type="radio" id="postcdrHandler" name="servicehandler" value="CDRHandler"/>
							<label for="postcdrHandler">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.cdrhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio"  id="postplugin" name="servicehandler" value="Plugin"/>
							<label for="postplugin">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.pluginhandler"/>
							</label>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>
<%--END : Handler Template  --%>

<%--START :Accounting Driver Template  --%>
<div id="acctDriverPopup" style="display: none;" title="Add Drivers">
	<table id="addAcctDriver" name="addAcctDriver" cellpadding="0" cellspacing="0"
		width="100%" class="box">
	</table>
</div>
<%--END :Accounting Driver Template  --%>
</body>
</html>