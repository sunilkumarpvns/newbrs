<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/js/UsageValidation.js"></script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="chargingrulebasename.update" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/chargingrulebasename/ChargingRuleBaseName/update" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token/>
			<div class="row">
				<div class="col-sm-9 col-lg-7">

					<input type="hidden" name="requestFromQosProfile" value="${requestScope.requestfromQosProfileView}" />

					<s:hidden name="chargingRuleBaseNameData.id" />
					<s:hidden id="chargingRuleDataServiceTypeDatas" value="%{chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas.size}"/>
					<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{chargingRuleBaseNameData.groups}"/>

					<s:textfield 	name="chargingRuleBaseNameData.name"
									key="chargingrulebasename.name"
									id="chargingRuleBaseName"
									cssClass="form-control focusElement"
									onblur="verifyUniqueness('chargingRuleBaseName','update','%{chargingRuleBaseNameData.id}','com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData','','');"
									tabindex="1" />

					<s:textarea 	name="chargingRuleBaseNameData.description"
								  	key="chargingrulebasename.description"
								   	cssClass="form-control"
								   	rows="2"
								   	tabindex="2" />

					<div class="form-group ">
						<label class="col-xs-12 col-sm-4 col-lg-3 control-label" id="lbl_pkgGroup">Groups </label>

						<div class="col-xs-12 col-sm-8 col-lg-9 controls">
							<select name="groupIds" key="chargingrulebasename.groups" class="form-control select2" style="width:100%"
									multiple="true">
								<s:iterator value="groupInfoList">
									<option locked="<s:property value="locked"/>" <s:property value="selected"/>
											value="<s:property value="id"/>" id="<s:property value="id"/>">
										<s:property value="name"/></option>
								</s:iterator>
							</select>
						</div>
					</div>


				</div>

				<div id="dataServiceTypes">
					<div class="col-sm-12">
						<table id='serviceTypesTable'  class="table table-blue table-bordered">

							<caption class="caption-header">
								<s:text name="chargingrulebasename.servicetypes"/>
								<div align="right" class="display-btn" >
									<span class="btn btn-group btn-group-xs defaultBtn" onclick="addServiceTypes();" tabindex="5" id="addRow"> <span class="glyphicon glyphicon-plus" ></span>
									</span>
								</div>
							</caption>

							<thead>
								<th><s:text name="chargingrulebasename.monitoringkey"/></th>
								<th style="text-align: center" ><s:text name="chargingrulebasename.servicetype"/></th>
								<th style="text-align: center"><s:text name="chargingrulebasename.slice.total"/></th>
								<th style="text-align: center"><s:text name="chargingrulebasename.slice.download"/></th>
								<th style="text-align: center"`><s:text name="chargingrulebasename.slice.upload"/></th>
								<th style="text-align: center"><s:text name="chargingrulebasename.slice.time"/></th>
								<th></th>
							</thead>

							<tbody>

								<s:iterator value="chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas" status="i" var="chargingRuleDataServiceTypeDatas">
									<tr>

										<td>
											<s:hidden value="%{#chargingRuleDataServiceTypeDatas.id}"/>

											<s:textfield value="%{#chargingRuleDataServiceTypeDatas.monitoringKey}"
														 name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].monitoringKey"
														 onblur="verifyUniquenessOfMonitoringKey(this.value, this.id,'%{#chargingRuleDataServiceTypeDatas.id}')"
														 cssClass="form-control"
														 id="monitoringKey%{#i.count-1}"
														 elementCssClass="col-xs-12"/>
										</td>

										<td >
											<s:select value="%{#chargingRuleDataServiceTypeDatas.dataServiceTypeData.id}"
													  name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].dataServiceTypeData.id"
													  list="dataServiceTypeDataList"
													  onblur="verifyUniquenessOfServiceType()"
													  id="dataServiceType%{#i.count-1}"
													  listValue="name"
													  listKey="id"
													  cssClass="form-control"
													  elementCssClass="col-xs-10">

											</s:select>
										</td>

										<td>

											<div class="col-sm-8 row">
														<s:textfield value="%{#chargingRuleDataServiceTypeDatas.sliceTotal}"
																	 name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].sliceTotal"
																	 cssClass="form-control"
																	 type="number"
																	 elementCssClass="col-xs-12"/>

													</div>
											<div class="col-sm-7 row">
														<s:select value="%{#chargingRuleDataServiceTypeDatas.sliceTotalUnit}"
																  name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].sliceTotalUnit"
																  list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
																  cssClass="form-control"
																  elementCssClass="col-xs-12" />
													</div>

										</td>
										<td>
												<div class="col-sm-7 row">
														<s:textfield value="%{#chargingRuleDataServiceTypeDatas.sliceDownload}"
																	 name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].sliceDownload"
																	 cssClass="form-control"
																	 type="number"
																	 elementCssClass="col-xs-12"/>

													</div>
											<div class="col-sm-7 row">
														<s:select value="%{#chargingRuleDataServiceTypeDatas.sliceDownloadUnit}"
																  name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].sliceDownloadUnit"
																  list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
																  cssClass="form-control"
																  elementCssClass="col-xs-12" />
													</div>

										</td>
										<td>
											<div class="col-sm-8 row">
														<s:textfield value="%{#chargingRuleDataServiceTypeDatas.sliceUpload}"
																	 name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].sliceUpload"
																	 cssClass="form-control"
																	 type="number"
																	 elementCssClass="col-xs-12"/>

													</div>
											<div class="col-sm-7 row">
														<s:select value="%{#chargingRuleDataServiceTypeDatas.sliceUploadUnit}"
																  name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].sliceUploadUnit"
																  list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
																  cssClass="form-control"
																  elementCssClass="col-xs-12" />
													</div>

										</td>
										<td>
											<div class="col-sm-8 row">
														<s:textfield value="%{#chargingRuleDataServiceTypeDatas.sliceTime}"
																	 name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].sliceTime"
																	 cssClass="form-control"
																	 type="number"
																	 elementCssClass="col-xs-12"/>

													</div>
											<div class="col-sm-7 row">
														<s:select value="%{#chargingRuleDataServiceTypeDatas.sliceTimeUnit}"
																  name="chargingRuleDataServiceTypeDatas[%{#i.count-1}].sliceTimeUnit"
																  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR, @com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE ,  @com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND}"
																  cssClass="form-control"
																  elementCssClass="col-xs-12" />
													</div>

										</td>

 										<td style='width:35px;'>
											<span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
												<a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span>
										</td>
									</tr>
								</s:iterator>

							</tbody>

						</table>
					</div>
					<div class="col-sm-6"> </div>
				</div>

				<div class="row">
					<div class="col-xs-12" align="center">
						<s:submit cssClass="btn  btn-sm btn-primary"
								  type="button"
								  role="button"
								  tabindex="13">
							<span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
						</s:submit>
						<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/chargingrulebasename/ChargingRuleBaseName/view?chargingRuleBaseNameId=${chargingRuleBaseNameData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> Charging Rule Base Name</button>
					</div>
				</div>
		</s:form>
	</div>
</div>
</div>

<%@include file="ChargingRuleBaseNameCommons.jsp"%>

<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/select2/js/nvselect2groups.js"></script>

<script type="text/javascript">

function verifyUniquenessOfMonitoringKey(value, id, serviceTypeDataId){

	if(isNullOrEmpty(value)) {
		setError(id,"MonitoringKey Required");
	}else{

		var isAllValidLocally = verifyUniquenessOfMonitoringKeyLocally();

		if(isAllValidLocally) {
			if(isNullOrEmpty(serviceTypeDataId)){
				verifyUniqueness(id, 'create', '', 'com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData', '', 'monitoringKey');
			}else {
				verifyUniqueness(id, 'update', serviceTypeDataId, 'com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData', '', 'monitoringKey');
			}
		}
	}
}

function validateForm() {


	var validNameFlag = verifyUniquenessOnSubmit('chargingRuleBaseName','update','<s:text name="chargingRuleBaseNameData.id"/>','com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData','','') ;

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
		var serviceTypeDataObjID = thisRowChilds.eq(0).val();
		var monitoringKeyVal 	 = thisRowChilds.find("input[type='text']").val();
		var monitoringKeyRowId 	 = thisRowChilds.find("input[type='text']").attr('id');

		var uniqFlag = true ;
		if(isNullOrEmpty(serviceTypeDataObjID)) {
			if(isNullOrEmpty(monitoringKeyVal)==false) {

				uniqFlag = verifyUniquenessOnSubmit(monitoringKeyRowId, 'create', '', 'com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData', '', 'monitoringKey');
			}
		}else{
			if(isNullOrEmpty(monitoringKeyVal)==false) {

				uniqFlag = verifyUniquenessOnSubmit(monitoringKeyRowId, 'update', serviceTypeDataObjID, 'com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData', '', 'monitoringKey');
			}
		}

		mkFlags = mkFlags && uniqFlag;

	});

 	return ( validNameFlag && mkFlags && moniKeyFlagLocally );

}

</script>