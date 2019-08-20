<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%@ page import="java.util.*" %>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="gatewayBean" name="gatewayData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData" />
    <tr>
		<td valign="top" align="right" valign="top" > 
			<table cellpadding="0" cellspacing="0" border="0" width="97%" >
			  <tr> 
	            <td class="tblheader-bold" width="30%" height="20%" colspan="2"><bean:message  bundle="gatewayResources" key="gateway.summary" /></td>
	          </tr>
			  <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="gatewayResources" key="gateway.creategateway" /></td>
	            <td class="tblcol" width="70%" height="20%"><bean:write name="gatewayBean" property="gatewayName"/>&nbsp;</td>
	          </tr>	          	
			  <tr> 
	            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="gatewayResources" key="gateway.ipaddress" /></td>
	            <td class="tblcol" width="70%" height="20%"><bean:write name="gatewayBean" property="connectionUrl"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol"  height="20%"><bean:message  bundle="gatewayResources" key="gateway.description" /></td>
	            <td class="tblcol" height="20%" ><bean:write name="gatewayBean" property="description"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" height="20%"><bean:message  bundle="gatewayResources" key="gateway.commprotocol" /></td>
	            <td class="tblcol"  height="20%" ><bean:write name="gatewayBean" property="commProtocol"/>&nbsp;</td>
	          </tr>
			</table>
		</td>
    </tr>
</table>
