<%@ page import="java.util.Map"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<table cellpadding="0" cellspacing="0" border="0" width="350" id="checkIpAddressTable" align="center">
	<tr>
		<td align="left" class="tblheader-bold" valign="top">&nbsp;
			<bean:message bundle="ippoolResources" key="ippool.serialnumber" />
		</td>
		<td align="left" class="tblheader-bold" valign="top" >&nbsp;
			<bean:message bundle="ippoolResources" key="ippool.header" />
		</td>
	
	</tr>
	<logic:iterate id="ipPoolData" name="ipPoolDataList" indexId="lstIndex" type="com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData">
		<tr>
			<td align="center" class="tblfirstcol"><%=lstIndex+1%></td>
			<td align="left" class="tblrows">&nbsp;<bean:write name="ipPoolData" property="name" /></td>
		</tr>
	</logic:iterate>
</table>