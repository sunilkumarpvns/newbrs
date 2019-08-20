<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.eappolicy.forms.UpdateEAPPolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData"%>
<%@page import="com.elitecore.elitesm.util.constants.PolicyPluginConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%
	List<PluginInstData> pluginInstDataList = (List<PluginInstData>) request.getAttribute("pluginInstDataList");
	UpdateEAPPolicyForm updateEAPPolicyForm = (UpdateEAPPolicyForm)request.getAttribute("updateEAPPolicyForm"); 
%>
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-policy.js"></script>
<script>

/* code for driver related jquery popup */
var jdriverNames = new Array();
var jdriverInstanceIds = new Array();
var count=0;
var driverScriptList = [];

<% 
if( Collectionz.isNullOrEmpty(updateEAPPolicyForm.getDriverScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : updateEAPPolicyForm.getDriverScriptList()){ %>
		driverScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

$(document).ready(function(){
	<logic:iterate id="obj" name="driverList" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData">
		jdriverNames[count] = new Array(3); 
		jdriverNames[count][0] = '<bean:write name="obj" property="name"/>' ;
		jdriverNames[count][1] = '<bean:write name="obj" property="description"/>' ;
		jdriverNames[count][2] = '<bean:write name="obj" property="driverTypeData.displayName"/>' ;
		jdriverInstanceIds[count] = '<bean:write name="obj" property="driverInstanceId"/>' ;
		count++;
	</logic:iterate>
	
	var headersArr = new Array(5);
		headersArr[0] = '';
		headersArr[1] = 'Driver Name';
		headersArr[2] = 'Driver Description';
		headersArr[3] = 'Driver Type';
		headersArr[4] = 'Weightage';
	initializeData(jdriverInstanceIds,jdriverNames,'addDriver','driverDataCheckBoxId',headersArr,'true',jdriverInstanceIds.length);
	
	/*  Additional Driver */
	var additionalHeadersArr = new Array(4);
		additionalHeadersArr[0] = '';
		additionalHeadersArr[1] = 'Additional Driver Name';
		additionalHeadersArr[2] = 'Driver Description';
		additionalHeadersArr[3] = 'Driver Type';
	
	initializeAdditionalData(jdriverInstanceIds,jdriverNames,'additionalDriver','additionalDriverCheckBoxId',additionalHeadersArr,jdriverInstanceIds.length);
	
	hideSelectedData('selecteddriverIds','driverDataCheckBoxId');
	hideSelectedAdditionalDriverData('selectedAdditionalDriverIds','additionalDriverCheckBoxId');
	
	 /* Script Autocomplete */
	setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
});

function hideSelectedData(componentId,checkboxid){
	var id = "#" + componentId + " " +"option";
	$(id).each(function(){
		var mainValue = $(this).val();
		var strippedVal = mainValue.split('-');
		var rowid="#row"+checkboxid+strippedVal[0];
		var addtionalId = "#additionalrow"+"additionalDriverCheckBoxId"+strippedVal[0];
		$(rowid).hide();
		$(addtionalId).hide();
	   });
}
	
function hideSelectedAdditionalDriverData(componentId,checkboxid){
	var id = "#" + componentId + " " +"option"; 
	$(id).each(function(){
		var mainValue = $(this).val();
		var primaryId="#row"+"driverDataCheckBoxId"+mainValue;
		$(primaryId).hide();
		$("#additionalrow"+checkboxid+mainValue).hide();
	});
}

function driverpopup(){
	openpopup('driverPopup','driverDataCheckBoxId',jdriverInstanceIds,jdriverNames,'selecteddriverIds');
}

function removeDrivers(){
	removeData('selecteddriverIds','driverDataCheckBoxId');	
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
	                	var labelVal=$("#"+checkBxId+listOfIds[0]).val();       
	                    $(comid).append("<option id="+ listOfIds[0] +" value="+ listOfIds[0] +" class=labeltext> "+labelVal+" </option>");
	                               		                                                        
	                    $("#row"+"driverDataCheckBoxId"+listOfIds[0]).hide();
	                    $("#additionalrow"+"additionalDriverCheckBoxId"+listOfIds[0]).hide();
						selectedItems[0].checked=false;	
	                 }else if(selectedItems.length>1 ){
	                  	for(var i=0;i<selectedItems.length;i++){
							if(selectedItems[i].checked == true){   	
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
function validateForm(){
	if(!validatePlugins('pre-plugin-mapping-table','post-plugin-mapping-table','prePluginsList','postPluginList')){
			return;
	}
	var supportedDriverList=document.getElementById("selecteddriverIds");	
	if(supportedDriverList.options.length > 0){
		selectAll(supportedDriverList);
		var additionalDriverList=document.getElementById("selectedAdditionalDriverIds");
	 	selectAll(additionalDriverList);
		document.forms[0].action.value = 'update';
	 	document.forms[0].submit();	 				
	}else{
		alert('Driver must be specified');	
	}
}

function selectAll(selObj){
	for(var i=0;i<selObj.options.length;i++){
		selObj.options[i].selected = true;
	}
}

function uncheckAllCheckBoxes(pluginNames){
		$("input:checkbox[name='"+pluginNames+"']").each(function(){
			if($(this).attr('checked')){
				$(this).attr('checked',false);
			}
		});
	}
	
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

<html:form action="/updateEAPPolicyDriverProfile">
	<html:hidden name="updateEAPPolicyForm" styleId="action" property="action" value="update"/>
	<html:hidden name="updateEAPPolicyForm" styleId="policyId" property="policyId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="auditUId" property="auditUId"/>
	<html:hidden name="updateEAPPolicyForm" styleId="name" property="name"/>
	<html:hidden name="updateEAPPolicyForm" property="prePluginsList" styleId="prePluginsList"/>
	<html:hidden name="updateEAPPolicyForm" property="postPluginList" styleId="postPluginList"/>
	<table width="100%">
		<tr>
			<td  class="tblheader-bold" valign="top" colspan="4">
				Update <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.driverdetails"/>
			</td>									
		</tr>

		<tr>
			<td width="29%" align="left" class="captiontext" valign="top">
				Driver Group
				<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.drivergroup" header="Driver Group"/>
			</td>
			<td  width="30%" align="left" class="labeltext" valign="top">
				<select class="labeltext" name="selecteddriverIds" id="selecteddriverIds" multiple="true" size="5" style="width: 250px;">
					<logic:iterate id="authPolicyDriverRelData"  name="updateEAPPolicyForm"  property="driversList" >
                  		<bean:define  id="driverInstanceData" name="authPolicyDriverRelData" property="driverData" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"/>
                  		<option value="<bean:write name="driverInstanceData" property="driverInstanceId"/>-<bean:write name="authPolicyDriverRelData" property="weightage"/>"><bean:write name="driverInstanceData" property="name"/>-W-<bean:write name="authPolicyDriverRelData" property="weightage"/></option> 
                   </logic:iterate> 
				</select>												
			</td>
			<td align="left" class="labeltext" valign="top" >
				<input type="button" value="Add " onClick="driverpopup()" class="light-btn"  style="width: 75px"/><br/><br/>
				<input type="button" value="Remove "  onclick="removeDrivers()" class="light-btn" style="width: 75px"/>
			</td>
		</tr>
		
		<tr>
			<td width="20%" align="left" class="captiontext" valign="top">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.additionalgroup" />
				<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.additionalgroup" header="servicepolicy.additionalgroup"/>
			</td>
			<td  width="30%"  align="left" class="labeltext" valign="top">
				<select class="labeltext" name="selectedAdditionalDriverIds" id="selectedAdditionalDriverIds" multiple="true" size="5" style="width: 250;">
					<logic:iterate id="authPolicyAdditionalDriverRelData"  name="updateEAPPolicyForm"  property="additionalDriverRelDataList" >
                  		<bean:define  id="driverInstanceData" name="authPolicyAdditionalDriverRelData" property="driverInstanceData" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"/>
                  		<option value="<bean:write name="driverInstanceData" property="driverInstanceId"/>"><bean:write name="driverInstanceData" property="name"/></option> 
                   </logic:iterate> 
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
				<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.eappolicy.script" header="servicepolicy.eappolicy.script"/>
			</td>
			<td align="left" class="labeltext" valign="top"  nowrap="nowrap" >
				<html:text property="script" size="30" style="width:250px" maxlength="255" styleClass="scriptInstAutocomplete"/>
			</td>	
		</tr>
		<tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="4">
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
								
								<logic:iterate id="obj" name="eapPolicyData" property="eapPolicyPluginConfigList">
			          				<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.IN_PLUGIN%>">
				          				<tr>
											<td class="tblfirstcol" width="47.5%">
												<input type="text" name="prePluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);" value='<bean:write name="obj" property="pluginName"/>'/>
											</td>	
											<td class="tblrows" width="47.5%">
												<textarea name="prePluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"><bean:write name="obj" property="pluginArgument"/></textarea>
											</td>	
											<td class="tblrows" width="5%" align="center">
												<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
											</td>	
										</tr>
			          				</logic:equal>
			          			</logic:iterate>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="4">
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
								<logic:iterate id="obj" name="eapPolicyData" property="eapPolicyPluginConfigList">
			          				<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.OUT_PLUGIN%>">
				          				<tr>
											<td class="tblfirstcol" width="47.5%">
												<input type="text" name="postPluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);" value='<bean:write name="obj" property="pluginName"/>'/>
											</td>	
											<td class="tblrows" width="47.5%">
												<textarea name="postPluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"><bean:write name="obj" property="pluginArgument"/></textarea>
											</td>	
											<td class="tblrows" width="5%" align="center">
												<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
											</td>	
										</tr>
			          				</logic:equal>
		          				</logic:iterate>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="btns-td" valign="middle">&nbsp;</td>
			<td class="btns-td" valign="middle" colspan="2">
				<input type="button" name="c_btnCreate" onclick="validateForm()" id="c_btnCreate2" value="Update" class="light-btn">
				<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchEAPPolicy.do?'" value="Cancel" class="light-btn"> 
			</td>
		</tr>
</table>
</html:form>
 
<div id="driverPopup" style="display: none;" title="Add Drivers">
	<table id="addDriver" name="addDriver" cellpadding="0" cellspacing="0" width="100%" class="box"></table>
</div>	
	
<div id="additionalDriverPopup" style="display: none;"  title="Add Additional Drivers">
	<table id="additionalDriver" name="additionalDriver" cellpadding="0" cellspacing="0" width="100%" class="box"></table>
</div>

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