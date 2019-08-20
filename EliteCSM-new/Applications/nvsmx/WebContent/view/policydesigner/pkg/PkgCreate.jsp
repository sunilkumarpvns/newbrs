<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">Create Package</h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/" action="" id="pkgCreate" name="pkgCreateForm" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" onsubmit="return validateForm()">
			<s:token />
			<div class="row">
				<div class="col-xs-6">
					<s:textfield 	name="pkgData.name" 			key="pkg.name" 	id="pkgName"		cssClass="form-control focusElement"
									onblur="verifyUniqueness('pkgName','create','','com.elitecore.corenetvertex.pkg.PkgData','','');"  />
					<s:textarea 	name="pkgData.description" 		key="pkg.description" 	cssClass="form-control" />
					<s:select 		name="groupIds" 	   		key="pkg.groups"    cssClass="form-control select2"	list="#session.staffBelongingGroups"  id="groupNames" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" />
					<s:select 		name="pkgData.status" 			key="pkg.status" 	cssClass="form-control"			list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" id="pkgStatus"	 />
					<s:hidden name="pkgData.packageMode" value="%{@com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}"/>

					<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
						<s:select 		name="pkgData.type" 		key="pkg.type" 		cssClass="form-control"			list="@com.elitecore.corenetvertex.pkg.PkgType@getUserPlan()" 	id="pkgType" onchange="checkForAddOn();" />
					</s:if>

					<s:else>
						<s:select name="pkgData.quotaProfileType" key="pkg.quotaprofiletype" cssClass="form-control"
								  list="promotionalPkgQuotaProfileTypes"
								  listValue="getVal()" id="quotaProfileType"
								  value="@com.elitecore.corenetvertex.constants.QuotaProfileType@USAGE_METERING_BASED.name()" />
					</s:else>


				</div>
				<s:hidden name="currency" id="currency" value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/>
				<div class="col-xs-6">
					<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
						<s:select 		name="pkgData.quotaProfileType" key="pkg.quotaprofiletype" 	cssClass="form-control"	list="@com.elitecore.corenetvertex.constants.QuotaProfileType@values()" listValue="getVal()" id="quotaProfileType"/>

						<s:select name="currencyCombo" id="currencyCombo" key="pkg.currency"
								  cssClass="form-control select2" list="@java.util.Currency@getAvailableCurrencies()"
								  listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'" value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"
								  cssStyle="width:100%" disabled="!@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isMultiCurrencyEnable()" onchange="getPackagesBasedOnCurrency();"/>




						<s:select 	id="exclusiveAddOn"	name="pkgData.exclusiveAddOn" 	 		key="pkg.exclusiveaddon" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control"
									 value="@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.isBooleanValue()" />

					</s:if>
					<s:else>
						<s:datepicker 	name="pkgData.availabilityStartDate" key="pkg.availability.start.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
										  showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"
										  id="availabilityStartDate" readonly="true"/>

						<s:datepicker 	name="pkgData.availabilityEndDate" key="pkg.availability.end.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
										  showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"
										  id="availabilityEndDate" readonly="true" />
						<s:select 	id="preferPromotionalQos"	name="pkgData.alwaysPreferPromotionalQoS" 	key="pkg.preferpromotionalqos" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control"
									 value="@com.elitecore.corenetvertex.constants.CommonStatusValues@ENABLE.isBooleanValue()" />
						<s:hidden name="pkgData.type" value="%{pkgData.type}" id="pkgType"/>
					</s:else>
					<s:if test="%{@com.elitecore.commons.base.Strings@isNullOrBlank(@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_1'))}">
						<s:textfield 	name="pkgData.param1"	key="Param 1" 		cssClass="form-control" />
					</s:if>
					<s:else>
						<s:textfield 	name="pkgData.param1" 	key="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_1')" 	cssClass="form-control" />
					</s:else>
					<s:if test="%{@com.elitecore.commons.base.Strings@isNullOrBlank(@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_2'))}">
						<s:textfield 	name="pkgData.param2" 	key="Param 2"  		cssClass="form-control" />
					</s:if>
					<s:else>
						<s:textfield 	name="pkgData.param2" 	key="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_2')"  	cssClass="form-control" />
					</s:else>

				</div>
			</div>

			<div class="row">
				<div class="col-xs-12" align="center">
					<!-- formaction attribute of button overrides form's default action. It takes absolute path as well as relative path.
					Here relative path is given. So it will call respective create methods for Promotional, Emergency and Data Package -->
					<button class="btn btn-primary btn-sm" type="submit" role="submit" formaction="create" id="pkgCreate_0">
						<span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
					</button>
				</div>
			</div>

		</s:form>
	</div>
</div>
<script type="text/javascript">

$(function(){
	$( ".select2" ).select2();
	checkForAddOn();
	checkForMandatoryField();
	getPackagesBasedOnCurrency();
});
function validateForm(){
	var isValidName = verifyUniquenessOnSubmit('pkgName','create','','com.elitecore.corenetvertex.pkg.PkgData','','')
	var pkgtype =$("#pkgType").val();
	var isRetiredStatus = true;
	var isValidPromotionalPkg = true;
    var status =$("#pkgStatus").val();
	if(status === '<s:property value="@com.elitecore.corenetvertex.constants.PkgStatus@RETIRED.name()"/>'){
		setError('pkgStatus',"<s:text name='error.status.retired'/>");
		isRetiredStatus = false;
	}
    var quotaProfileType = $("#quotaProfileType").val();
	if(pkgtype === '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name()"/>'){
		isValidPromotionalPkg = promotionalPkgValidation();
	}
	if(isValidName && isRetiredStatus && isValidPromotionalPkg){
        var quotaProfileTypeDisplayValue = $("#quotaProfileType :selected").text();
        var validationMsg = validatePackageConfiguration(quotaProfileType, deploymentMode, quotaProfileTypeDisplayValue);
        if (isNullOrEmpty(validationMsg)) {
            return true;
        }
        validationMsg = validationMsg + "</br> Do you want to proceed?"
        $("#packageValidationWarningMessage").html(validationMsg);
        $("#packageValidationModal").modal('show');
        return false;

    }
	return false;

}

function submitForm(){
    var pkgtype = $("#pkgType").val();
    var action = "${pageContext.request.contextPath}/policydesigner/pkg/Pkg/create";
    if (pkgtype === '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name()"/>') {
        action = "${pageContext.request.contextPath}/promotional/policydesigner/pkg/Pkg/PromotionalPkg/create";
    }
    document.forms["pkgCreateForm"].action = action;
    document.forms["pkgCreateForm"].submit();
}

function getPackagesBasedOnCurrency() {
	var currencyValue = $("#currencyCombo").val();
//	alert(currencyValue);
	$("#currency").val(currencyValue);
}

</script>
<%@include file="/view/policydesigner/pkg/PackageValidation.jsp"%>
<%@include file="/view/policydesigner/packageConfigurationWarning.jsp"%>
