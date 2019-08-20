<table id="tempsyServiceWiseTable" style="display: none;">
	<tr>
		<td><s:textfield cssClass="form-control" elementCssClass="col-xs-12"/> </td>
		<td>
			<s:select list="dataServiceTypeDataList"
					  listValue="name"
					  listKey="id" cssClass="form-control focusElement" elementCssClass="col-xs-10"></s:select></td>
		<td>
			<!-- SLICE TOTAL	-->

			<div class="col-sm-8 row">
				<s:textfield cssClass="form-control" placeholder="TOTAL"   type="number" elementCssClass="col-xs-12"	name="sliceTotal"/>

			</div>
			<div class="col-sm-7 row">
				<s:select
						name="sliceTotalUnit"
						cssClass="form-control"
						elementCssClass="col-xs-12"
						list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
						value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"	/>
			</div>
		</td>
		<td>
			<!-- SLICE DOWNLOAD	-->

			<div class="col-sm-8 row">
				<s:textfield cssClass="form-control" placeholder="DOWNLOAD"  type="number" elementCssClass="col-xs-12"  name="sliceDownload" 	/>

			</div>
			<div class="col-sm-7 row">
				<s:select
						name="sliceDownloadUnit"
						cssClass="form-control"
						elementCssClass="col-xs-12"
						list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
						value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"	/>
			</div>

		</td>
		<td>
			<!-- SLICE UPLOAD	-->


			<div class="col-sm-8 row">
				<s:textfield cssClass="form-control" placeholder="UPLOAD" type="number" elementCssClass="col-xs-12"	 name="sliceUpload" />

			</div>
			<div class="col-sm-7 row">
				<s:select
						name="sliceUploadUnit"
						cssClass="form-control"
						elementCssClass="col-xs-12"
						list="@com.elitecore.corenetvertex.constants.DataUnit@values()"
						value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"	/>
			</div>

		</td>
		<td>
			<!-- SLICE TIME	-->

			<div class="col-sm-8 row">
				<s:textfield cssClass="form-control" placeholder="TIME" type="number" elementCssClass="col-xs-12" name="sliceTime"/>

			</div>
			<div class="col-sm-7 row">

				<s:select
						cssClass="form-control"
						elementCssClass="col-xs-12"
						name="sliceTimeUnit"
						id="sliceTimeUnit"

						list="#{@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR, @com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE ,  @com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND}"
						value="%{@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE}" />
			</div>

		</td>
		<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>
	</tr>
</table>

<!-- Modal -->
<div id="myModal" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h5 class="modal-title"> Error </h5>
			</div>

			<div class="modal-body" align="center">
				<p style="color: #dd4444" >Please configure positive value for Either of Total, Download, Upload or Time</p>
			</div>
		</div>

	</div>
</div>

<style>
	.modal {
		text-align: center;
	}

	@media screen and (min-width: 768px) {
		.modal:before {
			display: inline-block;
			vertical-align: middle;
			content: " ";
			height: 100%;
		}
	}

	.modal-dialog {
		display: inline-block;
		text-align: left;
		vertical-align: middle;
		text-decoration-color: #dd4444;
		width : auto;
	}

	.modal-header
	{
		background-color: #dd4444;
	}

</style>

<script type="text/javascript">

	$(function(){
		$(".select2").select2();
	})

	var i = $("#chargingRuleDataServiceTypeDatas").val();;
	function addServiceTypes() {

		$("#serviceTypesTable tbody").append("<tr>" + $("#tempsyServiceWiseTable").find("tr").html() + "</tr>");

		var ID = "id";
		var NAME = "name";
		var ON_BLUR = "onblur";
		var MK_ON_BLUR_FUNCTION =	"verifyUniquenessOfMonitoringKey(this.value,this.id,'');"
		var ST_ON_BLUR_FUNCTION =	"verifyUniquenessOfServiceType();"
		var MK_ID = "monitoringKey"+i;

		$("#serviceTypesTable").find("tr:last td:nth-child(1)").find("input").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].monitoringKey');
		$("#serviceTypesTable").find("tr:last td:nth-child(1)").find("input").attr(ID,MK_ID);
		$("#serviceTypesTable").find("tr:last td:nth-child(1)").find("input").attr(ON_BLUR,	MK_ON_BLUR_FUNCTION);

		$("#"+MK_ID).focus();

		$("#serviceTypesTable").find("tr:last td:nth-child(2)").find("select").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].dataServiceTypeData.id');
		$("#serviceTypesTable").find("tr:last td:nth-child(2)").find("select").attr(ID,'dataServiceType'+i);
		$("#serviceTypesTable").find("tr:last td:nth-child(2)").find("select").attr(ON_BLUR,	ST_ON_BLUR_FUNCTION);

		$("#serviceTypesTable").find("tr:last td:nth-child(3)").find("input[name='sliceTotal']").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].sliceTotal');
		$("#serviceTypesTable").find("tr:last td:nth-child(3)").find("input").attr(ID,'sliceTotal'+i);
		$("#serviceTypesTable").find("tr:last td:nth-child(3)").find("select[name='sliceTotalUnit']").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].sliceTotalUnit');

		$("#serviceTypesTable").find("tr:last td:nth-child(4)").find("input[name='sliceDownload']").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].sliceDownload');
		$("#serviceTypesTable").find("tr:last td:nth-child(4)").find("input").attr(ID,'sliceDownload'+i);
		$("#serviceTypesTable").find("tr:last td:nth-child(4)").find("select[name='sliceDownloadUnit']").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].sliceDownloadUnit');

		$("#serviceTypesTable").find("tr:last td:nth-child(5)").find("input[name='sliceUpload']").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].sliceUpload');
		$("#serviceTypesTable").find("tr:last td:nth-child(5)").find("input").attr(ID,'sliceUpload'+i);
		$("#serviceTypesTable").find("tr:last td:nth-child(5)").find("select[name='sliceUploadUnit']").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].sliceUploadUnit');

		$("#serviceTypesTable").find("tr:last td:nth-child(6)").find("input[name='sliceTime']").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].sliceTime');
		$("#serviceTypesTable").find("tr:last td:nth-child(6)").find("input").attr(ID,'sliceTime'+i);
		$("#serviceTypesTable").find("tr:last td:nth-child(6)").find("select[name='sliceTimeUnit']").attr(NAME,'chargingRuleBaseNameData.chargingRuleDataServiceTypeDatas['+i+'].sliceTimeUnit');

		i++;
	}

	function checkSliceInformation(){

		var isAllSliceInformationValid = true;

		$('#serviceTypesTable tbody tr').each(function() {

			var isAllChildValEmpty =  true;

			var currentRowChilds = $(this).children().eq(0).children();
			var currRowMonitoringKeyVal = currentRowChilds.find("input[type='text']").val();
			var sliceElementIds = new Array();
			if( isNullOrEmpty(currRowMonitoringKeyVal) == false ) {

				for (var sliceParamsIndex = 2; sliceParamsIndex < 6; sliceParamsIndex++) {
					var thisRowSliceChilds = $(this).children().eq(sliceParamsIndex).children();
					var sliceChildVal = thisRowSliceChilds.find("input[type='number']").val();

					var curreElementID = thisRowSliceChilds.find("input[type='number']").attr('id');
					sliceElementIds.push(curreElementID);

					if ( isNullOrEmpty(sliceChildVal) == false && parseInt(sliceChildVal)>0) {
						isAllChildValEmpty = false;
					}

				}

				if (isAllChildValEmpty == true) {
					var arrayLength = sliceElementIds.length;
					for (var i = 0; i < arrayLength; i++) {
						setError(sliceElementIds[i], "");
					}
					isAllSliceInformationValid = false;
				}
			}
		});
		return isAllSliceInformationValid;
	}

	function checkSliceNegativeInformation(){
		var isAllChildValValid = true;

		$('#serviceTypesTable tbody tr').each(function() {

			var currentRowChilds = $(this).children().eq(0).children();
			var currRowMonitoringKeyVal = currentRowChilds.find("input[type='text']").val();

			if( isNullOrEmpty(currRowMonitoringKeyVal) == false ) {

				for (var sliceParamsIndex = 2; sliceParamsIndex < 6; sliceParamsIndex++) {
					var thisRowSliceChilds = $(this).children().eq(sliceParamsIndex).children();
					var sliceChildVal = thisRowSliceChilds.find("input[type='number']").val();
					var curreElementID = thisRowSliceChilds.find("input[type='number']").attr('id');
					if(parseInt(sliceChildVal)<=0){
						setError(curreElementID, "Value must be Positive");
						isAllChildValValid =  false;
						break;
					}
				}
			}
		});

		return isAllChildValValid;
	}

	function verifyUniquenessOfServiceType(){

		var isAllServiceTypeValid = true;
		var serviceTypeArray = new Array()

		$('#serviceTypesTable tbody tr').each(function() {

			var thisServiceTypeRowChilds = $(this).children().eq(1).children();
			var servivceTypeVal = thisServiceTypeRowChilds.find("select").val();
			var servivceTypeRowId = thisServiceTypeRowChilds.find("select").attr('id');

			if(serviceTypeArray.lastIndexOf(servivceTypeVal) != -1){
				isAllServiceTypeValid = false;
				setError(servivceTypeRowId,"Duplicate DataServiceType selected");
			}else{
				clearErrorMessagesById(servivceTypeRowId);
			}
			serviceTypeArray.push(servivceTypeVal)
		});

		return isAllServiceTypeValid;
	}

	function verifyUniquenessOfMonitoringKeyLocally(){

		var isAllMonitoringKeyKValid = true;
		var monitoringKeyValArray = new Array();

		$('#serviceTypesTable tbody tr').each(function() {

			var thisRowChilds = $(this).children().eq(0).children();
			var monitoringKeyVal = thisRowChilds.find("input[type='text']").val();
			var monitoringKeyBoxId = thisRowChilds.find("input[type='text']").attr('id');

			if(verifyValidName(monitoringKeyBoxId,"Monitoring Key") == false) {
				isAllMonitoringKeyKValid = false;
			}else{
				if(monitoringKeyValArray.lastIndexOf($.trim(monitoringKeyVal)) != -1){
					isAllMonitoringKeyKValid = false;
					setError(monitoringKeyBoxId,"Duplicate MonitoringKey Found");
				}else{
					clearErrorMessagesById(monitoringKeyBoxId);
				}
				monitoringKeyValArray.push($.trim(monitoringKeyVal))
			}
		});

		return isAllMonitoringKeyKValid;
	}


	function checkSliceFieldsMaxAllowedValues(){
		var isAllChildValValid = true;
		var hasAnyUnitMatch =  false;

		$('#serviceTypesTable tbody tr').each(function() {

			var currentRowChilds = $(this).children().eq(0).children();
			var currRowMonitoringKeyVal = currentRowChilds.find("input[type='text']").val();

			if( isNullOrEmpty(currRowMonitoringKeyVal) == false ) {

				for (var sliceParamsIndex = 2; sliceParamsIndex < 6; sliceParamsIndex++) {
					var thisRowSliceChilds = $(this).children().eq(sliceParamsIndex).children();
					var sliceChildVal = thisRowSliceChilds.find("input[type='number']").val();
					var curreElementID = thisRowSliceChilds.find("input[type='number']").attr('id');
					var selectTagUnitVal = thisRowSliceChilds.find("select").val();

					if(selectTagUnitVal=='BYTE' || selectTagUnitVal == 'KB' || selectTagUnitVal=='MB' || selectTagUnitVal=='GB') {
						hasAnyUnitMatch = true;
						var isValidSliceQuota = isValidQuota(curreElementID, sliceChildVal, selectTagUnitVal);
						if(isValidSliceQuota==false){
							isAllChildValValid =  false;
							break;
						}
					}

					if(selectTagUnitVal=='SECOND' || selectTagUnitVal == 'MINUTE' || selectTagUnitVal=='HOUR') {
						hasAnyUnitMatch = true;
						var isValidSliceQuota = isValidSliceTime(curreElementID, sliceChildVal, selectTagUnitVal);
						if(isValidSliceQuota==false){
							isAllChildValValid =  false;
							break;
						}
					}

					if(hasAnyUnitMatch == false){
						isAllChildValValid =  false;
						alert("Invalid Unit Found");
						break;
					}
				}
			}
		});

		return isAllChildValValid;
	}

</script>