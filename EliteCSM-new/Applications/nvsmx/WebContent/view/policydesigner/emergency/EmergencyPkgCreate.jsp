<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">Create Emergency Package</h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/emergency/EmergencyPkg/create" id="pkgCreate" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token />
			<div class="row">
				<div class="col-xs-6">
					<s:textfield 	name="pkgData.name" 			key="pkg.name" 	id="pkgName"		cssClass="form-control focusElement" 
					    onblur="verifyUniqueness('pkgName','create','','com.elitecore.corenetvertex.pkg.PkgData','','');"  />					
					<s:textarea 	name="pkgData.description" 		key="pkg.description" 	cssClass="form-control" />
					<s:select 		name="groupIds" 	   		key="pkg.groups"    cssClass="form-control select2"	list="#session.staffBelongingGroups" id="groupNames" multiple="true" listKey="id" listValue="name" cssStyle="width:100%" />
					
					<s:hidden 		name="pkgData.packageMode" value="%{@com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}"/>
					<s:select 		name="pkgData.status" 			key="pkg.status" 	cssClass="form-control"			list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" id="pkgStatus"	 />
				</div>

				<div class="col-xs-6">


					<s:datepicker 	name="pkgData.availabilityStartDate" key="pkg.availability.start.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
								 	showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"  
								 	id="availabilityStartDate" readonly="true" />
								  
					<s:datepicker 	name="pkgData.availabilityEndDate" key="pkg.availability.end.date" 	parentTheme="bootstrap" changeMonth="true" changeYear="true" cssClass="form-control"
								 	showAnim="slideDown" duration="fast" showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy" timepicker="true" timepickerFormat="HH:mm:ss"  
								  	id="availabilityEndDate" readonly="true" />								  				
								  	

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
					<s:label name="differenceMessage" key="" id="differenceMessage" />
				</div>
			</div>

			<div class="row">
				<div class="col-xs-12" align="center">					
						<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>		
				</div>
			</div>

		</s:form>
	</div>
</div>
<script type="text/javascript">

$(function(){
	$( ".select2" ).select2();
	//checkForAddOn();
});
function validateForm(){
	var isValidName = verifyUniquenessOnSubmit('pkgName','create','','com.elitecore.corenetvertex.pkg.PkgData','','')
	var pkgtype =$("#pkgType").val();

	var isRetiredStatus = true;


    var status =$("#pkgStatus").val();
    if(status === '<s:property value="@com.elitecore.corenetvertex.constants.PkgStatus@RETIRED.name()"/>'){
    	setError('pkgStatus',"<s:text name='error.status.retired'/>");
    	isRetiredStatus = false;
    }

    return  (isValidName && isRetiredStatus);
}
</script>
<%@include file="/view/policydesigner/emergency/PackageValidation.jsp"%>