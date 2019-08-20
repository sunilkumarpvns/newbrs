<%@page import="com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager"%>
<%@page import="com.elitecore.elitesm.web.systemstartup.defaultsetup.form.EliteAAASetupForm"%>
<%@page import="com.elitecore.elitesm.blmanager.datasource.DatabaseDSBLManager"%>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@page import="java.io.Console"%>
<%@page import="java.util.List" %>
<%@page import="com.elitecore.elitesm.hibernate.core.base.HBaseDataManager"%>
<%@page import ="com.elitecore.elitesm.web.systemstartup.defaultsetup.util.DefaultSetupClassNameAndProperty" %>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<html lang="en">

<head>
  <title>Database Configuration</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link REL="SHORTCUT ICON" HREF="<%=request.getContextPath()%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap.min.css">
  <script src="<%=request.getContextPath()%>/jquery/custom_scrollbar/jquery.min.js"></script>
  <script src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
  <script src="<%=request.getContextPath()%>/jquery/nanoscroll/jquery.min.js"></script>
  <link href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/startupConfiguration.css">
 <%  EliteAAASetupForm eliteAAASetupForm=(EliteAAASetupForm)request.getAttribute("eliteAAASetupForm");  %>
				
</head>

<body class="form-background-color">
	<div class="logo-headline"></div>
	<div style="float: right;">
		<img src="<%=request.getContextPath()%>/images/Sterlite_Tech_Product.jpg" width="263" height="54" usemap="#Map" border="0">
	</div>
	<div class="heading">
		EliteCSM Default Configuration
	</div>
	<div class="col-sm-8 col-xs-offset-2">
		<html:form action="/deleteAlreadyExistData.do" styleId="eliteAAASetupFormId" style="display:none;">
			<input type="hidden" name="loginMode" value="2">
		</html:form>
			
	</div> 
	
	<div class="col-sm-6 col-xs-offset-3">
		<div class="panel panel-default">
			<div class="alert alert-warning" style="padding: 10px 15px !important;margin-bottom: -10px;">
            	<strong>Warning!</strong>  Existing Configuration found in System
            </div>
			<div class="panel-body">
				<%
					List<String> duplicateModuleNames = eliteAAASetupForm.getDuplicateInstances();
					if(Collectionz.isNullOrEmpty(duplicateModuleNames) == false){
						for( String str : duplicateModuleNames){	
				%>
				<table>
					<tr>
						<br><td class="glyphicon glyphicon-menu-right body-font"  style="margin-left: 60px;">
							<%=str %>
						</td>
					</tr>
				</table>
					<%	}
					} 						
					%> 
					
				<table>
					<tr>
						<td> <b class="body-font">Continue</b> : <span class="body-font gry-color">It will delete all above module from the system and create fresh setup</span></td> <br>
					</tr>
					<tr>
						<td> <b class="body-font" style="padding-right: 33px;" >Skip</b> : <span class="body-font gry-color">It wan't create fresh setup and redirect to Login page </span></td>
					</tr>
				</table>
			</div>
			<div class="panel-footer" align="right">
				<button type="button" class="btn btn-primary" id="confirmOk" onclick="submitDetailsForm();">Continue</button>
		       	<button type="button" class="btn btn-primary" id="confirmCancel" onclick="skipDetailsForm();">Skip</button>
		    </div>
		    <div class="copyrightdiv">
			   	<p>
				    Copyright &copy;<a href="http://www.elitecore.com" target="_blank" style="color: white;">Sterlite Technologies Ltd.</a>
				</p>
			</div>
		</div>
	</div>
</body>

		<script type="text/javascript">
		function skipDetailsForm(){
			window.location.href="<%=request.getContextPath()%>/login.do";
		}
		
		function submitDetailsForm(){
			$("#eliteAAASetupFormId").submit();
		}
		</script>
</html>