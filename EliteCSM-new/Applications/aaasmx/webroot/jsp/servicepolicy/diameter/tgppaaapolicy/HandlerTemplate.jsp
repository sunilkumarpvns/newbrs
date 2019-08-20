<!-- Add Handler Popup -->
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
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.authenticationhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="authorizationHandler" name="servicehandler" value="AuthorizationHandler"/>
							<label for="authorizationHandler">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.authorizarionhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="profileLookUp" name="servicehandler" value="ProfileLookupDriver"/>
							<label for="profileLookUp">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.profilelookuphandler"/>
							</label>
						</td>
					</tr>
					 <tr>
						<td class="labeltext">
							<input type="radio" id="cdrHandler" name="servicehandler" value="CDRHandler"/>
							<label for="cdrHandler">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.cdrhandler"/>
							</label>
						</td>
					</tr> 
					<tr>
						<td class="labeltext">
							<input type="radio"  id="diameterConcurrencyHandler" name="servicehandler" value="ConcurrencyHandler"/>
							<label for="diameterConcurrencyHandler">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.concurrencyhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="diameterproxycomm" name="servicehandler" value="DiameterProxyCommunication"/>
							<label for="diameterproxycomm">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.diameterproxycommunicationhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="proxycomm" name="servicehandler" value="RadiusProxyCommunication"/>
							<label for="proxycomm">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.radiusproxycommunicationhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="diameterbroadcastcomm" name="servicehandler" value="DiameterBroadcastingCommunication"/>
							<label for="diameterbroadcastcomm">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.diameterbroadcastcommunicationhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio" id="broadcastcomm" name="servicehandler" value="RadiusBroadcastingCommunication"/>
							<label for="broadcastcomm">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.radiusbroadcastcommunicationhandler"/>
							</label>
						</td>
					</tr>
					<tr>
						<td class="labeltext">
							<input type="radio"  id="plugin" name="servicehandler" value="Plugin"/>
							<label for="plugin">
								<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.handlername.pluginhandler"/>
							</label>
						</td>
					</tr>
                   <tr>
				</table>
			</td>
		</tr>
	</table>
</div>