//var map=new Object();



$.Class.extend("HashMap",
{
	
		  	init: function()
		  	{
		        this.hashMap={};  		
		 	},
		 	add: function(key,value)
		  	{
		 		this.hashMap[key]=value;
		 	},
		 	get :function(key)
		 	{
			   return this.hashMap[key]; 
		    },
		 	remove:function(key)
		 	{
		 		delete this.hashMap[key];
		 	}
		}	 	
	 );


function fetchWidgetConfiguration(widgetId){
	$.ajax({url:'FetchWidgetConfiguration',
        type:'GET',
        data:{widgetId:widgetId},
        async:false,
        success: function(widgetConfigList){
       	 $.each(widgetConfigList, function(index, widgetConfigData) {
       		 if(widgetConfigData.parameterKey == 'ELITEAAAINSTANCES'){
       			var dataarray=widgetConfigData.parameterValue.split(",");
       			$("#"+widgetConfigData.widgetId+widgetConfigData.parameterKey).val(dataarray);
       		 }else if(widgetConfigData.parameterKey == 'CLIENTS'){
       			 if(typeof widgetConfigData.parameterValue === 'undefined'){
       				document.getElementById(widgetConfigData.widgetId+widgetConfigData.parameterKey).value='';
       			 }else{
       				document.getElementById(widgetConfigData.widgetId+widgetConfigData.parameterKey).value=widgetConfigData.parameterValue;
       			 }
       	     }else{
       			document.getElementById(widgetConfigData.widgetId+widgetConfigData.parameterKey).value=widgetConfigData.parameterValue;
       		 }
       	 });
       } 
	}); 
}
function saveWidgetConfiguration(widgetId,confJsp){
	var $form = $("#frm"+widgetId);
	var data=$form;
	console.log("Datas" +  $(data).serialize());
	 $.ajax({
       type: 'POST',
       url: 'SaveWidgetConfiguration',
       data: $(data).serialize()+"&widgetid="+widgetId,
       success: function (data) {
    	   $.ajax({
               url: confJsp,
               success: function (result) {
                   $('#widgetcontent'+widgetId).html(result);
               }
           });
       }
   }); 
	 
   $('#'+widgetId).find('.widgettitle').find('span').html($('#'+widgetId+'NAME').val());
}



function getWidgetConfiguration(widgetId){
	var hashMap1=new HashMap();
	$.ajax({
		url:'FetchWidgetConfiguration',
        type:'GET',
        data:{widgetId:widgetId},
        async:false,
        success: function(widgetConfigList){
        	 $.each(widgetConfigList, function(index, widgetConfigData) {
				 hashMap1.add(widgetConfigData.parameterKey,widgetConfigData.parameterValue);
	         });
        }
	});
	 return hashMap1;
}


function checkWidgetConfigurationSaved(widgetId){
	var response='';
	$.ajax({
		url:'CheckWidgetConfiguration',
        type:'GET',
        data:{widgetId:widgetId},
        async:false,
        success: function(result){
        	response=result;
        }
	});
	 return response;
}


