<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.radiusservicepolicy.data.RadServicePolicyData"%>
<%@page import="org.apache.struts.util.PropertyMessageResources"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.sessionmanager.data.ISessionManagerInstanceData"%>
<%@page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ServiceTypeConstants"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy.form.UpdateRadiusServicePolicyForm" %>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%
	RadServicePolicyData radiusServicePolicyData  = (RadServicePolicyData) request.getAttribute("radServicePolicyData");

	List<PluginInstData> postPluginList = (List<PluginInstData>)request.getAttribute("postPluginList");
	
	UpdateRadiusServicePolicyForm updateRadiusServicePolicyForm = (UpdateRadiusServicePolicyForm)request.getAttribute("updateRadiusServicePolicyForm");

	
	DriverBLManager driverManager = new DriverBLManager();
	List<DriverInstanceData> listOfDriver = driverManager.getDriverInstanceList(ServiceTypeConstants.AUTHENTICATION_SERVICE);
	
	String[] driverInstanceIds = new String [listOfDriver.size()];
	String[][] driverInstanceNames = new String[listOfDriver.size()][3]; 
	
	for(int i=0;i<listOfDriver.size();i++){
		DriverInstanceData data = listOfDriver.get(i);				
			driverInstanceNames[i][0] = String.valueOf(data.getName());
			driverInstanceNames[i][1] = String.valueOf(data.getDescription());
			driverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
			driverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
	}
		
	String[] cacheableDriverInstIds = (String[])request.getAttribute("cacheableDriverInstIds");
	String[][] cacheableDriverInstanceNames = (String[][])request.getAttribute("cacheableDriverInstanceNames");
	List<DriverInstanceData> cacheableDriverInstDataList = driverManager.getCacheableDriverData();
	Iterator<DriverInstanceData> cacheableDriverInstDataListItr = cacheableDriverInstDataList.iterator();
	List<DriverInstanceData> additionalableDriverInstDataList = driverManager.getCacheableDriverData();
	List<DriverInstanceData> listOfAcctDriver = (List<DriverInstanceData>)request.getAttribute("listOfAcctDriver");	
	List<DriverTypeData> driverTypeList =(List<DriverTypeData>)request.getAttribute("driverTypeList");
		
	String[] acctDriverInstanceIds = new String [listOfAcctDriver.size()];
	String[][] acctDriverInstanceNames = new String[listOfAcctDriver.size()][3]; 
		
	for(int i=0;i<listOfAcctDriver.size();i++){
		DriverInstanceData data = listOfAcctDriver.get(i);				
		acctDriverInstanceNames[i][0] = String.valueOf(data.getName());
		acctDriverInstanceNames[i][1] = String.valueOf(data.getDescription());
		acctDriverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
		acctDriverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
	}

%>
<style>
	#sortableClass { list-style-type: none; margin: 0; padding: 0; width: 100%;padding-right: 20px; }
	#sortableClass li { background-color: white;padding-bottom: 10px;padding-left: 10px;padding-right: 10px;}
	#sortableClassAdditional { list-style-type: none; margin: 0; padding: 0; width: 100%;padding-right: 20px;}
	#sortableClassAdditional li { background-color: white;padding-bottom: 10px;padding-left: 10px;padding-right: 10px;}
	.handler-css{}
	.ui-widget-content .ui-state-hover,.ui-widget-content .ui-state-focus{
    	background: none;
   		border: none;
	}
</style>
<script src="<%=request.getContextPath()%>/js/datatable/jquery-ui.js" /> 
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script src="<%=request.getContextPath()%>/js/servicepolicy/proxycommunication-update.js"></script> 
<script language="javascript" src="<%=request.getContextPath()%>/js/servicepolicy/cdr-mapping-table.js"></script>
<script src="<%=request.getContextPath()%>/js/servicepolicy/radius-service-policy-update-accounting.js"></script>
<script language="javascript"  src="<%=request.getContextPath()%>/js/servicepolicy/radiusservicepolicy/radius-service-policy.js"></script> 
<script src="<%=request.getContextPath()%>/js/servicepolicy/radius-service-policy.js"></script>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/togglebutton.css" />
<script type="text/javascript">
var prePluginList = [];
var postPluginList = [];
var driverScriptList = [];
var externalScriptList = [];

<% 
if( Collectionz.isNullOrEmpty(updateRadiusServicePolicyForm.getDriverScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : updateRadiusServicePolicyForm.getDriverScriptList()){ %>
		driverScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
if( Collectionz.isNullOrEmpty(updateRadiusServicePolicyForm.getExternalScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : updateRadiusServicePolicyForm.getExternalScriptList()){ %>
		externalScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

function changeValueOfFlow(obj){
	if( $(obj).val() == 'true'){
		$(obj).val('false');
		var handlerObject=$(obj).closest('table[class^="handler-class"]');
		var flag = false;
		$(handlerObject).find('tr').each(function(){
			$(this).addClass('disable-toggle-class');
		});
	}else{
		$(obj).val('true');
		var handlerObject=$(obj).closest('table[class^="handler-class"]');
		$(handlerObject).find('tr').each(function(){
			$(this).removeClass('disable-toggle-class');
		});
	}
}

$(document).ready(function() {
	
	/* Get post plugin list*/
	<%for(PluginInstData pluginInstData:postPluginList){%>
		postPluginList.push({"id":"<%=pluginInstData.getPluginInstanceId()%>","value":"<%=pluginInstData.getPluginTypeName()%>","label":"<%=pluginInstData.getName()%>"});
	<%}%>
	
	/* Setting Pre and Post Plugin Data List */
	setPluginData(getAcctPrePluginData(), 'pre-acct-plugin-mapping-table', 'pre-acct-plugin-mapping-template');
	setPluginData(getAcctPostPluginData(), 'post-acct-plugin-mapping-table', 'post-acct-plugin-mapping-template');
	
	getPrimaryServerData();
	setBroadcastServerOptions(getBroadcastServerOptions());
	setproxyESIServerData(getProxyCommunicationData());
	
	//For ==> Update Handler data
	setProfileLookupDriver(readProfileLookupDriver()); 
	setPluginHandler(readPluginHandler());
	setProxyCommunicationHandler(readProxyCommunicationHandler());
	setBroadcastCommunicationHandler(readBroadcastCommunicationHandler());
	setCDRGenerationHandler(readCDRGenerationHandler());
	setCoaDMGenerationHandler(readCOADMGenerationHandler());
    setStatefulProxySequentialHandler(readStatefulProxySequentialHandler());
    setStatefulProxyBroadcastHandler(readStatefulProxyBroadcastHandler());
	
	/* Script Autocomplete */
	setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
	setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
	
	/* START : below all functions are for delete rows for different handlers */
	
	$('.proxyCommunicationTbl td img.delete').live('click',function() {
			$(this).parent().parent().remove();
			setDropDown();
	});	
	
	$('.broadcastCommunicationTbl td img.delete').live('click',function() {
		$(this).parent().parent().remove();
		setDropDown();
	});	
	 
	$('.coaDmGenerationtbl td img.delete').live('click',function() {
		$(this).parent().parent().remove();
	});
	
	$('.coaDmGenerationNastbl td img.delete').live('click',function() {
		$(this).parent().parent().remove();
	});
	
	$('.mappingtblcdr td img.delete').live('click',function() {
		$(this).parent().parent().remove();
	});
	
	$('.mappingtblplugin td img.delete').live('click',function() {
		$(this).parent().parent().remove();
	});
	$('#nasClientMappingTable td img.delete').live('click',function() {
		$(this).parent().parent().remove();
	});
	
	$('.proxyCommServer td span.delete').live('click',function(){
		var selectObj =$(this).closest('.proxyCommServer');
		$(this).parent().parent().remove();
		resetProxyHandlerESIDropDown(selectObj);
	});
	
	$('.broadcastServer td span.delete').live('click',function(){
		var selectObj =$(this).closest('.broadcastServer');
		$(this).parent().parent().remove();
		resetProxyHandlerESIDropDown(selectObj);
	});
	 
	$('#prePluginTbl td span.delete').live('click',function(){
		$(this).parent().parent().remove();
	});
	
	$('#postPluginTbl td span.delete').live('click',function(){
		$(this).parent().parent().remove();
	});
	/* END */
	
	
	
	$("#accountingTbl tbody.parent").sortable({
		helper: fixHelper,
		placeholder: 'ui-state-highlight',
	 	update: function(event, ui) {
            var order = $("#accountingTbl tbody.parent").sortable("toArray");
	        var counter = 1;
	            
	        $.each(order, function() {
	           var value = this;
	           var trIds = $("#accountingTbl").find("#"+value).find('input#orderNumber');
	           $(trIds).val(counter);
	           counter++;
	        });
		},start: function(e, ui){
			var height = ui.item.height();
			ui.placeholder.height(ui.item.height());
			height =  height -10;
		    ui.placeholder.html("<div style='border:2px dashed #D8D8D8;height:"+height+";background:#F5F5F5;text-align: center;margin-right:0px;color:gray;'>Drag your handler here</div>");
		}
	});
	 
	$("#additionalTbl tbody.parent").sortable({
	  helper: fixHelper,
	  placeholder: 'ui-state-highlight',
	  update: function(event, ui) {
          var order = $("#additionalTbl tbody.parent").sortable("toArray");
          var counter = 1;
          $.each(order, function() {
           	var value = this;
            var trIds = $("#additionalTbl").find("#"+value).find('input#orderNumber');
          	$(trIds).val(counter);
           	counter++;
          });
          
	  },start: function(e, ui){
		 var height = ui.item.height();
		 ui.placeholder.height(ui.item.height());
		 height =  height -10;
		 ui.placeholder.html("<div style='border:2px dashed #D8D8D8;height:"+height+";background:#F5F5F5;text-align: center;margin-right:0px;color:gray;'>Drag your handler here</div>");
		}
	}); 
	
	loadConfirmDialog();
});

function  setAutoCompleteDataAuthPluginData(dbFieldObject){
	 $( dbFieldObject ).bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).autocomplete( "instance" ).menu.active ) {
				event.preventDefault();
			}
	 }).autocomplete({
		minLength: 0,
		source: function( request, response ) {
			response( $.ui.autocomplete.filter(
				postPluginList, extractLastDbFields( request.term ) ) );
		},
		focus: function() {
			return false;
		},
		select: function( event, ui ) {
			var val = this.value;
			var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
			var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
			 if(commaIndex == semiColonIndex) {
					val = "";
			}  else if(commaIndex > semiColonIndex) {
					val = val.substring(0,commaIndex+1); 
			} else if(semiColonIndex !=0 && semiColonIndex > commaIndex){
				val = val.substring(0,semiColonIndex+1); 
			}	 
			this.value = val + ui.item.label ;
			return false;
		}
	});
}

function  setAutoCompleteDataAuthPostPluginData(dbFieldObject){
	
	 $( dbFieldObject ).bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).autocomplete( "instance" ).menu.active ) {
				event.preventDefault();
			}
	 }).autocomplete({
		minLength: 0,
		source: function( request, response ) {
			response( $.ui.autocomplete.filter(
				postPluginList, extractLastDbFields( request.term ) ) );
		},
		focus: function() {
			return false;
		},
		select: function( event, ui ) {
			var val = this.value;
			var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
			var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
			 if(commaIndex == semiColonIndex) {
					val = "";
			}  else if(commaIndex > semiColonIndex) {
					val = val.substring(0,commaIndex+1); 
			} else if(semiColonIndex !=0 && semiColonIndex > commaIndex){
				val = val.substring(0,semiColonIndex+1); 
			}	 
			 this.value = val + ui.item.label ;
			return false;
		}
	});
}

function  setAutoCompleteDataforPlugin(dbFieldObject){
	var mainTableId = $(dbFieldObject).closest('.main-parent-table-class').attr('id');
	if(mainTableId == 'additionalTbl'){
		$( dbFieldObject ).bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).autocomplete( "instance" ).menu.active ) {
				event.preventDefault();
			}
		 }).autocomplete({
			minLength: 0,
			source: function( request, response ) {
				response( $.ui.autocomplete.filter(
					postPluginList, extractLastDbFields( request.term ) ) );
			},
			focus: function() {
				return false;
			},
			select: function( event, ui ) {
				var val = this.value;
				var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
				var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
				 if(commaIndex == semiColonIndex) {
						val = "";
				}  else if(commaIndex > semiColonIndex) {
						val = val.substring(0,commaIndex+1); 
				} else if(semiColonIndex !=0 && semiColonIndex > commaIndex){
					val = val.substring(0,semiColonIndex+1); 
				}	 
				this.value = val + ui.item.label ;
				return false;
			}
		});
	}else{
		$( dbFieldObject ).bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
				$( this ).autocomplete( "instance" ).menu.active ) {
				event.preventDefault();
			}
		 }).autocomplete({
			minLength: 0,
			source: function( request, response ) {
				response( $.ui.autocomplete.filter(
					postPluginList, extractLastDbFields( request.term ) ) );
			},
			focus: function() {
				return false;
			},
			select: function( event, ui ) {
				var val = this.value;
				var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
				var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
				 if(commaIndex == semiColonIndex) {
						val = "";
				}  else if(commaIndex > semiColonIndex) {
						val = val.substring(0,commaIndex+1); 
				} else if(semiColonIndex !=0 && semiColonIndex > commaIndex){
					val = val.substring(0,semiColonIndex+1); 
				}	 
				this.value = val + ui.item.label ;
				return false;
			}
		});
	}
}

function getAcctPrePluginData(){
	var acctPrePluginDataList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctPrePluginData">
		acctPrePluginDataList.push({'pluginName':'<bean:write name="obj" property="pluginName"/>','pluginArgument':'<bean:write name="obj" property="pluginArgument"/>'});
	</logic:iterate>
	return acctPrePluginDataList;
}

function getAcctPostPluginData(){
	 var acctPostPluginDataList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctPostPluginData">
	acctPostPluginDataList.push({'pluginName':'<bean:write name="obj" property="pluginName"/>','pluginArgument':'<bean:write name="obj" property="pluginArgument"/>'});
	</logic:iterate>
	return acctPostPluginDataList; 
}

/* This function is required for setting ESI Server list */
function getBroadcastServerOptions(){
	var broadcastServerOptions = [] ;
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctESIList">
		var obj = {};
		obj.value = '<bean:write name="obj" property="esiInstanceId"/>';
		obj.name =  '<bean:write name="obj" property="name"/>';
		broadcastServerOptions.push(obj);
	</logic:iterate>
	return broadcastServerOptions;
}

function readProfileLookupDriver(){
	var profileLookUpDriver = [] ;
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctProfileLookupDriver">
		
		var primaryDriversList = [];
		<logic:iterate id="primaryDriver" name="obj" property="primaryDriverRelDataList">
			var primaryDriver = {'driverInstanceId':'<bean:write name="primaryDriver" property="driverInstanceId"/>',
								 'weightage':'<bean:write name="primaryDriver" property="weightage"/>'};
			primaryDriversList.push(primaryDriver);
		</logic:iterate>
		
		var secondaryDriversList = [];
		<logic:iterate id="secondaryDriver" name="obj" property="secondaryDriverRelDataList">
			var secondaryDriver = {'secondaryDriverInstId':'<bean:write name="secondaryDriver" property="secondaryDriverInstId"/>',
								 'cacheDriverInstId':'<bean:write name="secondaryDriver" property="cacheDriverInstId"/>'};
			secondaryDriversList.push(secondaryDriver);
		</logic:iterate>
		
		var additionalDriverList = [];
		<logic:iterate id="additionalDriver" name="obj" property="additionalDriverRelDataList">
			var secondaryDriver = {'driverInstanceId':'<bean:write name="additionalDriver" property="driverInstanceId"/>',
							       'orderNumber':'<bean:write name="additionalDriver" property="orderNumber"/>'};
			additionalDriverList.push(secondaryDriver);
		</logic:iterate>
	
		var obj = {'realmPattern':'<bean:write name="obj" property="realmPattern"/>',
				   'stripIdentity':'<bean:write name="obj" property="stripIdentity"/>',
				   'separator':'<bean:write name="obj" property="separator"/>',
				   'selectCase':'<bean:write name="obj" property="selectCase"/>',
				   'trimUserIdentity':'<bean:write name="obj" property="trimUserIdentity"/>',
				   'trimPassword':'<bean:write name="obj" property="trimPassword"/>',
				   'anonymousProfileIdentity':'<bean:write name="obj" property="anonymousProfileIdentity"/>',
				   'driverScript':'<bean:write name="obj" property="driverScript"/>',
				   'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
				   'isAdditional':'<bean:write name="obj" property="isAdditional"/>',
				   'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
				   'handlerName':'<bean:write name="obj" property="handlerName"/>',
				   'primaryDriverRelDataList':primaryDriversList,
				   'secondaryDriverRelDataList':secondaryDriversList,
				   'additionalDriverRelDataList':additionalDriverList
				  };
		profileLookUpDriver.push(obj);
	</logic:iterate>
	return profileLookUpDriver;
}

function readPluginHandler(){
	var pluginHandlerList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctPluginHandlerList">
			var pluginDetails = [];
		    <logic:iterate id="pluginData" name="obj" property="lstPluginDetails">
				pluginDetails.push({'requestType':'<bean:write name="pluginData" property="requestType"/>','pluginName':'<bean:write name="pluginData" property="pluginName"/>','pluginArgument':'<bean:write name="pluginData" property="pluginArgument" />','ruleset':'<bean:write name="pluginData" property="ruleset"/>'});
			</logic:iterate>
			var pluginHandlerData = {'pluginDetails':pluginDetails,'orderNumber':'<bean:write name="obj" property="orderNumber"/>','isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>','handlerName':'<bean:write name="obj" property="handlerName"/>','isAdditional':'<bean:write name="obj" property="isAdditional"/>'};
		pluginHandlerList.push(pluginHandlerData);
	</logic:iterate>
	return pluginHandlerList;
}

function readProxyCommunicationHandler(){
	var proxyCommunicationList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctProxyCommunicationList">
			var proxyDetails = [];
		    <logic:iterate id="proxyCommData" name="obj" property="proxyCommunicationList">
		   		var esiList =[];
		   	  	<logic:iterate id="esiData" name="proxyCommData" property="esiListData">
		   	 		esiList.push({'esiId':'<bean:write name="esiData" property="esiId"/>','loadFactor':'<bean:write name="esiData" property="loadFactor"/>'});
		   	  	</logic:iterate>
		    	proxyDetails.push({'ruleset':'<bean:write name="proxyCommData" property="ruleset"/>','translationMappingName':'<bean:write name="proxyCommData" property="translationMappingName"/>','script':'<bean:write name="proxyCommData" property="script"/>','acceptOnTimeout':'<bean:write name="proxyCommData" property="acceptOnTimeout"/>','esiListData':esiList});
			</logic:iterate>
			var proxyCommunicationHandlerData = {'proxyCommunicationList':proxyDetails,'orderNumber':'<bean:write name="obj" property="orderNumber"/>','isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>','handlerName':'<bean:write name="obj" property="handlerName"/>','isAdditional':'<bean:write name="obj" property="isAdditional"/>'};
			proxyCommunicationList.push(proxyCommunicationHandlerData);
	</logic:iterate>
	return proxyCommunicationList;
}

function readBroadcastCommunicationHandler(){
	var broadcastCommunicationList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctBroadcastCommunicationList">
			var broadcastDetails = [];
		    <logic:iterate id="broadcastCommData" name="obj" property="broadcastCommunicationList">
		   		var esiList =[];
		   	  	<logic:iterate id="esiData" name="broadcastCommData" property="esiListData">
		   	 		esiList.push({'esiId':'<bean:write name="esiData" property="esiId"/>','loadFactor':'<bean:write name="esiData" property="loadFactor"/>'});
		   	  	</logic:iterate>
		   	 	broadcastDetails.push({'ruleset':'<bean:write name="broadcastCommData" property="ruleset"/>','translationMappingName':'<bean:write name="broadcastCommData" property="translationMappingName"/>','script':'<bean:write name="broadcastCommData" property="script"/>','acceptOnTimeout':'<bean:write name="broadcastCommData" property="acceptOnTimeout"/>','waitForResponse':'<bean:write name="broadcastCommData" property="waitForResponse"/>','esiListData':esiList});
			</logic:iterate>
			var broadcastCommunicationHandlerData = {'broadcastCommunicationList':broadcastDetails,'orderNumber':'<bean:write name="obj" property="orderNumber"/>','isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>','handlerName':'<bean:write name="obj" property="handlerName"/>','isAdditional':'<bean:write name="obj" property="isAdditional"/>'};
			broadcastCommunicationList.push(broadcastCommunicationHandlerData);
	</logic:iterate>
	return broadcastCommunicationList;
}

function readCDRGenerationHandler(){
	var cdrGenerationHandlerList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctCDRGenerationList">
		var cdrDetails = [];
	    <logic:iterate id="cdrGenerationData" name="obj" property="cdrGenerationDetailsList">
	    cdrDetails.push({'ruleset':'<bean:write name="cdrGenerationData" property="ruleset"/>','primaryDriverId':'<bean:write name="cdrGenerationData" property="primaryDriverId"/>','secondaryDriverId':'<bean:write name="cdrGenerationData" property="secondaryDriverId"/>','script':'<bean:write name="cdrGenerationData" property="script"/>','waitForCDRDump':'<bean:write name="cdrGenerationData" property="waitForCDRDump"/>'});
		</logic:iterate>
		var cdrHandlerData = {'cdrGenerationDetailsList':cdrDetails,'orderNumber':'<bean:write name="obj" property="orderNumber"/>','isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>','handlerName':'<bean:write name="obj" property="handlerName"/>','isAdditional':'<bean:write name="obj" property="isAdditional"/>'};
		cdrGenerationHandlerList.push(cdrHandlerData);
	</logic:iterate>
	return cdrGenerationHandlerList;
}


function readCOADMGenerationHandler(){
	var coaDMGenerationList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctCOADMGenList">
		var coaDmDetails = [];
	    <logic:iterate id="coaDMGenerationData" name="obj" property="coaDMGenerationDetailList">
	   		 coaDmDetails.push({'ruleset':'<bean:write name="coaDMGenerationData" property="ruleset"/>','packetType':'<bean:write name="coaDMGenerationData" property="packetType"/>','translationMapping':'<bean:write name="coaDMGenerationData" property="translationMapping"/>'});
		</logic:iterate>
		var coaDMHandlerData = {'coaDMGenerationDetailList':coaDmDetails,'orderNumber':'<bean:write name="obj" property="orderNumber"/>','isAdditional':'<bean:write name="obj" property="isAdditional"/>','isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>','handlerName':'<bean:write name="obj" property="handlerName"/>','scheduleAfterInMillis':'<bean:write name="obj" property="scheduleAfterInMillis"/>'};
		coaDMGenerationList.push(coaDMHandlerData);
	</logic:iterate>
	return coaDMGenerationList;
}

function readStatefulProxySequentialHandler(){
    var statefulProHandlerList = [];
    <logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="statefulProxySequentialHandlerList">
    var entryDataList = [];
    <logic:iterate id="handlerEntryData" name="obj" property="sequentialHandlerEntryData">
    entryDataList.push({'ruleset':'<bean:write name="handlerEntryData" property="ruleset"/>','serverGroupName':'<bean:write name="handlerEntryData" property="serverGroupName"/>',
        'translationMappingName':'<bean:write name="handlerEntryData" property="translationMappingName"/>','script':'<bean:write name="handlerEntryData" property="script"/>',
        'acceptOnTimeout':'<bean:write name="handlerEntryData" property="acceptOnTimeout"/>'})
    </logic:iterate>
    var statefulProxySeqHandlerData = {'statefulProxyCommunicationList':entryDataList,'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
        'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
        'handlerName':'<bean:write name="obj" property="handlerName"/>',
        'isAdditional':'<bean:write name="obj" property="isAdditional"/>'
    };
    statefulProHandlerList.push(statefulProxySeqHandlerData);
    </logic:iterate>
    return statefulProHandlerList;
}

function readStatefulProxyBroadcastHandler(){
    var statefulProBroadcastHandlerList = [];
    <logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="statefulProxyBroadcastHandlerAcctList">
    var entryDataList = [];
    <logic:iterate id="handlerEntryData" name="obj" property="broadcastHandlerEntryData">
    entryDataList.push({'ruleset':'<bean:write name="handlerEntryData" property="ruleset"/>','serverGroupName':'<bean:write name="handlerEntryData" property="serverGroupName"/>',
        'translationMappingName':'<bean:write name="handlerEntryData" property="translationMappingName"/>','script':'<bean:write name="handlerEntryData" property="script"/>',
        'acceptOnTimeout':'<bean:write name="handlerEntryData" property="acceptOnTimeout"/>','waitForResponse':'<bean:write name="handlerEntryData" property="waitForResponse"/>'})
    </logic:iterate>
    var statefulProxyBroadcastHandlerData = {'statefulProxyBroadcastCommunicationList':entryDataList,'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
        'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
        'handlerName':'<bean:write name="obj" property="handlerName"/>',
        'isAdditional':'<bean:write name="obj" property="isAdditional"/>'
    };
    statefulProBroadcastHandlerList.push(statefulProxyBroadcastHandlerData);
    </logic:iterate>
    return statefulProBroadcastHandlerList;
}


/* This function is required for getting accounting driver 
 * "var driverDataJson" is declared in cdr-mapping-table.js
 */
function getPrimaryServerData(){
	 <%for(DriverTypeData driverTypeData:driverTypeList){ %>
		driverDataJson['<%=driverTypeData.getName()%>'] = [];
		<%for(DriverInstanceData driverInstanceData:listOfAcctDriver){ 
			if(driverTypeData.getDriverTypeId() == driverInstanceData.getDriverTypeId()){%>
				driverDataJson['<%=driverTypeData.getName()%>'].push(
						{id:'<%=driverInstanceData.getDriverInstanceId()%>'
						,name:'<%=driverInstanceData.getName()%>'});
			<%}%>
	    <%}%>
	 <%}%>  
}

/* This function is required for reload list of accounting driver 
 * "var driverDataJson" is declared in cdr-mapping-table.js
 */
function reloadDriverList(){
	 var response;
	 $.ajax({url:'GetPrimaryDriverDataServlet',
			type:'POST',
			async:false,
			success: function(transport){
				response=transport;
				<%for(DriverTypeData driverTypeData:driverTypeList){ %>
					driverDataJson['<%=driverTypeData.getName()%>'] = [];
					var typeid=<%=driverTypeData.getDriverTypeId()%>;
					$.each(response, function(index, item) { 
						var innerTypeId= item.driverTypeId;
						if( parseInt(innerTypeId) == parseInt(typeid)){
							driverDataJson['<%=driverTypeData.getName()%>']
								.push({id : item.driverInstanceId,name: item.Name});
						}
					});
				<%}%>
			}
	});
	setOtherDriverDropDown('form_cdrGeneretaion');
}

function setPacketTypeForCOADM(selectedObject){
	$(selectedObject).css({'border':'none'});
}

function splitDbFields( val ) {
	return val.split( /[,;]\s*/ );
}

function extractLastDbFields( term ) {
	return splitDbFields( term ).pop();
}

var proxyESIServerData;
function setproxyESIServerData(proxyESIServerData){
	this.proxyESIServerData = proxyESIServerData;
}

function getProxyCommunicationData(){
	var proxyList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="acctESIList" type="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData">
		proxyList.push({'id':'<bean:write name="obj" property="esiInstanceId"/>','name':'<bean:write name="obj" property="name"/>','esiTypeId':'<bean:write name="obj" property="esiTypeId"/>'});
	</logic:iterate>
	return proxyList;
}
 
function getSelectedESIArray(selectedObject){
	var proxyListIdNameArray = new Array();
	var arrIndex = 0;
	var tableObj=$(selectedObject).parent().parent().parent().parent();
	$(tableObj).find(" select[name='esiId']").each(function(){
		if($(this).val() != "0"){
			proxyListIdNameArray[arrIndex++] = $(this).val();
		}				
	});
	return proxyListIdNameArray;
} 

function getAfterDeletedSelectedESIArray(tableObj){
	var proxyListIdNameArray = new Array();
	var arrIndex = 0;
	$(tableObj).find(" select[name='esiId']").each(function(){
		if($(this).val() != "0"){
			proxyListIdNameArray[arrIndex++] = $(this).val();
		}				
	});
	return proxyListIdNameArray;
} 

function addProxyDropDown(selectedObject){
	var proxyListIdNameArray = getSelectedESIArray(selectedObject);
	var tableObj = $(selectedObject).parent().parent().parent().parent();
	var selectBoxObj = $(tableObj).find( "tr:last").find("select:first");
	$(selectBoxObj).append("<option value='0'>--Select--</option>");

	if(proxyESIServerData!=undefined){
		$.each(proxyESIServerData, function(index, item) {
			if($.inArray(item.name,proxyListIdNameArray) < 0 ){
				$(selectBoxObj).append("<option value='" +  item.name + "'>" + item.name + "</option>");
			}
		});
	}
}
function getFirstESITypeId(esiValue){
	var esiTypeId="";
	var esiProxyList = getProxyCommunicationData();
	$(esiProxyList).each(function(key,value){
		if(value.id == esiValue){
			esiTypeId = value.esiTypeId;
		}
	});
	return esiTypeId;
}

function setProxyHandlerESIDropDown(selectedObject){
	var proxyListIdNameArray = getSelectedESIArray(selectedObject);
	var tableObj = $(selectedObject).parent().parent().parent().parent();
	$(tableObj).find("select[name='esiId']").each(function(){
		var currentVal = $(this).val();
		$(this).empty();
		var selectObj = this;
		$(selectObj).append("<option value='0'>--Select--</option>");
		if(proxyESIServerData!=undefined){
			$.each(proxyESIServerData, function(index, item) {
				if( $.inArray(item.id,proxyListIdNameArray) < 0 ||  item.id == currentVal ){
					$(selectObj).append("<option value='" +  item.name + "'>" + item.name + "</option>");
				}
			});
		}
		$(selectObj).val(currentVal);
	}); 
	$(tableObj).css({'border':'1px solid #c0c0c0'});
} 		

function resetProxyHandlerESIDropDown(tableObj){
	var proxyListIdNameArray = getAfterDeletedSelectedESIArray(tableObj);
	
	$(tableObj).find("select[name='esiId']").each(function(){
		var currentVal = $(this).val();
		$(this).empty();
		var selectObj = this;
		$(selectObj).append("<option value='0'>--Select--</option>");
		if(proxyESIServerData!=undefined){
			$.each(proxyESIServerData, function(index, item) {
				if( $.inArray(item.id,proxyListIdNameArray) < 0 ||  item.id == currentVal ){
					$(selectObj).append("<option value='" +  item.name + "'>" + item.name + "</option>");
				}
			});
		}
		$(selectObj).val(currentVal);
	}); 
	$(tableObj).css({'border':'1px solid #c0c0c0'});
} 

function addMoreServer(obj){
	var tableObj=$(obj).parent().parent().parent();
	var htmlSource="<tr>"+
	"<td align='left' class='top-border labeltext' valign='top' width='20%' id='tbl_attrid'>"+
	"<select name='esiId' class='noborder' style='width:100%;'  onchange='setProxyHandlerESIDropDown(this);'>"+
	"</select></td>"+
	"<td align='left' class='top-border labeltext' valign='top' width='12%'>"+
	"<select  id='loadFactor' name='loadFactor' class='noborder' style='width:100%;'><option value='0'>0</option><option selected='selected' value='1'>1</option><option value='2'>2</option><option value='3'>3</option><option value='4'>4</option><option value='5'>5</option><option value='6'>6</option><option value='7'>7</option><option value='8'>8</option><option value='9'>9</option><option value='10'>10</option></select></td>"+
	"<td align='center' class='top-border labeltext' valign='top' width='1%'><span class='delete remove-proxy-server'/>&nbsp;</td></tr>";
	$(tableObj).append(htmlSource);
	addProxyDropDown(obj);
}

function addESIData(tableObj,esiId,loadFactor){
	var htmlSource="<tr>"+
	"<td align='left' class='top-border labeltext' valign='top' width='20%' id='tbl_attrid'>"+
	"<select name='esiId' class='noborder' style='width:100%;'  onchange='setProxyHandlerESIDropDown(this);'><option value='0'>--Select--</option>"+
	"<logic:iterate id='obj' name='updateRadiusServicePolicyForm' property='acctESIList' type='com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData'>"+
	"<option value='<bean:write name='obj' property='name'/>'><bean:write name='obj' property='name'/></option></logic:iterate></select></td>"+
	"<td align='left' class='top-border labeltext' valign='top' width='12%'>"+
	"<select  id='loadFactor' name='loadFactor' class='noborder' style='width:100%;'><option value='0'>0</option><option selected='selected' value='1'>1</option><option value='2'>2</option><option value='3'>3</option><option value='4'>4</option><option value='5'>5</option><option value='6'>6</option><option value='7'>7</option><option value='8'>8</option><option value='9'>9</option><option value='10'>10</option></select></td>"+
	"<td align='center' class='top-border labeltext' valign='top' width='1%'><span class='delete remove-proxy-server'/>&nbsp;</td></tr>";
	$(tableObj).append(htmlSource);
	var tableObj1= $(tableObj).find("tr:last");
	var esiObj=$(tableObj1).find("select[name='esiId']");
	$(esiObj).val(esiId);
	
	var loadFactorObj=$(tableObj1).find("select[name='loadFactor']");
	$(loadFactorObj).val(loadFactor);
	resetProxyHandlerESIDropDown(tableObj);
}

function reInitializeHandlerData(){
	profileLookupDriverJson = [];
	cdrGenerationJson = [];
	pluginHandlerJson = [];
	coaDMGenerationJson = [];
	proxyCommunicationJson = [];
	broadcastCommunicationJson = [];
    statefulProxySequentialHandlerAcctJson = [];
    statefulProxyBroadcastHandlerAcctJson = [];
}

var profileLookupDriverJson = [];
var cdrGenerationJson = [];
var pluginHandlerJson = [];
var coaDMGenerationJson = [];
var proxyCommunicationJson = [];
var broadcastCommunicationJson = [];
var statefulProxySequentialHandlerAcctJson = [];
var statefulProxyBroadcastHandlerAcctJson = [];

function validate(){
	if(validateNoOfHandler('accountingTbl')){
		return false;
	}
	
	selectAll();
	reInitializeHandlerData();
	
	$('.form_profilelookup').each(function(){
		profileLookupDriverJson.push($(this).serializeObject());
	});
	
	$('.form_cdrGeneretaion').each(function(){
		var cdrGentable = $(this).find('.mappingtblcdr');
		var cdrGenerationDetails = [];
		var orderNumber = $(this).find('input[name=orderNumber]').val();
		var isAdditional = $(this).find('input[name=isAdditional]').val();
		var isHandlerEnabled=$(this).find('input:checkbox[name=isHandlerEnabled]').val();
		var handlerName = $(this).find('input[name=handlerName]').val();
		
		cdrGentable.find('tr').each(function(){
			var ruleset,primaryDriverId,secondaryDriverId,script,waitForCDRDump;	
			if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
				ruleset=  $(this).find("input[name='ruleset']").val();
			}
			if(typeof $(this).find("select[name='primaryDriverId']").val() !== 'undefined'){
				primaryDriverId=  $(this).find("select[name='primaryDriverId']").val();
			}
			
			if(typeof $(this).find("select[name='secondaryDriverId']").val() !== 'undefined'){
				secondaryDriverId=  $(this).find("select[name='secondaryDriverId']").val();
			}
			
			if(typeof $(this).find("input[name='script']").val() !== 'undefined'){
				script=  $(this).find("input[name='script']").val();
			}
			
			if(typeof $(this).find("input:checkbox[name='waitForCDRDump']").val() !== 'undefined'){
				waitForCDRDump=  $(this).find("input:checkbox[name='waitForCDRDump']").val();
			}
			
			if(!isEmpty(ruleset) || !isEmpty(primaryDriverId) || !isEmpty(secondaryDriverId) || !isEmpty(script) || !isEmpty(waitForCDRDump)){
				cdrGenerationDetails.push({'ruleset':ruleset,'primaryDriverId':primaryDriverId,'secondaryDriverId':secondaryDriverId,'script':script,'waitForCDRDump':waitForCDRDump});
			}
		});
		cdrGenerationJson.push({'orderNumber':orderNumber,'isAdditional':isAdditional,'isHandlerEnabled':isHandlerEnabled,'handlerName':handlerName,'cdrGenerationDetailsList':cdrGenerationDetails});
	});
	
	$('.form_coaDMGeneretaion').each(function(){
		var coaDMGeneration = $(this).find('.coaDmGenerationtbl');
		var coaDMGenerationDetails = [];
		var orderNumber = $(this).find('input[name=orderNumber]').val();
		var isAdditional = $(this).find('input[name=isAdditional]').val();
		var scheduleAfterInMillis = $(this).find('input[name=scheduleAfterInMillis]').val();
		var isHandlerEnabled=$(this).find('input:checkbox[name=isHandlerEnabled]').val();
		var handlerName = $(this).find('input[name=handlerName]').val();
		
		coaDMGeneration.find('tr').each(function(){
			var ruleset,packetType,translationMapping;	
			if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
				ruleset=  $(this).find("input[name='ruleset']").val();
			}
			if(typeof $(this).find("select[name='packetType']").val() !== 'undefined'){
				packetType=  $(this).find("select[name='packetType']").val();
			}
			
			if(typeof $(this).find("select[name='translationMapping']").val() !== 'undefined'){
				translationMapping=  $(this).find("select[name='translationMapping']").val();
			}
			
			if(!isEmpty(ruleset) || !isEmpty(packetType) || !isEmpty(translationMapping)){
				coaDMGenerationDetails.push({'ruleset':ruleset,'packetType':packetType,'translationMapping':translationMapping});
			}
		});
		coaDMGenerationJson.push({'orderNumber':orderNumber,'isAdditional':isAdditional,'isHandlerEnabled':isHandlerEnabled,'handlerName':handlerName,'coaDMGenerationDetailList':coaDMGenerationDetails,'scheduleAfterInMillis':scheduleAfterInMillis});
	});
	
	$('.form_pluginHandler').each(function(){
		var plugintable = $(this).find('.mappingtblplugin');
		var pluginDetails = [];
		var orderNumber = $(this).find('input[name=orderNumber]').val();
		var isAdditional = $(this).find('input[name=isAdditional]').val();
		var isHandlerEnabled=$(this).find('input:checkbox[name=isHandlerEnabled]').val();
		var handlerName = $(this).find('input[name=handlerName]').val();
		
		plugintable.find('tr').each(function(){
			var ruleset,pluginName,isResponse;	
			if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
				ruleset=  $(this).find("input[name='ruleset']").val();
			}
			
			if(typeof $(this).find("input[name='pluginName']").val() !== 'undefined'){
				pluginName=  $(this).find("input[name='pluginName']").val();
			}
			
			if(typeof $(this).find("input[name='pluginArgument']").val() !== 'undefined'){
				pluginArgument = $(this).find("input[name='pluginArgument']").val();
			}
			
			if(typeof $(this).find("select[name='requestType']").val() !== 'undefined'){
				isResponse=  $(this).find("select[name='requestType']").val();
			}
			if(!isEmpty(ruleset) || !isEmpty(pluginName) || !isEmpty(isResponse)){
				pluginDetails.push({'ruleset':ruleset,'pluginName':pluginName,'pluginArgument':pluginArgument,'requestType':isResponse});
			}
		});
		pluginHandlerJson.push({'orderNumber':orderNumber,'isAdditional':isAdditional,'isHandlerEnabled':isHandlerEnabled,'handlerName':handlerName,'lstPluginDetails':pluginDetails});
	});
	
	$('.form_proxycommunication').each(function(){
		var proxyTableObj = $(this).find('.proxyCommunicationTbl');
		var proxyCommunicationData = [];
		var orderNumber = $(this).find('input[name=orderNumber]').val();
		var isAdditional = $(this).find('input[name=isAdditional]').val();
		var isHandlerEnabled=$(this).find('input:checkbox[name=isHandlerEnabled]').val();
		var handlerName = $(this).find('input[name=handlerName]').val();
		
		proxyTableObj.find('tr').each(function(){
			var ruleset,translationMapping,script,acceptOnTimeout,serverData = [];
			
		   if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
			   ruleset=  $(this).find("input[name='ruleset']").val();
		   }
		   
		   if(typeof $(this).find("select[name='translationMappingName']").val() !== 'undefined'){
			   translationMapping=  $(this).find("select[name='translationMappingName']").val();
		   }
		   
		   if(typeof $(this).find("input[name='script']").val() !== 'undefined'){
			   script=  $(this).find("input[name='script']").val();
		   }
		   
		   if(typeof $(this).find("input:checkbox[name='acceptOnTimeout']").val() !== 'undefined'){
			   acceptOnTimeout=  $(this).find("input:checkbox[name='acceptOnTimeout']").val();
		   }
		   
		   
		 var innerTableObj = $(this).find('.proxyCommServer');
		  
		 innerTableObj.find('tr').each(function (i, el) {
		        var $tds = $(this).find('td');
		        var serverId = '';
		        var loadFactor = '';
		        if(typeof $($tds).find("select[name='esiId']").val() !== 'undefined'){
		        	serverId = $($tds).find("select:first").val();
		        }
		        if(typeof $($tds).find("select[name='loadFactor']").val() !== 'undefined'){
		        	loadFactor = $($tds).find("select[name='loadFactor']").val();
		        }
		        if(!isEmpty(serverId) || !isEmpty(loadFactor)){
		        	serverData.push({'esiId':serverId,'loadFactor':loadFactor});
		        }
		    });
		   
			if(!isEmpty(ruleset) || !isEmpty(translationMapping) || !isEmpty(acceptOnTimeout)){
			   proxyCommunicationData.push({'ruleset':ruleset,'translationMappingName':translationMapping,'script':script,'acceptOnTimeout':acceptOnTimeout,'esiListData':serverData});
		   }
		 
		}); 
		if(proxyCommunicationData.length >= 1){
			proxyCommunicationJson.push({'orderNumber':orderNumber,'isAdditional':isAdditional,'isHandlerEnabled':isHandlerEnabled,'handlerName':handlerName,'proxyCommunicationList':proxyCommunicationData});
		}else{
			proxyCommunicationJson=[];
		}
	});
	
	
	$('.form_broadcastcommunication').each(function(){
		var proxyTableObj = $(this).find('.broadcastCommunicationTbl');
		var broadcastCommunicationData = [];
		var orderNumber = $(this).find('input[name=orderNumber]').val();
		var isAdditional = $(this).find('input[name=isAdditional]').val();
		var isHandlerEnabled=$(this).find('input:checkbox[name=isHandlerEnabled]').val();
		var handlerName = $(this).find('input[name=handlerName]').val();
		
		proxyTableObj.find('tr').each(function(){
			var ruleset,translationMapping,script,acceptOnTimeout,serverData = [],waitForResponse;
			
		   if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
			   ruleset=  $(this).find("input[name='ruleset']").val();
		   }
		   
		   if(typeof $(this).find("select[name='translationMappingName']").val() !== 'undefined'){
			   translationMapping=  $(this).find("select[name='translationMappingName']").val();
		   }
		   
		   if(typeof $(this).find("input[name='script']").val() !== 'undefined'){
			   script=  $(this).find("input[name='script']").val();
		   }
		   
		   if(typeof $(this).find("input:checkbox[name='acceptOnTimeout']").val() !== 'undefined'){
			   acceptOnTimeout= $(this).find("input:checkbox[name='acceptOnTimeout']").val();
		   }
		   
		   if(typeof $(this).find("input:checkbox[name='waitForResponse']").val() !== 'undefined'){
			   waitForResponse = $(this).find("input:checkbox[name='waitForResponse']").val();
		   }
		   
		 var innerTableObj = $(this).find('.broadcastServer');
		  
		 innerTableObj.find('tr').each(function (i, el) {
		        var $tds = $(this).find('td');
		        var serverId = '';
		        var loadFactor = '';
		        if(typeof $($tds).find("select[name='esiId']").val() !== 'undefined'){
		        	serverId = $($tds).find("select:first").val();
		        }
		        if(typeof $($tds).find("select[name='loadFactor']").val() !== 'undefined'){
		        	loadFactor = $($tds).find("select[name='loadFactor']").val();
		        }
		        if(!isEmpty(serverId) || !isEmpty(loadFactor)){
		        	serverData.push({'esiId':serverId,'loadFactor':loadFactor});
		        }
		    });
		   
			if(!isEmpty(ruleset) || !isEmpty(translationMapping) || !isEmpty(acceptOnTimeout)){
				broadcastCommunicationData.push({'ruleset':ruleset,'translationMappingName':translationMapping,'script':script,'acceptOnTimeout':acceptOnTimeout,'waitForResponse':waitForResponse,'esiListData':serverData});
		   }
		 
		}); 
		if(broadcastCommunicationData.length >= 1){
			broadcastCommunicationJson.push({'orderNumber':orderNumber,'isAdditional':isAdditional,'isHandlerEnabled':isHandlerEnabled,'handlerName':handlerName,'broadcastCommunicationList':broadcastCommunicationData});
		}else{
			broadcastCommunicationJson=[];
		}
	});

    $('.form_statefulproxycommunication').each(function(){

        var proxyTableObj = $(this).find('.statefulProxyCommunicationTbl');
        var statefulProxySequentialData = [];
        var orderNumber = $(this).find('input[name=orderNumber]').val();
        var isAdditional = $(this).find('input[name=isAdditional]').val();
        var isHandlerEnabled=$(this).find('input:checkbox[name=isHandlerEnabled]').val();
        var handlerName = $(this).find('input[name=handlerName]').val();

        proxyTableObj.find('tr').each(function(){
            var ruleset,serverGroupName,translationMapping,script,acceptOnTimeout = [];

            if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
                ruleset=  $(this).find("input[name='ruleset']").val();
            }

            if(typeof $(this).find("select[name='serverGroupName']").val() !== 'undefined'){
                serverGroupName= $(this).find("select[name='serverGroupName']").val();
            }

            if(typeof $(this).find("select[name='translationMappingName']").val() !== 'undefined'){
                translationMapping=  $(this).find("select[name='translationMappingName']").val();
            }

            if(typeof $(this).find("input[name='script']").val() !== 'undefined'){
                script=  $(this).find("input[name='script']").val();
            }

            if(typeof $(this).find("input:checkbox[name='acceptOnTimeout']").val() !== 'undefined'){
                acceptOnTimeout=  $(this).find("input:checkbox[name='acceptOnTimeout']").val();
            }

            if(!isEmpty(serverGroupName) || !isEmpty(translationMapping)){
                statefulProxySequentialData.push({'ruleset':ruleset,'serverGroupName':serverGroupName,'translationMappingName':translationMapping,'script':script,'acceptOnTimeout':acceptOnTimeout});
            }
        });
        if(statefulProxySequentialData.length >= 1){
            statefulProxySequentialHandlerAcctJson.push({'orderNumber':orderNumber,'isAdditional':isAdditional,'isHandlerEnabled':isHandlerEnabled,'handlerName':handlerName,'sequentialHandlerEntryData':statefulProxySequentialData});
        }else{
            statefulProxySequentialHandlerAcctJson = [];
        }
    });

    $('.form_statefulproxybroadcastcommunication').each(function(){

        var proxyTableObj = $(this).find('.statefulProxyBroadcastCommunicationTbl');
        var statefulProxyBroadcastData = [];
        var orderNumber = $(this).find('input[name=orderNumber]').val();
        var isAdditional = $(this).find('input[name=isAdditional]').val();
        var isHandlerEnabled=$(this).find('input:checkbox[name=isHandlerEnabled]').val();
        var handlerName = $(this).find('input[name=handlerName]').val();

        proxyTableObj.find('tr').each(function(){
            var ruleset,serverGroupName,translationMapping,script,acceptOnTimeout,waitForResponse = [];

            if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
                ruleset=  $(this).find("input[name='ruleset']").val();
            }

            if(typeof $(this).find("select[name='serverGroupName']").val() !== 'undefined'){
                serverGroupName= $(this).find("select[name='serverGroupName']").val();
            }

            if(typeof $(this).find("select[name='translationMappingName']").val() !== 'undefined'){
                translationMapping=  $(this).find("select[name='translationMappingName']").val();
            }

            if(typeof $(this).find("input[name='script']").val() !== 'undefined'){
                script=  $(this).find("input[name='script']").val();
            }

            if(typeof $(this).find("input:checkbox[name='acceptOnTimeout']").val() !== 'undefined'){
                acceptOnTimeout=  $(this).find("input:checkbox[name='acceptOnTimeout']").val();
            }

            if(typeof $(this).find("input:checkbox[name='waitForResponse']").val() !== 'undefined'){
                waitForResponse=  $(this).find("input:checkbox[name='waitForResponse']").val();
            }

            if(!isEmpty(serverGroupName) || !isEmpty(translationMapping)){
                statefulProxyBroadcastData.push({'ruleset':ruleset,'serverGroupName':serverGroupName,'translationMappingName':translationMapping,'script':script,'acceptOnTimeout':acceptOnTimeout,'waitForResponse':waitForResponse});
            }
        });
        if(statefulProxyBroadcastData.length >= 1){
            statefulProxyBroadcastHandlerAcctJson.push({'orderNumber':orderNumber,'isAdditional':isAdditional,'isHandlerEnabled':isHandlerEnabled,'handlerName':handlerName,'broadcastHandlerEntryData':statefulProxyBroadcastData});
        }else{
            statefulProxyBroadcastHandlerAcctJson = [];
        }
    });
		
	var prePluginDataMapping = [];
	var postPluginDataMapping = [];
	
	$('table#pre-acct-plugin-mapping-table > tbody > tr').each(function(){
		
		   var pluginName,pluginArgument;
		
		   if(typeof $(this).find("textarea[name='pluginArgument']").val() !== 'undefined'){
			   pluginArgument=  $(this).find("textarea[name='pluginArgument']").val();
		   }
		   
		   if(typeof $(this).find("input[name='pluginName']").val() !== 'undefined'){
			   pluginName=  $(this).find("input[name='pluginName']").val();
		   }
		   
		   if(  !isEmpty(pluginName) || !isEmpty(pluginArgument)){
			   prePluginDataMapping.push({'pluginName':pluginName,'pluginArgument':pluginArgument});
		   }
	});
	
	$('table#post-acct-plugin-mapping-table > tbody > tr').each(function(){
		   var pluginName,pluginArgument;
		
		   if(typeof $(this).find("textarea[name='pluginArgument']").val() !== 'undefined'){
			   pluginArgument=  $(this).find("textarea[name='pluginArgument']").val();
		   }
		   
		   if(typeof $(this).find("input[name='pluginName']").val() !== 'undefined'){
			   pluginName=  $(this).find("input[name='pluginName']").val();
		   }
		   
		   if( !isEmpty(pluginName) || !isEmpty(pluginArgument) ){
			   postPluginDataMapping.push({'pluginName':pluginName,'pluginArgument':pluginArgument});
		   }
	});
	
	
	$('#acctprofileLookupDriverJson').val(JSON.stringify(profileLookupDriverJson));
	$('#acctCDRGenerationJson').val(JSON.stringify(cdrGenerationJson));
	$('#acctPluginHandlerJson').val(JSON.stringify(pluginHandlerJson));
	$('#acctCOADMGenerationJson').val(JSON.stringify(coaDMGenerationJson));
	$('#acctProxyCommunicationJson').val(JSON.stringify(proxyCommunicationJson));
	$('#acctBroadcastCommunicationJson').val(JSON.stringify(broadcastCommunicationJson));
    $('#statefulProxySequentialHandlerAcctJson').val(JSON.stringify(statefulProxySequentialHandlerAcctJson));
    $('#statefulProxyBroadcastHandlerAcctJson').val(JSON.stringify(statefulProxyBroadcastHandlerAcctJson));
	
	/* Plugin Mapping Data */
	$('#acctPrePluginJson').val(JSON.stringify(prePluginDataMapping));
	$('#acctPostPluginJson').val(JSON.stringify(postPluginDataMapping));
	
	if( !validateProfileLookupHandler() ){
		return false;
	}else if( !validateProxyCommunicationHandler() ){
		return false;
	}else if( !validateBroadCastHandler()){
		return false; 
	}else if( !validateCOADMHandler() ){
		return false;	
	}else if( !validatePluginHandler() ){
		return false;
	}else if( !validateHandlerName() ){
		return false;		
	}else if( !validateCDRHandler() ){
		return false;
	}else if( !validateStatefulProxySequentialHandler()){
        return false;
    }else if( !validateStatefulProxyBroadcastHandler()){
        return false;
    }else{
		document.forms['authForm'].submit();
	}
}

function setStatefulProxyHandlerDropDown(obj) {
    $(obj).css({'border':'1px solid #c0c0c0'});
}

function validatePluginHandler(){
    var valueToReturn = true;
    var noValueConfigured = false;
    var isValueConfigured = false;

    $('.form_pluginHandler').each(function(){
        var plugintable = $(this).find('.mappingtblplugin');

        var idOfTable = $(plugintable).attr('id');
        var rowCount = $('#'+idOfTable+' tr').length;
        if( rowCount == 1){
            noValueConfigured= true;
        }else if(rowCount > 1){
            isValueConfigured = true;
        }

        if(noValueConfigured){
            $(this).children('table').css({'border':'1px solid red'});
            valueToReturn = false;
            alert("Atleast one mapping is required in Plugin Handler");
            var addMappingButton = $(this).find('.light-btn');
            $(addMappingButton).focus();
        } else if(isValueConfigured){
				$(plugintable).find("input[name='pluginName']").each(function(){
                var pluginValue = $(this).val();
                if( pluginValue.length > 0 ){
                    valueToReturn = true;
                }else{
                    alert('Plugin Name must be specified');
                    $(this).focus();
                       valueToReturn=false;
                    return false;
                }
            });
        }
    });

    return valueToReturn;
}

function validateProfileLookupHandler(){
	var valueToReturn = true;
	$('.form_profilelookup').each(function(){
		var selectbox= $(this).find("select[name='selecteddriverIds']");
		var addButton = $(this).find('.driver-popup');
		if($(selectbox).val() == null){
			alert('Select at least one primary driver for policy');
			$(addButton).focus();
			valueToReturn = false;
			return false;
		}
	});
	return valueToReturn;
}

function validateCOADMHandler(){
	var noValueConfigured = false;
	var isValueConfigured = false;
	var valueToReturn = true;
	$('.form_coaDMGeneretaion').each(function(){
		var coaDMTable = $(this).find('.coaDmGenerationtbl');
		var coaDMmainTable = $(this).find('.tblmCOADMGeneration');
		var idofTable = $(coaDMTable).attr('id');
		var rowCount = $('#'+idofTable+' tr').length;
		if( rowCount == 1){
			noValueConfigured= true;
		}else if(rowCount > 1){
			isValueConfigured = true;
		}
		
	 	var throwErrorMsg = false;
		var skipFirstStep = false;
		 	
		if(noValueConfigured){
			$(coaDMmainTable).css({'border':'1px solid red'});
			alert('Atleast one Mapping is Required COA/DM Handler');
			var addMappingButton = $(this).find('.coa-dm-btn');
			$(addMappingButton).focus();
			throwErrorMsg=true;
			valueToReturn=false;
			return false;
		}else if(isValueConfigured){
				coaDMTable.find('tr').each(function(){
			 
				$(coaDMTable).css({'border':'1px solid #c0c0c0'});
				
				if(!skipFirstStep){
			       skipFirstStep=true;
			    }else{
			    	 var $tds = $(this).find('td');
			    	 var packetType = '';
			    	 
			    	 if(typeof $($tds).find("select[name='packetType']").val() !== 'undefined'){
			    	    packetType = $($tds).find("select:first").val();
				     }
			    	 
			    	 if(!isEmpty(packetType)){
					   	if(packetType == 0){
					    	$($($tds).find("select[name='packetType']")).css({'border':'1px solid red'});
							alert('Please Select at least one packet type from drop down');
							$($($tds).find("select[name='packetType']")).focus();
							valueToReturn=false;
							throwErrorMsg=true;
							return false;
					     }
					 }
			       }
			  }); 
				
			if(throwErrorMsg){
				return false;
			}
		}
	});
	return valueToReturn; 
} 


function validateProxyCommunicationHandler(){
	var noValueConfigured = false;
	var isValueConfigured = false;
	var valueToReturn = true;
	$('.form_proxycommunication').each(function(){
		var proxyTableObj = $(this).find('.proxyCommunicationTbl');
		var proxyMaintableObj = $(this).find('.tblmProxyCommunication');
		var idofTable = $(proxyTableObj).attr('id');
		var rowCount = $('#'+idofTable+' tr').length;
		if( rowCount == 1){
			noValueConfigured= true;
		}else if(rowCount > 1){
			isValueConfigured = true;
		}
		
		var isErrorFound = false;
		
		if(noValueConfigured){
			$(proxyMaintableObj).css({'border':'1px solid red'});
			alert('Atleast one Mapping is Required in Proxy Communication Handler');
			var addMappingButton = $(this).find('.proxy-com-btn');
			$(addMappingButton).focus();
			valueToReturn=false;
			isErrorFound=true;
			return false;
		}else if(isValueConfigured){
			 proxyTableObj.find('tr').each(function(){
			   
			 var configuredESIIdsList = [];  
			 var innerTableObj = $(this).find('.proxyCommServer');
			 	 var skipFirstStep = false;
			 	 var moreRowsFound = false;
			 	 var throwErrorMsg = false;
			 	 $(innerTableObj).css({'border':'1px solid #c0c0c0'});
				 innerTableObj.find('tr').each(function (i, el) {
				        if(!skipFirstStep){
				        	skipFirstStep=true;
				        }else{
				        	  var $tds = $(this).find('td');
						      var serverId = '';
						      
						      if(typeof $($tds).find("select[name='esiId']").val() !== 'undefined'){
						        	serverId = $($tds).find("select:first").val();
						        	var esiTypeId = getFirstESITypeId(serverId);
						        	configuredESIIdsList.push(esiTypeId);
						      }
						        
						      if(!isEmpty(serverId)){
						       	if(serverId == 0){
						       		throwErrorMsg=true;
						       		valueToReturn=false;
						        }
						      }
						      moreRowsFound=true;
				        }
				    });
				 if(throwErrorMsg){
					 	throwErrorMsg=true;
					 	$(innerTableObj).css({'border':'1px solid red'});
			       		alert('Please Select at least one ESI from drop down');
			       		isErrorFound=true;
			       		return false;
				 }else if (skipFirstStep && moreRowsFound == false && valueToReturn == true){
					    alert('Aleast one esi mapping is required in Proxy(Sequential) handler.');
			       		valueToReturn=false;
			       		isErrorFound=true;
			    		return false;
				 }else{
					 var checkForSameESIType = configuredESIIdsList.allValuesSame();
					 if(!checkForSameESIType){
						 $(innerTableObj).css({'border':'1px solid red'});
				       	 alert('Only Same type of esi would be allowed in a single group.');
				       	 valueToReturn=false;
				       	 isErrorFound=true;
				       	 return false;
					 }
				 }
			}); 
		}
		if(isErrorFound){
			return false;
		}
	});
	return valueToReturn; 
}
//check for all values are same or not
Array.prototype.allValuesSame = function() {

    for(var i = 1; i < this.length; i++){
        if(this[i] !== this[0])
            return false;
    }

    return true;
}

function validateBroadCastHandler(){
	var noValueConfigured = false;
	var isValueConfigured = false;
	var valueToReturn = true;
	$('.form_broadcastcommunication').each(function(){
		var broadcastTableObj = $(this).find('.broadcastCommunicationTbl');
		var broadcastMaintableObj = $(this).find('.tblmBroadCastCommunication');
		var idofTable = $(broadcastTableObj).attr('id');
		var rowCount = $('#'+idofTable+' tr').length;
		if( rowCount == 1){
			noValueConfigured= true;
		}else if(rowCount > 1){
			isValueConfigured = true;
		}
		
		var isErrorFound = false;
		
		if(noValueConfigured){
			$(broadcastMaintableObj).css({'border':'1px solid red'});
			alert('Atleast one Mapping is Required in Broadcasting(Parallel) Communication Handler');
			var addMappingButton = $(this).find('.broadcast-com-btn');
			$(addMappingButton).focus();
			valueToReturn=false;
			isErrorFound=true;
			return false;
		}else if(isValueConfigured){
			 broadcastTableObj.find('tr').each(function(){
			   
			 var configuredESIIdsList = [];  
			 var innerTableObj = $(this).find('.broadcastServer');
			 	 var skipFirstStep = false;
			 	 var moreRowsFound = false;
			 	 var throwErrorMsg = false;
			 	 $(innerTableObj).css({'border':'1px solid #c0c0c0'});
				 innerTableObj.find('tr').each(function (i, el) {
				        if(!skipFirstStep){
				        	skipFirstStep=true;
				        }else{
				        	  var $tds = $(this).find('td');
						      var serverId = '';
						      
						      if(typeof $($tds).find("select[name='esiId']").val() !== 'undefined'){
						        	serverId = $($tds).find("select:first").val();
						        	var esiTypeId = getFirstESITypeId(serverId);
						        	configuredESIIdsList.push(esiTypeId);
						      }
						        
						      if(!isEmpty(serverId)){
						       	if(serverId == 0){
						       		throwErrorMsg=true;
						       		valueToReturn=false;
						        }
						      }
						      moreRowsFound=true;
				        }
				    });
				 if(throwErrorMsg){
					 	throwErrorMsg=true;
					 	$(innerTableObj).css({'border':'1px solid red'});
			       		alert('Please Select at least one ESI from drop down');
			       		isErrorFound=true;
			       		return false;
				 }else if (skipFirstStep && moreRowsFound == false && valueToReturn == true){
					    alert('Aleast one esi mapping is required in Broadcasting(Parallel) handler.');
			       		valueToReturn=false;
			       		isErrorFound=true;
			    		return false;
				 }else{
					 var checkForSameESIType = configuredESIIdsList.allValuesSame();
					 if(!checkForSameESIType){
						 $(innerTableObj).css({'border':'1px solid red'});
				       	 alert('Only Same type of esi would be allowed in a single group.');
				       	 valueToReturn=false;
				       	 return false;
					 }
				 }
			}); 
		}
		
		if(isErrorFound){
			return false;
		}
		
	});
	return valueToReturn; 
}
function selectAll(){
	
	$('.form_profilelookup').each(function(){
		
		 var selecteddriverIds = $("select[name = 'selecteddriverIds']");
		 $(selecteddriverIds).find("option").each(function(){
			 $(this).attr('selected','selected');
		 });
		 
		 var selectedCacheDriverIds = $("select[name = 'selectedCacheDriverIds']");
		 $(selectedCacheDriverIds).find("option").each(function(){
			 $(this).attr('selected','selected');
		 });
         
		 var selectedAdditionalDriverIds = $("select[name = 'selectedAdditionalDriverIds']");
		 $(selectedAdditionalDriverIds).find("option").each(function(){
			 $(this).attr('selected','selected');
		 });
	});
}

/* This function used for convert plugin Alias to Name (Plugin Handler) */

function convertPrePluginAliasToName(pluginString,tableName){
	if(tableName == 'main-parent-table-class'){
		var stringArray = pluginString.split(","); 
		var finalResult = '';
		$.each(stringArray, function( index, value ) {
			

			var isAttributeFound = false;
			jQuery.map(prePluginList, function(obj) {
			    if(obj.value === value){
			    	if(finalResult.length == 0){
			    		finalResult =obj.label;
			    	}else{
			    		finalResult=finalResult+","+obj.label;
			    	}
			    	isAttributeFound = true;
			    }
			});
			
			if(isAttributeFound == false){
				if(finalResult.length == 0){
		    		finalResult =value;
		    	}else{
		    		finalResult=finalResult+","+value;
		    	}
			}
		});
		return finalResult;
	}else{
		var stringArray = pluginString.split(","); 
		var finalResult = '';
		$.each(stringArray, function( index, value ) {
			
			var isAttributeFound = false;
			jQuery.map(postPluginList, function(obj) {
			    if(obj.value === value){
			    	if(finalResult.length == 0){
			    		finalResult =obj.label;
			    	}else{
			    		finalResult=finalResult+","+obj.label;
			    	}
			    	isAttributeFound = true;
			    }
			});
			
			if(isAttributeFound == false){
				if(finalResult.length == 0){
		    		finalResult =value;
		    	}else{
		    		finalResult=finalResult+","+value;
		    	}
			}
		});
		return finalResult;
	}
}

setTitle('<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.acctflow"/>');
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<html:form action="/updateRadiusServicePolicyAcctServiceFlow" styleId="authForm">
		<html:hidden name="updateRadiusServicePolicyForm" styleId="radiusPolicyId" property="radiusPolicyId" value="<%=radiusServicePolicyData.getRadiusPolicyId()%>" />
		<html:hidden name="updateRadiusServicePolicyForm" styleId="action" property="action" value="update"/>
		<html:hidden name="updateRadiusServicePolicyForm" styleId="auditUid" property="auditUid"/>
		<html:hidden name="updateRadiusServicePolicyForm" styleId="radiusPolicyXml" property="radiusPolicyXml"/>
		
		<html:hidden property="acctCDRGenerationJson" value="" styleId="acctCDRGenerationJson"/>
		<html:hidden property="acctPluginHandlerJson" value="" styleId="acctPluginHandlerJson"/>
		<html:hidden property="acctCOADMGenerationJson" value="" styleId="acctCOADMGenerationJson"/>
		<html:hidden property="acctProxyCommunicationJson" value="" styleId="acctProxyCommunicationJson"/>
		<html:hidden property="acctBroadcastCommunicationJson" value="" styleId="acctBroadcastCommunicationJson"/>
		<html:hidden property="acctprofileLookupDriverJson" value="" styleId="acctprofileLookupDriverJson"/>
		<html:hidden property="statefulProxySequentialHandlerAcctJson" value="" styleId="statefulProxySequentialHandlerAcctJson" />
        <html:hidden property="statefulProxyBroadcastHandlerAcctJson" value="" styleId="statefulProxyBroadcastHandlerAcctJson" />
		
		<html:hidden property="acctPrePluginJson" value="" styleId="acctPrePluginJson" />
		<html:hidden property="acctPostPluginJson" value="" styleId="acctPostPluginJson" />
		
		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
					<tr>
						<td align="left"  valign="top" width="30%" colspan="6">
							<table width="100%" cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td width="100%" class="box">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td class="tbl-header-bold" valign="top" style="padding-left: 5px;">
													<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.preplugin" />
													<ec:elitehelp   header="radiusservicepolicy.preplugin" headerBundle="servicePolicyProperties" text="radiusservicepolicy.acctpreplugin.maindetails" ></ec:elitehelp>
												</td>
											</tr>
											<tr>
												<td style="padding: 10px;">
													<table id="prePluginTbl" class="prePluginTbl" cellspacing="0" cellpadding="0" width="70%">
														<tr>
															<td class="captiontext" valign="top" colspan="2">
																<input type="button" value="Add Plugin" onClick="addPluginMapping('pre-acct-plugin-mapping-table','pre-acct-plugin-mapping-template');" class="light-btn" style="" tabindex="2" /><br />
															</td>
														</tr>
														<tr>
															<td  class="captiontext" valign="top">
																<table cellspacing="0" cellpadding="0" border="0" width="100%" id="pre-acct-plugin-mapping-table" class="pre-acct-plugin-mapping-table">
																	<tr>
																		<td class="tbl-header-bold" width="47.5%">
																			<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.preplugin" />
																			<ec:elitehelp   header="radiusservicepolicy.preplugin" headerBundle="servicePolicyProperties" text="radiusservicepolicy.preplugin" ></ec:elitehelp>
																		</td>	
																		<td class="tbl-header-bold" width="47.5%">
																			<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.pluginarguments" />
																			<ec:elitehelp   header="radiusservicepolicy.prepluginlist" headerBundle="servicePolicyProperties" text="radiusservicepolicy.prepluginlist" ></ec:elitehelp>
																		</td>	
																		<td class="tbl-header-bold" width="5%">
																			Remove
																		</td>	
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td style="line-height: 15px;">&nbsp;</td>
								</tr>
								<tr>
									<td  width="100%" class="box">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td colspan="3" class="tbl-header-bold" valign="top" style="padding-left: 5px;">
													<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.serviceflow" />
													<ec:elitehelp   header="radiusservicepolicy.serviceflow" headerBundle="servicePolicyProperties" text="radiusservicepolicy.acctserviceflow.maindetails" ></ec:elitehelp>
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding-top: 10px;padding-left: 10px;">
													<input type="button" value="Add Service Handler" onClick="servicehandlerpopup('accountingTbl')" class="light-btn addServiceHandler" style="" tabindex="2" /><br />
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding: 10px;">
													<table id="accountingTbl" class="accountingTbl main-parent-table-class" cellspacing="0" cellpadding="0" width="100%">
														<tbody class="parent sortableClass">
														</tbody>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td style="line-height: 15px;">&nbsp;</td>
								</tr>
								<tr>
									<td  width="100%" class="box">
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td colspan="3" class="tbl-header-bold" valign="top" style="padding-left: 5px;">
													<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.postplugin" />
													<ec:elitehelp   header="radiusservicepolicy.serviceflow" headerBundle="servicePolicyProperties" text="radiusservicepolicy.acctpostplugin.maindetails" ></ec:elitehelp>
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding: 10px;">
													<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
															<tr>
																<td class="captiontext" valign="top" colspan="2">
																	<input type="button" value="Add Plugin" onClick="addPluginMapping('post-acct-plugin-mapping-table','post-acct-plugin-mapping-template');" class="light-btn" style="" tabindex="2" /><br />
																</td>
															</tr>
															<tr>
																<td  class="captiontext" valign="top">
																	<table cellspacing="0" cellpadding="0" border="0" width="100%" id="post-acct-plugin-mapping-table" class="post-acct-plugin-mapping-table">
																		<tr>
																			<td class="tbl-header-bold" width="47.5%">
																				<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.postplugin" />
																				<ec:elitehelp   header="radiusservicepolicy.postplugin" headerBundle="servicePolicyProperties" text="radiusservicepolicy.postplugin" ></ec:elitehelp>
																			</td>	
																			<td class="tbl-header-bold" width="47.5%">
																				<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.pluginarguments" />
																				<ec:elitehelp   header="radiusservicepolicy.prepluginlist" headerBundle="servicePolicyProperties" text="radiusservicepolicy.prepluginlist" ></ec:elitehelp>
																			</td>	
																			<td class="tbl-header-bold" width="5%">
																				Remove
																			</td>	
																		</tr>
																	</table>
																</td>
															</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td style="line-height: 15px;">&nbsp;</td>
								</tr>
								<tr>
									<td  width="100%" class="box" >
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td colspan="3" class="tbl-header-bold" valign="top" style="padding-left: 5px;">
													<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.postresponseserviceflow" />
													<ec:elitehelp   header="radiusservicepolicy.serviceflow" headerBundle="servicePolicyProperties" text="radiusservicepolicy.acctpostresponseservice.maindetails" ></ec:elitehelp>
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding-top: 10px;padding-left: 10px;">
													<input type="button" value="Add Service Handler" onClick="servicehandlerpopup('additionalTbl')" class="light-btn" style="" tabindex="2" /><br />
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding: 10px;">
													<table id="additionalTbl" class="additionalTbl main-parent-table-class" cellspacing="0" cellpadding="0" width="100%">
														<tbody class="parent sortableClassAdditional">
														</tbody>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3" align="left" style="padding-left: 290px;">
										<input type="button" value="Update " class="light-btn" onclick="validate()" tabindex="8" />
										<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewRadiusServicePolicy.do?radiusPolicyId=<%=radiusServicePolicyData.getRadiusPolicyId()%>'" value="Cancel" class="light-btn" tabindex="9">
									</td>
								</tr> 
							</table>
						</td>
					</tr>
					<tr>
						<td style="line-height: 15px;">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
</table>
<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>
</html:form>
<%@include file="AccountingHandlerTemplate.jsp" %>
<%@include file="PostResponseHandlerTemplate.jsp" %>
<div id="driverDiv" style="display: none;" class="driverDiv" title="Create Driver">
	<iframe id='contentIframe' src="<%=basePath%>/initCreateDriver.do?iframeContent=true" style="border: none;height: 100%;width: 100%;"></iframe> 
</div>
<table class="pre-acct-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="pluginName" class="noborder" style="width:100%;" onfocus="setAutoCompleteDataAuthPluginData(this);"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="pluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"></textarea>
		</td>	
		<td class="tblrows" width="5%" align="center">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
		</td>	
	</tr>
</table>

<table class="post-acct-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="pluginName" class="noborder" style="width:100%;" onfocus="setAutoCompleteDataAuthPluginData(this);"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="pluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"></textarea>
		</td>	
		<td class="tblrows" width="5%" align="center">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
		</td>	
	</tr>
</table>
<script>
	setTitle('<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.acctflow"/>');
</script>
