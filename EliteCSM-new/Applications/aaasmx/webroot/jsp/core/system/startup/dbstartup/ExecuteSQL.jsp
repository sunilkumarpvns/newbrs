<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.elitecore.elitesm.web.systemstartup.dbsetup.form.EliteAAAStartupDBSetupForm"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>EliteCSM - Server Manager</title>
		<link REL="SHORTCUT ICON" HREF="<%=request.getContextPath()%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap.min.css">
		<link href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" rel="stylesheet">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/startupConfiguration.css">
		
		<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.min.js"></script>
		<%
		  	EliteAAAStartupDBSetupForm eliteAAAStartupDBSetupForm = (EliteAAAStartupDBSetupForm)request.getAttribute("eliteAAAStartupDBSetupForm");
		  	String invalid = "";
		  	String errorMsg = ""; 
		  	String sqlExecuted = "";
		 	if(eliteAAAStartupDBSetupForm != null){
				invalid = String.valueOf(eliteAAAStartupDBSetupForm.isInvalidParamters()); 
		  		errorMsg = eliteAAAStartupDBSetupForm.getErrorMsg();
		  		sqlExecuted = String.valueOf(eliteAAAStartupDBSetupForm.getSqlExecuted());
		 	}
		%>
		<script type="text/javascript">
			$(document).ready(function(){
				$('#action').val('');
				$('#executingsqlFileProgress').hide();
				$('#noticeMsg').hide();
				$('#executesqlbtn').prop("disabled",false);
				$('#executesqlbtn').val("Execute");
				var invalid = <%=invalid%>
				if(invalid == true){
			    	$('#errorMsg').show();
			    	$('#errorMsg').focus();
				}
			}); 
			
			// for executing... button value change
			function DisplayProgressMessage(ctl, msg) {
			    $(ctl).prop("disabled", true);
			    $(ctl).text(msg);
			    return true;
			}
			
			function executeSQlFun(btn){
				
				$(btn).prop("disabled", true);
			    $(btn).val("Executing..");
			    $('#changedbpropertybtn').prop("disabled",true);
			   
				$('#action').val('execute');
				$('#executingsqlFileProgress').show();
				$('#noticeMsg').show();
				$('#startUpForm').submit();
			}
			
			function forwardDefaultSetup(){
				 window.location.href="<%=request.getContextPath()%>/startup.do";
			}
			
		</script>
	</head>
	<body class="form-background-color">
		<div class="header-black-div"></div>
		<div style="float: right;">
			<img src="<%=request.getContextPath()%>/images/Sterlite_Tech_Product.jpg" width="263" height="54" usemap="#Map" border="0">
		</div>
	
		<div class="heading">
			EliteCSM Database Configuration
		</div>
		<div class="col-sm-6 col-xs-offset-3 margin-top-10">
		
			<div class="panel panel-default">
				<% if("false".equals(sqlExecuted)) {%>	
				
				<div class="alert alert-danger alert-label-panel" id="errorMsg">
					<logic:notEmpty name="eliteAAAStartupDBSetupForm" property="errorMsg">		
						<strong><bean:write name="eliteAAAStartupDBSetupForm" property="errorMsg"/></strong>
					</logic:notEmpty> 
				</div>
					<div class="panel-heading">
						<strong>
							<bean:message bundle="descriptionResources" key="dbsetup.executesqltitle" /> 	
						</strong>
					</div>
				
					<div class="panel panel-success">
						<div class="panel-heading">
						<b>
						 <bean:message bundle="descriptionResources" key="dbsetup.executingsqlincreateduser" />
						</b>
						<div style="padding-left: 3%">
						* 	<bean:message bundle="descriptionResources" key="dbsetup.connectionurl" /> : <bean:write name="eliteAAAStartupDBSetupForm" property="connectionUrl"/><br/>
						* 	<bean:message bundle="descriptionResources" key="dbsetup.username" /> :  <bean:write name="eliteAAAStartupDBSetupForm" property="newUserName"/>
						</div>
						</div>
					</div>
					<div class="panel-body" style="padding-top: 0px">
						<p><b>Executing SQL</b></p>
						 <html:form action="/executeSQL.do" styleId="startUpForm">
							<html:hidden name="eliteAAAStartupDBSetupForm" styleId="connection url" property="connectionUrl" />
							<html:hidden name="eliteAAAStartupDBSetupForm" styleId="newUserName" property="newUserName" />
							<html:hidden name="eliteAAAStartupDBSetupForm" styleId="passWord" property="passWord" />
							<html:hidden name="eliteAAAStartupDBSetupForm" styleId="action" property="action" />
							<ul class="fa-ul" style="padding-left: 3%" >
								<li><i class="fa-li fa fa-check-square"></i>eliteaaa.sql</li>
								
								<logic:match value="postgres" name="eliteAAAStartupDBSetupForm" property="connectionUrl">
									<li><i class="fa-li fa fa-check-square"></i>eliteaaa-postgres.sql</li>
								</logic:match>
								<logic:match value="oracle" name="eliteAAAStartupDBSetupForm" property="connectionUrl">
									<li><i class="fa-li fa fa-check-square"></i>eliteaaa-oracle.sql</li>
								</logic:match>
								
								<li><i class="fa-li fa fa-check-square"></i>kpis.sql</li>
							</ul>
							<br/>
							<input type="button" class="btn btn-primary " style="float: right" onclick="executeSQlFun(this)" value="Execute" id="executesqlbtn"/>
						</html:form>
						<div align="center" class="progress_bar_css" id="executingsqlFileProgress" style="display: none">
							<img height="50" id="loading-image"
								src="<%=request.getContextPath()%>/images/ajaxloading.gif" />
						</div>
						
						<div id="noticeMsg" style="display: none" align="center">
							<span style="color: red">Please wait it will take few minutes, kindly do not close this window untill finish</span>
						</div>
					</div>
				
				<% } else{ %>
		
					<div class="alert alert-success">
						<strong> <bean:message bundle="descriptionResources"
								key="dbsetup.executedsqltitle" />
						</strong>
					</div>
		
					<div class="panel">
						<div class="panel-heading">
						<div style="padding-left: 15px">
						<ul class="fa-ul" >
								<li><i class="fa-li fa fa-check-square"></i>Created DB user</li>
								
								<logic:match value="postgres" name="eliteAAAStartupDBSetupForm" property="connectionUrl">
									<li><i class="fa-li fa fa-check-square"></i>Executed SQL files(eliteaaa.sql, eliteaaa-postgres.sql and kpis.sql)</li>
								</logic:match>
								<logic:match value="oracle" name="eliteAAAStartupDBSetupForm" property="connectionUrl">
									<li><i class="fa-li fa fa-check-square"></i>Executed SQL files(eliteaaa.sql, eliteaaa-oracle.sql and kpis.sql)</li>
								</logic:match>
								
							</ul>
						</div>
						</div>
					</div>
					<div style="margin-left: 28px">
						<p>
							<strong> kindly press Next button and go ahead for EliteCSM-Server Manager default setup</strong>
						</p>
					</div>
					
					<div class="panel-footer" align="right">
						<button type="button" class="btn btn-primary" onclick="forwardDefaultSetup()">Next</button>
					</div>
					
				<% } %>	
				
				<!-- Copyrights Section -->
					<div class="copyrightdiv">
				    	<p>
						    Copyright &copy; <a href="http://www.elitecore.com" target="_blank" style="color: white;">Sterlite Technologies Ltd.</a>
						</p>
				    </div>
				</div>
		</div>
	</body>
</html>