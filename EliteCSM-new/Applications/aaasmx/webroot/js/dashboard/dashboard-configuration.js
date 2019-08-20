function assignConfigValue(templateId){
	$.ajax({url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=fetchTemplateGlobalConfiguration',
	    type:'GET',
	    async:false,
	    data:{templateId:templateId},
	    success: function(templateGlobalConfDataList){
	   	 $.each(templateGlobalConfDataList, function(index, templateGlobalConfData) {
	   		 if(templateId === templateGlobalConfData.templateId){
	   			 var refreshInterval=templateGlobalConfData.templateId+"refreshInterval";
		   		 var active=templateGlobalConfData.templateId+"active";
		   		 $("#"+refreshInterval).val(templateGlobalConfData.refreshInterval);
		   		 $("#"+active).val(templateGlobalConfData.active);
	   		 }
	   	 });
	   } 
	}); 
}
var fadeIn=true;
function enableTable(){
	if(fadeIn == true){
		$('#widgetDiv').fadeIn(2000);
		fadeIn=false;
	}else{
		$('#widgetDiv').css('display','none');
		fadeIn=true;
	}
}

function customWidget(){
	$('#collapseId').css('display','block');
	$('#customWidgetTable').css('background-color','#85B4E7');
	$('#customWidget').fadeIn(2000);
}

function hideTable(){
	$('#customWidgetTable').css('background-color','#EEEEEE');
	$('#customWidget').css('display','none');
	$('#collapseId').css('display','none');
}

function showWidgetContent(templateId){
	$('#editdiv'+templateId).css('display','none');
	$('#div'+templateId).fadeIn(2000);
	assignConfigValue(templateId);
}

function saveWidgetConf(templateId){
	
	var templateId=templateId;
	var refreshIntervals=$('#'+templateId+"refreshInterval").val();
	var active=$('#'+templateId+"active").val();
	
	$.ajax({url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=saveTemplateGlobalConfiguration',
        type:'POST',
        data:{templateId:templateId,refreshIntervals:refreshIntervals,active:active},
        async:false,
        success: function(data){
      }
 });
	
$('#div'+templateId).fadeOut(10);
}

function cancelCustomWidget(templateId){
	$('#editdiv'+templateId).fadeOut(10);
}

function updateCustomWidget(templateId){
	var title=$("#title"+templateId).val();
	var jspUrl=$("#jspUrl"+templateId).val();
	var configUrl=$("#configJspUrl"+templateId).val();
	var thumbnail=$("#thumbnail"+templateId).val();
	var desription=$("#description"+templateId).val();
	var widgetGroovy=$("#widgetGroovy"+templateId).val();
	
	$.ajax({url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=updateCustomWidgetConf',
        type:'POST',
        data:{templateId:templateId,title:title,jspUrl:jspUrl,configUrl:configUrl,thumbnail:thumbnail,desription:desription,widgetGroovy:widgetGroovy},
        async:false,
        success: function(data){
      }
    
 });
 $('#editdiv'+templateId).fadeOut(10);
}

function getCustomWidgetConf(templateId){
	$('#div'+templateId).css('display','none');
	$('#editdiv'+templateId).fadeIn(2000);
	var templateId=templateId;
	$.ajax({url:'<%=request.getContextPath()%>/dashboardConfiguration.do?method=getCustomWidgetConf',
        type:'POST',
        data:{templateId:templateId},
        async:false,
        success: function(widgetTemplateData){
        	$("#title"+templateId).val(widgetTemplateData.title);
        	$("#jspUrl"+templateId).val(widgetTemplateData.jspUrl);
        	$("#configJspUrl"+templateId).val(widgetTemplateData.configJspUrl);
        	$("#thumbnail"+templateId).val(widgetTemplateData.thumbnail);
        	$("#description"+templateId).val(widgetTemplateData.description);
        	$("#widgetGroovy"+templateId).val(widgetTemplateData.widgetGroovy);
      }
 });
}

function cancelWidgetConf(templateId){
	$('#div'+templateId).fadeOut(10);
}