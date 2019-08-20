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
				<ec:elitehelp header="radiuspostservicepolicy.servicehandlerlist" headerBundle="servicePolicyProperties" text="radiuspostservicepolicy.servicehandlerlist" ></ec:elitehelp>
			</td>
			<td valign="top">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="labeltext">
							<input type="radio" id="postcdrGeneration" name="servicehandler" value="CDRGeneration"/>
							<label for="postcdrGeneration">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cdrgeneration"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio"  id="postcoaDMGen" name="servicehandler" value="coaDMGen"/>
							<label for="postcoaDMGen">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.coadmhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio"  id="postplugin" name="servicehandler" value="Plugin"/>
							<label for="postplugin">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin"/>
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