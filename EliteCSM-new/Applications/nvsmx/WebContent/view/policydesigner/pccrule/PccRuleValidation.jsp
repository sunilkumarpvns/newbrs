<%@taglib uri="/struts-tags/ec" prefix="s"%>
<script src="${pageContext.request.contextPath}/js/RatingGroup.js"></script>
<script type="text/javascript">

function disableSliceInformation() {
	var value = $("#usageMonitoring").val();
	if (value == "false") {
		$("#sliceInformation input").each(function() {
				$(this).attr('readonly', 'true');
		});
		$("#sliceInformation select").each(function() {
				$(this).attr('readonly', 'true');
		});
	} else {
		$("#sliceInformation input").each(function() {
			$(this).removeAttr('readonly');
		});
		$("#sliceInformation select").each(function() {
			$(this).removeAttr('readonly');
		});
	}

}

function verifyUsageMonitoring(){
	   var usageMonitoring = $("#usageMonitoring").val();

	    var sliceTotal = $("#slicetotal").val();
		var sliceDownload = $("#slicedownload").val();
		var sliceUpload = $("#sliceupload").val();
		var sliceTime = $("#slicetime").val();
		
		var sliceTotalUnit = $("#sliceTotalUnit").val();
		var sliceDownloadUnit = $("#sliceDownloadUnit").val();
		var sliceUploadUnit = $("#sliceUploadUnit").val();
		var sliceTimeUnit = $("#sliceTimeUnit").val();
		
		
	   if(isNullOrEmpty(sliceTotal)==true 
			   && isNullOrEmpty(sliceDownload)==true
			   && isNullOrEmpty(sliceUpload)==true
			   && isNullOrEmpty(sliceTime)==true && (usageMonitoring == "true" || usageMonitoring == true)){
	      setError("slicetime","<s:text name='pccrule.usagemonitoring.validation' />");
	      setError("slicetotal","");
	      setError("slicedownload","");
	      setError("sliceupload","");
	      return false;
	  }
	   if (isNullOrEmpty(sliceTotal) ==false && isNumberGreaterThanZero(sliceTotal)==false){
			setError('slicetotal', '<s:text name="error.postive.numeric"/>');
            return false;
		}
	   if (isValidQuota("slicetotal", sliceTotal, sliceTotalUnit) == false) {
			return false;
		}
		if (isNullOrEmpty(sliceDownload) ==false && isNumberGreaterThanZero(sliceDownload)==false){
			setError('slicedownload', '<s:text name="error.postive.numeric"/>');
			return false;
		}
		if (isValidQuota("slicedownload", sliceDownload, sliceDownloadUnit) == false) {
			return false;
		}
		if (isNullOrEmpty(sliceUpload) ==false && isNumberGreaterThanZero(sliceUpload)==false){
			setError('sliceupload', '<s:text name="error.postive.numeric"/>');
			return false;
		}
		if (isValidQuota("sliceupload", sliceUpload, sliceUploadUnit) == false) {
			return false;
		}
		if (isNullOrEmpty(sliceTime) ==false && isNumberGreaterThanZero(sliceTime)==false){
			setError('slicetime', '<s:text name="error.postive.numeric"/>');
			return false;
		}
		if(isValidSliceTime("slicetime",sliceTime,sliceTimeUnit) == false){
			return false;
		}

	   return true;
}

function verifyDownloads() {
    clearErrorMessagesById('mbrdl');
    clearErrorMessagesById('mbrul');
    clearErrorMessagesById('gbrdl');
    clearErrorMessagesById('gbrul');
       
        var mbrul = $("#mbrul").val();
        var mbrdl = $("#mbrdl").val();

        var gbrul = $("#gbrul").val();
        var gbrdl = $("#gbrdl").val();

        var mbrdlUnit=$("#mbrdlUnit").val();
		var mbrulUnit=$("#mbrulUnit").val();
		var gbrdlUnit=$("#gbrdlUnit").val();
		var gbrulUnit=$("#gbrulUnit").val();
        
        if (isNullOrEmpty(mbrdl) && isNullOrEmpty(gbrdl)) {
            
            var qciVal = $("#qci").val();
            if(qciVal<5){
            	setError('gbrdl', '<s:text name="pccrule.qos.error"/>');
            	setError('mbrdl', '<s:text name="pccrule.qos.error"/>');
            }else{
            	setError('mbrdl', '<s:text name="pccrule.qos.mbrdl.error"/>');
            }
            
            return false;
        }
        if (isNullOrEmpty(mbrdl) ==false && isNumberGreaterThanZero(mbrdl)==false){
			setError('mbrdl','<s:text name="error.postive.numeric"/>');
			return false;
		}
        if(isValidQos("mbrdl",mbrdl,mbrdlUnit)==false){
			return false;
	    }
	    
		if (isNullOrEmpty(mbrul) ==false && isNumberGreaterThanZero(mbrul)==false){
			setError('mbrul', '<s:text name="error.postive.numeric"/>');
			return false;
		}
	    if(isValidQos("mbrul",mbrul,mbrulUnit)==false){
			return false;
	    }
	
		
		if(isNullOrEmpty(gbrdl) == false && isNumberGreaterThanZero(gbrdl) == false){
			setError('gbrdl', '<s:text name="error.postive.numeric"/>');
			return false;
		}
	    if(isValidQos("gbrdl",gbrdl,gbrdlUnit)==false){
			return false;
	    }
	
		
		if (isNullOrEmpty(gbrul) ==false && isNumberGreaterThanZero(gbrul)==false){
			setError('gbrul', '<s:text name="error.postive.numeric"/>');
			return false;
		}
		if(isValidQos("gbrul",gbrul,gbrulUnit)==false){
			return false;
	    }
    return true;
   }
function disableQosInformation(){
	var value = $("#qci").val();
	if(value > 4){
		$("#gbrul").attr('disabled',true);
		$("#gbrdl").attr('disabled',true);
		$("#gbrulUnit").attr('disabled',true);
		$("#gbrdlUnit").attr('disabled',true);

	}else{
		$("#gbrul").removeAttr('disabled');
		$("#gbrdl").removeAttr('disabled');
		$("#gbrulUnit").removeAttr('disabled');
		$("#gbrdlUnit").removeAttr('disabled');
	}
}
var serviceTypeRatingGroupMapJson = JSON.parse('<s:property value="%{serviceTypeRatingGroupMapJson}" escapeHtml="false"/>');
var staffBelongingRatingGroupJson = JSON.parse('<s:property value="%{staffBelongingRatingGroupJson}" escapeHtml="false"/>');
var selectedChargingKey;
if ('<s:property value="%{selectedChargingKey}" escapeHtml="false"/>') {
    selectedChargingKey = JSON.parse('<s:property value="%{selectedChargingKey}" escapeHtml="false"/>');
}

function getSDF(serviceId) {
	$.ajax({
		async : true,
		type : "POST",
		dataType: "json",
		url : "${pageContext.request.contextPath}/subTable/policydesigner/pccrule/PccRule/getDefaultServiceFlow",
		data : { "serviceId": serviceId},
		success : function(data) {
			clearSDFTable();
				for( i in data){
					if(isNullOrEmpty(data[i]["Source IP"]) == true){
						data[i]["Source IP"] = '';
					}
					if(isNullOrEmpty(data[i]["Source Port"]) == true){
						data[i]["Source Port"] = '';
					}
					if(isNullOrEmpty(data[i]["Destination IP"]) == true){
						data[i]["Destination IP"] = '';
					}
					if(isNullOrEmpty(data[i]["Destination Port"]) == true){
						data[i]["Destination Port"] = '';
					}
					var tableRow ='<tr>'+
			         '<td style="width:18%"><select tabindex="11" name="pccRule.serviceDataFlowList['+i+'].flowAccess"  class="form-control form-control" id="flowAccess['+i+']" ><option value="permit in">PERMIT IN</option><option value="permit out">PERMIT OUT</option><option value="deny in">DENY IN</option><option value="deny out">DENY OUT</option></select></td>'+
					'<td style="width:12%"><select tabindex="12" name="pccRule.serviceDataFlowList['+i+'].protocol"  class="form-control form-control" id="protocol['+i+']"><option value="ip">IP</option><option value="6">TCP</option><option value="17">UDP</option></select></td>'+
					'<td><input class="form-control" tabindex="13" name="pccRule.serviceDataFlowList['+i+'].sourceIP" value="'+data[i]["Source IP"]+'" type="text"/></td>'+
					'<td><input class="form-control" tabindex="14" name="pccRule.serviceDataFlowList['+i+'].sourcePort" value="'+data[i]["Source Port"]+'"  type="text"/></td>'+
					'<td><input class="form-control" tabindex="15" name="pccRule.serviceDataFlowList['+i+'].destinationIP"  value="'+data[i]["Destination IP"]+'" type="text"/></td>'+
					'<td><input class="form-control" tabindex="16" name="pccRule.serviceDataFlowList['+i+'].destinationPort" value="'+data[i]["Destination Port"]+'" type="text"/></td>'+
					'<td><span class="btn defaultBtn" tabindex="17" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>'+
					'</tr>';
			$(tableRow).appendTo('#serviceDataFlowTable');
			var flowAccess=document.getElementById("flowAccess["+i+"]");
			var protocol=document.getElementById("protocol["+i+"]");
			if(data[i]['Flow Access']=="PERMIT OUT" || data[i]['Flow Access']=="permit out")
				flowAccess.options[1].selected = "selected";																																																																																																																																																																													
			else if(data[i]['Flow Access']=="DENY IN" || data[i]['Flow Access']=="deny in")
				flowAccess.options[2].selected = "selected";
			else if(data[i]['Flow Access']=="DENY OUT" || data[i]['Flow Access']=="deny out")
				flowAccess.options[3].selected = "selected";
			
			if(data[i]['Protocol']=="TCP" || data[i]['Protocol']=="6")
				protocol.options[1].selected = "selected";
			else if(data[i]['Protocol']=="UDP" || data[i]['Protocol']=="17")
				protocol.options[2].selected = "selected";
			   i++;
				}
				
		},
		error : function(xhr, status, errmsg) {
			alert(errmsg);
			console.log(status);
		}
	});
    fillRatingGroup("chargingKey", serviceTypeRatingGroupMapJson[serviceId], staffBelongingRatingGroupJson, selectedChargingKey);
}

function clearSDFTable(){
	i=0;
    for(var i = document.getElementById("serviceDataFlowTable").rows.length-1; i > 0 ;i--){
    	document.getElementById("serviceDataFlowTable").deleteRow(i);
    }
}

function setMonitoringKey(){
	var pccRuleName = $("#pccruleName").val();
	if(isMonitoringKeySame){
		$("#monitoringKey").val(pccRuleName);	
	}
}

var isMonitoringKeySame = true;
function isMonitoringKeyChanged() {
	var monitoringKey = $("#monitoringKey").val();
	var pccRuleName = $("#pccruleName").val();
	if (monitoringKey != pccRuleName) {
		isMonitoringKeySame = false;
	}
}
</script>