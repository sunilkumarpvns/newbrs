<%@page import="com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.EliteAAASetupAction"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.EliteAAASetupAction.MODULE_STATUS"%>
<%@page import="com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.EliteAAASetupAction.MODULE_NAME_CONSTANTS"%>
<%@page import="com.elitecore.commons.base.Strings"%>
<%@page import="com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager"%>
<%@page import="com.elitecore.elitesm.web.systemstartup.defaultsetup.form.EliteAAASetupForm"%>
<%@page import="com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager"%>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="java.io.Console"%>
<%@page import="java.util.List" %>
<%@page import="com.elitecore.elitesm.hibernate.core.base.HBaseDataManager"%>
<%@page import ="com.elitecore.elitesm.web.systemstartup.defaultsetup.util.DefaultSetupClassNameAndProperty" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@taglib prefix="ec" uri="/elitetags" %>
<html lang="en">

<head>
  <meta name="viewport" content="width=device-width, initial-scale=1">
 <title>EliteCSM - Server Manager</title>
 <link REL="SHORTCUT ICON" HREF="<%=request.getContextPath()%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap.min.css">
 <link href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
 <link href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" rel="stylesheet">
 <script src="<%=request.getContextPath()%>/jquery/nanoscroll/jquery.min.js"></script>
 <script src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
 <link rel="stylesheet" href="<%=request.getContextPath()%>/css/startupConfiguration.css">
 <script src="<%=request.getContextPath()%>/js/bootstrap/bootstrapValidator.min.js"></script>
 <link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrapValidator.min.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/viewpage/elitecsm.viewpage.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/helptag/elitecsm.openhelp.js"></script>
<script src="<%=request.getContextPath()%>/jquery/nanoscroll/jquery-ui.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/openhelp.js"></script>
  
  
 <%  EliteAAASetupForm eliteAAASetupForm=(EliteAAASetupForm)request.getAttribute("eliteAAASetupForm");
 	String isSuccess = null;
 	if(eliteAAASetupForm != null){
 		if(Strings.isNullOrEmpty(eliteAAASetupForm.getSuccess()) == false){
 			isSuccess = eliteAAASetupForm.getSuccess();
 		}
 	}
 %>

<script type="text/javascript">

     function validateForm(){
		var isValid = false;
		 	var pwd = $('#password').val();
	 		var conPwd = $('#confirmPassword').val();
	 		 
	 		if(pwd == conPwd){
	 			isValid = true;
	 			$('#panelHeading').hide();
	 		} else {
	 			$('#panelHeading').show();
	 			isValid = false;
	 		} 
	 		
	 		return isValid;
 	 } 
     
     function skipDetailsForm(){
			window.location.href="<%=request.getContextPath()%>/login.do";
		}
    
	$(document).ready(function() {
		var isCompleted = '<%= isSuccess%>';
				if(isCompleted === "true"){
					$("#mainDiv").hide();
					$("#secondDiv").show();
					$('#successMsg').show();
					$('#forwardToLogin').show();
				} else if (isCompleted === "false"){
					$("#mainDiv").hide();
					$("#secondDiv").show();
					$('#failurMsg').show();
					$('#forwardToLogin').show();
				}
			 
		 $('#confirmPassword').bind("cut copy paste",function(e) {
		     e.preventDefault();
		 });
		
		 $('#eliteAAASetupFormId').bootstrapValidator({
			 framework: 'bootstrap',
		        feedbackIcons: {
		            valid: 'glyphicon glyphicon-ok',
		            invalid: 'glyphicon glyphicon-remove',
		            validating: 'glyphicon glyphicon-refresh'
		        },
		        fields: {
		        	userName: {
		                validators: {
		                    notEmpty: {
		                        message: 'Username is required and cannot be empty'
		                    },
		                    regexp:{
		                    	regexp: '^[a-zA-Z0-9-_.@]*$',
		                        message: 'Not a valid name. Valid characters : A-Z, a-z, 0-9, ., -, _'
		                    }
		                }
		            },
		            password: {
		                validators: {
		                    notEmpty: {
		                        message: 'Password is required and cannot be empty'
		                    }
		                }
		            },
		            confirmPassword: {
		                validators: {
		                    notEmpty: {
		                        message: 'Password is required and cannot be empty'
		                    },
		            		identical: {
	                			field: 'password',
	                        		message: 'The Password and its confirm are not the same'
	                    		}
		                }
		            },
		            IPAddress:{
		            	validators: {
		                    ip: {
		                        message: 'Please enter a valid IP address'
		                    }
		                }
		            },
		            concurrentLoginLimit: {
		                validators: {
		                		digits: {
		                      	  message: 'Concurrent Login Limit number can contain digits only'
		                   		},
		                    	notEmpty: {
		                        	message: 'Concurrent Login Limit is required and cannot be empty'
		                    }
		                }
		            }
		        }
		    });
	});
	
</script>

</head>


<body class="form-background-color">
	<div class="logo-headline"></div>
	<div style="float: right;">
		<img src="<%=request.getContextPath()%>/images/Sterlite_Tech_Product.jpg" width="263" height="54" usemap="#Map" border="0">
	</div>
	
	<div class="heading">
		EliteCSM Default Configuration
	</div>
	

	<div class="col-sm-6 col-xs-offset-3 margin-top-10" id="mainDiv">
		<div class="panel panel-default">
			<div class="alert alert-danger disp-and-margin" id="panelHeading">
				 			The password and its confirm are not the same
			</div>
			<div class="alert alert-success disp-and-margin" id="panelSuccess">
  				<strong>Success!</strong> EliteAAA Default Configuration configured successfully.
			</div>
			
			<div class="alert alert-danger disp-and-margin" id="panelFailed">
  				<strong>Failed!</strong> EliteAAA Default Configuration failed to complete setup.
			</div>
			<div class="panel-heading"> <b> Default Subscriber Profile Records </b> </div>
				<div class="panel-body">
					<html:form action="/eliteAAASetup.do" styleId="eliteAAASetupFormId" styleClass="form-horizontal" onsubmit="return validateForm();">
					<input type="hidden" name="loginMode" value="2">
						<div class="form-group">
							<div class="col-sm-12">
								<label for="username">Username</label>
								<strong>
									<ec:elitehelp headerBundle="descriptionResources" text="username.help" header="setup.username" />
								</strong>
								<html:text property="userName" name="eliteAAASetupForm" styleId="username" styleClass="form-control" maxlength="64"></html:text>
							</div>
						</div>

						<div class="form-group">
							<div class="col-sm-6">
								<label for="password">Password</label>
								<strong>
									<ec:elitehelp headerBundle="descriptionResources" text="password.help" header="setup.password" />
								</strong>
								<html:password property="password" name="eliteAAASetupForm" styleId="password" styleClass="form-control"></html:password>
							</div>
							<div class="col-sm-6">
								<label for="confirmPassword">Confirm Password</label>
								<html:password property="confirmPassword" name="eliteAAASetupForm" styleId="confirmPassword" styleClass="form-control"></html:password>
							</div>
						</div>
						<div class="form-group">
							<div class="col-sm-6">
								<label for="MACAddress">MAC Address Check</label>
								<strong>
									<ec:elitehelp headerBundle="descriptionResources" text="macaddress.help" header="setup.macaddress" />
								</strong>
								<html:text property="MACAddress" name="eliteAAASetupForm" styleId="MACAddress" styleClass="form-control"></html:text>
							</div>
							<div class="col-sm-6">
								<label for="IPAddress">IPv4 Address Reply</label>
								<strong>
									<ec:elitehelp headerBundle="descriptionResources" text="ipv4address.help" header="setup.ipv4address" />
								</strong>
								<html:text property="IPAddress" name="eliteAAASetupForm" styleId="IPAddress" styleClass="form-control"></html:text>
							</div>
						</div>

						<div class="form-group">
							<div class="col-sm-12">
								<label for="concurrentLoginLimit">Concurrent Login Limit</label>
								<strong>
									<ec:elitehelp headerBundle="descriptionResources" text="concurrentloginlimit.help" header="setup.concurrentloginlimit" />
								</strong>
								<html:text property="concurrentLoginLimit" name="eliteAAASetupForm" styleId="concurrentLoginLimit" styleClass="form-control" maxlength="2"></html:text>
							</div>
						</div>
						<div class="form-group" >
							<div class="col-sm-12 btn-toolbar">
								<html:submit styleId="btnSubmit" styleClass="btn btn-primary pull-left" value="Configure" property="btnSubmit"></html:submit>
								<html:button styleId="btnSkip" styleClass="btn btn-primary pull-left" value="Skip" property="btnSkip" onclick="skipDetailsForm()"></html:button>
							</div>
						</div>
				</html:form>
			</div>
			<div class="copyrightdiv">
			    	<p>
					    Copyright &copy;<a href="http://www.elitecore.com" target="_blank" style="color: white;">Sterlite Technologies Ltd.</a>
					</p>
			</div>
		</div>
	</div>
	
	<div class="col-sm-6 col-xs-offset-3 margin-top-10" id="secondDiv" style="display: none;">
		<div class="panel panel-default">
			<div class="panel-heading"> <b> Default Configuration </b> </div>
				<div class="panel-body">
					<table width="80%" cellspacing="0" cellpadding="0" border="0" class="tableBody">
						<% int index = 1;
						Map<MODULE_NAME_CONSTANTS,MODULE_STATUS> moduleStatusMap = EliteAAASetupAction.getModuleStatusMap();
						if(moduleStatusMap != null){
						
						for(MODULE_NAME_CONSTANTS key:moduleStatusMap.keySet()){  
							String module =key.module;
							String status =moduleStatusMap.get(key).status;
						%>
							<tr>
								<td width="5%" class="status-panel startupfont css_<%=status%>">
									<%=index++%>.
								</td>
								<td width="80%" class="status-panel startupfont css_<%=status%>">
									<span><%=module %></span>
								</td>
								<td width="15%" class="status-panel startupfont css_<%=status%> css_status_<%=status%>"><%=status %></td>
							</tr>
							<%
							}
						}
					%> 
					</table>
				</div>	
				<div class="alert alert-success panel-alert" id="successMsg">
	  				<strong><b>EliteCSM Default Configuration configured successfully. Kindly click on 'Login' To Continue</b></strong>
				</div>
				<div class="alert alert-danger panel-alert" id="failurMsg">
	  				<strong><b>Failed To Configured EliteCSM Default Configuration. Kindly click on 'Login' To Continue</b></strong>
				</div>

				<div class="panel-footer"  id="footerId" align="right">
					<%-- <html:button styleId="btnContinue" styleClass="btn btn-primary pull-right" value="Login" property="btnContinue" onclick="skipDetailsForm()" style="display: none;"></html:button> --%>
					<button type="button" class="btn btn-primary" onclick="skipDetailsForm()" style="display: none;" id="forwardToLogin">Login</button>
				</div>
				<div class="copyrightdiv">
			    	<p>
					    Copyright &copy;<a href="http://www.elitecore.com" target="_blank" style="color: white;">Sterlite Technologies Ltd.</a>
					</p>
			    </div>
			</div>
		</div>
</body>
</html>