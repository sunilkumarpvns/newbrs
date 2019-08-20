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

<%
	RadServicePolicyData radiusServicePolicyData  = (RadServicePolicyData) request.getAttribute("radServicePolicyData");
	List<PluginInstData> prePluginList = (List<PluginInstData>)request.getAttribute("prePluginList");
	
	String sessionManagerParam =  radiusServicePolicyData.getSessionManagerId();
	
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
		acctDriverInstanceIds[i] = String.valueOf(data.getName());
	}

%>
<style>
	#sortableClass { list-style-type: none; margin: 0; padding: 0; width: 100%;padding-right: 20px; }
	#sortableClass li { background-color: white;padding-bottom: 10px;padding-left: 10px;padding-right: 10px;}
	#sortableClassAdditional { list-style-type: none; margin: 0; padding: 0; width: 100%;padding-right: 20px;}
	#sortableClassAdditional li { background-color: white;padding-bottom: 10px;padding-left: 10px;padding-right: 10px;}
	.handler-css{}
</style>
<script src="<%=request.getContextPath()%>/js/datatable/jquery-ui.js" /> 
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script src="<%=request.getContextPath()%>/js/servicepolicy/proxycommunication-update.js"></script> 
<script language="javascript" src="<%=request.getContextPath()%>/js/servicepolicy/cdr-mapping-table.js"></script>
<script src="<%=request.getContextPath()%>/js/servicepolicy/radius-service-policy-view-authentication.js"></script>
<script language="javascript"  src="<%=request.getContextPath()%>/js/servicepolicy/radiusservicepolicy/radius-service-policy.js"></script> 
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/togglebutton.css" />
<script type="text/javascript">
var prePluginList = [];

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

	/* Get pre plugin list*/
	<%for(PluginInstData pluginTypeData:prePluginList){%>
		prePluginList.push({"id":"<%=pluginTypeData.getPluginInstanceId()%>","value":"<%=pluginTypeData.getPluginTypeName()%>","label":"<%=pluginTypeData.getName()%>"});
	<%}%>
	
	/* Setting Pre and Post Plugin Data List */
	setPluginDatas(getAuthPrePluginData(), 'pre-plugin-mapping-table', 'pre-plugin-mapping-template');
	setPluginDatas(getAuthPostPluginData(), 'post-plugin-mapping-table', 'post-plugin-mapping-template');
	
	getPrimaryServerData();
	setBroadcastServerOptions(getBroadcastServerOptions());
	setproxyESIServerData(getProxyCommunicationData());
	
	//For ==> Update Handler data
	setAuthenticationHandler(readAuthenticationHandler());
	setAuthorizationHandler(readAuthorizationHandler());
	setProfileLookupDriver(readProfileLookupDriver()); 
	setConcurrencyHandler(readConcurrencyHandler());
	setPluginHandler(readPluginHandler());
	setProxyCommunicationHandler(readProxyCommunicationHandler());
	setBroadcastCommunicationHandler(readBroadcastCommunicationHandler());
	setCDRGenerationHandler(readCDRGenerationHandler());
	setCoaDMGenerationHandler(readCOADMGenerationHandler());
    setConcurrencyIMDGHandler(readConcurrencyIMDGHandler());
    setStatefulProxySequentialHandler(readStatefulProxySequentialHandler());
    setStatefulProxyBroadcastHandler(readStatefulProxyBroadcastHandler());

	/* END */
	 
	$('.is-handler-enabled').live('click',function(){
		if($(this).val() == "true"){
			var $div = $(this).closest('table[class^="handler-class"]');
   	    	$($div).css({'background-color':'white !important;'});
		}else{
			var $div = $(this).closest('table[class^="handler-class"]');
   	    	$($div).css({'background-color':'#f1f1f1 !important;'});
		}
	});
	
	retriveRadiusDictionaryAttributesForUsernameResponse();
	$('#authPrePluginJson').attr("readonly","readonly");
	$('#authPostPluginJson').attr("readonly","readonly");
});

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
	}
}

function getAuthPrePluginData(){
	var authPrePluginDataList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authPrePluginData">
		authPrePluginDataList.push({'pluginName':'<bean:write name="obj" property="pluginName"/>','pluginArgument':'<bean:write name="obj" property="pluginArgument"/>'});
	</logic:iterate>
	return authPrePluginDataList;
}

function getAuthPostPluginData(){
	 var authPostPluginDataList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authPostPluginData">
		authPostPluginDataList.push({'pluginName':'<bean:write name="obj" property="pluginName"/>','pluginArgument':'<bean:write name="obj" property="pluginArgument"/>'});
	</logic:iterate>
	return authPostPluginDataList; 
}

/* This function is required for setting ESI Server list */
function getBroadcastServerOptions(){
	var broadcastServerOptions = [] ;
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authBroadcastServerList">
		var obj = {};
		obj.value = '<bean:write name="obj" property="esiInstanceId"/>';
		obj.name =  '<bean:write name="obj" property="name"/>';
		broadcastServerOptions.push(obj);
	</logic:iterate>
	return broadcastServerOptions;
}

function readProfileLookupDriver(){
	var profileLookUpDriver = [] ;
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="profileLookupList">
		
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

function readAuthenticationHandler(){
	var authenticationHandlerList = [] ;
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authHandlerList">
	var authenticationData = {'selectedAuthMethodTypes':'<bean:write name="obj" property="selectedAuthMethodTypes"/>',
					'eapConfigId':'<bean:write name="obj" property="eapConfigId"/>',
				   'digestConfigId':'<bean:write name="obj" property="digestConfigId"/>',
				   'userName':'<bean:write name="obj" property="userName"/>',
				   'userNameResponseAttribs':'<bean:write name="obj" property="userNameResponseAttribs"/>',
				   'userNameExpression':'<bean:write name="obj" property="userNameExpression"/>',
				   'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
				   'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
				   'handlerName':'<bean:write name="obj" property="handlerName"/>',
				   'isAdditional':'<bean:write name="obj" property="isAdditional"/>'
				  };
		authenticationHandlerList.push(authenticationData);
	</logic:iterate>
	
	return authenticationHandlerList;
}

function readAuthorizationHandler(){
	var authorizationHandlerList = [] ;
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authorizationList">
	var authorizationData = {'wimaxEnabled':'<bean:write name="obj" property="wimaxEnabled"/>',
					'threeGPPEnabled':'<bean:write name="obj" property="threeGPPEnabled"/>',
				   'defaultSessionTimeout':'<bean:write name="obj" property="defaultSessionTimeout"/>',
				   'rejectOnCheckItemNotFound':'<bean:write name="obj" property="rejectOnCheckItemNotFound"/>',
				   'rejectOnRejectItemNotFound':'<bean:write name="obj" property="rejectOnRejectItemNotFound"/>',
				   'actionOnPolicyNotFound':'<bean:write name="obj" property="actionOnPolicyNotFound"/>',
				   'gracePolicyId':'<bean:write name="obj" property="gracePolicyId"/>',
				   'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
				   'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
				   'handlerName':'<bean:write name="obj" property="handlerName"/>',
				   'isAdditional':'<bean:write name="obj" property="isAdditional"/>'
				  };
		authorizationHandlerList.push(authorizationData);
	</logic:iterate>
	return authorizationHandlerList;
}
function readConcurrencyHandler(){
	var concurrencyHandlerList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="concurrencyHandlerList">
	var concurrencyHandlerData = {'ruleset':'<bean:write name="obj" property="ruleset"/>','sessionManagerId':'<bean:write name="obj" property="sessionManagerId"/>',
				   'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
				   'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
				   'handlerName':'<bean:write name="obj" property="handlerName"/>',
				   'isAdditional':'<bean:write name="obj" property="isAdditional"/>'
				  };
	concurrencyHandlerList.push(concurrencyHandlerData);
	</logic:iterate>
	return concurrencyHandlerList;
}

function readPluginHandler(){
	var pluginHandlerList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authPluginHandlerList">
			var pluginDetails = [];
		    <logic:iterate id="pluginData" name="obj" property="lstPluginDetails">
				pluginDetails.push({'requestType':'<bean:write name="pluginData" property="requestType"/>','pluginName':'<bean:write name="pluginData" property="pluginName"/>','pluginArgument':'<bean:write name="pluginData" property="pluginArgument"/>','ruleset':'<bean:write name="pluginData" property="ruleset"/>'});
			</logic:iterate>
			var pluginHandlerData = {'pluginDetails':pluginDetails,'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
					  				'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
					  				'handlerName':'<bean:write name="obj" property="handlerName"/>',
									'isAdditional':'<bean:write name="obj" property="isAdditional"/>'};
		pluginHandlerList.push(pluginHandlerData);
	</logic:iterate>
	return pluginHandlerList;
}

function readProxyCommunicationHandler(){
	var proxyCommunicationList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authProxyCommunicationList">
			var proxyDetails = [];
		    <logic:iterate id="proxyCommData" name="obj" property="proxyCommunicationList">
		   		var esiList =[];
		   	  	<logic:iterate id="esiData" name="proxyCommData" property="esiListData">
		   	 		esiList.push({'esiId':'<bean:write name="esiData" property="esiId"/>','loadFactor':'<bean:write name="esiData" property="loadFactor"/>'});
		   	  	</logic:iterate>
		    	proxyDetails.push({'ruleset':'<bean:write name="proxyCommData" property="ruleset"/>','translationMappingName':'<bean:write name="proxyCommData" property="translationMappingName"/>','script':'<bean:write name="proxyCommData" property="script"/>','acceptOnTimeout':'<bean:write name="proxyCommData" property="acceptOnTimeout"/>','esiListData':esiList});
			</logic:iterate>
			var proxyCommunicationHandlerData = {'proxyCommunicationList':proxyDetails,'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
					'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
					'handlerName':'<bean:write name="obj" property="handlerName"/>',
					'isAdditional':'<bean:write name="obj" property="isAdditional"/>'};
			proxyCommunicationList.push(proxyCommunicationHandlerData);
	</logic:iterate>
	return proxyCommunicationList;
}

function readBroadcastCommunicationHandler(){
	var broadcastCommunicationList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authBroadcastCommunicationList">
			var broadcastDetails = [];
		    <logic:iterate id="broadcastCommData" name="obj" property="broadcastCommunicationList">
		   		var esiList =[];
		   	  	<logic:iterate id="esiData" name="broadcastCommData" property="esiListData">
		   	 		esiList.push({'esiId':'<bean:write name="esiData" property="esiId"/>','loadFactor':'<bean:write name="esiData" property="loadFactor"/>'});
		   	  	</logic:iterate>
		   	 	broadcastDetails.push({'ruleset':'<bean:write name="broadcastCommData" property="ruleset"/>','translationMappingName':'<bean:write name="broadcastCommData" property="translationMappingName"/>','script':'<bean:write name="broadcastCommData" property="script"/>','acceptOnTimeout':'<bean:write name="broadcastCommData" property="acceptOnTimeout"/>','waitForResponse':'<bean:write name="broadcastCommData" property="waitForResponse"/>','esiListData':esiList});
			</logic:iterate>
			var broadcastCommunicationHandlerData = {'broadcastCommunicationList':broadcastDetails,'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
					  'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
					  'handlerName':'<bean:write name="obj" property="handlerName"/>',
					  'isAdditional':'<bean:write name="obj" property="isAdditional"/>'};
			broadcastCommunicationList.push(broadcastCommunicationHandlerData);
	</logic:iterate>
	return broadcastCommunicationList;
}

function readCDRGenerationHandler(){
	var cdrGenerationHandlerList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authCDRGenerationList">
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
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authCOADMGenList">
		var coaDmDetails = [];
	    <logic:iterate id="coaDMGenerationData" name="obj" property="coaDMGenerationDetailList">
	   		 coaDmDetails.push({'ruleset':'<bean:write name="coaDMGenerationData" property="ruleset"/>','packetType':'<bean:write name="coaDMGenerationData" property="packetType"/>','translationMapping':'<bean:write name="coaDMGenerationData" property="translationMapping"/>'});
		</logic:iterate>
		var coaDMHandlerData = {'coaDMGenerationDetailList':coaDmDetails,'orderNumber':'<bean:write name="obj" property="orderNumber"/>','isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>','handlerName':'<bean:write name="obj" property="handlerName"/>','isAdditional':'<bean:write name="obj" property="isAdditional"/>','scheduleAfterInMillis':'<bean:write name="obj" property="scheduleAfterInMillis"/>'};
		coaDMGenerationList.push(coaDMHandlerData);
	</logic:iterate>
	return coaDMGenerationList;
}

function readConcurrencyIMDGHandler(){
    var conImdgHandlerList = [];
    <logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="concurrencyIMDGHandlerList">
    var concurrencyIMDGHandlerData = {'ruleset':'<bean:write name="obj" property="ruleset"/>','imdgFieldName':'<bean:write name="obj" property="imdgFieldName"/>',
        'orderNumber':'<bean:write name="obj" property="orderNumber"/>',
        'isHandlerEnabled':'<bean:write name="obj" property="isHandlerEnabled"/>',
        'handlerName':'<bean:write name="obj" property="handlerName"/>',
        'isAdditional':'<bean:write name="obj" property="isAdditional"/>'
    };
    conImdgHandlerList.push(concurrencyIMDGHandlerData);
    </logic:iterate>
    return conImdgHandlerList;
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
    <logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="statefulProxyBroadcastHandlerAuthList">
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
				prePluginList, extractLastDbFields( request.term ) ) );
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
				prePluginList, extractLastDbFields( request.term ) ) );
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
					prePluginList, extractLastDbFields( request.term ) ) );
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
					prePluginList, extractLastDbFields( request.term ) ) );
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
function setPluginData(pluginData){
	var pluginValue=$(pluginData).val();
	pluginValue=pluginValue.trim();
	var lastChar = pluginValue.charAt(pluginValue.length - 1);
	if(lastChar == ","){
		var result = pluginValue.substring(0, pluginValue.length-1);
		$(pluginData).val(result);
	}
}

var proxyESIServerData;
function setproxyESIServerData(proxyESIServerData){
	this.proxyESIServerData = proxyESIServerData;
}

function getProxyCommunicationData(){
	var proxyList = [];
	<logic:iterate id="obj" name="updateRadiusServicePolicyForm" property="authBroadcastServerList" type="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData">
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
			if($.inArray(item.id,proxyListIdNameArray) < 0 ){
				$(selectBoxObj).append("<option value='" + item.id + "'>" + item.name + "</option>");
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
					$(selectObj).append("<option value='" + item.id + "'>" + item.name + "</option>");
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
					$(selectObj).append("<option value='" + item.name + "'>" + item.name + "</option>");
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
	"<td align='left' class='top-border labeltext' valign='top' width='55%' id='tbl_attrid'>"+
	"<select name='esiId' class='noborder' style='width:100%;' onchange='setProxyHandlerESIDropDown(this);' disabled='disabled'>"+
	"</select></td>"+
	"<td align='left' class='top-border labeltext' valign='top' width='40%'>"+
	"<select  id='loadFactor' name='loadFactor' class='noborder' style='width:100%;' disabled='disabled'><option value='0'>0</option><option selected='selected' value='1'>1</option><option value='2'>2</option><option value='3'>3</option><option value='4'>4</option><option value='5'>5</option><option value='6'>6</option><option value='7'>7</option><option value='8'>8</option><option value='9'>9</option><option value='10'>10</option></select></td>"+
	"<td align='center' class='top-border labeltext' valign='top' width='5%'><span class='delete remove-proxy-server'/>&nbsp;</td></tr>";
	$(tableObj).append(htmlSource);
	addProxyDropDown(obj);
}

function addESIData(tableObj,esiId,loadFactor){
	var htmlSource="<tr>"+
	"<td align='left' class='top-border labeltext' valign='top' width='20%' id='tbl_attrid'>"+
	"<select name='esiId' class='noborder' style='width:100%;' onchange='setProxyHandlerESIDropDown(this);' disabled='disabled'><option value='0'>--Select--</option>"+
	"<logic:iterate id='obj' name='updateRadiusServicePolicyForm' property='authBroadcastServerList' type='com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData'>"+
	"<option value='<bean:write name='obj' property='name'/>'><bean:write name='obj' property='name'/></option></logic:iterate></select></td>"+
	"<td align='left' class='top-border labeltext' valign='top' width='12%'>"+
	"<select  id='loadFactor' name='loadFactor' class='noborder' style='width:100%;' disabled='disabled'><option value='0'>0</option><option selected='selected' value='1'>1</option><option value='2'>2</option><option value='3'>3</option><option value='4'>4</option><option value='5'>5</option><option value='6'>6</option><option value='7'>7</option><option value='8'>8</option><option value='9'>9</option><option value='10'>10</option></select></td>"+
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
	authHandlerJson = [];
	profileLookupDriverJson = [];
	authorizationHandlerJson = [];
	concurrencyHandlerJson = [];
	cdrGenerationJson = [];
	pluginHandlerJson = [];
	coaDMGenerationJson = [];
	proxyCommunicationJson = [];
	broadcastCommunicationJson = [];
}

function getPrePluginAliasByName(authPluginListStr){
	var stringArray = authPluginListStr.split(","); 
	var finalResult = '';
	$.each(stringArray, function( index, value ) {
		
		var isAttributeFound = false;
		jQuery.map(prePluginList, function(obj) {
		    if(obj.label === value){
		    	if(finalResult.length == 0){
		    		finalResult =obj.value;
		    	}else{
		    		finalResult=finalResult+","+obj.value;
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

function getPostPluginAliasByName(authPostPluginListStr){
	var stringArray = authPostPluginListStr.split(","); 
	var finalResult = '';
	$.each(stringArray, function( index, value ) {
		var isAttributeFound = false;
		jQuery.map(postPluginList, function(obj) {
		    if(obj.label === value){
		    	if(finalResult.length == 0){
		    		finalResult =obj.value;
		    	}else{
		    		finalResult=finalResult+","+obj.value;
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

//check for all values are same or not
Array.prototype.allValuesSame = function() {

    for(var i = 1; i < this.length; i++){
        if(this[i] !== this[0])
            return false;
    }

    return true;
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

function setUsernameExpression(usernameObj){
	var usernameExpression = $(usernameObj).closest('table').find('.userNameExpression');
	if($(usernameObj).val() == 'Advanced'){
		   $(usernameExpression).attr("readonly", false);
	}else{
		   $(usernameExpression).attr("readonly", true);
	}
}

setTitle('<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.authflow"/>');
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<html:form action="/updateRadiusServicePolicyAuthServiceFlow" styleId="authForm">
		<html:hidden name="updateRadiusServicePolicyForm" styleId="radiusPolicyId" property="radiusPolicyId" value="<%=radiusServicePolicyData.getRadiusPolicyId()%>" />
		<html:hidden name="updateRadiusServicePolicyForm" styleId="action" property="action" value="update"/>
		<html:hidden name="updateRadiusServicePolicyForm" styleId="auditUid" property="auditUid"/>
		<html:hidden name="updateRadiusServicePolicyForm" styleId="sessionManagerId" property="sessionManagerId"/>
		
		<html:hidden property="authHandlerJson" value="" styleId="authHandlerJson"/>
		<html:hidden property="profileLookupDriverJson" value="" styleId="profileLookupDriverJson"/>
		<html:hidden property="authorizationHandlerJson" value="" styleId="authorizationHandlerJson"/>
		<html:hidden property="concurrencyHandlerJson" value="" styleId="concurrencyHandlerJson"/>
		<html:hidden property="cdrGenerationJson" value="" styleId="cdrGenerationJson"/>
		<html:hidden property="pluginHandlerJson" value="" styleId="pluginHandlerJson"/>
		<html:hidden property="coaDMGenerationJson" value="" styleId="coaDMGenerationJson"/>
		<html:hidden property="proxyCommunicationJson" value="" styleId="proxyCommunicationJson"/>
		<html:hidden property="broadcastCommunicationJson" value="" styleId="broadcastCommunicationJson" />
		
		<html:hidden property="authPrePluginList" value="" styleId="authPrePluginList"/>
		<html:hidden property="authPostPluginList" value="" styleId="authPostPluginList"/>
	
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
												</td>
											</tr>
											<tr>
												<td style="padding: 10px;">
													<table id="prePluginTbl" class="prePluginTbl" cellspacing="0" cellpadding="0" width="70%">
														<tr>
															<td  class="captiontext" valign="top">
																<table cellspacing="0" cellpadding="0" border="0" width="100%" id="pre-plugin-mapping-table" class="pre-plugin-mapping-table">
																	<tr>
																		<td class="tbl-header-bold" width="47.5%">
																			<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.preplugin" />
																			<ec:elitehelp header="radiusservicepolicy.preplugin" headerBundle="servicePolicyProperties" text="radiusservicepolicy.preplugin" ></ec:elitehelp>
																		</td>	
																		<td class="tbl-header-bold" width="47.5%">
																			<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.pluginarguments" />
																			<ec:elitehelp   header="radiusservicepolicy.plugin.pluginarguments" headerBundle="servicePolicyProperties" text="radiusservicepolicy.plugin.pluginarguments" ></ec:elitehelp>
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
													<ec:elitehelp   header="radiusservicepolicy.serviceflow" headerBundle="servicePolicyProperties" text="radiusservicepolicy.authserviceflow.maindetails" ></ec:elitehelp>
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding-top: 10px;padding-left: 10px;">
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding: 10px;">
													<table id="authenticationTbl" class="authenticationTbl main-parent-table-class" cellspacing="0" cellpadding="0" width="100%">
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
													<ec:elitehelp   header="radiusservicepolicy.serviceflow" headerBundle="servicePolicyProperties" text="radiusservicepolicy.authpostplugin.maindetails" ></ec:elitehelp>
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding: 10px;">
													<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
														<tr>
															<td  class="captiontext" valign="top">
																<table cellspacing="0" cellpadding="0" border="0" width="100%" id="post-plugin-mapping-table" class="post-plugin-mapping-table">
																	<tr>
																		<td class="tbl-header-bold" width="47.5%">
																			<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.postplugin" />
																			<ec:elitehelp   header="radiusservicepolicy.postplugin" headerBundle="servicePolicyProperties" text="radiusservicepolicy.postplugin" ></ec:elitehelp>
																		</td>	
																		<td class="tbl-header-bold" width="47.5%">
																			<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.pluginarguments" />
																			<ec:elitehelp   header="radiusservicepolicy.prepluginlist" headerBundle="servicePolicyProperties" text="radiusservicepolicy.prepluginlist" ></ec:elitehelp>
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
													<ec:elitehelp   header="radiusservicepolicy.serviceflow" headerBundle="servicePolicyProperties" text="radiusservicepolicy.authpostresponseservice.maindetails" ></ec:elitehelp>
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding-top: 10px;padding-left: 10px;">
												</td>
											</tr>
											<tr>
												<td colspan="3" style="padding: 10px;">
													<table id="additionalTbl" class="authenticationTbl main-parent-table-class" cellspacing="0" cellpadding="0" width="100%">
														<tbody class="parent sortableClassAdditional">
														</tbody>
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
<%@include file="HandlerTemplate.jsp" %>
<div id="driverDiv" style="display: none;" class="driverDiv" title="Create Driver">
	<iframe id='contentIframe' src="<%=basePath%>/initCreateDriver.do?iframeContent=true" style="border: none;height: 100%;width: 100%;"></iframe> 
</div>
<table class="pre-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="pluginName" class="noborder" style="width:100%;background-color: white;" onfocus="setAutoCompleteDataAuthPluginData(this);" disabled="disabled"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="pluginArgument" rows="1" cols="1" style="width:100%;height: 19px;background-color: white;" class="noborder" disabled="disabled"></textarea>
		</td>	
	</tr>
</table>

<table class="post-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="pluginName" class="noborder" style="width:100%;background-color: white;" onfocus="setAutoCompleteDataAuthPluginData(this);" disabled="disabled"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="pluginArgument" rows="1" cols="1" style="width:100%;height: 19px;background-color: white;" class="noborder" disabled="disabled"></textarea>
		</td>	
	</tr>
</table>
