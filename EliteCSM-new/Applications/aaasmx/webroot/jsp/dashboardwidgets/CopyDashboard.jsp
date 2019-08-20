<% String basePath = request.getContextPath();%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Manage Dashboard</title>
  	<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/dashboardui.css" />
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom.css" />
      <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/managedashboard.css" />
</head>
<body style="background-color: #FAFAFA;">
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
		<div style="padding: 10px 10px 10px 10px;height:245px;" class="mainDiv">
			<table border="0" cellspacing="0" cellpadding="0" width="80%" align="center">
				<thead>
					<tr>
						<th class="th_header" align="left">Create New Dashboard</th>
						<th class="th_header" align="right" title="Get help about to configure dashboard"><img src="<%=basePath%>/images/ico_help.png"/></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td style="vertical-align: middle;padding-top: 20px;">
							<table align="center">
								<tr>
									<td align="right" style="font-size: 12px;"><font style="color: red;">*</font>Name :</td>
									<td align="left">
										<input type="text" />
									</td>
								</tr>
								<tr>
									<td align="right" style="font-size: 12px;">Description :</td>
									<td align="left">
										<textarea rows="3" cols="85"></textarea>
									</td>
								</tr>
								<tr>
									<td align="right" style="font-size: 12px;">Start From :</td>
									<td align="left">
										<select>
											<option>Blank Dashboard</option>
											<option selected>EliteAAA</option>
											<option>EliteDSC</option>
											<option>Instance</option>
										</select>
									</td>
								</tr>
								<tr>
									<td align="right" style="font-size: 12px;">Add Shares :</td>
									<td align="left">
										<select>
											<option>Every One</option>
											<optgroup label="Group">
												<option>Admin-Access-Group</option>
												<option>Support-User-Access-Group</option>
												<option>Operator-Access-Group</option>
												<option>Web-Service-Access-Group</option>
											</optgroup>
											<optgroup label="Users">
												<option>Admin</option>
												<option>Operator</option>
												<option>User1</option>
											</optgroup>
										</select>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
			<div style="vertical-align: left;padding-top: 20px;padding-left: 250px;">
				<input type="button" name="btnSubmit" id="submitData" value="Add" class="manage_button" onclick="javascript:location.href='<%=basePath%>/jsp/dashboardwidgets/ManageDashboard.jsp'"/>
				<input type="button" name="btnSubmit" id="submitData" value="Cancel" class="manage_button" onclick="javascript:location.href='<%=basePath%>/jsp/dashboardwidgets/ManageDashboard.jsp'"/>
			</div>
		</div>
	</td>
</tr>
<%-- <tr>
	<td>
		<img alt="test" src="<%=basePath%>/images/dashboards_manage_48.png">
	</td>
	<td style="color: rgb(60, 120, 181);font-size: 2em;line-height: 1.167;">Manage Dashboards</td>
	<td>wwww</td>
</tr>
<tr>
	<td>eeee</td>
	<td colspan="2">eeeee</td>
</tr> --%>
</table>
</body>
</html>