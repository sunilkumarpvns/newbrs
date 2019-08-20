<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<html>
<head>
</head>
<body>
<%--START : Handler Template  --%>
<div id="addServiceHandlerPopup" style="display: none;" title="Add Service Handler">
	<table id="addServiceHandler" name="addServiceHandler" cellpadding="0" cellspacing="0" width="100%" >
		<tr>
			<td class="captiontext" valign="top" width="40%" style="padding-top: 3px;padding-right: 5px;" align="right">
				<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.handlerlist"/>
				<ec:elitehelp header="radiusservicepolicy.servicehandlerlist" headerBundle="servicePolicyProperties" text="radiusservicepolicy.servicehandlerlist" ></ec:elitehelp>
			</td>
			<td valign="top">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td class="labeltext">
							<input type="radio" id="authHandler" name="servicehandler" value="AuthenticationHandler"/>
							<label for="authHandler">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authenticationhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="authorizationHandler" name="servicehandler" value="AuthorizationHandler"/>
							<label for="authorizationHandler">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authorizationhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="profileLookUp" name="servicehandler" value="ProfileLookupDriver"/>
							<label for="profileLookUp">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.profilelookupdriver"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="cdrGenerationRdo" name="servicehandler" value="CDRGeneration"/>
							<label for="cdrGenerationRdo">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.cdrgeneration"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="ConcurrencyHandler" name="servicehandler" value="ConcurrencyHandler"/>
							<label for="ConcurrencyHandler">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.concurrencyhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="proxycomm" name="servicehandler" value="ProxyCommunication"/>
							<label for="proxycomm">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.proxycommunication"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="broadcastcomm" name="servicehandler" value="BroadcastingCommunication"/>
							<label for="broadcastcomm">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.broadcastcommunication"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio"  id="pluginRdo" name="servicehandler" value="Plugin"/>
							<label for="pluginRdo">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio"  id="coaDMGenRdo" name="servicehandler" value="coaDMGen"/>
							<label for="coaDMGenRdo">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.coadmhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio"  id="radConcurrency" name="servicehandler" value="RadiusIMDGConcurrency"/>
							<label for="radConcurrency">
								<bean:message bundle="servicePolicyProperties" key="radius.imdg.concurrency.handler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="statefulProxy" name="servicehandler" value="StatefulProxyCommunication">
							<label for="statefulProxy">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxy.handler" />
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="statefulBroadcastProxy" name="servicehandler" value="StatefulProxyBroadcastCommunication">
							<label for="statefulBroadcastProxy">
								<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.stateful.proxy.broadcast.handler" />
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