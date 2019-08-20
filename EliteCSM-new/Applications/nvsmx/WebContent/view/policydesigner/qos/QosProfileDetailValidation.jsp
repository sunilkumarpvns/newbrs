<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@taglib uri="/struts-tags/ec" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<script type="text/javascript">

var mbrdlError='<s:text name="qosprofile.detail.mbrdl.error"/>';
var aambrdlError= '<s:text name="qosprofile.detail.aambrdl.error"/>';
var positiveValueError='<s:text name="error.postive.numeric"/>';
var usageMonitoringValidationError='<s:text name="qosprofile.detail.usagemonitoring.validation" />';
var rejectCauseValidationError="<s:text name='qosprofile.detail.rejectcause.validation' />" ;
var redirecturlValidationError="<s:text name='qosprofile.detail.redirecturl.validation' />" ;


function disableRestAttributes() {
		var value = $("#action").val();
		if (value == 1) {
			$("input[id != 'redirectUrl']").each(function() {
				var notReadOnly = $(this).attr('id');
				if(notReadOnly=='rejectCause'){
					$(this).removeAttr('readonly');
				}else{
					$(this).attr('readonly', 'true');
				}
				
			});
			$("select").each(function() {
				var notReadOnly = $(this).attr('id');
				if(notReadOnly!='action'){
					$(this).attr('readonly', 'true');
				}
			});

		} else {
			clearErrorMessages("rejectCause");
			$("input").each(function() {
				$(this).removeAttr('readonly');
				var id = $(this).attr('id');
				if(id=='rejectCause'){
					$(this).attr('readonly', 'true');
				}
			});
			$("select").each(function() {
				$(this).removeAttr('readonly');
			});
			disableSliceInformation();
		}
	}
	function disableSliceInformation() {
		var value = $("#usageMonitoring").val();
			if($("#action").val() == 1){
				return;
			}
			if (value == "false") {
				$("#sliceInformation input").each(function () {
					$(this).attr('readonly', 'true');
				});
				$("#sliceInformation select").each(function () {
					$(this).attr('readonly', 'true');
				});
			} else {
				$("#sliceInformation input").each(function () {
					$(this).removeAttr('readonly');
				});
				$("#sliceInformation select").each(function () {
					$(this).removeAttr('readonly');
				});
			}
	}

function validateForm(){
		clearErrorMessagesById('mbrdl');
		clearErrorMessagesById('aambrdl');
		clearErrorMessagesById('aambrul');
		clearErrorMessagesById('mbrul');
		clearErrorMessagesById('slicetotal');
		clearErrorMessagesById('slicedownload');
		clearErrorMessagesById('sliceupload');
		clearErrorMessagesById('slicetime');
		clearErrorMessagesById('rejectCause');
		clearErrorMessagesById('redirectUrl');

		var mbrdl = $("#mbrdl").val();
		var mbrul = $("#mbrul").val();
		var aambrdl = $("#aambrdl").val();
		var aambrul = $("#aambrul").val();

		var mbrdlUnit = $("#mbrdlUnit").val();
		var mbrulUnit = $("#mbrulUnit").val();
		var aambrdlUnit = $("#aambrdlUnit").val();
		var aambrulUnit = $("#aambrulUnit").val();

		var usageMonitoring = $("#usageMonitoring").val();
		var sliceTotal = $("#slicetotal").val();
		var sliceDownload = $("#slicedownload").val();
		var sliceUpload = $("#sliceupload").val();
		var sliceTime = $("#slicetime").val();

		var sliceTotalUnit = $("#sliceTotalUnit").val();
		var sliceDownloadUnit = $("#sliceDownloadUnit").val();
		var sliceUploadUnit = $("#sliceUploadUnit").val();
		var sliceTimeUnit = $("#sliceTimeUnit").val();
		var redirectUrl = $("#redirectUrl").val();
		if ($("#action").val() == 0) {// action "0" is for accept

			if (isNullOrEmpty(mbrdl) && isNullOrEmpty(aambrdl)) {
				setError('mbrdl', mbrdlError);
				setError('aambrdl', aambrdlError);
				return false;
			}
			//isNaturalNumber
			if (isNullOrEmpty(mbrdl) == false && isNumberGreaterThanZero(mbrdl) == false) {
				setError('mbrdl', positiveValueError);
				return false;
			}
			if (isValidQos("mbrdl", mbrdl, mbrdlUnit) == false) {
				return false;
			}

			if (isNullOrEmpty(mbrul) == false && isNumberGreaterThanZero(mbrul) == false) {
				setError('mbrul', positiveValueError);
				return false;
			}
			if (isValidQos("mbrul", mbrul, mbrulUnit) == false) {
					return false;
			}

			if (isNullOrEmpty(aambrdl) == false && isNumberGreaterThanZero(aambrdl) == false) {
				setError('aambrdl', positiveValueError);
				return false;
			}
			if (isValidQos("aambrdl", aambrdl, aambrdlUnit) == false) {
				return false;
			}

			if (isNullOrEmpty(aambrul) == false && isNumberGreaterThanZero(aambrul) == false) {
				setError('aambrul', positiveValueError);
				return false;
			}
			if (isValidQos("aambrul", aambrul, aambrulUnit) == false) {
				return false;
			}
			if (isNullOrEmpty(sliceTotal) && isNullOrEmpty(sliceDownload)
					&& isNullOrEmpty(sliceUpload) && isNullOrEmpty(sliceTime) && (usageMonitoring == "true" || usageMonitoring == true)) {
				setError("slicetime", usageMonitoringValidationError);
				setError("slicetotal", "");
				setError("slicedownload", "");
				setError("sliceupload", "");
				return false;
			}
		} else {
			var rejectCause = $("#rejectCause").val();
			if (isNullOrEmpty(rejectCause)) {
				setError('rejectCause', rejectCauseValidationError);
				return false;
			}

			if (isValidQos("mbrdl", mbrdl, mbrdlUnit) == false) {
				return false;
			}

			if (isValidQos("mbrul", mbrul, mbrulUnit) == false) {
				return false;
			}

			if (isValidQos("aambrdl", aambrdl, aambrdlUnit) == false) {
				return false;
			}
			if (isValidQos("aambrul", aambrul, aambrulUnit) == false) {
				return false;
			}

		}
	if(redirectUrl.trim().length > 4000) {
		setError('redirectUrl', redirecturlValidationError);
		return false;
	}

	if (isNullOrEmpty(sliceTotal) == false
			&& isNumberGreaterThanZero(sliceTotal) == false) {
		setError('slicetotal', positiveValueError);
		return false;
	}
	if (isValidQuota("slicetotal", sliceTotal, sliceTotalUnit) == false) {
		return false;
	}
	if (isNullOrEmpty(sliceDownload) == false
			&& isNumberGreaterThanZero(sliceDownload) == false) {
		setError('slicedownload', positiveValueError);
		return false;
	}
	if (isValidQuota("slicedownload", sliceDownload, sliceDownloadUnit) == false) {
		return false;
	}
	if (isNullOrEmpty(sliceUpload) == false
			&& isNumberGreaterThanZero(sliceUpload) == false) {
		setError('sliceupload', positiveValueError);
		return false;
	}
	if (isValidQuota("sliceupload", sliceUpload, sliceUploadUnit) == false) {
		return false;
	}
	if (isNullOrEmpty(sliceTime) == false
			&& isNumberGreaterThanZero(sliceTime) == false) {
		setError('slicetime', positiveValueError);
		return false;
	}
	if(isValidSliceTime("slicetime",sliceTime,sliceTimeUnit) == false){
		return false;
	}
    if(isDeploymentModeOCS()){
        $("#packageValidationWarningMessage").html("QoS detail should not be configured for deployment mode: <b>"+deploymentMode+"</b> </br> Do you want to proceed?");
        $("#packageValidationModal").modal('show');
        return false;
    }
	return true;
}

$(function(){
    if(deploymentMode == '<s:property value="%{@com.elitecore.corenetvertex.constants.DeploymentMode@OCS.name()}"/>'){
        addWarning(".popup","QoS detail should not be configured for deployment mode: <b>"+deploymentMode+"</b>");
    }
});
function submitForm(){
    document.forms["qosProfileCreateDetailForm"].submit();
}

</script>



