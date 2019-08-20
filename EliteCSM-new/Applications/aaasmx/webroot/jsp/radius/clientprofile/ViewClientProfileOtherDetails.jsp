<%@ page import="java.util.*" %>








<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
	<bean:define id="radiusClientProfileBean" name="radiusClientProfileData" scope="request" type="com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData" /> 
	<tr>
		<td align="right" valign="top"> 
			<table cellpadding="0" cellspacing="0" border="0" width="100%" >
 				
				<tr> 
		            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="radiusResources" key="radius.clientprofile.viewotherinformation"/></td>
	            </tr>
					
					<tr>
						<td class="tblfirstcol" width="31%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.dnslist"/>
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="dnsList" />&nbsp;
						</td>
					</tr>
					<tr>
						<td class="tblfirstcol" width="31%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.useridentity" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="userIdentities" />&nbsp;
						</td>
					</tr>
					<tr>
						<td class="tblfirstcol" width="31%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.prepaidstandard" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="prepaidStandard" />&nbsp;
						</td>
					</tr>
					<tr>
						<td class="tblfirstcol" width="31%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.clientpolicy" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="clientPolicy" />&nbsp;
						</td>
					</tr>
					<tr>
						<td class="tblfirstcol" width="31%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.hotlinepolicy" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="hotlinePolicy" />&nbsp;
						</td>
					</tr>
					<tr>
						<td class="tblfirstcol" width="31%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.dhcpaddress" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="dhcpAddress" />&nbsp;
						</td>
					</tr>
					<tr>
						<td class="tblfirstcol" width="31%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.haaddress" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="haAddress" />&nbsp;
						</td>
					</tr>
					<tr> 
		           		 <td class="tblheader-bold" colspan="4" height="20%">
		           		 	<bean:message bundle="radiusResources" key="radius.clientprofile.dynaauthconfig"/>
		           		 </td>
	           		 </tr>
	           		 <tr>
						<td class="tblfirstcol" width="30%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.port" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<logic:notEqual name="radiusClientProfileBean" property="dynAuthPort" value="0">
								<bean:write name="radiusClientProfileBean" property="dynAuthPort"/>
							</logic:notEqual>
							&nbsp;
						</td>
					</tr>
					 <tr>
						<td class="tblfirstcol" width="30%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.coasupportedattribute" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="coaSupportedAttributes" />&nbsp;
						</td>
					</tr>
					 <tr>
						<td class="tblfirstcol" width="30%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.coaunsupportedattribute" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="coaUnsupportedAttributes" />&nbsp;
						</td>
					</tr>
					<tr>
						<td class="tblfirstcol" width="30%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.dmsupportedattribute" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="dmSupportedAttributes" />&nbsp;
						</td>
					</tr>
					<tr>
						<td class="tblfirstcol" width="30%" height="20%">
							<bean:message bundle="radiusResources" key="radius.clientprofile.dmunsupportedattribute" />
						</td>
						<td class="tblcol"  height="20%" colspan="3">
							<bean:write name="radiusClientProfileBean" property="dmUnsupportedAttributes" />&nbsp;
						</td>
					</tr>
				</table>
		</td>
	</tr>
</table>
	
    