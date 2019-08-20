<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<s:if test="#request.isManualPasswordChange != true">
	<div class="col-xs-8 col-xs-offset-2">
		<div class="panel panel-nv-invert">
			<div class="panel-body">
				<h3 align="center" ><s:text name="change.password.label" /></h3>
				<s:actionerror escape="false" />
				<s:actionmessage escape="false" />

</s:if>
<s:else>
	<div class="col-xs-8 col-sm-7 col-lg-4 col-xs-offset-2 col-lg-offset-4">
		<div class="panel panel-nv-invert" style="margin-top :60px">
			<div class="panel-body">
				<h3 align="center" ><s:text name="change.password.label" /> </h3>
</s:else>
				<s:form cssClass="well form-vertical" validate="true" id="changePasswordForm" action="commons/login/ChangePassword/changePassword" validator="validateForm()">
					<s:set var="isManualPasswordChange" value="%{#request.isManualPasswordChange}" ></s:set>
						<s:hidden name="isManualPasswordChange" value="%{#isManualPasswordChange}"/>
						<s:iterator value="changePasswordData.staffList">
							<s:set var="staffId" value="id"></s:set>
							<s:hidden name="changePasswordData.userId" value="%{staffId}"  />
							<s:textfield name="userName" disabled="true" cssClass="form-control"/>
						</s:iterator>
					<s:password id="oldPassword" name="changePasswordData.oldPassword" placeholder="Old Password" cssClass="form-control" />
					<s:password id="newPassword" name="changePasswordData.newPassword" placeholder="New Password" cssClass="form-control" />
					<s:password id="confirmPassword" placeholder="Confirm New Password" cssClass="form-control" />
					<s:submit id="changePassword" cssClass="btn btn-primary form-control col-xs-6" formIds="changePassword" value="Change"/>
				</s:form>
				<s:if test="@com.elitecore.nvsmx.system.constants.NVSMXCommonConstants@ADMIN_STAFF_ID == #session.staffId">
					<a href="${pageContext.request.contextPath}/commons/login/ChangePassword/initResetPassword" style="font-size: large"><s:text name="reset.other.user.password.label" /></a>
				</s:if>
</div>
</div>
	</div>
</div></div></div>


<script>
	var invalidPasswordPolicyMessage = "";
	function validateForm() {
		clearErrorMessagesById("newPassword");
		clearErrorMessagesById("confirmPassword");
		var newPassword = $("#newPassword").val();
		var confirmPassword = $("#confirmPassword").val();

		if (newPassword != confirmPassword) {
			setError('confirmPassword', '<s:text name="new.confirm.password.same.note" />');
			return false;
		}
		if(validatePasswordPolicy(newPassword) == false) {
			setError('newPassword', invalidPasswordPolicyMessage);
			return false;
		}
		return true;
	}




	function validatePasswordPolicy(password) {

		var isValidPassword = true;

		var minLength = '<s:property value="changePasswordData.passwordPolicyConfigData.minPasswordLength" />' ;
		var maxLength = '<s:property value="changePasswordData.passwordPolicyConfigData.maxPasswordLength" />';
		var minMaxLength = "{"+minLength+","+maxLength+"}";

		var allowedAlphabets = '<s:property value="changePasswordData.passwordPolicyConfigData.alphabetRange" />';
		if(allowedAlphabets == null || allowedAlphabets == "") {
			allowedAlphabets = "0";
		}
		allowedAlphabets = "{"+allowedAlphabets+"}";

		var allowedDigits = '<s:property value="changePasswordData.passwordPolicyConfigData.digitsRange" />';

		if(allowedDigits == null || allowedDigits == "") {
			allowedDigits = "0";
		}
		allowedDigits = "{"+allowedDigits+"}";


		var allowedSpecialCharacter ='<s:property value="changePasswordData.passwordPolicyConfigData.specialCharRange" />';

		if(allowedSpecialCharacter == null  || allowedSpecialCharacter == "") {
			allowedSpecialCharacter = "0";
		}
		allowedSpecialCharacter = "{"+allowedSpecialCharacter+"}";


		var prohibitedList = '<s:property value="changePasswordData.passwordPolicyConfigData.prohibitedChars" />'.split("");


		var specialCharacterList = "!#$%&'()*+,-./:;<=>?@[^_`{|}~";

		for (var i in prohibitedList) {
			if(password.indexOf(prohibitedList[i]) != -1) {
				isValidPassword = false;
				invalidPasswordPolicyMessage = "Special characters "+prohibitedList+" prohibited";
				return isValidPassword;
			}
		}
		var regExPattern = "((?=(.*\\d)"+allowedDigits+")(?=(.*[A-Za-z])"+allowedAlphabets+")(?=(.*["+specialCharacterList+"])"+allowedSpecialCharacter+")."+minMaxLength+")";
		isValidPassword = new RegExp(regExPattern).test(password);
		if(isValidPassword == false) {
			invalidPasswordPolicyMessage = '<s:text name="invalid.password.policy" />';
		}
		return isValidPassword;
	}
</script>