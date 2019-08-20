<% String basePath = request.getContextPath();%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.dashboard.form.DashboardForm"%>
<%@page import="com.elitecore.elitesm.datamanager.dashboard.data.DashboardData"%>
<%@page import="com.elitecore.elitesm.datamanager.dashboard.data.ManageDashboardData" %>
<%@ page import="com.elitecore.elitesm.web.tableorder.TableOrderData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm"%>
<%@ page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Manage Dashboard</title>
    <script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/jquery-1.6.4.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>/jquery/jplugin/lib/jquery-ui-1.8.16.custom.min.js"></script> 
  	<link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/dashboardui.css" />
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/jquery/jplugin/themes/default/jquery-ui-1.8.2.custom.css" />
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/css/managedashboard.css" />
    <script src="<%=request.getContextPath()%>/js/commonfunctions.js"></script>
	<script src="<%=request.getContextPath()%>/jquery/OrderTable/jquery.tablednd.js"></script>

    <LINK REL="stylesheet" TYPE="text/css" href="<%=request.getContextPath()%>/css/tablednd.css">

 <%  DashboardForm dashboardForm = (DashboardForm) request.getAttribute("dashboardForm");
	 List<ManageDashboardData> dashboardList = dashboardForm.getDashboardDataList();
	 String imageName=null;
	 String currentUserName = ((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")).getSystemUserName().trim();
 %> 

<script type="text/javascript">
var isValidName=false;
function verifyDashboardName(){
	var searchName=document.getElementById("dashboardName").value;
	isValidName=verifyInstanceName('<%=InstanceTypeConstants.DASHBOARD%>',searchName,'create','','verifyNameDiv');
}
function submitForm(){
	if(!isValidName) {
		alert('Enter Valid Name');
		$('#dashboardName').focus();
		return false;
	}else if(!isMaximumLimitReached()){
			alert('You have reached maximum limit of dashboard and you are not allows to create any more.');
			return false;
	}else{
		$("#dashboardFormId").submit();
	}
}

function isMaximumLimitReached(){
	var response='';
	$.ajax({url:'<%=request.getContextPath()%>/dashboard.do?method=checkDashboardLimit',
	    type:'POST',
	    async:false,
	    success: function(transport){
	    	response = transport;
	  }
	});
	if(response == 'true'){
		return false;
	}else{
		return true;
	}
	return true;
}

function setDragAndDropTable(tableId){
	$("#"+tableId+" td:last").addClass("nodrop nodrag");
	$("#"+tableId).tableDnD();
}
var deletedDashboardArray = [];
function deleteDashboard(dashboardId){
	$('#dataDiv').find('#dashboard_row_'+dashboardId).remove();
	deletedDashboardArray.push(dashboardId);
}

$(document).ready(function(){
	setDragAndDropTable("dataDiv");
	$(".saveorder").click(function(){
		if(deletedDashboardArray.length > 0){
			var deleteDashboardList = (deletedDashboardArray.toString());
			 $.ajax({url:'<%=request.getContextPath()%>/dashboard.do?method=deleteDashboard',
		         type:'POST',
		         data:{dashboardIds:deleteDashboardList},
		         async:false,
		         success: function(transport){
		       }
		    });
		}
		
		saveOrder("dashboardDatasIdS", "<%=TableOrderData.MANAGEDASHBOARD%>", "dashboard.do?method=initManage");
	 });
	
	 $('.dashboardConfigTd').hover(function(){
		 $('#configMenu').css('display','block');
 	 },function(){
 		 $('#configMenu').css('display','none');
  	 }); 
	 
	 
	 $('.dashboardConfigCreateTd').hover(function(){
		 $('#configCreateMenu').css('display','block');
 	 },function(){
 		 $('#configCreateMenu').css('display','none');
  	 }); 
});
$(function() {
	$( "#dialog-message" ).dialog({
	autoOpen: false,
	modal: true,
	buttons: {
	Cancel: function() {
		$( this ).dialog( "close" );
	},
	Delete: function() {
		$( this ).dialog( "close" );
		}
	}
	});
	
	$(".delete").click(function(){
		$( "#dialog-message" ).dialog("open");
	});
	
	var configMenu=false;
	$("#configMenu").click(function(){
		if(configMenu == false){
			$('.mainDashboardDiv').css('display','none');
			$('#configMenuImage').attr('src','images/icon-maximize.png');
			configMenu=true;
		}else{
			$('.mainDashboardDiv').fadeIn(10);
			$('#configMenuImage').attr('src','images/icon-minimize.png');
			configMenu=false;
		}
	});
	
	var configCreateMenu=false;
	$("#configCreateMenu").click(function(){
		if(configCreateMenu == false){
			$('.mainconfigCreateMenu').css('display','none');
			$('#configCreateImage').attr('src','images/icon-maximize.png');
			configCreateMenu=true;
		}else{
			$('.mainconfigCreateMenu').fadeIn(10);
			$('#configCreateImage').attr('src','images/icon-minimize.png');
			
			configCreateMenu=false;
		}
	});
	
	$(".starimage").click(function(){
		var src=$(this).attr('src');
		var isActive="";
		var yellowImage="<%=basePath%>/images/star_yellow.gif";
		if(src==yellowImage){
			var imagePath='<%=basePath%>/images/white_star.png';
			$(this).attr('src',imagePath);
			isActive="I";
		}else{
			var imagePath='<%=basePath%>/images/star_yellow.gif';
			$(this).attr('src',imagePath);
			isActive="A";
		}
		 var dashboardId=$(this).attr('id');
		 $.ajax({url:'<%=request.getContextPath()%>/dashboard.do?method=activeInActiveDashboard',
	          type:'POST',
	          data:{isActive:isActive,dashboardId:dashboardId},
	          async:false,
	          success: function(transport){
	        }
	    });
		
	  }); 
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
<table  border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
	<td class="sDashboardWidgetHeaderBarMain ui-widget-header">
		<span class="manageheader">
			<bean:message bundle="dashboardResources" key="dashboard.manage" />
		</span>
	</td>
</tr>
<tr>
<td style="padding-left: 20px;padding-top: 20px;padding-right: 20px;" class="dashboardConfigTd">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="sDashboardWidgetHeaderBarMain ui-widget-header" style="cursor: pointer;">
		  <span style="cursor: pointer;">
		  		<bean:message bundle="dashboardResources" key="dashboard.dashboardconfiguration" />
		  </span>
		  <span class="right icons" style="text-align: right;padding-right: 5px;cursor: pointer;display: none" id="configMenu">
				<span>
		  			<img alt="minimize" src="<%=basePath%>/images/icon-minimize.png" id="configMenuImage" title="Minimize Dialog"/>
		 		 </span>
		 </span>
		</td>
	</tr>
	<tr>
		<td align="left"  style="padding-top: 10px;padding-left: 20px;padding-bottom: 10px;;border-style: solid;border-width: 1px;border-color: rgb(192, 192, 192);background-color: white;">
			<div style="padding: 10px 20px 10px 10px;height:auto;" class="mainDashboardDiv">
				<table border="0" cellspacing="0" cellpadding="0" width="100%" id="myinnerDiv" >
					<thead >
						<tr>
							<th class="th_header" width="25%">
								<bean:message bundle="dashboardResources" key="dashboard.manage.name" />
							</th>
							<th class="th_header" width="25%">
								<bean:message bundle="dashboardResources" key="dashboard.manage.author" />
							</th>
							<th class="th_header" width="25%">
								<bean:message bundle="dashboardResources" key="dashboard.manage.sharedwith" />
							</th>
							<th class="th_header" width="25%">
								<bean:message bundle="dashboardResources" key="dashboard.manage.operations" />
							</th>
						</tr>
					</thead>
				</table>
		    	<table border="0" cellspacing="0" cellpadding="0" width="100%" id="dataDiv">
		    		 <%if(dashboardList != null && dashboardList.size() > 0){
							for(ManageDashboardData dashboardData:dashboardList){ %>
		    		<tr id="dashboard_row_<%=dashboardData.getDashboardId()%>">	
		    				<td width="25%" class="labeltext tdHover" style="color:#015198;font-size: small;">
		    						<input type="hidden" name="dashboardDatasIdS" value="<%=dashboardData.getDashboardRelId()%>" id="dashboardDataId" />
		    						<%if(dashboardData.getIsActive().equals("A")){%> 
											<%imageName="star_yellow.gif";
									}else{ 
										imageName="white_star.png";
									} %>
								    <img src="<%=basePath%>/images/<%=imageName%>" class="starimage" id="<%=dashboardData.getDashboardId()%>" style="cursor: pointer;"/>
									<%=dashboardData.getDashboardName() %>
		    				</td>
		    				<td width="25%" class="labeltext">&nbsp;<%=dashboardData.getAuthor() %></td>
		    				<td width="25%" class="labeltext">&nbsp;&nbsp;<%=dashboardData.getAddShares()%></td>
		    				<td width="25%" class="labeltext" >&nbsp;&nbsp;
		    					<%if(currentUserName.equals(dashboardData.getAuthor().trim()) || dashboardForm.isManageAccessGroupId() ==  true){ %>
									<%-- <img src="<%=basePath%>/images/copy.png" title="copy" onclick="javascript:location.href='<%=basePath%>/jsp/dashboardwidgets/CopyDashboard.jsp'" style="cursor: pointer;"/> --%>
									<img src="<%=basePath%>/images/brightcopy.png"/>
			<%-- 						<img src="<%=basePath%>/images/gtk-edit.png" title="edit" style="cursor: pointer;" onclick="javascript:location.href='<%=basePath%>/jsp/dashboardwidgets/EditDashboard.jsp'"/>
			 --%>						<img src="<%=basePath%>/images/gtk-edit.png" title="edit" style="cursor: pointer;" onclick="javascript:location.href='<%=basePath%>/dashboard.do?method=initUpdateDashboardData&dashboardId=<%=dashboardData.getDashboardId()%>'"/>
									<a id="delete_link_<%=dashboardData.getDashboardId()%>" href="#" onclick="deleteDashboard('<%=dashboardData.getDashboardId()%>');"><img src="<%=basePath%>/images/cross.jpg" title="delete" style="cursor: pointer;" class="delete"/></a>
								<%}else{%>
									<img src="<%=basePath%>/images/brightcopy.png"/>
									<img src="<%=basePath%>/images/hideedit.png"/>
									<img src="<%=basePath%>/images/hidedelete.jpg"/>
								<%} %>
		    				</td>
		    		</tr>
		       <%}} %>
				</table>
				<div style="vertical-align: left;padding-top: 20px;padding-left: 440px;">
					<input type="button" name="btnSubmit" id="submitData" value="Apply Now" class="manage_button saveorder"/>
					<input type="button" name="btnSubmit" id="submitData" value="Cancel" class="manage_button"  onclick="javascript:location.href='<%=basePath%>/dashboard.do?method=initManage'"/>
				</div>
			</div>
		</td>
	</tr>
	</table>
 </td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
<tr>
<td style="padding-left: 20px;padding-right: 20px;" class="dashboardConfigCreateTd">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="sDashboardWidgetHeaderBarMain ui-widget-header" style="cursor: pointer;">
			<span style="cursor: pointer;">
				<bean:message bundle="dashboardResources" key="dashboard.create" />
			</span>
			<span class="right icons" style="text-align: right;padding-right: 5px;cursor: pointer;display: none;" id="configCreateMenu">
				<span>
		  			<img alt="minimize" src="<%=basePath%>/images/icon-minimize.png" id="configCreateImage" title="Minimize Dialog"/>
		 		 </span>
		 </span>
		</td>
	</tr>
	<tr>
		<td align="left" style="border-style: solid;border-width: 1px;border-color: rgb(192, 192, 192);">
			<div style="padding-top: 10px;padding-right:30px;height:auto;background-color: white;" class="mainconfigCreateMenu">
		    	<html:form action="/dashboard?method=create" styleId="dashboardFormId">
		    	<table border="0" cellspacing="0" cellpadding="0" width="80%" align="center">
				<thead>
					<tr>
						<th class="th_header" align="left">
							<bean:message bundle="dashboardResources" key="dashboard.newcreate" />
						</th>
						<th class="th_header" align="right" title="Get help about to configure dashboard"><img src="<%=basePath%>/images/ico_help.png"/></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td style="vertical-align: middle;padding-top: 20px;">
							<table align="center">
								<tr>
									<td align="right" style="font-size: 12px;">
										<bean:message bundle="dashboardResources" key="dashboard.manage.name" />:
									</td>
									<td align="left">
										<html:text name="dashboardForm" tabindex="1" styleId="dashboardName" property="dashboardName" size="30" maxlength="256" onblur="verifyDashboardName();" style="width:250px" />
										<font color="#FF0000"> *</font>
										<div id="verifyNameDiv" style="font-size: 12px;font: arial;"></div>
									</td>
								</tr>
								<tr>
									<td align="right">
									</td>
									<td align="left" style="padding-left: 5px;">	
										<label class="dashboardHelpTag">
											<bean:message bundle="dashboardResources" key="dasboard.manage.name.desc" />
										</label>	
									</td>
								</tr>
								<tr>
									<td align="right" style="font-size: 12px;">
										<bean:message bundle="dashboardResources" key="dashboard.manage.description" /> :
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
										<html:select property="startFrom" styleId="startFrom" name="dashboardForm" value="EliteAAA" style="width: 250;" tabindex="18">
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
										<bean:message bundle="dashboardResources" key="dashboard.manage.addshares" />:
									</td>
									<td align="left">
										<html:select property="addShares" styleId="addShares" name="dashboardForm" value="Every One" style="width: 250;" tabindex="18">
											<html:option value="Every One">Every One</html:option>
											<optgroup label="Group">
											<logic:iterate id="groupData" name="dashboardForm" property="listAccessGroup" type="com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData">
													<html:option value="${groupData.groupId}">
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
							</table>
						</td>
					</tr>
					<tr>
						<td style="vertical-align: left;padding-top: 20px;padding-left: 350px;">
							<input type="button" name="btnSubmit" id="submitData" value="Add" class="manage_button" onclick="submitForm();"/>
							<input type="button" name="btnSubmit" id="submitData" value="Cancel" class="manage_button" onclick="javascript:location.href='<%=basePath%>/dashboard.do?method=initManage'"/>
						</td>
					</tr>
				</tbody>
				</table>
				</html:form>
			</div>
		</td>
	</tr>
	</table>
 </td>
</tr>
<tr>
	<td>&nbsp;</td>
</tr>
</table>
</body>
</html>