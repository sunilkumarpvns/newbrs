<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<div class="col-xs-8 col-sm-7 col-lg-4 col-xs-offset-2 col-lg-offset-4">
	<div class="panel panel-nv-invert" style="margin-top :60px">
		<div class="panel-body">
			<h3 align="center" > <s:text name="reset.password.label" /></h3>
			<s:form cssClass="well form-vertical" validate="true" id="resetPasswordForm" action="commons/login/ChangePassword/resetPassword" validator="validateForm()">
				<s:select name="changePasswordData.userId"  cssClass="form-control" list="changePasswordData.staffList" listKey="id" listValue="userName"/>
				<s:password id="newPassword" name="changePasswordData.newPassword" placeholder="New Password" cssClass="form-control" />
				<s:password id="confirmPassword" placeholder="Confirm New Password" cssClass="form-control" />
				<s:submit id="resetPassword" cssClass="btn btn-primary form-control col-xs-6" formIds="resetPassword" value="Reset"/>
			</s:form>
		</div>
	</div>
</div>

<script>
	function validateForm() {
		clearErrorMessagesById("newPassword");
		clearErrorMessagesById("confirmPassword");
		var newPassword = $("#newPassword").val();
		var confirmPassword = $("#confirmPassword").val();
		if (newPassword != confirmPassword) {
			setError('confirmPassword', '<s:text name="new.confirm.password.same.note" />');
			return false;
		}
		return true;
	}
</script>