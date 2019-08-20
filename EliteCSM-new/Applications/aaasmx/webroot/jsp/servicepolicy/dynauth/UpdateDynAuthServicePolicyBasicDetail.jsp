<%@page import="com.elitecore.aaa.radius.policies.servicepolicy.conf.impl.DynAuthServicePolicyConfigurationData.DBFailureAction"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.servicepolicy.dynauth.forms.UpdateDynAuthServicePolicyBasicDetailForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData"%>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.DynAuthPolicyConstant"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.aaa.radius.policies.servicepolicy.conf.impl.DynAuthServicePolicyConfigurationData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>

<%

DynAuthPolicyInstData dynauthPolicyInst = (DynAuthPolicyInstData) request.getAttribute("dynAuthPolicyInstData");
UpdateDynAuthServicePolicyBasicDetailForm updateDynAuthServicePolicyBasicDetailForm = (UpdateDynAuthServicePolicyBasicDetailForm) request.getAttribute("updateDynAuthServicePolicyBasicDetailForm");
String[] attrids = (String[])request.getAttribute("attrid");
String[] dbfieldVals = (String[])request.getAttribute("fieldList");
String[] defvalue = (String[])request.getAttribute("defVal");
String[] usedicval = (String[])request.getAttribute("mandatory");

String basePath1 = request.getContextPath();


String[] nasInstanceIds = (String[])request.getAttribute("nasInstanceIds");
String[][] nasInstanceNames =  (String[][])request.getAttribute("nasInstanceNames");
List<ExternalSystemInterfaceInstanceData> nasServers = (List<ExternalSystemInterfaceInstanceData>)request.getAttribute("nasInstanceList");	

%>

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>

var externalScriptList = [];

<% 
if( Collectionz.isNullOrEmpty(updateDynAuthServicePolicyBasicDetailForm.getExternalScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : updateDynAuthServicePolicyBasicDetailForm.getExternalScriptList()){ %>
		externalScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

setExpressionData("Radius");

var isValidName;
var jesiNames = new Array();
var jesiInstanceIds = new Array();

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

		var headersArr = new Array(5);
		headersArr[0] = '';
		headersArr[1] = '<bean:message bundle="externalsystemResources" key="esi.name" />';
		headersArr[2] = '<bean:message bundle="externalsystemResources" key="esi.address" />';
		headersArr[3] = '<bean:message bundle="externalsystemResources" key="esi.minlocalport" />';
		headersArr[4] = '<bean:message bundle="externalsystemResources" key="esi.expiredrequestlimitcount" />';
		
		
		initializeData(jesiInstanceIds,jesiNames,'nasTbl','nasCheckboxId',headersArr,'false',count1);
		hideSelectedData('nasClients','nasCheckboxId');
}

function nasServerPopup() {
	 openpopup('nasServersPopup','nasCheckboxId',jesiInstanceIds,jesiNames,'nasClients','false');	
}





var mainArray = new Array();
var count = 0;

$(document).ready(function() {		

	var attridnm =  new Array(<%=attrids.length%>)
	var field =  new Array(<%=dbfieldVals.length%>)
	var defvalnm = new Array(<%=defvalue.length%>);
	var mandatory = new Array(<%=usedicval.length%>);					
		
	<%int j =0;				
	for(j =0;j<attrids.length;j++){%>								

		attridnm[<%=j%>] = '<%=attrids[j]%>';	
		field[<%=j%>] = '<%=dbfieldVals[j]%>';
		defvalnm[<%=j%>] = '<%=defvalue[j]%>';
		mandatory[<%=j%>] = '<%=usedicval[j]%>';
		
		var attributeid = attridnm[<%=j%>];
		var dbfield = field[<%=j%>];
		var defaultval = defvalnm[<%=j%>];		
		var mandatoryval = mandatory[<%=j%>];
			
		if(defaultval == 'null'){
			defaultval = '';
		}
					
		$("#mappingtbl1 tr:last").after("<tr><td class='tblfirstcol'>" + attributeid + "</td><td class='tblfirstcol'>" + dbfield +  "&nbsp;</td><td class='tblrows'>" + defaultval + "&nbsp;</td><td class='tblrows'>" + mandatoryval + "&nbsp;</td><td class='tblrows'><img src='<%=basePath1%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
		   var form = document.forms[0];
		   var attridelement = document.createElement("input");
		   attridelement.type = "hidden";
		   attridelement.name = "attributeidval";
		   attridelement.value = attributeid;

		   var dbfieldelement = document.createElement("input");
		   dbfieldelement.type = "hidden";
		   dbfieldelement.name = "dbfieldVal";
		   dbfieldelement.value = dbfield;

		   var defaultvalelement = document.createElement("input");
		   defaultvalelement.type = "hidden";
		   defaultvalelement.name = "defaultval";
		   defaultvalelement.value = defaultval;

		   var mandatoryelement = document.createElement("input");
		   mandatoryelement.type = "hidden";
		   mandatoryelement.name = "mandatory";
		   mandatoryelement.value = mandatoryval;


		   form.appendChild(attridelement);
		   form.appendChild(dbfieldelement);
		   form.appendChild(defaultvalelement);
		   form.appendChild(mandatoryelement);
		   
		
		mainArray[count] = attributeid;				          		
		count = count + 1;	

		$('#mappingtbl1 td img.delete').live('click',function() {
			
			var removalVal = $(this).closest('tr').find('td:eq(0)').text(); 							

			for(var d=0;d<count;d++){
				var currentVal = mainArray[d];					
				if(currentVal == removalVal){
					mainArray[d] = ' ';
					removeHiddenInputFromForm(removalVal);
					break;
				}
			}		

									

			$(this).parent().parent().remove(); 
		});
		
	<%}%>	  
	
  });

	function removeHiddenInputFromForm(removalVal){
		 var form = document.forms[0];
		 var attrElementArray = document.getElementsByName("attributeidval");
		 var dbFieldElementArray = document.getElementsByName("dbfieldVal");
		 var defaultElementArray = document.getElementsByName("defaultval");
		 var mandatoryElementArray = document.getElementsByName("mandatory");
		 
		 if(attrElementArray.length>0){
				for(var i=0;i<attrElementArray.length;i++){
						if(attrElementArray[i].value==removalVal){

							form.removeChild(attrElementArray[i]);
							form.removeChild(dbFieldElementArray[i]);
							form.removeChild(defaultElementArray[i]);
							form.removeChild(mandatoryElementArray[i]);
							
							return;
						}
				}
			 
		 }
		 
		
	}
	function popup() {	
		$.fx.speeds._default = 1000;
		document.getElementById("popupContact").style.visibility = "visible";		
		$( "#popupContact" ).dialog({
			modal: true,
			autoOpen: false,		
			height: "auto",
			width: 500,		
			buttons:{					
            'Add': function() {
							var attributeid = $('#attributeids').val();
							var dbfield = $('#dbfields').val() ;
				      		var defaultval = $('#defaultvalue').val();
				      		var mandatoryval = $('#mandatory').val();
											      		
				      		if(isNull($('#attributeids').val())){
				      			$('#td1').text("Attribute Ids must be specified.");
				     		}else if(isNull($('#dbfields').val())){
				     			$('#td1').text("Db Field value must be specified.");
				     		}else if(isNull($('#mandatory').val())){
				     			$('#td1').text("Use Dic value must be specified.");
				     		}else{	
				     			var i = 0;							
								var flag = true;												 	
								if(document.getElementById('mappingtbl1').getElementsByTagName('tr').length >= 2){								
									for(i=0;i<mainArray.length;i++){									
										var value = mainArray[i];
										if(value == attributeid){
											$('#td1').text("Mapping with this value is already present so add another value for attribute");
											flag = false;
											break;
										}
									}
								}								
				         		if(flag){
				         			$("#mappingtbl1 tr:last").after("<tr><td class='tblfirstcol'>" + attributeid + "</td><td class='tblfirstcol'>" + dbfield +  "&nbsp;</td><td class='tblrows'>" + defaultval + "&nbsp;</td><td class='tblrows'>" + mandatoryval + "&nbsp;</td><td class='tblrows'><img src='<%=basePath1%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
									   var form = document.forms[0];
									   var attridelement = document.createElement("input");
									   attridelement.type = "hidden";
									   attridelement.name = "attributeidval";
									   attridelement.value = attributeid;

									   var dbfieldelement = document.createElement("input");
									   dbfieldelement.type = "hidden";
									   dbfieldelement.name = "dbfieldVal";
									   dbfieldelement.value = dbfield;

									   var defaultvalelement = document.createElement("input");
									   defaultvalelement.type = "hidden";
									   defaultvalelement.name = "defaultval";
									   defaultvalelement.value = defaultval;

									   var mandatoryelement = document.createElement("input");
									   mandatoryelement.type = "hidden";
									   mandatoryelement.name = "mandatory";
									   mandatoryelement.value = mandatoryval;


									   form.appendChild(attridelement);
									   form.appendChild(dbfieldelement);
									   form.appendChild(defaultvalelement);
									   form.appendChild(mandatoryelement);
									   
				         			
				         			mainArray[count] = attributeid;				          		
					          		count = count + 1;	
					          		$(this).dialog('close'); 
					         	}	
				         		        				    		         			   				         			          				          		
				         	}		         	
            },                
		    Cancel: function() {
            	$('#td1').text("");
            	$(this).dialog('close');
        	}
	        },
    	open: function() {
	        	$('#td1').text("");
    	},
    	close: function() {
    		$('#td1').text("");
    		document.getElementById("attributeids").value = "";
			document.getElementById("dbfields").value = "";
			document.getElementById("defaultvalue").value = "";        		
    		document.getElementById("c_btnCreate2").focus();
    	}			
		});
		$( "#popupContact" ).dialog("open");
		$(retriveTableFields(document.getElementById("databaseDatasourceId").value));
	}	 

	function setColumnsOnTextFields(){
		var dbId = document.getElementById("databaseDatasourceId").value;
		retriveTableFields(dbId);
	}

	function setColumnsForTables(){
		setColumnsOnTextFields();
	}

	function retriveTableFields(dbId) {
		var dbFieldStr;
		var tableName = document.getElementById("tableName").value;
		$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
			dbFieldStr = data.substring(1,data.length-3);
			var dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split(", ");
			setFields("dbfields",dbFieldArray);
			return dbFieldArray;
		});	
		
	}
	
function validate()
{	

	var nasclients = document.getElementById("nasClients");
	var isInvalidESI = false;
	var isESIConfigured = false ;
	
	if(nasclients!=null){
		if(nasclients.options.length==0){
			alert('NAS Client must be specified');
			return false;
		}
		selectAll(nasclients);
	}
	 
	
	if(isNull(document.forms[0].name.value)){
		alert('Policy Name must be specified');
		document.forms[0].name.focus();
		return;
	}
	
	if(isNull(document.forms[0].tableName.value)){
		alert('Table Name must be specified');
		document.forms[0].tableName.focus();
		return;
	}
	
	if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return;
	}
	
	if(isNull(document.forms[0].ruleSet.value)){
		alert('Policy Selection Rule must be specified');
		document.forms[0].ruleSet.focus();
		return;
	}

	if(!validateName(document.forms[0].name.value)){
		alert('Policy Name should have following characters. A-Z, a-z, 0-9, _ and - ');
		document.forms[0].name.focus();
		return;
	}
	
	var nasTableObj = $('#nasMappingtbl');
	var nasClientsData = [];
	var nasClientsJson = [];
	var orderNumber = 0;
	
	nasTableObj.find('tr').each(function(){
		var ruleset,translationMapping,script,clientsData = [];
		
	   if(typeof $(this).find("input[name='ruleset']").val() !== 'undefined'){
		   ruleset=  $(this).find("input[name='ruleset']").val();
	   }
	   
	   if(typeof $(this).find("select[name='translationMapConfigId']").val() !== 'undefined'){
		   translationMapping=  $(this).find("select[name='translationMapConfigId']").val();
	   }
	   
	   if(typeof $(this).find("input[name='script']").val() !== 'undefined'){
		   script=  $(this).find("input[name='script']").val();
	   }
	   
	   
	 var innerTableObj = $(this).find('.nasClienttable');
	  
	 innerTableObj.find('tr').each(function (i, el) {
	        var $tds = $(this).find('td');
	        var serverId = '';
	        var loadFactor = '';
	        if(typeof $($tds).find("select[name='esiId']").val() !== 'undefined'){
	        	if($($tds).find("select:first").val() == "0"){
	        		isInvalidESI = true;
	        		isESIConfigured = true;
	        	}
	        	serverId = $($tds).find("select:first").val();
	        }
	        if(typeof $($tds).find("select[name='loadFactor']").val() !== 'undefined'){
	        	loadFactor = $($tds).find("select[name='loadFactor']").val();
	        }
	        if(!isEmpty(serverId) || !isEmpty(loadFactor)){
	        	clientsData.push({'esiId':serverId,'loadFactor':loadFactor});
	        }
	    });
		if(!isEmpty(ruleset) || !isEmpty(translationMapping) || !isEmpty(script)){
			orderNumber++;
			nasClientsData.push({'orderNumber':orderNumber,'ruleset':ruleset,'translationMapConfigId':translationMapping,'script':script,'esiListData':clientsData});
		}else if(isEmpty(ruleset) && isEmpty(translationMapping) && isEmpty(script)){
			if(clientsData.length > 0){
				orderNumber++;
				nasClientsData.push({'orderNumber':orderNumber,'ruleset':ruleset,'translationMapConfigId':translationMapping,'script':script,'esiListData':clientsData});
			}
		}
	}); 
	$('#nasClientsJson').val(JSON.stringify(nasClientsData));
	
	if(isInvalidESI){
		alert('Please select at least one ESI Server');
		return;
	}
	
	document.forms[0].action.value="update";
	document.forms[0].submit();
}

function validateName(val){

	var test1 = /(^[A-Za-z0-9-_]*$)/;
	var regexp =new RegExp(test1);
	if(regexp.test(val)){
		return true;
	}
	return false;
}

function setColumnsOnDbAttrTextFields(){
	var attrVal = document.getElementById("attributeids").value;
	retriveRadiusDictionaryAttributes(attrVal,"attributeids");
}

function selectAll(selObj){
	
	for(var i=0;i<selObj.options.length;i++){
		selObj.options[i].selected = true;
	}
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DYNA_AUTH_POLICY%>',searchName,'update','<%=dynauthPolicyInst.getDynAuthPolicyId()%>','verifyNameDiv');
}
function addNASClientRow(templateId,tableId){
	var tableRowStr = $("#"+templateId).find("tr");
	$("#"+tableId).append("<tr>"+$(tableRowStr).html()+"</tr>");
	setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
}

function addMoreServer(obj){
	var tableObj=$(obj).parent().parent().parent();
	var htmlSource="<tr>"+
	"<td align='left' class='top-border labeltext'width='15%' id='tbl_attrid'>"+
	"<select name='esiId' class='noborder' style='width:100%;'><option value='0'>--Select--</option>"+
	<%for(ExternalSystemInterfaceInstanceData esiData : nasServers){%>
	"<option value='<%=esiData.getEsiInstanceId()%>'><%=esiData.getName()%></option>"+
	<%}%>
	"</select></td>"+
	"<td align='left' class='top-border labeltext' width='12%'>"+
	"<select  id='loadFactor' name='loadFactor' class='noborder' style='width:100%;'><option value='0'>0</option><option selected='selected' value='1'>1</option><option value='2'>2</option><option value='3'>3</option><option value='4'>4</option><option value='5'>5</option><option value='6'>6</option><option value='7'>7</option><option value='8'>8</option><option value='9'>9</option><option value='10'>10</option></select></td>"+
	"<td align='center' class='top-border labeltext' valign='top' width='3%'><img value='top' src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' style='padding-right: 5px; padding-top: 5px;' height='14' tabindex='10' />&nbsp;</td></tr>";
	$(tableObj).append(htmlSource);
}

function addESIData(tableObj,esiId,loadFactor){
	var htmlSource="<tr>"+
	"<td align='left' class='top-border labeltext' valign='top' width='15%' id='tbl_attrid'>"+
	"<select name='esiId' class='noborder' style='width:100%;'><option value='0'>--Select--</option>"+
	<%for(ExternalSystemInterfaceInstanceData esiData : nasServers){%>
	"<option value='<%=esiData.getEsiInstanceId()%>'><%=esiData.getName()%></option>"+
	<%}%>
	"</select></td>"+
	"<td align='left' class='top-border labeltext' valign='top' width='12%'>"+
	"<select  id='loadFactor' name='loadFactor' class='noborder' style='width:100%;'><option value='0'>0</option><option selected='selected' value='1'>1</option><option value='2'>2</option><option value='3'>3</option><option value='4'>4</option><option value='5'>5</option><option value='6'>6</option><option value='7'>7</option><option value='8'>8</option><option value='9'>9</option><option value='10'>10</option></select></td>"+
	"<td align='center' class='top-border labeltext' valign='top' width='3%'><img value='top' src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' style='padding-right: 5px; padding-top: 5px;' height='14' tabindex='10' />&nbsp;</td></tr>";
	$(tableObj).append(htmlSource);
	var tableObj= $(tableObj).find("tr:last");
	var esiObj=$(tableObj).find("select[name='esiId']");
	$(esiObj).val(esiId);
	
	var loadFactorObj=$(tableObj).find("select[name='loadFactor']");
	$(loadFactorObj).val(loadFactor);
}

function readNasClientDetails(){
	var nasClientsList = [];
	<logic:iterate id="obj" name="updateDynAuthServicePolicyBasicDetailForm" property="nasClientsList">
			var esiList = [];
		    <logic:iterate id="esiData" name="obj" property="esiListData">
		   	 	esiList.push({'esiId':'<bean:write name="esiData" property="esiId" />','loadFactor':'<bean:write name="esiData" property="loadFactor" />'});
		    </logic:iterate>
		    nasClientsList.push({'ruleset':'<bean:write name="obj" property="ruleset" />','translationMapConfigId':'<bean:write name="obj" property="translationMapConfigId" />','script':'<bean:write name="obj" property="script" />','esiListData':esiList});
	</logic:iterate>
	return nasClientsList;
}

var decodeEntities = (function() {
	  var element = document.createElement('div');

	  function decodeHTMLEntities (str) {
	    if(str && typeof str === 'string') {
	      str = str.replace(/<script[^>]*>([\S\s]*?)<\/script>/gmi, '');
	      str = str.replace(/<\/?\w(?:[^"'>]|"[^"]*"|'[^']*')*>/gmi, '');
	      element.innerHTML = str;
	      str = element.textContent;
	      element.textContent = '';
	    }

	    return str;
	  }

	  return decodeHTMLEntities;
	})();

$(document).ready(function(){
	
	var clientDetails = readNasClientDetails();
	
	$.each(clientDetails, function(key,value){
		addNASClientRow('nasMappingTable','nasMappingtbl');
		
		var findTableObj = $("#nasMappingtbl"+" tr:last").parent().parent().parent().parent();
		console.log(findTableObj);
		
		var rulesetrow = $(findTableObj).find("input[name='ruleset']");
		$(rulesetrow).val(decodeEntities(value.ruleset));
		
		//set translationMapConfigId
		var translationMapConfigIdrow = $(findTableObj).find("select[name='translationMapConfigId']");
		$(translationMapConfigIdrow).val(value.translationMapConfigId);
			
		//set Script
		var scriptrow = $(findTableObj).find("input[name='script']");
		$(scriptrow).val(value.script);
		
		var esiTableObj = $(findTableObj).find('#nasClienttable');
		 
	 	esiTableObj.find('tr.esi-data').each(function(){
	 		$(this).remove();
	 	});
		
		$.each(value.esiListData, function(esiKey,esiValue){
			addESIData(esiTableObj,esiValue.esiId,esiValue.loadFactor);
		});
		
		
	});
	
	/* Script Autocomplete */
	setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
	
	$('#nasMappingtbl td img.delete').live('click',function() {
		$(this).parent().parent().remove();
	});
});
</script>
<html:form action="/updateDynAuthServicePolicyBasicDetail">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">

		<html:hidden name="updateDynAuthServicePolicyBasicDetailForm" styleId="dynAuthPolicyId" property="dynAuthPolicyId" value="<%=dynauthPolicyInst.getDynAuthPolicyId()%>" />
		<html:hidden name="updateDynAuthServicePolicyBasicDetailForm" styleId="action" property="action" />
		<html:hidden name="updateDynAuthServicePolicyBasicDetailForm" styleId="auditUId" property="auditUId" />
		<html:hidden name="updateDynAuthServicePolicyBasicDetailForm" styleId="nasClientsJson" property="nasClientsJson" />
		<bean:define id="dynAuthPolicyInstBean" name="dynAuthPolicyInstData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.dynauth.data.DynAuthPolicyInstData" />

		<tr>
			<td valign="top" align="right">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" width="27%" colspan="4">
							<bean:message bundle="servicePolicyProperties" key="basic.details" />
						</td>
					</tr>
					<tr>

						<td align="left" class="captiontext" valign="top" width="27%">
							<bean:message key="general.name" /> 
							<ec:elitehelp  header="dynauthpolicy.name" headerBundle="servicePolicyProperties" text="dynaauthpolicy.name" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap" >
							<html:text property="name" size="35" styleId="name" onkeyup="verifyName();" style="width: 250;" tabindex="1" />
							<font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:checkbox styleId="status" property="status" value="1" tabindex="2" />Active
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="16%">
							<bean:message key="general.description" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:textarea property="description" styleId="description" rows="1" cols="50" style="width: 250;" tabindex="3" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="16%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.ruleset" />
							<ec:elitehelp  header="servicepolicy.dynauthpolicy.ruleset" headerBundle="servicePolicyProperties" text="dynaauthpolicy.ruleset" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:textarea property="ruleSet" styleId="ruleSet" rows="2" cols="50" style="width: 250;" tabindex="4" />
							<font color="#FF0000"> *</font> 
							<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleSet');" tabindex="5" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="16%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.resattrs" /> 
							<ec:elitehelp  header="servicepolicy.dynauthpolicy.resattrs" headerBundle="servicePolicyProperties" text="dynaauthpolicy.resattribute" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:textarea property="responseAttributes" styleId="responseAttributes" rows="2" cols="50" style="width: 250;" tabindex="6" />
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.validatepacket" />
							<ec:elitehelp  header="servicepolicy.dynauthpolicy.validatepacket" headerBundle="servicePolicyProperties" text="dynauthpolicy.validatepacket" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:select property="validatePacket" styleClass="labeltext" styleId="validatePacket" style="width: 130;" tabindex="7">
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>
							</html:select>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message  bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eventtimestamp" /> 
							<ec:elitehelp  header="servicepolicy.dynauthpolicy.eventtimestamp" headerBundle="servicePolicyProperties" text="dynaauthpolicy.timestampt" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:text property="eventTimestamp" styleId="eventTimestamp" maxlength="50" size="45" style="width: 250;" tabindex="11" />
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.mapdetails" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.databaseds" /> 
							<ec:elitehelp  header="servicepolicy.dynauthpolicy.databaseds" headerBundle="servicePolicyProperties" text="dynaauthpolicy.dbdatasource" ></ec:elitehelp>
						</td>

						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:select property="databaseDatasourceId" styleClass="labeltext" styleId="databaseDatasourceId" style="width: 200;" tabindex="8">
								<html:option value="">-Select-</html:option>
								<% if(updateDynAuthServicePolicyBasicDetailForm.getDatabaseDatasourceList()!=null){ %>
								<logic:iterate id="databaseDatasource" name="updateDynAuthServicePolicyBasicDetailForm" property="databaseDatasourceList" type="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData">
									<html:option value="<%=databaseDatasource.getDatabaseId()%>"><%=databaseDatasource.getName()%></html:option>
								</logic:iterate>
								<%} %>
							</html:select>
						</td>

					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.tablename" /> 
							<ec:elitehelp  header="servicepolicy.dynauthpolicy.tablename" headerBundle="servicePolicyProperties" text="dynaauthpolicy.table" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3"> 
							<html:text property="tableName" styleId="tableName" maxlength="2000" size="30" tabindex="9" style="width:200px;" />
							<font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.dbfailureaction" /> 
							<ec:elitehelp  header="servicepolicy.dynauthpolicy.dbfailureaction" headerBundle="servicePolicyProperties" text="servicepolicy.dynauthpolicy.dbfailureaction" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:select property="dbFailureAction" styleId="dbFailureAction" name="addDynAuthServicePolicyForm" style="width: 200;" value="<%=String.valueOf(updateDynAuthServicePolicyBasicDetailForm.getDbFailureAction())%>">
								<logic:iterate id="dbFailureActionInst"  collection="<%=DBFailureAction.values() %>"> 
									<%String displayText=((DBFailureAction)dbFailureActionInst).name(); %>
									<html:option value="<%=displayText%>"><%=displayText%></html:option>
								</logic:iterate>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eligiblesession" />
							<ec:elitehelp  header="servicepolicy.dynauthpolicy.eligiblesession" headerBundle="servicePolicyProperties" text="dynaauthpolicy.session" ></ec:elitehelp>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:select property="eligibleSessions" styleClass="labeltext" styleId="eligibleSessions" style="width: 200;" tabindex="10">
								<html:option value="<%=Integer.toString(DynAuthPolicyConstant.NONE)%>"> <bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eligiblesession.none" />
								</html:option>
								<html:option value="<%=Integer.toString(DynAuthPolicyConstant.RECENT)%>"> 
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eligiblesession.recent" />
								</html:option>
								<html:option value="<%=Integer.toString(DynAuthPolicyConstant.OLDEST)%>"> <bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eligiblesession.oldest" />
								</html:option>
								<html:option value="<%=Integer.toString(DynAuthPolicyConstant.ALL)%>"> <bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eligiblesession.all" /> </html:option>
							</html:select>
						</td>
					</tr>
					<tr>
						<td class="captiontext" valign="top" colspan="2" id="button">
							<input type="button" onclick="popup()" value=" Add Mapping " class="light-btn" style="size: 140px" tabindex="12">
						</td>
					</tr>

					<tr>

						<td width="90%" colspan="3" valign="top" class="captiontext">
							<table cellSpacing="0" cellPadding="0" width="90%" border="0" id="mappingtbl1" class="box">
								<tr>
									<td align="left" class="tblheader" valign="top" width="20%" id="tbl_attrid">
										Attribute Ids 
										<ec:elitehelp  header="servicepolicy.dynauthpolicy.attributeids" headerBundle="servicePolicyProperties" text="dynauthpolicy.attributeids" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader" valign="top" width="25%">
										DB Field 
										<ec:elitehelp  header="servicepolicy.dynauthpolicy.dbfield" headerBundle="servicePolicyProperties" text="dynauthpolicy.attributeids" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										Default Value 
										<ec:elitehelp  header="servicepolicy.dynauthpolicy.defaultvalue" headerBundle="servicePolicyProperties" text="dynauthpolicy.defaultvalue" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader" valign="top" width="25%">
										Mandatory
										<ec:elitehelp  header="servicepolicy.dynauthpolicy.mandatory" headerBundle="servicePolicyProperties" text="dynauthpolicy.mandatory" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader" valign="top" width="10%">Remove</td>
								</tr>
							</table>
						</td>
						<td width="10">&nbsp;</td>
					</tr>

					<tr>
						<td style="height: 100%">&nbsp;</td>
					</tr>
					<tr>
						<td class="tblheader-bold" colspan="7">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient" /> 
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="2" id="button">
							<input type="button" onclick="addNASClientRow('nasMappingTable','nasMappingtbl');" value=" Add NAS Clients " class="light-btn" style="size: 140px" tabindex="12">
						</td>
					</tr>
					<tr>
						<td width="98%" colspan="3" valign="top" class="captiontext">
							<table cellSpacing="0" cellPadding="0" width="98%" border="0" id="nasMappingtbl" class="box nasMappingtbl">
								<tr>
									<td align="left" class="tblheader" valign="top" width="20%" id="tbl_attrid">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient.ruleset" />
										<ec:elitehelp  header="dynauthpolicy.nas.ruleset" headerBundle="servicePolicyProperties" text="dynauthpolicy.nas.ruleset" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader" valign="top" width="35%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient.nas" />
										<ec:elitehelp  header="dynauthpolicy.nas.nas" headerBundle="servicePolicyProperties" text="dynauthpolicy.nas.nas" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient.translationmapping" />
										<ec:elitehelp  header="dynauthpolicy.nas.translationmapping" headerBundle="servicePolicyProperties" text="dynauthpolicy.nas.translationmapping" ></ec:elitehelp>
									</td>
									<td align="left" class="tblheader" valign="top" width="20%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient.script" />
										<ec:elitehelp  header="dynauthpolicy.nas.script" headerBundle="servicePolicyProperties" text="dynauthpolicy.nas.script" ></ec:elitehelp>
									</td>
									<td align="center" class="tblheader" valign="top" width="10%" >
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient.remove" />
									</td>
								</tr>
							</table>
						</td>
						<td width="10">&nbsp;</td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle">&nbsp;</td>
							<td class="btns-td" valign="middle" colspan="3">
								<input type="button" value="Update " class="light-btn" onclick="validate()" id="c_btnCreate2" tabindex="18" /> 
								<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath1%>/viewDynAuthServicePolicy.do?dynauthpolicyid=<%=dynAuthPolicyInstBean.getDynAuthPolicyId()%>'" value="Cancel" class="light-btn" tabindex="19"></td>
						</tr>

				</table>
			</td>
		</tr>
		<tr>
			<td class="small-gap" colspan="2">&nbsp;
				<div id="popupContact" style="display: none;" title="Add Field Mapping">
					<table>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.attributeids" />
							</td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<%-- 	<html:text styleId="attributeids" property="attributeids" size="25" maxlength="50" style="width:200px"/>    --%>
								<input type="text" name="attributeids" id="attributeids" size="30" autocomplete="off" onkeyup="setColumnsOnDbAttrTextFields();" style="width: 250px" tabindex="20" /> 
								<font color="#FF0000"> *</font>
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.dbfield" />
							</td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<html:text styleId="dbfields" property="dbfield" size="25" maxlength="30" style="width:250px" tabindex="21" />
								<font color="#FF0000"> *</font>
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="30%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.defaultvalue" />
							</td>
							<td align="left" class="labeltext" valign="top" width="70%">
								<html:text styleId="defaultvalue" property="defaultvalue" size="25" maxlength="50" style="width:250px" tabindex="22" />
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.mandatory" />
							</td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<select id="mandatory" tabindex="23">
									<option>True</option>
									<option>False</option>
							</select>
							</td>
						</tr>
						<div id="td1" style="color: red;" class="labeltext"></div>
					</table>
				</div>
			</td>
		</tr>




	</table>

	<div id="nasServersPopup" style="display: none;" title="Add Nas Servers">
		<table id="nasTbl" name="nasTbl" cellpadding="0" cellspacing="0" width="100%" class="box">
		</table>
	</div>
</html:form>
<!-- Mapping Table Row template -->
<table  id="nasMappingTable" style="display: none;" >
	<tr>
		<td style="border-bottom-style: solid;border-bottom-width: 1px;border-bottom-color: #CCCCCC;" width="20%">
			<input type="text" name="ruleset" id="ruleset"  maxlength="1000" size="28" style="width: 100%" />
		</td>
		<td  width="35%">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="nasClienttable" class="box nasClienttable">
					<tr>
						<td align="left" class="tblheader-policy" width="15%" id="name">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient.servername" />
							&nbsp;<!-- <span title="Add Server" onclick="addMoreServer(this)" class="add-proxy-server"></span> -->
							<img alt="" src="<%=basePath%>/images/plus.jpg" onclick="addMoreServer(this)" title="Add Server" />
						</td>
						<td align="left" class="tblheader-policy"  width="12%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient.loadfactor" />
						</td>
						<td align="left" class="tblheader-policy" valign="top" width="3%">Remove</td>
			    	</tr>
			    	<tr class="esi-data">
						<td align="left" class="labeltext"  width="15%" id="tbl_attrid">
							<select name="esiId" id="esiId" class="noborder labeltext" style="width:100%;">
								<option value='0'>--Select--</option>
								<%for(ExternalSystemInterfaceInstanceData esiData:nasServers){ %>
									<option value="<%=esiData.getEsiInstanceId()%>"><%=esiData.getName()%></option>
								<%} %>
							</select>
						</td>
						<td align="left" class="labeltext"  width="12%">
							<select  name="loadFactor" class="noborder labeltext" style="width:100%;">
								<option value="0">0</option>
								<option value="1" selected="selected">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
								<option value="6">6</option>
								<option value="7">7</option>
								<option value="8">8</option>
								<option value="9">9</option>
								<option value="10">10</option>
							</select>
						</td>
						<td align="center" class="labeltext"  width="3%">
							<img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" tabindex="10" />&nbsp;
						</td>
			    	</tr>
				</table>
		</td>
		<td class="tbldynapolicy" width="20%">
			<select
			name="translationMapConfigId"
			id="translationMapConfigId" style="width: 100%;">
				<option value="">--Select--</option>
				<optgroup label="Translation Mapping" class="labeltext">
					<logic:iterate id="translationMapping"
						name="updateDynAuthServicePolicyBasicDetailForm"
						property="translationMappingConfDataList"
						type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
						<option
							value="<%=ConfigConstant.TRANSLATION_MAPPING + translationMapping.getTranslationMapConfigId()%>"
							class="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></option>
					</logic:iterate>

				</optgroup>

				<optgroup label="Copy Packet Mapping" class="labeltext">
					<logic:iterate id="copyPacketMapping"
						name="updateDynAuthServicePolicyBasicDetailForm"
						property="copyPacketMappingConfDataList"
						type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData">
						<option
							value="<%= ConfigConstant.COPY_PACKET_MAPPING + copyPacketMapping.getCopyPacketTransConfId()%>"
							styleClass="<%=ConfigConstant.COPY_PACKET_MAPPING%>"><%=copyPacketMapping.getName()%></option>
					</logic:iterate>
				</optgroup>
		</select>
		</td>
		<td class="tbldynapolicy" width="20%">
			<input type="text" name="script" id="script"  maxlength="1000" size="28" style="width: 100%" class="esiScriptAutocomplete" />
		</td>
		<td class="tbldynapolicy" width="10%" align="center">
			<img value="top" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 5px; padding-top: 5px;" height="14" tabindex="10" />
		</td>
	</tr>
</table>

<script>
setColumnsOnTextFields();
nasEsiPopup();
</script>

<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>
