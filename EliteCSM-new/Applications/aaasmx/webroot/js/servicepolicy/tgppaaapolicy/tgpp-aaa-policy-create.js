/**
 * This js file contains the functions for function used for only create tgpp service policy
 * Created Date : 22 Jan 2016 
 * Author       : Nayana Rathod
 * 
 */
function addDefaultHandlerToFlow(handlerName,handlerTable){
	
	var htmlcontent = '';
	htmlcontent=getDefaultHandlerData(handlerName,handlerTable);
	
	var newRowId =$('#' +handlerTable).find('.commandcode-flow-table > tbody > tr').length;
	newRowId++;
	$('#' + handlerTable).find('.commandcode-flow-table').append('<tr id='+newRowId+'><td class="handler-css">'+htmlcontent+'</td></tr>');
}

function getDefaultHandlerData(handlerName,handlerTable){
	var widgetPath=getContextPath()+"/jsp/servicepolicy/diameter/tgppaaapolicy/"+handlerPagePath[handlerName];
	
	var formNextId=handlerName +"_"+createUUID();
	var isAdditional = false;
	var widgetNextId=formNextId;
	var response='';
	var orderNumber=$('#'+handlerTable).find('.commandcode-flow-table').find('tbody').find('tr').length;
	orderNumber = orderNumber +1;
    $.ajax({url:widgetPath,
          type:'GET',
          cache:false,
          data:'widgetId='+widgetNextId+'&isAdditional='+isAdditional+'&orderNumber='+orderNumber+'&handlerTable='+handlerTable+'&isViewPage='+isViewPage,
          async:false,
          success: function(transport){
             response=transport;
         }
    });
    if(response!=null){
    	return response;
    }else{
    	return null;
    }
}

/* Default CommandCode Flow & Handler */
function addCommandCodeFlow(display_label,commandCode,interfaceId){
	
	var id = $(".nav-tabs").children().length;
	var new_display_label = display_label.replace(/,/g, '_');
	new_display_label = new_display_label.replace(/\./g, '_');
	new_display_label = new_display_label.replace(/-/g, '_');
	
	var uuId = createUUID();
	var uniqueId = new_display_label + "_" + uuId;
	
	$('.nav-tabs li:last-child').before(' <li><a href="#'+new_display_label+'" role="tab" data-toggle="tab"><label id="tab_'+ uniqueId +'">' + display_label + '</label><input id="txt_'+ uniqueId +'" onkeyup="expand(this);" class="ccf-tab" />&nbsp; &nbsp; &nbsp; &nbsp;</a> <div id='+uniqueId+' > <i class="fa fa-pencil"></i> </div> &nbsp; &nbsp; &nbsp; <span> <i class="fa fa-trash-o"></i> </span> &nbsp;</li>');
	$('.tab-content').append('<div class="tab-pane fade" id="'+new_display_label+'"><table cellspacing="0" cellpadding="0" border="0" width="100%" class="service_handler_table" id="servicehandlertable_'+ uniqueId +'">'+ $('#service_handler_table').html() +'</table></div>');

	$('#txt_'+uniqueId).val(display_label);
	$('#txt_'+uniqueId).hide();
	$('#txt_'+uniqueId).attr("size",display_label.length);
	
	$('#servicehandlertable_'+uniqueId).find('.uniqueUUID').val(uuId);
	$('#servicehandlertable_'+uniqueId).find('.commandCode').attr('id','commandCode_'+uuId);
	$('#servicehandlertable_'+uniqueId).find('.interface').attr('id','interface_'+uuId);
	$('#servicehandlertable_'+uniqueId).find('.displayName').attr('id','displayName_'+uuId);
	
	$('#servicehandlertable_'+uniqueId).find('.commandcode-flow-table').attr('id','commandcode-flow-table_'+uuId);
	
	$('#servicehandlertable_'+uniqueId).find('.commandCode').val(commandCode);
	$('#servicehandlertable_'+uniqueId).find('.interface').val(interfaceId);
	$('#servicehandlertable_'+uniqueId).find('.displayName').val(display_label);
	
	return 'servicehandlertable_'+uniqueId;
}

function addDefaultFlowAndHandlers(){
	/* Adding Default Handler */
	var derServiceHandlerTableId = addCommandCodeFlow('DER','268','1');
	addDefaultHandlerToFlow('AuthenticationHandler',derServiceHandlerTableId);
	addDefaultHandlerToFlow('AuthorizationHandler',derServiceHandlerTableId);
	addDefaultHandlerToFlow('ProfileLookupDriver',derServiceHandlerTableId); 
	
	var acrServiceHandlerTableId = addCommandCodeFlow('ACR','271','1');	
	addDefaultHandlerToFlow('CDRHandler',acrServiceHandlerTableId);
	
	var aarServiceHandlerTableId = addCommandCodeFlow('AAR','265','1');	
	addDefaultHandlerToFlow('AuthenticationHandler',aarServiceHandlerTableId); 
    addDefaultHandlerToFlow('AuthorizationHandler',aarServiceHandlerTableId);
	addDefaultHandlerToFlow('ProfileLookupDriver',aarServiceHandlerTableId); 
}