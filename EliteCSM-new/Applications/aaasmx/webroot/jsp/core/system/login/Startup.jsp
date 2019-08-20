<%@page import="com.elitecore.commons.base.Strings"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus"%>
<%@page import="com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_CONSTANTS"%>
<%@page import="com.elitecore.elitesm.web.core.system.startup.EliteSMStartupStatus.SM_MODULE_STATUS"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="java.util.LinkedHashMap" %>
<%@page import="com.elitecore.elitesm.web.systemstartup.dbsetup.form.EliteAAAStartupDBSetupForm"%>
<html>
	<head>
		<title>EliteCSM - Server Manager</title>
		<link REL="SHORTCUT ICON" HREF="<%=request.getContextPath()%>/images/sterlitetech-favicon.ico" type="image/x-icon" title="AAASMX"/>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap.min.css">
		<link href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/schemaform/bootstrap.min.css">
		<link href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrap.min.css" rel="stylesheet"/> 
		<link href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" rel="stylesheet">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/startupConfiguration.css">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/css/bootstrap/css/bootstrapValidator.min.css" />
		<style type="text/css">
		
		.success-panel-div{
			margin-top: 20%;
			display: none;
			margin-bottom: 0px;
		}
		
		</style>
		<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js"></script>
		<script src="<%=request.getContextPath()%>/jquery/nanoscroll/jquery.min.js"></script>
		<script src="<%=request.getContextPath()%>/js/bootstrap/bootstrap.min.js"></script>
		<script src="<%=request.getContextPath()%>/js/bootstrap/bootstrapValidator.min.js"></script>
		<script language="javascript" src="<%=request.getContextPath()%>/js/openhelp.js"></script>	
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/viewpage/elitecsm.viewpage.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/js/helptag/elitecsm.openhelp.js"></script>
		<script src="<%=request.getContextPath()%>/jquery/nanoscroll/jquery-ui.js"></script>
		<script language="javascript" src="<%=request.getContextPath()%>/js/openhelp.js"></script> 
		<script>
		function breakout_of_frame(){
		  if (top.location != location){
		    top.location.href = document.location.href ;
		  }
		}
		</script>
		<%
		  	String isSetupCompleted = "";
		    isSetupCompleted = (String)request.getAttribute("isSetupCompleted");
		    
		    String isSetupAlreadyCompleted = "";
		    isSetupAlreadyCompleted = (String)request.getAttribute("isSetupAlreadyCompleted");
		    if(Strings.isNullOrBlank(isSetupAlreadyCompleted)){
		    	isSetupAlreadyCompleted = "false";
		    }
		 %>
	
	</head>
	<body id="maindiv" class="form-background-color" onload="breakout_of_frame();">
		<div class="header-black-div"></div>
		<div style="float: right;">
			<img src="<%=request.getContextPath()%>/images/Sterlite_Tech_Product.jpg" width="263" height="54" usemap="#Map" border="0">
		</div>
		
		<div class="heading">
			EliteCSM Startup Configuration
		</div>
		<div class="col-sm-6 col-xs-offset-3 margin-top-10">
			<div class="panel panel-default">
			<div class="header">
				<h4>
					 Welcome to Elitecore Server Manager
				</h4>
	    	</div>
			
			<div class="panel-body" style="padding-top: 5px">
				 <span class="blue-label" style="margin-bottom: 1%">
					Please wait while EliteCSM - Server Manager starts ...
				</span>
				<table width="80%" cellspacing="0" cellpadding="0" border="0" class="tableBody" style="margin-top: 1%">
					<% 	int index = 1;
						LinkedHashMap<SM_MODULE_CONSTANTS, SM_MODULE_STATUS> eliteSMStartupStatusMap = EliteSMStartupStatus.getModuleStatusMap();
						if(eliteSMStartupStatusMap != null && eliteSMStartupStatusMap.size() > 0){
							for(SM_MODULE_CONSTANTS key : eliteSMStartupStatusMap.keySet()){
								String label =key.label;
								String status =eliteSMStartupStatusMap.get(key).status; %>
								<tr>
									<td width="5%" class="startupfont css_<%=status%>">
										<%=index++%>.
									</td>
									<td width="80%" class="startupfont css_<%=status%>" id="<%=label %>">
										<span><%=label %></span>
									</td>
									<td width="15%" class="startupfont css_<%=status%> css_status_<%=status%> status-result" ><%=status %></td>
								</tr>
								<%
							}						
						}
					%> 
				</table>
				<div align="center" class="progress_bar_css" style="margin-bottom: 5%">
					<img height="50" id="loading-image" src="<%=request.getContextPath()%>/images/loading-blue.gif"/>
				</div> 
				
				
				<div class="alert alert-success success-panel-div" id="successMsg">
	  				<strong><b>Server Manager is ready for default setup kindly click on 'Next' button</b></strong>
				</div>
					
				</div>
				<div class="panel-footer" style="margin-top: 0px;" id="footerId"
						align="right">
						<button type="button" class="btn btn-primary"
							onclick="forwardToCaseSelection()" id="forwardToDefaultSetup" style="display: none">Next</button>
				</div>
				
			<div class="copyrightdiv">
			    	<p>
					    Copyright &copy; <a href="http://www.elitecore.com" target="_blank" style="color: white;">Sterlite Technologies Ltd.</a>
					</p>
			    </div>
			</div>
		</div>
		
	</body>
<script type="text/javascript">
 $(document).ready(function(){
	 
	 var isEnvironmentSetup = '<%=isSetupCompleted%>'
	 var isSetupAlreadyCompleted = '<%=isSetupAlreadyCompleted%>'
	 var timer = 0 , delay = 1500; 

	 $('.status-result').each(function(){
		var status = $(this).text();
		if("Failed" == status){
			$('.progress_bar_css').hide();
		}	
	 });
	 
	 if(isSetupAlreadyCompleted === 'true'){
		 timer = setInterval(function(){
			    $.ajax({
			      type: 'POST',
			      url: '<%=request.getContextPath()%>/getstartupStatus',
			      success: function(html,textStatus, xhr){
			    	  $('.tableBody').empty();
			    	  
			    	  if(isAllSuccess(html)){
				    	  clearInterval(timer);
				    	  window.location.href="<%=request.getContextPath()%>/login.do";
			    	  }else{
			    		  createFormWithStatusAndSubmit("startup.do","isSetupAlreadyCompleted","true");
			    	  }
			      }
			    });
			}, delay);
	 }else {
		 timer = setInterval(function(){
		    $.ajax({
		      type: 'POST',
		      url: '<%=request.getContextPath()%>/getstartupStatus',
		      success: function(html,textStatus, xhr){
		    	  $('.tableBody').empty();
		    	  
		    	  if(isAllSuccess(html)){
			    	  clearInterval(timer);
			    	  $('#successMsg').show();
			    	  $('#forwardToDefaultSetup').show();
			    	  $('.progress_bar_css').hide();
			    	 
		    	  }else{
		    		  window.location.href="<%=request.getContextPath()%>/startup.do";
		    	  }
		      }
		    });
		}, delay);
		 
		 if(isEnvironmentSetup === 'true' && isSetupAlreadyCompleted === 'false'){
			 $('#successMsg').show();
			 $('#forwardToDefaultSetup').show();
			 $('.progress_bar_css').hide();
		 }
	 }
	 
});    
 
 function createFormWithStatusAndSubmit(action,appendAttrbId,appendAttrValue) {
		createNewForm("newFormData",action);
		var name = $("#"+appendAttrbId).attr("name");
		$("#newFormData").append("<input type='hidden' name='"+name+"' value='"+appendAttrValue+"'>")
		$("#newFormData").submit();
}
 
 function isAllSuccess(status){
	 var isSuccess = false;
	 
	 var index = 1;
	 $.each($.parseJSON(status), function(key, value) {
		
		 var row = "<tr><td width='5%' class='startupfont css_"+value+"'>"+(index++)+"</td>"+
		 			"<td width='80%' class='startupfont css_"+value+"' id='"+key+"'>"+key+"</td>"+
		 			"<td width='15%' class='startupfont css_"+value+" css_status_"+value+" status-result'>"+value+"</td></tr>";
		 			
		   $('.tableBody').append(row);
		 
		    if(value == "Success"){
		    	isSuccess = true;
			} else {
				isSuccess = false;
			}
	 });
	 return isSuccess;
 }
 
 function forwardToCaseSelection(){
	 var form = document.createElement("form");
	 form.method = "POST";
	 form.action = "<%=request.getContextPath()%>/caseSelection.do";
	 var element1 = document.createElement("input");
	 element1.name="loginMode";
	 element1.value="2";
	 element1.type="hidden";
	 form.appendChild(element1);
	 document.body.appendChild(form);
	 form.submit();
 }
</script>
<link href="<%=request.getContextPath()%>/css/startup.css" rel="stylesheet" />
</html>

