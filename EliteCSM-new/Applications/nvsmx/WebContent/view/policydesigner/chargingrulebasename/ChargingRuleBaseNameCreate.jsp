<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/js/UsageValidation.js"></script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="chargingrulebasename.create" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/chargingrulebasename/ChargingRuleBaseName/create" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token/>
			<div class="row">

				<div class="col-sm-9 col-lg-7">

					<s:hidden id="chargingRuleDataServiceTypeDatas" value="0"/>

					<s:textfield
							name="chargingRuleBaseNameData.name" 	key="chargingrulebasename.name"
							value=""
							id="chargingRuleBaseName" 				cssClass="form-control focusElement"
							tabindex="1" 							onblur="verifyUniqueness('chargingRuleBaseName','create','','com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData','','');"/>

					<s:textarea
							name="chargingRuleBaseNameData.description" 	key="chargingrulebasename.description"
							cssClass="form-control" 						rows="2"
							tabindex="2" />

					<s:select
							name="groupIds"  key="chargingrulebasename.groups"
							cssClass="form-control select2"			list="#session.staffBelongingGroups"
							id="groupNames" 						multiple="true"
							listKey="id" 							listValue="name"
							cssStyle="width:100%" 					tabindex="2" />

				</div>

				<div id="dataServiceTypes">

							<div class="col-sm-12">
								<table id='serviceTypesTable'  class="table table-blue table-bordered">

									<caption class="caption-header">
										<s:text name="chargingrulebasename.servicetypes"/>
										<div align="right" class="display-btn" >
											<span class="btn btn-group btn-group-xs defaultBtn" onclick="addServiceTypes();" tabindex="5"
												id="addRow"> <span class="glyphicon glyphicon-plus" ></span>
											</span>
										</div>
									</caption>

									<thead>
										<th><s:text name="chargingrulebasename.monitoringkey"/></th>
										<th><s:text name="chargingrulebasename.servicetype"/></th>
										<th style="text-align: center" ><s:text name="chargingrulebasename.slice.total"/></th>
										<th style="text-align: center"><s:text name="chargingrulebasename.slice.download"/></th>
										<th style="text-align: center"><s:text name="chargingrulebasename.slice.upload"/></th>
										<th style="text-align: center"><s:text name="chargingrulebasename.slice.time"/></th>
										<th></th>
									</thead>

									<tbody></tbody>

								</table>

							</div>

							<div class="col-sm-6"> </div>
				</div>

			<div class="row">
				<div class="col-xs-12" align="center">					
					<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="13"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
				</div>
			</div>

		</s:form>
	</div>
</div>
</div>


<%@include file="ChargingRuleBaseNameCommons.jsp"%>

<script type="text/javascript">

	function verifyUniquenessOfMonitoringKey(value,id,serviceTypeDataId){

			var monitoringKeyVal = $("#"+id).val();
			if(isNullOrEmpty(monitoringKeyVal)==false) {
				var isAllValidLocally = verifyUniquenessOfMonitoringKeyLocally();

				if ( isAllValidLocally ) {
					verifyUniqueness(id, 'create', '', 'com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData', '', 'monitoringKey');
				}
			}
	}

	function validateForm() {

		var validNameFlag = verifyUniquenessOnSubmit('chargingRuleBaseName','create','','com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData','','') ;

		if(validNameFlag==false){
			return false;
		}

		var rowCount = $('#serviceTypesTable tbody tr').length;

		if(isNullOrEmpty(rowCount) || rowCount == 0){
			return true;
		}

		var serviceTypesValidationFlag = verifyUniquenessOfServiceType();
		var moniKeyFlagLocally = verifyUniquenessOfMonitoringKeyLocally();

		if(! (moniKeyFlagLocally && serviceTypesValidationFlag)){
			return false;
		}

		var isAllSliceInfoPositive = checkSliceNegativeInformation();
		if(isAllSliceInfoPositive==false){
			return false;
		}

		var isAllFieldsValuesProper = checkSliceFieldsMaxAllowedValues();
		if(isAllFieldsValuesProper==false){
			return false;
		}

		var isAllSliceValid = checkSliceInformation();
		if(isAllSliceValid==false){
			$("#myModal").modal("show");
			return false;
		}

		var mkFlags = true;
		$('#serviceTypesTable tbody tr').each(function() {

			var thisRowChilds = $(this).children().eq(0).children();
			var monitoringKeyRowId 	 = thisRowChilds.find("input[type='text']").attr('id');
			var monitoringKeyVal 	 = thisRowChilds.find("input[type='text']").val();
			if(isNullOrEmpty(monitoringKeyVal)==false && isAllSliceValid==true) {
				var uniqFlag = verifyUniquenessOnSubmit(monitoringKeyRowId, 'create', '', 'com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData', '', 'monitoringKey');
			}
			mkFlags = mkFlags && uniqFlag;
		});

		return ( mkFlags && validNameFlag && isAllSliceValid );
	}

</script>