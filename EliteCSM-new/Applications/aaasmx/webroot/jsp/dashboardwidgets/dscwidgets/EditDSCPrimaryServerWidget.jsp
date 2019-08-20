<%@ page errorPage="/jsp/dashboardwidgets/WidgetsErrorHandler.jsp" %>
<%@page import="com.elitecore.elitesm.datamanager.core.util.PageList"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData" %>
<html>
<head>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<script type="text/javascript">
var widgetId;
var configList = [];
var widgetConfigurations =[];
//get widget id
widgetId=$("#dscPrimaryServer").parent().parent().find('.widgetcontent').parent().attr("id");
var isElementFounds=false;

$(document).ready(function(){
	
	$.ajax({url:'<%=request.getContextPath()%>/FetchWidgetConfiguration',
        type:'GET',
        data:{widgetId:widgetId},
        async:false,
        success: function(transport){
       	 $.each(transport, function(key, widgetConfigData) {
       		 if(widgetConfigData.parameterKey == "NAME"){
				$('#dscWidgetName').val(widgetConfigData.parameterValue);
			}else if(widgetConfigData.parameterKey == "REFRESH INTERVAL"){
				$('#dscRefreshInterval').val(widgetConfigData.parameterValue);
			}else if(widgetConfigData.parameterKey == "DAYS PREVIOUSLY"){
				$('#dscDaysPreviously').val(widgetConfigData.parameterValue);
			}else if(widgetConfigData.parameterKey == "SERVER INSTANCES"){
				$('#dscServerInstance').val(widgetConfigData.parameterValue);
			}else if(widgetConfigData.parameterKey == "TPS USAGE"){
				$('#dscTpsUsage').val(widgetConfigData.parameterValue);
			}else if(widgetConfigData.parameterKey == "MEMORY USAGE"){
				$('#dscMemoryUsage').val(widgetConfigData.parameterValue);
			}else if(widgetConfigData.parameterKey == "THREAD USAGE"){
				$('#dscThreadUsage').val(widgetConfigData.parameterValue);
			}else if(widgetConfigData.parameterKey == "CPU USAGE"){
				$('#dscCpuUsage').val(widgetConfigData.parameterValue);
			}
       		 
             configList.push({'parameterId': widgetConfigData.parameterId,
            		   'parameterKey' : widgetConfigData.parameterKey,
            		   'parameterValue':widgetConfigData.parameterValue,
            		   'widgetId':widgetConfigData.widgetId});
             
             isElementFounds=true;
       	 });
      }
 });
 });
 $("#dscSubmitBtn").click(function () {
	
	 var name = $("#dscWidgetName").val();
	 var refreshInterval =$("#dscRefreshInterval").val();
	 var daysPreviously=$('#dscDaysPreviously').val();
	 var serverinstances =$("#dscServerInstance").val();
	 var tpsUsage =$("#dscTpsUsage").val();
	 var memoryUsage =$("#dscMemoryUsage").val();
	 var cpuUsage =$("#dscCpuUsage").val();
	 var threadUsage =$("#dscThreadUsage").val();

	 if(isElementFounds == false){
		 widgetConfigurations.push({'KEY':'NAME','VALUE':name});
		 widgetConfigurations.push({'KEY':'REFRESH INTERVAL','VALUE':refreshInterval});
		 widgetConfigurations.push({'KEY':'DAYS PREVIOUSLY','VALUE':daysPreviously});
		 widgetConfigurations.push({'KEY':'SERVER INSTANCES','VALUE':serverinstances});
		 widgetConfigurations.push({'KEY':'TPS USAGE','VALUE':tpsUsage});
		 widgetConfigurations.push({'KEY':'MEMORY USAGE','VALUE':memoryUsage});
		 widgetConfigurations.push({'KEY':'THREAD USAGE','VALUE':threadUsage});
		 widgetConfigurations.push({'KEY':'CPU USAGE','VALUE':cpuUsage});
		 
		 var jsonString = JSON.stringify(widgetConfigurations);
		 
		 $.ajax({url:'<%=request.getContextPath()%>/AddWidgetConfiguration',
	         type:'POST',
	         data:{widgetId:widgetId,jsonData:jsonString},
	         async:false,
	         success: function(transport){
	        	 $("#"+widgetId).find(".widgetcontent").load("jsp/dashboardwidgets/dscwidgets/DSCPrimaryServerWidget.jsp");
	       }
	  	 });
		
   
	 }else{
		 for(var i=0;i<configList.length;i++){
			 if(configList[i].parameterKey == "NAME"){
				 configList[i].parameterValue=name;
			 }else if(configList[i].parameterKey == "REFRESH INTERVAL"){
				 configList[i].parameterValue=refreshInterval;
			 }else if(configList[i].parameterKey == "DAYS PREVIOUSLY"){
				 configList[i].parameterValue=daysPreviously;
			 }else if(configList[i].parameterKey == "SERVER INSTANCES"){
				 configList[i].parameterValue=serverinstances;
			 }else if(configList[i].parameterKey == "TPS USAGE"){
				 configList[i].parameterValue=tpsUsage;
			 }else if(configList[i].parameterKey == "MEMORY USAGE"){
				 configList[i].parameterValue=memoryUsage;
			 }else if(configList[i].parameterKey == "THREAD USAGE"){
				 configList[i].parameterValue=threadUsage;
			 }else if(configList[i].parameterKey == "CPU USAGE"){
				 configList[i].parameterValue=cpuUsage;
			 }	
		 }
		 	 
		 // convert List to JSON string
		 var jsonString = JSON.stringify(configList);
	     
	     $.ajax({url:'<%=request.getContextPath()%>/WidgetConfiguration',
	         type:'POST',
	         data:{widgetId:widgetId,jsonData:jsonString},
	         async:false,
	         success: function(transport){
	        	 $("#"+widgetId).find(".widgetcontent").load("jsp/dashboardwidgets/dscwidgets/DSCPrimaryServerWidget.jsp");
	       }
	  	 });
		 
	 }
		 
	
}); 
 $('#cancelButton').click(function (){
	 $("#"+widgetId).find(".widgetcontent").load("jsp/dashboardwidgets/dscwidgets/DSCPrimaryServerWidget.jsp");
 });
 
 </script>
</head>
 <%  List<NetServerInstanceData> netServerInstanceList = (List<NetServerInstanceData>)request.getSession().getAttribute("serverAAAList");%>
 
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0" id="dscPrimaryServer">
<input type="hidden" id="editConfigPage" name="editConfigPage" value="editConfigPage" />
<input type="hidden" id="dscPrimaryServer" name="dscPrimaryServer" value="dscPrimaryServer"/>
<tr>
	<td align="left">
				<table style="width: 100%" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Name :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="dscWidgetName" name="dscWidgetName" title="Name" class="dashboardTextClass" value="DSC-Primary-Server">
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">The name of Widget on Dashboard</label>	
						</td>
					</tr>
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Refresh Interval :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="dscRefreshInterval" name="dscRefreshInterval" title="To date" class="dashboardTextClass" value="90">
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">How often you would like this widget to update</label>	
						</td>
					</tr>
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Days Previously :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;" width="70%">
								<input type="text" id="dscDaysPreviously" name="dscDaysPreviously" title="To date" class="dashboardTextClass" value="90">
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Days(including Today) to show in graph</label>	
						</td>
					</tr>
					<tr>
						<td align="right" width="30%">
							<label class="dashboardLabel">Server Instances :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
						 	<select class="dashboardDDClass" id="dscServerInstance" name="dscServerInstance" value="0">
						 			<option value="0">--select--</option>
									<%if (netServerInstanceList != null && netServerInstanceList.size() > 0) {
										for(NetServerInstanceData netServerInstanceData:netServerInstanceList){%>
											<option value="<%=netServerInstanceData.getNetServerId()%>">
												<%=netServerInstanceData.getName() %>
											</option>
										<%}
									} %>
							</select> 
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Selects the Server Instance for which statistics to display</label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">TPS Usage :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<select id="dscTpsUsage" name="dscTpsUsage" class="dashboardDDClass">
									<option value="YES">Yes</option>
									<option value="NO">No</option>
								</select>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Whether to show TPS Usage for instance </label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">Memory Usage :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<select id="dscMemoryUsage" name="dscMemoryUsage" class="dashboardDDClass">
									<option value="YES">Yes</option>
									<option value="NO">No</option>
								</select>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Whether to show Memory Usage for instance </label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">CPU Usage :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<select id="dscCpuUsage" name="dscCpuUsage" class="dashboardDDClass">
									<option value="YES">Yes</option>
									<option value="NO">No</option>
								</select>
						</td>
						</td>
					</tr>
					<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Whether to show CPU Usage for instance </label>	
						</td>
					</tr>
					<tr>
						<td align="right">
							<label class="dashboardLabel">Thread Usage :</label>
						</td>
						<td align="left" style="padding-top: 5px;padding-left: 5px;">
								<select id="dscThreadUsage" name="dscThreadUsage" class="dashboardDDClass">
									<option value="YES">Yes</option>
									<option value="NO">No</option>
								</select>
						</td>
					</tr>
						<tr>
						<td align="right">
						</td>
						<td align="left" style="padding-left: 5px;">	
							<label class="dashboardHelpTag">Whether to show Thread Usage for instance </label>	
						</td>
					</tr>
					<tr>
						<td align="center" colspan="2" style="padding-left: 10px;padding-right: 10px;">
							<div style=" min-height: 1px;clear:both; width:100%;border-bottom:1px solid #d1d1d1;height:1px; padding-top:5px;margin-top:5px;margin-bottom:5px;"></div>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td align="left" style="padding-left: 10px;padding-top: 10px;">
					            <input class="light-btn" type="submit" value="Save" id="dscSubmitBtn">&nbsp;&nbsp;&nbsp;
					            <input class="light-btn" type="button" text="Cancel" value="Cancel" id="cancelButton"/>
						</td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
	</td>
</tr>
</table>
</body>
</html>