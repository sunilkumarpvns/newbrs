<%@page import="com.elitecore.elitesm.util.constants.ExternalSystemConstants"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.web.sessionmanager.forms.CreateSessionManagerForm"%>
<%@page import="com.elitecore.elitesm.web.sessionmanager.forms.CreateSessionManagerDetailForm"%>
<%@page import="com.elitecore.elitesm.datamanager.sessionmanager.data.SMDBFieldMapData"%>
<%@page import="com.elitecore.elitesm.datamanager.sessionmanager.data.ISMDBFieldMapData"%>
<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping"%>
<%@page import="com.elitecore.coreradius.commons.util.constants.RadiusConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData"%>

<%
	String basePath = request.getContextPath();
	 
	List<ISMDBFieldMapData> dbFieldMapList = null;
	dbFieldMapList = (List<ISMDBFieldMapData>) request.getAttribute("dbFieldMapList");
	List<IDatabaseDSData> lstdatasource = (List<IDatabaseDSData>) request.getAttribute("lstDatasource");
	String acctUrl = basePath	+ "/initAddExtenalSystemPopup.do?externalSystemTypeId="	+ ExternalSystemConstants.ACCT_PROXY;
	String nasUrl = basePath	+ "/initAddExtenalSystemPopup.do?externalSystemTypeId=" + ExternalSystemConstants.NAS;
	
	String[] nasInstanceIds = (String[])request.getAttribute("nasInstanceIds");
	String[][] nasInstanceNames =  (String[][])request.getAttribute("nasInstanceNames");
	List<ExternalSystemInterfaceInstanceData> nasServers = (List<ExternalSystemInterfaceInstanceData>)request.getAttribute("nasInstanceList");	
	
	String esiInstanceIds[] = (String[])request.getAttribute("esiInstanceIds");
	String esiInstanceNames[][] =  (String[][])request.getAttribute("esiInstanceNames");
	List<ExternalSystemInterfaceInstanceData> proxyServerRelList = (List)request.getAttribute("proxyServerRelList"); 
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script language="javascript"> 
 var jesiNames = new Array();
 var jesiInstanceIds = new Array();

 	$(document).ready(function() {
 		var batchUpdateEnabled = document.getElementById("batchUpdateEnabled").value;
		if(batchUpdateEnabled == 'false') {
			document.getElementById("batchSize").disabled = true;
			document.getElementById("batchUpdateInterval").disabled = true;
		}else{
			document.getElementById("batchSize").disabled = false;
			document.getElementById("batchUpdateInterval").disabled = false;
		}  
		
		
		var autoSessionClosure= document.getElementById("autosessioncloser").value;
		if(autoSessionClosure == 'false') {
			document.getElementById("sessionTimeout").disabled = true;
			document.getElementById("closeBatchCount").disabled = true;
			document.getElementById("sessionThreadSleeptime").disabled = true;
			document.getElementById("sessionCloseAction").disabled = true;
		}else{
			document.getElementById("sessionTimeout").disabled = false;
			document.getElementById("closeBatchCount").disabled = false;
			document.getElementById("sessionThreadSleeptime").disabled = false;
			document.getElementById("sessionCloseAction").disabled = false;
		}	
		$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + "CALLING_STATION_ID" + "<input type='hidden' name = 'fieldVal' value='" + "CALLING_STATION_ID" + "'/>" +"</td><td class='tblfirstcol'>" + "0:31" + "<input type='hidden' name = 'refEntVal' id = 'ref' value='" + "0:31" + "'/>" +"</td><td class='tblfirstcol'>" + "String" + "<input type='hidden' name = 'dtTypeVal' value='" + "0" + "'/>" +"</td><td class='tblfirstcol'>" + "" + "<input type='hidden' name = 'defaultValue' value='" + "" + "'/>&nbsp;" +"</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
		$('#mappingtbl td img.delete').live('click',function() {
				 $(this).parent().parent().remove(); });
 	});		
 
 function nasEsiPopup() {
 	
 	var count1 = 0;

 	jesiNames.length = <%=nasServers.size()%>;				
 	jesiInstanceIds.length= <%=nasServers.size()%>;
 				
 		<%int l,m=0;
 		for(l =0;l<nasServers.size();l++){%>		
 			jesiNames[<%=l%>] = new Array(4);		
 				<%for(m=0;m<4;m++){%>												
 					jesiNames[<%=l%>][<%=m%>] = '<%=nasInstanceNames[l][m]%>'				
 				<%}%>
 			jesiInstanceIds[<%=l%>] = '<%=nasInstanceIds[l]%>'	
 			count1 ++;							
 		<%}%>	 	 

 		var headersArr = new Array(6);
		headersArr[0] = '';
		headersArr[1] = '<bean:message bundle="externalsystemResources" key="esi.name" />';
		headersArr[2] = '<bean:message bundle="externalsystemResources" key="esi.address" />';
		headersArr[3] = '<bean:message bundle="externalsystemResources" key="esi.minlocalport" />';
		headersArr[4] = '<bean:message bundle="externalsystemResources" key="esi.expiredrequestlimitcount" />';
		headersArr[5] = 'Weightage';
 		
 		initializeData(jesiInstanceIds,jesiNames,'nasTbl','nasServerCheckboxId',headersArr,'true',count1);
 }

 function nasServerPopup() {
	 openpopup('nasServersPopup','nasServerCheckboxId',jesiInstanceIds,jesiNames,'nasClients');	
 }
 
 
 var jacctProxyNames = new Array();
 var jacctProxyInstanceIds = new Array();
 
 
 function proxyServerPopup(){
	 var count = 0;
	 jacctProxyNames.length = <%=proxyServerRelList.size()%>;				
	 jacctProxyInstanceIds.length= <%=proxyServerRelList.size()%>;
			
		
		<%int j,k=0;
		for(j =0;j<proxyServerRelList.size();j++){%>		
		jacctProxyNames[<%=j%>] = new Array(4);		
				<%for(k=0;k<4;k++){%>												
				jacctProxyNames[<%=j%>][<%=k%>] = '<%=esiInstanceNames[j][k]%>'				
				<%}%>
				jacctProxyInstanceIds[<%=j%>] = '<%=esiInstanceIds[j]%>'	
			count ++;							
		<%}%>	 	 

		var headersArr = new Array(6);
		headersArr[0] = '';
		headersArr[1] = '<bean:message bundle="externalsystemResources" key="esi.name" />';
		headersArr[2] = '<bean:message bundle="externalsystemResources" key="esi.address" />';
		headersArr[3] = '<bean:message bundle="externalsystemResources" key="esi.minlocalport" />';
		headersArr[4] = '<bean:message bundle="externalsystemResources" key="esi.expiredrequestlimitcount" />';
		headersArr[5] = 'Weightage';
		
		initializeData(jacctProxyInstanceIds,jacctProxyNames,'acctProxyTbl','proxyCheckboxId',headersArr,'true',count);
 }
	 
 function proxyEsipopup(){
		openpopup('acctProxyServersPopup','proxyCheckboxId',jacctProxyInstanceIds,jacctProxyNames,'acctServers');
}
 
 
 var currentList; // for esi
 var serverListArray = new Array(); //for esi
 
 var mainArray = new Array(); // for db field mapping
 var refArray = new Array(); // for db field mapping
 var count = 0;
 var count1 = 0;
function validateCreate(dbFieldSize){
	if(document.forms[0].databaseId.value == '0'){
		
		document.forms[0].databaseId.focus();
		alert('Please Select Datasource');
		
	}else if(isNull(document.forms[0].tablename.value)){
		
		document.forms[0].tablename.focus();
		alert('Table name must be specified.');
		
   	}else if(isNull(document.forms[0].batchSize.value) && document.getElementById("batchUpdateEnabled").value == 'true'){
		
		document.forms[0].batchSize.focus();
		alert('Batch Size must be specified.');
		
   	}else if(isNaN(document.forms[0].batchSize.value) && document.getElementById("batchUpdateEnabled").value == 'true'){
   		alert('Batch Size must be Numeric');
   	}else if((document.forms[0].batchSize.value <= 0 || document.forms[0].batchSize.value > 3000) && document.getElementById("batchUpdateEnabled").value == 'true'){
		
		document.forms[0].batchSize.focus();
		alert('Batch Size must be between 1 and 3000');
		
   	}else if(isNull(document.forms[0].batchUpdateInterval.value) && document.getElementById("batchUpdateEnabled").value == 'true'){
		
		document.forms[0].batchUpdateInterval.focus();
		alert('Batch Update Interval must be specified.');
		
   	}else if(checkForBatchUpdateInterval() && document.getElementById("batchUpdateEnabled").value == 'true'){
   		alert('Batch Update Interval must be Positive Integer');
   		document.forms[0].batchUpdateInterval.focus();
   	}else if((isNull(document.forms[0].dbQueryTimeOut.value) || document.forms[0].dbQueryTimeOut.value <= 0)){
		
		document.forms[0].dbQueryTimeOut.focus();
		alert('DB Query TimeOut must be specified.');
		
   	}else if(isNaN(document.forms[0].dbQueryTimeOut.value)){
   		alert('DB Query TimeOut must be Numeric');
   	}else if(isNull(document.forms[0].sessionTimeout.value) && document.getElementById("autosessioncloser").value == 'true'){

   		document.forms[0].sessionTimeout.focus();
		alert('SessionTimeout must be specified.');
        
   	}else if(isNull(document.forms[0].closeBatchCount.value) && document.getElementById("autosessioncloser").value == 'true'){

   		document.forms[0].closeBatchCount.focus();
		alert('Session Close BatchCount  must be specified.');
		
   	}else if(isNull(document.forms[0].sessionThreadSleeptime.value)  && document.getElementById("autosessioncloser").value == 'true' ){

   		document.forms[0].sessionThreadSleeptime.focus();
		alert('Session Thread Sleeptime must be specified.');
		
   	}else if(isNull(document.forms[0].idSequenceName.value)){

   		document.forms[0].idSequenceName.focus();
		alert('Sequence Name must be specified.');
		
   	}else if(isNull(document.forms[0].startTimeField.value)){

   		document.forms[0].startTimeField.focus();
		alert('Start Time Field must be specified.');
		
   	}else if(isNull(document.forms[0].lastUpdatedTimeField.value)){
   		document.forms[0].lastUpdatedTimeField.focus();
		alert('Last Update Time Field must be specified.');
   	}else if(isNull(document.forms[0].concurrencyIdentityField.value)){
   		document.forms[0].concurrencyIdentityField.focus();
		alert('Concurrency Identity Field must be specified.');
   	}else if(!isValidStartTimeField()){
   		return;
   	}else if(!isValidLastUpdateTimeField()){
   		return;
	}else if(!isValidSessionOverrideColumn()){
   		return;
   	}else if(!validateReferAttributeMandatoryMappings()){
   		return;
  	}else if(!validateDbFieldMandatoryMappings()){
   		return;
   	}else if(!validateSessionCloseAction()){
   		return;
   	}else if(!isValidAddtionDBFieldMapping()){
   		return;
   	}else if(!isValidateconcurrencyIdentityField()){
   		return;
   	}else if(document.forms[0].searchAttribute.value.length > 0){
   	   	var searchAttrVal = document.forms[0].searchAttribute.value;
		
		var attrValArray = searchAttrVal.split(/[,; ]/);
		for(var j=0;j<attrValArray.length;j++){
			var bFound = false; 
			for(var i=0;i<mainArray.length;i++) {
				if(attrValArray[j] == mainArray[i]) {
					bFound = true;
	   	   	   	}
	   	   	}
	   	   	if(bFound==false){
	   	   		var bFlag=false;
	   	   		$("input[name=dbField]").each(function() { 
	   			 	if(($(this).val() === attrValArray[j])){
	   					 bFlag=true;
	   				} 
	   			});
	   	   		var checkGroupName=$.inArray('GROUPNAME',attrValArray);
	   	   		if(checkGroupName != -1){
	   	   			alert("Configured Discrete Search Field [GROUPNAME] is not allowed.");
	   	   			document.forms[0].searchAttribute.focus();
	   	   			return;
	   	   		}
	   	   		var checkUID=$.inArray('CONCUSERID',attrValArray);
   	   			if(checkUID != -1){
   	   				alert("Configured Discrete Search Field [CONCUSERID] is not allowed.");
   	   				document.forms[0].searchAttribute.focus();
   	   				return;
   	   			}
	   	   		if(bFlag == false){
		   	   		alert("Configured Discrete Search Field ["+attrValArray[j]+"] must be mapped in DB Field Mapping List");
		   	   		document.forms[0].searchAttribute.focus();
		   	   		return;
	   	   		}
	   	   	}
		}
		document.forms[0].checkAction.value='Save';
	    document.forms[0].submit();
   	}else{   
   		
   		enableAll();
     	document.forms[0].checkAction.value='Save';
        document.forms[0].submit();
   	}
}
function checkForBatchUpdateInterval(){
	var isNotNumber =  isNaN(document.forms[0].batchUpdateInterval.value);
	if(isNotNumber){
		return true;
	}else{
		if(document.forms[0].batchUpdateInterval.value <= 0){
			return true;
		}
	}
	return false;
}

function isValidStartTimeField(){
	var isValid = true;
	var value=$('#startTimeField').val();
	if(value=="GROUPNAME" || value =="CONCUSERID"){
		alert("Configured Start Time Field ["+value+"] is not allowed.");
		document.forms[0].startTimeField.focus();
		isValid=false;
	}
	return isValid;
	
}
function isValidLastUpdateTimeField(){
	var isValid = true;
	var value=$('#lastUpdatedTimeField').val();
	if(value=="GROUPNAME" || value =="CONCUSERID"){
		alert("Configured Last Update Time Field ["+value+"] is not allowed.");
		document.forms[0].lastUpdatedTimeField.focus();
		isValid=false;
	}
	return isValid;
}
function addDBplugin(){

	if(isNull(document.forms[0].dbFieldName.value)){
		
		document.forms[0].dbFieldName.focus();
		alert('DBfield Name must be specified.');
		
   	}else if(isNull(document.forms[0].referringEntity.value)){

   		document.forms[0].referringEntity.focus();
		alert('Referring Entity must be specified.');
        
   	}else{
	document.forms[0].checkAction.value='Add';
    document.forms[0].submit();
   	}
}

function removeDBplugin(index){
	document.forms[0].checkAction.value = 'Remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();
	
}

function popup(mylink, windowname,current)
{
	currentList = current;
	if (! window.focus)return true;
		var href;
	if (typeof(mylink) == 'string')
					href=mylink;
	else
					href=mylink.href;
					
	window.open(href, windowname, 'width=800,height=300,left=150,top=100,scrollbars=yes');
	return false;
} 
function addItemInCurrentList(esiServers,weightageArray, serverNameArray){
	
	var existLength = currentList.length
	var index = existLength;
	for(var i = 0; i < esiServers.length; i++) {
		
		if(esiServers[i].checked == true){
		var nameValue  = serverNameArray[i] +"-W-"+weightageArray[i];
		var keyValue   =esiServers[i].value  +"-"+weightageArray[i];
		currentList.options[index] = new Option(nameValue, keyValue);
		index++;
		}
	 }
}
function removeFromList(externalSystemList){
var size = externalSystemList.options.length;
for(var i = size - 1; i >= 0; i--) {
if ((externalSystemList.options[i] != null) && (externalSystemList.options[i].selected == true)) {
	externalSystemList.options[i] = null;
      }
   }
}

function selectAll(selObj){
	
	for(var i=0;i<selObj.options.length;i++){
		selObj.options[i].selected = true;
	}
}

function validateSessionCloseAction(){
	var element = document.getElementById("sessionCloseAction");
	var element1 = document.getElementById("sessionOverrideAction");
	var nasclients = document.getElementById("nasClients");
	var acctservers = document.getElementById("acctServers");
   
	if (element.value == <%=RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_DISCONNECT%> || element1.value == <%=RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DISCONNECT%>){
		if(nasclients!=null){
			if(nasclients.options.length==0){
				alert('NAS Client must be specified');
				return false;
			}
		}
	
	}
	if (element.value == <%=RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_STOP%> || element1.value == <%=RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_STOP%> ){
		if(acctservers!=null){
			if(acctservers.options.length==0){
				alert('Accounting Server must be specified');
				return false;
			}
		}
	}
	if (element.value == <%=RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_DM_AND_STOP%> || element1.value == <%=RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DM_AND_STOP%>){
		if(acctservers!=null){
			if(acctservers.options.length==0){
				alert('Accounting Server must be specified');
				return false;
			}
		}
		if(nasclients!=null){
			if(nasclients.options.length==0){
				alert('NAS Client must be specified');
				return false;
			}
		}
	}
	if(nasclients!=null){
	  selectAll(nasclients);
	}
	if(acctservers!=null){
	  selectAll(acctservers);	
	}
	return true;
}
function openPopup(){	
	$.fx.speeds._default = 1000;
	document.getElementById("popupfieldMapping").style.visibility = "visible";
	fieldName = [];
	$( "#popupfieldMapping" ).dialog({
		modal: true,
		autoOpen: false,		
		height: "auto",
		width: 500,				
		buttons:{					
            'Add': function() {	

					var name = $('#fieldName').val();
					var name0 = $('#referEntity').val(); 
					var name1 = $('#datatype').val();
					var name2 = $('#defval').val();
		          	refArray[count1++] = name0;
	          		if(isNull($('#fieldName').val())){
		     			alert('Field Name value must be specified.');
		     		}else if(isNull($('#referEntity').val())){
		     			alert('Referring Entity value must be specified.');
		     		}else if(isNull($('#datatype').val())){
		     			alert('Data type value must be specified.');
		     		}else if(isValidDuplicateDbFieldMappings($.trim(name))){
		     			alert("Value "+$.trim(name)+" is already present in "+fieldName+".so add another value for DB Field Name");
		     		}else {	
		     			var i = 0;							
						var flag = true;												 	
						if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){												
							for(i=0;i<mainArray.length;i++){									
								var value = mainArray[i];																						
								if(value == name){
									alert("Mapping with this value is already present so add another value for attribute");
									flag = false;
									break;
								}
							}
						}								
		         		if(flag){	
		         			var dataTypeLabel;	
			         		if(name1=='0'){
			         			dataTypeLabel="String";
			         		}else if(name1=='1'){
			         			dataTypeLabel="Timestamp";
			         		}
		         			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'fieldVal' value='" + name + "'/>" +"</td><td class='tblfirstcol'>" + name0 + "<input type='hidden' name = 'refEntVal' id = 'ref' value='" + name0 + "'/>" +"</td><td class='tblfirstcol'>" + dataTypeLabel + "<input type='hidden' name = 'dtTypeVal' value='" + name1 + "'/>" +"</td><td class='tblfirstcol'>" + name2 + "<input type='hidden' name = 'defaultValue' value='" + name2 + "'/>&nbsp;" +"</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
		         			$('#mappingtbl td img.delete').live('click',function() {

		       				 //var $td= $(this).parents('tr').children('td');			
		       				 var removalVal = $(this).closest('tr').find('td:eq(0)').text(); 							
		       				for(var d=0;d<count;d++){
		       					var currentVal = mainArray[d];					
		       					if(currentVal == removalVal){
		       						mainArray[d] = '  ';
		       						break;
		       					}
		       				}
		       				var removalRefVal = $(this).closest('tr').find('td:eq(1)').text();
		       				for(var f=0;f<count1;f++){
								var currentRefVal = refArray[f];
								if(currentRefVal == removalRefVal){
									refArray[f] = "";
									break;
								}
			       			}								
		       				 $(this).parent().parent().remove(); });		         						          					          	
			          		mainArray[count++] = name;				          					          							          					          		
			          		$(this).dialog('close');
			         	}	         				    		         			   				         			          				          		
		         	}		         				          		
            },                
		    Cancel: function() {
            	$(this).dialog('close');
        	}
        },
    	open: function() {
        	
    	},
    	close: function() {
    		document.getElementById("fieldName").value = "";
			document.getElementById("referEntity").value = "";
			document.getElementById("defval").value = "";  
    		document.getElementById("c_btnCreate2").focus();
    	}				
	});
	$( "#popupfieldMapping" ).dialog("open");
	$(retriveTableFields(document.getElementById("databaseId").value));
	
}


function setColumnsOnTextFields(){
	var dbId = document.getElementById("databaseId").value;
	retriveTableFields(dbId);
	retriveTableFieldsForSearchAttr(dbId);
}

function setColumnsForTables(){
	setColumnsOnTextFields();
	setColumnsOnSearchTextFields();
}

function retriveTableFields(dbId) {
	var dbFieldStr;
	var tableName = document.getElementById("tablename").value;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		data=data.trim();
		dbFieldStr = data.substring(1,data.length-1);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		setFields("fieldName",dbFieldArray);
		setFields("identityField",dbFieldArray);
		setFields("startTimeField",dbFieldArray);
		setFields("lastUpdatedTimeField",dbFieldArray);
		
		return dbFieldArray;
	});	
	
}

function setColumnsOnRefEntityAttrTextFields(){
	var refEntityAttrVal = document.getElementById("referEntity").value;
	retriveRadiusDictionaryAttributes(refEntityAttrVal,"referEntity");
}

	$(document).ready(function() {
		setColumnsOnSearchTextFields();
		retriveRadiusDictionaryAttributesForReferingAtrributes();
	});
	
	
	function setColumnsOnSearchTextFields(){
		var dbId = document.getElementById("databaseId").value;
		retriveTableFieldsForSearchAttr(dbId);
	 }
	 
	function retriveTableFieldsForSearchAttr(dbId) {
		var dbFieldStr;
		var myArray = new Array();
		var tableName = document.getElementById("tablename").value;
		$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
			data=data.trim();
			dbFieldStr = data.substring(1,data.length-1);
			var dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split(", ");
			for(var i=0;i<dbFieldArray.length;i++) {
				value = dbFieldArray[i].trim();
				label = dbFieldArray[i];
				var item = new ListItem(value,label); 
				myArray.push(item);
			}
			setSuggestionForSearchAttribute("searchAttribute",myArray);
			setSuggestionForSearchAttribute("sessionOverrideColumn",myArray);
			setSuggestionForConcurrencyIdentity("concurrencyIdentityField", myArray);
			setSuggestionForDBFieldAttribute("dbField",myArray);
			return dbFieldArray;
		});	
	}
	
	function setSuggestionForDBFieldAttribute(txtField,myArray) {
		 $( "."+ txtField).autocomplete({	
				source:myArray });
	}
	
	function split( val ) {
		return val.split( /[,;(]\s*/ );
	}
	function extractLast( term ) {
		return split( term ).pop();
	} 	
	function retriveRadiusDictionaryAttributesForReferingAtrributes() {
		var myArray = new Array();
		var dbFieldStr;
		var dbFieldArray;
		var searchNameOrAttributeId="";
		$.post("SearchRadiusAttributesServlet", {searchNameOrAttributeId:searchNameOrAttributeId}, function(data){
			data=data.trim();
			dbFieldStr = data.substring(1,data.length-1);
			dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split("#, ");
			var value;
			var label;
			var desc;
		
			for(var i=0;i<dbFieldArray.length;i++) {
				tmpArray = dbFieldArray[i].split(",");
				value = tmpArray[0].trim();
				label = tmpArray[1];
				var item = new ListItem(value,label); 
				myArray.push(item);
			}	
			setRadiusDictionaryReferAttributeFields("referingAttrib",myArray);
			return dbFieldArray; 
			
		});
	}
	function setRadiusDictionaryReferAttributeFields(txtField,myArray) {
		$( ".referAttributes").autocomplete({	
				source:function( request, response ) {
					response( $.ui.autocomplete.filter(
							myArray, extractLast( request.term ) ) );
				},
					
				focus: function( event, ui ) {
					return false;
				},
				select: function( event, ui ) {
					var val = this.value;
					var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
					var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
					var bracketIndex=val.lastIndexOf("(") == -1 ? 0 :val.lastIndexOf("(");
					 if(commaIndex == semiColonIndex && bracketIndex == commaIndex ) {
							val = "";
					}else if(commaIndex > semiColonIndex) {
						if(commaIndex < bracketIndex){
							val = val.substring(0,bracketIndex+1);
						}else{
							val = val.substring(0,commaIndex+1); 
						}
					}else if(bracketIndex>commaIndex){
						val = val.substring(0,bracketIndex+1);
					} else {
						val = val.substring(0,semiColonIndex+1);
					}
					this.value = val + ui.item.value ;
					return false;
				}
			});		
	}
	function setSuggestionForSearchAttribute(txtField,myArray) {
		$( "#"+ txtField).autocomplete({	
				source:function( request, response ) {
					response( $.ui.autocomplete.filter(
							myArray, extractLast( request.term ) ) );
				},
					
				focus: function( event, ui ) {
					return false;
				},
				select: function( event, ui ) {
					var val = this.value;
					var commaIndex = val.lastIndexOf(",") == -1 ? 0 : val.lastIndexOf(",");
					var semiColonIndex = val.lastIndexOf(";") == -1 ? 0 : val.lastIndexOf(";");
					if(commaIndex == semiColonIndex) {
						val = "";
					}else if(commaIndex > semiColonIndex) {
						val = val.substring(0,commaIndex+1);
					} else {
						val = val.substring(0,semiColonIndex+1);
					}
					this.value = val + ui.item.value ;
					return false;
				}
			});		
	}
	
	function setSuggestionForConcurrencyIdentity(txtField,myArray){
		$( "#"+ txtField).autocomplete({
			source: myArray
		});
	}

function setBatchUpdate() {
	var batchUpdateEnabled = document.getElementById("batchUpdateEnabled").value;
	if(batchUpdateEnabled == 'false') {
		document.getElementById("batchSize").disabled = true;
		document.getElementById("batchUpdateInterval").disabled = true;
	}else{
		document.getElementById("batchSize").disabled = false;
		document.getElementById("batchUpdateInterval").disabled = false;
	}	
}
function setAutoSessionClosure(){
	var autoSessionClosure= document.getElementById("autosessioncloser").value;
	if(autoSessionClosure == 'false') {
		document.getElementById("sessionTimeout").disabled = true;
		document.getElementById("closeBatchCount").disabled = true;
		document.getElementById("sessionThreadSleeptime").disabled = true;
		document.getElementById("sessionCloseAction").disabled = true;
	}else{
		document.getElementById("sessionTimeout").disabled = false;
		document.getElementById("closeBatchCount").disabled = false;
		document.getElementById("sessionThreadSleeptime").disabled = false;
		document.getElementById("sessionCloseAction").disabled = false;
	}	

}
function enableAll() {
	var batchUpdateEnabled = document.getElementById("batchUpdateEnabled").value;
	if(batchUpdateEnabled == 'false') {
		document.getElementById("batchSize").disabled = false;
		document.getElementById("batchUpdateInterval").disabled = false;
	}
}

function isValidSessionOverrideColumn(){
	if(isEmptyById("sessionOverrideColumn")) {
		return true;
	} else {
		var overrideValue = $("#sessionOverrideColumn").val();
		var overrideColumnArray = overrideValue.split(/[,; ]/);
		
		// Get all the mapping table field Array
		var dbFieldArray = new Array();
		$('input:hidden[name=fieldVal]').each(function(index) { 
			dbFieldArray[index] = $(this).val();
		});

		var mandatoryFieldArray=new Array();
		$('input[name=dbField]').each(function(index) { 
			mandatoryFieldArray[index] = $(this).val();
		});
		// Check Validation for column
		var isValid = true;
		var notValidColumnArray = new Array();
		$.each(overrideColumnArray, function(index, item) {
				if(!isEmpty(item) && $.inArray(item,dbFieldArray)<0) { 
					isValid = false;
					notValidColumnArray.push(item);
				}
		});
		var arrayC=[];
		$.each(mandatoryFieldArray.concat(notValidColumnArray),function(idx,val) {
		    if(val!=null) arrayC.push(val);
		});
	 	
		var uniqueNames = [],nonUnique=[];
		$.each(arrayC, function(i, el){
		    if($.inArray(el, uniqueNames) === -1){
		    	uniqueNames.push(el);
		    }else{
		    	nonUnique.push(el);
		    }
		});
	
		if(nonUnique.length == 0){
			if(!isValid){
				var checkElement=false,invalidStr="";
				for(var i=0;i<notValidColumnArray.length;i++) {
				  if(notValidColumnArray[i]=="GROUPNAME" || notValidColumnArray[i] == "CONCUSERID"){
					  if(invalidStr==""){
						  invalidStr=notValidColumnArray[i];
					  }else{
						  invalidStr=invalidStr+","+notValidColumnArray[i];
					  }
					  checkElement=true;
				  }
				}
				if(checkElement == false){
					alert("Configured Session Override Fields ["+notValidColumnArray+"] must be mapped in DB Field Mapping List");
					document.forms[0].sessionOverrideColumn.focus();
					return false;
				}else{
					alert("Configured Session Override Fields [ "+invalidStr+" ] is not allowed");
					document.forms[0].sessionOverrideColumn.focus();
					return false;
				}
		}
		}else{
			Array.prototype.diff = function(a) {
			    return this.filter(function(i) {return !(a.indexOf(i) > -1);});
			};
			
			var newArray=[];
			newArray=notValidColumnArray.diff(nonUnique);
			if(newArray.length>=1){
				if(!isValid){
					var checkElement=false,invalidStr="";
					for(var i=0;i<newArray.length;i++) {
						  if(newArray[i]=="GROUPNAME"|| notValidColumnArray[i] == "CONCUSERID"){
							  if(invalidStr==""){
								  invalidStr=notValidColumnArray[i];
							  }else{
								  invalidStr=invalidStr+","+notValidColumnArray[i];
							  }
							  checkElement=true;
						  }
					}
					if(checkElement == false){
						alert("Configured Session Override Fields [ "+newArray+" ] must be mapped in DB Field Mapping List");
						document.forms[0].sessionOverrideColumn.focus();
						return false;
					}else{
						alert("Configured Session Override Fields [ "+invalidStr+" ] is not allowed");
						document.forms[0].sessionOverrideColumn.focus();
						return false;
					}
	} 
			}else{
				return true;
}
		}
		return true;
	} 
}

function isDuplicateDbField(dbFieldval,fieldName){
	fieldName.pop(); 
	
	if(!isEmptyById("identityField") && dbFieldval == $.trim($("#dbField").val())){
		fieldName.push("Field Name");
		return true;
	}
	if(!isEmptyById("startTimeField") && dbFieldval == $.trim($("#startTimeField").val())){
		fieldName.push("Start Time Field");
		return true;
	}
	if(!isEmptyById("lastUpdatedTimeField") && dbFieldval == $.trim($("#lastUpdatedTimeField").val())){
		fieldName.push("Last Update Time Field");
		return true;
	}	
	if(!isEmptyById("sessionIdField") && dbFieldval == $.trim($("#sessionIdField").val())){
		fieldName.push("Session Id Field Name");
		return true;
	}	
	if(!isEmptyById("groupNameField") && dbFieldval == $.trim($("#groupNameField").val())){
		fieldName.push("Group Name Field");
		return true;
	}
	if(!isEmptyById("serviceTypeField") && dbFieldval == $.trim($("#serviceTypeField").val())){
		fieldName.push("Service Type Field ");
		return true;
	}	
	return null;
}


function isValidDuplicateDbFieldMappings(txtFieldName){
	var isValid=false,flag=true;
	$("input[name=dbField]").each(function() { 
		 if(($(this).val() === txtFieldName)){
			 flag=false;
		} 
	});
	if(flag==false){
		isValid=true;
	}
	return isValid;
}
function isValidAddtionDBFieldMapping(){
	var isValid = true,strValidMapping="";
	$("input:hidden[name=fieldVal]").each(function() { 
		var fieldName= [];
		if(isDuplicateDbField($(this).val(),fieldName)){
			alert("Duplicate entries for value "+$(this).val()+" in "+fieldName+" and Additional Field Mapping's DBField Name");
			isValid = false;
		}
	});
	$("input[name=fieldVal]").each(function() { 
		var dbFieldVal=$(this).val();
		if (dbFieldVal.indexOf("GROUPNAME") !=-1 ) {
			alert("Configured attribute in Mandatory DB Field Mappings [GROUPNAME] is not allowed");
			isValid = false;
			return false;
		}
		if(dbFieldVal.indexOf("CONCUSERID") !=-1 ){
			alert("Configured attribute in Mandatory DB Field Mappings [CONCUSERID] is not allowed");
			isValid = false;
			return false;
		}
		if((dbFieldVal == "GROUPNAME") || (dbFieldVal == "CONCUSERID")){
			if(strValidMapping == ""){
				strValidMapping=dbFieldVal;
			}else{
				strValidMapping=strValidMapping+","+dbFieldVal;
			}
			alert("Configured attribute in Additional DB Field Mappings ["+strValidMapping+"] is not allowed");
			isValid = false;
			return false;
		}
	});
	return isValid;
}

function isValidateconcurrencyIdentityField(){
	var concurrencyIdentityField = $('#concurrencyIdentityField').val().trim();
	if( concurrencyIdentityField == 'GROUPNAME' ){
		return true;
	}else{
		var isFieldMappingFound = false;
		$('.dbField').each(function(){
			if($(this).val() == concurrencyIdentityField){
				isFieldMappingFound = true;
			}
		});
		
		$('input:hidden[name=fieldVal]').each(function(index) { 
			if($(this).val() == concurrencyIdentityField){
				isFieldMappingFound = true;
			}
		});
		
		
		if(isFieldMappingFound){
			return true;
		}else{
			alert('Concurrency Identity Field must have mapping either Mandatory mapping or Additional DB Field Mapping');
			return false;
		}
	}
}

function validateReferAttributeMandatoryMappings(){
	var isValid = true,flag=false,equalsVal=false;
	$("input[name=referingAttrib]").each(function() { 
		if(($(this).val() === '')){
			alert("Refeing Attribute Must be specified.");
			isValid = false;
			flag=true;
			return false;
		}
	});
	
	if(flag==false){
		$("input[name=referingAttrib]").each(function() { 
			var element=$(this).val();
			var n=element.lastIndexOf('$'); 
			if(n>0){
				var bracketIndex = element.lastIndexOf('(');
				var between = element.substring(n,bracketIndex+1);
				var betweenString=between.toLowerCase();
				if(betweenString == "$req(" || betweenString == "$res("){
					var lastIndexofBracket=element.lastIndexOf(')'); 
					var lastSecondIndexofBracket=element.lastIndexOf('(');
					if(lastIndexofBracket > lastSecondIndexofBracket){
						var differenceOfBracket=(lastIndexofBracket-lastSecondIndexofBracket);
						if(differenceOfBracket == 1){
							alert("Invalid refering Attributes : " +element);
							equalsVal=true;
							isValid = false;
						}else{
							var countBracket=(element.split("(").length - 1); 
							var countClosingBracket=(element.split(")").length - 1);
							if(countBracket != countClosingBracket ){
								alert("Invalid refering Attributes : " +element);
								equalsVal=true;
								isValid = false;
							}else{
								equalsVal=true;
							}
						}
					}else if(lastIndexofBracket == -1){
						alert("Invalid refering Attributes :" +betweenString);
						equalsVal=true;
						isValid = false;
					}else{
						equalsVal=false;
					}
				}else{
					equalsVal=false;
				}
				if(equalsVal == false){
					alert("Invalid refering Attributes :" +betweenString);
					isValid = false;
					return false;
				}
			}

	});
	}
	return isValid;
}

function validateDbFieldMandatoryMappings(){
	var isValid = true,strValidMapping="";;
	$("input[name=dbField]").each(function() { 
		if(($(this).val() === '')){
			alert("DBField Attribute Must be specified.");
			isValid = false;
			return false;
		}
	});

	$("input[name=dbField]").each(function() { 
		var dbFieldVal=$(this).val();
		if (dbFieldVal.indexOf("GROUPNAME") !=-1 ) {
			alert("Configured attribute in Mandatory DB Field Mappings [GROUPNAME] is not allowed");
			isValid = false;
			return false;
		}
		if(dbFieldVal.indexOf("CONCUSERID") !=-1 ){
			alert("Configured attribute in Mandatory DB Field Mappings [CONCUSERID] is not allowed");
			isValid = false;
			return false;
		}
		if((dbFieldVal == "GROUPNAME") || (dbFieldVal == "CONCUSERID")){
			if(strValidMapping == ""){
				strValidMapping=dbFieldVal;
			}else{
				strValidMapping=strValidMapping+","+dbFieldVal;
			}
			alert("Configured attribute in Mandatory DB Field Mappings ["+strValidMapping+"] is not allowed");
			isValid = false;
			return false;
		}
	});
	return isValid;
}
setTitle('<bean:message bundle="sessionmanagerResources" key="sessionmanager.header"/>');
</script>

<html:form action="/createSessionManagerDetail">

	<html:hidden name="createSessionManagerDetailForm" styleId="action"
		property="action" value="createlocal" />
	<html:hidden name="createSessionManagerDetailForm"
		styleId="checkAction" property="checkAction" />
	<html:hidden name="createSessionManagerDetailForm" styleId="itemIndex"
		property="itemIndex" />


	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header" colspan="3"><bean:message
											bundle="sessionmanagerResources"
											key="sessionmanager.sessionmanagerconfig" /></td>
								</tr>
								<!-- <tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr> -->
								<tr>
									<td colspan="3">
										<table width="100%" name="c_tblCrossProductList"
											id="c_tblCrossProductList" align="right" border="0"
											cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" class="captiontext" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.datasource" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.datasource" 
																	header="sessionmanager.datasource"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="32%"
													colspan="3">
													<%if (lstdatasource == null) {%> 
													<bean:define id="lstDatasource"
														name="createSessionManagerDetailForm"
														property="lstDatasource"></bean:define> <%
					   	}
					   %> <html:select name="createSessionManagerDetailForm"
														styleId="databaseId" property="databaseId" size="1"
														onchange="setColumnsOnTextFields();" style="width:250px"
														tabindex="1">
														<html:option value="0">
															<bean:message bundle="sessionmanagerResources"
																key="sessionmanager.select" />
														</html:option>
														<html:options collection="lstDatasource"
															property="databaseId" labelProperty="name" />
													</html:select><font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.tablename" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.tablename" 
																	header="sessionmanager.tablename"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:text styleId="tablename" property="tablename"
														size="30" maxlength="2000" onblur="setColumnsForTables();" style="width:250px"
														tabindex="2" /><font color="#FF0000"> *</font>
												</td>
											</tr>

											<tr>
												
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources" 
														key="sessionmanager.sequencename" />
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.sequencename" 
																	header="sessionmanager.sequencename"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:text styleId="idSequenceName"
														property="idSequenceName" size="20" maxlength="30"
														 style="width:250px" tabindex="3" /><font color="#FF0000">
														*</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.starttimefield" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.starttimefield" 
																	header="sessionmanager.starttimefield"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3"><html:text
														styleId="startTimeField" property="startTimeField"
														size="20" maxlength="30" style="width:250px" tabindex="4" /><font
													color="#FF0000"> *</font></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.lastupdatetimefield" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.lastupdatetimefield" 
																	header="sessionmanager.lastupdatetimefield"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3"><html:text
														styleId="lastUpdatedTimeField"
														property="lastUpdatedTimeField" size="20" maxlength="30"
														style="width:250px" tabindex="5" /><font color="#FF0000">
														*</font></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.querytimeout" />
													<bean:message key="general.seconds" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.querytimeout"  header="sessionmanager.querytimeout"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:text styleId="dbQueryTimeOut" style="width:250px" property="dbQueryTimeOut" size="30" maxlength="60" tabindex="10" />
													<font color="#FF0000">*</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message	bundle="sessionmanagerResources"
														key="sessionmanager.behaviour" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.behaviour" 
																	header="sessionmanager.behaviour"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:select name="createSessionManagerDetailForm"
														styleId="behaviour" property="behaviour" size="1"
														style="width:250px" tabindex="6">
														<html:option value="1">Acct</html:option>
														<html:option value="2">Auth</html:option>
													</html:select>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.dboperationfailurebehaviour" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.dboperationfailurebehaviour" 
																	header="sessionmanager.dboperationfailurebehaviour"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:select name="createSessionManagerDetailForm"
														styleId="dbfailureaction" property="dbfailureaction" size="1"
														style="width:250px" tabindex="6">
														<html:option value='<%=ConfigConstant.IGNORE %>'>Ignore(Default)</html:option>
														<html:option value='<%=ConfigConstant.REJECT %>'>Reject</html:option>
														<html:option value='<%=ConfigConstant.DROP %>'>Drop</html:option>
													</html:select>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.sessionstopaction" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.sessionstopaction" header="sessionmanager.sessionstopaction"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:select name="createSessionManagerDetailForm" styleId="sessionStopAction" property="sessionStopAction" size="1" style="width:250px" tabindex="6">
														<html:option value='<%=ConfigConstant.DELETE %>'>Delete(Default)</html:option>
														<html:option value='<%=ConfigConstant.UPDATE %>'>Update</html:option>
													</html:select>
												</td>
											</tr>
											
											<tr>
												<td class="tblheader-bold" height="20%" colspan="4"><bean:message
														bundle="sessionmanagerResources"
														key="sessionmanager.batchupdateproperties" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.enabled" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.enabled" 
																	header="sessionmanager.enabled"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:select name="createSessionManagerDetailForm"
														styleId="batchUpdateEnabled" property="batchUpdateEnabled"
														size="1" value="true" onchange="setBatchUpdate();"
														tabindex="7">
														<html:option value="true">True</html:option>
														<html:option value="false">False</html:option>
													</html:select>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources" 
														key="sessionmanager.size" />
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.size" 
																	header="sessionmanager.size"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:text styleId="batchSize" property="batchSize"
														size="30" maxlength="60" tabindex="8"  style="width:250px" />
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.updateinterval" />
															<bean:message key="general.miliseconds" />
																<ec:elitehelp headerBundle="sessionmanagerResources" 
																	text="sessionmanager.updateinterval" 
																		header="sessionmanager.updateinterval"/>
												</td>

												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:text styleId="batchUpdateInterval"
														property="batchUpdateInterval" size="30" maxlength="5" style="width:250px"
														tabindex="9" />
												</td>
											</tr>

											<!-- session closer properties  header-->
											<tr>
												<td class="tblheader-bold" height="20%" colspan="4"><bean:message
														bundle="sessionmanagerResources"
														key="sessionmanager.sessioncloserproperties" /></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.autosessioncloser" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.autosessioncloser" 
																	header="sessionmanager.autosessioncloser"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:select name="createSessionManagerDetailForm"
														styleId="autosessioncloser" property="autosessioncloser"
														size="1" value="false" tabindex="11" onchange="setAutoSessionClosure();">
														<html:option value="true">True</html:option>
														<html:option value="false">False</html:option>
													</html:select>
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.sessiontimeout" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.sessiontimeout" 
																	header="sessionmanager.sessiontimeout"/> 
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:text styleId="sessionTimeout"
														property="sessionTimeout" size="30" maxlength="60" style="width:250px"
														tabindex="12" />
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.closebatchcount" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources"
																text="sessionmanager.closebatchcount" 
																	header="sessionmanager.closebatchcount"/> 
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:text styleId="closeBatchCount"
														property="closeBatchCount" size="30" maxlength="60" style="width:250px"
														tabindex="13" />
												</td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.sessionthreadsleeptime" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.sessionthreadsleeptime" 
																	header="sessionmanager.sessionthreadsleeptime"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:text styleId="sessionThreadSleeptime"
														property="sessionThreadSleeptime" size="10" maxlength="60"  style="width:250px"
														tabindex="14" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.sessioncloseaction" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.sessioncloseaction" 
																	header="sessionmanager.sessioncloseaction"/> 
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:select name="createSessionManagerDetailForm"
														styleId="sessionCloseAction" property="sessionCloseAction"  style="width:250px"
														size="1"
														value="<%=Integer.toString(RadiusConstants.SESSION_CLOSE_ACTION_NONE)%>"
														tabindex="15">
														<html:option
															value="<%=Integer.toString(RadiusConstants.SESSION_CLOSE_ACTION_NONE)%>">None</html:option>
														<html:option
															value="<%=Integer.toString(RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_DISCONNECT)%>">Generate Disconnect</html:option>
														<html:option
															value="<%=Integer.toString(RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_STOP)%>">Generate Stop</html:option>
														<html:option
															value="<%=Integer.toString(RadiusConstants.SESSION_CLOSE_ACTION_GENERATE_DM_AND_STOP)%>">Generate Disconnect and Stop</html:option>
													</html:select>
												</td>
											</tr>
											<!-- session override properties  header-->
											<tr>
												<td class="tblheader-bold" height="20%" colspan="4"><bean:message
														bundle="sessionmanagerResources"
														key="sessionmanager.sessionoverrideproperties" /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.sessionoverrideaction" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.sessionoverrideaction" 
																	header="sessionmanager.sessionoverrideaction"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:select name="createSessionManagerDetailForm"
														styleId="sessionOverrideAction"  style="width:250px"
														property="sessionOverrideAction" size="1"
														value="<%=Integer.toString(RadiusConstants.SESSION_OVERRIDE_ACTION_NONE)%>"
														tabindex="16">
														<html:option
															value="<%=Integer.toString(RadiusConstants.SESSION_OVERRIDE_ACTION_NONE)%>">None</html:option>
														<html:option
															value="<%=Integer.toString(RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DISCONNECT)%>">Generate Disconnect</html:option>
														<html:option
															value="<%=Integer.toString(RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_STOP)%>">Generate Stop</html:option>
														<html:option
															value="<%=Integer.toString(RadiusConstants.SESSION_OVERRIDE_ACTION_GENERATE_DM_AND_STOP)%>">Generate Disconnect and Stop</html:option>
													</html:select><font color="#FF0000"> *</font>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.sessionoverridecolumn" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.sessionoverridecolumn" 
																	header="sessionmanager.sessionoverridecolumn"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="4">
													<html:text property="sessionOverrideColumn"  style="width:250px"
														styleId="sessionOverrideColumn" size="40" tabindex="17"></html:text>
												</td>
											</tr>
											<!-- session ESI  header-->
											<tr>
												<td class="tblheader-bold" height="20%" colspan="4">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.sessionesi" />
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top">&nbsp;</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<table width="100%" cellpadding="0" cellspacing="0">
														<tr>
															<td>
																<table id="acct" class="box" cellpadding="0"
																	cellspacing="0" style="width: 300px">
																	<tr>
																		<td colspan="2" class="table-header">Add
																			Accounting Server (For Stop)</td>
																	</tr>
																	<tr>
																		<td style="padding-left: 10px"><br /> <select
																			style="width: 200px" id="acctServers"
																			name="acctServers" multiple="true" size="5" tabindex="18">
																		</select><br /> <br /></td>
																		<td valign="top"><br /> <input type="button"
																			value="Add" onclick="proxyEsipopup();"
																			class="light-btn" style="width: 70px" tabindex="19" /><br />
																			<br /> <input type="button" value="Remove"
																			onclick="removeData('acctServers','proxyCheckboxId')"
																			class="light-btn" style="width: 70px" tabindex="20" />
																		</td>
																	</tr>
														</table>
													</td>
													<td>
														<table id="nas" class="box" cellpadding="0"
																	cellspacing="0" style="width: 300px">
																	<tr>
																		<td colspan="2" class="table-header">Add NAS (For
																			Disconnect)</td>
																	</tr>
																	<tr>
																		<td style="padding-left: 10px"><br /> <select
																			style="width: 200px" id="nasClients"
																			name="nasClients" multiple="true" size="5" tabindex="21">
																		</select><br /> <br /></td>
																		<td valign="top"><br /> <input type="button"
																			value="Add" onclick="nasServerPopup();"
																			class="light-btn" style="width: 70px" tabindex="22" />
																			<br /> <br /> <input type="button" value="Remove "
																			onclick="removeData('nasClients','nasServerCheckboxId')"
																			class="light-btn" style="width: 70px" tabindex="23" />
																		</td>
																	</tr>
																</table>
													</td>
												</tr>
												</table>
												</td> 
												<!-- <td  class="labeltext" valign="top" colspan="3">
																
												</td> -->
											</tr>
											<tr>
												<td class="tblheader-bold" colspan="4" height="20%">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.searchcolumn.field" />
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources" key="sessionmanager.concurrencyidentityfield" /> 
													<ec:elitehelp headerBundle="sessionmanagerResources" text="sessionmanager.concurrencyidentityfield"  header="sessionmanager.concurrencyidentityfield"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<html:text property="concurrencyIdentityField"  style="width:250px"  styleId="concurrencyIdentityField" size="40"></html:text>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.searchcolumn" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.searchcolumn.field" 
																	header="sessionmanager.searchcolumn.field"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="3">
													<input type="text" name="searchAttribute"
													id="searchAttribute" size="30" autocomplete="off" 
													style="width: 250px" tabindex="24" />
												</td>

											</tr>
											<tr>
												<td class="tblheader-bold" colspan="4" height="20%"><bean:message
														bundle="sessionmanagerResources"
														key="sessionmanager.mandatoryfields" /></td>
											</tr>
											<tr>
											   <td colspan="5" class="captiontext">
												  <table cellSpacing="0" cellPadding="0" width="90%" border="0" id="mandatoryMappingTable">
													<tr>
														<td align="left" class="tblheader" valign="top" width="20%">
															<bean:message bundle="sessionmanagerResources" key="sessionmanager.field" /> 
																<ec:elitehelp headerBundle="sessionmanagerResources" 
																	text="sessionmanager.field" 
																		header="sessionmanager.field"/>
														</td>
														<td align="left" class="tblheader" valign="top" width="20%">
															<bean:message bundle="sessionmanagerResources" 
																key="sessionmanager.dbfieldname" /> 
																	<ec:elitehelp headerBundle="sessionmanagerResources" 
																		text="sessionmanager.dbfieldname" 
																			header="sessionmanager.dbfieldname"/>
														</td>
														<td align="left" class="tblheader" valign="top" width="20%">
															<bean:message bundle="sessionmanagerResources" 
																key="sessionmanager.referringentity" /> 
																	<ec:elitehelp headerBundle="sessionmanagerResources" 
																	text="sessionmanager.referringentity" 
																		header="sessionmanager.referringentity"/>
														</td>
														<td align="left" class="tblheader" valign="top" width="20%">
															<bean:message bundle="sessionmanagerResources" 
																key="sessionmanager.datatype" /> 
																	<ec:elitehelp headerBundle="sessionmanagerResources" 
																		text="sessionmanager.datatype" 
																			header="sessionmanager.datatype"/>
														</td>
														<td align="left" class="tblheader" valign="top" width="20%">
															<bean:message bundle="sessionmanagerResources" 
																key="sessionmanager.defaultvalue" /> 
																	<ec:elitehelp headerBundle="sessionmanagerResources" 
																		text="sessionmanager.defaultvalue" 
																			header="sessionmanager.defaultvalue"/>
														</td>
													</tr>
													<tr>
														<td align="left" class="labeltext" valign="top">
															<bean:message bundle="sessionmanagerResources" key="sessionmanager.sessionid.fieldname" /> 
															<html:hidden property="field" value="Session ID"/>
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="dbField" property="dbField" styleClass="dbField"  maxlength="30" style="width:100%" value="ACCT_SESSION_ID" tabindex="25" />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="referingAttrib" styleClass="referAttributes" value="0:44" property="referingAttrib" size="20" maxlength="50" style="width:100%" tabindex="26"  />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:hidden property="mandatoryFieldDataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>"/>
															<html:select name="createSessionManagerDetailForm" style="width:100%" tabindex="27" disabled="true" styleId="mandatoryFieldDataType" property="mandatoryFieldDataType" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">
																	<html:option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</html:option>
															</html:select>

														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="defaultVal" property="defaultVal" size="20" maxlength="30" style="width:100%" tabindex="28" />
														</td>
													</tr>
													<tr>
														<td align="left" class="labeltext" valign="top">
															<bean:message bundle="sessionmanagerResources" key="sessionmanager.servicetypefield" /> 
															<html:hidden property="field" value="PDP Type"/>
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="dbField" property="dbField" styleClass="dbField" size="20" value="NAS_PORT_TYPE" maxlength="30" style="width:100%" tabindex="29" />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="referingAttrib" property="referingAttrib" styleClass="referAttributes" size="20" value="0:61" maxlength="50" style="width:100%" tabindex="30"  />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:hidden property="mandatoryFieldDataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>"/>
															<html:select name="createSessionManagerDetailForm" style="width:100%" disabled="true" styleId="mandatoryFieldDataType" tabindex="31"  property="mandatoryFieldDataType" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">
																	<html:option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</html:option>
															</html:select>
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="defaultVal" property="defaultVal" size="20" maxlength="30" style="width:100%" tabindex="32"  />
														</td>
													</tr>
													<tr>
														<td align="left" class="labeltext" valign="top">
															<bean:message bundle="sessionmanagerResources" key="sessionmanager.sessiontimeouts" /> 
															<html:hidden property="field" value="Session Timeout"/>
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="dbField" styleClass="dbField" property="dbField" size="20" maxlength="30" value="SESSION_TIMEOUT" style="width:100%" tabindex="33" />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="referingAttrib" property="referingAttrib" styleClass="referAttributes" value="$RES(0:27)" size="20" maxlength="50" style="width:100%" tabindex="34"  />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:hidden property="mandatoryFieldDataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>"/>
															<html:select name="createSessionManagerDetailForm" style="width:100%" disabled="true" styleId="mandatoryFieldDataType" property="mandatoryFieldDataType" tabindex="35" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">
																	<html:option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</html:option>
															</html:select>
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="defaultVal" property="defaultVal" size="20" maxlength="30" style="width:100%" tabindex="36" />
														</td>
												</tr>
												<tr>
														<td align="left" class="labeltext" valign="top">
															<bean:message bundle="sessionmanagerResources" key="sessionmanager.aaaserverid" /> 
															<html:hidden property="field" value="AAA ID"/>
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="dbField" property="dbField" styleClass="dbField" size="20" maxlength="30" value="AAA_ID" style="width:100%" tabindex="37" />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="referingAttrib" property="referingAttrib" styleClass="referAttributes" value="21067:143" size="20" maxlength="50" style="width:100%" tabindex="38" />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:hidden property="mandatoryFieldDataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>"/>
															<html:select name="createSessionManagerDetailForm" style="width:100%" disabled="true" styleId="mandatoryFieldDataType" tabindex="39" property="mandatoryFieldDataType" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">
																	<html:option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</html:option>
															</html:select>
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="defaultVal" property="defaultVal" size="20" maxlength="30" style="width:100%" tabindex="40" />
														</td>
											</tr>
											<tr>
														<td align="left" class="labeltext" valign="top">
															<bean:message bundle="sessionmanagerResources" key="sessionmanager.nasid" /> 
															<html:hidden property="field" value="NAS ID"/>
												</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="dbField" property="dbField" styleClass="dbField" size="20" maxlength="30" value="NAS_IDENTIFIER" style="width:100%" tabindex="41" />
												</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="referingAttrib" property="referingAttrib" styleClass="referAttributes" value="0:32" size="20" maxlength="50" style="width:100%" tabindex="42"  />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:hidden property="mandatoryFieldDataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>"/>
															<html:select name="createSessionManagerDetailForm" style="width:100%" disabled="true" tabindex="43" styleId="mandatoryFieldDataType" property="mandatoryFieldDataType" size="1" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">
																	<html:option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</html:option>
															</html:select>
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="defaultVal" property="defaultVal" size="20" maxlength="30" style="width:100%" tabindex="44" />
														</td>
											</tr>
													<tr>
														<td align="left" class="labeltext" valign="top">
															<bean:message bundle="sessionmanagerResources" key="sessionmanager.useridentity" /> 
															<html:hidden property="field" value="User Identity"/> 
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="dbField" styleClass="dbField" property="dbField" size="20" maxlength="30" value="USER_NAME" style="width:100%" tabindex="45" />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="referingAttrib" property="referingAttrib" styleClass="referAttributes" value="0:1" size="20" maxlength="50" style="width:100%" tabindex="46" />
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:hidden property="mandatoryFieldDataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>"/>
															<html:select name="createSessionManagerDetailForm" style="width:100%" disabled="true" styleId="mandatoryFieldDataType" property="mandatoryFieldDataType" size="1" tabindex="47" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">
																	<html:option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</html:option>
															</html:select>
														</td>
														<td align="left" class="labeltext" valign="top">
															<html:text styleId="defaultVal" property="defaultVal" size="20" maxlength="30" style="width:100%" tabindex="48" />
														</td>
													</tr>
												</table>
											  </td>
											</tr>

											<tr>
												<td class="tblheader-bold" colspan="4" height="20%"><bean:message
														bundle="sessionmanagerResources"
														key="sessionmanager.dbplugin" /></td>
											</tr>

											<!-- add button  -->

											<tr>
												<td align="left" class="captiontext"><input
													type="button" value="   Add   " class="light-btn"
													onclick="openPopup();" tabindex="49"></td>
											</tr>
											<!--  display added dbplugin list -->

										</table>

									</td>
								</tr>

								<tr>
									<td></td>
									<td width="100%" valign="top">
										<table cellSpacing="0" cellPadding="0" width="90%" border="0"
											id="mappingtbl">
											<tr>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.dbfieldname" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.dbfieldname" 
																	header="sessionmanager.dbfieldname"/>
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.referringentity" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.referringentity" 
																	header="sessionmanager.referringentity"/>
												</td>
												<td align="left" class="tblheader" valign="top" width="25%">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.datatype" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.datatype" 
																	header="sessionmanager.datatype"/>
												</td>
												<td align="left" class="tblheader" valign="top" width="20%">
													<bean:message bundle="sessionmanagerResources"
														key="sessionmanager.defaultvalue" /> 
															<ec:elitehelp headerBundle="sessionmanagerResources" 
																text="sessionmanager.defaultvalue" 	
																	header="sessionmanager.defaultvalue"/>
												</td>
												<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" colspan="3" valign="top">
										&nbsp;</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle">&nbsp;</td>
									<td class="btns-td" valign="middle"><input type="button"
										value="Previous " onclick="history.go(-1)" class="light-btn"
										tabindex="51" /> <input type="button" name="c_btnCreate"
										onclick="validateCreate('<%=dbFieldMapList%>');"
										id="c_btnCreate2" value="Create" class="light-btn"
										tabindex="52"> <input type="reset"
										name="c_btnDeletePolicy"
										onclick="javascript:location.href='<%=basePath%>/initSearchSessionManager.do?/>'"
										value="Cancel" class="light-btn" tabindex="53"></td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>

	<div id="popupfieldMapping" title="Field Mapping"
		style="display: none;">
		<table>
			<tr>
				<td align="left" class="labeltext" valign="top" width="20%"><bean:message
						bundle="sessionmanagerResources" key="sessionmanager.dbfieldname" />
				</td>
				<td align="left" class="labeltext" valign="top" width="32%"><html:text
						styleId="fieldName" property="dbFieldName" size="25"
						maxlength="60" style="width:250px" tabindex="54" /> <font
					color="#FF0000"> *</font></td>
			</tr>

			<tr>
				<td align="left" class="labeltext" valign="top" width="20%"><bean:message
						bundle="sessionmanagerResources"
						key="sessionmanager.referringentity" /></td>
				<td align="left" class="labeltext" valign="top" width="32%">
					<%-- 	<html:text styleId="referEntity" property="referringEntity" size="25" maxlength="60" style="width:200px"/>    --%>
					<input type="text" name="referEntity" id="referEntity" size="30"
					autocomplete="off" onkeyup="setColumnsOnRefEntityAttrTextFields();"
					style="width: 250px" tabindex="55" /> <font color="#FF0000">
						*</font>
				</td>
			</tr>

			<tr>
				<td align="left" class="labeltext" valign="top" width="20%"><bean:message
						bundle="sessionmanagerResources" key="sessionmanager.datatype" />

				</td>
				<td align="left" class="labeltext" valign="top" width="32%"
					tabindex="56"><html:select
						name="createSessionManagerDetailForm" styleId="datatype"
						property="dataType" size="1"
						value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">
						<html:option
							value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</html:option>
						<html:option
							value="<%=Integer.toString(FieldMapping.TIMESTAMP_TYPE)%>">Timestamp</html:option>
					</html:select><font color="#FF0000"> *</font></td>
			</tr>

			<tr>
				<td align="left" class="labeltext" valign="top" width="20%"><bean:message
						bundle="sessionmanagerResources" key="sessionmanager.defaultvalue" /></td>
				<td align="left" class="labeltext" valign="top" width="32%"><html:text
						styleId="defval" property="defaultValue" size="25" maxlength="60"
						style="width:250px" tabindex="57" /></td>
			</tr>
		</table>
	</div>
	<div id="esi" style="display: none;" title="External Systems"></div>
	<div id="nasServersPopup" style="display: none;"
		title="Add Nas Servers">
		<table id="nasTbl" name="nasTbl" cellpadding="0" cellspacing="0"
			width="100%" class="box">
		</table>
	</div>

	<div id="acctProxyServersPopup" style="display: none;"
		title="Add Rad Acct Proxy Servers">
		<table id="acctProxyTbl" name="acctProxyTbl" cellpadding="0"
			cellspacing="0" width="100%" class="box">
		</table>
	</div>
</html:form>
<script>
proxyServerPopup();
nasEsiPopup();
</script>




