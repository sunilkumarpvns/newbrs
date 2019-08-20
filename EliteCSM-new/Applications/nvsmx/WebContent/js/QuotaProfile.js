var ALL_SERVICE_ID = "DATA_SERVICE_TYPE_1";

function addRow(tableid, index) {
		var rowId = tableid + "_row" + index;
		if($("#" + rowId).length){
				edit(tableid,rowId);
		}else{
			appendRow(tableid, index, rowId);
		}
	
	}
	function getFUPLevel(tableid){
		var fuplvl = 0;
		if(tableid == "tbl0"){
			fuplvl = 0;
		}else if(tableid == "tbl1"){
			fuplvl = 1;
		}else if(tableid == "tbl2"){
			fuplvl = 2;
		}
		return fuplvl;
	}
	function appendRow(tableid, index, rowId){
		var counts = index;
		var fuplvl;
		fuplvl = getFUPLevel(tableid);
		
		var arrowUpId = "arrowUp" + index;
		var arrowDownId = "arrowDown" + index;
		var serviceDataName = "quotaProfileDetailDatas";
		var fupLevelId = serviceDataName + "[" + counts + "].fupLevel";
		var serviceId = serviceDataName + "[" + counts + "].serviceId";
		var aggregationKeyId = serviceDataName + "[" + counts + "].aggregationKey";
		var totalQuotaId = serviceDataName + "[" + counts + "].total";
		var totalQuotaUnitId = serviceDataName + "[" + counts + "].totalUnit";
		var totalUploadId = serviceDataName + "[" + counts + "].upload";
		var totalUploadUnitId = serviceDataName + "[" + counts + "].uploadUnit";
		var totalDownloadId = serviceDataName + "[" + counts + "].download";
		var totalDownloadUnitId = serviceDataName + "[" + counts + "].downloadUnit";
		var totalTimeId = serviceDataName + "[" + counts + "].time";
		var totalTimeUnitId = serviceDataName + "[" + counts + "].timeUnit";

		var totalQuotaDetailId = "totalData" + counts;
		var totalUploadIdForSpan = "uploadData" + counts;
		var totalDownloadIdForSpan = "downloadData" + counts;

		var rowStr = rowId.toString();
		var serviceDataId = rowStr + "_service";
		var aggregationKeyDataId = rowStr + "_aggregationKey";
		var totalId = rowStr + "_total";
		var totalUnitDataId = rowStr + "_totalUnit";
		var uploadId = rowStr + "_upload";
		var uploadUnitDataId = rowStr + "_uploadUnit";
		var downloadId = rowStr + "_download";
		var downloadUnitId = rowStr + "_downloadUnit";
		var timeId = rowStr + "_time";
		var timeUnitId = rowStr + "_timeUnit";
		$("#" + tableid).append('<tr id='+rowId+' ><input type="hidden"  name='+fupLevelId+' value='+fuplvl+'><td style="text-align:left"><input type="hidden"  name='+serviceId+' id='+serviceDataId+'>'
								+ '<label id='+rowId+'lbl0 class="defaultLabel serviceType"></label>'
								+ '</td><td><input type="hidden"  name='+aggregationKeyId+' id='+aggregationKeyDataId+'>'
								+ '<label id='+rowId+'lbl1 class="defaultLabel aggregationKey"></label>'
								+ '</td><td style="text-align:right">'
								+ '<input type="hidden" name='+totalQuotaId+'  id='+totalId+'>'
								+ '<input type="hidden" name='+totalQuotaUnitId+'  id='+totalUnitDataId+'>'
								+ '<span class="displayText" id='+totalQuotaDetailId+'>'
								+ '<label id='+rowId+'lbl2 class="defaultLabel" ></label>'
								+ ' '
								+ '<label id='+rowId+'lbl3 class="defaultLabel" ></label>'
								+ ' '
								+ '</span>'
								+ '<input type="hidden" name='+totalDownloadId+' id='+downloadId+'>'
								+ '<input type="hidden" name='+totalDownloadUnitId+' id='+downloadUnitId+'>'
								+ '<span class="displayText" id='+totalDownloadIdForSpan+'>'
								+ '<label id='+rowId+'lbl4 class="defaultLabel" ></label>'
								+ '  '
								+ '<label id='+rowId+'lbl5 class="defaultLabel" ></label>'
								+ '<span class="glyphicon glyphicon-arrow-down up-down-arrow" id='+arrowDownId+'></span></span>'
								+ '<input type="hidden" name='+totalUploadId+' id='+uploadId+'>'
								+ '<input type="hidden" name='+totalUploadUnitId+'  id='+uploadUnitDataId+'>'
								+ '<span class="displayText" id='+totalUploadIdForSpan+'><label id='+rowId+'lbl6 class="defaultLabel" ></label>'
								+ ' '
								+ '<label id='+rowId+'lbl7 class="defaultLabel"></label>'
								+ '<span class="glyphicon glyphicon-arrow-up up-down-arrow" id='+arrowUpId+'></span>'
								+ ' '
								+ '</span>'
								+ '</td><td style="text-align:right" >'
								+ '<label id='+rowId+'lbl8 class="defaultLabel" ></label>'
								+ ' '
								+ '<input type="hidden" name='+totalTimeId+'  id='+timeId+'>'
								+ '<input type="hidden" name='+totalTimeUnitId+' id='+timeUnitId+'>'
								+ '<label id='+rowId+'lbl9 class="defaultLabel" ></label>'
								+ '</td><td><span class="btn defaultBtn"><a><span class="glyphicon glyphicon-pencil" title="edit" onclick=addRow("'
								+ tableid
								+ '","'
								+ index
								+ '");></span></a></span></td>'
								+ '<td><span class="btn defaultBtn" onclick=removeRow("'+tableid+'",this,'+index+')> <a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td></tr>');
		getDialogData(rowId);
		clearDialog();
		$("#serviceDialog").modal('hide');

	}
	function clearDialog() {
		$("#serviceData").val("DATA_SERVICE_TYPE_1");
		$("#aggregationkey").val("BILLING_CYCLE");
		$("#totalQuotaDialog").val("");
		$("#totalUploadDialog").val("");
		$("#totalDownloadDialog").val("");
		$("#totalTimeDialog").val("");
		clearErrorMessages("serviceData");
		clearErrorMessages("aggregationkey");
		$("#serviceData").removeAttr('disabled');
	}
	
	function openDialogBox(tableid) {
		$("#serviceDialog").modal('show');
		$('#btnAdd').unbind('click');
		$("#btnAdd").text("Add");
		$("#btnAdd").on('click', function() {
			if(validateDialogData()){
				var isDefaultBillingCycle = checkForDefaultServiceBillingCycle(tableid);
				var nextService = $("#serviceData option:selected").val();
				var aggregationKey = $("#aggregationkey option:selected").text();
				var maxServiceCount = getServiceCount(tableid,"",nextService,aggregationKey);
		        if(maxServiceCount > 5){
		        	setError("serviceData","Max 5 Other Services are configured");
		        }else{
		        	if(maxServiceCount != 0 && isDefaultBillingCycle == true){
		        		addRow(tableid, index);
		        		index++;
		        	}
		        }
			}
		});

	}
	
	function getServiceCount(tableid,rowId,nextServiceType,currentAK){
		
		var serviceIdMap = new Object();
		var aggregationKeyMap = new Object();
		var count = 1;

		serviceIdMap[nextServiceType] = nextServiceType;
		var serviceAggregationKeyCombo = tableid + ":" + nextServiceType +":"+currentAK;
		aggregationKeyMap[serviceAggregationKeyCombo] = serviceAggregationKeyCombo;

		$("#"+tableid+" tbody tr").each(function() {
			var currentRowId = $(this).attr('id');
			if (rowId == currentRowId) {
				return 'continue'; // TO continue for each loop in jquery any "non-false" value can do that
			}
			var serviceTypeId = $("#" + currentRowId + "_service").val();
			var AK = $("#" + currentRowId + "_aggregationKey").val();
			var serviceAggregationKey = tableid + ":" + serviceTypeId + ":" + AK;
			var key = serviceIdMap[serviceTypeId];
			var serviceAggregation = aggregationKeyMap[serviceAggregationKey];
			if (key == undefined && nextServiceType != ALL_SERVICE_ID) {
				serviceIdMap[serviceTypeId] = serviceTypeId;
				if (serviceTypeId != ALL_SERVICE_ID) {
					count++;
				}
			} else if (serviceAggregation != undefined) {
				setError("aggregationkey", "Service + Aggregation Key combination exists");
				count = 0;
				return false;
			}

		});
		return count;
	}

	function edit(tableid,rowId){
		setDialogData(rowId);
		$("#btnAdd").text("Edit");
		$("#serviceDialog").modal('show');
		$('#btnAdd').unbind('click');
		$("#btnAdd").on('click', function() {
			if(validateDialogData()){
				var isDefaultBillingCycle = checkForDefaultServiceBillingCycle(tableid);
				var nextService = $("#serviceData option:selected").val();
				var aggregationKey = $("#aggregationkey option:selected").text();
				var maxServiceCount = getServiceCount(tableid,rowId,nextService,aggregationKey);
				if(maxServiceCount > 5){
					setError("serviceData","Max 5 Other Services are configured");
				}else{
					if(maxServiceCount != 0 && isDefaultBillingCycle == true)
						editRow(rowId);
				}
			}
		});

	}
	function editRow(rowId){
		var flag = validateDialogData();
		if(flag == true){
			getDialogData(rowId);
			clearDialog();
			$("#serviceDialog").modal('hide');
			$(rowId).attr("class", "");
		}else{
			$("#serviceDialog").modal('show');
		}
	}

	function setDialogData(rowId){
		var serviceDataId = rowId + "_service";
		var aggregationKeyDataId = rowId + "_aggregationKey";
		var totalId = rowId + "_total";
		var totalUnitDataId = rowId + "_totalUnit";
		var uploadId = rowId + "_upload";
		var uploadUnitDataId = rowId + "_uploadUnit";
		var downloadId = rowId + "_download";
		var downloadUnitId = rowId + "_downloadUnit";
		var timeId = rowId + "_time";
		var timeUnitId = rowId + "_timeUnit";
		$("#serviceData").val($("#" + serviceDataId).val());
		$("#serviceData").attr('disabled','disabled');
		$("#aggregationkey").val($("#" + aggregationKeyDataId).val());
		$("#totalQuotaDialog").val($("#" + totalId).val());
		if (isNullOrEmpty($("#" + totalId).val())) {
			$("#ddlTotalUnitDialog").val("MB");
		} else {
			$("#ddlTotalUnitDialog").val($("#" + totalUnitDataId).val());
		}
		$("#totalUploadDialog").val($("#" + uploadId).val());
		if (isNullOrEmpty($("#" + uploadId).val())) {
			$("#ddlUploadUnitDialog").val("MB");
		} else {
			$("#ddlUploadUnitDialog").val($("#" + uploadUnitDataId).val());
		}
		$("#totalDownloadDialog").val($("#" + downloadId).val());
		if (isNullOrEmpty($("#" + downloadId).val())) {
			$("#ddlDownloadUnitDialog").val("MB");
		} else {
			$("#ddlDownloadUnitDialog").val($("#" + downloadUnitId).val());
		}
		if (isNullOrEmpty($("#" + timeId).val())) {
			$("#totalTimeUnitDialog").val("DAY");
		} else {
			$("#totalTimeUnitDialog").val($("#" + timeUnitId).val());
		}
		$("#totalTimeDialog").val($("#" + timeId).val());
	}
	
	// check for default service billing cycle
	function checkForDefaultServiceBillingCycle(tableid){
		var flag = true;
		var serviceId = $("#serviceData option:selected").val();
		var aggregationKey = $("#aggregationkey option:selected").text();
		if(serviceId == ALL_SERVICE_ID && aggregationKey == "Billing Cycle"){
			setError("aggregationkey","Billing Cycle can not be configured with Default Service");
			flag = false;
		}
		return flag;
	}
	
	function removeRow(tableid,obj,index){
		$(obj).parent().parent().remove();
	}
	$(function(){
		$("#serviceData").on('change', function() {
			clearErrorMessages("serviceData");
		});
		$("#aggregationkey").on('change', function() {
			clearErrorMessages("aggregationkey");
		});
	});
	
	function getDialogData(rowId){
		var serviceDataId = rowId + "_service";
		var aggregationKeyDataId = rowId + "_aggregationKey";
		var totalId = rowId + "_total";
		var totalUnitDataId = rowId + "_totalUnit";
		var uploadId = rowId + "_upload";
		var uploadUnitDataId = rowId + "_uploadUnit";
		var downloadId = rowId + "_download";
		var downloadUnitId = rowId + "_downloadUnit";
		var timeId = rowId + "_time";
		var timeUnitId = rowId + "_timeUnit";
		$("#" + serviceDataId).val($("#serviceData option:selected").val());
		$("#" + rowId + "lbl0").val($("#serviceData option:selected").text());
		$("#" + rowId + "lbl0").text($("#serviceData option:selected").text());
		$("#" + aggregationKeyDataId).val($("#aggregationkey").val());
		$("#" + rowId + "lbl1").val($("#aggregationkey").val());
		$("#" + rowId + "lbl1").text($("#aggregationkey option:selected").text());
		if(isNullOrEmpty($("#totalQuotaDialog").val())){
			$("#" + totalId).val("");
			$("#" + totalUnitDataId).val($("#ddlTotalUnitDialog").val());
		}else{
			$("#" + totalId).val($("#totalQuotaDialog").val());
			$("#" + totalUnitDataId).val($("#ddlTotalUnitDialog").val());
			$("#" + rowId + "lbl2").val($("#totalQuotaDialog").val());
			$("#" + rowId + "lbl3").val($("#ddlTotalUnitDialog").val());
			$("#" + rowId + "lbl2").text($("#totalQuotaDialog").val());
			$("#" + rowId + "lbl3").text($("#ddlTotalUnitDialog").val());
		}
		if(isNullOrEmpty($("#totalUploadDialog").val())){
			$("#" + uploadId).val("");
			$("#" + uploadUnitDataId).val($("#ddlUploadUnitDialog").val());
		}else{
			$("#" + uploadId).val($("#totalUploadDialog").val());
			$("#" + uploadUnitDataId).val($("#ddlUploadUnitDialog").val());
			$("#" + rowId + "lbl6").val($("#totalUploadDialog").val());
			$("#" + rowId + "lbl7").val($("#ddlUploadUnitDialog").val());
			$("#" + rowId + "lbl6").text($("#totalUploadDialog").val());
			$("#" + rowId + "lbl7").text($("#ddlUploadUnitDialog").val());
		}
        if(isNullOrEmpty($("#totalDownloadDialog").val())){
			$("#" + downloadId).val("");
			$("#" + downloadUnitId).val($("#ddlDownloadUnitDialog").val());
		}else{
			$("#" + downloadId).val($("#totalDownloadDialog").val());
			$("#" + downloadUnitId).val($("#ddlDownloadUnitDialog").val());
			$("#" + rowId + "lbl4").val($("#totalDownloadDialog").val());
			$("#" + rowId + "lbl5").val($("#ddlDownloadUnitDialog").val());
			$("#" + rowId + "lbl4").text($("#totalDownloadDialog").val());
			$("#" + rowId + "lbl5").text($("#ddlDownloadUnitDialog").val());
		}
		if(isNullOrEmpty($("#totalTimeDialog").val())){
			$("#" + timeId).val("");
			$("#" + timeUnitId).val($("#totalTimeUnitDialog").val());
		}else{
			$("#" + timeId).val($("#totalTimeDialog").val());
			$("#" + timeUnitId).val($("#totalTimeUnitDialog").val());
			$("#" + rowId + "lbl8").val($("#totalTimeDialog").val());
			$("#" + rowId + "lbl9").val($("#totalTimeUnitDialog").val());
			$("#" + rowId + "lbl8").text($("#totalTimeDialog").val());
			$("#" + rowId + "lbl9").text($("#totalTimeUnitDialog").val());
		}

		var tds = $("#" + rowId).find('td');
		var span = $(tds).find('span[class="displayText"]');
		var innerSpan = $(span).find('span');
		if(isNullOrEmpty($("#" + totalId).val())){
			$(span.eq(0)).attr('style', 'display:none');
		}else{
			$(span.eq(0)).removeAttr('style', 'display:none');
		}
		if(isNullOrEmpty($("#" + downloadId).val())){
			$(span.eq(1)).attr('style', 'display:none');
			$(innerSpan.eq(0)).attr('style', 'display:none');
		}else{
			$(span.eq(1)).removeAttr('style', 'display:none');
			$(innerSpan.eq(0)).removeAttr('style', 'display:none');
		}
		if(isNullOrEmpty($("#" + uploadId).val())){
			$(span.eq(2)).attr('style', 'display:none');
			$(innerSpan.eq(1)).attr('style', 'display:none');
		}else{
			$(span.eq(2)).removeAttr('style', 'display:none');
			$(innerSpan.eq(1)).removeAttr('style', 'display:none');
		}

	}

	
	function validateDialogData(){
		if (isNullTotal($("#totalQuotaDialog").val(),$("#totalUploadDialog").val(),$("#totalDownloadDialog").val(),
				$("#totalTimeDialog").val())) {
			alert("Please configure Total Quota or Total Time");
			return false;
		}  else if (isNegativeNumber($("#totalQuotaDialog").val())) {
			alert("Please configure positive numeric value for the Total Quota");
			return false;
		} else if (isNegativeNumber($("#totalUploadDialog").val())) {
			alert("Please configure positive numeric value for the Upload Quota");
			return false;
		} else if (isNegativeNumber($("#totalDownloadDialog").val())) {
			alert("Please configure positive numeric value for the Download Quota");
			return false;
		} else if (isNegativeNumber($("#totalTimeDialog").val())) {
			alert("Please configure positive numeric value for Total time");
			return false;
		} else {
			return true;
		}
	}
	function addFUP(){
		fupLevel = fupLevel + 1;
		if (fupLevel == 2){
			$("#btnAddFup").attr("disabled", "disabled");
		}
		$(fupDiv).clone().appendTo("#fup" + fupLevel).attr("id", "fupLevel0").find("*").each(function() {
					changeNameProperty(this);
				});
		changeIdPropertyAndDataTarget(fupLevel);
		index++;
	}
	function changeNameProperty(obj){
		var name = obj.name || "";
		var value = obj.value || "";
		if(isNullOrEmpty(value) == false){
			if(obj.type === 'text'){
				var val = "";
				obj.value = val;
			}
		}
		if (isNullOrEmpty(name) == false) {
			var str = name.replace("quotaProfileDetailDatas[0]", "quotaProfileDetailDatas["+ index + "]");
			obj.name = str;
		}
		
	}
	function changeIdPropertyAndDataTarget(fupLevel){
		var fup = "fup" + fupLevel;
		var tableName = "tbl" + fupLevel;

		$("#" + fup).find(".fieldSet-line").attr("id", fup + "FieldSet");
		$("#" + fup).find("div").first().attr("id", "fupLevel" +fupLevel );
		$("#" + fup).find("#hsqDetail").attr("data-target", "#" +"fupLevel" +fupLevel);
		$("#" + fup).find( "#" +"fupLevel" +fupLevel).find("#fupLevel").attr("value",
				fupLevel);

		$("#" + fup).find("#fupName").attr("value", "FUP" + fupLevel);
		$("#" + fup).find("#fupName").text("FUP" + fupLevel +" Level Quota ");

		$("#" + fup).find("#serviceConfiguration").find("table").attr("id",tableName);
		$("#" + fup).find("#serviceConfiguration").find("#addRow").attr(
				"onclick", "openDialogBox('" + tableName.toString() + "')");
	}
	
	
	function isNullTotal(total,upload,download,time){
		if(isNullOrEmpty(total)
		&& isNullOrEmpty(upload)
		&& isNullOrEmpty(download) 
		&& isNullOrEmpty(time)){
			return true;
		}else{
			return false;
		}
	}

	function validateBalanceAndFUPLevel(){
		var configureBalanceLevel = $("#balanceLevel").val();
		configureBalanceLevel = numericConfigureBalanceLevel(configureBalanceLevel);
		var isValidConfiguration = false;
		$(".fupLevel").each(function(){
			var configuredFupLevel = $(this).val();
			if(configureBalanceLevel == configuredFupLevel){
				isValidConfiguration =true;
				return false;
			}
		})
		if(!isValidConfiguration) {
			setError("balanceLevel","Configured Fup Level & Balance Level  must be in sync");
		}
		return isValidConfiguration;
	}


	function numericConfigureBalanceLevel(level){
		if(level == 'HSQ'){
			return "0";
		}else if(level == 'FUP1'){
			return "1";
		}else {
			return "2";
		}
	}