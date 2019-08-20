/* Global Variable*/

var failureDialog;
var successDialog;

$(function() {

	/* Failure Dialog */
	failureDialog = $("#failure-dialog").dialog({
		autoOpen : false,
		height : 200,
		width : 450,
		modal : true,
		buttons : {
			close : function() {
				failureDialog.dialog("close");
			}
		},
		close : function() {
		}
	});

	/* Success Dialog */
	successDialog = $("#success-dialog").dialog({
		autoOpen : false,
		height : 100,
		width : 350,
		modal : true,
		buttons : {
			close : function() {
				successDialog.dialog("close");
			}
		},
		close : function() {
		}
	});
});

/** submit the form */
function validateForm() {
	if (validateParameter()) {
		document.forms[0].submit();
	}
}

/** Validate mandatory parameters of form */
function validateParameter() {
	if (isNull(document.forms[0].connectionUrl.value)) {
		document.forms[0].connectionUrl.focus();
		alert('Connection URL must be specified');
		return false;
	} else if (isNull(document.forms[0].dbUsername.value)) {
		document.forms[0].dbUsername.focus();
		alert('Username must be specified');
		return false;
	} else if (isNull(document.forms[0].dbPassword.value)) {
		document.forms[0].dbPassword.focus();
		alert('Password must be specified');
		return false;
	}
	return true;
}