<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@ taglib uri="/WEB-INF/lib/jcaptcha4struts2-2.0.2.jar" prefix="jCaptcha" %>
	
	<div class="col-xs-8  col-xs-offset-2">
		<div class="panel panel-nv-invert">
			<div class="panel-body">
				<s:actionerror escape="false" />
				<s:actionmessage escape="false" /> 
				<s:form action="captcha/commons/forgotPassword/ForgotPassword/forgotPassword" validate="true" id="forgotPassword" name="forgotPasswordForm" cssClass="well form-vertical">
					<s:token/> 
					<s:textfield name="username" id="username" placeholder="User Name" cssClass="form-control" />
					<jCaptcha:image/> 
					<s:submit name="forgotPasswordBtn" id="forgotPasswordBtn" cssClass="btn btn-primary form-control" value="Send Email">
					</s:submit>
				</s:form>
			</div>
		</div>
	</div>
