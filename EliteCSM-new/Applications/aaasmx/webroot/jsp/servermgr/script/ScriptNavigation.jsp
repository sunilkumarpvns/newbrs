<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData"%>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
		<td colspan="2" valign="top">
			<%
				String navigationBasePath = request.getContextPath();
				ScriptInstanceData scriptInstanceData =(ScriptInstanceData)session.getAttribute("scriptInstanceData");
		
				String updateScriptInstance  = navigationBasePath+"/script.do?method=initUpdate&scriptId="+scriptInstanceData.getScriptId();	
				String viewScriptInstance = navigationBasePath+"/script.do?method=viewScript&action=view&scriptId="+scriptInstanceData.getScriptId();	
				String viewScriprHistory = navigationBasePath+"/script.do?method=viewScriptHistory&scriptId="+scriptInstanceData.getScriptId()+"&auditUid="+scriptInstanceData.getAuditUId()+"&name="+scriptInstanceData.getName();	
			%>

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" >
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow" />
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateScriptInstance%>">
											<bean:message bundle="scriptResources" key="script.updatescript" />
										</a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
						<bean:message key="general.view" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" >
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewScriptInstance%>">
											<bean:message bundle="scriptResources" key="script.viewscript" />
										</a>
									</td>
								</tr>
								<tr>
									<td class="subLinks">
										<a href="<%=viewScriprHistory%>">
											<bean:message bundle="scriptResources" key="script.history" />
										</a>
									</td>
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</td>
	</tr>

</table>

