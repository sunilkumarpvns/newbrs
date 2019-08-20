<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<%@ page import="org.apache.struts.util.MessageResources"%>
<%//      MessageResources messageResources = getResources(request,"resultMessageResources");%>
<% 
	String localBasePath = request.getContextPath();
%>

<script>
	function validateSynchronize(){
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="alert.updatenetserversynchronizeconfigdetailupdatedjsp.update.query"/>';        
//        msg = "All Services and its Details within this Server would be Updated. Do you like to continue ? "
        var agree = confirm(msg);
        if(agree){
            javascript:location.href='<%=localBasePath%>/synchronizeNetServerConfig.do?netserverid=<bean:write name="netServerInstanceBean" property="netServerId"/>';
    		//document.forms[0].submit();
        }
	}
	
	
</script>

<script>
	function validateSynchronizeBack(){
        var msg;
        msg = '<bean:message bundle="alertMessageResources" key="all.services.details.wouldbe.updated"/>';        
//        msg = "All Services and its Details within this Server would be Updated. Do you like to continue ? "
        var agree = confirm(msg);
        if(agree){
            javascript:location.href='<%=localBasePath%>/synchronizeBackNetServerConfig.do?netserverid=<bean:write name="netServerInstanceBean" property="netServerId"/>';
    		//document.forms[0].submit();
        }
	}
</script>

<html:form action="/updateNetServerSynchronizeConfigDetail">
	<html:hidden name="updateNetServerSynchronizeConfigDetailForm"
		styleId="action" property="action" />
	<html:hidden name="updateNetServerSynchronizeConfigDetailForm"
		styleId="netServerId" property="netServerId" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<bean:define id="netServerInstanceBean" name="netServerInstanceData"
			scope="request"
			type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData" />
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td class="tblheader-bold" colspan="2"><bean:message
								bundle="servermgrResources"
								key="servermgr.synchronizeserverdetails" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%"><bean:message
								bundle="servermgrResources" key="servermgr.admininterfaceip" /></td>
						<td align="left" class="labeltext" valign="top" width="32%"><bean:write
								name="netServerInstanceBean" property="adminHost" /> <!--  <input type="text" name="adminip" value="192.168.1.1"/> -->
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="10%"><bean:message
								bundle="servermgrResources" key="servermgr.admininterfaceport" /></td>
						<td align="left" class="labeltext" valign="top" width="32%"><bean:write
								name="netServerInstanceBean" property="adminPort" /> <!-- <input type="text" name="adminport" value="8090" /> -->
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					height="15%" align="right" class="captiontext">
					<tr>
						<td class="small-text-grey">Note : All Services and its
							Details within this Server would be Updated.</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle"><input type="button"
				name="c_btnSynchronize" onclick="validateSynchronize()"
				id="c_btnSynchronize" value=" Synchronize To" class="light-btn">
				<input type="button" name="c_btnSynchronizeBack"
				onclick="validateSynchronizeBack()" id="c_btnSynchronizeBack"
				value=" Synchronize From " class="light-btn"> <input
				type="reset" name="c_btnDeletePolicy"
				onclick="javascript:location.href='<%=localBasePath%>/viewNetServerInstance.do?netserverid=<bean:write name="netServerInstanceBean" property="netServerId"/>'"
				value="Cancel" class="light-btn"></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
	</table>
</html:form>
