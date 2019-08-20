<%@page import="com.elitecore.elitesm.web.dashboard.form.DashboardForm" %>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.dashboard.data.DashboardData" %>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<% String basePath = request.getContextPath();%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<html>
<head>
<% DashboardForm dashboardForm = (DashboardForm) request.getAttribute("dashboardForm");
   List<DashboardData> dashboardList = dashboardForm.getDashboardList(); 
   List lstAccessGroupData = ((DashboardForm)request.getAttribute("dashboardForm")).getListAccessGroup();
   List listSearchStaff =((DashboardForm)request.getAttribute("dashboardForm")).getListSearchStaff();
%>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Manage Dashboard</title>
<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/dashboardui.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom.css" />
<link rel="stylesheet" type="text/css" href="<%=basePath%>/css/managedashboard.css" />
<script type="text/javascript">
var isValidName;
function verifyDashboardName(){
	var searchName=document.getElementById("dashboardName").value;
	isValidName=verifyInstanceName('<%=InstanceTypeConstants.DASHBOARD%>',searchName,'update','<%=dashboardForm.getDashboardId()%>','verifyNameDiv');
}

function submitForm(){
	if(!isValidName) {
		alert('Enter Valid Name');
		$('#dashboardName').focus();
		return false;
	}else{
		$("#dashboardFormId").submit();
	}
}
function submitForm(){
	if(!isValidName) {
		alert('Enter Valid Name');
		$('#dashboardName').focus();
		return false;
	}else{
		$("#dashboardFormId").submit();
	}
}

$(document).ready(function(){
	verifyDashboardName();
});

var frameHeader = top.frames["topFrame"].document.getElementById('frameHeader');
$(frameHeader).hide();

var dashboardInitTime = top.frames["topFrame"].document.getElementById('dashboardInitTime');
$(dashboardInitTime).show();

var dashboardInitTime = top.frames["topFrame"].document.getElementById('dashboardInitTime');
$(dashboardInitTime).text("Dashboard Init Time : " +dformat);

</script>
</head>
<body style="background-color: #FAFAFA;">
<html:form action="/dashboard?method=updateDashboard" styleId="dashboardFormId">
<html:hidden property="dashboardId" styleId="dashboardId" name="dashboardForm" />
<table  border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td class="sDashboardWidgetHeaderBarMain ui-widget-header">Manage Dashboard</td>
</tr>
<tr>
	<td align="center" style="padding-top: 20px;color:#015198;font-size: 18px;">
		Dashboard Configurations
	</td>
</tr>
<tr>
	<td align="left" style="padding-top: 20px;padding-left: 30px;">
		<div style="padding: 10px 10px 10px 10px;height:330px;border-width:1px;border-style:solid;border-color: #CCC;" class="mainDiv">
			<table border="0" cellspacing="0" cellpadding="0" width="80%" align="center">
				<thead>
					<tr>
						<th class="th_header" align="left">Edit & Share Dashboard</th>
						<th class="th_header" align="right" title="Get help about to configure dashboard"><img src="<%=basePath%>/images/ico_help.png"/></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td style="vertical-align: left;padding-top: 20px;">
							<table align="left">
								<tr>
									<td align="right" style="font-size: 12px;"><font style="color: red;">*</font>	
										<bean:message bundle="dashboardResources" key="dashboard.manage.name" /> :
									</td>
									<td align="left">
										<html:text name="dashboardForm" tabindex="1" styleId="dashboardName" property="dashboardName" size="30" maxlength="256" onblur="verifyDashboardName();" style="width:250px" />
										<font color="#FF0000"> *</font>
										<div id="verifyNameDiv" style="font-size: 12px;font: arial;"></div>
									</td>
								</tr>
								<tr>
									<td align="right" style="font-size: 12px;">
										<bean:message bundle="dashboardResources" key="dasboard.manage.name.desc" /> :
									</td>
									<td align="left">
										<html:textarea styleId="dashboardDesc" name="dashboardForm" property="dashboardDesc" cols="85" rows="4" style="width:250px" tabindex="12" />
									</td>
								</tr>
								<tr>
									<td align="right">
									</td>
									<td align="left" style="padding-left: 5px;">	
										<label class="dashboardHelpTag">	
											<bean:message bundle="dashboardResources" key="dasboard.manage.desc.desc" />
										</label>	
									</td>
								</tr>
								<tr>
									<td align="right" style="font-size: 12px;">
										<bean:message bundle="dashboardResources" key="dashboard.manage.startfrom" /> :
									</td>
									<td align="left">
										<html:select property="startFrom" styleId="startFrom" name="dashboardForm" style="width: 250;" tabindex="18">
											<html:option value="Blank Dashboard">Blank Dashboard</html:option>
												<logic:iterate id="dashboardData" name="dashboardForm" property="dashboardList" type="com.elitecore.elitesm.datamanager.dashboard.data.DashboardData">
													<html:option value="${dashboardData.dashboardId}">
														<bean:write name="dashboardData" property="dashboardName" />
													</html:option>
												</logic:iterate>
										   </html:select>
									</td>
								</tr>
								<tr>
								 	<td align="right">
									</td>
									<td align="left" style="padding-left: 5px;">	
										<label class="dashboardHelpTag">
											<bean:message bundle="dashboardResources" key="dashboard.manage.startfrom.desc" />
										</label>	
									</td>
								</tr>
								<tr>
									<td align="right" style="font-size: 12px;">	
										<bean:message bundle="dashboardResources" key="dashboard.manage.addshares" /> :
									</td>
									<td align="left">
										<html:select property="addShares" styleId="addShares" name="dashboardForm" style="width: 250;" tabindex="18">
											<html:option value="Every One">Every One</html:option>
											<optgroup label="Group">
												<logic:iterate id="groupData" name="dashboardForm" property="listAccessGroup" type="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData">
													<html:option value="${groupData.name}">
														<bean:write name="groupData" property="name" />
													</html:option>
												</logic:iterate>												
											</optgroup>
											<optgroup label="Users">
												<logic:iterate id="staffData" name="dashboardForm" property="listSearchStaff" type="com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData">
													<html:option value="${staffData.userName}"> 
														<bean:write name="staffData" property="userName" />
													</html:option>
												</logic:iterate>
											</optgroup>
										</html:select>
									</td>
								</tr>
								<tr>
									<td align="right">
									</td>
									<td align="left" style="padding-left: 5px;">	
										<label class="dashboardHelpTag">
											<bean:message bundle="dashboardResources" key="dashboard.manage.addshares.desc" />
										 </label>	
									</td>
								</tr>
								<tr>
									<td align="right">&nbsp;</td>
									<td align="left" style="padding-left: 5px;">&nbsp;</td>
								</tr>
								<tr>
									<td align="right">
									</td>
									<td align="left" style="padding-left: 5px;">	
											<input type="button" name="btnSubmit" id="submitData" value="Update" class="manage_button" onclick="submitForm();"/>
											<input type="button" name="btnSubmit" id="submitData" value="Cancel" class="manage_button" onclick="javascript:location.href='<%=basePath%>/dashboard.do?method=initManage'"/>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
			
		</div>
	</td>
</tr>
</table>
</html:form>
</body>
</html>