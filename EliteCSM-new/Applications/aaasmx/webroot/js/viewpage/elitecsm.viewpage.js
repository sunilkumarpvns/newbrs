
/**
 * This js file contains the functions for viewing module specific details .
 * Created Date : 24th March 2015 
 * Author       : Nayana Rathod
 */

/**
 * @param caller
 * @param moduleName - Header of Dialog
 * @param instance_Name - Instance Name of perticular module
 * @param instance_Id - Instance Id of perticular module
 * @param moduleName - module name fetches from constant
 */
function openViewDetails(caller, instance_Id, instance_Name,  module_Name){

	var isNestedConfigured = false;
	var moduleDetailsJson = {};
	var viewHelpPopup='';
	
	/*
	 * Check for Module DRIVER is Configured
	 */
	
	if( module_Name == 'DRIVERS'){
		$.ajax({url:'viewModuleDetails.do?method=getDriverTypeByInstanceId',
	         type:'POST',
	         data:{instance_Id : instance_Id},
	         async:false,
	         success: function( module_name ){
	        	 module_Name = module_name.trim();
	        	 console.log('fetching module name using type id : '+module_Name);
	         }
	    });
	}
	
	/**
	* Purpose : It will used for fetching details as per given module name and instance Id 
	* @param instance_Id - Unique Id of any Instance like (Session Manager,Diameter Peer etc)
	* @param module_Name - module name of that instance for that use "EliteViewCommonConstant" constant file 
	*/
	$.ajax({url:'viewModuleDetails.do?method=getModuleDetails',
         type:'POST',
         data:{instance_Id : instance_Id, module_Name : module_Name },
         async:false,
         success: function( moduleDetails ){
        	 moduleDetailsJson = moduleDetails;
         }
    });
	
	/* Nested Object found & collect required details from nested object*/
	if(moduleDetailsJson != null){
		
		if( moduleDetailsJson.nestedObjectDetailsList ) {
			 isNestedConfigured = true;
        }
		 
		viewHelpPopup +=("<table width='100%' cellspacing='0' cellpadding='0' border='0' ");

		viewHelpPopup += addHeaderTemplate();
		 
		if( isNestedConfigured ){
			 
			 $.each( $.parseJSON( moduleDetailsJson.jsonObject ) , function(fieldKey, fieldValue){
				 if(isKeyFromNetsedObjects( moduleDetailsJson.nestedObjectDetailsList, fieldKey)){
					 var jsonObject = getJsonObject(moduleDetailsJson.nestedObjectDetailsList, fieldKey);
					 viewHelpPopup += convertToChildEntityFormat(fieldKey, fieldValue,jsonObject);
				 }else{
					 viewHelpPopup += convertToTemplate(fieldKey, fieldValue);
				 }
			 });
		   		 
		}else{
			$.each($.parseJSON( moduleDetailsJson.jsonObject), function(fieldName, fieldValue){
				viewHelpPopup += convertToTemplate(fieldName, fieldValue);
			});
	   	 }
		 
		 if( moduleDetailsJson.viewAdvancedDetailsLink ){
			 viewHelpPopup += addFooterTemplate( moduleDetailsJson.viewAdvancedDetailsLink );
		 }
			
		 viewHelpPopup +=("</table>");
		
		 if(typeof $('#'+ instance_Name +'_div') !== undefined &&  $('#'+ instance_Name +'_div').length > 0){
			$('#'+ instance_Name +'_div').remove();
		}
		
		$('<div/>', {
		    id: instance_Name + "_div"
		}).appendTo(document.body);

		
		$('#'+ instance_Name +'_div').html(viewHelpPopup);
		$('#'+ instance_Name +'_div').dialog({
			modal: false,
			autoOpen: false,
			maxHeight : 550,
			width : 550,
			title : instance_Name,
			close : function(){
				$('#'+ instance_Name +'_div').remove();
			}
		}).css("font-size", "12px");
		$('#'+ instance_Name +'_div').css({height:"300px", overflow:"auto"});
		$('#'+ instance_Name +'_div').dialog("open");
	} 
}
function getJsonObject(nestedObjectList, findingKey){
	var jsonObject = {};
	if( nestedObjectList.length > 0 ){
		 $.each(nestedObjectList, function(fieldName, fieldValue){
			if(fieldValue.key === findingKey){
				jsonObject.key=fieldValue.key;
				jsonObject.moduleName=fieldValue.moduleName;
				jsonObject.moduleInstanceName=fieldValue.moduleInstanceName;
				jsonObject.moduleId=fieldValue.moduleId;
			}
		 });
	}
	return jsonObject;
}
/* Check for key will match or not in nested Object list */
function isKeyFromNetsedObjects( nestedObjectList, findingKey ){
	var isNestedFound = false;
	if( nestedObjectList.length > 0 ){
		 $.each(nestedObjectList, function(fieldName, fieldValue){
			if(fieldValue.key === findingKey){
				isNestedFound = true;
			}
		 });
	}
	return isNestedFound;
}

/* Convert Key and Value data to generic template */
function convertToTemplate( fieldName, fieldValue ){
	if(fieldValue == null){
		fieldValue = "";
	}
	
	return '<tr><td class="labeltext tblfirstcol">' + fieldName + '</td><td class="labeltext tblrows">' + fieldValue + '</td></tr>';
}

/* generic template for header */
function addHeaderTemplate(){
	return '<tr><td class="tblheader eliteview-header" width="50%"> Field Name </td><td class="tblheader eliteview-header" width="50%"> Field Value </td></tr>';
}

/* generic template for footer */
function addFooterTemplate(viewAdvancedLink){
	return '<tr><td>&nbsp;</td></tr><tr><td class="labeltext eliteview-footer"><a class="view-advanced-details" href="'+viewAdvancedLink+'">View Advanced Details</a></td><td>&nbsp;</td></tr>';
}

function convertToChildEntityFormat(fieldName, fieldValue, jsonObject){
	return '<tr><td class="labeltext tblfirstcol">' + fieldName + '</td><td class="labeltext tblrows"><span class="view-details-css" onclick="openViewDetails(this,\''+ jsonObject.moduleId +'\',\''+jsonObject.moduleInstanceName+'\',\''+jsonObject.moduleName+'\');">'+ jsonObject.moduleInstanceName + '</span></td></tr>';
}