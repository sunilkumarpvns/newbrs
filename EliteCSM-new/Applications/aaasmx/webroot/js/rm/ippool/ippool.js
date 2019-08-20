/**
 * js file contains functions for ip pools
 */

	function addNewRow(){
		$("#ipAddressRangeTable tr:last").after("<tr>"+$('#ipRangeTemplateTable').html()+"</tr>");
		$("#ipAddressRangeTable tr:last").find("input:first").focus();
	}
	
	
	function validNASIP(ipAddress)
	{
		var ipre = /((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
		return ipre.test(ipAddress);
	}
	
	
	function isValidForm()
	{
		if(document.forms[0].name.value == ''){
			alert('Name is a compulsory field Please enter required data in the field');
			document.forms[0].name.focus();
			return false;
		}else if(isNull(document.forms[0].name.value)){
			alert('Name must be specified');
			document.forms[0].name.focus();
			return false;
		}else if(!isValidName) {
			alert('Enter Valid Name');
			document.forms[0].name.focus();
			return false;
		}else if($.trim($("#nasIPAddress").val()).length > 0 && ! validNASIP($.trim($("#nasIPAddress").val()))){
			alert('NAS IP Address is not valid. Please Enter valid data.');
			$("#nasIPAddress").focus();
			return false;
		}else if(!isValidIPPoolDetails()){
			return false;
		}
		return true;
	}
	
	function validateCreate()
	{
		if(isValidForm()) {
			// Because of  using iframe for upload set target value
			$("#ipPoolForm").attr("target", "");
			$("#ipPoolForm").attr("action", "createIPPool.do");
			$("#ipPoolForm").append("<input type='hidden' name='action' value='create' />");
			document.forms[0].submit();			
		}
	}


	function validateUpdate()
	{
		if(isValidForm()) {
			// Because of Iframe use set target value
			$("#ipPoolForm").attr("target", "");
			$("#ipPoolForm").attr("action", "updateIPPool.do");
			
			document.forms[0].submit();			
		}
	}
	
	function isValidIPPoolDetails(){
		var file = $("#fileUpload").val();
		if($.trim(file).length > 0){
			if(file.indexOf(".") == -1 || file.substr(file.indexOf("."))!=".csv"){
				alert("File must be a valid CSV File.");
				return false;
			}
			return true;
		}else{
			return (isValidRangeIds() && isValidIPRanges());
		}
	}
	
	function isValidRangeIds(){
		var isValid = true;
		var rangeIdArray = new Array();
		$("#ipPoolForm .rangeIdClass").each(function(){
			var rangeId = $.trim($(this).val());
			if(isEmpty(rangeId)){
				alert("Please Specify IP Address Range Id.");
				$(this).focus();
				isValid = false;
				return false;
			}
			rangeIdArray[rangeIdArray.length] = rangeId;
		});
		for(var i=0; i < rangeIdArray.length; i++ ){
			for(var j=i+1; j<rangeIdArray.length; j++){
				if(rangeIdArray[i] == rangeIdArray[j]){
					alert("Range Id Should be unique. Please correct it.");
					$("#ipPoolForm .rangeIdClass")[j].focus();
					return false;
				}
			}
		}
		return isValid;
	}
	
	function isValidIPRanges(){
		var isValid = true;
		var ipRangeArray = new Array();
		$("#ipPoolForm .ipAddressRangeClass").each(function(){
			var ipRange = $.trim($(this).val());
			if(isEmpty(ipRange)){
				alert("Please Specify IP Address Range.");
				$(this).focus();
				isValid = false;
				return false;
			}
			ipRangeArray[ipRangeArray.length] = ipRange;
		});
		
		var ipAddressValues = getNumericRangeValue(ipRangeArray);
		if(ipAddressValues != null) {
			for(var i =0 ; i<ipAddressValues.length; i++){
				for(var j=i+1; j<ipAddressValues.length; j++){
					if((ipAddressValues[j][0] >= ipAddressValues[i][0] && ipAddressValues[j][0] <= ipAddressValues[i][1]) ||
					   (ipAddressValues[j][1] >= ipAddressValues[i][0] && ipAddressValues[j][1] <= ipAddressValues[i][1])) {
							alert("Range '"+ipRangeArray[j]+"' overlaps  range '"+ipRangeArray[i]+"'.Please Correct it.");
							return false;
					}
				}
			}
		}else{
			return false;
		}
		return isValid;
	}
	
	function getNumericRangeValue(ipAddressRangeArray){
		var ipAddressValues = new Array(ipAddressRangeArray.length);
		for (var i = 0; i < ipAddressRangeArray.length; i++) {
			ipAddressValues[i] = new Array();
		}
		for(var index=0 ; index < ipAddressRangeArray.length; index++){
			if(ipAddressRangeArray[index].indexOf("-") != -1){
				var ipAddresseArray =  ipAddressRangeArray[index].split("-");
				if(isValidIP(ipAddresseArray[0]) && ipAddresseArray.length>1){
					if(!isNaN(ipAddresseArray[1])){
						if(!(ipAddresseArray[1] <= 65536 && ipAddresseArray[1] > 0)){
							alert("Invalid IP Address Range. Please provide total no of range between 0 to 65536.\n e.g. 10.106.1.25-25,10.106.1.25-65535");
							return null;
						}
						var ipLongValue = ipToNumber(ipAddresseArray[0]);
						ipAddressValues[index][0] = ipLongValue;
						ipAddressValues[index][1] = ipLongValue + parseFloat(ipAddresseArray[1]);
					}else if(isValidIP(ipAddresseArray[1])){
						/*validate Class B IP Address Range*/
						var dotIndex = 0;
						for(var i=0; i<ipAddresseArray[0].length; i++){
							if(ipAddresseArray[0].charAt(i) != ipAddresseArray[1].charAt(i)){
								alert("Network address of IP Address Range is mismatch.Please enter valid Class B network address\n e.g. IP Address Range 1.1.1.1-1.1.2.50(N/W Address : 1.1)");
								return null;
							}
							if(ipAddresseArray[0].charAt(i) == "."){
								dotIndex++;
							}
							if(dotIndex >= 2)
								break;
						}
						ipAddressValues[index][0] = ipToNumber(ipAddresseArray[0]);
						ipAddressValues[index][1] = ipToNumber(ipAddresseArray[1]);
					}else {
						alert("Invalid IP Address Range:"+ipAddressRangeArray[index]);
						return null;
					}
				}else{
					alert("Invalid IP Address Range:"+ipAddressRangeArray[index]);
					return null;
				}
			}else if(isValidIP(ipAddressRangeArray[index])){
				ipAddressValues[index][0] = ipToNumber(ipAddressRangeArray[index]);
				ipAddressValues[index][1] = ipToNumber(ipAddressRangeArray[index]);
			}else{
				alert("Invalid IP Address Range:"+ipAddressRangeArray[index]);
				return null;
			}
		}
		return ipAddressValues;
	}
	
	
	function ipToNumber(ipAddress){
		 var result = 0;
		 var atoms = ipAddress.split(".");
		 for (var i = 3; i >= 0; i--) {
			result |= (parseFloat($.trim(atoms[3 - i])) << (i * 8));
		 }
		return result & 0xFFFFFFFF;
	}
	
	function isValidIP(ipAddress)
	{
		var ipre=/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
		return ipre.test(ipAddress);
	}
	
	/**
	 * 
	 * @param url
	 * @param title
	 * @returns {Boolean}
	 */

	function checkIPAddList(url,title){
		if(!isValidIPPoolDetails())
			return false;
		
		var status = $("input[name='status']:checked").val();
		var nasIPAddress = null;
		if(status == "1"){
			nasIPAddress = $.trim($("#nasIPAddress").val());
			if(nasIPAddress.length == 0) {
				alert("Please Specify NAS IP Address for check");
				return false;
			}
		}
		
		var file = $("#fileUpload").val();
		if(file.indexOf(".") != -1 && file.substr(file.indexOf("."))==".csv"){
			/* created  IFrame  For UPload file*/ 
			var iframe = $('<iframe name="uploadIPAddressIFrame"  id="uploadIPAddressIFrame" style="display: none" />');
		    $("body").append(iframe);

		    /* Set Form for submit iframe*/
		    var form = $('#ipPoolForm');
		    form.attr("action", url);
		    form.attr("target", "uploadIPAddressIFrame");
		    form.submit();
		   
		    openDialog(title);
		    /* handle response of iframe */
		    $("#uploadIPAddressIFrame").load(function () {
		        response = $("#uploadIPAddressIFrame")[0].contentWindow.document.body.innerHTML;
		        $("#chkIPAddressDiv").html(response);
		        $("iframe#uploadIPAddressIFrame").remove();
		    });     
		}else{
			openDialog(title);
			checkIPAddressByRange(url,1);
		}
			
	}
	
	function checkIPAddressByRange(url,pageNumber){
		var newform = document.createElement('form');
		newform.setAttribute("id","ipPoolFormAjax");
		document.body.appendChild(newform);
		$("#ipPoolForm .ipAddressRangeClass").each(function(){
			$("#ipPoolFormAjax").append("<input type='hidden' name='ipAddressRanges' value='"+$(this).val()+"'>");
		});
		$("#ipPoolForm .rangeIdClass").each(function(){
			$("#ipPoolFormAjax").append("<input type='hidden' name='rangeId' value='"+$(this).val()+"'>");
		});
		
		if($("#ipPoolId").length != 0) {
			$("#ipPoolFormAjax").append("<input type='hidden' name='ipPoolId' value='"+$("#ipPoolId").val()+"'>");
		}
		var status = $("input[name='status']:checked").val();
		if(status == "1"){
			$("#ipPoolFormAjax").append("<input type='hidden' name='nasIPAddress' value='"+$.trim($("#nasIPAddress").val())+"'>");
		}
		$("#ipPoolFormAjax").append("<input type='hidden' name='pageNumber' value='"+pageNumber+"'>");
		$.ajax({
			url : url,
			type: "POST",
			async : false,
			data: $("#ipPoolFormAjax").serialize(),
			success : function (response) {
				$("#ipPoolFormAjax").remove();
				 $("#chkIPAddressDiv").html(response);
			}
		}); 
	}
	
	function openDialog(title){
		$('<div/>', {
		    id: "chkIPAddressDiv"
		}).appendTo(document.body);
		
		$("#chkIPAddressDiv").html("Loading...");
		
		$("#chkIPAddressDiv").dialog({
			modal: true,
			autoOpen: false,
			height: 400,
			width: 400,
			title : title,
			buttons : {
				Cancel: function() {
					$(this).dialog('close');
				}
			}, 
			close : function(){
				$("#chkIPAddressDiv").remove();
			}
		});
		
		$("#chkIPAddressDiv").dialog("open");
	}
	
	
	function viewIPPools(ipAddress){
		var ipPoolId = null;
		if($("#ipPoolId").length != 0) {
			ipPoolId = $("#ipPoolId").val(); 
		}
		var status = $("input[name='status']:checked").val();
		var nasIPAddress = null;
		if(status == "1"){
			nasIPAddress = $.trim($("#nasIPAddress").val());
			if(nasIPAddress.length == 0) {
				alert("Please Specify NAS IP Address for check");
				return false;
			}
		}
		var data = {
				ipAddress : ipAddress,
				ipPoolId  : ipPoolId,
				nasIPAddress : nasIPAddress
			};
		$.ajax({
			url : "viewIPPoolCheck.do",
			type: "POST",
			data: data,
			success : function (data) {
				setViewIPResponse(data);
			}
		}); 
	}

	function setViewIPResponse(data){
		var checkIpPoolRes = $("#chkIPAddressDiv").html();
		$("#chkIPAddressDiv").dialog("option","buttons",{
			Back : function () {
				setBackFunction(checkIpPoolRes);
			},
			Cancel: function() {
				$(this).dialog('close');
			}
		});
		$("#chkIPAddressDiv").html(data);
		$("#chkIPAddressDiv").dialog("option","title","IPPool List");
		
	}

	function setBackFunction(prevData){
		$("#chkIPAddressDiv").dialog("option","buttons",{
			Cancel: function() {
				$(this).dialog('close');
			}
		});
		$("#chkIPAddressDiv").html(prevData);
	}
	