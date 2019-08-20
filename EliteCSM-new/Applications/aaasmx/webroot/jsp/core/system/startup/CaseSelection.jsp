<%@page import="com.elitecore.elitesm.web.systemstartup.defaultsetup.controller.EliteAAASetupAction"%>
<%@page import="com.elitecore.commons.base.Strings"%>
<%@page import="com.elitecore.elitesm.web.systemstartup.defaultsetup.form.EliteAAACaseSelectionForm"%>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@taglib prefix="ec" uri="/elitetags" %>
<html lang="en">

<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>EliteCSM - Server Manager</title>
<link REL="SHORTCUT ICON" HREF="<%=request.getContextPath()%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap.min.css">
<link href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
<link href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrapValidator.min.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/startupConfiguration.css">
<script src="<%=request.getContextPath()%>/jquery/nanoscroll/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
<script src="<%=request.getContextPath()%>/js/bootstrap/bootstrapValidator.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/viewpage/elitecsm.viewpage.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/helptag/elitecsm.openhelp.js"></script>
<script src="<%=request.getContextPath()%>/jquery/nanoscroll/jquery-ui.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/openhelp.js"></script>


<% 
    	 EliteAAACaseSelectionForm eliteAAACaseSelectionForm = (EliteAAACaseSelectionForm)request.getAttribute("eliteAAACaseSelectionForm");
%>    	 


</head>
<body class="form-background-color">
	<div class="logo-headline"></div>
	<div style="float: right;">
		<img src="<%=request.getContextPath()%>/images/Sterlite_Tech_Product.jpg" width="263" height="54" usemap="#Map" border="0">
	</div>
	
	<div class="heading">
		<bean:message bundle="descriptionResources" key="casesensitivity.header"/>
	</div>

	<div class="col-sm-6 col-xs-offset-3 margin-top-10" id="mainDiv">
		<div class="panel panel-default">
			<div class="panel-heading"> <b> Case Sensitivity Configuration </b> </div>
				<div class="panel-body">
					<html:form action="/caseSelection.do" styleId="eliteAAACaseSelectionFormId" styleClass="form-horizontal" method="POST">
						<input type="hidden" name="loginMode" value="2" />
						<div class="form-group">
							<div class="col-sm-6">
								<label for="subscriber"><bean:message bundle="descriptionResources" key="casesensitivity.subscriber"/></label>
								<strong>
									<ec:elitehelp headerBundle="descriptionResources" text="username.help" header="setup.username" />
								</strong>
								<html:select property="caseSensitivityForSubscriber" name="eliteAAACaseSelectionForm" styleId="caseSensitivityForSubscriber" styleClass="form-control" >
									<html:option value="0">Case Sensitive</html:option>
									<html:option value="1">Lower Case</html:option>
									<html:option value="2">Upper Case</html:option>
								</html:select>
							</div>
							<div class="col-sm-6">
								<label for="caseSensitivityForPolicy"><bean:message bundle="descriptionResources" key="casesensitivity.policy"/></label>
								<strong>
									<ec:elitehelp headerBundle="descriptionResources" text="password.help" header="setup.password" />
								</strong>
								<html:select property="caseSensitivityForPolicy" name="eliteAAACaseSelectionForm" styleId="caseSensitivityForPolicy" styleClass="form-control" >
									<html:option value="0">Case Sensitive</html:option>
									<html:option value="1">Lower Case</html:option>
									<html:option value="2">Upper Case</html:option>
								</html:select>
							</div>
						</div>
					
						<div class="form-group" >
							<div class="col-sm-12 btn-toolbar">
								<html:submit styleId="btnSubmit" styleClass="btn btn-primary pull-right" value="Configure" property="btnSubmit"></html:submit>
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
</body>
</html>