<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@ taglib uri="http://code.google.com/p/jcaptcha4struts2/taglib/2.0" prefix="jCaptcha" %>
<div class="col-xs-8 col-xs-offset-2">
	<div class="panel panel-nv-invert">
		<div class="panel-body">
			<img src="${pageContext.request.contextPath}/images/avatar_2x.png" alt="..." class="img-circle" style="width: 30%; margin-left: 35%; margin-bottom: 15px;">
					<s:actionerror escape="false" />
					<s:form cssClass="well form-vertical" validate="true" id="loginform" action="captcha/commons/login/Login/login" validator="validateForm()">
						<s:token />
						<s:textfield id="username" name="login.username" placeholder="User Name" cssClass="form-control" />
						<s:password id="password" name="login.password" placeholder="Password" cssClass="form-control" autocomplete="off" />
						<s:if test="#session.captchaFailureCount > 2">
						<jCaptcha:image/>
						</s:if> 
						<s:submit id="loginsubmitbtn" cssClass="btn btn-primary form-control" formIds="login" value="Sign in"/>
					</s:form>
				</div>
			</div>
		</div>
<%-- 	<div id="systeminfodiv" class="hidden-xs">
 		<a href="#" data-toggle="collapse" data-target="#systeminfo"><span class="glyphicon glyphicon-cog" aria-hidden="true"></span></a>
 		<div id="systeminfo" class="collapse" style="width:350px;">
 			<p style="color: #e5e5e5; font-size: 20px; padding: 2px;">System Information</p>
			<ul class="list-group">
				<li class="list-group-item" style="text-align: left"><span class="badge">
					<s:property value="#application['systemInformation'].version" /></span> Product Version</li>
				<li class="list-group-item" style="text-align: left"><span class="badge">
					<s:property value="#application['systemInformation'].operatingSystem" /></span> Operating System</li>
				<li class="list-group-item" style="text-align: left"><span class="badge">
					<s:property value="#application['systemInformation'].apacheTomcat" /></span> Apache Tomcat</li>
				<li class="list-group-item" style="text-align: left"><span class="badge">
					<s:property value="#application['systemInformation'].java" /></span> Java</li>
				<li class="list-group-item" style="text-align: left"><span class="badge">
					<s:property value="#application['systemInformation'].ram" /></span> RAM</li>
				<li class="list-group-item" style="text-align: left"><span class="badge">
					<s:property value="#application['systemInformation'].databaseVersion" /></span> Database Version</li>
			</ul>
		</div>
 	</div> --%>
	
		

<script type="text/javascript">
 window.onload = function() {
    var errordivUsername = document.getElementById("username");
    var errordivPassword = document.getElementById("password");
    if(errordivPassword != null && errordivUsername == null){
    	document.getElementById("username").blur();
    }else{
    	document.getElementById("username").focus();
    } 
   $("#jCaptchaResponse").val("");
};

function validateForm(){
	var isValid = true;
	var captchaResponse = $("#jCaptchaResponse").val();
	if(captchaResponse === null || captchaResponse == ""){
		isValid = false;
		setError("jCaptchaResponse", "Please Enter Captcha")
	}
	return isValid;
}
 </script>
