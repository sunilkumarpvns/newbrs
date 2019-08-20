<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<script src="${pageContext.request.contextPath}/js/packageutility.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">Update Package</h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/" action=""  id="pkgUpdate" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" onsubmit="return validateForm();">
			<div class="row">
				<div class="col-xs-6">
					<s:hidden 		name="pkgData.id" />
					<s:textfield id="pkgName"			name="pkgData.name"  		key="pkg.name" 	cssClass="form-control focusElement" onblur="verifyUniqueness('pkgName','update','%{pkgData.id}','com.elitecore.corenetvertex.pkg.PkgData','','');"  />					
					<s:textarea  id="pkgDescription"	name="pkgData.description" 	key="pkg.description" 	cssClass="form-control" />					
					<div class="form-group ">

						<label class="col-xs-12 col-sm-4 col-lg-3 control-label" id="lbl_pkgGroup">Groups </label>

						<div class="col-xs-12 col-sm-8 col-lg-9 controls">
					<s:if test="%{pkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
								<s:hidden name="groupIds" value="%{pkgData.groups}"/>
								<select disabled="disabled" key="pkg.groups" class="form-control select2" style="width:100%" multiple="true">
									<s:iterator value="groupInfoList">
										<option locked="<s:property value="locked"/>" <s:property value="selected"/>
												value="<s:property value="id"/>" id="<s:property value="id"/>">
											<s:property value="name"/></option>
									</s:iterator>
								</select>
					</s:if>
					<s:else>
								<select name="groupIds" key="pkg.groups" class="form-control select2" style="width:100%" multiple="true">
									<s:iterator value="groupInfoList">
										<option locked="<s:property value="locked"/>" <s:property value="selected"/>
												value="<s:property value="id"/>" id="<s:property value="id"/>">
											<s:property value="name"/></option>
									</s:iterator>
								</select>
								<span id="validateGroup" style="display: none;"><strong style="color: red">Note : </strong>Changing group may invalidate product offer.</span>
					</s:else>
						</div>
					</div>
					<s:select 	 name="pkgData.status" 	id="pkgStatus"	    key="pkg.status" list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" 		cssClass="form-control" />
					<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
						<s:select name="pkgData.type" key="pkg.type" list="@com.elitecore.corenetvertex.pkg.PkgType@getUserPlan()" 		cssClass="form-control"  onchange="checkForAddOn();" disabled="true"/>
					</s:if>
					<s:select 	 id="pkgQuotaProfileType"	name="pkgData.quotaProfileType" key="pkg.quotaprofiletype" 	list="@com.elitecore.corenetvertex.constants.QuotaProfileType@values()" cssClass="form-control"	listValue="getVal()" disabled="true"/>
					<s:hidden    name="pkgData.quotaProfileType"/>
					<s:hidden id="pkgType" name="pkgData.type"/>
					<s:hidden 	 id="packageMode" name="pkgData.packageMode"  />

				</div>
				<s:hidden name="currency" id="currency" value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/>
				<div class="col-xs-6">

					<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name() == pkgData.packageMode || @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() == pkgData.packageMode}">
						<s:select name="currencyCombo" id="currencyCombo" key="pkg.currency"
								  cssClass="form-control select2" list="@java.util.Currency@getAvailableCurrencies()"
								  listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'"
								  value="pkgData.currency" cssStyle="width:100%" disabled="true" />
					</s:if>

					<s:else>
					<s:select name="currencyCombo" id="currencyCombo" key="pkg.currency"
							  cssClass="form-control select2" list="@java.util.Currency@getAvailableCurrencies()"
							  listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'" value="pkgData.currency"
							  cssStyle="width:100%" disabled="!@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isMultiCurrencyEnable()" onchange="getPackagesBasedOnCurrency();"/>

					</s:else>
					<s:if test="%{@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name() != pkgData.type}">
						<s:if test="pkgData.exclusiveAddOn==true">
							<s:select disabled="true"		name="pkgData.exclusiveAddOn" 	key="pkg.exclusiveaddon" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control"  />
							<s:hidden 	 name="pkgData.exclusiveAddOn"  />
						</s:if>
						<s:else>
							<s:select 	id="exclusiveAddOn"	name="pkgData.exclusiveAddOn" 	key="pkg.exclusiveaddon" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control" />
						</s:else>

					</s:if>
					<s:else>
						<s:datepicker 	name="pkgData.availabilityStartDate" key="pkg.availability.start.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
										  showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"
										  id="availabilityStartDate" readonly="true" />

						<s:datepicker 	name="pkgData.availabilityEndDate" key="pkg.availability.end.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
										  showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"
										  id="availabilityEndDate" readonly="true"  />
						<s:select 	id="preferPromotionalQos"	name="pkgData.alwaysPreferPromotionalQoS" 	key="pkg.preferpromotionalqos" list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()" listKey="isBooleanValue()" listValue="getStringNameBoolean()" cssClass="form-control" />
						<s:hidden name="pkgData.orderNumber"/>
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
					<button class="btn btn-primary btn-sm" type="submit" role="submit" formaction="update" id="btnUpdate">
						<span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
					</button>
					<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel"  style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${pkgData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg" /></button>
				</div>
			</div>

		</s:form>
	</div>
</div>

<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/select2/js/nvselect2groups.js"></script>

<script type="text/javascript">
var existingProductGroup = [];
function validateForm(){
	var isValidName = verifyUniquenessOnSubmit('pkgName','update','<s:text name="pkgData.id"/>','com.elitecore.corenetvertex.pkg.PkgData','','');
	var pkgtype =$("#pkgType").val();

	if (pkgtype === '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name()"/>') {
		return isValidName && promotionalPkgValidation();
	}

	return  (isValidName);
}

function getPackagesBasedOnCurrency() {
	var currencyValue = $("#currencyCombo").val();
	$("#currency").val(currencyValue);

	var systemParameterUpdated = <s:property value="getCurrencyUpdateAllowed()"/>;
	if(systemParameterUpdated == false){
		$("#currencyCombo").attr("disabled",true);
	}
}

$(function(){
	$(".select2").select2();
	checkForAddOn();
	checkForPackageMode();
	checkForMandatoryField();
	fetchExistingProductGroup();
	getPackagesBasedOnCurrency();
})

function fetchExistingProductGroup() {
    existingProductGroup = $('[name=groupIds]').val();
}

$('[name=groupIds]').on("change",function() {
    validateProductGroup(existingProductGroup, $('[name=groupIds]').val());
})
</script>
<%@include file="/view/policydesigner/pkg/PackageValidation.jsp"%>
