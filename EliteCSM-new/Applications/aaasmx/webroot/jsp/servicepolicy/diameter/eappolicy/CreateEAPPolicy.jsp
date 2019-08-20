<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.CreateEAPPolicyForm"%>
<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ServiceTypeConstants"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.diameterapi.diameter.common.util.constant.CommandCode" %>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData"%>
<%@page import="com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%
		String basePath = request.getContextPath();
		//code for driver jquery popup
		DriverBLManager driverManager = new DriverBLManager();
		List<DriverInstanceData> listOfDriver = driverManager.getDriverInstanceList(ServiceTypeConstants.NAS_AUTH_APPLICATION);
		
		String[] driverInstanceIds = new String [listOfDriver.size()];
		String[][] driverInstanceNames = new String[listOfDriver.size()][3]; 
		
		for(int i=0;i<listOfDriver.size();i++){
			DriverInstanceData data = listOfDriver.get(i);				
				driverInstanceNames[i][0] = String.valueOf(data.getName());
				driverInstanceNames[i][1] = String.valueOf(data.getDescription());
				driverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
			driverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
		}
		String statusVal=(String)request.getParameter("status");
		
		List<PluginInstData> pluginInstDataList = (List<PluginInstData>) request.getAttribute("pluginInstDataList");
		
		CreateEAPPolicyForm createEAPPolicyForm = (CreateEAPPolicyForm)request.getAttribute("createEAPPolicyForm"); 
		
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-policy.js"></script>
<script>
	setExpressionData("Diameter");
	var commandCodeList = [];
	var driverScriptList = [];

	<%for(CommandCode commandCode:CommandCode.VALUES){%>
		commandCodeList.push({'value':'<%=commandCode.code%>','label':'[<%=commandCode.code%>] <%=commandCode.name()%>','id':'<%=commandCode.code%>'});
	<%}%>

	<% 
		if( Collectionz.isNullOrEmpty(createEAPPolicyForm.getDriverScriptList()) == false ){
			for( ScriptInstanceData scriptInstData : createEAPPolicyForm.getDriverScriptList()){ %>
				driverScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
			<%}
		}
	%>

 	 var isValidName;
	 $(document).ready(function() {
		var chkBoxVal='<%=statusVal%>';
		if(chkBoxVal=='Inactive'){
			document.getElementById("status").checked=false;
		}else{
			document.getElementById("status").checked=true;
		}
		
		$('.responseAttributeTable td img.delete').live('click',function() {
			 $(this).parent().parent().remove(); 
		});
		
		setCommandCodeAutocompleteData();
		
		/* Advaned CUI Configuration */
		$(".cui-css").change(function(){
			   if($(this).val() == 'Advanced'){
				   $("#advancedCuiExpression").attr("readonly", false);
			   }else{
				   $("#advancedCuiExpression").attr("readonly", true);
			   }
		});
		
		 /* Script Autocomplete */
		setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
	});
	function splitData( val ) {
		return val.split( /[,;]\s*/ );
	}

	function extractLastItems( term ) {
		return splitData( term ).pop();
	}
	 function setCommandCodeAutocompleteData(){
			$( '.commandCode' ).bind( "keydown", function( event ) {
				if ( event.keyCode === $.ui.keyCode.TAB && $( this ).autocomplete( "instance" ).menu.active ) {
					event.preventDefault();
				}
			}).autocomplete({
			minLength: 0,
			source: function( request, response ) {
				response( $.ui.autocomplete.filter(
					commandCodeList, extractLastItems( request.term ) ) );
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
	 			this.value = val + ui.item.value ;
	 			return false;
			}
			});
	}
	 
	function validateForm(){
		 var behaviour = $("#defaultResponseBehaviour").val();
 		if(isNull(document.forms[0].name.value)){
 			alert('Name must be specified');
 		}else if(!isValidName) {
 			alert('Enter Valid Name');
 			document.forms[0].name.focus();
 			return;
 		}else if(isNull(document.forms[0].ruleSet.value)){
 			alert('RuleSet must be specified');
 		}else if(document.forms[0].eapConfigId.value == '0'){
 			alert('EAP Configuration must be specified');
 			document.forms[0].eapConfigId.focus();
 		}else if(isNull(document.forms[0].multipleUserIdentity.value)){
 			alert('User Identity Attribute must be specified');
 		}else if( $('#diameterConcurrency').val() == '0' && $('#additionalDiameterConcurrency').val() != '0'){
 			alert('Diameter Concurrency must be specified');
 			$('#diameterConcurrency').focus();
 		}else if(!isValidMappings()){
 			return false;
 		}else if( !(isNull( $('#defaultSessionTimeout').val() )) && isNaN($('#defaultSessionTimeout').val()) ){
 			alert('Default Session Timeout must be numeric ');
 			$('#defaultSessionTimeout').focus();
 			return;
 		}else if( !checkForDot() ){
 			$('#defaultSessionTimeout').focus();
 			return false;
 		}else if( $('#cui').val() == 'Advanced' && isNull($('#advancedCuiExpression').val())){
 			alert('Advanced CUI Expression must be specified');
 			$('#advancedCuiExpression').focus();
 			return;
 		}else if(!validatePlugins('pre-plugin-mapping-table','post-plugin-mapping-table','prePluginsList','postPluginList')){
 			return;
 		}else if((behaviour == "REJECT" || behaviour == "HOTLINE") && $("#defaultResponseBehaviorArgument").val().trim() == ""){
 				alert("Default Response Behaviour Argument must be specified when Default Response Behaviour is "+behaviour);
				$("#defaultResponseBehaviorArgument").focus();
 				return;
 		}
 		else{
 			var supportedDriverList=document.getElementById("selecteddriverIds");	

 			if(supportedDriverList.options.length > 0){
 				selectAll(supportedDriverList);
 				var additionalDriverList=document.getElementById("selectedAdditionalDriverIds");
 		 		selectAll(additionalDriverList);
 				document.forms[0].action.value = 'create';
 	 	 	 	document.forms[0].submit();	 				
 			}else{
				alert('Driver must be specified');	
 	 		}
 		}
			
	}
	function checkForDot(){
		if( !(isNull( $('#defaultSessionTimeout').val() ))) {
			var defaultValue = $('#defaultSessionTimeout').val();
			if (defaultValue.indexOf(".") !== -1) {
			    alert('Floating point value not allowed in Default Session Timeout ');
				return false;
			}
			return true;
		}else{
			return true;
		}
	}
	function isValidMappings(){
		var isValidMapping = true;
		$('.responseAttributeTable').find('.commandCode').each(function(){
			var nameValue = $.trim($(this).val());
			if(nameValue.length == 0) {
				alert("Command Code must be Specified");
				isValidMapping = false;
				$(this).focus();
				return false;
			}
		});
		return isValidMapping;
	}
	
	function setCommandCodeData(commandCodeObj){
		var commandCodevalue=$(commandCodeObj).val();
		commandCodevalue=commandCodevalue.trim();
		var lastChar = commandCodevalue.charAt(commandCodevalue.length - 1);
		if(lastChar == ","){
			var result = commandCodevalue.substring(0, commandCodevalue.length-1);
			$(commandCodeObj).val(result);
		}
	}

 	// code for driver related jquery popup
 	var jdriverNames = new Array();
 	var jdriverInstanceIds = new Array();
 	var count=0;

 	$(document).ready(function() {		
 		
 		jdriverNames.length = <%=listOfDriver.size()%>;				
 		jdriverInstanceIds.length= <%=listOfDriver.size()%>;
 			
 		
 		<%int j,k=0;
 		for(j =0;j<listOfDriver.size();j++){%>		
 			jdriverNames[<%=j%>] = new Array(3);		
 				<%for(k=0;k<3;k++){%>												
 					jdriverNames[<%=j%>][<%=k%>] = '<%=driverInstanceNames[j][k]%>'				
 				<%}%>
 			jdriverInstanceIds[<%=j%>] = '<%=driverInstanceIds[j]%>'	
 			count ++;							
 		<%}%>	 	 

 		var headersArr = new Array(5);
 		headersArr[0] = '';
 		headersArr[1] = 'Driver Name';
 		headersArr[2] = 'Driver Description';
 		headersArr[3] = 'Driver Type';
 		headersArr[4] = 'Weightage';
 		
 		initializeData(jdriverInstanceIds,jdriverNames,'addDriver','driverDataCheckBoxId',headersArr,'true',jdriverInstanceIds.length);
 		
 		var additionalHeadersArr = new Array(4);
 		additionalHeadersArr[0] = '';
 		additionalHeadersArr[1] = 'Additional Driver Name';
 		additionalHeadersArr[2] = 'Driver Description';
 		additionalHeadersArr[3] = 'Driver Type';
 		initializeAdditionalData(jdriverInstanceIds,jdriverNames,'additionalDriver','additionalDriverCheckBoxId',additionalHeadersArr,jdriverInstanceIds.length);
 	   
 	   }
 	);

 	function driverpopup(){
 		
 		openpopup('driverPopup','driverDataCheckBoxId',jdriverInstanceIds,jdriverNames,'selecteddriverIds');
 			
 	 }

 	function removeDrivers(){
 		removeData('selecteddriverIds','driverDataCheckBoxId');	
 			
 	 }

 	function selectAll(selObj){
 		for(var i=0;i<selObj.options.length;i++){
 			selObj.options[i].selected = true;
 		}
 	}

 	function verifyName() {
 		var searchName = document.getElementById("name").value;
 		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_EAP_POLICY%>',searchName,'create','','verifyNameDiv');
 	}

 	function setStripUserIdentity(){
 		var stripUserCheckBox = document.getElementById("stripUserCheckBox");
 		var realmPatternComp = document.getElementById("realmPattern");
 		var separatorComp = document.getElementById("realmSeparator");
 		if(stripUserCheckBox.checked ==true){
 			realmPatternComp.disabled=false;
 			separatorComp.disabled=false;
 		}else{
 			realmPatternComp.disabled=true;
 			separatorComp.disabled=true;
 		}
 	}

 	function setInit(){
 		var stripUserCheckBox = document.getElementById("stripUserCheckBox");
 		var trimUserIdentityCheckBox = document.getElementById("trimUserIdentityCheckBox");
 		var trimPasswordCheckBox = document.getElementById("trimPasswordCheckBox");
 		
 		document.getElementById("rejectOnRejectItemNotFound").checked = false;
        document.getElementById("rejectOnCheckItemNotFound").checked = false;
        document.getElementById("actionOnPolicyNotFound").checked = false;
 		
 		stripUserCheckBox.checked=false;
 		trimUserIdentityCheckBox.checked=true;
 		trimPasswordCheckBox.checked=false;
 		setStripUserIdentity();	
 	}

 	function setColumnsOnMultipleUIdTextFields(){
 		var multipleUserIdVal = document.getElementById("multipleUserIdentity").value;
 		retriveRadiusDictionaryAttributes(multipleUserIdVal,"multipleUserIdentity");
 	}

 	
 	
 	function initializeAdditionalData(listOfAdditionalId,listOfAdditionaldrNm,additionaltableId,additionalcheckbxId,additionalheaderArr,additionalcount){		
 		
 		var additionaltable = document.getElementById(additionaltableId);		
 		var additionalrowid = additionaltable.insertRow(0);
 		for(var d=0;d<additionalheaderArr.length;d++){
 			var cells = additionalrowid.insertCell(d);					
 			cells.innerHTML = additionalheaderArr[d];			
 			cells.className="tblheader-bold";						
 		}
 		var additionaltemp = 1;
 		
 		for(var a = 0;a<additionalcount;a++){
 			
 			var additionalrow = additionaltable.insertRow(additionaltemp);		
 			additionalrow.id = "additionalrow"+ additionalcheckbxId + listOfAdditionalId[a];
 			additionalrow.style.visibility = 'visible';
 	            
 			    var additionalcell1 = additionalrow.insertCell(0);
 			    var additionalelement1 = document.createElement("input");
 			    additionalelement1.type = "checkbox";
 			    additionalelement1.name = additionalcheckbxId;
 			    additionalelement1.id =additionalcheckbxId+listOfAdditionalId[a];		    		    		    
 			    additionalelement1.value = listOfAdditionaldrNm[a][0];
 			    additionalcell1.appendChild(additionalelement1);
 			    additionalcell1.className="tblfirstcol";
 			    additionalcell1.width="5%";
 			    
 			    var additionalcellNumber = 1;
 				
 				for(var b=0;b<listOfAdditionaldrNm[a].length;b++){
 					var cell = additionalrow.insertCell(additionalcellNumber);	
 					
 					if( additionalcellNumber == 1 ){
 						cell.innerHTML = '<span class="view-details-css" onclick="openViewDetails(this,'+listOfAdditionalId[a]+',\''+ listOfAdditionaldrNm[a][b] +'\',\''+'DRIVERS\');">' + listOfAdditionaldrNm[a][b] +"</span>";
 					}else{
 						cell.innerHTML = listOfAdditionaldrNm[a][b];
 					}
 					
 					cell.className="tblrows";
 					additionalcellNumber++;
 				}
 				additionaltemp++;
 			}
 	   }
 	function additionalDriverpopup(){
 		openAdditionalPopup('additionalDriverPopup','additionalDriverCheckBoxId',jdriverInstanceIds,jdriverNames,'selectedAdditionalDriverIds');
 	 }

 	function openAdditionalPopup(divId,checkBxId,listOfIds,listOfNms,componentId){
 		var id = "#" + divId;
 		var comid = "#" + componentId;
 		
 		document.getElementById(divId).style.visibility = "visible";
 				
 		$(id).dialog({
 			modal: true,
 			autoOpen: false,
 			minHeight: 200,
 			height: 250,
 			width: 600,
 			buttons:{					
 				        'Add': function() {
 	                       	var selectedItems=$("*[name='"+checkBxId+"']");
 	                        if(selectedItems.length==1 && selectedItems[0].checked  == true){
 	                        	var supportedDriverList=document.getElementById("selectedAdditionalDriverIds");
 	                            var labelVal=$("#"+checkBxId+listOfIds[0]).val();       
 	                           	$(comid).append("<option id="+ listOfIds[0] +" value="+ listOfIds[0] +" class=labeltext> "+labelVal+" </option>");
 	                               		                                                        
 	                           	$("#row"+"driverDataCheckBoxId"+listOfIds[0]).hide();
 	                        	$("#additionalrow"+"additionalDriverCheckBoxId"+listOfIds[0]).hide();
 								selectedItems[0].checked=false;	
 									        
 	                        }else if(selectedItems.length>1 ){
 	                        	for(var i=0;i<selectedItems.length;i++)
 		                        {
 									if(selectedItems[i].checked == true)
 		                            {   	
 										var supportedDriverList=document.getElementById("selectedAdditionalDriverIds");	
 										//var optionsval =$("#"+checkBxId+listOfIds[i]+"w").val();	
 		                                var labelVal=$("#"+checkBxId+listOfIds[i]).val();       
 		                               	$(comid).append("<option id="+ listOfIds[i] +" value="+ listOfIds[i] +" class=labeltext> "+labelVal+" </option>");	

 		                               	$("#row"+"driverDataCheckBoxId"+listOfIds[i]).hide();
 		                            	$("#additionalrow"+"additionalDriverCheckBoxId"+listOfIds[i]).hide();
 		                                selectedItems[i].checked=false;         
 		                            }                             
 		                        }
 	                        }
 	                        $(this).dialog('close');
 						},                
 						Cancel: function() {
 							$(this).dialog('close');
 						}
 					},
 			open: function() {
 			
 			},
 			close: function() {
 				
 			}				
 			});
 			$(id).dialog("open");	
 	 }
 	
 	function removeAdditionalDriverData(componentId,checkboxid){
 		 var id = "#" + componentId + " " +"option:selected";
 		 $(id).each(function(){
 			 var mainValue = $(this).val();
 		     $("#row"+"driverDataCheckBoxId"+mainValue).show();
 		     $("#additionalrow"+checkboxid+mainValue).show();
 		     $(this).remove();				      
 	  });
 	}
 	
 	function openpopup(divId,checkBxId,listOfIds,listOfNms,componentId,isWeightage){
 		
 		var id = "#" + divId;
 		var comid = "#" + componentId;
 		
 		document.getElementById(divId).style.visibility = "visible";

 	       <%-- Reset Popup Window--%>	
 		   if(isWeightage==null || isWeightage=='true')
 			{
 		       var jItems=$("*[name='"+checkBxId+"']"); //it will give element by name in jquery
 				 for(var i=0;i<jItems.length;i++)
 			     {
 			         $("#"+checkBxId+listOfIds[i]+"w").val("1");
 			     }

 		    }

 				
 		$(id).dialog({
 			modal: true,
 			autoOpen: false,
 			minHeight: 200,
 			height: 250,
 			width: 700,
 			buttons:{					
 				        'Add': function() {
 							
 	                       //var selectedItems=document.getElementsByName(checkBxId);
 	                       var selectedItems=$("*[name='"+checkBxId+"']");
 	                       
 	                        if(selectedItems.length==1 && selectedItems[0].checked  == true){
 	                                
 	                        		if(isWeightage==null || isWeightage=='true'){	 
 										var optionsval =$("#"+checkBxId+listOfIds[0]+"w").val();								
 	                                	var labelVal=$("#"+checkBxId+listOfIds[0]).val();       
 	                               		$(comid).append("<option id="+ listOfIds[0] +" value="+ listOfIds[0] + "-" + optionsval +" class=labeltext> "+labelVal+"-W-" + optionsval +" </option>");
 	                               		
 									}else if(isWeightage=='false'){
 		                                var labelVal=$("#"+checkBxId+listOfIds[0]).val();                                
 		                               	$(comid).append("<option id="+ listOfIds[0] +" value="+ listOfIds[0]  +" class=labeltext> "+labelVal +" </option>");
 									}
 	                               	$("#row"+checkBxId+listOfIds[0]).hide();
 	                               	$("#additionalrow"+"additionalDriverCheckBoxId"+listOfIds[0]).hide();
 	                            	selectedItems[0].checked=false;
 									
 									         
 	                        }else if(selectedItems.length>1 ){
 		                        for(var i=0;i<selectedItems.length;i++)
 		                        {
 									if(selectedItems[i].checked == true)
 		                            {   	                         
 										if(isWeightage==null || isWeightage=='true'){	 
 											var optionsval =$("#"+checkBxId+listOfIds[i]+"w").val();
 		                                	var labelVal=$("#"+checkBxId+listOfIds[i]).val();     
 		          							$(comid).append("<option id="+ listOfIds[i] +" value="+ listOfIds[i] + "-" + optionsval +" class=labeltext> "+labelVal+"-W-" + optionsval +" </option>");
 										}else if(isWeightage=='false'){
 			                                var labelVal=$("#"+checkBxId+listOfIds[i]).val();                                
 			                               	$(comid).append("<option id="+ listOfIds[i] +" value="+ listOfIds[i]  +" class=labeltext> "+labelVal +" </option>");
 										}
 										$("#row"+"driverDataCheckBoxId"+listOfIds[i]).hide();
 		                            	$("#additionalrow"+"additionalDriverCheckBoxId"+listOfIds[i]).hide();
 										selectedItems[i].checked=false;         
 		                            }                             
 		                        }
 	                        }
 	                        $(this).dialog('close');
 						},                
 						Cancel: function() {
 						$(this).dialog('close');
 						}
 					},
 			open: function() {
 			
 			},
 			close: function() {
 				
 			}				
 			});
 			$(id).dialog("open");
 			
 	 }
 	
 	function removeData(componentId,checkboxid){
 		 var id = "#" + componentId + " " +"option:selected";	
 		 
 		$(id).each(function(){
 			 var mainValue = $(this).val();
 			 var strippedVal = mainValue.split('-');
 		     var rowid="#row"+checkboxid+strippedVal[0];
 		     var addtionalId = "#additionalrow"+"additionalDriverCheckBoxId"+strippedVal[0];
 		     $(rowid).show();
 		     $(addtionalId).show();
 		     $(this).remove();				      
 	   });
 	}
 	function addResponseAttributesTable(tableId,templateId){
 		var tableRowStr = $("#"+templateId).find("tr");
 		$("#"+tableId+" tr:last").after("<tr>"+$(tableRowStr).html()+"</tr>");
 		$("#"+tableId+" tr:last").find("input:first").focus();
 		setCommandCodeAutocompleteData();
 	}
 	
 	function uncheckAllCheckBoxes(pluginNames){
 		$("input:checkbox[name='"+pluginNames+"']").each(function(){
 			if($(this).attr('checked')){
 				$(this).attr('checked',false);
 			}
 		});
 	}
 	
 	function setColumnsOnCuiRespAttrTextFields(){
 		var cuiRespAttrVal = document.getElementById("cuiResponseAttributes").value;
 		retriveRadiusDictionaryAttributes(cuiRespAttrVal,"cuiResponseAttributes");
 	}
 	
 	function setDiameterPolicyData(obj){
 		if ($(obj).attr('checked')) {
			$(obj).val('true');
		} else {
			$(obj).val('false');
		}
 	}
 	
 	
 	setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy"/>');
 	
 	var pluginList = [];
	<%if(pluginInstDataList != null){%>
		<logic:iterate id="pluginBean" name="pluginInstDataList">
			pluginList.push({
				'id' : '<bean:write property="pluginInstanceId" name="pluginBean" />',
				'value' : '<bean:write property="pluginTypeId" name="pluginBean" />',
				'label' : '<bean:write property="name" name="pluginBean" />'
			});
		</logic:iterate>
	<%}%>
</script>

<html:form action="/createEAPPolicy">
	
	<html:hidden name="createEAPPolicyForm" property="action" />
	<html:hidden name="createEAPPolicyForm" property="policyId" />
	<html:hidden name="createEAPPolicyForm" property="prePluginsList" styleId="prePluginsList"/>
	<html:hidden name="createEAPPolicyForm" property="postPluginList" styleId="postPluginList"/>
	
	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
  			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  				&nbsp;
  			</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
  		  		<tr>
		    		<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
					
						<tr>
							<td class="table-header">		
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.create" /></td>
						</tr>
				<tr>
					<td valign="middle" colspan="3">
					<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
						<tr>
							<td  class="tblheader-bold" valign="top" colspan="4">
								<bean:message bundle="servicePolicyProperties" key="basic.details"/>
							</td>									
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top"  width="25%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.name" />
								<ec:elitehelp  header="servicepolicy.eappolicy.name" headerBundle="servicePolicyProperties" text="eappolicy.name" ></ec:elitehelp>
							</td>
							<td align="left" class="labeltext" valign="top"  nowrap="nowrap" >
								<html:text styleId="name" property="name" size="30" onkeyup="verifyName();"
								maxlength="60" style="width:250px"/><font color="#FF0000"> *</font>
									<div id="verifyNameDiv" class="labeltext"></div>
							</td>	
							<td  align="left" class="labeltext" valign="top" >
								<input type="checkbox" name="status" id="status" value="1" checked="true"/>&nbsp;Active
							</td>	
						</tr>
		
						<tr>
							<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.desp" /></td>
							<td align="left" class="labeltext" valign="top" width="30%" colspan="2">
								<html:textarea styleId="description" property="description" cols="40" rows="2" style="width:250px"/>
							</td>							
						</tr>	
						
						<tr>
							<td align="left" class="captiontext" valign="top" width="18%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.ruleset" />
								<ec:elitehelp  header="servicepolicy.eappolicy.ruleset" headerBundle="servicePolicyProperties" text="eappolicy.ruleset" ></ec:elitehelp>
							</td>
							<td align="left" class="labeltext" valign="top" width="30%" colspan="2">
								<html:textarea styleId="ruleSet" property="ruleSet" cols="40" rows="2" style="width:250px"/>
								<font color="#FF0000"> *</font>		
								<img alt="Expression" src="<%=basePath%>/images/lookup.jpg" onclick="popupExpression('ruleSet');" />													
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="captiontext" valign="top" width="18%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.sessionmanagement" />
								<ec:elitehelp  header="servicepolicy.eappolicy.sessionmanagement" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.sessionmanagement" ></ec:elitehelp>
							</td>
							<td align="left" class="labeltext" valign="top" width="30%" colspan="2">
								<html:select property="sessionManagement" styleId="sessionManagement">
									<html:option value="true">True</html:option>
									<html:option value="false">False</html:option>
								</html:select>													
							</td>							
						</tr>
						
						<tr>
							<td class="captiontext" valign="top">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.requestmode" />
								<ec:elitehelp  header="servicepolicy.authpolicy.requestmode" headerBundle="servicePolicyProperties" text="authpolicy.reqmod" ></ec:elitehelp>
							</td>
							<td class="labeltext" valign="top">
								<html:select property="requestType" styleClass="labeltext" styleId="requestType"  >
									<html:option value="1">Authenticate-Only</html:option>
									<html:option value="2">Authorize-Only</html:option>
									<html:option value="3">Authenticate and Authorize</html:option>
								</html:select>
							</td>	
						</tr>
						<tr>	
								<td align="left" class="captiontext" valign="top">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.defaultresponsebehaviour" /> 
									<ec:elitehelp  header="servicepolicy.eappolicy.defaultresponsebehaviour"  headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.defaultauthresponsebehavior" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top">
									<html:select name="createEAPPolicyForm" styleId="defaultResponseBehaviour" property="defaultResponseBehaviour" style="width:90px" tabindex="9" >
										<logic:iterate id="behaviour"  collection="<%=DefaultResponseBehavior.DefaultResponseBehaviorType.values() %>">
											<html:option value="<%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%>"><%=((DefaultResponseBehavior.DefaultResponseBehaviorType)behaviour).name()%></html:option>
										</logic:iterate>
									</html:select>
								</td>
						</tr>
						<tr>
							<td align="left" class="captiontext" valign="top">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.defaultresponsebehaviourargument" /> 
								<ec:elitehelp  header="servicepolicy.eappolicy.defaultresponsebehaviourargument" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.defaultresponsebehaviourargument" ></ec:elitehelp>
							</td>
							<td align="left" class="labeltext" valign="top">
								<html:text property="defaultResponseBehaviorArgument" name="createEAPPolicyForm" styleId="defaultResponseBehaviorArgument"  maxlength="1000" styleClass="textbox_width" tabindex="10" />
							</td> 
					</tr>
									
						<tr>
							<td  class="tblheader-bold" valign="top" colspan="4">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.authenticationdetails"/>
							</td>									
						</tr>						
				
								<tr>
										<td class="captiontext">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.eapconfig"/>
											<ec:elitehelp  header="servicepolicy.eappolicy.eapconfig" headerBundle="servicePolicyProperties" text="authpolicy.eapconfig" ></ec:elitehelp>
										</td>	
											<td align="left" class="labeltext" valign="top" colspan="3">
											<html:select property="eapConfigId" styleClass="labeltext" styleId="eapConfigId" style="width:130px">
												<html:option value="0">--select--</html:option>
											    <logic:iterate id="eapConfInst" name="createEAPPolicyForm"  property="eapConfigurationList" type="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData">
													<html:option value="<%=eapConfInst.getEapId()%>"><%=eapConfInst.getName()%></html:option>
												</logic:iterate>											
											</html:select>
											<font color="#FF0000"> *</font>	
											</td>
								</tr>
																		
													
									<tr>
										<td align="left" class="captiontext" valign="top" >										
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.multipleuid"/>
											<ec:elitehelp  header="servicepolicy.eappolicy.multipleuid" headerBundle="servicePolicyProperties" text="authpolicy.usridentity" ></ec:elitehelp>
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3">											
											<%--html:text property="multipleUserIdentity" styleId="multipleUserIdentity" size="30" onkeyup="retriveDictionary();" AUTOCOMPLETE="off"/><font color="#FF0000"> *</font>--%>
											<input type="text" name="multipleUserIdentity" id="multipleUserIdentity" size="30" autocomplete="off" onkeyup="setColumnsOnMultipleUIdTextFields();" style="width:250px" value="0:1"/>	
											<font color="#FF0000"> *</font>																	
										</td>
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top" >											
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.eaphpolicy.casesensitiveuid"/>
											<ec:elitehelp  header="servicepolicy.eaphpolicy.casesensitiveuid" headerBundle="servicePolicyProperties" text="authpolicy.case" ></ec:elitehelp>
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3">
											<html:select property="caseSensitiveUserIdentity"  styleClass="labeltext" styleId="caseSensitiveUserIdentity" style="width:130px">
												<html:option value="1">No Change</html:option>
												<html:option value="2">Lower Case</html:option>											
												<html:option value="3">Upper Case</html:option>
											</html:select>											
										</td>
									</tr>
									<tr>									  
										<td align="left" class="captiontext" valign="top" >													
											<bean:message bundle="servicePolicyProperties" key="update.user.identity"/>
											<ec:elitehelp  header="update.user.identity" headerBundle="servicePolicyProperties" text="authpolicy.useridentity" ></ec:elitehelp>
										</td>											
										<td class="labeltext" colspan="3" valign="top" > 										
                                            <table width="100%" border="0" cellpadding="0" cellspacing="0" class="box">
                                                <tr>
                                                  <td class="labeltext">   
                                                    <html:checkbox
													property="stripUserIdentity" styleId="stripUserCheckBox"
													value="true" onclick="setStripUserIdentity()"></html:checkbox>
                                                    <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.stripuid"/>
                                                    
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.realmseparator" />
													<html:text property="realmSeparator" size="5" maxlength="1" styleId="realmSeparator"/>
													
													
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                                    <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.realmpattern" />
                                                    

                                                    <html:select property="realmPattern"  styleClass="labeltext" styleId="realmPattern">
                                                    <html:option value="prefix">Prefix</html:option>
                                                    <html:option value="suffix">Suffix</html:option>                                            
                                                    </html:select>
												  </td>
												  </tr>
												  <tr>
												  <td class="labeltext">

													<html:checkbox property="trimUserIdentity" value="true" styleId="trimUserIdentityCheckBox"></html:checkbox>
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.trimuid"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;																							
													<html:checkbox property="trimPassword" value="true" styleId="trimPasswordCheckBox"></html:checkbox>
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.trimpassword"/>												
												  
												  </td>
												  </tr>
												  </table>
												</td>	
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.anonymousidentity" /> 
											<ec:elitehelp  header="servicepolicy.eappolicy.anonymousidentity" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.anonymousidentity" ></ec:elitehelp>
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3">
												<html:text property="anonymousProfileIdentity" styleId="anonymousProfileIdentity" style="width:200px;"></html:text>
										</td>
									</tr>
							
							<tr>
								<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.authorizationdetails" />
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.wimax" /> 
									<ec:elitehelp  header="servicepolicy.eappolicy.wimax" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.wimax" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="3">
									<html:select property="wimax" styleClass="labeltext" styleId="wimax" tabindex="18">
										<html:option value="true">Enabled</html:option>
										<html:option value="false">Disabled</html:option>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" >											
										Diameter Policy 
								</td>
								<td align="left" class="labeltext" valign="top" colspan="3">
									<table width="100%" class="box" >
										<tr>
											<td class="labeltext" width="40%">
												<html:checkbox property="rejectOnCheckItemNotFound" styleId="rejectOnCheckItemNotFound" onclick="setDiameterPolicyData(this);">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.rejectoncheckitemnotfound" />
													<ec:elitehelp  header="servicepolicy.eappolicy.rejectoncheckitemnotfound" headerBundle="servicePolicyProperties" text="authpolicy.rejectoncheck" ></ec:elitehelp>
												</html:checkbox>
											</td>
											<td></td>
										</tr>
										<tr>
											<td class="labeltext">
												<html:checkbox property="rejectOnRejectItemNotFound" styleId="rejectOnRejectItemNotFound" onclick="setDiameterPolicyData(this);">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.rejectonrejectitemnotfound" />
													<ec:elitehelp  header="servicepolicy.eappolicy.rejectonrejectitemnotfound" headerBundle="servicePolicyProperties" text="authpolicy.rejectonreject" ></ec:elitehelp>
												</html:checkbox>
											</td>
											<td></td>
										</tr>
										<tr>
											<td class="labeltext">
												<html:checkbox property="actionOnPolicyNotFound" styleId="actionOnPolicyNotFound" onclick="setDiameterPolicyData(this);">
													<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.acceptonpolicynotfound" />
													<ec:elitehelp  header="servicepolicy.eappolicy.acceptonpolicynotfound" headerBundle="servicePolicyProperties" text="authpolicy.actionpolicy" ></ec:elitehelp>
												</html:checkbox>
											</td>
											<td></td>
										</tr>
										</table>											
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.gracepolicy" /> 
									<ec:elitehelp  header="servicepolicy.eappolicy.gracepolicy" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.gracepolicy" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="3">
									<html:select property="gracePolicy" styleClass="labeltext" styleId="gracePolicy" tabindex="18" style="width:175px;">
										<html:option value="">--select--</html:option>
											<logic:iterate id="gracePolicyInst" name="createEAPPolicyForm" property="gracePolicyList" type="com.elitecore.elitesm.datamanager.servermgr.gracepolicy.data.GracepolicyData">
												<html:option value="<%=gracePolicyInst.getName()%>"><%=gracePolicyInst.getName()%></html:option>
											</logic:iterate>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.diameterconcurrency" /> 
									<ec:elitehelp  header="servicepolicy.eappolicy.diameterconcurrency" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.diameterconcurrency" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="3">
									<html:select property="diameterConcurrency" styleClass="labeltext" styleId="diameterConcurrency" tabindex="18" style="width:175px;">
										<html:option value="0">--select--</html:option>
											<logic:iterate id="diameterConcurrencyInst" name="createEAPPolicyForm" property="diameterConcurrencyDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData">
												<html:option value="<%=String.valueOf(diameterConcurrencyInst.getDiaConConfigId())%>"><%=diameterConcurrencyInst.getName()%></html:option>
											</logic:iterate>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.additionaldiameterconcurrency" /> 
									<ec:elitehelp  header="servicepolicy.eappolicy.additionaldiameterconcurrency" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.additionaldiameterconcurrency" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="3">
									<html:select property="additionalDiameterConcurrency" styleClass="labeltext" styleId="additionalDiameterConcurrency" tabindex="18" style="width:175px;">
										<html:option value="0">--select--</html:option>
											<logic:iterate id="diameterConcurrencyInst" name="createEAPPolicyForm" property="diameterConcurrencyDataList" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData">
												<html:option value="<%=String.valueOf(diameterConcurrencyInst.getDiaConConfigId())%>"><%=diameterConcurrencyInst.getName()%></html:option>
											</logic:iterate>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.defaultsessiontimeout" /> 
									<ec:elitehelp  header="servicepolicy.eappolicy.defaultsessiontimeout" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.defaultsessiontimeout" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="3">
									<html:text styleId="defaultSessionTimeout" property="defaultSessionTimeout" name="createEAPPolicyForm" maxlength="10" style="width:175px;"/>
								</td>
							</tr>
							<tr>
								<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.rfc4372cui" />
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.cui" /> 
									<ec:elitehelp  header="servicepolicy.eappolicy.cui" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.cui" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="3">
									<html:select property="cui"  styleClass="cui-css labeltext" styleId="cui" style="width: 175;" tabindex="16">
										<html:option value="NONE">NONE</html:option>
										<html:option value="Authenticated-Identity">Authenticated-Identity</html:option>
										<html:option value="Group">Group</html:option>
										<html:option value="Profile-CUI">Profile-CUI</html:option>
										<html:option value="Advanced">Advanced</html:option>
									</html:select>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.advancedcuiexpression" /> 
									<ec:elitehelp  header="servicepolicy.eappolicy.advancedcuiexpression" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.advancedcuiexpression" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="3">
									<html:text property="advancedCuiExpression" name="createEAPPolicyForm" styleId="advancedCuiExpression" styleClass="advancedCuiExpression" readonly="true" style="width:250px;"></html:text>
								</td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.cuiresposeattribute" /> 
									<ec:elitehelp  header="servicepolicy.eappolicy.cuiresposeattribute" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.cuiresposeattribute" ></ec:elitehelp>
								</td>
								<td align="left" class="labeltext" valign="top" colspan="3">
									<html:text property="cuiResponseAttributes" name="createEAPPolicyForm" styleId="cuiResponseAttributes" onkeyup="setColumnsOnCuiRespAttrTextFields();" style="width:250px;"></html:text>
								</td>
							</tr>
							<tr>
								<td  class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.driverdetails"/>
								</td>									
							</tr>
							
							<tr>
								<td width="20%" align="left" class="captiontext" valign="top">
									Driver Group	
									<ec:elitehelp  header="Driver Group" headerBundle="servicePolicyProperties" text="authpolicy.drivergroup" ></ec:elitehelp>
								</td>
								<td  width="30%" align="left" class="labeltext" valign="top">
									<select class="labeltext" name="selecteddriverIds" id="selecteddriverIds" multiple="true" size="5" style="width: 250px;">
									</select>												
								</td>
										
								<td align="left" class="labeltext" valign="top" >
							   		<input type="button" value="Add " onClick="driverpopup()"  
							   		class="light-btn"  style="width: 75px"/><br/>
							   		<br/>
							   		<input type="button" value="Remove "  onclick="removeDrivers()" class="light-btn" style="width: 75px"/>
								</td>
							</tr>
							
							<tr>
								<td width="20%" align="left" class="captiontext" valign="top">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.additionalgroup" />
									<ec:elitehelp  header="servicepolicy.additionalgroup" headerBundle="servicePolicyProperties" text="authpolicy.additionalgroup" ></ec:elitehelp>
								</td>
								<td  width="30%"  align="left" class="labeltext" valign="top">
									<select class="labeltext" name="selectedAdditionalDriverIds" id="selectedAdditionalDriverIds" multiple="true" size="5" style="width: 250;">
									</select>												
								</td>
										
								<td align="left" class="labeltext" valign="top" >
									<input type="button" value="Add " onClick="additionalDriverpopup()"  class="light-btn"  style="width: 75px"/><br/>
									<br/>
									<input type="button" value="Remove "  onclick="removeAdditionalDriverData('selectedAdditionalDriverIds','additionalDriverCheckBoxId')" class="light-btn" style="width: 75px"/>
								</td>
							</tr>
							
							
						  <tr>
							<td align="left" class="captiontext" valign="top"  width="25%">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.script" />
								<ec:elitehelp  header="servicepolicy.eappolicy.script" headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.script" ></ec:elitehelp>
							</td>
							<td align="left" class="labeltext" valign="top"  nowrap="nowrap" >
								<html:text property="script" size="30" style="width:250px" maxlength="255" styleClass="scriptInstAutocomplete" />
							</td>	
						  </tr>
						 <tr>
							<td  class="tblheader-bold" valign="top" colspan="4">
								<bean:message bundle="servicePolicyProperties" key="servicepolicy.preplugins" /> 
							</td>
						 </tr>
						 <tr>
							<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
								<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
									<tr>
										<td class="captiontext" valign="top" colspan="2">
											<input type="button" value="Add Plugin" onClick="addPluginMapping('pre-plugin-mapping-table','pre-plugin-mapping-template');" class="light-btn" tabindex="2" /><br />
										</td>
									</tr>
									<tr>
										<td  class="captiontext" valign="top">
											<table cellspacing="0" cellpadding="0" border="0" width="100%" id="pre-plugin-mapping-table" class="pre-plugin-mapping-table">
												<tr>
													<td class="tbl-header-bold tbl-header-bg" width="47.5%">
														<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginname" />
														<ec:elitehelp   header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname" ></ec:elitehelp>
													</td>	
													<td class="tbl-header-bold tbl-header-bg" width="47.5%">
														<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginagr" />
														<ec:elitehelp header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg" ></ec:elitehelp>
													</td>	
													<td class="tbl-header-bold tbl-header-bg" width="5%">
														Remove
													</td>	
												</tr>
											</table>
										</td>
									</tr>
								</table>
							</td>
						  </tr>
							<tr>
								<td  class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.postplugins" /> 
								</td>
							</tr>
							<tr>
								<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
									<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
										<tr>
											<td class="captiontext" valign="top" colspan="2">
												<input type="button" value="Add Plugin" onClick="addPluginMapping('post-plugin-mapping-table','post-plugin-mapping-template');" class="light-btn" tabindex="2" /><br />
											</td>
										</tr>
										<tr>
											<td  class="captiontext" valign="top">
												<table cellspacing="0" cellpadding="0" border="0" width="100%" id="post-plugin-mapping-table" class="post-plugin-mapping-table">
													<tr>
														<td class="tbl-header-bold tbl-header-bg" width="47.5%">
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginname" />
															<ec:elitehelp   header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname" ></ec:elitehelp>
														</td>	
														<td class="tbl-header-bold tbl-header-bg" width="47.5%">
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.pluginagr" />
															<ec:elitehelp header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg" ></ec:elitehelp>
														</td>	
														<td class="tbl-header-bold tbl-header-bg" width="5%">
															Remove
														</td>	
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td align="left" class="tblheader-bold" valign="top" colspan="4">
									<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattributes" />
								</td>
							</tr>
							<tr>
								<td colspan="4">
									<table border="0" width="100%">
										<tr>
											<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
												<input type="button" value=" Add Mapping " class="light-btn" onclick="addResponseAttributesTable('responseAttributeTable','responseAttributesTemplate');" tabindex="25" />
											</td>
										</tr>
										<tr>
											<td align="left" class="captiontext" valign="top" width="25%" nowrap="nowrap">
											<!-- Attributes Table -->
												<table width="60%" cellspacing="0" cellpadding="0" border="0" id="responseAttributeTable" class="responseAttributeTable">
													<tr>
														<td align="left" class="tblheader" valign="top" width="35%">
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.commandcode" />
															<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.commandcode" header="servicepolicy.naspolicy.commandcode"/>
														</td>
														<td align="left" class="tblheader" valign="top" width="60%">
															<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.responseattribute" />
															<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.responseattribute" header="servicepolicy.naspolicy.responseattribute"/>
														</td>
														<td align="left" class="tblheader" valign="top"width="5%">Remove</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>
								<td class="btns-td" valign="middle">&nbsp;</td>
								<td class="btns-td" valign="middle" colspan="2">
								<input type="button" name="c_btnCreate" id="c_btnCreate2" value="Create" class="light-btn" onclick="validateForm()"> 
								<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchEAPPolicy.do?'" value="Cancel" class="light-btn">
								</td>
							</tr>
															
					</table>
					
					</td>
				</tr>
				</table>
				</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>
		</td>
	</tr>
</table>	
	 <div id="driverPopup" style="display: none;" title="Add Drivers">
		<table id="addDriver" name="addDriver" cellpadding="0" cellspacing="0" width="100%" class="box"> 	
		</table>
	</div>	
	<div id="additionalDriverPopup" style="display: none;"  title="Add Additional Drivers">
	<table id="additionalDriver" name="additionalDriver" cellpadding="0" cellspacing="0" width="100%" class="box"> 	
	</table>
</div>

<script>
setInit();
</script>
</html:form>
<div id="popupExpr" style="display: none;" title="ExpressionBuilder"> 
	<div id="expBuilderId" align="center" ></div>
</div>
<table style="display:none;" id="responseAttributesTemplate">
	<tr>
		<td class="allborder" width="35%">
			<input  autocomplete="off" class="commandCode noborder" type="text" name="commandCode" maxlength="1000" size="28"  style="width:200px;" onblur="setCommandCodeData(this);"/></td>
		<td class="tblrows" width="60%">
			<textarea rows="1" class="responseAttributes noborder" name="responseAttributes"  id="responseAttributes"  style="min-width:100%;min-height:10px;height:18px;"></textarea>
		</td>
		<td class="tblrows" align="center" width="5%"><img value="top" src="<%=request.getContextPath()%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  /></td>
	</tr>
</table> 

<!-- Mapping Table of Pre Plugin -->
<table class="pre-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="prePluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="prePluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"></textarea>
		</td>	
		<td class="tblrows" width="5%" align="center">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
		</td>	
	</tr>
</table>

<!-- Mapping Table of Post Plugin -->
<table class="post-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="postPluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="postPluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"></textarea>
		</td>	
		<td class="tblrows" width="5%" align="center">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
		</td>	
	</tr>
</table>