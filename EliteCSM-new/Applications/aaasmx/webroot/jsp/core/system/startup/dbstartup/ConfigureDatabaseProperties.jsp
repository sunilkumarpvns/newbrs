<!DOCTYPE html>
<%@page import="com.elitecore.elitesm.web.systemstartup.dbsetup.form.EliteAAAStartupDBSetupForm"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@taglib prefix="ec" uri="/elitetags" %>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>EliteCSM - Server Manager</title>
  
  <link REL="SHORTCUT ICON" HREF="<%=request.getContextPath()%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap.min.css">
  <link href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css" rel="stylesheet"/> 
  <link href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" rel="stylesheet">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/startupConfiguration.css">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrapValidator.min.css" />
  
  <script src="<%=request.getContextPath()%>/jquery/nanoscroll/jquery.min.js"></script>
  <script src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
  <script src="<%=request.getContextPath()%>/js/bootstrap/bootstrapValidator.min.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/viewpage/elitecsm.viewpage.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/helptag/elitecsm.openhelp.js"></script>
  <script src="<%=request.getContextPath()%>/jquery/nanoscroll/jquery-ui.js"></script>
  <script language="javascript" src="<%=request.getContextPath()%>/js/openhelp.js"></script> 
 
  <%
  	EliteAAAStartupDBSetupForm eliteAAAStartupDBSetupForm = (EliteAAAStartupDBSetupForm)request.getAttribute("eliteAAAStartupDBSetupForm");
  	String invalid = "";
  	String errorMsg = ""; 
  	String createNewUserSelected ="";
 	if(eliteAAAStartupDBSetupForm != null){
		invalid = String.valueOf(eliteAAAStartupDBSetupForm.isInvalidParamters()); 
  		errorMsg = eliteAAAStartupDBSetupForm.getErrorMsg();
  		createNewUserSelected = String.valueOf(eliteAAAStartupDBSetupForm.getCreateNewUserSelected());
 	}
  %>
  
  <style type="text/css">
	.ui-button {
		background-color: transparent;
		border: none;
		background: none;
	}
	
	.ui-dialog-titlebar-close:hover {
		background-color: transparent;
		border: none;
		background: none;
	}
</style>
  
<script type="text/javascript">
  
  $(document).ready(function(){
		  $('#passWord').bind("cut copy paste",function(e) {
	          e.preventDefault();
	      });
		  $('#dbAdminPassword').bind("cut copy paste",function(e) {
	          e.preventDefault();
	      });
	  
		   $('#eliteAAAStartupDBSetupForm').bootstrapValidator({
	          feedbackIcons: {
	              valid: 'glyphicon glyphicon-ok',
	              invalid: 'glyphicon glyphicon-remove',
	              validating: 'glyphicon glyphicon-refresh'
	          },
	          fields: {
	        	  connectionUrl: {
	                  validators: {
	                      notEmpty: {
	                          message: 'Connection URL must be required'
	                      }
	                  }
	              },
	              userName: {
	                  validators: {
	                      notEmpty: {
	                          message: 'Username must be required'
	                      },
                          regexp:{
                              regexp: '^[a-zA-Z]{1,}[a-zA-Z0-9_]*$',
                              message: 'Invalid Username. Valid characters : [A-Z, a-z, 0-9, _]'
                          }
	                  }
	              },
	        	  passWord: {
	                  validators: {
	                      notEmpty: {
	                          message: 'Password must be required'
	                      },
                          regexp:{
                        	  regexp: '^[a-zA-Z]{1,}[a-zA-Z0-9_]*$',
                              message: 'Invalid Password. Valid characters : [A-Z, a-z, 0-9, _]'
                          }
	                  }
	              },
		          dbAdminUsername: {
		                validators: {
		                     notEmpty: {
		                         message: 'Database Admin Username must be required'
		                      }
		                  }
		              },
		              dbAdminPassword: {
		                  validators: {
		                      notEmpty: {
		                          message: 'Database Admin Password must be required'
		                      }
		                  }
		              },
		              dbfFileLocation : {
		            	  validators: {
		                      notEmpty: {
		                          message: 'Location(.dbf) file must be required'
		                      }
		                  }
		              }
	          }
	      });
	 
			var invalid = <%=invalid%>
			if(invalid == true){
		    	$('#errorMsg').show();
		    	$('#errorMsg').focus();
			}
			
			var checkboxValue = '<%=createNewUserSelected%>';
			$('#createUser').val(checkboxValue);
			
			if($('#createUser').val() == 'true'){
				// enable dbuser name and password fields
				$('#dbAdminUsername').prop("disabled",false);
				$('#dbAdminPassword').prop("disabled",false);
				$('#dbfFileLocation').prop("disabled",false);
			}else{
				// disable dbusername , dbpassword fields
				$('#dbAdminUsername').prop("disabled",true);
				$('#dbAdminPassword').prop("disabled",true);
				$('#dbfFileLocation').prop("disabled",true);
			}
			
			$('#createUser').click(function(){
				if (this.checked) {
					  $('#createUser').val("true");
					  $('#dbAdminUsername').prop("disabled",false);
					  $('#dbAdminPassword').prop("disabled",false);
					  $('#dbfFileLocation').prop("disabled",false);
						
					  var dbAdminUserName = $('#dbAdminUsername');
					  var dbAdminPassword = $('#dbAdminPassword');
					  $('#eliteAAAStartupDBSetupForm').bootstrapValidator('addField', dbAdminUserName);
					  $('#eliteAAAStartupDBSetupForm').bootstrapValidator('addField', dbAdminPassword); 
					  $('#eliteAAAStartupDBSetupForm').data('bootstrapValidator').enableFieldValidators('dbAdminUsername',true);
					  $('#eliteAAAStartupDBSetupForm').data('bootstrapValidator').enableFieldValidators('dbAdminPassword',true);
					     
						 
			        } else {
			         $('#eliteAAAStartupDBSetupForm').data('bootstrapValidator').enableFieldValidators('dbAdminUsername',false);
			         $('#eliteAAAStartupDBSetupForm').data('bootstrapValidator').enableFieldValidators('dbAdminPassword',false);
			        	$('#createUser').val("false");
			        	$('#dbAdminUsername').prop("disabled",true);
						$('#dbAdminPassword').prop("disabled",true);
						$('#dbfFileLocation').prop("disabled",true);
							
			        }  
			});
		
	});   
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
	<div class="col-sm-6 col-xs-offset-3 margin-top-10" >
		<div class="panel panel-default">
			<div class="alert alert-danger alert-label-panel" id="errorMsg">
				<logic:notEmpty name="eliteAAAStartupDBSetupForm" property="errorMsg">		
					<strong><bean:write name="eliteAAAStartupDBSetupForm" property="errorMsg"/></strong>
				</logic:notEmpty> 
			</div>
			<div class="panel-heading">
				<strong>
					<bean:message bundle="descriptionResources" key="dbsetup.databaseconnectionparamter" /> 
					<ec:elitehelp headerBundle="descriptionResources" text="dbsetup.databaseconnectionparamter.help.decription" header="dbsetup.databaseconnectionparamter" />
				 </strong>
			</div>
			<div class="panel-body">
				 <html:form action="/configureDBProperty.do" method="POST" styleId="eliteAAAStartupDBSetupForm" styleClass="form-horizontal">
					<html:hidden name="eliteAAAStartupDBSetupForm" styleId="action1" property="action" value="update" />
					<input type="hidden" name="loginMode" value="2"> 
					<div class="form-group">
						<div class="col-sm-12">
							<label for="connection url" >
								<bean:message bundle="descriptionResources" key="dbsetup.connectionurl" />
								<ec:elitehelp headerBundle="descriptionResources" text="dbsetup.connectionurl.help.description" header="dbsetup.connectionurl" /> 
							</label>
							<html:text property="connectionUrl" name="eliteAAAStartupDBSetupForm" styleId="connectionUrl" styleClass="form-control"/>
						</div>
					</div>
					<div class="form-group">
						<div class="col-sm-6">
							<label for="user name">
								<bean:message bundle="descriptionResources" key="dbsetup.username" />
								<ec:elitehelp headerBundle="descriptionResources" text="dbsetup.username.help.description" header="dbsetup.username.help.title" />
							</label>
							<html:text property="userName" styleId="userName" name="eliteAAAStartupDBSetupForm" styleClass="form-control" maxlength="30"/>
						</div>
						<div class="col-sm-6">
							<label for="password">
								<bean:message bundle="descriptionResources" key="dbsetup.password" />
								<ec:elitehelp headerBundle="descriptionResources" text="dbsetup.password.help.description" header="dbsetup.password.help.title" />
							</label>
							<html:password property="passWord" styleId="passWord" name="eliteAAAStartupDBSetupForm" styleClass="form-control" maxlength="30"/>
						</div>
					</div>
					
					<p>
					  	<a data-toggle="collapse" href="#collapseExample" aria-expanded="false" aria-controls="collapseExample">
					  		<i class="fa fa-chevron-right"></i> Advanced Options
					  	</a>
					</p>
					
					<div class="collapse" id="collapseExample">
					    <div class="card card-block">
					    	
							<div class="panel panel-default" id="createUserParameterPanel" style="margin-bottom: 1%">
								
								<div class="panel-heading">
									<strong>
										<html:checkbox name="eliteAAAStartupDBSetupForm" styleId="createUser" property="createNewUserSelected"></html:checkbox>
										Database schema and user creation
										<ec:elitehelp headerBundle="descriptionResources" text="dbsetup.databaseconnectionparamter.help.decription" header="dbsetup.databaseconnectionparamter" />
									 </strong>
								</div>
								
								<div class="panel-body" style="margin-bottom: -10px">
										
									<div class="form-group">
										<div class="col-sm-6">
											<label for="user name">
												<bean:message bundle="descriptionResources" key="dbsetup.dbadminusername" />
												<ec:elitehelp headerBundle="descriptionResources" text="dbsetup.dbadminusername.help.description" header="dbsetup.dbadminusername" />
											</label> 
											<html:text property="dbAdminUsername" styleId="dbAdminUsername" name="eliteAAAStartupDBSetupForm" styleClass="form-control" maxlength="64"/>
										</div>
										<div class="col-sm-6">
											<label for="password">
												<bean:message bundle="descriptionResources" key="dbsetup.dbadminuserpassword" />
												<ec:elitehelp headerBundle="descriptionResources" text="dbsetup.dbadminuserpassword.help.description" header="dbsetup.dbadminuserpassword" />
											</label> 
											<html:password property="dbAdminPassword" styleId="dbAdminPassword" name="eliteAAAStartupDBSetupForm" styleClass="form-control" maxlength="30"/>
										</div>
									</div>
									
									<div class="form-group">
										<div class="col-sm-12">
											<label for="dbffilelocation"> 
												<bean:message bundle="descriptionResources" key="dbsetup.location" />
												<ec:elitehelp headerBundle="descriptionResources" text="dbsetup.location.help.description" header="dbsetup.location" />
											</label>
											<html:text property="dbfFileLocation" name="eliteAAAStartupDBSetupForm" styleId="dbfFileLocation" styleClass="form-control" />
										</div>
									 </div>	
							
								</div>
							</div>
					
						</div>
					</div>
					
					<div class="form-group" style="margin-bottom: 3px">
						<div class="col-sm-offset-2 col-sm-10">
							<html:submit styleClass="btn btn-primary" style="float:right"  value="Configure" styleId="configureOrNextBtn"/>
						</div>
					</div>
					
				</html:form>
			</div>
			<!-- Copyrights Section -->
			<div class="copyrightdiv">
				<span> Copyright &copy; <a href="http://www.elitecore.com" target="_blank" style="color: inherit;">Sterlite Technologies Ltd.</a>
				</span>
			</div>
			
		</div>
	</div>
</body>
</html>