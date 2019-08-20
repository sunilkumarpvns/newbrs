<%@ page
	import="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData"%>
<%@ page import="java.util.List"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>






<%
    String basePath = request.getContextPath();
    INetServerInstanceData iNetServerInstanceData = (INetServerInstanceData)request.getAttribute("iNetServerInstanceData");
    List netServerTypeList = (List)request.getAttribute("netServerTypeList");
%>

<script>
	function reponseUrl(value){
		if(value != null){
			document.forms[0].action=value;
			document.forms[0].submit();
		}else{
			history.back();
		}
	}
</script>
<table cellpadding="0" cellspacing="0" border="0" align="center"
	width="650">
	<tr>
		<td width="184">&nbsp;</td>
		<td width="27">&nbsp;</td>
		<td width="409">&nbsp;</td>
		<td width="30">&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td rowspan="3" valign="top">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td><img src="<%=basePath%>/images/error-top.jpg" border="0"></td>
				</tr>
				<tr>
					<td><img src="<%=basePath%>/images/success.jpg" border="0"></td>
				</tr>
				<tr>
					<td><img src="<%=basePath%>/images/success-btm.jpg" border="0"></td>
				</tr>
			</table>
		</td>
		<td><img src="<%=basePath%>/images/error-top1.jpg" height="24"></td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr>

		<td><img src="<%=basePath%>/images/error-top-left-curve.jpg"
			border="0"></td>
		<td background="<%=basePath%>/images/error-heading-bkgd.jpg"
			class="textbold">&lt;&lt;&lt; Success &gt;&gt;&gt;</td>
		<td><img src="<%=basePath%>/images/error-top-right-curve.jpg"
			border="0"></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan="4" valign="top"
			background="<%=basePath%>/images/error-bkgd.jpg">
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td width="5%">&nbsp;</td>
					<td width="89%" valign="top">
						<table type="tbl-list" width="100%">
							<tr>
								<td class="table-header" align="center" colspan="3"><bean:message
										bundle="servermgrResources"
										key="servermgr.serverinstancecreatedsuccessfully" /></td>
							</tr>
							<tr>
								<td class="labeltext" align="left" width="10%">&nbsp;</td>
								<td class="labeltext" align="left" width="45%"><bean:message
										bundle="servermgrResources" key="servermgr.servername" /></td>
								<td class="labeltext" align="left" width="45%"><bean:write
										name="iNetServerInstanceData" property="name" /></td>
							</tr>
							<tr>
								<td class="labeltext" align="left">&nbsp;</td>
								<td class="labeltext" align="left"><bean:message
										bundle="servermgrResources"
										key="servermgr.serveridentification" /></td>
								<td class="labeltext" align="left"><bean:write
										name="iNetServerInstanceData" property="netServerId" /></td>
							</tr>
							<tr>
								<td class=labeltext align="left">&nbsp;</td>
								<td class="labeltext" align="left"><bean:message
										bundle="servermgrResources" key="servermgr.servertype" /></td>
								<!-- 								<td class="labeltext" align="left" ><bean:write name="iNetServerInstanceData" property="netServerTypeId"/></td>   -->
								<td class="labeltext" align="left"><logic:iterate
										id="netServerTypeData" name="netServerTypeList"
										type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData">
										<logic:equal name="netServerTypeData"
											property="netServerTypeId"
											value="<%=iNetServerInstanceData.getNetServerTypeId()%>">
											<bean:write name="netServerTypeData" property="name" />
										</logic:equal>
									</logic:iterate></td>
							</tr>
							<tr>
								<td class="labeltext" align="left">&nbsp;</td>
								<td class="labeltext" align="left"><bean:message
										bundle="servermgrResources" key="servermgr.admininterfaceip" /></td>
								<td class="labeltext" align="left"><bean:write
										name="iNetServerInstanceData" property="adminHost" /></td>
							</tr>
							<tr>
								<td class="labeltext" align="left">&nbsp;</td>
								<td class="labeltext" align="left"><bean:message
										bundle="servermgrResources" key="servermgr.admininterfaceport" /></td>
								<td class="labeltext" align="left"><bean:write
										name="iNetServerInstanceData" property="adminPort" /></td>
							</tr>
							<tr>
								<td class="labeltext" align="left">&nbsp;</td>
								<td class="labeltext" align="left" valign="top"><bean:message
										bundle="servermgrResources" key="servermgr.serverhome" /></td>
								<td class="labeltext" align="left"><bean:write
										name="iNetServerInstanceData" property="serverHome" /></td>
							</tr>
							<tr>
								<td class="labeltext" align="left">&nbsp;</td>
								<td class="labeltext" align="left" valign="top"><bean:message
										bundle="servermgrResources" key="servermgr.javahome" /></td>
								<td class="labeltext" align="left"><bean:write
										name="iNetServerInstanceData" property="javaHome" /></td>
							</tr>
							<tr>
								<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td class="labeltext" valign="top" align="center" colspan="3">
									<%-- 									<input type="button" name="c_btnOK" onclick="javascript:location.href='<%=basePath%>/viewNetServerInstance.do?netserverid=<bean:write name="iNetServerInstanceData" property="netServerId"/>'" value="OK" class="light-btn" />									--%>
									<input type="button" name="c_btnOK"
									onclick="javascript:location.href='<%=basePath%>/listNetServerInstance.do'"
									value="OK" class="light-btn" />
								</td>
							</tr>
							<tr>
								<td class="small-text-grey" colspan="3">Note : Select
									Server and add Services.</td>
							</tr>
							<tr>
								<td align="left" class="labeltext" valign="top" align="center"
									valign="top" colspan="3">
									<div id="errorDetails" style="display: 'none'">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top"><p
														align="justify">&nbsp;</p></td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;</td>
											</tr>
										</table>
									</div>
								</td>
							</tr>
						</table>
					</td>
					<td width="6%">&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><img src="<%=basePath%>/images/error-btm-left-curve.jpg"
			border="0"></td>
		<td background="<%=basePath%>/images/error-btm-bkgd.jpg">&nbsp;</td>
		<td><img src="<%=basePath%>/images/error-btm-right-curve.jpg"
			border="0"></td>
	</tr>
</table>

