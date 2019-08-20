<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script type="text/javascript">
	function checkForAddOn() {
		var pkgtype = $("#pkgType").val();

        if (pkgtype === '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@BASE.name()"/>') {
            $("#exclusiveAddOn").attr('disabled', 'true');
        } else if (pkgtype === '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@ADDON.name()"/>') {
            $("#exclusiveAddOn").removeAttr('disabled');

        }
	}

	function checkForMandatoryField(){
		var pkgType = $("#pkgType").val();
		if (pkgType === '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name()"/>') {
			addRequired(document.getElementById("availabilityStartDate"));
			addRequired(document.getElementById("availabilityEndDate"));
		}
	}

	function checkForPackageMode(){
		var pkgMode =$("#packageMode").val();
		if(pkgMode=="LIVE2"){
			$("#pkgName").attr("disabled","true");
			$("#pkgDescription").attr("disabled","true");
			$("#groupNames").attr("disabled","true");
			$("#pkgQuotaProfileType").attr("disabled","true");
			$("#pkgType").attr("disabled","true");
			$("#exclusiveAddOn").attr("disabled","true");
			$("#btnUpdate").attr("formaction","updateStatus");
		}
	}

	function promotionalPkgValidation() {

		var status = $("#pkgStatus").val();
		var isRetiredStatus = true;

		if (status === '<s:property value="@com.elitecore.corenetvertex.constants.PkgStatus@RETIRED.name()"/>') {
			setError('pkgStatus', "<s:text name='error.status.retired'/>");
			isRetiredStatus = false;
		}

		var availabilityStartDate = $("#availabilityStartDate").val();
		var availabilityEndDate = $("#availabilityEndDate").val();


		var isValidAvailabilityStartDate = true;
		var isValidAvailabilityEndDate = true;

	    if (isNullOrEmpty(availabilityStartDate) == true) {
			setError('availabilityStartDate','<s:text name="pkg.promotional.availability.start.Date.error"/>');
			isValidAvailabilityStartDate = false;
		}
		if (isNullOrEmpty(availabilityEndDate) == true) {
			setError('availabilityEndDate','<s:text name="pkg.promotional.availability.end.Date.error"/>');
			isValidAvailabilityEndDate = false;
		}

		var quotaProfileType = $("#quotaProfileType").val();
		var isValidQuotaProfileType = true;
		if(quotaProfileType === '<s:property value="@com.elitecore.corenetvertex.constants.QuotaProfileType@SY_COUNTER_BASED.name()"/>' ||
            quotaProfileType === '<s:property value="@com.elitecore.corenetvertex.constants.QuotaProfileType@RnC_BASED.name()"/>' ) {

			setError('quotaProfileType', '<s:text name="pkg.promotional.quota.error"/>');
			isValidQuotaProfileType = false;
		}

		return isRetiredStatus && isValidAvailabilityStartDate && isValidAvailabilityEndDate && isValidQuotaProfileType;
	}

    function validatePackageConfiguration(quotaProfileType, deploymentMode, quotaProfileTypeDisplayValue) {
        if (isNullOrEmpty(quotaProfileType)) {
            return;
        }

        if (isDeploymentModePCC() || isDeploymentModeOCS()) {
            if (quotaProfileType != '<s:property value="%{@com.elitecore.corenetvertex.constants.QuotaProfileType@RnC_BASED.name()}"/>') {
                return  "Quota profile type: <b>" + quotaProfileTypeDisplayValue + "</b> is not compatible with deployment mode: <b>" + deploymentMode+"</b>"
            }
        } else if (isDeploymentModePCRF()) {
            if (quotaProfileType != '<s:property value="%{@com.elitecore.corenetvertex.constants.QuotaProfileType@USAGE_METERING_BASED.name()}"/>'
                && quotaProfileType != '<s:property value="%{@com.elitecore.corenetvertex.constants.QuotaProfileType@SY_COUNTER_BASED.name()}"/>') {
                return "Quota profile type: <b>" + quotaProfileTypeDisplayValue+ "</b> is not compatible with deployment mode: <b>" + deploymentMode+"</b>";
            }
        }
    }


</script>