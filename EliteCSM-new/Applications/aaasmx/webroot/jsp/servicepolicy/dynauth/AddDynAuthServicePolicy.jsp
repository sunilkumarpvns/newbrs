<%@page import="com.elitecore.aaa.radius.policies.servicepolicy.conf.impl.DynAuthServicePolicyConfigurationData.DBFailureAction"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.DynAuthPolicyConstant"%> 
<%@page import="com.elitecore.elitesm.web.servicepolicy.dynauth.forms.AddDynAuthServicePolicyForm"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData" %>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.aaa.radius.policies.servicepolicy.conf.impl.DynAuthServicePolicyConfigurationData" %>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>

<%
	String basePath = request.getContextPath();
	AddDynAuthServicePolicyForm addDynAuthServicePolicyForm = (AddDynAuthServicePolicyForm) request.getSession().getAttribute("addDynAuthServicePolicyForm");
	
	String[] nasInstanceIds = (String[])request.getSession().getAttribute("nasInstanceIds");
	String[][] nasInstanceNames =  (String[][])request.getSession().getAttribute("nasInstanceNames");
	List<ExternalSystemInterfaceInstanceData> nasServers = (List<ExternalSystemInterfaceInstanceData>)request.getSession().getAttribute("nasInstanceList");	
	String statusVal=(String)request.getParameter("status");
	
%>

<style>
.main-css {
	height: 8px;
}
</style>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>

var externalScriptList = [];

<% 
if( Collectionz.isNullOrEmpty(addDynAuthServicePolicyForm.getExternalScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : addDynAuthServicePolicyForm.getExternalScriptList()){ %>
		externalScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

setExpressionData("Radius");
 var isValidName;
 var mainArray = new Array();
 var count = 0;
	
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

							var name = $('#attributeids').val();
							var name0 = $('#dbfields').val() ;
				      		var name2 = $('#defaultvalue').val();
				      		var name3 = $('#mandatory').val();
											      		
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
										if(value == name){
											$('#td1').text("Mapping with this value is already present so add another value for attribute");
											flag = false;
											break;
										}
									}
								}								
				         		if(flag){
				         			$("#mappingtbl1 tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'attributeidval' value='" + name + "'/>" +"</td><td class='tblfirstcol'>" + name0 + "<input type='hidden' name = 'dbfieldVal' value='" + name0 + "'/>" + "&nbsp;</td><td class='tblrows'>" + name2 + "&nbsp" +"<input type='hidden' name = 'defaultval' value='" + name2 + "'/>" + "&nbsp;</td><td class='tblrows'>" + name3 + "<input type='hidden' name = 'mandatory' value='" + name3 + "'/>"+"&nbsp;</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");								
					         		mainArray[count] = name;				          		
					          		count = count + 1;					          		
					          		$(this).dialog('close');
					         	}
				         		$('#mappingtbl1 td img.delete').live('click',function() {
					   				var removalVal = $(this).closest('tr').find('td:eq(0)').text();
					   				for(var d=0;d<count;d++){
					   					var currentVal = mainArray[d];					
					   					if(currentVal == removalVal){
					   						mainArray[d] = '';
					   						break;
					   					}
					   				}								
					   			$(this).parent().parent().remove(); });	         				    		         			   				         			          				          		
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
		
		
		initializeData(jesiInstanceIds,jesiNames,'nasTbl','nasServerCheckboxId',headersArr,'false',count1);
}

function nasServerPopup() {
	 openpopup('nasServersPopup','nasServerCheckboxId',jesiInstanceIds,jesiNames,'nasClients','false');	
}

$(document).ready(function() {
	var chkBoxVal='<%=statusVal%>';
	if(chkBoxVal=='Inactive'){
		document.getElementById("status").checked=false;
	}else{
		document.getElementById("status").checked=true;
	}
	
	$('#nasMappingtbl td img.delete').live('click',function() {
		$(this).parent().parent().remove();
	});
	
	/* Script Autocomplete */
	setSuggestionForScript(externalScriptList, "esiScriptAutocomplete");
});

function selectAll(selObj){
	
	for(var i=0;i<selObj.options.length;i++){
		selObj.options[i].selected = true;
	}
}


function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DYNA_AUTH_POLICY%>',searchName,'create','','verifyNameDiv');
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

</script>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
	<tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
		<td>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
				<tr>
					<td cellpadding="0" cellspacing="0" border="0" width="100%"
						class="box">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
							<tr>
								<td class="table-header">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.create" />
								</td>
							</tr>
							<tr>
								<td colspan="3">
									<html:form action="/addDynAuthServicePolicy" styleId="dynaAuthForm">
										<html:hidden property="nasClientsJson" value="" styleId="nasClientsJson"/>
										<table name="c_tblCrossProductList" width="100%" border="0" cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" class="tblheader-bold" valign="top" width="27%">
													<bean:message bundle="servicePolicyProperties" key="basic.details" />
												</td>
											</tr>
											<tr>
												<td>
													<table border="0" width="100%" cellpadding="0" cellspacing="0">
														<tr>
															<td align="left" class="captiontext" valign="top" width="25%">
																<bean:message key="general.name" /> 
																<ec:elitehelp  header="dynauthpolicy.name" headerBundle="servicePolicyProperties" text="dynaauthpolicy.name" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" nowrap="nowrap" >
																<html:text property="name" size="40" onkeyup="verifyName();" styleId="name" style="width: 250;" tabindex="1" />
																<font color="#FF0000"> *</font>
																<div id="verifyNameDiv" class="labeltext"></div>
															</td>
															<td align="left" class="labeltext" valign="top">
																<input type="checkbox" name="status" id="status" value="1" checked="true" tabindex="2" />
																Active
															</td>
														</tr>

														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message key="general.description" />
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:textarea property="description" styleId="description" rows="2" cols="60" style="width: 250;" tabindex="3" />
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.ruleset" /> 
																<ec:elitehelp  header="servicepolicy.dynauthpolicy.ruleset" headerBundle="servicePolicyProperties" text="dynaauthpolicy.ruleset" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:textarea property="ruleSet" styleId="ruleSet" rows="2" cols="60" style="width: 250;" tabindex="4" />
																<font color="#FF0000"> *</font> &nbsp;&nbsp; 
																<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleSet');" tabindex="5" />
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.resattrs" /> 
																<ec:elitehelp  header="servicepolicy.dynauthpolicy.resattrs" headerBundle="servicePolicyProperties" text="dynaauthpolicy.resattribute" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:textarea property="responseAttributes" styleId="responseAttribues" rows="2" cols="60" style="width: 250;" tabindex="6" />
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
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eventtimestamp" /> 
																<ec:elitehelp  header="servicepolicy.dynauthpolicy.eventtimestamp" headerBundle="servicePolicyProperties" text="dynaauthpolicy.timestampt" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:text property="eventTimestamp" styleId="eventTimestamp" maxlength="50" size="50" style="width: 250;" tabindex="11" />
															</td>
														</tr>
														<tr>
															<td align="left" class="tblheader-bold" valign="top" colspan="7">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.mapdetails" />
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.databaseds" /> 
																<ec:elitehelp  header="servicepolicy.dynauthpolicy.databaseds" headerBundle="servicePolicyProperties" text="dynaauthpolicy.dbdatasource" ></ec:elitehelp>
															</td>

															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:select property="databaseDatasourceId" styleClass="labeltext" styleId="databaseDatasourceId" style="width: 200;" onchange="setColumnsOnTextFields();" tabindex="8">
																	<logic:iterate id="databaseDatasource" name="addDynAuthServicePolicyForm" property="databaseDatasourceList" type="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData">
																		<html:option value="<%=databaseDatasource.getDatabaseId()%>"><%=databaseDatasource.getName()%></html:option>
																	</logic:iterate>
																</html:select>
															</td>

														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.tablename" /> 
																<ec:elitehelp  header="servicepolicy.dynauthpolicy.tablename" headerBundle="servicePolicyProperties" text="dynaauthpolicy.table" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:text property="tableName" styleId="tableName" maxlength="2000" size="30" onblur="setColumnsForTables();" style="width: 200px;" tabindex="9" />
																<font color="#FF0000"> *</font>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top">
																<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.dbfailureaction" /> 
																<ec:elitehelp  header="servicepolicy.dynauthpolicy.dbfailureaction" headerBundle="servicePolicyProperties" text="servicepolicy.dynauthpolicy.dbfailureaction" ></ec:elitehelp>
															</td>
															<td align="left" class="labeltext" valign="top" colspan="3">
																<html:select property="dbFailureAction" styleId="dbFailureAction" name="addDynAuthServicePolicyForm" style="width: 200;">
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
																	<html:option value="<%=Integer.toString(DynAuthPolicyConstant.NONE)%>"> 
																		<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eligiblesession.none" />
																	</html:option>
																	<html:option value="<%=Integer.toString(DynAuthPolicyConstant.RECENT)%>">
																		<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eligiblesession.recent" />
																	</html:option>
																	<html:option value="<%=Integer.toString(DynAuthPolicyConstant.OLDEST)%>">
																		<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eligiblesession.oldest" />
																	</html:option>
																	<html:option value="<%=Integer.toString(DynAuthPolicyConstant.ALL)%>">
																		<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.eligiblesession.all" />
																	</html:option>
																</html:select>
															</td>
														</tr>
														<tr>
															<td align="left" class="captiontext" valign="top" colspan="2" id="button">
																<input type="button" onclick="popup()" value=" Add Mapping " class="light-btn" style="size: 140px" tabindex="12">
															</td>
														</tr>
														<tr>
															<td width="98%" colspan="3" valign="top" class="captiontext">
																<table cellSpacing="0" cellPadding="0" width="98%" border="0" id="mappingtbl1" class="box">
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
																		<td align="left" class="tblheader" valign="top" width="30%">
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
																<input type="button" value=" Create " class="light-btn" onclick="validate()" id="c_btnCreate2" tabindex="18" />
																<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchDynAuthServicePolicy.do?/>'" value="Cancel" class="light-btn" tabindex="19">
															</td>
														</tr>

													</table>
												</td>
											</tr>
										</table>
										<div id="popupContact" style="display: none;" title="Add Field Mapping">
											<table>
												<tr>
													<td align="left" class="labeltext" valign="top" width="25%">
														<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.attributeids" />
													</td>
													<td align="left" class="labeltext" valign="top" width="75%">
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


									</html:form></td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>

<div id="nasServersPopup" style="display: none;" title="Add Nas Servers">
	<table id="nasTbl" name="nasTbl" cellpadding="0" cellspacing="0" width="100%" class="box">
	</table>
</div>

<!-- Mapping Table Row template -->
<table  id="nasMappingTable" style="display: none;" >
	<tr>
		<td style="border-bottom-style: solid;border-bottom-width: 1px;border-bottom-color: #CCCCCC;" width="20%">
			<input type="text" name="ruleset" id="ruleset"  maxlength="1000" size="28" style="width: 100%" />
		</td>
		<td  width="30%">
			<table cellSpacing="0" cellPadding="0" width="100%" border="0" id="nasClienttable" class="box nasClienttable">
					<tr>
						<td align="left" class="tblheader-policy" width="15%" id="name">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient.servername" />
							&nbsp;
							<img alt="" src="<%=basePath%>/images/plus.jpg" onclick="addMoreServer(this)" title="Add Server" />
						</td>
						<td align="left" class="tblheader-policy"  width="12%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy.nasclient.loadfactor" />
						</td>
						<td align="left" class="tblheader-policy" valign="top" width="3%">Remove</td>
			    	</tr>
			    	<tr>
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
		<td class="tbldynapolicy" width="20%"><select
			name="translationMapConfigId"
			id="translationMapConfigId" style="width: 100%;">
				<option value="">--Select--</option>
				<optgroup label="Translation Mapping" class="labeltext">
					<logic:iterate id="translationMapping"
						name="addDynAuthServicePolicyForm"
						property="translationMappingConfDataList"
						type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
						<option
							value="<%=ConfigConstant.TRANSLATION_MAPPING + translationMapping.getTranslationMapConfigId()%>"
							class="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></option>
					</logic:iterate>

				</optgroup>

				<optgroup label="Copy Packet Mapping" class="labeltext">
					<logic:iterate id="copyPacketMapping"
						name="addDynAuthServicePolicyForm"
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
nasEsiPopup();
setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.dynauthpolicy"/>');
</script>
<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>