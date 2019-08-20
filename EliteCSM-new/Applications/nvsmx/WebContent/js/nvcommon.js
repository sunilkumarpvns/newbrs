/* 
 verifyUniqueness() require following arguments
 'fieldId' -- id field to validate
 'actionName' -- name of action to call for validation
 mode - created/update
 entityId- id of the entity to be validate
 className - class name of entity
 pkgId - pkg id or parent id under which current it uniqueness required
  it makes an AJAX call to the
 passed action's validation method and it return the appropriate result message.
 Invalid fielId message will be displayed in red color and valid fielId message will be displayed in green color. 
 Here 'actionName' value will be like '/validate/pkg/Pkg/validation'
 in this action value '/validate' is the namespace value in struts.xml file and remaining value will be mapped dynamically to the respective 
 action's method through wildcard used in the struts.xml file.  
name property should be present in the Controller class for performing validation on name
 */
	function verifyUniqueness(fieldId,mode,entityId,className,pkgId,propertyName){
		  var actionName="/validate/policydesigner/util/NameValidation/doValidation";
		  var pkgNameVal = $("#"+fieldId).val();
		  clearErrorMessages(null);
		  $.ajax({
			    type: "POST",
			    url: contextPath+actionName,
			    data : {
						"id" : entityId,
						"mode" : mode,
						"name" : pkgNameVal,
						"className" : className,
						"parentId" : pkgId,
						"property" : propertyName
		                },		    
			    success: function(data){			    			    			    			    			    	
			    	if(data.messageCode == "1"){
			    		setSuccess(fieldId);
			    	}else{
			    		setError(fieldId,data.message);
			    	}
			    	callback(data);
			    },
			    error: function(data){
			    	console.log(data);
			    }
		 });
	}
function callback(data){
	console.log(data);
}


function verifyUniquenessOnSubmit(fieldId,mode,entityId,className,pkgId,propertyName){
	  var actionName="/validate/policydesigner/util/NameValidation/doValidation";
	  var pkgNameVal = $("#"+fieldId).val();
	  clearErrorMessagesById(fieldId);
	  var response=false;
	  $.ajax({
		    type: "POST",
		    url: contextPath+actionName,
		    async:false,
		    data : {
					"id" : entityId,
					"mode" : mode,
					"name" : pkgNameVal,
					"className" : className,
					"parentId" : pkgId,
					"property" : propertyName
	                },		    
		    success: function(data){			    			    			    			    			    	
		    	if(data.messageCode == "1"){
		    		setSuccess(fieldId);
		    		response= true;
		    	}else{
		    		setError(fieldId,data.message);
		    		response=false;
		    	}
		    },
		    error: function(data){
		    	console.log(data);
		    }
	 });
	  return response;
}

function verifyValidName(fieldId,fieldName){
	var name = $("#"+fieldId).val();
	clearErrorMessagesById(fieldId);
	if(isNullOrEmpty(name)){
		setError(fieldId, fieldName + " Required");
		return false;
	}
	if(name.length < 2 || name.length > 100){
		setError(fieldId,"Length of " + fieldName + " must be between 2 and 100");
		return false;
	}
	var regex = '^[a-zA-Z0-9]+.*[a-zA-Z0-9]+$';
	var regexp = new RegExp(regex);
	if(regexp.test(name) == false){
		setError(fieldId,"Invalid " + fieldName + " (" + fieldName + " Should start and End with Alpha-numeric character)");
		return false;
	}
	return true;
}

/**
 * select all child checkboxes
 * @param parentElementName
 * @param childElementName
 */
function selectChildren(parentElementName,childElementName) {
	var isCheckedParent = $("[name='"+parentElementName+"']").prop('checked');
	if (isCheckedParent) {
		$("[name='"+childElementName+"']").prop("checked", true);
	} else {
		$("[name='"+childElementName+"']").removeAttr("checked");
	}
}


function clearErrorMessagesById(elementid){
	var curElement = $("#".concat(elementid)); 
	var parentElement = curElement.parent();
	$(parentElement).closest(".has-error").removeClass("has-error");
	$(parentElement).closest(".has-success").removeClass("has-success");
	$(parentElement).closest(".has-feedback").removeClass("has-feedback");
	$(parentElement).find(".alert").remove();
	$(parentElement).find(".nvsmx-alert").remove();
	$(parentElement).find(".glyphicon-remove").remove();
	$(parentElement).find(".alert-danger").remove();
	$(parentElement).find(".removeOnReset").remove();
	$(".nv-input-error").removeClass("nv-input-error");
}

/**
 * select Parent if all children are selected
 * @param childElementName
 * @param parentElementName
 */
function selectParent(childElementName,parentElementName){
	if($("[name='"+childElementName+"']").length == $("[name='"+childElementName+"']:checked").length){
		$("[name='"+parentElementName+"']").prop("checked", true);
	}else{
		$("[name='"+parentElementName+"']").removeAttr("checked");
	}
}

/**
 * disable delete button if all checkboxes are unchecked
 * @param currentElementName
 * @param btnId
 */
function disableDeleteButton(currentElementName,btnId) {
	if(($("[name='"+currentElementName+"']:checked").length)>0){
		$("#"+btnId).removeAttr("disabled");
	}else{
		$("#"+btnId).attr("disabled","disabled");
	}
}

/*
 * following functions can be used for validations related checks.
 * */
function isNullOrEmpty(fieldVal){
	
	if(typeof fieldVal == 'undefined' || fieldVal=='undefined' || fieldVal==null || $.trim(fieldVal).length==0 || fieldVal=="null"){
		return true;
	}else{
		return false;
	}
}
/**
 * will return true if fieldVal is negative or is not a number
 * @param fieldVal
 * @returns {Boolean}
 */
function isNegativeNumber(fieldVal){
	if(( fieldVal !=null && fieldVal < 0 ) || isNaN(fieldVal)){
		return true;
	}else{
		return false;
	}
}
/*
 * will check for positive integer number
 * 
 * */
function isNaturalNumber(val){
	//Excluding zero and only posive integer i.e {1,2,3...}
	if(val==null){
		return false;
	}
	var nre= /^[1-9]+[0-9]*$/;
	var regexp = new RegExp(nre);
	if(regexp.test(val)){
		return true;
	}
	return false;
}

function isNumberGreaterThanZero(val){
	//integer number should be greater than zero
	if(val==null){
		return false;
	}
	if(val > 0 && val.indexOf(".") == -1){
		return true;
	}else{
		return false;
	}
}

function isNaturalInteger(event){
	//including zero . i.e {0,1,2,3...}

	 if (event.ctrlKey==true && (event.which == '99' || event.which == '118' || event.which == '97')) {
		 return true;
     }
	 if( event.which == '0' || event.which == '8' || event.which == '9' ){
    	 return true;
     }
	var regExpr =/^\d+$/;
	if ( !String.fromCharCode( event.which ).match( regExpr )) {
		event.preventDefault();
	    return false; 
	   }
}

/* confirmation dialog code begin */

var options = {
		'animation'	:	true,
		'placement'	:	'left',
		'title'		:	'Are you sure ?',
		'btnOkLabel' :  'Yes',
		'btnCancelLabel' :  'No',			
		'btnCancelClass' :  'btn btn-sm btn-default',
		'btnOkClass' 	 :'btn btn-sm btn-primary',
		'singleton':true,
		'onConfirm'	:	function(){
			return true;
		},
		'onCancel'	:	function(){
			$('[data-toggle="confirmation-singleton"]').confirmation('hide');
		}
};
var deleteConfirm = function(){
	$('[data-toggle="confirmation-singleton"]').confirmation(options);
};
	/* confirmation dialog code end */


/* to test for positive decimal numbers
* 
*    values ---- result 
*      null ----    false
*       "" ----    false
*		" " ----    false 
*	100.00  ----    true 
*    100.123456 ---- true 
*	-100.235 ----   false 
*	-100     ----- false 
*	100.     -----  true 
*	100.00   -----  true 
*	100.123  ----- true 
* 
* */
function isPositiveDecimalNumber(id,val){
	clearErrorMessagesById(id);

	if(val=="" || $.trim(val).length==0){
		return true;	
	}
	if(val > 9999999.99){
		setError(id,"Max Length is reached for price");
		return false;
	}
	var pattern=/^[0-9]+(\.[0-9]{0,})?$/;
	var regex=new RegExp(pattern);
	if(regex.test(val) == false){
		setError(id,"Positive Numeric is allowed only");
		return false;
	}else{
		return true;
	}
}

function isPositiveDecimalNumberInGivenRange(id, val, lowerLimit, upperLimit){
    clearErrorMessagesById(id);

    if(val > upperLimit || val < lowerLimit){
        setError(id,"Value must be between 0 and 100");
        return false;
    }

    if (isNullOrEmpty(val) == false) {
        var regExp = new RegExp("^(([0-9]{0,3}(\\.\\d{0,2})?))$");

        if(regExp.test(val) == false) {
            setError(id,"It alllows maximum 3 numbers and upto 2 decimals");
            return false;

        }
    }
	return true;
}

/* to test for positive decimal numbers
* 
*    values ---- result 
*      null ----    false
*       "" ----    false
*		" " ----    false 
*	100.00  ----    true 
*    100.123456 ---- true 
*	-100.235 ----   false 
*	-100     ----- false 
*	100.     -----  true 
*	100.00   -----  true 
*	100.123  ----- true 
* 
* */

function isValidRate(id,val){
	clearErrorMessagesById(id);

	if(val=="" || $.trim(val).length==0){
		return true;	
	}
	if(val > 99999999.99){
		setError(id,"Max Length is reached for rate");
		return false;
	}
	var pattern=/^[0-9]+(\.[0-9]{0,6})?$/;
	var regex=new RegExp(pattern);
	
	if(regex.test(val) == false){
		setError(id,"Positive Rate with max 6 precision is allowed only");
		return false;
	}else{
		return true;
	}
}

function isValidDecimalRate(obj,val){
	clearErrorMessagesById(id);

	if(val=="" || $.trim(val).length==0){
		return true;	
	}
	if(val > 9999999.99){
		setError(id,"Max Length is reached for price");
		return false;
	}
	var pattern=/^[0-9]+(\.[0-9]{0,6})?$/;
	var regex=new RegExp(pattern);
	if(regex.test(val) == false){
		setError(id,"Positive Numeric is allowed only");
		return false;
	}else{
		return true;
	}
}



function addWarning(elementCSS,message){
	$(elementCSS).children("p").remove();
    $(elementCSS).addClass("alert alert-info popup warning-popup");
	$(elementCSS).children("button").after($("<p><span class='glyphicon glyphicon-ban-circle'></span>"+ message+"</p>"));
    $(elementCSS).children("button").attr('onclick',"$(this).parent().closest('div').hide();$(this).parent().closest('div').removeClass('alert alert-success')");
	$(elementCSS).css("display","block");
	warningPopupFade();
	return false;
}

function addSuccess(elementCSS,message){
    $(elementCSS).children("p").remove();
    $(elementCSS).removeClass("alert-danger");
    $(elementCSS).addClass("alert alert-success");
    $(elementCSS).children("button").after($("<p> <strong>Success!</strong>"+ message+"</p>"));
    $(elementCSS).children("button").removeAttr("data-dismiss");
    $(elementCSS).children("button").attr('onclick',"$(this).parent().closest('div').hide();$(this).parent().closest('div').removeClass('alert alert-success')");
    $(elementCSS).css("display","block");
    return false;
}
function addDanger(elementCSS,message){
    $(elementCSS).children("p").remove();
    $(elementCSS).removeClass("alert-success");
    $(elementCSS).addClass("alert alert-danger");
    $(elementCSS).children("button").after($("<p>"+ message+"</p>"));
    $(elementCSS).children("button").removeAttr("data-dismiss");
    $(elementCSS).children("button").attr('onclick',"$(this).parent().closest('div').hide();$(this).parent().closest('div').removeClass('alert alert-danger')");
    $(elementCSS).css("display","block");
    return false;
}
function addInfo(elementCSS,message){
    $(elementCSS).children("p").remove();
    $(elementCSS).addClass("alert alert-info")
    $(elementCSS).children("button").after($("<p>"+ message+"</p>"));
    $(elementCSS).children("button").removeAttr("data-dismiss");
    $(elementCSS).children("button").attr('onclick',"$(this).parent().closest('div').hide();$(this).parent().closest('div').removeClass('alert alert-info')");
    $(elementCSS).css("display","block");
    return false;
}

function deleteConfirmMsg(obj,message,placement){
       var optionsForDelete = {
		'animation'	:	true,
		'title'		:	message,
        'btnOkLabel' :  'Yes',
		'btnCancelLabel' :  'No',
		'btnCancelClass' :  'btn btn-sm btn-default',
		'btnOkClass' :  'btn btn-sm btn-primary',
		'trigger':'click',
		'singleton':true,
		'popout':true,
		'onConfirm'	:	function(){
			return true;
		},

		'onCancel'	:	function(){
			$(obj).confirmation('hide');
		}
	};
	if(isNullOrEmpty(placement) == false){
		optionsForDelete["placement"] = placement;
	}
	$(obj).confirmation(optionsForDelete);
	$(obj).confirmation('show');
	$(obj).on('hide.bs.confirmation', function(e) {
		$(e.currentTarget).unbind(); // or $(this)
	});

};


function warningPopupFade(){
	if($(".warning-popup").css("display") != "none"){
		$(".warning-popup").fadeTo(3000, 500).slideUp(500, function(){
			$(".warning-popup").hide();
		});	
	}
}

function getEncodeURI(url,component){
	location.href=url+encodeURIComponent(component);
}

function addEncodedURI(url, component, domElement,callbackFunction,attribute){
	    domElement.setAttribute(attribute,url+encodeURIComponent(component));
	    if(isNullOrEmpty(callbackFunction)==false) {
			if(typeof(callbackFunction) =='function' ){
				callbackFunction();
			}
		}
}

function getSelectedValuesForDelete(){
	var selectedData = false;
	var selectedDatas = document.getElementsByName("ids");
	var selectedModes = document.getElementsByName("modes");
	for (var index=0; index < selectedDatas.length; index++){
		if(selectedDatas[index].name == 'ids'){
			if(selectedDatas[index].checked == true){
				selectedData = true;
				if(selectedModes[index].value == 'LIVE2'){
					selectedData = false;
					return addWarning(".popup","LIVE2 Packages can not be deleted");
				}
			}
		}


	}
	if(selectedData == false){
		return addWarning(".popup","At least select one package for delete");
	}
	return selectedData;
}


function enableSelect2(){
    $(".select2").select2();
}